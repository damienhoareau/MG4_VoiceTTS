package android.view;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
public class RemoteAnimationAdapter implements Parcelable {
    public static final Parcelable.Creator<RemoteAnimationAdapter> CREATOR = new Parcelable.Creator<RemoteAnimationAdapter>() { // from class: android.view.RemoteAnimationAdapter.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RemoteAnimationAdapter createFromParcel(Parcel parcel) {
            return new RemoteAnimationAdapter(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RemoteAnimationAdapter[] newArray(int i) {
            return new RemoteAnimationAdapter[i];
        }
    };
    private int mCallingPid;
    private final long mDuration;
    private final IRemoteAnimationRunner mRunner;
    private final long mStatusBarTransitionDelay;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public RemoteAnimationAdapter(IRemoteAnimationRunner iRemoteAnimationRunner, long j, long j2) {
        this.mRunner = iRemoteAnimationRunner;
        this.mDuration = j;
        this.mStatusBarTransitionDelay = j2;
    }

    public RemoteAnimationAdapter(Parcel parcel) {
        this.mRunner = IRemoteAnimationRunner.Stub.asInterface(parcel.readStrongBinder());
        this.mDuration = parcel.readLong();
        this.mStatusBarTransitionDelay = parcel.readLong();
    }

    public IRemoteAnimationRunner getRunner() {
        return this.mRunner;
    }

    public long getDuration() {
        return this.mDuration;
    }

    public long getStatusBarTransitionDelay() {
        return this.mStatusBarTransitionDelay;
    }

    public void setCallingPid(int i) {
        this.mCallingPid = i;
    }

    public int getCallingPid() {
        return this.mCallingPid;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStrongInterface(this.mRunner);
        parcel.writeLong(this.mDuration);
        parcel.writeLong(this.mStatusBarTransitionDelay);
    }
}
