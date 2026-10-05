package android.renderscript;

/* JADX INFO: loaded from: classes2.dex */
public class Short2 {
    public short x;
    public short y;

    public short length() {
        return (short) 2;
    }

    public Short2() {
    }

    public Short2(short s) {
        this.y = s;
        this.x = s;
    }

    public Short2(short s, short s2) {
        this.x = s;
        this.y = s2;
    }

    public Short2(Short2 short2) {
        this.x = short2.x;
        this.y = short2.y;
    }

    public void add(Short2 short2) {
        this.x = (short) (this.x + short2.x);
        this.y = (short) (this.y + short2.y);
    }

    public static Short2 add(Short2 short2, Short2 short3) {
        Short2 short4 = new Short2();
        short4.x = (short) (short2.x + short3.x);
        short4.y = (short) (short2.y + short3.y);
        return short4;
    }

    public void add(short s) {
        this.x = (short) (this.x + s);
        this.y = (short) (this.y + s);
    }

    public static Short2 add(Short2 short2, short s) {
        Short2 short3 = new Short2();
        short3.x = (short) (short2.x + s);
        short3.y = (short) (short2.y + s);
        return short3;
    }

    public void sub(Short2 short2) {
        this.x = (short) (this.x - short2.x);
        this.y = (short) (this.y - short2.y);
    }

    public static Short2 sub(Short2 short2, Short2 short3) {
        Short2 short4 = new Short2();
        short4.x = (short) (short2.x - short3.x);
        short4.y = (short) (short2.y - short3.y);
        return short4;
    }

    public void sub(short s) {
        this.x = (short) (this.x - s);
        this.y = (short) (this.y - s);
    }

    public static Short2 sub(Short2 short2, short s) {
        Short2 short3 = new Short2();
        short3.x = (short) (short2.x - s);
        short3.y = (short) (short2.y - s);
        return short3;
    }

    public void mul(Short2 short2) {
        this.x = (short) (this.x * short2.x);
        this.y = (short) (this.y * short2.y);
    }

    public static Short2 mul(Short2 short2, Short2 short3) {
        Short2 short4 = new Short2();
        short4.x = (short) (short2.x * short3.x);
        short4.y = (short) (short2.y * short3.y);
        return short4;
    }

    public void mul(short s) {
        this.x = (short) (this.x * s);
        this.y = (short) (this.y * s);
    }

    public static Short2 mul(Short2 short2, short s) {
        Short2 short3 = new Short2();
        short3.x = (short) (short2.x * s);
        short3.y = (short) (short2.y * s);
        return short3;
    }

    public void div(Short2 short2) {
        this.x = (short) (this.x / short2.x);
        this.y = (short) (this.y / short2.y);
    }

    public static Short2 div(Short2 short2, Short2 short3) {
        Short2 short4 = new Short2();
        short4.x = (short) (short2.x / short3.x);
        short4.y = (short) (short2.y / short3.y);
        return short4;
    }

    public void div(short s) {
        this.x = (short) (this.x / s);
        this.y = (short) (this.y / s);
    }

    public static Short2 div(Short2 short2, short s) {
        Short2 short3 = new Short2();
        short3.x = (short) (short2.x / s);
        short3.y = (short) (short2.y / s);
        return short3;
    }

    public void mod(Short2 short2) {
        this.x = (short) (this.x % short2.x);
        this.y = (short) (this.y % short2.y);
    }

    public static Short2 mod(Short2 short2, Short2 short3) {
        Short2 short4 = new Short2();
        short4.x = (short) (short2.x % short3.x);
        short4.y = (short) (short2.y % short3.y);
        return short4;
    }

    public void mod(short s) {
        this.x = (short) (this.x % s);
        this.y = (short) (this.y % s);
    }

    public static Short2 mod(Short2 short2, short s) {
        Short2 short3 = new Short2();
        short3.x = (short) (short2.x % s);
        short3.y = (short) (short2.y % s);
        return short3;
    }

    public void negate() {
        this.x = (short) (-this.x);
        this.y = (short) (-this.y);
    }

    public short dotProduct(Short2 short2) {
        return (short) ((this.x * short2.x) + (this.y * short2.y));
    }

    public static short dotProduct(Short2 short2, Short2 short3) {
        return (short) ((short3.x * short2.x) + (short3.y * short2.y));
    }

    public void addMultiple(Short2 short2, short s) {
        this.x = (short) (this.x + (short2.x * s));
        this.y = (short) (this.y + (short2.y * s));
    }

    public void set(Short2 short2) {
        this.x = short2.x;
        this.y = short2.y;
    }

    public void setValues(short s, short s2) {
        this.x = s;
        this.y = s2;
    }

    public short elementSum() {
        return (short) (this.x + this.y);
    }

    public short get(int i) {
        if (i == 0) {
            return this.x;
        }
        if (i == 1) {
            return this.y;
        }
        throw new IndexOutOfBoundsException("Index: i");
    }

    public void setAt(int i, short s) {
        if (i == 0) {
            this.x = s;
        } else {
            if (i == 1) {
                this.y = s;
                return;
            }
            throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, short s) {
        if (i == 0) {
            this.x = (short) (this.x + s);
        } else {
            if (i == 1) {
                this.y = (short) (this.y + s);
                return;
            }
            throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void copyTo(short[] sArr, int i) {
        sArr[i] = this.x;
        sArr[i + 1] = this.y;
    }
}
