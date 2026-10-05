package android.telephony;

import android.annotation.SystemApi;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.os.Parcel;
import android.os.Parcelable;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import com.android.internal.telephony.uicc.IccUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
public final class UiccAccessRule implements Parcelable {
    public static final Parcelable.Creator<UiccAccessRule> CREATOR = new Parcelable.Creator<UiccAccessRule>() { // from class: android.telephony.UiccAccessRule.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UiccAccessRule createFromParcel(Parcel parcel) {
            return new UiccAccessRule(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UiccAccessRule[] newArray(int i) {
            return new UiccAccessRule[i];
        }
    };
    private static final int ENCODING_VERSION = 1;
    private static final String TAG = "UiccAccessRule";
    private final long mAccessType;
    private final byte[] mCertificateHash;
    private final String mPackageName;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static byte[] encodeRules(UiccAccessRule[] uiccAccessRuleArr) {
        if (uiccAccessRuleArr == null) {
            return null;
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            dataOutputStream.writeInt(1);
            dataOutputStream.writeInt(uiccAccessRuleArr.length);
            for (UiccAccessRule uiccAccessRule : uiccAccessRuleArr) {
                dataOutputStream.writeInt(uiccAccessRule.mCertificateHash.length);
                dataOutputStream.write(uiccAccessRule.mCertificateHash);
                if (uiccAccessRule.mPackageName != null) {
                    dataOutputStream.writeBoolean(true);
                    dataOutputStream.writeUTF(uiccAccessRule.mPackageName);
                } else {
                    dataOutputStream.writeBoolean(false);
                }
                dataOutputStream.writeLong(uiccAccessRule.mAccessType);
            }
            dataOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("ByteArrayOutputStream should never lead to an IOException", e);
        }
    }

    public static UiccAccessRule[] decodeRules(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        try {
            DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bArr));
            try {
                dataInputStream.readInt();
                int i = dataInputStream.readInt();
                UiccAccessRule[] uiccAccessRuleArr = new UiccAccessRule[i];
                for (int i2 = 0; i2 < i; i2++) {
                    byte[] bArr2 = new byte[dataInputStream.readInt()];
                    dataInputStream.readFully(bArr2);
                    uiccAccessRuleArr[i2] = new UiccAccessRule(bArr2, dataInputStream.readBoolean() ? dataInputStream.readUTF() : null, dataInputStream.readLong());
                }
                dataInputStream.close();
                dataInputStream.close();
                return uiccAccessRuleArr;
            } catch (Throwable th) {
                try {
                    throw th;
                } catch (Throwable th2) {
                    try {
                        dataInputStream.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                    throw th2;
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("ByteArrayInputStream should never lead to an IOException", e);
        }
    }

    public UiccAccessRule(byte[] bArr, String str, long j) {
        this.mCertificateHash = bArr;
        this.mPackageName = str;
        this.mAccessType = j;
    }

    UiccAccessRule(Parcel parcel) {
        this.mCertificateHash = parcel.createByteArray();
        this.mPackageName = parcel.readString();
        this.mAccessType = parcel.readLong();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByteArray(this.mCertificateHash);
        parcel.writeString(this.mPackageName);
        parcel.writeLong(this.mAccessType);
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String getCertificateHexString() {
        return IccUtils.bytesToHexString(this.mCertificateHash);
    }

    public int getCarrierPrivilegeStatus(PackageInfo packageInfo) {
        if (packageInfo.signatures == null || packageInfo.signatures.length == 0) {
            throw new IllegalArgumentException("Must use GET_SIGNATURES when looking up package info");
        }
        for (Signature signature : packageInfo.signatures) {
            int carrierPrivilegeStatus = getCarrierPrivilegeStatus(signature, packageInfo.packageName);
            if (carrierPrivilegeStatus != 0) {
                return carrierPrivilegeStatus;
            }
        }
        return 0;
    }

    public int getCarrierPrivilegeStatus(Signature signature, String str) {
        return (matches(getCertHash(signature, KeyProperties.DIGEST_SHA1), str) || matches(getCertHash(signature, KeyProperties.DIGEST_SHA256), str)) ? 1 : 0;
    }

    private boolean matches(byte[] bArr, String str) {
        return bArr != null && Arrays.equals(this.mCertificateHash, bArr) && (TextUtils.isEmpty(this.mPackageName) || this.mPackageName.equals(str));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UiccAccessRule uiccAccessRule = (UiccAccessRule) obj;
        return Arrays.equals(this.mCertificateHash, uiccAccessRule.mCertificateHash) && Objects.equals(this.mPackageName, uiccAccessRule.mPackageName) && this.mAccessType == uiccAccessRule.mAccessType;
    }

    public int hashCode() {
        return ((((Arrays.hashCode(this.mCertificateHash) + 31) * 31) + Objects.hashCode(this.mPackageName)) * 31) + Objects.hashCode(Long.valueOf(this.mAccessType));
    }

    public String toString() {
        return "cert: " + IccUtils.bytesToHexString(this.mCertificateHash) + " pkg: " + this.mPackageName + " access: " + this.mAccessType;
    }

    private static byte[] getCertHash(Signature signature, String str) {
        try {
            return MessageDigest.getInstance(str).digest(signature.toByteArray());
        } catch (NoSuchAlgorithmException e) {
            Rlog.e(TAG, "NoSuchAlgorithmException: " + e);
            return null;
        }
    }
}
