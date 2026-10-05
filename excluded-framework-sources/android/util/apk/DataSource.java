package android.util.apk;

import java.io.IOException;
import java.security.DigestException;

/* JADX INFO: loaded from: classes2.dex */
interface DataSource {
    void feedIntoDataDigester(DataDigester dataDigester, long j, int i) throws DigestException, IOException;

    long size();
}
