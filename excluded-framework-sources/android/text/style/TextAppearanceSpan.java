package android.text.style;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.LeakyTypefaceStorage;
import android.graphics.Typeface;
import android.os.Parcel;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import com.android.internal.R;

/* JADX INFO: loaded from: classes2.dex */
public class TextAppearanceSpan extends MetricAffectingSpan implements ParcelableSpan {
    private final String mFamilyName;
    private final int mStyle;
    private final ColorStateList mTextColor;
    private final ColorStateList mTextColorLink;
    private final int mTextSize;
    private final Typeface mTypeface;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeIdInternal() {
        return 17;
    }

    public TextAppearanceSpan(Context context, int i) {
        this(context, i, -1);
    }

    public TextAppearanceSpan(Context context, int i, int i2) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(i, R.styleable.TextAppearance);
        ColorStateList colorStateList = typedArrayObtainStyledAttributes.getColorStateList(3);
        this.mTextColorLink = typedArrayObtainStyledAttributes.getColorStateList(6);
        this.mTextSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(0, -1);
        this.mStyle = typedArrayObtainStyledAttributes.getInt(2, 0);
        if (!context.isRestricted() && context.canLoadUnsafeResources()) {
            this.mTypeface = typedArrayObtainStyledAttributes.getFont(12);
        } else {
            this.mTypeface = null;
        }
        if (this.mTypeface != null) {
            this.mFamilyName = null;
        } else {
            String string = typedArrayObtainStyledAttributes.getString(12);
            if (string != null) {
                this.mFamilyName = string;
            } else {
                int i3 = typedArrayObtainStyledAttributes.getInt(1, 0);
                if (i3 == 1) {
                    this.mFamilyName = "sans";
                } else if (i3 == 2) {
                    this.mFamilyName = "serif";
                } else if (i3 == 3) {
                    this.mFamilyName = "monospace";
                } else {
                    this.mFamilyName = null;
                }
            }
        }
        typedArrayObtainStyledAttributes.recycle();
        if (i2 >= 0) {
            TypedArray typedArrayObtainStyledAttributes2 = context.obtainStyledAttributes(16973829, R.styleable.Theme);
            colorStateList = typedArrayObtainStyledAttributes2.getColorStateList(i2);
            typedArrayObtainStyledAttributes2.recycle();
        }
        this.mTextColor = colorStateList;
    }

    public TextAppearanceSpan(String str, int i, int i2, ColorStateList colorStateList, ColorStateList colorStateList2) {
        this.mFamilyName = str;
        this.mStyle = i;
        this.mTextSize = i2;
        this.mTextColor = colorStateList;
        this.mTextColorLink = colorStateList2;
        this.mTypeface = null;
    }

    public TextAppearanceSpan(Parcel parcel) {
        this.mFamilyName = parcel.readString();
        this.mStyle = parcel.readInt();
        this.mTextSize = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.mTextColor = ColorStateList.CREATOR.createFromParcel(parcel);
        } else {
            this.mTextColor = null;
        }
        if (parcel.readInt() != 0) {
            this.mTextColorLink = ColorStateList.CREATOR.createFromParcel(parcel);
        } else {
            this.mTextColorLink = null;
        }
        this.mTypeface = LeakyTypefaceStorage.readTypefaceFromParcel(parcel);
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return getSpanTypeIdInternal();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        writeToParcelInternal(parcel, i);
    }

    @Override // android.text.ParcelableSpan
    public void writeToParcelInternal(Parcel parcel, int i) {
        parcel.writeString(this.mFamilyName);
        parcel.writeInt(this.mStyle);
        parcel.writeInt(this.mTextSize);
        if (this.mTextColor != null) {
            parcel.writeInt(1);
            this.mTextColor.writeToParcel(parcel, i);
        } else {
            parcel.writeInt(0);
        }
        if (this.mTextColorLink != null) {
            parcel.writeInt(1);
            this.mTextColorLink.writeToParcel(parcel, i);
        } else {
            parcel.writeInt(0);
        }
        LeakyTypefaceStorage.writeTypefaceToParcel(this.mTypeface, parcel);
    }

    public String getFamily() {
        return this.mFamilyName;
    }

    public ColorStateList getTextColor() {
        return this.mTextColor;
    }

    public ColorStateList getLinkTextColor() {
        return this.mTextColorLink;
    }

    public int getTextSize() {
        return this.mTextSize;
    }

    public int getTextStyle() {
        return this.mStyle;
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint textPaint) {
        updateMeasureState(textPaint);
        ColorStateList colorStateList = this.mTextColor;
        if (colorStateList != null) {
            textPaint.setColor(colorStateList.getColorForState(textPaint.drawableState, 0));
        }
        ColorStateList colorStateList2 = this.mTextColorLink;
        if (colorStateList2 != null) {
            textPaint.linkColor = colorStateList2.getColorForState(textPaint.drawableState, 0);
        }
    }

    @Override // android.text.style.MetricAffectingSpan
    public void updateMeasureState(TextPaint textPaint) {
        Typeface typefaceCreate;
        Typeface typeface = this.mTypeface;
        int style = 0;
        if (typeface != null) {
            style = this.mStyle;
            typefaceCreate = Typeface.create(typeface, style);
        } else if (this.mFamilyName == null && this.mStyle == 0) {
            typefaceCreate = null;
        } else {
            Typeface typeface2 = textPaint.getTypeface();
            style = (typeface2 != null ? typeface2.getStyle() : 0) | this.mStyle;
            String str = this.mFamilyName;
            if (str != null) {
                typefaceCreate = Typeface.create(str, style);
            } else if (typeface2 == null) {
                typefaceCreate = Typeface.defaultFromStyle(style);
            } else {
                typefaceCreate = Typeface.create(typeface2, style);
            }
        }
        if (typefaceCreate != null) {
            int i = style & (~typefaceCreate.getStyle());
            if ((i & 1) != 0) {
                textPaint.setFakeBoldText(true);
            }
            if ((i & 2) != 0) {
                textPaint.setTextSkewX(-0.25f);
            }
            textPaint.setTypeface(typefaceCreate);
        }
        int i2 = this.mTextSize;
        if (i2 > 0) {
            textPaint.setTextSize(i2);
        }
    }
}
