package android.renderscript;

/* JADX INFO: loaded from: classes2.dex */
public class Short4 {
    public short w;
    public short x;
    public short y;
    public short z;

    public short length() {
        return (short) 4;
    }

    public Short4() {
    }

    public Short4(short s) {
        this.w = s;
        this.z = s;
        this.y = s;
        this.x = s;
    }

    public Short4(short s, short s2, short s3, short s4) {
        this.x = s;
        this.y = s2;
        this.z = s3;
        this.w = s4;
    }

    public Short4(Short4 short4) {
        this.x = short4.x;
        this.y = short4.y;
        this.z = short4.z;
        this.w = short4.w;
    }

    public void add(Short4 short4) {
        this.x = (short) (this.x + short4.x);
        this.y = (short) (this.y + short4.y);
        this.z = (short) (this.z + short4.z);
        this.w = (short) (this.w + short4.w);
    }

    public static Short4 add(Short4 short4, Short4 short5) {
        Short4 short6 = new Short4();
        short6.x = (short) (short4.x + short5.x);
        short6.y = (short) (short4.y + short5.y);
        short6.z = (short) (short4.z + short5.z);
        short6.w = (short) (short4.w + short5.w);
        return short6;
    }

    public void add(short s) {
        this.x = (short) (this.x + s);
        this.y = (short) (this.y + s);
        this.z = (short) (this.z + s);
        this.w = (short) (this.w + s);
    }

    public static Short4 add(Short4 short4, short s) {
        Short4 short5 = new Short4();
        short5.x = (short) (short4.x + s);
        short5.y = (short) (short4.y + s);
        short5.z = (short) (short4.z + s);
        short5.w = (short) (short4.w + s);
        return short5;
    }

    public void sub(Short4 short4) {
        this.x = (short) (this.x - short4.x);
        this.y = (short) (this.y - short4.y);
        this.z = (short) (this.z - short4.z);
        this.w = (short) (this.w - short4.w);
    }

    public static Short4 sub(Short4 short4, Short4 short5) {
        Short4 short6 = new Short4();
        short6.x = (short) (short4.x - short5.x);
        short6.y = (short) (short4.y - short5.y);
        short6.z = (short) (short4.z - short5.z);
        short6.w = (short) (short4.w - short5.w);
        return short6;
    }

    public void sub(short s) {
        this.x = (short) (this.x - s);
        this.y = (short) (this.y - s);
        this.z = (short) (this.z - s);
        this.w = (short) (this.w - s);
    }

    public static Short4 sub(Short4 short4, short s) {
        Short4 short5 = new Short4();
        short5.x = (short) (short4.x - s);
        short5.y = (short) (short4.y - s);
        short5.z = (short) (short4.z - s);
        short5.w = (short) (short4.w - s);
        return short5;
    }

    public void mul(Short4 short4) {
        this.x = (short) (this.x * short4.x);
        this.y = (short) (this.y * short4.y);
        this.z = (short) (this.z * short4.z);
        this.w = (short) (this.w * short4.w);
    }

    public static Short4 mul(Short4 short4, Short4 short5) {
        Short4 short6 = new Short4();
        short6.x = (short) (short4.x * short5.x);
        short6.y = (short) (short4.y * short5.y);
        short6.z = (short) (short4.z * short5.z);
        short6.w = (short) (short4.w * short5.w);
        return short6;
    }

    public void mul(short s) {
        this.x = (short) (this.x * s);
        this.y = (short) (this.y * s);
        this.z = (short) (this.z * s);
        this.w = (short) (this.w * s);
    }

    public static Short4 mul(Short4 short4, short s) {
        Short4 short5 = new Short4();
        short5.x = (short) (short4.x * s);
        short5.y = (short) (short4.y * s);
        short5.z = (short) (short4.z * s);
        short5.w = (short) (short4.w * s);
        return short5;
    }

    public void div(Short4 short4) {
        this.x = (short) (this.x / short4.x);
        this.y = (short) (this.y / short4.y);
        this.z = (short) (this.z / short4.z);
        this.w = (short) (this.w / short4.w);
    }

    public static Short4 div(Short4 short4, Short4 short5) {
        Short4 short6 = new Short4();
        short6.x = (short) (short4.x / short5.x);
        short6.y = (short) (short4.y / short5.y);
        short6.z = (short) (short4.z / short5.z);
        short6.w = (short) (short4.w / short5.w);
        return short6;
    }

    public void div(short s) {
        this.x = (short) (this.x / s);
        this.y = (short) (this.y / s);
        this.z = (short) (this.z / s);
        this.w = (short) (this.w / s);
    }

    public static Short4 div(Short4 short4, short s) {
        Short4 short5 = new Short4();
        short5.x = (short) (short4.x / s);
        short5.y = (short) (short4.y / s);
        short5.z = (short) (short4.z / s);
        short5.w = (short) (short4.w / s);
        return short5;
    }

    public void mod(Short4 short4) {
        this.x = (short) (this.x % short4.x);
        this.y = (short) (this.y % short4.y);
        this.z = (short) (this.z % short4.z);
        this.w = (short) (this.w % short4.w);
    }

    public static Short4 mod(Short4 short4, Short4 short5) {
        Short4 short6 = new Short4();
        short6.x = (short) (short4.x % short5.x);
        short6.y = (short) (short4.y % short5.y);
        short6.z = (short) (short4.z % short5.z);
        short6.w = (short) (short4.w % short5.w);
        return short6;
    }

    public void mod(short s) {
        this.x = (short) (this.x % s);
        this.y = (short) (this.y % s);
        this.z = (short) (this.z % s);
        this.w = (short) (this.w % s);
    }

    public static Short4 mod(Short4 short4, short s) {
        Short4 short5 = new Short4();
        short5.x = (short) (short4.x % s);
        short5.y = (short) (short4.y % s);
        short5.z = (short) (short4.z % s);
        short5.w = (short) (short4.w % s);
        return short5;
    }

    public void negate() {
        this.x = (short) (-this.x);
        this.y = (short) (-this.y);
        this.z = (short) (-this.z);
        this.w = (short) (-this.w);
    }

    public short dotProduct(Short4 short4) {
        return (short) ((this.x * short4.x) + (this.y * short4.y) + (this.z * short4.z) + (this.w * short4.w));
    }

    public static short dotProduct(Short4 short4, Short4 short5) {
        return (short) ((short5.x * short4.x) + (short5.y * short4.y) + (short5.z * short4.z) + (short5.w * short4.w));
    }

    public void addMultiple(Short4 short4, short s) {
        this.x = (short) (this.x + (short4.x * s));
        this.y = (short) (this.y + (short4.y * s));
        this.z = (short) (this.z + (short4.z * s));
        this.w = (short) (this.w + (short4.w * s));
    }

    public void set(Short4 short4) {
        this.x = short4.x;
        this.y = short4.y;
        this.z = short4.z;
        this.w = short4.w;
    }

    public void setValues(short s, short s2, short s3, short s4) {
        this.x = s;
        this.y = s2;
        this.z = s3;
        this.w = s4;
    }

    public short elementSum() {
        return (short) (this.x + this.y + this.z + this.w);
    }

    public short get(int i) {
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

    public void setAt(int i, short s) {
        if (i == 0) {
            this.x = s;
            return;
        }
        if (i == 1) {
            this.y = s;
        } else if (i == 2) {
            this.z = s;
        } else {
            if (i == 3) {
                this.w = s;
                return;
            }
            throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, short s) {
        if (i == 0) {
            this.x = (short) (this.x + s);
            return;
        }
        if (i == 1) {
            this.y = (short) (this.y + s);
        } else if (i == 2) {
            this.z = (short) (this.z + s);
        } else {
            if (i == 3) {
                this.w = (short) (this.w + s);
                return;
            }
            throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void copyTo(short[] sArr, int i) {
        sArr[i] = this.x;
        sArr[i + 1] = this.y;
        sArr[i + 2] = this.z;
        sArr[i + 3] = this.w;
    }
}
