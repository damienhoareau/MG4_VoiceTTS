package android.view.textclassifier;

import com.android.internal.util.Preconditions;

/* JADX INFO: loaded from: classes2.dex */
final class TextClassificationSession implements TextClassifier {
    static final boolean DEBUG_LOG_ENABLED = true;
    private static final String LOG_TAG = "TextClassificationSession";
    private final TextClassificationContext mClassificationContext;
    private final TextClassifier mDelegate;
    private boolean mDestroyed;
    private final SelectionEventHelper mEventHelper;
    private final TextClassificationSessionId mSessionId = new TextClassificationSessionId();

    TextClassificationSession(TextClassificationContext textClassificationContext, TextClassifier textClassifier) {
        this.mClassificationContext = (TextClassificationContext) Preconditions.checkNotNull(textClassificationContext);
        this.mDelegate = (TextClassifier) Preconditions.checkNotNull(textClassifier);
        this.mEventHelper = new SelectionEventHelper(this.mSessionId, this.mClassificationContext);
        initializeRemoteSession();
    }

    @Override // android.view.textclassifier.TextClassifier
    public TextSelection suggestSelection(TextSelection.Request request) {
        checkDestroyed();
        return this.mDelegate.suggestSelection(request);
    }

    private void initializeRemoteSession() {
        TextClassifier textClassifier = this.mDelegate;
        if (textClassifier instanceof SystemTextClassifier) {
            ((SystemTextClassifier) textClassifier).initializeRemoteSession(this.mClassificationContext, this.mSessionId);
        }
    }

    @Override // android.view.textclassifier.TextClassifier
    public TextClassification classifyText(TextClassification.Request request) {
        checkDestroyed();
        return this.mDelegate.classifyText(request);
    }

    @Override // android.view.textclassifier.TextClassifier
    public TextLinks generateLinks(TextLinks.Request request) {
        checkDestroyed();
        return this.mDelegate.generateLinks(request);
    }

    @Override // android.view.textclassifier.TextClassifier
    public void onSelectionEvent(SelectionEvent selectionEvent) {
        checkDestroyed();
        Preconditions.checkNotNull(selectionEvent);
        if (this.mEventHelper.sanitizeEvent(selectionEvent)) {
            this.mDelegate.onSelectionEvent(selectionEvent);
        }
    }

    @Override // android.view.textclassifier.TextClassifier
    public void destroy() {
        this.mEventHelper.endSession();
        this.mDelegate.destroy();
        this.mDestroyed = true;
    }

    @Override // android.view.textclassifier.TextClassifier
    public boolean isDestroyed() {
        return this.mDestroyed;
    }

    private void checkDestroyed() {
        if (this.mDestroyed) {
            throw new IllegalStateException("This TextClassification session has been destroyed");
        }
    }

    private static final class SelectionEventHelper {
        private final TextClassificationContext mContext;
        private int mInvocationMethod = 0;
        private SelectionEvent mPrevEvent;
        private final TextClassificationSessionId mSessionId;
        private SelectionEvent mSmartEvent;
        private SelectionEvent mStartEvent;

        SelectionEventHelper(TextClassificationSessionId textClassificationSessionId, TextClassificationContext textClassificationContext) {
            this.mSessionId = (TextClassificationSessionId) Preconditions.checkNotNull(textClassificationSessionId);
            this.mContext = (TextClassificationContext) Preconditions.checkNotNull(textClassificationContext);
        }

        /* JADX WARN: Code duplicated, block: B:20:0x0034  */
        boolean sanitizeEvent(SelectionEvent selectionEvent) {
            SelectionEvent selectionEvent2;
            updateInvocationMethod(selectionEvent);
            modifyAutoSelectionEventType(selectionEvent);
            if (selectionEvent.getEventType() != 1 && this.mStartEvent == null) {
                Log.d(TextClassificationSession.LOG_TAG, "Selection session not yet started. Ignoring event");
                return false;
            }
            long jCurrentTimeMillis = System.currentTimeMillis();
            int eventType = selectionEvent.getEventType();
            if (eventType == 1) {
                Preconditions.checkArgument(selectionEvent.getAbsoluteEnd() == selectionEvent.getAbsoluteStart() + 1);
                selectionEvent.setSessionId(this.mSessionId);
                this.mStartEvent = selectionEvent;
            } else if (eventType == 2) {
                selectionEvent2 = this.mPrevEvent;
                if (selectionEvent2 != null && selectionEvent2.getAbsoluteStart() == selectionEvent.getAbsoluteStart() && this.mPrevEvent.getAbsoluteEnd() == selectionEvent.getAbsoluteEnd()) {
                    return false;
                }
            } else if (eventType == 3 || eventType == 4) {
                this.mSmartEvent = selectionEvent;
            } else if (eventType == 5) {
                selectionEvent2 = this.mPrevEvent;
                if (selectionEvent2 != null) {
                    return false;
                }
            }
            selectionEvent.setEventTime(jCurrentTimeMillis);
            SelectionEvent selectionEvent3 = this.mStartEvent;
            if (selectionEvent3 != null) {
                selectionEvent.setSessionId(selectionEvent3.getSessionId()).setDurationSinceSessionStart(jCurrentTimeMillis - this.mStartEvent.getEventTime()).setStart(selectionEvent.getAbsoluteStart() - this.mStartEvent.getAbsoluteStart()).setEnd(selectionEvent.getAbsoluteEnd() - this.mStartEvent.getAbsoluteStart());
            }
            SelectionEvent selectionEvent4 = this.mSmartEvent;
            if (selectionEvent4 != null) {
                selectionEvent.setResultId(selectionEvent4.getResultId()).setSmartStart(this.mSmartEvent.getAbsoluteStart() - this.mStartEvent.getAbsoluteStart()).setSmartEnd(this.mSmartEvent.getAbsoluteEnd() - this.mStartEvent.getAbsoluteStart());
            }
            SelectionEvent selectionEvent5 = this.mPrevEvent;
            if (selectionEvent5 != null) {
                selectionEvent.setDurationSincePreviousEvent(jCurrentTimeMillis - selectionEvent5.getEventTime()).setEventIndex(this.mPrevEvent.getEventIndex() + 1);
            }
            this.mPrevEvent = selectionEvent;
            return true;
        }

        void endSession() {
            this.mPrevEvent = null;
            this.mSmartEvent = null;
            this.mStartEvent = null;
        }

        private void updateInvocationMethod(SelectionEvent selectionEvent) {
            selectionEvent.setTextClassificationSessionContext(this.mContext);
            if (selectionEvent.getInvocationMethod() == 0) {
                selectionEvent.setInvocationMethod(this.mInvocationMethod);
            } else {
                this.mInvocationMethod = selectionEvent.getInvocationMethod();
            }
        }

        private void modifyAutoSelectionEventType(SelectionEvent selectionEvent) {
            int eventType = selectionEvent.getEventType();
            if (eventType == 3 || eventType == 4 || eventType == 5) {
                if (isPlatformLocalTextClassifierSmartSelection(selectionEvent.getResultId())) {
                    if (selectionEvent.getAbsoluteEnd() - selectionEvent.getAbsoluteStart() > 1) {
                        selectionEvent.setEventType(4);
                        return;
                    } else {
                        selectionEvent.setEventType(3);
                        return;
                    }
                }
                selectionEvent.setEventType(5);
            }
        }

        private static boolean isPlatformLocalTextClassifierSmartSelection(String str) {
            return TextClassifier.DEFAULT_LOG_TAG.equals(SelectionSessionLogger.SignatureParser.getClassifierId(str));
        }
    }
}
