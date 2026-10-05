package android.widget;

import android.view.MenuItem;
import com.android.internal.view.menu.MenuBuilder;

/* JADX INFO: loaded from: classes2.dex */
public interface MenuItemHoverListener {
    void onItemHoverEnter(MenuBuilder menuBuilder, MenuItem menuItem);

    void onItemHoverExit(MenuBuilder menuBuilder, MenuItem menuItem);
}
