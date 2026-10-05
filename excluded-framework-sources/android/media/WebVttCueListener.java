package android.media;

/* JADX INFO: compiled from: WebVttRenderer.java */
/* JADX INFO: loaded from: classes.dex */
interface WebVttCueListener {
    void onCueParsed(TextTrackCue textTrackCue);

    void onRegionParsed(TextTrackRegion textTrackRegion);
}
