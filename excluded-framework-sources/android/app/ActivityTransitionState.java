package android.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.transition.Transition;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.android.internal.view.OneShotPreDrawListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
class ActivityTransitionState {
    private static final String ENTERING_SHARED_ELEMENTS = "android:enteringSharedElements";
    private static final String EXITING_MAPPED_FROM = "android:exitingMappedFrom";
    private static final String EXITING_MAPPED_TO = "android:exitingMappedTo";
    private ExitTransitionCoordinator mCalledExitCoordinator;
    private ActivityOptions mEnterActivityOptions;
    private EnterTransitionCoordinator mEnterTransitionCoordinator;
    private ArrayList<String> mEnteringNames;
    private SparseArray<WeakReference<ExitTransitionCoordinator>> mExitTransitionCoordinators;
    private int mExitTransitionCoordinatorsKey = 1;
    private ArrayList<String> mExitingFrom;
    private ArrayList<String> mExitingTo;
    private ArrayList<View> mExitingToView;
    private boolean mHasExited;
    private boolean mIsEnterPostponed;
    private boolean mIsEnterTriggered;
    private ExitTransitionCoordinator mReturnExitCoordinator;

    public int addExitTransitionCoordinator(ExitTransitionCoordinator exitTransitionCoordinator) {
        if (this.mExitTransitionCoordinators == null) {
            this.mExitTransitionCoordinators = new SparseArray<>();
        }
        WeakReference<ExitTransitionCoordinator> weakReference = new WeakReference<>(exitTransitionCoordinator);
        for (int size = this.mExitTransitionCoordinators.size() - 1; size >= 0; size--) {
            if (this.mExitTransitionCoordinators.valueAt(size).get() == null) {
                this.mExitTransitionCoordinators.removeAt(size);
            }
        }
        int i = this.mExitTransitionCoordinatorsKey;
        this.mExitTransitionCoordinatorsKey = i + 1;
        this.mExitTransitionCoordinators.append(i, weakReference);
        return i;
    }

    public void readState(Bundle bundle) {
        if (bundle != null) {
            EnterTransitionCoordinator enterTransitionCoordinator = this.mEnterTransitionCoordinator;
            if (enterTransitionCoordinator == null || enterTransitionCoordinator.isReturning()) {
                this.mEnteringNames = bundle.getStringArrayList(ENTERING_SHARED_ELEMENTS);
            }
            if (this.mEnterTransitionCoordinator == null) {
                this.mExitingFrom = bundle.getStringArrayList(EXITING_MAPPED_FROM);
                this.mExitingTo = bundle.getStringArrayList(EXITING_MAPPED_TO);
            }
        }
    }

    public void saveState(Bundle bundle) {
        ArrayList<String> arrayList = this.mEnteringNames;
        if (arrayList != null) {
            bundle.putStringArrayList(ENTERING_SHARED_ELEMENTS, arrayList);
        }
        ArrayList<String> arrayList2 = this.mExitingFrom;
        if (arrayList2 != null) {
            bundle.putStringArrayList(EXITING_MAPPED_FROM, arrayList2);
            bundle.putStringArrayList(EXITING_MAPPED_TO, this.mExitingTo);
        }
    }

    public void setEnterActivityOptions(Activity activity, ActivityOptions activityOptions) {
        Window window = activity.getWindow();
        if (window == null) {
            return;
        }
        window.getDecorView();
        if (window.hasFeature(13) && activityOptions != null && this.mEnterActivityOptions == null && this.mEnterTransitionCoordinator == null && activityOptions.getAnimationType() == 5) {
            this.mEnterActivityOptions = activityOptions;
            this.mIsEnterTriggered = false;
            if (activityOptions.isReturning()) {
                restoreExitedViews();
                int resultCode = this.mEnterActivityOptions.getResultCode();
                if (resultCode != 0) {
                    Intent resultData = this.mEnterActivityOptions.getResultData();
                    if (resultData != null) {
                        resultData.setExtrasClassLoader(activity.getClassLoader());
                    }
                    activity.onActivityReenter(resultCode, resultData);
                }
            }
        }
    }

    public void enterReady(Activity activity) {
        ActivityOptions activityOptions = this.mEnterActivityOptions;
        if (activityOptions == null || this.mIsEnterTriggered) {
            return;
        }
        this.mIsEnterTriggered = true;
        this.mHasExited = false;
        ArrayList<String> sharedElementNames = activityOptions.getSharedElementNames();
        ResultReceiver resultReceiver = this.mEnterActivityOptions.getResultReceiver();
        if (this.mEnterActivityOptions.isReturning()) {
            restoreExitedViews();
            activity.getWindow().getDecorView().setVisibility(0);
        }
        this.mEnterTransitionCoordinator = new EnterTransitionCoordinator(activity, resultReceiver, sharedElementNames, this.mEnterActivityOptions.isReturning(), this.mEnterActivityOptions.isCrossTask());
        if (this.mEnterActivityOptions.isCrossTask()) {
            this.mExitingFrom = new ArrayList<>(this.mEnterActivityOptions.getSharedElementNames());
            this.mExitingTo = new ArrayList<>(this.mEnterActivityOptions.getSharedElementNames());
        }
        if (this.mIsEnterPostponed) {
            return;
        }
        startEnter();
    }

    public void postponeEnterTransition() {
        this.mIsEnterPostponed = true;
    }

    public void startPostponedEnterTransition() {
        if (this.mIsEnterPostponed) {
            this.mIsEnterPostponed = false;
            if (this.mEnterTransitionCoordinator != null) {
                startEnter();
            }
        }
    }

    private void startEnter() {
        if (this.mEnterTransitionCoordinator.isReturning()) {
            ArrayList<View> arrayList = this.mExitingToView;
            if (arrayList != null) {
                this.mEnterTransitionCoordinator.viewInstancesReady(this.mExitingFrom, this.mExitingTo, arrayList);
            } else {
                this.mEnterTransitionCoordinator.namedViewsReady(this.mExitingFrom, this.mExitingTo);
            }
        } else {
            this.mEnterTransitionCoordinator.namedViewsReady(null, null);
            this.mEnteringNames = this.mEnterTransitionCoordinator.getAllSharedElementNames();
        }
        this.mExitingFrom = null;
        this.mExitingTo = null;
        this.mExitingToView = null;
        this.mEnterActivityOptions = null;
    }

    public void onStop() {
        restoreExitedViews();
        EnterTransitionCoordinator enterTransitionCoordinator = this.mEnterTransitionCoordinator;
        if (enterTransitionCoordinator != null) {
            enterTransitionCoordinator.stop();
            this.mEnterTransitionCoordinator = null;
        }
        ExitTransitionCoordinator exitTransitionCoordinator = this.mReturnExitCoordinator;
        if (exitTransitionCoordinator != null) {
            exitTransitionCoordinator.stop();
            this.mReturnExitCoordinator = null;
        }
    }

    public void onResume(Activity activity, boolean z) {
        if (z || this.mEnterTransitionCoordinator == null) {
            restoreExitedViews();
            restoreReenteringViews();
        } else {
            activity.mHandler.postDelayed(new Runnable() { // from class: android.app.ActivityTransitionState.1
                @Override // java.lang.Runnable
                public void run() {
                    if (ActivityTransitionState.this.mEnterTransitionCoordinator == null || ActivityTransitionState.this.mEnterTransitionCoordinator.isWaitingForRemoteExit()) {
                        ActivityTransitionState.this.restoreExitedViews();
                        ActivityTransitionState.this.restoreReenteringViews();
                    }
                }
            }, 1000L);
        }
    }

    public void clear() {
        this.mEnteringNames = null;
        this.mExitingFrom = null;
        this.mExitingTo = null;
        this.mExitingToView = null;
        this.mCalledExitCoordinator = null;
        this.mEnterTransitionCoordinator = null;
        this.mEnterActivityOptions = null;
        this.mExitTransitionCoordinators = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restoreExitedViews() {
        ExitTransitionCoordinator exitTransitionCoordinator = this.mCalledExitCoordinator;
        if (exitTransitionCoordinator != null) {
            exitTransitionCoordinator.resetViews();
            this.mCalledExitCoordinator = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restoreReenteringViews() {
        EnterTransitionCoordinator enterTransitionCoordinator = this.mEnterTransitionCoordinator;
        if (enterTransitionCoordinator == null || !enterTransitionCoordinator.isReturning() || this.mEnterTransitionCoordinator.isCrossTask()) {
            return;
        }
        this.mEnterTransitionCoordinator.forceViewsToAppear();
        this.mExitingFrom = null;
        this.mExitingTo = null;
        this.mExitingToView = null;
    }

    public boolean startExitBackTransition(final Activity activity) {
        ViewGroup viewGroup;
        boolean z = false;
        if (this.mEnteringNames == null || this.mCalledExitCoordinator != null) {
            return false;
        }
        if (!this.mHasExited) {
            this.mHasExited = true;
            EnterTransitionCoordinator enterTransitionCoordinator = this.mEnterTransitionCoordinator;
            Transition transition = null;
            if (enterTransitionCoordinator != null) {
                Transition enterViewsTransition = enterTransitionCoordinator.getEnterViewsTransition();
                ViewGroup decor = this.mEnterTransitionCoordinator.getDecor();
                boolean zCancelEnter = this.mEnterTransitionCoordinator.cancelEnter();
                this.mEnterTransitionCoordinator = null;
                if (enterViewsTransition != null && decor != null) {
                    enterViewsTransition.pause(decor);
                }
                transition = enterViewsTransition;
                viewGroup = decor;
                z = zCancelEnter;
            } else {
                viewGroup = null;
            }
            this.mReturnExitCoordinator = new ExitTransitionCoordinator(activity, activity.getWindow(), activity.mEnterTransitionListener, this.mEnteringNames, null, null, true);
            if (transition != null && viewGroup != null) {
                transition.resume(viewGroup);
            }
            if (z && viewGroup != null) {
                OneShotPreDrawListener.add(viewGroup, new Runnable() { // from class: android.app.-$$Lambda$ActivityTransitionState$yioLR6wQWjZ9DcWK5bibElIbsXc
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$startExitBackTransition$0$ActivityTransitionState(activity);
                    }
                });
            } else {
                this.mReturnExitCoordinator.startExit(activity.mResultCode, activity.mResultData);
            }
        }
        return true;
    }

    public /* synthetic */ void lambda$startExitBackTransition$0$ActivityTransitionState(Activity activity) {
        ExitTransitionCoordinator exitTransitionCoordinator = this.mReturnExitCoordinator;
        if (exitTransitionCoordinator != null) {
            exitTransitionCoordinator.startExit(activity.mResultCode, activity.mResultData);
        }
    }

    public boolean isTransitionRunning() {
        EnterTransitionCoordinator enterTransitionCoordinator = this.mEnterTransitionCoordinator;
        if (enterTransitionCoordinator != null && enterTransitionCoordinator.isTransitionRunning()) {
            return true;
        }
        ExitTransitionCoordinator exitTransitionCoordinator = this.mCalledExitCoordinator;
        if (exitTransitionCoordinator != null && exitTransitionCoordinator.isTransitionRunning()) {
            return true;
        }
        ExitTransitionCoordinator exitTransitionCoordinator2 = this.mReturnExitCoordinator;
        return exitTransitionCoordinator2 != null && exitTransitionCoordinator2.isTransitionRunning();
    }

    public void startExitOutTransition(Activity activity, Bundle bundle) {
        this.mEnterTransitionCoordinator = null;
        if (!activity.getWindow().hasFeature(13) || this.mExitTransitionCoordinators == null) {
            return;
        }
        ActivityOptions activityOptions = new ActivityOptions(bundle);
        if (activityOptions.getAnimationType() == 5) {
            int iIndexOfKey = this.mExitTransitionCoordinators.indexOfKey(activityOptions.getExitCoordinatorKey());
            if (iIndexOfKey >= 0) {
                this.mCalledExitCoordinator = this.mExitTransitionCoordinators.valueAt(iIndexOfKey).get();
                this.mExitTransitionCoordinators.removeAt(iIndexOfKey);
                ExitTransitionCoordinator exitTransitionCoordinator = this.mCalledExitCoordinator;
                if (exitTransitionCoordinator != null) {
                    this.mExitingFrom = exitTransitionCoordinator.getAcceptedNames();
                    this.mExitingTo = this.mCalledExitCoordinator.getMappedNames();
                    this.mExitingToView = this.mCalledExitCoordinator.copyMappedViews();
                    this.mCalledExitCoordinator.startExit();
                }
            }
        }
    }
}
