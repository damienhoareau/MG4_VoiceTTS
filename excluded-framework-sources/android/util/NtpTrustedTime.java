package android.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.SntpClient;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.internal.R;

/* JADX INFO: loaded from: classes2.dex */
public class NtpTrustedTime implements TrustedTime {
    private static final boolean LOGD = false;
    private static final String TAG = "NtpTrustedTime";
    private static Context sContext;
    private static NtpTrustedTime sSingleton;
    private ConnectivityManager mCM;
    private long mCachedNtpCertainty;
    private long mCachedNtpElapsedRealtime;
    private long mCachedNtpTime;
    private boolean mHasCache;
    private final String mServer;
    private final long mTimeout;

    private NtpTrustedTime(String str, long j) {
        this.mServer = str;
        this.mTimeout = j;
    }

    public static synchronized NtpTrustedTime getInstance(Context context) {
        if (sSingleton == null) {
            Resources resources = context.getResources();
            ContentResolver contentResolver = context.getContentResolver();
            String string = resources.getString(R.string.config_ntpServer);
            long integer = resources.getInteger(R.integer.config_ntpTimeout);
            String string2 = Settings.Global.getString(contentResolver, Settings.Global.NTP_SERVER);
            long j = Settings.Global.getLong(contentResolver, Settings.Global.NTP_TIMEOUT, integer);
            if (string2 != null) {
                string = string2;
            }
            sSingleton = new NtpTrustedTime(string, j);
            sContext = context;
        }
        return sSingleton;
    }

    @Override // android.util.TrustedTime
    public boolean forceRefresh() {
        synchronized (this) {
            if (this.mCM == null) {
                this.mCM = (ConnectivityManager) sContext.getSystemService(ConnectivityManager.class);
            }
        }
        ConnectivityManager connectivityManager = this.mCM;
        return forceRefresh(connectivityManager == null ? null : connectivityManager.getActiveNetwork());
    }

    public boolean forceRefresh(Network network) {
        if (TextUtils.isEmpty(this.mServer)) {
            return false;
        }
        synchronized (this) {
            if (this.mCM == null) {
                this.mCM = (ConnectivityManager) sContext.getSystemService(ConnectivityManager.class);
            }
        }
        ConnectivityManager connectivityManager = this.mCM;
        NetworkInfo networkInfo = connectivityManager == null ? null : connectivityManager.getNetworkInfo(network);
        if (networkInfo != null && networkInfo.isConnected()) {
            SntpClient sntpClient = new SntpClient();
            if (sntpClient.requestTime(this.mServer, (int) this.mTimeout, network)) {
                this.mHasCache = true;
                this.mCachedNtpTime = sntpClient.getNtpTime();
                this.mCachedNtpElapsedRealtime = sntpClient.getNtpTimeReference();
                this.mCachedNtpCertainty = sntpClient.getRoundTripTime() / 2;
                return true;
            }
        }
        return false;
    }

    @Override // android.util.TrustedTime
    public boolean hasCache() {
        return this.mHasCache;
    }

    @Override // android.util.TrustedTime
    public long getCacheAge() {
        if (this.mHasCache) {
            return SystemClock.elapsedRealtime() - this.mCachedNtpElapsedRealtime;
        }
        return Long.MAX_VALUE;
    }

    @Override // android.util.TrustedTime
    public long getCacheCertainty() {
        if (this.mHasCache) {
            return this.mCachedNtpCertainty;
        }
        return Long.MAX_VALUE;
    }

    @Override // android.util.TrustedTime
    public long currentTimeMillis() {
        if (!this.mHasCache) {
            throw new IllegalStateException("Missing authoritative time source");
        }
        return this.mCachedNtpTime + getCacheAge();
    }

    public long getCachedNtpTime() {
        return this.mCachedNtpTime;
    }

    public long getCachedNtpTimeReference() {
        return this.mCachedNtpElapsedRealtime;
    }
}
