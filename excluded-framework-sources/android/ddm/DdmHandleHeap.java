package android.ddm;

import android.os.Debug;
import android.util.Log;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.harmony.dalvik.ddmc.Chunk;
import org.apache.harmony.dalvik.ddmc.ChunkHandler;
import org.apache.harmony.dalvik.ddmc.DdmServer;
import org.apache.harmony.dalvik.ddmc.DdmVmInternal;

/* JADX INFO: loaded from: classes.dex */
public class DdmHandleHeap extends ChunkHandler {
    public static final int CHUNK_HPIF = type("HPIF");
    public static final int CHUNK_HPSG = type("HPSG");
    public static final int CHUNK_HPDU = type("HPDU");
    public static final int CHUNK_HPDS = type("HPDS");
    public static final int CHUNK_NHSG = type("NHSG");
    public static final int CHUNK_HPGC = type("HPGC");
    public static final int CHUNK_REAE = type("REAE");
    public static final int CHUNK_REAQ = type("REAQ");
    public static final int CHUNK_REAL = type("REAL");
    private static DdmHandleHeap mInstance = new DdmHandleHeap();

    public void connected() {
    }

    public void disconnected() {
    }

    private DdmHandleHeap() {
    }

    public static void register() {
        DdmServer.registerHandler(CHUNK_HPIF, mInstance);
        DdmServer.registerHandler(CHUNK_HPSG, mInstance);
        DdmServer.registerHandler(CHUNK_HPDU, mInstance);
        DdmServer.registerHandler(CHUNK_HPDS, mInstance);
        DdmServer.registerHandler(CHUNK_NHSG, mInstance);
        DdmServer.registerHandler(CHUNK_HPGC, mInstance);
        DdmServer.registerHandler(CHUNK_REAE, mInstance);
        DdmServer.registerHandler(CHUNK_REAQ, mInstance);
        DdmServer.registerHandler(CHUNK_REAL, mInstance);
    }

    public Chunk handleChunk(Chunk chunk) {
        int i = chunk.type;
        if (i == CHUNK_HPIF) {
            return handleHPIF(chunk);
        }
        if (i == CHUNK_HPSG) {
            return handleHPSGNHSG(chunk, false);
        }
        if (i == CHUNK_HPDU) {
            return handleHPDU(chunk);
        }
        if (i == CHUNK_HPDS) {
            return handleHPDS(chunk);
        }
        if (i == CHUNK_NHSG) {
            return handleHPSGNHSG(chunk, true);
        }
        if (i == CHUNK_HPGC) {
            return handleHPGC(chunk);
        }
        if (i == CHUNK_REAE) {
            return handleREAE(chunk);
        }
        if (i == CHUNK_REAQ) {
            return handleREAQ(chunk);
        }
        if (i == CHUNK_REAL) {
            return handleREAL(chunk);
        }
        throw new RuntimeException("Unknown packet " + ChunkHandler.name(i));
    }

    private Chunk handleHPIF(Chunk chunk) {
        if (DdmVmInternal.heapInfoNotify(wrapChunk(chunk).get())) {
            return null;
        }
        return createFailChunk(1, "Unsupported HPIF what");
    }

    private Chunk handleHPSGNHSG(Chunk chunk, boolean z) {
        ByteBuffer byteBufferWrapChunk = wrapChunk(chunk);
        if (DdmVmInternal.heapSegmentNotify(byteBufferWrapChunk.get(), byteBufferWrapChunk.get(), z)) {
            return null;
        }
        return createFailChunk(1, "Unsupported HPSG what/when");
    }

    private Chunk handleHPDU(Chunk chunk) {
        ByteBuffer byteBufferWrapChunk = wrapChunk(chunk);
        byte b = -1;
        try {
            Debug.dumpHprofData(getString(byteBufferWrapChunk, byteBufferWrapChunk.getInt()));
            b = 0;
        } catch (IOException | RuntimeException unused) {
        } catch (UnsupportedOperationException unused2) {
            Log.w("ddm-heap", "hprof dumps not supported in this VM");
        }
        return new Chunk(CHUNK_HPDU, new byte[]{b}, 0, 1);
    }

    private Chunk handleHPDS(Chunk chunk) {
        String str;
        wrapChunk(chunk);
        try {
            Debug.dumpHprofDataDdms();
            str = null;
        } catch (UnsupportedOperationException unused) {
            str = "hprof dumps not supported in this VM";
        } catch (RuntimeException e) {
            str = "Exception: " + e.getMessage();
        }
        if (str == null) {
            return null;
        }
        Log.w("ddm-heap", str);
        return createFailChunk(1, str);
    }

    private Chunk handleHPGC(Chunk chunk) {
        Runtime.getRuntime().gc();
        return null;
    }

    private Chunk handleREAE(Chunk chunk) {
        DdmVmInternal.enableRecentAllocations(wrapChunk(chunk).get() != 0);
        return null;
    }

    private Chunk handleREAQ(Chunk chunk) {
        return new Chunk(CHUNK_REAQ, new byte[]{DdmVmInternal.getRecentAllocationStatus()}, 0, 1);
    }

    private Chunk handleREAL(Chunk chunk) {
        byte[] recentAllocations = DdmVmInternal.getRecentAllocations();
        return new Chunk(CHUNK_REAL, recentAllocations, 0, recentAllocations.length);
    }
}
