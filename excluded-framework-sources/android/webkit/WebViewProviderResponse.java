package android.webkit;

import android.content.pm.PackageInfo;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
public final class WebViewProviderResponse implements Parcelable {
    public static final Parcelable.Creator<WebViewProviderResponse> CREATOR = new Parcelable.Creator<WebViewProviderResponse>() { // from class: android.webkit.WebViewProviderResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WebViewProviderResponse createFromParcel(Parcel parcel) {
            return new WebViewProviderResponse(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WebViewProviderResponse[] newArray(int i) {
            return new WebViewProviderResponse[i];
        }
    };
    public final PackageInfo packageInfo;
    public final int status;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public WebViewProviderResponse(PackageInfo packageInfo, int i) {
        this.packageInfo = packageInfo;
        this.status = i;
    }

    private WebViewProviderResponse(Parcel parcel) {
        this.packageInfo = (PackageInfo) parcel.readTypedObject(PackageInfo.CREATOR);
        this.status = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedObject(this.packageInfo, i);
        parcel.writeInt(this.status);
    }
}
