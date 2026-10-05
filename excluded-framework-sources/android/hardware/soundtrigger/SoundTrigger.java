package android.hardware.soundtrigger;

import android.annotation.SystemApi;
import android.media.AudioFormat;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.system.OsConstants;
import com.android.internal.logging.nano.MetricsProto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
@SystemApi
public class SoundTrigger {
    public static final int RECOGNITION_MODE_USER_AUTHENTICATION = 4;
    public static final int RECOGNITION_MODE_USER_IDENTIFICATION = 2;
    public static final int RECOGNITION_MODE_VOICE_TRIGGER = 1;
    public static final int RECOGNITION_STATUS_ABORT = 1;
    public static final int RECOGNITION_STATUS_FAILURE = 2;
    public static final int RECOGNITION_STATUS_SUCCESS = 0;
    public static final int SERVICE_STATE_DISABLED = 1;
    public static final int SERVICE_STATE_ENABLED = 0;
    public static final int SOUNDMODEL_STATUS_UPDATED = 0;
    public static final int STATUS_ERROR = Integer.MIN_VALUE;
    public static final int STATUS_OK = 0;
    public static final int STATUS_PERMISSION_DENIED = -OsConstants.EPERM;
    public static final int STATUS_NO_INIT = -OsConstants.ENODEV;
    public static final int STATUS_BAD_VALUE = -OsConstants.EINVAL;
    public static final int STATUS_DEAD_OBJECT = -OsConstants.EPIPE;
    public static final int STATUS_INVALID_OPERATION = -OsConstants.ENOSYS;

    public interface StatusListener {
        void onRecognition(RecognitionEvent recognitionEvent);

        void onServiceDied();

        void onServiceStateChange(int i);

        void onSoundModelUpdate(SoundModelEvent soundModelEvent);
    }

    public static native int listModules(ArrayList<ModuleProperties> arrayList);

    private SoundTrigger() {
    }

    public static class ModuleProperties implements Parcelable {
        public static final Parcelable.Creator<ModuleProperties> CREATOR = new Parcelable.Creator<ModuleProperties>() { // from class: android.hardware.soundtrigger.SoundTrigger.ModuleProperties.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ModuleProperties createFromParcel(Parcel parcel) {
                return ModuleProperties.fromParcel(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ModuleProperties[] newArray(int i) {
                return new ModuleProperties[i];
            }
        };
        public final String description;
        public final int id;
        public final String implementor;
        public final int maxBufferMs;
        public final int maxKeyphrases;
        public final int maxSoundModels;
        public final int maxUsers;
        public final int powerConsumptionMw;
        public final int recognitionModes;
        public final boolean returnsTriggerInEvent;
        public final boolean supportsCaptureTransition;
        public final boolean supportsConcurrentCapture;
        public final UUID uuid;
        public final int version;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        ModuleProperties(int i, String str, String str2, String str3, int i2, int i3, int i4, int i5, int i6, boolean z, int i7, boolean z2, int i8, boolean z3) {
            this.id = i;
            this.implementor = str;
            this.description = str2;
            this.uuid = UUID.fromString(str3);
            this.version = i2;
            this.maxSoundModels = i3;
            this.maxKeyphrases = i4;
            this.maxUsers = i5;
            this.recognitionModes = i6;
            this.supportsCaptureTransition = z;
            this.maxBufferMs = i7;
            this.supportsConcurrentCapture = z2;
            this.powerConsumptionMw = i8;
            this.returnsTriggerInEvent = z3;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static ModuleProperties fromParcel(Parcel parcel) {
            return new ModuleProperties(parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readByte() == 1, parcel.readInt(), parcel.readByte() == 1, parcel.readInt(), parcel.readByte() == 1);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.id);
            parcel.writeString(this.implementor);
            parcel.writeString(this.description);
            parcel.writeString(this.uuid.toString());
            parcel.writeInt(this.version);
            parcel.writeInt(this.maxSoundModels);
            parcel.writeInt(this.maxKeyphrases);
            parcel.writeInt(this.maxUsers);
            parcel.writeInt(this.recognitionModes);
            parcel.writeByte(this.supportsCaptureTransition ? (byte) 1 : (byte) 0);
            parcel.writeInt(this.maxBufferMs);
            parcel.writeByte(this.supportsConcurrentCapture ? (byte) 1 : (byte) 0);
            parcel.writeInt(this.powerConsumptionMw);
            parcel.writeByte(this.returnsTriggerInEvent ? (byte) 1 : (byte) 0);
        }

        public String toString() {
            return "ModuleProperties [id=" + this.id + ", implementor=" + this.implementor + ", description=" + this.description + ", uuid=" + this.uuid + ", version=" + this.version + ", maxSoundModels=" + this.maxSoundModels + ", maxKeyphrases=" + this.maxKeyphrases + ", maxUsers=" + this.maxUsers + ", recognitionModes=" + this.recognitionModes + ", supportsCaptureTransition=" + this.supportsCaptureTransition + ", maxBufferMs=" + this.maxBufferMs + ", supportsConcurrentCapture=" + this.supportsConcurrentCapture + ", powerConsumptionMw=" + this.powerConsumptionMw + ", returnsTriggerInEvent=" + this.returnsTriggerInEvent + "]";
        }
    }

    public static class SoundModel {
        public static final int TYPE_GENERIC_SOUND = 1;
        public static final int TYPE_KEYPHRASE = 0;
        public static final int TYPE_UNKNOWN = -1;
        public final byte[] data;
        public final int type;
        public final UUID uuid;
        public final UUID vendorUuid;

        public SoundModel(UUID uuid, UUID uuid2, int i, byte[] bArr) {
            this.uuid = uuid;
            this.vendorUuid = uuid2;
            this.type = i;
            this.data = bArr;
        }

        public int hashCode() {
            int iHashCode = (((Arrays.hashCode(this.data) + 31) * 31) + this.type) * 31;
            UUID uuid = this.uuid;
            int iHashCode2 = (iHashCode + (uuid == null ? 0 : uuid.hashCode())) * 31;
            UUID uuid2 = this.vendorUuid;
            return iHashCode2 + (uuid2 != null ? uuid2.hashCode() : 0);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || !(obj instanceof SoundModel)) {
                return false;
            }
            SoundModel soundModel = (SoundModel) obj;
            if (!Arrays.equals(this.data, soundModel.data) || this.type != soundModel.type) {
                return false;
            }
            UUID uuid = this.uuid;
            if (uuid == null) {
                if (soundModel.uuid != null) {
                    return false;
                }
            } else if (!uuid.equals(soundModel.uuid)) {
                return false;
            }
            UUID uuid2 = this.vendorUuid;
            if (uuid2 == null) {
                if (soundModel.vendorUuid != null) {
                    return false;
                }
            } else if (!uuid2.equals(soundModel.vendorUuid)) {
                return false;
            }
            return true;
        }
    }

    public static class Keyphrase implements Parcelable {
        public static final Parcelable.Creator<Keyphrase> CREATOR = new Parcelable.Creator<Keyphrase>() { // from class: android.hardware.soundtrigger.SoundTrigger.Keyphrase.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Keyphrase createFromParcel(Parcel parcel) {
                return Keyphrase.fromParcel(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Keyphrase[] newArray(int i) {
                return new Keyphrase[i];
            }
        };
        public final int id;
        public final String locale;
        public final int recognitionModes;
        public final String text;
        public final int[] users;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public Keyphrase(int i, int i2, String str, String str2, int[] iArr) {
            this.id = i;
            this.recognitionModes = i2;
            this.locale = str;
            this.text = str2;
            this.users = iArr;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static Keyphrase fromParcel(Parcel parcel) {
            int[] iArr;
            int i = parcel.readInt();
            int i2 = parcel.readInt();
            String string = parcel.readString();
            String string2 = parcel.readString();
            int i3 = parcel.readInt();
            if (i3 >= 0) {
                int[] iArr2 = new int[i3];
                parcel.readIntArray(iArr2);
                iArr = iArr2;
            } else {
                iArr = null;
            }
            return new Keyphrase(i, i2, string, string2, iArr);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.id);
            parcel.writeInt(this.recognitionModes);
            parcel.writeString(this.locale);
            parcel.writeString(this.text);
            int[] iArr = this.users;
            if (iArr != null) {
                parcel.writeInt(iArr.length);
                parcel.writeIntArray(this.users);
            } else {
                parcel.writeInt(-1);
            }
        }

        public int hashCode() {
            String str = this.text;
            int iHashCode = ((((str == null ? 0 : str.hashCode()) + 31) * 31) + this.id) * 31;
            String str2 = this.locale;
            return ((((iHashCode + (str2 != null ? str2.hashCode() : 0)) * 31) + this.recognitionModes) * 31) + Arrays.hashCode(this.users);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Keyphrase keyphrase = (Keyphrase) obj;
            String str = this.text;
            if (str == null) {
                if (keyphrase.text != null) {
                    return false;
                }
            } else if (!str.equals(keyphrase.text)) {
                return false;
            }
            if (this.id != keyphrase.id) {
                return false;
            }
            String str2 = this.locale;
            if (str2 == null) {
                if (keyphrase.locale != null) {
                    return false;
                }
            } else if (!str2.equals(keyphrase.locale)) {
                return false;
            }
            return this.recognitionModes == keyphrase.recognitionModes && Arrays.equals(this.users, keyphrase.users);
        }

        public String toString() {
            return "Keyphrase [id=" + this.id + ", recognitionModes=" + this.recognitionModes + ", locale=" + this.locale + ", text=" + this.text + ", users=" + Arrays.toString(this.users) + "]";
        }
    }

    public static class KeyphraseSoundModel extends SoundModel implements Parcelable {
        public static final Parcelable.Creator<KeyphraseSoundModel> CREATOR = new Parcelable.Creator<KeyphraseSoundModel>() { // from class: android.hardware.soundtrigger.SoundTrigger.KeyphraseSoundModel.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public KeyphraseSoundModel createFromParcel(Parcel parcel) {
                return KeyphraseSoundModel.fromParcel(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public KeyphraseSoundModel[] newArray(int i) {
                return new KeyphraseSoundModel[i];
            }
        };
        public final Keyphrase[] keyphrases;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public KeyphraseSoundModel(UUID uuid, UUID uuid2, byte[] bArr, Keyphrase[] keyphraseArr) {
            super(uuid, uuid2, 0, bArr);
            this.keyphrases = keyphraseArr;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static KeyphraseSoundModel fromParcel(Parcel parcel) {
            return new KeyphraseSoundModel(UUID.fromString(parcel.readString()), parcel.readInt() >= 0 ? UUID.fromString(parcel.readString()) : null, parcel.readBlob(), (Keyphrase[]) parcel.createTypedArray(Keyphrase.CREATOR));
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.uuid.toString());
            if (this.vendorUuid == null) {
                parcel.writeInt(-1);
            } else {
                parcel.writeInt(this.vendorUuid.toString().length());
                parcel.writeString(this.vendorUuid.toString());
            }
            parcel.writeBlob(this.data);
            parcel.writeTypedArray(this.keyphrases, i);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("KeyphraseSoundModel [keyphrases=");
            sb.append(Arrays.toString(this.keyphrases));
            sb.append(", uuid=");
            sb.append(this.uuid);
            sb.append(", vendorUuid=");
            sb.append(this.vendorUuid);
            sb.append(", type=");
            sb.append(this.type);
            sb.append(", data=");
            sb.append(this.data == null ? 0 : this.data.length);
            sb.append("]");
            return sb.toString();
        }

        @Override // android.hardware.soundtrigger.SoundTrigger.SoundModel
        public int hashCode() {
            return (super.hashCode() * 31) + Arrays.hashCode(this.keyphrases);
        }

        @Override // android.hardware.soundtrigger.SoundTrigger.SoundModel
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            return super.equals(obj) && (obj instanceof KeyphraseSoundModel) && Arrays.equals(this.keyphrases, ((KeyphraseSoundModel) obj).keyphrases);
        }
    }

    public static class GenericSoundModel extends SoundModel implements Parcelable {
        public static final Parcelable.Creator<GenericSoundModel> CREATOR = new Parcelable.Creator<GenericSoundModel>() { // from class: android.hardware.soundtrigger.SoundTrigger.GenericSoundModel.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public GenericSoundModel createFromParcel(Parcel parcel) {
                return GenericSoundModel.fromParcel(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public GenericSoundModel[] newArray(int i) {
                return new GenericSoundModel[i];
            }
        };

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public GenericSoundModel(UUID uuid, UUID uuid2, byte[] bArr) {
            super(uuid, uuid2, 1, bArr);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static GenericSoundModel fromParcel(Parcel parcel) {
            return new GenericSoundModel(UUID.fromString(parcel.readString()), parcel.readInt() >= 0 ? UUID.fromString(parcel.readString()) : null, parcel.readBlob());
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.uuid.toString());
            if (this.vendorUuid == null) {
                parcel.writeInt(-1);
            } else {
                parcel.writeInt(this.vendorUuid.toString().length());
                parcel.writeString(this.vendorUuid.toString());
            }
            parcel.writeBlob(this.data);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("GenericSoundModel [uuid=");
            sb.append(this.uuid);
            sb.append(", vendorUuid=");
            sb.append(this.vendorUuid);
            sb.append(", type=");
            sb.append(this.type);
            sb.append(", data=");
            sb.append(this.data == null ? 0 : this.data.length);
            sb.append("]");
            return sb.toString();
        }
    }

    public static class RecognitionEvent {
        public static final Parcelable.Creator<RecognitionEvent> CREATOR = new Parcelable.Creator<RecognitionEvent>() { // from class: android.hardware.soundtrigger.SoundTrigger.RecognitionEvent.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public RecognitionEvent createFromParcel(Parcel parcel) {
                return RecognitionEvent.fromParcel(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public RecognitionEvent[] newArray(int i) {
                return new RecognitionEvent[i];
            }
        };
        public final boolean captureAvailable;
        public final int captureDelayMs;
        public final AudioFormat captureFormat;
        public final int capturePreambleMs;
        public final int captureSession;
        public final byte[] data;
        public final int soundModelHandle;
        public final int status;
        public final boolean triggerInData;

        public int describeContents() {
            return 0;
        }

        public RecognitionEvent(int i, int i2, boolean z, int i3, int i4, int i5, boolean z2, AudioFormat audioFormat, byte[] bArr) {
            this.status = i;
            this.soundModelHandle = i2;
            this.captureAvailable = z;
            this.captureSession = i3;
            this.captureDelayMs = i4;
            this.capturePreambleMs = i5;
            this.triggerInData = z2;
            this.captureFormat = audioFormat;
            this.data = bArr;
        }

        public boolean isCaptureAvailable() {
            return this.captureAvailable;
        }

        public AudioFormat getCaptureFormat() {
            return this.captureFormat;
        }

        public int getCaptureSession() {
            return this.captureSession;
        }

        public byte[] getData() {
            return this.data;
        }

        protected static RecognitionEvent fromParcel(Parcel parcel) {
            int i = parcel.readInt();
            int i2 = parcel.readInt();
            boolean z = parcel.readByte() == 1;
            int i3 = parcel.readInt();
            int i4 = parcel.readInt();
            int i5 = parcel.readInt();
            boolean z2 = parcel.readByte() == 1;
            AudioFormat audioFormatBuild = null;
            if (parcel.readByte() == 1) {
                audioFormatBuild = new AudioFormat.Builder().setChannelMask(parcel.readInt()).setEncoding(parcel.readInt()).setSampleRate(parcel.readInt()).build();
            }
            return new RecognitionEvent(i, i2, z, i3, i4, i5, z2, audioFormatBuild, parcel.readBlob());
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.status);
            parcel.writeInt(this.soundModelHandle);
            parcel.writeByte(this.captureAvailable ? (byte) 1 : (byte) 0);
            parcel.writeInt(this.captureSession);
            parcel.writeInt(this.captureDelayMs);
            parcel.writeInt(this.capturePreambleMs);
            parcel.writeByte(this.triggerInData ? (byte) 1 : (byte) 0);
            if (this.captureFormat != null) {
                parcel.writeByte((byte) 1);
                parcel.writeInt(this.captureFormat.getSampleRate());
                parcel.writeInt(this.captureFormat.getEncoding());
                parcel.writeInt(this.captureFormat.getChannelMask());
            } else {
                parcel.writeByte((byte) 0);
            }
            parcel.writeBlob(this.data);
        }

        public int hashCode() {
            boolean z = this.captureAvailable;
            int i = MetricsProto.MetricsEvent.AUTOFILL_SERVICE_DISABLED_APP;
            int i2 = ((((((((z ? 1231 : 1237) + 31) * 31) + this.captureDelayMs) * 31) + this.capturePreambleMs) * 31) + this.captureSession) * 31;
            if (!this.triggerInData) {
                i = 1237;
            }
            int sampleRate = i2 + i;
            AudioFormat audioFormat = this.captureFormat;
            if (audioFormat != null) {
                sampleRate = (((((sampleRate * 31) + audioFormat.getSampleRate()) * 31) + this.captureFormat.getEncoding()) * 31) + this.captureFormat.getChannelMask();
            }
            return (((((sampleRate * 31) + Arrays.hashCode(this.data)) * 31) + this.soundModelHandle) * 31) + this.status;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            RecognitionEvent recognitionEvent = (RecognitionEvent) obj;
            if (this.captureAvailable != recognitionEvent.captureAvailable || this.captureDelayMs != recognitionEvent.captureDelayMs || this.capturePreambleMs != recognitionEvent.capturePreambleMs || this.captureSession != recognitionEvent.captureSession || !Arrays.equals(this.data, recognitionEvent.data) || this.soundModelHandle != recognitionEvent.soundModelHandle || this.status != recognitionEvent.status || this.triggerInData != recognitionEvent.triggerInData) {
                return false;
            }
            AudioFormat audioFormat = this.captureFormat;
            if (audioFormat == null) {
                if (recognitionEvent.captureFormat != null) {
                    return false;
                }
            } else if (recognitionEvent.captureFormat == null || audioFormat.getSampleRate() != recognitionEvent.captureFormat.getSampleRate() || this.captureFormat.getEncoding() != recognitionEvent.captureFormat.getEncoding() || this.captureFormat.getChannelMask() != recognitionEvent.captureFormat.getChannelMask()) {
                return false;
            }
            return true;
        }

        public String toString() {
            String str;
            String str2;
            StringBuilder sb = new StringBuilder();
            sb.append("RecognitionEvent [status=");
            sb.append(this.status);
            sb.append(", soundModelHandle=");
            sb.append(this.soundModelHandle);
            sb.append(", captureAvailable=");
            sb.append(this.captureAvailable);
            sb.append(", captureSession=");
            sb.append(this.captureSession);
            sb.append(", captureDelayMs=");
            sb.append(this.captureDelayMs);
            sb.append(", capturePreambleMs=");
            sb.append(this.capturePreambleMs);
            sb.append(", triggerInData=");
            sb.append(this.triggerInData);
            String str3 = "";
            if (this.captureFormat == null) {
                str = "";
            } else {
                str = ", sampleRate=" + this.captureFormat.getSampleRate();
            }
            sb.append(str);
            if (this.captureFormat == null) {
                str2 = "";
            } else {
                str2 = ", encoding=" + this.captureFormat.getEncoding();
            }
            sb.append(str2);
            if (this.captureFormat != null) {
                str3 = ", channelMask=" + this.captureFormat.getChannelMask();
            }
            sb.append(str3);
            sb.append(", data=");
            byte[] bArr = this.data;
            sb.append(bArr == null ? 0 : bArr.length);
            sb.append("]");
            return sb.toString();
        }
    }

    public static class RecognitionConfig implements Parcelable {
        public static final Parcelable.Creator<RecognitionConfig> CREATOR = new Parcelable.Creator<RecognitionConfig>() { // from class: android.hardware.soundtrigger.SoundTrigger.RecognitionConfig.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public RecognitionConfig createFromParcel(Parcel parcel) {
                return RecognitionConfig.fromParcel(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public RecognitionConfig[] newArray(int i) {
                return new RecognitionConfig[i];
            }
        };
        public final boolean allowMultipleTriggers;
        public final boolean captureRequested;
        public final byte[] data;
        public final KeyphraseRecognitionExtra[] keyphrases;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public RecognitionConfig(boolean z, boolean z2, KeyphraseRecognitionExtra[] keyphraseRecognitionExtraArr, byte[] bArr) {
            this.captureRequested = z;
            this.allowMultipleTriggers = z2;
            this.keyphrases = keyphraseRecognitionExtraArr;
            this.data = bArr;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static RecognitionConfig fromParcel(Parcel parcel) {
            return new RecognitionConfig(parcel.readByte() == 1, parcel.readByte() == 1, (KeyphraseRecognitionExtra[]) parcel.createTypedArray(KeyphraseRecognitionExtra.CREATOR), parcel.readBlob());
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeByte(this.captureRequested ? (byte) 1 : (byte) 0);
            parcel.writeByte(this.allowMultipleTriggers ? (byte) 1 : (byte) 0);
            parcel.writeTypedArray(this.keyphrases, i);
            parcel.writeBlob(this.data);
        }

        public String toString() {
            return "RecognitionConfig [captureRequested=" + this.captureRequested + ", allowMultipleTriggers=" + this.allowMultipleTriggers + ", keyphrases=" + Arrays.toString(this.keyphrases) + ", data=" + Arrays.toString(this.data) + "]";
        }
    }

    public static class ConfidenceLevel implements Parcelable {
        public static final Parcelable.Creator<ConfidenceLevel> CREATOR = new Parcelable.Creator<ConfidenceLevel>() { // from class: android.hardware.soundtrigger.SoundTrigger.ConfidenceLevel.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ConfidenceLevel createFromParcel(Parcel parcel) {
                return ConfidenceLevel.fromParcel(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ConfidenceLevel[] newArray(int i) {
                return new ConfidenceLevel[i];
            }
        };
        public final int confidenceLevel;
        public final int userId;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public ConfidenceLevel(int i, int i2) {
            this.userId = i;
            this.confidenceLevel = i2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static ConfidenceLevel fromParcel(Parcel parcel) {
            return new ConfidenceLevel(parcel.readInt(), parcel.readInt());
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.userId);
            parcel.writeInt(this.confidenceLevel);
        }

        public int hashCode() {
            return ((this.confidenceLevel + 31) * 31) + this.userId;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ConfidenceLevel confidenceLevel = (ConfidenceLevel) obj;
            return this.confidenceLevel == confidenceLevel.confidenceLevel && this.userId == confidenceLevel.userId;
        }

        public String toString() {
            return "ConfidenceLevel [userId=" + this.userId + ", confidenceLevel=" + this.confidenceLevel + "]";
        }
    }

    public static class KeyphraseRecognitionExtra implements Parcelable {
        public static final Parcelable.Creator<KeyphraseRecognitionExtra> CREATOR = new Parcelable.Creator<KeyphraseRecognitionExtra>() { // from class: android.hardware.soundtrigger.SoundTrigger.KeyphraseRecognitionExtra.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public KeyphraseRecognitionExtra createFromParcel(Parcel parcel) {
                return KeyphraseRecognitionExtra.fromParcel(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public KeyphraseRecognitionExtra[] newArray(int i) {
                return new KeyphraseRecognitionExtra[i];
            }
        };
        public final int coarseConfidenceLevel;
        public final ConfidenceLevel[] confidenceLevels;
        public final int id;
        public final int recognitionModes;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public KeyphraseRecognitionExtra(int i, int i2, int i3, ConfidenceLevel[] confidenceLevelArr) {
            this.id = i;
            this.recognitionModes = i2;
            this.coarseConfidenceLevel = i3;
            this.confidenceLevels = confidenceLevelArr;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static KeyphraseRecognitionExtra fromParcel(Parcel parcel) {
            return new KeyphraseRecognitionExtra(parcel.readInt(), parcel.readInt(), parcel.readInt(), (ConfidenceLevel[]) parcel.createTypedArray(ConfidenceLevel.CREATOR));
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.id);
            parcel.writeInt(this.recognitionModes);
            parcel.writeInt(this.coarseConfidenceLevel);
            parcel.writeTypedArray(this.confidenceLevels, i);
        }

        public int hashCode() {
            return ((((((Arrays.hashCode(this.confidenceLevels) + 31) * 31) + this.id) * 31) + this.recognitionModes) * 31) + this.coarseConfidenceLevel;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            KeyphraseRecognitionExtra keyphraseRecognitionExtra = (KeyphraseRecognitionExtra) obj;
            return Arrays.equals(this.confidenceLevels, keyphraseRecognitionExtra.confidenceLevels) && this.id == keyphraseRecognitionExtra.id && this.recognitionModes == keyphraseRecognitionExtra.recognitionModes && this.coarseConfidenceLevel == keyphraseRecognitionExtra.coarseConfidenceLevel;
        }

        public String toString() {
            return "KeyphraseRecognitionExtra [id=" + this.id + ", recognitionModes=" + this.recognitionModes + ", coarseConfidenceLevel=" + this.coarseConfidenceLevel + ", confidenceLevels=" + Arrays.toString(this.confidenceLevels) + "]";
        }
    }

    public static class KeyphraseRecognitionEvent extends RecognitionEvent implements Parcelable {
        public static final Parcelable.Creator<KeyphraseRecognitionEvent> CREATOR = new Parcelable.Creator<KeyphraseRecognitionEvent>() { // from class: android.hardware.soundtrigger.SoundTrigger.KeyphraseRecognitionEvent.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public KeyphraseRecognitionEvent createFromParcel(Parcel parcel) {
                return KeyphraseRecognitionEvent.fromParcelForKeyphrase(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public KeyphraseRecognitionEvent[] newArray(int i) {
                return new KeyphraseRecognitionEvent[i];
            }
        };
        public final KeyphraseRecognitionExtra[] keyphraseExtras;

        @Override // android.hardware.soundtrigger.SoundTrigger.RecognitionEvent, android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public KeyphraseRecognitionEvent(int i, int i2, boolean z, int i3, int i4, int i5, boolean z2, AudioFormat audioFormat, byte[] bArr, KeyphraseRecognitionExtra[] keyphraseRecognitionExtraArr) {
            super(i, i2, z, i3, i4, i5, z2, audioFormat, bArr);
            this.keyphraseExtras = keyphraseRecognitionExtraArr;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static KeyphraseRecognitionEvent fromParcelForKeyphrase(Parcel parcel) {
            int i = parcel.readInt();
            int i2 = parcel.readInt();
            boolean z = parcel.readByte() == 1;
            int i3 = parcel.readInt();
            int i4 = parcel.readInt();
            int i5 = parcel.readInt();
            boolean z2 = parcel.readByte() == 1;
            AudioFormat audioFormatBuild = null;
            if (parcel.readByte() == 1) {
                audioFormatBuild = new AudioFormat.Builder().setChannelMask(parcel.readInt()).setEncoding(parcel.readInt()).setSampleRate(parcel.readInt()).build();
            }
            return new KeyphraseRecognitionEvent(i, i2, z, i3, i4, i5, z2, audioFormatBuild, parcel.readBlob(), (KeyphraseRecognitionExtra[]) parcel.createTypedArray(KeyphraseRecognitionExtra.CREATOR));
        }

        @Override // android.hardware.soundtrigger.SoundTrigger.RecognitionEvent, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.status);
            parcel.writeInt(this.soundModelHandle);
            parcel.writeByte(this.captureAvailable ? (byte) 1 : (byte) 0);
            parcel.writeInt(this.captureSession);
            parcel.writeInt(this.captureDelayMs);
            parcel.writeInt(this.capturePreambleMs);
            parcel.writeByte(this.triggerInData ? (byte) 1 : (byte) 0);
            if (this.captureFormat != null) {
                parcel.writeByte((byte) 1);
                parcel.writeInt(this.captureFormat.getSampleRate());
                parcel.writeInt(this.captureFormat.getEncoding());
                parcel.writeInt(this.captureFormat.getChannelMask());
            } else {
                parcel.writeByte((byte) 0);
            }
            parcel.writeBlob(this.data);
            parcel.writeTypedArray(this.keyphraseExtras, i);
        }

        @Override // android.hardware.soundtrigger.SoundTrigger.RecognitionEvent
        public int hashCode() {
            return (super.hashCode() * 31) + Arrays.hashCode(this.keyphraseExtras);
        }

        @Override // android.hardware.soundtrigger.SoundTrigger.RecognitionEvent
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            return super.equals(obj) && getClass() == obj.getClass() && Arrays.equals(this.keyphraseExtras, ((KeyphraseRecognitionEvent) obj).keyphraseExtras);
        }

        @Override // android.hardware.soundtrigger.SoundTrigger.RecognitionEvent
        public String toString() {
            String str;
            String str2;
            StringBuilder sb = new StringBuilder();
            sb.append("KeyphraseRecognitionEvent [keyphraseExtras=");
            sb.append(Arrays.toString(this.keyphraseExtras));
            sb.append(", status=");
            sb.append(this.status);
            sb.append(", soundModelHandle=");
            sb.append(this.soundModelHandle);
            sb.append(", captureAvailable=");
            sb.append(this.captureAvailable);
            sb.append(", captureSession=");
            sb.append(this.captureSession);
            sb.append(", captureDelayMs=");
            sb.append(this.captureDelayMs);
            sb.append(", capturePreambleMs=");
            sb.append(this.capturePreambleMs);
            sb.append(", triggerInData=");
            sb.append(this.triggerInData);
            String str3 = "";
            if (this.captureFormat == null) {
                str = "";
            } else {
                str = ", sampleRate=" + this.captureFormat.getSampleRate();
            }
            sb.append(str);
            if (this.captureFormat == null) {
                str2 = "";
            } else {
                str2 = ", encoding=" + this.captureFormat.getEncoding();
            }
            sb.append(str2);
            if (this.captureFormat != null) {
                str3 = ", channelMask=" + this.captureFormat.getChannelMask();
            }
            sb.append(str3);
            sb.append(", data=");
            sb.append(this.data == null ? 0 : this.data.length);
            sb.append("]");
            return sb.toString();
        }
    }

    public static class GenericRecognitionEvent extends RecognitionEvent implements Parcelable {
        public static final Parcelable.Creator<GenericRecognitionEvent> CREATOR = new Parcelable.Creator<GenericRecognitionEvent>() { // from class: android.hardware.soundtrigger.SoundTrigger.GenericRecognitionEvent.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public GenericRecognitionEvent createFromParcel(Parcel parcel) {
                return GenericRecognitionEvent.fromParcelForGeneric(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public GenericRecognitionEvent[] newArray(int i) {
                return new GenericRecognitionEvent[i];
            }
        };

        public GenericRecognitionEvent(int i, int i2, boolean z, int i3, int i4, int i5, boolean z2, AudioFormat audioFormat, byte[] bArr) {
            super(i, i2, z, i3, i4, i5, z2, audioFormat, bArr);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static GenericRecognitionEvent fromParcelForGeneric(Parcel parcel) {
            RecognitionEvent recognitionEventFromParcel = RecognitionEvent.fromParcel(parcel);
            return new GenericRecognitionEvent(recognitionEventFromParcel.status, recognitionEventFromParcel.soundModelHandle, recognitionEventFromParcel.captureAvailable, recognitionEventFromParcel.captureSession, recognitionEventFromParcel.captureDelayMs, recognitionEventFromParcel.capturePreambleMs, recognitionEventFromParcel.triggerInData, recognitionEventFromParcel.captureFormat, recognitionEventFromParcel.data);
        }

        @Override // android.hardware.soundtrigger.SoundTrigger.RecognitionEvent
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            return super.equals(obj);
        }

        @Override // android.hardware.soundtrigger.SoundTrigger.RecognitionEvent
        public String toString() {
            return "GenericRecognitionEvent ::" + super.toString();
        }
    }

    public static class SoundModelEvent implements Parcelable {
        public static final Parcelable.Creator<SoundModelEvent> CREATOR = new Parcelable.Creator<SoundModelEvent>() { // from class: android.hardware.soundtrigger.SoundTrigger.SoundModelEvent.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SoundModelEvent createFromParcel(Parcel parcel) {
                return SoundModelEvent.fromParcel(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SoundModelEvent[] newArray(int i) {
                return new SoundModelEvent[i];
            }
        };
        public final byte[] data;
        public final int soundModelHandle;
        public final int status;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        SoundModelEvent(int i, int i2, byte[] bArr) {
            this.status = i;
            this.soundModelHandle = i2;
            this.data = bArr;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static SoundModelEvent fromParcel(Parcel parcel) {
            return new SoundModelEvent(parcel.readInt(), parcel.readInt(), parcel.readBlob());
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.status);
            parcel.writeInt(this.soundModelHandle);
            parcel.writeBlob(this.data);
        }

        public int hashCode() {
            return ((((Arrays.hashCode(this.data) + 31) * 31) + this.soundModelHandle) * 31) + this.status;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            SoundModelEvent soundModelEvent = (SoundModelEvent) obj;
            return Arrays.equals(this.data, soundModelEvent.data) && this.soundModelHandle == soundModelEvent.soundModelHandle && this.status == soundModelEvent.status;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("SoundModelEvent [status=");
            sb.append(this.status);
            sb.append(", soundModelHandle=");
            sb.append(this.soundModelHandle);
            sb.append(", data=");
            byte[] bArr = this.data;
            sb.append(bArr == null ? 0 : bArr.length);
            sb.append("]");
            return sb.toString();
        }
    }

    public static SoundTriggerModule attachModule(int i, StatusListener statusListener, Handler handler) {
        if (statusListener == null) {
            return null;
        }
        return new SoundTriggerModule(i, statusListener, handler);
    }
}
