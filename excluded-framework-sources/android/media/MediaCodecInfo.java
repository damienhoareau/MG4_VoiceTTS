package android.media;

import android.bluetooth.BluetoothHealth;
import android.mtp.MtpConstants;
import android.opengl.GLES20;
import android.os.UserHandle;
import android.os.health.HealthKeys;
import android.telephony.NetworkScanRequest;
import android.util.Log;
import android.util.Pair;
import android.util.Range;
import android.util.Rational;
import android.util.Size;
import android.view.SurfaceControl;
import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.Protocol;
import com.iflytek.speech.ISSErrors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public final class MediaCodecInfo {
    private static final int DEFAULT_MAX_SUPPORTED_INSTANCES = 32;
    private static final int ERROR_NONE_SUPPORTED = 4;
    private static final int ERROR_UNRECOGNIZED = 1;
    private static final int ERROR_UNSUPPORTED = 2;
    private static final int MAX_SUPPORTED_INSTANCES_LIMIT = 256;
    private Map<String, CodecCapabilities> mCaps = new HashMap();
    private boolean mIsEncoder;
    private String mName;
    private static final Range<Integer> POSITIVE_INTEGERS = Range.create(1, Integer.MAX_VALUE);
    private static final Range<Long> POSITIVE_LONGS = Range.create(1L, Long.MAX_VALUE);
    private static final Range<Rational> POSITIVE_RATIONALS = Range.create(new Rational(1, Integer.MAX_VALUE), new Rational(Integer.MAX_VALUE, 1));
    private static final Range<Integer> SIZE_RANGE = Range.create(1, 32768);
    private static final Range<Integer> FRAME_RATE_RANGE = Range.create(0, 960);
    private static final Range<Integer> BITRATE_RANGE = Range.create(0, 500000000);

    MediaCodecInfo(String str, boolean z, CodecCapabilities[] codecCapabilitiesArr) {
        this.mName = str;
        this.mIsEncoder = z;
        for (CodecCapabilities codecCapabilities : codecCapabilitiesArr) {
            this.mCaps.put(codecCapabilities.getMimeType(), codecCapabilities);
        }
    }

    public final String getName() {
        return this.mName;
    }

    public final boolean isEncoder() {
        return this.mIsEncoder;
    }

    public final String[] getSupportedTypes() {
        Set<String> setKeySet = this.mCaps.keySet();
        String[] strArr = (String[]) setKeySet.toArray(new String[setKeySet.size()]);
        Arrays.sort(strArr);
        return strArr;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int checkPowerOfTwo(int i, String str) {
        if (((i - 1) & i) == 0) {
            return i;
        }
        throw new IllegalArgumentException(str);
    }

    private static class Feature {
        public boolean mDefault;
        public String mName;
        public int mValue;

        public Feature(String str, int i, boolean z) {
            this.mName = str;
            this.mValue = i;
            this.mDefault = z;
        }
    }

    public static final class CodecCapabilities {
        public static final int COLOR_Format12bitRGB444 = 3;
        public static final int COLOR_Format16bitARGB1555 = 5;
        public static final int COLOR_Format16bitARGB4444 = 4;
        public static final int COLOR_Format16bitBGR565 = 7;
        public static final int COLOR_Format16bitRGB565 = 6;
        public static final int COLOR_Format18BitBGR666 = 41;
        public static final int COLOR_Format18bitARGB1665 = 9;
        public static final int COLOR_Format18bitRGB666 = 8;
        public static final int COLOR_Format19bitARGB1666 = 10;
        public static final int COLOR_Format24BitABGR6666 = 43;
        public static final int COLOR_Format24BitARGB6666 = 42;
        public static final int COLOR_Format24bitARGB1887 = 13;
        public static final int COLOR_Format24bitBGR888 = 12;
        public static final int COLOR_Format24bitRGB888 = 11;
        public static final int COLOR_Format25bitARGB1888 = 14;
        public static final int COLOR_Format32bitABGR8888 = 2130747392;
        public static final int COLOR_Format32bitARGB8888 = 16;
        public static final int COLOR_Format32bitBGRA8888 = 15;
        public static final int COLOR_Format8bitRGB332 = 2;
        public static final int COLOR_FormatCbYCrY = 27;
        public static final int COLOR_FormatCrYCbY = 28;
        public static final int COLOR_FormatL16 = 36;
        public static final int COLOR_FormatL2 = 33;
        public static final int COLOR_FormatL24 = 37;
        public static final int COLOR_FormatL32 = 38;
        public static final int COLOR_FormatL4 = 34;
        public static final int COLOR_FormatL8 = 35;
        public static final int COLOR_FormatMonochrome = 1;
        public static final int COLOR_FormatRGBAFlexible = 2134288520;
        public static final int COLOR_FormatRGBFlexible = 2134292616;
        public static final int COLOR_FormatRawBayer10bit = 31;
        public static final int COLOR_FormatRawBayer8bit = 30;
        public static final int COLOR_FormatRawBayer8bitcompressed = 32;
        public static final int COLOR_FormatSurface = 2130708361;
        public static final int COLOR_FormatYCbYCr = 25;
        public static final int COLOR_FormatYCrYCb = 26;
        public static final int COLOR_FormatYUV411PackedPlanar = 18;
        public static final int COLOR_FormatYUV411Planar = 17;
        public static final int COLOR_FormatYUV420Flexible = 2135033992;
        public static final int COLOR_FormatYUV420PackedPlanar = 20;
        public static final int COLOR_FormatYUV420PackedSemiPlanar = 39;
        public static final int COLOR_FormatYUV420Planar = 19;
        public static final int COLOR_FormatYUV420SemiPlanar = 21;
        public static final int COLOR_FormatYUV422Flexible = 2135042184;
        public static final int COLOR_FormatYUV422PackedPlanar = 23;
        public static final int COLOR_FormatYUV422PackedSemiPlanar = 40;
        public static final int COLOR_FormatYUV422Planar = 22;
        public static final int COLOR_FormatYUV422SemiPlanar = 24;
        public static final int COLOR_FormatYUV444Flexible = 2135181448;
        public static final int COLOR_FormatYUV444Interleaved = 29;
        public static final int COLOR_QCOM_FormatYUV420SemiPlanar = 2141391872;
        public static final int COLOR_TI_FormatYUV420PackedSemiPlanar = 2130706688;
        private static final String TAG = "CodecCapabilities";
        public int[] colorFormats;
        private AudioCapabilities mAudioCaps;
        private MediaFormat mCapabilitiesInfo;
        private MediaFormat mDefaultFormat;
        private EncoderCapabilities mEncoderCaps;
        int mError;
        private int mFlagsRequired;
        private int mFlagsSupported;
        private int mFlagsVerified;
        private int mMaxSupportedInstances;
        private String mMime;
        private VideoCapabilities mVideoCaps;
        public CodecProfileLevel[] profileLevels;
        public static final String FEATURE_AdaptivePlayback = "adaptive-playback";
        public static final String FEATURE_SecurePlayback = "secure-playback";
        public static final String FEATURE_TunneledPlayback = "tunneled-playback";
        public static final String FEATURE_PartialFrame = "partial-frame";
        private static final Feature[] decoderFeatures = {new Feature(FEATURE_AdaptivePlayback, 1, true), new Feature(FEATURE_SecurePlayback, 2, false), new Feature(FEATURE_TunneledPlayback, 4, false), new Feature(FEATURE_PartialFrame, 8, false)};
        public static final String FEATURE_IntraRefresh = "intra-refresh";
        private static final Feature[] encoderFeatures = {new Feature(FEATURE_IntraRefresh, 1, false)};

        public CodecCapabilities() {
        }

        public final boolean isFeatureSupported(String str) {
            return checkFeature(str, this.mFlagsSupported);
        }

        public final boolean isFeatureRequired(String str) {
            return checkFeature(str, this.mFlagsRequired);
        }

        public String[] validFeatures() {
            Feature[] validFeatures = getValidFeatures();
            int length = validFeatures.length;
            String[] strArr = new String[length];
            for (int i = 0; i < length; i++) {
                strArr[i] = validFeatures[i].mName;
            }
            return strArr;
        }

        private Feature[] getValidFeatures() {
            if (!isEncoder()) {
                return decoderFeatures;
            }
            return encoderFeatures;
        }

        private boolean checkFeature(String str, int i) {
            for (Feature feature : getValidFeatures()) {
                if (feature.mName.equals(str)) {
                    return (feature.mValue & i) != 0;
                }
            }
            return false;
        }

        public boolean isRegular() {
            for (Feature feature : getValidFeatures()) {
                if (!feature.mDefault && isFeatureRequired(feature.mName)) {
                    return false;
                }
            }
            return true;
        }

        public final boolean isFormatSupported(MediaFormat mediaFormat) {
            Map<String, Object> map = mediaFormat.getMap();
            String str = (String) map.get(MediaFormat.KEY_MIME);
            if (str != null && !this.mMime.equalsIgnoreCase(str)) {
                return false;
            }
            for (Feature feature : getValidFeatures()) {
                Integer num = (Integer) map.get(MediaFormat.KEY_FEATURE_ + feature.mName);
                if (num != null && ((num.intValue() == 1 && !isFeatureSupported(feature.mName)) || (num.intValue() == 0 && isFeatureRequired(feature.mName)))) {
                    return false;
                }
            }
            Integer num2 = (Integer) map.get(MediaFormat.KEY_PROFILE);
            Integer num3 = (Integer) map.get("level");
            if (num2 != null) {
                if (!supportsProfileLevel(num2.intValue(), num3)) {
                    return false;
                }
                int i = 0;
                for (CodecProfileLevel codecProfileLevel : this.profileLevels) {
                    if (codecProfileLevel.profile == num2.intValue() && codecProfileLevel.level > i) {
                        i = codecProfileLevel.level;
                    }
                }
                CodecCapabilities codecCapabilitiesCreateFromProfileLevel = createFromProfileLevel(this.mMime, num2.intValue(), i);
                HashMap map2 = new HashMap(map);
                map2.remove(MediaFormat.KEY_PROFILE);
                MediaFormat mediaFormat2 = new MediaFormat(map2);
                if (codecCapabilitiesCreateFromProfileLevel != null && !codecCapabilitiesCreateFromProfileLevel.isFormatSupported(mediaFormat2)) {
                    return false;
                }
            }
            AudioCapabilities audioCapabilities = this.mAudioCaps;
            if (audioCapabilities != null && !audioCapabilities.supportsFormat(mediaFormat)) {
                return false;
            }
            VideoCapabilities videoCapabilities = this.mVideoCaps;
            if (videoCapabilities != null && !videoCapabilities.supportsFormat(mediaFormat)) {
                return false;
            }
            EncoderCapabilities encoderCapabilities = this.mEncoderCaps;
            return encoderCapabilities == null || encoderCapabilities.supportsFormat(mediaFormat);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static boolean supportsBitrate(Range<Integer> range, MediaFormat mediaFormat) {
            Map<String, Object> map = mediaFormat.getMap();
            Integer numValueOf = (Integer) map.get(MediaFormat.KEY_MAX_BIT_RATE);
            Integer num = (Integer) map.get(MediaFormat.KEY_BIT_RATE);
            if (num != null) {
                numValueOf = numValueOf != null ? Integer.valueOf(Math.max(num.intValue(), numValueOf.intValue())) : num;
            }
            if (numValueOf == null || numValueOf.intValue() <= 0) {
                return true;
            }
            return range.contains(numValueOf);
        }

        /* JADX WARN: Code duplicated, block: B:43:0x0083  */
        /* JADX WARN: Code duplicated, block: B:58:0x008b A[SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:59:0x00a4 A[SYNTHETIC] */
        private boolean supportsProfileLevel(int i, Integer num) {
            for (CodecProfileLevel codecProfileLevel : this.profileLevels) {
                if (codecProfileLevel.profile == i) {
                    if (num == null || this.mMime.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_AAC)) {
                        return true;
                    }
                    if ((!this.mMime.equalsIgnoreCase(MediaFormat.MIMETYPE_VIDEO_H263) || codecProfileLevel.level == num.intValue() || codecProfileLevel.level != 16 || num.intValue() <= 1) && (!this.mMime.equalsIgnoreCase(MediaFormat.MIMETYPE_VIDEO_MPEG4) || codecProfileLevel.level == num.intValue() || codecProfileLevel.level != 4 || num.intValue() <= 1)) {
                        if (this.mMime.equalsIgnoreCase(MediaFormat.MIMETYPE_VIDEO_HEVC)) {
                            boolean z = (codecProfileLevel.level & 44739242) != 0;
                            if (!((44739242 & num.intValue()) != 0) || z) {
                                if (codecProfileLevel.level >= num.intValue()) {
                                    if (createFromProfileLevel(this.mMime, i, codecProfileLevel.level) == null) {
                                        return true;
                                    }
                                }
                            }
                        } else if (codecProfileLevel.level >= num.intValue()) {
                            return createFromProfileLevel(this.mMime, i, codecProfileLevel.level) == null || createFromProfileLevel(this.mMime, i, num.intValue()) != null;
                        }
                    }
                }
            }
            return false;
        }

        public MediaFormat getDefaultFormat() {
            return this.mDefaultFormat;
        }

        public String getMimeType() {
            return this.mMime;
        }

        public int getMaxSupportedInstances() {
            return this.mMaxSupportedInstances;
        }

        private boolean isAudio() {
            return this.mAudioCaps != null;
        }

        public AudioCapabilities getAudioCapabilities() {
            return this.mAudioCaps;
        }

        private boolean isEncoder() {
            return this.mEncoderCaps != null;
        }

        public EncoderCapabilities getEncoderCapabilities() {
            return this.mEncoderCaps;
        }

        private boolean isVideo() {
            return this.mVideoCaps != null;
        }

        public VideoCapabilities getVideoCapabilities() {
            return this.mVideoCaps;
        }

        public CodecCapabilities dup() {
            CodecCapabilities codecCapabilities = new CodecCapabilities();
            CodecProfileLevel[] codecProfileLevelArr = this.profileLevels;
            codecCapabilities.profileLevels = (CodecProfileLevel[]) Arrays.copyOf(codecProfileLevelArr, codecProfileLevelArr.length);
            int[] iArr = this.colorFormats;
            codecCapabilities.colorFormats = Arrays.copyOf(iArr, iArr.length);
            codecCapabilities.mMime = this.mMime;
            codecCapabilities.mMaxSupportedInstances = this.mMaxSupportedInstances;
            codecCapabilities.mFlagsRequired = this.mFlagsRequired;
            codecCapabilities.mFlagsSupported = this.mFlagsSupported;
            codecCapabilities.mFlagsVerified = this.mFlagsVerified;
            codecCapabilities.mAudioCaps = this.mAudioCaps;
            codecCapabilities.mVideoCaps = this.mVideoCaps;
            codecCapabilities.mEncoderCaps = this.mEncoderCaps;
            codecCapabilities.mDefaultFormat = this.mDefaultFormat;
            codecCapabilities.mCapabilitiesInfo = this.mCapabilitiesInfo;
            return codecCapabilities;
        }

        public static CodecCapabilities createFromProfileLevel(String str, int i, int i2) {
            CodecProfileLevel codecProfileLevel = new CodecProfileLevel();
            codecProfileLevel.profile = i;
            codecProfileLevel.level = i2;
            MediaFormat mediaFormat = new MediaFormat();
            mediaFormat.setString(MediaFormat.KEY_MIME, str);
            CodecCapabilities codecCapabilities = new CodecCapabilities(new CodecProfileLevel[]{codecProfileLevel}, new int[0], true, 0, mediaFormat, new MediaFormat());
            if (codecCapabilities.mError != 0) {
                return null;
            }
            return codecCapabilities;
        }

        CodecCapabilities(CodecProfileLevel[] codecProfileLevelArr, int[] iArr, boolean z, int i, Map<String, Object> map, Map<String, Object> map2) {
            this(codecProfileLevelArr, iArr, z, i, new MediaFormat(map), new MediaFormat(map2));
        }

        CodecCapabilities(CodecProfileLevel[] codecProfileLevelArr, int[] iArr, boolean z, int i, MediaFormat mediaFormat, MediaFormat mediaFormat2) {
            Map<String, Object> map = mediaFormat2.getMap();
            this.colorFormats = iArr;
            this.mFlagsVerified = i;
            this.mDefaultFormat = mediaFormat;
            this.mCapabilitiesInfo = mediaFormat2;
            String string = mediaFormat.getString(MediaFormat.KEY_MIME);
            this.mMime = string;
            if (codecProfileLevelArr.length == 0 && string.equalsIgnoreCase(MediaFormat.MIMETYPE_VIDEO_VP9)) {
                CodecProfileLevel codecProfileLevel = new CodecProfileLevel();
                codecProfileLevel.profile = 1;
                codecProfileLevel.level = VideoCapabilities.equivalentVP9Level(mediaFormat2);
                codecProfileLevelArr = new CodecProfileLevel[]{codecProfileLevel};
            }
            this.profileLevels = codecProfileLevelArr;
            if (this.mMime.toLowerCase().startsWith("audio/")) {
                AudioCapabilities audioCapabilitiesCreate = AudioCapabilities.create(mediaFormat2, this);
                this.mAudioCaps = audioCapabilitiesCreate;
                audioCapabilitiesCreate.getDefaultFormat(this.mDefaultFormat);
            } else if (this.mMime.toLowerCase().startsWith("video/") || this.mMime.equalsIgnoreCase(MediaFormat.MIMETYPE_IMAGE_ANDROID_HEIC)) {
                this.mVideoCaps = VideoCapabilities.create(mediaFormat2, this);
            }
            if (z) {
                EncoderCapabilities encoderCapabilitiesCreate = EncoderCapabilities.create(mediaFormat2, this);
                this.mEncoderCaps = encoderCapabilitiesCreate;
                encoderCapabilitiesCreate.getDefaultFormat(this.mDefaultFormat);
            }
            this.mMaxSupportedInstances = Utils.parseIntSafely(MediaCodecList.getGlobalSettings().get("max-concurrent-instances"), 32);
            this.mMaxSupportedInstances = ((Integer) Range.create(1, 256).clamp(Integer.valueOf(Utils.parseIntSafely(map.get("max-concurrent-instances"), this.mMaxSupportedInstances)))).intValue();
            for (Feature feature : getValidFeatures()) {
                String str = MediaFormat.KEY_FEATURE_ + feature.mName;
                Integer num = (Integer) map.get(str);
                if (num != null) {
                    if (num.intValue() > 0) {
                        this.mFlagsRequired |= feature.mValue;
                    }
                    this.mFlagsSupported = feature.mValue | this.mFlagsSupported;
                    this.mDefaultFormat.setInteger(str, 1);
                }
            }
        }
    }

    public static final class AudioCapabilities {
        private static final int MAX_INPUT_CHANNEL_COUNT = 30;
        private static final String TAG = "AudioCapabilities";
        private Range<Integer> mBitrateRange;
        private int mMaxInputChannelCount;
        private CodecCapabilities mParent;
        private Range<Integer>[] mSampleRateRanges;
        private int[] mSampleRates;

        public Range<Integer> getBitrateRange() {
            return this.mBitrateRange;
        }

        public int[] getSupportedSampleRates() {
            int[] iArr = this.mSampleRates;
            return Arrays.copyOf(iArr, iArr.length);
        }

        public Range<Integer>[] getSupportedSampleRateRanges() {
            Range<Integer>[] rangeArr = this.mSampleRateRanges;
            return (Range[]) Arrays.copyOf(rangeArr, rangeArr.length);
        }

        public int getMaxInputChannelCount() {
            return this.mMaxInputChannelCount;
        }

        private AudioCapabilities() {
        }

        public static AudioCapabilities create(MediaFormat mediaFormat, CodecCapabilities codecCapabilities) {
            AudioCapabilities audioCapabilities = new AudioCapabilities();
            audioCapabilities.init(mediaFormat, codecCapabilities);
            return audioCapabilities;
        }

        private void init(MediaFormat mediaFormat, CodecCapabilities codecCapabilities) {
            this.mParent = codecCapabilities;
            initWithPlatformLimits();
            applyLevelLimits();
            parseFromInfo(mediaFormat);
        }

        private void initWithPlatformLimits() {
            this.mBitrateRange = Range.create(0, Integer.MAX_VALUE);
            this.mMaxInputChannelCount = 30;
            this.mSampleRateRanges = new Range[]{Range.create(8000, 96000)};
            this.mSampleRates = null;
        }

        private boolean supports(Integer num, Integer num2) {
            if (num2 == null || (num2.intValue() >= 1 && num2.intValue() <= this.mMaxInputChannelCount)) {
                return num == null || Utils.binarySearchDistinctRanges(this.mSampleRateRanges, num) >= 0;
            }
            return false;
        }

        public boolean isSampleRateSupported(int i) {
            return supports(Integer.valueOf(i), null);
        }

        private void limitSampleRates(int[] iArr) {
            Arrays.sort(iArr);
            ArrayList arrayList = new ArrayList();
            for (int i : iArr) {
                if (supports(Integer.valueOf(i), null)) {
                    arrayList.add(Range.create(Integer.valueOf(i), Integer.valueOf(i)));
                }
            }
            this.mSampleRateRanges = (Range[]) arrayList.toArray(new Range[arrayList.size()]);
            createDiscreteSampleRates();
        }

        private void createDiscreteSampleRates() {
            this.mSampleRates = new int[this.mSampleRateRanges.length];
            int i = 0;
            while (true) {
                Range<Integer>[] rangeArr = this.mSampleRateRanges;
                if (i >= rangeArr.length) {
                    return;
                }
                this.mSampleRates[i] = ((Integer) rangeArr[i].getLower()).intValue();
                i++;
            }
        }

        private void limitSampleRates(Range<Integer>[] rangeArr) {
            Utils.sortDistinctRanges(rangeArr);
            Range<Integer>[] rangeArrIntersectSortedDistinctRanges = Utils.intersectSortedDistinctRanges(this.mSampleRateRanges, rangeArr);
            this.mSampleRateRanges = rangeArrIntersectSortedDistinctRanges;
            for (Range<Integer> range : rangeArrIntersectSortedDistinctRanges) {
                if (!((Integer) range.getLower()).equals(range.getUpper())) {
                    this.mSampleRates = null;
                    return;
                }
            }
            createDiscreteSampleRates();
        }

        /* JADX WARN: Code duplicated, block: B:47:0x0193  */
        /* JADX WARN: Code duplicated, block: B:48:0x0197 A[DONT_INVERT] */
        /* JADX WARN: Code duplicated, block: B:49:0x0199  */
        private void applyLevelLimits() {
            int[] iArr;
            Range<Integer> rangeCreate;
            Range<Integer> rangeCreate2;
            Range<Integer> rangeCreate3;
            String mimeType = this.mParent.getMimeType();
            int i = 2;
            int[] iArr2 = null;
            if (mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_MPEG)) {
                iArr = new int[]{8000, 11025, ISSErrors.ISS_ERROR_HTTP_BASE, 16000, 22050, 24000, 32000, 44100, 48000};
                rangeCreate = Range.create(8000, 320000);
            } else {
                if (mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_AMR_NB)) {
                    iArr = new int[]{8000};
                    rangeCreate = Range.create(4750, 12200);
                } else if (mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_AMR_WB)) {
                    iArr = new int[]{16000};
                    rangeCreate = Range.create(6600, 23850);
                } else if (mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_AAC)) {
                    iArr = new int[]{7350, 8000, 11025, ISSErrors.ISS_ERROR_HTTP_BASE, 16000, 22050, 24000, 32000, 44100, 48000, 64000, 88200, 96000};
                    rangeCreate = Range.create(8000, 510000);
                    i = 48;
                } else {
                    if (mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_VORBIS)) {
                        Range<Integer> rangeCreate4 = Range.create(32000, 500000);
                        i = 255;
                        rangeCreate3 = Range.create(8000, Integer.valueOf(AudioFormat.SAMPLE_RATE_HZ_MAX));
                        rangeCreate = rangeCreate4;
                        rangeCreate2 = rangeCreate3;
                    } else if (mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_OPUS)) {
                        i = 255;
                        rangeCreate = Range.create(Integer.valueOf(BluetoothHealth.HEALTH_OPERATION_SUCCESS), 510000);
                        rangeCreate2 = null;
                        iArr2 = new int[]{8000, ISSErrors.ISS_ERROR_HTTP_BASE, 16000, 24000, 48000};
                    } else if (mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_RAW)) {
                        rangeCreate2 = Range.create(1, 96000);
                        rangeCreate = Range.create(1, 10000000);
                        i = AudioTrack.CHANNEL_COUNT_MAX;
                    } else if (mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_FLAC)) {
                        rangeCreate2 = Range.create(1, 655350);
                        i = 255;
                        rangeCreate = null;
                    } else if (mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_G711_ALAW) || mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_G711_MLAW)) {
                        iArr = new int[]{8000};
                        rangeCreate = Range.create(64000, 64000);
                        i = 30;
                    } else if (mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_MSGSM)) {
                        iArr = new int[]{8000};
                        rangeCreate = Range.create(Integer.valueOf(ISSErrors.ISS_ERROR_ISV_NO_USER), Integer.valueOf(ISSErrors.ISS_ERROR_ISV_NO_USER));
                    } else {
                        if (mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_AC3)) {
                            i = 6;
                        } else if (mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_EAC3)) {
                            i = 16;
                        } else {
                            Log.w(TAG, "Unsupported mime " + mimeType);
                            CodecCapabilities codecCapabilities = this.mParent;
                            codecCapabilities.mError = codecCapabilities.mError | 2;
                            rangeCreate2 = null;
                            rangeCreate = null;
                            i = 30;
                        }
                        rangeCreate2 = null;
                        rangeCreate = null;
                    }
                    if (iArr2 != null) {
                        limitSampleRates(iArr2);
                    } else if (rangeCreate2 != null) {
                        limitSampleRates(new Range[]{rangeCreate2});
                    }
                    applyLimits(i, rangeCreate);
                }
                i = 1;
            }
            rangeCreate3 = null;
            iArr2 = iArr;
            rangeCreate2 = rangeCreate3;
            if (iArr2 != null) {
                limitSampleRates(iArr2);
            } else if (rangeCreate2 != null) {
                limitSampleRates(new Range[]{rangeCreate2});
            }
            applyLimits(i, rangeCreate);
        }

        private void applyLimits(int i, Range<Integer> range) {
            this.mMaxInputChannelCount = ((Integer) Range.create(1, Integer.valueOf(this.mMaxInputChannelCount)).clamp(Integer.valueOf(i))).intValue();
            if (range != null) {
                this.mBitrateRange = this.mBitrateRange.intersect(range);
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        private void parseFromInfo(MediaFormat mediaFormat) {
            Range rangeIntersect = MediaCodecInfo.POSITIVE_INTEGERS;
            int intSafely = 0;
            if (mediaFormat.containsKey("sample-rate-ranges")) {
                String[] strArrSplit = mediaFormat.getString("sample-rate-ranges").split(",");
                Range[] rangeArr = new Range[strArrSplit.length];
                for (int i = 0; i < strArrSplit.length; i++) {
                    rangeArr[i] = Utils.parseIntRange(strArrSplit[i], null);
                }
                limitSampleRates((Range<Integer>[]) rangeArr);
            }
            if (mediaFormat.containsKey("max-channel-count")) {
                intSafely = Utils.parseIntSafely(mediaFormat.getString("max-channel-count"), 30);
            } else if ((this.mParent.mError & 2) == 0) {
                intSafely = 30;
            }
            if (mediaFormat.containsKey("bitrate-range")) {
                rangeIntersect = rangeIntersect.intersect(Utils.parseIntRange(mediaFormat.getString("bitrate-range"), rangeIntersect));
            }
            applyLimits(intSafely, rangeIntersect);
        }

        public void getDefaultFormat(MediaFormat mediaFormat) {
            if (((Integer) this.mBitrateRange.getLower()).equals(this.mBitrateRange.getUpper())) {
                mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, ((Integer) this.mBitrateRange.getLower()).intValue());
            }
            if (this.mMaxInputChannelCount == 1) {
                mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
            }
            int[] iArr = this.mSampleRates;
            if (iArr == null || iArr.length != 1) {
                return;
            }
            mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, iArr[0]);
        }

        public boolean supportsFormat(MediaFormat mediaFormat) {
            Map<String, Object> map = mediaFormat.getMap();
            return supports((Integer) map.get(MediaFormat.KEY_SAMPLE_RATE), (Integer) map.get(MediaFormat.KEY_CHANNEL_COUNT)) && CodecCapabilities.supportsBitrate(this.mBitrateRange, mediaFormat);
        }
    }

    public static final class VideoCapabilities {
        private static final String TAG = "VideoCapabilities";
        private boolean mAllowMbOverride;
        private Range<Rational> mAspectRatioRange;
        private Range<Integer> mBitrateRange;
        private Range<Rational> mBlockAspectRatioRange;
        private Range<Integer> mBlockCountRange;
        private int mBlockHeight;
        private int mBlockWidth;
        private Range<Long> mBlocksPerSecondRange;
        private Range<Integer> mFrameRateRange;
        private int mHeightAlignment;
        private Range<Integer> mHeightRange;
        private Range<Integer> mHorizontalBlockRange;
        private Map<Size, Range<Long>> mMeasuredFrameRates;
        private CodecCapabilities mParent;
        private int mSmallerDimensionUpperLimit;
        private Range<Integer> mVerticalBlockRange;
        private int mWidthAlignment;
        private Range<Integer> mWidthRange;

        public Range<Integer> getBitrateRange() {
            return this.mBitrateRange;
        }

        public Range<Integer> getSupportedWidths() {
            return this.mWidthRange;
        }

        public Range<Integer> getSupportedHeights() {
            return this.mHeightRange;
        }

        public int getWidthAlignment() {
            return this.mWidthAlignment;
        }

        public int getHeightAlignment() {
            return this.mHeightAlignment;
        }

        public int getSmallerDimensionUpperLimit() {
            return this.mSmallerDimensionUpperLimit;
        }

        public Range<Integer> getSupportedFrameRates() {
            return this.mFrameRateRange;
        }

        public Range<Integer> getSupportedWidthsFor(int i) {
            try {
                Range<Integer> range = this.mWidthRange;
                if (!this.mHeightRange.contains(Integer.valueOf(i)) || i % this.mHeightAlignment != 0) {
                    throw new IllegalArgumentException("unsupported height");
                }
                int iDivUp = Utils.divUp(i, this.mBlockHeight);
                double d = iDivUp;
                Range rangeIntersect = range.intersect(Integer.valueOf(((Math.max(Utils.divUp(((Integer) this.mBlockCountRange.getLower()).intValue(), iDivUp), (int) Math.ceil(((Rational) this.mBlockAspectRatioRange.getLower()).doubleValue() * d)) - 1) * this.mBlockWidth) + this.mWidthAlignment), Integer.valueOf(Math.min(((Integer) this.mBlockCountRange.getUpper()).intValue() / iDivUp, (int) (((Rational) this.mBlockAspectRatioRange.getUpper()).doubleValue() * d)) * this.mBlockWidth));
                if (i > this.mSmallerDimensionUpperLimit) {
                    rangeIntersect = rangeIntersect.intersect(1, Integer.valueOf(this.mSmallerDimensionUpperLimit));
                }
                double d2 = i;
                return rangeIntersect.intersect(Integer.valueOf((int) Math.ceil(((Rational) this.mAspectRatioRange.getLower()).doubleValue() * d2)), Integer.valueOf((int) (((Rational) this.mAspectRatioRange.getUpper()).doubleValue() * d2)));
            } catch (IllegalArgumentException unused) {
                Log.v(TAG, "could not get supported widths for " + i);
                throw new IllegalArgumentException("unsupported height");
            }
        }

        public Range<Integer> getSupportedHeightsFor(int i) {
            try {
                Range<Integer> range = this.mHeightRange;
                if (!this.mWidthRange.contains(Integer.valueOf(i)) || i % this.mWidthAlignment != 0) {
                    throw new IllegalArgumentException("unsupported width");
                }
                int iDivUp = Utils.divUp(i, this.mBlockWidth);
                double d = iDivUp;
                Range rangeIntersect = range.intersect(Integer.valueOf(((Math.max(Utils.divUp(((Integer) this.mBlockCountRange.getLower()).intValue(), iDivUp), (int) Math.ceil(d / ((Rational) this.mBlockAspectRatioRange.getUpper()).doubleValue())) - 1) * this.mBlockHeight) + this.mHeightAlignment), Integer.valueOf(Math.min(((Integer) this.mBlockCountRange.getUpper()).intValue() / iDivUp, (int) (d / ((Rational) this.mBlockAspectRatioRange.getLower()).doubleValue())) * this.mBlockHeight));
                if (i > this.mSmallerDimensionUpperLimit) {
                    rangeIntersect = rangeIntersect.intersect(1, Integer.valueOf(this.mSmallerDimensionUpperLimit));
                }
                double d2 = i;
                return rangeIntersect.intersect(Integer.valueOf((int) Math.ceil(d2 / ((Rational) this.mAspectRatioRange.getUpper()).doubleValue())), Integer.valueOf((int) (d2 / ((Rational) this.mAspectRatioRange.getLower()).doubleValue())));
            } catch (IllegalArgumentException unused) {
                Log.v(TAG, "could not get supported heights for " + i);
                throw new IllegalArgumentException("unsupported width");
            }
        }

        public Range<Double> getSupportedFrameRatesFor(int i, int i2) {
            if (!supports(Integer.valueOf(i), Integer.valueOf(i2), null)) {
                throw new IllegalArgumentException("unsupported size");
            }
            double dDivUp = Utils.divUp(i, this.mBlockWidth) * Utils.divUp(i2, this.mBlockHeight);
            return Range.create(Double.valueOf(Math.max(((Long) this.mBlocksPerSecondRange.getLower()).longValue() / dDivUp, ((Integer) this.mFrameRateRange.getLower()).intValue())), Double.valueOf(Math.min(((Long) this.mBlocksPerSecondRange.getUpper()).longValue() / dDivUp, ((Integer) this.mFrameRateRange.getUpper()).intValue())));
        }

        private int getBlockCount(int i, int i2) {
            return Utils.divUp(i, this.mBlockWidth) * Utils.divUp(i2, this.mBlockHeight);
        }

        private Size findClosestSize(int i, int i2) {
            int blockCount = getBlockCount(i, i2);
            Size size = null;
            int i3 = Integer.MAX_VALUE;
            for (Size size2 : this.mMeasuredFrameRates.keySet()) {
                int iAbs = Math.abs(blockCount - getBlockCount(size2.getWidth(), size2.getHeight()));
                if (iAbs < i3) {
                    size = size2;
                    i3 = iAbs;
                }
            }
            return size;
        }

        private Range<Double> estimateFrameRatesFor(int i, int i2) {
            Size sizeFindClosestSize = findClosestSize(i, i2);
            Range<Long> range = this.mMeasuredFrameRates.get(sizeFindClosestSize);
            Double dValueOf = Double.valueOf(((double) getBlockCount(sizeFindClosestSize.getWidth(), sizeFindClosestSize.getHeight())) / ((double) Math.max(getBlockCount(i, i2), 1)));
            return Range.create(Double.valueOf(((Long) range.getLower()).longValue() * dValueOf.doubleValue()), Double.valueOf(((Long) range.getUpper()).longValue() * dValueOf.doubleValue()));
        }

        public Range<Double> getAchievableFrameRatesFor(int i, int i2) {
            if (!supports(Integer.valueOf(i), Integer.valueOf(i2), null)) {
                throw new IllegalArgumentException("unsupported size");
            }
            Map<Size, Range<Long>> map = this.mMeasuredFrameRates;
            if (map == null || map.size() <= 0) {
                Log.w(TAG, "Codec did not publish any measurement data.");
                return null;
            }
            return estimateFrameRatesFor(i, i2);
        }

        public boolean areSizeAndRateSupported(int i, int i2, double d) {
            return supports(Integer.valueOf(i), Integer.valueOf(i2), Double.valueOf(d));
        }

        public boolean isSizeSupported(int i, int i2) {
            return supports(Integer.valueOf(i), Integer.valueOf(i2), null);
        }

        private boolean supports(Integer num, Integer num2, Number number) {
            boolean z = false;
            boolean zContains = num == null || (this.mWidthRange.contains(num) && num.intValue() % this.mWidthAlignment == 0);
            if (zContains && num2 != null) {
                zContains = this.mHeightRange.contains(num2) && num2.intValue() % this.mHeightAlignment == 0;
            }
            if (zContains && number != null) {
                zContains = this.mFrameRateRange.contains(Utils.intRangeFor(number.doubleValue()));
            }
            if (!zContains || num2 == null || num == null) {
                return zContains;
            }
            boolean z2 = Math.min(num2.intValue(), num.intValue()) <= this.mSmallerDimensionUpperLimit;
            int iDivUp = Utils.divUp(num.intValue(), this.mBlockWidth);
            int iDivUp2 = Utils.divUp(num2.intValue(), this.mBlockHeight);
            int i = iDivUp * iDivUp2;
            if (z2 && this.mBlockCountRange.contains(Integer.valueOf(i)) && this.mBlockAspectRatioRange.contains(new Rational(iDivUp, iDivUp2)) && this.mAspectRatioRange.contains(new Rational(num.intValue(), num2.intValue()))) {
                z = true;
            }
            if (!z || number == null) {
                return z;
            }
            return this.mBlocksPerSecondRange.contains(Utils.longRangeFor(((double) i) * number.doubleValue()));
        }

        public boolean supportsFormat(MediaFormat mediaFormat) {
            Map<String, Object> map = mediaFormat.getMap();
            return supports((Integer) map.get("width"), (Integer) map.get("height"), (Number) map.get(MediaFormat.KEY_FRAME_RATE)) && CodecCapabilities.supportsBitrate(this.mBitrateRange, mediaFormat);
        }

        private VideoCapabilities() {
        }

        public static VideoCapabilities create(MediaFormat mediaFormat, CodecCapabilities codecCapabilities) {
            VideoCapabilities videoCapabilities = new VideoCapabilities();
            videoCapabilities.init(mediaFormat, codecCapabilities);
            return videoCapabilities;
        }

        private void init(MediaFormat mediaFormat, CodecCapabilities codecCapabilities) {
            this.mParent = codecCapabilities;
            initWithPlatformLimits();
            applyLevelLimits();
            parseFromInfo(mediaFormat);
            updateLimits();
        }

        public Size getBlockSize() {
            return new Size(this.mBlockWidth, this.mBlockHeight);
        }

        public Range<Integer> getBlockCountRange() {
            return this.mBlockCountRange;
        }

        public Range<Long> getBlocksPerSecondRange() {
            return this.mBlocksPerSecondRange;
        }

        public Range<Rational> getAspectRatioRange(boolean z) {
            return z ? this.mBlockAspectRatioRange : this.mAspectRatioRange;
        }

        private void initWithPlatformLimits() {
            this.mBitrateRange = MediaCodecInfo.BITRATE_RANGE;
            this.mWidthRange = MediaCodecInfo.SIZE_RANGE;
            this.mHeightRange = MediaCodecInfo.SIZE_RANGE;
            this.mFrameRateRange = MediaCodecInfo.FRAME_RATE_RANGE;
            this.mHorizontalBlockRange = MediaCodecInfo.SIZE_RANGE;
            this.mVerticalBlockRange = MediaCodecInfo.SIZE_RANGE;
            this.mBlockCountRange = MediaCodecInfo.POSITIVE_INTEGERS;
            this.mBlocksPerSecondRange = MediaCodecInfo.POSITIVE_LONGS;
            this.mBlockAspectRatioRange = MediaCodecInfo.POSITIVE_RATIONALS;
            this.mAspectRatioRange = MediaCodecInfo.POSITIVE_RATIONALS;
            this.mWidthAlignment = 2;
            this.mHeightAlignment = 2;
            this.mBlockWidth = 2;
            this.mBlockHeight = 2;
            this.mSmallerDimensionUpperLimit = ((Integer) MediaCodecInfo.SIZE_RANGE.getUpper()).intValue();
        }

        private Map<Size, Range<Long>> getMeasuredFrameRates(Map<String, Object> map) {
            Size size;
            Range<Long> longRange;
            HashMap map2 = new HashMap();
            for (String str : map.keySet()) {
                if (str.startsWith("measured-frame-rate-")) {
                    str.substring(20);
                    String[] strArrSplit = str.split(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                    if (strArrSplit.length == 5 && (size = Utils.parseSize(strArrSplit[3], null)) != null && size.getWidth() * size.getHeight() > 0 && (longRange = Utils.parseLongRange(map.get(str), null)) != null && ((Long) longRange.getLower()).longValue() >= 0 && ((Long) longRange.getUpper()).longValue() >= 0) {
                        map2.put(size, longRange);
                    }
                }
            }
            return map2;
        }

        private static Pair<Range<Integer>, Range<Integer>> parseWidthHeightRanges(Object obj) {
            Pair<Size, Size> sizeRange = Utils.parseSizeRange(obj);
            if (sizeRange == null) {
                return null;
            }
            try {
                return Pair.create(Range.create(Integer.valueOf(sizeRange.first.getWidth()), Integer.valueOf(sizeRange.second.getWidth())), Range.create(Integer.valueOf(sizeRange.first.getHeight()), Integer.valueOf(sizeRange.second.getHeight())));
            } catch (IllegalArgumentException unused) {
                Log.w(TAG, "could not parse size range '" + obj + "'");
                return null;
            }
        }

        public static int equivalentVP9Level(MediaFormat mediaFormat) {
            Map<String, Object> map = mediaFormat.getMap();
            Size size = Utils.parseSize(map.get("block-size"), new Size(8, 8));
            int width = size.getWidth() * size.getHeight();
            Range<Integer> intRange = Utils.parseIntRange(map.get("block-count-range"), null);
            int iIntValue = intRange == null ? 0 : ((Integer) intRange.getUpper()).intValue() * width;
            Range<Long> longRange = Utils.parseLongRange(map.get("blocks-per-second-range"), null);
            long jLongValue = longRange == null ? 0L : ((long) width) * ((Long) longRange.getUpper()).longValue();
            Pair<Range<Integer>, Range<Integer>> widthHeightRanges = parseWidthHeightRanges(map.get("size-range"));
            int iMax = widthHeightRanges == null ? 0 : Math.max(((Integer) widthHeightRanges.first.getUpper()).intValue(), ((Integer) widthHeightRanges.second.getUpper()).intValue());
            Range<Integer> intRange2 = Utils.parseIntRange(map.get("bitrate-range"), null);
            int iDivUp = intRange2 != null ? Utils.divUp(((Integer) intRange2.getUpper()).intValue(), 1000) : 0;
            if (jLongValue <= 829440 && iIntValue <= 36864 && iDivUp <= 200 && iMax <= 512) {
                return 1;
            }
            if (jLongValue <= 2764800 && iIntValue <= 73728 && iDivUp <= 800 && iMax <= 768) {
                return 2;
            }
            if (jLongValue <= 4608000 && iIntValue <= 122880 && iDivUp <= 1800 && iMax <= 960) {
                return 4;
            }
            if (jLongValue <= 9216000 && iIntValue <= 245760 && iDivUp <= 3600 && iMax <= 1344) {
                return 8;
            }
            if (jLongValue <= 20736000 && iIntValue <= 552960 && iDivUp <= 7200 && iMax <= 2048) {
                return 16;
            }
            if (jLongValue <= 36864000 && iIntValue <= 983040 && iDivUp <= 12000 && iMax <= 2752) {
                return 32;
            }
            if (jLongValue <= 83558400 && iIntValue <= 2228224 && iDivUp <= 18000 && iMax <= 4160) {
                return 64;
            }
            if (jLongValue <= 160432128 && iIntValue <= 2228224 && iDivUp <= 30000 && iMax <= 4160) {
                return 128;
            }
            if (jLongValue <= 311951360 && iIntValue <= 8912896 && iDivUp <= 60000 && iMax <= 8384) {
                return 256;
            }
            if (jLongValue <= 588251136 && iIntValue <= 8912896 && iDivUp <= 120000 && iMax <= 8384) {
                return 512;
            }
            if (jLongValue <= 1176502272 && iIntValue <= 8912896 && iDivUp <= 180000 && iMax <= 8384) {
                return 1024;
            }
            if (jLongValue <= 1176502272 && iIntValue <= 35651584 && iDivUp <= 180000 && iMax <= 16832) {
                return 2048;
            }
            if (jLongValue <= 2353004544L && iIntValue <= 35651584 && iDivUp <= 240000 && iMax <= 16832) {
                return 4096;
            }
            if (jLongValue > 4706009088L || iIntValue > 35651584 || iDivUp > 480000 || iMax <= 16832) {
            }
            return 8192;
        }

        private void parseFromInfo(MediaFormat mediaFormat) {
            Range<Integer> range;
            Range<Integer> range2;
            Range<Integer> rangeExtend;
            Range<Integer> range3;
            Range<Integer> range4;
            Range range5;
            Map<String, Object> map = mediaFormat.getMap();
            Size size = new Size(this.mBlockWidth, this.mBlockHeight);
            Size size2 = new Size(this.mWidthAlignment, this.mHeightAlignment);
            Size size3 = Utils.parseSize(map.get("block-size"), size);
            Size size4 = Utils.parseSize(map.get("alignment"), size2);
            Range rangeIntersect = null;
            Range<Integer> intRange = Utils.parseIntRange(map.get("block-count-range"), null);
            Range<Long> longRange = Utils.parseLongRange(map.get("blocks-per-second-range"), null);
            this.mMeasuredFrameRates = getMeasuredFrameRates(map);
            Pair<Range<Integer>, Range<Integer>> widthHeightRanges = parseWidthHeightRanges(map.get("size-range"));
            if (widthHeightRanges != null) {
                range2 = widthHeightRanges.first;
                range = widthHeightRanges.second;
            } else {
                range = null;
                range2 = null;
            }
            if (!map.containsKey("feature-can-swap-width-height")) {
                rangeExtend = range;
                range3 = range2;
            } else if (range2 != null) {
                this.mSmallerDimensionUpperLimit = Math.min(((Integer) range2.getUpper()).intValue(), ((Integer) range.getUpper()).intValue());
                rangeExtend = range2.extend(range);
                range3 = rangeExtend;
            } else {
                Log.w(TAG, "feature can-swap-width-height is best used with size-range");
                this.mSmallerDimensionUpperLimit = Math.min(((Integer) this.mWidthRange.getUpper()).intValue(), ((Integer) this.mHeightRange.getUpper()).intValue());
                Range rangeExtend2 = this.mWidthRange.extend(this.mHeightRange);
                this.mHeightRange = rangeExtend2;
                this.mWidthRange = rangeExtend2;
                rangeExtend = range;
                range3 = range2;
            }
            Range<Rational> rationalRange = Utils.parseRationalRange(map.get("block-aspect-ratio-range"), null);
            Range<Rational> rationalRange2 = Utils.parseRationalRange(map.get("pixel-aspect-ratio-range"), null);
            Range<Integer> intRange2 = Utils.parseIntRange(map.get("frame-rate-range"), null);
            if (intRange2 != null) {
                try {
                    intRange2 = intRange2.intersect(MediaCodecInfo.FRAME_RATE_RANGE);
                } catch (IllegalArgumentException unused) {
                    Log.w(TAG, "frame rate range (" + intRange2 + ") is out of limits: " + MediaCodecInfo.FRAME_RATE_RANGE);
                    range4 = null;
                }
            }
            range4 = intRange2;
            Range<Integer> intRange3 = Utils.parseIntRange(map.get("bitrate-range"), null);
            if (intRange3 != null) {
                try {
                    rangeIntersect = intRange3.intersect(MediaCodecInfo.BITRATE_RANGE);
                } catch (IllegalArgumentException unused2) {
                    Log.w(TAG, "bitrate range (" + intRange3 + ") is out of limits: " + MediaCodecInfo.BITRATE_RANGE);
                }
                range5 = rangeIntersect;
            } else {
                range5 = intRange3;
            }
            MediaCodecInfo.checkPowerOfTwo(size3.getWidth(), "block-size width must be power of two");
            MediaCodecInfo.checkPowerOfTwo(size3.getHeight(), "block-size height must be power of two");
            MediaCodecInfo.checkPowerOfTwo(size4.getWidth(), "alignment width must be power of two");
            MediaCodecInfo.checkPowerOfTwo(size4.getHeight(), "alignment height must be power of two");
            Range range6 = range5;
            Range<Integer> range7 = range4;
            applyMacroBlockLimits(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE, size3.getWidth(), size3.getHeight(), size4.getWidth(), size4.getHeight());
            if ((this.mParent.mError & 2) != 0 || this.mAllowMbOverride) {
                if (range3 != null) {
                    this.mWidthRange = MediaCodecInfo.SIZE_RANGE.intersect(range3);
                }
                if (rangeExtend != null) {
                    this.mHeightRange = MediaCodecInfo.SIZE_RANGE.intersect(rangeExtend);
                }
                if (intRange != null) {
                    this.mBlockCountRange = MediaCodecInfo.POSITIVE_INTEGERS.intersect(Utils.factorRange(intRange, ((this.mBlockWidth * this.mBlockHeight) / size3.getWidth()) / size3.getHeight()));
                }
                if (longRange != null) {
                    this.mBlocksPerSecondRange = MediaCodecInfo.POSITIVE_LONGS.intersect(Utils.factorRange(longRange, ((this.mBlockWidth * this.mBlockHeight) / size3.getWidth()) / size3.getHeight()));
                }
                if (rationalRange2 != null) {
                    this.mBlockAspectRatioRange = MediaCodecInfo.POSITIVE_RATIONALS.intersect(Utils.scaleRange(rationalRange2, this.mBlockHeight / size3.getHeight(), this.mBlockWidth / size3.getWidth()));
                }
                if (rationalRange != null) {
                    this.mAspectRatioRange = MediaCodecInfo.POSITIVE_RATIONALS.intersect(rationalRange);
                }
                if (range7 != null) {
                    this.mFrameRateRange = MediaCodecInfo.FRAME_RATE_RANGE.intersect(range7);
                }
                if (range6 != null) {
                    if ((this.mParent.mError & 2) != 0) {
                        this.mBitrateRange = MediaCodecInfo.BITRATE_RANGE.intersect(range6);
                    } else {
                        this.mBitrateRange = this.mBitrateRange.intersect(range6);
                    }
                }
            } else {
                if (range3 != null) {
                    this.mWidthRange = this.mWidthRange.intersect(range3);
                }
                if (rangeExtend != null) {
                    this.mHeightRange = this.mHeightRange.intersect(rangeExtend);
                }
                if (intRange != null) {
                    this.mBlockCountRange = this.mBlockCountRange.intersect(Utils.factorRange(intRange, ((this.mBlockWidth * this.mBlockHeight) / size3.getWidth()) / size3.getHeight()));
                }
                if (longRange != null) {
                    this.mBlocksPerSecondRange = this.mBlocksPerSecondRange.intersect(Utils.factorRange(longRange, ((this.mBlockWidth * this.mBlockHeight) / size3.getWidth()) / size3.getHeight()));
                }
                if (rationalRange2 != null) {
                    this.mBlockAspectRatioRange = this.mBlockAspectRatioRange.intersect(Utils.scaleRange(rationalRange2, this.mBlockHeight / size3.getHeight(), this.mBlockWidth / size3.getWidth()));
                }
                if (rationalRange != null) {
                    this.mAspectRatioRange = this.mAspectRatioRange.intersect(rationalRange);
                }
                if (range7 != null) {
                    this.mFrameRateRange = this.mFrameRateRange.intersect(range7);
                }
                if (range6 != null) {
                    this.mBitrateRange = this.mBitrateRange.intersect(range6);
                }
            }
            updateLimits();
        }

        private void applyBlockLimits(int i, int i2, Range<Integer> range, Range<Long> range2, Range<Rational> range3) {
            MediaCodecInfo.checkPowerOfTwo(i, "blockWidth must be a power of two");
            MediaCodecInfo.checkPowerOfTwo(i2, "blockHeight must be a power of two");
            int iMax = Math.max(i, this.mBlockWidth);
            int iMax2 = Math.max(i2, this.mBlockHeight);
            int i3 = iMax * iMax2;
            int i4 = (i3 / this.mBlockWidth) / this.mBlockHeight;
            if (i4 != 1) {
                this.mBlockCountRange = Utils.factorRange(this.mBlockCountRange, i4);
                this.mBlocksPerSecondRange = Utils.factorRange(this.mBlocksPerSecondRange, i4);
                this.mBlockAspectRatioRange = Utils.scaleRange(this.mBlockAspectRatioRange, iMax2 / this.mBlockHeight, iMax / this.mBlockWidth);
                this.mHorizontalBlockRange = Utils.factorRange(this.mHorizontalBlockRange, iMax / this.mBlockWidth);
                this.mVerticalBlockRange = Utils.factorRange(this.mVerticalBlockRange, iMax2 / this.mBlockHeight);
            }
            int i5 = (i3 / i) / i2;
            if (i5 != 1) {
                range = Utils.factorRange(range, i5);
                range2 = Utils.factorRange(range2, i5);
                range3 = Utils.scaleRange(range3, iMax2 / i2, iMax / i);
            }
            this.mBlockCountRange = this.mBlockCountRange.intersect(range);
            this.mBlocksPerSecondRange = this.mBlocksPerSecondRange.intersect(range2);
            this.mBlockAspectRatioRange = this.mBlockAspectRatioRange.intersect(range3);
            this.mBlockWidth = iMax;
            this.mBlockHeight = iMax2;
        }

        private void applyAlignment(int i, int i2) {
            MediaCodecInfo.checkPowerOfTwo(i, "widthAlignment must be a power of two");
            MediaCodecInfo.checkPowerOfTwo(i2, "heightAlignment must be a power of two");
            if (i > this.mBlockWidth || i2 > this.mBlockHeight) {
                applyBlockLimits(Math.max(i, this.mBlockWidth), Math.max(i2, this.mBlockHeight), MediaCodecInfo.POSITIVE_INTEGERS, MediaCodecInfo.POSITIVE_LONGS, MediaCodecInfo.POSITIVE_RATIONALS);
            }
            this.mWidthAlignment = Math.max(i, this.mWidthAlignment);
            this.mHeightAlignment = Math.max(i2, this.mHeightAlignment);
            this.mWidthRange = Utils.alignRange(this.mWidthRange, this.mWidthAlignment);
            this.mHeightRange = Utils.alignRange(this.mHeightRange, this.mHeightAlignment);
        }

        private void updateLimits() {
            Range rangeIntersect = this.mHorizontalBlockRange.intersect(Utils.factorRange(this.mWidthRange, this.mBlockWidth));
            this.mHorizontalBlockRange = rangeIntersect;
            this.mHorizontalBlockRange = rangeIntersect.intersect(Range.create(Integer.valueOf(((Integer) this.mBlockCountRange.getLower()).intValue() / ((Integer) this.mVerticalBlockRange.getUpper()).intValue()), Integer.valueOf(((Integer) this.mBlockCountRange.getUpper()).intValue() / ((Integer) this.mVerticalBlockRange.getLower()).intValue())));
            Range rangeIntersect2 = this.mVerticalBlockRange.intersect(Utils.factorRange(this.mHeightRange, this.mBlockHeight));
            this.mVerticalBlockRange = rangeIntersect2;
            this.mVerticalBlockRange = rangeIntersect2.intersect(Range.create(Integer.valueOf(((Integer) this.mBlockCountRange.getLower()).intValue() / ((Integer) this.mHorizontalBlockRange.getUpper()).intValue()), Integer.valueOf(((Integer) this.mBlockCountRange.getUpper()).intValue() / ((Integer) this.mHorizontalBlockRange.getLower()).intValue())));
            this.mBlockCountRange = this.mBlockCountRange.intersect(Range.create(Integer.valueOf(((Integer) this.mHorizontalBlockRange.getLower()).intValue() * ((Integer) this.mVerticalBlockRange.getLower()).intValue()), Integer.valueOf(((Integer) this.mHorizontalBlockRange.getUpper()).intValue() * ((Integer) this.mVerticalBlockRange.getUpper()).intValue())));
            this.mBlockAspectRatioRange = this.mBlockAspectRatioRange.intersect(new Rational(((Integer) this.mHorizontalBlockRange.getLower()).intValue(), ((Integer) this.mVerticalBlockRange.getUpper()).intValue()), new Rational(((Integer) this.mHorizontalBlockRange.getUpper()).intValue(), ((Integer) this.mVerticalBlockRange.getLower()).intValue()));
            this.mWidthRange = this.mWidthRange.intersect(Integer.valueOf(((((Integer) this.mHorizontalBlockRange.getLower()).intValue() - 1) * this.mBlockWidth) + this.mWidthAlignment), Integer.valueOf(((Integer) this.mHorizontalBlockRange.getUpper()).intValue() * this.mBlockWidth));
            this.mHeightRange = this.mHeightRange.intersect(Integer.valueOf(((((Integer) this.mVerticalBlockRange.getLower()).intValue() - 1) * this.mBlockHeight) + this.mHeightAlignment), Integer.valueOf(((Integer) this.mVerticalBlockRange.getUpper()).intValue() * this.mBlockHeight));
            this.mAspectRatioRange = this.mAspectRatioRange.intersect(new Rational(((Integer) this.mWidthRange.getLower()).intValue(), ((Integer) this.mHeightRange.getUpper()).intValue()), new Rational(((Integer) this.mWidthRange.getUpper()).intValue(), ((Integer) this.mHeightRange.getLower()).intValue()));
            this.mSmallerDimensionUpperLimit = Math.min(this.mSmallerDimensionUpperLimit, Math.min(((Integer) this.mWidthRange.getUpper()).intValue(), ((Integer) this.mHeightRange.getUpper()).intValue()));
            Range rangeIntersect3 = this.mBlocksPerSecondRange.intersect(Long.valueOf(((long) ((Integer) this.mBlockCountRange.getLower()).intValue()) * ((long) ((Integer) this.mFrameRateRange.getLower()).intValue())), Long.valueOf(((long) ((Integer) this.mBlockCountRange.getUpper()).intValue()) * ((long) ((Integer) this.mFrameRateRange.getUpper()).intValue())));
            this.mBlocksPerSecondRange = rangeIntersect3;
            this.mFrameRateRange = this.mFrameRateRange.intersect(Integer.valueOf((int) (((Long) rangeIntersect3.getLower()).longValue() / ((long) ((Integer) this.mBlockCountRange.getUpper()).intValue()))), Integer.valueOf((int) (((Long) this.mBlocksPerSecondRange.getUpper()).longValue() / ((double) ((Integer) this.mBlockCountRange.getLower()).intValue()))));
        }

        private void applyMacroBlockLimits(int i, int i2, int i3, long j, int i4, int i5, int i6, int i7) {
            applyMacroBlockLimits(1, 1, i, i2, i3, j, i4, i5, i6, i7);
        }

        private void applyMacroBlockLimits(int i, int i2, int i3, int i4, int i5, long j, int i6, int i7, int i8, int i9) {
            applyAlignment(i8, i9);
            applyBlockLimits(i6, i7, Range.create(1, Integer.valueOf(i5)), Range.create(1L, Long.valueOf(j)), Range.create(new Rational(1, i4), new Rational(i3, 1)));
            this.mHorizontalBlockRange = this.mHorizontalBlockRange.intersect(Integer.valueOf(Utils.divUp(i, this.mBlockWidth / i6)), Integer.valueOf(i3 / (this.mBlockWidth / i6)));
            this.mVerticalBlockRange = this.mVerticalBlockRange.intersect(Integer.valueOf(Utils.divUp(i2, this.mBlockHeight / i7)), Integer.valueOf(i4 / (this.mBlockHeight / i7)));
        }

        /* JADX WARN: Code duplicated, block: B:108:0x0308 A[PHI: r16
  0x0308: PHI (r16v1 int) = (r16v0 int), (r16v5 int) binds: [B:106:0x02de, B:91:0x025c] A[DONT_GENERATE, DONT_INLINE]] */
        /* JADX WARN: Code duplicated, block: B:234:0x07bc  */
        /* JADX WARN: Code duplicated, block: B:252:0x07fa  */
        /* JADX WARN: Code duplicated, block: B:253:0x07ff  */
        /* JADX WARN: Code duplicated, block: B:371:0x0185 A[SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:55:0x0144  */
        /* JADX WARN: Code duplicated, block: B:56:0x0147  */
        /* JADX WARN: Code duplicated, block: B:65:0x0181  */
        /* JADX WARN: Failed to find 'out' block for switch in B:123:0x03bd. Please report as an issue. */
        private void applyLevelLimits() {
            Integer num;
            CodecProfileLevel[] codecProfileLevelArr;
            int i;
            int i2;
            int i3;
            int i4;
            double d;
            int i5;
            int i6;
            double d2;
            int i7;
            double d3;
            long j;
            int i8;
            int i9;
            int i10;
            int i11;
            long j2;
            int i12;
            int i13;
            long j3;
            int i14;
            int i15;
            int i16;
            int iMax;
            int i17;
            int i18;
            int i19;
            int i20;
            int i21;
            int i22;
            boolean z;
            int i23;
            int i24;
            int i25;
            int i26;
            int i27;
            int i28;
            int i29;
            String str;
            String str2;
            int i30;
            int i31;
            int i32;
            int i33;
            int i34;
            int i35;
            int i36;
            boolean z2;
            boolean z3;
            int i37;
            int i38;
            int i39;
            int iMax2;
            int i40;
            int i41;
            int i42;
            int i43;
            int i44;
            int i45;
            int i46;
            boolean z4;
            int i47;
            int i48;
            int i49;
            int i50;
            int i51;
            int i52;
            int i53;
            int i54;
            boolean z5;
            int i55;
            int i56;
            int i57;
            int i58;
            int i59;
            int i60;
            CodecProfileLevel[] codecProfileLevelArr2 = this.mParent.profileLevels;
            String mimeType = this.mParent.getMimeType();
            boolean zEqualsIgnoreCase = mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_VIDEO_AVC);
            String str3 = " for ";
            int i61 = 2;
            String str4 = TAG;
            int i62 = 1;
            Integer num2 = 1;
            if (zEqualsIgnoreCase) {
                int length = codecProfileLevelArr2.length;
                long jMax = 1485;
                int iMax3 = 99;
                int i63 = 0;
                int iMax4 = 64000;
                i2 = 4;
                int iMax5 = MetricsProto.MetricsEvent.ACTION_DATA_SAVER_BLACKLIST;
                while (i63 < length) {
                    CodecProfileLevel codecProfileLevel = codecProfileLevelArr2[i63];
                    int i64 = codecProfileLevel.level;
                    if (i64 != i62) {
                        if (i64 != i61) {
                            switch (i64) {
                                case 4:
                                    i50 = PathInterpolatorCompat.MAX_NUM_POINTS;
                                    i56 = 192;
                                    i57 = 900;
                                    i52 = i56;
                                    i53 = i57;
                                    i51 = MetricsProto.MetricsEvent.ACTION_DATA_SAVER_BLACKLIST;
                                    break;
                                case 8:
                                    i50 = BluetoothHealth.HEALTH_OPERATION_SUCCESS;
                                    i56 = MetricsProto.MetricsEvent.ACTION_SHOW_SETTINGS_SUGGESTION;
                                    i57 = 2376;
                                    i52 = i56;
                                    i53 = i57;
                                    i51 = MetricsProto.MetricsEvent.ACTION_DATA_SAVER_BLACKLIST;
                                    break;
                                case 16:
                                    i58 = 768;
                                    i52 = i58;
                                    i53 = 2376;
                                    i50 = 11880;
                                    i51 = MetricsProto.MetricsEvent.ACTION_DATA_SAVER_BLACKLIST;
                                    break;
                                case 32:
                                    i58 = 2000;
                                    i52 = i58;
                                    i53 = 2376;
                                    i50 = 11880;
                                    i51 = MetricsProto.MetricsEvent.ACTION_DATA_SAVER_BLACKLIST;
                                    break;
                                case 64:
                                    i50 = 19800;
                                    i51 = MetricsProto.MetricsEvent.DEFAULT_AUTOFILL_PICKER;
                                    i59 = 4000;
                                    i60 = 4752;
                                    i52 = i59;
                                    i53 = i60;
                                    break;
                                case 128:
                                    i50 = 20250;
                                    i51 = 1620;
                                    i59 = 4000;
                                    i60 = 8100;
                                    i52 = i59;
                                    i53 = i60;
                                    break;
                                case 256:
                                    i50 = 40500;
                                    i51 = 1620;
                                    i59 = 10000;
                                    i60 = 8100;
                                    i52 = i59;
                                    i53 = i60;
                                    break;
                                case 512:
                                    i50 = 108000;
                                    i51 = NetworkScanRequest.MAX_SEARCH_MAX_SEC;
                                    i59 = ISSErrors.ISS_ERROR_LUA_BASE;
                                    i60 = 18000;
                                    i52 = i59;
                                    i53 = i60;
                                    break;
                                case 1024:
                                    i50 = 216000;
                                    i51 = 5120;
                                    i59 = 20000;
                                    i60 = MtpConstants.DEVICE_PROPERTY_UNDEFINED;
                                    i52 = i59;
                                    i53 = i60;
                                    break;
                                case 2048:
                                    i50 = 245760;
                                    i51 = 8192;
                                    i59 = 20000;
                                    i60 = 32768;
                                    i52 = i59;
                                    i53 = i60;
                                    break;
                                case 4096:
                                    i50 = 245760;
                                    i51 = 8192;
                                    i59 = 50000;
                                    i60 = 32768;
                                    i52 = i59;
                                    i53 = i60;
                                    break;
                                case 8192:
                                    i50 = 522240;
                                    i51 = 8704;
                                    i59 = 50000;
                                    i60 = GLES20.GL_STENCIL_BACK_FUNC;
                                    i52 = i59;
                                    i53 = i60;
                                    break;
                                case 16384:
                                    i50 = 589824;
                                    i51 = 22080;
                                    i59 = 135000;
                                    i60 = 110400;
                                    i52 = i59;
                                    i53 = i60;
                                    break;
                                case 32768:
                                    i50 = SurfaceControl.FX_SURFACE_MASK;
                                    i51 = 36864;
                                    i59 = 240000;
                                    i60 = 184320;
                                    i52 = i59;
                                    i53 = i60;
                                    break;
                                case 65536:
                                    i50 = 2073600;
                                    i51 = 36864;
                                    i59 = 240000;
                                    i60 = 184320;
                                    i52 = i59;
                                    i53 = i60;
                                    break;
                                default:
                                    Log.w(TAG, "Unrecognized level " + codecProfileLevel.level + " for " + mimeType);
                                    i2 |= 1;
                                    i50 = 0;
                                    i51 = 0;
                                    i52 = 0;
                                    i53 = 0;
                                    break;
                            }
                        } else {
                            i50 = 1485;
                            i51 = 99;
                            i52 = 128;
                        }
                        i54 = codecProfileLevel.profile;
                        if (i54 != i62 || i54 == 2) {
                            z5 = true;
                            i55 = i52 * 1000;
                        } else {
                            if (i54 != 4) {
                                if (i54 == 8) {
                                    i55 = i52 * MetricsProto.MetricsEvent.FIELD_SELECTION_RANGE_START;
                                } else if (i54 != 16) {
                                    if (i54 != 32 && i54 != 64) {
                                        if (i54 != 65536) {
                                            if (i54 != 524288) {
                                                Log.w(TAG, "Unrecognized profile " + codecProfileLevel.profile + " for " + mimeType);
                                                i2 |= 1;
                                                i55 = i52 * 1000;
                                            } else {
                                                i55 = i52 * MetricsProto.MetricsEvent.FIELD_SELECTION_RANGE_START;
                                            }
                                        }
                                        z5 = true;
                                    }
                                    i55 = i52 * 1000;
                                } else {
                                    i55 = i52 * PathInterpolatorCompat.MAX_NUM_POINTS;
                                }
                                length = length;
                                z5 = true;
                            }
                            Log.w(TAG, "Unsupported profile " + codecProfileLevel.profile + " for " + mimeType);
                            i2 |= 2;
                            z5 = false;
                            i55 = i52 * 1000;
                        }
                        if (z5) {
                            i2 &= -5;
                        }
                        jMax = Math.max(i50, jMax);
                        iMax3 = Math.max(i51, iMax3);
                        iMax4 = Math.max(i55, iMax4);
                        iMax5 = Math.max(iMax5, i53);
                        i63++;
                        length = length;
                        i62 = 1;
                        i61 = 2;
                    } else {
                        i50 = 1485;
                        i51 = 99;
                        i52 = 64;
                    }
                    i53 = MetricsProto.MetricsEvent.ACTION_DATA_SAVER_BLACKLIST;
                    i54 = codecProfileLevel.profile;
                    if (i54 != i62) {
                        z5 = true;
                        i55 = i52 * 1000;
                    } else {
                        z5 = true;
                        i55 = i52 * 1000;
                    }
                    if (z5) {
                        i2 &= -5;
                    }
                    jMax = Math.max(i50, jMax);
                    iMax3 = Math.max(i51, iMax3);
                    iMax4 = Math.max(i55, iMax4);
                    iMax5 = Math.max(iMax5, i53);
                    i63++;
                    length = length;
                    i62 = 1;
                    i61 = 2;
                }
                int iSqrt = (int) Math.sqrt(iMax3 * 8);
                applyMacroBlockLimits(iSqrt, iSqrt, iMax3, jMax, 16, 16, 1, 1);
                num = num2;
                i = iMax4;
            } else {
                if (mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_VIDEO_MPEG2)) {
                    int length2 = codecProfileLevelArr2.length;
                    int iMax6 = 64000;
                    long jMax2 = 1485;
                    int iMax7 = 9;
                    int iMax8 = 99;
                    int iMax9 = 11;
                    int i65 = 0;
                    int iMax10 = 15;
                    i2 = 4;
                    while (i65 < length2) {
                        CodecProfileLevel codecProfileLevel2 = codecProfileLevelArr2[i65];
                        int i66 = codecProfileLevel2.profile;
                        if (i66 != 0) {
                            i40 = length2;
                            if (i66 == 1) {
                                int i67 = codecProfileLevel2.level;
                                if (i67 == 0) {
                                    i45 = 4000;
                                    i46 = 30;
                                    i41 = 22;
                                    i42 = 18;
                                    i44 = MetricsProto.MetricsEvent.ACTION_DATA_SAVER_BLACKLIST;
                                    i43 = 11880;
                                } else if (i67 != 1) {
                                    if (i67 != 2) {
                                        if (i67 == 3) {
                                            i46 = 60;
                                            i41 = 120;
                                            i42 = 68;
                                            i47 = 244800;
                                        } else if (i67 != 4) {
                                            Log.w(str4, "Unrecognized profile/level " + codecProfileLevel2.profile + "/" + codecProfileLevel2.level + str3 + mimeType);
                                        } else {
                                            i46 = 60;
                                            i41 = 120;
                                            i42 = 68;
                                            i47 = 489600;
                                        }
                                        i48 = 8160;
                                        i49 = 80000;
                                    } else {
                                        i46 = 60;
                                        i41 = 90;
                                        i42 = 68;
                                        i47 = 183600;
                                        i48 = 6120;
                                        i49 = 60000;
                                    }
                                    i43 = i47;
                                    i44 = i48;
                                    i45 = i49;
                                } else {
                                    i41 = 45;
                                    i42 = 36;
                                    i43 = 40500;
                                    i44 = 1620;
                                    i45 = 15000;
                                    i46 = 30;
                                }
                                z4 = true;
                            } else if (i66 == 2 || i66 == 3 || i66 == 4 || i66 == 5) {
                                Log.i(str4, "Unsupported profile " + codecProfileLevel2.profile + str3 + mimeType);
                                i2 |= 2;
                                str3 = str3;
                                str4 = str4;
                                num2 = num2;
                                i46 = 0;
                                i45 = 0;
                                i41 = 0;
                                i42 = 0;
                                i44 = 0;
                                i43 = 0;
                                z4 = false;
                            } else {
                                Log.w(str4, "Unrecognized profile " + codecProfileLevel2.profile + str3 + mimeType);
                            }
                            i2 |= 1;
                            i46 = 0;
                            i45 = 0;
                            i41 = 0;
                            i42 = 0;
                            i44 = 0;
                            i43 = 0;
                            z4 = true;
                        } else {
                            i40 = length2;
                            if (codecProfileLevel2.level != 1) {
                                Log.w(str4, "Unrecognized profile/level " + codecProfileLevel2.profile + "/" + codecProfileLevel2.level + str3 + mimeType);
                                i2 |= 1;
                                i46 = 0;
                                i45 = 0;
                                i41 = 0;
                                i42 = 0;
                                i44 = 0;
                                i43 = 0;
                            } else {
                                i41 = 45;
                                i42 = 36;
                                i43 = 40500;
                                i44 = 1620;
                                i45 = 15000;
                                i46 = 30;
                            }
                            z4 = true;
                        }
                        if (z4) {
                            i2 &= -5;
                        }
                        jMax2 = Math.max(i43, jMax2);
                        iMax8 = Math.max(i44, iMax8);
                        iMax6 = Math.max(i45 * 1000, iMax6);
                        iMax9 = Math.max(i41, iMax9);
                        iMax7 = Math.max(i42, iMax7);
                        iMax10 = Math.max(i46, iMax10);
                        i65++;
                        length2 = i40;
                        num2 = num2;
                        str4 = str4;
                        str3 = str3;
                        codecProfileLevelArr2 = codecProfileLevelArr2;
                        mimeType = mimeType;
                    }
                    num = num2;
                    iMax = iMax6;
                    applyMacroBlockLimits(iMax9, iMax7, iMax8, jMax2, 16, 16, 1, 1);
                    this.mFrameRateRange = this.mFrameRateRange.intersect(12, Integer.valueOf(iMax10));
                } else {
                    String str5 = " for ";
                    String str6 = TAG;
                    num = num2;
                    if (mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_VIDEO_MPEG4)) {
                        CodecProfileLevel[] codecProfileLevelArr3 = codecProfileLevelArr2;
                        int length3 = codecProfileLevelArr3.length;
                        iMax = 64000;
                        long jMax3 = 1485;
                        int i68 = 0;
                        int i69 = 11;
                        int i70 = 9;
                        int iMax11 = 99;
                        int i71 = 15;
                        i2 = 4;
                        while (i68 < length3) {
                            CodecProfileLevel codecProfileLevel3 = codecProfileLevelArr3[i68];
                            int i72 = codecProfileLevel3.profile;
                            if (i72 == 1) {
                                str = str6;
                                str2 = str5;
                                i30 = length3;
                                int i73 = codecProfileLevel3.level;
                                if (i73 == 1) {
                                    i31 = 15;
                                    i32 = 64;
                                } else if (i73 == 2) {
                                    i31 = 15;
                                    i32 = 128;
                                } else if (i73 == 4) {
                                    codecProfileLevelArr3 = codecProfileLevelArr3;
                                    str = str;
                                    str2 = str2;
                                    i31 = 30;
                                    i32 = 64;
                                    i33 = 99;
                                    i34 = 11;
                                    i35 = 9;
                                    i36 = 1485;
                                    z2 = true;
                                    z3 = false;
                                } else if (i73 == 8) {
                                    i36 = 5940;
                                    str = str;
                                    str2 = str2;
                                    i31 = 30;
                                    i32 = 128;
                                    i33 = MetricsProto.MetricsEvent.ACTION_DATA_SAVER_BLACKLIST;
                                    i34 = 22;
                                    i35 = 18;
                                    z2 = true;
                                    z3 = false;
                                } else if (i73 == 16) {
                                    i37 = MetricsProto.MetricsEvent.ACTION_SHOW_SETTINGS_SUGGESTION;
                                    codecProfileLevelArr3 = codecProfileLevelArr3;
                                    str = str;
                                    str2 = str2;
                                    i32 = i37;
                                    i31 = 30;
                                    i33 = MetricsProto.MetricsEvent.ACTION_DATA_SAVER_BLACKLIST;
                                    i34 = 22;
                                    i35 = 18;
                                    i36 = 11880;
                                    z2 = true;
                                    z3 = false;
                                } else if (i73 != 64) {
                                    if (i73 == 128) {
                                        i34 = 45;
                                        i35 = 36;
                                        i36 = 40500;
                                        i38 = 1620;
                                        i39 = 8000;
                                    } else if (i73 != 256) {
                                        Log.w(str, "Unrecognized profile/level " + codecProfileLevel3.profile + "/" + codecProfileLevel3.level + str2 + mimeType);
                                        i2 |= 1;
                                        str = str;
                                        str2 = str2;
                                        i31 = 0;
                                        i32 = 0;
                                        i33 = 0;
                                        i34 = 0;
                                        i35 = 0;
                                        i36 = 0;
                                        z2 = true;
                                        z3 = false;
                                    } else {
                                        i34 = 80;
                                        i35 = 45;
                                        i36 = 108000;
                                        i38 = NetworkScanRequest.MAX_SEARCH_MAX_SEC;
                                        i39 = ISSErrors.ISS_ERROR_HTTP_BASE;
                                    }
                                    codecProfileLevelArr3 = codecProfileLevelArr3;
                                    str2 = str2;
                                    i33 = i38;
                                    i31 = 30;
                                    z3 = false;
                                    str = str;
                                    i32 = i39;
                                    z2 = true;
                                } else {
                                    i34 = 40;
                                    i36 = 36000;
                                    codecProfileLevelArr3 = codecProfileLevelArr3;
                                    str2 = str2;
                                    i33 = 1200;
                                    i31 = 30;
                                    z2 = true;
                                    z3 = false;
                                    str = str;
                                    i32 = 4000;
                                    i35 = 30;
                                }
                                i33 = 99;
                                i34 = 11;
                                i35 = 9;
                                i36 = 1485;
                                z2 = true;
                                z3 = true;
                            } else {
                                if (i72 != 2) {
                                    switch (i72) {
                                        case 4:
                                        case 8:
                                        case 16:
                                        case 32:
                                        case 64:
                                        case 128:
                                        case 256:
                                        case 512:
                                        case 1024:
                                        case 2048:
                                        case 4096:
                                        case 8192:
                                        case 16384:
                                            break;
                                        case 32768:
                                            str = str6;
                                            str2 = str5;
                                            int i74 = codecProfileLevel3.level;
                                            i30 = length3;
                                            if (i74 == 1 || i74 == 4) {
                                                i36 = 2970;
                                                codecProfileLevelArr3 = codecProfileLevelArr3;
                                                str = str;
                                                str2 = str2;
                                                i31 = 30;
                                                i32 = 128;
                                                i33 = 99;
                                                i34 = 11;
                                                i35 = 9;
                                            } else if (i74 != 8) {
                                                if (i74 == 16) {
                                                    i37 = 768;
                                                } else if (i74 != 24) {
                                                    if (i74 == 32) {
                                                        i34 = 44;
                                                        i35 = 36;
                                                        i36 = 23760;
                                                        i38 = MetricsProto.MetricsEvent.DEFAULT_AUTOFILL_PICKER;
                                                        i39 = PathInterpolatorCompat.MAX_NUM_POINTS;
                                                    } else if (i74 != 128) {
                                                        Log.w(str, "Unrecognized profile/level " + codecProfileLevel3.profile + "/" + codecProfileLevel3.level + str2 + mimeType);
                                                        i2 |= 1;
                                                        str = str;
                                                        str2 = str2;
                                                        i31 = 0;
                                                        i32 = 0;
                                                        i33 = 0;
                                                        i34 = 0;
                                                        i35 = 0;
                                                        i36 = 0;
                                                    } else {
                                                        i34 = 45;
                                                        i35 = 36;
                                                        i36 = 48600;
                                                        i38 = 1620;
                                                        i39 = 8000;
                                                    }
                                                    codecProfileLevelArr3 = codecProfileLevelArr3;
                                                    str2 = str2;
                                                    i33 = i38;
                                                    i31 = 30;
                                                    z3 = false;
                                                    str = str;
                                                    i32 = i39;
                                                    z2 = true;
                                                } else {
                                                    i37 = 1500;
                                                }
                                                codecProfileLevelArr3 = codecProfileLevelArr3;
                                                str = str;
                                                str2 = str2;
                                                i32 = i37;
                                                i31 = 30;
                                                i33 = MetricsProto.MetricsEvent.ACTION_DATA_SAVER_BLACKLIST;
                                                i34 = 22;
                                                i35 = 18;
                                                i36 = 11880;
                                            } else {
                                                i36 = 5940;
                                                str = str;
                                                str2 = str2;
                                                i32 = 384;
                                                i31 = 30;
                                                i33 = MetricsProto.MetricsEvent.ACTION_DATA_SAVER_BLACKLIST;
                                                i34 = 22;
                                                i35 = 18;
                                            }
                                            z2 = true;
                                            z3 = false;
                                            break;
                                        default:
                                            StringBuilder sb = new StringBuilder();
                                            sb.append("Unrecognized profile ");
                                            sb.append(codecProfileLevel3.profile);
                                            String str7 = str5;
                                            sb.append(str7);
                                            sb.append(mimeType);
                                            String str8 = str6;
                                            Log.w(str8, sb.toString());
                                            i2 |= 1;
                                            str2 = str7;
                                            i30 = length3;
                                            str = str8;
                                            i31 = 0;
                                            i32 = 0;
                                            i33 = 0;
                                            i34 = 0;
                                            i35 = 0;
                                            i36 = 0;
                                            z2 = true;
                                            z3 = false;
                                            break;
                                    }
                                }
                                String str9 = str6;
                                String str10 = str5;
                                i30 = length3;
                                Log.i(str9, "Unsupported profile " + codecProfileLevel3.profile + str10 + mimeType);
                                i2 |= 2;
                                codecProfileLevelArr3 = codecProfileLevelArr3;
                                str = str9;
                                str2 = str10;
                                i31 = 0;
                                i32 = 0;
                                i33 = 0;
                                i34 = 0;
                                i35 = 0;
                                i36 = 0;
                                z2 = false;
                                z3 = false;
                            }
                            if (z2) {
                                i2 &= -5;
                            }
                            int i75 = i68;
                            String str11 = mimeType;
                            jMax3 = Math.max(i36, jMax3);
                            iMax11 = Math.max(i33, iMax11);
                            iMax = Math.max(i32 * 1000, iMax);
                            if (z3) {
                                int iMax12 = Math.max(i34, i69);
                                int iMax13 = Math.max(i35, i70);
                                iMax2 = Math.max(i31, i71);
                                i69 = iMax12;
                                i70 = iMax13;
                            } else {
                                int iSqrt2 = (int) Math.sqrt(i33 * 2);
                                int iMax14 = Math.max(iSqrt2, i69);
                                int iMax15 = Math.max(iSqrt2, i70);
                                iMax2 = Math.max(Math.max(i31, 60), i71);
                                i70 = iMax15;
                                i69 = iMax14;
                            }
                            i71 = iMax2;
                            i68 = i75 + 1;
                            length3 = i30;
                            str6 = str;
                            mimeType = str11;
                            str5 = str2;
                            codecProfileLevelArr3 = codecProfileLevelArr3;
                        }
                        applyMacroBlockLimits(i69, i70, iMax11, jMax3, 16, 16, 1, 1);
                        this.mFrameRateRange = this.mFrameRateRange.intersect(12, Integer.valueOf(i71));
                    } else {
                        String str12 = str6;
                        String str13 = str5;
                        if (mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_VIDEO_H263)) {
                            CodecProfileLevel[] codecProfileLevelArr4 = codecProfileLevelArr2;
                            int length4 = codecProfileLevelArr4.length;
                            int i76 = 16;
                            long jMax4 = 1485;
                            iMax = 64000;
                            int i77 = 0;
                            int iMin = 11;
                            int iMin2 = 9;
                            int iMax16 = 11;
                            int iMax17 = 9;
                            int iMax18 = 99;
                            int i78 = 15;
                            i2 = 4;
                            while (i77 < length4) {
                                CodecProfileLevel codecProfileLevel4 = codecProfileLevelArr4[i77];
                                int i79 = length4;
                                int i80 = codecProfileLevel4.level;
                                CodecProfileLevel[] codecProfileLevelArr5 = codecProfileLevelArr4;
                                if (i80 != 1) {
                                    if (i80 != 2) {
                                        if (i80 != 4) {
                                            if (i80 == 8) {
                                                i27 = 32;
                                            } else if (i80 != 16) {
                                                if (i80 == 32) {
                                                    i23 = 60;
                                                    i20 = 19800;
                                                    i19 = 22;
                                                    i21 = 18;
                                                    i76 = 4;
                                                    i22 = 64;
                                                } else if (i80 == 64) {
                                                    i23 = 60;
                                                    i19 = 45;
                                                    i20 = 40500;
                                                    i21 = 18;
                                                    i76 = 4;
                                                    i22 = 128;
                                                } else if (i80 != 128) {
                                                    StringBuilder sb2 = new StringBuilder();
                                                    sb2.append("Unrecognized profile/level ");
                                                    sb2.append(codecProfileLevel4.profile);
                                                    sb2.append("/");
                                                    sb2.append(codecProfileLevel4.level);
                                                    str13 = str13;
                                                    sb2.append(str13);
                                                    sb2.append(mimeType);
                                                    i76 = i76;
                                                    str12 = str12;
                                                    Log.w(str12, sb2.toString());
                                                    i2 |= 1;
                                                    i77 = i77;
                                                    i17 = iMin;
                                                    iMin = i17;
                                                    i18 = iMin2;
                                                    iMin2 = i18;
                                                    i19 = 0;
                                                    i20 = 0;
                                                    i21 = 0;
                                                    i22 = 0;
                                                    z = false;
                                                    i23 = 0;
                                                } else {
                                                    str12 = str12;
                                                    str13 = str13;
                                                    i22 = 256;
                                                    iMin = iMin;
                                                    iMin2 = iMin2;
                                                    i23 = 60;
                                                    i21 = 36;
                                                    i20 = 81000;
                                                    z = false;
                                                    i17 = 1;
                                                    i18 = 1;
                                                    i77 = i77;
                                                    i19 = 45;
                                                    i76 = 4;
                                                }
                                                z = false;
                                                i17 = 1;
                                                i18 = 1;
                                            } else {
                                                i76 = i76;
                                                boolean z6 = codecProfileLevel4.profile == 1 || codecProfileLevel4.profile == 4;
                                                if (z6) {
                                                    i28 = iMin;
                                                    i29 = iMin2;
                                                } else {
                                                    i28 = 1;
                                                    i76 = 4;
                                                    i29 = 1;
                                                }
                                                z = z6;
                                                iMin = iMin;
                                                iMin2 = iMin2;
                                                i17 = i28;
                                                i18 = i29;
                                                i19 = 11;
                                                i20 = 1485;
                                                i21 = 9;
                                                i22 = 2;
                                            }
                                            i24 = codecProfileLevel4.profile;
                                            int i81 = i78;
                                            if (i24 != 1 && i24 != 2 && i24 != 4 && i24 != 8 && i24 != 16 && i24 != 32 && i24 != 64 && i24 != 128 && i24 != 256) {
                                                Log.w(str12, "Unrecognized profile " + codecProfileLevel4.profile + str13 + mimeType);
                                                i2 |= 1;
                                            }
                                            if (z) {
                                                i25 = 11;
                                                i26 = 9;
                                            } else {
                                                this.mAllowMbOverride = true;
                                                i25 = i17;
                                                i26 = i18;
                                            }
                                            i2 &= -5;
                                            jMax4 = Math.max(i20, jMax4);
                                            iMax18 = Math.max(i19 * i21, iMax18);
                                            iMax = Math.max(64000 * i22, iMax);
                                            iMax16 = Math.max(i19, iMax16);
                                            iMax17 = Math.max(i21, iMax17);
                                            int iMax19 = Math.max(i23, i81);
                                            iMin = Math.min(i25, iMin);
                                            iMin2 = Math.min(i26, iMin2);
                                            i78 = iMax19;
                                            i77++;
                                            str13 = str13;
                                            str12 = str12;
                                            length4 = i79;
                                            i76 = i76;
                                            mimeType = mimeType;
                                            codecProfileLevelArr4 = codecProfileLevelArr5;
                                        } else {
                                            i27 = 6;
                                        }
                                        i22 = i27;
                                        i17 = iMin;
                                        iMin = i17;
                                        i18 = iMin2;
                                        iMin2 = i18;
                                        i19 = 22;
                                        i20 = 11880;
                                        i21 = 18;
                                    } else {
                                        i77 = i77;
                                        i76 = i76;
                                        str12 = str12;
                                        str13 = str13;
                                        i17 = iMin;
                                        iMin = i17;
                                        i18 = iMin2;
                                        iMin2 = i18;
                                        i20 = 5940;
                                        i19 = 22;
                                        i21 = 18;
                                        i22 = 2;
                                    }
                                    z = true;
                                    i23 = 30;
                                    i24 = codecProfileLevel4.profile;
                                    int i82 = i78;
                                    if (i24 != 1) {
                                        Log.w(str12, "Unrecognized profile " + codecProfileLevel4.profile + str13 + mimeType);
                                        i2 |= 1;
                                    }
                                    if (z) {
                                        i25 = 11;
                                        i26 = 9;
                                    } else {
                                        this.mAllowMbOverride = true;
                                        i25 = i17;
                                        i26 = i18;
                                    }
                                    i2 &= -5;
                                    jMax4 = Math.max(i20, jMax4);
                                    iMax18 = Math.max(i19 * i21, iMax18);
                                    iMax = Math.max(64000 * i22, iMax);
                                    iMax16 = Math.max(i19, iMax16);
                                    iMax17 = Math.max(i21, iMax17);
                                    int iMax110 = Math.max(i23, i82);
                                    iMin = Math.min(i25, iMin);
                                    iMin2 = Math.min(i26, iMin2);
                                    i78 = iMax110;
                                    i77++;
                                    str13 = str13;
                                    str12 = str12;
                                    length4 = i79;
                                    i76 = i76;
                                    mimeType = mimeType;
                                    codecProfileLevelArr4 = codecProfileLevelArr5;
                                } else {
                                    i76 = i76;
                                    i17 = iMin;
                                    iMin = i17;
                                    i18 = iMin2;
                                    iMin2 = i18;
                                    i19 = 11;
                                    i20 = 1485;
                                    i21 = 9;
                                    i22 = 1;
                                    z = true;
                                }
                                i23 = 15;
                                i24 = codecProfileLevel4.profile;
                                int i83 = i78;
                                if (i24 != 1) {
                                    Log.w(str12, "Unrecognized profile " + codecProfileLevel4.profile + str13 + mimeType);
                                    i2 |= 1;
                                }
                                if (z) {
                                    i25 = 11;
                                    i26 = 9;
                                } else {
                                    this.mAllowMbOverride = true;
                                    i25 = i17;
                                    i26 = i18;
                                }
                                i2 &= -5;
                                jMax4 = Math.max(i20, jMax4);
                                iMax18 = Math.max(i19 * i21, iMax18);
                                iMax = Math.max(64000 * i22, iMax);
                                iMax16 = Math.max(i19, iMax16);
                                iMax17 = Math.max(i21, iMax17);
                                int iMax111 = Math.max(i23, i83);
                                iMin = Math.min(i25, iMin);
                                iMin2 = Math.min(i26, iMin2);
                                i78 = iMax111;
                                i77++;
                                str13 = str13;
                                str12 = str12;
                                length4 = i79;
                                i76 = i76;
                                mimeType = mimeType;
                                codecProfileLevelArr4 = codecProfileLevelArr5;
                            }
                            int i84 = iMin;
                            int i85 = i76;
                            int i86 = i78;
                            if (!this.mAllowMbOverride) {
                                this.mBlockAspectRatioRange = Range.create(new Rational(11, 9), new Rational(11, 9));
                            }
                            applyMacroBlockLimits(i84, iMin2, iMax16, iMax17, iMax18, jMax4, 16, 16, i85, i85);
                            this.mFrameRateRange = Range.create(num, Integer.valueOf(i86));
                        } else {
                            Integer num3 = num;
                            if (mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_VIDEO_VP8)) {
                                iMax = 100000000;
                                i2 = 4;
                                for (CodecProfileLevel codecProfileLevel5 : codecProfileLevelArr2) {
                                    int i87 = codecProfileLevel5.level;
                                    if (i87 != 1 && i87 != 2 && i87 != 4 && i87 != 8) {
                                        Log.w(str12, "Unrecognized level " + codecProfileLevel5.level + str13 + mimeType);
                                        i2 |= 1;
                                    }
                                    if (codecProfileLevel5.profile != 1) {
                                        Log.w(str12, "Unrecognized profile " + codecProfileLevel5.profile + str13 + mimeType);
                                        i2 |= 1;
                                    }
                                    i2 &= -5;
                                }
                                applyMacroBlockLimits(32767, 32767, Integer.MAX_VALUE, 2147483647L, 16, 16, 1, 1);
                                num = num3;
                            } else if (mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_VIDEO_VP9)) {
                                long jMax5 = 829440;
                                int iMax20 = 36864;
                                int iMax21 = 512;
                                int length5 = codecProfileLevelArr.length;
                                int iMax22 = 200000;
                                int i88 = 0;
                                i2 = 4;
                                while (i88 < length5) {
                                    CodecProfileLevel codecProfileLevel6 = codecProfileLevelArr[i88];
                                    int i89 = codecProfileLevel6.level;
                                    if (i89 == 1) {
                                        codecProfileLevelArr = codecProfileLevelArr2;
                                        j = 829440;
                                        i8 = 36864;
                                        i9 = 200;
                                        i10 = 512;
                                    } else if (i89 != 2) {
                                        switch (i89) {
                                            case 4:
                                                j = 4608000;
                                                i8 = 122880;
                                                i9 = 1800;
                                                i10 = 960;
                                                break;
                                            case 8:
                                                j = 9216000;
                                                i8 = 245760;
                                                i9 = NetworkScanRequest.MAX_SEARCH_MAX_SEC;
                                                i10 = 1344;
                                                break;
                                            case 16:
                                                j = 20736000;
                                                i8 = 552960;
                                                i9 = 7200;
                                                i10 = 2048;
                                                break;
                                            case 32:
                                                j = 36864000;
                                                i8 = SurfaceControl.FX_SURFACE_MASK;
                                                i9 = ISSErrors.ISS_ERROR_HTTP_BASE;
                                                i10 = 2752;
                                                break;
                                            case 64:
                                                j = 83558400;
                                                i8 = 2228224;
                                                i9 = 18000;
                                                i10 = 4160;
                                                break;
                                            case 128:
                                                j = 160432128;
                                                i8 = 2228224;
                                                i9 = 30000;
                                                i10 = 4160;
                                                break;
                                            case 256:
                                                j3 = 311951360;
                                                i14 = 60000;
                                                i15 = 8384;
                                                i9 = i14;
                                                i8 = 8912896;
                                                int i90 = length5;
                                                i12 = i15;
                                                long j4 = j3;
                                                i11 = i90;
                                                j2 = j4;
                                                break;
                                            case 512:
                                                j3 = 588251136;
                                                i14 = 120000;
                                                i15 = 8384;
                                                i9 = i14;
                                                i8 = 8912896;
                                                int i91 = length5;
                                                i12 = i15;
                                                long j5 = j3;
                                                i11 = i91;
                                                j2 = j5;
                                                break;
                                            case 1024:
                                                j3 = 1176502272;
                                                i14 = 180000;
                                                i15 = 8384;
                                                i9 = i14;
                                                i8 = 8912896;
                                                int i92 = length5;
                                                i12 = i15;
                                                long j6 = j3;
                                                i11 = i92;
                                                j2 = j6;
                                                break;
                                            case 2048:
                                                j3 = 1176502272;
                                                i16 = 180000;
                                                i15 = 16832;
                                                i9 = i16;
                                                i8 = 35651584;
                                                int i93 = length5;
                                                i12 = i15;
                                                long j7 = j3;
                                                i11 = i93;
                                                j2 = j7;
                                                break;
                                            case 4096:
                                                j3 = 2353004544L;
                                                i16 = 240000;
                                                i15 = 16832;
                                                i9 = i16;
                                                i8 = 35651584;
                                                int i94 = length5;
                                                i12 = i15;
                                                long j8 = j3;
                                                i11 = i94;
                                                j2 = j8;
                                                break;
                                            case 8192:
                                                j3 = 4706009088L;
                                                i16 = 480000;
                                                i15 = 16832;
                                                i9 = i16;
                                                i8 = 35651584;
                                                int i95 = length5;
                                                i12 = i15;
                                                long j9 = j3;
                                                i11 = i95;
                                                j2 = j9;
                                                break;
                                            default:
                                                Log.w(str12, "Unrecognized level " + codecProfileLevel6.level + str13 + mimeType);
                                                i2 |= 1;
                                                num3 = num3;
                                                i8 = 0;
                                                i9 = 0;
                                                i11 = length5;
                                                j2 = 0;
                                                i12 = 0;
                                                break;
                                        }
                                        i13 = codecProfileLevel6.profile;
                                        CodecProfileLevel[] codecProfileLevelArr6 = codecProfileLevelArr;
                                        if (i13 == 1 && i13 != 2 && i13 != 4 && i13 != 8 && i13 != 4096 && i13 != 8192) {
                                            Log.w(str12, "Unrecognized profile " + codecProfileLevel6.profile + str13 + mimeType);
                                            i2 |= 1;
                                        }
                                        i2 &= -5;
                                        jMax5 = Math.max(j2, jMax5);
                                        iMax20 = Math.max(i8, iMax20);
                                        iMax22 = Math.max(i9 * 1000, iMax22);
                                        iMax21 = Math.max(i12, iMax21);
                                        i88++;
                                        length5 = i11;
                                        num3 = num3;
                                        codecProfileLevelArr = codecProfileLevelArr6;
                                    } else {
                                        codecProfileLevelArr = codecProfileLevelArr2;
                                        j = 2764800;
                                        i8 = 73728;
                                        i9 = 800;
                                        i10 = 768;
                                    }
                                    num3 = num3;
                                    long j10 = j;
                                    i11 = length5;
                                    j2 = j10;
                                    i12 = i10;
                                    i13 = codecProfileLevel6.profile;
                                    CodecProfileLevel[] codecProfileLevelArr7 = codecProfileLevelArr;
                                    if (i13 == 1) {
                                    }
                                    i2 &= -5;
                                    jMax5 = Math.max(j2, jMax5);
                                    iMax20 = Math.max(i8, iMax20);
                                    iMax22 = Math.max(i9 * 1000, iMax22);
                                    iMax21 = Math.max(i12, iMax21);
                                    i88++;
                                    length5 = i11;
                                    num3 = num3;
                                    codecProfileLevelArr = codecProfileLevelArr7;
                                }
                                codecProfileLevelArr = codecProfileLevelArr2;
                                num = num3;
                                int iDivUp = Utils.divUp(iMax21, 8);
                                applyMacroBlockLimits(iDivUp, iDivUp, Utils.divUp(iMax20, 64), Utils.divUp(jMax5, 64L), 8, 8, 1, 1);
                                i = iMax22;
                            } else {
                                num = num3;
                                if (mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_VIDEO_HEVC)) {
                                    CodecProfileLevel[] codecProfileLevelArr8 = codecProfileLevelArr;
                                    int length6 = codecProfileLevelArr8.length;
                                    long jMax6 = 8640;
                                    int iMax23 = 128000;
                                    int i96 = 4;
                                    int iMax24 = 576;
                                    int i97 = 0;
                                    while (i97 < length6) {
                                        CodecProfileLevel codecProfileLevel7 = codecProfileLevelArr8[i97];
                                        int i98 = codecProfileLevel7.level;
                                        double d4 = 30.0d;
                                        if (i98 != 1 && i98 != 2) {
                                            switch (i98) {
                                                case 4:
                                                case 8:
                                                    i3 = 122880;
                                                    i4 = 1500;
                                                    break;
                                                case 16:
                                                case 32:
                                                    i3 = 245760;
                                                    i4 = PathInterpolatorCompat.MAX_NUM_POINTS;
                                                    break;
                                                case 64:
                                                case 128:
                                                    i3 = 552960;
                                                    i4 = BluetoothHealth.HEALTH_OPERATION_SUCCESS;
                                                    break;
                                                case 256:
                                                case 512:
                                                    d = 33.75d;
                                                    i3 = SurfaceControl.FX_SURFACE_MASK;
                                                    i5 = 10000;
                                                    double d5 = d;
                                                    i4 = i5;
                                                    d4 = d5;
                                                    break;
                                                case 1024:
                                                    i3 = 2228224;
                                                    i4 = ISSErrors.ISS_ERROR_HTTP_BASE;
                                                    break;
                                                case 2048:
                                                    i3 = 2228224;
                                                    i4 = 30000;
                                                    break;
                                                case 4096:
                                                    d = 60.0d;
                                                    i3 = 2228224;
                                                    i5 = 20000;
                                                    double d6 = d;
                                                    i4 = i5;
                                                    d4 = d6;
                                                    break;
                                                case 8192:
                                                    d = 60.0d;
                                                    i3 = 2228224;
                                                    i5 = 50000;
                                                    double d7 = d;
                                                    i4 = i5;
                                                    d4 = d7;
                                                    break;
                                                case 16384:
                                                    i6 = 25000;
                                                    i4 = i6;
                                                    i3 = 8912896;
                                                    break;
                                                case 32768:
                                                    i6 = UserHandle.PER_USER_RANGE;
                                                    i4 = i6;
                                                    i3 = 8912896;
                                                    break;
                                                case 65536:
                                                    d2 = 60.0d;
                                                    i6 = HealthKeys.BASE_PACKAGE;
                                                    d4 = d2;
                                                    i4 = i6;
                                                    i3 = 8912896;
                                                    break;
                                                case 131072:
                                                    d2 = 60.0d;
                                                    i6 = Protocol.BASE_WIFI_SCANNER_SERVICE;
                                                    d4 = d2;
                                                    i4 = i6;
                                                    i3 = 8912896;
                                                    break;
                                                case 262144:
                                                    d2 = 120.0d;
                                                    i6 = 60000;
                                                    d4 = d2;
                                                    i4 = i6;
                                                    i3 = 8912896;
                                                    break;
                                                case 524288:
                                                    d2 = 120.0d;
                                                    i6 = 240000;
                                                    d4 = d2;
                                                    i4 = i6;
                                                    i3 = 8912896;
                                                    break;
                                                case 1048576:
                                                    i7 = 60000;
                                                    i4 = i7;
                                                    i3 = 35651584;
                                                    break;
                                                case 2097152:
                                                    i7 = 240000;
                                                    i4 = i7;
                                                    i3 = 35651584;
                                                    break;
                                                case 4194304:
                                                    d3 = 60.0d;
                                                    i7 = 120000;
                                                    d4 = d3;
                                                    i4 = i7;
                                                    i3 = 35651584;
                                                    break;
                                                case 8388608:
                                                    d3 = 60.0d;
                                                    i7 = 480000;
                                                    d4 = d3;
                                                    i4 = i7;
                                                    i3 = 35651584;
                                                    break;
                                                case 16777216:
                                                    d3 = 120.0d;
                                                    i7 = 240000;
                                                    d4 = d3;
                                                    i4 = i7;
                                                    i3 = 35651584;
                                                    break;
                                                case 33554432:
                                                    d3 = 120.0d;
                                                    i7 = 800000;
                                                    d4 = d3;
                                                    i4 = i7;
                                                    i3 = 35651584;
                                                    break;
                                                default:
                                                    Log.w(str12, "Unrecognized level " + codecProfileLevel7.level + str13 + mimeType);
                                                    i96 |= 1;
                                                    i3 = 0;
                                                    i4 = 0;
                                                    d4 = 0.0d;
                                                    break;
                                            }
                                        } else {
                                            codecProfileLevelArr = codecProfileLevelArr2;
                                            codecProfileLevelArr = codecProfileLevelArr2;
                                            i3 = 36864;
                                            d4 = 15.0d;
                                            i4 = 128;
                                        }
                                        int i99 = codecProfileLevel7.profile;
                                        CodecProfileLevel[] codecProfileLevelArr9 = codecProfileLevelArr8;
                                        if (i99 != 1 && i99 != 2 && i99 != 4096) {
                                            Log.w(str12, "Unrecognized profile " + codecProfileLevel7.profile + str13 + mimeType);
                                            i96 |= 1;
                                        }
                                        int i100 = i3 >> 6;
                                        i96 &= -5;
                                        jMax6 = Math.max((int) (d4 * ((double) i100)), jMax6);
                                        iMax24 = Math.max(i100, iMax24);
                                        iMax23 = Math.max(i4 * 1000, iMax23);
                                        i97++;
                                        codecProfileLevelArr8 = codecProfileLevelArr9;
                                    }
                                    codecProfileLevelArr = codecProfileLevelArr2;
                                    int iSqrt3 = (int) Math.sqrt(iMax24 * 8);
                                    applyMacroBlockLimits(iSqrt3, iSqrt3, iMax24, jMax6, 8, 8, 1, 1);
                                    i = iMax23;
                                    i2 = i96;
                                } else {
                                    codecProfileLevelArr = codecProfileLevelArr2;
                                    Log.w(str12, "Unsupported mime " + mimeType);
                                    i = 64000;
                                    i2 = 6;
                                }
                            }
                        }
                    }
                }
                i = iMax;
            }
            this.mBitrateRange = Range.create(num, Integer.valueOf(i));
            this.mParent.mError |= i2;
        }
    }

    public static final class EncoderCapabilities {
        public static final int BITRATE_MODE_CBR = 2;
        public static final int BITRATE_MODE_CQ = 0;
        public static final int BITRATE_MODE_VBR = 1;
        private static final Feature[] bitrates = {new Feature("VBR", 1, true), new Feature("CBR", 2, false), new Feature("CQ", 0, false)};
        private int mBitControl;
        private Range<Integer> mComplexityRange;
        private Integer mDefaultComplexity;
        private Integer mDefaultQuality;
        private CodecCapabilities mParent;
        private Range<Integer> mQualityRange;
        private String mQualityScale;

        public Range<Integer> getQualityRange() {
            return this.mQualityRange;
        }

        public Range<Integer> getComplexityRange() {
            return this.mComplexityRange;
        }

        private static int parseBitrateMode(String str) {
            for (Feature feature : bitrates) {
                if (feature.mName.equalsIgnoreCase(str)) {
                    return feature.mValue;
                }
            }
            return 0;
        }

        public boolean isBitrateModeSupported(int i) {
            for (Feature feature : bitrates) {
                if (i == feature.mValue) {
                    return ((1 << i) & this.mBitControl) != 0;
                }
            }
            return false;
        }

        private EncoderCapabilities() {
        }

        public static EncoderCapabilities create(MediaFormat mediaFormat, CodecCapabilities codecCapabilities) {
            EncoderCapabilities encoderCapabilities = new EncoderCapabilities();
            encoderCapabilities.init(mediaFormat, codecCapabilities);
            return encoderCapabilities;
        }

        private void init(MediaFormat mediaFormat, CodecCapabilities codecCapabilities) {
            this.mParent = codecCapabilities;
            this.mComplexityRange = Range.create(0, 0);
            this.mQualityRange = Range.create(0, 0);
            this.mBitControl = 2;
            applyLevelLimits();
            parseFromInfo(mediaFormat);
        }

        private void applyLevelLimits() {
            String mimeType = this.mParent.getMimeType();
            if (mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_FLAC)) {
                this.mComplexityRange = Range.create(0, 8);
                this.mBitControl = 1;
            } else if (mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_AMR_NB) || mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_AMR_WB) || mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_G711_ALAW) || mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_G711_MLAW) || mimeType.equalsIgnoreCase(MediaFormat.MIMETYPE_AUDIO_MSGSM)) {
                this.mBitControl = 4;
            }
        }

        private void parseFromInfo(MediaFormat mediaFormat) {
            Map<String, Object> map = mediaFormat.getMap();
            if (mediaFormat.containsKey("complexity-range")) {
                this.mComplexityRange = Utils.parseIntRange(mediaFormat.getString("complexity-range"), this.mComplexityRange);
            }
            if (mediaFormat.containsKey("quality-range")) {
                this.mQualityRange = Utils.parseIntRange(mediaFormat.getString("quality-range"), this.mQualityRange);
            }
            if (mediaFormat.containsKey("feature-bitrate-modes")) {
                for (String str : mediaFormat.getString("feature-bitrate-modes").split(",")) {
                    this.mBitControl = (1 << parseBitrateMode(str)) | this.mBitControl;
                }
            }
            try {
                this.mDefaultComplexity = Integer.valueOf(Integer.parseInt((String) map.get("complexity-default")));
            } catch (NumberFormatException unused) {
            }
            try {
                this.mDefaultQuality = Integer.valueOf(Integer.parseInt((String) map.get("quality-default")));
            } catch (NumberFormatException unused2) {
            }
            this.mQualityScale = (String) map.get("quality-scale");
        }

        private boolean supports(Integer num, Integer num2, Integer num3) {
            boolean zContains = num != null ? this.mComplexityRange.contains(num) : true;
            if (zContains && num2 != null) {
                zContains = this.mQualityRange.contains(num2);
            }
            if (!zContains || num3 == null) {
                return zContains;
            }
            for (CodecProfileLevel codecProfileLevel : this.mParent.profileLevels) {
                if (codecProfileLevel.profile == num3.intValue()) {
                    num3 = null;
                    break;
                }
            }
            return num3 == null;
        }

        public void getDefaultFormat(MediaFormat mediaFormat) {
            Integer num;
            Integer num2;
            if (!((Integer) this.mQualityRange.getUpper()).equals(this.mQualityRange.getLower()) && (num2 = this.mDefaultQuality) != null) {
                mediaFormat.setInteger(MediaFormat.KEY_QUALITY, num2.intValue());
            }
            if (!((Integer) this.mComplexityRange.getUpper()).equals(this.mComplexityRange.getLower()) && (num = this.mDefaultComplexity) != null) {
                mediaFormat.setInteger(MediaFormat.KEY_COMPLEXITY, num.intValue());
            }
            for (Feature feature : bitrates) {
                if ((this.mBitControl & (1 << feature.mValue)) != 0) {
                    mediaFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, feature.mValue);
                    return;
                }
            }
        }

        public boolean supportsFormat(MediaFormat mediaFormat) {
            Map<String, Object> map = mediaFormat.getMap();
            String mimeType = this.mParent.getMimeType();
            Integer num = (Integer) map.get(MediaFormat.KEY_BITRATE_MODE);
            if (num != null && !isBitrateModeSupported(num.intValue())) {
                return false;
            }
            Integer num2 = (Integer) map.get(MediaFormat.KEY_COMPLEXITY);
            if (MediaFormat.MIMETYPE_AUDIO_FLAC.equalsIgnoreCase(mimeType)) {
                Integer num3 = (Integer) map.get(MediaFormat.KEY_FLAC_COMPRESSION_LEVEL);
                if (num2 == null) {
                    num2 = num3;
                } else if (num3 != null && !num2.equals(num3)) {
                    throw new IllegalArgumentException("conflicting values for complexity and flac-compression-level");
                }
            }
            Integer num4 = (Integer) map.get(MediaFormat.KEY_PROFILE);
            if (MediaFormat.MIMETYPE_AUDIO_AAC.equalsIgnoreCase(mimeType)) {
                Integer num5 = (Integer) map.get(MediaFormat.KEY_AAC_PROFILE);
                if (num4 == null) {
                    num4 = num5;
                } else if (num5 != null && !num5.equals(num4)) {
                    throw new IllegalArgumentException("conflicting values for profile and aac-profile");
                }
            }
            return supports(num2, (Integer) map.get(MediaFormat.KEY_QUALITY), num4);
        }
    }

    public static final class CodecProfileLevel {
        public static final int AACObjectELD = 39;
        public static final int AACObjectERLC = 17;
        public static final int AACObjectERScalable = 20;
        public static final int AACObjectHE = 5;
        public static final int AACObjectHE_PS = 29;
        public static final int AACObjectLC = 2;
        public static final int AACObjectLD = 23;
        public static final int AACObjectLTP = 4;
        public static final int AACObjectMain = 1;
        public static final int AACObjectSSR = 3;
        public static final int AACObjectScalable = 6;
        public static final int AACObjectXHE = 42;
        public static final int AVCLevel1 = 1;
        public static final int AVCLevel11 = 4;
        public static final int AVCLevel12 = 8;
        public static final int AVCLevel13 = 16;
        public static final int AVCLevel1b = 2;
        public static final int AVCLevel2 = 32;
        public static final int AVCLevel21 = 64;
        public static final int AVCLevel22 = 128;
        public static final int AVCLevel3 = 256;
        public static final int AVCLevel31 = 512;
        public static final int AVCLevel32 = 1024;
        public static final int AVCLevel4 = 2048;
        public static final int AVCLevel41 = 4096;
        public static final int AVCLevel42 = 8192;
        public static final int AVCLevel5 = 16384;
        public static final int AVCLevel51 = 32768;
        public static final int AVCLevel52 = 65536;
        public static final int AVCProfileBaseline = 1;
        public static final int AVCProfileConstrainedBaseline = 65536;
        public static final int AVCProfileConstrainedHigh = 524288;
        public static final int AVCProfileExtended = 4;
        public static final int AVCProfileHigh = 8;
        public static final int AVCProfileHigh10 = 16;
        public static final int AVCProfileHigh422 = 32;
        public static final int AVCProfileHigh444 = 64;
        public static final int AVCProfileMain = 2;
        public static final int DolbyVisionLevelFhd24 = 4;
        public static final int DolbyVisionLevelFhd30 = 8;
        public static final int DolbyVisionLevelFhd60 = 16;
        public static final int DolbyVisionLevelHd24 = 1;
        public static final int DolbyVisionLevelHd30 = 2;
        public static final int DolbyVisionLevelUhd24 = 32;
        public static final int DolbyVisionLevelUhd30 = 64;
        public static final int DolbyVisionLevelUhd48 = 128;
        public static final int DolbyVisionLevelUhd60 = 256;
        public static final int DolbyVisionProfileDvavPen = 2;
        public static final int DolbyVisionProfileDvavPer = 1;
        public static final int DolbyVisionProfileDvavSe = 512;
        public static final int DolbyVisionProfileDvheDen = 8;
        public static final int DolbyVisionProfileDvheDer = 4;
        public static final int DolbyVisionProfileDvheDtb = 128;
        public static final int DolbyVisionProfileDvheDth = 64;
        public static final int DolbyVisionProfileDvheDtr = 16;
        public static final int DolbyVisionProfileDvheSt = 256;
        public static final int DolbyVisionProfileDvheStn = 32;
        public static final int H263Level10 = 1;
        public static final int H263Level20 = 2;
        public static final int H263Level30 = 4;
        public static final int H263Level40 = 8;
        public static final int H263Level45 = 16;
        public static final int H263Level50 = 32;
        public static final int H263Level60 = 64;
        public static final int H263Level70 = 128;
        public static final int H263ProfileBackwardCompatible = 4;
        public static final int H263ProfileBaseline = 1;
        public static final int H263ProfileH320Coding = 2;
        public static final int H263ProfileHighCompression = 32;
        public static final int H263ProfileHighLatency = 256;
        public static final int H263ProfileISWV2 = 8;
        public static final int H263ProfileISWV3 = 16;
        public static final int H263ProfileInterlace = 128;
        public static final int H263ProfileInternet = 64;
        public static final int HEVCHighTierLevel1 = 2;
        public static final int HEVCHighTierLevel2 = 8;
        public static final int HEVCHighTierLevel21 = 32;
        public static final int HEVCHighTierLevel3 = 128;
        public static final int HEVCHighTierLevel31 = 512;
        public static final int HEVCHighTierLevel4 = 2048;
        public static final int HEVCHighTierLevel41 = 8192;
        public static final int HEVCHighTierLevel5 = 32768;
        public static final int HEVCHighTierLevel51 = 131072;
        public static final int HEVCHighTierLevel52 = 524288;
        public static final int HEVCHighTierLevel6 = 2097152;
        public static final int HEVCHighTierLevel61 = 8388608;
        public static final int HEVCHighTierLevel62 = 33554432;
        private static final int HEVCHighTierLevels = 44739242;
        public static final int HEVCMainTierLevel1 = 1;
        public static final int HEVCMainTierLevel2 = 4;
        public static final int HEVCMainTierLevel21 = 16;
        public static final int HEVCMainTierLevel3 = 64;
        public static final int HEVCMainTierLevel31 = 256;
        public static final int HEVCMainTierLevel4 = 1024;
        public static final int HEVCMainTierLevel41 = 4096;
        public static final int HEVCMainTierLevel5 = 16384;
        public static final int HEVCMainTierLevel51 = 65536;
        public static final int HEVCMainTierLevel52 = 262144;
        public static final int HEVCMainTierLevel6 = 1048576;
        public static final int HEVCMainTierLevel61 = 4194304;
        public static final int HEVCMainTierLevel62 = 16777216;
        public static final int HEVCProfileMain = 1;
        public static final int HEVCProfileMain10 = 2;
        public static final int HEVCProfileMain10HDR10 = 4096;
        public static final int HEVCProfileMainStill = 4;
        public static final int MPEG2LevelH14 = 2;
        public static final int MPEG2LevelHL = 3;
        public static final int MPEG2LevelHP = 4;
        public static final int MPEG2LevelLL = 0;
        public static final int MPEG2LevelML = 1;
        public static final int MPEG2Profile422 = 2;
        public static final int MPEG2ProfileHigh = 5;
        public static final int MPEG2ProfileMain = 1;
        public static final int MPEG2ProfileSNR = 3;
        public static final int MPEG2ProfileSimple = 0;
        public static final int MPEG2ProfileSpatial = 4;
        public static final int MPEG4Level0 = 1;
        public static final int MPEG4Level0b = 2;
        public static final int MPEG4Level1 = 4;
        public static final int MPEG4Level2 = 8;
        public static final int MPEG4Level3 = 16;
        public static final int MPEG4Level3b = 24;
        public static final int MPEG4Level4 = 32;
        public static final int MPEG4Level4a = 64;
        public static final int MPEG4Level5 = 128;
        public static final int MPEG4Level6 = 256;
        public static final int MPEG4ProfileAdvancedCoding = 4096;
        public static final int MPEG4ProfileAdvancedCore = 8192;
        public static final int MPEG4ProfileAdvancedRealTime = 1024;
        public static final int MPEG4ProfileAdvancedScalable = 16384;
        public static final int MPEG4ProfileAdvancedSimple = 32768;
        public static final int MPEG4ProfileBasicAnimated = 256;
        public static final int MPEG4ProfileCore = 4;
        public static final int MPEG4ProfileCoreScalable = 2048;
        public static final int MPEG4ProfileHybrid = 512;
        public static final int MPEG4ProfileMain = 8;
        public static final int MPEG4ProfileNbit = 16;
        public static final int MPEG4ProfileScalableTexture = 32;
        public static final int MPEG4ProfileSimple = 1;
        public static final int MPEG4ProfileSimpleFBA = 128;
        public static final int MPEG4ProfileSimpleFace = 64;
        public static final int MPEG4ProfileSimpleScalable = 2;
        public static final int VP8Level_Version0 = 1;
        public static final int VP8Level_Version1 = 2;
        public static final int VP8Level_Version2 = 4;
        public static final int VP8Level_Version3 = 8;
        public static final int VP8ProfileMain = 1;
        public static final int VP9Level1 = 1;
        public static final int VP9Level11 = 2;
        public static final int VP9Level2 = 4;
        public static final int VP9Level21 = 8;
        public static final int VP9Level3 = 16;
        public static final int VP9Level31 = 32;
        public static final int VP9Level4 = 64;
        public static final int VP9Level41 = 128;
        public static final int VP9Level5 = 256;
        public static final int VP9Level51 = 512;
        public static final int VP9Level52 = 1024;
        public static final int VP9Level6 = 2048;
        public static final int VP9Level61 = 4096;
        public static final int VP9Level62 = 8192;
        public static final int VP9Profile0 = 1;
        public static final int VP9Profile1 = 2;
        public static final int VP9Profile2 = 4;
        public static final int VP9Profile2HDR = 4096;
        public static final int VP9Profile3 = 8;
        public static final int VP9Profile3HDR = 8192;
        public int level;
        public int profile;

        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof CodecProfileLevel)) {
                return false;
            }
            CodecProfileLevel codecProfileLevel = (CodecProfileLevel) obj;
            return codecProfileLevel.profile == this.profile && codecProfileLevel.level == this.level;
        }

        public int hashCode() {
            return Long.hashCode((((long) this.profile) << 32) | ((long) this.level));
        }
    }

    public final CodecCapabilities getCapabilitiesForType(String str) {
        CodecCapabilities codecCapabilities = this.mCaps.get(str);
        if (codecCapabilities == null) {
            throw new IllegalArgumentException("codec does not support type");
        }
        return codecCapabilities.dup();
    }

    public MediaCodecInfo makeRegular() {
        ArrayList arrayList = new ArrayList();
        for (CodecCapabilities codecCapabilities : this.mCaps.values()) {
            if (codecCapabilities.isRegular()) {
                arrayList.add(codecCapabilities);
            }
        }
        if (arrayList.size() == 0) {
            return null;
        }
        return arrayList.size() == this.mCaps.size() ? this : new MediaCodecInfo(this.mName, this.mIsEncoder, (CodecCapabilities[]) arrayList.toArray(new CodecCapabilities[arrayList.size()]));
    }
}
