package android.security.keystore;

import android.security.keymaster.KeymasterArguments;
import android.security.keymaster.KeymasterDefs;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import javax.crypto.spec.IvParameterSpec;

/* JADX INFO: loaded from: classes2.dex */
public class AndroidKeyStore3DESCipherSpi extends AndroidKeyStoreCipherSpiBase {
    private static final int BLOCK_SIZE_BYTES = 8;
    private byte[] mIv;
    private boolean mIvHasBeenUsed;
    private final boolean mIvRequired;
    private final int mKeymasterBlockMode;
    private final int mKeymasterPadding;

    @Override // javax.crypto.CipherSpi
    protected int engineGetBlockSize() {
        return 8;
    }

    @Override // javax.crypto.CipherSpi
    protected int engineGetOutputSize(int i) {
        return i + 24;
    }

    @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase
    protected int getAdditionalEntropyAmountForFinish() {
        return 0;
    }

    @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase
    public /* bridge */ /* synthetic */ void finalize() throws Throwable {
        super.finalize();
    }

    AndroidKeyStore3DESCipherSpi(int i, int i2, boolean z) {
        this.mKeymasterBlockMode = i;
        this.mKeymasterPadding = i2;
        this.mIvRequired = z;
    }

    static abstract class ECB extends AndroidKeyStore3DESCipherSpi {
        protected ECB(int i) {
            super(1, i, false);
        }

        public static class NoPadding extends ECB {
            @Override // android.security.keystore.AndroidKeyStore3DESCipherSpi, android.security.keystore.AndroidKeyStoreCipherSpiBase
            public /* bridge */ /* synthetic */ void finalize() throws Throwable {
                super.finalize();
            }

            public NoPadding() {
                super(1);
            }
        }

        public static class PKCS7Padding extends ECB {
            @Override // android.security.keystore.AndroidKeyStore3DESCipherSpi, android.security.keystore.AndroidKeyStoreCipherSpiBase
            public /* bridge */ /* synthetic */ void finalize() throws Throwable {
                super.finalize();
            }

            public PKCS7Padding() {
                super(64);
            }
        }
    }

    static abstract class CBC extends AndroidKeyStore3DESCipherSpi {
        protected CBC(int i) {
            super(2, i, true);
        }

        public static class NoPadding extends CBC {
            @Override // android.security.keystore.AndroidKeyStore3DESCipherSpi, android.security.keystore.AndroidKeyStoreCipherSpiBase
            public /* bridge */ /* synthetic */ void finalize() throws Throwable {
                super.finalize();
            }

            public NoPadding() {
                super(1);
            }
        }

        public static class PKCS7Padding extends CBC {
            @Override // android.security.keystore.AndroidKeyStore3DESCipherSpi, android.security.keystore.AndroidKeyStoreCipherSpiBase
            public /* bridge */ /* synthetic */ void finalize() throws Throwable {
                super.finalize();
            }

            public PKCS7Padding() {
                super(64);
            }
        }
    }

    @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase
    protected void initKey(int i, Key key) throws InvalidKeyException {
        if (!(key instanceof AndroidKeyStoreSecretKey)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Unsupported key: ");
            sb.append(key != null ? key.getClass().getName() : "null");
            throw new InvalidKeyException(sb.toString());
        }
        if (!KeyProperties.KEY_ALGORITHM_3DES.equalsIgnoreCase(key.getAlgorithm())) {
            throw new InvalidKeyException("Unsupported key algorithm: " + key.getAlgorithm() + ". Only " + KeyProperties.KEY_ALGORITHM_3DES + " supported");
        }
        setKey((AndroidKeyStoreSecretKey) key);
    }

    @Override // javax.crypto.CipherSpi
    protected final byte[] engineGetIV() {
        return ArrayUtils.cloneIfNotEmpty(this.mIv);
    }

    @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase, javax.crypto.CipherSpi
    protected AlgorithmParameters engineGetParameters() {
        byte[] bArr;
        if (!this.mIvRequired || (bArr = this.mIv) == null || bArr.length <= 0) {
            return null;
        }
        try {
            AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance(KeyProperties.KEY_ALGORITHM_3DES);
            algorithmParameters.init(new IvParameterSpec(this.mIv));
            return algorithmParameters;
        } catch (NoSuchAlgorithmException e) {
            throw new ProviderException("Failed to obtain 3DES AlgorithmParameters", e);
        } catch (InvalidParameterSpecException e2) {
            throw new ProviderException("Failed to initialize 3DES AlgorithmParameters with an IV", e2);
        }
    }

    @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase
    protected void initAlgorithmSpecificParameters() throws InvalidKeyException {
        if (this.mIvRequired && !isEncrypting()) {
            throw new InvalidKeyException("IV required when decrypting. Use IvParameterSpec or AlgorithmParameters to provide it.");
        }
    }

    @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase
    protected void initAlgorithmSpecificParameters(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        if (!this.mIvRequired) {
            if (algorithmParameterSpec == null) {
                return;
            }
            throw new InvalidAlgorithmParameterException("Unsupported parameters: " + algorithmParameterSpec);
        }
        if (algorithmParameterSpec == null) {
            if (!isEncrypting()) {
                throw new InvalidAlgorithmParameterException("IvParameterSpec must be provided when decrypting");
            }
        } else {
            if (!(algorithmParameterSpec instanceof IvParameterSpec)) {
                throw new InvalidAlgorithmParameterException("Only IvParameterSpec supported");
            }
            byte[] iv = ((IvParameterSpec) algorithmParameterSpec).getIV();
            this.mIv = iv;
            if (iv == null) {
                throw new InvalidAlgorithmParameterException("Null IV in IvParameterSpec");
            }
        }
    }

    @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase
    protected void initAlgorithmSpecificParameters(AlgorithmParameters algorithmParameters) throws InvalidAlgorithmParameterException {
        if (!this.mIvRequired) {
            if (algorithmParameters == null) {
                return;
            }
            throw new InvalidAlgorithmParameterException("Unsupported parameters: " + algorithmParameters);
        }
        if (algorithmParameters == null) {
            if (!isEncrypting()) {
                throw new InvalidAlgorithmParameterException("IV required when decrypting. Use IvParameterSpec or AlgorithmParameters to provide it.");
            }
            return;
        }
        if (!KeyProperties.KEY_ALGORITHM_3DES.equalsIgnoreCase(algorithmParameters.getAlgorithm())) {
            throw new InvalidAlgorithmParameterException("Unsupported AlgorithmParameters algorithm: " + algorithmParameters.getAlgorithm() + ". Supported: DESede");
        }
        try {
            byte[] iv = ((IvParameterSpec) algorithmParameters.getParameterSpec(IvParameterSpec.class)).getIV();
            this.mIv = iv;
            if (iv == null) {
                throw new InvalidAlgorithmParameterException("Null IV in AlgorithmParameters");
            }
        } catch (InvalidParameterSpecException e) {
            if (!isEncrypting()) {
                throw new InvalidAlgorithmParameterException("IV required when decrypting, but not found in parameters: " + algorithmParameters, e);
            }
            this.mIv = null;
        }
    }

    @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase
    protected final int getAdditionalEntropyAmountForBegin() {
        return (this.mIvRequired && this.mIv == null && isEncrypting()) ? 8 : 0;
    }

    @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase
    protected void addAlgorithmSpecificParametersToBegin(KeymasterArguments keymasterArguments) {
        byte[] bArr;
        if (isEncrypting() && this.mIvRequired && this.mIvHasBeenUsed) {
            throw new IllegalStateException("IV has already been used. Reusing IV in encryption mode violates security best practices.");
        }
        keymasterArguments.addEnum(KeymasterDefs.KM_TAG_ALGORITHM, 33);
        keymasterArguments.addEnum(KeymasterDefs.KM_TAG_BLOCK_MODE, this.mKeymasterBlockMode);
        keymasterArguments.addEnum(KeymasterDefs.KM_TAG_PADDING, this.mKeymasterPadding);
        if (!this.mIvRequired || (bArr = this.mIv) == null) {
            return;
        }
        keymasterArguments.addBytes(KeymasterDefs.KM_TAG_NONCE, bArr);
    }

    @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase
    protected void loadAlgorithmSpecificParametersFromBeginResult(KeymasterArguments keymasterArguments) {
        this.mIvHasBeenUsed = true;
        byte[] bytes = keymasterArguments.getBytes(KeymasterDefs.KM_TAG_NONCE, null);
        byte[] bArr = (bytes == null || bytes.length != 0) ? bytes : null;
        if (!this.mIvRequired) {
            if (bArr != null) {
                throw new ProviderException("IV in use despite IV not being used by this transformation");
            }
            return;
        }
        byte[] bArr2 = this.mIv;
        if (bArr2 == null) {
            this.mIv = bArr;
        } else if (bArr != null && !Arrays.equals(bArr, bArr2)) {
            throw new ProviderException("IV in use differs from provided IV");
        }
    }

    @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase
    protected final void resetAll() {
        this.mIv = null;
        this.mIvHasBeenUsed = false;
        super.resetAll();
    }
}
