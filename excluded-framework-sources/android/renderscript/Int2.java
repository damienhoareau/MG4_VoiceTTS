package android.renderscript;

/* JADX INFO: loaded from: classes2.dex */
public class Int2 {
    public int x;
    public int y;

    public int length() {
        return 2;
    }

    public Int2() {
    }

    public Int2(int i) {
        this.y = i;
        this.x = i;
    }

    public Int2(int i, int i2) {
        this.x = i;
        this.y = i2;
    }

    public Int2(Int2 int2) {
        this.x = int2.x;
        this.y = int2.y;
    }

    public void add(Int2 int2) {
        this.x += int2.x;
        this.y += int2.y;
    }

    public static Int2 add(Int2 int2, Int2 int3) {
        Int2 int4 = new Int2();
        int4.x = int2.x + int3.x;
        int4.y = int2.y + int3.y;
        return int4;
    }

    public void add(int i) {
        this.x += i;
        this.y += i;
    }

    public static Int2 add(Int2 int2, int i) {
        Int2 int3 = new Int2();
        int3.x = int2.x + i;
        int3.y = int2.y + i;
        return int3;
    }

    public void sub(Int2 int2) {
        this.x -= int2.x;
        this.y -= int2.y;
    }

    public static Int2 sub(Int2 int2, Int2 int3) {
        Int2 int4 = new Int2();
        int4.x = int2.x - int3.x;
        int4.y = int2.y - int3.y;
        return int4;
    }

    public void sub(int i) {
        this.x -= i;
        this.y -= i;
    }

    public static Int2 sub(Int2 int2, int i) {
        Int2 int3 = new Int2();
        int3.x = int2.x - i;
        int3.y = int2.y - i;
        return int3;
    }

    public void mul(Int2 int2) {
        this.x *= int2.x;
        this.y *= int2.y;
    }

    public static Int2 mul(Int2 int2, Int2 int3) {
        Int2 int4 = new Int2();
        int4.x = int2.x * int3.x;
        int4.y = int2.y * int3.y;
        return int4;
    }

    public void mul(int i) {
        this.x *= i;
        this.y *= i;
    }

    public static Int2 mul(Int2 int2, int i) {
        Int2 int3 = new Int2();
        int3.x = int2.x * i;
        int3.y = int2.y * i;
        return int3;
    }

    public void div(Int2 int2) {
        this.x /= int2.x;
        this.y /= int2.y;
    }

    public static Int2 div(Int2 int2, Int2 int3) {
        Int2 int4 = new Int2();
        int4.x = int2.x / int3.x;
        int4.y = int2.y / int3.y;
        return int4;
    }

    public void div(int i) {
        this.x /= i;
        this.y /= i;
    }

    public static Int2 div(Int2 int2, int i) {
        Int2 int3 = new Int2();
        int3.x = int2.x / i;
        int3.y = int2.y / i;
        return int3;
    }

    public void mod(Int2 int2) {
        this.x %= int2.x;
        this.y %= int2.y;
    }

    public static Int2 mod(Int2 int2, Int2 int3) {
        Int2 int4 = new Int2();
        int4.x = int2.x % int3.x;
        int4.y = int2.y % int3.y;
        return int4;
    }

    public void mod(int i) {
        this.x %= i;
        this.y %= i;
    }

    public static Int2 mod(Int2 int2, int i) {
        Int2 int3 = new Int2();
        int3.x = int2.x % i;
        int3.y = int2.y % i;
        return int3;
    }

    public void negate() {
        this.x = -this.x;
        this.y = -this.y;
    }

    public int dotProduct(Int2 int2) {
        return (this.x * int2.x) + (this.y * int2.y);
    }

    public static int dotProduct(Int2 int2, Int2 int3) {
        return (int3.x * int2.x) + (int3.y * int2.y);
    }

    public void addMultiple(Int2 int2, int i) {
        this.x += int2.x * i;
        this.y += int2.y * i;
    }

    public void set(Int2 int2) {
        this.x = int2.x;
        this.y = int2.y;
    }

    public void setValues(int i, int i2) {
        this.x = i;
        this.y = i2;
    }

    public int elementSum() {
        return this.x + this.y;
    }

    public int get(int i) {
        if (i == 0) {
            return this.x;
        }
        if (i == 1) {
            return this.y;
        }
        throw new IndexOutOfBoundsException("Index: i");
    }

    public void setAt(int i, int i2) {
        if (i == 0) {
            this.x = i2;
        } else {
            if (i == 1) {
                this.y = i2;
                return;
            }
            throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, int i2) {
        if (i == 0) {
            this.x += i2;
        } else {
            if (i == 1) {
                this.y += i2;
                return;
            }
            throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void copyTo(int[] iArr, int i) {
        iArr[i] = this.x;
        iArr[i + 1] = this.y;
    }
}
