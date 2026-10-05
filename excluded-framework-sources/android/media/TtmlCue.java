package android.media;

/* JADX INFO: compiled from: TtmlRenderer.java */
/* JADX INFO: loaded from: classes.dex */
class TtmlCue extends SubtitleTrack.Cue {
    public String mText;
    public String mTtmlFragment;

    public TtmlCue(long j, long j2, String str, String str2) {
        this.mStartTimeMs = j;
        this.mEndTimeMs = j2;
        this.mText = str;
        this.mTtmlFragment = str2;
    }
}
