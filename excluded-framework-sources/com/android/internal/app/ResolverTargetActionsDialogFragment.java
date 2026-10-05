package com.android.internal.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import com.android.internal.R;

/* JADX INFO: loaded from: classes3.dex */
public class ResolverTargetActionsDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    private static final int APP_INFO_INDEX = 1;
    private static final String NAME_KEY = "componentName";
    private static final String PINNED_KEY = "pinned";
    private static final String TITLE_KEY = "title";
    private static final int TOGGLE_PIN_INDEX = 0;

    public ResolverTargetActionsDialogFragment() {
    }

    public ResolverTargetActionsDialogFragment(CharSequence charSequence, ComponentName componentName, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence("title", charSequence);
        bundle.putParcelable(NAME_KEY, componentName);
        bundle.putBoolean("pinned", z);
        setArguments(bundle);
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Bundle arguments = getArguments();
        return new AlertDialog.Builder(getContext()).setCancelable(true).setItems(arguments.getBoolean("pinned", false) ? R.array.resolver_target_actions_unpin : R.array.resolver_target_actions_pin, this).setTitle(arguments.getCharSequence("title")).create();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        ComponentName componentName = (ComponentName) getArguments().getParcelable(NAME_KEY);
        if (i == 0) {
            SharedPreferences pinnedSharedPrefs = ChooserActivity.getPinnedSharedPrefs(getContext());
            String strFlattenToString = componentName.flattenToString();
            if (pinnedSharedPrefs.getBoolean(componentName.flattenToString(), false)) {
                pinnedSharedPrefs.edit().remove(strFlattenToString).apply();
            } else {
                pinnedSharedPrefs.edit().putBoolean(strFlattenToString, true).apply();
            }
            getActivity().recreate();
        } else if (i == 1) {
            startActivity(new Intent().setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", componentName.getPackageName(), null)).addFlags(524288));
        }
        dismiss();
    }
}
