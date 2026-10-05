package android.filterpacks.imageproc;

import android.app.slice.SliceItem;
import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;

/* JADX INFO: loaded from: classes.dex */
public class ImageSlicer extends Filter {
    private int mInputHeight;
    private int mInputWidth;
    private Frame mOriginalFrame;
    private int mOutputHeight;
    private int mOutputWidth;

    @GenerateFieldPort(name = "padSize")
    private int mPadSize;
    private Program mProgram;
    private int mSliceHeight;
    private int mSliceIndex;
    private int mSliceWidth;

    @GenerateFieldPort(name = "xSlices")
    private int mXSlices;

    @GenerateFieldPort(name = "ySlices")
    private int mYSlices;

    @Override // android.filterfw.core.Filter
    public FrameFormat getOutputFormat(String str, FrameFormat frameFormat) {
        return frameFormat;
    }

    public ImageSlicer(String str) {
        super(str);
        this.mSliceIndex = 0;
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        addMaskedInputPort(SliceItem.FORMAT_IMAGE, ImageFormat.create(3, 3));
        addOutputBasedOnInput(SliceItem.FORMAT_IMAGE, SliceItem.FORMAT_IMAGE);
    }

    private void calcOutputFormatForInput(Frame frame) {
        this.mInputWidth = frame.getFormat().getWidth();
        int height = frame.getFormat().getHeight();
        this.mInputHeight = height;
        int i = this.mInputWidth;
        int i2 = this.mXSlices;
        int i3 = ((i + i2) - 1) / i2;
        this.mSliceWidth = i3;
        int i4 = this.mYSlices;
        int i5 = ((height + i4) - 1) / i4;
        this.mSliceHeight = i5;
        int i6 = this.mPadSize;
        this.mOutputWidth = i3 + (i6 * 2);
        this.mOutputHeight = i5 + (i6 * 2);
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext filterContext) {
        if (this.mSliceIndex == 0) {
            Frame framePullInput = pullInput(SliceItem.FORMAT_IMAGE);
            this.mOriginalFrame = framePullInput;
            calcOutputFormatForInput(framePullInput);
        }
        MutableFrameFormat mutableFrameFormatMutableCopy = this.mOriginalFrame.getFormat().mutableCopy();
        mutableFrameFormatMutableCopy.setDimensions(this.mOutputWidth, this.mOutputHeight);
        Frame frameNewFrame = filterContext.getFrameManager().newFrame(mutableFrameFormatMutableCopy);
        if (this.mProgram == null) {
            this.mProgram = ShaderProgram.createIdentity(filterContext);
        }
        int i = this.mSliceIndex;
        int i2 = this.mXSlices;
        int i3 = i % i2;
        int i4 = i / i2;
        int i5 = i3 * this.mSliceWidth;
        int i6 = this.mPadSize;
        int i7 = this.mInputWidth;
        float f = (i5 - i6) / i7;
        float f2 = (i4 * this.mSliceHeight) - i6;
        int i8 = this.mInputHeight;
        ((ShaderProgram) this.mProgram).setSourceRect(f, f2 / i8, this.mOutputWidth / i7, this.mOutputHeight / i8);
        this.mProgram.process(this.mOriginalFrame, frameNewFrame);
        int i9 = this.mSliceIndex + 1;
        this.mSliceIndex = i9;
        if (i9 == this.mXSlices * this.mYSlices) {
            this.mSliceIndex = 0;
            this.mOriginalFrame.release();
            setWaitsOnInputPort(SliceItem.FORMAT_IMAGE, true);
        } else {
            this.mOriginalFrame.retain();
            setWaitsOnInputPort(SliceItem.FORMAT_IMAGE, false);
        }
        pushOutput(SliceItem.FORMAT_IMAGE, frameNewFrame);
        frameNewFrame.release();
    }
}
