package com.alibaba.fastjson;

import android.app.slice.Slice;
import android.provider.SettingsStringUtil;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.JSONLexerBase;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.FieldDeserializer;
import com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.FieldSerializer;
import com.alibaba.fastjson.serializer.JavaBeanSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.util.IOUtils;
import com.alibaba.fastjson.util.TypeUtils;
import com.android.internal.app.DumpHeapActivity;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.telephony.PhoneConstants;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes3.dex */
public class JSONPath implements JSONAware {
    static final long LENGTH = -1580386065683472715L;
    static final long SIZE = 5614464919154503228L;
    private static ConcurrentMap<String, JSONPath> pathCache = new ConcurrentHashMap(128, 0.75f, 1);
    private boolean hasRefSegment;
    private boolean ignoreNullValue;
    private ParserConfig parserConfig;
    private final String path;
    private Segment[] segments;
    private SerializeConfig serializeConfig;

    interface Filter {
        boolean apply(JSONPath jSONPath, Object obj, Object obj2, Object obj3);
    }

    enum Operator {
        EQ,
        NE,
        GT,
        GE,
        LT,
        LE,
        LIKE,
        NOT_LIKE,
        RLIKE,
        NOT_RLIKE,
        IN,
        NOT_IN,
        BETWEEN,
        NOT_BETWEEN,
        And,
        Or,
        REG_MATCH
    }

    interface Segment {
        Object eval(JSONPath jSONPath, Object obj, Object obj2);

        void extract(JSONPath jSONPath, DefaultJSONParser defaultJSONParser, Context context);
    }

    public JSONPath(String str) {
        this(str, SerializeConfig.getGlobalInstance(), ParserConfig.getGlobalInstance(), true);
    }

    public JSONPath(String str, boolean z) {
        this(str, SerializeConfig.getGlobalInstance(), ParserConfig.getGlobalInstance(), z);
    }

    public JSONPath(String str, SerializeConfig serializeConfig, ParserConfig parserConfig, boolean z) {
        if (str == null || str.length() == 0) {
            throw new JSONPathException("json-path can not be null or empty");
        }
        this.path = str;
        this.serializeConfig = serializeConfig;
        this.parserConfig = parserConfig;
        this.ignoreNullValue = z;
    }

    protected void init() {
        if (this.segments != null) {
            return;
        }
        if (PhoneConstants.APN_TYPE_ALL.equals(this.path)) {
            this.segments = new Segment[]{WildCardSegment.instance};
            return;
        }
        JSONPathParser jSONPathParser = new JSONPathParser(this.path);
        this.segments = jSONPathParser.explain();
        this.hasRefSegment = jSONPathParser.hasRefSegment;
    }

    public boolean isRef() {
        try {
            init();
            for (int i = 0; i < this.segments.length; i++) {
                Class<?> cls = this.segments[i].getClass();
                if (cls != ArrayAccessSegment.class && cls != PropertySegment.class) {
                    return false;
                }
            }
            return true;
        } catch (JSONPathException unused) {
            return false;
        }
    }

    public Object eval(Object obj) {
        if (obj == null) {
            return null;
        }
        init();
        int i = 0;
        Object objEval = obj;
        while (true) {
            Segment[] segmentArr = this.segments;
            if (i >= segmentArr.length) {
                return objEval;
            }
            objEval = segmentArr[i].eval(this, obj, objEval);
            i++;
        }
    }

    public <T> T eval(Object obj, Type type, ParserConfig parserConfig) {
        return (T) TypeUtils.cast(eval(obj), type, parserConfig);
    }

    public <T> T eval(Object obj, Type type) {
        return (T) eval(obj, type, ParserConfig.getGlobalInstance());
    }

    /* JADX WARN: Code duplicated, block: B:64:0x009e  */
    public Object extract(DefaultJSONParser defaultJSONParser) {
        boolean z;
        if (defaultJSONParser == null) {
            return null;
        }
        init();
        if (this.hasRefSegment) {
            return eval(defaultJSONParser.parse());
        }
        Segment[] segmentArr = this.segments;
        if (segmentArr.length == 0) {
            return defaultJSONParser.parse();
        }
        Segment segment = segmentArr[segmentArr.length - 1];
        if ((segment instanceof TypeSegment) || (segment instanceof FloorSegment) || (segment instanceof MultiIndexSegment)) {
            return eval(defaultJSONParser.parse());
        }
        Context context = null;
        int i = 0;
        while (true) {
            Segment[] segmentArr2 = this.segments;
            if (i < segmentArr2.length) {
                Segment segment2 = segmentArr2[i];
                boolean z2 = i == segmentArr2.length - 1;
                if (context != null && context.object != null) {
                    context.object = segment2.eval(this, null, context.object);
                } else {
                    if (z2) {
                        z = true;
                    } else {
                        Segment segment3 = this.segments[i + 1];
                        if (((segment2 instanceof PropertySegment) && ((PropertySegment) segment2).deep && ((segment3 instanceof ArrayAccessSegment) || (segment3 instanceof MultiIndexSegment) || (segment3 instanceof MultiPropertySegment) || (segment3 instanceof SizeSegment) || (segment3 instanceof PropertySegment) || (segment3 instanceof FilterSegment))) || (((segment3 instanceof ArrayAccessSegment) && ((ArrayAccessSegment) segment3).index < 0) || (segment3 instanceof FilterSegment) || (segment2 instanceof WildCardSegment) || (segment2 instanceof MultiIndexSegment))) {
                            z = true;
                        } else {
                            z = false;
                        }
                    }
                    Context context2 = new Context(context, z);
                    segment2.extract(this, defaultJSONParser, context2);
                    context = context2;
                }
                i++;
            } else {
                return context.object;
            }
        }
    }

    private static class Context {
        final boolean eval;
        Object object;
        final Context parent;

        public Context(Context context, boolean z) {
            this.parent = context;
            this.eval = z;
        }
    }

    public boolean contains(Object obj) {
        if (obj == null) {
            return false;
        }
        init();
        Object obj2 = obj;
        int i = 0;
        while (true) {
            Segment[] segmentArr = this.segments;
            if (i >= segmentArr.length) {
                return true;
            }
            Object objEval = segmentArr[i].eval(this, obj, obj2);
            if (objEval == null) {
                return false;
            }
            if (objEval == Collections.EMPTY_LIST && (obj2 instanceof List)) {
                return ((List) obj2).contains(objEval);
            }
            i++;
            obj2 = objEval;
        }
    }

    public boolean containsValue(Object obj, Object obj2) {
        Object objEval = eval(obj);
        if (objEval == obj2) {
            return true;
        }
        if (objEval == null) {
            return false;
        }
        if (objEval instanceof Iterable) {
            Iterator it = ((Iterable) objEval).iterator();
            while (it.hasNext()) {
                if (eq(it.next(), obj2)) {
                    return true;
                }
            }
            return false;
        }
        return eq(objEval, obj2);
    }

    public int size(Object obj) {
        if (obj == null) {
            return -1;
        }
        init();
        int i = 0;
        Object objEval = obj;
        while (true) {
            Segment[] segmentArr = this.segments;
            if (i < segmentArr.length) {
                objEval = segmentArr[i].eval(this, obj, objEval);
                i++;
            } else {
                return evalSize(objEval);
            }
        }
    }

    public Set<?> keySet(Object obj) {
        if (obj == null) {
            return null;
        }
        init();
        int i = 0;
        Object objEval = obj;
        while (true) {
            Segment[] segmentArr = this.segments;
            if (i < segmentArr.length) {
                objEval = segmentArr[i].eval(this, obj, objEval);
                i++;
            } else {
                return evalKeySet(objEval);
            }
        }
    }

    public void patchAdd(Object obj, Object obj2, boolean z) {
        if (obj == null) {
            return;
        }
        init();
        Object obj3 = null;
        int i = 0;
        Object obj4 = obj;
        while (true) {
            Segment[] segmentArr = this.segments;
            if (i >= segmentArr.length) {
                break;
            }
            Segment segment = segmentArr[i];
            Object objEval = segment.eval(this, obj, obj4);
            if (objEval == null && i != this.segments.length - 1 && (segment instanceof PropertySegment)) {
                objEval = new JSONObject();
                ((PropertySegment) segment).setValue(this, obj4, objEval);
            }
            i++;
            obj3 = obj4;
            obj4 = objEval;
        }
        if (!z && (obj4 instanceof Collection)) {
            ((Collection) obj4).add(obj2);
            return;
        }
        if (obj4 != null && !z) {
            Class<?> cls = obj4.getClass();
            if (cls.isArray()) {
                int length = Array.getLength(obj4);
                Object objNewInstance = Array.newInstance(cls.getComponentType(), length + 1);
                System.arraycopy(obj4, 0, objNewInstance, 0, length);
                Array.set(objNewInstance, length, obj2);
                obj2 = objNewInstance;
            } else if (!Map.class.isAssignableFrom(cls)) {
                throw new JSONException("unsupported array put operation. " + cls);
            }
        }
        Segment[] segmentArr2 = this.segments;
        Segment segment2 = segmentArr2[segmentArr2.length - 1];
        if (segment2 instanceof PropertySegment) {
            ((PropertySegment) segment2).setValue(this, obj3, obj2);
        } else {
            if (segment2 instanceof ArrayAccessSegment) {
                ((ArrayAccessSegment) segment2).setValue(this, obj3, obj2);
                return;
            }
            throw new UnsupportedOperationException();
        }
    }

    public void arrayAdd(Object obj, Object... objArr) {
        if (objArr == null || objArr.length == 0 || obj == null) {
            return;
        }
        init();
        Object obj2 = null;
        int i = 0;
        Object objEval = obj;
        int i2 = 0;
        while (true) {
            Segment[] segmentArr = this.segments;
            if (i2 >= segmentArr.length) {
                break;
            }
            if (i2 == segmentArr.length - 1) {
                obj2 = objEval;
            }
            objEval = this.segments[i2].eval(this, obj, objEval);
            i2++;
        }
        if (objEval == null) {
            throw new JSONPathException("value not found in path " + this.path);
        }
        if (objEval instanceof Collection) {
            Collection collection = (Collection) objEval;
            int length = objArr.length;
            while (i < length) {
                collection.add(objArr[i]);
                i++;
            }
            return;
        }
        Class<?> cls = objEval.getClass();
        if (cls.isArray()) {
            int length2 = Array.getLength(objEval);
            Object objNewInstance = Array.newInstance(cls.getComponentType(), objArr.length + length2);
            System.arraycopy(objEval, 0, objNewInstance, 0, length2);
            while (i < objArr.length) {
                Array.set(objNewInstance, length2 + i, objArr[i]);
                i++;
            }
            Segment[] segmentArr2 = this.segments;
            Segment segment = segmentArr2[segmentArr2.length - 1];
            if (segment instanceof PropertySegment) {
                ((PropertySegment) segment).setValue(this, obj2, objNewInstance);
                return;
            } else {
                if (segment instanceof ArrayAccessSegment) {
                    ((ArrayAccessSegment) segment).setValue(this, obj2, objNewInstance);
                    return;
                }
                throw new UnsupportedOperationException();
            }
        }
        throw new JSONException("unsupported array put operation. " + cls);
    }

    public boolean remove(Object obj) {
        boolean z = false;
        if (obj == null) {
            return false;
        }
        init();
        Object obj2 = null;
        Segment[] segmentArr = this.segments;
        Segment segment = segmentArr[segmentArr.length - 1];
        Object objEval = obj;
        int i = 0;
        while (true) {
            Segment[] segmentArr2 = this.segments;
            if (i >= segmentArr2.length) {
                break;
            }
            if (i == segmentArr2.length - 1) {
                obj2 = objEval;
                break;
            }
            Segment segment2 = segmentArr2[i];
            if (i == segmentArr2.length - 2 && (segment instanceof FilterSegment) && (segment2 instanceof PropertySegment)) {
                FilterSegment filterSegment = (FilterSegment) segment;
                if (objEval instanceof List) {
                    PropertySegment propertySegment = (PropertySegment) segment2;
                    Iterator it = ((List) objEval).iterator();
                    while (it.hasNext()) {
                        Object objEval2 = propertySegment.eval(this, obj, it.next());
                        if (objEval2 instanceof Iterable) {
                            filterSegment.remove(this, obj, objEval2);
                        } else if ((objEval2 instanceof Map) && filterSegment.filter.apply(this, obj, objEval, objEval2)) {
                            it.remove();
                        }
                    }
                    return true;
                }
                if (objEval instanceof Map) {
                    PropertySegment propertySegment2 = (PropertySegment) segment2;
                    Object objEval3 = propertySegment2.eval(this, obj, objEval);
                    if (objEval3 == null) {
                        return false;
                    }
                    if ((objEval3 instanceof Map) && filterSegment.filter.apply(this, obj, objEval, objEval3)) {
                        propertySegment2.remove(this, objEval);
                        return true;
                    }
                }
            }
            objEval = segment2.eval(this, obj, objEval);
            if (objEval == null) {
                break;
            }
            i++;
        }
        if (obj2 == null) {
            return false;
        }
        if (segment instanceof PropertySegment) {
            PropertySegment propertySegment3 = (PropertySegment) segment;
            if (obj2 instanceof Collection) {
                Segment[] segmentArr3 = this.segments;
                if (segmentArr3.length > 1) {
                    Segment segment3 = segmentArr3[segmentArr3.length - 2];
                    if ((segment3 instanceof RangeSegment) || (segment3 instanceof MultiIndexSegment)) {
                        Iterator it2 = ((Collection) obj2).iterator();
                        while (it2.hasNext()) {
                            if (propertySegment3.remove(this, it2.next())) {
                                z = true;
                            }
                        }
                        return z;
                    }
                }
            }
            return propertySegment3.remove(this, obj2);
        }
        if (segment instanceof ArrayAccessSegment) {
            return ((ArrayAccessSegment) segment).remove(this, obj2);
        }
        if (segment instanceof FilterSegment) {
            return ((FilterSegment) segment).remove(this, obj, obj2);
        }
        throw new UnsupportedOperationException();
    }

    public boolean set(Object obj, Object obj2) {
        return set(obj, obj2, true);
    }

    /* JADX WARN: Code duplicated, block: B:21:0x004b  */
    public boolean set(Object obj, Object obj2, boolean z) {
        Class<?> cls;
        JavaBeanDeserializer javaBeanDeserializer;
        if (obj == null) {
            return false;
        }
        init();
        Object obj3 = obj;
        int i = 0;
        Object obj4 = null;
        while (true) {
            Segment[] segmentArr = this.segments;
            if (i >= segmentArr.length) {
                obj3 = obj4;
                break;
            }
            Segment segment = segmentArr[i];
            Object objEval = segment.eval(this, obj, obj3);
            if (objEval == null) {
                Segment[] segmentArr2 = this.segments;
                Segment segment2 = i < segmentArr2.length - 1 ? segmentArr2[i + 1] : null;
                if (segment2 instanceof PropertySegment) {
                    if (segment instanceof PropertySegment) {
                        String str = ((PropertySegment) segment).propertyName;
                        JavaBeanDeserializer javaBeanDeserializer2 = getJavaBeanDeserializer(obj3.getClass());
                        if (javaBeanDeserializer2 != null) {
                            cls = javaBeanDeserializer2.getFieldDeserializer(str).fieldInfo.fieldClass;
                            javaBeanDeserializer = getJavaBeanDeserializer(cls);
                        } else {
                            cls = null;
                            javaBeanDeserializer = null;
                        }
                    } else {
                        cls = null;
                        javaBeanDeserializer = null;
                    }
                    if (javaBeanDeserializer != null) {
                        if (javaBeanDeserializer.beanInfo.defaultConstructor == null) {
                            return false;
                        }
                        objEval = javaBeanDeserializer.createInstance((DefaultJSONParser) null, cls);
                    } else {
                        objEval = new JSONObject();
                    }
                } else {
                    objEval = segment2 instanceof ArrayAccessSegment ? new JSONArray() : null;
                }
                if (objEval != null) {
                    if (segment instanceof PropertySegment) {
                        ((PropertySegment) segment).setValue(this, obj3, objEval);
                    } else {
                        if (!(segment instanceof ArrayAccessSegment)) {
                            break;
                        }
                        ((ArrayAccessSegment) segment).setValue(this, obj3, objEval);
                    }
                } else {
                    break;
                }
            }
            i++;
            obj4 = obj3;
            obj3 = objEval;
        }
        if (obj3 == null) {
            return false;
        }
        Segment[] segmentArr3 = this.segments;
        Segment segment3 = segmentArr3[segmentArr3.length - 1];
        if (segment3 instanceof PropertySegment) {
            ((PropertySegment) segment3).setValue(this, obj3, obj2);
            return true;
        }
        if (segment3 instanceof ArrayAccessSegment) {
            return ((ArrayAccessSegment) segment3).setValue(this, obj3, obj2);
        }
        throw new UnsupportedOperationException();
    }

    public static Object eval(Object obj, String str) {
        return compile(str).eval(obj);
    }

    public static Object eval(Object obj, String str, boolean z) {
        return compile(str, z).eval(obj);
    }

    public static int size(Object obj, String str) {
        JSONPath jSONPathCompile = compile(str);
        return jSONPathCompile.evalSize(jSONPathCompile.eval(obj));
    }

    public static Set<?> keySet(Object obj, String str) {
        JSONPath jSONPathCompile = compile(str);
        return jSONPathCompile.evalKeySet(jSONPathCompile.eval(obj));
    }

    public static boolean contains(Object obj, String str) {
        if (obj == null) {
            return false;
        }
        return compile(str).contains(obj);
    }

    public static boolean containsValue(Object obj, String str, Object obj2) {
        return compile(str).containsValue(obj, obj2);
    }

    public static void arrayAdd(Object obj, String str, Object... objArr) {
        compile(str).arrayAdd(obj, objArr);
    }

    public static boolean set(Object obj, String str, Object obj2) {
        return compile(str).set(obj, obj2);
    }

    public static boolean remove(Object obj, String str) {
        return compile(str).remove(obj);
    }

    public static JSONPath compile(String str) {
        if (str == null) {
            throw new JSONPathException("jsonpath can not be null");
        }
        JSONPath jSONPath = pathCache.get(str);
        if (jSONPath != null) {
            return jSONPath;
        }
        JSONPath jSONPath2 = new JSONPath(str);
        if (pathCache.size() >= 1024) {
            return jSONPath2;
        }
        pathCache.putIfAbsent(str, jSONPath2);
        return pathCache.get(str);
    }

    public static JSONPath compile(String str, boolean z) {
        if (str == null) {
            throw new JSONPathException("jsonpath can not be null");
        }
        JSONPath jSONPath = pathCache.get(str);
        if (jSONPath != null) {
            return jSONPath;
        }
        JSONPath jSONPath2 = new JSONPath(str, z);
        if (pathCache.size() >= 1024) {
            return jSONPath2;
        }
        pathCache.putIfAbsent(str, jSONPath2);
        return pathCache.get(str);
    }

    public static Object read(String str, String str2) {
        return compile(str2).eval(JSON.parse(str));
    }

    public static <T> T read(String str, String str2, Type type, ParserConfig parserConfig) {
        return (T) compile(str2).eval(JSON.parse(str), type, parserConfig);
    }

    public static <T> T read(String str, String str2, Type type) {
        return (T) read(str, str2, type, null);
    }

    public static Object extract(String str, String str2, ParserConfig parserConfig, int i, Feature... featureArr) {
        DefaultJSONParser defaultJSONParser = new DefaultJSONParser(str, parserConfig, i | Feature.OrderedField.mask);
        Object objExtract = compile(str2).extract(defaultJSONParser);
        defaultJSONParser.lexer.close();
        return objExtract;
    }

    public static Object extract(String str, String str2) {
        return extract(str, str2, ParserConfig.global, JSON.DEFAULT_PARSER_FEATURE, new Feature[0]);
    }

    public static Map<String, Object> paths(Object obj) {
        return paths(obj, SerializeConfig.globalInstance);
    }

    public static Map<String, Object> paths(Object obj, SerializeConfig serializeConfig) {
        IdentityHashMap identityHashMap = new IdentityHashMap();
        HashMap map = new HashMap();
        paths(identityHashMap, map, "/", obj, serializeConfig);
        return map;
    }

    private static void paths(Map<Object, String> map, Map<String, Object> map2, String str, Object obj, SerializeConfig serializeConfig) {
        StringBuilder sb;
        StringBuilder sb2;
        StringBuilder sb3;
        StringBuilder sb4;
        if (obj == null) {
            return;
        }
        int i = 0;
        if (map.put(obj, str) != null) {
            Class<?> cls = obj.getClass();
            if (!(cls == String.class || cls == Boolean.class || cls == Character.class || cls == UUID.class || cls.isEnum() || (obj instanceof Number) || (obj instanceof Date))) {
                return;
            }
        }
        map2.put(str, obj);
        if (obj instanceof Map) {
            for (Map.Entry entry : ((Map) obj).entrySet()) {
                Object key = entry.getKey();
                if (key instanceof String) {
                    if (str.equals("/")) {
                        sb4 = new StringBuilder();
                    } else {
                        sb4 = new StringBuilder();
                        sb4.append(str);
                    }
                    sb4.append("/");
                    sb4.append(key);
                    paths(map, map2, sb4.toString(), entry.getValue(), serializeConfig);
                }
            }
            return;
        }
        if (obj instanceof Collection) {
            for (Object obj2 : (Collection) obj) {
                if (str.equals("/")) {
                    sb3 = new StringBuilder();
                } else {
                    sb3 = new StringBuilder();
                    sb3.append(str);
                }
                sb3.append("/");
                sb3.append(i);
                paths(map, map2, sb3.toString(), obj2, serializeConfig);
                i++;
            }
            return;
        }
        Class<?> cls2 = obj.getClass();
        if (cls2.isArray()) {
            int length = Array.getLength(obj);
            while (i < length) {
                Object obj3 = Array.get(obj, i);
                if (str.equals("/")) {
                    sb2 = new StringBuilder();
                } else {
                    sb2 = new StringBuilder();
                    sb2.append(str);
                }
                sb2.append("/");
                sb2.append(i);
                paths(map, map2, sb2.toString(), obj3, serializeConfig);
                i++;
            }
            return;
        }
        if (ParserConfig.isPrimitive2(cls2) || cls2.isEnum()) {
            return;
        }
        ObjectSerializer objectWriter = serializeConfig.getObjectWriter(cls2);
        if (objectWriter instanceof JavaBeanSerializer) {
            try {
                for (Map.Entry<String, Object> entry2 : ((JavaBeanSerializer) objectWriter).getFieldValuesMap(obj).entrySet()) {
                    String key2 = entry2.getKey();
                    if (key2 instanceof String) {
                        if (str.equals("/")) {
                            sb = new StringBuilder();
                            sb.append("/");
                            sb.append(key2);
                        } else {
                            sb = new StringBuilder();
                            sb.append(str);
                            sb.append("/");
                            sb.append(key2);
                        }
                        paths(map, map2, sb.toString(), entry2.getValue(), serializeConfig);
                    }
                }
            } catch (Exception e) {
                throw new JSONException("toJSON error", e);
            }
        }
    }

    public String getPath() {
        return this.path;
    }

    static class JSONPathParser {
        private char ch;
        private boolean hasRefSegment;
        private int level;
        private final String path;
        private int pos;
        private static final String strArrayRegex = "'\\s*,\\s*'";
        private static final Pattern strArrayPatternx = Pattern.compile(strArrayRegex);

        static boolean isDigitFirst(char c) {
            return c == '-' || c == '+' || (c >= '0' && c <= '9');
        }

        public JSONPathParser(String str) {
            this.path = str;
            next();
        }

        void next() {
            String str = this.path;
            int i = this.pos;
            this.pos = i + 1;
            this.ch = str.charAt(i);
        }

        char getNextChar() {
            return this.path.charAt(this.pos);
        }

        boolean isEOF() {
            return this.pos >= this.path.length();
        }

        Segment readSegement() {
            boolean z;
            char c;
            if (this.level == 0 && this.path.length() == 1) {
                if (isDigitFirst(this.ch)) {
                    return new ArrayAccessSegment(this.ch - '0');
                }
                char c2 = this.ch;
                if ((c2 >= 'a' && c2 <= 'z') || ((c = this.ch) >= 'A' && c <= 'Z')) {
                    return new PropertySegment(Character.toString(this.ch), false);
                }
            }
            while (!isEOF()) {
                skipWhitespace();
                char c3 = this.ch;
                if (c3 != '$') {
                    if (c3 != '.' && c3 != '/') {
                        if (c3 == '[') {
                            return parseArrayAccess(true);
                        }
                        if (this.level == 0) {
                            return new PropertySegment(readName(), false);
                        }
                        if (c3 == '?') {
                            return new FilterSegment((Filter) parseArrayAccessFilter(false));
                        }
                        throw new JSONPathException("not support jsonpath : " + this.path);
                    }
                    char c4 = this.ch;
                    next();
                    if (c4 == '.' && this.ch == '.') {
                        next();
                        int length = this.path.length();
                        int i = this.pos;
                        if (length > i + 3 && this.ch == '[' && this.path.charAt(i) == '*' && this.path.charAt(this.pos + 1) == ']' && this.path.charAt(this.pos + 2) == '.') {
                            next();
                            next();
                            next();
                            next();
                        }
                        z = true;
                    } else {
                        z = false;
                    }
                    char c5 = this.ch;
                    if (c5 == '*' || (z && c5 == '[')) {
                        boolean z2 = this.ch == '[';
                        if (!isEOF()) {
                            next();
                        }
                        if (!z) {
                            return WildCardSegment.instance;
                        }
                        if (z2) {
                            return WildCardSegment.instance_deep_objectOnly;
                        }
                        return WildCardSegment.instance_deep;
                    }
                    if (isDigitFirst(this.ch)) {
                        return parseArrayAccess(false);
                    }
                    String name = readName();
                    if (this.ch == '(') {
                        next();
                        if (this.ch == ')') {
                            if (!isEOF()) {
                                next();
                            }
                            if (DumpHeapActivity.KEY_SIZE.equals(name) || "length".equals(name)) {
                                return SizeSegment.instance;
                            }
                            if (Slice.SUBTYPE_MAX.equals(name)) {
                                return MaxSegment.instance;
                            }
                            if ("min".equals(name)) {
                                return MinSegment.instance;
                            }
                            if ("keySet".equals(name)) {
                                return KeySetSegment.instance;
                            }
                            if ("type".equals(name)) {
                                return TypeSegment.instance;
                            }
                            if ("floor".equals(name)) {
                                return FloorSegment.instance;
                            }
                            throw new JSONPathException("not support jsonpath : " + this.path);
                        }
                        throw new JSONPathException("not support jsonpath : " + this.path);
                    }
                    return new PropertySegment(name, z);
                }
                next();
                skipWhitespace();
                if (this.ch == '?') {
                    return new FilterSegment((Filter) parseArrayAccessFilter(false));
                }
            }
            return null;
        }

        public final void skipWhitespace() {
            while (true) {
                char c = this.ch;
                if (c > ' ') {
                    return;
                }
                if (c != ' ' && c != '\r' && c != '\n' && c != '\t' && c != '\f' && c != '\b') {
                    return;
                } else {
                    next();
                }
            }
        }

        Segment parseArrayAccess(boolean z) {
            Object arrayAccessFilter = parseArrayAccessFilter(z);
            if (arrayAccessFilter instanceof Segment) {
                return (Segment) arrayAccessFilter;
            }
            return new FilterSegment((Filter) arrayAccessFilter);
        }

        Object parseArrayAccessFilter(boolean z) {
            int i;
            boolean z2;
            boolean z3;
            char c;
            char c2;
            char c3;
            char c4;
            char c5;
            Filter nullSegement;
            char c6;
            String str;
            String[] strArr;
            String str2;
            String str3;
            Filter matchSegement;
            Operator operator;
            Filter stringOpSegement;
            char c7;
            Filter valueSegment;
            char c8;
            Filter valueSegment2;
            char c9;
            Filter notNullSegement;
            char c10;
            int i2;
            Filter doubleOpSegement;
            char c11;
            char c12;
            char c13;
            char c14;
            char c15;
            int i3;
            String strReplaceAll;
            int i4;
            char cCharAt;
            if (z) {
                accept('[');
            }
            if (this.ch == '?') {
                next();
                accept('(');
                i = 1;
                while (this.ch == '(') {
                    next();
                    i++;
                }
                z2 = true;
            } else {
                i = 0;
                z2 = false;
            }
            skipWhitespace();
            if (z2 || IOUtils.firstIdentifier(this.ch) || Character.isJavaIdentifierStart(this.ch) || (c14 = this.ch) == '\\' || c14 == '@') {
                if (this.ch == '@') {
                    next();
                    accept('.');
                }
                String name = readName();
                skipWhitespace();
                if (z2 && this.ch == ')') {
                    next();
                    Filter notNullSegement2 = new NotNullSegement(name, false);
                    while (true) {
                        c13 = this.ch;
                        if (c13 != ' ') {
                            break;
                        }
                        next();
                    }
                    if (c13 == '&' || c13 == '|') {
                        notNullSegement2 = filterRest(notNullSegement2);
                    }
                    if (z) {
                        accept(']');
                    }
                    return notNullSegement2;
                }
                if (z && this.ch == ']') {
                    if (isEOF() && name.equals("last")) {
                        return new MultiIndexSegment(new int[]{-1});
                    }
                    next();
                    Filter notNullSegement3 = new NotNullSegement(name, false);
                    while (true) {
                        c12 = this.ch;
                        if (c12 != ' ') {
                            break;
                        }
                        next();
                    }
                    if (c12 == '&' || c12 == '|') {
                        notNullSegement3 = filterRest(notNullSegement3);
                    }
                    accept(')');
                    if (z2) {
                        accept(')');
                    }
                    if (z) {
                        accept(']');
                    }
                    return notNullSegement3;
                }
                skipWhitespace();
                if (this.ch == '(') {
                    next();
                    accept(')');
                    skipWhitespace();
                    z3 = true;
                } else {
                    z3 = false;
                }
                Operator op = readOp();
                skipWhitespace();
                if (op == Operator.BETWEEN || op == Operator.NOT_BETWEEN) {
                    boolean z4 = op == Operator.NOT_BETWEEN;
                    Object value = readValue();
                    if (!"and".equalsIgnoreCase(readName())) {
                        throw new JSONPathException(this.path);
                    }
                    Object value2 = readValue();
                    if (value == null || value2 == null) {
                        throw new JSONPathException(this.path);
                    }
                    if (JSONPath.isInt(value.getClass()) && JSONPath.isInt(value2.getClass())) {
                        return new IntBetweenSegement(name, z3, TypeUtils.longExtractValue((Number) value), TypeUtils.longExtractValue((Number) value2), z4);
                    }
                    throw new JSONPathException(this.path);
                }
                if (op == Operator.IN || op == Operator.NOT_IN) {
                    boolean z5 = op == Operator.NOT_IN;
                    accept('(');
                    JSONArray jSONArray = new JSONArray();
                    jSONArray.add(readValue());
                    while (true) {
                        skipWhitespace();
                        if (this.ch != ',') {
                            break;
                        }
                        next();
                        jSONArray.add(readValue());
                    }
                    boolean z6 = true;
                    boolean z7 = true;
                    boolean z8 = true;
                    for (Object obj : jSONArray) {
                        if (obj != null) {
                            Class<?> cls = obj.getClass();
                            if (z6 && cls != Byte.class && cls != Short.class && cls != Integer.class && cls != Long.class) {
                                z6 = false;
                                z8 = false;
                            }
                            if (z7 && cls != String.class) {
                                z7 = false;
                            }
                        } else if (z6) {
                            z6 = false;
                        }
                    }
                    if (jSONArray.size() == 1 && jSONArray.get(0) == null) {
                        if (z5) {
                            nullSegement = new NotNullSegement(name, z3);
                        } else {
                            nullSegement = new NullSegement(name, z3);
                        }
                        while (true) {
                            c6 = this.ch;
                            if (c6 != ' ') {
                                break;
                            }
                            next();
                        }
                        if (c6 == '&' || c6 == '|') {
                            nullSegement = filterRest(nullSegement);
                        }
                        accept(')');
                        if (z2) {
                            accept(')');
                        }
                        if (z) {
                            accept(']');
                        }
                        return nullSegement;
                    }
                    if (z6) {
                        if (jSONArray.size() == 1) {
                            Filter intOpSegement = new IntOpSegement(name, z3, TypeUtils.longExtractValue((Number) jSONArray.get(0)), z5 ? Operator.NE : Operator.EQ);
                            while (true) {
                                c5 = this.ch;
                                if (c5 != ' ') {
                                    break;
                                }
                                next();
                            }
                            if (c5 == '&' || c5 == '|') {
                                intOpSegement = filterRest(intOpSegement);
                            }
                            accept(')');
                            if (z2) {
                                accept(')');
                            }
                            if (z) {
                                accept(']');
                            }
                            return intOpSegement;
                        }
                        int size = jSONArray.size();
                        long[] jArr = new long[size];
                        for (int i5 = 0; i5 < size; i5++) {
                            jArr[i5] = TypeUtils.longExtractValue((Number) jSONArray.get(i5));
                        }
                        Filter intInSegement = new IntInSegement(name, z3, jArr, z5);
                        while (true) {
                            c4 = this.ch;
                            if (c4 != ' ') {
                                break;
                            }
                            next();
                        }
                        if (c4 == '&' || c4 == '|') {
                            intInSegement = filterRest(intInSegement);
                        }
                        accept(')');
                        if (z2) {
                            accept(')');
                        }
                        if (z) {
                            accept(']');
                        }
                        return intInSegement;
                    }
                    if (!z7) {
                        if (z8) {
                            int size2 = jSONArray.size();
                            Long[] lArr = new Long[size2];
                            for (int i6 = 0; i6 < size2; i6++) {
                                Number number = (Number) jSONArray.get(i6);
                                if (number != null) {
                                    lArr[i6] = Long.valueOf(TypeUtils.longExtractValue(number));
                                }
                            }
                            Filter intObjInSegement = new IntObjInSegement(name, z3, lArr, z5);
                            while (true) {
                                c = this.ch;
                                if (c != ' ') {
                                    break;
                                }
                                next();
                            }
                            if (c == '&' || c == '|') {
                                intObjInSegement = filterRest(intObjInSegement);
                            }
                            accept(')');
                            if (z2) {
                                accept(')');
                            }
                            if (z) {
                                accept(']');
                            }
                            return intObjInSegement;
                        }
                        throw new UnsupportedOperationException();
                    }
                    if (jSONArray.size() == 1) {
                        Filter stringOpSegement2 = new StringOpSegement(name, z3, (String) jSONArray.get(0), z5 ? Operator.NE : Operator.EQ);
                        while (true) {
                            c3 = this.ch;
                            if (c3 != ' ') {
                                break;
                            }
                            next();
                        }
                        if (c3 == '&' || c3 == '|') {
                            stringOpSegement2 = filterRest(stringOpSegement2);
                        }
                        accept(')');
                        if (z2) {
                            accept(')');
                        }
                        if (z) {
                            accept(']');
                        }
                        return stringOpSegement2;
                    }
                    String[] strArr2 = new String[jSONArray.size()];
                    jSONArray.toArray(strArr2);
                    Filter stringInSegement = new StringInSegement(name, z3, strArr2, z5);
                    while (true) {
                        c2 = this.ch;
                        if (c2 != ' ') {
                            break;
                        }
                        next();
                    }
                    if (c2 == '&' || c2 == '|') {
                        stringInSegement = filterRest(stringInSegement);
                    }
                    accept(')');
                    if (z2) {
                        accept(')');
                    }
                    if (z) {
                        accept(']');
                    }
                    return stringInSegement;
                }
                char c16 = this.ch;
                if (c16 == '\'' || c16 == '\"') {
                    String string = readString();
                    if (op == Operator.RLIKE) {
                        matchSegement = new RlikeSegement(name, z3, string, false);
                    } else {
                        if (op == Operator.NOT_RLIKE) {
                            stringOpSegement = new RlikeSegement(name, z3, string, true);
                        } else if (op == Operator.LIKE || op == Operator.NOT_LIKE) {
                            while (string.indexOf("%%") != -1) {
                                string = string.replaceAll("%%", "%");
                            }
                            boolean z9 = op == Operator.NOT_LIKE;
                            int iIndexOf = string.indexOf(37);
                            if (iIndexOf == -1) {
                                if (op == Operator.LIKE) {
                                    operator = Operator.EQ;
                                } else {
                                    operator = Operator.NE;
                                }
                                stringOpSegement = new StringOpSegement(name, z3, string, operator);
                            } else {
                                String[] strArrSplit = string.split("%");
                                if (iIndexOf == 0) {
                                    if (string.charAt(string.length() - 1) == '%') {
                                        int length = strArrSplit.length - 1;
                                        String[] strArr3 = new String[length];
                                        System.arraycopy(strArrSplit, 1, strArr3, 0, length);
                                        strArr = strArr3;
                                        str = null;
                                        str2 = null;
                                    } else {
                                        String str4 = strArrSplit[strArrSplit.length - 1];
                                        if (strArrSplit.length > 2) {
                                            int length2 = strArrSplit.length - 2;
                                            String[] strArr4 = new String[length2];
                                            System.arraycopy(strArrSplit, 1, strArr4, 0, length2);
                                            str2 = str4;
                                            strArr = strArr4;
                                            str = null;
                                        } else {
                                            str2 = str4;
                                            str = null;
                                            strArr = null;
                                        }
                                    }
                                } else {
                                    if (string.charAt(string.length() - 1) == '%') {
                                        if (strArrSplit.length == 1) {
                                            str3 = strArrSplit[0];
                                        } else {
                                            strArr = strArrSplit;
                                            str = null;
                                            str2 = null;
                                        }
                                    } else if (strArrSplit.length == 1) {
                                        str3 = strArrSplit[0];
                                    } else if (strArrSplit.length == 2) {
                                        str = strArrSplit[0];
                                        str2 = strArrSplit[1];
                                        strArr = null;
                                    } else {
                                        String str5 = strArrSplit[0];
                                        String str6 = strArrSplit[strArrSplit.length - 1];
                                        int length3 = strArrSplit.length - 2;
                                        String[] strArr5 = new String[length3];
                                        System.arraycopy(strArrSplit, 1, strArr5, 0, length3);
                                        str = str5;
                                        strArr = strArr5;
                                        str2 = str6;
                                    }
                                    str = str3;
                                    str2 = null;
                                    strArr = null;
                                }
                                matchSegement = new MatchSegement(name, z3, str, str2, strArr, z9);
                            }
                        } else {
                            matchSegement = new StringOpSegement(name, z3, string, op);
                        }
                        matchSegement = stringOpSegement;
                    }
                    while (true) {
                        c7 = this.ch;
                        if (c7 != ' ') {
                            break;
                        }
                        next();
                    }
                    if (c7 == '&' || c7 == '|') {
                        matchSegement = filterRest(matchSegement);
                    }
                    if (z2) {
                        accept(')');
                    }
                    if (z) {
                        accept(']');
                    }
                    return matchSegement;
                }
                if (isDigitFirst(c16)) {
                    long longValue = readLongValue();
                    double doubleValue = this.ch == '.' ? readDoubleValue(longValue) : 0.0d;
                    if (doubleValue == 0.0d) {
                        doubleOpSegement = new IntOpSegement(name, z3, longValue, op);
                    } else {
                        doubleOpSegement = new DoubleOpSegement(name, z3, doubleValue, op);
                    }
                    while (true) {
                        c11 = this.ch;
                        if (c11 != ' ') {
                            break;
                        }
                        next();
                    }
                    if (i > 1 && c11 == ')') {
                        next();
                    }
                    char c17 = this.ch;
                    if (c17 == '&' || c17 == '|') {
                        doubleOpSegement = filterRest(doubleOpSegement);
                    }
                    if (z2) {
                        accept(')');
                    }
                    if (z) {
                        accept(']');
                    }
                    return doubleOpSegement;
                }
                char c18 = this.ch;
                if (c18 == '$') {
                    RefOpSegement refOpSegement = new RefOpSegement(name, z3, readSegement(), op);
                    this.hasRefSegment = true;
                    while (this.ch == ' ') {
                        next();
                    }
                    if (z2) {
                        accept(')');
                    }
                    if (z) {
                        accept(']');
                    }
                    return refOpSegement;
                }
                if (c18 == '/') {
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        next();
                        char c19 = this.ch;
                        if (c19 == '/') {
                            break;
                        }
                        if (c19 == '\\') {
                            next();
                            sb.append(this.ch);
                        } else {
                            sb.append(c19);
                        }
                    }
                    next();
                    if (this.ch == 'i') {
                        next();
                        i2 = 2;
                    } else {
                        i2 = 0;
                    }
                    RegMatchSegement regMatchSegement = new RegMatchSegement(name, z3, Pattern.compile(sb.toString(), i2), op);
                    if (z2) {
                        accept(')');
                    }
                    if (z) {
                        accept(']');
                    }
                    return regMatchSegement;
                }
                if (c18 == 'n') {
                    if ("null".equals(readName())) {
                        if (op == Operator.EQ) {
                            notNullSegement = new NullSegement(name, z3);
                        } else {
                            notNullSegement = op == Operator.NE ? new NotNullSegement(name, z3) : null;
                        }
                        if (notNullSegement != null) {
                            while (true) {
                                c10 = this.ch;
                                if (c10 != ' ') {
                                    break;
                                }
                                next();
                            }
                            if (c10 == '&' || c10 == '|') {
                                notNullSegement = filterRest(notNullSegement);
                            }
                        }
                        if (z2) {
                            accept(')');
                        }
                        accept(']');
                        if (notNullSegement != null) {
                            return notNullSegement;
                        }
                        throw new UnsupportedOperationException();
                    }
                } else if (c18 == 't') {
                    if ("true".equals(readName())) {
                        if (op == Operator.EQ) {
                            valueSegment2 = new ValueSegment(name, z3, Boolean.TRUE, true);
                        } else {
                            valueSegment2 = op == Operator.NE ? new ValueSegment(name, z3, Boolean.TRUE, false) : null;
                        }
                        if (valueSegment2 != null) {
                            while (true) {
                                c9 = this.ch;
                                if (c9 != ' ') {
                                    break;
                                }
                                next();
                            }
                            if (c9 == '&' || c9 == '|') {
                                valueSegment2 = filterRest(valueSegment2);
                            }
                        }
                        if (z2) {
                            accept(')');
                        }
                        accept(']');
                        if (valueSegment2 != null) {
                            return valueSegment2;
                        }
                        throw new UnsupportedOperationException();
                    }
                } else if (c18 == 'f' && "false".equals(readName())) {
                    if (op == Operator.EQ) {
                        valueSegment = new ValueSegment(name, z3, Boolean.FALSE, true);
                    } else {
                        valueSegment = op == Operator.NE ? new ValueSegment(name, z3, Boolean.FALSE, false) : null;
                    }
                    if (valueSegment != null) {
                        while (true) {
                            c8 = this.ch;
                            if (c8 != ' ') {
                                break;
                            }
                            next();
                        }
                        if (c8 == '&' || c8 == '|') {
                            valueSegment = filterRest(valueSegment);
                        }
                    }
                    if (z2) {
                        accept(')');
                    }
                    accept(']');
                    if (valueSegment != null) {
                        return valueSegment;
                    }
                    throw new UnsupportedOperationException();
                }
                throw new UnsupportedOperationException();
            }
            int i7 = this.pos - 1;
            while (true) {
                char c20 = this.ch;
                if (c20 == ']' || c20 == '/' || isEOF() || !(this.ch != '.' || z2 || z2 || c14 == '\'')) {
                    break;
                }
                if (this.ch == '\\') {
                    next();
                }
                next();
            }
            if (z || (c15 = this.ch) == '/' || c15 == '.') {
                int i8 = this.pos;
                i3 = i8 - 1;
            } else {
                i3 = this.pos;
            }
            String strSubstring = this.path.substring(i7, i3);
            if (strSubstring.indexOf(92) != 0) {
                StringBuilder sb2 = new StringBuilder(strSubstring.length());
                int i9 = 0;
                while (i9 < strSubstring.length()) {
                    char cCharAt2 = strSubstring.charAt(i9);
                    if (cCharAt2 == '\\' && i9 < strSubstring.length() - 1 && ((cCharAt = strSubstring.charAt((i4 = i9 + 1))) == '@' || cCharAt2 == '\\' || cCharAt2 == '\"')) {
                        sb2.append(cCharAt);
                        i9 = i4;
                    } else {
                        sb2.append(cCharAt2);
                    }
                    i9++;
                }
                strSubstring = sb2.toString();
            }
            if (strSubstring.indexOf("\\.") != -1) {
                if (c14 == '\'' && strSubstring.length() > 2 && strSubstring.charAt(strSubstring.length() - 1) == c14) {
                    strReplaceAll = strSubstring.substring(1, strSubstring.length() - 1);
                } else {
                    strReplaceAll = strSubstring.replaceAll("\\\\\\.", "\\.");
                    if (strReplaceAll.indexOf("\\-") != -1) {
                        strReplaceAll = strReplaceAll.replaceAll("\\\\-", NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                    }
                }
                if (z2) {
                    accept(')');
                }
                return new PropertySegment(strReplaceAll, false);
            }
            Segment segmentBuildArraySegement = buildArraySegement(strSubstring);
            if (z && !isEOF()) {
                accept(']');
            }
            return segmentBuildArraySegement;
        }

        Filter filterRest(Filter filter) {
            boolean z = true;
            boolean z2 = this.ch == '&';
            if ((this.ch != '&' || getNextChar() != '&') && (this.ch != '|' || getNextChar() != '|')) {
                return filter;
            }
            next();
            next();
            if (this.ch == '(') {
                next();
            } else {
                z = false;
            }
            while (this.ch == ' ') {
                next();
            }
            FilterGroup filterGroup = new FilterGroup(filter, (Filter) parseArrayAccessFilter(false), z2);
            if (z && this.ch == ')') {
                next();
            }
            return filterGroup;
        }

        protected long readLongValue() {
            int i = this.pos - 1;
            char c = this.ch;
            if (c == '+' || c == '-') {
                next();
            }
            while (true) {
                char c2 = this.ch;
                if (c2 < '0' || c2 > '9') {
                    break;
                }
                next();
            }
            return Long.parseLong(this.path.substring(i, this.pos - 1));
        }

        protected double readDoubleValue(long j) {
            int i = this.pos - 1;
            next();
            while (true) {
                char c = this.ch;
                if (c < '0' || c > '9') {
                    break;
                }
                next();
            }
            return Double.parseDouble(this.path.substring(i, this.pos - 1)) + j;
        }

        protected Object readValue() {
            skipWhitespace();
            if (isDigitFirst(this.ch)) {
                return Long.valueOf(readLongValue());
            }
            char c = this.ch;
            if (c == '\"' || c == '\'') {
                return readString();
            }
            if (c == 'n') {
                if ("null".equals(readName())) {
                    return null;
                }
                throw new JSONPathException(this.path);
            }
            throw new UnsupportedOperationException();
        }

        protected Operator readOp() {
            Operator operator;
            char c = this.ch;
            if (c == '=') {
                next();
                char c2 = this.ch;
                if (c2 == '~') {
                    next();
                    operator = Operator.REG_MATCH;
                } else if (c2 == '=') {
                    next();
                    operator = Operator.EQ;
                } else {
                    operator = Operator.EQ;
                }
            } else if (c == '!') {
                next();
                accept('=');
                operator = Operator.NE;
            } else if (c == '<') {
                next();
                if (this.ch == '=') {
                    next();
                    operator = Operator.LE;
                } else {
                    operator = Operator.LT;
                }
            } else if (c == '>') {
                next();
                if (this.ch == '=') {
                    next();
                    operator = Operator.GE;
                } else {
                    operator = Operator.GT;
                }
            } else {
                operator = null;
            }
            if (operator != null) {
                return operator;
            }
            String name = readName();
            if ("not".equalsIgnoreCase(name)) {
                skipWhitespace();
                String name2 = readName();
                if ("like".equalsIgnoreCase(name2)) {
                    return Operator.NOT_LIKE;
                }
                if ("rlike".equalsIgnoreCase(name2)) {
                    return Operator.NOT_RLIKE;
                }
                if ("in".equalsIgnoreCase(name2)) {
                    return Operator.NOT_IN;
                }
                if ("between".equalsIgnoreCase(name2)) {
                    return Operator.NOT_BETWEEN;
                }
                throw new UnsupportedOperationException();
            }
            if ("nin".equalsIgnoreCase(name)) {
                return Operator.NOT_IN;
            }
            if ("like".equalsIgnoreCase(name)) {
                return Operator.LIKE;
            }
            if ("rlike".equalsIgnoreCase(name)) {
                return Operator.RLIKE;
            }
            if ("in".equalsIgnoreCase(name)) {
                return Operator.IN;
            }
            if ("between".equalsIgnoreCase(name)) {
                return Operator.BETWEEN;
            }
            throw new UnsupportedOperationException();
        }

        String readName() {
            skipWhitespace();
            char c = this.ch;
            if (c != '\\' && !Character.isJavaIdentifierStart(c)) {
                throw new JSONPathException("illeal jsonpath syntax. " + this.path);
            }
            StringBuilder sb = new StringBuilder();
            while (!isEOF()) {
                char c2 = this.ch;
                if (c2 == '\\') {
                    next();
                    sb.append(this.ch);
                    if (isEOF()) {
                        return sb.toString();
                    }
                    next();
                } else {
                    if (!Character.isJavaIdentifierPart(c2)) {
                        break;
                    }
                    sb.append(this.ch);
                    next();
                }
            }
            if (isEOF() && Character.isJavaIdentifierPart(this.ch)) {
                sb.append(this.ch);
            }
            return sb.toString();
        }

        String readString() {
            char c = this.ch;
            next();
            int i = this.pos - 1;
            while (this.ch != c && !isEOF()) {
                next();
            }
            String strSubstring = this.path.substring(i, isEOF() ? this.pos : this.pos - 1);
            accept(c);
            return strSubstring;
        }

        void accept(char c) {
            if (this.ch == ' ') {
                next();
            }
            if (this.ch != c) {
                throw new JSONPathException("expect '" + c + ", but '" + this.ch + "'");
            }
            if (isEOF()) {
                return;
            }
            next();
        }

        public Segment[] explain() {
            String str = this.path;
            if (str == null || str.length() == 0) {
                throw new IllegalArgumentException();
            }
            Segment[] segmentArr = new Segment[8];
            while (true) {
                Segment segement = readSegement();
                if (segement == null) {
                    break;
                }
                if (segement instanceof PropertySegment) {
                    PropertySegment propertySegment = (PropertySegment) segement;
                    if (propertySegment.deep || !propertySegment.propertyName.equals(PhoneConstants.APN_TYPE_ALL)) {
                    }
                }
                int i = this.level;
                if (i == segmentArr.length) {
                    Segment[] segmentArr2 = new Segment[(i * 3) / 2];
                    System.arraycopy(segmentArr, 0, segmentArr2, 0, i);
                    segmentArr = segmentArr2;
                }
                int i2 = this.level;
                this.level = i2 + 1;
                segmentArr[i2] = segement;
            }
            int i3 = this.level;
            if (i3 == segmentArr.length) {
                return segmentArr;
            }
            Segment[] segmentArr3 = new Segment[i3];
            System.arraycopy(segmentArr, 0, segmentArr3, 0, i3);
            return segmentArr3;
        }

        Segment buildArraySegement(String str) {
            int length = str.length();
            char cCharAt = str.charAt(0);
            int i = length - 1;
            char cCharAt2 = str.charAt(i);
            int iIndexOf = str.indexOf(44);
            if (str.length() > 2 && cCharAt == '\'' && cCharAt2 == '\'') {
                String strSubstring = str.substring(1, i);
                if (iIndexOf == -1 || !strArrayPatternx.matcher(str).find()) {
                    return new PropertySegment(strSubstring, false);
                }
                return new MultiPropertySegment(strSubstring.split(strArrayRegex));
            }
            int iIndexOf2 = str.indexOf(58);
            if (iIndexOf == -1 && iIndexOf2 == -1) {
                if (TypeUtils.isNumber(str)) {
                    try {
                        return new ArrayAccessSegment(Integer.parseInt(str));
                    } catch (NumberFormatException unused) {
                        return new PropertySegment(str, false);
                    }
                }
                if (str.charAt(0) == '\"' && str.charAt(str.length() - 1) == '\"') {
                    str = str.substring(1, str.length() - 1);
                }
                return new PropertySegment(str, false);
            }
            if (iIndexOf != -1) {
                String[] strArrSplit = str.split(",");
                int[] iArr = new int[strArrSplit.length];
                for (int i2 = 0; i2 < strArrSplit.length; i2++) {
                    iArr[i2] = Integer.parseInt(strArrSplit[i2]);
                }
                return new MultiIndexSegment(iArr);
            }
            if (iIndexOf2 != -1) {
                String[] strArrSplit2 = str.split(SettingsStringUtil.DELIMITER);
                int length2 = strArrSplit2.length;
                int[] iArr2 = new int[length2];
                for (int i3 = 0; i3 < strArrSplit2.length; i3++) {
                    String str2 = strArrSplit2[i3];
                    if (str2.length() != 0) {
                        iArr2[i3] = Integer.parseInt(str2);
                    } else if (i3 == 0) {
                        iArr2[i3] = 0;
                    } else {
                        throw new UnsupportedOperationException();
                    }
                }
                int i4 = iArr2[0];
                int i5 = length2 > 1 ? iArr2[1] : -1;
                int i6 = length2 == 3 ? iArr2[2] : 1;
                if (i5 < 0 || i5 >= i4) {
                    if (i6 <= 0) {
                        throw new UnsupportedOperationException("step must greater than zero : " + i6);
                    }
                    return new RangeSegment(i4, i5, i6);
                }
                throw new UnsupportedOperationException("end must greater than or equals start. start " + i4 + ",  end " + i5);
            }
            throw new UnsupportedOperationException();
        }
    }

    static class SizeSegment implements Segment {
        public static final SizeSegment instance = new SizeSegment();

        SizeSegment() {
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public Integer eval(JSONPath jSONPath, Object obj, Object obj2) {
            return Integer.valueOf(jSONPath.evalSize(obj2));
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public void extract(JSONPath jSONPath, DefaultJSONParser defaultJSONParser, Context context) {
            context.object = Integer.valueOf(jSONPath.evalSize(defaultJSONParser.parse()));
        }
    }

    static class TypeSegment implements Segment {
        public static final TypeSegment instance = new TypeSegment();

        TypeSegment() {
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public String eval(JSONPath jSONPath, Object obj, Object obj2) {
            if (obj2 == null) {
                return "null";
            }
            if (obj2 instanceof Collection) {
                return "array";
            }
            if (obj2 instanceof Number) {
                return "number";
            }
            if (obj2 instanceof Boolean) {
                return "boolean";
            }
            return ((obj2 instanceof String) || (obj2 instanceof UUID) || (obj2 instanceof Enum)) ? "string" : "object";
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public void extract(JSONPath jSONPath, DefaultJSONParser defaultJSONParser, Context context) {
            throw new UnsupportedOperationException();
        }
    }

    static class FloorSegment implements Segment {
        public static final FloorSegment instance = new FloorSegment();

        FloorSegment() {
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public Object eval(JSONPath jSONPath, Object obj, Object obj2) {
            if (obj2 instanceof JSONArray) {
                JSONArray jSONArray = (JSONArray) ((JSONArray) obj2).clone();
                for (int i = 0; i < jSONArray.size(); i++) {
                    Object obj3 = jSONArray.get(i);
                    Object objFloor = floor(obj3);
                    if (objFloor != obj3) {
                        jSONArray.set(i, objFloor);
                    }
                }
                return jSONArray;
            }
            return floor(obj2);
        }

        private static Object floor(Object obj) {
            if (obj == null) {
                return null;
            }
            if (obj instanceof Float) {
                return Double.valueOf(Math.floor(((Float) obj).floatValue()));
            }
            if (obj instanceof Double) {
                return Double.valueOf(Math.floor(((Double) obj).doubleValue()));
            }
            if (obj instanceof BigDecimal) {
                return ((BigDecimal) obj).setScale(0, RoundingMode.FLOOR);
            }
            if ((obj instanceof Byte) || (obj instanceof Short) || (obj instanceof Integer) || (obj instanceof Long) || (obj instanceof BigInteger)) {
                return obj;
            }
            throw new UnsupportedOperationException();
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public void extract(JSONPath jSONPath, DefaultJSONParser defaultJSONParser, Context context) {
            throw new UnsupportedOperationException();
        }
    }

    static class MaxSegment implements Segment {
        public static final MaxSegment instance = new MaxSegment();

        MaxSegment() {
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public Object eval(JSONPath jSONPath, Object obj, Object obj2) {
            if (obj2 instanceof Collection) {
                Object obj3 = null;
                for (Object obj4 : (Collection) obj2) {
                    if (obj4 != null && (obj3 == null || JSONPath.compare(obj3, obj4) < 0)) {
                        obj3 = obj4;
                    }
                }
                return obj3;
            }
            throw new UnsupportedOperationException();
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public void extract(JSONPath jSONPath, DefaultJSONParser defaultJSONParser, Context context) {
            throw new UnsupportedOperationException();
        }
    }

    static class MinSegment implements Segment {
        public static final MinSegment instance = new MinSegment();

        MinSegment() {
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public Object eval(JSONPath jSONPath, Object obj, Object obj2) {
            if (obj2 instanceof Collection) {
                Object obj3 = null;
                for (Object obj4 : (Collection) obj2) {
                    if (obj4 != null && (obj3 == null || JSONPath.compare(obj3, obj4) > 0)) {
                        obj3 = obj4;
                    }
                }
                return obj3;
            }
            throw new UnsupportedOperationException();
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public void extract(JSONPath jSONPath, DefaultJSONParser defaultJSONParser, Context context) {
            throw new UnsupportedOperationException();
        }
    }

    static int compare(Object obj, Object obj2) {
        Object d;
        Object f;
        if (obj.getClass() == obj2.getClass()) {
            return ((Comparable) obj).compareTo(obj2);
        }
        Class<?> cls = obj.getClass();
        Class<?> cls2 = obj2.getClass();
        if (cls == BigDecimal.class) {
            if (cls2 == Integer.class) {
                f = new BigDecimal(((Integer) obj2).intValue());
            } else if (cls2 == Long.class) {
                f = new BigDecimal(((Long) obj2).longValue());
            } else if (cls2 == Float.class) {
                f = new BigDecimal(((Float) obj2).floatValue());
            } else if (cls2 == Double.class) {
                f = new BigDecimal(((Double) obj2).doubleValue());
            }
            obj2 = f;
        } else if (cls == Long.class) {
            if (cls2 == Integer.class) {
                f = new Long(((Integer) obj2).intValue());
                obj2 = f;
            } else {
                if (cls2 == BigDecimal.class) {
                    d = new BigDecimal(((Long) obj).longValue());
                } else if (cls2 == Float.class) {
                    d = new Float(((Long) obj).longValue());
                } else if (cls2 == Double.class) {
                    d = new Double(((Long) obj).longValue());
                }
                obj = d;
            }
        } else if (cls == Integer.class) {
            if (cls2 == Long.class) {
                d = new Long(((Integer) obj).intValue());
            } else if (cls2 == BigDecimal.class) {
                d = new BigDecimal(((Integer) obj).intValue());
            } else if (cls2 == Float.class) {
                d = new Float(((Integer) obj).intValue());
            } else if (cls2 == Double.class) {
                d = new Double(((Integer) obj).intValue());
            }
            obj = d;
        } else if (cls == Double.class) {
            if (cls2 == Integer.class) {
                f = new Double(((Integer) obj2).intValue());
            } else if (cls2 == Long.class) {
                f = new Double(((Long) obj2).longValue());
            } else if (cls2 == Float.class) {
                f = new Double(((Float) obj2).floatValue());
            }
            obj2 = f;
        } else if (cls == Float.class) {
            if (cls2 == Integer.class) {
                f = new Float(((Integer) obj2).intValue());
            } else if (cls2 == Long.class) {
                f = new Float(((Long) obj2).longValue());
            } else if (cls2 == Double.class) {
                d = new Double(((Float) obj).floatValue());
                obj = d;
            }
            obj2 = f;
        }
        return ((Comparable) obj).compareTo(obj2);
    }

    static class KeySetSegment implements Segment {
        public static final KeySetSegment instance = new KeySetSegment();

        KeySetSegment() {
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public Object eval(JSONPath jSONPath, Object obj, Object obj2) {
            return jSONPath.evalKeySet(obj2);
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public void extract(JSONPath jSONPath, DefaultJSONParser defaultJSONParser, Context context) {
            throw new UnsupportedOperationException();
        }
    }

    static class PropertySegment implements Segment {
        private final boolean deep;
        private final String propertyName;
        private final long propertyNameHash;

        public PropertySegment(String str, boolean z) {
            this.propertyName = str;
            this.propertyNameHash = TypeUtils.fnv1a_64(str);
            this.deep = z;
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public Object eval(JSONPath jSONPath, Object obj, Object obj2) {
            if (this.deep) {
                ArrayList arrayList = new ArrayList();
                jSONPath.deepScan(obj2, this.propertyName, arrayList);
                return arrayList;
            }
            return jSONPath.getPropertyValue(obj2, this.propertyName, this.propertyNameHash);
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public void extract(JSONPath jSONPath, DefaultJSONParser defaultJSONParser, Context context) {
            Object objIntegerValue;
            Object objIntegerValue2;
            JSONArray jSONArray;
            Object objIntegerValue3;
            JSONLexerBase jSONLexerBase = (JSONLexerBase) defaultJSONParser.lexer;
            if (this.deep && context.object == null) {
                context.object = new JSONArray();
            }
            if (jSONLexerBase.token() == 14) {
                if (PhoneConstants.APN_TYPE_ALL.equals(this.propertyName)) {
                    return;
                }
                jSONLexerBase.nextToken();
                if (this.deep) {
                    jSONArray = (JSONArray) context.object;
                } else {
                    jSONArray = new JSONArray();
                }
                while (true) {
                    int i = jSONLexerBase.token();
                    if (i == 12) {
                        boolean z = this.deep;
                        if (z) {
                            extract(jSONPath, defaultJSONParser, context);
                        } else {
                            int iSeekObjectToField = jSONLexerBase.seekObjectToField(this.propertyNameHash, z);
                            if (iSeekObjectToField == 3) {
                                int i2 = jSONLexerBase.token();
                                if (i2 == 2) {
                                    objIntegerValue3 = jSONLexerBase.integerValue();
                                    jSONLexerBase.nextToken();
                                } else if (i2 == 4) {
                                    objIntegerValue3 = jSONLexerBase.stringVal();
                                    jSONLexerBase.nextToken();
                                } else {
                                    objIntegerValue3 = defaultJSONParser.parse();
                                }
                                jSONArray.add(objIntegerValue3);
                                if (jSONLexerBase.token() == 13) {
                                    jSONLexerBase.nextToken();
                                } else {
                                    jSONLexerBase.skipObject(false);
                                }
                            } else if (iSeekObjectToField == -1) {
                                continue;
                            } else {
                                if (this.deep) {
                                    throw new UnsupportedOperationException(jSONLexerBase.info());
                                }
                                jSONLexerBase.skipObject(false);
                            }
                        }
                    } else if (i == 14) {
                        if (this.deep) {
                            extract(jSONPath, defaultJSONParser, context);
                        } else {
                            jSONLexerBase.skipObject(false);
                        }
                    } else {
                        switch (i) {
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                                jSONLexerBase.nextToken();
                                break;
                        }
                    }
                    if (jSONLexerBase.token() == 15) {
                        jSONLexerBase.nextToken();
                        if (this.deep || jSONArray.size() <= 0) {
                            return;
                        }
                        context.object = jSONArray;
                        return;
                    }
                    if (jSONLexerBase.token() == 16) {
                        jSONLexerBase.nextToken();
                    } else {
                        throw new JSONException("illegal json : " + jSONLexerBase.info());
                    }
                }
            } else {
                boolean z2 = this.deep;
                if (!z2) {
                    if (jSONLexerBase.seekObjectToField(this.propertyNameHash, z2) == 3 && context.eval) {
                        int i3 = jSONLexerBase.token();
                        if (i3 == 2) {
                            objIntegerValue2 = jSONLexerBase.integerValue();
                            jSONLexerBase.nextToken(16);
                        } else if (i3 == 3) {
                            objIntegerValue2 = jSONLexerBase.decimalValue();
                            jSONLexerBase.nextToken(16);
                        } else if (i3 == 4) {
                            objIntegerValue2 = jSONLexerBase.stringVal();
                            jSONLexerBase.nextToken(16);
                        } else {
                            objIntegerValue2 = defaultJSONParser.parse();
                        }
                        if (context.eval) {
                            context.object = objIntegerValue2;
                            return;
                        }
                        return;
                    }
                    return;
                }
                while (true) {
                    int iSeekObjectToField2 = jSONLexerBase.seekObjectToField(this.propertyNameHash, this.deep);
                    if (iSeekObjectToField2 == -1) {
                        return;
                    }
                    if (iSeekObjectToField2 == 3) {
                        if (context.eval) {
                            int i4 = jSONLexerBase.token();
                            if (i4 == 2) {
                                objIntegerValue = jSONLexerBase.integerValue();
                                jSONLexerBase.nextToken(16);
                            } else if (i4 == 3) {
                                objIntegerValue = jSONLexerBase.decimalValue();
                                jSONLexerBase.nextToken(16);
                            } else if (i4 == 4) {
                                objIntegerValue = jSONLexerBase.stringVal();
                                jSONLexerBase.nextToken(16);
                            } else {
                                objIntegerValue = defaultJSONParser.parse();
                            }
                            if (context.eval) {
                                if (context.object instanceof List) {
                                    List list = (List) context.object;
                                    if (list.size() == 0 && (objIntegerValue instanceof List)) {
                                        context.object = objIntegerValue;
                                    } else {
                                        list.add(objIntegerValue);
                                    }
                                } else {
                                    context.object = objIntegerValue;
                                }
                            }
                        }
                    } else if (iSeekObjectToField2 == 1 || iSeekObjectToField2 == 2) {
                        extract(jSONPath, defaultJSONParser, context);
                    }
                }
            }
        }

        public void setValue(JSONPath jSONPath, Object obj, Object obj2) {
            if (this.deep) {
                jSONPath.deepSet(obj, this.propertyName, this.propertyNameHash, obj2);
            } else {
                jSONPath.setPropertyValue(obj, this.propertyName, this.propertyNameHash, obj2);
            }
        }

        public boolean remove(JSONPath jSONPath, Object obj) {
            return jSONPath.removePropertyValue(obj, this.propertyName, this.deep);
        }
    }

    static class MultiPropertySegment implements Segment {
        private final String[] propertyNames;
        private final long[] propertyNamesHash;

        public MultiPropertySegment(String[] strArr) {
            this.propertyNames = strArr;
            this.propertyNamesHash = new long[strArr.length];
            int i = 0;
            while (true) {
                long[] jArr = this.propertyNamesHash;
                if (i >= jArr.length) {
                    return;
                }
                jArr[i] = TypeUtils.fnv1a_64(strArr[i]);
                i++;
            }
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public Object eval(JSONPath jSONPath, Object obj, Object obj2) {
            ArrayList arrayList = new ArrayList(this.propertyNames.length);
            int i = 0;
            while (true) {
                String[] strArr = this.propertyNames;
                if (i >= strArr.length) {
                    return arrayList;
                }
                arrayList.add(jSONPath.getPropertyValue(obj2, strArr[i], this.propertyNamesHash[i]));
                i++;
            }
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public void extract(JSONPath jSONPath, DefaultJSONParser defaultJSONParser, Context context) {
            JSONArray jSONArray;
            Object objIntegerValue;
            JSONLexerBase jSONLexerBase = (JSONLexerBase) defaultJSONParser.lexer;
            if (context.object == null) {
                jSONArray = new JSONArray();
                context.object = jSONArray;
            } else {
                jSONArray = (JSONArray) context.object;
            }
            for (int size = jSONArray.size(); size < this.propertyNamesHash.length; size++) {
                jSONArray.add(null);
            }
            do {
                int iSeekObjectToField = jSONLexerBase.seekObjectToField(this.propertyNamesHash);
                if (jSONLexerBase.matchStat != 3) {
                    return;
                }
                int i = jSONLexerBase.token();
                if (i == 2) {
                    objIntegerValue = jSONLexerBase.integerValue();
                    jSONLexerBase.nextToken(16);
                } else if (i == 3) {
                    objIntegerValue = jSONLexerBase.decimalValue();
                    jSONLexerBase.nextToken(16);
                } else if (i == 4) {
                    objIntegerValue = jSONLexerBase.stringVal();
                    jSONLexerBase.nextToken(16);
                } else {
                    objIntegerValue = defaultJSONParser.parse();
                }
                jSONArray.set(iSeekObjectToField, objIntegerValue);
            } while (jSONLexerBase.token() == 16);
        }
    }

    static class WildCardSegment implements Segment {
        public static final WildCardSegment instance = new WildCardSegment(false, false);
        public static final WildCardSegment instance_deep = new WildCardSegment(true, false);
        public static final WildCardSegment instance_deep_objectOnly = new WildCardSegment(true, true);
        private boolean deep;
        private boolean objectOnly;

        private WildCardSegment(boolean z, boolean z2) {
            this.deep = z;
            this.objectOnly = z2;
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public Object eval(JSONPath jSONPath, Object obj, Object obj2) {
            if (!this.deep) {
                return jSONPath.getPropertyValues(obj2);
            }
            ArrayList arrayList = new ArrayList();
            jSONPath.deepGetPropertyValues(obj2, arrayList);
            return arrayList;
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public void extract(JSONPath jSONPath, DefaultJSONParser defaultJSONParser, Context context) {
            if (context.eval) {
                Object obj = defaultJSONParser.parse();
                if (this.deep) {
                    ArrayList arrayList = new ArrayList();
                    if (this.objectOnly) {
                        jSONPath.deepGetObjects(obj, arrayList);
                    } else {
                        jSONPath.deepGetPropertyValues(obj, arrayList);
                    }
                    context.object = arrayList;
                    return;
                }
                if (obj instanceof JSONObject) {
                    Collection<?> collectionValues = ((JSONObject) obj).values();
                    JSONArray jSONArray = new JSONArray(collectionValues.size());
                    jSONArray.addAll(collectionValues);
                    context.object = jSONArray;
                    return;
                }
                if (obj instanceof JSONArray) {
                    context.object = obj;
                    return;
                }
            }
            throw new JSONException("TODO");
        }
    }

    static class ArrayAccessSegment implements Segment {
        private final int index;

        public ArrayAccessSegment(int i) {
            this.index = i;
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public Object eval(JSONPath jSONPath, Object obj, Object obj2) {
            return jSONPath.getArrayItem(obj2, this.index);
        }

        public boolean setValue(JSONPath jSONPath, Object obj, Object obj2) {
            return jSONPath.setArrayItem(jSONPath, obj, this.index, obj2);
        }

        public boolean remove(JSONPath jSONPath, Object obj) {
            return jSONPath.removeArrayItem(jSONPath, obj, this.index);
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public void extract(JSONPath jSONPath, DefaultJSONParser defaultJSONParser, Context context) {
            if (((JSONLexerBase) defaultJSONParser.lexer).seekArrayToItem(this.index) && context.eval) {
                context.object = defaultJSONParser.parse();
            }
        }
    }

    static class MultiIndexSegment implements Segment {
        private final int[] indexes;

        public MultiIndexSegment(int[] iArr) {
            this.indexes = iArr;
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public Object eval(JSONPath jSONPath, Object obj, Object obj2) {
            JSONArray jSONArray = new JSONArray(this.indexes.length);
            int i = 0;
            while (true) {
                int[] iArr = this.indexes;
                if (i >= iArr.length) {
                    return jSONArray;
                }
                jSONArray.add(jSONPath.getArrayItem(obj2, iArr[i]));
                i++;
            }
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public void extract(JSONPath jSONPath, DefaultJSONParser defaultJSONParser, Context context) {
            if (context.eval) {
                Object obj = defaultJSONParser.parse();
                if (obj instanceof List) {
                    int[] iArr = this.indexes;
                    int length = iArr.length;
                    int[] iArr2 = new int[length];
                    System.arraycopy(iArr, 0, iArr2, 0, length);
                    List list = (List) obj;
                    if (iArr2[0] >= 0) {
                        for (int size = list.size() - 1; size >= 0; size--) {
                            if (Arrays.binarySearch(iArr2, size) < 0) {
                                list.remove(size);
                            }
                        }
                        context.object = list;
                        return;
                    }
                }
            }
            throw new UnsupportedOperationException();
        }
    }

    static class RangeSegment implements Segment {
        private final int end;
        private final int start;
        private final int step;

        public RangeSegment(int i, int i2, int i3) {
            this.start = i;
            this.end = i2;
            this.step = i3;
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public Object eval(JSONPath jSONPath, Object obj, Object obj2) {
            int iIntValue = SizeSegment.instance.eval(jSONPath, obj, obj2).intValue();
            int i = this.start;
            if (i < 0) {
                i += iIntValue;
            }
            int i2 = this.end;
            if (i2 < 0) {
                i2 += iIntValue;
            }
            int i3 = ((i2 - i) / this.step) + 1;
            if (i3 == -1) {
                return null;
            }
            ArrayList arrayList = new ArrayList(i3);
            while (i <= i2 && i < iIntValue) {
                arrayList.add(jSONPath.getArrayItem(obj2, i));
                i += this.step;
            }
            return arrayList;
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public void extract(JSONPath jSONPath, DefaultJSONParser defaultJSONParser, Context context) {
            throw new UnsupportedOperationException();
        }
    }

    static class NotNullSegement extends PropertyFilter {
        public NotNullSegement(String str, boolean z) {
            super(str, z);
        }

        @Override // com.alibaba.fastjson.JSONPath.Filter
        public boolean apply(JSONPath jSONPath, Object obj, Object obj2, Object obj3) {
            return jSONPath.getPropertyValue(obj3, this.propertyName, this.propertyNameHash) != null;
        }
    }

    static class NullSegement extends PropertyFilter {
        public NullSegement(String str, boolean z) {
            super(str, z);
        }

        @Override // com.alibaba.fastjson.JSONPath.Filter
        public boolean apply(JSONPath jSONPath, Object obj, Object obj2, Object obj3) {
            return get(jSONPath, obj, obj3) == null;
        }
    }

    static class ValueSegment extends PropertyFilter {
        private boolean eq;
        private final Object value;

        public ValueSegment(String str, boolean z, Object obj, boolean z2) {
            super(str, z);
            this.eq = true;
            if (obj == null) {
                throw new IllegalArgumentException("value is null");
            }
            this.value = obj;
            this.eq = z2;
        }

        @Override // com.alibaba.fastjson.JSONPath.Filter
        public boolean apply(JSONPath jSONPath, Object obj, Object obj2, Object obj3) {
            boolean zEquals = this.value.equals(get(jSONPath, obj, obj3));
            return !this.eq ? !zEquals : zEquals;
        }
    }

    static class IntInSegement extends PropertyFilter {
        private final boolean not;
        private final long[] values;

        public IntInSegement(String str, boolean z, long[] jArr, boolean z2) {
            super(str, z);
            this.values = jArr;
            this.not = z2;
        }

        @Override // com.alibaba.fastjson.JSONPath.Filter
        public boolean apply(JSONPath jSONPath, Object obj, Object obj2, Object obj3) {
            Object obj4 = get(jSONPath, obj, obj3);
            if (obj4 == null) {
                return false;
            }
            if (obj4 instanceof Number) {
                long jLongExtractValue = TypeUtils.longExtractValue((Number) obj4);
                for (long j : this.values) {
                    if (j == jLongExtractValue) {
                        return !this.not;
                    }
                }
            }
            return this.not;
        }
    }

    static class IntBetweenSegement extends PropertyFilter {
        private final long endValue;
        private final boolean not;
        private final long startValue;

        public IntBetweenSegement(String str, boolean z, long j, long j2, boolean z2) {
            super(str, z);
            this.startValue = j;
            this.endValue = j2;
            this.not = z2;
        }

        @Override // com.alibaba.fastjson.JSONPath.Filter
        public boolean apply(JSONPath jSONPath, Object obj, Object obj2, Object obj3) {
            Object obj4 = get(jSONPath, obj, obj3);
            if (obj4 == null) {
                return false;
            }
            if (obj4 instanceof Number) {
                long jLongExtractValue = TypeUtils.longExtractValue((Number) obj4);
                if (jLongExtractValue >= this.startValue && jLongExtractValue <= this.endValue) {
                    return !this.not;
                }
            }
            return this.not;
        }
    }

    static class IntObjInSegement extends PropertyFilter {
        private final boolean not;
        private final Long[] values;

        public IntObjInSegement(String str, boolean z, Long[] lArr, boolean z2) {
            super(str, z);
            this.values = lArr;
            this.not = z2;
        }

        @Override // com.alibaba.fastjson.JSONPath.Filter
        public boolean apply(JSONPath jSONPath, Object obj, Object obj2, Object obj3) {
            Object obj4 = get(jSONPath, obj, obj3);
            int i = 0;
            if (obj4 == null) {
                Long[] lArr = this.values;
                int length = lArr.length;
                while (i < length) {
                    if (lArr[i] == null) {
                        return !this.not;
                    }
                    i++;
                }
                return this.not;
            }
            if (obj4 instanceof Number) {
                long jLongExtractValue = TypeUtils.longExtractValue((Number) obj4);
                Long[] lArr2 = this.values;
                int length2 = lArr2.length;
                while (i < length2) {
                    Long l = lArr2[i];
                    if (l != null && l.longValue() == jLongExtractValue) {
                        return !this.not;
                    }
                    i++;
                }
            }
            return this.not;
        }
    }

    static class StringInSegement extends PropertyFilter {
        private final boolean not;
        private final String[] values;

        public StringInSegement(String str, boolean z, String[] strArr, boolean z2) {
            super(str, z);
            this.values = strArr;
            this.not = z2;
        }

        @Override // com.alibaba.fastjson.JSONPath.Filter
        public boolean apply(JSONPath jSONPath, Object obj, Object obj2, Object obj3) {
            Object obj4 = get(jSONPath, obj, obj3);
            for (String str : this.values) {
                if (str == obj4) {
                    return !this.not;
                }
                if (str != null && str.equals(obj4)) {
                    return !this.not;
                }
            }
            return this.not;
        }
    }

    static class IntOpSegement extends PropertyFilter {
        private final Operator op;
        private final long value;
        private BigDecimal valueDecimal;
        private Double valueDouble;
        private Float valueFloat;

        public IntOpSegement(String str, boolean z, long j, Operator operator) {
            super(str, z);
            this.value = j;
            this.op = operator;
        }

        @Override // com.alibaba.fastjson.JSONPath.Filter
        public boolean apply(JSONPath jSONPath, Object obj, Object obj2, Object obj3) {
            Object obj4 = get(jSONPath, obj, obj3);
            if (obj4 == null || !(obj4 instanceof Number)) {
                return false;
            }
            if (obj4 instanceof BigDecimal) {
                if (this.valueDecimal == null) {
                    this.valueDecimal = BigDecimal.valueOf(this.value);
                }
                int iCompareTo = this.valueDecimal.compareTo((BigDecimal) obj4);
                switch (AnonymousClass1.$SwitchMap$com$alibaba$fastjson$JSONPath$Operator[this.op.ordinal()]) {
                    case 1:
                        return iCompareTo == 0;
                    case 2:
                        return iCompareTo != 0;
                    case 3:
                        return iCompareTo <= 0;
                    case 4:
                        return iCompareTo < 0;
                    case 5:
                        return iCompareTo >= 0;
                    case 6:
                        return iCompareTo > 0;
                    default:
                        return false;
                }
            }
            if (obj4 instanceof Float) {
                if (this.valueFloat == null) {
                    this.valueFloat = Float.valueOf(this.value);
                }
                int iCompareTo2 = this.valueFloat.compareTo((Float) obj4);
                switch (AnonymousClass1.$SwitchMap$com$alibaba$fastjson$JSONPath$Operator[this.op.ordinal()]) {
                    case 1:
                        return iCompareTo2 == 0;
                    case 2:
                        return iCompareTo2 != 0;
                    case 3:
                        return iCompareTo2 <= 0;
                    case 4:
                        return iCompareTo2 < 0;
                    case 5:
                        return iCompareTo2 >= 0;
                    case 6:
                        return iCompareTo2 > 0;
                    default:
                        return false;
                }
            }
            if (obj4 instanceof Double) {
                if (this.valueDouble == null) {
                    this.valueDouble = Double.valueOf(this.value);
                }
                int iCompareTo3 = this.valueDouble.compareTo((Double) obj4);
                switch (AnonymousClass1.$SwitchMap$com$alibaba$fastjson$JSONPath$Operator[this.op.ordinal()]) {
                    case 1:
                        return iCompareTo3 == 0;
                    case 2:
                        return iCompareTo3 != 0;
                    case 3:
                        return iCompareTo3 <= 0;
                    case 4:
                        return iCompareTo3 < 0;
                    case 5:
                        return iCompareTo3 >= 0;
                    case 6:
                        return iCompareTo3 > 0;
                    default:
                        return false;
                }
            }
            long jLongExtractValue = TypeUtils.longExtractValue((Number) obj4);
            switch (AnonymousClass1.$SwitchMap$com$alibaba$fastjson$JSONPath$Operator[this.op.ordinal()]) {
                case 1:
                    return jLongExtractValue == this.value;
                case 2:
                    return jLongExtractValue != this.value;
                case 3:
                    return jLongExtractValue >= this.value;
                case 4:
                    return jLongExtractValue > this.value;
                case 5:
                    return jLongExtractValue <= this.value;
                case 6:
                    return jLongExtractValue < this.value;
                default:
                    return false;
            }
        }
    }

    /* JADX INFO: renamed from: com.alibaba.fastjson.JSONPath$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$alibaba$fastjson$JSONPath$Operator;

        static {
            int[] iArr = new int[Operator.values().length];
            $SwitchMap$com$alibaba$fastjson$JSONPath$Operator = iArr;
            try {
                iArr[Operator.EQ.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$alibaba$fastjson$JSONPath$Operator[Operator.NE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$alibaba$fastjson$JSONPath$Operator[Operator.GE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$alibaba$fastjson$JSONPath$Operator[Operator.GT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$alibaba$fastjson$JSONPath$Operator[Operator.LE.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$alibaba$fastjson$JSONPath$Operator[Operator.LT.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }

    static abstract class PropertyFilter implements Filter {
        static long TYPE = TypeUtils.fnv1a_64("type");
        protected final boolean function;
        protected Segment functionExpr;
        protected final String propertyName;
        protected final long propertyNameHash;

        protected PropertyFilter(String str, boolean z) {
            this.propertyName = str;
            long jFnv1a_64 = TypeUtils.fnv1a_64(str);
            this.propertyNameHash = jFnv1a_64;
            this.function = z;
            if (z) {
                if (jFnv1a_64 == TYPE) {
                    this.functionExpr = TypeSegment.instance;
                } else {
                    if (jFnv1a_64 == JSONPath.SIZE) {
                        this.functionExpr = SizeSegment.instance;
                        return;
                    }
                    throw new JSONPathException("unsupported funciton : " + str);
                }
            }
        }

        protected Object get(JSONPath jSONPath, Object obj, Object obj2) {
            Segment segment = this.functionExpr;
            if (segment != null) {
                return segment.eval(jSONPath, obj, obj2);
            }
            return jSONPath.getPropertyValue(obj2, this.propertyName, this.propertyNameHash);
        }
    }

    static class DoubleOpSegement extends PropertyFilter {
        private final Operator op;
        private final double value;

        public DoubleOpSegement(String str, boolean z, double d, Operator operator) {
            super(str, z);
            this.value = d;
            this.op = operator;
        }

        @Override // com.alibaba.fastjson.JSONPath.Filter
        public boolean apply(JSONPath jSONPath, Object obj, Object obj2, Object obj3) {
            Object obj4 = get(jSONPath, obj, obj3);
            if (obj4 == null || !(obj4 instanceof Number)) {
                return false;
            }
            double dDoubleValue = ((Number) obj4).doubleValue();
            switch (AnonymousClass1.$SwitchMap$com$alibaba$fastjson$JSONPath$Operator[this.op.ordinal()]) {
                case 1:
                    return dDoubleValue == this.value;
                case 2:
                    return dDoubleValue != this.value;
                case 3:
                    return dDoubleValue >= this.value;
                case 4:
                    return dDoubleValue > this.value;
                case 5:
                    return dDoubleValue <= this.value;
                case 6:
                    return dDoubleValue < this.value;
                default:
                    return false;
            }
        }
    }

    static class RefOpSegement extends PropertyFilter {
        private final Operator op;
        private final Segment refSgement;

        public RefOpSegement(String str, boolean z, Segment segment, Operator operator) {
            super(str, z);
            this.refSgement = segment;
            this.op = operator;
        }

        @Override // com.alibaba.fastjson.JSONPath.Filter
        public boolean apply(JSONPath jSONPath, Object obj, Object obj2, Object obj3) {
            Object obj4 = get(jSONPath, obj, obj3);
            if (obj4 == null || !(obj4 instanceof Number)) {
                return false;
            }
            Object objEval = this.refSgement.eval(jSONPath, obj, obj);
            if ((objEval instanceof Integer) || (objEval instanceof Long) || (objEval instanceof Short) || (objEval instanceof Byte)) {
                long jLongExtractValue = TypeUtils.longExtractValue((Number) objEval);
                if ((obj4 instanceof Integer) || (obj4 instanceof Long) || (obj4 instanceof Short) || (obj4 instanceof Byte)) {
                    long jLongExtractValue2 = TypeUtils.longExtractValue((Number) obj4);
                    switch (AnonymousClass1.$SwitchMap$com$alibaba$fastjson$JSONPath$Operator[this.op.ordinal()]) {
                        case 1:
                            return jLongExtractValue2 == jLongExtractValue;
                        case 2:
                            return jLongExtractValue2 != jLongExtractValue;
                        case 3:
                            return jLongExtractValue2 >= jLongExtractValue;
                        case 4:
                            return jLongExtractValue2 > jLongExtractValue;
                        case 5:
                            return jLongExtractValue2 <= jLongExtractValue;
                        case 6:
                            return jLongExtractValue2 < jLongExtractValue;
                    }
                }
                if (obj4 instanceof BigDecimal) {
                    int iCompareTo = BigDecimal.valueOf(jLongExtractValue).compareTo((BigDecimal) obj4);
                    switch (AnonymousClass1.$SwitchMap$com$alibaba$fastjson$JSONPath$Operator[this.op.ordinal()]) {
                        case 1:
                            return iCompareTo == 0;
                        case 2:
                            return iCompareTo != 0;
                        case 3:
                            return iCompareTo <= 0;
                        case 4:
                            return iCompareTo < 0;
                        case 5:
                            return iCompareTo >= 0;
                        case 6:
                            return iCompareTo > 0;
                        default:
                            return false;
                    }
                }
            }
            throw new UnsupportedOperationException();
        }
    }

    static class MatchSegement extends PropertyFilter {
        private final String[] containsValues;
        private final String endsWithValue;
        private final int minLength;
        private final boolean not;
        private final String startsWithValue;

        public MatchSegement(String str, boolean z, String str2, String str3, String[] strArr, boolean z2) {
            super(str, z);
            this.startsWithValue = str2;
            this.endsWithValue = str3;
            this.containsValues = strArr;
            this.not = z2;
            int length = str2 != null ? str2.length() + 0 : 0;
            length = str3 != null ? length + str3.length() : length;
            if (strArr != null) {
                for (String str4 : strArr) {
                    length += str4.length();
                }
            }
            this.minLength = length;
        }

        @Override // com.alibaba.fastjson.JSONPath.Filter
        public boolean apply(JSONPath jSONPath, Object obj, Object obj2, Object obj3) {
            int length;
            Object obj4 = get(jSONPath, obj, obj3);
            if (obj4 == null) {
                return false;
            }
            String string = obj4.toString();
            if (string.length() < this.minLength) {
                return this.not;
            }
            String str = this.startsWithValue;
            if (str == null) {
                length = 0;
            } else {
                if (!string.startsWith(str)) {
                    return this.not;
                }
                length = this.startsWithValue.length() + 0;
            }
            String[] strArr = this.containsValues;
            if (strArr != null) {
                for (String str2 : strArr) {
                    int iIndexOf = string.indexOf(str2, length);
                    if (iIndexOf == -1) {
                        return this.not;
                    }
                    length = iIndexOf + str2.length();
                }
            }
            String str3 = this.endsWithValue;
            if (str3 != null && !string.endsWith(str3)) {
                return this.not;
            }
            return !this.not;
        }
    }

    static class RlikeSegement extends PropertyFilter {
        private final boolean not;
        private final Pattern pattern;

        public RlikeSegement(String str, boolean z, String str2, boolean z2) {
            super(str, z);
            this.pattern = Pattern.compile(str2);
            this.not = z2;
        }

        @Override // com.alibaba.fastjson.JSONPath.Filter
        public boolean apply(JSONPath jSONPath, Object obj, Object obj2, Object obj3) {
            Object obj4 = get(jSONPath, obj, obj3);
            if (obj4 == null) {
                return false;
            }
            boolean zMatches = this.pattern.matcher(obj4.toString()).matches();
            return this.not ? !zMatches : zMatches;
        }
    }

    static class StringOpSegement extends PropertyFilter {
        private final Operator op;
        private final String value;

        public StringOpSegement(String str, boolean z, String str2, Operator operator) {
            super(str, z);
            this.value = str2;
            this.op = operator;
        }

        @Override // com.alibaba.fastjson.JSONPath.Filter
        public boolean apply(JSONPath jSONPath, Object obj, Object obj2, Object obj3) {
            Object obj4 = get(jSONPath, obj, obj3);
            if (this.op == Operator.EQ) {
                return this.value.equals(obj4);
            }
            if (this.op == Operator.NE) {
                return !this.value.equals(obj4);
            }
            if (obj4 == null) {
                return false;
            }
            int iCompareTo = this.value.compareTo(obj4.toString());
            if (this.op == Operator.GE) {
                return iCompareTo <= 0;
            }
            if (this.op == Operator.GT) {
                return iCompareTo < 0;
            }
            if (this.op == Operator.LE) {
                return iCompareTo >= 0;
            }
            return this.op == Operator.LT && iCompareTo > 0;
        }
    }

    static class RegMatchSegement extends PropertyFilter {
        private final Operator op;
        private final Pattern pattern;

        public RegMatchSegement(String str, boolean z, Pattern pattern, Operator operator) {
            super(str, z);
            this.pattern = pattern;
            this.op = operator;
        }

        @Override // com.alibaba.fastjson.JSONPath.Filter
        public boolean apply(JSONPath jSONPath, Object obj, Object obj2, Object obj3) {
            Object obj4 = get(jSONPath, obj, obj3);
            if (obj4 == null) {
                return false;
            }
            return this.pattern.matcher(obj4.toString()).matches();
        }
    }

    public static class FilterSegment implements Segment {
        private final Filter filter;

        public FilterSegment(Filter filter) {
            this.filter = filter;
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public Object eval(JSONPath jSONPath, Object obj, Object obj2) {
            if (obj2 == null) {
                return null;
            }
            JSONArray jSONArray = new JSONArray();
            if (obj2 instanceof Iterable) {
                for (Object obj3 : (Iterable) obj2) {
                    if (this.filter.apply(jSONPath, obj, obj2, obj3)) {
                        jSONArray.add(obj3);
                    }
                }
                return jSONArray;
            }
            if (this.filter.apply(jSONPath, obj, obj2, obj2)) {
                return obj2;
            }
            return null;
        }

        @Override // com.alibaba.fastjson.JSONPath.Segment
        public void extract(JSONPath jSONPath, DefaultJSONParser defaultJSONParser, Context context) {
            Object obj = defaultJSONParser.parse();
            context.object = eval(jSONPath, obj, obj);
        }

        public boolean remove(JSONPath jSONPath, Object obj, Object obj2) {
            if (obj2 == null || !(obj2 instanceof Iterable)) {
                return false;
            }
            Iterator it = ((Iterable) obj2).iterator();
            while (it.hasNext()) {
                if (this.filter.apply(jSONPath, obj, obj2, it.next())) {
                    it.remove();
                }
            }
            return true;
        }
    }

    static class FilterGroup implements Filter {
        private boolean and;
        private List<Filter> fitlers;

        public FilterGroup(Filter filter, Filter filter2, boolean z) {
            ArrayList arrayList = new ArrayList(2);
            this.fitlers = arrayList;
            arrayList.add(filter);
            this.fitlers.add(filter2);
            this.and = z;
        }

        @Override // com.alibaba.fastjson.JSONPath.Filter
        public boolean apply(JSONPath jSONPath, Object obj, Object obj2, Object obj3) {
            if (this.and) {
                Iterator<Filter> it = this.fitlers.iterator();
                while (it.hasNext()) {
                    if (!it.next().apply(jSONPath, obj, obj2, obj3)) {
                        return false;
                    }
                }
                return true;
            }
            Iterator<Filter> it2 = this.fitlers.iterator();
            while (it2.hasNext()) {
                if (it2.next().apply(jSONPath, obj, obj2, obj3)) {
                    return true;
                }
            }
            return false;
        }
    }

    protected Object getArrayItem(Object obj, int i) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof List) {
            List list = (List) obj;
            if (i >= 0) {
                if (i < list.size()) {
                    return list.get(i);
                }
                return null;
            }
            if (Math.abs(i) <= list.size()) {
                return list.get(list.size() + i);
            }
            return null;
        }
        if (obj.getClass().isArray()) {
            int length = Array.getLength(obj);
            if (i >= 0) {
                if (i < length) {
                    return Array.get(obj, i);
                }
                return null;
            }
            if (Math.abs(i) <= length) {
                return Array.get(obj, length + i);
            }
            return null;
        }
        if (obj instanceof Map) {
            Map map = (Map) obj;
            Object obj2 = map.get(Integer.valueOf(i));
            return obj2 == null ? map.get(Integer.toString(i)) : obj2;
        }
        if (!(obj instanceof Collection)) {
            if (i == 0) {
                return obj;
            }
            throw new UnsupportedOperationException();
        }
        int i2 = 0;
        for (Object obj3 : (Collection) obj) {
            if (i2 == i) {
                return obj3;
            }
            i2++;
        }
        return null;
    }

    public boolean setArrayItem(JSONPath jSONPath, Object obj, int i, Object obj2) {
        if (obj instanceof List) {
            List list = (List) obj;
            if (i >= 0) {
                list.set(i, obj2);
            } else {
                list.set(list.size() + i, obj2);
            }
            return true;
        }
        Class<?> cls = obj.getClass();
        if (cls.isArray()) {
            int length = Array.getLength(obj);
            if (i >= 0) {
                if (i < length) {
                    Array.set(obj, i, obj2);
                }
            } else if (Math.abs(i) <= length) {
                Array.set(obj, length + i, obj2);
            }
            return true;
        }
        throw new JSONPathException("unsupported set operation." + cls);
    }

    public boolean removeArrayItem(JSONPath jSONPath, Object obj, int i) {
        if (obj instanceof List) {
            List list = (List) obj;
            if (i >= 0) {
                if (i >= list.size()) {
                    return false;
                }
                list.remove(i);
                return true;
            }
            int size = list.size() + i;
            if (size < 0) {
                return false;
            }
            list.remove(size);
            return true;
        }
        throw new JSONPathException("unsupported set operation." + obj.getClass());
    }

    protected Collection<Object> getPropertyValues(Object obj) {
        if (obj == null) {
            return null;
        }
        JavaBeanSerializer javaBeanSerializer = getJavaBeanSerializer(obj.getClass());
        if (javaBeanSerializer != null) {
            try {
                return javaBeanSerializer.getFieldValues(obj);
            } catch (Exception e) {
                throw new JSONPathException("jsonpath error, path " + this.path, e);
            }
        }
        if (obj instanceof Map) {
            return ((Map) obj).values();
        }
        if (obj instanceof Collection) {
            return (Collection) obj;
        }
        throw new UnsupportedOperationException();
    }

    protected void deepGetObjects(Object obj, List<Object> list) {
        Collection fieldValues;
        Class<?> cls = obj.getClass();
        JavaBeanSerializer javaBeanSerializer = getJavaBeanSerializer(cls);
        if (javaBeanSerializer != null) {
            try {
                fieldValues = javaBeanSerializer.getFieldValues(obj);
                list.add(obj);
            } catch (Exception e) {
                throw new JSONPathException("jsonpath error, path " + this.path, e);
            }
        } else if (obj instanceof Map) {
            list.add(obj);
            fieldValues = ((Map) obj).values();
        } else {
            fieldValues = obj instanceof Collection ? (Collection) obj : null;
        }
        if (fieldValues != null) {
            for (Object obj2 : fieldValues) {
                if (obj2 != null && !ParserConfig.isPrimitive2(obj2.getClass())) {
                    deepGetObjects(obj2, list);
                }
            }
            return;
        }
        throw new UnsupportedOperationException(cls.getName());
    }

    protected void deepGetPropertyValues(Object obj, List<Object> list) {
        Collection fieldValues;
        Class<?> cls = obj.getClass();
        JavaBeanSerializer javaBeanSerializer = getJavaBeanSerializer(cls);
        if (javaBeanSerializer != null) {
            try {
                fieldValues = javaBeanSerializer.getFieldValues(obj);
            } catch (Exception e) {
                throw new JSONPathException("jsonpath error, path " + this.path, e);
            }
        } else if (obj instanceof Map) {
            fieldValues = ((Map) obj).values();
        } else {
            fieldValues = obj instanceof Collection ? (Collection) obj : null;
        }
        if (fieldValues != null) {
            for (Object obj2 : fieldValues) {
                if (obj2 == null || ParserConfig.isPrimitive2(obj2.getClass())) {
                    list.add(obj2);
                } else {
                    deepGetPropertyValues(obj2, list);
                }
            }
            return;
        }
        throw new UnsupportedOperationException(cls.getName());
    }

    static boolean eq(Object obj, Object obj2) {
        if (obj == obj2) {
            return true;
        }
        if (obj == null || obj2 == null) {
            return false;
        }
        if (obj.getClass() == obj2.getClass()) {
            return obj.equals(obj2);
        }
        if (obj instanceof Number) {
            if (obj2 instanceof Number) {
                return eqNotNull((Number) obj, (Number) obj2);
            }
            return false;
        }
        return obj.equals(obj2);
    }

    static boolean eqNotNull(Number number, Number number2) {
        Class<?> cls = number.getClass();
        boolean zIsInt = isInt(cls);
        Class<?> cls2 = number2.getClass();
        boolean zIsInt2 = isInt(cls2);
        if (number instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) number;
            if (zIsInt2) {
                return bigDecimal.equals(BigDecimal.valueOf(TypeUtils.longExtractValue(number2)));
            }
        }
        if (zIsInt) {
            if (zIsInt2) {
                return number.longValue() == number2.longValue();
            }
            if (number2 instanceof BigInteger) {
                return BigInteger.valueOf(number.longValue()).equals((BigInteger) number);
            }
        }
        if (zIsInt2 && (number instanceof BigInteger)) {
            return ((BigInteger) number).equals(BigInteger.valueOf(TypeUtils.longExtractValue(number2)));
        }
        boolean zIsDouble = isDouble(cls);
        boolean zIsDouble2 = isDouble(cls2);
        return ((zIsDouble && zIsDouble2) || ((zIsDouble && zIsInt2) || (zIsDouble2 && zIsInt))) && number.doubleValue() == number2.doubleValue();
    }

    protected static boolean isDouble(Class<?> cls) {
        return cls == Float.class || cls == Double.class;
    }

    protected static boolean isInt(Class<?> cls) {
        return cls == Byte.class || cls == Short.class || cls == Integer.class || cls == Long.class;
    }

    protected Object getPropertyValue(Object obj, String str, long j) {
        Object obj2;
        JSONArray jSONArray = null;
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            try {
                obj2 = (JSONObject) JSON.parse((String) obj, this.parserConfig);
            } catch (Exception unused) {
                obj2 = obj;
            }
        } else {
            obj2 = obj;
        }
        if (obj2 instanceof Map) {
            Map map = (Map) obj2;
            Object obj3 = map.get(str);
            if (obj3 == null) {
                return (SIZE == j || LENGTH == j) ? Integer.valueOf(map.size()) : obj3;
            }
            return obj3;
        }
        JavaBeanSerializer javaBeanSerializer = getJavaBeanSerializer(obj2.getClass());
        if (javaBeanSerializer != null) {
            try {
                return javaBeanSerializer.getFieldValue(obj2, str, j, false);
            } catch (Exception e) {
                throw new JSONPathException("jsonpath error, path " + this.path + ", segement " + str, e);
            }
        }
        int i = 0;
        if (obj2 instanceof List) {
            List list = (List) obj2;
            if (SIZE == j || LENGTH == j) {
                return Integer.valueOf(list.size());
            }
            while (i < list.size()) {
                Object obj4 = list.get(i);
                if (obj4 == list) {
                    if (jSONArray == null) {
                        jSONArray = new JSONArray(list.size());
                    }
                    jSONArray.add(obj4);
                } else {
                    Object propertyValue = getPropertyValue(obj4, str, j);
                    if (propertyValue instanceof Collection) {
                        Collection collection = (Collection) propertyValue;
                        if (jSONArray == null) {
                            jSONArray = new JSONArray(list.size());
                        }
                        jSONArray.addAll(collection);
                    } else if (propertyValue != null || !this.ignoreNullValue) {
                        if (jSONArray == null) {
                            jSONArray = new JSONArray(list.size());
                        }
                        jSONArray.add(propertyValue);
                    }
                }
                i++;
            }
            return jSONArray == null ? Collections.emptyList() : jSONArray;
        }
        if (obj2 instanceof Object[]) {
            Object[] objArr = (Object[]) obj2;
            if (SIZE == j || LENGTH == j) {
                return Integer.valueOf(objArr.length);
            }
            JSONArray jSONArray2 = new JSONArray(objArr.length);
            while (i < objArr.length) {
                Object[] objArr2 = objArr[i];
                if (objArr2 == objArr) {
                    jSONArray2.add(objArr2);
                } else {
                    Object propertyValue2 = getPropertyValue(objArr2, str, j);
                    if (propertyValue2 instanceof Collection) {
                        jSONArray2.addAll((Collection) propertyValue2);
                    } else if (propertyValue2 != null || !this.ignoreNullValue) {
                        jSONArray2.add(propertyValue2);
                    }
                }
                i++;
            }
            return jSONArray2;
        }
        if (obj2 instanceof Enum) {
            Enum r9 = (Enum) obj2;
            if (-4270347329889690746L == j) {
                return r9.name();
            }
            if (-1014497654951707614L == j) {
                return Integer.valueOf(r9.ordinal());
            }
        }
        if (obj2 instanceof Calendar) {
            Calendar calendar = (Calendar) obj2;
            if (8963398325558730460L == j) {
                return Integer.valueOf(calendar.get(1));
            }
            if (-811277319855450459L == j) {
                return Integer.valueOf(calendar.get(2));
            }
            if (-3851359326990528739L == j) {
                return Integer.valueOf(calendar.get(5));
            }
            if (4647432019745535567L == j) {
                return Integer.valueOf(calendar.get(11));
            }
            if (6607618197526598121L == j) {
                return Integer.valueOf(calendar.get(12));
            }
            if (-6586085717218287427L == j) {
                return Integer.valueOf(calendar.get(13));
            }
        }
        return null;
    }

    protected void deepScan(Object obj, String str, List<Object> list) {
        if (obj == null) {
            return;
        }
        if (obj instanceof Map) {
            for (Map.Entry entry : ((Map) obj).entrySet()) {
                Object value = entry.getValue();
                if (str.equals(entry.getKey())) {
                    if (value instanceof Collection) {
                        list.addAll((Collection) value);
                    } else {
                        list.add(value);
                    }
                } else if (value != null && !ParserConfig.isPrimitive2(value.getClass())) {
                    deepScan(value, str, list);
                }
            }
            return;
        }
        if (obj instanceof Collection) {
            for (Object obj2 : (Collection) obj) {
                if (!ParserConfig.isPrimitive2(obj2.getClass())) {
                    deepScan(obj2, str, list);
                }
            }
            return;
        }
        JavaBeanSerializer javaBeanSerializer = getJavaBeanSerializer(obj.getClass());
        if (javaBeanSerializer != null) {
            try {
                FieldSerializer fieldSerializer = javaBeanSerializer.getFieldSerializer(str);
                if (fieldSerializer != null) {
                    try {
                        list.add(fieldSerializer.getPropertyValueDirect(obj));
                        return;
                    } catch (IllegalAccessException e) {
                        throw new JSONException("getFieldValue error." + str, e);
                    } catch (InvocationTargetException e2) {
                        throw new JSONException("getFieldValue error." + str, e2);
                    }
                }
                Iterator<Object> it = javaBeanSerializer.getFieldValues(obj).iterator();
                while (it.hasNext()) {
                    deepScan(it.next(), str, list);
                }
                return;
            } catch (Exception e3) {
                throw new JSONPathException("jsonpath error, path " + this.path + ", segement " + str, e3);
            }
        }
        if (obj instanceof List) {
            List list2 = (List) obj;
            for (int i = 0; i < list2.size(); i++) {
                deepScan(list2.get(i), str, list);
            }
        }
    }

    protected void deepSet(Object obj, String str, long j, Object obj2) {
        if (obj == null) {
            return;
        }
        if (obj instanceof Map) {
            Map map = (Map) obj;
            if (map.containsKey(str)) {
                map.get(str);
                map.put(str, obj2);
                return;
            } else {
                Iterator it = map.values().iterator();
                while (it.hasNext()) {
                    deepSet(it.next(), str, j, obj2);
                }
                return;
            }
        }
        Class<?> cls = obj.getClass();
        JavaBeanDeserializer javaBeanDeserializer = getJavaBeanDeserializer(cls);
        if (javaBeanDeserializer != null) {
            try {
                FieldDeserializer fieldDeserializer = javaBeanDeserializer.getFieldDeserializer(str);
                if (fieldDeserializer != null) {
                    fieldDeserializer.setValue(obj, obj2);
                    return;
                }
                Iterator<Object> it2 = getJavaBeanSerializer(cls).getObjectFieldValues(obj).iterator();
                while (it2.hasNext()) {
                    deepSet(it2.next(), str, j, obj2);
                }
                return;
            } catch (Exception e) {
                throw new JSONPathException("jsonpath error, path " + this.path + ", segement " + str, e);
            }
        }
        if (obj instanceof List) {
            List list = (List) obj;
            for (int i = 0; i < list.size(); i++) {
                deepSet(list.get(i), str, j, obj2);
            }
        }
    }

    protected boolean setPropertyValue(Object obj, String str, long j, Object obj2) {
        if (obj instanceof Map) {
            ((Map) obj).put(str, obj2);
            return true;
        }
        if (obj instanceof List) {
            for (Object obj3 : (List) obj) {
                if (obj3 != null) {
                    setPropertyValue(obj3, str, j, obj2);
                }
            }
            return true;
        }
        ObjectDeserializer deserializer = this.parserConfig.getDeserializer(obj.getClass());
        JavaBeanDeserializer javaBeanDeserializer = deserializer instanceof JavaBeanDeserializer ? (JavaBeanDeserializer) deserializer : null;
        if (javaBeanDeserializer != null) {
            FieldDeserializer fieldDeserializer = javaBeanDeserializer.getFieldDeserializer(j);
            if (fieldDeserializer == null) {
                return false;
            }
            if (obj2 != null && obj2.getClass() != fieldDeserializer.fieldInfo.fieldClass) {
                obj2 = TypeUtils.cast(obj2, fieldDeserializer.fieldInfo.fieldType, this.parserConfig);
            }
            fieldDeserializer.setValue(obj, obj2);
            return true;
        }
        throw new UnsupportedOperationException();
    }

    protected boolean removePropertyValue(Object obj, String str, boolean z) {
        boolean z2 = true;
        if (obj instanceof Map) {
            Map map = (Map) obj;
            z2 = map.remove(str) != null;
            if (z) {
                Iterator it = map.values().iterator();
                while (it.hasNext()) {
                    removePropertyValue(it.next(), str, z);
                }
            }
            return z2;
        }
        ObjectDeserializer deserializer = this.parserConfig.getDeserializer(obj.getClass());
        JavaBeanDeserializer javaBeanDeserializer = deserializer instanceof JavaBeanDeserializer ? (JavaBeanDeserializer) deserializer : null;
        if (javaBeanDeserializer == null) {
            if (z) {
                return false;
            }
            throw new UnsupportedOperationException();
        }
        FieldDeserializer fieldDeserializer = javaBeanDeserializer.getFieldDeserializer(str);
        if (fieldDeserializer != null) {
            fieldDeserializer.setValue(obj, (String) null);
        } else {
            z2 = false;
        }
        if (z) {
            for (Object obj2 : getPropertyValues(obj)) {
                if (obj2 != null) {
                    removePropertyValue(obj2, str, z);
                }
            }
        }
        return z2;
    }

    protected JavaBeanSerializer getJavaBeanSerializer(Class<?> cls) {
        ObjectSerializer objectWriter = this.serializeConfig.getObjectWriter(cls);
        if (objectWriter instanceof JavaBeanSerializer) {
            return (JavaBeanSerializer) objectWriter;
        }
        return null;
    }

    protected JavaBeanDeserializer getJavaBeanDeserializer(Class<?> cls) {
        ObjectDeserializer deserializer = this.parserConfig.getDeserializer(cls);
        if (deserializer instanceof JavaBeanDeserializer) {
            return (JavaBeanDeserializer) deserializer;
        }
        return null;
    }

    int evalSize(Object obj) {
        if (obj == null) {
            return -1;
        }
        if (obj instanceof Collection) {
            return ((Collection) obj).size();
        }
        if (obj instanceof Object[]) {
            return ((Object[]) obj).length;
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj);
        }
        if (obj instanceof Map) {
            int i = 0;
            Iterator it = ((Map) obj).values().iterator();
            while (it.hasNext()) {
                if (it.next() != null) {
                    i++;
                }
            }
            return i;
        }
        JavaBeanSerializer javaBeanSerializer = getJavaBeanSerializer(obj.getClass());
        if (javaBeanSerializer == null) {
            return -1;
        }
        try {
            return javaBeanSerializer.getSize(obj);
        } catch (Exception e) {
            throw new JSONPathException("evalSize error : " + this.path, e);
        }
    }

    Set<?> evalKeySet(Object obj) {
        JavaBeanSerializer javaBeanSerializer;
        if (obj == null) {
            return null;
        }
        if (obj instanceof Map) {
            return ((Map) obj).keySet();
        }
        if ((obj instanceof Collection) || (obj instanceof Object[]) || obj.getClass().isArray() || (javaBeanSerializer = getJavaBeanSerializer(obj.getClass())) == null) {
            return null;
        }
        try {
            return javaBeanSerializer.getFieldNames(obj);
        } catch (Exception e) {
            throw new JSONPathException("evalKeySet error : " + this.path, e);
        }
    }

    @Override // com.alibaba.fastjson.JSONAware
    public String toJSONString() {
        return JSON.toJSONString(this.path);
    }

    public static Object reserveToArray(Object obj, String... strArr) {
        JSONArray jSONArray = new JSONArray();
        if (strArr != null && strArr.length != 0) {
            for (String str : strArr) {
                JSONPath jSONPathCompile = compile(str);
                jSONPathCompile.init();
                jSONArray.add(jSONPathCompile.eval(obj));
            }
        }
        return jSONArray;
    }

    public static Object reserveToObject(Object obj, String... strArr) {
        Object objEval;
        if (strArr == null || strArr.length == 0) {
            return obj;
        }
        JSONObject jSONObject = new JSONObject(true);
        for (String str : strArr) {
            JSONPath jSONPathCompile = compile(str);
            jSONPathCompile.init();
            Segment[] segmentArr = jSONPathCompile.segments;
            if ((segmentArr[segmentArr.length - 1] instanceof PropertySegment) && (objEval = jSONPathCompile.eval(obj)) != null) {
                jSONPathCompile.set(jSONObject, objEval);
            }
        }
        return jSONObject;
    }
}
