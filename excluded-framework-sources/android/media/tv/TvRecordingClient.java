package android.media.tv;

import android.annotation.SystemApi;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import java.util.ArrayDeque;
import java.util.Queue;

/* JADX INFO: loaded from: classes.dex */
public class TvRecordingClient {
    private static final boolean DEBUG = false;
    private static final String TAG = "TvRecordingClient";
    private final RecordingCallback mCallback;
    private final Handler mHandler;
    private boolean mIsRecordingStarted;
    private boolean mIsTuned;
    private final Queue<Pair<String, Bundle>> mPendingAppPrivateCommands = new ArrayDeque();
    private TvInputManager.Session mSession;
    private MySessionCallback mSessionCallback;
    private final TvInputManager mTvInputManager;

    public static abstract class RecordingCallback {
        public void onConnectionFailed(String str) {
        }

        public void onDisconnected(String str) {
        }

        public void onError(int i) {
        }

        @SystemApi
        public void onEvent(String str, String str2, Bundle bundle) {
        }

        public void onRecordingStopped(Uri uri) {
        }

        public void onTuned(Uri uri) {
        }
    }

    public TvRecordingClient(Context context, String str, RecordingCallback recordingCallback, Handler handler) {
        this.mCallback = recordingCallback;
        this.mHandler = handler == null ? new Handler(Looper.getMainLooper()) : handler;
        this.mTvInputManager = (TvInputManager) context.getSystemService(Context.TV_INPUT_SERVICE);
    }

    public void tune(String str, Uri uri) {
        tune(str, uri, null);
    }

    public void tune(String str, Uri uri, Bundle bundle) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("inputId cannot be null or an empty string");
        }
        if (this.mIsRecordingStarted) {
            throw new IllegalStateException("tune failed - recording already started");
        }
        MySessionCallback mySessionCallback = this.mSessionCallback;
        if (mySessionCallback != null && TextUtils.equals(mySessionCallback.mInputId, str)) {
            TvInputManager.Session session = this.mSession;
            if (session != null) {
                session.tune(uri, bundle);
                return;
            } else {
                this.mSessionCallback.mChannelUri = uri;
                this.mSessionCallback.mConnectionParams = bundle;
                return;
            }
        }
        resetInternal();
        MySessionCallback mySessionCallback2 = new MySessionCallback(str, uri, bundle);
        this.mSessionCallback = mySessionCallback2;
        TvInputManager tvInputManager = this.mTvInputManager;
        if (tvInputManager != null) {
            tvInputManager.createRecordingSession(str, mySessionCallback2, this.mHandler);
        }
    }

    public void release() {
        resetInternal();
    }

    private void resetInternal() {
        this.mSessionCallback = null;
        this.mPendingAppPrivateCommands.clear();
        TvInputManager.Session session = this.mSession;
        if (session != null) {
            session.release();
            this.mSession = null;
        }
    }

    public void startRecording(Uri uri) {
        if (!this.mIsTuned) {
            throw new IllegalStateException("startRecording failed - not yet tuned");
        }
        TvInputManager.Session session = this.mSession;
        if (session != null) {
            session.startRecording(uri);
            this.mIsRecordingStarted = true;
        }
    }

    public void stopRecording() {
        if (!this.mIsRecordingStarted) {
            Log.w(TAG, "stopRecording failed - recording not yet started");
        }
        TvInputManager.Session session = this.mSession;
        if (session != null) {
            session.stopRecording();
        }
    }

    public void sendAppPrivateCommand(String str, Bundle bundle) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("action cannot be null or an empty string");
        }
        TvInputManager.Session session = this.mSession;
        if (session != null) {
            session.sendAppPrivateCommand(str, bundle);
            return;
        }
        Log.w(TAG, "sendAppPrivateCommand - session not yet created (action \"" + str + "\" pending)");
        this.mPendingAppPrivateCommands.add(Pair.create(str, bundle));
    }

    private class MySessionCallback extends TvInputManager.SessionCallback {
        Uri mChannelUri;
        Bundle mConnectionParams;
        final String mInputId;

        MySessionCallback(String str, Uri uri, Bundle bundle) {
            this.mInputId = str;
            this.mChannelUri = uri;
            this.mConnectionParams = bundle;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.media.tv.TvInputManager.SessionCallback
        public void onSessionCreated(TvInputManager.Session session) {
            if (this == TvRecordingClient.this.mSessionCallback) {
                TvRecordingClient.this.mSession = session;
                if (session != null) {
                    for (Pair pair : TvRecordingClient.this.mPendingAppPrivateCommands) {
                        TvRecordingClient.this.mSession.sendAppPrivateCommand((String) pair.first, (Bundle) pair.second);
                    }
                    TvRecordingClient.this.mPendingAppPrivateCommands.clear();
                    TvRecordingClient.this.mSession.tune(this.mChannelUri, this.mConnectionParams);
                    return;
                }
                TvRecordingClient.this.mSessionCallback = null;
                if (TvRecordingClient.this.mCallback != null) {
                    TvRecordingClient.this.mCallback.onConnectionFailed(this.mInputId);
                    return;
                }
                return;
            }
            Log.w(TvRecordingClient.TAG, "onSessionCreated - session already created");
            if (session != null) {
                session.release();
            }
        }

        @Override // android.media.tv.TvInputManager.SessionCallback
        void onTuned(TvInputManager.Session session, Uri uri) {
            if (this == TvRecordingClient.this.mSessionCallback) {
                TvRecordingClient.this.mIsTuned = true;
                TvRecordingClient.this.mCallback.onTuned(uri);
            } else {
                Log.w(TvRecordingClient.TAG, "onTuned - session not created");
            }
        }

        @Override // android.media.tv.TvInputManager.SessionCallback
        public void onSessionReleased(TvInputManager.Session session) {
            if (this == TvRecordingClient.this.mSessionCallback) {
                TvRecordingClient.this.mIsTuned = false;
                TvRecordingClient.this.mIsRecordingStarted = false;
                TvRecordingClient.this.mSessionCallback = null;
                TvRecordingClient.this.mSession = null;
                if (TvRecordingClient.this.mCallback != null) {
                    TvRecordingClient.this.mCallback.onDisconnected(this.mInputId);
                    return;
                }
                return;
            }
            Log.w(TvRecordingClient.TAG, "onSessionReleased - session not created");
        }

        @Override // android.media.tv.TvInputManager.SessionCallback
        public void onRecordingStopped(TvInputManager.Session session, Uri uri) {
            if (this == TvRecordingClient.this.mSessionCallback) {
                TvRecordingClient.this.mIsRecordingStarted = false;
                TvRecordingClient.this.mCallback.onRecordingStopped(uri);
            } else {
                Log.w(TvRecordingClient.TAG, "onRecordingStopped - session not created");
            }
        }

        @Override // android.media.tv.TvInputManager.SessionCallback
        public void onError(TvInputManager.Session session, int i) {
            if (this == TvRecordingClient.this.mSessionCallback) {
                TvRecordingClient.this.mCallback.onError(i);
            } else {
                Log.w(TvRecordingClient.TAG, "onError - session not created");
            }
        }

        @Override // android.media.tv.TvInputManager.SessionCallback
        public void onSessionEvent(TvInputManager.Session session, String str, Bundle bundle) {
            if (this == TvRecordingClient.this.mSessionCallback) {
                if (TvRecordingClient.this.mCallback != null) {
                    TvRecordingClient.this.mCallback.onEvent(this.mInputId, str, bundle);
                    return;
                }
                return;
            }
            Log.w(TvRecordingClient.TAG, "onSessionEvent - session not created");
        }
    }
}
