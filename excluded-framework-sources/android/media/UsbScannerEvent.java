package android.media;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
public class UsbScannerEvent {
    public static final int EVENT_MEDIA_DEVICE_STATE_MOUNTED = 1;
    public static final int EVENT_MEDIA_DEVICE_STATE_MOUNT_ERROR = 3;
    public static final int EVENT_MEDIA_DEVICE_STATE_SYNC_COMPLETED = 35;
    public static final int EVENT_MEDIA_DEVICE_STATE_SYNC_INCOMPLETE = 37;
    public static final int EVENT_MEDIA_DEVICE_STATE_SYNC_IN_PROGRESS = 34;
    public static final int EVENT_MEDIA_DEVICE_STATE_SYNC_STARTED = 33;
    public static final int EVENT_MEDIA_DEVICE_STATE_SYNC_TIMEOUT = 36;
    public static final int EVENT_MEDIA_DEVICE_STATE_SYNC_UNKNOWN = 32;
    public static final int EVENT_MEDIA_DEVICE_STATE_UNKNOWN = 0;
    public static final int EVENT_MEDIA_DEVICE_STATE_UNMOUNTED = 2;
    public static final int INSTANCE_FOUR = 4;
    public static final int INSTANCE_ONE = 1;
    public static final int INSTANCE_THREE = 3;
    public static final int INSTANCE_TWO = 2;
    public static final int SCAN_ALL = 7;
    public static final int SCAN_AUDIO = 1;
    public static final int SCAN_AUDIO_IMAGE = 5;
    public static final int SCAN_AUDIO_VIDEO = 3;
    public static final int SCAN_FILES_COUNT = 100;
    public static final int SCAN_IMAGE = 4;
    public static final int SCAN_UNKNOWN = 0;
    public static final int SCAN_VIDEO = 2;
    public static final int SCAN_VIDEO_IMAGE = 6;
    public static final int TYPE_MTP = 2;
    public static final int TYPE_RESERVED = 255;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_USB = 1;

    @Retention(RetentionPolicy.SOURCE)
    public @interface MediaEvent {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ScanType {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface SourceInstance {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface SourceType {
    }
}
