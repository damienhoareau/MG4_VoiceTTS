package android.net;

/* JADX INFO: loaded from: classes.dex */
public class ParseException extends RuntimeException {
    public String response;

    ParseException(String str) {
        this.response = str;
    }
}
