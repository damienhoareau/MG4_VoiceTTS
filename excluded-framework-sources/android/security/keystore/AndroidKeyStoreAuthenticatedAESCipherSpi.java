package android.security.keystore;

import android.os.IBinder;
import android.security.KeyStore;
import android.security.KeyStoreException;
import android.security.keymaster.KeymasterArguments;
import android.security.keymaster.KeymasterDefs;
import android.security.keymaster.OperationResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import javax.crypto.spec.GCMParameterSpec;
import libcore.util.EmptyArray;

/* JADX INFO: loaded from: classes2.dex */
abstract class AndroidKeyStoreAuthenticatedAESCipherSpi extends AndroidKeyStoreCipherSpiBase {
    private static final int BLOCK_SIZE_BYTES = 16;
    private byte[] mIv;
    private boolean mIvHasBeenUsed;
    private final int mKeymasterBlockMode;
    private final int mKeymasterPadding;

    @Override // javax.crypto.CipherSpi
    protected final int engineGetBlockSize() {
        return 16;
    }

    static abstract class GCM extends AndroidKeyStoreAuthenticatedAESCipherSpi {
        private static final int DEFAULT_TAG_LENGTH_BITS = 128;
        private static final int IV_LENGTH_BYTES = 12;
        private static final int MAX_SUPPORTED_TAG_LENGTH_BITS = 128;
        static final int MIN_SUPPORTED_TAG_LENGTH_BITS = 96;
        private int mTagLengthBits;

        @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase
        protected final int getAdditionalEntropyAmountForFinish() {
            return 0;
        }

        GCM(int i) {
            super(32, i);
            this.mTagLengthBits = 128;
        }

        @Override // android.security.keystore.AndroidKeyStoreAuthenticatedAESCipherSpi, android.security.keystore.AndroidKeyStoreCipherSpiBase
        protected final void resetAll() {
            this.mTagLengthBits = 128;
            super.resetAll();
        }

        @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase
        protected final void resetWhilePreservingInitState() {
            super.resetWhilePreservingInitState();
        }

        @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase
        protected final void initAlgorithmSpecificParameters() throws InvalidKeyException {
            if (!isEncrypting()) {
                throw new InvalidKeyException("IV required when decrypting. Use IvParameterSpec or AlgorithmParameters to provide it.");
            }
        }

        @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase
        protected final void initAlgorithmSpecificParameters(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
            if (algorithmParameterSpec == null) {
                if (!isEncrypting()) {
                    throw new InvalidAlgorithmParameterException("GCMParameterSpec must be provided when decrypting");
                }
                return;
            }
            if (!(algorithmParameterSpec instanceof GCMParameterSpec)) {
                throw new InvalidAlgorithmParameterException("Only GCMParameterSpec supported");
            }
            GCMParameterSpec gCMParameterSpec = (GCMParameterSpec) algorithmParameterSpec;
            byte[] iv = gCMParameterSpec.getIV();
            if (iv == null) {
                throw new InvalidAlgorithmParameterException("Null IV in GCMParameterSpec");
            }
            if (iv.length != 12) {
                throw new InvalidAlgorithmParameterException("Unsupported IV length: " + iv.length + " bytes. Only 12 bytes long IV supported");
            }
            int tLen = gCMParameterSpec.getTLen();
            if (tLen < 96 || tLen > 128 || tLen % 8 != 0) {
                throw new InvalidAlgorithmParameterException("Unsupported tag length: " + tLen + " bits. Supported lengths: 96, 104, 112, 120, 128");
            }
            setIv(iv);
            this.mTagLengthBits = tLen;
        }

        @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase
        protected final void initAlgorithmSpecificParameters(AlgorithmParameters algorithmParameters) throws InvalidAlgorithmParameterException {
            if (algorithmParameters == null) {
                if (!isEncrypting()) {
                    throw new InvalidAlgorithmParameterException("IV required when decrypting. Use GCMParameterSpec or GCM AlgorithmParameters to provide it.");
                }
                return;
            }
            if (!KeyProperties.BLOCK_MODE_GCM.equalsIgnoreCase(algorithmParameters.getAlgorithm())) {
                throw new InvalidAlgorithmParameterException("Unsupported AlgorithmParameters algorithm: " + algorithmParameters.getAlgorithm() + ". Supported: GCM");
            }
            try {
                initAlgorithmSpecificParameters((GCMParameterSpec) algorithmParameters.getParameterSpec(GCMParameterSpec.class));
            } catch (InvalidParameterSpecException e) {
                if (!isEncrypting()) {
                    throw new InvalidAlgorithmParameterException("IV and tag length required when decrypting, but not found in parameters: " + algorithmParameters, e);
                }
                setIv(null);
            }
        }

        @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase, javax.crypto.CipherSpi
        protected final AlgorithmParameters engineGetParameters() {
            byte[] iv = getIv();
            if (iv == null || iv.length <= 0) {
                return null;
            }
            try {
                AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance(KeyProperties.BLOCK_MODE_GCM);
                algorithmParameters.init(new GCMParameterSpec(this.mTagLengthBits, iv));
                return algorithmParameters;
            } catch (NoSuchAlgorithmException e) {
                throw new ProviderException("Failed to obtain GCM AlgorithmParameters", e);
            } catch (InvalidParameterSpecException e2) {
                throw new ProviderException("Failed to initialize GCM AlgorithmParameters", e2);
            }
        }

        @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase
        protected KeyStoreCryptoOperationStreamer createMainDataStreamer(KeyStore keyStore, IBinder iBinder) {
            KeyStoreCryptoOperationChunkedStreamer keyStoreCryptoOperationChunkedStreamer = new KeyStoreCryptoOperationChunkedStreamer(new KeyStoreCryptoOperationChunkedStreamer.MainDataStream(keyStore, iBinder));
            return isEncrypting() ? keyStoreCryptoOperationChunkedStreamer : new BufferAllOutputUntilDoFinalStreamer(keyStoreCryptoOperationChunkedStreamer);
        }

        @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase
        protected final KeyStoreCryptoOperationStreamer createAdditionalAuthenticationDataStreamer(KeyStore keyStore, IBinder iBinder) {
            return new KeyStoreCryptoOperationChunkedStreamer(new AdditionalAuthenticationDataStream(keyStore, iBinder));
        }

        @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase
        protected final int getAdditionalEntropyAmountForBegin() {
            return (getIv() == null && isEncrypting()) ? 12 : 0;
        }

        @Override // android.security.keystore.AndroidKeyStoreAuthenticatedAESCipherSpi, android.security.keystore.AndroidKeyStoreCipherSpiBase
        protected final void addAlgorithmSpecificParametersToBegin(KeymasterArguments keymasterArguments) {
            super.addAlgorithmSpecificParametersToBegin(keymasterArguments);
            keymasterArguments.addUnsignedInt(KeymasterDefs.KM_TAG_MAC_LENGTH, this.mTagLengthBits);
        }

        protected final int getTagLengthBits() {
            return this.mTagLengthBits;
        }

        public static final class NoPadding extends GCM {
            @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase
            public /* bridge */ /* synthetic */ void finalize() throws Throwable {
                super.finalize();
            }

            public NoPadding() {
                super(1);
            }

            @Override // javax.crypto.CipherSpi
            protected final int engineGetOutputSize(int i) {
                long consumedInputSizeBytes;
                int tagLengthBits = (getTagLengthBits() + 7) / 8;
                if (isEncrypting()) {
                    consumedInputSizeBytes = (getConsumedInputSizeBytes() - getProducedOutputSizeBytes()) + ((long) i) + ((long) tagLengthBits);
                } else {
                    consumedInputSizeBytes = ((getConsumedInputSizeBytes() - getProducedOutputSizeBytes()) + ((long) i)) - ((long) tagLengthBits);
                }
                if (consumedInputSizeBytes < 0) {
                    return 0;
                }
                if (consumedInputSizeBytes > 2147483647L) {
                    return Integer.MAX_VALUE;
                }
                return (int) consumedInputSizeBytes;
            }
        }
    }

    AndroidKeyStoreAuthenticatedAESCipherSpi(int i, int i2) {
        this.mKeymasterBlockMode = i;
        this.mKeymasterPadding = i2;
    }

    @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase
    protected void resetAll() {
        this.mIv = null;
        this.mIvHasBeenUsed = false;
        super.resetAll();
    }

    @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase
    protected final void initKey(int i, Key key) throws InvalidKeyException {
        if (!(key instanceof AndroidKeyStoreSecretKey)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Unsupported key: ");
            sb.append(key != null ? key.getClass().getName() : "null");
            throw new InvalidKeyException(sb.toString());
        }
        if (!KeyProperties.KEY_ALGORITHM_AES.equalsIgnoreCase(key.getAlgorithm())) {
            throw new InvalidKeyException("Unsupported key algorithm: " + key.getAlgorithm() + ". Only " + KeyProperties.KEY_ALGORITHM_AES + " supported");
        }
        setKey((AndroidKeyStoreSecretKey) key);
    }

    @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase
    protected void addAlgorithmSpecificParametersToBegin(KeymasterArguments keymasterArguments) {
        if (isEncrypting() && this.mIvHasBeenUsed) {
            throw new IllegalStateException("IV has already been used. Reusing IV in encryption mode violates security best practices.");
        }
        keymasterArguments.addEnum(KeymasterDefs.KM_TAG_ALGORITHM, 32);
        keymasterArguments.addEnum(KeymasterDefs.KM_TAG_BLOCK_MODE, this.mKeymasterBlockMode);
        keymasterArguments.addEnum(KeymasterDefs.KM_TAG_PADDING, this.mKeymasterPadding);
        byte[] bArr = this.mIv;
        if (bArr != null) {
            keymasterArguments.addBytes(KeymasterDefs.KM_TAG_NONCE, bArr);
        }
    }

    @Override // android.security.keystore.AndroidKeyStoreCipherSpiBase
    protected final void loadAlgorithmSpecificParametersFromBeginResult(KeymasterArguments keymasterArguments) {
        this.mIvHasBeenUsed = true;
        byte[] bytes = keymasterArguments.getBytes(KeymasterDefs.KM_TAG_NONCE, null);
        byte[] bArr = (bytes == null || bytes.length != 0) ? bytes : null;
        byte[] bArr2 = this.mIv;
        if (bArr2 == null) {
            this.mIv = bArr;
        } else if (bArr != null && !Arrays.equals(bArr, bArr2)) {
            throw new ProviderException("IV in use differs from provided IV");
        }
    }

    @Override // javax.crypto.CipherSpi
    protected final byte[] engineGetIV() {
        return ArrayUtils.cloneIfNotEmpty(this.mIv);
    }

    protected void setIv(byte[] bArr) {
        this.mIv = bArr;
    }

    protected byte[] getIv() {
        return this.mIv;
    }

    private static class BufferAllOutputUntilDoFinalStreamer implements KeyStoreCryptoOperationStreamer {
        private ByteArrayOutputStream mBufferedOutput;
        private final KeyStoreCryptoOperationStreamer mDelegate;
        private long mProducedOutputSizeBytes;

        private BufferAllOutputUntilDoFinalStreamer(KeyStoreCryptoOperationStreamer keyStoreCryptoOperationStreamer) {
            this.mBufferedOutput = new ByteArrayOutputStream();
            this.mDelegate = keyStoreCryptoOperationStreamer;
        }

        @Override // android.security.keystore.KeyStoreCryptoOperationStreamer
        public byte[] update(byte[] bArr, int i, int i2) throws KeyStoreException {
            byte[] bArrUpdate = this.mDelegate.update(bArr, i, i2);
            if (bArrUpdate != null) {
                try {
                    this.mBufferedOutput.write(bArrUpdate);
                } catch (IOException e) {
                    throw new ProviderException("Failed to buffer output", e);
                }
            }
            return EmptyArray.BYTE;
        }

        @Override // android.security.keystore.KeyStoreCryptoOperationStreamer
        public byte[] doFinal(byte[] bArr, int i, int i2, byte[] bArr2, byte[] bArr3) throws KeyStoreException {
            byte[] bArrDoFinal = this.mDelegate.doFinal(bArr, i, i2, bArr2, bArr3);
            if (bArrDoFinal != null) {
                try {
                    this.mBufferedOutput.write(bArrDoFinal);
                } catch (IOException e) {
                    throw new ProviderException("Failed to buffer output", e);
                }
            }
            byte[] byteArray = this.mBufferedOutput.toByteArray();
            this.mBufferedOutput.reset();
            this.mProducedOutputSizeBytes += (long) byteArray.length;
            return byteArray;
        }

        @Override // android.security.keystore.KeyStoreCryptoOperationStreamer
        public long getConsumedInputSizeBytes() {
            return this.mDelegate.getConsumedInputSizeBytes();
        }

        @Override // android.security.keystore.KeyStoreCryptoOperationStreamer
        public long getProducedOutputSizeBytes() {
            return this.mProducedOutputSizeBytes;
        }
    }

    private static class AdditionalAuthenticationDataStream implements KeyStoreCryptoOperationChunkedStreamer.Stream {
        private final KeyStore mKeyStore;
        private final IBinder mOperationToken;

        private AdditionalAuthenticationDataStream(KeyStore keyStore, IBinder iBinder) {
            this.mKeyStore = keyStore;
            this.mOperationToken = iBinder;
        }

        @Override // android.security.keystore.KeyStoreCryptoOperationChunkedStreamer.Stream
        public OperationResult update(byte[] bArr) {
            KeymasterArguments keymasterArguments = new KeymasterArguments();
            keymasterArguments.addBytes(KeymasterDefs.KM_TAG_ASSOCIATED_DATA, bArr);
            OperationResult operationResultUpdate = this.mKeyStore.update(this.mOperationToken, keymasterArguments, null);
            return operationResultUpdate.resultCode == 1 ? new OperationResult(operationResultUpdate.resultCode, operationResultUpdate.token, operationResultUpdate.operationHandle, bArr.length, operationResultUpdate.output, operationResultUpdate.outParams) : operationResultUpdate;
        }

        @Override // android.security.keystore.KeyStoreCryptoOperationChunkedStreamer.Stream
        public OperationResult finish(byte[] bArr, byte[] bArr2) {
            if (bArr2 != null && bArr2.length > 0) {
                throw new ProviderException("AAD stream does not support additional entropy");
            }
            return new OperationResult(1, this.mOperationToken, 0L, 0, EmptyArray.BYTE, new KeymasterArguments());
        }
    }
}
