package android.media;

import android.annotation.SystemApi;
import android.net.INetd;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.proto.ProtoOutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public final class AudioAttributes implements Parcelable {
    private static final int ALL_PARCEL_FLAGS = 1;
    private static final int ATTR_PARCEL_IS_NULL_BUNDLE = -1977;
    private static final int ATTR_PARCEL_IS_VALID_BUNDLE = 1980;
    public static final int AUDIOFOCUS_DELAYED = 1;
    public static final int AUDIOFOCUS_GRANTED = 0;
    public static final int AUDIOFOCUS_MIX = 3;
    public static final int AUDIOFOCUS_REJECT = 2;
    public static final int CAR_SOURCE_TYPE_AA_MEDIA = 70;
    public static final int CAR_SOURCE_TYPE_AA_NAVI = 71;
    public static final int CAR_SOURCE_TYPE_AA_PHONE = 73;
    public static final int CAR_SOURCE_TYPE_AA_RINGTONE = 74;
    public static final int CAR_SOURCE_TYPE_AA_VR = 72;
    public static final int CAR_SOURCE_TYPE_ALARM = 37;
    public static final int CAR_SOURCE_TYPE_AUX_0 = 10;
    public static final int CAR_SOURCE_TYPE_AUX_1 = 11;
    public static final int CAR_SOURCE_TYPE_BEEP = 35;
    public static final int CAR_SOURCE_TYPE_BT_AUDIO = 5;
    public static final int CAR_SOURCE_TYPE_BT_PHONE = 40;
    public static final int CAR_SOURCE_TYPE_B_CALL = 43;
    public static final int CAR_SOURCE_TYPE_CL_ASR = 63;
    public static final int CAR_SOURCE_TYPE_CL_MEDIA = 60;
    public static final int CAR_SOURCE_TYPE_CL_NAVI = 61;
    public static final int CAR_SOURCE_TYPE_CL_PROMPT = 62;
    public static final int CAR_SOURCE_TYPE_CP_ALERT = 56;
    public static final int CAR_SOURCE_TYPE_CP_MEDIA = 50;
    public static final int CAR_SOURCE_TYPE_CP_NAVI = 52;
    public static final int CAR_SOURCE_TYPE_CP_PHONE = 53;
    public static final int CAR_SOURCE_TYPE_CP_RING = 54;
    public static final int CAR_SOURCE_TYPE_CP_SIRI = 51;
    public static final int CAR_SOURCE_TYPE_CP_SPOKEN_AUDIO = 55;
    public static final int CAR_SOURCE_TYPE_DAB = 3;
    public static final int CAR_SOURCE_TYPE_DISC = 4;
    public static final int CAR_SOURCE_TYPE_DLNA_MUSIC = 15;
    public static final int CAR_SOURCE_TYPE_EA = 81;
    public static final int CAR_SOURCE_TYPE_EA_PHONE = 83;
    public static final int CAR_SOURCE_TYPE_EA_PROMPT = 82;
    public static final int CAR_SOURCE_TYPE_ENGINEERING_MODE = 90;
    public static final int CAR_SOURCE_TYPE_E_CALL = 42;
    public static final int CAR_SOURCE_TYPE_INSTANT_MESSAGE = 45;
    public static final int CAR_SOURCE_TYPE_IPOD_0 = 8;
    public static final int CAR_SOURCE_TYPE_IPOD_1 = 9;
    public static final int CAR_SOURCE_TYPE_I_CALL = 44;
    public static final int CAR_SOURCE_TYPE_LOCAL_VEDIO = 18;
    public static final int CAR_SOURCE_TYPE_NAVI = 33;
    public static final int CAR_SOURCE_TYPE_NAVI_PROMPT = 34;
    public static final int CAR_SOURCE_TYPE_NONE = 0;
    public static final int CAR_SOURCE_TYPE_ONLINE_MUSIC = 14;
    public static final int CAR_SOURCE_TYPE_ONLINE_VEDIO = 17;
    public static final int CAR_SOURCE_TYPE_PROMPT = 30;
    public static final int CAR_SOURCE_TYPE_RADIO_AM = 2;
    public static final int CAR_SOURCE_TYPE_RADIO_FM = 1;
    public static final int CAR_SOURCE_TYPE_RVC = 80;
    public static final int CAR_SOURCE_TYPE_SD_0 = 12;
    public static final int CAR_SOURCE_TYPE_SD_1 = 13;
    public static final int CAR_SOURCE_TYPE_SMS = 36;
    public static final int CAR_SOURCE_TYPE_SYSTEM = 32;
    public static final int CAR_SOURCE_TYPE_TA = 19;
    public static final int CAR_SOURCE_TYPE_TBOX_PHONE = 41;
    public static final int CAR_SOURCE_TYPE_TERM = 38;
    public static final int CAR_SOURCE_TYPE_USB_0 = 6;
    public static final int CAR_SOURCE_TYPE_USB_1 = 7;
    public static final int CAR_SOURCE_TYPE_VEDIO = 16;
    public static final int CAR_SOURCE_TYPE_VR = 31;
    public static final int CONTENT_TYPE_MOVIE = 3;
    public static final int CONTENT_TYPE_MUSIC = 2;
    public static final int CONTENT_TYPE_SONIFICATION = 4;
    public static final int CONTENT_TYPE_SPEECH = 1;
    public static final int CONTENT_TYPE_UNKNOWN = 0;
    public static final Parcelable.Creator<AudioAttributes> CREATOR;
    public static final int DSP_SOURCE_AUX = 3;
    public static final int DSP_SOURCE_BT_AUDIO = 8;
    public static final int DSP_SOURCE_BT_PHONE = 9;
    public static final int DSP_SOURCE_BT_RINGTONE = 10;
    public static final int DSP_SOURCE_CD = 7;
    public static final int DSP_SOURCE_IIS0 = 4;
    public static final int DSP_SOURCE_IPOD = 6;
    public static final int DSP_SOURCE_MAX = 16;
    public static final int DSP_SOURCE_MIC = 14;
    public static final int DSP_SOURCE_MLINK = 12;
    public static final int DSP_SOURCE_NAV = 5;
    public static final int DSP_SOURCE_NONE = 0;
    public static final int DSP_SOURCE_RINGTONE = 11;
    public static final int DSP_SOURCE_SINE = 15;
    public static final int DSP_SOURCE_TUNER_AM = 2;
    public static final int DSP_SOURCE_TUNER_FM = 1;
    public static final int DSP_SOURCE_VR = 13;
    private static final int FLAG_ALL = 1023;
    private static final int FLAG_ALL_PUBLIC = 273;
    public static final int FLAG_AUDIBILITY_ENFORCED = 1;

    @SystemApi
    public static final int FLAG_BEACON = 8;

    @SystemApi
    public static final int FLAG_BYPASS_INTERRUPTION_POLICY = 64;

    @SystemApi
    public static final int FLAG_BYPASS_MUTE = 128;
    public static final int FLAG_DEEP_BUFFER = 512;
    public static final int FLAG_HW_AV_SYNC = 16;

    @SystemApi
    public static final int FLAG_HW_HOTWORD = 32;
    public static final int FLAG_LOW_LATENCY = 256;
    public static final int FLAG_NONE = 0;
    public static final int FLAG_PRIMARY_CHANNEL_UNMUTE = 1;
    public static final int FLAG_SCO = 4;
    public static final int FLAG_SECURE = 2;
    public static final int FLATTEN_TAGS = 1;
    public static final String KEY_CAR_SOURCE_TYPE = "key_car_source_type";
    public static final String KEY_DSP_SOURCE_TYPE = "key_dsp_source_type";
    public static final String KEY_PACKAGE_NAME = "key_package_name";
    public static final int[] SDK_USAGES;
    public static final int SOC_BUS_MEDIA = 0;
    public static final int SOC_BUS_MIX = 1;
    public static final int SOC_BUS_PHONE = 3;
    public static final int SOC_BUS_RINGTONE = 5;
    public static final int SOC_BUS_SYSTEM = 2;
    public static final int SOC_BUS_TTS = 4;
    public static final int SUPPRESSIBLE_ALARM = 4;
    public static final int SUPPRESSIBLE_CALL = 2;
    public static final int SUPPRESSIBLE_MEDIA = 5;
    public static final int SUPPRESSIBLE_NEVER = 3;
    public static final int SUPPRESSIBLE_NOTIFICATION = 1;
    public static final int SUPPRESSIBLE_SYSTEM = 6;
    public static final SparseIntArray SUPPRESSIBLE_USAGES;
    private static final String TAG = "AudioAttributes";
    public static final int USAGE_ALARM = 4;
    public static final int USAGE_ASSISTANCE_ACCESSIBILITY = 11;
    public static final int USAGE_ASSISTANCE_NAVIGATION_GUIDANCE = 12;
    public static final int USAGE_ASSISTANCE_SONIFICATION = 13;
    public static final int USAGE_ASSISTANT = 16;
    public static final int USAGE_GAME = 14;
    public static final int USAGE_MEDIA = 1;
    public static final int USAGE_NOTIFICATION = 5;
    public static final int USAGE_NOTIFICATION_COMMUNICATION_DELAYED = 9;
    public static final int USAGE_NOTIFICATION_COMMUNICATION_INSTANT = 8;
    public static final int USAGE_NOTIFICATION_COMMUNICATION_REQUEST = 7;
    public static final int USAGE_NOTIFICATION_EVENT = 10;
    public static final int USAGE_NOTIFICATION_RINGTONE = 6;
    public static final int USAGE_POWER = 999;
    public static final int USAGE_REVERSE = 998;
    public static final int USAGE_UNKNOWN = 0;
    public static final int USAGE_VIRTUAL_SOURCE = 15;
    public static final int USAGE_VOICE_COMMUNICATION = 2;
    public static final int USAGE_VOICE_COMMUNICATION_SIGNALLING = 3;

    @SystemApi
    public boolean isGoogleVRFocus;
    private AudioBehavior mBehavior;
    private Bundle mBundle;
    private int mContentType;
    private int mFlags;
    private String mFormattedTags;
    private int mSource;
    private HashSet<String> mTags;
    private int mUsage;

    @Retention(RetentionPolicy.SOURCE)
    public @interface AttributeContentType {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface AttributeUsage {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int usageForStreamType(int i) {
        switch (i) {
            case 0:
                return 2;
            case 1:
            case 7:
                return 13;
            case 2:
                return 6;
            case 3:
                return 1;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 2;
            case 8:
                return 3;
            case 9:
            default:
                return 0;
            case 10:
                return 11;
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    static {
        SparseIntArray sparseIntArray = new SparseIntArray();
        SUPPRESSIBLE_USAGES = sparseIntArray;
        sparseIntArray.put(5, 1);
        SUPPRESSIBLE_USAGES.put(6, 2);
        SUPPRESSIBLE_USAGES.put(7, 2);
        SUPPRESSIBLE_USAGES.put(8, 1);
        SUPPRESSIBLE_USAGES.put(9, 1);
        SUPPRESSIBLE_USAGES.put(10, 1);
        SUPPRESSIBLE_USAGES.put(11, 3);
        SUPPRESSIBLE_USAGES.put(2, 3);
        SUPPRESSIBLE_USAGES.put(4, 4);
        SUPPRESSIBLE_USAGES.put(1, 5);
        SUPPRESSIBLE_USAGES.put(12, 5);
        SUPPRESSIBLE_USAGES.put(14, 5);
        SUPPRESSIBLE_USAGES.put(16, 5);
        SUPPRESSIBLE_USAGES.put(0, 5);
        SUPPRESSIBLE_USAGES.put(3, 6);
        SUPPRESSIBLE_USAGES.put(13, 6);
        SDK_USAGES = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16};
        CREATOR = new Parcelable.Creator<AudioAttributes>() { // from class: android.media.AudioAttributes.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public AudioAttributes createFromParcel(Parcel parcel) {
                return new AudioAttributes(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public AudioAttributes[] newArray(int i) {
                return new AudioAttributes[i];
            }
        };
    }

    private AudioAttributes() {
        this.mUsage = 0;
        this.mContentType = 0;
        this.mSource = -1;
        this.mFlags = 0;
    }

    public void setAudioBehavior(AudioBehavior audioBehavior) {
        this.mBehavior = audioBehavior;
    }

    public AudioBehavior getAudioBehavior() {
        return this.mBehavior;
    }

    public int getContentType() {
        return this.mContentType;
    }

    public int getUsage() {
        return this.mUsage;
    }

    @SystemApi
    public int getCapturePreset() {
        return this.mSource;
    }

    public int getFlags() {
        return this.mFlags & 273;
    }

    @SystemApi
    public int getAllFlags() {
        return this.mFlags & 1023;
    }

    @SystemApi
    public Bundle getBundle() {
        Bundle bundle = this.mBundle;
        return bundle == null ? bundle : new Bundle(this.mBundle);
    }

    @SystemApi
    public Bundle getPrivateBundle() {
        synchronized (this) {
            if (this.mBundle == null) {
                Bundle bundle = new Bundle();
                this.mBundle = bundle;
                return bundle;
            }
            return this.mBundle;
        }
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(this.mTags);
    }

    public static class Builder {
        private Bundle mBundle;
        private int mContentType;
        private int mFlags;
        private int mSource;
        private HashSet<String> mTags;
        private int mUsage;

        public Builder() {
            this.mUsage = 0;
            this.mContentType = 0;
            this.mSource = -1;
            this.mFlags = 0;
            this.mTags = new HashSet<>();
        }

        public Builder(AudioAttributes audioAttributes) {
            this.mUsage = 0;
            this.mContentType = 0;
            this.mSource = -1;
            this.mFlags = 0;
            this.mTags = new HashSet<>();
            this.mUsage = audioAttributes.mUsage;
            this.mContentType = audioAttributes.mContentType;
            this.mFlags = audioAttributes.mFlags;
            this.mTags = (HashSet) audioAttributes.mTags.clone();
        }

        public AudioAttributes build() {
            AudioAttributes audioAttributes = new AudioAttributes();
            audioAttributes.mContentType = this.mContentType;
            audioAttributes.mUsage = this.mUsage;
            audioAttributes.mSource = this.mSource;
            audioAttributes.mFlags = this.mFlags;
            audioAttributes.mTags = (HashSet) this.mTags.clone();
            audioAttributes.mFormattedTags = TextUtils.join(";", this.mTags);
            if (this.mBundle != null) {
                audioAttributes.mBundle = new Bundle(this.mBundle);
            }
            return audioAttributes;
        }

        /* JADX WARN: Code duplicated, block: B:8:0x000f  */
        public Builder setUsage(int i) {
            if (i == 998 || i == 999) {
                this.mUsage = i;
            } else {
                switch (i) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                        this.mUsage = i;
                        break;
                    default:
                        this.mUsage = 0;
                        break;
                }
            }
            return this;
        }

        public Builder setContentType(int i) {
            if (i == 0 || i == 1 || i == 2 || i == 3 || i == 4) {
                this.mContentType = i;
            } else {
                this.mUsage = 0;
            }
            return this;
        }

        public Builder setFlags(int i) {
            this.mFlags = (i & 1023) | this.mFlags;
            return this;
        }

        public Builder replaceFlags(int i) {
            this.mFlags = i & 1023;
            return this;
        }

        @SystemApi
        public Builder addBundle(Bundle bundle) {
            if (bundle == null) {
                throw new IllegalArgumentException("Illegal null bundle");
            }
            Bundle bundle2 = this.mBundle;
            if (bundle2 == null) {
                this.mBundle = new Bundle(bundle);
            } else {
                bundle2.putAll(bundle);
            }
            return this;
        }

        public Builder addTag(String str) {
            this.mTags.add(str);
            return this;
        }

        public Builder setLegacyStreamType(int i) {
            if (i == 10) {
                throw new IllegalArgumentException("STREAM_ACCESSIBILITY is not a legacy stream type that was used for audio playback");
            }
            return setInternalLegacyStreamType(i);
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        public Builder setInternalLegacyStreamType(int i) {
            switch (i) {
                case 0:
                    this.mContentType = 1;
                    break;
                case 1:
                    this.mContentType = 4;
                    break;
                case 2:
                    this.mContentType = 4;
                    break;
                case 3:
                    this.mContentType = 2;
                    break;
                case 4:
                    this.mContentType = 4;
                    break;
                case 5:
                    this.mContentType = 4;
                    break;
                case 6:
                    this.mContentType = 1;
                    this.mFlags |= 4;
                    break;
                case 7:
                    this.mFlags = 1 | this.mFlags;
                    this.mContentType = 4;
                    break;
                case 8:
                    this.mContentType = 4;
                    break;
                case 9:
                    this.mContentType = 4;
                    break;
                case 10:
                    this.mContentType = 1;
                    break;
                default:
                    Log.e(AudioAttributes.TAG, "Invalid stream type " + i + " for AudioAttributes");
                    break;
            }
            this.mUsage = AudioAttributes.usageForStreamType(i);
            return this;
        }

        @SystemApi
        public Builder setCapturePreset(int i) {
            if (i == 0 || i == 1 || i == 5 || i == 6 || i == 7 || i == 9) {
                this.mSource = i;
            } else {
                Log.e(AudioAttributes.TAG, "Invalid capture preset " + i + " for AudioAttributes");
            }
            return this;
        }

        @SystemApi
        public Builder setInternalCapturePreset(int i) {
            if (i == 1999 || i == 8 || i == 1998 || i == 3 || i == 2 || i == 4) {
                this.mSource = i;
            } else {
                setCapturePreset(i);
            }
            return this;
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mUsage);
        parcel.writeInt(this.mContentType);
        parcel.writeInt(this.mSource);
        parcel.writeInt(this.mFlags);
        int i2 = i & 1;
        parcel.writeInt(i2);
        if (i2 == 0) {
            String[] strArr = new String[this.mTags.size()];
            this.mTags.toArray(strArr);
            parcel.writeStringArray(strArr);
        } else if (i2 == 1) {
            parcel.writeString(this.mFormattedTags);
        }
        if (this.mBundle == null) {
            parcel.writeInt(ATTR_PARCEL_IS_NULL_BUNDLE);
        } else {
            parcel.writeInt(ATTR_PARCEL_IS_VALID_BUNDLE);
            parcel.writeBundle(this.mBundle);
        }
    }

    private AudioAttributes(Parcel parcel) {
        this.mUsage = 0;
        this.mContentType = 0;
        this.mSource = -1;
        this.mFlags = 0;
        this.mUsage = parcel.readInt();
        this.mContentType = parcel.readInt();
        this.mSource = parcel.readInt();
        this.mFlags = parcel.readInt();
        boolean z = (parcel.readInt() & 1) == 1;
        this.mTags = new HashSet<>();
        if (z) {
            String str = new String(parcel.readString());
            this.mFormattedTags = str;
            this.mTags.add(str);
        } else {
            String[] stringArray = parcel.readStringArray();
            for (int length = stringArray.length - 1; length >= 0; length--) {
                this.mTags.add(stringArray[length]);
            }
            this.mFormattedTags = TextUtils.join(";", this.mTags);
        }
        int i = parcel.readInt();
        if (i == ATTR_PARCEL_IS_NULL_BUNDLE) {
            this.mBundle = null;
        } else if (i == ATTR_PARCEL_IS_VALID_BUNDLE) {
            this.mBundle = new Bundle(parcel.readBundle());
        } else {
            Log.e(TAG, "Illegal value unmarshalling AudioAttributes, can't initialize bundle");
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AudioAttributes audioAttributes = (AudioAttributes) obj;
        return this.mContentType == audioAttributes.mContentType && this.mFlags == audioAttributes.mFlags && this.mSource == audioAttributes.mSource && this.mUsage == audioAttributes.mUsage && this.mFormattedTags.equals(audioAttributes.mFormattedTags);
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mContentType), Integer.valueOf(this.mFlags), Integer.valueOf(this.mSource), Integer.valueOf(this.mUsage), this.mFormattedTags, this.mBundle);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AudioAttributes: usage=");
        sb.append(usageToString());
        sb.append(" content=");
        sb.append(contentTypeToString());
        sb.append(" flags=0x");
        sb.append(Integer.toHexString(this.mFlags).toUpperCase());
        sb.append(" tags=");
        sb.append(this.mFormattedTags);
        sb.append(" bundle=");
        Bundle bundle = this.mBundle;
        sb.append(bundle == null ? "null" : bundle.toString());
        sb.append(" AudioBehavior=");
        AudioBehavior audioBehavior = this.mBehavior;
        sb.append(audioBehavior != null ? audioBehavior.toString() : "null");
        return new String(sb.toString());
    }

    public void writeToProto(ProtoOutputStream protoOutputStream, long j) {
        long jStart = protoOutputStream.start(j);
        protoOutputStream.write(1159641169921L, this.mUsage);
        protoOutputStream.write(1159641169922L, this.mContentType);
        protoOutputStream.write(1120986464259L, this.mFlags);
        for (String str : this.mFormattedTags.split(";")) {
            String strTrim = str.trim();
            if (strTrim != "") {
                protoOutputStream.write(2237677961220L, strTrim);
            }
        }
        protoOutputStream.end(jStart);
    }

    public String usageToString() {
        return usageToString(this.mUsage);
    }

    public static String usageToString(int i) {
        if (i == 16) {
            return new String("USAGE_ASSISTANT");
        }
        if (i == 998) {
            return new String("USAGE_REVERSE");
        }
        if (i != 999) {
            switch (i) {
                case 0:
                    return new String("USAGE_UNKNOWN");
                case 1:
                    return new String("USAGE_MEDIA");
                case 2:
                    return new String("USAGE_VOICE_COMMUNICATION");
                case 3:
                    return new String("USAGE_VOICE_COMMUNICATION_SIGNALLING");
                case 4:
                    return new String("USAGE_ALARM");
                case 5:
                    return new String("USAGE_NOTIFICATION");
                case 6:
                    return new String("USAGE_NOTIFICATION_RINGTONE");
                case 7:
                    return new String("USAGE_NOTIFICATION_COMMUNICATION_REQUEST");
                case 8:
                    return new String("USAGE_NOTIFICATION_COMMUNICATION_INSTANT");
                case 9:
                    return new String("USAGE_NOTIFICATION_COMMUNICATION_DELAYED");
                case 10:
                    return new String("USAGE_NOTIFICATION_EVENT");
                case 11:
                    return new String("USAGE_ASSISTANCE_ACCESSIBILITY");
                case 12:
                    return new String("USAGE_ASSISTANCE_NAVIGATION_GUIDANCE");
                case 13:
                    return new String("USAGE_ASSISTANCE_SONIFICATION");
                case 14:
                    return new String("USAGE_GAME");
                default:
                    return new String("unknown usage " + i);
            }
        }
        return new String("USAGE_POWER");
    }

    public String contentTypeToString() {
        int i = this.mContentType;
        if (i == 0) {
            return new String("CONTENT_TYPE_UNKNOWN");
        }
        if (i == 1) {
            return new String("CONTENT_TYPE_SPEECH");
        }
        if (i == 2) {
            return new String("CONTENT_TYPE_MUSIC");
        }
        if (i == 3) {
            return new String("CONTENT_TYPE_MOVIE");
        }
        if (i == 4) {
            return new String("CONTENT_TYPE_SONIFICATION");
        }
        return new String("unknown content type " + this.mContentType);
    }

    public static String carSourceTypeToString(int i) {
        if (i != 90) {
            switch (i) {
                case 0:
                    return new String(KeyProperties.DIGEST_NONE);
                case 1:
                    return new String("RADIO_FM");
                case 2:
                    return new String("RADIO_AM");
                case 3:
                    return new String("DAB");
                case 4:
                    return new String("DISC");
                case 5:
                    return new String("BT_AUDIO");
                case 6:
                    return new String("USB_0");
                case 7:
                    return new String("USB_1");
                case 8:
                    return new String("IPOD_0");
                case 9:
                    return new String("IPOD_1");
                case 10:
                    return new String("AUX_0 ");
                case 11:
                    return new String("AUX_1");
                case 12:
                    return new String("SD_0");
                case 13:
                    return new String("SD_1");
                case 14:
                    return new String("ONLINE_MUSIC");
                case 15:
                    return new String("DLNA_MUSIC");
                case 16:
                    return new String("VEDIO");
                case 17:
                    return new String("ONLINE_VEDIO");
                case 18:
                    return new String("LOCAL_VEDIO");
                case 19:
                    return new String("TA");
                default:
                    switch (i) {
                        case 30:
                            return new String("PROMPT");
                        case 31:
                            return new String("VR");
                        case 32:
                            return new String(INetd.PERMISSION_SYSTEM);
                        case 33:
                            return new String("NAVI");
                        case 34:
                            return new String("NAVI_PROMPT");
                        case 35:
                            return new String("BEEP");
                        case 36:
                            return new String("SMS");
                        case 37:
                            return new String("ALARM");
                        case 38:
                            return new String("TERM");
                        default:
                            switch (i) {
                                case 40:
                                    return new String("BT_PHONE");
                                case 41:
                                    return new String("TBOX_PHONE");
                                case 42:
                                    return new String("E_CALL");
                                case 43:
                                    return new String("B_CALL");
                                case 44:
                                    return new String("I_CALL");
                                default:
                                    switch (i) {
                                        case 50:
                                            return new String("CP_MEDIA");
                                        case 51:
                                            return new String("CP_SIRI");
                                        case 52:
                                            return new String("CP_NAVI");
                                        case 53:
                                            return new String("CP_PHONE");
                                        case 54:
                                            return new String("CP_RING");
                                        case 55:
                                            return new String("CP_SPOKEN_AUDIO");
                                        case 56:
                                            return new String("CP_ALERT");
                                        default:
                                            switch (i) {
                                                case 60:
                                                    return new String("CL_MEDIA");
                                                case 61:
                                                    return new String("CL_NAVI");
                                                case 62:
                                                    return new String("CL_PROMPT");
                                                case 63:
                                                    return new String("CL_ASR");
                                                default:
                                                    switch (i) {
                                                        case 70:
                                                            return new String("AA_MEDIA");
                                                        case 71:
                                                            return new String("AA_NAVI");
                                                        case 72:
                                                            return new String("AA_VR");
                                                        case 73:
                                                            return new String("AA_PHONE");
                                                        case 74:
                                                            return new String("AA_RINGTONE");
                                                        default:
                                                            switch (i) {
                                                                case 80:
                                                                    return new String("RVC");
                                                                case 81:
                                                                    return new String("EA");
                                                                case 82:
                                                                    return new String("EA_PROMPT");
                                                                case 83:
                                                                    return new String("EA_PHONE");
                                                                default:
                                                                    return new String("unknown car source type " + i);
                                                            }
                                                    }
                                            }
                                    }
                            }
                    }
            }
        }
        return new String("ENGINEERING_MODE");
    }

    public static String audiofocusTypeToString(int i) {
        if (i == 0) {
            return new String("AUDIOFOCUS_GRANTED");
        }
        if (i == 1) {
            return new String("AUDIOFOCUS_DELAYED");
        }
        if (i == 2) {
            return new String("AUDIOFOCUS_REJECT");
        }
        if (i == 3) {
            return new String("AUDIOFOCUS_MIX");
        }
        return new String("unknown audio focus type " + i);
    }

    public int getVolumeControlStream() {
        return toVolumeStreamType(true, this);
    }

    public static int toLegacyStreamType(AudioAttributes audioAttributes) {
        return toVolumeStreamType(false, audioAttributes);
    }

    private static int toVolumeStreamType(boolean z, AudioAttributes audioAttributes) {
        if ((audioAttributes.getFlags() & 1) == 1) {
            return z ? 1 : 7;
        }
        if ((audioAttributes.getFlags() & 4) == 4) {
            return z ? 0 : 6;
        }
        switch (audioAttributes.getUsage()) {
            case 0:
            case 1:
            case 12:
            case 14:
            case 16:
                return 3;
            case 2:
                return 0;
            case 3:
                return z ? 0 : 8;
            case 4:
                return 4;
            case 5:
            case 7:
            case 8:
            case 9:
            case 10:
                return 5;
            case 6:
                return 2;
            case 11:
                return 10;
            case 13:
                return 1;
            case 15:
            default:
                if (!z) {
                    return 3;
                }
                throw new IllegalArgumentException("Unknown usage value " + audioAttributes.getUsage() + " in audio attributes");
        }
    }
}
