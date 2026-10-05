package android.net;

import android.os.SystemClock;
import android.util.Log;
import android.util.proto.ProtoOutputStream;
import com.android.internal.telephony.GsmAlphabet;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class SntpClient {
    private static final boolean DBG = true;
    private static final int NTP_LEAP_NOSYNC = 3;
    private static final int NTP_MODE_BROADCAST = 5;
    private static final int NTP_MODE_CLIENT = 3;
    private static final int NTP_MODE_SERVER = 4;
    private static final int NTP_PACKET_SIZE = 48;
    private static final int NTP_PORT = 123;
    private static final int NTP_STRATUM_DEATH = 0;
    private static final int NTP_STRATUM_MAX = 15;
    private static final int NTP_VERSION = 3;
    private static final long OFFSET_1900_TO_1970 = 2208988800L;
    private static final int ORIGINATE_TIME_OFFSET = 24;
    private static final int RECEIVE_TIME_OFFSET = 32;
    private static final int REFERENCE_TIME_OFFSET = 16;
    private static final String TAG = "SntpClient";
    private static final int TRANSMIT_TIME_OFFSET = 40;
    private long mNtpTime;
    private long mNtpTimeReference;
    private long mRoundTripTime;

    private static class InvalidServerReplyException extends Exception {
        public InvalidServerReplyException(String str) {
            super(str);
        }
    }

    public boolean requestTime(String str, int i, Network network) {
        network.setPrivateDnsBypass(true);
        try {
            return requestTime(network.getByName(str), 123, i, network);
        } catch (Exception e) {
            EventLogTags.writeNtpFailure(str, e.toString());
            Log.d(TAG, "request time failed: " + e);
            return false;
        }
    }

    /* JADX WARN: Code duplicated, block: B:31:0x00f5  */
    public boolean requestTime(InetAddress inetAddress, int i, int i2, Network network) throws Throwable {
        DatagramSocket datagramSocket;
        int andSetThreadStatsTag = TrafficStats.getAndSetThreadStatsTag(TrafficStats.TAG_SYSTEM_NTP);
        DatagramSocket datagramSocket2 = null;
        try {
            try {
                DatagramSocket datagramSocket3 = new DatagramSocket();
                try {
                    try {
                        network.bindSocket(datagramSocket3);
                        datagramSocket3.setSoTimeout(i2);
                        byte[] bArr = new byte[48];
                        try {
                            DatagramPacket datagramPacket = new DatagramPacket(bArr, 48, inetAddress, i);
                            bArr[0] = GsmAlphabet.GSM_EXTENDED_ESCAPE;
                            long jCurrentTimeMillis = System.currentTimeMillis();
                            long jElapsedRealtime = SystemClock.elapsedRealtime();
                            writeTimeStamp(bArr, 40, jCurrentTimeMillis);
                            datagramSocket3.send(datagramPacket);
                            datagramSocket3.receive(new DatagramPacket(bArr, 48));
                            long jElapsedRealtime2 = SystemClock.elapsedRealtime();
                            long j = jElapsedRealtime2 - jElapsedRealtime;
                            long j2 = jCurrentTimeMillis + j;
                            byte b = (byte) ((bArr[0] >> 6) & 3);
                            byte b2 = (byte) (bArr[0] & 7);
                            int i3 = bArr[1] & 255;
                            long timeStamp = readTimeStamp(bArr, 24);
                            long timeStamp2 = readTimeStamp(bArr, 32);
                            datagramSocket = datagramSocket3;
                            try {
                                long timeStamp3 = readTimeStamp(bArr, 40);
                                checkValidServerReply(b, b2, i3, timeStamp3);
                                long j3 = j - (timeStamp3 - timeStamp2);
                                long j4 = ((timeStamp2 - timeStamp) + (timeStamp3 - j2)) / 2;
                                EventLogTags.writeNtpSuccess(inetAddress.toString(), j3, j4);
                                Log.d(TAG, "round trip: " + j3 + "ms, clock offset: " + j4 + "ms");
                                this.mNtpTime = j2 + j4;
                                this.mNtpTimeReference = jElapsedRealtime2;
                                this.mRoundTripTime = j3;
                                datagramSocket.close();
                                TrafficStats.setThreadStatsTag(andSetThreadStatsTag);
                                return true;
                            } catch (Exception e) {
                                e = e;
                                datagramSocket2 = datagramSocket;
                                EventLogTags.writeNtpFailure(inetAddress.toString(), e.toString());
                                Log.d(TAG, "request time failed: " + e);
                                if (datagramSocket2 != null) {
                                    datagramSocket2.close();
                                }
                                TrafficStats.setThreadStatsTag(andSetThreadStatsTag);
                                return false;
                            } catch (Throwable th) {
                                th = th;
                                datagramSocket2 = datagramSocket;
                                if (datagramSocket2 != null) {
                                    datagramSocket2.close();
                                }
                                TrafficStats.setThreadStatsTag(andSetThreadStatsTag);
                                throw th;
                            }
                        } catch (Exception e2) {
                            e = e2;
                            datagramSocket = datagramSocket3;
                            datagramSocket2 = datagramSocket;
                            EventLogTags.writeNtpFailure(inetAddress.toString(), e.toString());
                            Log.d(TAG, "request time failed: " + e);
                            if (datagramSocket2 != null) {
                                datagramSocket2.close();
                            }
                            TrafficStats.setThreadStatsTag(andSetThreadStatsTag);
                            return false;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        datagramSocket = datagramSocket3;
                    }
                } catch (Exception e3) {
                    e = e3;
                }
            } catch (Exception e4) {
                e = e4;
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    @Deprecated
    public boolean requestTime(String str, int i) {
        Log.w(TAG, "Shame on you for calling the hidden API requestTime()!");
        return false;
    }

    public long getNtpTime() {
        return this.mNtpTime;
    }

    public long getNtpTimeReference() {
        return this.mNtpTimeReference;
    }

    public long getRoundTripTime() {
        return this.mRoundTripTime;
    }

    private static void checkValidServerReply(byte b, byte b2, int i, long j) throws InvalidServerReplyException {
        if (b == 3) {
            throw new InvalidServerReplyException("unsynchronized server");
        }
        if (b2 != 4 && b2 != 5) {
            throw new InvalidServerReplyException("untrusted mode: " + ((int) b2));
        }
        if (i != 0 && i <= 15) {
            if (j == 0) {
                throw new InvalidServerReplyException("zero transmitTime");
            }
        } else {
            throw new InvalidServerReplyException("untrusted stratum: " + i);
        }
    }

    private long read32(byte[] bArr, int i) {
        int i2 = bArr[i];
        int i3 = bArr[i + 1];
        int i4 = bArr[i + 2];
        int i5 = bArr[i + 3];
        if ((i2 & 128) == 128) {
            i2 = (i2 & 127) + 128;
        }
        if ((i3 & 128) == 128) {
            i3 = (i3 & 127) + 128;
        }
        if ((i4 & 128) == 128) {
            i4 = (i4 & 127) + 128;
        }
        if ((i5 & 128) == 128) {
            i5 = (i5 & 127) + 128;
        }
        return (((long) i2) << 24) + (((long) i3) << 16) + (((long) i4) << 8) + ((long) i5);
    }

    private long readTimeStamp(byte[] bArr, int i) {
        long j = read32(bArr, i);
        long j2 = read32(bArr, i + 4);
        if (j == 0 && j2 == 0) {
            return 0L;
        }
        return ((j - OFFSET_1900_TO_1970) * 1000) + ((j2 * 1000) / ProtoOutputStream.FIELD_TYPE_DOUBLE);
    }

    private void writeTimeStamp(byte[] bArr, int i, long j) {
        if (j == 0) {
            Arrays.fill(bArr, i, i + 8, (byte) 0);
            return;
        }
        long j2 = j / 1000;
        long j3 = j - (j2 * 1000);
        long j4 = j2 + OFFSET_1900_TO_1970;
        int i2 = i + 1;
        bArr[i] = (byte) (j4 >> 24);
        int i3 = i2 + 1;
        bArr[i2] = (byte) (j4 >> 16);
        int i4 = i3 + 1;
        bArr[i3] = (byte) (j4 >> 8);
        int i5 = i4 + 1;
        bArr[i4] = (byte) (j4 >> 0);
        long j5 = (j3 * ProtoOutputStream.FIELD_TYPE_DOUBLE) / 1000;
        int i6 = i5 + 1;
        bArr[i5] = (byte) (j5 >> 24);
        int i7 = i6 + 1;
        bArr[i6] = (byte) (j5 >> 16);
        bArr[i7] = (byte) (j5 >> 8);
        bArr[i7 + 1] = (byte) (Math.random() * 255.0d);
    }
}
