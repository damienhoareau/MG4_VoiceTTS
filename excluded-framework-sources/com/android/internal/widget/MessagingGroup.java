package com.android.internal.widget;

import android.app.Person;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Icon;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Pools;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import com.android.internal.R;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
@RemoteViews.RemoteView
public class MessagingGroup extends LinearLayout implements MessagingLinearLayout.MessagingChild {
    private static Pools.SimplePool<MessagingGroup> sInstancePool = new Pools.SynchronizedPool(10);
    private ArrayList<MessagingMessage> mAddedMessages;
    private Icon mAvatarIcon;
    private CharSequence mAvatarName;
    private String mAvatarSymbol;
    private ImageView mAvatarView;
    private Point mDisplaySize;
    private boolean mFirstLayout;
    private ViewGroup mImageContainer;
    private boolean mImagesAtEnd;
    private boolean mIsHidingAnimated;
    private MessagingImageMessage mIsolatedMessage;
    private int mLayoutColor;
    private MessagingLinearLayout mMessageContainer;
    private List<MessagingMessage> mMessages;
    private boolean mNeedsGeneratedAvatar;
    private Person mSender;
    private ImageFloatingTextView mSenderName;
    private ProgressBar mSendingSpinner;
    private View mSendingSpinnerContainer;
    private int mSendingTextColor;
    private int mTextColor;
    private boolean mTransformingImages;

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    public MessagingGroup(Context context) {
        super(context);
        this.mAvatarSymbol = "";
        this.mAvatarName = "";
        this.mAddedMessages = new ArrayList<>();
        this.mDisplaySize = new Point();
    }

    public MessagingGroup(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mAvatarSymbol = "";
        this.mAvatarName = "";
        this.mAddedMessages = new ArrayList<>();
        this.mDisplaySize = new Point();
    }

    public MessagingGroup(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mAvatarSymbol = "";
        this.mAvatarName = "";
        this.mAddedMessages = new ArrayList<>();
        this.mDisplaySize = new Point();
    }

    public MessagingGroup(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mAvatarSymbol = "";
        this.mAvatarName = "";
        this.mAddedMessages = new ArrayList<>();
        this.mDisplaySize = new Point();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mMessageContainer = (MessagingLinearLayout) findViewById(R.id.group_message_container);
        this.mSenderName = (ImageFloatingTextView) findViewById(R.id.message_name);
        this.mAvatarView = (ImageView) findViewById(R.id.message_icon);
        this.mImageContainer = (ViewGroup) findViewById(R.id.messaging_group_icon_container);
        this.mSendingSpinner = (ProgressBar) findViewById(R.id.messaging_group_sending_progress);
        this.mSendingSpinnerContainer = findViewById(R.id.messaging_group_sending_progress_container);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        this.mDisplaySize.x = displayMetrics.widthPixels;
        this.mDisplaySize.y = displayMetrics.heightPixels;
    }

    public void updateClipRect() {
        Rect rect;
        if (this.mSenderName.getVisibility() == 8 || this.mTransformingImages) {
            rect = null;
        } else {
            ViewGroup viewGroup = (ViewGroup) this.mSenderName.getParent();
            int distanceFromParent = (getDistanceFromParent(this.mSenderName, viewGroup) - getDistanceFromParent(this.mMessageContainer, viewGroup)) + this.mSenderName.getHeight();
            int iMax = Math.max(this.mDisplaySize.x, this.mDisplaySize.y);
            rect = new Rect(0, distanceFromParent, iMax, iMax);
        }
        this.mMessageContainer.setClipBounds(rect);
    }

    private int getDistanceFromParent(View view, ViewGroup viewGroup) {
        int top = 0;
        while (view != viewGroup) {
            top = (int) (top + view.getTop() + view.getTranslationY());
            view = (View) view.getParent();
        }
        return top;
    }

    public void setSender(Person person, CharSequence charSequence) {
        this.mSender = person;
        if (charSequence == null) {
            charSequence = person.getName();
        }
        this.mSenderName.setText(charSequence);
        boolean z = person.getIcon() == null;
        this.mNeedsGeneratedAvatar = z;
        if (!z) {
            setAvatar(person.getIcon());
        }
        this.mAvatarView.setVisibility(0);
        this.mSenderName.setVisibility(TextUtils.isEmpty(charSequence) ? 8 : 0);
    }

    public void setSending(boolean z) {
        int i = z ? 0 : 8;
        if (this.mSendingSpinnerContainer.getVisibility() != i) {
            this.mSendingSpinnerContainer.setVisibility(i);
            updateMessageColor();
        }
    }

    private int calculateSendingTextColor() {
        TypedValue typedValue = new TypedValue();
        this.mContext.getResources().getValue(R.dimen.notification_secondary_text_disabled_alpha, typedValue, true);
        return Color.valueOf(Color.red(this.mTextColor), Color.green(this.mTextColor), Color.blue(this.mTextColor), typedValue.getFloat()).toArgb();
    }

    public void setAvatar(Icon icon) {
        this.mAvatarIcon = icon;
        this.mAvatarView.setImageIcon(icon);
        this.mAvatarSymbol = "";
        this.mAvatarName = "";
    }

    static MessagingGroup createGroup(MessagingLinearLayout messagingLinearLayout) {
        MessagingGroup messagingGroupAcquire = sInstancePool.acquire();
        if (messagingGroupAcquire == null) {
            messagingGroupAcquire = (MessagingGroup) LayoutInflater.from(messagingLinearLayout.getContext()).inflate(R.layout.notification_template_messaging_group, (ViewGroup) messagingLinearLayout, false);
            messagingGroupAcquire.addOnLayoutChangeListener(MessagingLayout.MESSAGING_PROPERTY_ANIMATOR);
        }
        messagingLinearLayout.addView(messagingGroupAcquire);
        return messagingGroupAcquire;
    }

    public void removeMessage(final MessagingMessage messagingMessage) {
        final View view = messagingMessage.getView();
        boolean zIsShown = view.isShown();
        final ViewGroup viewGroup = (ViewGroup) view.getParent();
        if (viewGroup == null) {
            return;
        }
        viewGroup.removeView(view);
        Runnable runnable = new Runnable() { // from class: com.android.internal.widget.-$$Lambda$MessagingGroup$uEKViIlAuE6AYNmbbTgLGe5mU7I
            @Override // java.lang.Runnable
            public final void run() {
                MessagingGroup.lambda$removeMessage$0(viewGroup, view, messagingMessage);
            }
        };
        if (zIsShown && !MessagingLinearLayout.isGone(view)) {
            viewGroup.addTransientView(view, 0);
            performRemoveAnimation(view, runnable);
        } else {
            runnable.run();
        }
    }

    static /* synthetic */ void lambda$removeMessage$0(ViewGroup viewGroup, View view, MessagingMessage messagingMessage) {
        viewGroup.removeTransientView(view);
        messagingMessage.recycle();
    }

    public void recycle() {
        MessagingImageMessage messagingImageMessage = this.mIsolatedMessage;
        if (messagingImageMessage != null) {
            this.mImageContainer.removeView(messagingImageMessage);
        }
        for (int i = 0; i < this.mMessages.size(); i++) {
            MessagingMessage messagingMessage = this.mMessages.get(i);
            this.mMessageContainer.removeView(messagingMessage.getView());
            messagingMessage.recycle();
        }
        setAvatar(null);
        this.mAvatarView.setAlpha(1.0f);
        this.mAvatarView.setTranslationY(0.0f);
        this.mSenderName.setAlpha(1.0f);
        this.mSenderName.setTranslationY(0.0f);
        setAlpha(1.0f);
        this.mIsolatedMessage = null;
        this.mMessages = null;
        this.mAddedMessages.clear();
        this.mFirstLayout = true;
        MessagingPropertyAnimator.recycle(this);
        sInstancePool.release(this);
    }

    public void removeGroupAnimated(final Runnable runnable) {
        performRemoveAnimation(this, new Runnable() { // from class: com.android.internal.widget.-$$Lambda$MessagingGroup$QKnXYzCylYJqF8wEQG98SXlcu2M
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$removeGroupAnimated$1$MessagingGroup(runnable);
            }
        });
    }

    public /* synthetic */ void lambda$removeGroupAnimated$1$MessagingGroup(Runnable runnable) {
        setAlpha(1.0f);
        MessagingPropertyAnimator.setToLaidOutPosition(this);
        if (runnable != null) {
            runnable.run();
        }
    }

    public void performRemoveAnimation(View view, Runnable runnable) {
        performRemoveAnimation(view, -view.getHeight(), runnable);
    }

    private void performRemoveAnimation(View view, int i, Runnable runnable) {
        MessagingPropertyAnimator.startLocalTranslationTo(view, i, MessagingLayout.FAST_OUT_LINEAR_IN);
        MessagingPropertyAnimator.fadeOut(view, runnable);
    }

    public CharSequence getSenderName() {
        return this.mSenderName.getText();
    }

    public static void dropCache() {
        sInstancePool = new Pools.SynchronizedPool(10);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.internal.widget.MessagingLinearLayout.MessagingChild
    public int getMeasuredType() {
        if (this.mIsolatedMessage != null) {
            return 1;
        }
        boolean z = false;
        for (int childCount = this.mMessageContainer.getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = this.mMessageContainer.getChildAt(childCount);
            if (childAt.getVisibility() != 8 && (childAt instanceof MessagingLinearLayout.MessagingChild)) {
                int measuredType = ((MessagingLinearLayout.MessagingChild) childAt).getMeasuredType();
                if (((MessagingLinearLayout.LayoutParams) childAt.getLayoutParams()).hide || (measuredType == 2)) {
                    return z ? 1 : 2;
                }
                if (measuredType == 1) {
                    return 1;
                }
                z = true;
            }
        }
        return 0;
    }

    @Override // com.android.internal.widget.MessagingLinearLayout.MessagingChild
    public int getConsumedLines() {
        int iMax = 0;
        for (int i = 0; i < this.mMessageContainer.getChildCount(); i++) {
            KeyEvent.Callback childAt = this.mMessageContainer.getChildAt(i);
            if (childAt instanceof MessagingLinearLayout.MessagingChild) {
                iMax += ((MessagingLinearLayout.MessagingChild) childAt).getConsumedLines();
            }
        }
        if (this.mIsolatedMessage != null) {
            iMax = Math.max(iMax, 1);
        }
        return iMax + 1;
    }

    @Override // com.android.internal.widget.MessagingLinearLayout.MessagingChild
    public void setMaxDisplayedLines(int i) {
        this.mMessageContainer.setMaxDisplayedLines(i);
    }

    @Override // com.android.internal.widget.MessagingLinearLayout.MessagingChild
    public void hideAnimated() {
        setIsHidingAnimated(true);
        removeGroupAnimated(new Runnable() { // from class: com.android.internal.widget.-$$Lambda$MessagingGroup$buM2CBWR7uz4neT0lee-MKMDx5M
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$hideAnimated$2$MessagingGroup();
            }
        });
    }

    public /* synthetic */ void lambda$hideAnimated$2$MessagingGroup() {
        setIsHidingAnimated(false);
    }

    @Override // com.android.internal.widget.MessagingLinearLayout.MessagingChild
    public boolean isHidingAnimated() {
        return this.mIsHidingAnimated;
    }

    private void setIsHidingAnimated(boolean z) {
        ViewParent parent = getParent();
        this.mIsHidingAnimated = z;
        invalidate();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).invalidate();
        }
    }

    public Icon getAvatarSymbolIfMatching(CharSequence charSequence, String str, int i) {
        if (this.mAvatarName.equals(charSequence) && this.mAvatarSymbol.equals(str) && i == this.mLayoutColor) {
            return this.mAvatarIcon;
        }
        return null;
    }

    public void setCreatedAvatar(Icon icon, CharSequence charSequence, String str, int i) {
        if (this.mAvatarName.equals(charSequence) && this.mAvatarSymbol.equals(str) && i == this.mLayoutColor) {
            return;
        }
        setAvatar(icon);
        this.mAvatarSymbol = str;
        setLayoutColor(i);
        this.mAvatarName = charSequence;
    }

    public void setTextColors(int i, int i2) {
        this.mTextColor = i2;
        this.mSendingTextColor = calculateSendingTextColor();
        updateMessageColor();
        this.mSenderName.setTextColor(i);
    }

    public void setLayoutColor(int i) {
        if (i != this.mLayoutColor) {
            this.mLayoutColor = i;
            this.mSendingSpinner.setIndeterminateTintList(ColorStateList.valueOf(i));
        }
    }

    private void updateMessageColor() {
        if (this.mMessages != null) {
            int i = this.mSendingSpinnerContainer.getVisibility() == 0 ? this.mSendingTextColor : this.mTextColor;
            for (MessagingMessage messagingMessage : this.mMessages) {
                messagingMessage.setColor(messagingMessage.getMessage().isRemoteInputHistory() ? i : this.mTextColor);
            }
        }
    }

    public void setMessages(List<MessagingMessage> list) {
        MessagingImageMessage messagingImageMessage = null;
        int i = 0;
        for (int i2 = 0; i2 < list.size(); i2++) {
            MessagingMessage messagingMessage = list.get(i2);
            if (messagingMessage.getGroup() != this) {
                messagingMessage.setMessagingGroup(this);
                this.mAddedMessages.add(messagingMessage);
            }
            boolean z = messagingMessage instanceof MessagingImageMessage;
            if (this.mImagesAtEnd && z) {
                messagingImageMessage = (MessagingImageMessage) messagingMessage;
            } else {
                if (removeFromParentIfDifferent(messagingMessage, this.mMessageContainer)) {
                    ViewGroup.LayoutParams layoutParams = messagingMessage.getView().getLayoutParams();
                    if (layoutParams != null && !(layoutParams instanceof MessagingLinearLayout.LayoutParams)) {
                        messagingMessage.getView().setLayoutParams(this.mMessageContainer.generateDefaultLayoutParams());
                    }
                    this.mMessageContainer.addView(messagingMessage.getView(), i);
                }
                if (z) {
                    ((MessagingImageMessage) messagingMessage).setIsolated(false);
                }
                if (i != this.mMessageContainer.indexOfChild(messagingMessage.getView())) {
                    this.mMessageContainer.removeView(messagingMessage.getView());
                    this.mMessageContainer.addView(messagingMessage.getView(), i);
                }
                i++;
            }
        }
        if (messagingImageMessage != null) {
            if (removeFromParentIfDifferent(messagingImageMessage, this.mImageContainer)) {
                this.mImageContainer.removeAllViews();
                this.mImageContainer.addView(messagingImageMessage.getView());
            }
            messagingImageMessage.setIsolated(true);
        } else if (this.mIsolatedMessage != null) {
            this.mImageContainer.removeAllViews();
        }
        this.mIsolatedMessage = messagingImageMessage;
        updateImageContainerVisibility();
        this.mMessages = list;
        updateMessageColor();
    }

    private void updateImageContainerVisibility() {
        this.mImageContainer.setVisibility((this.mIsolatedMessage == null || !this.mImagesAtEnd) ? 8 : 0);
    }

    private boolean removeFromParentIfDifferent(MessagingMessage messagingMessage, ViewGroup viewGroup) {
        ViewParent parent = messagingMessage.getView().getParent();
        if (parent == viewGroup) {
            return false;
        }
        if (!(parent instanceof ViewGroup)) {
            return true;
        }
        ((ViewGroup) parent).removeView(messagingMessage.getView());
        return true;
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (!this.mAddedMessages.isEmpty()) {
            final boolean z2 = this.mFirstLayout;
            getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: com.android.internal.widget.MessagingGroup.1
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    for (MessagingMessage messagingMessage : MessagingGroup.this.mAddedMessages) {
                        if (messagingMessage.getView().isShown()) {
                            MessagingPropertyAnimator.fadeIn(messagingMessage.getView());
                            if (!z2) {
                                MessagingPropertyAnimator.startLocalTranslationFrom(messagingMessage.getView(), messagingMessage.getView().getHeight(), MessagingLayout.LINEAR_OUT_SLOW_IN);
                            }
                        }
                    }
                    MessagingGroup.this.mAddedMessages.clear();
                    MessagingGroup.this.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }
        this.mFirstLayout = false;
        updateClipRect();
    }

    public int calculateGroupCompatibility(MessagingGroup messagingGroup) {
        if (!TextUtils.equals(getSenderName(), messagingGroup.getSenderName())) {
            return 0;
        }
        int i = 1;
        for (int i2 = 0; i2 < this.mMessages.size() && i2 < messagingGroup.mMessages.size(); i2++) {
            List<MessagingMessage> list = this.mMessages;
            MessagingMessage messagingMessage = list.get((list.size() - 1) - i2);
            List<MessagingMessage> list2 = messagingGroup.mMessages;
            if (!messagingMessage.sameAs(list2.get((list2.size() - 1) - i2))) {
                return i;
            }
            i++;
        }
        return i;
    }

    public View getSenderView() {
        return this.mSenderName;
    }

    public View getAvatar() {
        return this.mAvatarView;
    }

    public MessagingLinearLayout getMessageContainer() {
        return this.mMessageContainer;
    }

    public MessagingImageMessage getIsolatedMessage() {
        return this.mIsolatedMessage;
    }

    public boolean needsGeneratedAvatar() {
        return this.mNeedsGeneratedAvatar;
    }

    public Person getSender() {
        return this.mSender;
    }

    public void setTransformingImages(boolean z) {
        this.mTransformingImages = z;
    }

    public void setDisplayImagesAtEnd(boolean z) {
        if (this.mImagesAtEnd != z) {
            this.mImagesAtEnd = z;
            updateImageContainerVisibility();
        }
    }

    public List<MessagingMessage> getMessages() {
        return this.mMessages;
    }
}
