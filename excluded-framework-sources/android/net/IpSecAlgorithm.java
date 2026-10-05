package android.net;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.util.HexDump;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public final class IpSecAlgorithm implements Parcelable {
    public static final String AUTH_CRYPT_AES_GCM = "rfc4106(gcm(aes))";
    public static final String AUTH_HMAC_MD5 = "hmac(md5)";
    public static final String AUTH_HMAC_SHA1 = "hmac(sha1)";
    public static final String AUTH_HMAC_SHA256 = "hmac(sha256)";
    public static final String AUTH_HMAC_SHA384 = "hmac(sha384)";
    public static final String AUTH_HMAC_SHA512 = "hmac(sha512)";
    public static final Parcelable.Creator<IpSecAlgorithm> CREATOR = new Parcelable.Creator<IpSecAlgorithm>() { // from class: android.net.IpSecAlgorithm.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public IpSecAlgorithm createFromParcel(Parcel parcel) {
            return new IpSecAlgorithm(parcel.readString(), parcel.createByteArray(), parcel.readInt());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public IpSecAlgorithm[] newArray(int i) {
            return new IpSecAlgorithm[i];
        }
    };
    public static final String CRYPT_AES_CBC = "cbc(aes)";
    public static final String CRYPT_NULL = "ecb(cipher_null)";
    private static final String TAG = "IpSecAlgorithm";
    private final byte[] mKey;
    private final String mName;
    private final int mTruncLenBits;

    @Retention(RetentionPolicy.SOURCE)
    public @interface AlgorithmName {
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public IpSecAlgorithm(String str, byte[] bArr) {
        this(str, bArr, 0);
    }

    public IpSecAlgorithm(String str, byte[] bArr, int i) {
        this.mName = str;
        byte[] bArr2 = (byte[]) bArr.clone();
        this.mKey = bArr2;
        this.mTruncLenBits = i;
        checkValidOrThrow(this.mName, bArr2.length * 8, i);
    }

    public String getName() {
        return this.mName;
    }

    public byte[] getKey() {
        return (byte[]) this.mKey.clone();
    }

    public int getTruncationLengthBits() {
        return this.mTruncLenBits;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mName);
        parcel.writeByteArray(this.mKey);
        parcel.writeInt(this.mTruncLenBits);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:26:0x0051  */
    /* JADX WARN: Code duplicated, block: B:43:0x008c A[PHI: r8
  0x008c: PHI (r8v14 boolean) = (r8v3 boolean), (r8v6 boolean), (r8v9 boolean), (r8v20 boolean), (r8v20 boolean), (r8v20 boolean) binds: [B:75:0x00c3, B:69:0x00b9, B:63:0x00af, B:40:0x0086, B:41:0x0088, B:42:0x008a] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:57:0x00a5 A[PHI: r0
  0x00a5: PHI (r0v25 boolean) = (r0v22 boolean), (r0v27 boolean) binds: [B:56:0x00a3, B:49:0x0097] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:84:0x00d2 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:85:0x00d4 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:86:0x00d5  */
    /* JADX WARN: Code duplicated, block: B:88:0x00ec  */
    /* JADX WARN: Instruction removed from duplicated block: B:86:0x00d5, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:88:0x00ec, please report this as an issue */
    private static void checkValidOrThrow(String str, int i, int i2) {
        boolean z;
        boolean z2;
        boolean z3 = false;
        boolean z4 = true;
        switch (str) {
            case "cbc(aes)":
                z = i == 128 || i == 192 || i == 256;
                if (!z) {
                    throw new IllegalArgumentException("Invalid key material keyLength: " + i);
                }
                if (z4) {
                    return;
                }
                throw new IllegalArgumentException("Invalid truncation keyLength: " + i2);
            case "hmac(md5)":
                z = i == 128;
                if (i2 >= 96 && i2 <= 128) {
                    z3 = true;
                }
                z4 = z3;
                if (!z) {
                    throw new IllegalArgumentException("Invalid key material keyLength: " + i);
                }
                if (z4) {
                    return;
                }
                throw new IllegalArgumentException("Invalid truncation keyLength: " + i2);
            case "hmac(sha1)":
                z = i == 160;
                if (i2 >= 96 && i2 <= 160) {
                    z3 = true;
                }
                z4 = z3;
                if (!z) {
                    throw new IllegalArgumentException("Invalid key material keyLength: " + i);
                }
                if (z4) {
                    return;
                }
                throw new IllegalArgumentException("Invalid truncation keyLength: " + i2);
            case "hmac(sha256)":
                z = i == 256;
                if (i2 >= 96 && i2 <= 256) {
                    z3 = true;
                }
                z4 = z3;
                if (!z) {
                    throw new IllegalArgumentException("Invalid key material keyLength: " + i);
                }
                if (z4) {
                    return;
                }
                throw new IllegalArgumentException("Invalid truncation keyLength: " + i2);
            case "hmac(sha384)":
                z2 = i == 384;
                if (i2 >= 192 && i2 <= 384) {
                    z3 = true;
                }
                z = z2;
                z4 = z3;
                if (!z) {
                    throw new IllegalArgumentException("Invalid key material keyLength: " + i);
                }
                if (z4) {
                    return;
                }
                throw new IllegalArgumentException("Invalid truncation keyLength: " + i2);
            case "hmac(sha512)":
                z2 = i == 512;
                if (i2 >= 256 && i2 <= 512) {
                    z3 = true;
                }
                z = z2;
                z4 = z3;
                if (!z) {
                    throw new IllegalArgumentException("Invalid key material keyLength: " + i);
                }
                if (z4) {
                    return;
                }
                throw new IllegalArgumentException("Invalid truncation keyLength: " + i2);
            case "rfc4106(gcm(aes))":
                z = i == 160 || i == 224 || i == 288;
                if (i2 == 64 || i2 == 96 || i2 == 128) {
                    z3 = true;
                }
                z4 = z3;
                if (!z) {
                    throw new IllegalArgumentException("Invalid key material keyLength: " + i);
                }
                if (z4) {
                    return;
                }
                throw new IllegalArgumentException("Invalid truncation keyLength: " + i2);
            default:
                throw new IllegalArgumentException("Couldn't find an algorithm: " + str);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:20:0x0043  */
    public boolean isAuthentication() {
        byte b;
        switch (getName()) {
            case "hmac(sha256)":
                b = 2;
                break;
            case "hmac(sha384)":
                b = 3;
                break;
            case "hmac(sha512)":
                b = 4;
                break;
            case "hmac(md5)":
                b = 0;
                break;
            case "hmac(sha1)":
                b = 1;
                break;
            default:
                b = -1;
                break;
        }
        return b == 0 || b == 1 || b == 2 || b == 3 || b == 4;
    }

    public boolean isEncryption() {
        return getName().equals(CRYPT_AES_CBC);
    }

    public boolean isAead() {
        return getName().equals(AUTH_CRYPT_AES_GCM);
    }

    private static boolean isUnsafeBuild() {
        return Build.IS_DEBUGGABLE && Build.IS_ENG;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{mName=");
        sb.append(this.mName);
        sb.append(", mKey=");
        sb.append(isUnsafeBuild() ? HexDump.toHexString(this.mKey) : "<hidden>");
        sb.append(", mTruncLenBits=");
        sb.append(this.mTruncLenBits);
        sb.append("}");
        return sb.toString();
    }

    public static boolean equals(IpSecAlgorithm ipSecAlgorithm, IpSecAlgorithm ipSecAlgorithm2) {
        if (ipSecAlgorithm == null || ipSecAlgorithm2 == null) {
            return ipSecAlgorithm == ipSecAlgorithm2;
        }
        return ipSecAlgorithm.mName.equals(ipSecAlgorithm2.mName) && Arrays.equals(ipSecAlgorithm.mKey, ipSecAlgorithm2.mKey) && ipSecAlgorithm.mTruncLenBits == ipSecAlgorithm2.mTruncLenBits;
    }
}
