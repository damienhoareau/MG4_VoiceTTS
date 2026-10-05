package android.hardware.camera2.params;

import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.utils.HashCodeHelpers;
import android.hardware.camera2.utils.SurfaceUtils;
import android.media.ImageReader;
import android.media.MediaCodec;
import android.media.MediaRecorder;
import android.renderscript.Allocation;
import android.util.Range;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.util.Preconditions;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public final class StreamConfigurationMap {
    private static final long DURATION_20FPS_NS = 50000000;
    private static final int DURATION_MIN_FRAME = 0;
    private static final int DURATION_STALL = 1;
    private static final int HAL_DATASPACE_DEPTH = 4096;
    private static final int HAL_DATASPACE_RANGE_SHIFT = 27;
    private static final int HAL_DATASPACE_STANDARD_SHIFT = 16;
    private static final int HAL_DATASPACE_TRANSFER_SHIFT = 22;
    private static final int HAL_DATASPACE_UNKNOWN = 0;
    private static final int HAL_DATASPACE_V0_JFIF = 146931712;
    private static final int HAL_PIXEL_FORMAT_BLOB = 33;
    private static final int HAL_PIXEL_FORMAT_IMPLEMENTATION_DEFINED = 34;
    private static final int HAL_PIXEL_FORMAT_RAW10 = 37;
    private static final int HAL_PIXEL_FORMAT_RAW12 = 38;
    private static final int HAL_PIXEL_FORMAT_RAW16 = 32;
    private static final int HAL_PIXEL_FORMAT_RAW_OPAQUE = 36;
    private static final int HAL_PIXEL_FORMAT_Y16 = 540422489;
    private static final int HAL_PIXEL_FORMAT_YCbCr_420_888 = 35;
    private static final String TAG = "StreamConfigurationMap";
    private final StreamConfiguration[] mConfigurations;
    private final StreamConfiguration[] mDepthConfigurations;
    private final StreamConfigurationDuration[] mDepthMinFrameDurations;
    private final StreamConfigurationDuration[] mDepthStallDurations;
    private final HighSpeedVideoConfiguration[] mHighSpeedVideoConfigurations;
    private final ReprocessFormatsMap mInputOutputFormatsMap;
    private final boolean mListHighResolution;
    private final StreamConfigurationDuration[] mMinFrameDurations;
    private final StreamConfigurationDuration[] mStallDurations;
    private final SparseIntArray mOutputFormats = new SparseIntArray();
    private final SparseIntArray mHighResOutputFormats = new SparseIntArray();
    private final SparseIntArray mAllOutputFormats = new SparseIntArray();
    private final SparseIntArray mInputFormats = new SparseIntArray();
    private final SparseIntArray mDepthOutputFormats = new SparseIntArray();
    private final HashMap<Size, Integer> mHighSpeedVideoSizeMap = new HashMap<>();
    private final HashMap<Range<Integer>, Integer> mHighSpeedVideoFpsRangeMap = new HashMap<>();

    private String formatToString(int i) {
        if (i == 1) {
            return "RGBA_8888";
        }
        if (i == 2) {
            return "RGBX_8888";
        }
        if (i == 3) {
            return "RGB_888";
        }
        if (i == 4) {
            return "RGB_565";
        }
        if (i == 16) {
            return "NV16";
        }
        if (i == 17) {
            return "NV21";
        }
        if (i == 256) {
            return "JPEG";
        }
        if (i == 257) {
            return "DEPTH_POINT_CLOUD";
        }
        switch (i) {
            case 20:
                return "YUY2";
            case 32:
                return "RAW_SENSOR";
            case 4098:
                return "RAW_DEPTH";
            case ImageFormat.Y8 /* 538982489 */:
                return "Y8";
            case 540422489:
                return "Y16";
            case ImageFormat.YV12 /* 842094169 */:
                return "YV12";
            case ImageFormat.DEPTH16 /* 1144402265 */:
                return "DEPTH16";
            default:
                switch (i) {
                    case 34:
                        return "PRIVATE";
                    case 35:
                        return "YUV_420_888";
                    case 36:
                        return "RAW_PRIVATE";
                    case 37:
                        return "RAW10";
                    default:
                        return IccCardConstants.INTENT_VALUE_ICC_UNKNOWN;
                }
        }
    }

    static int imageFormatToDataspace(int i) {
        if (i != 256) {
            return (i == 257 || i == 4098 || i == 1144402265) ? 4096 : 0;
        }
        return HAL_DATASPACE_V0_JFIF;
    }

    static int imageFormatToInternal(int i) {
        if (i == 256 || i == 257) {
            return 33;
        }
        if (i == 4098) {
            return 32;
        }
        if (i != 1144402265) {
            return i;
        }
        return 540422489;
    }

    public StreamConfigurationMap(StreamConfiguration[] streamConfigurationArr, StreamConfigurationDuration[] streamConfigurationDurationArr, StreamConfigurationDuration[] streamConfigurationDurationArr2, StreamConfiguration[] streamConfigurationArr2, StreamConfigurationDuration[] streamConfigurationDurationArr3, StreamConfigurationDuration[] streamConfigurationDurationArr4, HighSpeedVideoConfiguration[] highSpeedVideoConfigurationArr, ReprocessFormatsMap reprocessFormatsMap, boolean z) {
        SparseIntArray sparseIntArray;
        int i = 0;
        if (streamConfigurationArr == null) {
            Preconditions.checkArrayElementsNotNull(streamConfigurationArr2, "depthConfigurations");
            this.mConfigurations = new StreamConfiguration[0];
            this.mMinFrameDurations = new StreamConfigurationDuration[0];
            this.mStallDurations = new StreamConfigurationDuration[0];
        } else {
            this.mConfigurations = (StreamConfiguration[]) Preconditions.checkArrayElementsNotNull(streamConfigurationArr, "configurations");
            this.mMinFrameDurations = (StreamConfigurationDuration[]) Preconditions.checkArrayElementsNotNull(streamConfigurationDurationArr, "minFrameDurations");
            this.mStallDurations = (StreamConfigurationDuration[]) Preconditions.checkArrayElementsNotNull(streamConfigurationDurationArr2, "stallDurations");
        }
        this.mListHighResolution = z;
        if (streamConfigurationArr2 == null) {
            this.mDepthConfigurations = new StreamConfiguration[0];
            this.mDepthMinFrameDurations = new StreamConfigurationDuration[0];
            this.mDepthStallDurations = new StreamConfigurationDuration[0];
        } else {
            this.mDepthConfigurations = (StreamConfiguration[]) Preconditions.checkArrayElementsNotNull(streamConfigurationArr2, "depthConfigurations");
            this.mDepthMinFrameDurations = (StreamConfigurationDuration[]) Preconditions.checkArrayElementsNotNull(streamConfigurationDurationArr3, "depthMinFrameDurations");
            this.mDepthStallDurations = (StreamConfigurationDuration[]) Preconditions.checkArrayElementsNotNull(streamConfigurationDurationArr4, "depthStallDurations");
        }
        if (highSpeedVideoConfigurationArr == null) {
            this.mHighSpeedVideoConfigurations = new HighSpeedVideoConfiguration[0];
        } else {
            this.mHighSpeedVideoConfigurations = (HighSpeedVideoConfiguration[]) Preconditions.checkArrayElementsNotNull(highSpeedVideoConfigurationArr, "highSpeedVideoConfigurations");
        }
        StreamConfiguration[] streamConfigurationArr3 = this.mConfigurations;
        int length = streamConfigurationArr3.length;
        int i2 = 0;
        while (i2 < length) {
            StreamConfiguration streamConfiguration = streamConfigurationArr3[i2];
            int format = streamConfiguration.getFormat();
            if (streamConfiguration.isOutput()) {
                SparseIntArray sparseIntArray2 = this.mAllOutputFormats;
                sparseIntArray2.put(format, sparseIntArray2.get(format) + 1);
                long duration = 0;
                if (this.mListHighResolution) {
                    StreamConfigurationDuration[] streamConfigurationDurationArr5 = this.mMinFrameDurations;
                    int length2 = streamConfigurationDurationArr5.length;
                    for (int i3 = i; i3 < length2; i3++) {
                        StreamConfigurationDuration streamConfigurationDuration = streamConfigurationDurationArr5[i3];
                        if (streamConfigurationDuration.getFormat() == format && streamConfigurationDuration.getWidth() == streamConfiguration.getSize().getWidth() && streamConfigurationDuration.getHeight() == streamConfiguration.getSize().getHeight()) {
                            duration = streamConfigurationDuration.getDuration();
                            break;
                        }
                    }
                }
                sparseIntArray = duration <= DURATION_20FPS_NS ? this.mOutputFormats : this.mHighResOutputFormats;
            } else {
                sparseIntArray = this.mInputFormats;
            }
            sparseIntArray.put(format, sparseIntArray.get(format) + 1);
            i2++;
            i = 0;
        }
        for (StreamConfiguration streamConfiguration2 : this.mDepthConfigurations) {
            if (streamConfiguration2.isOutput()) {
                this.mDepthOutputFormats.put(streamConfiguration2.getFormat(), this.mDepthOutputFormats.get(streamConfiguration2.getFormat()) + 1);
            }
        }
        if (streamConfigurationArr != null && this.mOutputFormats.indexOfKey(34) < 0) {
            throw new AssertionError("At least one stream configuration for IMPLEMENTATION_DEFINED must exist");
        }
        for (HighSpeedVideoConfiguration highSpeedVideoConfiguration : this.mHighSpeedVideoConfigurations) {
            Size size = highSpeedVideoConfiguration.getSize();
            Range<Integer> fpsRange = highSpeedVideoConfiguration.getFpsRange();
            Integer num = this.mHighSpeedVideoSizeMap.get(size);
            this.mHighSpeedVideoSizeMap.put(size, Integer.valueOf((num == null ? 0 : num).intValue() + 1));
            Integer num2 = this.mHighSpeedVideoFpsRangeMap.get(fpsRange);
            if (num2 == null) {
                num2 = 0;
            }
            this.mHighSpeedVideoFpsRangeMap.put(fpsRange, Integer.valueOf(num2.intValue() + 1));
        }
        this.mInputOutputFormatsMap = reprocessFormatsMap;
    }

    public final int[] getOutputFormats() {
        return getPublicFormats(true);
    }

    public final int[] getValidOutputFormatsForInput(int i) {
        ReprocessFormatsMap reprocessFormatsMap = this.mInputOutputFormatsMap;
        return reprocessFormatsMap == null ? new int[0] : reprocessFormatsMap.getOutputs(i);
    }

    public final int[] getInputFormats() {
        return getPublicFormats(false);
    }

    public Size[] getInputSizes(int i) {
        return getPublicFormatSizes(i, false, false);
    }

    public boolean isOutputSupportedFor(int i) {
        checkArgumentFormat(i);
        int iImageFormatToInternal = imageFormatToInternal(i);
        if (imageFormatToDataspace(i) == 4096) {
            return this.mDepthOutputFormats.indexOfKey(iImageFormatToInternal) >= 0;
        }
        return getFormatsMap(true).indexOfKey(iImageFormatToInternal) >= 0;
    }

    public static <T> boolean isOutputSupportedFor(Class<T> cls) {
        Preconditions.checkNotNull(cls, "klass must not be null");
        return cls == ImageReader.class || cls == MediaRecorder.class || cls == MediaCodec.class || cls == Allocation.class || cls == SurfaceHolder.class || cls == SurfaceTexture.class;
    }

    public boolean isOutputSupportedFor(Surface surface) {
        Preconditions.checkNotNull(surface, "surface must not be null");
        Size surfaceSize = SurfaceUtils.getSurfaceSize(surface);
        int surfaceFormat = SurfaceUtils.getSurfaceFormat(surface);
        int surfaceDataspace = SurfaceUtils.getSurfaceDataspace(surface);
        boolean zIsFlexibleConsumer = SurfaceUtils.isFlexibleConsumer(surface);
        for (StreamConfiguration streamConfiguration : surfaceDataspace != 4096 ? this.mConfigurations : this.mDepthConfigurations) {
            if (streamConfiguration.getFormat() == surfaceFormat && streamConfiguration.isOutput()) {
                if (streamConfiguration.getSize().equals(surfaceSize)) {
                    return true;
                }
                if (zIsFlexibleConsumer && streamConfiguration.getSize().getWidth() <= 1920) {
                    return true;
                }
            }
        }
        return false;
    }

    public <T> Size[] getOutputSizes(Class<T> cls) {
        if (isOutputSupportedFor(cls)) {
            return getInternalFormatSizes(34, 0, true, false);
        }
        return null;
    }

    public Size[] getOutputSizes(int i) {
        return getPublicFormatSizes(i, true, false);
    }

    public Size[] getHighSpeedVideoSizes() {
        Set<Size> setKeySet = this.mHighSpeedVideoSizeMap.keySet();
        return (Size[]) setKeySet.toArray(new Size[setKeySet.size()]);
    }

    public Range<Integer>[] getHighSpeedVideoFpsRangesFor(Size size) {
        Integer num = this.mHighSpeedVideoSizeMap.get(size);
        if (num == null || num.intValue() == 0) {
            throw new IllegalArgumentException(String.format("Size %s does not support high speed video recording", size));
        }
        Range<Integer>[] rangeArr = new Range[num.intValue()];
        int i = 0;
        for (HighSpeedVideoConfiguration highSpeedVideoConfiguration : this.mHighSpeedVideoConfigurations) {
            if (size.equals(highSpeedVideoConfiguration.getSize())) {
                rangeArr[i] = highSpeedVideoConfiguration.getFpsRange();
                i++;
            }
        }
        return rangeArr;
    }

    public Range<Integer>[] getHighSpeedVideoFpsRanges() {
        Set<Range<Integer>> setKeySet = this.mHighSpeedVideoFpsRangeMap.keySet();
        return (Range[]) setKeySet.toArray(new Range[setKeySet.size()]);
    }

    public Size[] getHighSpeedVideoSizesFor(Range<Integer> range) {
        Integer num = this.mHighSpeedVideoFpsRangeMap.get(range);
        if (num == null || num.intValue() == 0) {
            throw new IllegalArgumentException(String.format("FpsRange %s does not support high speed video recording", range));
        }
        Size[] sizeArr = new Size[num.intValue()];
        int i = 0;
        for (HighSpeedVideoConfiguration highSpeedVideoConfiguration : this.mHighSpeedVideoConfigurations) {
            if (range.equals(highSpeedVideoConfiguration.getFpsRange())) {
                sizeArr[i] = highSpeedVideoConfiguration.getSize();
                i++;
            }
        }
        return sizeArr;
    }

    public Size[] getHighResolutionOutputSizes(int i) {
        if (this.mListHighResolution) {
            return getPublicFormatSizes(i, true, true);
        }
        return null;
    }

    public long getOutputMinFrameDuration(int i, Size size) {
        Preconditions.checkNotNull(size, "size must not be null");
        checkArgumentFormatSupported(i, true);
        return getInternalFormatDuration(imageFormatToInternal(i), imageFormatToDataspace(i), size, 0);
    }

    public <T> long getOutputMinFrameDuration(Class<T> cls, Size size) {
        if (!isOutputSupportedFor(cls)) {
            throw new IllegalArgumentException("klass was not supported");
        }
        return getInternalFormatDuration(34, 0, size, 0);
    }

    public long getOutputStallDuration(int i, Size size) {
        checkArgumentFormatSupported(i, true);
        return getInternalFormatDuration(imageFormatToInternal(i), imageFormatToDataspace(i), size, 1);
    }

    public <T> long getOutputStallDuration(Class<T> cls, Size size) {
        if (!isOutputSupportedFor(cls)) {
            throw new IllegalArgumentException("klass was not supported");
        }
        return getInternalFormatDuration(34, 0, size, 1);
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof StreamConfigurationMap)) {
            return false;
        }
        StreamConfigurationMap streamConfigurationMap = (StreamConfigurationMap) obj;
        return Arrays.equals(this.mConfigurations, streamConfigurationMap.mConfigurations) && Arrays.equals(this.mMinFrameDurations, streamConfigurationMap.mMinFrameDurations) && Arrays.equals(this.mStallDurations, streamConfigurationMap.mStallDurations) && Arrays.equals(this.mDepthConfigurations, streamConfigurationMap.mDepthConfigurations) && Arrays.equals(this.mHighSpeedVideoConfigurations, streamConfigurationMap.mHighSpeedVideoConfigurations);
    }

    public int hashCode() {
        return HashCodeHelpers.hashCodeGeneric(this.mConfigurations, this.mMinFrameDurations, this.mStallDurations, this.mDepthConfigurations, this.mHighSpeedVideoConfigurations);
    }

    private int checkArgumentFormatSupported(int i, boolean z) {
        checkArgumentFormat(i);
        int iImageFormatToInternal = imageFormatToInternal(i);
        int iImageFormatToDataspace = imageFormatToDataspace(i);
        if (z) {
            if (iImageFormatToDataspace == 4096) {
                if (this.mDepthOutputFormats.indexOfKey(iImageFormatToInternal) >= 0) {
                    return i;
                }
            } else if (this.mAllOutputFormats.indexOfKey(iImageFormatToInternal) >= 0) {
                return i;
            }
        } else if (this.mInputFormats.indexOfKey(iImageFormatToInternal) >= 0) {
            return i;
        }
        throw new IllegalArgumentException(String.format("format %x is not supported by this stream configuration map", Integer.valueOf(i)));
    }

    static int checkArgumentFormatInternal(int i) {
        if (i != 33 && i != 34 && i != 36) {
            if (i == 256) {
                throw new IllegalArgumentException("ImageFormat.JPEG is an unknown internal format");
            }
            if (i != 540422489) {
                return checkArgumentFormat(i);
            }
        }
        return i;
    }

    static int checkArgumentFormat(int i) {
        if (ImageFormat.isPublicFormat(i) || PixelFormat.isPublicFormat(i)) {
            return i;
        }
        throw new IllegalArgumentException(String.format("format 0x%x was not defined in either ImageFormat or PixelFormat", Integer.valueOf(i)));
    }

    static int imageFormatToPublic(int i) {
        if (i == 33) {
            return 256;
        }
        if (i != 256) {
            return i;
        }
        throw new IllegalArgumentException("ImageFormat.JPEG is an unknown internal format");
    }

    static int depthFormatToPublic(int i) {
        if (i == 256) {
            throw new IllegalArgumentException("ImageFormat.JPEG is an unknown internal format");
        }
        if (i == 540422489) {
            return ImageFormat.DEPTH16;
        }
        switch (i) {
            case 32:
                return 4098;
            case 33:
                return 257;
            case 34:
                throw new IllegalArgumentException("IMPLEMENTATION_DEFINED must not leak to public API");
            default:
                throw new IllegalArgumentException("Unknown DATASPACE_DEPTH format " + i);
        }
    }

    static int[] imageFormatToPublic(int[] iArr) {
        if (iArr == null) {
            return null;
        }
        for (int i = 0; i < iArr.length; i++) {
            iArr[i] = imageFormatToPublic(iArr[i]);
        }
        return iArr;
    }

    public static int[] imageFormatToInternal(int[] iArr) {
        if (iArr == null) {
            return null;
        }
        for (int i = 0; i < iArr.length; i++) {
            iArr[i] = imageFormatToInternal(iArr[i]);
        }
        return iArr;
    }

    private Size[] getPublicFormatSizes(int i, boolean z, boolean z2) {
        try {
            checkArgumentFormatSupported(i, z);
            return getInternalFormatSizes(imageFormatToInternal(i), imageFormatToDataspace(i), z, z2);
        } catch (IllegalArgumentException unused) {
            return null;
        }
    }

    private Size[] getInternalFormatSizes(int i, int i2, boolean z, boolean z2) {
        SparseIntArray sparseIntArray;
        char c;
        StreamConfigurationMap streamConfigurationMap = this;
        int i3 = i;
        char c2 = 4096;
        if (i2 == 4096 && z2) {
            return new Size[0];
        }
        if (!z) {
            sparseIntArray = streamConfigurationMap.mInputFormats;
        } else if (i2 == 4096) {
            sparseIntArray = streamConfigurationMap.mDepthOutputFormats;
        } else if (z2) {
            sparseIntArray = streamConfigurationMap.mHighResOutputFormats;
        } else {
            sparseIntArray = streamConfigurationMap.mOutputFormats;
        }
        int i4 = sparseIntArray.get(i3);
        if (((!z || i2 == 4096) && i4 == 0) || (z && i2 != 4096 && streamConfigurationMap.mAllOutputFormats.get(i3) == 0)) {
            throw new IllegalArgumentException("format not available");
        }
        Size[] sizeArr = new Size[i4];
        StreamConfiguration[] streamConfigurationArr = i2 == 4096 ? streamConfigurationMap.mDepthConfigurations : streamConfigurationMap.mConfigurations;
        StreamConfigurationDuration[] streamConfigurationDurationArr = i2 == 4096 ? streamConfigurationMap.mDepthMinFrameDurations : streamConfigurationMap.mMinFrameDurations;
        int length = streamConfigurationArr.length;
        int i5 = 0;
        int i6 = 0;
        while (i5 < length) {
            StreamConfiguration streamConfiguration = streamConfigurationArr[i5];
            int format = streamConfiguration.getFormat();
            if (format == i3 && streamConfiguration.isOutput() == z) {
                if (z && streamConfigurationMap.mListHighResolution) {
                    long duration = 0;
                    for (StreamConfigurationDuration streamConfigurationDuration : streamConfigurationDurationArr) {
                        if (streamConfigurationDuration.getFormat() == format && streamConfigurationDuration.getWidth() == streamConfiguration.getSize().getWidth() && streamConfigurationDuration.getHeight() == streamConfiguration.getSize().getHeight()) {
                            duration = streamConfigurationDuration.getDuration();
                            break;
                        }
                    }
                    c = 4096;
                    if (i2 != 4096) {
                        if (z2 != (duration > DURATION_20FPS_NS)) {
                        }
                    }
                } else {
                    c = c2;
                }
                sizeArr[i6] = streamConfiguration.getSize();
                i6++;
            } else {
                c = c2;
            }
            i5++;
            i3 = i;
            c2 = c;
            streamConfigurationMap = this;
        }
        if (i6 == i4) {
            return sizeArr;
        }
        throw new AssertionError("Too few sizes (expected " + i4 + ", actual " + i6 + ")");
    }

    private int[] getPublicFormats(boolean z) {
        int publicFormatCount = getPublicFormatCount(z);
        int[] iArr = new int[publicFormatCount];
        SparseIntArray formatsMap = getFormatsMap(z);
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        while (i2 < formatsMap.size()) {
            iArr[i3] = imageFormatToPublic(formatsMap.keyAt(i2));
            i2++;
            i3++;
        }
        if (z) {
            while (i < this.mDepthOutputFormats.size()) {
                iArr[i3] = depthFormatToPublic(this.mDepthOutputFormats.keyAt(i));
                i++;
                i3++;
            }
        }
        if (publicFormatCount == i3) {
            return iArr;
        }
        throw new AssertionError("Too few formats " + i3 + ", expected " + publicFormatCount);
    }

    private SparseIntArray getFormatsMap(boolean z) {
        return z ? this.mAllOutputFormats : this.mInputFormats;
    }

    private long getInternalFormatDuration(int i, int i2, Size size, int i3) {
        if (!isSupportedInternalConfiguration(i, i2, size)) {
            throw new IllegalArgumentException("size was not supported");
        }
        for (StreamConfigurationDuration streamConfigurationDuration : getDurations(i3, i2)) {
            if (streamConfigurationDuration.getFormat() == i && streamConfigurationDuration.getWidth() == size.getWidth() && streamConfigurationDuration.getHeight() == size.getHeight()) {
                return streamConfigurationDuration.getDuration();
            }
        }
        return 0L;
    }

    private StreamConfigurationDuration[] getDurations(int i, int i2) {
        if (i == 0) {
            return i2 == 4096 ? this.mDepthMinFrameDurations : this.mMinFrameDurations;
        }
        if (i == 1) {
            return i2 == 4096 ? this.mDepthStallDurations : this.mStallDurations;
        }
        throw new IllegalArgumentException("duration was invalid");
    }

    private int getPublicFormatCount(boolean z) {
        int size = getFormatsMap(z).size();
        return z ? size + this.mDepthOutputFormats.size() : size;
    }

    private static <T> boolean arrayContains(T[] tArr, T t) {
        if (tArr == null) {
            return false;
        }
        for (T t2 : tArr) {
            if (Objects.equals(t2, t)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSupportedInternalConfiguration(int i, int i2, Size size) {
        StreamConfiguration[] streamConfigurationArr = i2 == 4096 ? this.mDepthConfigurations : this.mConfigurations;
        for (int i3 = 0; i3 < streamConfigurationArr.length; i3++) {
            if (streamConfigurationArr[i3].getFormat() == i && streamConfigurationArr[i3].getSize().equals(size)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("StreamConfiguration(");
        appendOutputsString(sb);
        sb.append(", ");
        appendHighResOutputsString(sb);
        sb.append(", ");
        appendInputsString(sb);
        sb.append(", ");
        appendValidOutputFormatsForInputString(sb);
        sb.append(", ");
        appendHighSpeedVideoConfigurationsString(sb);
        sb.append(")");
        return sb.toString();
    }

    private void appendOutputsString(StringBuilder sb) {
        sb.append("Outputs(");
        for (int i : getOutputFormats()) {
            for (Size size : getOutputSizes(i)) {
                sb.append(String.format("[w:%d, h:%d, format:%s(%d), min_duration:%d, stall:%d], ", Integer.valueOf(size.getWidth()), Integer.valueOf(size.getHeight()), formatToString(i), Integer.valueOf(i), Long.valueOf(getOutputMinFrameDuration(i, size)), Long.valueOf(getOutputStallDuration(i, size))));
            }
        }
        if (sb.charAt(sb.length() - 1) == ' ') {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append(")");
    }

    private void appendHighResOutputsString(StringBuilder sb) {
        sb.append("HighResolutionOutputs(");
        for (int i : getOutputFormats()) {
            Size[] highResolutionOutputSizes = getHighResolutionOutputSizes(i);
            if (highResolutionOutputSizes != null) {
                for (Size size : highResolutionOutputSizes) {
                    sb.append(String.format("[w:%d, h:%d, format:%s(%d), min_duration:%d, stall:%d], ", Integer.valueOf(size.getWidth()), Integer.valueOf(size.getHeight()), formatToString(i), Integer.valueOf(i), Long.valueOf(getOutputMinFrameDuration(i, size)), Long.valueOf(getOutputStallDuration(i, size))));
                }
            }
        }
        if (sb.charAt(sb.length() - 1) == ' ') {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append(")");
    }

    private void appendInputsString(StringBuilder sb) {
        sb.append("Inputs(");
        for (int i : getInputFormats()) {
            for (Size size : getInputSizes(i)) {
                sb.append(String.format("[w:%d, h:%d, format:%s(%d)], ", Integer.valueOf(size.getWidth()), Integer.valueOf(size.getHeight()), formatToString(i), Integer.valueOf(i)));
            }
        }
        if (sb.charAt(sb.length() - 1) == ' ') {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append(")");
    }

    private void appendValidOutputFormatsForInputString(StringBuilder sb) {
        sb.append("ValidOutputFormatsForInput(");
        for (int i : getInputFormats()) {
            sb.append(String.format("[in:%s(%d), out:", formatToString(i), Integer.valueOf(i)));
            int[] validOutputFormatsForInput = getValidOutputFormatsForInput(i);
            for (int i2 = 0; i2 < validOutputFormatsForInput.length; i2++) {
                sb.append(String.format("%s(%d)", formatToString(validOutputFormatsForInput[i2]), Integer.valueOf(validOutputFormatsForInput[i2])));
                if (i2 < validOutputFormatsForInput.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("], ");
        }
        if (sb.charAt(sb.length() - 1) == ' ') {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append(")");
    }

    private void appendHighSpeedVideoConfigurationsString(StringBuilder sb) {
        sb.append("HighSpeedVideoConfigurations(");
        for (Size size : getHighSpeedVideoSizes()) {
            for (Range<Integer> range : getHighSpeedVideoFpsRangesFor(size)) {
                sb.append(String.format("[w:%d, h:%d, min_fps:%d, max_fps:%d], ", Integer.valueOf(size.getWidth()), Integer.valueOf(size.getHeight()), range.getLower(), range.getUpper()));
            }
        }
        if (sb.charAt(sb.length() - 1) == ' ') {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append(")");
    }
}
