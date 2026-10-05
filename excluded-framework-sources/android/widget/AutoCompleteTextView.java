package android.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.R;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes2.dex */
public class AutoCompleteTextView extends EditText implements Filter.FilterListener {
    static final boolean DEBUG = false;
    static final int EXPAND_MAX = 3;
    static final String TAG = "AutoCompleteTextView";
    private ListAdapter mAdapter;
    private boolean mBlockCompletion;
    private int mDropDownAnchorId;
    private boolean mDropDownDismissedOnCompletion;
    private Filter mFilter;
    private int mHintResource;
    private CharSequence mHintText;
    private TextView mHintView;
    private AdapterView.OnItemClickListener mItemClickListener;
    private AdapterView.OnItemSelectedListener mItemSelectedListener;
    private int mLastKeyCode;
    private PopupDataSetObserver mObserver;
    private boolean mOpenBefore;
    private final PassThroughClickListener mPassThroughClickListener;
    private final ListPopupWindow mPopup;
    private boolean mPopupCanBeUpdated;
    private final Context mPopupContext;
    private int mThreshold;
    private Validator mValidator;

    public interface OnDismissListener {
        void onDismiss();
    }

    public interface Validator {
        CharSequence fixText(CharSequence charSequence);

        boolean isValid(CharSequence charSequence);
    }

    public AutoCompleteTextView(Context context) {
        this(context, null);
    }

    public AutoCompleteTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842859);
    }

    public AutoCompleteTextView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public AutoCompleteTextView(Context context, AttributeSet attributeSet, int i, int i2) {
        this(context, attributeSet, i, i2, null);
    }

    public AutoCompleteTextView(Context context, AttributeSet attributeSet, int i, int i2, Resources.Theme theme) {
        super(context, attributeSet, i, i2);
        this.mDropDownDismissedOnCompletion = true;
        this.mLastKeyCode = 0;
        this.mValidator = null;
        this.mPopupCanBeUpdated = true;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.AutoCompleteTextView, i, i2);
        if (theme != null) {
            this.mPopupContext = new ContextThemeWrapper(context, theme);
        } else {
            int resourceId = typedArrayObtainStyledAttributes.getResourceId(8, 0);
            if (resourceId != 0) {
                this.mPopupContext = new ContextThemeWrapper(context, resourceId);
            } else {
                this.mPopupContext = context;
            }
        }
        Context context2 = this.mPopupContext;
        TypedArray typedArrayObtainStyledAttributes2 = context2 != context ? context2.obtainStyledAttributes(attributeSet, R.styleable.AutoCompleteTextView, i, i2) : typedArrayObtainStyledAttributes;
        Drawable drawable = typedArrayObtainStyledAttributes2.getDrawable(3);
        int layoutDimension = typedArrayObtainStyledAttributes2.getLayoutDimension(5, -2);
        int layoutDimension2 = typedArrayObtainStyledAttributes2.getLayoutDimension(7, -2);
        int resourceId2 = typedArrayObtainStyledAttributes2.getResourceId(1, R.layout.simple_dropdown_hint);
        CharSequence text = typedArrayObtainStyledAttributes2.getText(0);
        if (typedArrayObtainStyledAttributes2 != typedArrayObtainStyledAttributes) {
            typedArrayObtainStyledAttributes2.recycle();
        }
        ListPopupWindow listPopupWindow = new ListPopupWindow(this.mPopupContext, attributeSet, i, i2);
        this.mPopup = listPopupWindow;
        listPopupWindow.setSoftInputMode(16);
        this.mPopup.setPromptPosition(1);
        this.mPopup.setListSelector(drawable);
        this.mPopup.setOnItemClickListener(new DropDownItemClickListener());
        this.mPopup.setWidth(layoutDimension);
        this.mPopup.setHeight(layoutDimension2);
        this.mHintResource = resourceId2;
        setCompletionHint(text);
        this.mDropDownAnchorId = typedArrayObtainStyledAttributes.getResourceId(6, -1);
        this.mThreshold = typedArrayObtainStyledAttributes.getInt(2, 2);
        typedArrayObtainStyledAttributes.recycle();
        int inputType = getInputType();
        if ((inputType & 15) == 1) {
            setRawInputType(inputType | 65536);
        }
        setFocusable(true);
        addTextChangedListener(new MyWatcher());
        PassThroughClickListener passThroughClickListener = new PassThroughClickListener();
        this.mPassThroughClickListener = passThroughClickListener;
        super.setOnClickListener(passThroughClickListener);
    }

    @Override // android.view.View
    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mPassThroughClickListener.mWrapped = onClickListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onClickImpl() {
        if (isPopupShowing()) {
            ensureImeVisible(true);
        }
    }

    public void setCompletionHint(CharSequence charSequence) {
        this.mHintText = charSequence;
        if (charSequence != null) {
            TextView textView = this.mHintView;
            if (textView == null) {
                TextView textView2 = (TextView) LayoutInflater.from(this.mPopupContext).inflate(this.mHintResource, (ViewGroup) null).findViewById(16908308);
                textView2.setText(this.mHintText);
                this.mHintView = textView2;
                this.mPopup.setPromptView(textView2);
                return;
            }
            textView.setText(charSequence);
            return;
        }
        this.mPopup.setPromptView(null);
        this.mHintView = null;
    }

    public CharSequence getCompletionHint() {
        return this.mHintText;
    }

    public int getDropDownWidth() {
        return this.mPopup.getWidth();
    }

    public void setDropDownWidth(int i) {
        this.mPopup.setWidth(i);
    }

    public int getDropDownHeight() {
        return this.mPopup.getHeight();
    }

    public void setDropDownHeight(int i) {
        this.mPopup.setHeight(i);
    }

    public int getDropDownAnchor() {
        return this.mDropDownAnchorId;
    }

    public void setDropDownAnchor(int i) {
        this.mDropDownAnchorId = i;
        this.mPopup.setAnchorView(null);
    }

    public Drawable getDropDownBackground() {
        return this.mPopup.getBackground();
    }

    public void setDropDownBackgroundDrawable(Drawable drawable) {
        this.mPopup.setBackgroundDrawable(drawable);
    }

    public void setDropDownBackgroundResource(int i) {
        this.mPopup.setBackgroundDrawable(getContext().getDrawable(i));
    }

    public void setDropDownVerticalOffset(int i) {
        this.mPopup.setVerticalOffset(i);
    }

    public int getDropDownVerticalOffset() {
        return this.mPopup.getVerticalOffset();
    }

    public void setDropDownHorizontalOffset(int i) {
        this.mPopup.setHorizontalOffset(i);
    }

    public int getDropDownHorizontalOffset() {
        return this.mPopup.getHorizontalOffset();
    }

    public void setDropDownAnimationStyle(int i) {
        this.mPopup.setAnimationStyle(i);
    }

    public int getDropDownAnimationStyle() {
        return this.mPopup.getAnimationStyle();
    }

    public boolean isDropDownAlwaysVisible() {
        return this.mPopup.isDropDownAlwaysVisible();
    }

    public void setDropDownAlwaysVisible(boolean z) {
        this.mPopup.setDropDownAlwaysVisible(z);
    }

    public boolean isDropDownDismissedOnCompletion() {
        return this.mDropDownDismissedOnCompletion;
    }

    public void setDropDownDismissedOnCompletion(boolean z) {
        this.mDropDownDismissedOnCompletion = z;
    }

    public int getThreshold() {
        return this.mThreshold;
    }

    public void setThreshold(int i) {
        if (i <= 0) {
            i = 1;
        }
        this.mThreshold = i;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.mItemClickListener = onItemClickListener;
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener) {
        this.mItemSelectedListener = onItemSelectedListener;
    }

    @Deprecated
    public AdapterView.OnItemClickListener getItemClickListener() {
        return this.mItemClickListener;
    }

    @Deprecated
    public AdapterView.OnItemSelectedListener getItemSelectedListener() {
        return this.mItemSelectedListener;
    }

    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return this.mItemClickListener;
    }

    public AdapterView.OnItemSelectedListener getOnItemSelectedListener() {
        return this.mItemSelectedListener;
    }

    public void setOnDismissListener(final OnDismissListener onDismissListener) {
        this.mPopup.setOnDismissListener(onDismissListener != null ? new PopupWindow.OnDismissListener() { // from class: android.widget.AutoCompleteTextView.1
            @Override // android.widget.PopupWindow.OnDismissListener
            public void onDismiss() {
                onDismissListener.onDismiss();
            }
        } : null);
    }

    public ListAdapter getAdapter() {
        return this.mAdapter;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T extends ListAdapter & Filterable> void setAdapter(T t) {
        PopupDataSetObserver popupDataSetObserver = this.mObserver;
        if (popupDataSetObserver == null) {
            this.mObserver = new PopupDataSetObserver();
        } else {
            ListAdapter listAdapter = this.mAdapter;
            if (listAdapter != null) {
                listAdapter.unregisterDataSetObserver(popupDataSetObserver);
            }
        }
        this.mAdapter = t;
        if (t != 0) {
            this.mFilter = t.getFilter();
            t.registerDataSetObserver(this.mObserver);
        } else {
            this.mFilter = null;
        }
        this.mPopup.setAdapter(this.mAdapter);
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onKeyPreIme(int i, KeyEvent keyEvent) {
        if (i == 4 && isPopupShowing() && !this.mPopup.isDropDownAlwaysVisible()) {
            if (keyEvent.getAction() == 0 && keyEvent.getRepeatCount() == 0) {
                KeyEvent.DispatcherState keyDispatcherState = getKeyDispatcherState();
                if (keyDispatcherState != null) {
                    keyDispatcherState.startTracking(keyEvent, this);
                }
                return true;
            }
            if (keyEvent.getAction() == 1) {
                KeyEvent.DispatcherState keyDispatcherState2 = getKeyDispatcherState();
                if (keyDispatcherState2 != null) {
                    keyDispatcherState2.handleUpEvent(keyEvent);
                }
                if (keyEvent.isTracking() && !keyEvent.isCanceled()) {
                    dismissDropDown();
                    return true;
                }
            }
        }
        return super.onKeyPreIme(i, keyEvent);
    }

    @Override // android.widget.TextView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (this.mPopup.onKeyUp(i, keyEvent) && (i == 23 || i == 61 || i == 66)) {
            if (keyEvent.hasNoModifiers()) {
                performCompletion();
            }
            return true;
        }
        if (isPopupShowing() && i == 61 && keyEvent.hasNoModifiers()) {
            performCompletion();
            return true;
        }
        return super.onKeyUp(i, keyEvent);
    }

    @Override // android.widget.TextView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (this.mPopup.onKeyDown(i, keyEvent)) {
            return true;
        }
        if (!isPopupShowing() && i == 20 && keyEvent.hasNoModifiers()) {
            performValidation();
        }
        if (isPopupShowing() && i == 61 && keyEvent.hasNoModifiers()) {
            return true;
        }
        this.mLastKeyCode = i;
        boolean zOnKeyDown = super.onKeyDown(i, keyEvent);
        this.mLastKeyCode = 0;
        if (zOnKeyDown && isPopupShowing()) {
            clearListSelection();
        }
        return zOnKeyDown;
    }

    public boolean enoughToFilter() {
        return getText().length() >= this.mThreshold;
    }

    private class MyWatcher implements TextWatcher {
        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        private MyWatcher() {
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
            AutoCompleteTextView.this.doAfterTextChanged();
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            AutoCompleteTextView.this.doBeforeTextChanged();
        }
    }

    void doBeforeTextChanged() {
        if (this.mBlockCompletion) {
            return;
        }
        this.mOpenBefore = isPopupShowing();
    }

    void doAfterTextChanged() {
        if (this.mBlockCompletion) {
            return;
        }
        if (!this.mOpenBefore || isPopupShowing()) {
            if (enoughToFilter()) {
                if (this.mFilter != null) {
                    this.mPopupCanBeUpdated = true;
                    performFiltering(getText(), this.mLastKeyCode);
                    return;
                }
                return;
            }
            if (!this.mPopup.isDropDownAlwaysVisible()) {
                dismissDropDown();
            }
            Filter filter = this.mFilter;
            if (filter != null) {
                filter.filter(null);
            }
        }
    }

    public boolean isPopupShowing() {
        return this.mPopup.isShowing();
    }

    protected CharSequence convertSelectionToString(Object obj) {
        return this.mFilter.convertResultToString(obj);
    }

    public void clearListSelection() {
        this.mPopup.clearListSelection();
    }

    public void setListSelection(int i) {
        this.mPopup.setSelection(i);
    }

    public int getListSelection() {
        return this.mPopup.getSelectedItemPosition();
    }

    protected void performFiltering(CharSequence charSequence, int i) {
        this.mFilter.filter(charSequence, this);
    }

    public void performCompletion() {
        performCompletion(null, -1, -1L);
    }

    @Override // android.widget.TextView
    public void onCommitCompletion(CompletionInfo completionInfo) {
        if (isPopupShowing()) {
            this.mPopup.performItemClick(completionInfo.getPosition());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performCompletion(View view, int i, long j) {
        Object item;
        if (isPopupShowing()) {
            if (i < 0) {
                item = this.mPopup.getSelectedItem();
            } else {
                item = this.mAdapter.getItem(i);
            }
            if (item == null) {
                Log.w(TAG, "performCompletion: no selected item");
                return;
            }
            this.mBlockCompletion = true;
            replaceText(convertSelectionToString(item));
            this.mBlockCompletion = false;
            if (this.mItemClickListener != null) {
                ListPopupWindow listPopupWindow = this.mPopup;
                if (view == null || i < 0) {
                    view = listPopupWindow.getSelectedView();
                    i = listPopupWindow.getSelectedItemPosition();
                    j = listPopupWindow.getSelectedItemId();
                }
                this.mItemClickListener.onItemClick(listPopupWindow.getListView(), view, i, j);
            }
        }
        if (!this.mDropDownDismissedOnCompletion || this.mPopup.isDropDownAlwaysVisible()) {
            return;
        }
        dismissDropDown();
    }

    public boolean isPerformingCompletion() {
        return this.mBlockCompletion;
    }

    public void setText(CharSequence charSequence, boolean z) {
        if (z) {
            setText(charSequence);
            return;
        }
        this.mBlockCompletion = true;
        setText(charSequence);
        this.mBlockCompletion = false;
    }

    protected void replaceText(CharSequence charSequence) {
        clearComposingText();
        setText(charSequence);
        Editable text = getText();
        Selection.setSelection(text, text.length());
    }

    @Override // android.widget.Filter.FilterListener
    public void onFilterComplete(int i) {
        updateDropDownForFilter(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDropDownForFilter(int i) {
        if (getWindowVisibility() == 8) {
            return;
        }
        boolean zIsDropDownAlwaysVisible = this.mPopup.isDropDownAlwaysVisible();
        boolean zEnoughToFilter = enoughToFilter();
        if ((i > 0 || zIsDropDownAlwaysVisible) && zEnoughToFilter) {
            if (hasFocus() && hasWindowFocus() && this.mPopupCanBeUpdated) {
                showDropDown();
                return;
            }
            return;
        }
        if (zIsDropDownAlwaysVisible || !isPopupShowing()) {
            return;
        }
        dismissDropDown();
        this.mPopupCanBeUpdated = true;
    }

    @Override // android.widget.TextView, android.view.View
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (z || this.mPopup.isDropDownAlwaysVisible()) {
            return;
        }
        dismissDropDown();
    }

    @Override // android.view.View
    protected void onDisplayHint(int i) {
        super.onDisplayHint(i);
        if (i == 4 && !this.mPopup.isDropDownAlwaysVisible()) {
            dismissDropDown();
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected void onFocusChanged(boolean z, int i, Rect rect) {
        super.onFocusChanged(z, i, rect);
        if (isTemporarilyDetached()) {
            return;
        }
        if (!z) {
            performValidation();
        }
        if (z || this.mPopup.isDropDownAlwaysVisible()) {
            return;
        }
        dismissDropDown();
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        dismissDropDown();
        super.onDetachedFromWindow();
    }

    public void dismissDropDown() {
        InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
        if (inputMethodManagerPeekInstance != null) {
            inputMethodManagerPeekInstance.displayCompletions(this, null);
        }
        this.mPopup.dismiss();
        this.mPopupCanBeUpdated = false;
    }

    @Override // android.widget.TextView, android.view.View
    protected boolean setFrame(int i, int i2, int i3, int i4) {
        boolean frame = super.setFrame(i, i2, i3, i4);
        if (isPopupShowing()) {
            showDropDown();
        }
        return frame;
    }

    public void showDropDownAfterLayout() {
        this.mPopup.postShow();
    }

    public void ensureImeVisible(boolean z) {
        this.mPopup.setInputMethodMode(z ? 1 : 2);
        if (this.mPopup.isDropDownAlwaysVisible() || (this.mFilter != null && enoughToFilter())) {
            showDropDown();
        }
    }

    public boolean isInputMethodNotNeeded() {
        return this.mPopup.getInputMethodMode() == 2;
    }

    public void showDropDown() {
        buildImeCompletions();
        if (this.mPopup.getAnchorView() == null) {
            if (this.mDropDownAnchorId != -1) {
                this.mPopup.setAnchorView(getRootView().findViewById(this.mDropDownAnchorId));
            } else {
                this.mPopup.setAnchorView(this);
            }
        }
        if (!isPopupShowing()) {
            this.mPopup.setInputMethodMode(1);
            this.mPopup.setListItemExpandMax(3);
        }
        this.mPopup.show();
        this.mPopup.getListView().setOverScrollMode(0);
    }

    public void setForceIgnoreOutsideTouch(boolean z) {
        this.mPopup.setForceIgnoreOutsideTouch(z);
    }

    private void buildImeCompletions() {
        InputMethodManager inputMethodManagerPeekInstance;
        ListAdapter listAdapter = this.mAdapter;
        if (listAdapter == null || (inputMethodManagerPeekInstance = InputMethodManager.peekInstance()) == null) {
            return;
        }
        int iMin = Math.min(listAdapter.getCount(), 20);
        CompletionInfo[] completionInfoArr = new CompletionInfo[iMin];
        int i = 0;
        for (int i2 = 0; i2 < iMin; i2++) {
            if (listAdapter.isEnabled(i2)) {
                completionInfoArr[i] = new CompletionInfo(listAdapter.getItemId(i2), i, convertSelectionToString(listAdapter.getItem(i2)));
                i++;
            }
        }
        if (i != iMin) {
            CompletionInfo[] completionInfoArr2 = new CompletionInfo[i];
            System.arraycopy(completionInfoArr, 0, completionInfoArr2, 0, i);
            completionInfoArr = completionInfoArr2;
        }
        inputMethodManagerPeekInstance.displayCompletions(this, completionInfoArr);
    }

    public void setValidator(Validator validator) {
        this.mValidator = validator;
    }

    public Validator getValidator() {
        return this.mValidator;
    }

    public void performValidation() {
        if (this.mValidator == null) {
            return;
        }
        Editable text = getText();
        if (TextUtils.isEmpty(text) || this.mValidator.isValid(text)) {
            return;
        }
        setText(this.mValidator.fixText(text));
    }

    protected Filter getFilter() {
        return this.mFilter;
    }

    private class DropDownItemClickListener implements AdapterView.OnItemClickListener {
        private DropDownItemClickListener() {
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView adapterView, View view, int i, long j) {
            AutoCompleteTextView.this.performCompletion(view, i, j);
        }
    }

    private class PassThroughClickListener implements View.OnClickListener {
        private View.OnClickListener mWrapped;

        private PassThroughClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            AutoCompleteTextView.this.onClickImpl();
            View.OnClickListener onClickListener = this.mWrapped;
            if (onClickListener != null) {
                onClickListener.onClick(view);
            }
        }
    }

    private static class PopupDataSetObserver extends DataSetObserver {
        private final WeakReference<AutoCompleteTextView> mViewReference;
        private final Runnable updateRunnable;

        private PopupDataSetObserver(AutoCompleteTextView autoCompleteTextView) {
            this.updateRunnable = new Runnable() { // from class: android.widget.AutoCompleteTextView.PopupDataSetObserver.1
                @Override // java.lang.Runnable
                public void run() {
                    ListAdapter listAdapter;
                    AutoCompleteTextView autoCompleteTextView2 = (AutoCompleteTextView) PopupDataSetObserver.this.mViewReference.get();
                    if (autoCompleteTextView2 == null || (listAdapter = autoCompleteTextView2.mAdapter) == null) {
                        return;
                    }
                    autoCompleteTextView2.updateDropDownForFilter(listAdapter.getCount());
                }
            };
            this.mViewReference = new WeakReference<>(autoCompleteTextView);
        }

        @Override // android.database.DataSetObserver
        public void onChanged() {
            AutoCompleteTextView autoCompleteTextView = this.mViewReference.get();
            if (autoCompleteTextView == null || autoCompleteTextView.mAdapter == null) {
                return;
            }
            autoCompleteTextView.post(this.updateRunnable);
        }
    }
}
