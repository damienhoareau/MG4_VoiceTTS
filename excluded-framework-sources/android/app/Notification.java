package android.app;

import android.Manifest;
import android.annotation.SystemApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.AudioAttributes;
import android.media.PlayerBase;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.BadParcelableException;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.text.BidiFormatter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.TextAppearanceSpan;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseArray;
import android.util.proto.ProtoOutputStream;
import android.widget.RemoteViews;
import com.android.internal.R;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.NotificationColorUtil;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class Notification implements Parcelable {
    public static final AudioAttributes AUDIO_ATTRIBUTES_DEFAULT;
    public static final int BADGE_ICON_LARGE = 2;
    public static final int BADGE_ICON_NONE = 0;
    public static final int BADGE_ICON_SMALL = 1;
    public static final String CATEGORY_ALARM = "alarm";
    public static final String CATEGORY_CALL = "call";

    @SystemApi
    public static final String CATEGORY_CAR_EMERGENCY = "car_emergency";

    @SystemApi
    public static final String CATEGORY_CAR_INFORMATION = "car_information";

    @SystemApi
    public static final String CATEGORY_CAR_WARNING = "car_warning";
    public static final String CATEGORY_EMAIL = "email";
    public static final String CATEGORY_ERROR = "err";
    public static final String CATEGORY_EVENT = "event";
    public static final String CATEGORY_MESSAGE = "msg";
    public static final String CATEGORY_NAVIGATION = "navigation";
    public static final String CATEGORY_PROGRESS = "progress";
    public static final String CATEGORY_PROMO = "promo";
    public static final String CATEGORY_RECOMMENDATION = "recommendation";
    public static final String CATEGORY_REMINDER = "reminder";
    public static final String CATEGORY_SERVICE = "service";
    public static final String CATEGORY_SOCIAL = "social";
    public static final String CATEGORY_STATUS = "status";
    public static final String CATEGORY_SYSTEM = "sys";
    public static final String CATEGORY_TRANSPORT = "transport";
    public static final int COLOR_DEFAULT = 0;
    public static final int COLOR_INVALID = 1;
    public static final Parcelable.Creator<Notification> CREATOR;
    public static final int DEFAULT_ALL = -1;
    public static final int DEFAULT_LIGHTS = 4;
    public static final int DEFAULT_SOUND = 1;
    public static final int DEFAULT_VIBRATE = 2;

    @SystemApi
    public static final String EXTRA_ALLOW_DURING_SETUP = "android.allowDuringSetup";
    public static final String EXTRA_AUDIO_CONTENTS_URI = "android.audioContents";
    public static final String EXTRA_BACKGROUND_IMAGE_URI = "android.backgroundImageUri";
    public static final String EXTRA_BIG_TEXT = "android.bigText";
    public static final String EXTRA_BUILDER_APPLICATION_INFO = "android.appInfo";
    public static final String EXTRA_CHANNEL_GROUP_ID = "android.intent.extra.CHANNEL_GROUP_ID";
    public static final String EXTRA_CHANNEL_ID = "android.intent.extra.CHANNEL_ID";
    public static final String EXTRA_CHRONOMETER_COUNT_DOWN = "android.chronometerCountDown";
    public static final String EXTRA_COLORIZED = "android.colorized";
    public static final String EXTRA_COMPACT_ACTIONS = "android.compactActions";
    public static final String EXTRA_CONTAINS_CUSTOM_VIEW = "android.contains.customView";
    public static final String EXTRA_CONVERSATION_TITLE = "android.conversationTitle";
    public static final String EXTRA_FOREGROUND_APPS = "android.foregroundApps";
    public static final String EXTRA_HIDE_SMART_REPLIES = "android.hideSmartReplies";
    public static final String EXTRA_HISTORIC_MESSAGES = "android.messages.historic";
    public static final String EXTRA_INFO_TEXT = "android.infoText";
    public static final String EXTRA_IS_GROUP_CONVERSATION = "android.isGroupConversation";

    @Deprecated
    public static final String EXTRA_LARGE_ICON = "android.largeIcon";
    public static final String EXTRA_LARGE_ICON_BIG = "android.largeIcon.big";
    public static final String EXTRA_MEDIA_SESSION = "android.mediaSession";
    public static final String EXTRA_MESSAGES = "android.messages";
    public static final String EXTRA_MESSAGING_PERSON = "android.messagingUser";
    public static final String EXTRA_NOTIFICATION_ID = "android.intent.extra.NOTIFICATION_ID";
    public static final String EXTRA_NOTIFICATION_TAG = "android.intent.extra.NOTIFICATION_TAG";
    public static final String EXTRA_PEOPLE = "android.people";
    public static final String EXTRA_PEOPLE_LIST = "android.people.list";
    public static final String EXTRA_PICTURE = "android.picture";
    public static final String EXTRA_PROGRESS = "android.progress";
    public static final String EXTRA_PROGRESS_INDETERMINATE = "android.progressIndeterminate";
    public static final String EXTRA_PROGRESS_MAX = "android.progressMax";
    public static final String EXTRA_REDUCED_IMAGES = "android.reduced.images";
    public static final String EXTRA_REMOTE_INPUT_DRAFT = "android.remoteInputDraft";
    public static final String EXTRA_REMOTE_INPUT_HISTORY = "android.remoteInputHistory";
    public static final String EXTRA_SELF_DISPLAY_NAME = "android.selfDisplayName";
    public static final String EXTRA_SHOW_CHRONOMETER = "android.showChronometer";
    public static final String EXTRA_SHOW_REMOTE_INPUT_SPINNER = "android.remoteInputSpinner";
    public static final String EXTRA_SHOW_WHEN = "android.showWhen";

    @Deprecated
    public static final String EXTRA_SMALL_ICON = "android.icon";

    @SystemApi
    public static final String EXTRA_SUBSTITUTE_APP_NAME = "android.substName";
    public static final String EXTRA_SUB_TEXT = "android.subText";
    public static final String EXTRA_SUMMARY_TEXT = "android.summaryText";
    public static final String EXTRA_TEMPLATE = "android.template";
    public static final String EXTRA_TEXT = "android.text";
    public static final String EXTRA_TEXT_LINES = "android.textLines";
    public static final String EXTRA_TITLE = "android.title";
    public static final String EXTRA_TITLE_BIG = "android.title.big";

    @SystemApi
    public static final int FLAG_AUTOGROUP_SUMMARY = 1024;
    public static final int FLAG_AUTO_CANCEL = 16;
    public static final int FLAG_CAN_COLORIZE = 2048;
    public static final int FLAG_FOREGROUND_SERVICE = 64;
    public static final int FLAG_GROUP_SUMMARY = 512;

    @Deprecated
    public static final int FLAG_HIGH_PRIORITY = 128;
    public static final int FLAG_INSISTENT = 4;
    public static final int FLAG_LOCAL_ONLY = 256;
    public static final int FLAG_NO_CLEAR = 32;
    public static final int FLAG_ONGOING_EVENT = 2;
    public static final int FLAG_ONLY_ALERT_ONCE = 8;

    @Deprecated
    public static final int FLAG_SHOW_LIGHTS = 1;
    public static final int GROUP_ALERT_ALL = 0;
    public static final int GROUP_ALERT_CHILDREN = 2;
    public static final int GROUP_ALERT_SUMMARY = 1;
    public static final String INTENT_CATEGORY_NOTIFICATION_PREFERENCES = "android.intent.category.NOTIFICATION_PREFERENCES";
    private static final int MAX_CHARSEQUENCE_LENGTH = 5120;
    private static final int MAX_REPLY_HISTORY = 5;

    @Deprecated
    public static final int PRIORITY_DEFAULT = 0;

    @Deprecated
    public static final int PRIORITY_HIGH = 1;

    @Deprecated
    public static final int PRIORITY_LOW = -1;

    @Deprecated
    public static final int PRIORITY_MAX = 2;

    @Deprecated
    public static final int PRIORITY_MIN = -2;
    private static final ArraySet<Integer> STANDARD_LAYOUTS;

    @Deprecated
    public static final int STREAM_DEFAULT = -1;
    private static final String TAG = "Notification";
    public static final int VISIBILITY_PRIVATE = 0;
    public static final int VISIBILITY_PUBLIC = 1;
    public static final int VISIBILITY_SECRET = -1;
    public static IBinder processWhitelistToken;
    public Action[] actions;
    public ArraySet<PendingIntent> allPendingIntents;

    @Deprecated
    public AudioAttributes audioAttributes;

    @Deprecated
    public int audioStreamType;

    @Deprecated
    public RemoteViews bigContentView;
    public String category;
    public int color;
    public PendingIntent contentIntent;

    @Deprecated
    public RemoteViews contentView;
    private long creationTime;

    @Deprecated
    public int defaults;
    public PendingIntent deleteIntent;
    public Bundle extras;
    public int flags;
    public PendingIntent fullScreenIntent;

    @Deprecated
    public RemoteViews headsUpContentView;

    @Deprecated
    public int icon;
    public int iconLevel;

    @Deprecated
    public Bitmap largeIcon;

    @Deprecated
    public int ledARGB;

    @Deprecated
    public int ledOffMS;

    @Deprecated
    public int ledOnMS;
    private int mBadgeIcon;
    private String mChannelId;
    private int mGroupAlertBehavior;
    private String mGroupKey;
    private Icon mLargeIcon;
    private CharSequence mSettingsText;
    private String mShortcutId;
    private Icon mSmallIcon;
    private String mSortKey;
    private long mTimeout;
    private boolean mUsesStandardHeader;
    private IBinder mWhitelistToken;
    public int number;

    @Deprecated
    public int priority;
    public Notification publicVersion;

    @Deprecated
    public Uri sound;
    public CharSequence tickerText;

    @Deprecated
    public RemoteViews tickerView;

    @Deprecated
    public long[] vibrate;
    public int visibility;
    public long when;

    public interface Extender {
        Builder extend(Builder builder);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface GroupAlertBehavior {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Priority {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Visibility {
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    static {
        ArraySet<Integer> arraySet = new ArraySet<>();
        STANDARD_LAYOUTS = arraySet;
        arraySet.add(Integer.valueOf(R.layout.notification_template_material_base));
        STANDARD_LAYOUTS.add(Integer.valueOf(R.layout.notification_template_material_big_base));
        STANDARD_LAYOUTS.add(Integer.valueOf(R.layout.notification_template_material_big_picture));
        STANDARD_LAYOUTS.add(Integer.valueOf(R.layout.notification_template_material_big_text));
        STANDARD_LAYOUTS.add(Integer.valueOf(R.layout.notification_template_material_inbox));
        STANDARD_LAYOUTS.add(Integer.valueOf(R.layout.notification_template_material_messaging));
        STANDARD_LAYOUTS.add(Integer.valueOf(R.layout.notification_template_material_media));
        STANDARD_LAYOUTS.add(Integer.valueOf(R.layout.notification_template_material_big_media));
        STANDARD_LAYOUTS.add(Integer.valueOf(R.layout.notification_template_ambient_header));
        STANDARD_LAYOUTS.add(Integer.valueOf(R.layout.notification_template_header));
        STANDARD_LAYOUTS.add(Integer.valueOf(R.layout.notification_template_material_ambient));
        AUDIO_ATTRIBUTES_DEFAULT = new AudioAttributes.Builder().setContentType(4).setUsage(5).build();
        CREATOR = new Parcelable.Creator<Notification>() { // from class: android.app.Notification.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Notification createFromParcel(Parcel parcel) {
                return new Notification(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Notification[] newArray(int i) {
                return new Notification[i];
            }
        };
    }

    public String getGroup() {
        return this.mGroupKey;
    }

    public String getSortKey() {
        return this.mSortKey;
    }

    public static class Action implements Parcelable {
        public static final Parcelable.Creator<Action> CREATOR = new Parcelable.Creator<Action>() { // from class: android.app.Notification.Action.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Action createFromParcel(Parcel parcel) {
                return new Action(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public Action[] newArray(int i) {
                return new Action[i];
            }
        };
        private static final String EXTRA_DATA_ONLY_INPUTS = "android.extra.DATA_ONLY_INPUTS";
        public static final int SEMANTIC_ACTION_ARCHIVE = 5;
        public static final int SEMANTIC_ACTION_CALL = 10;
        public static final int SEMANTIC_ACTION_DELETE = 4;
        public static final int SEMANTIC_ACTION_MARK_AS_READ = 2;
        public static final int SEMANTIC_ACTION_MARK_AS_UNREAD = 3;
        public static final int SEMANTIC_ACTION_MUTE = 6;
        public static final int SEMANTIC_ACTION_NONE = 0;
        public static final int SEMANTIC_ACTION_REPLY = 1;
        public static final int SEMANTIC_ACTION_THUMBS_DOWN = 9;
        public static final int SEMANTIC_ACTION_THUMBS_UP = 8;
        public static final int SEMANTIC_ACTION_UNMUTE = 7;
        public PendingIntent actionIntent;

        @Deprecated
        public int icon;
        private boolean mAllowGeneratedReplies;
        private final Bundle mExtras;
        private Icon mIcon;
        private final RemoteInput[] mRemoteInputs;
        private final int mSemanticAction;
        public CharSequence title;

        public interface Extender {
            Builder extend(Builder builder);
        }

        @Retention(RetentionPolicy.SOURCE)
        public @interface SemanticAction {
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        private Action(Parcel parcel) {
            this.mAllowGeneratedReplies = true;
            if (parcel.readInt() != 0) {
                Icon iconCreateFromParcel = Icon.CREATOR.createFromParcel(parcel);
                this.mIcon = iconCreateFromParcel;
                if (iconCreateFromParcel.getType() == 2) {
                    this.icon = this.mIcon.getResId();
                }
            }
            this.title = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
            if (parcel.readInt() == 1) {
                this.actionIntent = PendingIntent.CREATOR.createFromParcel(parcel);
            }
            this.mExtras = Bundle.setDefusable(parcel.readBundle(), true);
            this.mRemoteInputs = (RemoteInput[]) parcel.createTypedArray(RemoteInput.CREATOR);
            this.mAllowGeneratedReplies = parcel.readInt() == 1;
            this.mSemanticAction = parcel.readInt();
        }

        @Deprecated
        public Action(int i, CharSequence charSequence, PendingIntent pendingIntent) {
            this(Icon.createWithResource("", i), charSequence, pendingIntent, new Bundle(), null, true, 0);
        }

        private Action(Icon icon, CharSequence charSequence, PendingIntent pendingIntent, Bundle bundle, RemoteInput[] remoteInputArr, boolean z, int i) {
            this.mAllowGeneratedReplies = true;
            this.mIcon = icon;
            if (icon != null && icon.getType() == 2) {
                this.icon = icon.getResId();
            }
            this.title = charSequence;
            this.actionIntent = pendingIntent;
            this.mExtras = bundle == null ? new Bundle() : bundle;
            this.mRemoteInputs = remoteInputArr;
            this.mAllowGeneratedReplies = z;
            this.mSemanticAction = i;
        }

        public Icon getIcon() {
            int i;
            if (this.mIcon == null && (i = this.icon) != 0) {
                this.mIcon = Icon.createWithResource("", i);
            }
            return this.mIcon;
        }

        public Bundle getExtras() {
            return this.mExtras;
        }

        public boolean getAllowGeneratedReplies() {
            return this.mAllowGeneratedReplies;
        }

        public RemoteInput[] getRemoteInputs() {
            return this.mRemoteInputs;
        }

        public int getSemanticAction() {
            return this.mSemanticAction;
        }

        public RemoteInput[] getDataOnlyRemoteInputs() {
            return (RemoteInput[]) this.mExtras.getParcelableArray(EXTRA_DATA_ONLY_INPUTS);
        }

        public static final class Builder {
            private boolean mAllowGeneratedReplies;
            private final Bundle mExtras;
            private final Icon mIcon;
            private final PendingIntent mIntent;
            private ArrayList<RemoteInput> mRemoteInputs;
            private int mSemanticAction;
            private final CharSequence mTitle;

            @Deprecated
            public Builder(int i, CharSequence charSequence, PendingIntent pendingIntent) {
                this(Icon.createWithResource("", i), charSequence, pendingIntent);
            }

            public Builder(Icon icon, CharSequence charSequence, PendingIntent pendingIntent) {
                this(icon, charSequence, pendingIntent, new Bundle(), null, true, 0);
            }

            public Builder(Action action) {
                this(action.getIcon(), action.title, action.actionIntent, new Bundle(action.mExtras), action.getRemoteInputs(), action.getAllowGeneratedReplies(), action.getSemanticAction());
            }

            private Builder(Icon icon, CharSequence charSequence, PendingIntent pendingIntent, Bundle bundle, RemoteInput[] remoteInputArr, boolean z, int i) {
                this.mAllowGeneratedReplies = true;
                this.mIcon = icon;
                this.mTitle = charSequence;
                this.mIntent = pendingIntent;
                this.mExtras = bundle;
                if (remoteInputArr != null) {
                    ArrayList<RemoteInput> arrayList = new ArrayList<>(remoteInputArr.length);
                    this.mRemoteInputs = arrayList;
                    Collections.addAll(arrayList, remoteInputArr);
                }
                this.mAllowGeneratedReplies = z;
                this.mSemanticAction = i;
            }

            public Builder addExtras(Bundle bundle) {
                if (bundle != null) {
                    this.mExtras.putAll(bundle);
                }
                return this;
            }

            public Bundle getExtras() {
                return this.mExtras;
            }

            public Builder addRemoteInput(RemoteInput remoteInput) {
                if (this.mRemoteInputs == null) {
                    this.mRemoteInputs = new ArrayList<>();
                }
                this.mRemoteInputs.add(remoteInput);
                return this;
            }

            public Builder setAllowGeneratedReplies(boolean z) {
                this.mAllowGeneratedReplies = z;
                return this;
            }

            public Builder setSemanticAction(int i) {
                this.mSemanticAction = i;
                return this;
            }

            public Builder extend(Extender extender) {
                extender.extend(this);
                return this;
            }

            public Action build() {
                ArrayList arrayList = new ArrayList();
                RemoteInput[] remoteInputArr = (RemoteInput[]) this.mExtras.getParcelableArray(Action.EXTRA_DATA_ONLY_INPUTS);
                if (remoteInputArr != null) {
                    for (RemoteInput remoteInput : remoteInputArr) {
                        arrayList.add(remoteInput);
                    }
                }
                ArrayList arrayList2 = new ArrayList();
                ArrayList<RemoteInput> arrayList3 = this.mRemoteInputs;
                if (arrayList3 != null) {
                    for (RemoteInput remoteInput2 : arrayList3) {
                        if (remoteInput2.isDataOnly()) {
                            arrayList.add(remoteInput2);
                        } else {
                            arrayList2.add(remoteInput2);
                        }
                    }
                }
                if (!arrayList.isEmpty()) {
                    this.mExtras.putParcelableArray(Action.EXTRA_DATA_ONLY_INPUTS, (RemoteInput[]) arrayList.toArray(new RemoteInput[arrayList.size()]));
                }
                return new Action(this.mIcon, this.mTitle, this.mIntent, this.mExtras, arrayList2.isEmpty() ? null : (RemoteInput[]) arrayList2.toArray(new RemoteInput[arrayList2.size()]), this.mAllowGeneratedReplies, this.mSemanticAction);
            }
        }

        /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
        public Action m9clone() {
            return new Action(getIcon(), this.title, this.actionIntent, this.mExtras == null ? new Bundle() : new Bundle(this.mExtras), getRemoteInputs(), getAllowGeneratedReplies(), getSemanticAction());
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            Icon icon = getIcon();
            if (icon != null) {
                parcel.writeInt(1);
                icon.writeToParcel(parcel, 0);
            } else {
                parcel.writeInt(0);
            }
            TextUtils.writeToParcel(this.title, parcel, i);
            if (this.actionIntent != null) {
                parcel.writeInt(1);
                this.actionIntent.writeToParcel(parcel, i);
            } else {
                parcel.writeInt(0);
            }
            parcel.writeBundle(this.mExtras);
            parcel.writeTypedArray(this.mRemoteInputs, i);
            parcel.writeInt(this.mAllowGeneratedReplies ? 1 : 0);
            parcel.writeInt(this.mSemanticAction);
        }

        public static final class WearableExtender implements Extender {
            private static final int DEFAULT_FLAGS = 1;
            private static final String EXTRA_WEARABLE_EXTENSIONS = "android.wearable.EXTENSIONS";
            private static final int FLAG_AVAILABLE_OFFLINE = 1;
            private static final int FLAG_HINT_DISPLAY_INLINE = 4;
            private static final int FLAG_HINT_LAUNCHES_ACTIVITY = 2;
            private static final String KEY_CANCEL_LABEL = "cancelLabel";
            private static final String KEY_CONFIRM_LABEL = "confirmLabel";
            private static final String KEY_FLAGS = "flags";
            private static final String KEY_IN_PROGRESS_LABEL = "inProgressLabel";
            private CharSequence mCancelLabel;
            private CharSequence mConfirmLabel;
            private int mFlags;
            private CharSequence mInProgressLabel;

            public WearableExtender() {
                this.mFlags = 1;
            }

            public WearableExtender(Action action) {
                this.mFlags = 1;
                Bundle bundle = action.getExtras().getBundle(EXTRA_WEARABLE_EXTENSIONS);
                if (bundle != null) {
                    this.mFlags = bundle.getInt("flags", 1);
                    this.mInProgressLabel = bundle.getCharSequence(KEY_IN_PROGRESS_LABEL);
                    this.mConfirmLabel = bundle.getCharSequence(KEY_CONFIRM_LABEL);
                    this.mCancelLabel = bundle.getCharSequence(KEY_CANCEL_LABEL);
                }
            }

            @Override // android.app.Notification.Action.Extender
            public Builder extend(Builder builder) {
                Bundle bundle = new Bundle();
                int i = this.mFlags;
                if (i != 1) {
                    bundle.putInt("flags", i);
                }
                CharSequence charSequence = this.mInProgressLabel;
                if (charSequence != null) {
                    bundle.putCharSequence(KEY_IN_PROGRESS_LABEL, charSequence);
                }
                CharSequence charSequence2 = this.mConfirmLabel;
                if (charSequence2 != null) {
                    bundle.putCharSequence(KEY_CONFIRM_LABEL, charSequence2);
                }
                CharSequence charSequence3 = this.mCancelLabel;
                if (charSequence3 != null) {
                    bundle.putCharSequence(KEY_CANCEL_LABEL, charSequence3);
                }
                builder.getExtras().putBundle(EXTRA_WEARABLE_EXTENSIONS, bundle);
                return builder;
            }

            /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
            public WearableExtender m10clone() {
                WearableExtender wearableExtender = new WearableExtender();
                wearableExtender.mFlags = this.mFlags;
                wearableExtender.mInProgressLabel = this.mInProgressLabel;
                wearableExtender.mConfirmLabel = this.mConfirmLabel;
                wearableExtender.mCancelLabel = this.mCancelLabel;
                return wearableExtender;
            }

            public WearableExtender setAvailableOffline(boolean z) {
                setFlag(1, z);
                return this;
            }

            public boolean isAvailableOffline() {
                return (this.mFlags & 1) != 0;
            }

            private void setFlag(int i, boolean z) {
                if (z) {
                    this.mFlags = i | this.mFlags;
                } else {
                    this.mFlags = (~i) & this.mFlags;
                }
            }

            @Deprecated
            public WearableExtender setInProgressLabel(CharSequence charSequence) {
                this.mInProgressLabel = charSequence;
                return this;
            }

            @Deprecated
            public CharSequence getInProgressLabel() {
                return this.mInProgressLabel;
            }

            @Deprecated
            public WearableExtender setConfirmLabel(CharSequence charSequence) {
                this.mConfirmLabel = charSequence;
                return this;
            }

            @Deprecated
            public CharSequence getConfirmLabel() {
                return this.mConfirmLabel;
            }

            @Deprecated
            public WearableExtender setCancelLabel(CharSequence charSequence) {
                this.mCancelLabel = charSequence;
                return this;
            }

            @Deprecated
            public CharSequence getCancelLabel() {
                return this.mCancelLabel;
            }

            public WearableExtender setHintLaunchesActivity(boolean z) {
                setFlag(2, z);
                return this;
            }

            public boolean getHintLaunchesActivity() {
                return (this.mFlags & 2) != 0;
            }

            public WearableExtender setHintDisplayActionInline(boolean z) {
                setFlag(4, z);
                return this;
            }

            public boolean getHintDisplayActionInline() {
                return (this.mFlags & 4) != 0;
            }
        }
    }

    public Notification() {
        this.number = 0;
        this.audioStreamType = -1;
        this.audioAttributes = AUDIO_ATTRIBUTES_DEFAULT;
        this.color = 0;
        this.extras = new Bundle();
        this.mGroupAlertBehavior = 0;
        this.mBadgeIcon = 0;
        this.when = System.currentTimeMillis();
        this.creationTime = System.currentTimeMillis();
        this.priority = 0;
    }

    public Notification(Context context, int i, CharSequence charSequence, long j, CharSequence charSequence2, CharSequence charSequence3, Intent intent) {
        this.number = 0;
        this.audioStreamType = -1;
        this.audioAttributes = AUDIO_ATTRIBUTES_DEFAULT;
        this.color = 0;
        this.extras = new Bundle();
        this.mGroupAlertBehavior = 0;
        this.mBadgeIcon = 0;
        new Builder(context).setWhen(j).setSmallIcon(i).setTicker(charSequence).setContentTitle(charSequence2).setContentText(charSequence3).setContentIntent(PendingIntent.getActivity(context, 0, intent, 0)).buildInto(this);
    }

    @Deprecated
    public Notification(int i, CharSequence charSequence, long j) {
        this.number = 0;
        this.audioStreamType = -1;
        this.audioAttributes = AUDIO_ATTRIBUTES_DEFAULT;
        this.color = 0;
        this.extras = new Bundle();
        this.mGroupAlertBehavior = 0;
        this.mBadgeIcon = 0;
        this.icon = i;
        this.tickerText = charSequence;
        this.when = j;
        this.creationTime = System.currentTimeMillis();
    }

    public Notification(Parcel parcel) {
        this.number = 0;
        this.audioStreamType = -1;
        this.audioAttributes = AUDIO_ATTRIBUTES_DEFAULT;
        this.color = 0;
        this.extras = new Bundle();
        this.mGroupAlertBehavior = 0;
        this.mBadgeIcon = 0;
        readFromParcelImpl(parcel);
        this.allPendingIntents = parcel.readArraySet(null);
    }

    private void readFromParcelImpl(Parcel parcel) {
        parcel.readInt();
        IBinder strongBinder = parcel.readStrongBinder();
        this.mWhitelistToken = strongBinder;
        if (strongBinder == null) {
            this.mWhitelistToken = processWhitelistToken;
        }
        parcel.setClassCookie(PendingIntent.class, this.mWhitelistToken);
        this.when = parcel.readLong();
        this.creationTime = parcel.readLong();
        if (parcel.readInt() != 0) {
            Icon iconCreateFromParcel = Icon.CREATOR.createFromParcel(parcel);
            this.mSmallIcon = iconCreateFromParcel;
            if (iconCreateFromParcel.getType() == 2) {
                this.icon = this.mSmallIcon.getResId();
            }
        }
        this.number = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.contentIntent = PendingIntent.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.deleteIntent = PendingIntent.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.tickerText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.tickerView = RemoteViews.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.contentView = RemoteViews.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.mLargeIcon = Icon.CREATOR.createFromParcel(parcel);
        }
        this.defaults = parcel.readInt();
        this.flags = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.sound = Uri.CREATOR.createFromParcel(parcel);
        }
        this.audioStreamType = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.audioAttributes = AudioAttributes.CREATOR.createFromParcel(parcel);
        }
        this.vibrate = parcel.createLongArray();
        this.ledARGB = parcel.readInt();
        this.ledOnMS = parcel.readInt();
        this.ledOffMS = parcel.readInt();
        this.iconLevel = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.fullScreenIntent = PendingIntent.CREATOR.createFromParcel(parcel);
        }
        this.priority = parcel.readInt();
        this.category = parcel.readString();
        this.mGroupKey = parcel.readString();
        this.mSortKey = parcel.readString();
        this.extras = Bundle.setDefusable(parcel.readBundle(), true);
        fixDuplicateExtras();
        this.actions = (Action[]) parcel.createTypedArray(Action.CREATOR);
        if (parcel.readInt() != 0) {
            this.bigContentView = RemoteViews.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.headsUpContentView = RemoteViews.CREATOR.createFromParcel(parcel);
        }
        this.visibility = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.publicVersion = CREATOR.createFromParcel(parcel);
        }
        this.color = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.mChannelId = parcel.readString();
        }
        this.mTimeout = parcel.readLong();
        if (parcel.readInt() != 0) {
            this.mShortcutId = parcel.readString();
        }
        this.mBadgeIcon = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.mSettingsText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        }
        this.mGroupAlertBehavior = parcel.readInt();
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public Notification m8clone() {
        Notification notification = new Notification();
        cloneInto(notification, true);
        return notification;
    }

    public void cloneInto(Notification notification, boolean z) {
        RemoteViews remoteViews;
        RemoteViews remoteViews2;
        Icon icon;
        RemoteViews remoteViews3;
        RemoteViews remoteViews4;
        notification.mWhitelistToken = this.mWhitelistToken;
        notification.when = this.when;
        notification.creationTime = this.creationTime;
        notification.mSmallIcon = this.mSmallIcon;
        notification.number = this.number;
        notification.contentIntent = this.contentIntent;
        notification.deleteIntent = this.deleteIntent;
        notification.fullScreenIntent = this.fullScreenIntent;
        CharSequence charSequence = this.tickerText;
        if (charSequence != null) {
            notification.tickerText = charSequence.toString();
        }
        if (z && (remoteViews4 = this.tickerView) != null) {
            notification.tickerView = remoteViews4.mo11clone();
        }
        if (z && (remoteViews3 = this.contentView) != null) {
            notification.contentView = remoteViews3.mo11clone();
        }
        if (z && (icon = this.mLargeIcon) != null) {
            notification.mLargeIcon = icon;
        }
        notification.iconLevel = this.iconLevel;
        notification.sound = this.sound;
        notification.audioStreamType = this.audioStreamType;
        if (this.audioAttributes != null) {
            notification.audioAttributes = new AudioAttributes.Builder(this.audioAttributes).build();
        }
        long[] jArr = this.vibrate;
        int i = 0;
        if (jArr != null) {
            int length = jArr.length;
            long[] jArr2 = new long[length];
            notification.vibrate = jArr2;
            System.arraycopy(jArr, 0, jArr2, 0, length);
        }
        notification.ledARGB = this.ledARGB;
        notification.ledOnMS = this.ledOnMS;
        notification.ledOffMS = this.ledOffMS;
        notification.defaults = this.defaults;
        notification.flags = this.flags;
        notification.priority = this.priority;
        notification.category = this.category;
        notification.mGroupKey = this.mGroupKey;
        notification.mSortKey = this.mSortKey;
        if (this.extras != null) {
            try {
                Bundle bundle = new Bundle(this.extras);
                notification.extras = bundle;
                bundle.size();
            } catch (BadParcelableException e) {
                Log.e(TAG, "could not unparcel extras from notification: " + this, e);
                notification.extras = null;
            }
        }
        if (!ArrayUtils.isEmpty(this.allPendingIntents)) {
            notification.allPendingIntents = new ArraySet<>((ArraySet) this.allPendingIntents);
        }
        Action[] actionArr = this.actions;
        if (actionArr != null) {
            notification.actions = new Action[actionArr.length];
            while (true) {
                Action[] actionArr2 = this.actions;
                if (i >= actionArr2.length) {
                    break;
                }
                if (actionArr2[i] != null) {
                    notification.actions[i] = actionArr2[i].m9clone();
                }
                i++;
            }
        }
        if (z && (remoteViews2 = this.bigContentView) != null) {
            notification.bigContentView = remoteViews2.mo11clone();
        }
        if (z && (remoteViews = this.headsUpContentView) != null) {
            notification.headsUpContentView = remoteViews.mo11clone();
        }
        notification.visibility = this.visibility;
        if (this.publicVersion != null) {
            Notification notification2 = new Notification();
            notification.publicVersion = notification2;
            this.publicVersion.cloneInto(notification2, z);
        }
        notification.color = this.color;
        notification.mChannelId = this.mChannelId;
        notification.mTimeout = this.mTimeout;
        notification.mShortcutId = this.mShortcutId;
        notification.mBadgeIcon = this.mBadgeIcon;
        notification.mSettingsText = this.mSettingsText;
        notification.mGroupAlertBehavior = this.mGroupAlertBehavior;
        if (z) {
            return;
        }
        notification.lightenPayload();
    }

    public void visitUris(Consumer<Uri> consumer) {
        Bundle bundle;
        consumer.accept(this.sound);
        RemoteViews remoteViews = this.tickerView;
        if (remoteViews != null) {
            remoteViews.visitUris(consumer);
        }
        RemoteViews remoteViews2 = this.contentView;
        if (remoteViews2 != null) {
            remoteViews2.visitUris(consumer);
        }
        RemoteViews remoteViews3 = this.bigContentView;
        if (remoteViews3 != null) {
            remoteViews3.visitUris(consumer);
        }
        RemoteViews remoteViews4 = this.headsUpContentView;
        if (remoteViews4 != null) {
            remoteViews4.visitUris(consumer);
        }
        Bundle bundle2 = this.extras;
        if (bundle2 != null) {
            consumer.accept((Uri) bundle2.getParcelable("android.audioContents"));
            consumer.accept((Uri) this.extras.getParcelable("android.backgroundImageUri"));
        }
        if (!MessagingStyle.class.equals(getNotificationStyle()) || (bundle = this.extras) == null) {
            return;
        }
        Parcelable[] parcelableArray = bundle.getParcelableArray("android.messages");
        if (!ArrayUtils.isEmpty(parcelableArray)) {
            Iterator<MessagingStyle.Message> it = MessagingStyle.Message.getMessagesFromBundleArray(parcelableArray).iterator();
            while (it.hasNext()) {
                consumer.accept(it.next().getDataUri());
            }
        }
        Parcelable[] parcelableArray2 = this.extras.getParcelableArray(EXTRA_HISTORIC_MESSAGES);
        if (ArrayUtils.isEmpty(parcelableArray2)) {
            return;
        }
        Iterator<MessagingStyle.Message> it2 = MessagingStyle.Message.getMessagesFromBundleArray(parcelableArray2).iterator();
        while (it2.hasNext()) {
            consumer.accept(it2.next().getDataUri());
        }
    }

    public final void lightenPayload() {
        Object obj;
        this.tickerView = null;
        this.contentView = null;
        this.bigContentView = null;
        this.headsUpContentView = null;
        this.mLargeIcon = null;
        Bundle bundle = this.extras;
        if (bundle == null || bundle.isEmpty()) {
            return;
        }
        Set<String> setKeySet = this.extras.keySet();
        int size = setKeySet.size();
        String[] strArr = (String[]) setKeySet.toArray(new String[size]);
        for (int i = 0; i < size; i++) {
            String str = strArr[i];
            if (!"android.tv.EXTENSIONS".equals(str) && (obj = this.extras.get(str)) != null && ((obj instanceof Parcelable) || (obj instanceof Parcelable[]) || (obj instanceof SparseArray) || (obj instanceof ArrayList))) {
                this.extras.remove(str);
            }
        }
    }

    public static CharSequence safeCharSequence(CharSequence charSequence) {
        if (charSequence == null) {
            return charSequence;
        }
        if (charSequence.length() > 5120) {
            charSequence = charSequence.subSequence(0, 5120);
        }
        if (charSequence instanceof Parcelable) {
            Log.e(TAG, "warning: " + charSequence.getClass().getCanonicalName() + " instance is a custom Parcelable and not allowed in Notification");
            return charSequence.toString();
        }
        return removeTextSizeSpans(charSequence);
    }

    private static CharSequence removeTextSizeSpans(CharSequence charSequence) {
        Object textAppearanceSpan;
        if (!(charSequence instanceof Spanned)) {
            return charSequence;
        }
        Spanned spanned = (Spanned) charSequence;
        Object[] spans = spanned.getSpans(0, spanned.length(), Object.class);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(spanned.toString());
        for (Object obj : spans) {
            Object underlying = obj instanceof CharacterStyle ? ((CharacterStyle) obj).getUnderlying() : obj;
            if (underlying instanceof TextAppearanceSpan) {
                TextAppearanceSpan textAppearanceSpan2 = (TextAppearanceSpan) underlying;
                textAppearanceSpan = new TextAppearanceSpan(textAppearanceSpan2.getFamily(), textAppearanceSpan2.getTextStyle(), -1, textAppearanceSpan2.getTextColor(), textAppearanceSpan2.getLinkTextColor());
            } else {
                if (!(underlying instanceof RelativeSizeSpan) && !(underlying instanceof AbsoluteSizeSpan)) {
                    textAppearanceSpan = obj;
                }
            }
            spannableStringBuilder.setSpan(textAppearanceSpan, spanned.getSpanStart(obj), spanned.getSpanEnd(obj), spanned.getSpanFlags(obj));
        }
        return spannableStringBuilder;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(final Parcel parcel, int i) {
        boolean z = this.allPendingIntents == null;
        if (z) {
            PendingIntent.setOnMarshaledListener(new PendingIntent.OnMarshaledListener() { // from class: android.app.-$$Lambda$Notification$hOCsSZH8tWalFSbIzQ9x9IcPa9M
                @Override // android.app.PendingIntent.OnMarshaledListener
                public final void onMarshaled(PendingIntent pendingIntent, Parcel parcel2, int i2) {
                    this.f$0.lambda$writeToParcel$0$Notification(parcel, pendingIntent, parcel2, i2);
                }
            });
        }
        try {
            writeToParcelImpl(parcel, i);
            parcel.writeArraySet(this.allPendingIntents);
        } finally {
            if (z) {
                PendingIntent.setOnMarshaledListener(null);
            }
        }
    }

    public /* synthetic */ void lambda$writeToParcel$0$Notification(Parcel parcel, PendingIntent pendingIntent, Parcel parcel2, int i) {
        if (parcel == parcel2) {
            if (this.allPendingIntents == null) {
                this.allPendingIntents = new ArraySet<>();
            }
            this.allPendingIntents.add(pendingIntent);
        }
    }

    private void writeToParcelImpl(Parcel parcel, int i) {
        Bitmap bitmap;
        int i2;
        parcel.writeInt(1);
        parcel.writeStrongBinder(this.mWhitelistToken);
        parcel.writeLong(this.when);
        parcel.writeLong(this.creationTime);
        if (this.mSmallIcon == null && (i2 = this.icon) != 0) {
            this.mSmallIcon = Icon.createWithResource("", i2);
        }
        if (this.mSmallIcon != null) {
            parcel.writeInt(1);
            this.mSmallIcon.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.number);
        if (this.contentIntent != null) {
            parcel.writeInt(1);
            this.contentIntent.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        if (this.deleteIntent != null) {
            parcel.writeInt(1);
            this.deleteIntent.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        if (this.tickerText != null) {
            parcel.writeInt(1);
            TextUtils.writeToParcel(this.tickerText, parcel, i);
        } else {
            parcel.writeInt(0);
        }
        if (this.tickerView != null) {
            parcel.writeInt(1);
            this.tickerView.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        if (this.contentView != null) {
            parcel.writeInt(1);
            this.contentView.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        if (this.mLargeIcon == null && (bitmap = this.largeIcon) != null) {
            this.mLargeIcon = Icon.createWithBitmap(bitmap);
        }
        if (this.mLargeIcon != null) {
            parcel.writeInt(1);
            this.mLargeIcon.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.defaults);
        parcel.writeInt(this.flags);
        if (this.sound != null) {
            parcel.writeInt(1);
            this.sound.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.audioStreamType);
        if (this.audioAttributes != null) {
            parcel.writeInt(1);
            this.audioAttributes.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeLongArray(this.vibrate);
        parcel.writeInt(this.ledARGB);
        parcel.writeInt(this.ledOnMS);
        parcel.writeInt(this.ledOffMS);
        parcel.writeInt(this.iconLevel);
        if (this.fullScreenIntent != null) {
            parcel.writeInt(1);
            this.fullScreenIntent.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.priority);
        parcel.writeString(this.category);
        parcel.writeString(this.mGroupKey);
        parcel.writeString(this.mSortKey);
        parcel.writeBundle(this.extras);
        parcel.writeTypedArray(this.actions, 0);
        if (this.bigContentView != null) {
            parcel.writeInt(1);
            this.bigContentView.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        if (this.headsUpContentView != null) {
            parcel.writeInt(1);
            this.headsUpContentView.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.visibility);
        if (this.publicVersion != null) {
            parcel.writeInt(1);
            this.publicVersion.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.color);
        if (this.mChannelId != null) {
            parcel.writeInt(1);
            parcel.writeString(this.mChannelId);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeLong(this.mTimeout);
        if (this.mShortcutId != null) {
            parcel.writeInt(1);
            parcel.writeString(this.mShortcutId);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.mBadgeIcon);
        if (this.mSettingsText != null) {
            parcel.writeInt(1);
            TextUtils.writeToParcel(this.mSettingsText, parcel, i);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.mGroupAlertBehavior);
    }

    public static boolean areActionsVisiblyDifferent(Notification notification, Notification notification2) {
        Action[] actionArr = notification.actions;
        Action[] actionArr2 = notification2.actions;
        if ((actionArr == null && actionArr2 != null) || (actionArr != null && actionArr2 == null)) {
            return true;
        }
        if (actionArr != null && actionArr2 != null) {
            if (actionArr.length != actionArr2.length) {
                return true;
            }
            for (int i = 0; i < actionArr.length; i++) {
                if (!Objects.equals(String.valueOf(actionArr[i].title), String.valueOf(actionArr2[i].title))) {
                    return true;
                }
                RemoteInput[] remoteInputs = actionArr[i].getRemoteInputs();
                RemoteInput[] remoteInputs2 = actionArr2[i].getRemoteInputs();
                if (remoteInputs == null) {
                    remoteInputs = new RemoteInput[0];
                }
                if (remoteInputs2 == null) {
                    remoteInputs2 = new RemoteInput[0];
                }
                if (remoteInputs.length != remoteInputs2.length) {
                    return true;
                }
                for (int i2 = 0; i2 < remoteInputs.length; i2++) {
                    if (!Objects.equals(String.valueOf(remoteInputs[i2].getLabel()), String.valueOf(remoteInputs2[i2].getLabel()))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean areStyledNotificationsVisiblyDifferent(Builder builder, Builder builder2) {
        if (builder.getStyle() == null) {
            return builder2.getStyle() != null;
        }
        if (builder2.getStyle() == null) {
            return true;
        }
        return builder.getStyle().areNotificationsVisiblyDifferent(builder2.getStyle());
    }

    public static boolean areRemoteViewsChanged(Builder builder, Builder builder2) {
        return !Objects.equals(Boolean.valueOf(builder.usesStandardHeader()), Boolean.valueOf(builder2.usesStandardHeader())) || areRemoteViewsChanged(builder.mN.contentView, builder2.mN.contentView) || areRemoteViewsChanged(builder.mN.bigContentView, builder2.mN.bigContentView) || areRemoteViewsChanged(builder.mN.headsUpContentView, builder2.mN.headsUpContentView);
    }

    private static boolean areRemoteViewsChanged(RemoteViews remoteViews, RemoteViews remoteViews2) {
        if (remoteViews == null && remoteViews2 == null) {
            return false;
        }
        return (remoteViews == null && remoteViews2 != null) || !((remoteViews == null || remoteViews2 != null) && Objects.equals(Integer.valueOf(remoteViews.getLayoutId()), Integer.valueOf(remoteViews2.getLayoutId())) && Objects.equals(Integer.valueOf(remoteViews.getSequenceNumber()), Integer.valueOf(remoteViews2.getSequenceNumber())));
    }

    private void fixDuplicateExtras() {
        if (this.extras != null) {
            fixDuplicateExtra(this.mSmallIcon, "android.icon");
            fixDuplicateExtra(this.mLargeIcon, "android.largeIcon");
        }
    }

    private void fixDuplicateExtra(Parcelable parcelable, String str) {
        if (parcelable == null || this.extras.getParcelable(str) == null) {
            return;
        }
        this.extras.putParcelable(str, parcelable);
    }

    @Deprecated
    public void setLatestEventInfo(Context context, CharSequence charSequence, CharSequence charSequence2, PendingIntent pendingIntent) {
        if (context.getApplicationInfo().targetSdkVersion > 22) {
            Log.e(TAG, "setLatestEventInfo() is deprecated and you should feel deprecated.", new Throwable());
        }
        if (context.getApplicationInfo().targetSdkVersion < 24) {
            this.extras.putBoolean("android.showWhen", true);
        }
        Builder builder = new Builder(context, this);
        if (charSequence != null) {
            builder.setContentTitle(charSequence);
        }
        if (charSequence2 != null) {
            builder.setContentText(charSequence2);
        }
        builder.setContentIntent(pendingIntent);
        builder.build();
    }

    public static void addFieldsFromContext(Context context, Notification notification) {
        addFieldsFromContext(context.getApplicationInfo(), notification);
    }

    public static void addFieldsFromContext(ApplicationInfo applicationInfo, Notification notification) {
        notification.extras.putParcelable(EXTRA_BUILDER_APPLICATION_INFO, applicationInfo);
    }

    public void writeToProto(ProtoOutputStream protoOutputStream, long j) {
        long jStart = protoOutputStream.start(j);
        protoOutputStream.write(1138166333441L, getChannelId());
        protoOutputStream.write(1133871366146L, this.tickerText != null);
        protoOutputStream.write(1120986464259L, this.flags);
        protoOutputStream.write(1120986464260L, this.color);
        protoOutputStream.write(1138166333445L, this.category);
        protoOutputStream.write(1138166333446L, this.mGroupKey);
        protoOutputStream.write(1138166333447L, this.mSortKey);
        Action[] actionArr = this.actions;
        if (actionArr != null) {
            protoOutputStream.write(1120986464264L, actionArr.length);
        }
        int i = this.visibility;
        if (i >= -1 && i <= 1) {
            protoOutputStream.write(1159641169929L, i);
        }
        Notification notification = this.publicVersion;
        if (notification != null) {
            notification.writeToProto(protoOutputStream, 1146756268042L);
        }
        protoOutputStream.end(jStart);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Notification(channel=");
        sb.append(getChannelId());
        sb.append(" pri=");
        sb.append(this.priority);
        sb.append(" contentView=");
        RemoteViews remoteViews = this.contentView;
        if (remoteViews != null) {
            sb.append(remoteViews.getPackage());
            sb.append("/0x");
            sb.append(Integer.toHexString(this.contentView.getLayoutId()));
        } else {
            sb.append("null");
        }
        sb.append(" vibrate=");
        if ((this.defaults & 2) != 0) {
            sb.append(PhoneConstants.APN_TYPE_DEFAULT);
        } else {
            long[] jArr = this.vibrate;
            if (jArr != null) {
                int length = jArr.length - 1;
                sb.append("[");
                for (int i = 0; i < length; i++) {
                    sb.append(this.vibrate[i]);
                    sb.append(',');
                }
                if (length != -1) {
                    sb.append(this.vibrate[length]);
                }
                sb.append("]");
            } else {
                sb.append("null");
            }
        }
        sb.append(" sound=");
        if ((this.defaults & 1) != 0) {
            sb.append(PhoneConstants.APN_TYPE_DEFAULT);
        } else {
            Uri uri = this.sound;
            if (uri != null) {
                sb.append(uri.toString());
            } else {
                sb.append("null");
            }
        }
        if (this.tickerText != null) {
            sb.append(" tick");
        }
        sb.append(" defaults=0x");
        sb.append(Integer.toHexString(this.defaults));
        sb.append(" flags=0x");
        sb.append(Integer.toHexString(this.flags));
        sb.append(String.format(" color=0x%08x", Integer.valueOf(this.color)));
        if (this.category != null) {
            sb.append(" category=");
            sb.append(this.category);
        }
        if (this.mGroupKey != null) {
            sb.append(" groupKey=");
            sb.append(this.mGroupKey);
        }
        if (this.mSortKey != null) {
            sb.append(" sortKey=");
            sb.append(this.mSortKey);
        }
        if (this.actions != null) {
            sb.append(" actions=");
            sb.append(this.actions.length);
        }
        sb.append(" vis=");
        sb.append(visibilityToString(this.visibility));
        if (this.publicVersion != null) {
            sb.append(" publicVersion=");
            sb.append(this.publicVersion.toString());
        }
        sb.append(")");
        return sb.toString();
    }

    public static String visibilityToString(int i) {
        if (i == -1) {
            return "SECRET";
        }
        if (i == 0) {
            return "PRIVATE";
        }
        if (i == 1) {
            return "PUBLIC";
        }
        return "UNKNOWN(" + String.valueOf(i) + ")";
    }

    public static String priorityToString(int i) {
        if (i == -2) {
            return "MIN";
        }
        if (i == -1) {
            return "LOW";
        }
        if (i == 0) {
            return "DEFAULT";
        }
        if (i == 1) {
            return "HIGH";
        }
        if (i == 2) {
            return "MAX";
        }
        return "UNKNOWN(" + String.valueOf(i) + ")";
    }

    public boolean hasCompletedProgress() {
        return this.extras.containsKey("android.progress") && this.extras.containsKey("android.progressMax") && this.extras.getInt("android.progressMax") != 0 && this.extras.getInt("android.progress") == this.extras.getInt("android.progressMax");
    }

    @Deprecated
    public String getChannel() {
        return this.mChannelId;
    }

    public String getChannelId() {
        return this.mChannelId;
    }

    @Deprecated
    public long getTimeout() {
        return this.mTimeout;
    }

    public long getTimeoutAfter() {
        return this.mTimeout;
    }

    public int getBadgeIconType() {
        return this.mBadgeIcon;
    }

    public String getShortcutId() {
        return this.mShortcutId;
    }

    public CharSequence getSettingsText() {
        return this.mSettingsText;
    }

    public int getGroupAlertBehavior() {
        return this.mGroupAlertBehavior;
    }

    public Icon getSmallIcon() {
        return this.mSmallIcon;
    }

    public void setSmallIcon(Icon icon) {
        this.mSmallIcon = icon;
    }

    public Icon getLargeIcon() {
        return this.mLargeIcon;
    }

    public boolean isGroupSummary() {
        return (this.mGroupKey == null || (this.flags & 512) == 0) ? false : true;
    }

    public boolean isGroupChild() {
        return this.mGroupKey != null && (this.flags & 512) == 0;
    }

    public boolean suppressAlertingDueToGrouping() {
        if (isGroupSummary() && getGroupAlertBehavior() == 2) {
            return true;
        }
        return isGroupChild() && getGroupAlertBehavior() == 1;
    }

    public static class Builder {
        public static final String EXTRA_REBUILD_BIG_CONTENT_VIEW_ACTION_COUNT = "android.rebuild.bigViewActionCount";
        public static final String EXTRA_REBUILD_CONTENT_VIEW_ACTION_COUNT = "android.rebuild.contentViewActionCount";
        public static final String EXTRA_REBUILD_HEADS_UP_CONTENT_VIEW_ACTION_COUNT = "android.rebuild.hudViewActionCount";
        private static final int LIGHTNESS_TEXT_DIFFERENCE_DARK = -10;
        private static final int LIGHTNESS_TEXT_DIFFERENCE_LIGHT = 20;
        private static final int MAX_ACTION_BUTTONS = 3;
        private static final boolean USE_ONLY_TITLE_IN_LOW_PRIORITY_SUMMARY = SystemProperties.getBoolean("notifications.only_title", true);
        private ArrayList<Action> mActions;
        private int mBackgroundColor;
        private int mCachedAmbientColor;
        private int mCachedAmbientColorIsFor;
        private int mCachedContrastColor;
        private int mCachedContrastColorIsFor;
        private NotificationColorUtil mColorUtil;
        private Context mContext;
        private int mForegroundColor;
        private boolean mInNightMode;
        private boolean mIsLegacy;
        private boolean mIsLegacyInitialized;
        private Notification mN;
        private int mNeutralColor;
        private ArrayList<Action> mOriginalActions;
        StandardTemplateParams mParams;
        private ArrayList<Person> mPersonList;
        private int mPrimaryTextColor;
        private boolean mRebuildStyledRemoteViews;
        private int mSecondaryTextColor;
        private Style mStyle;
        private int mTextColorsAreForBackground;
        private boolean mTintActionButtons;
        private Bundle mUserExtras;

        private int getActionLayoutResource() {
            return R.layout.notification_material_action;
        }

        private int getActionTombstoneLayoutResource() {
            return R.layout.notification_material_action_tombstone;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getBaseLayoutResource() {
            return R.layout.notification_template_material_base;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getBigBaseLayoutResource() {
            return R.layout.notification_template_material_big_base;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getBigPictureLayoutResource() {
            return R.layout.notification_template_material_big_picture;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getBigTextLayoutResource() {
            return R.layout.notification_template_material_big_text;
        }

        private int getEmphasizedActionLayoutResource() {
            return R.layout.notification_material_action_emphasized;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getInboxLayoutResource() {
            return R.layout.notification_template_material_inbox;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getMessagingLayoutResource() {
            return R.layout.notification_template_material_messaging;
        }

        public Builder(Context context, String str) {
            this(context, (Notification) null);
            this.mN.mChannelId = str;
        }

        @Deprecated
        public Builder(Context context) {
            this(context, (Notification) null);
        }

        public Builder(Context context, Notification notification) {
            this.mUserExtras = new Bundle();
            this.mActions = new ArrayList<>(3);
            this.mPersonList = new ArrayList<>();
            this.mCachedContrastColor = 1;
            this.mCachedContrastColorIsFor = 1;
            this.mCachedAmbientColor = 1;
            this.mCachedAmbientColorIsFor = 1;
            this.mNeutralColor = 1;
            this.mParams = new StandardTemplateParams();
            this.mTextColorsAreForBackground = 1;
            this.mPrimaryTextColor = 1;
            this.mSecondaryTextColor = 1;
            this.mBackgroundColor = 1;
            this.mForegroundColor = 1;
            this.mContext = context;
            Resources resources = context.getResources();
            this.mTintActionButtons = resources.getBoolean(R.bool.config_tintNotificationActionButtons);
            if (resources.getBoolean(R.bool.config_enableNightMode)) {
                this.mInNightMode = (resources.getConfiguration().uiMode & 48) == 32;
            }
            if (notification == null) {
                this.mN = new Notification();
                if (context.getApplicationInfo().targetSdkVersion < 24) {
                    this.mN.extras.putBoolean("android.showWhen", true);
                }
                this.mN.priority = 0;
                this.mN.visibility = 0;
                return;
            }
            this.mN = notification;
            if (notification.actions != null) {
                Collections.addAll(this.mActions, this.mN.actions);
            }
            if (this.mN.extras.containsKey(Notification.EXTRA_PEOPLE_LIST)) {
                this.mPersonList.addAll(this.mN.extras.getParcelableArrayList(Notification.EXTRA_PEOPLE_LIST));
            }
            if (this.mN.getSmallIcon() == null && this.mN.icon != 0) {
                setSmallIcon(this.mN.icon);
            }
            if (this.mN.getLargeIcon() == null && this.mN.largeIcon != null) {
                setLargeIcon(this.mN.largeIcon);
            }
            String string = this.mN.extras.getString("android.template");
            if (TextUtils.isEmpty(string)) {
                return;
            }
            Class<? extends Style> notificationStyleClass = Notification.getNotificationStyleClass(string);
            if (notificationStyleClass == null) {
                Log.d(Notification.TAG, "Unknown style class: " + string);
                return;
            }
            try {
                Constructor<? extends Style> declaredConstructor = notificationStyleClass.getDeclaredConstructor(new Class[0]);
                declaredConstructor.setAccessible(true);
                Style styleNewInstance = declaredConstructor.newInstance(new Object[0]);
                styleNewInstance.restoreFromExtras(this.mN.extras);
                if (styleNewInstance != null) {
                    setStyle(styleNewInstance);
                }
            } catch (Throwable th) {
                Log.e(Notification.TAG, "Could not create Style", th);
            }
        }

        private NotificationColorUtil getColorUtil() {
            if (this.mColorUtil == null) {
                this.mColorUtil = NotificationColorUtil.getInstance(this.mContext);
            }
            return this.mColorUtil;
        }

        public Builder setShortcutId(String str) {
            this.mN.mShortcutId = str;
            return this;
        }

        public Builder setBadgeIconType(int i) {
            this.mN.mBadgeIcon = i;
            return this;
        }

        public Builder setGroupAlertBehavior(int i) {
            this.mN.mGroupAlertBehavior = i;
            return this;
        }

        @Deprecated
        public Builder setChannel(String str) {
            this.mN.mChannelId = str;
            return this;
        }

        public Builder setChannelId(String str) {
            this.mN.mChannelId = str;
            return this;
        }

        @Deprecated
        public Builder setTimeout(long j) {
            this.mN.mTimeout = j;
            return this;
        }

        public Builder setTimeoutAfter(long j) {
            this.mN.mTimeout = j;
            return this;
        }

        public Builder setWhen(long j) {
            this.mN.when = j;
            return this;
        }

        public Builder setShowWhen(boolean z) {
            this.mN.extras.putBoolean("android.showWhen", z);
            return this;
        }

        public Builder setUsesChronometer(boolean z) {
            this.mN.extras.putBoolean("android.showChronometer", z);
            return this;
        }

        public Builder setChronometerCountDown(boolean z) {
            this.mN.extras.putBoolean(Notification.EXTRA_CHRONOMETER_COUNT_DOWN, z);
            return this;
        }

        public Builder setSmallIcon(int i) {
            return setSmallIcon(i != 0 ? Icon.createWithResource(this.mContext, i) : null);
        }

        public Builder setSmallIcon(int i, int i2) {
            this.mN.iconLevel = i2;
            return setSmallIcon(i);
        }

        public Builder setSmallIcon(Icon icon) {
            this.mN.setSmallIcon(icon);
            if (icon != null && icon.getType() == 2) {
                this.mN.icon = icon.getResId();
            }
            return this;
        }

        public Builder setContentTitle(CharSequence charSequence) {
            this.mN.extras.putCharSequence("android.title", Notification.safeCharSequence(charSequence));
            return this;
        }

        public Builder setContentText(CharSequence charSequence) {
            this.mN.extras.putCharSequence("android.text", Notification.safeCharSequence(charSequence));
            return this;
        }

        public Builder setSubText(CharSequence charSequence) {
            this.mN.extras.putCharSequence("android.subText", Notification.safeCharSequence(charSequence));
            return this;
        }

        public Builder setSettingsText(CharSequence charSequence) {
            this.mN.mSettingsText = Notification.safeCharSequence(charSequence);
            return this;
        }

        public Builder setRemoteInputHistory(CharSequence[] charSequenceArr) {
            if (charSequenceArr == null) {
                this.mN.extras.putCharSequenceArray("android.remoteInputHistory", null);
            } else {
                int iMin = Math.min(5, charSequenceArr.length);
                CharSequence[] charSequenceArr2 = new CharSequence[iMin];
                for (int i = 0; i < iMin; i++) {
                    charSequenceArr2[i] = Notification.safeCharSequence(charSequenceArr[i]);
                }
                this.mN.extras.putCharSequenceArray("android.remoteInputHistory", charSequenceArr2);
            }
            return this;
        }

        public Builder setShowRemoteInputSpinner(boolean z) {
            this.mN.extras.putBoolean(Notification.EXTRA_SHOW_REMOTE_INPUT_SPINNER, z);
            return this;
        }

        public Builder setHideSmartReplies(boolean z) {
            this.mN.extras.putBoolean(Notification.EXTRA_HIDE_SMART_REPLIES, z);
            return this;
        }

        public Builder setNumber(int i) {
            this.mN.number = i;
            return this;
        }

        @Deprecated
        public Builder setContentInfo(CharSequence charSequence) {
            this.mN.extras.putCharSequence("android.infoText", Notification.safeCharSequence(charSequence));
            return this;
        }

        public Builder setProgress(int i, int i2, boolean z) {
            this.mN.extras.putInt("android.progress", i2);
            this.mN.extras.putInt("android.progressMax", i);
            this.mN.extras.putBoolean("android.progressIndeterminate", z);
            return this;
        }

        @Deprecated
        public Builder setContent(RemoteViews remoteViews) {
            return setCustomContentView(remoteViews);
        }

        public Builder setCustomContentView(RemoteViews remoteViews) {
            this.mN.contentView = remoteViews;
            return this;
        }

        public Builder setCustomBigContentView(RemoteViews remoteViews) {
            this.mN.bigContentView = remoteViews;
            return this;
        }

        public Builder setCustomHeadsUpContentView(RemoteViews remoteViews) {
            this.mN.headsUpContentView = remoteViews;
            return this;
        }

        public Builder setContentIntent(PendingIntent pendingIntent) {
            this.mN.contentIntent = pendingIntent;
            return this;
        }

        public Builder setDeleteIntent(PendingIntent pendingIntent) {
            this.mN.deleteIntent = pendingIntent;
            return this;
        }

        public Builder setFullScreenIntent(PendingIntent pendingIntent, boolean z) {
            this.mN.fullScreenIntent = pendingIntent;
            setFlag(128, z);
            return this;
        }

        public Builder setTicker(CharSequence charSequence) {
            this.mN.tickerText = Notification.safeCharSequence(charSequence);
            return this;
        }

        @Deprecated
        public Builder setTicker(CharSequence charSequence, RemoteViews remoteViews) {
            setTicker(charSequence);
            return this;
        }

        public Builder setLargeIcon(Bitmap bitmap) {
            return setLargeIcon(bitmap != null ? Icon.createWithBitmap(bitmap) : null);
        }

        public Builder setLargeIcon(Icon icon) {
            this.mN.mLargeIcon = icon;
            this.mN.extras.putParcelable("android.largeIcon", icon);
            return this;
        }

        @Deprecated
        public Builder setSound(Uri uri) {
            this.mN.sound = uri;
            this.mN.audioAttributes = Notification.AUDIO_ATTRIBUTES_DEFAULT;
            return this;
        }

        @Deprecated
        public Builder setSound(Uri uri, int i) {
            PlayerBase.deprecateStreamTypeForPlayback(i, Notification.TAG, "setSound()");
            this.mN.sound = uri;
            this.mN.audioStreamType = i;
            return this;
        }

        @Deprecated
        public Builder setSound(Uri uri, AudioAttributes audioAttributes) {
            this.mN.sound = uri;
            this.mN.audioAttributes = audioAttributes;
            return this;
        }

        @Deprecated
        public Builder setVibrate(long[] jArr) {
            this.mN.vibrate = jArr;
            return this;
        }

        @Deprecated
        public Builder setLights(int i, int i2, int i3) {
            this.mN.ledARGB = i;
            this.mN.ledOnMS = i2;
            this.mN.ledOffMS = i3;
            if (i2 != 0 || i3 != 0) {
                this.mN.flags |= 1;
            }
            return this;
        }

        public Builder setOngoing(boolean z) {
            setFlag(2, z);
            return this;
        }

        public Builder setColorized(boolean z) {
            this.mN.extras.putBoolean(Notification.EXTRA_COLORIZED, z);
            return this;
        }

        public Builder setOnlyAlertOnce(boolean z) {
            setFlag(8, z);
            return this;
        }

        public Builder setAutoCancel(boolean z) {
            setFlag(16, z);
            return this;
        }

        public Builder setLocalOnly(boolean z) {
            setFlag(256, z);
            return this;
        }

        @Deprecated
        public Builder setDefaults(int i) {
            this.mN.defaults = i;
            return this;
        }

        @Deprecated
        public Builder setPriority(int i) {
            this.mN.priority = i;
            return this;
        }

        public Builder setCategory(String str) {
            this.mN.category = str;
            return this;
        }

        public Builder addPerson(String str) {
            addPerson(new Person.Builder().setUri(str).build());
            return this;
        }

        public Builder addPerson(Person person) {
            this.mPersonList.add(person);
            return this;
        }

        public Builder setGroup(String str) {
            this.mN.mGroupKey = str;
            return this;
        }

        public Builder setGroupSummary(boolean z) {
            setFlag(512, z);
            return this;
        }

        public Builder setSortKey(String str) {
            this.mN.mSortKey = str;
            return this;
        }

        public Builder addExtras(Bundle bundle) {
            if (bundle != null) {
                this.mUserExtras.putAll(bundle);
            }
            return this;
        }

        public Builder setExtras(Bundle bundle) {
            if (bundle != null) {
                this.mUserExtras = bundle;
            }
            return this;
        }

        public Bundle getExtras() {
            return this.mUserExtras;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Bundle getAllExtras() {
            Bundle bundle = (Bundle) this.mUserExtras.clone();
            bundle.putAll(this.mN.extras);
            return bundle;
        }

        @Deprecated
        public Builder addAction(int i, CharSequence charSequence, PendingIntent pendingIntent) {
            this.mActions.add(new Action(i, Notification.safeCharSequence(charSequence), pendingIntent));
            return this;
        }

        public Builder addAction(Action action) {
            if (action != null) {
                this.mActions.add(action);
            }
            return this;
        }

        public Builder setActions(Action... actionArr) {
            this.mActions.clear();
            for (int i = 0; i < actionArr.length; i++) {
                if (actionArr[i] != null) {
                    this.mActions.add(actionArr[i]);
                }
            }
            return this;
        }

        public Builder setStyle(Style style) {
            if (this.mStyle != style) {
                this.mStyle = style;
                if (style != null) {
                    style.setBuilder(this);
                    this.mN.extras.putString("android.template", style.getClass().getName());
                } else {
                    this.mN.extras.remove("android.template");
                }
            }
            return this;
        }

        public Style getStyle() {
            return this.mStyle;
        }

        public Builder setVisibility(int i) {
            this.mN.visibility = i;
            return this;
        }

        public Builder setPublicVersion(Notification notification) {
            if (notification != null) {
                this.mN.publicVersion = new Notification();
                notification.cloneInto(this.mN.publicVersion, true);
            } else {
                this.mN.publicVersion = null;
            }
            return this;
        }

        public Builder extend(Extender extender) {
            extender.extend(this);
            return this;
        }

        public Builder setFlag(int i, boolean z) {
            if (z) {
                Notification notification = this.mN;
                notification.flags = i | notification.flags;
            } else {
                Notification notification2 = this.mN;
                notification2.flags = (~i) & notification2.flags;
            }
            return this;
        }

        public Builder setColor(int i) {
            this.mN.color = i;
            sanitizeColor();
            return this;
        }

        private Drawable getProfileBadgeDrawable() {
            if (this.mContext.getUserId() == 0) {
                return null;
            }
            return this.mContext.getPackageManager().getUserBadgeForDensityNoBackground(new UserHandle(this.mContext.getUserId()), 0);
        }

        private Bitmap getProfileBadge() {
            Drawable profileBadgeDrawable = getProfileBadgeDrawable();
            if (profileBadgeDrawable == null) {
                return null;
            }
            int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.notification_badge_size);
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(dimensionPixelSize, dimensionPixelSize, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmapCreateBitmap);
            profileBadgeDrawable.setBounds(0, 0, dimensionPixelSize, dimensionPixelSize);
            profileBadgeDrawable.draw(canvas);
            return bitmapCreateBitmap;
        }

        private void bindProfileBadge(RemoteViews remoteViews) {
            Bitmap profileBadge = getProfileBadge();
            if (profileBadge != null) {
                remoteViews.setImageViewBitmap(R.id.profile_badge, profileBadge);
                remoteViews.setViewVisibility(R.id.profile_badge, 0);
                if (isColorized()) {
                    remoteViews.setDrawableTint(R.id.profile_badge, false, getPrimaryTextColor(), PorterDuff.Mode.SRC_ATOP);
                }
            }
        }

        public boolean usesStandardHeader() {
            if (this.mN.mUsesStandardHeader) {
                return true;
            }
            if (this.mContext.getApplicationInfo().targetSdkVersion >= 24 && this.mN.contentView == null && this.mN.bigContentView == null) {
                return true;
            }
            return (this.mN.contentView == null || Notification.STANDARD_LAYOUTS.contains(Integer.valueOf(this.mN.contentView.getLayoutId()))) && (this.mN.bigContentView == null || Notification.STANDARD_LAYOUTS.contains(Integer.valueOf(this.mN.bigContentView.getLayoutId())));
        }

        private void resetStandardTemplate(RemoteViews remoteViews) {
            resetNotificationHeader(remoteViews);
            remoteViews.setViewVisibility(R.id.right_icon, 8);
            remoteViews.setViewVisibility(16908310, 8);
            remoteViews.setTextViewText(16908310, null);
            remoteViews.setViewVisibility(R.id.text, 8);
            remoteViews.setTextViewText(R.id.text, null);
            remoteViews.setViewVisibility(R.id.text_line_1, 8);
            remoteViews.setTextViewText(R.id.text_line_1, null);
        }

        private void resetNotificationHeader(RemoteViews remoteViews) {
            remoteViews.setBoolean(R.id.notification_header, "setExpanded", false);
            remoteViews.setTextViewText(R.id.app_name_text, null);
            remoteViews.setViewVisibility(R.id.chronometer, 8);
            remoteViews.setViewVisibility(R.id.header_text, 8);
            remoteViews.setTextViewText(R.id.header_text, null);
            remoteViews.setViewVisibility(R.id.header_text_secondary, 8);
            remoteViews.setTextViewText(R.id.header_text_secondary, null);
            remoteViews.setViewVisibility(R.id.header_text_divider, 8);
            remoteViews.setViewVisibility(R.id.header_text_secondary_divider, 8);
            remoteViews.setViewVisibility(R.id.time_divider, 8);
            remoteViews.setViewVisibility(R.id.time, 8);
            remoteViews.setImageViewIcon(R.id.profile_badge, null);
            remoteViews.setViewVisibility(R.id.profile_badge, 8);
            this.mN.mUsesStandardHeader = false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public RemoteViews applyStandardTemplate(int i, TemplateBindResult templateBindResult) {
            return applyStandardTemplate(i, this.mParams.reset().fillTextsFrom(this), templateBindResult);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public RemoteViews applyStandardTemplate(int i, boolean z, TemplateBindResult templateBindResult) {
            return applyStandardTemplate(i, this.mParams.reset().hasProgress(z).fillTextsFrom(this), templateBindResult);
        }

        private RemoteViews applyStandardTemplate(int i, StandardTemplateParams standardTemplateParams, TemplateBindResult templateBindResult) {
            BuilderRemoteViews builderRemoteViews = new BuilderRemoteViews(this.mContext.getApplicationInfo(), i);
            resetStandardTemplate(builderRemoteViews);
            Bundle bundle = this.mN.extras;
            updateBackgroundColor(builderRemoteViews);
            bindNotificationHeader(builderRemoteViews, standardTemplateParams.ambient, standardTemplateParams.headerTextSecondary);
            bindLargeIconAndReply(builderRemoteViews, standardTemplateParams, templateBindResult);
            boolean zHandleProgressBar = handleProgressBar(standardTemplateParams.hasProgress, builderRemoteViews, bundle);
            if (standardTemplateParams.title != null) {
                builderRemoteViews.setViewVisibility(16908310, 0);
                builderRemoteViews.setTextViewText(16908310, processTextSpans(standardTemplateParams.title));
                if (!standardTemplateParams.ambient) {
                    setTextViewColorPrimary(builderRemoteViews, 16908310);
                }
                builderRemoteViews.setViewLayoutWidth(16908310, zHandleProgressBar ? -2 : -1);
            }
            if (standardTemplateParams.text != null) {
                int i2 = zHandleProgressBar ? R.id.text_line_1 : R.id.text;
                builderRemoteViews.setTextViewText(i2, processTextSpans(standardTemplateParams.text));
                if (!standardTemplateParams.ambient) {
                    setTextViewColorSecondary(builderRemoteViews, i2);
                }
                builderRemoteViews.setViewVisibility(i2, 0);
            }
            setContentMinHeight(builderRemoteViews, zHandleProgressBar || this.mN.hasLargeIcon());
            return builderRemoteViews;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public CharSequence processTextSpans(CharSequence charSequence) {
            return hasForegroundColor() ? NotificationColorUtil.clearColorSpans(charSequence) : charSequence;
        }

        private void setTextViewColorPrimary(RemoteViews remoteViews, int i) {
            ensureColors();
            remoteViews.setTextColor(i, this.mPrimaryTextColor);
        }

        private boolean hasForegroundColor() {
            return this.mForegroundColor != 1;
        }

        public int getPrimaryTextColor() {
            ensureColors();
            return this.mPrimaryTextColor;
        }

        public int getSecondaryTextColor() {
            ensureColors();
            return this.mSecondaryTextColor;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setTextViewColorSecondary(RemoteViews remoteViews, int i) {
            ensureColors();
            remoteViews.setTextColor(i, this.mSecondaryTextColor);
        }

        private void ensureColors() {
            int backgroundColor = getBackgroundColor();
            if (this.mPrimaryTextColor == 1 || this.mSecondaryTextColor == 1 || this.mTextColorsAreForBackground != backgroundColor) {
                this.mTextColorsAreForBackground = backgroundColor;
                if (!hasForegroundColor() || !isColorized()) {
                    this.mPrimaryTextColor = NotificationColorUtil.resolvePrimaryColor(this.mContext, backgroundColor);
                    this.mSecondaryTextColor = NotificationColorUtil.resolveSecondaryColor(this.mContext, backgroundColor);
                    if (backgroundColor == 0 || !isColorized()) {
                        return;
                    }
                    this.mPrimaryTextColor = NotificationColorUtil.findAlphaToMeetContrast(this.mPrimaryTextColor, backgroundColor, 4.5d);
                    this.mSecondaryTextColor = NotificationColorUtil.findAlphaToMeetContrast(this.mSecondaryTextColor, backgroundColor, 4.5d);
                    return;
                }
                double dCalculateLuminance = NotificationColorUtil.calculateLuminance(backgroundColor);
                double dCalculateLuminance2 = NotificationColorUtil.calculateLuminance(this.mForegroundColor);
                double dCalculateContrast = NotificationColorUtil.calculateContrast(this.mForegroundColor, backgroundColor);
                boolean z = (dCalculateLuminance > dCalculateLuminance2 && NotificationColorUtil.satisfiesTextContrast(backgroundColor, -16777216)) || (dCalculateLuminance <= dCalculateLuminance2 && !NotificationColorUtil.satisfiesTextContrast(backgroundColor, -1));
                if (dCalculateContrast < 4.5d) {
                    if (z) {
                        int iFindContrastColor = NotificationColorUtil.findContrastColor(this.mForegroundColor, backgroundColor, true, 4.5d);
                        this.mSecondaryTextColor = iFindContrastColor;
                        this.mPrimaryTextColor = NotificationColorUtil.changeColorLightness(iFindContrastColor, -20);
                        return;
                    } else {
                        int iFindContrastColorAgainstDark = NotificationColorUtil.findContrastColorAgainstDark(this.mForegroundColor, backgroundColor, true, 4.5d);
                        this.mSecondaryTextColor = iFindContrastColorAgainstDark;
                        this.mPrimaryTextColor = NotificationColorUtil.changeColorLightness(iFindContrastColorAgainstDark, 10);
                        return;
                    }
                }
                int i = this.mForegroundColor;
                this.mPrimaryTextColor = i;
                int iChangeColorLightness = NotificationColorUtil.changeColorLightness(i, z ? 20 : -10);
                this.mSecondaryTextColor = iChangeColorLightness;
                if (NotificationColorUtil.calculateContrast(iChangeColorLightness, backgroundColor) < 4.5d) {
                    if (z) {
                        this.mSecondaryTextColor = NotificationColorUtil.findContrastColor(this.mSecondaryTextColor, backgroundColor, true, 4.5d);
                    } else {
                        this.mSecondaryTextColor = NotificationColorUtil.findContrastColorAgainstDark(this.mSecondaryTextColor, backgroundColor, true, 4.5d);
                    }
                    this.mPrimaryTextColor = NotificationColorUtil.changeColorLightness(this.mSecondaryTextColor, z ? -20 : 10);
                }
            }
        }

        private void updateBackgroundColor(RemoteViews remoteViews) {
            if (isColorized()) {
                remoteViews.setInt(R.id.status_bar_latest_event_content, "setBackgroundColor", getBackgroundColor());
            } else {
                remoteViews.setInt(R.id.status_bar_latest_event_content, "setBackgroundResource", 0);
            }
        }

        void setContentMinHeight(RemoteViews remoteViews, boolean z) {
            remoteViews.setInt(R.id.notification_main_column, "setMinimumHeight", z ? this.mContext.getResources().getDimensionPixelSize(R.dimen.notification_min_content_height) : 0);
        }

        private boolean handleProgressBar(boolean z, RemoteViews remoteViews, Bundle bundle) {
            int i = bundle.getInt("android.progressMax", 0);
            int i2 = bundle.getInt("android.progress", 0);
            boolean z2 = bundle.getBoolean("android.progressIndeterminate");
            if (z && (i != 0 || z2)) {
                remoteViews.setViewVisibility(16908301, 0);
                remoteViews.setProgressBar(16908301, i, i2, z2);
                remoteViews.setProgressBackgroundTintList(16908301, ColorStateList.valueOf(this.mContext.getColor(R.color.notification_progress_background_color)));
                if (this.mN.color == 0) {
                    return true;
                }
                ColorStateList colorStateListValueOf = ColorStateList.valueOf(resolveContrastColor());
                remoteViews.setProgressTintList(16908301, colorStateListValueOf);
                remoteViews.setProgressIndeterminateTintList(16908301, colorStateListValueOf);
                return true;
            }
            remoteViews.setViewVisibility(16908301, 8);
            return false;
        }

        private void bindLargeIconAndReply(RemoteViews remoteViews, StandardTemplateParams standardTemplateParams, TemplateBindResult templateBindResult) {
            boolean z = true;
            int i = 0;
            boolean zBindLargeIcon = bindLargeIcon(remoteViews, standardTemplateParams.hideLargeIcon || standardTemplateParams.ambient);
            if (!standardTemplateParams.hideReplyIcon && !standardTemplateParams.ambient) {
                z = false;
            }
            boolean zBindReplyIcon = bindReplyIcon(remoteViews, z);
            if (!zBindLargeIcon && !zBindReplyIcon) {
                i = 8;
            }
            remoteViews.setViewVisibility(R.id.right_icon_container, i);
            int iCalculateMarginEnd = calculateMarginEnd(zBindLargeIcon, zBindReplyIcon);
            remoteViews.setViewLayoutMarginEnd(R.id.line1, iCalculateMarginEnd);
            remoteViews.setViewLayoutMarginEnd(R.id.text, iCalculateMarginEnd);
            remoteViews.setViewLayoutMarginEnd(16908301, iCalculateMarginEnd);
            if (templateBindResult != null) {
                templateBindResult.setIconMarginEnd(iCalculateMarginEnd);
            }
        }

        private int calculateMarginEnd(boolean z, boolean z2) {
            int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.notification_content_margin_end);
            int dimensionPixelSize2 = this.mContext.getResources().getDimensionPixelSize(R.dimen.notification_right_icon_size);
            int dimensionPixelSize3 = z2 ? (dimensionPixelSize2 + 0) - (this.mContext.getResources().getDimensionPixelSize(R.dimen.notification_reply_inset) * 2) : 0;
            if (z) {
                dimensionPixelSize3 += dimensionPixelSize2;
                if (z2) {
                    dimensionPixelSize3 += dimensionPixelSize;
                }
            }
            return (z2 || z) ? dimensionPixelSize3 + dimensionPixelSize : dimensionPixelSize3;
        }

        private boolean bindLargeIcon(RemoteViews remoteViews, boolean z) {
            if (this.mN.mLargeIcon == null && this.mN.largeIcon != null) {
                Notification notification = this.mN;
                notification.mLargeIcon = Icon.createWithBitmap(notification.largeIcon);
            }
            boolean z2 = (this.mN.mLargeIcon == null || z) ? false : true;
            if (z2) {
                remoteViews.setViewVisibility(R.id.right_icon, 0);
                remoteViews.setImageViewIcon(R.id.right_icon, this.mN.mLargeIcon);
                processLargeLegacyIcon(this.mN.mLargeIcon, remoteViews);
            }
            return z2;
        }

        private boolean bindReplyIcon(RemoteViews remoteViews, boolean z) {
            Action action;
            boolean z2 = !z;
            if (z2) {
                Action actionFindReplyAction = findReplyAction();
                action = actionFindReplyAction;
                z2 = actionFindReplyAction != null;
            } else {
                action = null;
            }
            if (z2) {
                remoteViews.setViewVisibility(R.id.reply_icon_action, 0);
                remoteViews.setDrawableTint(R.id.reply_icon_action, false, getNeutralColor(), PorterDuff.Mode.SRC_ATOP);
                remoteViews.setOnClickPendingIntent(R.id.reply_icon_action, action.actionIntent);
                remoteViews.setRemoteInputs(R.id.reply_icon_action, action.mRemoteInputs);
            } else {
                remoteViews.setRemoteInputs(R.id.reply_icon_action, null);
            }
            remoteViews.setViewVisibility(R.id.reply_icon_action, z2 ? 0 : 8);
            return z2;
        }

        private Action findReplyAction() {
            ArrayList<Action> arrayList = this.mActions;
            ArrayList<Action> arrayList2 = this.mOriginalActions;
            if (arrayList2 != null) {
                arrayList = arrayList2;
            }
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                Action action = arrayList.get(i);
                if (hasValidRemoteInput(action)) {
                    return action;
                }
            }
            return null;
        }

        private void bindNotificationHeader(RemoteViews remoteViews, boolean z, CharSequence charSequence) {
            bindSmallIcon(remoteViews, z);
            bindHeaderAppName(remoteViews, z);
            if (!z) {
                bindHeaderText(remoteViews);
                bindHeaderTextSecondary(remoteViews, charSequence);
                bindHeaderChronometerAndTime(remoteViews);
                bindProfileBadge(remoteViews);
            }
            bindActivePermissions(remoteViews, z);
            bindExpandButton(remoteViews);
            this.mN.mUsesStandardHeader = true;
        }

        private void bindActivePermissions(RemoteViews remoteViews, boolean z) {
            int iResolveAmbientColor = z ? resolveAmbientColor() : getNeutralColor();
            remoteViews.setDrawableTint(R.id.camera, false, iResolveAmbientColor, PorterDuff.Mode.SRC_ATOP);
            remoteViews.setDrawableTint(R.id.mic, false, iResolveAmbientColor, PorterDuff.Mode.SRC_ATOP);
            remoteViews.setDrawableTint(R.id.overlay, false, iResolveAmbientColor, PorterDuff.Mode.SRC_ATOP);
        }

        private void bindExpandButton(RemoteViews remoteViews) {
            int primaryTextColor = isColorized() ? getPrimaryTextColor() : getSecondaryTextColor();
            remoteViews.setDrawableTint(R.id.expand_button, false, primaryTextColor, PorterDuff.Mode.SRC_ATOP);
            remoteViews.setInt(R.id.notification_header, "setOriginalNotificationColor", primaryTextColor);
        }

        private void bindHeaderChronometerAndTime(RemoteViews remoteViews) {
            if (!showsTimeOrChronometer()) {
                remoteViews.setLong(R.id.time, "setTime", this.mN.when != 0 ? this.mN.when : this.mN.creationTime);
                return;
            }
            remoteViews.setViewVisibility(R.id.time_divider, 0);
            setTextViewColorSecondary(remoteViews, R.id.time_divider);
            if (this.mN.extras.getBoolean("android.showChronometer")) {
                remoteViews.setViewVisibility(R.id.chronometer, 0);
                remoteViews.setLong(R.id.chronometer, "setBase", this.mN.when + (SystemClock.elapsedRealtime() - System.currentTimeMillis()));
                remoteViews.setBoolean(R.id.chronometer, "setStarted", true);
                remoteViews.setChronometerCountDown(R.id.chronometer, this.mN.extras.getBoolean(Notification.EXTRA_CHRONOMETER_COUNT_DOWN));
                setTextViewColorSecondary(remoteViews, R.id.chronometer);
                return;
            }
            remoteViews.setViewVisibility(R.id.time, 0);
            remoteViews.setLong(R.id.time, "setTime", this.mN.when);
            setTextViewColorSecondary(remoteViews, R.id.time);
        }

        private void bindHeaderText(RemoteViews remoteViews) {
            Style style;
            CharSequence charSequence = this.mN.extras.getCharSequence("android.subText");
            if (charSequence == null && (style = this.mStyle) != null && style.mSummaryTextSet && this.mStyle.hasSummaryInHeader()) {
                charSequence = this.mStyle.mSummaryText;
            }
            if (charSequence == null && this.mContext.getApplicationInfo().targetSdkVersion < 24 && this.mN.extras.getCharSequence("android.infoText") != null) {
                charSequence = this.mN.extras.getCharSequence("android.infoText");
            }
            if (charSequence != null) {
                remoteViews.setTextViewText(R.id.header_text, processTextSpans(processLegacyText(charSequence)));
                setTextViewColorSecondary(remoteViews, R.id.header_text);
                remoteViews.setViewVisibility(R.id.header_text, 0);
                remoteViews.setViewVisibility(R.id.header_text_divider, 0);
                setTextViewColorSecondary(remoteViews, R.id.header_text_divider);
            }
        }

        private void bindHeaderTextSecondary(RemoteViews remoteViews, CharSequence charSequence) {
            if (TextUtils.isEmpty(charSequence)) {
                return;
            }
            remoteViews.setTextViewText(R.id.header_text_secondary, processTextSpans(processLegacyText(charSequence)));
            setTextViewColorSecondary(remoteViews, R.id.header_text_secondary);
            remoteViews.setViewVisibility(R.id.header_text_secondary, 0);
            remoteViews.setViewVisibility(R.id.header_text_secondary_divider, 0);
            setTextViewColorSecondary(remoteViews, R.id.header_text_secondary_divider);
        }

        public String loadHeaderAppName() {
            String str;
            String string;
            PackageManager packageManager = this.mContext.getPackageManager();
            if (this.mN.extras.containsKey(Notification.EXTRA_SUBSTITUTE_APP_NAME)) {
                String packageName = this.mContext.getPackageName();
                string = this.mN.extras.getString(Notification.EXTRA_SUBSTITUTE_APP_NAME);
                if (packageManager.checkPermission(Manifest.permission.SUBSTITUTE_NOTIFICATION_APP_NAME, packageName) != 0) {
                    str = string;
                    Log.w(Notification.TAG, "warning: pkg " + packageName + " attempting to substitute app name '" + string + "' without holding perm " + Manifest.permission.SUBSTITUTE_NOTIFICATION_APP_NAME);
                    str = null;
                }
            } else {
                str = null;
            }
            str = string;
            boolean zIsEmpty = TextUtils.isEmpty(str);
            CharSequence applicationLabel = str;
            if (zIsEmpty) {
                applicationLabel = packageManager.getApplicationLabel(this.mContext.getApplicationInfo());
            }
            if (TextUtils.isEmpty(applicationLabel)) {
                return null;
            }
            return String.valueOf(applicationLabel);
        }

        private void bindHeaderAppName(RemoteViews remoteViews, boolean z) {
            remoteViews.setTextViewText(R.id.app_name_text, loadHeaderAppName());
            if (isColorized() && !z) {
                setTextViewColorPrimary(remoteViews, R.id.app_name_text);
            } else {
                remoteViews.setTextColor(R.id.app_name_text, z ? resolveAmbientColor() : getSecondaryTextColor());
            }
        }

        private void bindSmallIcon(RemoteViews remoteViews, boolean z) {
            if (this.mN.mSmallIcon == null && this.mN.icon != 0) {
                Notification notification = this.mN;
                notification.mSmallIcon = Icon.createWithResource(this.mContext, notification.icon);
            }
            remoteViews.setImageViewIcon(16908294, this.mN.mSmallIcon);
            remoteViews.setInt(16908294, "setImageLevel", this.mN.iconLevel);
            processSmallIconColor(this.mN.mSmallIcon, remoteViews, z);
        }

        private boolean showsTimeOrChronometer() {
            return this.mN.showsTime() || this.mN.showsChronometer();
        }

        private void resetStandardTemplateWithActions(RemoteViews remoteViews) {
            remoteViews.setViewVisibility(R.id.actions, 8);
            remoteViews.removeAllViews(R.id.actions);
            remoteViews.setViewVisibility(R.id.notification_material_reply_container, 8);
            remoteViews.setTextViewText(R.id.notification_material_reply_text_1, null);
            remoteViews.setViewVisibility(R.id.notification_material_reply_text_1_container, 8);
            remoteViews.setViewVisibility(R.id.notification_material_reply_progress, 8);
            remoteViews.setViewVisibility(R.id.notification_material_reply_text_2, 8);
            remoteViews.setTextViewText(R.id.notification_material_reply_text_2, null);
            remoteViews.setViewVisibility(R.id.notification_material_reply_text_3, 8);
            remoteViews.setTextViewText(R.id.notification_material_reply_text_3, null);
            remoteViews.setViewLayoutMarginBottomDimen(R.id.notification_action_list_margin_target, R.dimen.notification_content_margin);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public RemoteViews applyStandardTemplateWithActions(int i, TemplateBindResult templateBindResult) {
            return applyStandardTemplateWithActions(i, this.mParams.reset().fillTextsFrom(this), templateBindResult);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public RemoteViews applyStandardTemplateWithActions(int i, StandardTemplateParams standardTemplateParams, TemplateBindResult templateBindResult) {
            boolean z;
            RemoteViews remoteViewsApplyStandardTemplate = applyStandardTemplate(i, standardTemplateParams, templateBindResult);
            resetStandardTemplateWithActions(remoteViewsApplyStandardTemplate);
            int size = this.mActions.size();
            boolean z2 = (this.mN.fullScreenIntent == null || standardTemplateParams.ambient) ? false : true;
            remoteViewsApplyStandardTemplate.setBoolean(R.id.actions, "setEmphasizedMode", z2);
            if (size > 0) {
                remoteViewsApplyStandardTemplate.setViewVisibility(R.id.actions_container, 0);
                remoteViewsApplyStandardTemplate.setViewVisibility(R.id.actions, 0);
                remoteViewsApplyStandardTemplate.setViewLayoutMarginBottomDimen(R.id.notification_action_list_margin_target, 0);
                if (size > 3) {
                    size = 3;
                }
                z = false;
                for (int i2 = 0; i2 < size; i2++) {
                    Action action = this.mActions.get(i2);
                    boolean zHasValidRemoteInput = hasValidRemoteInput(action);
                    z |= zHasValidRemoteInput;
                    RemoteViews remoteViewsGenerateActionButton = generateActionButton(action, z2, standardTemplateParams.ambient);
                    if (zHasValidRemoteInput && !z2) {
                        remoteViewsGenerateActionButton.setInt(R.id.action0, "setBackgroundResource", 0);
                    }
                    remoteViewsApplyStandardTemplate.addView(R.id.actions, remoteViewsGenerateActionButton);
                }
            } else {
                remoteViewsApplyStandardTemplate.setViewVisibility(R.id.actions_container, 8);
                z = false;
            }
            CharSequence[] charSequenceArray = this.mN.extras.getCharSequenceArray("android.remoteInputHistory");
            if (!standardTemplateParams.ambient && z && charSequenceArray != null && charSequenceArray.length > 0 && !TextUtils.isEmpty(charSequenceArray[0]) && standardTemplateParams.maxRemoteInputHistory > 0) {
                boolean z3 = this.mN.extras.getBoolean(Notification.EXTRA_SHOW_REMOTE_INPUT_SPINNER);
                remoteViewsApplyStandardTemplate.setViewVisibility(R.id.notification_material_reply_container, 0);
                remoteViewsApplyStandardTemplate.setViewVisibility(R.id.notification_material_reply_text_1_container, 0);
                remoteViewsApplyStandardTemplate.setTextViewText(R.id.notification_material_reply_text_1, processTextSpans(charSequenceArray[0]));
                setTextViewColorSecondary(remoteViewsApplyStandardTemplate, R.id.notification_material_reply_text_1);
                remoteViewsApplyStandardTemplate.setViewVisibility(R.id.notification_material_reply_progress, z3 ? 0 : 8);
                remoteViewsApplyStandardTemplate.setProgressIndeterminateTintList(R.id.notification_material_reply_progress, ColorStateList.valueOf(isColorized() ? getPrimaryTextColor() : resolveContrastColor()));
                if (charSequenceArray.length > 1 && !TextUtils.isEmpty(charSequenceArray[1]) && standardTemplateParams.maxRemoteInputHistory > 1) {
                    remoteViewsApplyStandardTemplate.setViewVisibility(R.id.notification_material_reply_text_2, 0);
                    remoteViewsApplyStandardTemplate.setTextViewText(R.id.notification_material_reply_text_2, processTextSpans(charSequenceArray[1]));
                    setTextViewColorSecondary(remoteViewsApplyStandardTemplate, R.id.notification_material_reply_text_2);
                    if (charSequenceArray.length > 2 && !TextUtils.isEmpty(charSequenceArray[2]) && standardTemplateParams.maxRemoteInputHistory > 2) {
                        remoteViewsApplyStandardTemplate.setViewVisibility(R.id.notification_material_reply_text_3, 0);
                        remoteViewsApplyStandardTemplate.setTextViewText(R.id.notification_material_reply_text_3, processTextSpans(charSequenceArray[2]));
                        setTextViewColorSecondary(remoteViewsApplyStandardTemplate, R.id.notification_material_reply_text_3);
                    }
                }
            }
            return remoteViewsApplyStandardTemplate;
        }

        private boolean hasValidRemoteInput(Action action) {
            RemoteInput[] remoteInputs;
            if (TextUtils.isEmpty(action.title) || action.actionIntent == null || (remoteInputs = action.getRemoteInputs()) == null) {
                return false;
            }
            for (RemoteInput remoteInput : remoteInputs) {
                CharSequence[] choices = remoteInput.getChoices();
                if (remoteInput.getAllowFreeFormInput()) {
                    return true;
                }
                if (choices != null && choices.length != 0) {
                    return true;
                }
            }
            return false;
        }

        public RemoteViews createContentView() {
            return createContentView(false);
        }

        public RemoteViews createContentView(boolean z) {
            RemoteViews remoteViewsMakeContentView;
            if (this.mN.contentView != null && useExistingRemoteView()) {
                return this.mN.contentView;
            }
            Style style = this.mStyle;
            return (style == null || (remoteViewsMakeContentView = style.makeContentView(z)) == null) ? applyStandardTemplate(getBaseLayoutResource(), null) : remoteViewsMakeContentView;
        }

        private boolean useExistingRemoteView() {
            Style style = this.mStyle;
            return style == null || !(style.displayCustomViewInline() || this.mRebuildStyledRemoteViews);
        }

        public RemoteViews createBigContentView() {
            if (this.mN.bigContentView != null && useExistingRemoteView()) {
                return this.mN.bigContentView;
            }
            Style style = this.mStyle;
            RemoteViews remoteViewsApplyStandardTemplateWithActions = null;
            if (style != null) {
                remoteViewsApplyStandardTemplateWithActions = style.makeBigContentView();
                hideLine1Text(remoteViewsApplyStandardTemplateWithActions);
            } else if (this.mActions.size() != 0) {
                remoteViewsApplyStandardTemplateWithActions = applyStandardTemplateWithActions(getBigBaseLayoutResource(), null);
            }
            makeHeaderExpanded(remoteViewsApplyStandardTemplateWithActions);
            return remoteViewsApplyStandardTemplateWithActions;
        }

        public RemoteViews makeNotificationHeader(boolean z) {
            Boolean bool = (Boolean) this.mN.extras.get(Notification.EXTRA_COLORIZED);
            this.mN.extras.putBoolean(Notification.EXTRA_COLORIZED, false);
            BuilderRemoteViews builderRemoteViews = new BuilderRemoteViews(this.mContext.getApplicationInfo(), z ? R.layout.notification_template_ambient_header : R.layout.notification_template_header);
            resetNotificationHeader(builderRemoteViews);
            bindNotificationHeader(builderRemoteViews, z, null);
            if (bool != null) {
                this.mN.extras.putBoolean(Notification.EXTRA_COLORIZED, bool.booleanValue());
            } else {
                this.mN.extras.remove(Notification.EXTRA_COLORIZED);
            }
            return builderRemoteViews;
        }

        public RemoteViews makeAmbientNotification() {
            return applyStandardTemplateWithActions(R.layout.notification_template_material_ambient, this.mParams.reset().ambient(true).fillTextsFrom(this).hasProgress(false), null);
        }

        private void hideLine1Text(RemoteViews remoteViews) {
            if (remoteViews != null) {
                remoteViews.setViewVisibility(R.id.text_line_1, 8);
            }
        }

        public static void makeHeaderExpanded(RemoteViews remoteViews) {
            if (remoteViews != null) {
                remoteViews.setBoolean(R.id.notification_header, "setExpanded", true);
            }
        }

        public RemoteViews createHeadsUpContentView(boolean z) {
            if (this.mN.headsUpContentView != null && useExistingRemoteView()) {
                return this.mN.headsUpContentView;
            }
            Style style = this.mStyle;
            if (style != null) {
                RemoteViews remoteViewsMakeHeadsUpContentView = style.makeHeadsUpContentView(z);
                if (remoteViewsMakeHeadsUpContentView != null) {
                    return remoteViewsMakeHeadsUpContentView;
                }
            } else if (this.mActions.size() == 0) {
                return null;
            }
            return applyStandardTemplateWithActions(getBigBaseLayoutResource(), this.mParams.reset().fillTextsFrom(this).setMaxRemoteInputHistory(1), null);
        }

        public RemoteViews createHeadsUpContentView() {
            return createHeadsUpContentView(false);
        }

        public RemoteViews makePublicContentView() {
            return makePublicView(false);
        }

        public RemoteViews makePublicAmbientNotification() {
            return makePublicView(true);
        }

        private RemoteViews makePublicView(boolean z) {
            RemoteViews remoteViewsMakeNotificationHeader;
            if (this.mN.publicVersion != null) {
                Builder builderRecoverBuilder = recoverBuilder(this.mContext, this.mN.publicVersion);
                return z ? builderRecoverBuilder.makeAmbientNotification() : builderRecoverBuilder.createContentView();
            }
            Bundle bundle = this.mN.extras;
            Style style = this.mStyle;
            this.mStyle = null;
            Icon icon = this.mN.mLargeIcon;
            this.mN.mLargeIcon = null;
            Bitmap bitmap = this.mN.largeIcon;
            this.mN.largeIcon = null;
            ArrayList<Action> arrayList = this.mActions;
            this.mActions = new ArrayList<>();
            Bundle bundle2 = new Bundle();
            bundle2.putBoolean("android.showWhen", bundle.getBoolean("android.showWhen"));
            bundle2.putBoolean("android.showChronometer", bundle.getBoolean("android.showChronometer"));
            bundle2.putBoolean(Notification.EXTRA_CHRONOMETER_COUNT_DOWN, bundle.getBoolean(Notification.EXTRA_CHRONOMETER_COUNT_DOWN));
            String string = bundle.getString(Notification.EXTRA_SUBSTITUTE_APP_NAME);
            if (string != null) {
                bundle2.putString(Notification.EXTRA_SUBSTITUTE_APP_NAME, string);
            }
            this.mN.extras = bundle2;
            if (z) {
                bundle2.putCharSequence("android.title", this.mContext.getString(R.string.notification_hidden_text));
                remoteViewsMakeNotificationHeader = makeAmbientNotification();
            } else {
                remoteViewsMakeNotificationHeader = makeNotificationHeader(false);
                remoteViewsMakeNotificationHeader.setBoolean(R.id.notification_header, "setExpandOnlyOnButton", true);
            }
            this.mN.extras = bundle;
            this.mN.mLargeIcon = icon;
            this.mN.largeIcon = bitmap;
            this.mActions = arrayList;
            this.mStyle = style;
            return remoteViewsMakeNotificationHeader;
        }

        public RemoteViews makeLowPriorityContentView(boolean z) {
            int i = this.mN.color;
            this.mN.color = 0;
            CharSequence charSequence = this.mN.extras.getCharSequence("android.subText");
            if (!z || TextUtils.isEmpty(charSequence)) {
                CharSequence charSequenceCreateSummaryText = createSummaryText();
                if (!TextUtils.isEmpty(charSequenceCreateSummaryText)) {
                    this.mN.extras.putCharSequence("android.subText", charSequenceCreateSummaryText);
                }
            }
            RemoteViews remoteViewsMakeNotificationHeader = makeNotificationHeader(false);
            remoteViewsMakeNotificationHeader.setBoolean(R.id.notification_header, "setAcceptAllTouches", true);
            if (charSequence != null) {
                this.mN.extras.putCharSequence("android.subText", charSequence);
            } else {
                this.mN.extras.remove("android.subText");
            }
            this.mN.color = i;
            return remoteViewsMakeNotificationHeader;
        }

        private CharSequence createSummaryText() {
            CharSequence charSequence = this.mN.extras.getCharSequence("android.title");
            if (USE_ONLY_TITLE_IN_LOW_PRIORITY_SUMMARY) {
                return charSequence;
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            if (charSequence == null) {
                charSequence = this.mN.extras.getCharSequence("android.title.big");
            }
            BidiFormatter bidiFormatter = BidiFormatter.getInstance();
            if (charSequence != null) {
                spannableStringBuilder.append(bidiFormatter.unicodeWrap(charSequence));
            }
            CharSequence charSequence2 = this.mN.extras.getCharSequence("android.text");
            if (charSequence != null && charSequence2 != null) {
                spannableStringBuilder.append(bidiFormatter.unicodeWrap(this.mContext.getText(R.string.notification_header_divider_symbol_with_spaces)));
            }
            if (charSequence2 != null) {
                spannableStringBuilder.append(bidiFormatter.unicodeWrap(charSequence2));
            }
            return spannableStringBuilder;
        }

        private RemoteViews generateActionButton(Action action, boolean z, boolean z2) {
            int actionLayoutResource;
            CharSequence charSequenceEnsureColorSpanContrast;
            int primaryTextColor;
            boolean z3 = action.actionIntent == null;
            ApplicationInfo applicationInfo = this.mContext.getApplicationInfo();
            if (z) {
                actionLayoutResource = getEmphasizedActionLayoutResource();
            } else if (z3) {
                actionLayoutResource = getActionTombstoneLayoutResource();
            } else {
                actionLayoutResource = getActionLayoutResource();
            }
            BuilderRemoteViews builderRemoteViews = new BuilderRemoteViews(applicationInfo, actionLayoutResource);
            if (!z3) {
                builderRemoteViews.setOnClickPendingIntent(R.id.action0, action.actionIntent);
            }
            builderRemoteViews.setContentDescription(R.id.action0, action.title);
            if (action.mRemoteInputs != null) {
                builderRemoteViews.setRemoteInputs(R.id.action0, action.mRemoteInputs);
            }
            if (z) {
                CharSequence charSequence = action.title;
                ColorStateList[] colorStateListArr = null;
                int iResolveBackgroundColor = resolveBackgroundColor();
                if (isLegacy()) {
                    charSequenceEnsureColorSpanContrast = NotificationColorUtil.clearColorSpans(charSequence);
                } else {
                    colorStateListArr = new ColorStateList[1];
                    charSequenceEnsureColorSpanContrast = ensureColorSpanContrast(charSequence, iResolveBackgroundColor, colorStateListArr);
                }
                builderRemoteViews.setTextViewText(R.id.action0, processTextSpans(charSequenceEnsureColorSpanContrast));
                setTextViewColorPrimary(builderRemoteViews, R.id.action0);
                boolean z4 = (colorStateListArr == null || colorStateListArr[0] == null) ? false : true;
                if (z4) {
                    iResolveBackgroundColor = colorStateListArr[0].getDefaultColor();
                    primaryTextColor = NotificationColorUtil.resolvePrimaryColor(this.mContext, iResolveBackgroundColor);
                    builderRemoteViews.setTextColor(R.id.action0, primaryTextColor);
                } else if (this.mN.color != 0 && !isColorized() && this.mTintActionButtons) {
                    primaryTextColor = resolveContrastColor();
                    builderRemoteViews.setTextColor(R.id.action0, primaryTextColor);
                } else {
                    primaryTextColor = getPrimaryTextColor();
                }
                builderRemoteViews.setColorStateList(R.id.action0, "setRippleColor", ColorStateList.valueOf((primaryTextColor & 16777215) | 855638016));
                builderRemoteViews.setColorStateList(R.id.action0, "setButtonBackground", ColorStateList.valueOf(iResolveBackgroundColor));
                builderRemoteViews.setBoolean(R.id.action0, "setHasStroke", !z4);
            } else {
                builderRemoteViews.setTextViewText(R.id.action0, processTextSpans(processLegacyText(action.title)));
                if (isColorized() && !z2) {
                    setTextViewColorPrimary(builderRemoteViews, R.id.action0);
                } else if (this.mN.color != 0 && this.mTintActionButtons) {
                    builderRemoteViews.setTextColor(R.id.action0, z2 ? resolveAmbientColor() : resolveContrastColor());
                }
            }
            return builderRemoteViews;
        }

        private CharSequence ensureColorSpanContrast(CharSequence charSequence, int i, ColorStateList[] colorStateListArr) {
            int i2;
            ForegroundColorSpan foregroundColorSpan;
            ColorStateList colorStateList;
            if (!(charSequence instanceof Spanned)) {
                return charSequence;
            }
            Spanned spanned = (Spanned) charSequence;
            boolean z = false;
            Object[] spans = spanned.getSpans(0, spanned.length(), Object.class);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(spanned.toString());
            int length = spans.length;
            int i3 = 0;
            while (i3 < length) {
                Object obj = spans[i3];
                int spanStart = spanned.getSpanStart(obj);
                int spanEnd = spanned.getSpanEnd(obj);
                boolean z2 = spanEnd - spanStart == charSequence.length() ? true : z;
                Object underlying = obj instanceof CharacterStyle ? ((CharacterStyle) obj).getUnderlying() : obj;
                if (underlying instanceof TextAppearanceSpan) {
                    TextAppearanceSpan textAppearanceSpan = (TextAppearanceSpan) underlying;
                    ColorStateList textColor = textAppearanceSpan.getTextColor();
                    if (textColor != null) {
                        int[] colors = textColor.getColors();
                        int length2 = colors.length;
                        int[] iArr = new int[length2];
                        int i4 = 0;
                        while (i4 < length2) {
                            iArr[i4] = NotificationColorUtil.ensureLargeTextContrast(colors[i4], i, this.mInNightMode);
                            i4++;
                            length = length;
                            colors = colors;
                        }
                        i2 = length;
                        ColorStateList colorStateList2 = new ColorStateList((int[][]) textColor.getStates().clone(), iArr);
                        if (z2) {
                            colorStateListArr[0] = colorStateList2;
                            colorStateList = null;
                        } else {
                            colorStateList = colorStateList2;
                        }
                        underlying = new TextAppearanceSpan(textAppearanceSpan.getFamily(), textAppearanceSpan.getTextStyle(), textAppearanceSpan.getTextSize(), colorStateList, textAppearanceSpan.getLinkTextColor());
                    } else {
                        i2 = length;
                    }
                    z = false;
                } else {
                    spans = spans;
                    i2 = length;
                    if (underlying instanceof ForegroundColorSpan) {
                        int iEnsureLargeTextContrast = NotificationColorUtil.ensureLargeTextContrast(((ForegroundColorSpan) underlying).getForegroundColor(), i, this.mInNightMode);
                        if (z2) {
                            z = false;
                            colorStateListArr[0] = ColorStateList.valueOf(iEnsureLargeTextContrast);
                            foregroundColorSpan = null;
                        } else {
                            z = false;
                            foregroundColorSpan = new ForegroundColorSpan(iEnsureLargeTextContrast);
                        }
                        underlying = foregroundColorSpan;
                    } else {
                        z = false;
                        underlying = obj;
                    }
                }
                if (underlying != null) {
                    spannableStringBuilder.setSpan(underlying, spanStart, spanEnd, spanned.getSpanFlags(obj));
                }
                i3++;
                spans = spans;
                length = i2;
            }
            return spannableStringBuilder;
        }

        private boolean isLegacy() {
            if (!this.mIsLegacyInitialized) {
                this.mIsLegacy = this.mContext.getApplicationInfo().targetSdkVersion < 21;
                this.mIsLegacyInitialized = true;
            }
            return this.mIsLegacy;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public CharSequence processLegacyText(CharSequence charSequence) {
            return processLegacyText(charSequence, false);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public CharSequence processLegacyText(CharSequence charSequence, boolean z) {
            return (isLegacy() || textColorsNeedInversion()) != z ? getColorUtil().invertCharSequenceColors(charSequence) : charSequence;
        }

        private void processSmallIconColor(Icon icon, RemoteViews remoteViews, boolean z) {
            int iResolveContrastColor;
            boolean z2 = !isLegacy() || getColorUtil().isGrayscaleIcon(this.mContext, icon);
            if (z) {
                iResolveContrastColor = resolveAmbientColor();
            } else if (isColorized()) {
                iResolveContrastColor = getPrimaryTextColor();
            } else {
                iResolveContrastColor = resolveContrastColor();
            }
            if (z2) {
                remoteViews.setDrawableTint(16908294, false, iResolveContrastColor, PorterDuff.Mode.SRC_ATOP);
            }
            remoteViews.setInt(R.id.notification_header, "setOriginalIconColor", z2 ? iResolveContrastColor : 1);
        }

        private void processLargeLegacyIcon(Icon icon, RemoteViews remoteViews) {
            if (icon != null && isLegacy() && getColorUtil().isGrayscaleIcon(this.mContext, icon)) {
                remoteViews.setDrawableTint(16908294, false, resolveContrastColor(), PorterDuff.Mode.SRC_ATOP);
            }
        }

        private void sanitizeColor() {
            if (this.mN.color != 0) {
                this.mN.color |= -16777216;
            }
        }

        int resolveContrastColor() {
            int iResolveContrastColor;
            int i;
            if (this.mCachedContrastColorIsFor == this.mN.color && (i = this.mCachedContrastColor) != 1) {
                return i;
            }
            int color = this.mContext.getColor(R.color.notification_material_background_color);
            if (this.mN.color == 0) {
                ensureColors();
                iResolveContrastColor = NotificationColorUtil.resolveDefaultColor(this.mContext, color);
            } else {
                iResolveContrastColor = NotificationColorUtil.resolveContrastColor(this.mContext, this.mN.color, color, this.mInNightMode);
            }
            if (Color.alpha(iResolveContrastColor) < 255) {
                iResolveContrastColor = NotificationColorUtil.compositeColors(iResolveContrastColor, color);
            }
            this.mCachedContrastColorIsFor = this.mN.color;
            this.mCachedContrastColor = iResolveContrastColor;
            return iResolveContrastColor;
        }

        int resolveNeutralColor() {
            int i = this.mNeutralColor;
            if (i != 1) {
                return i;
            }
            int color = this.mContext.getColor(R.color.notification_material_background_color);
            int iResolveDefaultColor = NotificationColorUtil.resolveDefaultColor(this.mContext, color);
            this.mNeutralColor = iResolveDefaultColor;
            if (Color.alpha(iResolveDefaultColor) < 255) {
                this.mNeutralColor = NotificationColorUtil.compositeColors(this.mNeutralColor, color);
            }
            return this.mNeutralColor;
        }

        int resolveAmbientColor() {
            if (this.mCachedAmbientColorIsFor == this.mN.color && this.mCachedAmbientColorIsFor != 1) {
                return this.mCachedAmbientColor;
            }
            int iResolveAmbientColor = NotificationColorUtil.resolveAmbientColor(this.mContext, this.mN.color);
            this.mCachedAmbientColorIsFor = this.mN.color;
            this.mCachedAmbientColor = iResolveAmbientColor;
            return iResolveAmbientColor;
        }

        public Notification buildUnstyled() {
            if (this.mActions.size() > 0) {
                this.mN.actions = new Action[this.mActions.size()];
                this.mActions.toArray(this.mN.actions);
            }
            if (!this.mPersonList.isEmpty()) {
                this.mN.extras.putParcelableArrayList(Notification.EXTRA_PEOPLE_LIST, this.mPersonList);
            }
            if (this.mN.bigContentView != null || this.mN.contentView != null || this.mN.headsUpContentView != null) {
                this.mN.extras.putBoolean(Notification.EXTRA_CONTAINS_CUSTOM_VIEW, true);
            }
            return this.mN;
        }

        public static Builder recoverBuilder(Context context, Notification notification) {
            ApplicationInfo applicationInfo = (ApplicationInfo) notification.extras.getParcelable(Notification.EXTRA_BUILDER_APPLICATION_INFO);
            if (applicationInfo != null) {
                try {
                    context = context.createApplicationContext(applicationInfo, 4);
                } catch (PackageManager.NameNotFoundException unused) {
                    Log.e(Notification.TAG, "ApplicationInfo " + applicationInfo + " not found");
                }
            }
            return new Builder(context, notification);
        }

        @Deprecated
        public Notification getNotification() {
            return build();
        }

        public Notification build() {
            if (this.mUserExtras != null) {
                this.mN.extras = getAllExtras();
            }
            this.mN.creationTime = System.currentTimeMillis();
            Notification.addFieldsFromContext(this.mContext, this.mN);
            buildUnstyled();
            Style style = this.mStyle;
            if (style != null) {
                style.reduceImageSizes(this.mContext);
                this.mStyle.purgeResources();
                this.mStyle.validate(this.mContext);
                this.mStyle.buildStyled(this.mN);
            }
            this.mN.reduceImageSizes(this.mContext);
            if (this.mContext.getApplicationInfo().targetSdkVersion < 24 && useExistingRemoteView()) {
                if (this.mN.contentView == null) {
                    this.mN.contentView = createContentView();
                    this.mN.extras.putInt(EXTRA_REBUILD_CONTENT_VIEW_ACTION_COUNT, this.mN.contentView.getSequenceNumber());
                }
                if (this.mN.bigContentView == null) {
                    this.mN.bigContentView = createBigContentView();
                    if (this.mN.bigContentView != null) {
                        this.mN.extras.putInt(EXTRA_REBUILD_BIG_CONTENT_VIEW_ACTION_COUNT, this.mN.bigContentView.getSequenceNumber());
                    }
                }
                if (this.mN.headsUpContentView == null) {
                    this.mN.headsUpContentView = createHeadsUpContentView();
                    if (this.mN.headsUpContentView != null) {
                        this.mN.extras.putInt(EXTRA_REBUILD_HEADS_UP_CONTENT_VIEW_ACTION_COUNT, this.mN.headsUpContentView.getSequenceNumber());
                    }
                }
            }
            if ((this.mN.defaults & 4) != 0) {
                this.mN.flags |= 1;
            }
            this.mN.allPendingIntents = null;
            return this.mN;
        }

        public Notification buildInto(Notification notification) {
            build().cloneInto(notification, true);
            return notification;
        }

        public static Notification maybeCloneStrippedForDelivery(Notification notification, boolean z, Context context) {
            String string = notification.extras.getString("android.template");
            if (!z && !TextUtils.isEmpty(string) && Notification.getNotificationStyleClass(string) == null) {
                return notification;
            }
            boolean z2 = (notification.contentView instanceof BuilderRemoteViews) && notification.extras.getInt(EXTRA_REBUILD_CONTENT_VIEW_ACTION_COUNT, -1) == notification.contentView.getSequenceNumber();
            boolean z3 = (notification.bigContentView instanceof BuilderRemoteViews) && notification.extras.getInt(EXTRA_REBUILD_BIG_CONTENT_VIEW_ACTION_COUNT, -1) == notification.bigContentView.getSequenceNumber();
            boolean z4 = (notification.headsUpContentView instanceof BuilderRemoteViews) && notification.extras.getInt(EXTRA_REBUILD_HEADS_UP_CONTENT_VIEW_ACTION_COUNT, -1) == notification.headsUpContentView.getSequenceNumber();
            if (!z && !z2 && !z3 && !z4) {
                return notification;
            }
            Notification notificationM8clone = notification.m8clone();
            if (z2) {
                notificationM8clone.contentView = null;
                notificationM8clone.extras.remove(EXTRA_REBUILD_CONTENT_VIEW_ACTION_COUNT);
            }
            if (z3) {
                notificationM8clone.bigContentView = null;
                notificationM8clone.extras.remove(EXTRA_REBUILD_BIG_CONTENT_VIEW_ACTION_COUNT);
            }
            if (z4) {
                notificationM8clone.headsUpContentView = null;
                notificationM8clone.extras.remove(EXTRA_REBUILD_HEADS_UP_CONTENT_VIEW_ACTION_COUNT);
            }
            if (z && context.getResources().getStringArray(R.array.config_allowedManagedServicesOnLowRamDevices).length == 0) {
                notificationM8clone.extras.remove("android.tv.EXTENSIONS");
                notificationM8clone.extras.remove("android.wearable.EXTENSIONS");
                notificationM8clone.extras.remove("android.car.EXTENSIONS");
            }
            return notificationM8clone;
        }

        private int getBackgroundColor() {
            if (!isColorized()) {
                return 0;
            }
            int i = this.mBackgroundColor;
            return i != 1 ? i : this.mN.color;
        }

        private int getNeutralColor() {
            if (isColorized()) {
                return getSecondaryTextColor();
            }
            return resolveNeutralColor();
        }

        private int resolveBackgroundColor() {
            int backgroundColor = getBackgroundColor();
            return backgroundColor == 0 ? this.mContext.getColor(R.color.notification_material_background_color) : backgroundColor;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isColorized() {
            return this.mN.isColorized();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean shouldTintActionButtons() {
            return this.mTintActionButtons;
        }

        private boolean textColorsNeedInversion() {
            int i;
            Style style = this.mStyle;
            return style != null && MediaStyle.class.equals(style.getClass()) && (i = this.mContext.getApplicationInfo().targetSdkVersion) > 23 && i < 26;
        }

        public void setColorPalette(int i, int i2) {
            this.mBackgroundColor = i;
            this.mForegroundColor = i2;
            this.mTextColorsAreForBackground = 1;
            ensureColors();
        }

        public void setRebuildStyledRemoteViews(boolean z) {
            this.mRebuildStyledRemoteViews = z;
        }

        public CharSequence getHeadsUpStatusBarText(boolean z) {
            Style style = this.mStyle;
            if (style != null && !z) {
                CharSequence headsUpStatusBarText = style.getHeadsUpStatusBarText();
                if (!TextUtils.isEmpty(headsUpStatusBarText)) {
                    return headsUpStatusBarText;
                }
            }
            return loadHeaderAppName();
        }
    }

    void reduceImageSizes(Context context) {
        int i;
        if (this.extras.getBoolean(EXTRA_REDUCED_IMAGES)) {
            return;
        }
        boolean zIsLowRamDeviceStatic = ActivityManager.isLowRamDeviceStatic();
        if (this.mLargeIcon != null || this.largeIcon != null) {
            Resources resources = context.getResources();
            Class<? extends Style> notificationStyle = getNotificationStyle();
            int dimensionPixelSize = resources.getDimensionPixelSize(zIsLowRamDeviceStatic ? R.dimen.notification_right_icon_size_low_ram : R.dimen.notification_right_icon_size);
            if (MediaStyle.class.equals(notificationStyle) || DecoratedMediaCustomViewStyle.class.equals(notificationStyle)) {
                int dimensionPixelSize2 = resources.getDimensionPixelSize(zIsLowRamDeviceStatic ? R.dimen.notification_media_image_max_height_low_ram : R.dimen.notification_media_image_max_height);
                dimensionPixelSize = resources.getDimensionPixelSize(zIsLowRamDeviceStatic ? R.dimen.notification_media_image_max_width_low_ram : R.dimen.notification_media_image_max_width);
                i = dimensionPixelSize2;
            } else {
                i = dimensionPixelSize;
            }
            Icon icon = this.mLargeIcon;
            if (icon != null) {
                icon.scaleDownIfNecessary(dimensionPixelSize, i);
            }
            Bitmap bitmap = this.largeIcon;
            if (bitmap != null) {
                this.largeIcon = Icon.scaleDownIfNecessary(bitmap, dimensionPixelSize, i);
            }
        }
        reduceImageSizesForRemoteView(this.contentView, context, zIsLowRamDeviceStatic);
        reduceImageSizesForRemoteView(this.headsUpContentView, context, zIsLowRamDeviceStatic);
        reduceImageSizesForRemoteView(this.bigContentView, context, zIsLowRamDeviceStatic);
        this.extras.putBoolean(EXTRA_REDUCED_IMAGES, true);
    }

    private void reduceImageSizesForRemoteView(RemoteViews remoteViews, Context context, boolean z) {
        if (remoteViews != null) {
            Resources resources = context.getResources();
            remoteViews.reduceImageSizes(resources.getDimensionPixelSize(z ? R.dimen.notification_custom_view_max_image_width_low_ram : R.dimen.notification_custom_view_max_image_width), resources.getDimensionPixelSize(z ? R.dimen.notification_custom_view_max_image_height_low_ram : R.dimen.notification_custom_view_max_image_height));
        }
    }

    private boolean isForegroundService() {
        return (this.flags & 64) != 0;
    }

    public boolean hasMediaSession() {
        return this.extras.getParcelable("android.mediaSession") != null;
    }

    public Class<? extends Style> getNotificationStyle() {
        String string = this.extras.getString("android.template");
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        return getNotificationStyleClass(string);
    }

    public boolean isColorized() {
        if (isColorizedMedia()) {
            return true;
        }
        return this.extras.getBoolean(EXTRA_COLORIZED) && (hasColorizedPermission() || isForegroundService());
    }

    private boolean hasColorizedPermission() {
        return (this.flags & 2048) != 0;
    }

    public boolean isColorizedMedia() {
        Class<? extends Style> notificationStyle = getNotificationStyle();
        if (!MediaStyle.class.equals(notificationStyle)) {
            return DecoratedMediaCustomViewStyle.class.equals(notificationStyle) && this.extras.getBoolean(EXTRA_COLORIZED) && hasMediaSession();
        }
        Boolean bool = (Boolean) this.extras.get(EXTRA_COLORIZED);
        return (bool == null || bool.booleanValue()) && hasMediaSession();
    }

    public boolean isMediaNotification() {
        Class<? extends Style> notificationStyle = getNotificationStyle();
        return MediaStyle.class.equals(notificationStyle) || DecoratedMediaCustomViewStyle.class.equals(notificationStyle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasLargeIcon() {
        return (this.mLargeIcon == null && this.largeIcon == null) ? false : true;
    }

    public boolean showsTime() {
        return this.when != 0 && this.extras.getBoolean("android.showWhen");
    }

    public boolean showsChronometer() {
        return this.when != 0 && this.extras.getBoolean("android.showChronometer");
    }

    @SystemApi
    public static Class<? extends Style> getNotificationStyleClass(String str) {
        Class<? extends Style>[] clsArr = {BigTextStyle.class, BigPictureStyle.class, InboxStyle.class, MediaStyle.class, DecoratedCustomViewStyle.class, DecoratedMediaCustomViewStyle.class, MessagingStyle.class};
        for (int i = 0; i < 7; i++) {
            Class<? extends Style> cls = clsArr[i];
            if (str.equals(cls.getName())) {
                return cls;
            }
        }
        return null;
    }

    public static abstract class Style {
        static final int MAX_REMOTE_INPUT_HISTORY_LINES = 3;
        private CharSequence mBigContentTitle;
        protected Builder mBuilder;
        protected CharSequence mSummaryText = null;
        protected boolean mSummaryTextSet = false;

        public abstract boolean areNotificationsVisiblyDifferent(Style style);

        public boolean displayCustomViewInline() {
            return false;
        }

        public CharSequence getHeadsUpStatusBarText() {
            return null;
        }

        protected boolean hasProgress() {
            return true;
        }

        public boolean hasSummaryInHeader() {
            return true;
        }

        public RemoteViews makeBigContentView() {
            return null;
        }

        public RemoteViews makeContentView(boolean z) {
            return null;
        }

        public RemoteViews makeHeadsUpContentView(boolean z) {
            return null;
        }

        public void purgeResources() {
        }

        public void reduceImageSizes(Context context) {
        }

        public void validate(Context context) {
        }

        protected void internalSetBigContentTitle(CharSequence charSequence) {
            this.mBigContentTitle = charSequence;
        }

        protected void internalSetSummaryText(CharSequence charSequence) {
            this.mSummaryText = charSequence;
            this.mSummaryTextSet = true;
        }

        public void setBuilder(Builder builder) {
            if (this.mBuilder != builder) {
                this.mBuilder = builder;
                if (builder != null) {
                    builder.setStyle(this);
                }
            }
        }

        protected void checkBuilder() {
            if (this.mBuilder == null) {
                throw new IllegalArgumentException("Style requires a valid Builder object");
            }
        }

        protected RemoteViews getStandardView(int i) {
            return getStandardView(i, null);
        }

        protected RemoteViews getStandardView(int i, TemplateBindResult templateBindResult) {
            checkBuilder();
            CharSequence charSequence = this.mBuilder.getAllExtras().getCharSequence("android.title");
            CharSequence charSequence2 = this.mBigContentTitle;
            if (charSequence2 != null) {
                this.mBuilder.setContentTitle(charSequence2);
            }
            RemoteViews remoteViewsApplyStandardTemplateWithActions = this.mBuilder.applyStandardTemplateWithActions(i, templateBindResult);
            this.mBuilder.getAllExtras().putCharSequence("android.title", charSequence);
            CharSequence charSequence3 = this.mBigContentTitle;
            if (charSequence3 != null && charSequence3.equals("")) {
                remoteViewsApplyStandardTemplateWithActions.setViewVisibility(R.id.line1, 8);
            } else {
                remoteViewsApplyStandardTemplateWithActions.setViewVisibility(R.id.line1, 0);
            }
            return remoteViewsApplyStandardTemplateWithActions;
        }

        public void addExtras(Bundle bundle) {
            if (this.mSummaryTextSet) {
                bundle.putCharSequence("android.summaryText", this.mSummaryText);
            }
            CharSequence charSequence = this.mBigContentTitle;
            if (charSequence != null) {
                bundle.putCharSequence("android.title.big", charSequence);
            }
            bundle.putString("android.template", getClass().getName());
        }

        protected void restoreFromExtras(Bundle bundle) {
            if (bundle.containsKey("android.summaryText")) {
                this.mSummaryText = bundle.getCharSequence("android.summaryText");
                this.mSummaryTextSet = true;
            }
            if (bundle.containsKey("android.title.big")) {
                this.mBigContentTitle = bundle.getCharSequence("android.title.big");
            }
        }

        public Notification buildStyled(Notification notification) {
            addExtras(notification.extras);
            return notification;
        }

        public Notification build() {
            checkBuilder();
            return this.mBuilder.build();
        }
    }

    public static class BigPictureStyle extends Style {
        public static final int MIN_ASHMEM_BITMAP_SIZE = 131072;
        private Icon mBigLargeIcon;
        private boolean mBigLargeIconSet = false;
        private Bitmap mPicture;

        @Override // android.app.Notification.Style
        public boolean hasSummaryInHeader() {
            return false;
        }

        public BigPictureStyle() {
        }

        @Deprecated
        public BigPictureStyle(Builder builder) {
            setBuilder(builder);
        }

        public BigPictureStyle setBigContentTitle(CharSequence charSequence) {
            internalSetBigContentTitle(Notification.safeCharSequence(charSequence));
            return this;
        }

        public BigPictureStyle setSummaryText(CharSequence charSequence) {
            internalSetSummaryText(Notification.safeCharSequence(charSequence));
            return this;
        }

        public Bitmap getBigPicture() {
            return this.mPicture;
        }

        public BigPictureStyle bigPicture(Bitmap bitmap) {
            this.mPicture = bitmap;
            return this;
        }

        public BigPictureStyle bigLargeIcon(Bitmap bitmap) {
            return bigLargeIcon(bitmap != null ? Icon.createWithBitmap(bitmap) : null);
        }

        public BigPictureStyle bigLargeIcon(Icon icon) {
            this.mBigLargeIconSet = true;
            this.mBigLargeIcon = icon;
            return this;
        }

        @Override // android.app.Notification.Style
        public void purgeResources() {
            super.purgeResources();
            Bitmap bitmap = this.mPicture;
            if (bitmap != null && bitmap.isMutable() && this.mPicture.getAllocationByteCount() >= 131072) {
                this.mPicture = this.mPicture.createAshmemBitmap();
            }
            Icon icon = this.mBigLargeIcon;
            if (icon != null) {
                icon.convertToAshmem();
            }
        }

        @Override // android.app.Notification.Style
        public void reduceImageSizes(Context context) {
            super.reduceImageSizes(context);
            Resources resources = context.getResources();
            boolean zIsLowRamDeviceStatic = ActivityManager.isLowRamDeviceStatic();
            if (this.mPicture != null) {
                this.mPicture = Icon.scaleDownIfNecessary(this.mPicture, resources.getDimensionPixelSize(zIsLowRamDeviceStatic ? R.dimen.notification_big_picture_max_height_low_ram : R.dimen.notification_big_picture_max_height), resources.getDimensionPixelSize(zIsLowRamDeviceStatic ? R.dimen.notification_big_picture_max_width_low_ram : R.dimen.notification_big_picture_max_width));
            }
            if (this.mBigLargeIcon != null) {
                int dimensionPixelSize = resources.getDimensionPixelSize(zIsLowRamDeviceStatic ? R.dimen.notification_right_icon_size_low_ram : R.dimen.notification_right_icon_size);
                this.mBigLargeIcon.scaleDownIfNecessary(dimensionPixelSize, dimensionPixelSize);
            }
        }

        @Override // android.app.Notification.Style
        public RemoteViews makeBigContentView() {
            Icon icon;
            Bitmap bitmap;
            if (this.mBigLargeIconSet) {
                icon = this.mBuilder.mN.mLargeIcon;
                this.mBuilder.mN.mLargeIcon = this.mBigLargeIcon;
                bitmap = this.mBuilder.mN.largeIcon;
                this.mBuilder.mN.largeIcon = null;
            } else {
                icon = null;
                bitmap = null;
            }
            RemoteViews standardView = getStandardView(this.mBuilder.getBigPictureLayoutResource(), null);
            if (this.mSummaryTextSet) {
                standardView.setTextViewText(R.id.text, this.mBuilder.processTextSpans(this.mBuilder.processLegacyText(this.mSummaryText)));
                this.mBuilder.setTextViewColorSecondary(standardView, R.id.text);
                standardView.setViewVisibility(R.id.text, 0);
            }
            this.mBuilder.setContentMinHeight(standardView, this.mBuilder.mN.hasLargeIcon());
            if (this.mBigLargeIconSet) {
                this.mBuilder.mN.mLargeIcon = icon;
                this.mBuilder.mN.largeIcon = bitmap;
            }
            standardView.setImageViewBitmap(R.id.big_picture, this.mPicture);
            return standardView;
        }

        @Override // android.app.Notification.Style
        public void addExtras(Bundle bundle) {
            super.addExtras(bundle);
            if (this.mBigLargeIconSet) {
                bundle.putParcelable("android.largeIcon.big", this.mBigLargeIcon);
            }
            bundle.putParcelable("android.picture", this.mPicture);
        }

        @Override // android.app.Notification.Style
        protected void restoreFromExtras(Bundle bundle) {
            super.restoreFromExtras(bundle);
            if (bundle.containsKey("android.largeIcon.big")) {
                this.mBigLargeIconSet = true;
                this.mBigLargeIcon = (Icon) bundle.getParcelable("android.largeIcon.big");
            }
            this.mPicture = (Bitmap) bundle.getParcelable("android.picture");
        }

        @Override // android.app.Notification.Style
        public boolean areNotificationsVisiblyDifferent(Style style) {
            if (style == null || getClass() != style.getClass()) {
                return true;
            }
            return areBitmapsObviouslyDifferent(getBigPicture(), ((BigPictureStyle) style).getBigPicture());
        }

        private static boolean areBitmapsObviouslyDifferent(Bitmap bitmap, Bitmap bitmap2) {
            if (bitmap == bitmap2) {
                return false;
            }
            if (bitmap == null || bitmap2 == null) {
                return true;
            }
            return (bitmap.getWidth() == bitmap2.getWidth() && bitmap.getHeight() == bitmap2.getHeight() && bitmap.getConfig() == bitmap2.getConfig() && bitmap.getGenerationId() == bitmap2.getGenerationId()) ? false : true;
        }
    }

    public static class BigTextStyle extends Style {
        private CharSequence mBigText;

        public BigTextStyle() {
        }

        @Deprecated
        public BigTextStyle(Builder builder) {
            setBuilder(builder);
        }

        public BigTextStyle setBigContentTitle(CharSequence charSequence) {
            internalSetBigContentTitle(Notification.safeCharSequence(charSequence));
            return this;
        }

        public BigTextStyle setSummaryText(CharSequence charSequence) {
            internalSetSummaryText(Notification.safeCharSequence(charSequence));
            return this;
        }

        public BigTextStyle bigText(CharSequence charSequence) {
            this.mBigText = Notification.safeCharSequence(charSequence);
            return this;
        }

        public CharSequence getBigText() {
            return this.mBigText;
        }

        @Override // android.app.Notification.Style
        public void addExtras(Bundle bundle) {
            super.addExtras(bundle);
            bundle.putCharSequence("android.bigText", this.mBigText);
        }

        @Override // android.app.Notification.Style
        protected void restoreFromExtras(Bundle bundle) {
            super.restoreFromExtras(bundle);
            this.mBigText = bundle.getCharSequence("android.bigText");
        }

        @Override // android.app.Notification.Style
        public RemoteViews makeContentView(boolean z) {
            if (z) {
                this.mBuilder.mOriginalActions = this.mBuilder.mActions;
                this.mBuilder.mActions = new ArrayList();
                RemoteViews remoteViewsMakeBigContentView = makeBigContentView();
                this.mBuilder.mActions = this.mBuilder.mOriginalActions;
                this.mBuilder.mOriginalActions = null;
                return remoteViewsMakeBigContentView;
            }
            return super.makeContentView(z);
        }

        @Override // android.app.Notification.Style
        public RemoteViews makeHeadsUpContentView(boolean z) {
            if (z && this.mBuilder.mActions.size() > 0) {
                return makeBigContentView();
            }
            return super.makeHeadsUpContentView(z);
        }

        @Override // android.app.Notification.Style
        public RemoteViews makeBigContentView() {
            CharSequence charSequence = this.mBuilder.getAllExtras().getCharSequence("android.text");
            this.mBuilder.getAllExtras().putCharSequence("android.text", null);
            TemplateBindResult templateBindResult = new TemplateBindResult();
            RemoteViews standardView = getStandardView(this.mBuilder.getBigTextLayoutResource(), templateBindResult);
            standardView.setInt(R.id.big_text, "setImageEndMargin", templateBindResult.getIconMarginEnd());
            this.mBuilder.getAllExtras().putCharSequence("android.text", charSequence);
            CharSequence charSequenceProcessLegacyText = this.mBuilder.processLegacyText(this.mBigText);
            if (TextUtils.isEmpty(charSequenceProcessLegacyText)) {
                charSequenceProcessLegacyText = this.mBuilder.processLegacyText(charSequence);
            }
            applyBigTextContentView(this.mBuilder, standardView, charSequenceProcessLegacyText);
            return standardView;
        }

        @Override // android.app.Notification.Style
        public boolean areNotificationsVisiblyDifferent(Style style) {
            if (style == null || getClass() != style.getClass()) {
                return true;
            }
            return !Objects.equals(String.valueOf(getBigText()), String.valueOf(((BigTextStyle) style).getBigText()));
        }

        static void applyBigTextContentView(Builder builder, RemoteViews remoteViews, CharSequence charSequence) {
            remoteViews.setTextViewText(R.id.big_text, builder.processTextSpans(charSequence));
            builder.setTextViewColorSecondary(remoteViews, R.id.big_text);
            remoteViews.setViewVisibility(R.id.big_text, TextUtils.isEmpty(charSequence) ? 8 : 0);
            remoteViews.setBoolean(R.id.big_text, "setHasImage", builder.mN.hasLargeIcon());
        }
    }

    public static class MessagingStyle extends Style {
        public static final int MAXIMUM_RETAINED_MESSAGES = 25;
        CharSequence mConversationTitle;
        List<Message> mHistoricMessages;
        boolean mIsGroupConversation;
        List<Message> mMessages;
        Person mUser;

        MessagingStyle() {
            this.mMessages = new ArrayList();
            this.mHistoricMessages = new ArrayList();
        }

        public MessagingStyle(CharSequence charSequence) {
            this(new Person.Builder().setName(charSequence).build());
        }

        public MessagingStyle(Person person) {
            this.mMessages = new ArrayList();
            this.mHistoricMessages = new ArrayList();
            this.mUser = person;
        }

        @Override // android.app.Notification.Style
        public void validate(Context context) {
            super.validate(context);
            if (context.getApplicationInfo().targetSdkVersion >= 28) {
                Person person = this.mUser;
                if (person == null || person.getName() == null) {
                    throw new RuntimeException("User must be valid and have a name.");
                }
            }
        }

        @Override // android.app.Notification.Style
        public CharSequence getHeadsUpStatusBarText() {
            CharSequence charSequence;
            if (!TextUtils.isEmpty(((Style) this).mBigContentTitle)) {
                charSequence = ((Style) this).mBigContentTitle;
            } else {
                charSequence = this.mConversationTitle;
            }
            if (TextUtils.isEmpty(charSequence) || hasOnlyWhiteSpaceSenders()) {
                return null;
            }
            return charSequence;
        }

        public Person getUser() {
            return this.mUser;
        }

        public CharSequence getUserDisplayName() {
            return this.mUser.getName();
        }

        public MessagingStyle setConversationTitle(CharSequence charSequence) {
            this.mConversationTitle = charSequence;
            return this;
        }

        public CharSequence getConversationTitle() {
            return this.mConversationTitle;
        }

        public MessagingStyle addMessage(CharSequence charSequence, long j, CharSequence charSequence2) {
            return addMessage(charSequence, j, charSequence2 == null ? null : new Person.Builder().setName(charSequence2).build());
        }

        public MessagingStyle addMessage(CharSequence charSequence, long j, Person person) {
            return addMessage(new Message(charSequence, j, person));
        }

        public MessagingStyle addMessage(Message message) {
            this.mMessages.add(message);
            if (this.mMessages.size() > 25) {
                this.mMessages.remove(0);
            }
            return this;
        }

        public MessagingStyle addHistoricMessage(Message message) {
            this.mHistoricMessages.add(message);
            if (this.mHistoricMessages.size() > 25) {
                this.mHistoricMessages.remove(0);
            }
            return this;
        }

        public List<Message> getMessages() {
            return this.mMessages;
        }

        public List<Message> getHistoricMessages() {
            return this.mHistoricMessages;
        }

        public MessagingStyle setGroupConversation(boolean z) {
            this.mIsGroupConversation = z;
            return this;
        }

        public boolean isGroupConversation() {
            if (this.mBuilder == null || this.mBuilder.mContext.getApplicationInfo().targetSdkVersion >= 28) {
                return this.mIsGroupConversation;
            }
            return this.mConversationTitle != null;
        }

        @Override // android.app.Notification.Style
        public void addExtras(Bundle bundle) {
            super.addExtras(bundle);
            Person person = this.mUser;
            if (person != null) {
                bundle.putCharSequence("android.selfDisplayName", person.getName());
                bundle.putParcelable(Notification.EXTRA_MESSAGING_PERSON, this.mUser);
            }
            CharSequence charSequence = this.mConversationTitle;
            if (charSequence != null) {
                bundle.putCharSequence("android.conversationTitle", charSequence);
            }
            if (!this.mMessages.isEmpty()) {
                bundle.putParcelableArray("android.messages", Message.getBundleArrayForMessages(this.mMessages));
            }
            if (!this.mHistoricMessages.isEmpty()) {
                bundle.putParcelableArray(Notification.EXTRA_HISTORIC_MESSAGES, Message.getBundleArrayForMessages(this.mHistoricMessages));
            }
            fixTitleAndTextExtras(bundle);
            bundle.putBoolean("android.isGroupConversation", this.mIsGroupConversation);
        }

        private void fixTitleAndTextExtras(Bundle bundle) {
            Message messageFindLatestIncomingMessage = findLatestIncomingMessage();
            CharSequence string = null;
            CharSequence charSequence = messageFindLatestIncomingMessage == null ? null : messageFindLatestIncomingMessage.mText;
            if (messageFindLatestIncomingMessage != null) {
                string = ((messageFindLatestIncomingMessage.mSender == null || TextUtils.isEmpty(messageFindLatestIncomingMessage.mSender.getName())) ? this.mUser : messageFindLatestIncomingMessage.mSender).getName();
            }
            if (!TextUtils.isEmpty(this.mConversationTitle)) {
                if (!TextUtils.isEmpty(string)) {
                    BidiFormatter bidiFormatter = BidiFormatter.getInstance();
                    string = this.mBuilder.mContext.getString(R.string.notification_messaging_title_template, bidiFormatter.unicodeWrap(this.mConversationTitle), bidiFormatter.unicodeWrap(string));
                } else {
                    string = this.mConversationTitle;
                }
            }
            if (string != null) {
                bundle.putCharSequence("android.title", string);
            }
            if (charSequence != null) {
                bundle.putCharSequence("android.text", charSequence);
            }
        }

        @Override // android.app.Notification.Style
        protected void restoreFromExtras(Bundle bundle) {
            super.restoreFromExtras(bundle);
            Person person = (Person) bundle.getParcelable(Notification.EXTRA_MESSAGING_PERSON);
            this.mUser = person;
            if (person == null) {
                this.mUser = new Person.Builder().setName(bundle.getCharSequence("android.selfDisplayName")).build();
            }
            this.mConversationTitle = bundle.getCharSequence("android.conversationTitle");
            this.mMessages = Message.getMessagesFromBundleArray(bundle.getParcelableArray("android.messages"));
            this.mHistoricMessages = Message.getMessagesFromBundleArray(bundle.getParcelableArray(Notification.EXTRA_HISTORIC_MESSAGES));
            this.mIsGroupConversation = bundle.getBoolean("android.isGroupConversation");
        }

        @Override // android.app.Notification.Style
        public RemoteViews makeContentView(boolean z) {
            this.mBuilder.mOriginalActions = this.mBuilder.mActions;
            this.mBuilder.mActions = new ArrayList();
            RemoteViews remoteViewsMakeMessagingView = makeMessagingView(true, false);
            this.mBuilder.mActions = this.mBuilder.mOriginalActions;
            this.mBuilder.mOriginalActions = null;
            return remoteViewsMakeMessagingView;
        }

        @Override // android.app.Notification.Style
        public boolean areNotificationsVisiblyDifferent(Style style) {
            CharSequence name;
            CharSequence name2;
            if (style == null || getClass() != style.getClass()) {
                return true;
            }
            List<Message> messages = getMessages();
            List<Message> messages2 = ((MessagingStyle) style).getMessages();
            if (messages == null || messages2 == null) {
                messages2 = new ArrayList<>();
            }
            int size = messages.size();
            if (size != messages2.size()) {
                return true;
            }
            for (int i = 0; i < size; i++) {
                Message message = messages.get(i);
                Message message2 = messages2.get(i);
                if (!Objects.equals(String.valueOf(message.getText()), String.valueOf(message2.getText())) || !Objects.equals(message.getDataUri(), message2.getDataUri())) {
                    return true;
                }
                if (message.getSenderPerson() == null) {
                    name = message.getSender();
                } else {
                    name = message.getSenderPerson().getName();
                }
                String strValueOf = String.valueOf(name);
                if (message2.getSenderPerson() == null) {
                    name2 = message2.getSender();
                } else {
                    name2 = message2.getSenderPerson().getName();
                }
                if (!Objects.equals(strValueOf, String.valueOf(name2))) {
                    return true;
                }
                if (!Objects.equals(message.getSenderPerson() == null ? null : message.getSenderPerson().getKey(), message2.getSenderPerson() != null ? message2.getSenderPerson().getKey() : null)) {
                    return true;
                }
            }
            return false;
        }

        private Message findLatestIncomingMessage() {
            return findLatestIncomingMessage(this.mMessages);
        }

        public static Message findLatestIncomingMessage(List<Message> list) {
            for (int size = list.size() - 1; size >= 0; size--) {
                Message message = list.get(size);
                if (message.mSender != null && !TextUtils.isEmpty(message.mSender.getName())) {
                    return message;
                }
            }
            if (list.isEmpty()) {
                return null;
            }
            return list.get(list.size() - 1);
        }

        @Override // android.app.Notification.Style
        public RemoteViews makeBigContentView() {
            return makeMessagingView(false, true);
        }

        private RemoteViews makeMessagingView(boolean z, boolean z2) {
            CharSequence charSequence;
            boolean z3;
            CharSequence charSequence2;
            Icon icon;
            if (!TextUtils.isEmpty(((Style) this).mBigContentTitle)) {
                charSequence = ((Style) this).mBigContentTitle;
            } else {
                charSequence = this.mConversationTitle;
            }
            if (!(this.mBuilder.mContext.getApplicationInfo().targetSdkVersion >= 28)) {
                boolean zIsEmpty = TextUtils.isEmpty(charSequence);
                Icon icon2 = this.mBuilder.mN.mLargeIcon;
                if (hasOnlyWhiteSpaceSenders()) {
                    charSequence2 = charSequence;
                    charSequence = null;
                    icon = icon2;
                    z3 = true;
                } else {
                    icon = icon2;
                    z3 = zIsEmpty;
                    charSequence2 = null;
                }
            } else {
                z3 = !isGroupConversation();
                charSequence2 = null;
                icon = null;
            }
            TemplateBindResult templateBindResult = new TemplateBindResult();
            RemoteViews remoteViewsApplyStandardTemplateWithActions = this.mBuilder.applyStandardTemplateWithActions(this.mBuilder.getMessagingLayoutResource(), this.mBuilder.mParams.reset().hasProgress(false).title(charSequence).text(null).hideLargeIcon(z2 || z3).hideReplyIcon(z2).headerTextSecondary(charSequence), templateBindResult);
            addExtras(this.mBuilder.mN.extras);
            remoteViewsApplyStandardTemplateWithActions.setViewLayoutMarginEnd(R.id.notification_messaging, templateBindResult.getIconMarginEnd());
            remoteViewsApplyStandardTemplateWithActions.setInt(R.id.status_bar_latest_event_content, "setLayoutColor", this.mBuilder.isColorized() ? this.mBuilder.getPrimaryTextColor() : this.mBuilder.resolveContrastColor());
            remoteViewsApplyStandardTemplateWithActions.setInt(R.id.status_bar_latest_event_content, "setSenderTextColor", this.mBuilder.getPrimaryTextColor());
            remoteViewsApplyStandardTemplateWithActions.setInt(R.id.status_bar_latest_event_content, "setMessageTextColor", this.mBuilder.getSecondaryTextColor());
            remoteViewsApplyStandardTemplateWithActions.setBoolean(R.id.status_bar_latest_event_content, "setDisplayImagesAtEnd", z);
            remoteViewsApplyStandardTemplateWithActions.setIcon(R.id.status_bar_latest_event_content, "setAvatarReplacement", icon);
            remoteViewsApplyStandardTemplateWithActions.setCharSequence(R.id.status_bar_latest_event_content, "setNameReplacement", charSequence2);
            remoteViewsApplyStandardTemplateWithActions.setBoolean(R.id.status_bar_latest_event_content, "setIsOneToOne", z3);
            remoteViewsApplyStandardTemplateWithActions.setBundle(R.id.status_bar_latest_event_content, "setData", this.mBuilder.mN.extras);
            return remoteViewsApplyStandardTemplateWithActions;
        }

        private boolean hasOnlyWhiteSpaceSenders() {
            for (int i = 0; i < this.mMessages.size(); i++) {
                Person senderPerson = this.mMessages.get(i).getSenderPerson();
                if (senderPerson != null && !isWhiteSpace(senderPerson.getName())) {
                    return false;
                }
            }
            return true;
        }

        private boolean isWhiteSpace(CharSequence charSequence) {
            if (TextUtils.isEmpty(charSequence) || charSequence.toString().matches("^\\s*$")) {
                return true;
            }
            for (int i = 0; i < charSequence.length(); i++) {
                if (charSequence.charAt(i) != 8203) {
                    return false;
                }
            }
            return true;
        }

        private CharSequence createConversationTitleFromMessages() {
            ArraySet arraySet = new ArraySet();
            for (int i = 0; i < this.mMessages.size(); i++) {
                Person senderPerson = this.mMessages.get(i).getSenderPerson();
                if (senderPerson != null) {
                    arraySet.add(senderPerson.getName());
                }
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            int size = arraySet.size();
            for (int i2 = 0; i2 < size; i2++) {
                CharSequence charSequence = (CharSequence) arraySet.valueAt(i2);
                if (!TextUtils.isEmpty(spannableStringBuilder)) {
                    spannableStringBuilder.append((CharSequence) ", ");
                }
                spannableStringBuilder.append(BidiFormatter.getInstance().unicodeWrap(charSequence));
            }
            return spannableStringBuilder;
        }

        @Override // android.app.Notification.Style
        public RemoteViews makeHeadsUpContentView(boolean z) {
            RemoteViews remoteViewsMakeMessagingView = makeMessagingView(true, true);
            remoteViewsMakeMessagingView.setInt(R.id.notification_messaging, "setMaxDisplayedLines", 1);
            return remoteViewsMakeMessagingView;
        }

        private static TextAppearanceSpan makeFontColorSpan(int i) {
            return new TextAppearanceSpan(null, 0, 0, ColorStateList.valueOf(i), null);
        }

        public static final class Message {
            static final String KEY_DATA_MIME_TYPE = "type";
            static final String KEY_DATA_URI = "uri";
            static final String KEY_EXTRAS_BUNDLE = "extras";
            static final String KEY_REMOTE_INPUT_HISTORY = "remote_input_history";
            static final String KEY_SENDER = "sender";
            static final String KEY_SENDER_PERSON = "sender_person";
            static final String KEY_TEXT = "text";
            static final String KEY_TIMESTAMP = "time";
            private String mDataMimeType;
            private Uri mDataUri;
            private Bundle mExtras;
            private final boolean mRemoteInputHistory;
            private final Person mSender;
            private final CharSequence mText;
            private final long mTimestamp;

            public Message(CharSequence charSequence, long j, CharSequence charSequence2) {
                this(charSequence, j, charSequence2 == null ? null : new Person.Builder().setName(charSequence2).build());
            }

            public Message(CharSequence charSequence, long j, Person person) {
                this(charSequence, j, person, false);
            }

            public Message(CharSequence charSequence, long j, Person person, boolean z) {
                this.mExtras = new Bundle();
                this.mText = charSequence;
                this.mTimestamp = j;
                this.mSender = person;
                this.mRemoteInputHistory = z;
            }

            public Message setData(String str, Uri uri) {
                this.mDataMimeType = str;
                this.mDataUri = uri;
                return this;
            }

            public CharSequence getText() {
                return this.mText;
            }

            public long getTimestamp() {
                return this.mTimestamp;
            }

            public Bundle getExtras() {
                return this.mExtras;
            }

            public CharSequence getSender() {
                Person person = this.mSender;
                if (person == null) {
                    return null;
                }
                return person.getName();
            }

            public Person getSenderPerson() {
                return this.mSender;
            }

            public String getDataMimeType() {
                return this.mDataMimeType;
            }

            public Uri getDataUri() {
                return this.mDataUri;
            }

            public boolean isRemoteInputHistory() {
                return this.mRemoteInputHistory;
            }

            private Bundle toBundle() {
                Bundle bundle = new Bundle();
                CharSequence charSequence = this.mText;
                if (charSequence != null) {
                    bundle.putCharSequence("text", charSequence);
                }
                bundle.putLong("time", this.mTimestamp);
                Person person = this.mSender;
                if (person != null) {
                    bundle.putCharSequence("sender", person.getName());
                    bundle.putParcelable(KEY_SENDER_PERSON, this.mSender);
                }
                String str = this.mDataMimeType;
                if (str != null) {
                    bundle.putString("type", str);
                }
                Uri uri = this.mDataUri;
                if (uri != null) {
                    bundle.putParcelable("uri", uri);
                }
                Bundle bundle2 = this.mExtras;
                if (bundle2 != null) {
                    bundle.putBundle(KEY_EXTRAS_BUNDLE, bundle2);
                }
                boolean z = this.mRemoteInputHistory;
                if (z) {
                    bundle.putBoolean(KEY_REMOTE_INPUT_HISTORY, z);
                }
                return bundle;
            }

            static Bundle[] getBundleArrayForMessages(List<Message> list) {
                Bundle[] bundleArr = new Bundle[list.size()];
                int size = list.size();
                for (int i = 0; i < size; i++) {
                    bundleArr[i] = list.get(i).toBundle();
                }
                return bundleArr;
            }

            public static List<Message> getMessagesFromBundleArray(Parcelable[] parcelableArr) {
                Message messageFromBundle;
                if (parcelableArr == null) {
                    return new ArrayList();
                }
                ArrayList arrayList = new ArrayList(parcelableArr.length);
                for (int i = 0; i < parcelableArr.length; i++) {
                    if ((parcelableArr[i] instanceof Bundle) && (messageFromBundle = getMessageFromBundle((Bundle) parcelableArr[i])) != null) {
                        arrayList.add(messageFromBundle);
                    }
                }
                return arrayList;
            }

            public static Message getMessageFromBundle(Bundle bundle) {
                CharSequence charSequence;
                try {
                    if (bundle.containsKey("text") && bundle.containsKey("time")) {
                        Person personBuild = (Person) bundle.getParcelable(KEY_SENDER_PERSON);
                        if (personBuild == null && (charSequence = bundle.getCharSequence("sender")) != null) {
                            personBuild = new Person.Builder().setName(charSequence).build();
                        }
                        Message message = new Message(bundle.getCharSequence("text"), bundle.getLong("time"), personBuild, bundle.getBoolean(KEY_REMOTE_INPUT_HISTORY, false));
                        if (bundle.containsKey("type") && bundle.containsKey("uri")) {
                            message.setData(bundle.getString("type"), (Uri) bundle.getParcelable("uri"));
                        }
                        if (bundle.containsKey(KEY_EXTRAS_BUNDLE)) {
                            message.getExtras().putAll(bundle.getBundle(KEY_EXTRAS_BUNDLE));
                        }
                        return message;
                    }
                } catch (ClassCastException unused) {
                }
                return null;
            }
        }
    }

    public static class InboxStyle extends Style {
        private static final int NUMBER_OF_HISTORY_ALLOWED_UNTIL_REDUCTION = 1;
        private ArrayList<CharSequence> mTexts = new ArrayList<>(5);

        public InboxStyle() {
        }

        @Deprecated
        public InboxStyle(Builder builder) {
            setBuilder(builder);
        }

        public InboxStyle setBigContentTitle(CharSequence charSequence) {
            internalSetBigContentTitle(Notification.safeCharSequence(charSequence));
            return this;
        }

        public InboxStyle setSummaryText(CharSequence charSequence) {
            internalSetSummaryText(Notification.safeCharSequence(charSequence));
            return this;
        }

        public InboxStyle addLine(CharSequence charSequence) {
            this.mTexts.add(Notification.safeCharSequence(charSequence));
            return this;
        }

        public ArrayList<CharSequence> getLines() {
            return this.mTexts;
        }

        @Override // android.app.Notification.Style
        public void addExtras(Bundle bundle) {
            super.addExtras(bundle);
            bundle.putCharSequenceArray("android.textLines", (CharSequence[]) this.mTexts.toArray(new CharSequence[this.mTexts.size()]));
        }

        @Override // android.app.Notification.Style
        protected void restoreFromExtras(Bundle bundle) {
            super.restoreFromExtras(bundle);
            this.mTexts.clear();
            if (bundle.containsKey("android.textLines")) {
                Collections.addAll(this.mTexts, bundle.getCharSequenceArray("android.textLines"));
            }
        }

        /* JADX WARN: Code duplicated, block: B:18:0x0095 A[PHI: r0
  0x0095: PHI (r0v6 int) = (r0v5 int), (r0v5 int), (r0v5 int), (r0v11 int) binds: [B:9:0x0071, B:11:0x0074, B:13:0x0084, B:16:0x008f] A[DONT_GENERATE, DONT_INLINE]] */
        @Override // android.app.Notification.Style
        public RemoteViews makeBigContentView() {
            int i;
            int i2;
            CharSequence charSequence = this.mBuilder.mN.extras.getCharSequence("android.text");
            this.mBuilder.getAllExtras().putCharSequence("android.text", null);
            TemplateBindResult templateBindResult = new TemplateBindResult();
            RemoteViews standardView = getStandardView(this.mBuilder.getInboxLayoutResource(), templateBindResult);
            this.mBuilder.getAllExtras().putCharSequence("android.text", charSequence);
            int[] iArr = {R.id.inbox_text0, R.id.inbox_text1, R.id.inbox_text2, R.id.inbox_text3, R.id.inbox_text4, R.id.inbox_text5, R.id.inbox_text6};
            for (int i3 = 0; i3 < 7; i3++) {
                standardView.setViewVisibility(iArr[i3], 8);
            }
            int dimensionPixelSize = this.mBuilder.mContext.getResources().getDimensionPixelSize(R.dimen.notification_inbox_item_top_padding);
            int i4 = this.mBuilder.mActions.size() > 0 ? 6 : 7;
            CharSequence[] charSequenceArray = this.mBuilder.mN.extras.getCharSequenceArray("android.remoteInputHistory");
            boolean z = true;
            if (charSequenceArray == null || charSequenceArray.length <= 1) {
                i = 0;
                i2 = 0;
            } else {
                int size = (this.mTexts.size() + Math.min(charSequenceArray.length, 3)) - 1;
                if (size > i4) {
                    int i5 = size - i4;
                    if (this.mTexts.size() > i4) {
                        i4 -= i5;
                        i = 0;
                        i2 = 0;
                    } else {
                        i2 = i5;
                        i = 0;
                    }
                } else {
                    i = 0;
                    i2 = 0;
                }
            }
            while (i2 < this.mTexts.size() && i2 < i4) {
                CharSequence charSequence2 = this.mTexts.get(i2);
                if (!TextUtils.isEmpty(charSequence2)) {
                    standardView.setViewVisibility(iArr[i2], 0);
                    standardView.setTextViewText(iArr[i2], this.mBuilder.processTextSpans(this.mBuilder.processLegacyText(charSequence2)));
                    this.mBuilder.setTextViewColorSecondary(standardView, iArr[i2]);
                    standardView.setViewPadding(iArr[i2], 0, dimensionPixelSize, 0, 0);
                    handleInboxImageMargin(standardView, iArr[i2], z, templateBindResult.getIconMarginEnd());
                    i = z ? iArr[i2] : 0;
                    z = false;
                }
                i2++;
            }
            if (i != 0) {
                standardView.setViewPadding(i, 0, this.mBuilder.mContext.getResources().getDimensionPixelSize(R.dimen.notification_text_margin_top), 0, 0);
            }
            return standardView;
        }

        @Override // android.app.Notification.Style
        public boolean areNotificationsVisiblyDifferent(Style style) {
            if (style == null || getClass() != style.getClass()) {
                return true;
            }
            ArrayList<CharSequence> lines = getLines();
            ArrayList<CharSequence> lines2 = ((InboxStyle) style).getLines();
            int size = lines.size();
            if (size != lines2.size()) {
                return true;
            }
            for (int i = 0; i < size; i++) {
                if (!Objects.equals(String.valueOf(lines.get(i)), String.valueOf(lines2.get(i)))) {
                    return true;
                }
            }
            return false;
        }

        /* JADX WARN: Code duplicated, block: B:12:0x002a  */
        private void handleInboxImageMargin(RemoteViews remoteViews, int i, boolean z, int i2) {
            if (z) {
                if (this.mBuilder.mN.extras.getInt("android.progressMax", 0) != 0 || this.mBuilder.mN.extras.getBoolean("android.progressIndeterminate")) {
                    i2 = 0;
                }
            } else {
                i2 = 0;
            }
            remoteViews.setViewLayoutMarginEnd(i, i2);
        }
    }

    public static class MediaStyle extends Style {
        static final int MAX_MEDIA_BUTTONS = 5;
        static final int MAX_MEDIA_BUTTONS_IN_COMPACT = 3;
        private int[] mActionsToShowInCompact = null;
        private MediaSession.Token mToken;

        @Override // android.app.Notification.Style
        protected boolean hasProgress() {
            return false;
        }

        public MediaStyle() {
        }

        @Deprecated
        public MediaStyle(Builder builder) {
            setBuilder(builder);
        }

        public MediaStyle setShowActionsInCompactView(int... iArr) {
            this.mActionsToShowInCompact = iArr;
            return this;
        }

        public MediaStyle setMediaSession(MediaSession.Token token) {
            this.mToken = token;
            return this;
        }

        @Override // android.app.Notification.Style
        public Notification buildStyled(Notification notification) {
            super.buildStyled(notification);
            if (notification.category == null) {
                notification.category = "transport";
            }
            return notification;
        }

        @Override // android.app.Notification.Style
        public RemoteViews makeContentView(boolean z) {
            return makeMediaContentView();
        }

        @Override // android.app.Notification.Style
        public RemoteViews makeBigContentView() {
            return makeMediaBigContentView();
        }

        @Override // android.app.Notification.Style
        public RemoteViews makeHeadsUpContentView(boolean z) {
            RemoteViews remoteViewsMakeMediaBigContentView = makeMediaBigContentView();
            return remoteViewsMakeMediaBigContentView != null ? remoteViewsMakeMediaBigContentView : makeMediaContentView();
        }

        @Override // android.app.Notification.Style
        public void addExtras(Bundle bundle) {
            super.addExtras(bundle);
            MediaSession.Token token = this.mToken;
            if (token != null) {
                bundle.putParcelable("android.mediaSession", token);
            }
            int[] iArr = this.mActionsToShowInCompact;
            if (iArr != null) {
                bundle.putIntArray("android.compactActions", iArr);
            }
        }

        @Override // android.app.Notification.Style
        protected void restoreFromExtras(Bundle bundle) {
            super.restoreFromExtras(bundle);
            if (bundle.containsKey("android.mediaSession")) {
                this.mToken = (MediaSession.Token) bundle.getParcelable("android.mediaSession");
            }
            if (bundle.containsKey("android.compactActions")) {
                this.mActionsToShowInCompact = bundle.getIntArray("android.compactActions");
            }
        }

        @Override // android.app.Notification.Style
        public boolean areNotificationsVisiblyDifferent(Style style) {
            return style == null || getClass() != style.getClass();
        }

        private RemoteViews generateMediaActionButton(Action action, int i) {
            boolean z = action.actionIntent == null;
            BuilderRemoteViews builderRemoteViews = new BuilderRemoteViews(this.mBuilder.mContext.getApplicationInfo(), R.layout.notification_material_media_action);
            builderRemoteViews.setImageViewIcon(R.id.action0, action.getIcon());
            if (!this.mBuilder.shouldTintActionButtons() && !this.mBuilder.isColorized()) {
                i = NotificationColorUtil.resolveColor(this.mBuilder.mContext, 0);
            }
            builderRemoteViews.setDrawableTint(R.id.action0, false, i, PorterDuff.Mode.SRC_ATOP);
            if (!z) {
                builderRemoteViews.setOnClickPendingIntent(R.id.action0, action.actionIntent);
            }
            builderRemoteViews.setContentDescription(R.id.action0, action.title);
            return builderRemoteViews;
        }

        private RemoteViews makeMediaContentView() {
            RemoteViews remoteViewsApplyStandardTemplate = this.mBuilder.applyStandardTemplate(R.layout.notification_template_material_media, false, (TemplateBindResult) null);
            int size = this.mBuilder.mActions.size();
            int[] iArr = this.mActionsToShowInCompact;
            int iMin = iArr == null ? 0 : Math.min(iArr.length, 3);
            if (iMin > 0) {
                remoteViewsApplyStandardTemplate.removeAllViews(R.id.media_actions);
                for (int i = 0; i < iMin; i++) {
                    if (i >= size) {
                        throw new IllegalArgumentException(String.format("setShowActionsInCompactView: action %d out of bounds (max %d)", Integer.valueOf(i), Integer.valueOf(size - 1)));
                    }
                    remoteViewsApplyStandardTemplate.addView(R.id.media_actions, generateMediaActionButton((Action) this.mBuilder.mActions.get(this.mActionsToShowInCompact[i]), getActionColor()));
                }
            }
            handleImage(remoteViewsApplyStandardTemplate);
            int i2 = R.dimen.notification_content_margin_end;
            if (this.mBuilder.mN.hasLargeIcon()) {
                i2 = R.dimen.notification_media_image_margin_end;
            }
            remoteViewsApplyStandardTemplate.setViewLayoutMarginEndDimen(R.id.notification_main_column, i2);
            return remoteViewsApplyStandardTemplate;
        }

        private int getActionColor() {
            return this.mBuilder.isColorized() ? this.mBuilder.getPrimaryTextColor() : this.mBuilder.resolveContrastColor();
        }

        private RemoteViews makeMediaBigContentView() {
            int iMin = Math.min(this.mBuilder.mActions.size(), 5);
            int[] iArr = this.mActionsToShowInCompact;
            int iMin2 = iArr == null ? 0 : Math.min(iArr.length, 3);
            if (!this.mBuilder.mN.hasLargeIcon() && iMin <= iMin2) {
                return null;
            }
            RemoteViews remoteViewsApplyStandardTemplate = this.mBuilder.applyStandardTemplate(R.layout.notification_template_material_big_media, false, (TemplateBindResult) null);
            if (iMin > 0) {
                remoteViewsApplyStandardTemplate.removeAllViews(R.id.media_actions);
                for (int i = 0; i < iMin; i++) {
                    remoteViewsApplyStandardTemplate.addView(R.id.media_actions, generateMediaActionButton((Action) this.mBuilder.mActions.get(i), getActionColor()));
                }
            }
            handleImage(remoteViewsApplyStandardTemplate);
            return remoteViewsApplyStandardTemplate;
        }

        private void handleImage(RemoteViews remoteViews) {
            if (this.mBuilder.mN.hasLargeIcon()) {
                remoteViews.setViewLayoutMarginEndDimen(R.id.line1, 0);
                remoteViews.setViewLayoutMarginEndDimen(R.id.text, 0);
            }
        }
    }

    public static class DecoratedCustomViewStyle extends Style {
        @Override // android.app.Notification.Style
        public boolean displayCustomViewInline() {
            return true;
        }

        @Override // android.app.Notification.Style
        public RemoteViews makeContentView(boolean z) {
            return makeStandardTemplateWithCustomContent(this.mBuilder.mN.contentView);
        }

        @Override // android.app.Notification.Style
        public RemoteViews makeBigContentView() {
            return makeDecoratedBigContentView();
        }

        @Override // android.app.Notification.Style
        public RemoteViews makeHeadsUpContentView(boolean z) {
            return makeDecoratedHeadsUpContentView();
        }

        private RemoteViews makeDecoratedHeadsUpContentView() {
            RemoteViews remoteViews = this.mBuilder.mN.headsUpContentView == null ? this.mBuilder.mN.contentView : this.mBuilder.mN.headsUpContentView;
            if (this.mBuilder.mActions.size() == 0) {
                return makeStandardTemplateWithCustomContent(remoteViews);
            }
            TemplateBindResult templateBindResult = new TemplateBindResult();
            RemoteViews remoteViewsApplyStandardTemplateWithActions = this.mBuilder.applyStandardTemplateWithActions(this.mBuilder.getBigBaseLayoutResource(), templateBindResult);
            buildIntoRemoteViewContent(remoteViewsApplyStandardTemplateWithActions, remoteViews, templateBindResult);
            return remoteViewsApplyStandardTemplateWithActions;
        }

        private RemoteViews makeStandardTemplateWithCustomContent(RemoteViews remoteViews) {
            TemplateBindResult templateBindResult = new TemplateBindResult();
            RemoteViews remoteViewsApplyStandardTemplate = this.mBuilder.applyStandardTemplate(this.mBuilder.getBaseLayoutResource(), templateBindResult);
            buildIntoRemoteViewContent(remoteViewsApplyStandardTemplate, remoteViews, templateBindResult);
            return remoteViewsApplyStandardTemplate;
        }

        private RemoteViews makeDecoratedBigContentView() {
            RemoteViews remoteViews = this.mBuilder.mN.bigContentView == null ? this.mBuilder.mN.contentView : this.mBuilder.mN.bigContentView;
            if (this.mBuilder.mActions.size() == 0) {
                return makeStandardTemplateWithCustomContent(remoteViews);
            }
            TemplateBindResult templateBindResult = new TemplateBindResult();
            RemoteViews remoteViewsApplyStandardTemplateWithActions = this.mBuilder.applyStandardTemplateWithActions(this.mBuilder.getBigBaseLayoutResource(), templateBindResult);
            buildIntoRemoteViewContent(remoteViewsApplyStandardTemplateWithActions, remoteViews, templateBindResult);
            return remoteViewsApplyStandardTemplateWithActions;
        }

        private void buildIntoRemoteViewContent(RemoteViews remoteViews, RemoteViews remoteViews2, TemplateBindResult templateBindResult) {
            if (remoteViews2 != null) {
                RemoteViews remoteViewsMo11clone = remoteViews2.mo11clone();
                remoteViews.removeAllViewsExceptId(R.id.notification_main_column, 16908301);
                remoteViews.addView(R.id.notification_main_column, remoteViewsMo11clone, 0);
                remoteViews.setReapplyDisallowed();
            }
            remoteViews.setViewLayoutMarginEnd(R.id.notification_main_column, this.mBuilder.mContext.getResources().getDimensionPixelSize(R.dimen.notification_content_margin_end) + templateBindResult.getIconMarginEnd());
        }

        @Override // android.app.Notification.Style
        public boolean areNotificationsVisiblyDifferent(Style style) {
            return style == null || getClass() != style.getClass();
        }
    }

    public static class DecoratedMediaCustomViewStyle extends MediaStyle {
        @Override // android.app.Notification.Style
        public boolean displayCustomViewInline() {
            return true;
        }

        @Override // android.app.Notification.MediaStyle, android.app.Notification.Style
        public RemoteViews makeContentView(boolean z) {
            return buildIntoRemoteView(super.makeContentView(false), R.id.notification_content_container, this.mBuilder.mN.contentView);
        }

        @Override // android.app.Notification.MediaStyle, android.app.Notification.Style
        public RemoteViews makeBigContentView() {
            return makeBigContentViewWithCustomContent(this.mBuilder.mN.bigContentView != null ? this.mBuilder.mN.bigContentView : this.mBuilder.mN.contentView);
        }

        private RemoteViews makeBigContentViewWithCustomContent(RemoteViews remoteViews) {
            RemoteViews remoteViewsMakeBigContentView = super.makeBigContentView();
            if (remoteViewsMakeBigContentView == null) {
                if (remoteViews != this.mBuilder.mN.contentView) {
                    return buildIntoRemoteView(super.makeContentView(false), R.id.notification_content_container, remoteViews);
                }
                return null;
            }
            return buildIntoRemoteView(remoteViewsMakeBigContentView, R.id.notification_main_column, remoteViews);
        }

        @Override // android.app.Notification.MediaStyle, android.app.Notification.Style
        public RemoteViews makeHeadsUpContentView(boolean z) {
            return makeBigContentViewWithCustomContent(this.mBuilder.mN.headsUpContentView != null ? this.mBuilder.mN.headsUpContentView : this.mBuilder.mN.contentView);
        }

        @Override // android.app.Notification.MediaStyle, android.app.Notification.Style
        public boolean areNotificationsVisiblyDifferent(Style style) {
            return style == null || getClass() != style.getClass();
        }

        private RemoteViews buildIntoRemoteView(RemoteViews remoteViews, int i, RemoteViews remoteViews2) {
            if (remoteViews2 != null) {
                RemoteViews remoteViewsMo11clone = remoteViews2.mo11clone();
                remoteViewsMo11clone.overrideTextColors(this.mBuilder.getPrimaryTextColor());
                remoteViews.removeAllViews(i);
                remoteViews.addView(i, remoteViewsMo11clone);
                remoteViews.setReapplyDisallowed();
            }
            return remoteViews;
        }
    }

    public static final class WearableExtender implements Extender {
        private static final int DEFAULT_CONTENT_ICON_GRAVITY = 8388613;
        private static final int DEFAULT_FLAGS = 1;
        private static final int DEFAULT_GRAVITY = 80;
        private static final String EXTRA_WEARABLE_EXTENSIONS = "android.wearable.EXTENSIONS";
        private static final int FLAG_BIG_PICTURE_AMBIENT = 32;
        private static final int FLAG_CONTENT_INTENT_AVAILABLE_OFFLINE = 1;
        private static final int FLAG_HINT_AVOID_BACKGROUND_CLIPPING = 16;
        private static final int FLAG_HINT_CONTENT_INTENT_LAUNCHES_ACTIVITY = 64;
        private static final int FLAG_HINT_HIDE_ICON = 2;
        private static final int FLAG_HINT_SHOW_BACKGROUND_ONLY = 4;
        private static final int FLAG_START_SCROLL_BOTTOM = 8;
        private static final String KEY_ACTIONS = "actions";
        private static final String KEY_BACKGROUND = "background";
        private static final String KEY_BRIDGE_TAG = "bridgeTag";
        private static final String KEY_CONTENT_ACTION_INDEX = "contentActionIndex";
        private static final String KEY_CONTENT_ICON = "contentIcon";
        private static final String KEY_CONTENT_ICON_GRAVITY = "contentIconGravity";
        private static final String KEY_CUSTOM_CONTENT_HEIGHT = "customContentHeight";
        private static final String KEY_CUSTOM_SIZE_PRESET = "customSizePreset";
        private static final String KEY_DISMISSAL_ID = "dismissalId";
        private static final String KEY_DISPLAY_INTENT = "displayIntent";
        private static final String KEY_FLAGS = "flags";
        private static final String KEY_GRAVITY = "gravity";
        private static final String KEY_HINT_SCREEN_TIMEOUT = "hintScreenTimeout";
        private static final String KEY_PAGES = "pages";
        public static final int SCREEN_TIMEOUT_LONG = -1;
        public static final int SCREEN_TIMEOUT_SHORT = 0;
        public static final int SIZE_DEFAULT = 0;
        public static final int SIZE_FULL_SCREEN = 5;
        public static final int SIZE_LARGE = 4;
        public static final int SIZE_MEDIUM = 3;
        public static final int SIZE_SMALL = 2;
        public static final int SIZE_XSMALL = 1;
        public static final int UNSET_ACTION_INDEX = -1;
        private ArrayList<Action> mActions;
        private Bitmap mBackground;
        private String mBridgeTag;
        private int mContentActionIndex;
        private int mContentIcon;
        private int mContentIconGravity;
        private int mCustomContentHeight;
        private int mCustomSizePreset;
        private String mDismissalId;
        private PendingIntent mDisplayIntent;
        private int mFlags;
        private int mGravity;
        private int mHintScreenTimeout;
        private ArrayList<Notification> mPages;

        public WearableExtender() {
            this.mActions = new ArrayList<>();
            this.mFlags = 1;
            this.mPages = new ArrayList<>();
            this.mContentIconGravity = 8388613;
            this.mContentActionIndex = -1;
            this.mCustomSizePreset = 0;
            this.mGravity = 80;
        }

        public WearableExtender(Notification notification) {
            this.mActions = new ArrayList<>();
            this.mFlags = 1;
            this.mPages = new ArrayList<>();
            this.mContentIconGravity = 8388613;
            this.mContentActionIndex = -1;
            this.mCustomSizePreset = 0;
            this.mGravity = 80;
            Bundle bundle = notification.extras.getBundle(EXTRA_WEARABLE_EXTENSIONS);
            if (bundle != null) {
                ArrayList parcelableArrayList = bundle.getParcelableArrayList("actions");
                if (parcelableArrayList != null) {
                    this.mActions.addAll(parcelableArrayList);
                }
                this.mFlags = bundle.getInt("flags", 1);
                this.mDisplayIntent = (PendingIntent) bundle.getParcelable(KEY_DISPLAY_INTENT);
                Notification[] notificationArrayFromBundle = Notification.getNotificationArrayFromBundle(bundle, KEY_PAGES);
                if (notificationArrayFromBundle != null) {
                    Collections.addAll(this.mPages, notificationArrayFromBundle);
                }
                this.mBackground = (Bitmap) bundle.getParcelable(KEY_BACKGROUND);
                this.mContentIcon = bundle.getInt(KEY_CONTENT_ICON);
                this.mContentIconGravity = bundle.getInt(KEY_CONTENT_ICON_GRAVITY, 8388613);
                this.mContentActionIndex = bundle.getInt(KEY_CONTENT_ACTION_INDEX, -1);
                this.mCustomSizePreset = bundle.getInt(KEY_CUSTOM_SIZE_PRESET, 0);
                this.mCustomContentHeight = bundle.getInt(KEY_CUSTOM_CONTENT_HEIGHT);
                this.mGravity = bundle.getInt(KEY_GRAVITY, 80);
                this.mHintScreenTimeout = bundle.getInt(KEY_HINT_SCREEN_TIMEOUT);
                this.mDismissalId = bundle.getString(KEY_DISMISSAL_ID);
                this.mBridgeTag = bundle.getString(KEY_BRIDGE_TAG);
            }
        }

        @Override // android.app.Notification.Extender
        public Builder extend(Builder builder) {
            Bundle bundle = new Bundle();
            if (!this.mActions.isEmpty()) {
                bundle.putParcelableArrayList("actions", this.mActions);
            }
            int i = this.mFlags;
            if (i != 1) {
                bundle.putInt("flags", i);
            }
            PendingIntent pendingIntent = this.mDisplayIntent;
            if (pendingIntent != null) {
                bundle.putParcelable(KEY_DISPLAY_INTENT, pendingIntent);
            }
            if (!this.mPages.isEmpty()) {
                ArrayList<Notification> arrayList = this.mPages;
                bundle.putParcelableArray(KEY_PAGES, (Parcelable[]) arrayList.toArray(new Notification[arrayList.size()]));
            }
            Bitmap bitmap = this.mBackground;
            if (bitmap != null) {
                bundle.putParcelable(KEY_BACKGROUND, bitmap);
            }
            int i2 = this.mContentIcon;
            if (i2 != 0) {
                bundle.putInt(KEY_CONTENT_ICON, i2);
            }
            int i3 = this.mContentIconGravity;
            if (i3 != 8388613) {
                bundle.putInt(KEY_CONTENT_ICON_GRAVITY, i3);
            }
            int i4 = this.mContentActionIndex;
            if (i4 != -1) {
                bundle.putInt(KEY_CONTENT_ACTION_INDEX, i4);
            }
            int i5 = this.mCustomSizePreset;
            if (i5 != 0) {
                bundle.putInt(KEY_CUSTOM_SIZE_PRESET, i5);
            }
            int i6 = this.mCustomContentHeight;
            if (i6 != 0) {
                bundle.putInt(KEY_CUSTOM_CONTENT_HEIGHT, i6);
            }
            int i7 = this.mGravity;
            if (i7 != 80) {
                bundle.putInt(KEY_GRAVITY, i7);
            }
            int i8 = this.mHintScreenTimeout;
            if (i8 != 0) {
                bundle.putInt(KEY_HINT_SCREEN_TIMEOUT, i8);
            }
            String str = this.mDismissalId;
            if (str != null) {
                bundle.putString(KEY_DISMISSAL_ID, str);
            }
            String str2 = this.mBridgeTag;
            if (str2 != null) {
                bundle.putString(KEY_BRIDGE_TAG, str2);
            }
            builder.getExtras().putBundle(EXTRA_WEARABLE_EXTENSIONS, bundle);
            return builder;
        }

        /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
        public WearableExtender m12clone() {
            WearableExtender wearableExtender = new WearableExtender();
            wearableExtender.mActions = new ArrayList<>(this.mActions);
            wearableExtender.mFlags = this.mFlags;
            wearableExtender.mDisplayIntent = this.mDisplayIntent;
            wearableExtender.mPages = new ArrayList<>(this.mPages);
            wearableExtender.mBackground = this.mBackground;
            wearableExtender.mContentIcon = this.mContentIcon;
            wearableExtender.mContentIconGravity = this.mContentIconGravity;
            wearableExtender.mContentActionIndex = this.mContentActionIndex;
            wearableExtender.mCustomSizePreset = this.mCustomSizePreset;
            wearableExtender.mCustomContentHeight = this.mCustomContentHeight;
            wearableExtender.mGravity = this.mGravity;
            wearableExtender.mHintScreenTimeout = this.mHintScreenTimeout;
            wearableExtender.mDismissalId = this.mDismissalId;
            wearableExtender.mBridgeTag = this.mBridgeTag;
            return wearableExtender;
        }

        public WearableExtender addAction(Action action) {
            this.mActions.add(action);
            return this;
        }

        public WearableExtender addActions(List<Action> list) {
            this.mActions.addAll(list);
            return this;
        }

        public WearableExtender clearActions() {
            this.mActions.clear();
            return this;
        }

        public List<Action> getActions() {
            return this.mActions;
        }

        public WearableExtender setDisplayIntent(PendingIntent pendingIntent) {
            this.mDisplayIntent = pendingIntent;
            return this;
        }

        public PendingIntent getDisplayIntent() {
            return this.mDisplayIntent;
        }

        public WearableExtender addPage(Notification notification) {
            this.mPages.add(notification);
            return this;
        }

        public WearableExtender addPages(List<Notification> list) {
            this.mPages.addAll(list);
            return this;
        }

        public WearableExtender clearPages() {
            this.mPages.clear();
            return this;
        }

        public List<Notification> getPages() {
            return this.mPages;
        }

        public WearableExtender setBackground(Bitmap bitmap) {
            this.mBackground = bitmap;
            return this;
        }

        public Bitmap getBackground() {
            return this.mBackground;
        }

        @Deprecated
        public WearableExtender setContentIcon(int i) {
            this.mContentIcon = i;
            return this;
        }

        @Deprecated
        public int getContentIcon() {
            return this.mContentIcon;
        }

        @Deprecated
        public WearableExtender setContentIconGravity(int i) {
            this.mContentIconGravity = i;
            return this;
        }

        @Deprecated
        public int getContentIconGravity() {
            return this.mContentIconGravity;
        }

        public WearableExtender setContentAction(int i) {
            this.mContentActionIndex = i;
            return this;
        }

        public int getContentAction() {
            return this.mContentActionIndex;
        }

        @Deprecated
        public WearableExtender setGravity(int i) {
            this.mGravity = i;
            return this;
        }

        @Deprecated
        public int getGravity() {
            return this.mGravity;
        }

        @Deprecated
        public WearableExtender setCustomSizePreset(int i) {
            this.mCustomSizePreset = i;
            return this;
        }

        @Deprecated
        public int getCustomSizePreset() {
            return this.mCustomSizePreset;
        }

        @Deprecated
        public WearableExtender setCustomContentHeight(int i) {
            this.mCustomContentHeight = i;
            return this;
        }

        @Deprecated
        public int getCustomContentHeight() {
            return this.mCustomContentHeight;
        }

        public WearableExtender setStartScrollBottom(boolean z) {
            setFlag(8, z);
            return this;
        }

        public boolean getStartScrollBottom() {
            return (this.mFlags & 8) != 0;
        }

        public WearableExtender setContentIntentAvailableOffline(boolean z) {
            setFlag(1, z);
            return this;
        }

        public boolean getContentIntentAvailableOffline() {
            return (this.mFlags & 1) != 0;
        }

        @Deprecated
        public WearableExtender setHintHideIcon(boolean z) {
            setFlag(2, z);
            return this;
        }

        @Deprecated
        public boolean getHintHideIcon() {
            return (this.mFlags & 2) != 0;
        }

        @Deprecated
        public WearableExtender setHintShowBackgroundOnly(boolean z) {
            setFlag(4, z);
            return this;
        }

        @Deprecated
        public boolean getHintShowBackgroundOnly() {
            return (this.mFlags & 4) != 0;
        }

        @Deprecated
        public WearableExtender setHintAvoidBackgroundClipping(boolean z) {
            setFlag(16, z);
            return this;
        }

        @Deprecated
        public boolean getHintAvoidBackgroundClipping() {
            return (this.mFlags & 16) != 0;
        }

        @Deprecated
        public WearableExtender setHintScreenTimeout(int i) {
            this.mHintScreenTimeout = i;
            return this;
        }

        @Deprecated
        public int getHintScreenTimeout() {
            return this.mHintScreenTimeout;
        }

        public WearableExtender setHintAmbientBigPicture(boolean z) {
            setFlag(32, z);
            return this;
        }

        public boolean getHintAmbientBigPicture() {
            return (this.mFlags & 32) != 0;
        }

        public WearableExtender setHintContentIntentLaunchesActivity(boolean z) {
            setFlag(64, z);
            return this;
        }

        public boolean getHintContentIntentLaunchesActivity() {
            return (this.mFlags & 64) != 0;
        }

        public WearableExtender setDismissalId(String str) {
            this.mDismissalId = str;
            return this;
        }

        public String getDismissalId() {
            return this.mDismissalId;
        }

        public WearableExtender setBridgeTag(String str) {
            this.mBridgeTag = str;
            return this;
        }

        public String getBridgeTag() {
            return this.mBridgeTag;
        }

        private void setFlag(int i, boolean z) {
            if (z) {
                this.mFlags = i | this.mFlags;
            } else {
                this.mFlags = (~i) & this.mFlags;
            }
        }
    }

    public static final class CarExtender implements Extender {
        private static final String EXTRA_CAR_EXTENDER = "android.car.EXTENSIONS";
        private static final String EXTRA_COLOR = "app_color";
        private static final String EXTRA_CONVERSATION = "car_conversation";
        private static final String EXTRA_LARGE_ICON = "large_icon";
        private static final String TAG = "CarExtender";
        private int mColor;
        private Bitmap mLargeIcon;
        private UnreadConversation mUnreadConversation;

        public CarExtender() {
            this.mColor = 0;
        }

        public CarExtender(Notification notification) {
            this.mColor = 0;
            Bundle bundle = notification.extras == null ? null : notification.extras.getBundle(EXTRA_CAR_EXTENDER);
            if (bundle != null) {
                this.mLargeIcon = (Bitmap) bundle.getParcelable(EXTRA_LARGE_ICON);
                this.mColor = bundle.getInt(EXTRA_COLOR, 0);
                this.mUnreadConversation = UnreadConversation.getUnreadConversationFromBundle(bundle.getBundle(EXTRA_CONVERSATION));
            }
        }

        @Override // android.app.Notification.Extender
        public Builder extend(Builder builder) {
            Bundle bundle = new Bundle();
            Bitmap bitmap = this.mLargeIcon;
            if (bitmap != null) {
                bundle.putParcelable(EXTRA_LARGE_ICON, bitmap);
            }
            int i = this.mColor;
            if (i != 0) {
                bundle.putInt(EXTRA_COLOR, i);
            }
            UnreadConversation unreadConversation = this.mUnreadConversation;
            if (unreadConversation != null) {
                bundle.putBundle(EXTRA_CONVERSATION, unreadConversation.getBundleForUnreadConversation());
            }
            builder.getExtras().putBundle(EXTRA_CAR_EXTENDER, bundle);
            return builder;
        }

        public CarExtender setColor(int i) {
            this.mColor = i;
            return this;
        }

        public int getColor() {
            return this.mColor;
        }

        public CarExtender setLargeIcon(Bitmap bitmap) {
            this.mLargeIcon = bitmap;
            return this;
        }

        public Bitmap getLargeIcon() {
            return this.mLargeIcon;
        }

        public CarExtender setUnreadConversation(UnreadConversation unreadConversation) {
            this.mUnreadConversation = unreadConversation;
            return this;
        }

        public UnreadConversation getUnreadConversation() {
            return this.mUnreadConversation;
        }

        public static class UnreadConversation {
            private static final String KEY_AUTHOR = "author";
            private static final String KEY_MESSAGES = "messages";
            private static final String KEY_ON_READ = "on_read";
            private static final String KEY_ON_REPLY = "on_reply";
            private static final String KEY_PARTICIPANTS = "participants";
            private static final String KEY_REMOTE_INPUT = "remote_input";
            private static final String KEY_TEXT = "text";
            private static final String KEY_TIMESTAMP = "timestamp";
            private final long mLatestTimestamp;
            private final String[] mMessages;
            private final String[] mParticipants;
            private final PendingIntent mReadPendingIntent;
            private final RemoteInput mRemoteInput;
            private final PendingIntent mReplyPendingIntent;

            UnreadConversation(String[] strArr, RemoteInput remoteInput, PendingIntent pendingIntent, PendingIntent pendingIntent2, String[] strArr2, long j) {
                this.mMessages = strArr;
                this.mRemoteInput = remoteInput;
                this.mReadPendingIntent = pendingIntent2;
                this.mReplyPendingIntent = pendingIntent;
                this.mParticipants = strArr2;
                this.mLatestTimestamp = j;
            }

            public String[] getMessages() {
                return this.mMessages;
            }

            public RemoteInput getRemoteInput() {
                return this.mRemoteInput;
            }

            public PendingIntent getReplyPendingIntent() {
                return this.mReplyPendingIntent;
            }

            public PendingIntent getReadPendingIntent() {
                return this.mReadPendingIntent;
            }

            public String[] getParticipants() {
                return this.mParticipants;
            }

            public String getParticipant() {
                String[] strArr = this.mParticipants;
                if (strArr.length > 0) {
                    return strArr[0];
                }
                return null;
            }

            public long getLatestTimestamp() {
                return this.mLatestTimestamp;
            }

            Bundle getBundleForUnreadConversation() {
                Bundle bundle = new Bundle();
                String[] strArr = this.mParticipants;
                String str = (strArr == null || strArr.length <= 1) ? null : strArr[0];
                int length = this.mMessages.length;
                Parcelable[] parcelableArr = new Parcelable[length];
                for (int i = 0; i < length; i++) {
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("text", this.mMessages[i]);
                    bundle2.putString("author", str);
                    parcelableArr[i] = bundle2;
                }
                bundle.putParcelableArray(KEY_MESSAGES, parcelableArr);
                RemoteInput remoteInput = this.mRemoteInput;
                if (remoteInput != null) {
                    bundle.putParcelable(KEY_REMOTE_INPUT, remoteInput);
                }
                bundle.putParcelable(KEY_ON_REPLY, this.mReplyPendingIntent);
                bundle.putParcelable(KEY_ON_READ, this.mReadPendingIntent);
                bundle.putStringArray(KEY_PARTICIPANTS, this.mParticipants);
                bundle.putLong("timestamp", this.mLatestTimestamp);
                return bundle;
            }

            static UnreadConversation getUnreadConversationFromBundle(Bundle bundle) {
                String[] strArr;
                if (bundle == null) {
                    return null;
                }
                Parcelable[] parcelableArray = bundle.getParcelableArray(KEY_MESSAGES);
                if (parcelableArray != null) {
                    int length = parcelableArray.length;
                    String[] strArr2 = new String[length];
                    boolean z = false;
                    int i = 0;
                    while (true) {
                        if (i >= length) {
                            z = true;
                            break;
                        }
                        if (!(parcelableArray[i] instanceof Bundle)) {
                            break;
                        }
                        strArr2[i] = ((Bundle) parcelableArray[i]).getString("text");
                        if (strArr2[i] == null) {
                            break;
                        }
                        i++;
                    }
                    if (!z) {
                        return null;
                    }
                    strArr = strArr2;
                } else {
                    strArr = null;
                }
                PendingIntent pendingIntent = (PendingIntent) bundle.getParcelable(KEY_ON_READ);
                PendingIntent pendingIntent2 = (PendingIntent) bundle.getParcelable(KEY_ON_REPLY);
                RemoteInput remoteInput = (RemoteInput) bundle.getParcelable(KEY_REMOTE_INPUT);
                String[] stringArray = bundle.getStringArray(KEY_PARTICIPANTS);
                if (stringArray == null || stringArray.length != 1) {
                    return null;
                }
                return new UnreadConversation(strArr, remoteInput, pendingIntent2, pendingIntent, stringArray, bundle.getLong("timestamp"));
            }
        }

        public static class Builder {
            private long mLatestTimestamp;
            private final List<String> mMessages = new ArrayList();
            private final String mParticipant;
            private PendingIntent mReadPendingIntent;
            private RemoteInput mRemoteInput;
            private PendingIntent mReplyPendingIntent;

            public Builder(String str) {
                this.mParticipant = str;
            }

            public Builder addMessage(String str) {
                this.mMessages.add(str);
                return this;
            }

            public Builder setReplyAction(PendingIntent pendingIntent, RemoteInput remoteInput) {
                this.mRemoteInput = remoteInput;
                this.mReplyPendingIntent = pendingIntent;
                return this;
            }

            public Builder setReadPendingIntent(PendingIntent pendingIntent) {
                this.mReadPendingIntent = pendingIntent;
                return this;
            }

            public Builder setLatestTimestamp(long j) {
                this.mLatestTimestamp = j;
                return this;
            }

            public UnreadConversation build() {
                List<String> list = this.mMessages;
                return new UnreadConversation((String[]) list.toArray(new String[list.size()]), this.mRemoteInput, this.mReplyPendingIntent, this.mReadPendingIntent, new String[]{this.mParticipant}, this.mLatestTimestamp);
            }
        }
    }

    @SystemApi
    public static final class TvExtender implements Extender {
        private static final String EXTRA_CHANNEL_ID = "channel_id";
        private static final String EXTRA_CONTENT_INTENT = "content_intent";
        private static final String EXTRA_DELETE_INTENT = "delete_intent";
        private static final String EXTRA_FLAGS = "flags";
        private static final String EXTRA_SUPPRESS_SHOW_OVER_APPS = "suppressShowOverApps";
        private static final String EXTRA_TV_EXTENDER = "android.tv.EXTENSIONS";
        private static final int FLAG_AVAILABLE_ON_TV = 1;
        private static final String TAG = "TvExtender";
        private String mChannelId;
        private PendingIntent mContentIntent;
        private PendingIntent mDeleteIntent;
        private int mFlags;
        private boolean mSuppressShowOverApps;

        public TvExtender() {
            this.mFlags = 1;
        }

        public TvExtender(Notification notification) {
            Bundle bundle = notification.extras == null ? null : notification.extras.getBundle(EXTRA_TV_EXTENDER);
            if (bundle != null) {
                this.mFlags = bundle.getInt("flags");
                this.mChannelId = bundle.getString("channel_id");
                this.mSuppressShowOverApps = bundle.getBoolean(EXTRA_SUPPRESS_SHOW_OVER_APPS);
                this.mContentIntent = (PendingIntent) bundle.getParcelable(EXTRA_CONTENT_INTENT);
                this.mDeleteIntent = (PendingIntent) bundle.getParcelable(EXTRA_DELETE_INTENT);
            }
        }

        @Override // android.app.Notification.Extender
        public Builder extend(Builder builder) {
            Bundle bundle = new Bundle();
            bundle.putInt("flags", this.mFlags);
            bundle.putString("channel_id", this.mChannelId);
            bundle.putBoolean(EXTRA_SUPPRESS_SHOW_OVER_APPS, this.mSuppressShowOverApps);
            PendingIntent pendingIntent = this.mContentIntent;
            if (pendingIntent != null) {
                bundle.putParcelable(EXTRA_CONTENT_INTENT, pendingIntent);
            }
            PendingIntent pendingIntent2 = this.mDeleteIntent;
            if (pendingIntent2 != null) {
                bundle.putParcelable(EXTRA_DELETE_INTENT, pendingIntent2);
            }
            builder.getExtras().putBundle(EXTRA_TV_EXTENDER, bundle);
            return builder;
        }

        public boolean isAvailableOnTv() {
            return (this.mFlags & 1) != 0;
        }

        public TvExtender setChannel(String str) {
            this.mChannelId = str;
            return this;
        }

        public TvExtender setChannelId(String str) {
            this.mChannelId = str;
            return this;
        }

        @Deprecated
        public String getChannel() {
            return this.mChannelId;
        }

        public String getChannelId() {
            return this.mChannelId;
        }

        public TvExtender setContentIntent(PendingIntent pendingIntent) {
            this.mContentIntent = pendingIntent;
            return this;
        }

        public PendingIntent getContentIntent() {
            return this.mContentIntent;
        }

        public TvExtender setDeleteIntent(PendingIntent pendingIntent) {
            this.mDeleteIntent = pendingIntent;
            return this;
        }

        public PendingIntent getDeleteIntent() {
            return this.mDeleteIntent;
        }

        public TvExtender setSuppressShowOverApps(boolean z) {
            this.mSuppressShowOverApps = z;
            return this;
        }

        public boolean getSuppressShowOverApps() {
            return this.mSuppressShowOverApps;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Notification[] getNotificationArrayFromBundle(Bundle bundle, String str) {
        Parcelable[] parcelableArray = bundle.getParcelableArray(str);
        if ((parcelableArray instanceof Notification[]) || parcelableArray == null) {
            return (Notification[]) parcelableArray;
        }
        Notification[] notificationArr = (Notification[]) Arrays.copyOf(parcelableArray, parcelableArray.length, Notification[].class);
        bundle.putParcelableArray(str, notificationArr);
        return notificationArr;
    }

    private static class BuilderRemoteViews extends RemoteViews {
        public BuilderRemoteViews(Parcel parcel) {
            super(parcel);
        }

        public BuilderRemoteViews(ApplicationInfo applicationInfo, int i) {
            super(applicationInfo, i);
        }

        @Override // android.widget.RemoteViews
        /* JADX INFO: renamed from: clone */
        public BuilderRemoteViews mo11clone() {
            Parcel parcelObtain = Parcel.obtain();
            writeToParcel(parcelObtain, 0);
            parcelObtain.setDataPosition(0);
            BuilderRemoteViews builderRemoteViews = new BuilderRemoteViews(parcelObtain);
            parcelObtain.recycle();
            return builderRemoteViews;
        }
    }

    private static class TemplateBindResult {
        int mIconMarginEnd;

        private TemplateBindResult() {
        }

        public int getIconMarginEnd() {
            return this.mIconMarginEnd;
        }

        public void setIconMarginEnd(int i) {
            this.mIconMarginEnd = i;
        }
    }

    private static class StandardTemplateParams {
        boolean ambient;
        boolean hasProgress;
        CharSequence headerTextSecondary;
        boolean hideLargeIcon;
        boolean hideReplyIcon;
        int maxRemoteInputHistory;
        CharSequence text;
        CharSequence title;

        private StandardTemplateParams() {
            this.hasProgress = true;
            this.ambient = false;
            this.maxRemoteInputHistory = 3;
        }

        final StandardTemplateParams reset() {
            this.hasProgress = true;
            this.ambient = false;
            this.title = null;
            this.text = null;
            this.headerTextSecondary = null;
            this.maxRemoteInputHistory = 3;
            return this;
        }

        final StandardTemplateParams hasProgress(boolean z) {
            this.hasProgress = z;
            return this;
        }

        final StandardTemplateParams title(CharSequence charSequence) {
            this.title = charSequence;
            return this;
        }

        final StandardTemplateParams text(CharSequence charSequence) {
            this.text = charSequence;
            return this;
        }

        final StandardTemplateParams headerTextSecondary(CharSequence charSequence) {
            this.headerTextSecondary = charSequence;
            return this;
        }

        final StandardTemplateParams hideLargeIcon(boolean z) {
            this.hideLargeIcon = z;
            return this;
        }

        final StandardTemplateParams hideReplyIcon(boolean z) {
            this.hideReplyIcon = z;
            return this;
        }

        final StandardTemplateParams ambient(boolean z) {
            Preconditions.checkState(this.title == null && this.text == null, "must set ambient before text");
            this.ambient = z;
            return this;
        }

        final StandardTemplateParams fillTextsFrom(Builder builder) {
            Bundle bundle = builder.mN.extras;
            this.title = builder.processLegacyText(bundle.getCharSequence("android.title"), this.ambient);
            CharSequence charSequence = bundle.getCharSequence("android.bigText");
            if (!this.ambient || TextUtils.isEmpty(charSequence)) {
                charSequence = bundle.getCharSequence("android.text");
            }
            this.text = builder.processLegacyText(charSequence, this.ambient);
            return this;
        }

        public StandardTemplateParams setMaxRemoteInputHistory(int i) {
            this.maxRemoteInputHistory = i;
            return this;
        }
    }
}
