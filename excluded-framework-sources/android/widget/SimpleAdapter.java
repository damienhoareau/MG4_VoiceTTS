package android.widget;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiEnterpriseConfig;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class SimpleAdapter extends BaseAdapter implements Filterable, ThemedSpinnerAdapter {
    private List<? extends Map<String, ?>> mData;
    private LayoutInflater mDropDownInflater;
    private int mDropDownResource;
    private SimpleFilter mFilter;
    private String[] mFrom;
    private final LayoutInflater mInflater;
    private int mResource;
    private int[] mTo;
    private ArrayList<Map<String, ?>> mUnfilteredData;
    private ViewBinder mViewBinder;

    public interface ViewBinder {
        boolean setViewValue(View view, Object obj, String str);
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    public SimpleAdapter(Context context, List<? extends Map<String, ?>> list, int i, String[] strArr, int[] iArr) {
        this.mData = list;
        this.mDropDownResource = i;
        this.mResource = i;
        this.mFrom = strArr;
        this.mTo = iArr;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mData.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return this.mData.get(i);
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        return createViewFromResource(this.mInflater, i, view, viewGroup, this.mResource);
    }

    private View createViewFromResource(LayoutInflater layoutInflater, int i, View view, ViewGroup viewGroup, int i2) {
        if (view == null) {
            view = layoutInflater.inflate(i2, viewGroup, false);
        }
        bindView(i, view);
        return view;
    }

    public void setDropDownViewResource(int i) {
        this.mDropDownResource = i;
    }

    @Override // android.widget.ThemedSpinnerAdapter
    public void setDropDownViewTheme(Resources.Theme theme) {
        if (theme == null) {
            this.mDropDownInflater = null;
        } else if (theme == this.mInflater.getContext().getTheme()) {
            this.mDropDownInflater = this.mInflater;
        } else {
            this.mDropDownInflater = LayoutInflater.from(new ContextThemeWrapper(this.mInflater.getContext(), theme));
        }
    }

    @Override // android.widget.ThemedSpinnerAdapter
    public Resources.Theme getDropDownViewTheme() {
        LayoutInflater layoutInflater = this.mDropDownInflater;
        if (layoutInflater == null) {
            return null;
        }
        return layoutInflater.getContext().getTheme();
    }

    @Override // android.widget.BaseAdapter, android.widget.SpinnerAdapter
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = this.mDropDownInflater;
        if (layoutInflater == null) {
            layoutInflater = this.mInflater;
        }
        return createViewFromResource(layoutInflater, i, view, viewGroup, this.mDropDownResource);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    private void bindView(int i, View view) {
        Map<String, ?> map = this.mData.get(i);
        if (map == null) {
            return;
        }
        ViewBinder viewBinder = this.mViewBinder;
        String[] strArr = this.mFrom;
        int[] iArr = this.mTo;
        int length = iArr.length;
        for (int i2 = 0; i2 < length; i2++) {
            View viewFindViewById = view.findViewById(iArr[i2]);
            if (viewFindViewById != 0) {
                Object obj = map.get(strArr[i2]);
                String string = obj == null ? "" : obj.toString();
                String str = string != null ? string : "";
                if (viewBinder != null ? viewBinder.setViewValue(viewFindViewById, obj, str) : false) {
                    continue;
                } else if (viewFindViewById instanceof Checkable) {
                    if (obj instanceof Boolean) {
                        ((Checkable) viewFindViewById).setChecked(((Boolean) obj).booleanValue());
                    } else if (viewFindViewById instanceof TextView) {
                        setViewText((TextView) viewFindViewById, str);
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append(viewFindViewById.getClass().getName());
                        sb.append(" should be bound to a Boolean, not a ");
                        sb.append(obj == null ? "<unknown type>" : obj.getClass());
                        throw new IllegalStateException(sb.toString());
                    }
                } else if (viewFindViewById instanceof TextView) {
                    setViewText((TextView) viewFindViewById, str);
                } else if (viewFindViewById instanceof ImageView) {
                    if (obj instanceof Integer) {
                        setViewImage((ImageView) viewFindViewById, ((Integer) obj).intValue());
                    } else {
                        setViewImage((ImageView) viewFindViewById, str);
                    }
                } else {
                    throw new IllegalStateException(viewFindViewById.getClass().getName() + " is not a  view that can be bounds by this SimpleAdapter");
                }
            }
        }
    }

    public ViewBinder getViewBinder() {
        return this.mViewBinder;
    }

    public void setViewBinder(ViewBinder viewBinder) {
        this.mViewBinder = viewBinder;
    }

    public void setViewImage(ImageView imageView, int i) {
        imageView.setImageResource(i);
    }

    public void setViewImage(ImageView imageView, String str) {
        try {
            imageView.setImageResource(Integer.parseInt(str));
        } catch (NumberFormatException unused) {
            imageView.setImageURI(Uri.parse(str));
        }
    }

    public void setViewText(TextView textView, String str) {
        textView.setText(str);
    }

    @Override // android.widget.Filterable
    public Filter getFilter() {
        if (this.mFilter == null) {
            this.mFilter = new SimpleFilter();
        }
        return this.mFilter;
    }

    private class SimpleFilter extends Filter {
        private SimpleFilter() {
        }

        @Override // android.widget.Filter
        protected Filter.FilterResults performFiltering(CharSequence charSequence) {
            Filter.FilterResults filterResults = new Filter.FilterResults();
            if (SimpleAdapter.this.mUnfilteredData == null) {
                SimpleAdapter.this.mUnfilteredData = new ArrayList(SimpleAdapter.this.mData);
            }
            if (charSequence == null || charSequence.length() == 0) {
                ArrayList arrayList = SimpleAdapter.this.mUnfilteredData;
                filterResults.values = arrayList;
                filterResults.count = arrayList.size();
            } else {
                String lowerCase = charSequence.toString().toLowerCase();
                ArrayList arrayList2 = SimpleAdapter.this.mUnfilteredData;
                int size = arrayList2.size();
                ArrayList arrayList3 = new ArrayList(size);
                for (int i = 0; i < size; i++) {
                    Map map = (Map) arrayList2.get(i);
                    if (map != null) {
                        int length = SimpleAdapter.this.mTo.length;
                        for (int i2 = 0; i2 < length; i2++) {
                            for (String str : ((String) map.get(SimpleAdapter.this.mFrom[i2])).split(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER)) {
                                if (str.toLowerCase().startsWith(lowerCase)) {
                                    arrayList3.add(map);
                                    break;
                                }
                            }
                        }
                    }
                }
                filterResults.values = arrayList3;
                filterResults.count = arrayList3.size();
            }
            return filterResults;
        }

        @Override // android.widget.Filter
        protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
            SimpleAdapter.this.mData = (List) filterResults.values;
            if (filterResults.count > 0) {
                SimpleAdapter.this.notifyDataSetChanged();
            } else {
                SimpleAdapter.this.notifyDataSetInvalidated();
            }
        }
    }
}
