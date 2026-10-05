package android.renderscript;

/* JADX INFO: loaded from: classes2.dex */
public class Double4 {
    public double w;
    public double x;
    public double y;
    public double z;

    public int length() {
        return 4;
    }

    public Double4() {
    }

    public Double4(Double4 double4) {
        this.x = double4.x;
        this.y = double4.y;
        this.z = double4.z;
        this.w = double4.w;
    }

    public Double4(double d, double d2, double d3, double d4) {
        this.x = d;
        this.y = d2;
        this.z = d3;
        this.w = d4;
    }

    public static Double4 add(Double4 double4, Double4 double5) {
        Double4 double6 = new Double4();
        double6.x = double4.x + double5.x;
        double6.y = double4.y + double5.y;
        double6.z = double4.z + double5.z;
        double6.w = double4.w + double5.w;
        return double6;
    }

    public void add(Double4 double4) {
        this.x += double4.x;
        this.y += double4.y;
        this.z += double4.z;
        this.w += double4.w;
    }

    public void add(double d) {
        this.x += d;
        this.y += d;
        this.z += d;
        this.w += d;
    }

    public static Double4 add(Double4 double4, double d) {
        Double4 double5 = new Double4();
        double5.x = double4.x + d;
        double5.y = double4.y + d;
        double5.z = double4.z + d;
        double5.w = double4.w + d;
        return double5;
    }

    public void sub(Double4 double4) {
        this.x -= double4.x;
        this.y -= double4.y;
        this.z -= double4.z;
        this.w -= double4.w;
    }

    public void sub(double d) {
        this.x -= d;
        this.y -= d;
        this.z -= d;
        this.w -= d;
    }

    public static Double4 sub(Double4 double4, double d) {
        Double4 double5 = new Double4();
        double5.x = double4.x - d;
        double5.y = double4.y - d;
        double5.z = double4.z - d;
        double5.w = double4.w - d;
        return double5;
    }

    public static Double4 sub(Double4 double4, Double4 double5) {
        Double4 double6 = new Double4();
        double6.x = double4.x - double5.x;
        double6.y = double4.y - double5.y;
        double6.z = double4.z - double5.z;
        double6.w = double4.w - double5.w;
        return double6;
    }

    public void mul(Double4 double4) {
        this.x *= double4.x;
        this.y *= double4.y;
        this.z *= double4.z;
        this.w *= double4.w;
    }

    public void mul(double d) {
        this.x *= d;
        this.y *= d;
        this.z *= d;
        this.w *= d;
    }

    public static Double4 mul(Double4 double4, Double4 double5) {
        Double4 double6 = new Double4();
        double6.x = double4.x * double5.x;
        double6.y = double4.y * double5.y;
        double6.z = double4.z * double5.z;
        double6.w = double4.w * double5.w;
        return double6;
    }

    public static Double4 mul(Double4 double4, double d) {
        Double4 double5 = new Double4();
        double5.x = double4.x * d;
        double5.y = double4.y * d;
        double5.z = double4.z * d;
        double5.w = double4.w * d;
        return double5;
    }

    public void div(Double4 double4) {
        this.x /= double4.x;
        this.y /= double4.y;
        this.z /= double4.z;
        this.w /= double4.w;
    }

    public void div(double d) {
        this.x /= d;
        this.y /= d;
        this.z /= d;
        this.w /= d;
    }

    public static Double4 div(Double4 double4, double d) {
        Double4 double5 = new Double4();
        double5.x = double4.x / d;
        double5.y = double4.y / d;
        double5.z = double4.z / d;
        double5.w = double4.w / d;
        return double5;
    }

    public static Double4 div(Double4 double4, Double4 double5) {
        Double4 double6 = new Double4();
        double6.x = double4.x / double5.x;
        double6.y = double4.y / double5.y;
        double6.z = double4.z / double5.z;
        double6.w = double4.w / double5.w;
        return double6;
    }

    public double dotProduct(Double4 double4) {
        return (this.x * double4.x) + (this.y * double4.y) + (this.z * double4.z) + (this.w * double4.w);
    }

    public static double dotProduct(Double4 double4, Double4 double5) {
        return (double5.x * double4.x) + (double5.y * double4.y) + (double5.z * double4.z) + (double5.w * double4.w);
    }

    public void addMultiple(Double4 double4, double d) {
        this.x += double4.x * d;
        this.y += double4.y * d;
        this.z += double4.z * d;
        this.w += double4.w * d;
    }

    public void set(Double4 double4) {
        this.x = double4.x;
        this.y = double4.y;
        this.z = double4.z;
        this.w = double4.w;
    }

    public void negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.w = -this.w;
    }

    public double elementSum() {
        return this.x + this.y + this.z + this.w;
    }

    public double get(int i) {
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

    public void setAt(int i, double d) {
        if (i == 0) {
            this.x = d;
            return;
        }
        if (i == 1) {
            this.y = d;
        } else if (i == 2) {
            this.z = d;
        } else {
            if (i == 3) {
                this.w = d;
                return;
            }
            throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void addAt(int i, double d) {
        if (i == 0) {
            this.x += d;
            return;
        }
        if (i == 1) {
            this.y += d;
        } else if (i == 2) {
            this.z += d;
        } else {
            if (i == 3) {
                this.w += d;
                return;
            }
            throw new IndexOutOfBoundsException("Index: i");
        }
    }

    public void setValues(double d, double d2, double d3, double d4) {
        this.x = d;
        this.y = d2;
        this.z = d3;
        this.w = d4;
    }

    public void copyTo(double[] dArr, int i) {
        dArr[i] = this.x;
        dArr[i + 1] = this.y;
        dArr[i + 2] = this.z;
        dArr[i + 3] = this.w;
    }
}
