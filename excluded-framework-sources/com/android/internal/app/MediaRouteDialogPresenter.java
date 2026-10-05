package com.android.internal.app;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.media.MediaRouter;
import android.util.Log;
import android.view.View;

/* JADX INFO: loaded from: classes3.dex */
public abstract class MediaRouteDialogPresenter {
    private static final String CHOOSER_FRAGMENT_TAG = "android.app.MediaRouteButton:MediaRouteChooserDialogFragment";
    private static final String CONTROLLER_FRAGMENT_TAG = "android.app.MediaRouteButton:MediaRouteControllerDialogFragment";
    private static final String TAG = "MediaRouter";

    public static DialogFragment showDialogFragment(Activity activity, int i, View.OnClickListener onClickListener) {
        MediaRouter mediaRouter = (MediaRouter) activity.getSystemService(Context.MEDIA_ROUTER_SERVICE);
        FragmentManager fragmentManager = activity.getFragmentManager();
        MediaRouter.RouteInfo selectedRoute = mediaRouter.getSelectedRoute();
        if (selectedRoute.isDefault() || !selectedRoute.matchesTypes(i)) {
            if (fragmentManager.findFragmentByTag(CHOOSER_FRAGMENT_TAG) != null) {
                Log.w(TAG, "showDialog(): Route chooser dialog already showing!");
                return null;
            }
            MediaRouteChooserDialogFragment mediaRouteChooserDialogFragment = new MediaRouteChooserDialogFragment();
            mediaRouteChooserDialogFragment.setRouteTypes(i);
            mediaRouteChooserDialogFragment.setExtendedSettingsClickListener(onClickListener);
            mediaRouteChooserDialogFragment.show(fragmentManager, CHOOSER_FRAGMENT_TAG);
            return mediaRouteChooserDialogFragment;
        }
        if (fragmentManager.findFragmentByTag(CONTROLLER_FRAGMENT_TAG) != null) {
            Log.w(TAG, "showDialog(): Route controller dialog already showing!");
            return null;
        }
        MediaRouteControllerDialogFragment mediaRouteControllerDialogFragment = new MediaRouteControllerDialogFragment();
        mediaRouteControllerDialogFragment.show(fragmentManager, CONTROLLER_FRAGMENT_TAG);
        return mediaRouteControllerDialogFragment;
    }

    public static Dialog createDialog(Context context, int i, View.OnClickListener onClickListener) {
        MediaRouter mediaRouter = (MediaRouter) context.getSystemService(Context.MEDIA_ROUTER_SERVICE);
        int i2 = MediaRouteChooserDialog.isLightTheme(context) ? 16974130 : 16974126;
        MediaRouter.RouteInfo selectedRoute = mediaRouter.getSelectedRoute();
        if (selectedRoute.isDefault() || !selectedRoute.matchesTypes(i)) {
            MediaRouteChooserDialog mediaRouteChooserDialog = new MediaRouteChooserDialog(context, i2);
            mediaRouteChooserDialog.setRouteTypes(i);
            mediaRouteChooserDialog.setExtendedSettingsClickListener(onClickListener);
            return mediaRouteChooserDialog;
        }
        return new MediaRouteControllerDialog(context, i2);
    }
}
