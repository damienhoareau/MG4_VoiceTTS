package android.media;

import android.app.ActivityThread;
import android.app.Application;
import android.app.CarConfigManager;
import android.app.backup.FullBackup;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import android.util.TimeUtils;
import android.view.Surface;
import android.view.SurfaceHolder;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.util.Preconditions;
import dalvik.system.CloseGuard;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import libcore.io.IoBridge;
import libcore.io.Streams;

/* JADX INFO: loaded from: classes.dex */
public final class MediaPlayer2Impl extends MediaPlayer2 {
    private static final int INVOKE_ID_ADD_EXTERNAL_SOURCE = 2;
    private static final int INVOKE_ID_ADD_EXTERNAL_SOURCE_FD = 3;
    private static final int INVOKE_ID_DESELECT_TRACK = 5;
    private static final int INVOKE_ID_GET_SELECTED_TRACK = 7;
    private static final int INVOKE_ID_GET_TRACK_INFO = 1;
    private static final int INVOKE_ID_SELECT_TRACK = 4;
    private static final int INVOKE_ID_SET_VIDEO_SCALE_MODE = 6;
    private static final int KEY_PARAMETER_AUDIO_ATTRIBUTES = 1400;
    private static final int MEDIA_AUDIO_ROUTING_CHANGED = 10000;
    private static final int MEDIA_BUFFERING_UPDATE = 3;
    private static final int MEDIA_DRM_INFO = 210;
    private static final int MEDIA_ERROR = 100;
    private static final int MEDIA_INFO = 200;
    private static final int MEDIA_META_DATA = 202;
    private static final int MEDIA_NOP = 0;
    private static final int MEDIA_NOTIFY_TIME = 98;
    private static final int MEDIA_PAUSED = 7;
    private static final int MEDIA_PLAYBACK_COMPLETE = 2;
    private static final int MEDIA_PREPARED = 1;
    private static final int MEDIA_SEEK_COMPLETE = 4;
    private static final int MEDIA_SET_VIDEO_SIZE = 5;
    private static final int MEDIA_SKIPPED = 9;
    private static final int MEDIA_STARTED = 6;
    private static final int MEDIA_STOPPED = 8;
    private static final int MEDIA_SUBTITLE_DATA = 201;
    private static final int MEDIA_TIMED_TEXT = 99;
    private static final int NEXT_SOURCE_STATE_ERROR = -1;
    private static final int NEXT_SOURCE_STATE_INIT = 0;
    private static final int NEXT_SOURCE_STATE_PREPARED = 2;
    private static final int NEXT_SOURCE_STATE_PREPARING = 1;
    private static final String TAG = "MediaPlayer2Impl";
    private boolean mActiveDrmScheme;
    private DataSourceDesc mCurrentDSD;
    private Task mCurrentTask;
    private boolean mDrmConfigAllowed;
    private DrmInfoImpl mDrmInfoImpl;
    private boolean mDrmInfoResolved;
    private MediaDrm mDrmObj;
    private boolean mDrmProvisioningInProgress;
    private ProvisioningThread mDrmProvisioningThread;
    private byte[] mDrmSessionId;
    private UUID mDrmUUID;
    private EventHandler mEventHandler;
    private HandlerThread mHandlerThread;
    private int mListenerContext;
    private long mNativeContext;
    private long mNativeSurfaceTexture;
    private List<DataSourceDesc> mNextDSDs;
    private long mNextSrcId;
    private MediaPlayer2.OnDrmConfigHelper mOnDrmConfigHelper;
    private MediaPlayer2.OnSubtitleDataListener mOnSubtitleDataListener;
    private Vector<InputStream> mOpenSubtitleSources;
    private boolean mPrepareDrmInProgress;
    private boolean mScreenOnWhilePlaying;
    private long mSrcIdGenerator;
    private boolean mStayAwake;
    private SubtitleController mSubtitleController;
    private SurfaceHolder mSurfaceHolder;
    private final Handler mTaskHandler;
    private TimeProvider mTimeProvider;
    private PowerManager.WakeLock mWakeLock = null;
    private int mStreamType = Integer.MIN_VALUE;
    private final CloseGuard mGuard = CloseGuard.get();
    private final Object mSrcLock = new Object();
    private long mCurrentSrcId = 0;
    private int mNextSourceState = 0;
    private boolean mNextSourcePlayPending = false;
    private AtomicInteger mBufferedPercentageCurrent = new AtomicInteger(0);
    private AtomicInteger mBufferedPercentageNext = new AtomicInteger(0);
    private volatile float mVolume = 1.0f;
    private final Object mDrmLock = new Object();
    private final Object mTaskLock = new Object();
    private final List<Task> mPendingTasks = new LinkedList();
    private AudioDeviceInfo mPreferredDevice = null;
    private ArrayMap<AudioRouting.OnRoutingChangedListener, NativeRoutingEventHandlerDelegate> mRoutingChangeListeners = new ArrayMap<>();
    private Vector<Pair<Integer, SubtitleTrack>> mIndexTrackPairs = new Vector<>();
    private BitSet mInbandTrackIndices = new BitSet();
    private int mSelectedSubtitleTrackIndex = -1;
    private MediaPlayer2.OnSubtitleDataListener mSubtitleDataListener = new MediaPlayer2.OnSubtitleDataListener() { // from class: android.media.MediaPlayer2Impl.25
        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.media.MediaPlayer2.OnSubtitleDataListener
        public void onSubtitleData(MediaPlayer2 mediaPlayer2, SubtitleData subtitleData) {
            int trackIndex = subtitleData.getTrackIndex();
            synchronized (MediaPlayer2Impl.this.mIndexTrackPairs) {
                for (Pair pair : MediaPlayer2Impl.this.mIndexTrackPairs) {
                    if (pair.first != 0 && ((Integer) pair.first).intValue() == trackIndex && pair.second != 0) {
                        ((SubtitleTrack) pair.second).onData(subtitleData);
                    }
                }
            }
        }
    };
    private final Object mEventCbLock = new Object();
    private ArrayList<Pair<Executor, MediaPlayer2.MediaPlayer2EventCallback>> mEventCallbackRecords = new ArrayList<>();
    private final Object mDrmEventCbLock = new Object();
    private ArrayList<Pair<Executor, MediaPlayer2.DrmEventCallback>> mDrmEventCallbackRecords = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: private */
    public native void _attachAuxEffect(int i);

    private native int _getAudioStreamType() throws IllegalStateException;

    private native void _notifyAt(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public native void _pause() throws IllegalStateException;

    private native void _prepareDrm(byte[] bArr, byte[] bArr2);

    private native void _release();

    /* JADX INFO: Access modifiers changed from: private */
    public native void _releaseDrm();

    private native void _reset();

    /* JADX INFO: Access modifiers changed from: private */
    public final native void _seekTo(long j, int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native void _setAudioSessionId(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public native void _setAuxEffectSendLevel(float f);

    /* JADX INFO: Access modifiers changed from: private */
    public native void _setBufferingParams(BufferingParams bufferingParams);

    /* JADX INFO: Access modifiers changed from: private */
    public native void _setPlaybackParams(PlaybackParams playbackParams);

    /* JADX INFO: Access modifiers changed from: private */
    public native void _setSyncParams(SyncParams syncParams);

    /* JADX INFO: Access modifiers changed from: private */
    public native void _setVideoSurface(Surface surface);

    /* JADX INFO: Access modifiers changed from: private */
    public native void _setVolume(float f, float f2);

    /* JADX INFO: Access modifiers changed from: private */
    public native void _start() throws IllegalStateException;

    private native void _stop() throws IllegalStateException;

    private native Parcel getParameter(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isVideoScalingModeSupported(int i) {
        return i == 1 || i == 2;
    }

    private native void nativeHandleDataSourceCallback(boolean z, long j, Media2DataSource media2DataSource);

    private native void nativeHandleDataSourceFD(boolean z, long j, FileDescriptor fileDescriptor, long j2, long j3) throws IOException;

    private native void nativeHandleDataSourceUrl(boolean z, long j, Media2HTTPService media2HTTPService, String str, String[] strArr, String[] strArr2) throws IOException;

    private native void nativePlayNextDataSource(long j);

    private final native void native_enableDeviceCallback(boolean z);

    private final native void native_finalize();

    private native int native_getMediaPlayer2State();

    private final native boolean native_getMetadata(boolean z, boolean z2, Parcel parcel);

    private native PersistableBundle native_getMetrics();

    private final native int native_getRoutedDeviceId();

    private static final native void native_init();

    private final native int native_invoke(Parcel parcel, Parcel parcel2);

    private final native int native_setMetadataFilter(Parcel parcel);

    private final native boolean native_setOutputDevice(int i);

    private final native void native_setup(Object obj);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native void native_stream_event_onStreamDataRequest(long j, long j2, long j3);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native void native_stream_event_onStreamPresentationEnd(long j, long j2);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native void native_stream_event_onTearDown(long j, long j2);

    /* JADX INFO: Access modifiers changed from: private */
    public native void setLooping(boolean z);

    /* JADX INFO: Access modifiers changed from: private */
    public native boolean setParameter(int i, Parcel parcel);

    public native void _prepare();

    @Override // android.media.MediaPlayer2
    public void clearPendingCommands() {
    }

    @Override // android.media.MediaPlayer2
    public native int getAudioSessionId();

    @Override // android.media.MediaPlayer2
    public native BufferingParams getBufferingParams();

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public int getBufferingState() {
        return 0;
    }

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public native long getCurrentPosition();

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public native long getDuration();

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public float getMaxPlayerVolume() {
        return 1.0f;
    }

    @Override // android.media.MediaPlayer2
    public native PlaybackParams getPlaybackParams();

    @Override // android.media.MediaPlayer2
    public native SyncParams getSyncParams();

    @Override // android.media.MediaPlayer2
    public native int getVideoHeight();

    @Override // android.media.MediaPlayer2
    public native int getVideoWidth();

    @Override // android.media.MediaPlayer2
    public native boolean isLooping();

    @Override // android.media.MediaPlayer2
    public native boolean isPlaying();

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public boolean isReversePlaybackSupported() {
        return false;
    }

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public void registerPlayerEventCallback(Executor executor, MediaPlayerBase.PlayerEventCallback playerEventCallback) {
    }

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public void unregisterPlayerEventCallback(MediaPlayerBase.PlayerEventCallback playerEventCallback) {
    }

    static /* synthetic */ long access$708(MediaPlayer2Impl mediaPlayer2Impl) {
        long j = mediaPlayer2Impl.mSrcIdGenerator;
        mediaPlayer2Impl.mSrcIdGenerator = 1 + j;
        return j;
    }

    static {
        System.loadLibrary("media2_jni");
        native_init();
    }

    public MediaPlayer2Impl() {
        this.mSrcIdGenerator = 0L;
        long j = 0 + 1;
        this.mSrcIdGenerator = j;
        this.mSrcIdGenerator = 1 + j;
        this.mNextSrcId = j;
        Looper looperMyLooper = Looper.myLooper();
        if (looperMyLooper != null) {
            this.mEventHandler = new EventHandler(this, looperMyLooper);
        } else {
            Looper mainLooper = Looper.getMainLooper();
            if (mainLooper != null) {
                this.mEventHandler = new EventHandler(this, mainLooper);
            } else {
                this.mEventHandler = null;
            }
        }
        HandlerThread handlerThread = new HandlerThread("MediaPlayer2TaskThread");
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mTaskHandler = new Handler(this.mHandlerThread.getLooper());
        this.mTimeProvider = new TimeProvider(this);
        this.mOpenSubtitleSources = new Vector<>();
        this.mGuard.open("close");
        native_setup(new WeakReference(this));
    }

    @Override // android.media.MediaPlayer2, java.lang.AutoCloseable
    public void close() {
        synchronized (this.mGuard) {
            release();
        }
    }

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public void play() {
        addTask(new Task(5, false) { // from class: android.media.MediaPlayer2Impl.1
            @Override // android.media.MediaPlayer2Impl.Task
            void process() {
                MediaPlayer2Impl.this.stayAwake(true);
                MediaPlayer2Impl.this._start();
            }
        });
    }

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public void prepare() {
        addTask(new Task(6, true) { // from class: android.media.MediaPlayer2Impl.2
            @Override // android.media.MediaPlayer2Impl.Task
            void process() {
                MediaPlayer2Impl.this._prepare();
            }
        });
    }

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public void pause() {
        addTask(new Task(4, false) { // from class: android.media.MediaPlayer2Impl.3
            @Override // android.media.MediaPlayer2Impl.Task
            void process() {
                MediaPlayer2Impl.this.stayAwake(false);
                MediaPlayer2Impl.this._pause();
            }
        });
    }

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public void skipToNext() {
        addTask(new Task(29, false) { // from class: android.media.MediaPlayer2Impl.4
            @Override // android.media.MediaPlayer2Impl.Task
            void process() {
            }
        });
    }

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public long getBufferedPosition() {
        return (getDuration() * ((long) this.mBufferedPercentageCurrent.get())) / 100;
    }

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public int getPlayerState() {
        int mediaPlayer2State = getMediaPlayer2State();
        if (mediaPlayer2State == 1) {
            return 0;
        }
        if (mediaPlayer2State == 2 || mediaPlayer2State == 3) {
            return 1;
        }
        return mediaPlayer2State != 4 ? 3 : 2;
    }

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public void setAudioAttributes(final AudioAttributes audioAttributes) {
        addTask(new Task(16, false) { // from class: android.media.MediaPlayer2Impl.5
            @Override // android.media.MediaPlayer2Impl.Task
            void process() {
                if (audioAttributes == null) {
                    throw new IllegalArgumentException("Cannot set AudioAttributes to null");
                }
                Parcel parcelObtain = Parcel.obtain();
                audioAttributes.writeToParcel(parcelObtain, 1);
                MediaPlayer2Impl.this.setParameter(1400, parcelObtain);
                parcelObtain.recycle();
            }
        });
    }

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public AudioAttributes getAudioAttributes() {
        Parcel parameter = getParameter(1400);
        AudioAttributes audioAttributesCreateFromParcel = AudioAttributes.CREATOR.createFromParcel(parameter);
        parameter.recycle();
        return audioAttributesCreateFromParcel;
    }

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public void setDataSource(final DataSourceDesc dataSourceDesc) {
        addTask(new Task(19, false) { // from class: android.media.MediaPlayer2Impl.6
            @Override // android.media.MediaPlayer2Impl.Task
            void process() {
                Preconditions.checkNotNull(dataSourceDesc, "the DataSourceDesc cannot be null");
                synchronized (MediaPlayer2Impl.this.mSrcLock) {
                    MediaPlayer2Impl.this.mCurrentDSD = dataSourceDesc;
                    MediaPlayer2Impl.this.mCurrentSrcId = MediaPlayer2Impl.access$708(MediaPlayer2Impl.this);
                    try {
                        MediaPlayer2Impl.this.handleDataSource(true, dataSourceDesc, MediaPlayer2Impl.this.mCurrentSrcId);
                    } catch (IOException unused) {
                    }
                }
            }
        });
    }

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public void setNextDataSource(final DataSourceDesc dataSourceDesc) {
        addTask(new Task(22, false) { // from class: android.media.MediaPlayer2Impl.7
            @Override // android.media.MediaPlayer2Impl.Task
            void process() {
                Preconditions.checkNotNull(dataSourceDesc, "the DataSourceDesc cannot be null");
                synchronized (MediaPlayer2Impl.this.mSrcLock) {
                    MediaPlayer2Impl.this.mNextDSDs = new ArrayList(1);
                    MediaPlayer2Impl.this.mNextDSDs.add(dataSourceDesc);
                    MediaPlayer2Impl.this.mNextSrcId = MediaPlayer2Impl.access$708(MediaPlayer2Impl.this);
                    MediaPlayer2Impl.this.mNextSourceState = 0;
                    MediaPlayer2Impl.this.mNextSourcePlayPending = false;
                }
                if (MediaPlayer2Impl.this.getMediaPlayer2State() != 1) {
                    synchronized (MediaPlayer2Impl.this.mSrcLock) {
                        MediaPlayer2Impl.this.prepareNextDataSource_l();
                    }
                }
            }
        });
    }

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public void setNextDataSources(final List<DataSourceDesc> list) {
        addTask(new Task(23, false) { // from class: android.media.MediaPlayer2Impl.8
            @Override // android.media.MediaPlayer2Impl.Task
            void process() {
                List list2 = list;
                if (list2 == null || list2.size() == 0) {
                    throw new IllegalArgumentException("data source list cannot be null or empty.");
                }
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    if (((DataSourceDesc) it.next()) == null) {
                        throw new IllegalArgumentException("DataSourceDesc in the source list cannot be null.");
                    }
                }
                synchronized (MediaPlayer2Impl.this.mSrcLock) {
                    MediaPlayer2Impl.this.mNextDSDs = new ArrayList(list);
                    MediaPlayer2Impl.this.mNextSrcId = MediaPlayer2Impl.access$708(MediaPlayer2Impl.this);
                    MediaPlayer2Impl.this.mNextSourceState = 0;
                    MediaPlayer2Impl.this.mNextSourcePlayPending = false;
                }
                if (MediaPlayer2Impl.this.getMediaPlayer2State() != 1) {
                    synchronized (MediaPlayer2Impl.this.mSrcLock) {
                        MediaPlayer2Impl.this.prepareNextDataSource_l();
                    }
                }
            }
        });
    }

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public DataSourceDesc getCurrentDataSource() {
        DataSourceDesc dataSourceDesc;
        synchronized (this.mSrcLock) {
            dataSourceDesc = this.mCurrentDSD;
        }
        return dataSourceDesc;
    }

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public void loopCurrent(final boolean z) {
        addTask(new Task(3, false) { // from class: android.media.MediaPlayer2Impl.9
            @Override // android.media.MediaPlayer2Impl.Task
            void process() {
                MediaPlayer2Impl.this.setLooping(z);
            }
        });
    }

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public void setPlaybackSpeed(final float f) {
        addTask(new Task(25, false) { // from class: android.media.MediaPlayer2Impl.10
            @Override // android.media.MediaPlayer2Impl.Task
            void process() {
                MediaPlayer2Impl mediaPlayer2Impl = MediaPlayer2Impl.this;
                mediaPlayer2Impl._setPlaybackParams(mediaPlayer2Impl.getPlaybackParams().setSpeed(f));
            }
        });
    }

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public float getPlaybackSpeed() {
        return getPlaybackParams().getSpeed();
    }

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public void setPlayerVolume(final float f) {
        addTask(new Task(26, false) { // from class: android.media.MediaPlayer2Impl.11
            @Override // android.media.MediaPlayer2Impl.Task
            void process() {
                MediaPlayer2Impl.this.mVolume = f;
                MediaPlayer2Impl mediaPlayer2Impl = MediaPlayer2Impl.this;
                float f2 = f;
                mediaPlayer2Impl._setVolume(f2, f2);
            }
        });
    }

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public float getPlayerVolume() {
        return this.mVolume;
    }

    @Override // android.media.MediaPlayer2
    public Parcel newRequest() {
        return Parcel.obtain();
    }

    @Override // android.media.MediaPlayer2
    public void invoke(Parcel parcel, Parcel parcel2) {
        int iNative_invoke = native_invoke(parcel, parcel2);
        parcel2.setDataPosition(0);
        if (iNative_invoke == 0) {
            return;
        }
        throw new RuntimeException("failure code: " + iNative_invoke);
    }

    /* JADX INFO: renamed from: android.media.MediaPlayer2Impl$12, reason: invalid class name */
    class AnonymousClass12 extends Task {
        final /* synthetic */ Object val$label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass12(int i, boolean z, Object obj) {
            super(i, z);
            this.val$label = obj;
        }

        @Override // android.media.MediaPlayer2Impl.Task
        void process() {
            synchronized (MediaPlayer2Impl.this.mEventCbLock) {
                for (final Pair pair : MediaPlayer2Impl.this.mEventCallbackRecords) {
                    Executor executor = (Executor) pair.first;
                    final Object obj = this.val$label;
                    executor.execute(new Runnable() { // from class: android.media.-$$Lambda$MediaPlayer2Impl$12$GAwhcv62KlexkkYkbjb8-qEksjI
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$process$0$MediaPlayer2Impl$12(pair, obj);
                        }
                    });
                }
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        public /* synthetic */ void lambda$process$0$MediaPlayer2Impl$12(Pair pair, Object obj) {
            ((MediaPlayer2.MediaPlayer2EventCallback) pair.second).onCommandLabelReached(MediaPlayer2Impl.this, obj);
        }
    }

    @Override // android.media.MediaPlayer2
    public void notifyWhenCommandLabelReached(Object obj) {
        addTask(new AnonymousClass12(1003, false, obj));
    }

    @Override // android.media.MediaPlayer2
    public void setDisplay(SurfaceHolder surfaceHolder) {
        this.mSurfaceHolder = surfaceHolder;
        _setVideoSurface(surfaceHolder != null ? surfaceHolder.getSurface() : null);
        updateSurfaceScreenOn();
    }

    @Override // android.media.MediaPlayer2
    public void setSurface(final Surface surface) {
        addTask(new Task(27, false) { // from class: android.media.MediaPlayer2Impl.13
            @Override // android.media.MediaPlayer2Impl.Task
            void process() {
                if (MediaPlayer2Impl.this.mScreenOnWhilePlaying && surface != null) {
                    Log.w(MediaPlayer2Impl.TAG, "setScreenOnWhilePlaying(true) is ineffective for Surface");
                }
                MediaPlayer2Impl.this.mSurfaceHolder = null;
                MediaPlayer2Impl.this._setVideoSurface(surface);
                MediaPlayer2Impl.this.updateSurfaceScreenOn();
            }
        });
    }

    @Override // android.media.MediaPlayer2
    public void setVideoScalingMode(final int i) {
        addTask(new Task(1002, false) { // from class: android.media.MediaPlayer2Impl.14
            @Override // android.media.MediaPlayer2Impl.Task
            void process() {
                if (!MediaPlayer2Impl.this.isVideoScalingModeSupported(i)) {
                    throw new IllegalArgumentException("Scaling mode " + i + " is not supported");
                }
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInt(6);
                    parcelObtain.writeInt(i);
                    MediaPlayer2Impl.this.invoke(parcelObtain, parcelObtain2);
                } finally {
                    parcelObtain.recycle();
                    parcelObtain2.recycle();
                }
            }
        });
    }

    private void addTask(Task task) {
        synchronized (this.mTaskLock) {
            this.mPendingTasks.add(task);
            processPendingTask_l();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processPendingTask_l() {
        if (this.mCurrentTask == null && !this.mPendingTasks.isEmpty()) {
            Task taskRemove = this.mPendingTasks.remove(0);
            this.mCurrentTask = taskRemove;
            this.mTaskHandler.post(taskRemove);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDataSource(boolean z, DataSourceDesc dataSourceDesc, long j) throws IOException {
        Preconditions.checkNotNull(dataSourceDesc, "the DataSourceDesc cannot be null");
        int type = dataSourceDesc.getType();
        if (type == 1) {
            handleDataSource(z, j, dataSourceDesc.getMedia2DataSource());
            return;
        }
        if (type == 2) {
            handleDataSource(z, j, dataSourceDesc.getFileDescriptor(), dataSourceDesc.getFileDescriptorOffset(), dataSourceDesc.getFileDescriptorLength());
        } else if (type == 3) {
            handleDataSource(z, j, dataSourceDesc.getUriContext(), dataSourceDesc.getUri(), dataSourceDesc.getUriHeaders(), dataSourceDesc.getUriCookies());
        }
    }

    private void handleDataSource(boolean z, long j, Context context, Uri uri, Map<String, String> map, List<HttpCookie> list) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();
        String scheme = uri.getScheme();
        String authorityWithoutUserId = ContentProvider.getAuthorityWithoutUserId(uri.getAuthority());
        if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            handleDataSource(z, j, uri.getPath(), (Map<String, String>) null, (List<HttpCookie>) null);
            return;
        }
        if ("content".equals(scheme) && "settings".equals(authorityWithoutUserId)) {
            int defaultType = RingtoneManager.getDefaultType(uri);
            Uri cacheForType = RingtoneManager.getCacheForType(defaultType, context.getUserId());
            Uri actualDefaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, defaultType);
            if (attemptDataSource(z, j, contentResolver, cacheForType) || attemptDataSource(z, j, contentResolver, actualDefaultRingtoneUri)) {
                return;
            }
            handleDataSource(z, j, uri.toString(), map, list);
            return;
        }
        if (attemptDataSource(z, j, contentResolver, uri)) {
            return;
        }
        handleDataSource(z, j, uri.toString(), map, list);
    }

    private boolean attemptDataSource(boolean z, long j, ContentResolver contentResolver, Uri uri) {
        try {
            AssetFileDescriptor assetFileDescriptorOpenAssetFileDescriptor = contentResolver.openAssetFileDescriptor(uri, FullBackup.ROOT_TREE_TOKEN);
            try {
                if (assetFileDescriptorOpenAssetFileDescriptor.getDeclaredLength() < 0) {
                    handleDataSource(z, j, assetFileDescriptorOpenAssetFileDescriptor.getFileDescriptor(), 0L, DataSourceDesc.LONG_MAX);
                } else {
                    handleDataSource(z, j, assetFileDescriptorOpenAssetFileDescriptor.getFileDescriptor(), assetFileDescriptorOpenAssetFileDescriptor.getStartOffset(), assetFileDescriptorOpenAssetFileDescriptor.getDeclaredLength());
                }
                if (assetFileDescriptorOpenAssetFileDescriptor != null) {
                    assetFileDescriptorOpenAssetFileDescriptor.close();
                }
                return true;
            } catch (Throwable th) {
                try {
                    throw th;
                } catch (Throwable th2) {
                    if (assetFileDescriptorOpenAssetFileDescriptor != null) {
                        try {
                            assetFileDescriptorOpenAssetFileDescriptor.close();
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                        }
                    }
                    throw th2;
                }
            }
        } catch (IOException | NullPointerException | SecurityException e) {
            Log.w(TAG, "Couldn't open " + uri + ": " + e);
            return false;
        }
    }

    private void handleDataSource(boolean z, long j, String str, Map<String, String> map, List<HttpCookie> list) throws IOException {
        String[] strArr;
        String[] strArr2;
        if (map != null) {
            String[] strArr3 = new String[map.size()];
            String[] strArr4 = new String[map.size()];
            int i = 0;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                strArr3[i] = entry.getKey();
                strArr4[i] = entry.getValue();
                i++;
            }
            strArr = strArr3;
            strArr2 = strArr4;
        } else {
            strArr = null;
            strArr2 = null;
        }
        handleDataSource(z, j, str, strArr, strArr2, list);
    }

    private void handleDataSource(boolean z, long j, String str, String[] strArr, String[] strArr2, List<HttpCookie> list) throws IOException {
        String path;
        Uri uri = Uri.parse(str);
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            path = uri.getPath();
        } else {
            path = str;
            if (scheme != null) {
                nativeHandleDataSourceUrl(z, j, Media2HTTPService.createHTTPService(str, list), str, strArr, strArr2);
                return;
            }
        }
        File file = new File(path);
        if (file.exists()) {
            FileInputStream fileInputStream = new FileInputStream(file);
            handleDataSource(z, j, fileInputStream.getFD(), 0L, DataSourceDesc.LONG_MAX);
            fileInputStream.close();
            return;
        }
        throw new IOException("handleDataSource failed.");
    }

    private void handleDataSource(boolean z, long j, FileDescriptor fileDescriptor, long j2, long j3) throws IOException {
        nativeHandleDataSourceFD(z, j, fileDescriptor, j2, j3);
    }

    private void handleDataSource(boolean z, long j, Media2DataSource media2DataSource) {
        nativeHandleDataSourceCallback(z, j, media2DataSource);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void prepareNextDataSource_l() {
        List<DataSourceDesc> list = this.mNextDSDs;
        if (list == null || list.isEmpty() || this.mNextSourceState != 0) {
            return;
        }
        try {
            this.mNextSourceState = 1;
            handleDataSource(false, this.mNextDSDs.get(0), this.mNextSrcId);
        } catch (Exception unused) {
            final Message messageObtainMessage = this.mEventHandler.obtainMessage(100, 1, -1010, null);
            final long j = this.mNextSrcId;
            this.mEventHandler.post(new Runnable() { // from class: android.media.MediaPlayer2Impl.15
                @Override // java.lang.Runnable
                public void run() {
                    MediaPlayer2Impl.this.mEventHandler.handleMessage(messageObtainMessage, j);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playNextDataSource_l() {
        List<DataSourceDesc> list = this.mNextDSDs;
        if (list == null || list.isEmpty()) {
            return;
        }
        int i = this.mNextSourceState;
        if (i == 2) {
            this.mCurrentDSD = this.mNextDSDs.get(0);
            this.mCurrentSrcId = this.mNextSrcId;
            this.mBufferedPercentageCurrent.set(this.mBufferedPercentageNext.get());
            this.mNextDSDs.remove(0);
            long j = this.mSrcIdGenerator;
            this.mSrcIdGenerator = 1 + j;
            this.mNextSrcId = j;
            this.mBufferedPercentageNext.set(0);
            this.mNextSourceState = 0;
            this.mNextSourcePlayPending = false;
            final long j2 = this.mCurrentSrcId;
            try {
                nativePlayNextDataSource(j2);
                return;
            } catch (Exception unused) {
                final Message messageObtainMessage = this.mEventHandler.obtainMessage(100, 1, -1010, null);
                this.mEventHandler.post(new Runnable() { // from class: android.media.MediaPlayer2Impl.16
                    @Override // java.lang.Runnable
                    public void run() {
                        MediaPlayer2Impl.this.mEventHandler.handleMessage(messageObtainMessage, j2);
                    }
                });
                return;
            }
        }
        if (i == 0) {
            prepareNextDataSource_l();
        }
        this.mNextSourcePlayPending = true;
    }

    private int getAudioStreamType() {
        if (this.mStreamType == Integer.MIN_VALUE) {
            this.mStreamType = _getAudioStreamType();
        }
        return this.mStreamType;
    }

    @Override // android.media.MediaPlayer2
    public void stop() {
        stayAwake(false);
        _stop();
    }

    @Override // android.media.MediaPlayer2, android.media.AudioRouting
    public boolean setPreferredDevice(AudioDeviceInfo audioDeviceInfo) {
        if (audioDeviceInfo != null && !audioDeviceInfo.isSink()) {
            return false;
        }
        boolean zNative_setOutputDevice = native_setOutputDevice(audioDeviceInfo != null ? audioDeviceInfo.getId() : 0);
        if (zNative_setOutputDevice) {
            synchronized (this) {
                this.mPreferredDevice = audioDeviceInfo;
            }
        }
        return zNative_setOutputDevice;
    }

    @Override // android.media.MediaPlayer2, android.media.AudioRouting
    public AudioDeviceInfo getPreferredDevice() {
        AudioDeviceInfo audioDeviceInfo;
        synchronized (this) {
            audioDeviceInfo = this.mPreferredDevice;
        }
        return audioDeviceInfo;
    }

    @Override // android.media.MediaPlayer2, android.media.AudioRouting
    public AudioDeviceInfo getRoutedDevice() {
        int iNative_getRoutedDeviceId = native_getRoutedDeviceId();
        if (iNative_getRoutedDeviceId == 0) {
            return null;
        }
        AudioDeviceInfo[] devicesStatic = AudioManager.getDevicesStatic(2);
        for (int i = 0; i < devicesStatic.length; i++) {
            if (devicesStatic[i].getId() == iNative_getRoutedDeviceId) {
                return devicesStatic[i];
            }
        }
        return null;
    }

    private void enableNativeRoutingCallbacksLocked(boolean z) {
        if (this.mRoutingChangeListeners.size() == 0) {
            native_enableDeviceCallback(z);
        }
    }

    @Override // android.media.MediaPlayer2, android.media.AudioRouting
    public void addOnRoutingChangedListener(AudioRouting.OnRoutingChangedListener onRoutingChangedListener, Handler handler) {
        synchronized (this.mRoutingChangeListeners) {
            if (onRoutingChangedListener != null) {
                if (!this.mRoutingChangeListeners.containsKey(onRoutingChangedListener)) {
                    enableNativeRoutingCallbacksLocked(true);
                    ArrayMap<AudioRouting.OnRoutingChangedListener, NativeRoutingEventHandlerDelegate> arrayMap = this.mRoutingChangeListeners;
                    if (handler == null) {
                        handler = this.mEventHandler;
                    }
                    arrayMap.put(onRoutingChangedListener, new NativeRoutingEventHandlerDelegate(this, onRoutingChangedListener, handler));
                }
            }
        }
    }

    @Override // android.media.MediaPlayer2, android.media.AudioRouting
    public void removeOnRoutingChangedListener(AudioRouting.OnRoutingChangedListener onRoutingChangedListener) {
        synchronized (this.mRoutingChangeListeners) {
            if (this.mRoutingChangeListeners.containsKey(onRoutingChangedListener)) {
                this.mRoutingChangeListeners.remove(onRoutingChangedListener);
                enableNativeRoutingCallbacksLocked(false);
            }
        }
    }

    @Override // android.media.MediaPlayer2
    public void setWakeMode(Context context, int i) {
        boolean z = true;
        if (SystemProperties.getBoolean("audio.offload.ignore_setawake", false)) {
            Log.w(TAG, "IGNORING setWakeMode " + i);
            return;
        }
        PowerManager.WakeLock wakeLock = this.mWakeLock;
        if (wakeLock != null) {
            if (wakeLock.isHeld()) {
                this.mWakeLock.release();
            } else {
                z = false;
            }
            this.mWakeLock = null;
        } else {
            z = false;
        }
        PowerManager.WakeLock wakeLockNewWakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(i | 536870912, MediaPlayer2Impl.class.getName());
        this.mWakeLock = wakeLockNewWakeLock;
        wakeLockNewWakeLock.setReferenceCounted(false);
        if (z) {
            this.mWakeLock.acquire();
        }
    }

    @Override // android.media.MediaPlayer2
    public void setScreenOnWhilePlaying(boolean z) {
        if (this.mScreenOnWhilePlaying != z) {
            if (z && this.mSurfaceHolder == null) {
                Log.w(TAG, "setScreenOnWhilePlaying(true) is ineffective without a SurfaceHolder");
            }
            this.mScreenOnWhilePlaying = z;
            updateSurfaceScreenOn();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stayAwake(boolean z) {
        PowerManager.WakeLock wakeLock = this.mWakeLock;
        if (wakeLock != null) {
            if (z && !wakeLock.isHeld()) {
                this.mWakeLock.acquire();
            } else if (!z && this.mWakeLock.isHeld()) {
                this.mWakeLock.release();
            }
        }
        this.mStayAwake = z;
        updateSurfaceScreenOn();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSurfaceScreenOn() {
        SurfaceHolder surfaceHolder = this.mSurfaceHolder;
        if (surfaceHolder != null) {
            surfaceHolder.setKeepScreenOn(this.mScreenOnWhilePlaying && this.mStayAwake);
        }
    }

    @Override // android.media.MediaPlayer2
    public PersistableBundle getMetrics() {
        return native_getMetrics();
    }

    @Override // android.media.MediaPlayer2
    public int getMediaPlayer2State() {
        return native_getMediaPlayer2State();
    }

    @Override // android.media.MediaPlayer2
    public void setBufferingParams(final BufferingParams bufferingParams) {
        addTask(new Task(1001, false) { // from class: android.media.MediaPlayer2Impl.17
            @Override // android.media.MediaPlayer2Impl.Task
            void process() {
                Preconditions.checkNotNull(bufferingParams, "the BufferingParams cannot be null");
                MediaPlayer2Impl.this._setBufferingParams(bufferingParams);
            }
        });
    }

    @Override // android.media.MediaPlayer2
    public PlaybackParams easyPlaybackParams(float f, int i) {
        PlaybackParams playbackParams = new PlaybackParams();
        playbackParams.allowDefaults();
        if (i == 0) {
            playbackParams.setSpeed(f).setPitch(1.0f);
        } else if (i == 1) {
            playbackParams.setSpeed(f).setPitch(1.0f).setAudioFallbackMode(2);
        } else if (i == 2) {
            playbackParams.setSpeed(f).setPitch(f);
        } else {
            throw new IllegalArgumentException("Audio playback mode " + i + " is not supported");
        }
        return playbackParams;
    }

    @Override // android.media.MediaPlayer2
    public void setPlaybackParams(final PlaybackParams playbackParams) {
        addTask(new Task(24, false) { // from class: android.media.MediaPlayer2Impl.18
            @Override // android.media.MediaPlayer2Impl.Task
            void process() {
                Preconditions.checkNotNull(playbackParams, "the PlaybackParams cannot be null");
                MediaPlayer2Impl.this._setPlaybackParams(playbackParams);
            }
        });
    }

    @Override // android.media.MediaPlayer2
    public void setSyncParams(final SyncParams syncParams) {
        addTask(new Task(28, false) { // from class: android.media.MediaPlayer2Impl.19
            @Override // android.media.MediaPlayer2Impl.Task
            void process() {
                Preconditions.checkNotNull(syncParams, "the SyncParams cannot be null");
                MediaPlayer2Impl.this._setSyncParams(syncParams);
            }
        });
    }

    @Override // android.media.MediaPlayer2
    public void seekTo(final long j, final int i) {
        addTask(new Task(14, true) { // from class: android.media.MediaPlayer2Impl.20
            @Override // android.media.MediaPlayer2Impl.Task
            void process() {
                int i2 = i;
                if (i2 < 0 || i2 > 3) {
                    throw new IllegalArgumentException("Illegal seek mode: " + i);
                }
                long j2 = j;
                if (j2 > 2147483647L) {
                    Log.w(MediaPlayer2Impl.TAG, "seekTo offset " + j2 + " is too large, cap to 2147483647");
                    j2 = 2147483647L;
                } else if (j2 < -2147483648L) {
                    Log.w(MediaPlayer2Impl.TAG, "seekTo offset " + j2 + " is too small, cap to -2147483648");
                    j2 = -2147483648L;
                }
                MediaPlayer2Impl.this._seekTo(j2, i);
            }
        });
    }

    @Override // android.media.MediaPlayer2
    public MediaTimestamp getTimestamp() {
        try {
            return new MediaTimestamp(getCurrentPosition() * 1000, System.nanoTime(), isPlaying() ? getPlaybackParams().getSpeed() : 0.0f);
        } catch (IllegalStateException unused) {
            return null;
        }
    }

    @Override // android.media.MediaPlayer2
    public Metadata getMetadata(boolean z, boolean z2) {
        Parcel parcelObtain = Parcel.obtain();
        Metadata metadata = new Metadata();
        if (!native_getMetadata(z, z2, parcelObtain)) {
            parcelObtain.recycle();
            return null;
        }
        if (metadata.parse(parcelObtain)) {
            return metadata;
        }
        parcelObtain.recycle();
        return null;
    }

    @Override // android.media.MediaPlayer2
    public int setMetadataFilter(Set<Integer> set, Set<Integer> set2) {
        Parcel parcelNewRequest = newRequest();
        int iDataSize = parcelNewRequest.dataSize() + ((set.size() + 1 + 1 + set2.size()) * 4);
        if (parcelNewRequest.dataCapacity() < iDataSize) {
            parcelNewRequest.setDataCapacity(iDataSize);
        }
        parcelNewRequest.writeInt(set.size());
        Iterator<Integer> it = set.iterator();
        while (it.hasNext()) {
            parcelNewRequest.writeInt(it.next().intValue());
        }
        parcelNewRequest.writeInt(set2.size());
        Iterator<Integer> it2 = set2.iterator();
        while (it2.hasNext()) {
            parcelNewRequest.writeInt(it2.next().intValue());
        }
        return native_setMetadataFilter(parcelNewRequest);
    }

    @Override // android.media.MediaPlayer2, android.media.MediaPlayerBase
    public void reset() {
        this.mSelectedSubtitleTrackIndex = -1;
        synchronized (this.mOpenSubtitleSources) {
            Iterator<InputStream> it = this.mOpenSubtitleSources.iterator();
            while (it.hasNext()) {
                try {
                    it.next().close();
                } catch (IOException unused) {
                }
            }
            this.mOpenSubtitleSources.clear();
        }
        SubtitleController subtitleController = this.mSubtitleController;
        if (subtitleController != null) {
            subtitleController.reset();
        }
        TimeProvider timeProvider = this.mTimeProvider;
        if (timeProvider != null) {
            timeProvider.close();
            this.mTimeProvider = null;
        }
        synchronized (this.mEventCbLock) {
            this.mEventCallbackRecords.clear();
        }
        synchronized (this.mDrmEventCbLock) {
            this.mDrmEventCallbackRecords.clear();
        }
        stayAwake(false);
        _reset();
        EventHandler eventHandler = this.mEventHandler;
        if (eventHandler != null) {
            eventHandler.removeCallbacksAndMessages(null);
        }
        synchronized (this.mIndexTrackPairs) {
            this.mIndexTrackPairs.clear();
            this.mInbandTrackIndices.clear();
        }
        resetDrmState();
    }

    @Override // android.media.MediaPlayer2
    public void notifyAt(long j) {
        _notifyAt(j);
    }

    @Override // android.media.MediaPlayer2
    public void setAudioSessionId(final int i) {
        addTask(new Task(17, false) { // from class: android.media.MediaPlayer2Impl.21
            @Override // android.media.MediaPlayer2Impl.Task
            void process() {
                MediaPlayer2Impl.this._setAudioSessionId(i);
            }
        });
    }

    @Override // android.media.MediaPlayer2
    public void attachAuxEffect(final int i) {
        addTask(new Task(1, false) { // from class: android.media.MediaPlayer2Impl.22
            @Override // android.media.MediaPlayer2Impl.Task
            void process() {
                MediaPlayer2Impl.this._attachAuxEffect(i);
            }
        });
    }

    @Override // android.media.MediaPlayer2
    public void setAuxEffectSendLevel(final float f) {
        addTask(new Task(18, false) { // from class: android.media.MediaPlayer2Impl.23
            @Override // android.media.MediaPlayer2Impl.Task
            void process() {
                MediaPlayer2Impl.this._setAuxEffectSendLevel(f);
            }
        });
    }

    public static final class TrackInfoImpl extends MediaPlayer2.TrackInfo {
        static final Parcelable.Creator<TrackInfoImpl> CREATOR = new Parcelable.Creator<TrackInfoImpl>() { // from class: android.media.MediaPlayer2Impl.TrackInfoImpl.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public TrackInfoImpl createFromParcel(Parcel parcel) {
                return new TrackInfoImpl(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public TrackInfoImpl[] newArray(int i) {
                return new TrackInfoImpl[i];
            }
        };
        final MediaFormat mFormat;
        final int mTrackType;

        @Override // android.media.MediaPlayer2.TrackInfo
        public int getTrackType() {
            return this.mTrackType;
        }

        @Override // android.media.MediaPlayer2.TrackInfo
        public String getLanguage() {
            String string = this.mFormat.getString("language");
            return string == null ? "und" : string;
        }

        @Override // android.media.MediaPlayer2.TrackInfo
        public MediaFormat getFormat() {
            int i = this.mTrackType;
            if (i == 3 || i == 4) {
                return this.mFormat;
            }
            return null;
        }

        TrackInfoImpl(Parcel parcel) {
            this.mTrackType = parcel.readInt();
            MediaFormat mediaFormatCreateSubtitleFormat = MediaFormat.createSubtitleFormat(parcel.readString(), parcel.readString());
            this.mFormat = mediaFormatCreateSubtitleFormat;
            if (this.mTrackType == 4) {
                mediaFormatCreateSubtitleFormat.setInteger(MediaFormat.KEY_IS_AUTOSELECT, parcel.readInt());
                this.mFormat.setInteger(MediaFormat.KEY_IS_DEFAULT, parcel.readInt());
                this.mFormat.setInteger(MediaFormat.KEY_IS_FORCED_SUBTITLE, parcel.readInt());
            }
        }

        TrackInfoImpl(int i, MediaFormat mediaFormat) {
            this.mTrackType = i;
            this.mFormat = mediaFormat;
        }

        void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mTrackType);
            parcel.writeString(getLanguage());
            if (this.mTrackType == 4) {
                parcel.writeString(this.mFormat.getString(MediaFormat.KEY_MIME));
                parcel.writeInt(this.mFormat.getInteger(MediaFormat.KEY_IS_AUTOSELECT));
                parcel.writeInt(this.mFormat.getInteger(MediaFormat.KEY_IS_DEFAULT));
                parcel.writeInt(this.mFormat.getInteger(MediaFormat.KEY_IS_FORCED_SUBTITLE));
            }
        }

        @Override // android.media.MediaPlayer2.TrackInfo
        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append(getClass().getName());
            sb.append('{');
            int i = this.mTrackType;
            if (i == 1) {
                sb.append("VIDEO");
            } else if (i == 2) {
                sb.append("AUDIO");
            } else if (i == 3) {
                sb.append("TIMEDTEXT");
            } else if (i == 4) {
                sb.append("SUBTITLE");
            } else {
                sb.append(IccCardConstants.INTENT_VALUE_ICC_UNKNOWN);
            }
            sb.append(", " + this.mFormat.toString());
            sb.append("}");
            return sb.toString();
        }
    }

    @Override // android.media.MediaPlayer2
    public List<MediaPlayer2.TrackInfo> getTrackInfo() {
        List<MediaPlayer2.TrackInfo> listAsList;
        TrackInfoImpl[] inbandTrackInfoImpl = getInbandTrackInfoImpl();
        synchronized (this.mIndexTrackPairs) {
            int size = this.mIndexTrackPairs.size();
            TrackInfoImpl[] trackInfoImplArr = new TrackInfoImpl[size];
            for (int i = 0; i < size; i++) {
                Pair<Integer, SubtitleTrack> pair = this.mIndexTrackPairs.get(i);
                if (pair.first != null) {
                    trackInfoImplArr[i] = inbandTrackInfoImpl[pair.first.intValue()];
                } else {
                    SubtitleTrack subtitleTrack = pair.second;
                    trackInfoImplArr[i] = new TrackInfoImpl(subtitleTrack.getTrackType(), subtitleTrack.getFormat());
                }
            }
            listAsList = Arrays.asList(trackInfoImplArr);
        }
        return listAsList;
    }

    private TrackInfoImpl[] getInbandTrackInfoImpl() throws IllegalStateException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            parcelObtain.writeInt(1);
            invoke(parcelObtain, parcelObtain2);
            return (TrackInfoImpl[]) parcelObtain2.createTypedArray(TrackInfoImpl.CREATOR);
        } finally {
            parcelObtain.recycle();
            parcelObtain2.recycle();
        }
    }

    private static boolean availableMimeTypeForExternalSource(String str) {
        return "application/x-subrip".equals(str);
    }

    @Override // android.media.MediaPlayer2
    public void setSubtitleAnchor(SubtitleController subtitleController, SubtitleController.Anchor anchor) {
        this.mSubtitleController = subtitleController;
        subtitleController.setAnchor(anchor);
    }

    private synchronized void setSubtitleAnchor() {
        if (this.mSubtitleController == null && ActivityThread.currentApplication() != null) {
            final HandlerThread handlerThread = new HandlerThread("SetSubtitleAnchorThread");
            handlerThread.start();
            new Handler(handlerThread.getLooper()).post(new Runnable() { // from class: android.media.MediaPlayer2Impl.24
                @Override // java.lang.Runnable
                public void run() {
                    Application applicationCurrentApplication = ActivityThread.currentApplication();
                    MediaPlayer2Impl.this.mSubtitleController = new SubtitleController(applicationCurrentApplication, MediaPlayer2Impl.this.mTimeProvider, MediaPlayer2Impl.this);
                    MediaPlayer2Impl.this.mSubtitleController.setAnchor(new SubtitleController.Anchor() { // from class: android.media.MediaPlayer2Impl.24.1
                        @Override // android.media.SubtitleController.Anchor
                        public void setSubtitleWidget(SubtitleTrack.RenderingWidget renderingWidget) {
                        }

                        @Override // android.media.SubtitleController.Anchor
                        public Looper getSubtitleLooper() {
                            return Looper.getMainLooper();
                        }
                    });
                    handlerThread.getLooper().quitSafely();
                }
            });
            try {
                handlerThread.join();
            } catch (InterruptedException unused) {
                Thread.currentThread().interrupt();
                Log.w(TAG, "failed to join SetSubtitleAnchorThread");
            }
        }
    }

    @Override // android.media.MediaPlayer2, android.media.SubtitleController.Listener
    public void onSubtitleTrackSelected(SubtitleTrack subtitleTrack) {
        int i = this.mSelectedSubtitleTrackIndex;
        if (i >= 0) {
            try {
                selectOrDeselectInbandTrack(i, false);
            } catch (IllegalStateException unused) {
            }
            this.mSelectedSubtitleTrackIndex = -1;
        }
        setOnSubtitleDataListener(null);
        if (subtitleTrack == null) {
            return;
        }
        synchronized (this.mIndexTrackPairs) {
            for (Pair<Integer, SubtitleTrack> pair : this.mIndexTrackPairs) {
                if (pair.first != null && pair.second == subtitleTrack) {
                    this.mSelectedSubtitleTrackIndex = pair.first.intValue();
                    break;
                }
            }
        }
        int i2 = this.mSelectedSubtitleTrackIndex;
        if (i2 >= 0) {
            try {
                selectOrDeselectInbandTrack(i2, true);
            } catch (IllegalStateException unused2) {
            }
            setOnSubtitleDataListener(this.mSubtitleDataListener);
        }
    }

    @Override // android.media.MediaPlayer2
    public void addSubtitleSource(final InputStream inputStream, final MediaFormat mediaFormat) throws IllegalStateException {
        if (inputStream != null) {
            synchronized (this.mOpenSubtitleSources) {
                this.mOpenSubtitleSources.add(inputStream);
            }
        } else {
            Log.w(TAG, "addSubtitleSource called with null InputStream");
        }
        getMediaTimeProvider();
        final HandlerThread handlerThread = new HandlerThread("SubtitleReadThread", 9);
        handlerThread.start();
        new Handler(handlerThread.getLooper()).post(new Runnable() { // from class: android.media.MediaPlayer2Impl.26
            private int addTrack() {
                SubtitleTrack subtitleTrackAddTrack;
                if (inputStream == null || MediaPlayer2Impl.this.mSubtitleController == null || (subtitleTrackAddTrack = MediaPlayer2Impl.this.mSubtitleController.addTrack(mediaFormat)) == null) {
                    return 901;
                }
                Scanner scanner = new Scanner(inputStream, "UTF-8");
                String next = scanner.useDelimiter("\\A").next();
                synchronized (MediaPlayer2Impl.this.mOpenSubtitleSources) {
                    MediaPlayer2Impl.this.mOpenSubtitleSources.remove(inputStream);
                }
                scanner.close();
                synchronized (MediaPlayer2Impl.this.mIndexTrackPairs) {
                    MediaPlayer2Impl.this.mIndexTrackPairs.add(Pair.create(null, subtitleTrackAddTrack));
                }
                TimeProvider.EventHandler eventHandler = MediaPlayer2Impl.this.mTimeProvider.mEventHandler;
                eventHandler.sendMessage(eventHandler.obtainMessage(1, 4, 0, Pair.create(subtitleTrackAddTrack, next.getBytes())));
                return 803;
            }

            @Override // java.lang.Runnable
            public void run() {
                int iAddTrack = addTrack();
                if (MediaPlayer2Impl.this.mEventHandler != null) {
                    MediaPlayer2Impl.this.mEventHandler.sendMessage(MediaPlayer2Impl.this.mEventHandler.obtainMessage(200, iAddTrack, 0, null));
                }
                handlerThread.getLooper().quitSafely();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scanInternalSubtitleTracks() {
        setSubtitleAnchor();
        populateInbandTracks();
        SubtitleController subtitleController = this.mSubtitleController;
        if (subtitleController != null) {
            subtitleController.selectDefaultTrack();
        }
    }

    private void populateInbandTracks() {
        TrackInfoImpl[] inbandTrackInfoImpl = getInbandTrackInfoImpl();
        synchronized (this.mIndexTrackPairs) {
            for (int i = 0; i < inbandTrackInfoImpl.length; i++) {
                if (!this.mInbandTrackIndices.get(i)) {
                    this.mInbandTrackIndices.set(i);
                    if (inbandTrackInfoImpl[i].getTrackType() == 4) {
                        this.mIndexTrackPairs.add(Pair.create(Integer.valueOf(i), this.mSubtitleController.addTrack(inbandTrackInfoImpl[i].getFormat())));
                    } else {
                        this.mIndexTrackPairs.add(Pair.create(Integer.valueOf(i), null));
                    }
                }
            }
        }
    }

    @Override // android.media.MediaPlayer2
    public void addTimedTextSource(String str, String str2) throws IOException {
        if (!availableMimeTypeForExternalSource(str2)) {
            throw new IllegalArgumentException("Illegal mimeType for timed text source: " + str2);
        }
        File file = new File(str);
        if (file.exists()) {
            FileInputStream fileInputStream = new FileInputStream(file);
            addTimedTextSource(fileInputStream.getFD(), str2);
            fileInputStream.close();
            return;
        }
        throw new IOException(str);
    }

    @Override // android.media.MediaPlayer2
    public void addTimedTextSource(Context context, Uri uri, String str) throws IOException {
        String scheme = uri.getScheme();
        if (scheme == null || scheme.equals(ContentResolver.SCHEME_FILE)) {
            addTimedTextSource(uri.getPath(), str);
            return;
        }
        AutoCloseable autoCloseable = null;
        try {
            AssetFileDescriptor assetFileDescriptorOpenAssetFileDescriptor = context.getContentResolver().openAssetFileDescriptor(uri, FullBackup.ROOT_TREE_TOKEN);
            if (assetFileDescriptorOpenAssetFileDescriptor == null) {
                if (assetFileDescriptorOpenAssetFileDescriptor != null) {
                    assetFileDescriptorOpenAssetFileDescriptor.close();
                }
            } else {
                addTimedTextSource(assetFileDescriptorOpenAssetFileDescriptor.getFileDescriptor(), str);
                if (assetFileDescriptorOpenAssetFileDescriptor != null) {
                    assetFileDescriptorOpenAssetFileDescriptor.close();
                }
            }
        } catch (IOException unused) {
            if (0 == 0) {
                return;
            }
            autoCloseable.close();
        } catch (SecurityException unused2) {
            if (0 == 0) {
                return;
            }
            autoCloseable.close();
        } catch (Throwable th) {
            if (0 != 0) {
                autoCloseable.close();
            }
            throw th;
        }
    }

    @Override // android.media.MediaPlayer2
    public void addTimedTextSource(FileDescriptor fileDescriptor, String str) {
        addTimedTextSource(fileDescriptor, 0L, DataSourceDesc.LONG_MAX, str);
    }

    @Override // android.media.MediaPlayer2
    public void addTimedTextSource(FileDescriptor fileDescriptor, final long j, final long j2, String str) {
        if (!availableMimeTypeForExternalSource(str)) {
            throw new IllegalArgumentException("Illegal mimeType for timed text source: " + str);
        }
        try {
            final FileDescriptor fileDescriptorDup = Os.dup(fileDescriptor);
            MediaFormat mediaFormat = new MediaFormat();
            mediaFormat.setString(MediaFormat.KEY_MIME, str);
            mediaFormat.setInteger(MediaFormat.KEY_IS_TIMED_TEXT, 1);
            if (this.mSubtitleController == null) {
                setSubtitleAnchor();
            }
            if (!this.mSubtitleController.hasRendererFor(mediaFormat)) {
                this.mSubtitleController.registerRenderer(new SRTRenderer(ActivityThread.currentApplication(), this.mEventHandler));
            }
            final SubtitleTrack subtitleTrackAddTrack = this.mSubtitleController.addTrack(mediaFormat);
            synchronized (this.mIndexTrackPairs) {
                this.mIndexTrackPairs.add(Pair.create(null, subtitleTrackAddTrack));
            }
            getMediaTimeProvider();
            final HandlerThread handlerThread = new HandlerThread("TimedTextReadThread", 9);
            handlerThread.start();
            new Handler(handlerThread.getLooper()).post(new Runnable() { // from class: android.media.MediaPlayer2Impl.27
                private int addTrack() {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    try {
                        Os.lseek(fileDescriptorDup, j, OsConstants.SEEK_SET);
                        byte[] bArr = new byte[4096];
                        long j3 = 0;
                        while (j3 < j2) {
                            int i = IoBridge.read(fileDescriptorDup, bArr, 0, (int) Math.min(4096, j2 - j3));
                            if (i < 0) {
                                break;
                            }
                            byteArrayOutputStream.write(bArr, 0, i);
                            j3 += (long) i;
                        }
                        TimeProvider.EventHandler eventHandler = MediaPlayer2Impl.this.mTimeProvider.mEventHandler;
                        eventHandler.sendMessage(eventHandler.obtainMessage(1, 4, 0, Pair.create(subtitleTrackAddTrack, byteArrayOutputStream.toByteArray())));
                        return 803;
                    } catch (Exception e) {
                        Log.e(MediaPlayer2Impl.TAG, e.getMessage(), e);
                        return 900;
                    } finally {
                        try {
                            Os.close(fileDescriptorDup);
                        } catch (ErrnoException e2) {
                            Log.e(MediaPlayer2Impl.TAG, e2.getMessage(), e2);
                        }
                    }
                }

                @Override // java.lang.Runnable
                public void run() {
                    int iAddTrack = addTrack();
                    if (MediaPlayer2Impl.this.mEventHandler != null) {
                        MediaPlayer2Impl.this.mEventHandler.sendMessage(MediaPlayer2Impl.this.mEventHandler.obtainMessage(200, iAddTrack, 0, null));
                    }
                    handlerThread.getLooper().quitSafely();
                }
            });
        } catch (ErrnoException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override // android.media.MediaPlayer2
    public int getSelectedTrack(int i) {
        SubtitleTrack selectedTrack;
        if (this.mSubtitleController != null && ((i == 4 || i == 3) && (selectedTrack = this.mSubtitleController.getSelectedTrack()) != null)) {
            synchronized (this.mIndexTrackPairs) {
                for (int i2 = 0; i2 < this.mIndexTrackPairs.size(); i2++) {
                    if (this.mIndexTrackPairs.get(i2).second == selectedTrack && selectedTrack.getTrackType() == i) {
                        return i2;
                    }
                }
            }
        }
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            parcelObtain.writeInt(7);
            parcelObtain.writeInt(i);
            invoke(parcelObtain, parcelObtain2);
            int i3 = parcelObtain2.readInt();
            synchronized (this.mIndexTrackPairs) {
                for (int i4 = 0; i4 < this.mIndexTrackPairs.size(); i4++) {
                    Pair<Integer, SubtitleTrack> pair = this.mIndexTrackPairs.get(i4);
                    if (pair.first != null && pair.first.intValue() == i3) {
                        parcelObtain.recycle();
                        parcelObtain2.recycle();
                        return i4;
                    }
                }
                parcelObtain.recycle();
                parcelObtain2.recycle();
                return -1;
            }
        } catch (Throwable th) {
            parcelObtain.recycle();
            parcelObtain2.recycle();
            throw th;
        }
    }

    @Override // android.media.MediaPlayer2
    public void selectTrack(final int i) {
        addTask(new Task(15, false) { // from class: android.media.MediaPlayer2Impl.28
            @Override // android.media.MediaPlayer2Impl.Task
            void process() {
                MediaPlayer2Impl.this.selectOrDeselectTrack(i, true);
            }
        });
    }

    @Override // android.media.MediaPlayer2
    public void deselectTrack(final int i) {
        addTask(new Task(2, false) { // from class: android.media.MediaPlayer2Impl.29
            @Override // android.media.MediaPlayer2Impl.Task
            void process() {
                MediaPlayer2Impl.this.selectOrDeselectTrack(i, false);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void selectOrDeselectTrack(int i, boolean z) throws IllegalStateException {
        populateInbandTracks();
        try {
            Pair<Integer, SubtitleTrack> pair = this.mIndexTrackPairs.get(i);
            SubtitleTrack subtitleTrack = pair.second;
            if (subtitleTrack == null) {
                selectOrDeselectInbandTrack(pair.first.intValue(), z);
                return;
            }
            SubtitleController subtitleController = this.mSubtitleController;
            if (subtitleController == null) {
                return;
            }
            if (!z) {
                if (subtitleController.getSelectedTrack() == subtitleTrack) {
                    this.mSubtitleController.selectTrack(null);
                    return;
                } else {
                    Log.w(TAG, "trying to deselect track that was not selected");
                    return;
                }
            }
            if (subtitleTrack.getTrackType() == 3) {
                int selectedTrack = getSelectedTrack(3);
                synchronized (this.mIndexTrackPairs) {
                    if (selectedTrack >= 0) {
                        if (selectedTrack < this.mIndexTrackPairs.size()) {
                            Pair<Integer, SubtitleTrack> pair2 = this.mIndexTrackPairs.get(selectedTrack);
                            if (pair2.first != null && pair2.second == null) {
                                selectOrDeselectInbandTrack(pair2.first.intValue(), false);
                            }
                        }
                    }
                }
            }
            this.mSubtitleController.selectTrack(subtitleTrack);
        } catch (ArrayIndexOutOfBoundsException unused) {
        }
    }

    private void selectOrDeselectInbandTrack(int i, boolean z) throws IllegalStateException {
        Parcel parcelObtain = Parcel.obtain();
        Parcel parcelObtain2 = Parcel.obtain();
        try {
            parcelObtain.writeInt(z ? 4 : 5);
            parcelObtain.writeInt(i);
            invoke(parcelObtain, parcelObtain2);
        } finally {
            parcelObtain.recycle();
            parcelObtain2.recycle();
        }
    }

    protected void finalize() throws Throwable {
        CloseGuard closeGuard = this.mGuard;
        if (closeGuard != null) {
            closeGuard.warnIfOpen();
        }
        close();
        native_finalize();
    }

    private void release() {
        stayAwake(false);
        updateSurfaceScreenOn();
        synchronized (this.mEventCbLock) {
            this.mEventCallbackRecords.clear();
        }
        HandlerThread handlerThread = this.mHandlerThread;
        if (handlerThread != null) {
            handlerThread.quitSafely();
            this.mHandlerThread = null;
        }
        TimeProvider timeProvider = this.mTimeProvider;
        if (timeProvider != null) {
            timeProvider.close();
            this.mTimeProvider = null;
        }
        this.mOnSubtitleDataListener = null;
        this.mOnDrmConfigHelper = null;
        synchronized (this.mDrmEventCbLock) {
            this.mDrmEventCallbackRecords.clear();
        }
        resetDrmState();
        _release();
    }

    @Override // android.media.MediaPlayer2
    public MediaTimeProvider getMediaTimeProvider() {
        if (this.mTimeProvider == null) {
            this.mTimeProvider = new TimeProvider(this);
        }
        return this.mTimeProvider;
    }

    /* JADX INFO: Access modifiers changed from: private */
    class EventHandler extends Handler {
        private MediaPlayer2Impl mMediaPlayer;

        public EventHandler(MediaPlayer2Impl mediaPlayer2Impl, Looper looper) {
            super(looper);
            this.mMediaPlayer = mediaPlayer2Impl;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            handleMessage(message, 0L);
        }

        public void handleMessage(Message message, long j) {
            final DrmInfoImpl drmInfoImplMakeCopy;
            final DataSourceDesc dataSourceDesc;
            if (this.mMediaPlayer.mNativeContext == 0) {
                Log.w(MediaPlayer2Impl.TAG, "mediaplayer2 went away with unhandled events");
                return;
            }
            final int i = message.arg1;
            final int i2 = message.arg2;
            int i3 = message.what;
            final TimedMetaData timedMetaDataCreateTimedMetaDataFromParcel = null;
            final TimedText timedText = null;
            if (i3 == 210) {
                if (message.obj == null) {
                    Log.w(MediaPlayer2Impl.TAG, "MEDIA_DRM_INFO msg.obj=NULL");
                    return;
                }
                if (message.obj instanceof Parcel) {
                    synchronized (MediaPlayer2Impl.this.mDrmLock) {
                        drmInfoImplMakeCopy = MediaPlayer2Impl.this.mDrmInfoImpl != null ? MediaPlayer2Impl.this.mDrmInfoImpl.makeCopy() : null;
                    }
                    if (drmInfoImplMakeCopy != null) {
                        synchronized (MediaPlayer2Impl.this.mEventCbLock) {
                            for (final Pair pair : MediaPlayer2Impl.this.mDrmEventCallbackRecords) {
                                ((Executor) pair.first).execute(new Runnable() { // from class: android.media.-$$Lambda$MediaPlayer2Impl$EventHandler$XDpOSvYSapoVyl-BYW0W8pLfp3A
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        this.f$0.lambda$handleMessage$1$MediaPlayer2Impl$EventHandler(pair, drmInfoImplMakeCopy);
                                    }
                                });
                            }
                        }
                        return;
                    }
                    return;
                }
                Log.w(MediaPlayer2Impl.TAG, "MEDIA_DRM_INFO msg.obj of unexpected type " + message.obj);
                return;
            }
            if (i3 != 10000) {
                switch (i3) {
                    case 0:
                        return;
                    case 1:
                        try {
                            MediaPlayer2Impl.this.scanInternalSubtitleTracks();
                            break;
                        } catch (RuntimeException unused) {
                            sendMessage(obtainMessage(100, 1, -1010, null));
                        }
                        synchronized (MediaPlayer2Impl.this.mSrcLock) {
                            Log.i(MediaPlayer2Impl.TAG, "MEDIA_PREPARED: srcId=" + j + ", currentSrcId=" + MediaPlayer2Impl.this.mCurrentSrcId + ", nextSrcId=" + MediaPlayer2Impl.this.mNextSrcId);
                            if (j == MediaPlayer2Impl.this.mCurrentSrcId) {
                                dataSourceDesc = MediaPlayer2Impl.this.mCurrentDSD;
                                MediaPlayer2Impl.this.prepareNextDataSource_l();
                            } else if (MediaPlayer2Impl.this.mNextDSDs == null || MediaPlayer2Impl.this.mNextDSDs.isEmpty() || j != MediaPlayer2Impl.this.mNextSrcId) {
                                dataSourceDesc = null;
                            } else {
                                dataSourceDesc = (DataSourceDesc) MediaPlayer2Impl.this.mNextDSDs.get(0);
                                MediaPlayer2Impl.this.mNextSourceState = 2;
                                if (MediaPlayer2Impl.this.mNextSourcePlayPending) {
                                    MediaPlayer2Impl.this.playNextDataSource_l();
                                }
                            }
                            break;
                        }
                        if (dataSourceDesc != null) {
                            synchronized (MediaPlayer2Impl.this.mEventCbLock) {
                                for (final Pair pair2 : MediaPlayer2Impl.this.mEventCallbackRecords) {
                                    ((Executor) pair2.first).execute(new Runnable() { // from class: android.media.-$$Lambda$MediaPlayer2Impl$EventHandler$a55WUDW_Ad0Vmi1x4yZhQXvPqdc
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            this.f$0.lambda$handleMessage$0$MediaPlayer2Impl$EventHandler(pair2, dataSourceDesc);
                                        }
                                    });
                                }
                                break;
                            }
                        }
                        synchronized (MediaPlayer2Impl.this.mTaskLock) {
                            if (MediaPlayer2Impl.this.mCurrentTask != null && MediaPlayer2Impl.this.mCurrentTask.mMediaCallType == 6 && MediaPlayer2Impl.this.mCurrentTask.mDSD == dataSourceDesc && MediaPlayer2Impl.this.mCurrentTask.mNeedToWaitForEventToComplete) {
                                MediaPlayer2Impl.this.mCurrentTask.sendCompleteNotification(0);
                                MediaPlayer2Impl.this.mCurrentTask = null;
                                MediaPlayer2Impl.this.processPendingTask_l();
                            }
                            break;
                        }
                        return;
                    case 2:
                        final DataSourceDesc dataSourceDesc2 = MediaPlayer2Impl.this.mCurrentDSD;
                        synchronized (MediaPlayer2Impl.this.mSrcLock) {
                            if (j == MediaPlayer2Impl.this.mCurrentSrcId) {
                                Log.i(MediaPlayer2Impl.TAG, "MEDIA_PLAYBACK_COMPLETE: srcId=" + j + ", currentSrcId=" + MediaPlayer2Impl.this.mCurrentSrcId + ", nextSrcId=" + MediaPlayer2Impl.this.mNextSrcId);
                                MediaPlayer2Impl.this.playNextDataSource_l();
                            }
                            break;
                        }
                        synchronized (MediaPlayer2Impl.this.mEventCbLock) {
                            for (final Pair pair3 : MediaPlayer2Impl.this.mEventCallbackRecords) {
                                ((Executor) pair3.first).execute(new Runnable() { // from class: android.media.-$$Lambda$MediaPlayer2Impl$EventHandler$SRqj_-_1CH9_ez58ikKgR8GPWEc
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        this.f$0.lambda$handleMessage$2$MediaPlayer2Impl$EventHandler(pair3, dataSourceDesc2);
                                    }
                                });
                            }
                            break;
                        }
                        MediaPlayer2Impl.this.stayAwake(false);
                        return;
                    case 3:
                        final int i4 = message.arg1;
                        synchronized (MediaPlayer2Impl.this.mEventCbLock) {
                            if (j == MediaPlayer2Impl.this.mCurrentSrcId) {
                                MediaPlayer2Impl.this.mBufferedPercentageCurrent.set(i4);
                                for (final Pair pair4 : MediaPlayer2Impl.this.mEventCallbackRecords) {
                                    ((Executor) pair4.first).execute(new Runnable() { // from class: android.media.-$$Lambda$MediaPlayer2Impl$EventHandler$Dr_ImxKsZcrvP7slv6KPxdUdzXk
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            this.f$0.lambda$handleMessage$3$MediaPlayer2Impl$EventHandler(pair4, i4);
                                        }
                                    });
                                }
                            } else if (j == MediaPlayer2Impl.this.mNextSrcId && !MediaPlayer2Impl.this.mNextDSDs.isEmpty()) {
                                MediaPlayer2Impl.this.mBufferedPercentageNext.set(i4);
                                final DataSourceDesc dataSourceDesc3 = (DataSourceDesc) MediaPlayer2Impl.this.mNextDSDs.get(0);
                                for (final Pair pair5 : MediaPlayer2Impl.this.mEventCallbackRecords) {
                                    ((Executor) pair5.first).execute(new Runnable() { // from class: android.media.-$$Lambda$MediaPlayer2Impl$EventHandler$hsCyoCNpv30l9tb7sOpVC4dnMy8
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            this.f$0.lambda$handleMessage$4$MediaPlayer2Impl$EventHandler(pair5, dataSourceDesc3, i4);
                                        }
                                    });
                                }
                            }
                            break;
                        }
                        return;
                    case 4:
                        synchronized (MediaPlayer2Impl.this.mTaskLock) {
                            if (MediaPlayer2Impl.this.mCurrentTask != null && MediaPlayer2Impl.this.mCurrentTask.mMediaCallType == 14 && MediaPlayer2Impl.this.mCurrentTask.mNeedToWaitForEventToComplete) {
                                MediaPlayer2Impl.this.mCurrentTask.sendCompleteNotification(0);
                                MediaPlayer2Impl.this.mCurrentTask = null;
                                MediaPlayer2Impl.this.processPendingTask_l();
                            }
                            break;
                        }
                        break;
                    case 5:
                        final int i5 = message.arg1;
                        final int i6 = message.arg2;
                        synchronized (MediaPlayer2Impl.this.mEventCbLock) {
                            for (final Pair pair6 : MediaPlayer2Impl.this.mEventCallbackRecords) {
                                ((Executor) pair6.first).execute(new Runnable() { // from class: android.media.-$$Lambda$MediaPlayer2Impl$EventHandler$ALpPmFUNsJxKZK0N2HhQK6ZY4XM
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        this.f$0.lambda$handleMessage$5$MediaPlayer2Impl$EventHandler(pair6, i5, i6);
                                    }
                                });
                            }
                            break;
                        }
                        return;
                    case 6:
                    case 7:
                        TimeProvider timeProvider = MediaPlayer2Impl.this.mTimeProvider;
                        if (timeProvider != null) {
                            timeProvider.onPaused(message.what == 7);
                            return;
                        }
                        return;
                    case 8:
                        TimeProvider timeProvider2 = MediaPlayer2Impl.this.mTimeProvider;
                        if (timeProvider2 != null) {
                            timeProvider2.onStopped();
                            return;
                        }
                        return;
                    case 9:
                        break;
                    default:
                        switch (i3) {
                            case 98:
                                TimeProvider timeProvider3 = MediaPlayer2Impl.this.mTimeProvider;
                                if (timeProvider3 != null) {
                                    timeProvider3.onNotifyTime();
                                    return;
                                }
                                return;
                            case 99:
                                if (message.obj instanceof Parcel) {
                                    Parcel parcel = (Parcel) message.obj;
                                    timedText = new TimedText(parcel);
                                    parcel.recycle();
                                }
                                synchronized (MediaPlayer2Impl.this.mEventCbLock) {
                                    for (final Pair pair7 : MediaPlayer2Impl.this.mEventCallbackRecords) {
                                        ((Executor) pair7.first).execute(new Runnable() { // from class: android.media.-$$Lambda$MediaPlayer2Impl$EventHandler$sx24vrhw_-7V07cadDNXlQ5kv04
                                            @Override // java.lang.Runnable
                                            public final void run() {
                                                this.f$0.lambda$handleMessage$9$MediaPlayer2Impl$EventHandler(pair7, timedText);
                                            }
                                        });
                                    }
                                    break;
                                }
                                return;
                            case 100:
                                Log.e(MediaPlayer2Impl.TAG, "Error (" + message.arg1 + "," + message.arg2 + ")");
                                synchronized (MediaPlayer2Impl.this.mEventCbLock) {
                                    for (final Pair pair8 : MediaPlayer2Impl.this.mEventCallbackRecords) {
                                        ((Executor) pair8.first).execute(new Runnable() { // from class: android.media.-$$Lambda$MediaPlayer2Impl$EventHandler$5fCusDxj0OAxGzH6d86WnqVt8Rw
                                            @Override // java.lang.Runnable
                                            public final void run() {
                                                this.f$0.lambda$handleMessage$6$MediaPlayer2Impl$EventHandler(pair8, i, i2);
                                            }
                                        });
                                        ((Executor) pair8.first).execute(new Runnable() { // from class: android.media.-$$Lambda$MediaPlayer2Impl$EventHandler$9rzGOSqsKQVeN_cdPvY8essrTyg
                                            @Override // java.lang.Runnable
                                            public final void run() {
                                                this.f$0.lambda$handleMessage$7$MediaPlayer2Impl$EventHandler(pair8);
                                            }
                                        });
                                    }
                                    break;
                                }
                                MediaPlayer2Impl.this.stayAwake(false);
                                return;
                            default:
                                switch (i3) {
                                    case 200:
                                        int i7 = message.arg1;
                                        if (i7 != 2) {
                                            if (i7 == 802) {
                                                try {
                                                    MediaPlayer2Impl.this.scanInternalSubtitleTracks();
                                                } catch (RuntimeException unused2) {
                                                    sendMessage(obtainMessage(100, 1, -1010, null));
                                                }
                                                break;
                                            } else if (i7 != 803) {
                                                switch (i7) {
                                                    case 700:
                                                        Log.i(MediaPlayer2Impl.TAG, "Info (" + message.arg1 + "," + message.arg2 + ")");
                                                        break;
                                                    case 701:
                                                    case 702:
                                                        TimeProvider timeProvider4 = MediaPlayer2Impl.this.mTimeProvider;
                                                        if (timeProvider4 != null) {
                                                            timeProvider4.onBuffering(message.arg1 == 701);
                                                        }
                                                        break;
                                                }
                                            }
                                            message.arg1 = 802;
                                            if (MediaPlayer2Impl.this.mSubtitleController != null) {
                                                MediaPlayer2Impl.this.mSubtitleController.selectDefaultTrack();
                                            }
                                        } else if (j == MediaPlayer2Impl.this.mCurrentSrcId) {
                                            MediaPlayer2Impl.this.prepareNextDataSource_l();
                                        }
                                        synchronized (MediaPlayer2Impl.this.mEventCbLock) {
                                            for (final Pair pair9 : MediaPlayer2Impl.this.mEventCallbackRecords) {
                                                ((Executor) pair9.first).execute(new Runnable() { // from class: android.media.-$$Lambda$MediaPlayer2Impl$EventHandler$iPmZQ0HxMVwbBcbhgpHbun3WGTk
                                                    @Override // java.lang.Runnable
                                                    public final void run() {
                                                        this.f$0.lambda$handleMessage$8$MediaPlayer2Impl$EventHandler(pair9, i, i2);
                                                    }
                                                });
                                            }
                                            break;
                                        }
                                        return;
                                    case 201:
                                        MediaPlayer2.OnSubtitleDataListener onSubtitleDataListener = MediaPlayer2Impl.this.mOnSubtitleDataListener;
                                        if (onSubtitleDataListener != null && (message.obj instanceof Parcel)) {
                                            Parcel parcel2 = (Parcel) message.obj;
                                            SubtitleData subtitleData = new SubtitleData(parcel2);
                                            parcel2.recycle();
                                            onSubtitleDataListener.onSubtitleData(this.mMediaPlayer, subtitleData);
                                            return;
                                        }
                                        return;
                                    case 202:
                                        if (message.obj instanceof Parcel) {
                                            Parcel parcel3 = (Parcel) message.obj;
                                            timedMetaDataCreateTimedMetaDataFromParcel = TimedMetaData.createTimedMetaDataFromParcel(parcel3);
                                            parcel3.recycle();
                                        }
                                        synchronized (MediaPlayer2Impl.this.mEventCbLock) {
                                            for (final Pair pair10 : MediaPlayer2Impl.this.mEventCallbackRecords) {
                                                ((Executor) pair10.first).execute(new Runnable() { // from class: android.media.-$$Lambda$MediaPlayer2Impl$EventHandler$5DmGtkuYQXExyXOBI9Qvu64NQ68
                                                    @Override // java.lang.Runnable
                                                    public final void run() {
                                                        this.f$0.lambda$handleMessage$10$MediaPlayer2Impl$EventHandler(pair10, timedMetaDataCreateTimedMetaDataFromParcel);
                                                    }
                                                });
                                            }
                                            break;
                                        }
                                        return;
                                    default:
                                        Log.e(MediaPlayer2Impl.TAG, "Unknown message type " + message.what);
                                        return;
                                }
                        }
                }
                TimeProvider timeProvider5 = MediaPlayer2Impl.this.mTimeProvider;
                if (timeProvider5 != null) {
                    timeProvider5.onSeekComplete(this.mMediaPlayer);
                    return;
                }
                return;
            }
            AudioManager.resetAudioPortGeneration();
            synchronized (MediaPlayer2Impl.this.mRoutingChangeListeners) {
                Iterator it = MediaPlayer2Impl.this.mRoutingChangeListeners.values().iterator();
                while (it.hasNext()) {
                    ((NativeRoutingEventHandlerDelegate) it.next()).notifyClient();
                }
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        public /* synthetic */ void lambda$handleMessage$0$MediaPlayer2Impl$EventHandler(Pair pair, DataSourceDesc dataSourceDesc) {
            ((MediaPlayer2.MediaPlayer2EventCallback) pair.second).onInfo(this.mMediaPlayer, dataSourceDesc, 100, 0);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public /* synthetic */ void lambda$handleMessage$1$MediaPlayer2Impl$EventHandler(Pair pair, DrmInfoImpl drmInfoImpl) {
            ((MediaPlayer2.DrmEventCallback) pair.second).onDrmInfo(this.mMediaPlayer, MediaPlayer2Impl.this.mCurrentDSD, drmInfoImpl);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public /* synthetic */ void lambda$handleMessage$2$MediaPlayer2Impl$EventHandler(Pair pair, DataSourceDesc dataSourceDesc) {
            ((MediaPlayer2.MediaPlayer2EventCallback) pair.second).onInfo(this.mMediaPlayer, dataSourceDesc, 5, 0);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public /* synthetic */ void lambda$handleMessage$3$MediaPlayer2Impl$EventHandler(Pair pair, int i) {
            ((MediaPlayer2.MediaPlayer2EventCallback) pair.second).onInfo(this.mMediaPlayer, MediaPlayer2Impl.this.mCurrentDSD, 704, i);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public /* synthetic */ void lambda$handleMessage$4$MediaPlayer2Impl$EventHandler(Pair pair, DataSourceDesc dataSourceDesc, int i) {
            ((MediaPlayer2.MediaPlayer2EventCallback) pair.second).onInfo(this.mMediaPlayer, dataSourceDesc, 704, i);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public /* synthetic */ void lambda$handleMessage$5$MediaPlayer2Impl$EventHandler(Pair pair, int i, int i2) {
            ((MediaPlayer2.MediaPlayer2EventCallback) pair.second).onVideoSizeChanged(this.mMediaPlayer, MediaPlayer2Impl.this.mCurrentDSD, i, i2);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public /* synthetic */ void lambda$handleMessage$6$MediaPlayer2Impl$EventHandler(Pair pair, int i, int i2) {
            ((MediaPlayer2.MediaPlayer2EventCallback) pair.second).onError(this.mMediaPlayer, MediaPlayer2Impl.this.mCurrentDSD, i, i2);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public /* synthetic */ void lambda$handleMessage$7$MediaPlayer2Impl$EventHandler(Pair pair) {
            ((MediaPlayer2.MediaPlayer2EventCallback) pair.second).onInfo(this.mMediaPlayer, MediaPlayer2Impl.this.mCurrentDSD, 5, 0);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public /* synthetic */ void lambda$handleMessage$8$MediaPlayer2Impl$EventHandler(Pair pair, int i, int i2) {
            ((MediaPlayer2.MediaPlayer2EventCallback) pair.second).onInfo(this.mMediaPlayer, MediaPlayer2Impl.this.mCurrentDSD, i, i2);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public /* synthetic */ void lambda$handleMessage$9$MediaPlayer2Impl$EventHandler(Pair pair, TimedText timedText) {
            ((MediaPlayer2.MediaPlayer2EventCallback) pair.second).onTimedText(this.mMediaPlayer, MediaPlayer2Impl.this.mCurrentDSD, timedText);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public /* synthetic */ void lambda$handleMessage$10$MediaPlayer2Impl$EventHandler(Pair pair, TimedMetaData timedMetaData) {
            ((MediaPlayer2.MediaPlayer2EventCallback) pair.second).onTimedMetaDataAvailable(this.mMediaPlayer, MediaPlayer2Impl.this.mCurrentDSD, timedMetaData);
        }
    }

    private static void postEventFromNative(Object obj, final long j, int i, int i2, int i3, Object obj2) {
        MediaPlayer2Impl mediaPlayer2Impl = (MediaPlayer2Impl) ((WeakReference) obj).get();
        if (mediaPlayer2Impl == null) {
            return;
        }
        if (i == 1) {
            synchronized (mediaPlayer2Impl.mDrmLock) {
                mediaPlayer2Impl.mDrmInfoResolved = true;
            }
        } else if (i != 200) {
            if (i == 210) {
                Log.v(TAG, "postEventFromNative MEDIA_DRM_INFO");
                if (obj2 instanceof Parcel) {
                    DrmInfoImpl drmInfoImpl = new DrmInfoImpl((Parcel) obj2);
                    synchronized (mediaPlayer2Impl.mDrmLock) {
                        mediaPlayer2Impl.mDrmInfoImpl = drmInfoImpl;
                    }
                } else {
                    Log.w(TAG, "MEDIA_DRM_INFO msg.obj of unexpected type " + obj2);
                }
            }
        } else if (i2 == 2) {
            new Thread(new Runnable() { // from class: android.media.MediaPlayer2Impl.30
                @Override // java.lang.Runnable
                public void run() {
                    MediaPlayer2Impl.this.play();
                }
            }).start();
            Thread.yield();
        }
        EventHandler eventHandler = mediaPlayer2Impl.mEventHandler;
        if (eventHandler != null) {
            final Message messageObtainMessage = eventHandler.obtainMessage(i, i2, i3, obj2);
            mediaPlayer2Impl.mEventHandler.post(new Runnable() { // from class: android.media.MediaPlayer2Impl.31
                @Override // java.lang.Runnable
                public void run() {
                    MediaPlayer2Impl.this.mEventHandler.handleMessage(messageObtainMessage, j);
                }
            });
        }
    }

    @Override // android.media.MediaPlayer2
    public void setMediaPlayer2EventCallback(Executor executor, MediaPlayer2.MediaPlayer2EventCallback mediaPlayer2EventCallback) {
        if (mediaPlayer2EventCallback == null) {
            throw new IllegalArgumentException("Illegal null MediaPlayer2EventCallback");
        }
        if (executor == null) {
            throw new IllegalArgumentException("Illegal null Executor for the MediaPlayer2EventCallback");
        }
        synchronized (this.mEventCbLock) {
            this.mEventCallbackRecords.add(new Pair<>(executor, mediaPlayer2EventCallback));
        }
    }

    @Override // android.media.MediaPlayer2
    public void clearMediaPlayer2EventCallback() {
        synchronized (this.mEventCbLock) {
            this.mEventCallbackRecords.clear();
        }
    }

    @Override // android.media.MediaPlayer2
    public void setOnSubtitleDataListener(MediaPlayer2.OnSubtitleDataListener onSubtitleDataListener) {
        this.mOnSubtitleDataListener = onSubtitleDataListener;
    }

    @Override // android.media.MediaPlayer2
    public void setOnDrmConfigHelper(MediaPlayer2.OnDrmConfigHelper onDrmConfigHelper) {
        synchronized (this.mDrmLock) {
            this.mOnDrmConfigHelper = onDrmConfigHelper;
        }
    }

    @Override // android.media.MediaPlayer2
    public void setDrmEventCallback(Executor executor, MediaPlayer2.DrmEventCallback drmEventCallback) {
        if (drmEventCallback == null) {
            throw new IllegalArgumentException("Illegal null MediaPlayer2EventCallback");
        }
        if (executor == null) {
            throw new IllegalArgumentException("Illegal null Executor for the MediaPlayer2EventCallback");
        }
        synchronized (this.mDrmEventCbLock) {
            this.mDrmEventCallbackRecords.add(new Pair<>(executor, drmEventCallback));
        }
    }

    @Override // android.media.MediaPlayer2
    public void clearDrmEventCallback() {
        synchronized (this.mDrmEventCbLock) {
            this.mDrmEventCallbackRecords.clear();
        }
    }

    @Override // android.media.MediaPlayer2
    public MediaPlayer2.DrmInfo getDrmInfo() {
        DrmInfoImpl drmInfoImplMakeCopy;
        synchronized (this.mDrmLock) {
            if (!this.mDrmInfoResolved && this.mDrmInfoImpl == null) {
                Log.v(TAG, "The Player has not been prepared yet");
                throw new IllegalStateException("The Player has not been prepared yet");
            }
            drmInfoImplMakeCopy = this.mDrmInfoImpl != null ? this.mDrmInfoImpl.makeCopy() : null;
        }
        return drmInfoImplMakeCopy;
    }

    @Override // android.media.MediaPlayer2
    public void prepareDrm(UUID uuid) throws UnsupportedSchemeException, MediaPlayer2.ProvisioningNetworkErrorException, ResourceBusyException, MediaPlayer2.ProvisioningServerErrorException {
        boolean z;
        Log.v(TAG, "prepareDrm: uuid: " + uuid + " mOnDrmConfigHelper: " + this.mOnDrmConfigHelper);
        synchronized (this.mDrmLock) {
            if (this.mDrmInfoImpl == null) {
                Log.e(TAG, "prepareDrm(): Wrong usage: The player must be prepared and DRM info be retrieved before this call.");
                throw new IllegalStateException("prepareDrm(): Wrong usage: The player must be prepared and DRM info be retrieved before this call.");
            }
            if (this.mActiveDrmScheme) {
                String str = "prepareDrm(): Wrong usage: There is already an active DRM scheme with " + this.mDrmUUID;
                Log.e(TAG, str);
                throw new IllegalStateException(str);
            }
            if (this.mPrepareDrmInProgress) {
                Log.e(TAG, "prepareDrm(): Wrong usage: There is already a pending prepareDrm call.");
                throw new IllegalStateException("prepareDrm(): Wrong usage: There is already a pending prepareDrm call.");
            }
            if (this.mDrmProvisioningInProgress) {
                Log.e(TAG, "prepareDrm(): Unexpectd: Provisioning is already in progress.");
                throw new IllegalStateException("prepareDrm(): Unexpectd: Provisioning is already in progress.");
            }
            cleanDrmObj();
            z = true;
            this.mPrepareDrmInProgress = true;
            try {
                prepareDrm_createDrmStep(uuid);
                this.mDrmConfigAllowed = true;
            } catch (Exception e) {
                Log.w(TAG, "prepareDrm(): Exception ", e);
                this.mPrepareDrmInProgress = false;
                throw e;
            }
        }
        MediaPlayer2.OnDrmConfigHelper onDrmConfigHelper = this.mOnDrmConfigHelper;
        if (onDrmConfigHelper != null) {
            onDrmConfigHelper.onDrmConfig(this, this.mCurrentDSD);
        }
        synchronized (this.mDrmLock) {
            try {
                this.mDrmConfigAllowed = false;
                try {
                    try {
                        try {
                            try {
                                prepareDrm_openSessionStep(uuid);
                                this.mDrmUUID = uuid;
                                this.mActiveDrmScheme = true;
                                if (!this.mDrmProvisioningInProgress) {
                                    this.mPrepareDrmInProgress = false;
                                }
                            } catch (IllegalStateException unused) {
                                Log.e(TAG, "prepareDrm(): Wrong usage: The player must be in the prepared state to call prepareDrm().");
                                throw new IllegalStateException("prepareDrm(): Wrong usage: The player must be in the prepared state to call prepareDrm().");
                            }
                        } catch (Exception e2) {
                            Log.e(TAG, "prepareDrm: Exception " + e2);
                            throw e2;
                        }
                    } catch (Throwable th) {
                        th = th;
                        if (!this.mDrmProvisioningInProgress) {
                            this.mPrepareDrmInProgress = false;
                        }
                        if (z) {
                            cleanDrmObj();
                        }
                        throw th;
                    }
                } catch (NotProvisionedException unused2) {
                    Log.w(TAG, "prepareDrm: NotProvisionedException");
                    int iHandleProvisioninig = HandleProvisioninig(uuid);
                    if (iHandleProvisioninig != 0) {
                        if (iHandleProvisioninig == 1) {
                            Log.e(TAG, "prepareDrm: Provisioning was required but failed due to a network error.");
                            throw new ProvisioningNetworkErrorExceptionImpl("prepareDrm: Provisioning was required but failed due to a network error.");
                        }
                        if (iHandleProvisioninig == 2) {
                            Log.e(TAG, "prepareDrm: Provisioning was required but the request was denied by the server.");
                            throw new ProvisioningServerErrorExceptionImpl("prepareDrm: Provisioning was required but the request was denied by the server.");
                        }
                        Log.e(TAG, "prepareDrm: Post-provisioning preparation failed.");
                        throw new IllegalStateException("prepareDrm: Post-provisioning preparation failed.");
                    }
                    if (!this.mDrmProvisioningInProgress) {
                        this.mPrepareDrmInProgress = false;
                    }
                    z = false;
                }
            } catch (Throwable th2) {
                th = th2;
                z = false;
            }
        }
        if (z) {
            synchronized (this.mDrmEventCbLock) {
                for (final Pair<Executor, MediaPlayer2.DrmEventCallback> pair : this.mDrmEventCallbackRecords) {
                    pair.first.execute(new Runnable() { // from class: android.media.-$$Lambda$MediaPlayer2Impl$1jR0wmXW_cOZenZs6Xt6lhAUeQ0
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$prepareDrm$0$MediaPlayer2Impl(pair);
                        }
                    });
                }
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public /* synthetic */ void lambda$prepareDrm$0$MediaPlayer2Impl(Pair pair) {
        ((MediaPlayer2.DrmEventCallback) pair.second).onDrmPrepared(this, this.mCurrentDSD, 0);
    }

    @Override // android.media.MediaPlayer2
    public void releaseDrm() throws MediaPlayer2.NoDrmSchemeException {
        addTask(new Task(12, false) { // from class: android.media.MediaPlayer2Impl.32
            @Override // android.media.MediaPlayer2Impl.Task
            void process() throws MediaPlayer2.NoDrmSchemeException {
                synchronized (MediaPlayer2Impl.this.mDrmLock) {
                    Log.v(MediaPlayer2Impl.TAG, "releaseDrm:");
                    if (MediaPlayer2Impl.this.mActiveDrmScheme) {
                        try {
                            MediaPlayer2Impl.this._releaseDrm();
                            MediaPlayer2Impl.this.cleanDrmObj();
                            MediaPlayer2Impl.this.mActiveDrmScheme = false;
                        } catch (IllegalStateException e) {
                            Log.w(MediaPlayer2Impl.TAG, "releaseDrm: Exception ", e);
                            throw new IllegalStateException("releaseDrm: The player is not in a valid state.");
                        } catch (Exception e2) {
                            Log.e(MediaPlayer2Impl.TAG, "releaseDrm: Exception ", e2);
                        }
                    } else {
                        Log.e(MediaPlayer2Impl.TAG, "releaseDrm(): No active DRM scheme to release.");
                        throw new NoDrmSchemeExceptionImpl("releaseDrm: No active DRM scheme to release.");
                    }
                }
            }
        });
    }

    /* JADX WARN: Code duplicated, block: B:13:0x0048 A[Catch: Exception -> 0x0043, NotProvisionedException -> 0x0088, all -> 0x00a6, TryCatch #1 {NotProvisionedException -> 0x0088, blocks: (B:8:0x0040, B:13:0x0048, B:15:0x004f), top: B:28:0x0040, outer: #0 }] */
    /* JADX WARN: Code duplicated, block: B:14:0x004e  */
    @Override // android.media.MediaPlayer2
    public MediaDrm.KeyRequest getDrmKeyRequest(byte[] bArr, byte[] bArr2, String str, int i, Map<String, String> map) throws MediaPlayer2.NoDrmSchemeException {
        HashMap<String, String> map2;
        MediaDrm.KeyRequest keyRequest;
        Log.v(TAG, "getDrmKeyRequest:  keySetId: " + bArr + " initData:" + bArr2 + " mimeType: " + str + " keyType: " + i + " optionalParameters: " + map);
        synchronized (this.mDrmLock) {
            if (!this.mActiveDrmScheme) {
                Log.e(TAG, "getDrmKeyRequest NoDrmSchemeException");
                throw new NoDrmSchemeExceptionImpl("getDrmKeyRequest: Has to set a DRM scheme first.");
            }
            if (i != 3) {
                try {
                    try {
                        bArr = this.mDrmSessionId;
                        byte[] bArr3 = bArr;
                        if (map != null) {
                            map2 = new HashMap<>(map);
                        } else {
                            map2 = null;
                        }
                        keyRequest = this.mDrmObj.getKeyRequest(bArr3, bArr2, str, i, map2);
                        Log.v(TAG, "getDrmKeyRequest:   --> request: " + keyRequest);
                    } catch (NotProvisionedException unused) {
                        Log.w(TAG, "getDrmKeyRequest NotProvisionedException: Unexpected. Shouldn't have reached here.");
                        throw new IllegalStateException("getDrmKeyRequest: Unexpected provisioning error.");
                    }
                } catch (Exception e) {
                    Log.w(TAG, "getDrmKeyRequest Exception " + e);
                    throw e;
                }
            } else {
                byte[] bArr4 = bArr;
                if (map != null) {
                    map2 = new HashMap<>(map);
                } else {
                    map2 = null;
                }
                keyRequest = this.mDrmObj.getKeyRequest(bArr4, bArr2, str, i, map2);
                Log.v(TAG, "getDrmKeyRequest:   --> request: " + keyRequest);
            }
            throw th;
        }
        return keyRequest;
    }

    @Override // android.media.MediaPlayer2
    public byte[] provideDrmKeyResponse(byte[] bArr, byte[] bArr2) throws DeniedByServerException, MediaPlayer2.NoDrmSchemeException {
        byte[] bArr3;
        byte[] bArrProvideKeyResponse;
        Log.v(TAG, "provideDrmKeyResponse: keySetId: " + bArr + " response: " + bArr2);
        synchronized (this.mDrmLock) {
            if (!this.mActiveDrmScheme) {
                Log.e(TAG, "getDrmKeyRequest NoDrmSchemeException");
                throw new NoDrmSchemeExceptionImpl("getDrmKeyRequest: Has to set a DRM scheme first.");
            }
            if (bArr == null) {
                try {
                    bArr3 = this.mDrmSessionId;
                } catch (NotProvisionedException unused) {
                    Log.w(TAG, "provideDrmKeyResponse NotProvisionedException: Unexpected. Shouldn't have reached here.");
                    throw new IllegalStateException("provideDrmKeyResponse: Unexpected provisioning error.");
                } catch (Exception e) {
                    Log.w(TAG, "provideDrmKeyResponse Exception " + e);
                    throw e;
                }
            } else {
                bArr3 = bArr;
            }
            bArrProvideKeyResponse = this.mDrmObj.provideKeyResponse(bArr3, bArr2);
            Log.v(TAG, "provideDrmKeyResponse: keySetId: " + bArr + " response: " + bArr2 + " --> " + bArrProvideKeyResponse);
        }
        return bArrProvideKeyResponse;
    }

    @Override // android.media.MediaPlayer2
    public void restoreDrmKeys(final byte[] bArr) throws MediaPlayer2.NoDrmSchemeException {
        addTask(new Task(13, false) { // from class: android.media.MediaPlayer2Impl.33
            @Override // android.media.MediaPlayer2Impl.Task
            void process() throws MediaPlayer2.NoDrmSchemeException {
                Log.v(MediaPlayer2Impl.TAG, "restoreDrmKeys: keySetId: " + bArr);
                synchronized (MediaPlayer2Impl.this.mDrmLock) {
                    if (MediaPlayer2Impl.this.mActiveDrmScheme) {
                        try {
                            MediaPlayer2Impl.this.mDrmObj.restoreKeys(MediaPlayer2Impl.this.mDrmSessionId, bArr);
                        } catch (Exception e) {
                            Log.w(MediaPlayer2Impl.TAG, "restoreKeys Exception " + e);
                            throw e;
                        }
                    } else {
                        Log.w(MediaPlayer2Impl.TAG, "restoreDrmKeys NoDrmSchemeException");
                        throw new NoDrmSchemeExceptionImpl("restoreDrmKeys: Has to set a DRM scheme first.");
                    }
                }
            }
        });
    }

    @Override // android.media.MediaPlayer2
    public String getDrmPropertyString(String str) throws MediaPlayer2.NoDrmSchemeException {
        String propertyString;
        Log.v(TAG, "getDrmPropertyString: propertyName: " + str);
        synchronized (this.mDrmLock) {
            if (!this.mActiveDrmScheme && !this.mDrmConfigAllowed) {
                Log.w(TAG, "getDrmPropertyString NoDrmSchemeException");
                throw new NoDrmSchemeExceptionImpl("getDrmPropertyString: Has to prepareDrm() first.");
            }
            try {
                propertyString = this.mDrmObj.getPropertyString(str);
            } catch (Exception e) {
                Log.w(TAG, "getDrmPropertyString Exception " + e);
                throw e;
            }
        }
        Log.v(TAG, "getDrmPropertyString: propertyName: " + str + " --> value: " + propertyString);
        return propertyString;
    }

    @Override // android.media.MediaPlayer2
    public void setDrmPropertyString(String str, String str2) throws MediaPlayer2.NoDrmSchemeException {
        Log.v(TAG, "setDrmPropertyString: propertyName: " + str + " value: " + str2);
        synchronized (this.mDrmLock) {
            if (!this.mActiveDrmScheme && !this.mDrmConfigAllowed) {
                Log.w(TAG, "setDrmPropertyString NoDrmSchemeException");
                throw new NoDrmSchemeExceptionImpl("setDrmPropertyString: Has to prepareDrm() first.");
            }
            try {
                this.mDrmObj.setPropertyString(str, str2);
            } catch (Exception e) {
                Log.w(TAG, "setDrmPropertyString Exception " + e);
                throw e;
            }
        }
    }

    public static final class DrmInfoImpl extends MediaPlayer2.DrmInfo {
        private Map<UUID, byte[]> mapPssh;
        private UUID[] supportedSchemes;

        @Override // android.media.MediaPlayer2.DrmInfo
        public Map<UUID, byte[]> getPssh() {
            return this.mapPssh;
        }

        @Override // android.media.MediaPlayer2.DrmInfo
        public List<UUID> getSupportedSchemes() {
            return Arrays.asList(this.supportedSchemes);
        }

        private DrmInfoImpl(Map<UUID, byte[]> map, UUID[] uuidArr) {
            this.mapPssh = map;
            this.supportedSchemes = uuidArr;
        }

        private DrmInfoImpl(Parcel parcel) {
            Log.v(MediaPlayer2Impl.TAG, "DrmInfoImpl(" + parcel + ") size " + parcel.dataSize());
            int i = parcel.readInt();
            byte[] bArr = new byte[i];
            parcel.readByteArray(bArr);
            Log.v(MediaPlayer2Impl.TAG, "DrmInfoImpl() PSSH: " + arrToHex(bArr));
            this.mapPssh = parsePSSH(bArr, i);
            Log.v(MediaPlayer2Impl.TAG, "DrmInfoImpl() PSSH: " + this.mapPssh);
            int i2 = parcel.readInt();
            this.supportedSchemes = new UUID[i2];
            for (int i3 = 0; i3 < i2; i3++) {
                byte[] bArr2 = new byte[16];
                parcel.readByteArray(bArr2);
                this.supportedSchemes[i3] = bytesToUUID(bArr2);
                Log.v(MediaPlayer2Impl.TAG, "DrmInfoImpl() supportedScheme[" + i3 + "]: " + this.supportedSchemes[i3]);
            }
            Log.v(MediaPlayer2Impl.TAG, "DrmInfoImpl() Parcel psshsize: " + i + " supportedDRMsCount: " + i2);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public DrmInfoImpl makeCopy() {
            return new DrmInfoImpl(this.mapPssh, this.supportedSchemes);
        }

        private String arrToHex(byte[] bArr) {
            String str = CarConfigManager.HEX_VALUE_DEFAULT;
            for (byte b : bArr) {
                str = str + String.format("%02x", Byte.valueOf(b));
            }
            return str;
        }

        private UUID bytesToUUID(byte[] bArr) {
            long j = 0;
            long j2 = 0;
            for (int i = 0; i < 8; i++) {
                int i2 = (7 - i) * 8;
                j |= (((long) bArr[i]) & 255) << i2;
                j2 |= (((long) bArr[i + 8]) & 255) << i2;
            }
            return new UUID(j, j2);
        }

        private Map<UUID, byte[]> parsePSSH(byte[] bArr, int i) {
            int i2;
            byte b;
            HashMap map = new HashMap();
            int i3 = i;
            int i4 = 0;
            int i5 = 0;
            while (i3 > 0) {
                if (i3 < 16) {
                    Log.w(MediaPlayer2Impl.TAG, String.format("parsePSSH: len is too short to parse UUID: (%d < 16) pssh: %d", Integer.valueOf(i3), Integer.valueOf(i)));
                    return null;
                }
                int i6 = i4 + 16;
                UUID uuidBytesToUUID = bytesToUUID(Arrays.copyOfRange(bArr, i4, i6));
                int i7 = i3 - 16;
                if (i7 < 4) {
                    Log.w(MediaPlayer2Impl.TAG, String.format("parsePSSH: len is too short to parse datalen: (%d < 4) pssh: %d", Integer.valueOf(i7), Integer.valueOf(i)));
                    return null;
                }
                int i8 = i6 + 4;
                byte[] bArrCopyOfRange = Arrays.copyOfRange(bArr, i6, i8);
                if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                    i2 = ((bArrCopyOfRange[2] & 255) << 16) | ((bArrCopyOfRange[3] & 255) << 24) | ((bArrCopyOfRange[1] & 255) << 8);
                    b = bArrCopyOfRange[0];
                } else {
                    i2 = ((bArrCopyOfRange[1] & 255) << 16) | ((bArrCopyOfRange[0] & 255) << 24) | ((bArrCopyOfRange[2] & 255) << 8);
                    b = bArrCopyOfRange[3];
                }
                int i9 = i2 | (b & 255);
                int i10 = i7 - 4;
                if (i10 < i9) {
                    Log.w(MediaPlayer2Impl.TAG, String.format("parsePSSH: len is too short to parse data: (%d < %d) pssh: %d", Integer.valueOf(i10), Integer.valueOf(i9), Integer.valueOf(i)));
                    return null;
                }
                int i11 = i8 + i9;
                byte[] bArrCopyOfRange2 = Arrays.copyOfRange(bArr, i8, i11);
                i3 = i10 - i9;
                Log.v(MediaPlayer2Impl.TAG, String.format("parsePSSH[%d]: <%s, %s> pssh: %d", Integer.valueOf(i5), uuidBytesToUUID, arrToHex(bArrCopyOfRange2), Integer.valueOf(i)));
                i5++;
                map.put(uuidBytesToUUID, bArrCopyOfRange2);
                i4 = i11;
            }
            return map;
        }
    }

    public static final class NoDrmSchemeExceptionImpl extends MediaPlayer2.NoDrmSchemeException {
        public NoDrmSchemeExceptionImpl(String str) {
            super(str);
        }
    }

    public static final class ProvisioningNetworkErrorExceptionImpl extends MediaPlayer2.ProvisioningNetworkErrorException {
        public ProvisioningNetworkErrorExceptionImpl(String str) {
            super(str);
        }
    }

    public static final class ProvisioningServerErrorExceptionImpl extends MediaPlayer2.ProvisioningServerErrorException {
        public ProvisioningServerErrorExceptionImpl(String str) {
            super(str);
        }
    }

    private void prepareDrm_createDrmStep(UUID uuid) throws Exception {
        Log.v(TAG, "prepareDrm_createDrmStep: UUID: " + uuid);
        try {
            this.mDrmObj = new MediaDrm(uuid);
            Log.v(TAG, "prepareDrm_createDrmStep: Created mDrmObj=" + this.mDrmObj);
        } catch (Exception e) {
            Log.e(TAG, "prepareDrm_createDrmStep: MediaDrm failed with " + e);
            throw e;
        }
    }

    private void prepareDrm_openSessionStep(UUID uuid) throws Exception {
        Log.v(TAG, "prepareDrm_openSessionStep: uuid: " + uuid);
        try {
            this.mDrmSessionId = this.mDrmObj.openSession();
            Log.v(TAG, "prepareDrm_openSessionStep: mDrmSessionId=" + this.mDrmSessionId);
            _prepareDrm(getByteArrayFromUUID(uuid), this.mDrmSessionId);
            Log.v(TAG, "prepareDrm_openSessionStep: _prepareDrm/Crypto succeeded");
        } catch (Exception e) {
            Log.e(TAG, "prepareDrm_openSessionStep: open/crypto failed with " + e);
            throw e;
        }
    }

    private static boolean setAudioOutputDeviceById(AudioTrack audioTrack, int i) {
        if (audioTrack == null) {
            return false;
        }
        if (i == 0) {
            audioTrack.setPreferredDevice(null);
            return true;
        }
        for (AudioDeviceInfo audioDeviceInfo : AudioManager.getDevicesStatic(2)) {
            if (audioDeviceInfo.getId() == i) {
                audioTrack.setPreferredDevice(audioDeviceInfo);
                return true;
            }
        }
        return false;
    }

    private static class StreamEventCallback extends AudioTrack.StreamEventCallback {
        public long mJAudioTrackPtr;
        public long mNativeCallbackPtr;
        public long mUserDataPtr;

        public StreamEventCallback(long j, long j2, long j3) {
            this.mJAudioTrackPtr = j;
            this.mNativeCallbackPtr = j2;
            this.mUserDataPtr = j3;
        }

        @Override // android.media.AudioTrack.StreamEventCallback
        public void onTearDown(AudioTrack audioTrack) {
            MediaPlayer2Impl.native_stream_event_onTearDown(this.mNativeCallbackPtr, this.mUserDataPtr);
        }

        @Override // android.media.AudioTrack.StreamEventCallback
        public void onStreamPresentationEnd(AudioTrack audioTrack) {
            MediaPlayer2Impl.native_stream_event_onStreamPresentationEnd(this.mNativeCallbackPtr, this.mUserDataPtr);
        }

        @Override // android.media.AudioTrack.StreamEventCallback
        public void onStreamDataRequest(AudioTrack audioTrack) {
            MediaPlayer2Impl.native_stream_event_onStreamDataRequest(this.mJAudioTrackPtr, this.mNativeCallbackPtr, this.mUserDataPtr);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    class ProvisioningThread extends Thread {
        public static final int TIMEOUT_MS = 60000;
        private Object drmLock;
        private boolean finished;
        private MediaPlayer2Impl mediaPlayer;
        private int status;
        private String urlStr;
        private UUID uuid;

        private ProvisioningThread() {
        }

        public int status() {
            return this.status;
        }

        public ProvisioningThread initialize(MediaDrm.ProvisionRequest provisionRequest, UUID uuid, MediaPlayer2Impl mediaPlayer2Impl) {
            this.drmLock = mediaPlayer2Impl.mDrmLock;
            this.mediaPlayer = mediaPlayer2Impl;
            this.urlStr = provisionRequest.getDefaultUrl() + "&signedRequest=" + new String(provisionRequest.getData());
            this.uuid = uuid;
            this.status = 3;
            Log.v(MediaPlayer2Impl.TAG, "HandleProvisioninig: Thread is initialised url: " + this.urlStr);
            return this;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            boolean z;
            boolean z2;
            boolean zResumePrepareDrm;
            boolean zResumePrepareDrm2;
            byte[] fully = null;
            try {
                URL url = new URL(this.urlStr);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                try {
                    try {
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setDoOutput(false);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.setConnectTimeout(60000);
                        httpURLConnection.setReadTimeout(60000);
                        httpURLConnection.connect();
                        fully = Streams.readFully(httpURLConnection.getInputStream());
                        Log.v(MediaPlayer2Impl.TAG, "HandleProvisioninig: Thread run: response " + fully.length + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + fully);
                    } catch (Throwable th) {
                        httpURLConnection.disconnect();
                        throw th;
                    }
                } catch (Exception e) {
                    this.status = 1;
                    Log.w(MediaPlayer2Impl.TAG, "HandleProvisioninig: Thread run: connect " + e + " url: " + url);
                }
                httpURLConnection.disconnect();
            } catch (Exception e2) {
                this.status = 1;
                Log.w(MediaPlayer2Impl.TAG, "HandleProvisioninig: Thread run: openConnection " + e2);
            }
            if (fully != null) {
                try {
                    MediaPlayer2Impl.this.mDrmObj.provideProvisionResponse(fully);
                    Log.v(MediaPlayer2Impl.TAG, "HandleProvisioninig: Thread run: provideProvisionResponse SUCCEEDED!");
                    z = true;
                } catch (Exception e3) {
                    this.status = 2;
                    Log.w(MediaPlayer2Impl.TAG, "HandleProvisioninig: Thread run: provideProvisionResponse " + e3);
                    z = false;
                }
            } else {
                z = false;
            }
            synchronized (MediaPlayer2Impl.this.mDrmEventCbLock) {
                z2 = !MediaPlayer2Impl.this.mDrmEventCallbackRecords.isEmpty();
            }
            if (!z2) {
                if (z) {
                    zResumePrepareDrm = this.mediaPlayer.resumePrepareDrm(this.uuid);
                    this.status = zResumePrepareDrm ? 0 : 3;
                } else {
                    zResumePrepareDrm = false;
                }
                this.mediaPlayer.mDrmProvisioningInProgress = false;
                this.mediaPlayer.mPrepareDrmInProgress = false;
                if (!zResumePrepareDrm) {
                    MediaPlayer2Impl.this.cleanDrmObj();
                }
            } else {
                synchronized (this.drmLock) {
                    if (z) {
                        zResumePrepareDrm2 = this.mediaPlayer.resumePrepareDrm(this.uuid);
                        this.status = zResumePrepareDrm2 ? 0 : 3;
                    } else {
                        zResumePrepareDrm2 = false;
                    }
                    this.mediaPlayer.mDrmProvisioningInProgress = false;
                    this.mediaPlayer.mPrepareDrmInProgress = false;
                    if (!zResumePrepareDrm2) {
                        MediaPlayer2Impl.this.cleanDrmObj();
                    }
                }
                synchronized (MediaPlayer2Impl.this.mDrmEventCbLock) {
                    for (final Pair pair : MediaPlayer2Impl.this.mDrmEventCallbackRecords) {
                        ((Executor) pair.first).execute(new Runnable() { // from class: android.media.-$$Lambda$MediaPlayer2Impl$ProvisioningThread$ghq9Dd9r2O6PXBn2hv4fhVAxaTQ
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.lambda$run$0$MediaPlayer2Impl$ProvisioningThread(pair);
                            }
                        });
                    }
                }
            }
            this.finished = true;
        }

        /* JADX WARN: Multi-variable type inference failed */
        public /* synthetic */ void lambda$run$0$MediaPlayer2Impl$ProvisioningThread(Pair pair) {
            ((MediaPlayer2.DrmEventCallback) pair.second).onDrmPrepared(this.mediaPlayer, MediaPlayer2Impl.this.mCurrentDSD, this.status);
        }
    }

    private int HandleProvisioninig(UUID uuid) {
        if (this.mDrmProvisioningInProgress) {
            Log.e(TAG, "HandleProvisioninig: Unexpected mDrmProvisioningInProgress");
            return 3;
        }
        MediaDrm.ProvisionRequest provisionRequest = this.mDrmObj.getProvisionRequest();
        if (provisionRequest == null) {
            Log.e(TAG, "HandleProvisioninig: getProvisionRequest returned null.");
            return 3;
        }
        Log.v(TAG, "HandleProvisioninig provReq  data: " + provisionRequest.getData() + " url: " + provisionRequest.getDefaultUrl());
        boolean z = true;
        this.mDrmProvisioningInProgress = true;
        ProvisioningThread provisioningThreadInitialize = new ProvisioningThread().initialize(provisionRequest, uuid, this);
        this.mDrmProvisioningThread = provisioningThreadInitialize;
        provisioningThreadInitialize.start();
        synchronized (this.mDrmEventCbLock) {
            if (this.mDrmEventCallbackRecords.isEmpty()) {
                z = false;
            }
        }
        if (z) {
            return 0;
        }
        try {
            this.mDrmProvisioningThread.join();
        } catch (Exception e) {
            Log.w(TAG, "HandleProvisioninig: Thread.join Exception " + e);
        }
        int iStatus = this.mDrmProvisioningThread.status();
        this.mDrmProvisioningThread = null;
        return iStatus;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean resumePrepareDrm(UUID uuid) {
        Log.v(TAG, "resumePrepareDrm: uuid: " + uuid);
        try {
            prepareDrm_openSessionStep(uuid);
            this.mDrmUUID = uuid;
            this.mActiveDrmScheme = true;
            return true;
        } catch (Exception e) {
            Log.w(TAG, "HandleProvisioninig: Thread run _prepareDrm resume failed with " + e);
            return false;
        }
    }

    private void resetDrmState() {
        synchronized (this.mDrmLock) {
            Log.v(TAG, "resetDrmState:  mDrmInfoImpl=" + this.mDrmInfoImpl + " mDrmProvisioningThread=" + this.mDrmProvisioningThread + " mPrepareDrmInProgress=" + this.mPrepareDrmInProgress + " mActiveDrmScheme=" + this.mActiveDrmScheme);
            this.mDrmInfoResolved = false;
            this.mDrmInfoImpl = null;
            if (this.mDrmProvisioningThread != null) {
                try {
                    this.mDrmProvisioningThread.join();
                } catch (InterruptedException e) {
                    Log.w(TAG, "resetDrmState: ProvThread.join Exception " + e);
                }
                this.mDrmProvisioningThread = null;
                this.mPrepareDrmInProgress = false;
                this.mActiveDrmScheme = false;
                cleanDrmObj();
            } else {
                this.mPrepareDrmInProgress = false;
                this.mActiveDrmScheme = false;
                cleanDrmObj();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cleanDrmObj() {
        Log.v(TAG, "cleanDrmObj: mDrmObj=" + this.mDrmObj + " mDrmSessionId=" + this.mDrmSessionId);
        byte[] bArr = this.mDrmSessionId;
        if (bArr != null) {
            this.mDrmObj.closeSession(bArr);
            this.mDrmSessionId = null;
        }
        MediaDrm mediaDrm = this.mDrmObj;
        if (mediaDrm != null) {
            mediaDrm.release();
            this.mDrmObj = null;
        }
    }

    private static final byte[] getByteArrayFromUUID(UUID uuid) {
        long mostSignificantBits = uuid.getMostSignificantBits();
        long leastSignificantBits = uuid.getLeastSignificantBits();
        byte[] bArr = new byte[16];
        for (int i = 0; i < 8; i++) {
            int i2 = (7 - i) * 8;
            bArr[i] = (byte) (mostSignificantBits >>> i2);
            bArr[i + 8] = (byte) (leastSignificantBits >>> i2);
        }
        return bArr;
    }

    static class TimeProvider implements MediaTimeProvider {
        private static final long MAX_EARLY_CALLBACK_US = 1000;
        private static final long MAX_NS_WITHOUT_POSITION_CHECK = 5000000000L;
        private static final int NOTIFY = 1;
        private static final int NOTIFY_SEEK = 3;
        private static final int NOTIFY_STOP = 2;
        private static final int NOTIFY_TIME = 0;
        private static final int NOTIFY_TRACK_DATA = 4;
        private static final String TAG = "MTP";
        private static final long TIME_ADJUSTMENT_RATE = 2;
        private boolean mBuffering;
        private EventHandler mEventHandler;
        private HandlerThread mHandlerThread;
        private long mLastReportedTime;
        private long mLastTimeUs;
        private MediaTimeProvider.OnMediaTimeListener[] mListeners;
        private MediaPlayer2Impl mPlayer;
        private boolean mRefresh;
        private long[] mTimes;
        private boolean mPaused = true;
        private boolean mStopped = true;
        private boolean mPausing = false;
        private boolean mSeeking = false;
        public boolean DEBUG = false;

        public TimeProvider(MediaPlayer2Impl mediaPlayer2Impl) {
            this.mLastTimeUs = 0L;
            this.mRefresh = false;
            this.mPlayer = mediaPlayer2Impl;
            try {
                getCurrentTimeUs(true, false);
            } catch (IllegalStateException unused) {
                this.mRefresh = true;
            }
            Looper looperMyLooper = Looper.myLooper();
            if (looperMyLooper == null && (looperMyLooper = Looper.getMainLooper()) == null) {
                HandlerThread handlerThread = new HandlerThread("MediaPlayer2MTPEventThread", -2);
                this.mHandlerThread = handlerThread;
                handlerThread.start();
                looperMyLooper = this.mHandlerThread.getLooper();
            }
            this.mEventHandler = new EventHandler(looperMyLooper);
            this.mListeners = new MediaTimeProvider.OnMediaTimeListener[0];
            this.mTimes = new long[0];
            this.mLastTimeUs = 0L;
        }

        private void scheduleNotification(int i, long j) {
            if (this.mSeeking && i == 0) {
                return;
            }
            if (this.DEBUG) {
                Log.v(TAG, "scheduleNotification " + i + " in " + j);
            }
            this.mEventHandler.removeMessages(1);
            this.mEventHandler.sendMessageDelayed(this.mEventHandler.obtainMessage(1, i, 0), (int) (j / 1000));
        }

        public void close() {
            this.mEventHandler.removeMessages(1);
            HandlerThread handlerThread = this.mHandlerThread;
            if (handlerThread != null) {
                handlerThread.quitSafely();
                this.mHandlerThread = null;
            }
        }

        protected void finalize() {
            HandlerThread handlerThread = this.mHandlerThread;
            if (handlerThread != null) {
                handlerThread.quitSafely();
            }
        }

        public void onNotifyTime() {
            synchronized (this) {
                if (this.DEBUG) {
                    Log.d(TAG, "onNotifyTime: ");
                }
                scheduleNotification(0, 0L);
            }
        }

        public void onPaused(boolean z) {
            synchronized (this) {
                if (this.DEBUG) {
                    Log.d(TAG, "onPaused: " + z);
                }
                if (this.mStopped) {
                    this.mStopped = false;
                    this.mSeeking = true;
                    scheduleNotification(3, 0L);
                } else {
                    this.mPausing = z;
                    this.mSeeking = false;
                    scheduleNotification(0, 0L);
                }
            }
        }

        public void onBuffering(boolean z) {
            synchronized (this) {
                if (this.DEBUG) {
                    Log.d(TAG, "onBuffering: " + z);
                }
                this.mBuffering = z;
                scheduleNotification(0, 0L);
            }
        }

        public void onStopped() {
            synchronized (this) {
                if (this.DEBUG) {
                    Log.d(TAG, "onStopped");
                }
                this.mPaused = true;
                this.mStopped = true;
                this.mSeeking = false;
                this.mBuffering = false;
                scheduleNotification(2, 0L);
            }
        }

        public void onSeekComplete(MediaPlayer2Impl mediaPlayer2Impl) {
            synchronized (this) {
                this.mStopped = false;
                this.mSeeking = true;
                scheduleNotification(3, 0L);
            }
        }

        public void onNewPlayer() {
            if (this.mRefresh) {
                synchronized (this) {
                    this.mStopped = false;
                    this.mSeeking = true;
                    this.mBuffering = false;
                    scheduleNotification(3, 0L);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized void notifySeek() {
            try {
                this.mSeeking = false;
                try {
                    long currentTimeUs = getCurrentTimeUs(true, false);
                    if (this.DEBUG) {
                        Log.d(TAG, "onSeekComplete at " + currentTimeUs);
                    }
                    for (MediaTimeProvider.OnMediaTimeListener onMediaTimeListener : this.mListeners) {
                        if (onMediaTimeListener == null) {
                            break;
                        }
                        onMediaTimeListener.onSeek(currentTimeUs);
                    }
                } catch (IllegalStateException unused) {
                    if (this.DEBUG) {
                        Log.d(TAG, "onSeekComplete but no player");
                    }
                    this.mPausing = true;
                    notifyTimedEvent(false);
                }
            } catch (Throwable th) {
                throw th;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized void notifyTrackData(Pair<SubtitleTrack, byte[]> pair) {
            pair.first.onData(pair.second, true, -1L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized void notifyStop() {
            for (MediaTimeProvider.OnMediaTimeListener onMediaTimeListener : this.mListeners) {
                if (onMediaTimeListener == null) {
                    break;
                }
                onMediaTimeListener.onStop();
            }
        }

        private int registerListener(MediaTimeProvider.OnMediaTimeListener onMediaTimeListener) {
            int i = 0;
            while (true) {
                MediaTimeProvider.OnMediaTimeListener[] onMediaTimeListenerArr = this.mListeners;
                if (i >= onMediaTimeListenerArr.length || onMediaTimeListenerArr[i] == onMediaTimeListener || onMediaTimeListenerArr[i] == null) {
                    break;
                }
                i++;
            }
            MediaTimeProvider.OnMediaTimeListener[] onMediaTimeListenerArr2 = this.mListeners;
            if (i >= onMediaTimeListenerArr2.length) {
                int i2 = i + 1;
                MediaTimeProvider.OnMediaTimeListener[] onMediaTimeListenerArr3 = new MediaTimeProvider.OnMediaTimeListener[i2];
                long[] jArr = new long[i2];
                System.arraycopy(onMediaTimeListenerArr2, 0, onMediaTimeListenerArr3, 0, onMediaTimeListenerArr2.length);
                long[] jArr2 = this.mTimes;
                System.arraycopy(jArr2, 0, jArr, 0, jArr2.length);
                this.mListeners = onMediaTimeListenerArr3;
                this.mTimes = jArr;
            }
            MediaTimeProvider.OnMediaTimeListener[] onMediaTimeListenerArr4 = this.mListeners;
            if (onMediaTimeListenerArr4[i] == null) {
                onMediaTimeListenerArr4[i] = onMediaTimeListener;
                this.mTimes[i] = -1;
            }
            return i;
        }

        @Override // android.media.MediaTimeProvider
        public void notifyAt(long j, MediaTimeProvider.OnMediaTimeListener onMediaTimeListener) {
            synchronized (this) {
                if (this.DEBUG) {
                    Log.d(TAG, "notifyAt " + j);
                }
                this.mTimes[registerListener(onMediaTimeListener)] = j;
                scheduleNotification(0, 0L);
            }
        }

        @Override // android.media.MediaTimeProvider
        public void scheduleUpdate(MediaTimeProvider.OnMediaTimeListener onMediaTimeListener) {
            synchronized (this) {
                if (this.DEBUG) {
                    Log.d(TAG, "scheduleUpdate");
                }
                int iRegisterListener = registerListener(onMediaTimeListener);
                if (!this.mStopped) {
                    this.mTimes[iRegisterListener] = 0;
                    scheduleNotification(0, 0L);
                }
            }
        }

        @Override // android.media.MediaTimeProvider
        public void cancelNotifications(MediaTimeProvider.OnMediaTimeListener onMediaTimeListener) {
            synchronized (this) {
                for (int i = 0; i < this.mListeners.length; i++) {
                    if (this.mListeners[i] == onMediaTimeListener) {
                        int i2 = i + 1;
                        System.arraycopy(this.mListeners, i2, this.mListeners, i, (this.mListeners.length - i) - 1);
                        System.arraycopy(this.mTimes, i2, this.mTimes, i, (this.mTimes.length - i) - 1);
                        this.mListeners[this.mListeners.length - 1] = null;
                        this.mTimes[this.mTimes.length - 1] = -1;
                        break;
                    }
                    if (this.mListeners[i] == null) {
                        break;
                    }
                }
                scheduleNotification(0, 0L);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized void notifyTimedEvent(boolean z) {
            long currentTimeUs;
            try {
                currentTimeUs = getCurrentTimeUs(z, true);
            } catch (IllegalStateException unused) {
                this.mRefresh = true;
                this.mPausing = true;
                currentTimeUs = getCurrentTimeUs(z, true);
            }
            if (this.mSeeking) {
                return;
            }
            if (this.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("notifyTimedEvent(");
                sb.append(this.mLastTimeUs);
                sb.append(" -> ");
                sb.append(currentTimeUs);
                sb.append(") from {");
                boolean z2 = true;
                for (long j : this.mTimes) {
                    if (j != -1) {
                        if (!z2) {
                            sb.append(", ");
                        }
                        sb.append(j);
                        z2 = false;
                    }
                }
                sb.append("}");
                Log.d(TAG, sb.toString());
            }
            Vector vector = new Vector();
            long j2 = currentTimeUs;
            for (int i = 0; i < this.mTimes.length && this.mListeners[i] != null; i++) {
                if (this.mTimes[i] > -1) {
                    if (this.mTimes[i] <= 1000 + currentTimeUs) {
                        vector.add(this.mListeners[i]);
                        if (this.DEBUG) {
                            Log.d(TAG, Environment.MEDIA_REMOVED);
                        }
                        this.mTimes[i] = -1;
                    } else if (j2 == currentTimeUs || this.mTimes[i] < j2) {
                        j2 = this.mTimes[i];
                    }
                }
            }
            if (j2 > currentTimeUs && !this.mPaused) {
                if (this.DEBUG) {
                    Log.d(TAG, "scheduling for " + j2 + " and " + currentTimeUs);
                }
                this.mPlayer.notifyAt(j2);
            } else {
                this.mEventHandler.removeMessages(1);
            }
            Iterator it = vector.iterator();
            while (it.hasNext()) {
                ((MediaTimeProvider.OnMediaTimeListener) it.next()).onTimedEvent(currentTimeUs);
            }
        }

        @Override // android.media.MediaTimeProvider
        public long getCurrentTimeUs(boolean z, boolean z2) throws IllegalStateException {
            synchronized (this) {
                if (this.mPaused && !z) {
                    return this.mLastReportedTime;
                }
                try {
                    this.mLastTimeUs = this.mPlayer.getCurrentPosition() * 1000;
                    this.mPaused = !this.mPlayer.isPlaying() || this.mBuffering;
                    if (this.DEBUG) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(this.mPaused ? "paused" : "playing");
                        sb.append(" at ");
                        sb.append(this.mLastTimeUs);
                        Log.v(TAG, sb.toString());
                    }
                    if (!z2 || this.mLastTimeUs >= this.mLastReportedTime) {
                        this.mLastReportedTime = this.mLastTimeUs;
                    } else if (this.mLastReportedTime - this.mLastTimeUs > TimeUtils.NANOS_PER_MS) {
                        this.mStopped = false;
                        this.mSeeking = true;
                        scheduleNotification(3, 0L);
                    }
                    return this.mLastReportedTime;
                } catch (IllegalStateException e) {
                    if (this.mPausing) {
                        this.mPausing = false;
                        if (!z2 || this.mLastReportedTime < this.mLastTimeUs) {
                            this.mLastReportedTime = this.mLastTimeUs;
                        }
                        this.mPaused = true;
                        if (this.DEBUG) {
                            Log.d(TAG, "illegal state, but pausing: estimating at " + this.mLastReportedTime);
                        }
                        return this.mLastReportedTime;
                    }
                    throw e;
                }
            }
        }

        private class EventHandler extends Handler {
            public EventHandler(Looper looper) {
                super(looper);
            }

            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what == 1) {
                    int i = message.arg1;
                    if (i == 0) {
                        TimeProvider.this.notifyTimedEvent(true);
                        return;
                    }
                    if (i == 2) {
                        TimeProvider.this.notifyStop();
                    } else if (i == 3) {
                        TimeProvider.this.notifySeek();
                    } else {
                        if (i != 4) {
                            return;
                        }
                        TimeProvider.this.notifyTrackData((Pair) message.obj);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    abstract class Task implements Runnable {
        private DataSourceDesc mDSD;
        private final int mMediaCallType;
        private final boolean mNeedToWaitForEventToComplete;

        abstract void process() throws IOException, MediaPlayer2.NoDrmSchemeException;

        public Task(int i, boolean z) {
            this.mMediaCallType = i;
            this.mNeedToWaitForEventToComplete = z;
        }

        @Override // java.lang.Runnable
        public void run() {
            int i;
            try {
                process();
                i = 0;
            } catch (MediaPlayer2.NoDrmSchemeException unused) {
                i = 5;
            } catch (IOException unused2) {
                i = 4;
            } catch (IllegalArgumentException unused3) {
                i = 2;
            } catch (IllegalStateException unused4) {
                i = 1;
            } catch (SecurityException unused5) {
                i = 3;
            } catch (Exception unused6) {
                i = Integer.MIN_VALUE;
            }
            synchronized (MediaPlayer2Impl.this.mSrcLock) {
                this.mDSD = MediaPlayer2Impl.this.mCurrentDSD;
            }
            if (this.mNeedToWaitForEventToComplete && i == 0) {
                return;
            }
            sendCompleteNotification(i);
            synchronized (MediaPlayer2Impl.this.mTaskLock) {
                MediaPlayer2Impl.this.mCurrentTask = null;
                MediaPlayer2Impl.this.processPendingTask_l();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void sendCompleteNotification(final int i) {
            if (this.mMediaCallType == 1003) {
                return;
            }
            synchronized (MediaPlayer2Impl.this.mEventCbLock) {
                for (final Pair pair : MediaPlayer2Impl.this.mEventCallbackRecords) {
                    ((Executor) pair.first).execute(new Runnable() { // from class: android.media.-$$Lambda$MediaPlayer2Impl$Task$FRvdJ9PUPHSq0Jucj91aL6zYEJY
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$sendCompleteNotification$0$MediaPlayer2Impl$Task(pair, i);
                        }
                    });
                }
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        public /* synthetic */ void lambda$sendCompleteNotification$0$MediaPlayer2Impl$Task(Pair pair, int i) {
            ((MediaPlayer2.MediaPlayer2EventCallback) pair.second).onCallCompleted(MediaPlayer2Impl.this, this.mDSD, this.mMediaCallType, i);
        }
    }
}
