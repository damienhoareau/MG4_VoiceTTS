package com.android.internal.app;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.android.internal.R;
import com.android.internal.widget.ResolverDrawerLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class AccessibilityButtonChooserActivity extends Activity {
    private static final String MAGNIFICATION_COMPONENT_ID = "com.android.server.accessibility.MagnificationController";
    private AccessibilityButtonTarget mMagnificationTarget = null;
    private List<AccessibilityButtonTarget> mTargets = null;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.accessibility_button_chooser);
        ResolverDrawerLayout resolverDrawerLayout = (ResolverDrawerLayout) findViewById(R.id.contentPanel);
        if (resolverDrawerLayout != null) {
            resolverDrawerLayout.setOnDismissedListener(new ResolverDrawerLayout.OnDismissedListener() { // from class: com.android.internal.app.-$$Lambda$EK3sgUmlvAVQupMeTV9feOrWuPE
                @Override // com.android.internal.widget.ResolverDrawerLayout.OnDismissedListener
                public final void onDismissed() {
                    this.f$0.finish();
                }
            });
        }
        if (TextUtils.isEmpty(Settings.Secure.getString(getContentResolver(), Settings.Secure.ACCESSIBILITY_BUTTON_TARGET_COMPONENT))) {
            ((TextView) findViewById(R.id.accessibility_button_prompt)).setVisibility(0);
        }
        this.mMagnificationTarget = new AccessibilityButtonTarget(this, MAGNIFICATION_COMPONENT_ID, R.string.accessibility_magnification_chooser_text, R.drawable.ic_accessibility_magnification);
        this.mTargets = getServiceAccessibilityButtonTargets(this);
        if (Settings.Secure.getInt(getContentResolver(), Settings.Secure.ACCESSIBILITY_DISPLAY_MAGNIFICATION_NAVBAR_ENABLED, 0) == 1) {
            this.mTargets.add(this.mMagnificationTarget);
        }
        if (this.mTargets.size() < 2) {
            finish();
        }
        GridView gridView = (GridView) findViewById(R.id.accessibility_button_chooser_grid);
        gridView.setAdapter((ListAdapter) new TargetAdapter());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.internal.app.-$$Lambda$AccessibilityButtonChooserActivity$VBT2N_0vKxB2VkOg6zxi5sAX6xc
            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                this.f$0.lambda$onCreate$0$AccessibilityButtonChooserActivity(adapterView, view, i, j);
            }
        });
    }

    public /* synthetic */ void lambda$onCreate$0$AccessibilityButtonChooserActivity(AdapterView adapterView, View view, int i, long j) {
        onTargetSelected(this.mTargets.get(i));
    }

    private static List<AccessibilityButtonTarget> getServiceAccessibilityButtonTargets(Context context) {
        List<AccessibilityServiceInfo> enabledAccessibilityServiceList = ((AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE)).getEnabledAccessibilityServiceList(-1);
        if (enabledAccessibilityServiceList == null) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList(enabledAccessibilityServiceList.size());
        for (AccessibilityServiceInfo accessibilityServiceInfo : enabledAccessibilityServiceList) {
            if ((accessibilityServiceInfo.flags & 256) != 0) {
                arrayList.add(new AccessibilityButtonTarget(context, accessibilityServiceInfo));
            }
        }
        return arrayList;
    }

    private void onTargetSelected(AccessibilityButtonTarget accessibilityButtonTarget) {
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ACCESSIBILITY_BUTTON_TARGET_COMPONENT, accessibilityButtonTarget.getId());
        finish();
    }

    private class TargetAdapter extends BaseAdapter {
        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return null;
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        private TargetAdapter() {
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return AccessibilityButtonChooserActivity.this.mTargets.size();
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View viewInflate = AccessibilityButtonChooserActivity.this.getLayoutInflater().inflate(R.layout.accessibility_button_chooser_item, viewGroup, false);
            AccessibilityButtonTarget accessibilityButtonTarget = (AccessibilityButtonTarget) AccessibilityButtonChooserActivity.this.mTargets.get(i);
            ImageView imageView = (ImageView) viewInflate.findViewById(R.id.accessibility_button_target_icon);
            TextView textView = (TextView) viewInflate.findViewById(R.id.accessibility_button_target_label);
            imageView.setImageDrawable(accessibilityButtonTarget.getDrawable());
            textView.setText(accessibilityButtonTarget.getLabel());
            return viewInflate;
        }
    }

    private static class AccessibilityButtonTarget {
        public Drawable mDrawable;
        public String mId;
        public CharSequence mLabel;

        public AccessibilityButtonTarget(Context context, AccessibilityServiceInfo accessibilityServiceInfo) {
            this.mId = accessibilityServiceInfo.getComponentName().flattenToString();
            this.mLabel = accessibilityServiceInfo.getResolveInfo().loadLabel(context.getPackageManager());
            this.mDrawable = accessibilityServiceInfo.getResolveInfo().loadIcon(context.getPackageManager());
        }

        public AccessibilityButtonTarget(Context context, String str, int i, int i2) {
            this.mId = str;
            this.mLabel = context.getText(i);
            this.mDrawable = context.getDrawable(i2);
        }

        public String getId() {
            return this.mId;
        }

        public CharSequence getLabel() {
            return this.mLabel;
        }

        public Drawable getDrawable() {
            return this.mDrawable;
        }
    }
}
