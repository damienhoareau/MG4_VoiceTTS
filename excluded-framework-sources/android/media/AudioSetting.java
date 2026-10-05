package android.media;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public class AudioSetting implements Parcelable {
    public static final String AUDIO_SETTING_3D_CHIME = "3d_chime";
    public static final String AUDIO_SETTING_A2B_MODEL = "A2B_MODEL";
    public static final String AUDIO_SETTING_ACTIVE_MEDIA_CHANNEL = "active_media_channel";
    public static final String AUDIO_SETTING_ACTIVE_NAVI_CHANNEL = "active_navi_channel";
    public static final String AUDIO_SETTING_ACTIVE_NOISE_REDUCTION = "active_noise_reduction";
    public static final String AUDIO_SETTING_ACTIVE_NOTICE_CHANNEL = "active_notice_channel";
    public static final String AUDIO_SETTING_ACTIVE_PHONE_CHANNEL = "active_phone_channel";
    public static final String AUDIO_SETTING_ACTIVE_VR_CHANNEL = "active_vr_channel";
    public static final String AUDIO_SETTING_AMBIENT_SOUND = "ambient_sound";
    public static final int AUDIO_SETTING_AMBIENT_SOUND_CINEMA = 3;
    public static final int AUDIO_SETTING_AMBIENT_SOUND_NATURAL = 1;
    public static final int AUDIO_SETTING_AMBIENT_SOUND_OFF = 0;
    public static final int AUDIO_SETTING_AMBIENT_SOUND_SCENE = 2;
    public static final int AUDIO_SETTING_AMBIENT_SOUND_SMART = 4;
    public static final String AUDIO_SETTING_All_MUTE = "all_mute";
    public static final String AUDIO_SETTING_BALANCE = "balance";
    public static final int AUDIO_SETTING_BASE_SETTING = 14;
    public static final String AUDIO_SETTING_BASS = "bass";
    public static final String AUDIO_SETTING_BOSE_SOUND_EFFECT = "bose_sound_effect";
    public static final int AUDIO_SETTING_BOSE_SOUND_EFFECT_ALL = 0;
    public static final int AUDIO_SETTING_BOSE_SOUND_EFFECT_DRIVER = 1;
    public static final int AUDIO_SETTING_BOSE_SOUND_EFFECT_TELEPHONE = 2;
    public static final String AUDIO_SETTING_CHIME_POSITION = "chime_position";
    public static final String AUDIO_SETTING_CHIME_VOLUME = "chime_volume";
    public static final int AUDIO_SETTING_CHIME_VOLUME_DEFAULT = 8;
    public static final int AUDIO_SETTING_CHIME_VOLUME_MAX = 16;
    public static final int AUDIO_SETTING_CHIME_VOLUME_MIN = 2;
    public static final int AUDIO_SETTING_DISABLE = 0;
    public static final String AUDIO_SETTING_DYNAMIC_SOUND_QUALITY = "dynamic_sound_quality";
    public static final String AUDIO_SETTING_DYNAMIC_SOUND_QUALITY_COMPENSATION = "dynamic_sound_quality_compensation";
    public static final int AUDIO_SETTING_ENABLE = 1;
    public static final String AUDIO_SETTING_ENGINE_ENHANCEMENTS = "engine_enhancements";
    public static final String AUDIO_SETTING_EQ_USER_BAND1 = "geq_band1";
    public static final String AUDIO_SETTING_EQ_USER_BAND2 = "geq_band2";
    public static final String AUDIO_SETTING_EQ_USER_BAND3 = "geq_band3";
    public static final String AUDIO_SETTING_EQ_USER_BAND4 = "geq_band4";
    public static final String AUDIO_SETTING_EQ_USER_BAND5 = "geq_band5";
    public static final String AUDIO_SETTING_ESE = "ESE";
    public static final String AUDIO_SETTING_FADER = "fader";
    public static final String AUDIO_SETTING_GEQ_BASE = "eq_base";
    public static final String AUDIO_SETTING_HEADREST_CODRIVER = "co_driver_headrest";
    public static final String AUDIO_SETTING_HEADREST_DRIVER = "driver_headrest";
    public static final String AUDIO_SETTING_HEADREST_STAGE_CODRIVER = "co_driver_headrest_stage";
    public static final String AUDIO_SETTING_HEADREST_STAGE_DRIVER = "driver_headrest_stage";
    public static final String AUDIO_SETTING_HRT_VOICE = "hrt_voice";
    public static final String AUDIO_SETTING_HRT_VOICE_STAGE = "hrt_voice_stage";
    public static final String AUDIO_SETTING_LOUDNESS = "loudness";
    public static final String AUDIO_SETTING_MEDIA_MUTE = "media_mute";
    public static final String AUDIO_SETTING_MIDDLE = "middle";
    public static final String AUDIO_SETTING_MUSIC_LIGHT = "music_light";
    public static final String AUDIO_SETTING_NAVI_DUCK = "navi_duck";
    public static final String AUDIO_SETTING_PEDESTRIAN_WARNING = "pedestrian_warning";
    public static final int AUDIO_SETTING_PEDESTRIAN_WARNING_TYEP_1 = 1;
    public static final int AUDIO_SETTING_PEDESTRIAN_WARNING_TYEP_2 = 2;
    public static final int AUDIO_SETTING_PEDESTRIAN_WARNING_TYEP_3 = 3;
    public static final int AUDIO_SETTING_PEDESTRIAN_WARNING_TYEP_DISBALE = 0;
    public static final String AUDIO_SETTING_PHONE_RING = "ring";
    public static final String AUDIO_SETTING_PRESET_EQ = "preset_eq";
    public static final int AUDIO_SETTING_PRESET_EQ_CLASSIC = 2;
    public static final int AUDIO_SETTING_PRESET_EQ_JAZZ = 5;
    public static final int AUDIO_SETTING_PRESET_EQ_OFF = 0;
    public static final int AUDIO_SETTING_PRESET_EQ_POPS = 3;
    public static final int AUDIO_SETTING_PRESET_EQ_ROCK = 6;
    public static final int AUDIO_SETTING_PRESET_EQ_SMART = 1;
    public static final int AUDIO_SETTING_PRESET_EQ_USER = 7;
    public static final int AUDIO_SETTING_PRESET_EQ_VOCAL = 4;
    public static final String AUDIO_SETTING_REAR_QUIET_MODE = "rear_quiet_mode";
    public static final String AUDIO_SETTING_SOUND_QUALITY = "sound_quality";
    public static final String AUDIO_SETTING_SOUND_STAGE = "sound_stage";
    public static final int AUDIO_SETTING_SOUND_STAGE_ALL = 1;
    public static final int AUDIO_SETTING_SOUND_STAGE_BACK = 4;
    public static final int AUDIO_SETTING_SOUND_STAGE_DRIVER = 2;
    public static final int AUDIO_SETTING_SOUND_STAGE_FRONT = 3;
    public static final int AUDIO_SETTING_SOUND_STAGE_OFF = 0;
    public static final String AUDIO_SETTING_SOUND_WAVE = "sound_wave";
    public static final String AUDIO_SETTING_SOUND_WAVE_SWITCH_VOLUME = "sound_wave_switch_volume";
    public static final String AUDIO_SETTING_SURROUND = "surround";
    public static final int AUDIO_SETTING_SURROUND_OFF = 0;
    public static final int AUDIO_SETTING_SURROUND_SMART = 6;
    public static final int AUDIO_SETTING_SURROUND_STEREO = 1;
    public static final int AUDIO_SETTING_SURROUND_SURROUND = 2;
    public static final int AUDIO_SETTING_SURROUND_SURROUND_2D = 3;
    public static final int AUDIO_SETTING_SURROUND_SURROUND_3D = 4;
    public static final int AUDIO_SETTING_SURROUND_SURROUND_4D = 5;
    public static final String AUDIO_SETTING_SVC = "svc_level";
    public static final int AUDIO_SETTING_SVC_LEVEL_HIGH = 3;
    public static final int AUDIO_SETTING_SVC_LEVEL_LOW = 1;
    public static final int AUDIO_SETTING_SVC_LEVEL_MIDDLE = 2;
    public static final int AUDIO_SETTING_SVC_LEVEL_OFF = 0;
    public static final String AUDIO_SETTING_TONE_CONTROL_BASS = "tone_control_bass";
    public static final String AUDIO_SETTING_TONE_CONTROL_MIDRANGE = "tone_control_midrange";
    public static final String AUDIO_SETTING_TONE_CONTROL_TREBLE = "tone_control_trible";
    public static final String AUDIO_SETTING_TRIBLE = "trible";
    public static final String AUDIO_SETTING_VIRTUAL_PROTECT_TONE = "protect_tone";
    public static final String AUDIO_SETTING_VIRTUAL_SUBWOOFER = "subwoofer";
    public static final String AUDIO_SETTING_VOICE_PARTITION = "voice_partition";
    public static final int AUDIO_SETTING_VOICE_PARTITION_ALL = 6;
    public static final int AUDIO_SETTING_VOICE_PARTITION_FL = 0;
    public static final int AUDIO_SETTING_VOICE_PARTITION_FR = 1;
    public static final int AUDIO_SETTING_VOICE_PARTITION_ML = 2;
    public static final int AUDIO_SETTING_VOICE_PARTITION_MR = 3;
    public static final int AUDIO_SETTING_VOICE_PARTITION_RL = 4;
    public static final int AUDIO_SETTING_VOICE_PARTITION_RR = 5;
    public static final String AUDIO_SETTING_VOLUME_COMPENSATE = "auto_volume_compensate";
    public static final String AUDIO_SETTING_VR_LIGHT = "vr_light";
    public static final String AUDIO_SETTING_WELCOME = "welcome";
    public static final Parcelable.Creator<AudioSetting> CREATOR = new Parcelable.Creator<AudioSetting>() { // from class: android.media.AudioSetting.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AudioSetting createFromParcel(Parcel parcel) {
            return new AudioSetting(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AudioSetting[] newArray(int i) {
            return new AudioSetting[i];
        }
    };
    private final int flag;
    private final int group;
    private final int index;
    private final String key;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public AudioSetting(String str, int i, int i2, int i3) {
        this.key = str;
        this.index = i;
        this.group = i2;
        this.flag = i3;
    }

    public String getKey() {
        return this.key;
    }

    public int getIndex() {
        return this.index;
    }

    public int getGroup() {
        return this.group;
    }

    public int getFlag() {
        return this.flag;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.key);
        parcel.writeInt(this.index);
        parcel.writeInt(this.group);
        parcel.writeInt(this.flag);
    }

    protected AudioSetting(Parcel parcel) {
        this.key = parcel.readString();
        this.index = parcel.readInt();
        this.group = parcel.readInt();
        this.flag = parcel.readInt();
    }
}
