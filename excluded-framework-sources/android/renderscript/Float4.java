package android.renderscript;

/* JADX INFO: loaded from: classes2.dex */
public class Float4 {
    public float w;
    public float x;
    public float y;
    public float z;

    public int length() {
        return 4;
    }

    public Float4() {
    }

    public Float4(Float4 float4) {
        this.x = float4.x;
        this.y = float4.y;
        this.z = float4.z;
        this.w = float4.w;
    }

    public Float4(float f, float f2, float f3, float f4) {
        this.x = f;
        this.y = f2;
        this.z = f3;
        this.w = f4;
    }

    public static Float4 add(Float4 float4, Float4 float5) {
        Float4 float6 = new Float4();
        float6.x = float4.x + float5.x;
        float6.y = float4.y + float5.y;
        float6.z = float4.z + float5.z;
        float6.w = float4.w + float5.w;
        return float6;
    }

    public void add(Float4 float4) {
        this.x += float4.x;
        this.y += float4.y;
        this.z += float4.z;
        this.w += float4.w;
    }

    public void add(float f) {
        this.x += f;
        this.y += f;
        this.z += f;
        this.w += f;
    }

    public static Float4 add(Float4 float4, float f) {
        Float4 float5 = new Float4();
        float5.x = float4.x + f;
        float5.y = float4.y + f;
        float5.z = float4.z + f;
        float5.w = float4.w + f;
        return float5;
    }

    public void sub(Float4 float4) {
        this.x -= float4.x;
        this.y -= float4.y;
        this.z -= float4.z;
        this.w -= float4.w;
    }

    public void sub(float f) {
        this.x -= f;
        this.y -= f;
        this.z -= f;
        this.w -= f;
    }

    public static Float4 sub(Float4 float4, float f) {
        Float4 float5 = new Float4();
        float5.x = float4.x - f;
        float5.y = float4.y - f;
        float5.z = float4.z - f;
        float5.w = float4.w - f;
        return float5;
    }

    public static Float4 sub(Float4 float4, Float4 float5) {
        Float4 float6 = new Float4();
        float6.x = float4.x - float5.x;
        float6.y = float4.y - float5.y;
        float6.z = float4.z - float5.z;
        float6.w = float4.w - float5.w;
        return float6;
    }

    public void mul(Float4 float4) {
        this.x *= float4.x;
        this.y *= float4.y;
        this.z *= float4.z;
        this.w *= float4.w;
    }

    public void mul(float f) {
        this.x *= f;
        this.y *= f;
        this.z *= f;
        this.w *= f;
    }

    public static Float4 mul(Float4 float4, Float4 float5) {
        Float4 float6 = new Float4();
        float6.x = float4.x * float5.x;
        float6.y = float4.y * float5.y;
        float6.z = float4.z * float5.z;
        float6.w = float4.w * float5.w;
        return float6;
    }

    public static Float4 mul(Float4 float4, float f) {
        Float4 float5 = new Float4();
        float5.x = float4.x * f;
        float5.y = float4.y * f;
        float5.z = float4.z * f;
        float5.w = float4.w * f;
        return float5;
    }

    public void div(Float4 float4) {
        this.x /= float4.x;
        this.y /= float4.y;
        this.z /= float4.z;
        this.w /= float4.w;
    }

    public void div(float f) {
        this.x /= f;
        this.y /= f;
        this.z /= f;
        this.w /= f;
    }

    public static Float4 div(Float4 float4, float f) {
        Float4 float5 = new Float4();
        float5.x = float4.x / f;
        float5.y = float4.y / f;
        float5.z = float4.z / f;
        float5.w = float4.w / f;
        return float5;
    }

    public static Float4 div(Float4 float4, Float4 float5) {
        Float4 float6 = new Float4();
        float6.x = float4.x / float5.x;
        float6.y = float4.y / float5.y;
        float6.z = float4.z / float5.z;
        float6.w = float4.w / float5.w;
        return float6;
    }

    public float dotProduct(Float4 float4) {
        return (this.x * float4.x) + (this.y * float4.y) + (this.z * float4.z) + (this.w * float4.w);
    }

    public static float dotProduct(Float4 float4, Float4 float5) {
        return (float5.x * float4.x) + (float5.y * float4.y) + (float5.z * float4.z) + (float5.w * float4.w);
    }

    public void addMultiple(Float4 float4, float f) {
        this.x += float4.x * f;
        this.y += float4.y * f;
        this.z += float4.z * f;
        this.w += float4.w * f;
    }

    public void set(Float4 float4) {
        this.x = float4.x;
        this.y = float4.y;
        this.z = float4.z;
        this.w = float4.w;
    }

    public void negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.w = -this.w;
    }

    public float elementSum() {
        return this.x + this.y + this.z + this.w;
    }

    public float get(int i) {
        if (i == 0) {
            return this.x;
        }
        if (i == 1) {
            return this.y;
        }
        if (i == 2) {
            return this.z;
        }
        if (i == 3) {
            return this.w;
        }
        throw new IndexOutOfBoundsException("Index: i");
    }

    public void setAt(int i, float f) {
        if (i == 0) {
            this.x = f;
            return;
        }
        if (i == 1) {
            this.y = f;
        } else if (i == 2) {
            this.z = f;
        } else {
            if (i == 3) {
                this.w = f;
                return;
            }
            throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, float f) {
        if (i == 0) {
            this.x += f;
            return;
        }
        if (i == 1) {
            this.y += f;
        } else if (i == 2) {
            this.z += f;
        } else {
            if (i == 3) {
                this.w += f;
                return;
            }
            throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setValues(float f, float f2, float f3, float f4) {
        this.x = f;
        this.y = f2;
        this.z = f3;
        this.w = f4;
    }

    public void copyTo(float[] fArr, int i) {
        fArr[i] = this.x;
        fArr[i + 1] = this.y;
        fArr[i + 2] = this.z;
        fArr[i + 3] = this.w;
    }
}
