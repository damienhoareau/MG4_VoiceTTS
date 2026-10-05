package android.media;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import com.android.internal.R;
import java.io.IOException;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class Ringtone {
    private static final boolean LOGD = true;
    private static final String MEDIA_SELECTION = "mime_type LIKE 'audio/%' OR mime_type IN ('application/ogg', 'application/x-flac')";
    private static final String TAG = "Ringtone";
    private final boolean mAllowRemote;
    private final AudioManager mAudioManager;
    private final Context mContext;
    private MediaPlayer mLocalPlayer;
    private final IRingtonePlayer mRemotePlayer;
    private final Binder mRemoteToken;
    private String mTitle;
    private Uri mUri;
    private static final String[] MEDIA_COLUMNS = {"_id", "_data", "title"};
    private static final ArrayList<Ringtone> sActiveRingtones = new ArrayList<>();
    private final MyOnCompletionListener mCompletionListener = new MyOnCompletionListener();
    private AudioAttributes mAudioAttributes = new AudioAttributes.Builder().setUsage(6).setContentType(4).build();
    private boolean mIsLooping = false;
    private float mVolume = 1.0f;
    private final Object mPlaybackSettingsLock = new Object();

    public Ringtone(Context context, boolean z) {
        this.mContext = context;
        AudioManager audioManager = (AudioManager) context.getSystemService("audio");
        this.mAudioManager = audioManager;
        this.mAllowRemote = z;
        this.mRemotePlayer = z ? audioManager.getRingtonePlayer() : null;
        this.mRemoteToken = z ? new Binder() : null;
    }

    @Deprecated
    public void setStreamType(int i) {
        PlayerBase.deprecateStreamTypeForPlayback(i, TAG, "setStreamType()");
        setAudioAttributes(new AudioAttributes.Builder().setInternalLegacyStreamType(i).build());
    }

    @Deprecated
    public int getStreamType() {
        return AudioAttributes.toLegacyStreamType(this.mAudioAttributes);
    }

    public void setAudioAttributes(AudioAttributes audioAttributes) throws IllegalArgumentException {
        if (audioAttributes == null) {
            throw new IllegalArgumentException("Invalid null AudioAttributes for Ringtone");
        }
        this.mAudioAttributes = audioAttributes;
        setUri(this.mUri);
    }

    public AudioAttributes getAudioAttributes() {
        return this.mAudioAttributes;
    }

    public void setLooping(boolean z) {
        synchronized (this.mPlaybackSettingsLock) {
            this.mIsLooping = z;
            applyPlaybackProperties_sync();
        }
    }

    public boolean isLooping() {
        boolean z;
        synchronized (this.mPlaybackSettingsLock) {
            z = this.mIsLooping;
        }
        return z;
    }

    public void setVolume(float f) {
        synchronized (this.mPlaybackSettingsLock) {
            if (f < 0.0f) {
                f = 0.0f;
            }
            if (f > 1.0f) {
                f = 1.0f;
            }
            this.mVolume = f;
            applyPlaybackProperties_sync();
        }
    }

    public float getVolume() {
        float f;
        synchronized (this.mPlaybackSettingsLock) {
            f = this.mVolume;
        }
        return f;
    }

    private void applyPlaybackProperties_sync() {
        IRingtonePlayer iRingtonePlayer;
        MediaPlayer mediaPlayer = this.mLocalPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(this.mVolume);
            this.mLocalPlayer.setLooping(this.mIsLooping);
        } else {
            if (this.mAllowRemote && (iRingtonePlayer = this.mRemotePlayer) != null) {
                try {
                    iRingtonePlayer.setPlaybackProperties(this.mRemoteToken, this.mVolume, this.mIsLooping);
                    return;
                } catch (RemoteException e) {
                    Log.w(TAG, "Problem setting playback properties: ", e);
                    return;
                }
            }
            Log.w(TAG, "Neither local nor remote player available when applying playback properties");
        }
    }

    public String getTitle(Context context) throws Throwable {
        String str = this.mTitle;
        if (str != null) {
            return str;
        }
        String title = getTitle(context, this.mUri, true, this.mAllowRemote);
        this.mTitle = title;
        return title;
    }

    /* JADX WARN: Code duplicated, block: B:42:0x0094  */
    /* JADX WARN: Code duplicated, block: B:45:0x00a2  */
    /* JADX WARN: Code duplicated, block: B:47:0x00ab A[ORIG_RETURN, RETURN] */
    /* JADX WARN: Code duplicated, block: B:58:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:59:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r10v0, types: [boolean] */
    /* JADX WARN: Type inference failed for: r7v4 */
    /* JADX WARN: Type inference failed for: r7v5 */
    /* JADX WARN: Type inference failed for: r7v6, types: [android.database.Cursor] */
    public static String getTitle(Context context, Uri uri, boolean z, boolean z2) throws Throwable {
        String string;
        String string2;
        Cursor cursorQuery;
        ContentResolver contentResolver = context.getContentResolver();
        if (uri != null) {
            String authorityWithoutUserId = ContentProvider.getAuthorityWithoutUserId(uri.getAuthority());
            ?? r7 = 0;
            string = null;
            string = null;
            string = null;
            string = null;
            string = null;
            try {
                if (!"settings".equals(authorityWithoutUserId)) {
                    try {
                        if (MediaStore.AUTHORITY.equals(authorityWithoutUserId)) {
                            cursorQuery = contentResolver.query(uri, MEDIA_COLUMNS, z2 ? null : MEDIA_SELECTION, null, null);
                            if (cursorQuery != null) {
                                try {
                                    if (cursorQuery.getCount() == 1) {
                                        cursorQuery.moveToFirst();
                                        String string3 = cursorQuery.getString(2);
                                        if (cursorQuery != null) {
                                            cursorQuery.close();
                                        }
                                        return string3;
                                    }
                                } catch (SecurityException unused) {
                                    IRingtonePlayer ringtonePlayer = z2 ? ((AudioManager) context.getSystemService("audio")).getRingtonePlayer() : null;
                                    if (ringtonePlayer != null) {
                                        try {
                                            string = ringtonePlayer.getTitle(uri);
                                        } catch (RemoteException unused2) {
                                        }
                                    }
                                    if (cursorQuery != null) {
                                    }
                                    if (string == null) {
                                        string = uri.getLastPathSegment();
                                    }
                                    if (string == null) {
                                        return string;
                                    }
                                    string2 = context.getString(R.string.ringtone_unknown);
                                    if (string2 == null) {
                                        return "";
                                    }
                                    return string2;
                                }
                            }
                        } else {
                            cursorQuery = null;
                        }
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                    } catch (SecurityException unused3) {
                        cursorQuery = null;
                    } catch (Throwable th) {
                        th = th;
                        if (r7 != 0) {
                            r7.close();
                        }
                        throw th;
                    }
                    if (string == null) {
                        string = uri.getLastPathSegment();
                    }
                } else if (z != 0) {
                    string = context.getString(R.string.ringtone_default_with_actual, getTitle(context, RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.getDefaultType(uri)), false, z2));
                }
            } catch (Throwable th2) {
                th = th2;
                r7 = z;
            }
        } else {
            string = context.getString(R.string.ringtone_silent);
        }
        if (string == null) {
            return string;
        }
        string2 = context.getString(R.string.ringtone_unknown);
        if (string2 == null) {
            return "";
        }
        return string2;
    }

    public void setUri(Uri uri) {
        destroyLocalPlayer();
        this.mUri = uri;
        if (uri == null) {
            return;
        }
        MediaPlayer mediaPlayer = new MediaPlayer();
        this.mLocalPlayer = mediaPlayer;
        try {
            mediaPlayer.setDataSource(this.mContext, this.mUri);
            this.mLocalPlayer.setAudioAttributes(this.mAudioAttributes);
            synchronized (this.mPlaybackSettingsLock) {
                applyPlaybackProperties_sync();
            }
            this.mLocalPlayer.prepare();
        } catch (IOException | SecurityException e) {
            destroyLocalPlayer();
            if (!this.mAllowRemote) {
                Log.w(TAG, "Remote playback not allowed: " + e);
            }
        }
        if (this.mLocalPlayer != null) {
            Log.d(TAG, "Successfully created local player");
        } else {
            Log.d(TAG, "Problem opening; delegating to remote player");
        }
    }

    public Uri getUri() {
        return this.mUri;
    }

    public void play() {
        boolean z;
        float f;
        if (this.mLocalPlayer != null) {
            if (this.mAudioManager.getStreamVolume(AudioAttributes.toLegacyStreamType(this.mAudioAttributes)) != 0) {
                startLocalPlayer();
                return;
            }
            return;
        }
        if (this.mAllowRemote && this.mRemotePlayer != null) {
            Uri canonicalUri = this.mUri.getCanonicalUri();
            synchronized (this.mPlaybackSettingsLock) {
                z = this.mIsLooping;
                f = this.mVolume;
            }
            try {
                this.mRemotePlayer.play(this.mRemoteToken, canonicalUri, this.mAudioAttributes, f, z);
                return;
            } catch (RemoteException e) {
                if (playFallbackRingtone()) {
                    return;
                }
                Log.w(TAG, "Problem playing ringtone: " + e);
                return;
            }
        }
        if (playFallbackRingtone()) {
            return;
        }
        Log.w(TAG, "Neither local nor remote playback available");
    }

    public void stop() {
        IRingtonePlayer iRingtonePlayer;
        if (this.mLocalPlayer != null) {
            destroyLocalPlayer();
            return;
        }
        if (!this.mAllowRemote || (iRingtonePlayer = this.mRemotePlayer) == null) {
            return;
        }
        try {
            iRingtonePlayer.stop(this.mRemoteToken);
        } catch (RemoteException e) {
            Log.w(TAG, "Problem stopping ringtone: " + e);
        }
    }

    private void destroyLocalPlayer() {
        MediaPlayer mediaPlayer = this.mLocalPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(null);
            this.mLocalPlayer.reset();
            this.mLocalPlayer.release();
            this.mLocalPlayer = null;
            synchronized (sActiveRingtones) {
                sActiveRingtones.remove(this);
            }
        }
    }

    private void startLocalPlayer() {
        if (this.mLocalPlayer == null) {
            return;
        }
        synchronized (sActiveRingtones) {
            sActiveRingtones.add(this);
        }
        this.mLocalPlayer.setOnCompletionListener(this.mCompletionListener);
        this.mLocalPlayer.start();
    }

    public boolean isPlaying() {
        IRingtonePlayer iRingtonePlayer;
        MediaPlayer mediaPlayer = this.mLocalPlayer;
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        if (this.mAllowRemote && (iRingtonePlayer = this.mRemotePlayer) != null) {
            try {
                return iRingtonePlayer.isPlaying(this.mRemoteToken);
            } catch (RemoteException e) {
                Log.w(TAG, "Problem checking ringtone: " + e);
                return false;
            }
        }
        Log.w(TAG, "Neither local nor remote playback available");
        return false;
    }

    private boolean playFallbackRingtone() {
        if (this.mAudioManager.getStreamVolume(AudioAttributes.toLegacyStreamType(this.mAudioAttributes)) == 0) {
            return false;
        }
        int defaultType = RingtoneManager.getDefaultType(this.mUri);
        if (defaultType == -1 || RingtoneManager.getActualDefaultRingtoneUri(this.mContext, defaultType) != null) {
            try {
                AssetFileDescriptor assetFileDescriptorOpenRawResourceFd = this.mContext.getResources().openRawResourceFd(R.raw.fallbackring);
                if (assetFileDescriptorOpenRawResourceFd != null) {
                    this.mLocalPlayer = new MediaPlayer();
                    if (assetFileDescriptorOpenRawResourceFd.getDeclaredLength() < 0) {
                        this.mLocalPlayer.setDataSource(assetFileDescriptorOpenRawResourceFd.getFileDescriptor());
                    } else {
                        this.mLocalPlayer.setDataSource(assetFileDescriptorOpenRawResourceFd.getFileDescriptor(), assetFileDescriptorOpenRawResourceFd.getStartOffset(), assetFileDescriptorOpenRawResourceFd.getDeclaredLength());
                    }
                    this.mLocalPlayer.setAudioAttributes(this.mAudioAttributes);
                    synchronized (this.mPlaybackSettingsLock) {
                        applyPlaybackProperties_sync();
                    }
                    this.mLocalPlayer.prepare();
                    startLocalPlayer();
                    assetFileDescriptorOpenRawResourceFd.close();
                    return true;
                }
                Log.e(TAG, "Could not load fallback ringtone");
                return false;
            } catch (Resources.NotFoundException unused) {
                Log.e(TAG, "Fallback ringtone does not exist");
                return false;
            } catch (IOException unused2) {
                destroyLocalPlayer();
                Log.e(TAG, "Failed to open fallback ringtone");
                return false;
            }
        }
        Log.w(TAG, "not playing fallback for " + this.mUri);
        return false;
    }

    void setTitle(String str) {
        this.mTitle = str;
    }

    protected void finalize() {
        MediaPlayer mediaPlayer = this.mLocalPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        MyOnCompletionListener() {
        }

        @Override // android.media.MediaPlayer.OnCompletionListener
        public void onCompletion(MediaPlayer mediaPlayer) {
            synchronized (Ringtone.sActiveRingtones) {
                Ringtone.sActiveRingtones.remove(Ringtone.this);
            }
            mediaPlayer.setOnCompletionListener(null);
        }
    }
}
