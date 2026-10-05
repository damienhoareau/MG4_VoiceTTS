package android.view.accessibility;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import android.view.IWindow;
import com.android.internal.R;
import com.android.internal.util.IntPair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public final class AccessibilityManager {
    public static final String ACTION_CHOOSE_ACCESSIBILITY_BUTTON = "com.android.internal.intent.action.CHOOSE_ACCESSIBILITY_BUTTON";
    public static final int AUTOCLICK_DELAY_DEFAULT = 600;
    public static final int DALTONIZER_CORRECT_DEUTERANOMALY = 12;
    public static final int DALTONIZER_DISABLED = -1;
    public static final int DALTONIZER_SIMULATE_MONOCHROMACY = 0;
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "AccessibilityManager";
    public static final int STATE_FLAG_ACCESSIBILITY_ENABLED = 1;
    public static final int STATE_FLAG_HIGH_TEXT_CONTRAST_ENABLED = 4;
    public static final int STATE_FLAG_TOUCH_EXPLORATION_ENABLED = 2;
    private static AccessibilityManager sInstance;
    static final Object sInstanceSync = new Object();
    AccessibilityPolicy mAccessibilityPolicy;
    final Handler mHandler;
    boolean mIsEnabled;
    boolean mIsHighTextContrastEnabled;
    boolean mIsTouchExplorationEnabled;
    private SparseArray<List<AccessibilityRequestPreparer>> mRequestPreparerLists;
    private IAccessibilityManager mService;
    final int mUserId;
    private final Object mLock = new Object();
    int mRelevantEventTypes = -1;
    private final ArrayMap<AccessibilityStateChangeListener, Handler> mAccessibilityStateChangeListeners = new ArrayMap<>();
    private final ArrayMap<TouchExplorationStateChangeListener, Handler> mTouchExplorationStateChangeListeners = new ArrayMap<>();
    private final ArrayMap<HighTextContrastChangeListener, Handler> mHighTextContrastStateChangeListeners = new ArrayMap<>();
    private final ArrayMap<AccessibilityServicesStateChangeListener, Handler> mServicesStateChangeListeners = new ArrayMap<>();
    private final IAccessibilityManagerClient.Stub mClient = new AnonymousClass1();
    final Handler.Callback mCallback = new MyCallback(this, null);

    public interface AccessibilityPolicy {
        List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(int i, List<AccessibilityServiceInfo> list);

        List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList(List<AccessibilityServiceInfo> list);

        int getRelevantEventTypes(int i);

        boolean isEnabled(boolean z);

        AccessibilityEvent onAccessibilityEvent(AccessibilityEvent accessibilityEvent, boolean z, int i);
    }

    public interface AccessibilityServicesStateChangeListener {
        void onAccessibilityServicesStateChanged(AccessibilityManager accessibilityManager);
    }

    public interface AccessibilityStateChangeListener {
        void onAccessibilityStateChanged(boolean z);
    }

    public interface HighTextContrastChangeListener {
        void onHighTextContrastStateChanged(boolean z);
    }

    public interface TouchExplorationStateChangeListener {
        void onTouchExplorationStateChanged(boolean z);
    }

    /* JADX INFO: renamed from: android.view.accessibility.AccessibilityManager$1, reason: invalid class name */
    class AnonymousClass1 extends IAccessibilityManagerClient.Stub {
        AnonymousClass1() {
        }

        @Override // android.view.accessibility.IAccessibilityManagerClient
        public void setState(int i) {
            AccessibilityManager.this.mHandler.obtainMessage(1, i, 0).sendToTarget();
        }

        @Override // android.view.accessibility.IAccessibilityManagerClient
        public void notifyServicesStateChanged() {
            synchronized (AccessibilityManager.this.mLock) {
                if (AccessibilityManager.this.mServicesStateChangeListeners.isEmpty()) {
                    return;
                }
                int size = new ArrayMap(AccessibilityManager.this.mServicesStateChangeListeners).size();
                for (int i = 0; i < size; i++) {
                    final AccessibilityServicesStateChangeListener accessibilityServicesStateChangeListener = (AccessibilityServicesStateChangeListener) AccessibilityManager.this.mServicesStateChangeListeners.keyAt(i);
                    ((Handler) AccessibilityManager.this.mServicesStateChangeListeners.valueAt(i)).post(new Runnable() { // from class: android.view.accessibility.-$$Lambda$AccessibilityManager$1$o7fCplskH9NlBwJvkl6NoZ0L_BA
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$notifyServicesStateChanged$0$AccessibilityManager$1(accessibilityServicesStateChangeListener);
                        }
                    });
                }
            }
        }

        public /* synthetic */ void lambda$notifyServicesStateChanged$0$AccessibilityManager$1(AccessibilityServicesStateChangeListener accessibilityServicesStateChangeListener) {
            accessibilityServicesStateChangeListener.onAccessibilityServicesStateChanged(AccessibilityManager.this);
        }

        @Override // android.view.accessibility.IAccessibilityManagerClient
        public void setRelevantEventTypes(int i) {
            AccessibilityManager.this.mRelevantEventTypes = i;
        }
    }

    public static AccessibilityManager getInstance(Context context) {
        synchronized (sInstanceSync) {
            if (sInstance == null) {
                sInstance = new AccessibilityManager(context, (IAccessibilityManager) null, (Binder.getCallingUid() == 1000 || context.checkCallingOrSelfPermission(Manifest.permission.INTERACT_ACROSS_USERS) == 0 || context.checkCallingOrSelfPermission(Manifest.permission.INTERACT_ACROSS_USERS_FULL) == 0) ? -2 : context.getUserId());
            }
        }
        return sInstance;
    }

    public AccessibilityManager(Context context, IAccessibilityManager iAccessibilityManager, int i) {
        this.mHandler = new Handler(context.getMainLooper(), this.mCallback);
        this.mUserId = i;
        synchronized (this.mLock) {
            tryConnectToServiceLocked(iAccessibilityManager);
        }
    }

    public AccessibilityManager(Handler handler, IAccessibilityManager iAccessibilityManager, int i) {
        this.mHandler = handler;
        this.mUserId = i;
        synchronized (this.mLock) {
            tryConnectToServiceLocked(iAccessibilityManager);
        }
    }

    public IAccessibilityManagerClient getClient() {
        return this.mClient;
    }

    public Handler.Callback getCallback() {
        return this.mCallback;
    }

    public boolean isEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mIsEnabled || (this.mAccessibilityPolicy != null && this.mAccessibilityPolicy.isEnabled(this.mIsEnabled));
        }
        return z;
    }

    public boolean isTouchExplorationEnabled() {
        synchronized (this.mLock) {
            if (getServiceLocked() == null) {
                return false;
            }
            return this.mIsTouchExplorationEnabled;
        }
    }

    public boolean isHighTextContrastEnabled() {
        synchronized (this.mLock) {
            if (getServiceLocked() == null) {
                return false;
            }
            return this.mIsHighTextContrastEnabled;
        }
    }

    public void sendAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        AccessibilityEvent accessibilityEventOnAccessibilityEvent;
        synchronized (this.mLock) {
            IAccessibilityManager serviceLocked = getServiceLocked();
            if (serviceLocked == null) {
                return;
            }
            accessibilityEvent.setEventTime(SystemClock.uptimeMillis());
            if (this.mAccessibilityPolicy != null) {
                accessibilityEventOnAccessibilityEvent = this.mAccessibilityPolicy.onAccessibilityEvent(accessibilityEvent, this.mIsEnabled, this.mRelevantEventTypes);
                if (accessibilityEventOnAccessibilityEvent == null) {
                    return;
                }
            } else {
                accessibilityEventOnAccessibilityEvent = accessibilityEvent;
            }
            if (!isEnabled()) {
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    throw new IllegalStateException("Accessibility off. Did you forget to check that?");
                }
                Log.e(LOG_TAG, "AccessibilityEvent sent with accessibility disabled");
                return;
            }
            if ((accessibilityEventOnAccessibilityEvent.getEventType() & this.mRelevantEventTypes) == 0) {
                return;
            }
            int i = this.mUserId;
            try {
                try {
                    long jClearCallingIdentity = Binder.clearCallingIdentity();
                    try {
                        serviceLocked.sendAccessibilityEvent(accessibilityEventOnAccessibilityEvent, i);
                        Binder.restoreCallingIdentity(jClearCallingIdentity);
                        if (accessibilityEvent != accessibilityEventOnAccessibilityEvent) {
                            accessibilityEvent.recycle();
                        }
                    } catch (Throwable th) {
                        Binder.restoreCallingIdentity(jClearCallingIdentity);
                        throw th;
                    }
                } catch (RemoteException e) {
                    Log.e(LOG_TAG, "Error during sending " + accessibilityEventOnAccessibilityEvent + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER, e);
                    if (accessibilityEvent != accessibilityEventOnAccessibilityEvent) {
                    }
                }
                accessibilityEventOnAccessibilityEvent.recycle();
            } catch (Throwable th2) {
                if (accessibilityEvent != accessibilityEventOnAccessibilityEvent) {
                    accessibilityEvent.recycle();
                }
                accessibilityEventOnAccessibilityEvent.recycle();
                throw th2;
            }
        }
    }

    public void interrupt() {
        synchronized (this.mLock) {
            IAccessibilityManager serviceLocked = getServiceLocked();
            if (serviceLocked == null) {
                return;
            }
            if (!isEnabled()) {
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    throw new IllegalStateException("Accessibility off. Did you forget to check that?");
                }
                Log.e(LOG_TAG, "Interrupt called with accessibility disabled");
            } else {
                int i = this.mUserId;
                try {
                    serviceLocked.interrupt(i);
                } catch (RemoteException e) {
                    Log.e(LOG_TAG, "Error while requesting interrupt from all services. ", e);
                }
            }
        }
    }

    @Deprecated
    public List<ServiceInfo> getAccessibilityServiceList() {
        List<AccessibilityServiceInfo> installedAccessibilityServiceList = getInstalledAccessibilityServiceList();
        ArrayList arrayList = new ArrayList();
        int size = installedAccessibilityServiceList.size();
        for (int i = 0; i < size; i++) {
            arrayList.add(installedAccessibilityServiceList.get(i).getResolveInfo().serviceInfo);
        }
        return Collections.unmodifiableList(arrayList);
    }

    public List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList() {
        synchronized (this.mLock) {
            IAccessibilityManager serviceLocked = getServiceLocked();
            if (serviceLocked == null) {
                return Collections.emptyList();
            }
            int i = this.mUserId;
            List<AccessibilityServiceInfo> installedAccessibilityServiceList = null;
            try {
                installedAccessibilityServiceList = serviceLocked.getInstalledAccessibilityServiceList(i);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, "Error while obtaining the installed AccessibilityServices. ", e);
            }
            AccessibilityPolicy accessibilityPolicy = this.mAccessibilityPolicy;
            if (accessibilityPolicy != null) {
                installedAccessibilityServiceList = accessibilityPolicy.getInstalledAccessibilityServiceList(installedAccessibilityServiceList);
            }
            if (installedAccessibilityServiceList != null) {
                return Collections.unmodifiableList(installedAccessibilityServiceList);
            }
            return Collections.emptyList();
        }
    }

    public List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(int i) {
        synchronized (this.mLock) {
            IAccessibilityManager serviceLocked = getServiceLocked();
            if (serviceLocked == null) {
                return Collections.emptyList();
            }
            int i2 = this.mUserId;
            List<AccessibilityServiceInfo> enabledAccessibilityServiceList = null;
            try {
                enabledAccessibilityServiceList = serviceLocked.getEnabledAccessibilityServiceList(i, i2);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, "Error while obtaining the installed AccessibilityServices. ", e);
            }
            AccessibilityPolicy accessibilityPolicy = this.mAccessibilityPolicy;
            if (accessibilityPolicy != null) {
                enabledAccessibilityServiceList = accessibilityPolicy.getEnabledAccessibilityServiceList(i, enabledAccessibilityServiceList);
            }
            if (enabledAccessibilityServiceList != null) {
                return Collections.unmodifiableList(enabledAccessibilityServiceList);
            }
            return Collections.emptyList();
        }
    }

    public boolean addAccessibilityStateChangeListener(AccessibilityStateChangeListener accessibilityStateChangeListener) {
        addAccessibilityStateChangeListener(accessibilityStateChangeListener, null);
        return true;
    }

    public void addAccessibilityStateChangeListener(AccessibilityStateChangeListener accessibilityStateChangeListener, Handler handler) {
        synchronized (this.mLock) {
            ArrayMap<AccessibilityStateChangeListener, Handler> arrayMap = this.mAccessibilityStateChangeListeners;
            if (handler == null) {
                handler = this.mHandler;
            }
            arrayMap.put(accessibilityStateChangeListener, handler);
        }
    }

    public boolean removeAccessibilityStateChangeListener(AccessibilityStateChangeListener accessibilityStateChangeListener) {
        boolean z;
        synchronized (this.mLock) {
            int iIndexOfKey = this.mAccessibilityStateChangeListeners.indexOfKey(accessibilityStateChangeListener);
            this.mAccessibilityStateChangeListeners.remove(accessibilityStateChangeListener);
            z = iIndexOfKey >= 0;
        }
        return z;
    }

    public boolean addTouchExplorationStateChangeListener(TouchExplorationStateChangeListener touchExplorationStateChangeListener) {
        addTouchExplorationStateChangeListener(touchExplorationStateChangeListener, null);
        return true;
    }

    public void addTouchExplorationStateChangeListener(TouchExplorationStateChangeListener touchExplorationStateChangeListener, Handler handler) {
        synchronized (this.mLock) {
            ArrayMap<TouchExplorationStateChangeListener, Handler> arrayMap = this.mTouchExplorationStateChangeListeners;
            if (handler == null) {
                handler = this.mHandler;
            }
            arrayMap.put(touchExplorationStateChangeListener, handler);
        }
    }

    public boolean removeTouchExplorationStateChangeListener(TouchExplorationStateChangeListener touchExplorationStateChangeListener) {
        boolean z;
        synchronized (this.mLock) {
            int iIndexOfKey = this.mTouchExplorationStateChangeListeners.indexOfKey(touchExplorationStateChangeListener);
            this.mTouchExplorationStateChangeListeners.remove(touchExplorationStateChangeListener);
            z = iIndexOfKey >= 0;
        }
        return z;
    }

    public void addAccessibilityServicesStateChangeListener(AccessibilityServicesStateChangeListener accessibilityServicesStateChangeListener, Handler handler) {
        synchronized (this.mLock) {
            ArrayMap<AccessibilityServicesStateChangeListener, Handler> arrayMap = this.mServicesStateChangeListeners;
            if (handler == null) {
                handler = this.mHandler;
            }
            arrayMap.put(accessibilityServicesStateChangeListener, handler);
        }
    }

    public void removeAccessibilityServicesStateChangeListener(AccessibilityServicesStateChangeListener accessibilityServicesStateChangeListener) {
        synchronized (this.mLock) {
            this.mServicesStateChangeListeners.remove(accessibilityServicesStateChangeListener);
        }
    }

    public void addAccessibilityRequestPreparer(AccessibilityRequestPreparer accessibilityRequestPreparer) {
        if (this.mRequestPreparerLists == null) {
            this.mRequestPreparerLists = new SparseArray<>(1);
        }
        int accessibilityViewId = accessibilityRequestPreparer.getView().getAccessibilityViewId();
        List<AccessibilityRequestPreparer> arrayList = this.mRequestPreparerLists.get(accessibilityViewId);
        if (arrayList == null) {
            arrayList = new ArrayList<>(1);
            this.mRequestPreparerLists.put(accessibilityViewId, arrayList);
        }
        arrayList.add(accessibilityRequestPreparer);
    }

    public void removeAccessibilityRequestPreparer(AccessibilityRequestPreparer accessibilityRequestPreparer) {
        int accessibilityViewId;
        List<AccessibilityRequestPreparer> list;
        if (this.mRequestPreparerLists == null || (list = this.mRequestPreparerLists.get((accessibilityViewId = accessibilityRequestPreparer.getView().getAccessibilityViewId()))) == null) {
            return;
        }
        list.remove(accessibilityRequestPreparer);
        if (list.isEmpty()) {
            this.mRequestPreparerLists.remove(accessibilityViewId);
        }
    }

    public List<AccessibilityRequestPreparer> getRequestPreparersForAccessibilityId(int i) {
        SparseArray<List<AccessibilityRequestPreparer>> sparseArray = this.mRequestPreparerLists;
        if (sparseArray == null) {
            return null;
        }
        return sparseArray.get(i);
    }

    public void addHighTextContrastStateChangeListener(HighTextContrastChangeListener highTextContrastChangeListener, Handler handler) {
        synchronized (this.mLock) {
            ArrayMap<HighTextContrastChangeListener, Handler> arrayMap = this.mHighTextContrastStateChangeListeners;
            if (handler == null) {
                handler = this.mHandler;
            }
            arrayMap.put(highTextContrastChangeListener, handler);
        }
    }

    public void removeHighTextContrastStateChangeListener(HighTextContrastChangeListener highTextContrastChangeListener) {
        synchronized (this.mLock) {
            this.mHighTextContrastStateChangeListeners.remove(highTextContrastChangeListener);
        }
    }

    public void setAccessibilityPolicy(AccessibilityPolicy accessibilityPolicy) {
        synchronized (this.mLock) {
            this.mAccessibilityPolicy = accessibilityPolicy;
        }
    }

    public boolean isAccessibilityVolumeStreamActive() {
        List<AccessibilityServiceInfo> enabledAccessibilityServiceList = getEnabledAccessibilityServiceList(-1);
        for (int i = 0; i < enabledAccessibilityServiceList.size(); i++) {
            if ((enabledAccessibilityServiceList.get(i).flags & 128) != 0) {
                return true;
            }
        }
        return false;
    }

    public boolean sendFingerprintGesture(int i) {
        synchronized (this.mLock) {
            IAccessibilityManager serviceLocked = getServiceLocked();
            if (serviceLocked == null) {
                return false;
            }
            try {
                return serviceLocked.sendFingerprintGesture(i);
            } catch (RemoteException unused) {
                return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setStateLocked(int i) {
        boolean z = (i & 1) != 0;
        boolean z2 = (i & 2) != 0;
        boolean z3 = (i & 4) != 0;
        boolean zIsEnabled = isEnabled();
        boolean z4 = this.mIsTouchExplorationEnabled;
        boolean z5 = this.mIsHighTextContrastEnabled;
        this.mIsEnabled = z;
        this.mIsTouchExplorationEnabled = z2;
        this.mIsHighTextContrastEnabled = z3;
        if (zIsEnabled != isEnabled()) {
            notifyAccessibilityStateChanged();
        }
        if (z4 != z2) {
            notifyTouchExplorationStateChanged();
        }
        if (z5 != z3) {
            notifyHighTextContrastStateChanged();
        }
    }

    public AccessibilityServiceInfo getInstalledServiceInfoWithComponentName(ComponentName componentName) {
        List<AccessibilityServiceInfo> installedAccessibilityServiceList = getInstalledAccessibilityServiceList();
        if (installedAccessibilityServiceList != null && componentName != null) {
            for (int i = 0; i < installedAccessibilityServiceList.size(); i++) {
                if (componentName.equals(installedAccessibilityServiceList.get(i).getComponentName())) {
                    return installedAccessibilityServiceList.get(i);
                }
            }
        }
        return null;
    }

    public int addAccessibilityInteractionConnection(IWindow iWindow, String str, IAccessibilityInteractionConnection iAccessibilityInteractionConnection) {
        synchronized (this.mLock) {
            IAccessibilityManager serviceLocked = getServiceLocked();
            if (serviceLocked == null) {
                return -1;
            }
            int i = this.mUserId;
            try {
                return serviceLocked.addAccessibilityInteractionConnection(iWindow, iAccessibilityInteractionConnection, str, i);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, "Error while adding an accessibility interaction connection. ", e);
                return -1;
            }
        }
    }

    public void removeAccessibilityInteractionConnection(IWindow iWindow) {
        synchronized (this.mLock) {
            IAccessibilityManager serviceLocked = getServiceLocked();
            if (serviceLocked == null) {
                return;
            }
            try {
                serviceLocked.removeAccessibilityInteractionConnection(iWindow);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, "Error while removing an accessibility interaction connection. ", e);
            }
        }
    }

    public void performAccessibilityShortcut() {
        synchronized (this.mLock) {
            IAccessibilityManager serviceLocked = getServiceLocked();
            if (serviceLocked == null) {
                return;
            }
            try {
                serviceLocked.performAccessibilityShortcut();
            } catch (RemoteException e) {
                Log.e(LOG_TAG, "Error performing accessibility shortcut. ", e);
            }
        }
    }

    public void notifyAccessibilityButtonClicked() {
        synchronized (this.mLock) {
            IAccessibilityManager serviceLocked = getServiceLocked();
            if (serviceLocked == null) {
                return;
            }
            try {
                serviceLocked.notifyAccessibilityButtonClicked();
            } catch (RemoteException e) {
                Log.e(LOG_TAG, "Error while dispatching accessibility button click", e);
            }
        }
    }

    public void notifyAccessibilityButtonVisibilityChanged(boolean z) {
        synchronized (this.mLock) {
            IAccessibilityManager serviceLocked = getServiceLocked();
            if (serviceLocked == null) {
                return;
            }
            try {
                serviceLocked.notifyAccessibilityButtonVisibilityChanged(z);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, "Error while dispatching accessibility button visibility change", e);
            }
        }
    }

    public void setPictureInPictureActionReplacingConnection(IAccessibilityInteractionConnection iAccessibilityInteractionConnection) {
        synchronized (this.mLock) {
            IAccessibilityManager serviceLocked = getServiceLocked();
            if (serviceLocked == null) {
                return;
            }
            try {
                serviceLocked.setPictureInPictureActionReplacingConnection(iAccessibilityInteractionConnection);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, "Error setting picture in picture action replacement", e);
            }
        }
    }

    private IAccessibilityManager getServiceLocked() {
        if (this.mService == null) {
            tryConnectToServiceLocked(null);
        }
        return this.mService;
    }

    private void tryConnectToServiceLocked(IAccessibilityManager iAccessibilityManager) {
        if (iAccessibilityManager == null) {
            IBinder service = ServiceManager.getService(Context.ACCESSIBILITY_SERVICE);
            if (service == null) {
                return;
            } else {
                iAccessibilityManager = IAccessibilityManager.Stub.asInterface(service);
            }
        }
        try {
            long jAddClient = iAccessibilityManager.addClient(this.mClient, this.mUserId);
            setStateLocked(IntPair.first(jAddClient));
            this.mRelevantEventTypes = IntPair.second(jAddClient);
            this.mService = iAccessibilityManager;
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "AccessibilityManagerService is dead", e);
        }
    }

    private void notifyAccessibilityStateChanged() {
        synchronized (this.mLock) {
            if (this.mAccessibilityStateChangeListeners.isEmpty()) {
                return;
            }
            final boolean zIsEnabled = isEnabled();
            ArrayMap arrayMap = new ArrayMap(this.mAccessibilityStateChangeListeners);
            int size = arrayMap.size();
            for (int i = 0; i < size; i++) {
                final AccessibilityStateChangeListener accessibilityStateChangeListener = (AccessibilityStateChangeListener) arrayMap.keyAt(i);
                ((Handler) arrayMap.valueAt(i)).post(new Runnable() { // from class: android.view.accessibility.-$$Lambda$AccessibilityManager$yzw5NYY7_MfAQ9gLy3mVllchaXo
                    @Override // java.lang.Runnable
                    public final void run() {
                        accessibilityStateChangeListener.onAccessibilityStateChanged(zIsEnabled);
                    }
                });
            }
        }
    }

    private void notifyTouchExplorationStateChanged() {
        synchronized (this.mLock) {
            if (this.mTouchExplorationStateChangeListeners.isEmpty()) {
                return;
            }
            final boolean z = this.mIsTouchExplorationEnabled;
            ArrayMap arrayMap = new ArrayMap(this.mTouchExplorationStateChangeListeners);
            int size = arrayMap.size();
            for (int i = 0; i < size; i++) {
                final TouchExplorationStateChangeListener touchExplorationStateChangeListener = (TouchExplorationStateChangeListener) arrayMap.keyAt(i);
                ((Handler) arrayMap.valueAt(i)).post(new Runnable() { // from class: android.view.accessibility.-$$Lambda$AccessibilityManager$a0OtrjOl35tiW2vwyvAmY6_LiLI
                    @Override // java.lang.Runnable
                    public final void run() {
                        touchExplorationStateChangeListener.onTouchExplorationStateChanged(z);
                    }
                });
            }
        }
    }

    private void notifyHighTextContrastStateChanged() {
        synchronized (this.mLock) {
            if (this.mHighTextContrastStateChangeListeners.isEmpty()) {
                return;
            }
            final boolean z = this.mIsHighTextContrastEnabled;
            ArrayMap arrayMap = new ArrayMap(this.mHighTextContrastStateChangeListeners);
            int size = arrayMap.size();
            for (int i = 0; i < size; i++) {
                final HighTextContrastChangeListener highTextContrastChangeListener = (HighTextContrastChangeListener) arrayMap.keyAt(i);
                ((Handler) arrayMap.valueAt(i)).post(new Runnable() { // from class: android.view.accessibility.-$$Lambda$AccessibilityManager$4M6GrmFiqsRwVzn352N10DcU6RM
                    @Override // java.lang.Runnable
                    public final void run() {
                        highTextContrastChangeListener.onHighTextContrastStateChanged(z);
                    }
                });
            }
        }
    }

    public static boolean isAccessibilityButtonSupported() {
        return Resources.getSystem().getBoolean(R.bool.config_showNavigationBar);
    }

    private final class MyCallback implements Handler.Callback {
        public static final int MSG_SET_STATE = 1;

        private MyCallback() {
        }

        /* synthetic */ MyCallback(AccessibilityManager accessibilityManager, AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            if (message.what == 1) {
                int i = message.arg1;
                synchronized (AccessibilityManager.this.mLock) {
                    AccessibilityManager.this.setStateLocked(i);
                }
            }
            return true;
        }
    }
}
