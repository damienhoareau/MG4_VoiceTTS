package android.view.textclassifier;

import android.content.Context;
import android.database.ContentObserver;
import android.os.ServiceManager;
import android.provider.Settings;
import android.service.textclassifier.TextClassifierService;
import com.android.internal.util.Preconditions;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes2.dex */
public final class TextClassificationManager {
    private static final String LOG_TAG = "TextClassificationManager";
    private final Context mContext;
    private TextClassifier mCustomTextClassifier;
    private TextClassifier mLocalTextClassifier;
    private TextClassificationConstants mSettings;
    private TextClassifier mSystemTextClassifier;
    private final Object mLock = new Object();
    private final TextClassificationSessionFactory mDefaultSessionFactory = new TextClassificationSessionFactory() { // from class: android.view.textclassifier.-$$Lambda$TextClassificationManager$JIaezIJbMig_-kVzN6oArzkTsJE
        @Override // android.view.textclassifier.TextClassificationSessionFactory
        public final TextClassifier createTextClassificationSession(TextClassificationContext textClassificationContext) {
            return this.f$0.lambda$new$0$TextClassificationManager(textClassificationContext);
        }
    };
    private TextClassificationSessionFactory mSessionFactory = this.mDefaultSessionFactory;
    private final SettingsObserver mSettingsObserver = new SettingsObserver(this);

    public /* synthetic */ TextClassifier lambda$new$0$TextClassificationManager(TextClassificationContext textClassificationContext) {
        return new TextClassificationSession(textClassificationContext, getTextClassifier());
    }

    public TextClassificationManager(Context context) {
        this.mContext = (Context) Preconditions.checkNotNull(context);
    }

    public TextClassifier getTextClassifier() {
        synchronized (this.mLock) {
            if (this.mCustomTextClassifier != null) {
                return this.mCustomTextClassifier;
            }
            if (isSystemTextClassifierEnabled()) {
                return getSystemTextClassifier();
            }
            return getLocalTextClassifier();
        }
    }

    public void setTextClassifier(TextClassifier textClassifier) {
        synchronized (this.mLock) {
            this.mCustomTextClassifier = textClassifier;
        }
    }

    public TextClassifier getTextClassifier(int i) {
        if (i == 0) {
            return getLocalTextClassifier();
        }
        return getSystemTextClassifier();
    }

    private TextClassificationConstants getSettings() {
        TextClassificationConstants textClassificationConstants;
        synchronized (this.mLock) {
            if (this.mSettings == null) {
                this.mSettings = TextClassificationConstants.loadFromString(Settings.Global.getString(getApplicationContext().getContentResolver(), Settings.Global.TEXT_CLASSIFIER_CONSTANTS));
            }
            textClassificationConstants = this.mSettings;
        }
        return textClassificationConstants;
    }

    public TextClassifier createTextClassificationSession(TextClassificationContext textClassificationContext) {
        Preconditions.checkNotNull(textClassificationContext);
        TextClassifier textClassifierCreateTextClassificationSession = this.mSessionFactory.createTextClassificationSession(textClassificationContext);
        Preconditions.checkNotNull(textClassifierCreateTextClassificationSession, "Session Factory should never return null");
        return textClassifierCreateTextClassificationSession;
    }

    public TextClassifier createTextClassificationSession(TextClassificationContext textClassificationContext, TextClassifier textClassifier) {
        Preconditions.checkNotNull(textClassificationContext);
        Preconditions.checkNotNull(textClassifier);
        return new TextClassificationSession(textClassificationContext, textClassifier);
    }

    public void setTextClassificationSessionFactory(TextClassificationSessionFactory textClassificationSessionFactory) {
        synchronized (this.mLock) {
            try {
                if (textClassificationSessionFactory != null) {
                    this.mSessionFactory = textClassificationSessionFactory;
                } else {
                    this.mSessionFactory = this.mDefaultSessionFactory;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    protected void finalize() throws Throwable {
        try {
            if (this.mSettingsObserver != null) {
                getApplicationContext().getContentResolver().unregisterContentObserver(this.mSettingsObserver);
            }
        } finally {
            super.finalize();
        }
    }

    private TextClassifier getSystemTextClassifier() {
        synchronized (this.mLock) {
            if (this.mSystemTextClassifier == null && isSystemTextClassifierEnabled()) {
                try {
                    this.mSystemTextClassifier = new SystemTextClassifier(this.mContext, getSettings());
                    Log.d(LOG_TAG, "Initialized SystemTextClassifier");
                } catch (ServiceManager.ServiceNotFoundException e) {
                    Log.e(LOG_TAG, "Could not initialize SystemTextClassifier", e);
                }
            }
        }
        TextClassifier textClassifier = this.mSystemTextClassifier;
        return textClassifier != null ? textClassifier : TextClassifier.NO_OP;
    }

    private TextClassifier getLocalTextClassifier() {
        TextClassifier textClassifier;
        synchronized (this.mLock) {
            if (this.mLocalTextClassifier == null) {
                if (getSettings().isLocalTextClassifierEnabled()) {
                    this.mLocalTextClassifier = new TextClassifierImpl(this.mContext, getSettings(), TextClassifier.NO_OP);
                } else {
                    Log.d(LOG_TAG, "Local TextClassifier disabled");
                    this.mLocalTextClassifier = TextClassifier.NO_OP;
                }
            }
            textClassifier = this.mLocalTextClassifier;
        }
        return textClassifier;
    }

    private boolean isSystemTextClassifierEnabled() {
        return getSettings().isSystemTextClassifierEnabled() && TextClassifierService.getServiceComponentName(this.mContext) != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invalidate() {
        synchronized (this.mLock) {
            this.mSettings = null;
            this.mLocalTextClassifier = null;
            this.mSystemTextClassifier = null;
        }
    }

    Context getApplicationContext() {
        if (this.mContext.getApplicationContext() != null) {
            return this.mContext.getApplicationContext();
        }
        return this.mContext;
    }

    public static TextClassificationConstants getSettings(Context context) {
        Preconditions.checkNotNull(context);
        TextClassificationManager textClassificationManager = (TextClassificationManager) context.getSystemService(TextClassificationManager.class);
        if (textClassificationManager != null) {
            return textClassificationManager.getSettings();
        }
        return TextClassificationConstants.loadFromString(Settings.Global.getString(context.getApplicationContext().getContentResolver(), Settings.Global.TEXT_CLASSIFIER_CONSTANTS));
    }

    private static final class SettingsObserver extends ContentObserver {
        private final WeakReference<TextClassificationManager> mTcm;

        SettingsObserver(TextClassificationManager textClassificationManager) {
            super(null);
            this.mTcm = new WeakReference<>(textClassificationManager);
            textClassificationManager.getApplicationContext().getContentResolver().registerContentObserver(Settings.Global.getUriFor(Settings.Global.TEXT_CLASSIFIER_CONSTANTS), false, this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            TextClassificationManager textClassificationManager = this.mTcm.get();
            if (textClassificationManager != null) {
                textClassificationManager.invalidate();
            }
        }
    }
}
