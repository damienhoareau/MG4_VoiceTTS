package android.ddm;

import org.apache.harmony.dalvik.ddmc.Chunk;
import org.apache.harmony.dalvik.ddmc.ChunkHandler;
import org.apache.harmony.dalvik.ddmc.DdmServer;

/* JADX INFO: loaded from: classes.dex */
public class DdmHandleExit extends ChunkHandler {
    public static final int CHUNK_EXIT = type("EXIT");
    private static DdmHandleExit mInstance = new DdmHandleExit();

    public void connected() {
    }

    public void disconnected() {
    }

    private DdmHandleExit() {
    }

    public static void register() {
        DdmServer.registerHandler(CHUNK_EXIT, mInstance);
    }

    public Chunk handleChunk(Chunk chunk) {
        Runtime.getRuntime().halt(wrapChunk(chunk).getInt());
        return null;
    }
}
