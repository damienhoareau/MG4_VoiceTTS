package android.media;

import android.content.IntentFilter;
import android.net.NetworkUtils;
import android.net.ProxyInfo;
import android.net.captiveportal.CaptivePortalProbeSpec;
import android.os.StrictMode;
import android.provider.SettingsStringUtil;
import android.util.Log;
import com.android.internal.content.NativeLibraryHelper;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.UnknownServiceException;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class Media2HTTPConnection {
    private static final int CONNECT_TIMEOUT_MS = 30000;
    private static final int HTTP_TEMP_REDIRECT = 307;
    private static final int MAX_REDIRECTS = 20;
    private static final String TAG = "Media2HTTPConnection";
    private static final boolean VERBOSE = false;
    private long mCurrentOffset = -1;
    private URL mURL = null;
    private Map<String, String> mHeaders = null;
    private HttpURLConnection mConnection = null;
    private long mTotalSize = -1;
    private InputStream mInputStream = null;
    private boolean mAllowCrossDomainRedirect = true;
    private boolean mAllowCrossProtocolRedirect = true;

    public Media2HTTPConnection() {
        if (CookieHandler.getDefault() == null) {
            Log.w(TAG, "Media2HTTPConnection: Unexpected. No CookieHandler found.");
        }
    }

    public boolean connect(String str, String str2) {
        try {
            disconnect();
            this.mAllowCrossDomainRedirect = true;
            this.mURL = new URL(str);
            this.mHeaders = convertHeaderStringToMap(str2);
            return true;
        } catch (MalformedURLException unused) {
            return false;
        }
    }

    private boolean parseBoolean(String str) {
        try {
            return Long.parseLong(str) != 0;
        } catch (NumberFormatException unused) {
            return "true".equalsIgnoreCase(str) || "yes".equalsIgnoreCase(str);
        }
    }

    private boolean filterOutInternalHeaders(String str, String str2) {
        if (!"android-allow-cross-domain-redirect".equalsIgnoreCase(str)) {
            return false;
        }
        boolean z = parseBoolean(str2);
        this.mAllowCrossDomainRedirect = z;
        this.mAllowCrossProtocolRedirect = z;
        return true;
    }

    private Map<String, String> convertHeaderStringToMap(String str) {
        HashMap map = new HashMap();
        for (String str2 : str.split("\r\n")) {
            int iIndexOf = str2.indexOf(SettingsStringUtil.DELIMITER);
            if (iIndexOf >= 0) {
                String strSubstring = str2.substring(0, iIndexOf);
                String strSubstring2 = str2.substring(iIndexOf + 1);
                if (!filterOutInternalHeaders(strSubstring, strSubstring2)) {
                    map.put(strSubstring, strSubstring2);
                }
            }
        }
        return map;
    }

    public void disconnect() {
        teardownConnection();
        this.mHeaders = null;
        this.mURL = null;
    }

    private void teardownConnection() {
        if (this.mConnection != null) {
            InputStream inputStream = this.mInputStream;
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException unused) {
                }
                this.mInputStream = null;
            }
            this.mConnection.disconnect();
            this.mConnection = null;
            this.mCurrentOffset = -1L;
        }
    }

    private static final boolean isLocalHost(URL url) {
        String host;
        if (url == null || (host = url.getHost()) == null) {
            return false;
        }
        try {
            return host.equalsIgnoreCase(ProxyInfo.LOCAL_HOST) || NetworkUtils.numericToInetAddress(host).isLoopbackAddress();
        } catch (IllegalArgumentException unused) {
        }
    }

    private void seekTo(long j) throws IOException {
        int iLastIndexOf;
        teardownConnection();
        int i = 0;
        try {
            URL url = this.mURL;
            boolean zIsLocalHost = isLocalHost(url);
            while (true) {
                if (zIsLocalHost) {
                    this.mConnection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
                } else {
                    this.mConnection = (HttpURLConnection) url.openConnection();
                }
                this.mConnection.setConnectTimeout(30000);
                this.mConnection.setInstanceFollowRedirects(this.mAllowCrossDomainRedirect);
                if (this.mHeaders != null) {
                    for (Map.Entry<String, String> entry : this.mHeaders.entrySet()) {
                        this.mConnection.setRequestProperty(entry.getKey(), entry.getValue());
                    }
                }
                if (j > 0) {
                    this.mConnection.setRequestProperty("Range", "bytes=" + j + NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                }
                int responseCode = this.mConnection.getResponseCode();
                if (responseCode != 300 && responseCode != 301 && responseCode != 302 && responseCode != 303 && responseCode != 307) {
                    if (this.mAllowCrossDomainRedirect) {
                        this.mURL = this.mConnection.getURL();
                    }
                    if (responseCode == 206) {
                        String headerField = this.mConnection.getHeaderField("Content-Range");
                        this.mTotalSize = -1L;
                        if (headerField != null && (iLastIndexOf = headerField.lastIndexOf(47)) >= 0) {
                            try {
                                this.mTotalSize = Long.parseLong(headerField.substring(iLastIndexOf + 1));
                            } catch (NumberFormatException unused) {
                            }
                        }
                    } else {
                        if (responseCode != 200) {
                            throw new IOException();
                        }
                        this.mTotalSize = this.mConnection.getContentLength();
                    }
                    if (j > 0 && responseCode != 206) {
                        throw new ProtocolException();
                    }
                    this.mInputStream = new BufferedInputStream(this.mConnection.getInputStream());
                    this.mCurrentOffset = j;
                    return;
                }
                i++;
                if (i > 20) {
                    throw new NoRouteToHostException("Too many redirects: " + i);
                }
                String requestMethod = this.mConnection.getRequestMethod();
                if (responseCode == 307 && !requestMethod.equals("GET") && !requestMethod.equals("HEAD")) {
                    throw new NoRouteToHostException("Invalid redirect");
                }
                String headerField2 = this.mConnection.getHeaderField(CaptivePortalProbeSpec.HTTP_LOCATION_HEADER_NAME);
                if (headerField2 == null) {
                    throw new NoRouteToHostException("Invalid redirect");
                }
                URL url2 = new URL(this.mURL, headerField2);
                if (!url2.getProtocol().equals(IntentFilter.SCHEME_HTTPS) && !url2.getProtocol().equals(IntentFilter.SCHEME_HTTP)) {
                    throw new NoRouteToHostException("Unsupported protocol redirect");
                }
                boolean zEquals = this.mURL.getProtocol().equals(url2.getProtocol());
                if (!this.mAllowCrossProtocolRedirect && !zEquals) {
                    throw new NoRouteToHostException("Cross-protocol redirects are disallowed");
                }
                boolean zEquals2 = this.mURL.getHost().equals(url2.getHost());
                if (!this.mAllowCrossDomainRedirect && !zEquals2) {
                    throw new NoRouteToHostException("Cross-domain redirects are disallowed");
                }
                if (responseCode != 307) {
                    this.mURL = url2;
                }
                url = url2;
            }
        } catch (IOException e) {
            this.mTotalSize = -1L;
            teardownConnection();
            this.mCurrentOffset = -1L;
            throw e;
        }
    }

    public int readAt(long j, byte[] bArr, int i) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        try {
            if (j != this.mCurrentOffset) {
                seekTo(j);
            }
            int i2 = 0;
            int i3 = this.mInputStream.read(bArr, 0, i);
            if (i3 != -1) {
                i2 = i3;
            }
            this.mCurrentOffset += (long) i2;
            return i2;
        } catch (NoRouteToHostException e) {
            Log.w(TAG, "readAt " + j + " / " + i + " => " + e);
            return -1010;
        } catch (ProtocolException e2) {
            Log.w(TAG, "readAt " + j + " / " + i + " => " + e2);
            return -1010;
        } catch (UnknownServiceException e3) {
            Log.w(TAG, "readAt " + j + " / " + i + " => " + e3);
            return -1010;
        } catch (IOException | Exception unused) {
            return -1;
        }
    }

    public long getSize() {
        if (this.mConnection == null) {
            try {
                seekTo(0L);
            } catch (IOException unused) {
                return -1L;
            }
        }
        return this.mTotalSize;
    }

    public String getMIMEType() {
        if (this.mConnection == null) {
            try {
                seekTo(0L);
            } catch (IOException unused) {
                return "application/octet-stream";
            }
        }
        return this.mConnection.getContentType();
    }

    public String getUri() {
        return this.mURL.toString();
    }
}
