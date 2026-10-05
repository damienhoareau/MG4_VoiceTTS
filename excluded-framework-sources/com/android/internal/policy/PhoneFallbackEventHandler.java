package com.android.internal.policy;

import android.app.KeyguardManager;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.UserHandle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.FallbackEventHandler;
import android.view.KeyEvent;
import android.view.View;

/* JADX INFO: loaded from: classes3.dex */
public class PhoneFallbackEventHandler implements FallbackEventHandler {
    private static final boolean DEBUG = false;
    private static String TAG = "PhoneFallbackEventHandler";
    AudioManager mAudioManager;
    Context mContext;
    KeyguardManager mKeyguardManager;
    MediaSessionManager mMediaSessionManager;
    SearchManager mSearchManager;
    TelephonyManager mTelephonyManager;
    View mView;

    public PhoneFallbackEventHandler(Context context) {
        this.mContext = context;
    }

    @Override // android.view.FallbackEventHandler
    public void setView(View view) {
        this.mView = view;
    }

    @Override // android.view.FallbackEventHandler
    public void preDispatchKeyEvent(KeyEvent keyEvent) {
        getAudioManager().preDispatchKeyEvent(keyEvent, Integer.MIN_VALUE);
    }

    @Override // android.view.FallbackEventHandler
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        int action = keyEvent.getAction();
        int keyCode = keyEvent.getKeyCode();
        if (action == 0) {
            return onKeyDown(keyCode, keyEvent);
        }
        return onKeyUp(keyCode, keyEvent);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:42:0x0099  */
    /* JADX WARN: Code duplicated, block: B:44:0x00a3 A[RETURN] */
    boolean onKeyDown(int i, KeyEvent keyEvent) {
        KeyEvent.DispatcherState keyDispatcherState = this.mView.getKeyDispatcherState();
        if (i == 5) {
            if (keyEvent.getRepeatCount() == 0) {
                keyDispatcherState.startTracking(keyEvent, this);
            } else if (keyEvent.isLongPress() && keyDispatcherState.isTracking(keyEvent)) {
                keyDispatcherState.performedLongPress(keyEvent);
                if (isUserSetupComplete()) {
                    this.mView.performHapticFeedback(0);
                    Intent intent = new Intent(Intent.ACTION_VOICE_COMMAND);
                    intent.setFlags(268435456);
                    try {
                        sendCloseSystemWindows();
                        this.mContext.startActivity(intent);
                    } catch (ActivityNotFoundException unused) {
                        startCallActivity();
                    }
                } else {
                    Log.i(TAG, "Not starting call activity because user setup is in progress.");
                }
            }
            return true;
        }
        if (i != 27) {
            if (i != 79 && i != 130 && i != 222) {
                if (i != 24 && i != 25) {
                    if (i == 126 || i == 127) {
                        if (getTelephonyManager().getCallState() != 0) {
                            return true;
                        }
                    } else {
                        switch (i) {
                            case 84:
                                if (!isNotInstantAppAndKeyguardRestricted(keyDispatcherState)) {
                                    if (keyEvent.getRepeatCount() == 0) {
                                        keyDispatcherState.startTracking(keyEvent, this);
                                    } else if (keyEvent.isLongPress() && keyDispatcherState.isTracking(keyEvent)) {
                                        Configuration configuration = this.mContext.getResources().getConfiguration();
                                        if (configuration.keyboard == 1 || configuration.hardKeyboardHidden == 2) {
                                            if (isUserSetupComplete()) {
                                                Intent intent2 = new Intent(Intent.ACTION_SEARCH_LONG_PRESS);
                                                intent2.setFlags(268435456);
                                                try {
                                                    this.mView.performHapticFeedback(0);
                                                    sendCloseSystemWindows();
                                                    getSearchManager().stopSearch();
                                                    this.mContext.startActivity(intent2);
                                                    keyDispatcherState.performedLongPress(keyEvent);
                                                    return true;
                                                } catch (ActivityNotFoundException unused2) {
                                                }
                                            } else {
                                                Log.i(TAG, "Not dispatching SEARCH long press because user setup is in progress.");
                                            }
                                        }
                                    }
                                }
                                break;
                            case 85:
                                if (getTelephonyManager().getCallState() != 0) {
                                    return true;
                                }
                                break;
                        }
                    }
                } else {
                    handleVolumeKeyEvent(keyEvent);
                    return true;
                }
            }
            handleMediaKeyEvent(keyEvent);
            return true;
        }
        if (!isNotInstantAppAndKeyguardRestricted(keyDispatcherState)) {
            if (keyEvent.getRepeatCount() == 0) {
                keyDispatcherState.startTracking(keyEvent, this);
            } else if (keyEvent.isLongPress() && keyDispatcherState.isTracking(keyEvent)) {
                keyDispatcherState.performedLongPress(keyEvent);
                if (isUserSetupComplete()) {
                    this.mView.performHapticFeedback(0);
                    sendCloseSystemWindows();
                    Intent intent3 = new Intent(Intent.ACTION_CAMERA_BUTTON, (Uri) null);
                    intent3.addFlags(268435456);
                    intent3.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
                    this.mContext.sendOrderedBroadcastAsUser(intent3, UserHandle.CURRENT_OR_SELF, null, null, null, 0, null, null);
                } else {
                    Log.i(TAG, "Not dispatching CAMERA long press because user setup is in progress.");
                }
            }
            return true;
        }
        return false;
    }

    private boolean isNotInstantAppAndKeyguardRestricted(KeyEvent.DispatcherState dispatcherState) {
        return !this.mContext.getPackageManager().isInstantApp() && (getKeyguardManager().inKeyguardRestrictedInputMode() || dispatcherState == null);
    }

    boolean onKeyUp(int i, KeyEvent keyEvent) {
        KeyEvent.DispatcherState keyDispatcherState = this.mView.getKeyDispatcherState();
        if (keyDispatcherState != null) {
            keyDispatcherState.handleUpEvent(keyEvent);
        }
        if (i == 5) {
            if (isNotInstantAppAndKeyguardRestricted(keyDispatcherState)) {
                return false;
            }
            if (keyEvent.isTracking() && !keyEvent.isCanceled()) {
                if (isUserSetupComplete()) {
                    startCallActivity();
                } else {
                    Log.i(TAG, "Not starting call activity because user setup is in progress.");
                }
            }
            return true;
        }
        if (i != 27) {
            if (i != 79 && i != 130 && i != 222) {
                if (i == 24 || i == 25) {
                    if (!keyEvent.isCanceled()) {
                        handleVolumeKeyEvent(keyEvent);
                    }
                    return true;
                }
                if (i != 126 && i != 127) {
                    switch (i) {
                        case 85:
                        case 86:
                        case 87:
                        case 88:
                        case 89:
                        case 90:
                        case 91:
                            break;
                        default:
                            return false;
                    }
                }
            }
            handleMediaKeyEvent(keyEvent);
            return true;
        }
        if (isNotInstantAppAndKeyguardRestricted(keyDispatcherState)) {
            return false;
        }
        if (keyEvent.isTracking()) {
            keyEvent.isCanceled();
        }
        return true;
    }

    void startCallActivity() {
        sendCloseSystemWindows();
        Intent intent = new Intent(Intent.ACTION_CALL_BUTTON);
        intent.setFlags(268435456);
        try {
            this.mContext.startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            Log.w(TAG, "No activity found for android.intent.action.CALL_BUTTON.");
        }
    }

    SearchManager getSearchManager() {
        if (this.mSearchManager == null) {
            this.mSearchManager = (SearchManager) this.mContext.getSystemService("search");
        }
        return this.mSearchManager;
    }

    TelephonyManager getTelephonyManager() {
        if (this.mTelephonyManager == null) {
            this.mTelephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        }
        return this.mTelephonyManager;
    }

    KeyguardManager getKeyguardManager() {
        if (this.mKeyguardManager == null) {
            this.mKeyguardManager = (KeyguardManager) this.mContext.getSystemService(Context.KEYGUARD_SERVICE);
        }
        return this.mKeyguardManager;
    }

    AudioManager getAudioManager() {
        if (this.mAudioManager == null) {
            this.mAudioManager = (AudioManager) this.mContext.getSystemService("audio");
        }
        return this.mAudioManager;
    }

    MediaSessionManager getMediaSessionManager() {
        if (this.mMediaSessionManager == null) {
            this.mMediaSessionManager = (MediaSessionManager) this.mContext.getSystemService(Context.MEDIA_SESSION_SERVICE);
        }
        return this.mMediaSessionManager;
    }

    void sendCloseSystemWindows() {
        PhoneWindow.sendCloseSystemWindows(this.mContext, null);
    }

    private void handleVolumeKeyEvent(KeyEvent keyEvent) {
        getMediaSessionManager().dispatchVolumeKeyEventAsSystemService(keyEvent, Integer.MIN_VALUE);
    }

    private void handleMediaKeyEvent(KeyEvent keyEvent) {
        getMediaSessionManager().dispatchMediaKeyEventAsSystemService(keyEvent);
    }

    private boolean isUserSetupComplete() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 0) != 0;
    }
}
