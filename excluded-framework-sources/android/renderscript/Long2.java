package android.renderscript;

/* JADX INFO: loaded from: classes2.dex */
public class Long2 {
    public long x;
    public long y;

    public long length() {
        return 2L;
    }

    public Long2() {
    }

    public Long2(long j) {
        this.y = j;
        this.x = j;
    }

    public Long2(long j, long j2) {
        this.x = j;
        this.y = j2;
    }

    public Long2(Long2 long2) {
        this.x = long2.x;
        this.y = long2.y;
    }

    public void add(Long2 long2) {
        this.x += long2.x;
        this.y += long2.y;
    }

    public static Long2 add(Long2 long2, Long2 long3) {
        Long2 long4 = new Long2();
        long4.x = long2.x + long3.x;
        long4.y = long2.y + long3.y;
        return long4;
    }

    public void add(long j) {
        this.x += j;
        this.y += j;
    }

    public static Long2 add(Long2 long2, long j) {
        Long2 long3 = new Long2();
        long3.x = long2.x + j;
        long3.y = long2.y + j;
        return long3;
    }

    public void sub(Long2 long2) {
        this.x -= long2.x;
        this.y -= long2.y;
    }

    public static Long2 sub(Long2 long2, Long2 long3) {
        Long2 long4 = new Long2();
        long4.x = long2.x - long3.x;
        long4.y = long2.y - long3.y;
        return long4;
    }

    public void sub(long j) {
        this.x -= j;
        this.y -= j;
    }

    public static Long2 sub(Long2 long2, long j) {
        Long2 long3 = new Long2();
        long3.x = long2.x - j;
        long3.y = long2.y - j;
        return long3;
    }

    public void mul(Long2 long2) {
        this.x *= long2.x;
        this.y *= long2.y;
    }

    public static Long2 mul(Long2 long2, Long2 long3) {
        Long2 long4 = new Long2();
        long4.x = long2.x * long3.x;
        long4.y = long2.y * long3.y;
        return long4;
    }

    public void mul(long j) {
        this.x *= j;
        this.y *= j;
    }

    public static Long2 mul(Long2 long2, long j) {
        Long2 long3 = new Long2();
        long3.x = long2.x * j;
        long3.y = long2.y * j;
        return long3;
    }

    public void div(Long2 long2) {
        this.x /= long2.x;
        this.y /= long2.y;
    }

    public static Long2 div(Long2 long2, Long2 long3) {
        Long2 long4 = new Long2();
        long4.x = long2.x / long3.x;
        long4.y = long2.y / long3.y;
        return long4;
    }

    public void div(long j) {
        this.x /= j;
        this.y /= j;
    }

    public static Long2 div(Long2 long2, long j) {
        Long2 long3 = new Long2();
        long3.x = long2.x / j;
        long3.y = long2.y / j;
        return long3;
    }

    public void mod(Long2 long2) {
        this.x %= long2.x;
        this.y %= long2.y;
    }

    public static Long2 mod(Long2 long2, Long2 long3) {
        Long2 long4 = new Long2();
        long4.x = long2.x % long3.x;
        long4.y = long2.y % long3.y;
        return long4;
    }

    public void mod(long j) {
        this.x %= j;
        this.y %= j;
    }

    public static Long2 mod(Long2 long2, long j) {
        Long2 long3 = new Long2();
        long3.x = long2.x % j;
        long3.y = long2.y % j;
        return long3;
    }

    public void negate() {
        this.x = -this.x;
        this.y = -this.y;
    }

    public long dotProduct(Long2 long2) {
        return (this.x * long2.x) + (this.y * long2.y);
    }

    public static long dotProduct(Long2 long2, Long2 long3) {
        return (long3.x * long2.x) + (long3.y * long2.y);
    }

    public void addMultiple(Long2 long2, long j) {
        this.x += long2.x * j;
        this.y += long2.y * j;
    }

    public void set(Long2 long2) {
        this.x = long2.x;
        this.y = long2.y;
    }

    public void setValues(long j, long j2) {
        this.x = j;
        this.y = j2;
    }

    public long elementSum() {
        return this.x + this.y;
    }

    public long get(int i) {
        if (i == 0) {
            return this.x;
        }
        if (i == 1) {
            return this.y;
        }
        throw new IndexOutOfBoundsException("Index: i");
    }

    public void setAt(int i, long j) {
        if (i == 0) {
            this.x = j;
        } else {
            if (i == 1) {
                this.y = j;
                return;
            }
            throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, long j) {
        if (i == 0) {
            this.x += j;
        } else {
            if (i == 1) {
                this.y += j;
                return;
            }
            throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void copyTo(long[] jArr, int i) {
        jArr[i] = this.x;
        jArr[i + 1] = this.y;
    }
}
