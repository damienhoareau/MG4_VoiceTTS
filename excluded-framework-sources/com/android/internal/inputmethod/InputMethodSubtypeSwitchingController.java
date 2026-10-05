package com.android.internal.inputmethod;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Printer;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TreeMap;

/* JADX INFO: loaded from: classes3.dex */
public class InputMethodSubtypeSwitchingController {
    private static final boolean DEBUG = false;
    private static final int NOT_A_SUBTYPE_ID = -1;
    private static final String TAG = InputMethodSubtypeSwitchingController.class.getSimpleName();
    private ControllerImpl mController;
    private final InputMethodUtils.InputMethodSettings mSettings;
    private InputMethodAndSubtypeList mSubtypeList;

    public static class ImeSubtypeListItem implements Comparable<ImeSubtypeListItem> {
        public final CharSequence mImeName;
        public final InputMethodInfo mImi;
        public final boolean mIsSystemLanguage;
        public final boolean mIsSystemLocale;
        public final int mSubtypeId;
        public final CharSequence mSubtypeName;

        public ImeSubtypeListItem(CharSequence charSequence, CharSequence charSequence2, InputMethodInfo inputMethodInfo, int i, String str, String str2) {
            this.mImeName = charSequence;
            this.mSubtypeName = charSequence2;
            this.mImi = inputMethodInfo;
            this.mSubtypeId = i;
            boolean z = false;
            if (TextUtils.isEmpty(str)) {
                this.mIsSystemLocale = false;
                this.mIsSystemLanguage = false;
                return;
            }
            boolean zEquals = str.equals(str2);
            this.mIsSystemLocale = zEquals;
            if (zEquals) {
                this.mIsSystemLanguage = true;
                return;
            }
            String languageFromLocaleString = parseLanguageFromLocaleString(str2);
            String languageFromLocaleString2 = parseLanguageFromLocaleString(str);
            if (languageFromLocaleString.length() >= 2 && languageFromLocaleString.equals(languageFromLocaleString2)) {
                z = true;
            }
            this.mIsSystemLanguage = z;
        }

        private static String parseLanguageFromLocaleString(String str) {
            int iIndexOf = str.indexOf(95);
            return iIndexOf < 0 ? str : str.substring(0, iIndexOf);
        }

        private static int compareNullableCharSequences(CharSequence charSequence, CharSequence charSequence2) {
            boolean zIsEmpty = TextUtils.isEmpty(charSequence);
            boolean zIsEmpty2 = TextUtils.isEmpty(charSequence2);
            if (zIsEmpty || zIsEmpty2) {
                return (zIsEmpty ? 1 : 0) - (zIsEmpty2 ? 1 : 0);
            }
            return charSequence.toString().compareTo(charSequence2.toString());
        }

        @Override // java.lang.Comparable
        public int compareTo(ImeSubtypeListItem imeSubtypeListItem) {
            int iCompareNullableCharSequences = compareNullableCharSequences(this.mImeName, imeSubtypeListItem.mImeName);
            if (iCompareNullableCharSequences != 0) {
                return iCompareNullableCharSequences;
            }
            int i = (this.mIsSystemLocale ? -1 : 0) - (imeSubtypeListItem.mIsSystemLocale ? -1 : 0);
            if (i != 0) {
                return i;
            }
            int i2 = (this.mIsSystemLanguage ? -1 : 0) - (imeSubtypeListItem.mIsSystemLanguage ? -1 : 0);
            return i2 != 0 ? i2 : compareNullableCharSequences(this.mSubtypeName, imeSubtypeListItem.mSubtypeName);
        }

        public String toString() {
            return "ImeSubtypeListItem{mImeName=" + ((Object) this.mImeName) + " mSubtypeName=" + ((Object) this.mSubtypeName) + " mSubtypeId=" + this.mSubtypeId + " mIsSystemLocale=" + this.mIsSystemLocale + " mIsSystemLanguage=" + this.mIsSystemLanguage + "}";
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof ImeSubtypeListItem)) {
                return false;
            }
            ImeSubtypeListItem imeSubtypeListItem = (ImeSubtypeListItem) obj;
            return Objects.equals(this.mImi, imeSubtypeListItem.mImi) && this.mSubtypeId == imeSubtypeListItem.mSubtypeId;
        }
    }

    private static class InputMethodAndSubtypeList {
        private final Context mContext;
        private final PackageManager mPm;
        private final InputMethodUtils.InputMethodSettings mSettings;
        private final TreeMap<InputMethodInfo, List<InputMethodSubtype>> mSortedImmis = new TreeMap<>(new Comparator<InputMethodInfo>() { // from class: com.android.internal.inputmethod.InputMethodSubtypeSwitchingController.InputMethodAndSubtypeList.1
            @Override // java.util.Comparator
            public int compare(InputMethodInfo inputMethodInfo, InputMethodInfo inputMethodInfo2) {
                if (inputMethodInfo2 == null) {
                    return 0;
                }
                if (inputMethodInfo == null) {
                    return 1;
                }
                if (InputMethodAndSubtypeList.this.mPm == null) {
                    return inputMethodInfo.getId().compareTo(inputMethodInfo2.getId());
                }
                return (((Object) inputMethodInfo.loadLabel(InputMethodAndSubtypeList.this.mPm)) + "/" + inputMethodInfo.getId()).toString().compareTo((((Object) inputMethodInfo2.loadLabel(InputMethodAndSubtypeList.this.mPm)) + "/" + inputMethodInfo2.getId()).toString());
            }
        });
        private final String mSystemLocaleStr;

        public InputMethodAndSubtypeList(Context context, InputMethodUtils.InputMethodSettings inputMethodSettings) {
            this.mContext = context;
            this.mSettings = inputMethodSettings;
            this.mPm = context.getPackageManager();
            Locale locale = context.getResources().getConfiguration().locale;
            this.mSystemLocaleStr = locale != null ? locale.toString() : "";
        }

        public List<ImeSubtypeListItem> getSortedInputMethodAndSubtypeList(boolean z, boolean z2) {
            HashMap<InputMethodInfo, List<InputMethodSubtype>> map;
            ArrayList arrayList = new ArrayList();
            HashMap<InputMethodInfo, List<InputMethodSubtype>> explicitlyOrImplicitlyEnabledInputMethodsAndSubtypeListLocked = this.mSettings.getExplicitlyOrImplicitlyEnabledInputMethodsAndSubtypeListLocked(this.mContext);
            if (explicitlyOrImplicitlyEnabledInputMethodsAndSubtypeListLocked == null || explicitlyOrImplicitlyEnabledInputMethodsAndSubtypeListLocked.size() == 0) {
                return Collections.emptyList();
            }
            boolean z3 = (z2 && z) ? false : z;
            this.mSortedImmis.clear();
            this.mSortedImmis.putAll(explicitlyOrImplicitlyEnabledInputMethodsAndSubtypeListLocked);
            for (InputMethodInfo inputMethodInfo : this.mSortedImmis.keySet()) {
                if (inputMethodInfo != null) {
                    List<InputMethodSubtype> list = explicitlyOrImplicitlyEnabledInputMethodsAndSubtypeListLocked.get(inputMethodInfo);
                    HashSet hashSet = new HashSet();
                    Iterator<InputMethodSubtype> it = list.iterator();
                    while (it.hasNext()) {
                        hashSet.add(String.valueOf(it.next().hashCode()));
                    }
                    CharSequence charSequenceLoadLabel = inputMethodInfo.loadLabel(this.mPm);
                    if (hashSet.size() > 0) {
                        int subtypeCount = inputMethodInfo.getSubtypeCount();
                        int i = 0;
                        while (i < subtypeCount) {
                            InputMethodSubtype subtypeAt = inputMethodInfo.getSubtypeAt(i);
                            String strValueOf = String.valueOf(subtypeAt.hashCode());
                            if (hashSet.contains(strValueOf) && (z3 || !subtypeAt.isAuxiliary())) {
                                arrayList.add(new ImeSubtypeListItem(charSequenceLoadLabel, subtypeAt.overridesImplicitlyEnabledSubtype() ? null : subtypeAt.getDisplayName(this.mContext, inputMethodInfo.getPackageName(), inputMethodInfo.getServiceInfo().applicationInfo), inputMethodInfo, i, subtypeAt.getLocale(), this.mSystemLocaleStr));
                                hashSet.remove(strValueOf);
                            }
                            i++;
                            subtypeCount = subtypeCount;
                            explicitlyOrImplicitlyEnabledInputMethodsAndSubtypeListLocked = explicitlyOrImplicitlyEnabledInputMethodsAndSubtypeListLocked;
                        }
                        map = explicitlyOrImplicitlyEnabledInputMethodsAndSubtypeListLocked;
                    } else {
                        map = explicitlyOrImplicitlyEnabledInputMethodsAndSubtypeListLocked;
                        arrayList.add(new ImeSubtypeListItem(charSequenceLoadLabel, null, inputMethodInfo, -1, null, this.mSystemLocaleStr));
                    }
                    explicitlyOrImplicitlyEnabledInputMethodsAndSubtypeListLocked = map;
                }
            }
            Collections.sort(arrayList);
            return arrayList;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int calculateSubtypeId(InputMethodInfo inputMethodInfo, InputMethodSubtype inputMethodSubtype) {
        if (inputMethodSubtype != null) {
            return InputMethodUtils.getSubtypeIdFromHashCode(inputMethodInfo, inputMethodSubtype.hashCode());
        }
        return -1;
    }

    private static class StaticRotationList {
        private final List<ImeSubtypeListItem> mImeSubtypeList;

        public StaticRotationList(List<ImeSubtypeListItem> list) {
            this.mImeSubtypeList = list;
        }

        private int getIndex(InputMethodInfo inputMethodInfo, InputMethodSubtype inputMethodSubtype) {
            int iCalculateSubtypeId = InputMethodSubtypeSwitchingController.calculateSubtypeId(inputMethodInfo, inputMethodSubtype);
            int size = this.mImeSubtypeList.size();
            for (int i = 0; i < size; i++) {
                ImeSubtypeListItem imeSubtypeListItem = this.mImeSubtypeList.get(i);
                if (inputMethodInfo.equals(imeSubtypeListItem.mImi) && imeSubtypeListItem.mSubtypeId == iCalculateSubtypeId) {
                    return i;
                }
            }
            return -1;
        }

        public ImeSubtypeListItem getNextInputMethodLocked(boolean z, InputMethodInfo inputMethodInfo, InputMethodSubtype inputMethodSubtype, boolean z2) {
            int index;
            if (inputMethodInfo == null) {
                return null;
            }
            if (this.mImeSubtypeList.size() <= 1 || (index = getIndex(inputMethodInfo, inputMethodSubtype)) < 0) {
                return null;
            }
            int size = this.mImeSubtypeList.size();
            for (int i = 1; i < size; i++) {
                ImeSubtypeListItem imeSubtypeListItem = this.mImeSubtypeList.get(((z2 ? i : size - i) + index) % size);
                if (!z || inputMethodInfo.equals(imeSubtypeListItem.mImi)) {
                    return imeSubtypeListItem;
                }
            }
            return null;
        }

        protected void dump(Printer printer, String str) {
            int size = this.mImeSubtypeList.size();
            for (int i = 0; i < size; i++) {
                printer.println(str + "rank=" + i + " item=" + this.mImeSubtypeList.get(i));
            }
        }
    }

    private static class DynamicRotationList {
        private static final String TAG = DynamicRotationList.class.getSimpleName();
        private final List<ImeSubtypeListItem> mImeSubtypeList;
        private final int[] mUsageHistoryOfSubtypeListItemIndex;

        private DynamicRotationList(List<ImeSubtypeListItem> list) {
            this.mImeSubtypeList = list;
            this.mUsageHistoryOfSubtypeListItemIndex = new int[list.size()];
            int size = this.mImeSubtypeList.size();
            for (int i = 0; i < size; i++) {
                this.mUsageHistoryOfSubtypeListItemIndex[i] = i;
            }
        }

        private int getUsageRank(InputMethodInfo inputMethodInfo, InputMethodSubtype inputMethodSubtype) {
            int iCalculateSubtypeId = InputMethodSubtypeSwitchingController.calculateSubtypeId(inputMethodInfo, inputMethodSubtype);
            int length = this.mUsageHistoryOfSubtypeListItemIndex.length;
            for (int i = 0; i < length; i++) {
                ImeSubtypeListItem imeSubtypeListItem = this.mImeSubtypeList.get(this.mUsageHistoryOfSubtypeListItemIndex[i]);
                if (imeSubtypeListItem.mImi.equals(inputMethodInfo) && imeSubtypeListItem.mSubtypeId == iCalculateSubtypeId) {
                    return i;
                }
            }
            return -1;
        }

        public void onUserAction(InputMethodInfo inputMethodInfo, InputMethodSubtype inputMethodSubtype) {
            int usageRank = getUsageRank(inputMethodInfo, inputMethodSubtype);
            if (usageRank <= 0) {
                return;
            }
            int[] iArr = this.mUsageHistoryOfSubtypeListItemIndex;
            int i = iArr[usageRank];
            System.arraycopy(iArr, 0, iArr, 1, usageRank);
            this.mUsageHistoryOfSubtypeListItemIndex[0] = i;
        }

        public ImeSubtypeListItem getNextInputMethodLocked(boolean z, InputMethodInfo inputMethodInfo, InputMethodSubtype inputMethodSubtype, boolean z2) {
            int usageRank = getUsageRank(inputMethodInfo, inputMethodSubtype);
            if (usageRank < 0) {
                return null;
            }
            int length = this.mUsageHistoryOfSubtypeListItemIndex.length;
            for (int i = 1; i < length; i++) {
                ImeSubtypeListItem imeSubtypeListItem = this.mImeSubtypeList.get(this.mUsageHistoryOfSubtypeListItemIndex[((z2 ? i : length - i) + usageRank) % length]);
                if (!z || inputMethodInfo.equals(imeSubtypeListItem.mImi)) {
                    return imeSubtypeListItem;
                }
            }
            return null;
        }

        protected void dump(Printer printer, String str) {
            int i = 0;
            while (true) {
                int[] iArr = this.mUsageHistoryOfSubtypeListItemIndex;
                if (i >= iArr.length) {
                    return;
                }
                printer.println(str + "rank=" + iArr[i] + " item=" + this.mImeSubtypeList.get(i));
                i++;
            }
        }
    }

    public static class ControllerImpl {
        private final DynamicRotationList mSwitchingAwareRotationList;
        private final StaticRotationList mSwitchingUnawareRotationList;

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r1v5 */
        /* JADX WARN: Type inference failed for: r1v6 */
        /* JADX WARN: Type inference failed for: r1v7 */
        public static ControllerImpl createFrom(ControllerImpl controllerImpl, List<ImeSubtypeListItem> list) {
            StaticRotationList staticRotationList;
            StaticRotationList staticRotationList2;
            DynamicRotationList dynamicRotationList;
            List<ImeSubtypeListItem> listFilterImeSubtypeList = filterImeSubtypeList(list, true);
            StaticRotationList staticRotationList3 = 0;
            staticRotationList3 = 0;
            staticRotationList3 = 0;
            DynamicRotationList dynamicRotationList2 = (controllerImpl == null || (dynamicRotationList = controllerImpl.mSwitchingAwareRotationList) == null || !Objects.equals(dynamicRotationList.mImeSubtypeList, listFilterImeSubtypeList)) ? null : controllerImpl.mSwitchingAwareRotationList;
            if (dynamicRotationList2 == null) {
                dynamicRotationList2 = new DynamicRotationList(listFilterImeSubtypeList);
            }
            List<ImeSubtypeListItem> listFilterImeSubtypeList2 = filterImeSubtypeList(list, false);
            if (controllerImpl != null && (staticRotationList = controllerImpl.mSwitchingUnawareRotationList) != null && Objects.equals(staticRotationList.mImeSubtypeList, listFilterImeSubtypeList2)) {
                staticRotationList2 = controllerImpl.mSwitchingUnawareRotationList;
            }
            if (staticRotationList3 == 0) {
                staticRotationList3 = staticRotationList2;
                staticRotationList3 = new StaticRotationList(listFilterImeSubtypeList2);
            }
            staticRotationList3 = staticRotationList2;
            return new ControllerImpl(dynamicRotationList2, staticRotationList3);
        }

        private ControllerImpl(DynamicRotationList dynamicRotationList, StaticRotationList staticRotationList) {
            this.mSwitchingAwareRotationList = dynamicRotationList;
            this.mSwitchingUnawareRotationList = staticRotationList;
        }

        public ImeSubtypeListItem getNextInputMethod(boolean z, InputMethodInfo inputMethodInfo, InputMethodSubtype inputMethodSubtype, boolean z2) {
            if (inputMethodInfo == null) {
                return null;
            }
            if (inputMethodInfo.supportsSwitchingToNextInputMethod()) {
                return this.mSwitchingAwareRotationList.getNextInputMethodLocked(z, inputMethodInfo, inputMethodSubtype, z2);
            }
            return this.mSwitchingUnawareRotationList.getNextInputMethodLocked(z, inputMethodInfo, inputMethodSubtype, z2);
        }

        public void onUserActionLocked(InputMethodInfo inputMethodInfo, InputMethodSubtype inputMethodSubtype) {
            if (inputMethodInfo != null && inputMethodInfo.supportsSwitchingToNextInputMethod()) {
                this.mSwitchingAwareRotationList.onUserAction(inputMethodInfo, inputMethodSubtype);
            }
        }

        private static List<ImeSubtypeListItem> filterImeSubtypeList(List<ImeSubtypeListItem> list, boolean z) {
            ArrayList arrayList = new ArrayList();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                ImeSubtypeListItem imeSubtypeListItem = list.get(i);
                if (imeSubtypeListItem.mImi.supportsSwitchingToNextInputMethod() == z) {
                    arrayList.add(imeSubtypeListItem);
                }
            }
            return arrayList;
        }

        protected void dump(Printer printer) {
            printer.println("    mSwitchingAwareRotationList:");
            this.mSwitchingAwareRotationList.dump(printer, "      ");
            printer.println("    mSwitchingUnawareRotationList:");
            this.mSwitchingUnawareRotationList.dump(printer, "      ");
        }
    }

    private InputMethodSubtypeSwitchingController(InputMethodUtils.InputMethodSettings inputMethodSettings, Context context) {
        this.mSettings = inputMethodSettings;
        resetCircularListLocked(context);
    }

    public static InputMethodSubtypeSwitchingController createInstanceLocked(InputMethodUtils.InputMethodSettings inputMethodSettings, Context context) {
        return new InputMethodSubtypeSwitchingController(inputMethodSettings, context);
    }

    public void onUserActionLocked(InputMethodInfo inputMethodInfo, InputMethodSubtype inputMethodSubtype) {
        ControllerImpl controllerImpl = this.mController;
        if (controllerImpl == null) {
            return;
        }
        controllerImpl.onUserActionLocked(inputMethodInfo, inputMethodSubtype);
    }

    public void resetCircularListLocked(Context context) {
        InputMethodAndSubtypeList inputMethodAndSubtypeList = new InputMethodAndSubtypeList(context, this.mSettings);
        this.mSubtypeList = inputMethodAndSubtypeList;
        this.mController = ControllerImpl.createFrom(this.mController, inputMethodAndSubtypeList.getSortedInputMethodAndSubtypeList(false, false));
    }

    public ImeSubtypeListItem getNextInputMethodLocked(boolean z, InputMethodInfo inputMethodInfo, InputMethodSubtype inputMethodSubtype, boolean z2) {
        ControllerImpl controllerImpl = this.mController;
        if (controllerImpl == null) {
            return null;
        }
        return controllerImpl.getNextInputMethod(z, inputMethodInfo, inputMethodSubtype, z2);
    }

    public List<ImeSubtypeListItem> getSortedInputMethodAndSubtypeListLocked(boolean z, boolean z2) {
        return this.mSubtypeList.getSortedInputMethodAndSubtypeList(z, z2);
    }

    public void dump(Printer printer) {
        ControllerImpl controllerImpl = this.mController;
        if (controllerImpl != null) {
            controllerImpl.dump(printer);
        } else {
            printer.println("    mController=null");
        }
    }
}
