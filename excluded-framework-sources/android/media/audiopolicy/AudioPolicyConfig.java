package android.media.audiopolicy;

import android.app.backup.FullBackup;
import android.media.AudioFormat;
import android.media.TtmlUtils;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.SettingsStringUtil;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class AudioPolicyConfig implements Parcelable {
    public static final Parcelable.Creator<AudioPolicyConfig> CREATOR = new Parcelable.Creator<AudioPolicyConfig>() { // from class: android.media.audiopolicy.AudioPolicyConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AudioPolicyConfig createFromParcel(Parcel parcel) {
            return new AudioPolicyConfig(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AudioPolicyConfig[] newArray(int i) {
            return new AudioPolicyConfig[i];
        }
    };
    private static final String TAG = "AudioPolicyConfig";
    protected int mDuckingPolicy;
    private int mMixCounter;
    protected final ArrayList<AudioMix> mMixes;
    private String mRegistrationId;

    private static String mixTypeId(int i) {
        if (i == 0) {
            return TtmlUtils.TAG_P;
        }
        return i == 1 ? FullBackup.ROOT_TREE_TOKEN : "i";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    protected AudioPolicyConfig(AudioPolicyConfig audioPolicyConfig) {
        this.mDuckingPolicy = 0;
        this.mRegistrationId = null;
        this.mMixCounter = 0;
        this.mMixes = audioPolicyConfig.mMixes;
    }

    AudioPolicyConfig(ArrayList<AudioMix> arrayList) {
        this.mDuckingPolicy = 0;
        this.mRegistrationId = null;
        this.mMixCounter = 0;
        this.mMixes = arrayList;
    }

    public void addMix(AudioMix audioMix) throws IllegalArgumentException {
        if (audioMix == null) {
            throw new IllegalArgumentException("Illegal null AudioMix argument");
        }
        this.mMixes.add(audioMix);
    }

    public ArrayList<AudioMix> getMixes() {
        return this.mMixes;
    }

    public int hashCode() {
        return Objects.hash(this.mMixes);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mMixes.size());
        for (AudioMix audioMix : this.mMixes) {
            parcel.writeInt(audioMix.getRouteFlags());
            parcel.writeInt(audioMix.mCallbackFlags);
            parcel.writeInt(audioMix.mDeviceSystemType);
            parcel.writeString(audioMix.mDeviceAddress);
            parcel.writeInt(audioMix.getFormat().getSampleRate());
            parcel.writeInt(audioMix.getFormat().getEncoding());
            parcel.writeInt(audioMix.getFormat().getChannelMask());
            ArrayList<AudioMixingRule.AudioMixMatchCriterion> criteria = audioMix.getRule().getCriteria();
            parcel.writeInt(criteria.size());
            Iterator<AudioMixingRule.AudioMixMatchCriterion> it = criteria.iterator();
            while (it.hasNext()) {
                it.next().writeToParcel(parcel);
            }
        }
    }

    private AudioPolicyConfig(Parcel parcel) {
        this.mDuckingPolicy = 0;
        this.mRegistrationId = null;
        this.mMixCounter = 0;
        this.mMixes = new ArrayList<>();
        int i = parcel.readInt();
        for (int i2 = 0; i2 < i; i2++) {
            AudioMix.Builder builder = new AudioMix.Builder();
            builder.setRouteFlags(parcel.readInt());
            builder.setCallbackFlags(parcel.readInt());
            builder.setDevice(parcel.readInt(), parcel.readString());
            builder.setFormat(new AudioFormat.Builder().setSampleRate(parcel.readInt()).setChannelMask(parcel.readInt()).setEncoding(parcel.readInt()).build());
            int i3 = parcel.readInt();
            AudioMixingRule.Builder builder2 = new AudioMixingRule.Builder();
            for (int i4 = 0; i4 < i3; i4++) {
                builder2.addRuleFromParcel(parcel);
            }
            builder.setMixingRule(builder2.build());
            this.mMixes.add(builder.build());
        }
    }

    public String toLogFriendlyString() {
        String str;
        String str2 = new String("android.media.audiopolicy.AudioPolicyConfig:\n") + this.mMixes.size() + " AudioMix: " + this.mRegistrationId + "\n";
        for (AudioMix audioMix : this.mMixes) {
            str2 = ((((str2 + "* route flags=0x" + Integer.toHexString(audioMix.getRouteFlags()) + "\n") + "  rate=" + audioMix.getFormat().getSampleRate() + "Hz\n") + "  encoding=" + audioMix.getFormat().getEncoding() + "\n") + "  channels=0x") + Integer.toHexString(audioMix.getFormat().getChannelMask()).toUpperCase() + "\n";
            for (AudioMixingRule.AudioMixMatchCriterion audioMixMatchCriterion : audioMix.getRule().getCriteria()) {
                int i = audioMixMatchCriterion.mRule;
                if (i == 1) {
                    str = (str2 + "  match usage ") + audioMixMatchCriterion.mAttr.usageToString();
                } else if (i == 2) {
                    str = (str2 + "  match capture preset ") + audioMixMatchCriterion.mAttr.getCapturePreset();
                } else if (i == 4) {
                    str = (str2 + "  match UID ") + audioMixMatchCriterion.mIntProp;
                } else if (i != 32772) {
                    switch (i) {
                        case 32769:
                            str = (str2 + "  exclude usage ") + audioMixMatchCriterion.mAttr.usageToString();
                            break;
                        case 32770:
                            str = (str2 + "  exclude capture preset ") + audioMixMatchCriterion.mAttr.getCapturePreset();
                            break;
                        default:
                            str = str2 + "invalid rule!";
                            break;
                    }
                } else {
                    str = (str2 + "  exclude UID ") + audioMixMatchCriterion.mIntProp;
                }
                str2 = str + "\n";
            }
        }
        return str2;
    }

    protected void setRegistration(String str) {
        String str2 = this.mRegistrationId;
        boolean z = str2 == null || str2.isEmpty();
        boolean z2 = str == null || str.isEmpty();
        if (!z && !z2 && !this.mRegistrationId.equals(str)) {
            Log.e(TAG, "Invalid registration transition from " + this.mRegistrationId + " to " + str);
            return;
        }
        if (str == null) {
            str = "";
        }
        this.mRegistrationId = str;
        Iterator<AudioMix> it = this.mMixes.iterator();
        while (it.hasNext()) {
            setMixRegistration(it.next());
        }
    }

    private void setMixRegistration(AudioMix audioMix) {
        if (!this.mRegistrationId.isEmpty()) {
            if ((audioMix.getRouteFlags() & 2) == 2) {
                audioMix.setRegistration(this.mRegistrationId + "mix" + mixTypeId(audioMix.getMixType()) + SettingsStringUtil.DELIMITER + this.mMixCounter);
            } else if ((audioMix.getRouteFlags() & 1) == 1) {
                audioMix.setRegistration(audioMix.mDeviceAddress);
            }
        } else {
            audioMix.setRegistration("");
        }
        this.mMixCounter++;
    }

    protected void add(ArrayList<AudioMix> arrayList) {
        for (AudioMix audioMix : arrayList) {
            setMixRegistration(audioMix);
            this.mMixes.add(audioMix);
        }
    }

    protected void remove(ArrayList<AudioMix> arrayList) {
        Iterator<AudioMix> it = arrayList.iterator();
        while (it.hasNext()) {
            this.mMixes.remove(it.next());
        }
    }

    protected String getRegistration() {
        return this.mRegistrationId;
    }
}
