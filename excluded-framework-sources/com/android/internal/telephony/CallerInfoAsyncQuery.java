package com.android.internal.telephony;

import android.app.ActivityManager;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.Rlog;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class CallerInfoAsyncQuery {
    private static final boolean DBG = false;
    private static final boolean ENABLE_UNKNOWN_NUMBER_GEO_DESCRIPTION = true;
    private static final int EVENT_ADD_LISTENER = 2;
    private static final int EVENT_EMERGENCY_NUMBER = 4;
    private static final int EVENT_END_OF_QUEUE = 3;
    private static final int EVENT_GET_GEO_DESCRIPTION = 6;
    private static final int EVENT_NEW_QUERY = 1;
    private static final int EVENT_VOICEMAIL_NUMBER = 5;
    private static final String LOG_TAG = "CallerInfoAsyncQuery";
    private CallerInfoAsyncQueryHandler mHandler;

    public interface OnQueryCompleteListener {
        void onQueryComplete(int i, Object obj, CallerInfo callerInfo);
    }

    private static final class CookieWrapper {
        public Object cookie;
        public int event;
        public String geoDescription;
        public OnQueryCompleteListener listener;
        public String number;
        public int subId;

        private CookieWrapper() {
        }
    }

    public static class QueryPoolException extends SQLException {
        public QueryPoolException(String str) {
            super(str);
        }
    }

    static ContentResolver getCurrentProfileContentResolver(Context context) {
        int currentUser = ActivityManager.getCurrentUser();
        if (UserManager.get(context).getUserHandle() != currentUser) {
            try {
                return context.createPackageContextAsUser(context.getPackageName(), 0, new UserHandle(currentUser)).getContentResolver();
            } catch (PackageManager.NameNotFoundException e) {
                Rlog.e(LOG_TAG, "Can't find self package", e);
            }
        }
        return context.getContentResolver();
    }

    private class CallerInfoAsyncQueryHandler extends AsyncQueryHandler {
        private CallerInfo mCallerInfo;
        private Context mContext;
        private List<Runnable> mPendingListenerCallbacks;
        private Uri mQueryUri;

        protected class CallerInfoWorkerHandler extends AsyncQueryHandler.WorkerHandler {
            public CallerInfoWorkerHandler(Looper looper) {
                super(looper);
            }

            @Override // android.content.AsyncQueryHandler.WorkerHandler, android.os.Handler
            public void handleMessage(Message message) {
                AsyncQueryHandler.WorkerArgs workerArgs = (AsyncQueryHandler.WorkerArgs) message.obj;
                CookieWrapper cookieWrapper = (CookieWrapper) workerArgs.cookie;
                if (cookieWrapper == null) {
                    Rlog.i(CallerInfoAsyncQuery.LOG_TAG, "Unexpected command (CookieWrapper is null): " + message.what + " ignored by CallerInfoWorkerHandler, passing onto parent.");
                    super.handleMessage(message);
                    return;
                }
                Rlog.d(CallerInfoAsyncQuery.LOG_TAG, "Processing event: " + cookieWrapper.event + " token (arg1): " + message.arg1 + " command: " + message.what + " query URI: " + CallerInfoAsyncQuery.sanitizeUriToString(workerArgs.uri));
                switch (cookieWrapper.event) {
                    case 1:
                        super.handleMessage(message);
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        Message messageObtainMessage = workerArgs.handler.obtainMessage(message.what);
                        messageObtainMessage.obj = workerArgs;
                        messageObtainMessage.arg1 = message.arg1;
                        messageObtainMessage.sendToTarget();
                        break;
                    case 6:
                        handleGeoDescription(message);
                        break;
                }
            }

            private void handleGeoDescription(Message message) {
                AsyncQueryHandler.WorkerArgs workerArgs = (AsyncQueryHandler.WorkerArgs) message.obj;
                CookieWrapper cookieWrapper = (CookieWrapper) workerArgs.cookie;
                if (!TextUtils.isEmpty(cookieWrapper.number) && cookieWrapper.cookie != null && CallerInfoAsyncQueryHandler.this.mContext != null) {
                    SystemClock.elapsedRealtime();
                    cookieWrapper.geoDescription = CallerInfo.getGeoDescription(CallerInfoAsyncQueryHandler.this.mContext, cookieWrapper.number);
                    SystemClock.elapsedRealtime();
                }
                Message messageObtainMessage = workerArgs.handler.obtainMessage(message.what);
                messageObtainMessage.obj = workerArgs;
                messageObtainMessage.arg1 = message.arg1;
                messageObtainMessage.sendToTarget();
            }
        }

        private CallerInfoAsyncQueryHandler(Context context) {
            super(CallerInfoAsyncQuery.getCurrentProfileContentResolver(context));
            this.mPendingListenerCallbacks = new ArrayList();
            this.mContext = context;
        }

        @Override // android.content.AsyncQueryHandler
        protected Handler createHandler(Looper looper) {
            return new CallerInfoWorkerHandler(looper);
        }

        @Override // android.content.AsyncQueryHandler
        protected void onQueryComplete(final int i, Object obj, Cursor cursor) {
            Rlog.d(CallerInfoAsyncQuery.LOG_TAG, "##### onQueryComplete() #####   query complete for token: " + i);
            final CookieWrapper cookieWrapper = (CookieWrapper) obj;
            if (cookieWrapper == null) {
                Rlog.i(CallerInfoAsyncQuery.LOG_TAG, "Cookie is null, ignoring onQueryComplete() request.");
                if (cursor != null) {
                    cursor.close();
                    return;
                }
                return;
            }
            if (cookieWrapper.event == 3) {
                Iterator<Runnable> it = this.mPendingListenerCallbacks.iterator();
                while (it.hasNext()) {
                    it.next().run();
                }
                this.mPendingListenerCallbacks.clear();
                CallerInfoAsyncQuery.this.release();
                if (cursor != null) {
                    cursor.close();
                    return;
                }
                return;
            }
            if (cookieWrapper.event == 6) {
                CallerInfo callerInfo = this.mCallerInfo;
                if (callerInfo != null) {
                    callerInfo.geoDescription = cookieWrapper.geoDescription;
                }
                CookieWrapper cookieWrapper2 = new CookieWrapper();
                cookieWrapper2.event = 3;
                startQuery(i, cookieWrapper2, null, null, null, null, null);
            }
            if (this.mCallerInfo == null) {
                if (this.mContext == null || this.mQueryUri == null) {
                    throw new QueryPoolException("Bad context or query uri, or CallerInfoAsyncQuery already released.");
                }
                if (cookieWrapper.event == 4) {
                    this.mCallerInfo = new CallerInfo().markAsEmergency(this.mContext);
                } else if (cookieWrapper.event == 5) {
                    this.mCallerInfo = new CallerInfo().markAsVoiceMail(cookieWrapper.subId);
                } else {
                    this.mCallerInfo = CallerInfo.getCallerInfo(this.mContext, this.mQueryUri, cursor);
                    CallerInfo callerInfoDoSecondaryLookupIfNecessary = CallerInfo.doSecondaryLookupIfNecessary(this.mContext, cookieWrapper.number, this.mCallerInfo);
                    if (callerInfoDoSecondaryLookupIfNecessary != this.mCallerInfo) {
                        this.mCallerInfo = callerInfoDoSecondaryLookupIfNecessary;
                    }
                    if (!TextUtils.isEmpty(cookieWrapper.number)) {
                        this.mCallerInfo.phoneNumber = PhoneNumberUtils.formatNumber(cookieWrapper.number, this.mCallerInfo.normalizedNumber, CallerInfo.getCurrentCountryIso(this.mContext));
                    }
                    if (TextUtils.isEmpty(this.mCallerInfo.name)) {
                        cookieWrapper.event = 6;
                        startQuery(i, cookieWrapper, null, null, null, null, null);
                        return;
                    }
                }
                CookieWrapper cookieWrapper3 = new CookieWrapper();
                cookieWrapper3.event = 3;
                startQuery(i, cookieWrapper3, null, null, null, null, null);
            }
            if (cookieWrapper.listener != null) {
                this.mPendingListenerCallbacks.add(new Runnable() { // from class: com.android.internal.telephony.CallerInfoAsyncQuery.CallerInfoAsyncQueryHandler.1
                    @Override // java.lang.Runnable
                    public void run() {
                        cookieWrapper.listener.onQueryComplete(i, cookieWrapper.cookie, CallerInfoAsyncQueryHandler.this.mCallerInfo);
                    }
                });
            } else {
                Rlog.w(CallerInfoAsyncQuery.LOG_TAG, "There is no listener to notify for this query.");
            }
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private CallerInfoAsyncQuery() {
    }

    public static CallerInfoAsyncQuery startQuery(int i, Context context, Uri uri, OnQueryCompleteListener onQueryCompleteListener, Object obj) {
        CallerInfoAsyncQuery callerInfoAsyncQuery = new CallerInfoAsyncQuery();
        callerInfoAsyncQuery.allocate(context, uri);
        CookieWrapper cookieWrapper = new CookieWrapper();
        cookieWrapper.listener = onQueryCompleteListener;
        cookieWrapper.cookie = obj;
        cookieWrapper.event = 1;
        callerInfoAsyncQuery.mHandler.startQuery(i, cookieWrapper, uri, null, null, null, null);
        return callerInfoAsyncQuery;
    }

    public static CallerInfoAsyncQuery startQuery(int i, Context context, String str, OnQueryCompleteListener onQueryCompleteListener, Object obj) {
        return startQuery(i, context, str, onQueryCompleteListener, obj, SubscriptionManager.getDefaultSubscriptionId());
    }

    public static CallerInfoAsyncQuery startQuery(int i, Context context, String str, OnQueryCompleteListener onQueryCompleteListener, Object obj, int i2) {
        Uri uriBuild = ContactsContract.PhoneLookup.ENTERPRISE_CONTENT_FILTER_URI.buildUpon().appendPath(str).appendQueryParameter("sip", String.valueOf(PhoneNumberUtils.isUriNumber(str))).build();
        CallerInfoAsyncQuery callerInfoAsyncQuery = new CallerInfoAsyncQuery();
        callerInfoAsyncQuery.allocate(context, uriBuild);
        CookieWrapper cookieWrapper = new CookieWrapper();
        cookieWrapper.listener = onQueryCompleteListener;
        cookieWrapper.cookie = obj;
        cookieWrapper.number = str;
        cookieWrapper.subId = i2;
        if (PhoneNumberUtils.isLocalEmergencyNumber(context, str)) {
            cookieWrapper.event = 4;
        } else if (PhoneNumberUtils.isVoiceMailNumber(context, i2, str)) {
            cookieWrapper.event = 5;
        } else {
            cookieWrapper.event = 1;
        }
        callerInfoAsyncQuery.mHandler.startQuery(i, cookieWrapper, uriBuild, null, null, null, null);
        return callerInfoAsyncQuery;
    }

    public void addQueryListener(int i, OnQueryCompleteListener onQueryCompleteListener, Object obj) {
        CookieWrapper cookieWrapper = new CookieWrapper();
        cookieWrapper.listener = onQueryCompleteListener;
        cookieWrapper.cookie = obj;
        cookieWrapper.event = 2;
        this.mHandler.startQuery(i, cookieWrapper, null, null, null, null, null);
    }

    private void allocate(Context context, Uri uri) {
        if (context == null || uri == null) {
            throw new QueryPoolException("Bad context or query uri.");
        }
        CallerInfoAsyncQueryHandler callerInfoAsyncQueryHandler = new CallerInfoAsyncQueryHandler(context);
        this.mHandler = callerInfoAsyncQueryHandler;
        callerInfoAsyncQueryHandler.mQueryUri = uri;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void release() {
        this.mHandler.mContext = null;
        this.mHandler.mQueryUri = null;
        this.mHandler.mCallerInfo = null;
        this.mHandler = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String sanitizeUriToString(Uri uri) {
        if (uri == null) {
            return "";
        }
        String string = uri.toString();
        int iLastIndexOf = string.lastIndexOf(47);
        if (iLastIndexOf <= 0) {
            return string;
        }
        return string.substring(0, iLastIndexOf) + "/xxxxxxx";
    }
}
