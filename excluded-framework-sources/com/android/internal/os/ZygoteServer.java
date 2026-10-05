package com.android.internal.os;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructPollfd;
import android.util.Log;
import android.util.Slog;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes3.dex */
class ZygoteServer {
    private static final String ANDROID_SOCKET_PREFIX = "ANDROID_SOCKET_";
    public static final String TAG = "ZygoteServer";
    private boolean mCloseSocketFd;
    private boolean mIsForkChild;
    private LocalServerSocket mServerSocket;

    ZygoteServer() {
    }

    void setForkChild() {
        this.mIsForkChild = true;
    }

    void registerServerSocketFromEnv(String str) {
        if (this.mServerSocket == null) {
            String str2 = ANDROID_SOCKET_PREFIX + str;
            try {
                int i = Integer.parseInt(System.getenv(str2));
                try {
                    FileDescriptor fileDescriptor = new FileDescriptor();
                    fileDescriptor.setInt$(i);
                    this.mServerSocket = new LocalServerSocket(fileDescriptor);
                    this.mCloseSocketFd = true;
                } catch (IOException e) {
                    throw new RuntimeException("Error binding to local socket '" + i + "'", e);
                }
            } catch (RuntimeException e2) {
                throw new RuntimeException(str2 + " unset or invalid", e2);
            }
        }
    }

    void registerServerSocketAtAbstractName(String str) {
        if (this.mServerSocket == null) {
            try {
                this.mServerSocket = new LocalServerSocket(str);
                this.mCloseSocketFd = false;
            } catch (IOException e) {
                throw new RuntimeException("Error binding to abstract socket '" + str + "'", e);
            }
        }
    }

    private ZygoteConnection acceptCommandPeer(String str) {
        try {
            return createNewConnection(this.mServerSocket.accept(), str);
        } catch (IOException e) {
            throw new RuntimeException("IOException during accept()", e);
        }
    }

    protected ZygoteConnection createNewConnection(LocalSocket localSocket, String str) throws IOException {
        return new ZygoteConnection(localSocket, str);
    }

    void closeServerSocket() {
        try {
            if (this.mServerSocket != null) {
                FileDescriptor fileDescriptor = this.mServerSocket.getFileDescriptor();
                this.mServerSocket.close();
                if (fileDescriptor != null && this.mCloseSocketFd) {
                    Os.close(fileDescriptor);
                }
            }
        } catch (ErrnoException e) {
            Log.e(TAG, "Zygote:  error closing descriptor", e);
        } catch (IOException e2) {
            Log.e(TAG, "Zygote:  error closing sockets", e2);
        }
        this.mServerSocket = null;
    }

    FileDescriptor getServerSocketFileDescriptor() {
        return this.mServerSocket.getFileDescriptor();
    }

    Runnable runSelectLoop(String str) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        arrayList.add(this.mServerSocket.getFileDescriptor());
        arrayList2.add(null);
        while (true) {
            int size = arrayList.size();
            StructPollfd[] structPollfdArr = new StructPollfd[size];
            for (int i = 0; i < size; i++) {
                structPollfdArr[i] = new StructPollfd();
                structPollfdArr[i].fd = (FileDescriptor) arrayList.get(i);
                structPollfdArr[i].events = (short) OsConstants.POLLIN;
            }
            try {
                Os.poll(structPollfdArr, -1);
                for (int i2 = size - 1; i2 >= 0; i2--) {
                    if ((structPollfdArr[i2].revents & OsConstants.POLLIN) != 0) {
                        if (i2 == 0) {
                            ZygoteConnection zygoteConnectionAcceptCommandPeer = acceptCommandPeer(str);
                            arrayList2.add(zygoteConnectionAcceptCommandPeer);
                            arrayList.add(zygoteConnectionAcceptCommandPeer.getFileDesciptor());
                        } else {
                            try {
                                try {
                                    ZygoteConnection zygoteConnection = (ZygoteConnection) arrayList2.get(i2);
                                    Runnable runnableProcessOneCommand = zygoteConnection.processOneCommand(this);
                                    if (this.mIsForkChild) {
                                        if (runnableProcessOneCommand == null) {
                                            throw new IllegalStateException("command == null");
                                        }
                                        this.mIsForkChild = false;
                                        return runnableProcessOneCommand;
                                    }
                                    if (runnableProcessOneCommand != null) {
                                        throw new IllegalStateException("command != null");
                                    }
                                    if (zygoteConnection.isClosedByPeer()) {
                                        zygoteConnection.closeSocket();
                                        arrayList2.remove(i2);
                                        arrayList.remove(i2);
                                    }
                                    this.mIsForkChild = false;
                                } catch (Exception e) {
                                    if (!this.mIsForkChild) {
                                        Slog.e(TAG, "Exception executing zygote command: ", e);
                                        ((ZygoteConnection) arrayList2.remove(i2)).closeSocket();
                                        arrayList.remove(i2);
                                    } else {
                                        Log.e(TAG, "Caught post-fork exception in child process.", e);
                                        throw e;
                                    }
                                }
                            } catch (Throwable th) {
                                this.mIsForkChild = false;
                                throw th;
                            }
                        }
                    }
                }
            } catch (ErrnoException e2) {
                throw new RuntimeException("poll failed", e2);
            }
        }
    }
}
