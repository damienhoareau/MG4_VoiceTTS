package android.drm;

import java.util.HashMap;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class DrmInfoRequest {
    public static final String ACCOUNT_ID = "account_id";
    public static final String SUBSCRIPTION_ID = "subscription_id";
    public static final int TYPE_REGISTRATION_INFO = 1;
    public static final int TYPE_RIGHTS_ACQUISITION_INFO = 3;
    public static final int TYPE_RIGHTS_ACQUISITION_PROGRESS_INFO = 4;
    public static final int TYPE_UNREGISTRATION_INFO = 2;
    private final int mInfoType;
    private final String mMimeType;
    private final HashMap<String, Object> mRequestInformation = new HashMap<>();

    static boolean isValidType(int i) {
        return i == 1 || i == 2 || i == 3 || i == 4;
    }

    public DrmInfoRequest(int i, String str) {
        this.mInfoType = i;
        this.mMimeType = str;
        if (isValid()) {
            return;
        }
        throw new IllegalArgumentException("infoType: " + i + ",mimeType: " + str);
    }

    public String getMimeType() {
        return this.mMimeType;
    }

    public int getInfoType() {
        return this.mInfoType;
    }

    public void put(String str, Object obj) {
        this.mRequestInformation.put(str, obj);
    }

    public Object get(String str) {
        return this.mRequestInformation.get(str);
    }

    public Iterator<String> keyIterator() {
        return this.mRequestInformation.keySet().iterator();
    }

    public Iterator<Object> iterator() {
        return this.mRequestInformation.values().iterator();
    }

    boolean isValid() {
        String str = this.mMimeType;
        return (str == null || str.equals("") || this.mRequestInformation == null || !isValidType(this.mInfoType)) ? false : true;
    }
}
