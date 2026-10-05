package android.util.apk;

import android.app.backup.FullBackup;
import android.security.keystore.KeyProperties;
import android.util.ArrayMap;
import android.util.Pair;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
final class ApkSigningBlockUtils {
    private static final long APK_SIG_BLOCK_MAGIC_HI = 3617552046287187010L;
    private static final long APK_SIG_BLOCK_MAGIC_LO = 2334950737559900225L;
    private static final int APK_SIG_BLOCK_MIN_SIZE = 32;
    private static final int CHUNK_SIZE_BYTES = 1048576;
    static final int CONTENT_DIGEST_CHUNKED_SHA256 = 1;
    static final int CONTENT_DIGEST_CHUNKED_SHA512 = 2;
    static final int CONTENT_DIGEST_VERITY_CHUNKED_SHA256 = 3;
    static final int SIGNATURE_DSA_WITH_SHA256 = 769;
    static final int SIGNATURE_ECDSA_WITH_SHA256 = 513;
    static final int SIGNATURE_ECDSA_WITH_SHA512 = 514;
    static final int SIGNATURE_RSA_PKCS1_V1_5_WITH_SHA256 = 259;
    static final int SIGNATURE_RSA_PKCS1_V1_5_WITH_SHA512 = 260;
    static final int SIGNATURE_RSA_PSS_WITH_SHA256 = 257;
    static final int SIGNATURE_RSA_PSS_WITH_SHA512 = 258;
    static final int SIGNATURE_VERITY_DSA_WITH_SHA256 = 1061;
    static final int SIGNATURE_VERITY_ECDSA_WITH_SHA256 = 1059;
    static final int SIGNATURE_VERITY_RSA_PKCS1_V1_5_WITH_SHA256 = 1057;

    private ApkSigningBlockUtils() {
    }

    static SignatureInfo findSignature(RandomAccessFile randomAccessFile, int i) throws SignatureNotFoundException, IOException {
        Pair<ByteBuffer, Long> eocd = getEocd(randomAccessFile);
        ByteBuffer byteBuffer = eocd.first;
        long jLongValue = eocd.second.longValue();
        if (ZipUtils.isZip64EndOfCentralDirectoryLocatorPresent(randomAccessFile, jLongValue)) {
            throw new SignatureNotFoundException("ZIP64 APK not supported");
        }
        long centralDirOffset = getCentralDirOffset(byteBuffer, jLongValue);
        Pair<ByteBuffer, Long> pairFindApkSigningBlock = findApkSigningBlock(randomAccessFile, centralDirOffset);
        ByteBuffer byteBuffer2 = pairFindApkSigningBlock.first;
        return new SignatureInfo(findApkSignatureSchemeBlock(byteBuffer2, i), pairFindApkSigningBlock.second.longValue(), centralDirOffset, jLongValue, byteBuffer);
    }

    static void verifyIntegrity(Map<Integer, byte[]> map, RandomAccessFile randomAccessFile, SignatureInfo signatureInfo) throws SecurityException {
        if (map.isEmpty()) {
            throw new SecurityException("No digests provided");
        }
        ArrayMap arrayMap = new ArrayMap();
        boolean z = true;
        if (map.containsKey(1)) {
            arrayMap.put(1, map.get(1));
        }
        if (map.containsKey(2)) {
            arrayMap.put(2, map.get(2));
        }
        boolean z2 = false;
        if (!arrayMap.isEmpty()) {
            try {
                verifyIntegrityFor1MbChunkBasedAlgorithm(arrayMap, randomAccessFile.getFD(), signatureInfo);
                z = false;
            } catch (IOException e) {
                throw new SecurityException("Cannot get FD", e);
            }
        }
        if (map.containsKey(3)) {
            verifyIntegrityForVerityBasedAlgorithm(map.get(3), randomAccessFile, signatureInfo);
        } else {
            z2 = z;
        }
        if (z2) {
            throw new SecurityException("No known digest exists for integrity check");
        }
    }

    private static void verifyIntegrityFor1MbChunkBasedAlgorithm(Map<Integer, byte[]> map, FileDescriptor fileDescriptor, SignatureInfo signatureInfo) throws SecurityException {
        MemoryMappedFileDataSource memoryMappedFileDataSource = new MemoryMappedFileDataSource(fileDescriptor, 0L, signatureInfo.apkSigningBlockOffset);
        MemoryMappedFileDataSource memoryMappedFileDataSource2 = new MemoryMappedFileDataSource(fileDescriptor, signatureInfo.centralDirOffset, signatureInfo.eocdOffset - signatureInfo.centralDirOffset);
        ByteBuffer byteBufferDuplicate = signatureInfo.eocd.duplicate();
        byteBufferDuplicate.order(ByteOrder.LITTLE_ENDIAN);
        ZipUtils.setZipEocdCentralDirectoryOffset(byteBufferDuplicate, signatureInfo.apkSigningBlockOffset);
        ByteBufferDataSource byteBufferDataSource = new ByteBufferDataSource(byteBufferDuplicate);
        int size = map.size();
        int[] iArr = new int[size];
        Iterator<Integer> it = map.keySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            iArr[i] = it.next().intValue();
            i++;
        }
        try {
            byte[][] bArrComputeContentDigestsPer1MbChunk = computeContentDigestsPer1MbChunk(iArr, new DataSource[]{memoryMappedFileDataSource, memoryMappedFileDataSource2, byteBufferDataSource});
            for (int i2 = 0; i2 < size; i2++) {
                int i3 = iArr[i2];
                if (!MessageDigest.isEqual(map.get(Integer.valueOf(i3)), bArrComputeContentDigestsPer1MbChunk[i2])) {
                    throw new SecurityException(getContentDigestAlgorithmJcaDigestAlgorithm(i3) + " digest of contents did not verify");
                }
            }
        } catch (DigestException e) {
            throw new SecurityException("Failed to compute digest(s) of contents", e);
        }
    }

    private static byte[][] computeContentDigestsPer1MbChunk(int[] iArr, DataSource[] dataSourceArr) throws DigestException {
        String str;
        DataSource[] dataSourceArr2 = dataSourceArr;
        long j = 0;
        int i = 0;
        long chunkCount = 0;
        for (DataSource dataSource : dataSourceArr2) {
            chunkCount += getChunkCount(dataSource.size());
        }
        if (chunkCount >= 2097151) {
            throw new DigestException("Too many chunks: " + chunkCount);
        }
        int i2 = (int) chunkCount;
        byte[][] bArr = new byte[iArr.length][];
        for (int i3 = 0; i3 < iArr.length; i3++) {
            byte[] bArr2 = new byte[(getContentDigestAlgorithmOutputSizeBytes(iArr[i3]) * i2) + 5];
            bArr2[0] = 90;
            setUnsignedInt32LittleEndian(i2, bArr2, 1);
            bArr[i3] = bArr2;
        }
        byte[] bArr3 = new byte[5];
        bArr3[0] = -91;
        int length = iArr.length;
        MessageDigest[] messageDigestArr = new MessageDigest[length];
        int i4 = 0;
        while (true) {
            str = " digest not supported";
            if (i4 >= iArr.length) {
                break;
            }
            String contentDigestAlgorithmJcaDigestAlgorithm = getContentDigestAlgorithmJcaDigestAlgorithm(iArr[i4]);
            try {
                messageDigestArr[i4] = MessageDigest.getInstance(contentDigestAlgorithmJcaDigestAlgorithm);
                i4++;
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(contentDigestAlgorithmJcaDigestAlgorithm + " digest not supported", e);
            }
        }
        MultipleDigestDataDigester multipleDigestDataDigester = new MultipleDigestDataDigester(messageDigestArr);
        int length2 = dataSourceArr2.length;
        int i5 = 0;
        int i6 = 0;
        while (i5 < length2) {
            DataSource dataSource2 = dataSourceArr2[i5];
            int i7 = length2;
            String str2 = str;
            long j2 = j;
            MultipleDigestDataDigester multipleDigestDataDigester2 = multipleDigestDataDigester;
            long size = dataSource2.size();
            while (size > j) {
                int iMin = (int) Math.min(size, 1048576L);
                setUnsignedInt32LittleEndian(iMin, bArr3, 1);
                for (int i8 = 0; i8 < length; i8++) {
                    messageDigestArr[i8].update(bArr3);
                }
                MultipleDigestDataDigester multipleDigestDataDigester3 = multipleDigestDataDigester2;
                try {
                    dataSource2.feedIntoDataDigester(multipleDigestDataDigester3, j2, iMin);
                    multipleDigestDataDigester2 = multipleDigestDataDigester3;
                    int i9 = 0;
                    while (i9 < iArr.length) {
                        int i10 = iArr[i9];
                        byte[] bArr4 = bArr3;
                        byte[] bArr5 = bArr[i9];
                        int contentDigestAlgorithmOutputSizeBytes = getContentDigestAlgorithmOutputSizeBytes(i10);
                        int i11 = length;
                        MessageDigest messageDigest = messageDigestArr[i9];
                        MessageDigest[] messageDigestArr2 = messageDigestArr;
                        int iDigest = messageDigest.digest(bArr5, (i6 * contentDigestAlgorithmOutputSizeBytes) + 5, contentDigestAlgorithmOutputSizeBytes);
                        if (iDigest != contentDigestAlgorithmOutputSizeBytes) {
                            throw new RuntimeException("Unexpected output size of " + messageDigest.getAlgorithm() + " digest: " + iDigest);
                        }
                        i9++;
                        bArr3 = bArr4;
                        length = i11;
                        messageDigestArr = messageDigestArr2;
                    }
                    long j3 = iMin;
                    j2 += j3;
                    size -= j3;
                    i6++;
                    bArr3 = bArr3;
                    j = 0;
                } catch (IOException e2) {
                    throw new DigestException("Failed to digest chunk #" + i6 + " of section #" + i, e2);
                }
            }
            i++;
            i5++;
            dataSourceArr2 = dataSourceArr;
            multipleDigestDataDigester = multipleDigestDataDigester2;
            str = str2;
            length2 = i7;
            j = 0;
        }
        String str3 = str;
        byte[][] bArr6 = new byte[iArr.length][];
        for (int i12 = 0; i12 < iArr.length; i12++) {
            int i13 = iArr[i12];
            byte[] bArr7 = bArr[i12];
            String contentDigestAlgorithmJcaDigestAlgorithm2 = getContentDigestAlgorithmJcaDigestAlgorithm(i13);
            try {
                bArr6[i12] = MessageDigest.getInstance(contentDigestAlgorithmJcaDigestAlgorithm2).digest(bArr7);
            } catch (NoSuchAlgorithmException e3) {
                throw new RuntimeException(contentDigestAlgorithmJcaDigestAlgorithm2 + str3, e3);
            }
        }
        return bArr6;
    }

    static byte[] parseVerityDigestAndVerifySourceLength(byte[] bArr, long j, SignatureInfo signatureInfo) throws SecurityException {
        if (bArr.length != 40) {
            throw new SecurityException("Verity digest size is wrong: " + bArr.length);
        }
        ByteBuffer byteBufferOrder = ByteBuffer.wrap(bArr).order(ByteOrder.LITTLE_ENDIAN);
        byteBufferOrder.position(32);
        if (byteBufferOrder.getLong() != j - (signatureInfo.centralDirOffset - signatureInfo.apkSigningBlockOffset)) {
            throw new SecurityException("APK content size did not verify");
        }
        return Arrays.copyOfRange(bArr, 0, 32);
    }

    private static void verifyIntegrityForVerityBasedAlgorithm(byte[] bArr, RandomAccessFile randomAccessFile, SignatureInfo signatureInfo) throws SecurityException {
        try {
            if (Arrays.equals(parseVerityDigestAndVerifySourceLength(bArr, randomAccessFile.length(), signatureInfo), ApkVerityBuilder.generateApkVerity(randomAccessFile, signatureInfo, new ByteBufferFactory() { // from class: android.util.apk.ApkSigningBlockUtils.1
                @Override // android.util.apk.ByteBufferFactory
                public ByteBuffer create(int i) {
                    return ByteBuffer.allocate(i);
                }
            }).rootHash)) {
            } else {
                throw new SecurityException("APK verity digest of contents did not verify");
            }
        } catch (IOException | DigestException | NoSuchAlgorithmException e) {
            throw new SecurityException("Error during verification", e);
        }
    }

    public static byte[] generateApkVerity(String str, ByteBufferFactory byteBufferFactory, SignatureInfo signatureInfo) throws SignatureNotFoundException, NoSuchAlgorithmException, DigestException, IOException, SecurityException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(str, FullBackup.ROOT_TREE_TOKEN);
        try {
            byte[] bArr = ApkVerityBuilder.generateApkVerity(randomAccessFile, signatureInfo, byteBufferFactory).rootHash;
            randomAccessFile.close();
            return bArr;
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                try {
                    randomAccessFile.close();
                } catch (Throwable th3) {
                    th.addSuppressed(th3);
                }
                throw th2;
            }
        }
    }

    static Pair<ByteBuffer, Long> getEocd(RandomAccessFile randomAccessFile) throws SignatureNotFoundException, IOException {
        Pair<ByteBuffer, Long> pairFindZipEndOfCentralDirectoryRecord = ZipUtils.findZipEndOfCentralDirectoryRecord(randomAccessFile);
        if (pairFindZipEndOfCentralDirectoryRecord != null) {
            return pairFindZipEndOfCentralDirectoryRecord;
        }
        throw new SignatureNotFoundException("Not an APK file: ZIP End of Central Directory record not found");
    }

    static long getCentralDirOffset(ByteBuffer byteBuffer, long j) throws SignatureNotFoundException {
        long zipEocdCentralDirectoryOffset = ZipUtils.getZipEocdCentralDirectoryOffset(byteBuffer);
        if (zipEocdCentralDirectoryOffset <= j) {
            if (ZipUtils.getZipEocdCentralDirectorySizeBytes(byteBuffer) + zipEocdCentralDirectoryOffset == j) {
                return zipEocdCentralDirectoryOffset;
            }
            throw new SignatureNotFoundException("ZIP Central Directory is not immediately followed by End of Central Directory");
        }
        throw new SignatureNotFoundException("ZIP Central Directory offset out of range: " + zipEocdCentralDirectoryOffset + ". ZIP End of Central Directory offset: " + j);
    }

    private static long getChunkCount(long j) {
        return ((j + 1048576) - 1) / 1048576;
    }

    static int compareSignatureAlgorithm(int i, int i2) {
        return compareContentDigestAlgorithm(getSignatureAlgorithmContentDigestAlgorithm(i), getSignatureAlgorithmContentDigestAlgorithm(i2));
    }

    private static int compareContentDigestAlgorithm(int i, int i2) {
        if (i == 1) {
            if (i2 == 1) {
                return 0;
            }
            if (i2 == 2 || i2 == 3) {
                return -1;
            }
            throw new IllegalArgumentException("Unknown digestAlgorithm2: " + i2);
        }
        if (i == 2) {
            if (i2 != 1) {
                if (i2 == 2) {
                    return 0;
                }
                if (i2 != 3) {
                    throw new IllegalArgumentException("Unknown digestAlgorithm2: " + i2);
                }
            }
            return 1;
        }
        if (i != 3) {
            throw new IllegalArgumentException("Unknown digestAlgorithm1: " + i);
        }
        if (i2 == 1) {
            return 1;
        }
        if (i2 == 2) {
            return -1;
        }
        if (i2 == 3) {
            return 0;
        }
        throw new IllegalArgumentException("Unknown digestAlgorithm2: " + i2);
    }

    static int getSignatureAlgorithmContentDigestAlgorithm(int i) {
        if (i == 513) {
            return 1;
        }
        if (i == 514) {
            return 2;
        }
        if (i == 769) {
            return 1;
        }
        if (i == SIGNATURE_VERITY_RSA_PKCS1_V1_5_WITH_SHA256 || i == SIGNATURE_VERITY_ECDSA_WITH_SHA256 || i == 1061) {
            return 3;
        }
        switch (i) {
            case 257:
            case 259:
                return 1;
            case 258:
            case 260:
                return 2;
            default:
                throw new IllegalArgumentException("Unknown signature algorithm: 0x" + Long.toHexString(i & (-1)));
        }
    }

    static String getContentDigestAlgorithmJcaDigestAlgorithm(int i) {
        if (i == 1) {
            return KeyProperties.DIGEST_SHA256;
        }
        if (i == 2) {
            return KeyProperties.DIGEST_SHA512;
        }
        if (i == 3) {
            return KeyProperties.DIGEST_SHA256;
        }
        throw new IllegalArgumentException("Unknown content digest algorthm: " + i);
    }

    private static int getContentDigestAlgorithmOutputSizeBytes(int i) {
        if (i == 1) {
            return 32;
        }
        if (i == 2) {
            return 64;
        }
        if (i == 3) {
            return 32;
        }
        throw new IllegalArgumentException("Unknown content digest algorthm: " + i);
    }

    static String getSignatureAlgorithmJcaKeyAlgorithm(int i) {
        if (i == 513 || i == 514) {
            return KeyProperties.KEY_ALGORITHM_EC;
        }
        if (i == 769) {
            return "DSA";
        }
        if (i == SIGNATURE_VERITY_RSA_PKCS1_V1_5_WITH_SHA256) {
            return KeyProperties.KEY_ALGORITHM_RSA;
        }
        if (i == SIGNATURE_VERITY_ECDSA_WITH_SHA256) {
            return KeyProperties.KEY_ALGORITHM_EC;
        }
        if (i == 1061) {
            return "DSA";
        }
        switch (i) {
            case 257:
            case 258:
            case 259:
            case 260:
                return KeyProperties.KEY_ALGORITHM_RSA;
            default:
                throw new IllegalArgumentException("Unknown signature algorithm: 0x" + Long.toHexString(i & (-1)));
        }
    }

    static Pair<String, ? extends AlgorithmParameterSpec> getSignatureAlgorithmJcaSignatureAlgorithm(int i) {
        if (i != 513) {
            if (i == 514) {
                return Pair.create("SHA512withECDSA", null);
            }
            if (i != 769) {
                if (i != SIGNATURE_VERITY_RSA_PKCS1_V1_5_WITH_SHA256) {
                    if (i != SIGNATURE_VERITY_ECDSA_WITH_SHA256) {
                        if (i != 1061) {
                            switch (i) {
                                case 257:
                                    return Pair.create("SHA256withRSA/PSS", new PSSParameterSpec(KeyProperties.DIGEST_SHA256, "MGF1", MGF1ParameterSpec.SHA256, 32, 1));
                                case 258:
                                    return Pair.create("SHA512withRSA/PSS", new PSSParameterSpec(KeyProperties.DIGEST_SHA512, "MGF1", MGF1ParameterSpec.SHA512, 64, 1));
                                case 259:
                                    break;
                                case 260:
                                    return Pair.create("SHA512withRSA", null);
                                default:
                                    throw new IllegalArgumentException("Unknown signature algorithm: 0x" + Long.toHexString(i & (-1)));
                            }
                        }
                    }
                }
                return Pair.create("SHA256withRSA", null);
            }
            return Pair.create("SHA256withDSA", null);
        }
        return Pair.create("SHA256withECDSA", null);
    }

    static ByteBuffer sliceFromTo(ByteBuffer byteBuffer, int i, int i2) {
        if (i < 0) {
            throw new IllegalArgumentException("start: " + i);
        }
        if (i2 < i) {
            throw new IllegalArgumentException("end < start: " + i2 + " < " + i);
        }
        int iCapacity = byteBuffer.capacity();
        if (i2 > byteBuffer.capacity()) {
            throw new IllegalArgumentException("end > capacity: " + i2 + " > " + iCapacity);
        }
        int iLimit = byteBuffer.limit();
        int iPosition = byteBuffer.position();
        try {
            byteBuffer.position(0);
            byteBuffer.limit(i2);
            byteBuffer.position(i);
            ByteBuffer byteBufferSlice = byteBuffer.slice();
            byteBufferSlice.order(byteBuffer.order());
            return byteBufferSlice;
        } finally {
            byteBuffer.position(0);
            byteBuffer.limit(iLimit);
            byteBuffer.position(iPosition);
        }
    }

    static ByteBuffer getByteBuffer(ByteBuffer byteBuffer, int i) throws BufferUnderflowException {
        if (i < 0) {
            throw new IllegalArgumentException("size: " + i);
        }
        int iLimit = byteBuffer.limit();
        int iPosition = byteBuffer.position();
        int i2 = i + iPosition;
        if (i2 < iPosition || i2 > iLimit) {
            throw new BufferUnderflowException();
        }
        byteBuffer.limit(i2);
        try {
            ByteBuffer byteBufferSlice = byteBuffer.slice();
            byteBufferSlice.order(byteBuffer.order());
            byteBuffer.position(i2);
            return byteBufferSlice;
        } finally {
            byteBuffer.limit(iLimit);
        }
    }

    static ByteBuffer getLengthPrefixedSlice(ByteBuffer byteBuffer) throws IOException {
        if (byteBuffer.remaining() < 4) {
            throw new IOException("Remaining buffer too short to contain length of length-prefixed field. Remaining: " + byteBuffer.remaining());
        }
        int i = byteBuffer.getInt();
        if (i < 0) {
            throw new IllegalArgumentException("Negative length");
        }
        if (i > byteBuffer.remaining()) {
            throw new IOException("Length-prefixed field longer than remaining buffer. Field length: " + i + ", remaining: " + byteBuffer.remaining());
        }
        return getByteBuffer(byteBuffer, i);
    }

    static byte[] readLengthPrefixedByteArray(ByteBuffer byteBuffer) throws IOException {
        int i = byteBuffer.getInt();
        if (i < 0) {
            throw new IOException("Negative length");
        }
        if (i > byteBuffer.remaining()) {
            throw new IOException("Underflow while reading length-prefixed value. Length: " + i + ", available: " + byteBuffer.remaining());
        }
        byte[] bArr = new byte[i];
        byteBuffer.get(bArr);
        return bArr;
    }

    static void setUnsignedInt32LittleEndian(int i, byte[] bArr, int i2) {
        bArr[i2] = (byte) (i & 255);
        bArr[i2 + 1] = (byte) ((i >>> 8) & 255);
        bArr[i2 + 2] = (byte) ((i >>> 16) & 255);
        bArr[i2 + 3] = (byte) ((i >>> 24) & 255);
    }

    static Pair<ByteBuffer, Long> findApkSigningBlock(RandomAccessFile randomAccessFile, long j) throws SignatureNotFoundException, IOException {
        if (j < 32) {
            throw new SignatureNotFoundException("APK too small for APK Signing Block. ZIP Central Directory offset: " + j);
        }
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(24);
        byteBufferAllocate.order(ByteOrder.LITTLE_ENDIAN);
        randomAccessFile.seek(j - ((long) byteBufferAllocate.capacity()));
        randomAccessFile.readFully(byteBufferAllocate.array(), byteBufferAllocate.arrayOffset(), byteBufferAllocate.capacity());
        if (byteBufferAllocate.getLong(8) != APK_SIG_BLOCK_MAGIC_LO || byteBufferAllocate.getLong(16) != APK_SIG_BLOCK_MAGIC_HI) {
            throw new SignatureNotFoundException("No APK Signing Block before ZIP Central Directory");
        }
        long j2 = byteBufferAllocate.getLong(0);
        if (j2 < byteBufferAllocate.capacity() || j2 > 2147483639) {
            throw new SignatureNotFoundException("APK Signing Block size out of range: " + j2);
        }
        int i = (int) (8 + j2);
        long j3 = j - ((long) i);
        if (j3 < 0) {
            throw new SignatureNotFoundException("APK Signing Block offset out of range: " + j3);
        }
        ByteBuffer byteBufferAllocate2 = ByteBuffer.allocate(i);
        byteBufferAllocate2.order(ByteOrder.LITTLE_ENDIAN);
        randomAccessFile.seek(j3);
        randomAccessFile.readFully(byteBufferAllocate2.array(), byteBufferAllocate2.arrayOffset(), byteBufferAllocate2.capacity());
        long j4 = byteBufferAllocate2.getLong(0);
        if (j4 != j2) {
            throw new SignatureNotFoundException("APK Signing Block sizes in header and footer do not match: " + j4 + " vs " + j2);
        }
        return Pair.create(byteBufferAllocate2, Long.valueOf(j3));
    }

    static ByteBuffer findApkSignatureSchemeBlock(ByteBuffer byteBuffer, int i) throws SignatureNotFoundException {
        checkByteOrderLittleEndian(byteBuffer);
        ByteBuffer byteBufferSliceFromTo = sliceFromTo(byteBuffer, 8, byteBuffer.capacity() - 24);
        int i2 = 0;
        while (byteBufferSliceFromTo.hasRemaining()) {
            i2++;
            if (byteBufferSliceFromTo.remaining() < 8) {
                throw new SignatureNotFoundException("Insufficient data to read size of APK Signing Block entry #" + i2);
            }
            long j = byteBufferSliceFromTo.getLong();
            if (j < 4 || j > 2147483647L) {
                throw new SignatureNotFoundException("APK Signing Block entry #" + i2 + " size out of range: " + j);
            }
            int i3 = (int) j;
            int iPosition = byteBufferSliceFromTo.position() + i3;
            if (i3 > byteBufferSliceFromTo.remaining()) {
                throw new SignatureNotFoundException("APK Signing Block entry #" + i2 + " size out of range: " + i3 + ", available: " + byteBufferSliceFromTo.remaining());
            }
            if (byteBufferSliceFromTo.getInt() == i) {
                return getByteBuffer(byteBufferSliceFromTo, i3 - 4);
            }
            byteBufferSliceFromTo.position(iPosition);
        }
        throw new SignatureNotFoundException("No block with ID " + i + " in APK Signing Block.");
    }

    private static void checkByteOrderLittleEndian(ByteBuffer byteBuffer) {
        if (byteBuffer.order() != ByteOrder.LITTLE_ENDIAN) {
            throw new IllegalArgumentException("ByteBuffer byte order must be little endian");
        }
    }

    private static class MultipleDigestDataDigester implements DataDigester {
        private final MessageDigest[] mMds;

        MultipleDigestDataDigester(MessageDigest[] messageDigestArr) {
            this.mMds = messageDigestArr;
        }

        @Override // android.util.apk.DataDigester
        public void consume(ByteBuffer byteBuffer) {
            ByteBuffer byteBufferSlice = byteBuffer.slice();
            for (MessageDigest messageDigest : this.mMds) {
                byteBufferSlice.position(0);
                messageDigest.update(byteBufferSlice);
            }
        }
    }
}
