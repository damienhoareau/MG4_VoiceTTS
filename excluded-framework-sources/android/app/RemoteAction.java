package android.app;

import android.graphics.drawable.Icon;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public final class RemoteAction implements Parcelable {
    public static final Parcelable.Creator<RemoteAction> CREATOR = new Parcelable.Creator<RemoteAction>() { // from class: android.app.RemoteAction.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RemoteAction createFromParcel(Parcel parcel) {
            return new RemoteAction(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RemoteAction[] newArray(int i) {
            return new RemoteAction[i];
        }
    };
    private static final String TAG = "RemoteAction";
    private final PendingIntent mActionIntent;
    private final CharSequence mContentDescription;
    private boolean mEnabled;
    private final Icon mIcon;
    private boolean mShouldShowIcon;
    private final CharSequence mTitle;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    RemoteAction(Parcel parcel) {
        this.mIcon = Icon.CREATOR.createFromParcel(parcel);
        this.mTitle = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        this.mContentDescription = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        this.mActionIntent = PendingIntent.CREATOR.createFromParcel(parcel);
        this.mEnabled = parcel.readBoolean();
        this.mShouldShowIcon = parcel.readBoolean();
    }

    public RemoteAction(Icon icon, CharSequence charSequence, CharSequence charSequence2, PendingIntent pendingIntent) {
        if (icon == null || charSequence == null || charSequence2 == null || pendingIntent == null) {
            throw new IllegalArgumentException("Expected icon, title, content description and action callback");
        }
        this.mIcon = icon;
        this.mTitle = charSequence;
        this.mContentDescription = charSequence2;
        this.mActionIntent = pendingIntent;
        this.mEnabled = true;
        this.mShouldShowIcon = true;
    }

    public void setEnabled(boolean z) {
        this.mEnabled = z;
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public void setShouldShowIcon(boolean z) {
        this.mShouldShowIcon = z;
    }

    public boolean shouldShowIcon() {
        return this.mShouldShowIcon;
    }

    public Icon getIcon() {
        return this.mIcon;
    }

    public CharSequence getTitle() {
        return this.mTitle;
    }

    public CharSequence getContentDescription() {
        return this.mContentDescription;
    }

    public PendingIntent getActionIntent() {
        return this.mActionIntent;
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public RemoteAction m14clone() {
        RemoteAction remoteAction = new RemoteAction(this.mIcon, this.mTitle, this.mContentDescription, this.mActionIntent);
        remoteAction.setEnabled(this.mEnabled);
        remoteAction.setShouldShowIcon(this.mShouldShowIcon);
        return remoteAction;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        this.mIcon.writeToParcel(parcel, 0);
        TextUtils.writeToParcel(this.mTitle, parcel, i);
        TextUtils.writeToParcel(this.mContentDescription, parcel, i);
        this.mActionIntent.writeToParcel(parcel, i);
        parcel.writeBoolean(this.mEnabled);
        parcel.writeBoolean(this.mShouldShowIcon);
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.print(str);
        printWriter.print("title=" + ((Object) this.mTitle));
        printWriter.print(" enabled=" + this.mEnabled);
        printWriter.print(" contentDescription=" + ((Object) this.mContentDescription));
        printWriter.print(" icon=" + this.mIcon);
        printWriter.print(" action=" + this.mActionIntent.getIntent());
        printWriter.print(" shouldShowIcon=" + this.mShouldShowIcon);
        printWriter.println();
    }
}
