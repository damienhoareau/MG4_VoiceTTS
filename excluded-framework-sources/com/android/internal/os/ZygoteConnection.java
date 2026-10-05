package com.android.internal.os;

import android.net.Credentials;
import android.net.LocalSocket;
import android.os.FactoryTest;
import android.os.Process;
import android.os.SystemProperties;
import android.os.Trace;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructPollfd;
import android.util.Log;
import android.util.TimeUtils;
import dalvik.system.VMRuntime;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import libcore.io.IoUtils;

/* JADX INFO: loaded from: classes3.dex */
class ZygoteConnection {
    private static final String TAG = "Zygote";
    private static final int[][] intArray2d = (int[][]) Array.newInstance((Class<?>) int.class, 0, 0);
    private final String abiList;
    private boolean isEof;
    private final LocalSocket mSocket;
    private final DataOutputStream mSocketOutStream;
    private final BufferedReader mSocketReader;
    private final Credentials peer;

    ZygoteConnection(LocalSocket localSocket, String str) throws IOException {
        this.mSocket = localSocket;
        this.abiList = str;
        this.mSocketOutStream = new DataOutputStream(localSocket.getOutputStream());
        this.mSocketReader = new BufferedReader(new InputStreamReader(localSocket.getInputStream()), 256);
        this.mSocket.setSoTimeout(1000);
        try {
            this.peer = this.mSocket.getPeerCredentials();
            this.isEof = false;
        } catch (IOException e) {
            Log.e(TAG, "Cannot read peer credentials", e);
            throw e;
        }
    }

    FileDescriptor getFileDesciptor() {
        return this.mSocket.getFileDescriptor();
    }

    Runnable processOneCommand(ZygoteServer zygoteServer) throws Throwable {
        FileDescriptor fileDescriptor;
        FileDescriptor fileDescriptor2;
        int[] iArr;
        FileDescriptor fileDescriptor3;
        try {
            String[] argumentList = readArgumentList();
            FileDescriptor[] ancillaryFileDescriptors = this.mSocket.getAncillaryFileDescriptors();
            if (argumentList == null) {
                this.isEof = true;
                return null;
            }
            Arguments arguments = new Arguments(argumentList);
            if (arguments.abiListQuery) {
                handleAbiListQuery();
                return null;
            }
            if (arguments.preloadDefault) {
                handlePreload();
                return null;
            }
            if (arguments.preloadPackage != null) {
                handlePreloadPackage(arguments.preloadPackage, arguments.preloadPackageLibs, arguments.preloadPackageLibFileName, arguments.preloadPackageCacheKey);
                return null;
            }
            if (arguments.apiBlacklistExemptions != null) {
                handleApiBlacklistExemptions(arguments.apiBlacklistExemptions);
                return null;
            }
            if (arguments.hiddenApiAccessLogSampleRate != -1) {
                handleHiddenApiAccessLogSampleRate(arguments.hiddenApiAccessLogSampleRate);
                return null;
            }
            if (arguments.permittedCapabilities != 0 || arguments.effectiveCapabilities != 0) {
                throw new ZygoteSecurityException("Client may not specify capabilities: permitted=0x" + Long.toHexString(arguments.permittedCapabilities) + ", effective=0x" + Long.toHexString(arguments.effectiveCapabilities));
            }
            applyUidSecurityPolicy(arguments, this.peer);
            applyInvokeWithSecurityPolicy(arguments, this.peer);
            applyDebuggerSystemProperty(arguments);
            applyInvokeWithSystemProperty(arguments);
            int[][] iArr2 = arguments.rlimits != null ? (int[][]) arguments.rlimits.toArray(intArray2d) : null;
            if (arguments.invokeWith != null) {
                try {
                    FileDescriptor[] fileDescriptorArrPipe2 = Os.pipe2(OsConstants.O_CLOEXEC);
                    FileDescriptor fileDescriptor4 = fileDescriptorArrPipe2[1];
                    FileDescriptor fileDescriptor5 = fileDescriptorArrPipe2[0];
                    Os.fcntlInt(fileDescriptor4, OsConstants.F_SETFD, 0);
                    fileDescriptor = fileDescriptor5;
                    fileDescriptor2 = fileDescriptor4;
                    iArr = new int[]{fileDescriptor4.getInt$(), fileDescriptor5.getInt$()};
                } catch (ErrnoException e) {
                    throw new IllegalStateException("Unable to set up pipe for invoke-with", e);
                }
            } else {
                iArr = null;
                fileDescriptor2 = null;
                fileDescriptor = null;
            }
            int[] iArr3 = {-1, -1};
            FileDescriptor fileDescriptor6 = this.mSocket.getFileDescriptor();
            if (fileDescriptor6 != null) {
                iArr3[0] = fileDescriptor6.getInt$();
            }
            FileDescriptor serverSocketFileDescriptor = zygoteServer.getServerSocketFileDescriptor();
            if (serverSocketFileDescriptor != null) {
                iArr3[1] = serverSocketFileDescriptor.getInt$();
            }
            FileDescriptor fileDescriptor7 = fileDescriptor2;
            FileDescriptor fileDescriptor8 = fileDescriptor;
            int iForkAndSpecialize = Zygote.forkAndSpecialize(arguments.uid, arguments.gid, arguments.gids, arguments.runtimeFlags, iArr2, arguments.mountExternal, arguments.seInfo, arguments.niceName, iArr3, iArr, arguments.startChildZygote, arguments.instructionSet, arguments.appDataDir);
            if (iForkAndSpecialize == 0) {
                try {
                    zygoteServer.setForkChild();
                    zygoteServer.closeServerSocket();
                    IoUtils.closeQuietly(fileDescriptor8);
                    try {
                        try {
                            Runnable runnableHandleChildProc = handleChildProc(arguments, ancillaryFileDescriptors, fileDescriptor7, arguments.startChildZygote);
                            IoUtils.closeQuietly(fileDescriptor7);
                            IoUtils.closeQuietly((FileDescriptor) null);
                            return runnableHandleChildProc;
                        } catch (Throwable th) {
                            th = th;
                            fileDescriptor3 = fileDescriptor7;
                            fileDescriptor8 = null;
                            IoUtils.closeQuietly(fileDescriptor3);
                            IoUtils.closeQuietly(fileDescriptor8);
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    fileDescriptor3 = fileDescriptor7;
                }
            } else {
                try {
                    IoUtils.closeQuietly(fileDescriptor7);
                    try {
                        handleParentProc(iForkAndSpecialize, ancillaryFileDescriptors, fileDescriptor8);
                        IoUtils.closeQuietly((FileDescriptor) null);
                        IoUtils.closeQuietly(fileDescriptor8);
                        return null;
                    } catch (Throwable th4) {
                        th = th4;
                        fileDescriptor8 = fileDescriptor8;
                        fileDescriptor3 = null;
                    }
                } catch (Throwable th5) {
                    th = th5;
                    fileDescriptor3 = fileDescriptor7;
                }
            }
            IoUtils.closeQuietly(fileDescriptor3);
            IoUtils.closeQuietly(fileDescriptor8);
            throw th;
        } catch (IOException e2) {
            throw new IllegalStateException("IOException on command socket", e2);
        }
    }

    private void handleAbiListQuery() {
        try {
            byte[] bytes = this.abiList.getBytes(StandardCharsets.US_ASCII);
            this.mSocketOutStream.writeInt(bytes.length);
            this.mSocketOutStream.write(bytes);
        } catch (IOException e) {
            throw new IllegalStateException("Error writing to command socket", e);
        }
    }

    private void handlePreload() {
        try {
            if (isPreloadComplete()) {
                this.mSocketOutStream.writeInt(1);
            } else {
                preload();
                this.mSocketOutStream.writeInt(0);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error writing to command socket", e);
        }
    }

    private void handleApiBlacklistExemptions(String[] strArr) {
        try {
            ZygoteInit.setApiBlacklistExemptions(strArr);
            this.mSocketOutStream.writeInt(0);
        } catch (IOException e) {
            throw new IllegalStateException("Error writing to command socket", e);
        }
    }

    private void handleHiddenApiAccessLogSampleRate(int i) {
        try {
            ZygoteInit.setHiddenApiAccessLogSampleRate(i);
            this.mSocketOutStream.writeInt(0);
        } catch (IOException e) {
            throw new IllegalStateException("Error writing to command socket", e);
        }
    }

    protected void preload() {
        ZygoteInit.lazyPreload();
    }

    protected boolean isPreloadComplete() {
        return ZygoteInit.isPreloadComplete();
    }

    protected DataOutputStream getSocketOutputStream() {
        return this.mSocketOutStream;
    }

    protected void handlePreloadPackage(String str, String str2, String str3, String str4) {
        throw new RuntimeException("Zyogte does not support package preloading");
    }

    void closeSocket() {
        try {
            this.mSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception while closing command socket in parent", e);
        }
    }

    boolean isClosedByPeer() {
        return this.isEof;
    }

    static class Arguments {
        boolean abiListQuery;
        String[] apiBlacklistExemptions;
        String appDataDir;
        boolean capabilitiesSpecified;
        long effectiveCapabilities;
        boolean gidSpecified;
        int[] gids;
        String instructionSet;
        String invokeWith;
        String niceName;
        long permittedCapabilities;
        boolean preloadDefault;
        String preloadPackage;
        String preloadPackageCacheKey;
        String preloadPackageLibFileName;
        String preloadPackageLibs;
        String[] remainingArgs;
        ArrayList<int[]> rlimits;
        int runtimeFlags;
        String seInfo;
        boolean seInfoSpecified;
        boolean startChildZygote;
        int targetSdkVersion;
        boolean targetSdkVersionSpecified;
        boolean uidSpecified;
        int uid = 0;
        int gid = 0;
        int mountExternal = 0;
        int hiddenApiAccessLogSampleRate = -1;

        Arguments(String[] strArr) throws IllegalArgumentException {
            parseArgs(strArr);
        }

        private void parseArgs(String[] strArr) throws IllegalArgumentException {
            boolean z = false;
            int length = 0;
            boolean z2 = false;
            boolean z3 = true;
            while (length < strArr.length) {
                String str = strArr[length];
                if (str.equals("--")) {
                    length++;
                    break;
                }
                if (str.startsWith("--setuid=")) {
                    if (this.uidSpecified) {
                        throw new IllegalArgumentException("Duplicate arg specified");
                    }
                    this.uidSpecified = true;
                    this.uid = Integer.parseInt(str.substring(str.indexOf(61) + 1));
                } else if (str.startsWith("--setgid=")) {
                    if (this.gidSpecified) {
                        throw new IllegalArgumentException("Duplicate arg specified");
                    }
                    this.gidSpecified = true;
                    this.gid = Integer.parseInt(str.substring(str.indexOf(61) + 1));
                } else if (str.startsWith("--target-sdk-version=")) {
                    if (this.targetSdkVersionSpecified) {
                        throw new IllegalArgumentException("Duplicate target-sdk-version specified");
                    }
                    this.targetSdkVersionSpecified = true;
                    this.targetSdkVersion = Integer.parseInt(str.substring(str.indexOf(61) + 1));
                } else if (str.equals("--runtime-args")) {
                    z2 = true;
                } else if (str.startsWith("--runtime-flags=")) {
                    this.runtimeFlags = Integer.parseInt(str.substring(str.indexOf(61) + 1));
                } else if (str.startsWith("--seinfo=")) {
                    if (this.seInfoSpecified) {
                        throw new IllegalArgumentException("Duplicate arg specified");
                    }
                    this.seInfoSpecified = true;
                    this.seInfo = str.substring(str.indexOf(61) + 1);
                } else if (str.startsWith("--capabilities=")) {
                    if (this.capabilitiesSpecified) {
                        throw new IllegalArgumentException("Duplicate arg specified");
                    }
                    this.capabilitiesSpecified = true;
                    String[] strArrSplit = str.substring(str.indexOf(61) + 1).split(",", 2);
                    if (strArrSplit.length == 1) {
                        long jLongValue = Long.decode(strArrSplit[0]).longValue();
                        this.effectiveCapabilities = jLongValue;
                        this.permittedCapabilities = jLongValue;
                    } else {
                        this.permittedCapabilities = Long.decode(strArrSplit[0]).longValue();
                        this.effectiveCapabilities = Long.decode(strArrSplit[1]).longValue();
                    }
                } else if (str.startsWith("--rlimit=")) {
                    String[] strArrSplit2 = str.substring(str.indexOf(61) + 1).split(",");
                    if (strArrSplit2.length != 3) {
                        throw new IllegalArgumentException("--rlimit= should have 3 comma-delimited ints");
                    }
                    int[] iArr = new int[strArrSplit2.length];
                    for (int i = 0; i < strArrSplit2.length; i++) {
                        iArr[i] = Integer.parseInt(strArrSplit2[i]);
                    }
                    if (this.rlimits == null) {
                        this.rlimits = new ArrayList<>();
                    }
                    this.rlimits.add(iArr);
                } else if (str.startsWith("--setgroups=")) {
                    if (this.gids != null) {
                        throw new IllegalArgumentException("Duplicate arg specified");
                    }
                    String[] strArrSplit3 = str.substring(str.indexOf(61) + 1).split(",");
                    this.gids = new int[strArrSplit3.length];
                    for (int length2 = strArrSplit3.length - 1; length2 >= 0; length2--) {
                        this.gids[length2] = Integer.parseInt(strArrSplit3[length2]);
                    }
                } else if (str.equals("--invoke-with")) {
                    if (this.invokeWith != null) {
                        throw new IllegalArgumentException("Duplicate arg specified");
                    }
                    length++;
                    try {
                        this.invokeWith = strArr[length];
                    } catch (IndexOutOfBoundsException unused) {
                        throw new IllegalArgumentException("--invoke-with requires argument");
                    }
                } else if (str.startsWith("--nice-name=")) {
                    if (this.niceName != null) {
                        throw new IllegalArgumentException("Duplicate arg specified");
                    }
                    this.niceName = str.substring(str.indexOf(61) + 1);
                } else if (str.equals("--mount-external-default")) {
                    this.mountExternal = 1;
                } else if (str.equals("--mount-external-read")) {
                    this.mountExternal = 2;
                } else if (str.equals("--mount-external-write")) {
                    this.mountExternal = 3;
                } else if (str.equals("--query-abi-list")) {
                    this.abiListQuery = true;
                } else if (str.startsWith("--instruction-set=")) {
                    this.instructionSet = str.substring(str.indexOf(61) + 1);
                } else if (str.startsWith("--app-data-dir=")) {
                    this.appDataDir = str.substring(str.indexOf(61) + 1);
                } else if (str.equals("--preload-package")) {
                    int i2 = length + 1;
                    this.preloadPackage = strArr[i2];
                    int i3 = i2 + 1;
                    this.preloadPackageLibs = strArr[i3];
                    int i4 = i3 + 1;
                    this.preloadPackageLibFileName = strArr[i4];
                    length = i4 + 1;
                    this.preloadPackageCacheKey = strArr[length];
                } else {
                    if (str.equals("--preload-default")) {
                        this.preloadDefault = true;
                    } else if (str.equals("--start-child-zygote")) {
                        this.startChildZygote = true;
                    } else if (str.equals("--set-api-blacklist-exemptions")) {
                        this.apiBlacklistExemptions = (String[]) Arrays.copyOfRange(strArr, length + 1, strArr.length);
                        length = strArr.length;
                    } else {
                        if (!str.startsWith("--hidden-api-log-sampling-rate=")) {
                            break;
                        }
                        String strSubstring = str.substring(str.indexOf(61) + 1);
                        try {
                            this.hiddenApiAccessLogSampleRate = Integer.parseInt(strSubstring);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid log sampling rate: " + strSubstring, e);
                        }
                    }
                    z3 = false;
                }
                length++;
            }
            if (this.abiListQuery) {
                if (strArr.length - length > 0) {
                    throw new IllegalArgumentException("Unexpected arguments after --query-abi-list.");
                }
            } else if (this.preloadPackage != null) {
                if (strArr.length - length > 0) {
                    throw new IllegalArgumentException("Unexpected arguments after --preload-package.");
                }
            } else if (z3) {
                if (!z2) {
                    throw new IllegalArgumentException("Unexpected argument : " + strArr[length]);
                }
                String[] strArr2 = new String[strArr.length - length];
                this.remainingArgs = strArr2;
                System.arraycopy(strArr, length, strArr2, 0, strArr2.length);
            }
            if (this.startChildZygote) {
                for (String str2 : this.remainingArgs) {
                    if (str2.startsWith(Zygote.CHILD_ZYGOTE_SOCKET_NAME_ARG)) {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    throw new IllegalArgumentException("--start-child-zygote specified without --zygote-socket=");
                }
            }
        }
    }

    private String[] readArgumentList() throws IOException {
        try {
            String line = this.mSocketReader.readLine();
            if (line == null) {
                return null;
            }
            int i = Integer.parseInt(line);
            if (i > 1024) {
                throw new IOException("max arg count exceeded");
            }
            String[] strArr = new String[i];
            for (int i2 = 0; i2 < i; i2++) {
                strArr[i2] = this.mSocketReader.readLine();
                if (strArr[i2] == null) {
                    throw new IOException("truncated request");
                }
            }
            return strArr;
        } catch (NumberFormatException unused) {
            Log.e(TAG, "invalid Zygote wire format: non-int at argc");
            throw new IOException("invalid wire format");
        }
    }

    private static void applyUidSecurityPolicy(Arguments arguments, Credentials credentials) throws ZygoteSecurityException {
        if (credentials.getUid() == 1000) {
            if ((FactoryTest.getMode() == 0) && arguments.uidSpecified && arguments.uid < 1000) {
                throw new ZygoteSecurityException("System UID may not launch process with UID < 1000");
            }
        }
        if (!arguments.uidSpecified) {
            arguments.uid = credentials.getUid();
            arguments.uidSpecified = true;
        }
        if (arguments.gidSpecified) {
            return;
        }
        arguments.gid = credentials.getGid();
        arguments.gidSpecified = true;
    }

    public static void applyDebuggerSystemProperty(Arguments arguments) {
        if (RoSystemProperties.DEBUGGABLE) {
            arguments.runtimeFlags |= 1;
        }
    }

    private static void applyInvokeWithSecurityPolicy(Arguments arguments, Credentials credentials) throws ZygoteSecurityException {
        int uid = credentials.getUid();
        if (arguments.invokeWith != null && uid != 0 && (arguments.runtimeFlags & 1) == 0) {
            throw new ZygoteSecurityException("Peer is permitted to specify anexplicit invoke-with wrapper command only for debuggableapplications.");
        }
    }

    public static void applyInvokeWithSystemProperty(Arguments arguments) {
        if (arguments.invokeWith != null || arguments.niceName == null) {
            return;
        }
        arguments.invokeWith = SystemProperties.get("wrap." + arguments.niceName);
        if (arguments.invokeWith == null || arguments.invokeWith.length() != 0) {
            return;
        }
        arguments.invokeWith = null;
    }

    private Runnable handleChildProc(Arguments arguments, FileDescriptor[] fileDescriptorArr, FileDescriptor fileDescriptor, boolean z) {
        closeSocket();
        if (fileDescriptorArr != null) {
            try {
                Os.dup2(fileDescriptorArr[0], OsConstants.STDIN_FILENO);
                Os.dup2(fileDescriptorArr[1], OsConstants.STDOUT_FILENO);
                Os.dup2(fileDescriptorArr[2], OsConstants.STDERR_FILENO);
                for (FileDescriptor fileDescriptor2 : fileDescriptorArr) {
                    IoUtils.closeQuietly(fileDescriptor2);
                }
            } catch (ErrnoException e) {
                Log.e(TAG, "Error reopening stdio", e);
            }
        }
        if (arguments.niceName != null) {
            Process.setArgV0(arguments.niceName);
        }
        Trace.traceEnd(64L);
        if (arguments.invokeWith != null) {
            WrapperInit.execApplication(arguments.invokeWith, arguments.niceName, arguments.targetSdkVersion, VMRuntime.getCurrentInstructionSet(), fileDescriptor, arguments.remainingArgs);
            throw new IllegalStateException("WrapperInit.execApplication unexpectedly returned");
        }
        if (!z) {
            return ZygoteInit.zygoteInit(arguments.targetSdkVersion, arguments.remainingArgs, null);
        }
        return ZygoteInit.childZygoteInit(arguments.targetSdkVersion, arguments.remainingArgs, null);
    }

    /* JADX WARN: Code duplicated, block: B:63:0x00c8  */
    /* JADX WARN: Code duplicated, block: B:65:0x00cb A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:68:0x00d4  */
    /* JADX WARN: Code duplicated, block: B:69:0x00eb  */
    /* JADX WARN: Code duplicated, block: B:71:0x0109 A[PHI: r7
  0x0109: PHI (r7v2 short) = (r7v1 short), (r7v4 short), (r7v4 short) binds: [B:70:0x0108, B:62:0x00c6, B:69:0x00eb] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Instruction removed from duplicated block: B:68:0x00d4, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:69:0x00eb, please report this as an issue */
    /* JADX WARN: Multi-variable type inference failed */
    private void handleParentProc(int i, FileDescriptor[] fileDescriptorArr, FileDescriptor fileDescriptor) {
        short s;
        boolean z;
        boolean z2;
        int i2;
        int i3;
        int parentPid;
        int i4 = i;
        if (i4 > 0) {
            setChildPgid(i);
        }
        short s2 = 0;
        if (fileDescriptorArr != null) {
            for (FileDescriptor fileDescriptor2 : fileDescriptorArr) {
                IoUtils.closeQuietly(fileDescriptor2);
            }
        }
        boolean z3 = true;
        try {
            if (fileDescriptor != null && i4 > 0) {
                try {
                    StructPollfd[] structPollfdArr = {new StructPollfd()};
                    int i5 = 4;
                    byte[] bArr = new byte[4];
                    long jNanoTime = System.nanoTime();
                    int i6 = 0;
                    int i7 = 30000;
                    while (true) {
                        if (i6 >= i5 || i7 <= 0) {
                            z2 = z3;
                            s = s2;
                            break;
                        }
                        try {
                            structPollfdArr[s2].fd = fileDescriptor;
                            structPollfdArr[s2].events = (short) OsConstants.POLLIN;
                            structPollfdArr[s2].revents = s2;
                            structPollfdArr[s2].userData = null;
                            int iPoll = Os.poll(structPollfdArr, i7);
                            try {
                                int iNanoTime = 30000 - ((int) ((System.nanoTime() - jNanoTime) / TimeUtils.NANOS_PER_MS));
                                if (iPoll > 0) {
                                    s = 0;
                                    try {
                                        if ((structPollfdArr[0].revents & OsConstants.POLLIN) == 0) {
                                            z2 = true;
                                            break;
                                        }
                                        z2 = true;
                                        try {
                                            int i8 = Os.read(fileDescriptor, bArr, i6, 1);
                                            if (i8 < 0) {
                                                throw new RuntimeException("Some error");
                                            }
                                            i6 += i8;
                                        } catch (Exception e) {
                                            e = e;
                                            i2 = -1;
                                            Log.w(TAG, "Error reading pid from wrapped process, child may have died", e);
                                            i3 = i2;
                                            if (i3 > 0) {
                                                parentPid = i3;
                                                while (parentPid > 0) {
                                                    parentPid = Process.getParentPid(parentPid);
                                                }
                                                if (parentPid > 0) {
                                                    Log.i(TAG, "Wrapped process has pid " + i3);
                                                    i4 = i3;
                                                    z = z2;
                                                } else {
                                                    Log.w(TAG, "Wrapped process reported a pid that is not a child of the process that we forked: childPid=" + i4 + " innerPid=" + i3);
                                                    z = s;
                                                }
                                            } else {
                                                z = s;
                                            }
                                            this.mSocketOutStream.writeInt(i4);
                                            this.mSocketOutStream.writeBoolean(z);
                                            return;
                                        }
                                    } catch (Exception e2) {
                                        e = e2;
                                        z2 = true;
                                        i2 = -1;
                                        Log.w(TAG, "Error reading pid from wrapped process, child may have died", e);
                                        i3 = i2;
                                        if (i3 > 0) {
                                            parentPid = i3;
                                            while (parentPid > 0) {
                                                parentPid = Process.getParentPid(parentPid);
                                            }
                                            if (parentPid > 0) {
                                                Log.i(TAG, "Wrapped process has pid " + i3);
                                                i4 = i3;
                                                z = z2;
                                            } else {
                                                Log.w(TAG, "Wrapped process reported a pid that is not a child of the process that we forked: childPid=" + i4 + " innerPid=" + i3);
                                                z = s;
                                            }
                                        } else {
                                            z = s;
                                        }
                                        this.mSocketOutStream.writeInt(i4);
                                        this.mSocketOutStream.writeBoolean(z);
                                        return;
                                    }
                                } else {
                                    s = 0;
                                    z2 = true;
                                    if (iPoll == 0) {
                                        Log.w(TAG, "Timed out waiting for child.");
                                    }
                                }
                                i7 = iNanoTime;
                                s2 = s;
                                z3 = z2;
                                i5 = 4;
                            } catch (Exception e3) {
                                e = e3;
                                s = 0;
                            }
                        } catch (Exception e4) {
                            e = e4;
                            z2 = z3;
                            s = s2;
                        }
                    }
                    i3 = i6 == 4 ? new DataInputStream(new ByteArrayInputStream(bArr)).readInt() : -1;
                    if (i3 == -1) {
                        try {
                            Log.w(TAG, "Error reading pid from wrapped process, child may have died");
                        } catch (Exception e5) {
                            e = e5;
                            i2 = i3;
                            Log.w(TAG, "Error reading pid from wrapped process, child may have died", e);
                            i3 = i2;
                        }
                    }
                } catch (Exception e6) {
                    e = e6;
                    z2 = true;
                    s = 0;
                    i2 = -1;
                }
                if (i3 > 0) {
                    parentPid = i3;
                    while (parentPid > 0 && parentPid != i4) {
                        parentPid = Process.getParentPid(parentPid);
                    }
                    if (parentPid > 0) {
                        Log.i(TAG, "Wrapped process has pid " + i3);
                        i4 = i3;
                        z = z2;
                    } else {
                        Log.w(TAG, "Wrapped process reported a pid that is not a child of the process that we forked: childPid=" + i4 + " innerPid=" + i3);
                    }
                }
                this.mSocketOutStream.writeInt(i4);
                this.mSocketOutStream.writeBoolean(z);
                return;
            }
            s = 0;
            this.mSocketOutStream.writeInt(i4);
            this.mSocketOutStream.writeBoolean(z);
            return;
        } catch (IOException e7) {
            throw new IllegalStateException("Error writing to command socket", e7);
        }
        z = s;
    }

    private void setChildPgid(int i) {
        try {
            Os.setpgid(i, Os.getpgid(this.peer.getPid()));
        } catch (ErrnoException unused) {
            Log.i(TAG, "Zygote: setpgid failed. This is normal if peer is not in our session");
        }
    }
}
