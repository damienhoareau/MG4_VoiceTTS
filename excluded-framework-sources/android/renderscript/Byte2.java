package android.renderscript;

/* JADX INFO: loaded from: classes2.dex */
public class Byte2 {
    public byte x;
    public byte y;

    public byte length() {
        return (byte) 2;
    }

    public Byte2() {
    }

    public Byte2(byte b, byte b2) {
        this.x = b;
        this.y = b2;
    }

    public Byte2(Byte2 byte2) {
        this.x = byte2.x;
        this.y = byte2.y;
    }

    public void add(Byte2 byte2) {
        this.x = (byte) (this.x + byte2.x);
        this.y = (byte) (this.y + byte2.y);
    }

    public static Byte2 add(Byte2 byte2, Byte2 byte3) {
        Byte2 byte4 = new Byte2();
        byte4.x = (byte) (byte2.x + byte3.x);
        byte4.y = (byte) (byte2.y + byte3.y);
        return byte4;
    }

    public void add(byte b) {
        this.x = (byte) (this.x + b);
        this.y = (byte) (this.y + b);
    }

    public static Byte2 add(Byte2 byte2, byte b) {
        Byte2 byte3 = new Byte2();
        byte3.x = (byte) (byte2.x + b);
        byte3.y = (byte) (byte2.y + b);
        return byte3;
    }

    public void sub(Byte2 byte2) {
        this.x = (byte) (this.x - byte2.x);
        this.y = (byte) (this.y - byte2.y);
    }

    public static Byte2 sub(Byte2 byte2, Byte2 byte3) {
        Byte2 byte4 = new Byte2();
        byte4.x = (byte) (byte2.x - byte3.x);
        byte4.y = (byte) (byte2.y - byte3.y);
        return byte4;
    }

    public void sub(byte b) {
        this.x = (byte) (this.x - b);
        this.y = (byte) (this.y - b);
    }

    public static Byte2 sub(Byte2 byte2, byte b) {
        Byte2 byte3 = new Byte2();
        byte3.x = (byte) (byte2.x - b);
        byte3.y = (byte) (byte2.y - b);
        return byte3;
    }

    public void mul(Byte2 byte2) {
        this.x = (byte) (this.x * byte2.x);
        this.y = (byte) (this.y * byte2.y);
    }

    public static Byte2 mul(Byte2 byte2, Byte2 byte3) {
        Byte2 byte4 = new Byte2();
        byte4.x = (byte) (byte2.x * byte3.x);
        byte4.y = (byte) (byte2.y * byte3.y);
        return byte4;
    }

    public void mul(byte b) {
        this.x = (byte) (this.x * b);
        this.y = (byte) (this.y * b);
    }

    public static Byte2 mul(Byte2 byte2, byte b) {
        Byte2 byte3 = new Byte2();
        byte3.x = (byte) (byte2.x * b);
        byte3.y = (byte) (byte2.y * b);
        return byte3;
    }

    public void div(Byte2 byte2) {
        this.x = (byte) (this.x / byte2.x);
        this.y = (byte) (this.y / byte2.y);
    }

    public static Byte2 div(Byte2 byte2, Byte2 byte3) {
        Byte2 byte4 = new Byte2();
        byte4.x = (byte) (byte2.x / byte3.x);
        byte4.y = (byte) (byte2.y / byte3.y);
        return byte4;
    }

    public void div(byte b) {
        this.x = (byte) (this.x / b);
        this.y = (byte) (this.y / b);
    }

    public static Byte2 div(Byte2 byte2, byte b) {
        Byte2 byte3 = new Byte2();
        byte3.x = (byte) (byte2.x / b);
        byte3.y = (byte) (byte2.y / b);
        return byte3;
    }

    public void negate() {
        this.x = (byte) (-this.x);
        this.y = (byte) (-this.y);
    }

    public byte dotProduct(Byte2 byte2) {
        return (byte) ((this.x * byte2.x) + (this.y * byte2.y));
    }

    public static byte dotProduct(Byte2 byte2, Byte2 byte3) {
        return (byte) ((byte3.x * byte2.x) + (byte3.y * byte2.y));
    }

    public void addMultiple(Byte2 byte2, byte b) {
        this.x = (byte) (this.x + (byte2.x * b));
        this.y = (byte) (this.y + (byte2.y * b));
    }

    public void set(Byte2 byte2) {
        this.x = byte2.x;
        this.y = byte2.y;
    }

    public void setValues(byte b, byte b2) {
        this.x = b;
        this.y = b2;
    }

    public byte elementSum() {
        return (byte) (this.x + this.y);
    }

    public byte get(int i) {
        if (i == 0) {
            return this.x;
        }
        if (i == 1) {
            return this.y;
        }
        throw new IndexOutOfBoundsException("Index: i");
    }

    public void setAt(int i, byte b) {
        if (i == 0) {
            this.x = b;
        } else {
            if (i == 1) {
                this.y = b;
                return;
            }
            throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, byte b) {
        if (i == 0) {
            this.x = (byte) (this.x + b);
        } else {
            if (i == 1) {
                this.y = (byte) (this.y + b);
                return;
            }
            throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void copyTo(byte[] bArr, int i) {
        bArr[i] = this.x;
        bArr[i + 1] = this.y;
    }
}
