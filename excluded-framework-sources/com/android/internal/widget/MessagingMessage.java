package com.android.internal.widget;

import android.app.ActivityManager;
import android.app.Notification;
import android.view.View;
import java.util.Objects;

/* JADX INFO: loaded from: classes3.dex */
public interface MessagingMessage extends MessagingLinearLayout.MessagingChild {
    public static final String IMAGE_MIME_TYPE_PREFIX = "image/";

    MessagingMessageState getState();

    int getVisibility();

    default boolean hasOverlappingRendering() {
        return false;
    }

    default void setColor(int i) {
    }

    void setVisibility(int i);

    static MessagingMessage createMessage(MessagingLayout messagingLayout, Notification.MessagingStyle.Message message) {
        if (hasImage(message) && !ActivityManager.isLowRamDeviceStatic()) {
            return MessagingImageMessage.createMessage(messagingLayout, message);
        }
        return MessagingTextMessage.createMessage(messagingLayout, message);
    }

    static void dropCache() {
        MessagingTextMessage.dropCache();
        MessagingImageMessage.dropCache();
    }

    static boolean hasImage(Notification.MessagingStyle.Message message) {
        return (message.getDataUri() == null || message.getDataMimeType() == null || !message.getDataMimeType().startsWith(IMAGE_MIME_TYPE_PREFIX)) ? false : true;
    }

    default boolean setMessage(Notification.MessagingStyle.Message message) {
        getState().setMessage(message);
        return true;
    }

    default Notification.MessagingStyle.Message getMessage() {
        return getState().getMessage();
    }

    default boolean sameAs(Notification.MessagingStyle.Message message) {
        Notification.MessagingStyle.Message message2 = getMessage();
        if (Objects.equals(message.getText(), message2.getText()) && Objects.equals(message.getSender(), message2.getSender())) {
            return ((message.isRemoteInputHistory() != message2.isRemoteInputHistory()) || Objects.equals(Long.valueOf(message.getTimestamp()), Long.valueOf(message2.getTimestamp()))) && Objects.equals(message.getDataMimeType(), message2.getDataMimeType()) && Objects.equals(message.getDataUri(), message2.getDataUri());
        }
        return false;
    }

    default boolean sameAs(MessagingMessage messagingMessage) {
        return sameAs(messagingMessage.getMessage());
    }

    default void removeMessage() {
        getGroup().removeMessage(this);
    }

    default void setMessagingGroup(MessagingGroup messagingGroup) {
        getState().setGroup(messagingGroup);
    }

    default void setIsHistoric(boolean z) {
        getState().setIsHistoric(z);
    }

    default MessagingGroup getGroup() {
        return getState().getGroup();
    }

    default void setIsHidingAnimated(boolean z) {
        getState().setIsHidingAnimated(z);
    }

    @Override // com.android.internal.widget.MessagingLinearLayout.MessagingChild
    default boolean isHidingAnimated() {
        return getState().isHidingAnimated();
    }

    @Override // com.android.internal.widget.MessagingLinearLayout.MessagingChild
    default void hideAnimated() {
        setIsHidingAnimated(true);
        getGroup().performRemoveAnimation(getView(), new Runnable() { // from class: com.android.internal.widget.-$$Lambda$MessagingMessage$goi5oiwdlMBbUvfJzNl7fGbZ-K0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.setIsHidingAnimated(false);
            }
        });
    }

    default void recycle() {
        getState().recycle();
    }

    /* JADX WARN: Multi-variable type inference failed */
    default View getView() {
        return (View) this;
    }
}
