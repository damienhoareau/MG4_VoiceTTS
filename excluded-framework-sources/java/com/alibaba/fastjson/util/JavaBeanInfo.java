package com.alibaba.fastjson.util;

import android.provider.ContactsContract;
import android.provider.Settings;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONPOJOBuilder;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/* JADX INFO: loaded from: classes3.dex */
public class JavaBeanInfo {
    public final Method buildMethod;
    public final Class<?> builderClass;
    public final Class<?> clazz;
    public final Constructor<?> creatorConstructor;
    public Type[] creatorConstructorParameterTypes;
    public String[] creatorConstructorParameters;
    public final Constructor<?> defaultConstructor;
    public final int defaultConstructorParameterSize;
    public final Method factoryMethod;
    public final FieldInfo[] fields;
    public final JSONType jsonType;
    public boolean kotlin;
    public Constructor<?> kotlinDefaultConstructor;
    public String[] orders;
    public final int parserFeatures;
    public final FieldInfo[] sortedFields;
    public final String typeKey;
    public final String typeName;

    public JavaBeanInfo(Class<?> cls, Class<?> cls2, Constructor<?> constructor, Constructor<?> constructor2, Method method, Method method2, JSONType jSONType, List<FieldInfo> list) {
        JSONField jSONField;
        this.clazz = cls;
        this.builderClass = cls2;
        this.defaultConstructor = constructor;
        this.creatorConstructor = constructor2;
        this.factoryMethod = method;
        this.parserFeatures = TypeUtils.getParserFeatures(cls);
        this.buildMethod = method2;
        this.jsonType = jSONType;
        if (jSONType != null) {
            String strTypeName = jSONType.typeName();
            String strTypeKey = jSONType.typeKey();
            this.typeKey = strTypeKey.length() <= 0 ? null : strTypeKey;
            if (strTypeName.length() != 0) {
                this.typeName = strTypeName;
            } else {
                this.typeName = cls.getName();
            }
            String[] strArrOrders = jSONType.orders();
            this.orders = strArrOrders.length == 0 ? null : strArrOrders;
        } else {
            this.typeName = cls.getName();
            this.typeKey = null;
            this.orders = null;
        }
        FieldInfo[] fieldInfoArr = new FieldInfo[list.size()];
        this.fields = fieldInfoArr;
        list.toArray(fieldInfoArr);
        FieldInfo[] fieldInfoArr2 = this.fields;
        FieldInfo[] fieldInfoArr3 = new FieldInfo[fieldInfoArr2.length];
        boolean z = false;
        if (this.orders != null) {
            LinkedHashMap linkedHashMap = new LinkedHashMap(list.size());
            for (FieldInfo fieldInfo : this.fields) {
                linkedHashMap.put(fieldInfo.name, fieldInfo);
            }
            int i = 0;
            for (String str : this.orders) {
                FieldInfo fieldInfo2 = (FieldInfo) linkedHashMap.get(str);
                if (fieldInfo2 != null) {
                    fieldInfoArr3[i] = fieldInfo2;
                    linkedHashMap.remove(str);
                    i++;
                }
            }
            Iterator it = linkedHashMap.values().iterator();
            while (it.hasNext()) {
                fieldInfoArr3[i] = (FieldInfo) it.next();
                i++;
            }
        } else {
            System.arraycopy(fieldInfoArr2, 0, fieldInfoArr3, 0, fieldInfoArr2.length);
            Arrays.sort(fieldInfoArr3);
        }
        this.sortedFields = Arrays.equals(this.fields, fieldInfoArr3) ? this.fields : fieldInfoArr3;
        if (constructor != null) {
            this.defaultConstructorParameterSize = constructor.getParameterTypes().length;
        } else if (method != null) {
            this.defaultConstructorParameterSize = method.getParameterTypes().length;
        } else {
            this.defaultConstructorParameterSize = 0;
        }
        if (constructor2 != null) {
            this.creatorConstructorParameterTypes = constructor2.getParameterTypes();
            boolean zIsKotlin = TypeUtils.isKotlin(cls);
            this.kotlin = zIsKotlin;
            if (zIsKotlin) {
                this.creatorConstructorParameters = TypeUtils.getKoltinConstructorParameters(cls);
                try {
                    this.kotlinDefaultConstructor = cls.getConstructor(new Class[0]);
                } catch (Throwable unused) {
                }
                Annotation[][] parameterAnnotations = TypeUtils.getParameterAnnotations(constructor2);
                for (int i2 = 0; i2 < this.creatorConstructorParameters.length && i2 < parameterAnnotations.length; i2++) {
                    Annotation[] annotationArr = parameterAnnotations[i2];
                    int length = annotationArr.length;
                    int i3 = 0;
                    while (true) {
                        if (i3 >= length) {
                            jSONField = null;
                            break;
                        }
                        Annotation annotation = annotationArr[i3];
                        if (annotation instanceof JSONField) {
                            jSONField = (JSONField) annotation;
                            break;
                        }
                        i3++;
                    }
                    if (jSONField != null) {
                        String strName = jSONField.name();
                        if (strName.length() > 0) {
                            this.creatorConstructorParameters[i2] = strName;
                        }
                    }
                }
                return;
            }
            if (this.creatorConstructorParameterTypes.length == this.fields.length) {
                int i4 = 0;
                while (true) {
                    Type[] typeArr = this.creatorConstructorParameterTypes;
                    if (i4 >= typeArr.length) {
                        z = true;
                        break;
                    } else if (typeArr[i4] != this.fields[i4].fieldClass) {
                        break;
                    } else {
                        i4++;
                    }
                }
            }
            if (z) {
                return;
            }
            this.creatorConstructorParameters = ASMUtils.lookupParameterNames(constructor2);
        }
    }

    private static FieldInfo getField(List<FieldInfo> list, String str) {
        for (FieldInfo fieldInfo : list) {
            if (fieldInfo.name.equals(str)) {
                return fieldInfo;
            }
            Field field = fieldInfo.field;
            if (field != null && fieldInfo.getAnnotation() != null && field.getName().equals(str)) {
                return fieldInfo;
            }
        }
        return null;
    }

    static boolean add(List<FieldInfo> list, FieldInfo fieldInfo) {
        for (int size = list.size() - 1; size >= 0; size--) {
            FieldInfo fieldInfo2 = list.get(size);
            if (fieldInfo2.name.equals(fieldInfo.name) && (!fieldInfo2.getOnly || fieldInfo.getOnly)) {
                if (fieldInfo2.fieldClass.isAssignableFrom(fieldInfo.fieldClass)) {
                    list.set(size, fieldInfo);
                    return true;
                }
                if (fieldInfo2.compareTo(fieldInfo) >= 0) {
                    return false;
                }
                list.set(size, fieldInfo);
                return true;
            }
        }
        list.add(fieldInfo);
        return true;
    }

    public static JavaBeanInfo build(Class<?> cls, Type type, PropertyNamingStrategy propertyNamingStrategy) {
        return build(cls, type, propertyNamingStrategy, false, TypeUtils.compatibleWithJavaBean, false);
    }

    private static Map<TypeVariable, Type> buildGenericInfo(Class<?> cls) {
        Class<? super Object> superclass = cls.getSuperclass();
        HashMap map = null;
        if (superclass == null) {
            return null;
        }
        while (true) {
            Class<? super Object> cls2 = superclass;
            Class<?> cls3 = cls;
            cls = cls2;
            if (cls == null || cls == Object.class) {
                break;
            }
            if (cls3.getGenericSuperclass() instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) cls3.getGenericSuperclass()).getActualTypeArguments();
                TypeVariable<Class<?>>[] typeParameters = cls.getTypeParameters();
                for (int i = 0; i < actualTypeArguments.length; i++) {
                    if (map == null) {
                        map = new HashMap();
                    }
                    if (map.containsKey(actualTypeArguments[i])) {
                        map.put(typeParameters[i], map.get(actualTypeArguments[i]));
                    } else {
                        map.put(typeParameters[i], actualTypeArguments[i]);
                    }
                }
            }
            superclass = cls.getSuperclass();
        }
        return map;
    }

    public static JavaBeanInfo build(Class<?> cls, Type type, PropertyNamingStrategy propertyNamingStrategy, boolean z, boolean z2) {
        return build(cls, type, propertyNamingStrategy, z, z2, false);
    }

    /* JADX WARN: Code duplicated, block: B:156:0x02ee A[PHI: r8
  0x02ee: PHI (r8v37 boolean) = (r8v32 boolean), (r8v32 boolean), (r8v40 boolean) binds: [B:147:0x02c5, B:149:0x02cb, B:137:0x0298] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:184:0x0341  */
    /* JADX WARN: Code duplicated, block: B:185:0x0347  */
    /* JADX WARN: Code duplicated, block: B:238:0x0475  */
    /* JADX WARN: Code duplicated, block: B:303:0x0642  */
    /* JADX WARN: Code duplicated, block: B:305:0x0656  */
    /* JADX WARN: Code duplicated, block: B:306:0x066c  */
    /* JADX WARN: Code duplicated, block: B:316:0x0693  */
    /* JADX WARN: Code duplicated, block: B:318:0x0697  */
    /* JADX WARN: Code duplicated, block: B:319:0x06ad  */
    /* JADX WARN: Code duplicated, block: B:321:0x06b9  */
    /* JADX WARN: Code duplicated, block: B:330:0x06fe  */
    /* JADX WARN: Code duplicated, block: B:332:0x0708  */
    /* JADX WARN: Code duplicated, block: B:335:0x0718  */
    /* JADX WARN: Code duplicated, block: B:337:0x071d  */
    /* JADX WARN: Code duplicated, block: B:339:0x0725  */
    /* JADX WARN: Code duplicated, block: B:342:0x072e  */
    /* JADX WARN: Code duplicated, block: B:343:0x0730  */
    /* JADX WARN: Code duplicated, block: B:346:0x0737  */
    /* JADX WARN: Code duplicated, block: B:348:0x0755  */
    /* JADX WARN: Code duplicated, block: B:349:0x077b  */
    /* JADX WARN: Code duplicated, block: B:352:0x0781  */
    /* JADX WARN: Code duplicated, block: B:357:0x0794 A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:361:0x079e  */
    /* JADX WARN: Code duplicated, block: B:364:0x07a7  */
    /* JADX WARN: Code duplicated, block: B:366:0x07b3  */
    /* JADX WARN: Code duplicated, block: B:368:0x07bf  */
    /* JADX WARN: Code duplicated, block: B:371:0x07cb  */
    /* JADX WARN: Code duplicated, block: B:401:0x086c  */
    /* JADX WARN: Code duplicated, block: B:403:0x0872  */
    /* JADX WARN: Code duplicated, block: B:405:0x088d  */
    /* JADX WARN: Code duplicated, block: B:407:0x0891  */
    /* JADX WARN: Code duplicated, block: B:408:0x089a  */
    /* JADX WARN: Code duplicated, block: B:410:0x08a1  */
    /* JADX WARN: Code duplicated, block: B:412:0x08a7  */
    /* JADX WARN: Code duplicated, block: B:414:0x08ae  */
    /* JADX WARN: Code duplicated, block: B:415:0x08d3  */
    /* JADX WARN: Code duplicated, block: B:416:0x08d5  */
    /* JADX WARN: Code duplicated, block: B:418:0x08d9  */
    /* JADX WARN: Code duplicated, block: B:420:0x08e5  */
    /* JADX WARN: Code duplicated, block: B:422:0x08eb  */
    /* JADX WARN: Code duplicated, block: B:424:0x08fb  */
    /* JADX WARN: Code duplicated, block: B:426:0x0919  */
    /* JADX WARN: Code duplicated, block: B:427:0x0944  */
    /* JADX WARN: Code duplicated, block: B:428:0x0951  */
    /* JADX WARN: Code duplicated, block: B:430:0x0961  */
    /* JADX WARN: Code duplicated, block: B:433:0x0974  */
    /* JADX WARN: Code duplicated, block: B:438:0x09c2  */
    /* JADX WARN: Code duplicated, block: B:472:0x0a60  */
    /* JADX WARN: Code duplicated, block: B:474:0x0a6c  */
    /* JADX WARN: Code duplicated, block: B:476:0x0a76  */
    /* JADX WARN: Code duplicated, block: B:481:0x0a89  */
    /* JADX WARN: Code duplicated, block: B:484:0x0a97  */
    /* JADX WARN: Code duplicated, block: B:486:0x0a9b  */
    /* JADX WARN: Code duplicated, block: B:490:0x0ab8  */
    /* JADX WARN: Code duplicated, block: B:495:0x0b21  */
    /* JADX WARN: Code duplicated, block: B:498:0x0b28  */
    /* JADX WARN: Code duplicated, block: B:500:0x0b2c  */
    /* JADX WARN: Code duplicated, block: B:502:0x0b2f A[LOOP:7: B:501:0x0b2d->B:502:0x0b2f, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:526:0x09a4 A[EDGE_INSN: B:526:0x09a4->B:436:0x09a4 BREAK  A[LOOP:4: B:301:0x063d->B:435:0x0992], SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:534:0x07bc A[SYNTHETIC] */
    /* JADX WARN: Instruction removed from duplicated block: B:403:0x0872, please report this as an issue */
    public static JavaBeanInfo build(Class<?> cls, Type type, PropertyNamingStrategy propertyNamingStrategy, boolean z, boolean z2, boolean z3) {
        Constructor<?> defaultConstructor;
        Constructor<?> constructor;
        ArrayList arrayList;
        boolean z4;
        PropertyNamingStrategy propertyNamingStrategy2;
        Method[] methodArr;
        Constructor<?> constructor2;
        String str;
        Field[] fieldArr;
        Class<?> cls2;
        JSONType jSONType;
        PropertyNamingStrategy propertyNamingStrategy3;
        Method[] methodArr2;
        int i;
        int length;
        int i2;
        String str2;
        int i3;
        Field[] fieldArr2;
        PropertyNamingStrategy propertyNamingStrategy4;
        Type type2;
        Method[] methods;
        int length2;
        int i4;
        PropertyNamingStrategy propertyNamingStrategy5;
        Field[] fieldArr3;
        Type type3;
        Class<?> superclass;
        Method method;
        String name;
        Field[] fieldArr4;
        JSONField jSONField;
        String propertyNameByMethodName;
        Field[] fieldArr5;
        Field field;
        Field field2;
        JSONField jSONField2;
        String str3;
        Method method2;
        int iOrdinal;
        int iOf;
        int i5;
        String name2;
        Class<?> returnType;
        Class<?>[] parameterTypes;
        JSONField superMethodAnnotation;
        int i6;
        int i7;
        String str4;
        char cCharAt;
        ArrayList arrayList2;
        Class<?> cls3;
        Field[] fieldArr6;
        String propertyNameByMethodName2;
        Field field3;
        boolean z5;
        int i8;
        JSONField jSONField3;
        int i9;
        PropertyNamingStrategy propertyNamingStrategy6;
        JSONField jSONField4;
        int iOf2;
        Field[] fieldArr7;
        int i10;
        int iOf3;
        int i11;
        int i12;
        int i13;
        String str5;
        StringBuilder sb;
        String str6;
        String str7;
        char cCharAt2;
        Constructor<?> creatorConstructor;
        boolean z6;
        int i14;
        String[] strArrLookupParameterNames;
        boolean z7;
        String[] strArrLookupParameterNames2;
        Class<?>[] parameterTypes2;
        JSONField jSONField5;
        int iOf4;
        int i15;
        int i16;
        JSONField jSONField6;
        String strName;
        int i17;
        int i18;
        int iOf5;
        JSONField jSONField7;
        Field field4;
        String strName2;
        int iOrdinal2;
        int iOf6;
        int iOf7;
        Field field5;
        String[] strArr;
        Constructor<?> creatorConstructor2;
        PropertyNamingStrategy propertyNamingStrategyNaming;
        Class<?> cls4 = cls;
        boolean z8 = z3;
        JSONType jSONType2 = (JSONType) TypeUtils.getAnnotation(cls4, JSONType.class);
        PropertyNamingStrategy propertyNamingStrategy7 = (jSONType2 == null || (propertyNamingStrategyNaming = jSONType2.naming()) == null || propertyNamingStrategyNaming == PropertyNamingStrategy.CamelCase) ? propertyNamingStrategy : propertyNamingStrategyNaming;
        Class<?> builderClass = getBuilderClass(cls4, jSONType2);
        Field[] declaredFields = cls.getDeclaredFields();
        Method[] methods2 = cls.getMethods();
        Map<TypeVariable, Type> mapBuildGenericInfo = buildGenericInfo(cls);
        boolean zIsKotlin = TypeUtils.isKotlin(cls);
        Constructor<?>[] declaredConstructors = cls.getDeclaredConstructors();
        if (!zIsKotlin || declaredConstructors.length == 1) {
            if (builderClass == null) {
                defaultConstructor = getDefaultConstructor(cls4, declaredConstructors);
            } else {
                defaultConstructor = getDefaultConstructor(builderClass, builderClass.getDeclaredConstructors());
            }
            constructor = defaultConstructor;
        } else {
            constructor = null;
        }
        Method method3 = null;
        Method factoryMethod = null;
        ArrayList arrayList3 = new ArrayList();
        if (z) {
            for (Class<?> superclass2 = cls4; superclass2 != null; superclass2 = superclass2.getSuperclass()) {
                computeFields(cls4, type, propertyNamingStrategy7, arrayList3, superclass2.getDeclaredFields());
            }
            if (constructor != null) {
                TypeUtils.setAccessible(constructor);
            }
            return new JavaBeanInfo(cls, builderClass, constructor, null, null, null, jSONType2, arrayList3);
        }
        boolean z9 = cls.isInterface() || Modifier.isAbstract(cls.getModifiers());
        if ((constructor == null && builderClass == null) || z9) {
            Type mixInAnnotations = JSON.getMixInAnnotations(cls);
            if (!(mixInAnnotations instanceof Class) || (creatorConstructor2 = getCreatorConstructor(((Class) mixInAnnotations).getConstructors())) == null) {
                creatorConstructor = null;
            } else {
                try {
                    creatorConstructor = cls4.getConstructor(creatorConstructor2.getParameterTypes());
                } catch (NoSuchMethodException unused) {
                    creatorConstructor = null;
                }
            }
            if (creatorConstructor == null) {
                creatorConstructor = getCreatorConstructor(declaredConstructors);
            }
            constructor2 = creatorConstructor;
            if (constructor2 != null && !z9) {
                TypeUtils.setAccessible(constructor2);
                Class<?>[] parameterTypes3 = constructor2.getParameterTypes();
                if (parameterTypes3.length > 0) {
                    String[] strArrLookupParameterNames3 = null;
                    int i19 = 0;
                    for (Annotation[][] parameterAnnotations = TypeUtils.getParameterAnnotations(constructor2); i19 < parameterTypes3.length && i19 < parameterAnnotations.length; parameterAnnotations = parameterAnnotations) {
                        Annotation[] annotationArr = parameterAnnotations[i19];
                        int length3 = annotationArr.length;
                        int i20 = 0;
                        while (true) {
                            if (i20 >= length3) {
                                jSONField7 = null;
                                break;
                            }
                            Annotation annotation = annotationArr[i20];
                            Annotation[] annotationArr2 = annotationArr;
                            if (annotation instanceof JSONField) {
                                jSONField7 = (JSONField) annotation;
                                break;
                            }
                            i20++;
                            annotationArr = annotationArr2;
                        }
                        Class<?> cls5 = parameterTypes3[i19];
                        Type type4 = constructor2.getGenericParameterTypes()[i19];
                        if (jSONField7 != null) {
                            field4 = TypeUtils.getField(cls4, jSONField7.name(), declaredFields);
                            iOrdinal2 = jSONField7.ordinal();
                            iOf6 = SerializerFeature.of(jSONField7.serialzeFeatures());
                            iOf7 = Feature.of(jSONField7.parseFeatures());
                            strName2 = jSONField7.name();
                        } else {
                            field4 = null;
                            strName2 = null;
                            iOrdinal2 = 0;
                            iOf6 = 0;
                            iOf7 = 0;
                        }
                        if (strName2 == null || strName2.length() == 0) {
                            if (strArrLookupParameterNames3 == null) {
                                strArrLookupParameterNames3 = ASMUtils.lookupParameterNames(constructor2);
                            }
                            strName2 = strArrLookupParameterNames3[i19];
                        }
                        if (field4 == null) {
                            if (strArrLookupParameterNames3 == null) {
                                if (zIsKotlin) {
                                    strArrLookupParameterNames3 = TypeUtils.getKoltinConstructorParameters(cls);
                                } else {
                                    strArrLookupParameterNames3 = ASMUtils.lookupParameterNames(constructor2);
                                }
                            }
                            if (strArrLookupParameterNames3.length > i19) {
                                strArr = strArrLookupParameterNames3;
                                field5 = TypeUtils.getField(cls4, strArrLookupParameterNames3[i19], declaredFields);
                            }
                            ArrayList arrayList4 = arrayList3;
                            add(arrayList4, new FieldInfo(strName2, cls, cls5, type4, field5, iOrdinal2, iOf6, iOf7));
                            i19++;
                            parameterTypes3 = parameterTypes3;
                            methods2 = methods2;
                            arrayList3 = arrayList4;
                            propertyNamingStrategy7 = propertyNamingStrategy7;
                            strArrLookupParameterNames3 = strArr;
                        }
                        field5 = field4;
                        strArr = strArrLookupParameterNames3;
                        ArrayList arrayList5 = arrayList3;
                        add(arrayList5, new FieldInfo(strName2, cls, cls5, type4, field5, iOrdinal2, iOf6, iOf7));
                        i19++;
                        parameterTypes3 = parameterTypes3;
                        methods2 = methods2;
                        arrayList3 = arrayList5;
                        propertyNamingStrategy7 = propertyNamingStrategy7;
                        strArrLookupParameterNames3 = strArr;
                    }
                }
                arrayList = arrayList3;
                propertyNamingStrategy2 = propertyNamingStrategy7;
                methodArr = methods2;
            } else {
                arrayList = arrayList3;
                propertyNamingStrategy2 = propertyNamingStrategy7;
                methodArr = methods2;
                factoryMethod = getFactoryMethod(cls4, methodArr, z8);
                if (factoryMethod != null) {
                    TypeUtils.setAccessible(factoryMethod);
                    Class<?>[] parameterTypes4 = factoryMethod.getParameterTypes();
                    if (parameterTypes4.length > 0) {
                        Annotation[][] parameterAnnotations2 = TypeUtils.getParameterAnnotations(factoryMethod);
                        String[] strArrLookupParameterNames4 = null;
                        int i21 = 0;
                        while (i21 < parameterTypes4.length) {
                            Annotation[] annotationArr3 = parameterAnnotations2[i21];
                            int length4 = annotationArr3.length;
                            int i22 = 0;
                            while (true) {
                                if (i22 >= length4) {
                                    jSONField6 = null;
                                    break;
                                }
                                Annotation annotation2 = annotationArr3[i22];
                                if (annotation2 instanceof JSONField) {
                                    jSONField6 = (JSONField) annotation2;
                                    break;
                                }
                                i22++;
                            }
                            if (jSONField6 == null && (!z8 || !TypeUtils.isJacksonCreator(factoryMethod))) {
                                throw new JSONException("illegal json creator");
                            }
                            if (jSONField6 != null) {
                                strName = jSONField6.name();
                                int iOrdinal3 = jSONField6.ordinal();
                                int iOf8 = SerializerFeature.of(jSONField6.serialzeFeatures());
                                i17 = iOrdinal3;
                                iOf5 = Feature.of(jSONField6.parseFeatures());
                                i18 = iOf8;
                            } else {
                                strName = null;
                                i17 = 0;
                                i18 = 0;
                                iOf5 = 0;
                            }
                            if (strName == null || strName.length() == 0) {
                                if (strArrLookupParameterNames4 == null) {
                                    strArrLookupParameterNames4 = ASMUtils.lookupParameterNames(factoryMethod);
                                }
                                strName = strArrLookupParameterNames4[i21];
                            }
                            String[] strArr2 = strArrLookupParameterNames4;
                            add(arrayList, new FieldInfo(strName, cls, parameterTypes4[i21], factoryMethod.getGenericParameterTypes()[i21], TypeUtils.getField(cls4, strName, declaredFields), i17, i18, iOf5));
                            i21++;
                            z8 = z3;
                            parameterTypes4 = parameterTypes4;
                            strArrLookupParameterNames4 = strArr2;
                        }
                        return new JavaBeanInfo(cls, builderClass, null, null, factoryMethod, null, jSONType2, arrayList);
                    }
                } else if (!z9) {
                    String name3 = cls.getName();
                    if (zIsKotlin && declaredConstructors.length > 0) {
                        String[] koltinConstructorParameters = TypeUtils.getKoltinConstructorParameters(cls);
                        Constructor<?> kotlinConstructor = TypeUtils.getKotlinConstructor(declaredConstructors, koltinConstructorParameters);
                        TypeUtils.setAccessible(kotlinConstructor);
                        constructor2 = kotlinConstructor;
                        strArrLookupParameterNames = koltinConstructorParameters;
                    } else {
                        int length5 = declaredConstructors.length;
                        String[] strArr3 = null;
                        int i23 = 0;
                        while (true) {
                            if (i23 >= length5) {
                                z6 = true;
                                i14 = 0;
                                strArrLookupParameterNames = strArr3;
                                break;
                            }
                            Constructor<?> constructor3 = declaredConstructors[i23];
                            Class<?>[] parameterTypes5 = constructor3.getParameterTypes();
                            if (name3.equals("org.springframework.security.web.authentication.WebAuthenticationDetails")) {
                                if (parameterTypes5.length == 2) {
                                    z7 = false;
                                    if (parameterTypes5[0] == String.class) {
                                        if (parameterTypes5[1] == String.class) {
                                            constructor3.setAccessible(true);
                                            strArrLookupParameterNames = ASMUtils.lookupParameterNames(constructor3);
                                            constructor2 = constructor3;
                                            i14 = 0;
                                            z6 = true;
                                            break;
                                        }
                                    }
                                }
                                i23++;
                            } else {
                                z7 = false;
                                if (name3.equals("org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken")) {
                                    if (parameterTypes5.length == 3 && parameterTypes5[0] == Object.class) {
                                        if (parameterTypes5[1] == Object.class && parameterTypes5[2] == Collection.class) {
                                            constructor3.setAccessible(true);
                                            strArrLookupParameterNames = new String[]{"principal", "credentials", Settings.EXTRA_AUTHORITIES};
                                            constructor2 = constructor3;
                                        }
                                    }
                                    i23++;
                                } else {
                                    if (name3.equals("org.springframework.security.core.authority.SimpleGrantedAuthority")) {
                                        z6 = true;
                                        i14 = 0;
                                        if (parameterTypes5.length == 1 && parameterTypes5[0] == String.class) {
                                            strArrLookupParameterNames = new String[]{ContactsContract.Directory.DIRECTORY_AUTHORITY};
                                            constructor2 = constructor3;
                                            break;
                                        }
                                    } else if (((constructor3.getModifiers() & 1) != 0) && (strArrLookupParameterNames2 = ASMUtils.lookupParameterNames(constructor3)) != null && strArrLookupParameterNames2.length != 0 && (constructor2 == null || strArr3 == null || strArrLookupParameterNames2.length > strArr3.length)) {
                                        constructor2 = constructor3;
                                        strArr3 = strArrLookupParameterNames2;
                                    }
                                    i23++;
                                }
                            }
                        }
                        if (strArrLookupParameterNames != null) {
                            parameterTypes2 = constructor2.getParameterTypes();
                        } else {
                            parameterTypes2 = null;
                        }
                        if (strArrLookupParameterNames == null && parameterTypes2.length == strArrLookupParameterNames.length) {
                            Annotation[][] parameterAnnotations3 = TypeUtils.getParameterAnnotations(constructor2);
                            int i24 = i14;
                            while (i24 < parameterTypes2.length) {
                                Annotation[] annotationArr4 = parameterAnnotations3[i24];
                                String str8 = strArrLookupParameterNames[i24];
                                int length6 = annotationArr4.length;
                                int i25 = i14;
                                while (true) {
                                    if (i25 >= length6) {
                                        jSONField5 = null;
                                        break;
                                    }
                                    Annotation annotation3 = annotationArr4[i25];
                                    if (annotation3 instanceof JSONField) {
                                        jSONField5 = (JSONField) annotation3;
                                        break;
                                    }
                                    i25++;
                                }
                                Class<?> cls6 = parameterTypes2[i24];
                                Type type5 = constructor2.getGenericParameterTypes()[i24];
                                Field field6 = TypeUtils.getField(cls4, str8, declaredFields);
                                if (field6 != null && jSONField5 == null) {
                                    jSONField5 = (JSONField) TypeUtils.getAnnotation(field6, JSONField.class);
                                }
                                if (jSONField5 == null) {
                                    if ("org.springframework.security.core.userdetails.User".equals(name3) && "password".equals(str8)) {
                                        iOf4 = Feature.InitStringFieldAsEmpty.mask;
                                        i16 = i14;
                                        i15 = i16;
                                    } else {
                                        i16 = i14;
                                        i15 = i16;
                                        iOf4 = i15;
                                    }
                                } else {
                                    String strName3 = jSONField5.name();
                                    if (strName3.length() != 0) {
                                        str8 = strName3;
                                    }
                                    int iOrdinal4 = jSONField5.ordinal();
                                    int iOf9 = SerializerFeature.of(jSONField5.serialzeFeatures());
                                    iOf4 = Feature.of(jSONField5.parseFeatures());
                                    i15 = iOf9;
                                    i16 = iOrdinal4;
                                }
                                add(arrayList, new FieldInfo(str8, cls, cls6, type5, field6, i16, i15, iOf4));
                                i24++;
                                name3 = name3;
                                z6 = true;
                                strArrLookupParameterNames = strArrLookupParameterNames;
                                parameterTypes2 = parameterTypes2;
                                i14 = 0;
                            }
                            z4 = z6;
                            if (!zIsKotlin && !cls.getName().equals("javax.servlet.http.Cookie")) {
                                return new JavaBeanInfo(cls, builderClass, null, constructor2, null, null, jSONType2, arrayList);
                            }
                        } else {
                            throw new JSONException("default constructor not found. " + cls4);
                        }
                    }
                    z6 = true;
                    i14 = 0;
                    if (strArrLookupParameterNames != null) {
                        parameterTypes2 = constructor2.getParameterTypes();
                    } else {
                        parameterTypes2 = null;
                    }
                    if (strArrLookupParameterNames == null) {
                    }
                    throw new JSONException("default constructor not found. " + cls4);
                }
            }
            z4 = true;
        } else {
            arrayList = arrayList3;
            z4 = true;
            propertyNamingStrategy2 = propertyNamingStrategy7;
            methodArr = methods2;
            constructor2 = null;
        }
        if (constructor != null) {
            TypeUtils.setAccessible(constructor);
        }
        String str9 = "set";
        if (builderClass != null) {
            JSONPOJOBuilder jSONPOJOBuilder = (JSONPOJOBuilder) TypeUtils.getAnnotation(builderClass, JSONPOJOBuilder.class);
            String strWithPrefix = jSONPOJOBuilder != null ? jSONPOJOBuilder.withPrefix() : null;
            if (strWithPrefix == null) {
                strWithPrefix = "with";
            }
            String str10 = strWithPrefix;
            Method[] methods3 = builderClass.getMethods();
            int length7 = methods3.length;
            int i26 = 0;
            while (i26 < length7) {
                Method method4 = methods3[i26];
                if (!Modifier.isStatic(method4.getModifiers()) && method4.getReturnType().equals(builderClass)) {
                    JSONField superMethodAnnotation2 = (JSONField) TypeUtils.getAnnotation(method4, JSONField.class);
                    if (superMethodAnnotation2 == null) {
                        superMethodAnnotation2 = TypeUtils.getSuperMethodAnnotation(cls4, method4);
                    }
                    JSONField jSONField8 = superMethodAnnotation2;
                    if (jSONField8 == null) {
                        i11 = 0;
                        i12 = 0;
                        i13 = 0;
                    } else if (jSONField8.deserialize()) {
                        int iOrdinal5 = jSONField8.ordinal();
                        int iOf10 = SerializerFeature.of(jSONField8.serialzeFeatures());
                        int iOf11 = Feature.of(jSONField8.parseFeatures());
                        if (jSONField8.name().length() != 0) {
                            i26 = i26;
                            length7 = length7;
                            methods3 = methods3;
                            declaredFields = declaredFields;
                            builderClass = builderClass;
                            jSONType2 = jSONType2;
                            propertyNamingStrategy2 = propertyNamingStrategy2;
                            methodArr = methodArr;
                            add(arrayList, new FieldInfo(jSONField8.name(), method4, null, cls, type, iOrdinal5, iOf10, iOf11, jSONField8, null, null, mapBuildGenericInfo));
                            str6 = str10;
                            str7 = str9;
                        } else {
                            i11 = iOrdinal5;
                            i12 = iOf10;
                            i13 = iOf11;
                        }
                    } else {
                        i26 = i26;
                        length7 = length7;
                        methods3 = methods3;
                        str6 = str10;
                        str7 = str9;
                        declaredFields = declaredFields;
                        builderClass = builderClass;
                        jSONType2 = jSONType2;
                        propertyNamingStrategy2 = propertyNamingStrategy2;
                        methodArr = methodArr;
                    }
                    String name4 = method4.getName();
                    String str11 = str9;
                    if (name4.startsWith(str11) && name4.length() > 3) {
                        sb = new StringBuilder(name4.substring(3));
                    } else {
                        if (str10.length() == 0) {
                            sb = new StringBuilder(name4);
                        } else {
                            str5 = str10;
                            if (name4.startsWith(str5) && name4.length() > str5.length()) {
                                sb = new StringBuilder(name4.substring(str5.length()));
                                cCharAt2 = sb.charAt(0);
                                if (str5.length() != 0 || Character.isUpperCase(cCharAt2)) {
                                    sb.setCharAt(0, Character.toLowerCase(cCharAt2));
                                    str6 = str5;
                                    str7 = str11;
                                    add(arrayList, new FieldInfo(sb.toString(), method4, null, cls, type, i11, i12, i13, jSONField8, null, null, mapBuildGenericInfo));
                                }
                            }
                        }
                        str6 = str5;
                        str7 = str11;
                    }
                    str5 = str10;
                    cCharAt2 = sb.charAt(0);
                    if (str5.length() != 0) {
                    }
                    sb.setCharAt(0, Character.toLowerCase(cCharAt2));
                    str6 = str5;
                    str7 = str11;
                    add(arrayList, new FieldInfo(sb.toString(), method4, null, cls, type, i11, i12, i13, jSONField8, null, null, mapBuildGenericInfo));
                } else {
                    i26 = i26;
                    length7 = length7;
                    methods3 = methods3;
                    str6 = str10;
                    str7 = str9;
                    declaredFields = declaredFields;
                    builderClass = builderClass;
                    jSONType2 = jSONType2;
                    propertyNamingStrategy2 = propertyNamingStrategy2;
                    methodArr = methodArr;
                }
                i26++;
                cls4 = cls;
                methodArr = methodArr;
                str10 = str6;
                jSONType2 = jSONType2;
                length7 = length7;
                methods3 = methods3;
                declaredFields = declaredFields;
                builderClass = builderClass;
                propertyNamingStrategy2 = propertyNamingStrategy2;
                str9 = str7;
                z4 = true;
            }
            str = str9;
            fieldArr = declaredFields;
            cls2 = builderClass;
            jSONType = jSONType2;
            propertyNamingStrategy3 = propertyNamingStrategy2;
            methodArr2 = methodArr;
            if (cls2 != null) {
                JSONPOJOBuilder jSONPOJOBuilder2 = (JSONPOJOBuilder) TypeUtils.getAnnotation(cls2, JSONPOJOBuilder.class);
                String strBuildMethod = jSONPOJOBuilder2 != null ? jSONPOJOBuilder2.buildMethod() : null;
                if (strBuildMethod == null || strBuildMethod.length() == 0) {
                    strBuildMethod = "build";
                }
                i = 0;
                try {
                    method3 = cls2.getMethod(strBuildMethod, new Class[0]);
                } catch (NoSuchMethodException | SecurityException unused2) {
                }
                if (method3 == null) {
                    try {
                        method3 = cls2.getMethod("create", new Class[0]);
                    } catch (NoSuchMethodException | SecurityException unused3) {
                    }
                }
                if (method3 == null) {
                    throw new JSONException("buildMethod not found.");
                }
                TypeUtils.setAccessible(method3);
            }
            length = methodArr2.length;
            i2 = i;
            while (true) {
                str2 = "get";
                i3 = 4;
                if (i2 < length) {
                    break;
                }
                method2 = methodArr2[i2];
                iOrdinal = 0;
                iOf = 0;
                i5 = 0;
                name2 = method2.getName();
                if (Modifier.isStatic(method2.getModifiers())) {
                    i6 = i2;
                    i7 = length;
                    i8 = i;
                    cls2 = cls2;
                    methodArr2 = methodArr2;
                    fieldArr6 = fieldArr;
                    propertyNamingStrategy3 = propertyNamingStrategy3;
                    str4 = str;
                } else {
                    returnType = method2.getReturnType();
                    if ((!returnType.equals(Void.TYPE) || returnType.equals(method2.getDeclaringClass())) && method2.getDeclaringClass() != Object.class) {
                        parameterTypes = method2.getParameterTypes();
                        if (parameterTypes.length == 0) {
                            i6 = i2;
                            i7 = length;
                            i8 = i;
                            cls2 = cls2;
                            methodArr2 = methodArr2;
                            fieldArr6 = fieldArr;
                            propertyNamingStrategy3 = propertyNamingStrategy3;
                            str4 = str;
                        } else if (parameterTypes.length > 2) {
                            i6 = i2;
                            i7 = length;
                            i8 = i;
                            cls2 = cls2;
                            methodArr2 = methodArr2;
                            fieldArr6 = fieldArr;
                            propertyNamingStrategy3 = propertyNamingStrategy3;
                            str4 = str;
                        } else {
                            superMethodAnnotation = (JSONField) TypeUtils.getAnnotation(method2, JSONField.class);
                            if (superMethodAnnotation == null && parameterTypes.length == 2 && parameterTypes[i] == String.class && parameterTypes[1] == Object.class) {
                                i6 = i2;
                                i7 = length;
                                add(arrayList, new FieldInfo("", method2, null, cls, type, 0, 0, 0, superMethodAnnotation, null, null, mapBuildGenericInfo));
                            } else {
                                i6 = i2;
                                i7 = length;
                                if (parameterTypes.length == 1) {
                                    if (superMethodAnnotation == null) {
                                        superMethodAnnotation = TypeUtils.getSuperMethodAnnotation(cls, method2);
                                    }
                                    if (superMethodAnnotation == null || name2.length() >= 4) {
                                        if (superMethodAnnotation != null) {
                                            if (superMethodAnnotation.deserialize()) {
                                                iOrdinal = superMethodAnnotation.ordinal();
                                                iOf = SerializerFeature.of(superMethodAnnotation.serialzeFeatures());
                                                iOf3 = Feature.of(superMethodAnnotation.parseFeatures());
                                                if (superMethodAnnotation.name().length() != 0) {
                                                    add(arrayList, new FieldInfo(superMethodAnnotation.name(), method2, null, cls, type, iOrdinal, iOf, iOf3, superMethodAnnotation, null, null, mapBuildGenericInfo));
                                                } else {
                                                    i5 = iOf3;
                                                }
                                            }
                                        }
                                        str4 = str;
                                        if ((superMethodAnnotation == null || name2.startsWith(str4)) && cls2 == null) {
                                            cCharAt = name2.charAt(3);
                                            if (zIsKotlin) {
                                                arrayList2 = new ArrayList();
                                                for (i10 = 0; i10 < methodArr2.length; i10++) {
                                                    if (methodArr2[i10].getName().startsWith("get")) {
                                                        arrayList2.add(methodArr2[i10].getName());
                                                    }
                                                }
                                            } else {
                                                arrayList2 = null;
                                            }
                                            if (!Character.isUpperCase(cCharAt) || cCharAt > 512) {
                                                cls3 = cls;
                                                fieldArr6 = fieldArr;
                                                if (zIsKotlin) {
                                                    propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                                } else if (TypeUtils.compatibleWithJavaBean) {
                                                    propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                                } else {
                                                    propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                                }
                                            } else {
                                                if (cCharAt != '_') {
                                                    cls3 = cls;
                                                    fieldArr7 = fieldArr;
                                                    if (cCharAt == 'f') {
                                                        propertyNameByMethodName2 = name2.substring(3);
                                                    } else if (name2.length() >= 5 && Character.isUpperCase(name2.charAt(4))) {
                                                        propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                                    } else {
                                                        propertyNameByMethodName2 = name2.substring(3);
                                                        field3 = TypeUtils.getField(cls3, propertyNameByMethodName2, fieldArr7);
                                                        if (field3 == null) {
                                                            fieldArr6 = fieldArr7;
                                                        }
                                                    }
                                                    fieldArr6 = fieldArr7;
                                                } else if (zIsKotlin) {
                                                    propertyNameByMethodName2 = arrayList2.contains("g" + name2.substring(1)) ? name2.substring(3) : "is" + name2.substring(3);
                                                    cls3 = cls;
                                                    fieldArr7 = fieldArr;
                                                    field3 = TypeUtils.getField(cls3, propertyNameByMethodName2, fieldArr7);
                                                } else {
                                                    cls3 = cls;
                                                    fieldArr7 = fieldArr;
                                                    String strSubstring = name2.substring(4);
                                                    Field field7 = TypeUtils.getField(cls3, strSubstring, fieldArr7);
                                                    if (field7 != null || (field7 = TypeUtils.getField(cls3, (propertyNameByMethodName2 = name2.substring(3)), fieldArr7)) == null) {
                                                        propertyNameByMethodName2 = strSubstring;
                                                    }
                                                    field3 = field7;
                                                }
                                                fieldArr6 = fieldArr7;
                                                if (field3 == null) {
                                                    field3 = TypeUtils.getField(cls3, propertyNameByMethodName2, fieldArr6);
                                                }
                                                if (field3 == null) {
                                                    i8 = 0;
                                                    if (parameterTypes[0] == Boolean.TYPE) {
                                                        StringBuilder sb2 = new StringBuilder();
                                                        sb2.append("is");
                                                        sb2.append(Character.toUpperCase(propertyNameByMethodName2.charAt(0)));
                                                        z5 = true;
                                                        sb2.append(propertyNameByMethodName2.substring(1));
                                                        field3 = TypeUtils.getField(cls3, sb2.toString(), fieldArr6);
                                                    } else {
                                                        z5 = true;
                                                    }
                                                } else {
                                                    z5 = true;
                                                    i8 = 0;
                                                }
                                                if (field3 != null) {
                                                    jSONField4 = (JSONField) TypeUtils.getAnnotation(field3, JSONField.class);
                                                    if (jSONField4 != null) {
                                                        if (!jSONField4.deserialize()) {
                                                            iOrdinal = jSONField4.ordinal();
                                                            iOf = SerializerFeature.of(jSONField4.serialzeFeatures());
                                                            iOf2 = Feature.of(jSONField4.parseFeatures());
                                                            if (jSONField4.name().length() != 0) {
                                                                add(arrayList, new FieldInfo(jSONField4.name(), method2, field3, cls, type, iOrdinal, iOf, iOf2, superMethodAnnotation, jSONField4, null, mapBuildGenericInfo));
                                                            } else {
                                                                i9 = iOf2;
                                                            }
                                                        }
                                                        propertyNamingStrategy3 = propertyNamingStrategy3;
                                                    } else {
                                                        i9 = i5;
                                                    }
                                                    jSONField3 = jSONField4;
                                                } else {
                                                    fieldArr6 = fieldArr6;
                                                    i8 = i8;
                                                    str4 = str4;
                                                    methodArr2 = methodArr2;
                                                    jSONField3 = null;
                                                    i9 = i5;
                                                }
                                                propertyNamingStrategy6 = propertyNamingStrategy3;
                                                if (propertyNamingStrategy6 != null) {
                                                    propertyNameByMethodName2 = propertyNamingStrategy6.translate(propertyNameByMethodName2);
                                                }
                                                cls2 = cls2;
                                                propertyNamingStrategy3 = propertyNamingStrategy6;
                                                add(arrayList, new FieldInfo(propertyNameByMethodName2, method2, field3, cls, type, iOrdinal, iOf, i9, superMethodAnnotation, jSONField3, null, mapBuildGenericInfo));
                                            }
                                            field3 = null;
                                            if (field3 == null) {
                                                field3 = TypeUtils.getField(cls3, propertyNameByMethodName2, fieldArr6);
                                            }
                                            if (field3 == null) {
                                                i8 = 0;
                                                if (parameterTypes[0] == Boolean.TYPE) {
                                                    StringBuilder sb3 = new StringBuilder();
                                                    sb3.append("is");
                                                    sb3.append(Character.toUpperCase(propertyNameByMethodName2.charAt(0)));
                                                    z5 = true;
                                                    sb3.append(propertyNameByMethodName2.substring(1));
                                                    field3 = TypeUtils.getField(cls3, sb3.toString(), fieldArr6);
                                                } else {
                                                    z5 = true;
                                                }
                                            } else {
                                                z5 = true;
                                                i8 = 0;
                                            }
                                            if (field3 != null) {
                                                jSONField4 = (JSONField) TypeUtils.getAnnotation(field3, JSONField.class);
                                                if (jSONField4 != null) {
                                                    if (!jSONField4.deserialize()) {
                                                        iOrdinal = jSONField4.ordinal();
                                                        iOf = SerializerFeature.of(jSONField4.serialzeFeatures());
                                                        iOf2 = Feature.of(jSONField4.parseFeatures());
                                                        if (jSONField4.name().length() != 0) {
                                                            add(arrayList, new FieldInfo(jSONField4.name(), method2, field3, cls, type, iOrdinal, iOf, iOf2, superMethodAnnotation, jSONField4, null, mapBuildGenericInfo));
                                                        } else {
                                                            i9 = iOf2;
                                                        }
                                                    }
                                                    propertyNamingStrategy3 = propertyNamingStrategy3;
                                                } else {
                                                    i9 = i5;
                                                }
                                                jSONField3 = jSONField4;
                                            } else {
                                                fieldArr6 = fieldArr6;
                                                i8 = i8;
                                                str4 = str4;
                                                methodArr2 = methodArr2;
                                                jSONField3 = null;
                                                i9 = i5;
                                            }
                                            propertyNamingStrategy6 = propertyNamingStrategy3;
                                            if (propertyNamingStrategy6 != null) {
                                                propertyNameByMethodName2 = propertyNamingStrategy6.translate(propertyNameByMethodName2);
                                            }
                                            cls2 = cls2;
                                            propertyNamingStrategy3 = propertyNamingStrategy6;
                                            add(arrayList, new FieldInfo(propertyNameByMethodName2, method2, field3, cls, type, iOrdinal, iOf, i9, superMethodAnnotation, jSONField3, null, mapBuildGenericInfo));
                                        } else {
                                            fieldArr6 = fieldArr;
                                        }
                                        i8 = 0;
                                    }
                                }
                                cls2 = cls2;
                                methodArr2 = methodArr2;
                                fieldArr6 = fieldArr;
                                propertyNamingStrategy3 = propertyNamingStrategy3;
                                str4 = str;
                                i8 = 0;
                            }
                            cls2 = cls2;
                            methodArr2 = methodArr2;
                            fieldArr6 = fieldArr;
                            str4 = str;
                            i8 = 0;
                        }
                    } else {
                        i6 = i2;
                        i7 = length;
                        i8 = i;
                        cls2 = cls2;
                        methodArr2 = methodArr2;
                        fieldArr6 = fieldArr;
                        propertyNamingStrategy3 = propertyNamingStrategy3;
                        str4 = str;
                    }
                }
                i2 = i6 + 1;
                propertyNamingStrategy3 = propertyNamingStrategy3;
                length = i7;
                i = i8;
                methodArr2 = methodArr2;
                str = str4;
                cls2 = cls2;
                fieldArr = fieldArr6;
            }
            int i27 = i;
            Class<?> cls7 = cls2;
            fieldArr2 = fieldArr;
            propertyNamingStrategy4 = propertyNamingStrategy3;
            type2 = type;
            computeFields(cls, type2, propertyNamingStrategy4, arrayList, cls.getFields());
            methods = cls.getMethods();
            length2 = methods.length;
            i4 = i27;
            while (i4 < length2) {
                method = methods[i4];
                name = method.getName();
                if (name.length() < i3 && !Modifier.isStatic(method.getModifiers()) && cls7 == null && name.startsWith(str2) && Character.isUpperCase(name.charAt(3)) && method.getParameterTypes().length == 0 && ((Collection.class.isAssignableFrom(method.getReturnType()) || Map.class.isAssignableFrom(method.getReturnType()) || AtomicBoolean.class == method.getReturnType() || AtomicInteger.class == method.getReturnType() || AtomicLong.class == method.getReturnType()) && ((jSONField = (JSONField) TypeUtils.getAnnotation(method, JSONField.class)) == null || !jSONField.deserialize()))) {
                    if (jSONField == null && jSONField.name().length() > 0) {
                        propertyNameByMethodName = jSONField.name();
                        field2 = null;
                        fieldArr5 = fieldArr2;
                    } else {
                        propertyNameByMethodName = TypeUtils.getPropertyNameByMethodName(name);
                        fieldArr5 = fieldArr2;
                        field = TypeUtils.getField(cls, propertyNameByMethodName, fieldArr5);
                        if (field == null) {
                            field2 = null;
                        } else {
                            jSONField2 = (JSONField) TypeUtils.getAnnotation(field, JSONField.class);
                            if (jSONField2 != null || jSONField2.deserialize()) {
                                if (!Collection.class.isAssignableFrom(method.getReturnType()) || Map.class.isAssignableFrom(method.getReturnType())) {
                                    field2 = field;
                                } else {
                                    field2 = null;
                                }
                            }
                        }
                        fieldArr4 = fieldArr5;
                    }
                    if (propertyNamingStrategy4 != null) {
                        propertyNameByMethodName = propertyNamingStrategy4.translate(propertyNameByMethodName);
                    }
                    str3 = propertyNameByMethodName;
                    if (getField(arrayList, str3) != null) {
                        fieldArr4 = fieldArr5;
                    } else {
                        fieldArr4 = fieldArr5;
                        i4 = i4;
                        i3 = i3;
                        str2 = str2;
                        length2 = length2;
                        methods = methods;
                        propertyNamingStrategy4 = propertyNamingStrategy4;
                        add(arrayList, new FieldInfo(str3, method, field2, cls, type, 0, 0, 0, jSONField, null, null, mapBuildGenericInfo));
                    }
                    i4++;
                    type2 = type2;
                    length2 = length2;
                    i3 = i3;
                    str2 = str2;
                    methods = methods;
                    propertyNamingStrategy4 = propertyNamingStrategy4;
                    fieldArr2 = fieldArr4;
                } else {
                    fieldArr4 = fieldArr2;
                }
                i4++;
                type2 = type2;
                length2 = length2;
                i3 = i3;
                str2 = str2;
                methods = methods;
                propertyNamingStrategy4 = propertyNamingStrategy4;
                fieldArr2 = fieldArr4;
            }
            propertyNamingStrategy5 = propertyNamingStrategy4;
            fieldArr3 = fieldArr2;
            type3 = type2;
            if (arrayList.size() == 0) {
                if (TypeUtils.isXmlField(cls) ? true : z) {
                    for (superclass = cls; superclass != null; superclass = superclass.getSuperclass()) {
                        computeFields(cls, type3, propertyNamingStrategy5, arrayList, fieldArr3);
                    }
                }
            }
            return new JavaBeanInfo(cls, cls7, constructor, constructor2, factoryMethod, method3, jSONType, arrayList);
        }
        str = "set";
        fieldArr = declaredFields;
        cls2 = builderClass;
        jSONType = jSONType2;
        propertyNamingStrategy3 = propertyNamingStrategy2;
        methodArr2 = methodArr;
        i = 0;
        length = methodArr2.length;
        i2 = i;
        while (true) {
            str2 = "get";
            i3 = 4;
            if (i2 < length) {
                break;
                break;
            }
            method2 = methodArr2[i2];
            iOrdinal = 0;
            iOf = 0;
            i5 = 0;
            name2 = method2.getName();
            if (Modifier.isStatic(method2.getModifiers())) {
                i6 = i2;
                i7 = length;
                i8 = i;
                cls2 = cls2;
                methodArr2 = methodArr2;
                fieldArr6 = fieldArr;
                propertyNamingStrategy3 = propertyNamingStrategy3;
                str4 = str;
            } else {
                returnType = method2.getReturnType();
                if (returnType.equals(Void.TYPE)) {
                    parameterTypes = method2.getParameterTypes();
                    if (parameterTypes.length == 0) {
                        i6 = i2;
                        i7 = length;
                        i8 = i;
                        cls2 = cls2;
                        methodArr2 = methodArr2;
                        fieldArr6 = fieldArr;
                        propertyNamingStrategy3 = propertyNamingStrategy3;
                        str4 = str;
                    } else if (parameterTypes.length > 2) {
                        i6 = i2;
                        i7 = length;
                        i8 = i;
                        cls2 = cls2;
                        methodArr2 = methodArr2;
                        fieldArr6 = fieldArr;
                        propertyNamingStrategy3 = propertyNamingStrategy3;
                        str4 = str;
                    } else {
                        superMethodAnnotation = (JSONField) TypeUtils.getAnnotation(method2, JSONField.class);
                        if (superMethodAnnotation == null) {
                            i6 = i2;
                            i7 = length;
                            if (parameterTypes.length == 1) {
                                if (superMethodAnnotation == null) {
                                    superMethodAnnotation = TypeUtils.getSuperMethodAnnotation(cls, method2);
                                }
                                if (superMethodAnnotation == null) {
                                    if (superMethodAnnotation != null) {
                                        if (superMethodAnnotation.deserialize()) {
                                            iOrdinal = superMethodAnnotation.ordinal();
                                            iOf = SerializerFeature.of(superMethodAnnotation.serialzeFeatures());
                                            iOf3 = Feature.of(superMethodAnnotation.parseFeatures());
                                            if (superMethodAnnotation.name().length() != 0) {
                                                add(arrayList, new FieldInfo(superMethodAnnotation.name(), method2, null, cls, type, iOrdinal, iOf, iOf3, superMethodAnnotation, null, null, mapBuildGenericInfo));
                                                cls2 = cls2;
                                                methodArr2 = methodArr2;
                                                fieldArr6 = fieldArr;
                                                str4 = str;
                                                i8 = 0;
                                            } else {
                                                i5 = iOf3;
                                            }
                                        }
                                    }
                                    str4 = str;
                                    if (superMethodAnnotation == null) {
                                        cCharAt = name2.charAt(3);
                                        if (zIsKotlin) {
                                            arrayList2 = new ArrayList();
                                            while (i10 < methodArr2.length) {
                                                if (methodArr2[i10].getName().startsWith("get")) {
                                                    arrayList2.add(methodArr2[i10].getName());
                                                }
                                            }
                                        } else {
                                            arrayList2 = null;
                                        }
                                        if (Character.isUpperCase(cCharAt)) {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        } else {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        }
                                        if (field3 == null) {
                                            field3 = TypeUtils.getField(cls3, propertyNameByMethodName2, fieldArr6);
                                        }
                                        if (field3 == null) {
                                            i8 = 0;
                                            if (parameterTypes[0] == Boolean.TYPE) {
                                                StringBuilder sb4 = new StringBuilder();
                                                sb4.append("is");
                                                sb4.append(Character.toUpperCase(propertyNameByMethodName2.charAt(0)));
                                                z5 = true;
                                                sb4.append(propertyNameByMethodName2.substring(1));
                                                field3 = TypeUtils.getField(cls3, sb4.toString(), fieldArr6);
                                            } else {
                                                z5 = true;
                                            }
                                        } else {
                                            z5 = true;
                                            i8 = 0;
                                        }
                                        if (field3 != null) {
                                            jSONField4 = (JSONField) TypeUtils.getAnnotation(field3, JSONField.class);
                                            if (jSONField4 != null) {
                                                if (!jSONField4.deserialize()) {
                                                    iOrdinal = jSONField4.ordinal();
                                                    iOf = SerializerFeature.of(jSONField4.serialzeFeatures());
                                                    iOf2 = Feature.of(jSONField4.parseFeatures());
                                                    if (jSONField4.name().length() != 0) {
                                                        add(arrayList, new FieldInfo(jSONField4.name(), method2, field3, cls, type, iOrdinal, iOf, iOf2, superMethodAnnotation, jSONField4, null, mapBuildGenericInfo));
                                                    } else {
                                                        i9 = iOf2;
                                                    }
                                                }
                                                propertyNamingStrategy3 = propertyNamingStrategy3;
                                            } else {
                                                i9 = i5;
                                            }
                                            jSONField3 = jSONField4;
                                        } else {
                                            fieldArr6 = fieldArr6;
                                            i8 = i8;
                                            str4 = str4;
                                            methodArr2 = methodArr2;
                                            jSONField3 = null;
                                            i9 = i5;
                                        }
                                        propertyNamingStrategy6 = propertyNamingStrategy3;
                                        if (propertyNamingStrategy6 != null) {
                                            propertyNameByMethodName2 = propertyNamingStrategy6.translate(propertyNameByMethodName2);
                                        }
                                        cls2 = cls2;
                                        propertyNamingStrategy3 = propertyNamingStrategy6;
                                        add(arrayList, new FieldInfo(propertyNameByMethodName2, method2, field3, cls, type, iOrdinal, iOf, i9, superMethodAnnotation, jSONField3, null, mapBuildGenericInfo));
                                    } else {
                                        cCharAt = name2.charAt(3);
                                        if (zIsKotlin) {
                                            arrayList2 = new ArrayList();
                                            while (i10 < methodArr2.length) {
                                                if (methodArr2[i10].getName().startsWith("get")) {
                                                    arrayList2.add(methodArr2[i10].getName());
                                                }
                                            }
                                        } else {
                                            arrayList2 = null;
                                        }
                                        if (Character.isUpperCase(cCharAt)) {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        } else {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        }
                                        if (field3 == null) {
                                            field3 = TypeUtils.getField(cls3, propertyNameByMethodName2, fieldArr6);
                                        }
                                        if (field3 == null) {
                                            i8 = 0;
                                            if (parameterTypes[0] == Boolean.TYPE) {
                                                StringBuilder sb5 = new StringBuilder();
                                                sb5.append("is");
                                                sb5.append(Character.toUpperCase(propertyNameByMethodName2.charAt(0)));
                                                z5 = true;
                                                sb5.append(propertyNameByMethodName2.substring(1));
                                                field3 = TypeUtils.getField(cls3, sb5.toString(), fieldArr6);
                                            } else {
                                                z5 = true;
                                            }
                                        } else {
                                            z5 = true;
                                            i8 = 0;
                                        }
                                        if (field3 != null) {
                                            jSONField4 = (JSONField) TypeUtils.getAnnotation(field3, JSONField.class);
                                            if (jSONField4 != null) {
                                                if (!jSONField4.deserialize()) {
                                                    iOrdinal = jSONField4.ordinal();
                                                    iOf = SerializerFeature.of(jSONField4.serialzeFeatures());
                                                    iOf2 = Feature.of(jSONField4.parseFeatures());
                                                    if (jSONField4.name().length() != 0) {
                                                        add(arrayList, new FieldInfo(jSONField4.name(), method2, field3, cls, type, iOrdinal, iOf, iOf2, superMethodAnnotation, jSONField4, null, mapBuildGenericInfo));
                                                    } else {
                                                        i9 = iOf2;
                                                    }
                                                }
                                                propertyNamingStrategy3 = propertyNamingStrategy3;
                                            } else {
                                                i9 = i5;
                                            }
                                            jSONField3 = jSONField4;
                                        } else {
                                            fieldArr6 = fieldArr6;
                                            i8 = i8;
                                            str4 = str4;
                                            methodArr2 = methodArr2;
                                            jSONField3 = null;
                                            i9 = i5;
                                        }
                                        propertyNamingStrategy6 = propertyNamingStrategy3;
                                        if (propertyNamingStrategy6 != null) {
                                            propertyNameByMethodName2 = propertyNamingStrategy6.translate(propertyNameByMethodName2);
                                        }
                                        cls2 = cls2;
                                        propertyNamingStrategy3 = propertyNamingStrategy6;
                                        add(arrayList, new FieldInfo(propertyNameByMethodName2, method2, field3, cls, type, iOrdinal, iOf, i9, superMethodAnnotation, jSONField3, null, mapBuildGenericInfo));
                                    }
                                } else {
                                    if (superMethodAnnotation != null) {
                                        if (superMethodAnnotation.deserialize()) {
                                            iOrdinal = superMethodAnnotation.ordinal();
                                            iOf = SerializerFeature.of(superMethodAnnotation.serialzeFeatures());
                                            iOf3 = Feature.of(superMethodAnnotation.parseFeatures());
                                            if (superMethodAnnotation.name().length() != 0) {
                                                add(arrayList, new FieldInfo(superMethodAnnotation.name(), method2, null, cls, type, iOrdinal, iOf, iOf3, superMethodAnnotation, null, null, mapBuildGenericInfo));
                                                cls2 = cls2;
                                                methodArr2 = methodArr2;
                                                fieldArr6 = fieldArr;
                                                str4 = str;
                                                i8 = 0;
                                            } else {
                                                i5 = iOf3;
                                            }
                                        }
                                    }
                                    str4 = str;
                                    if (superMethodAnnotation == null) {
                                        cCharAt = name2.charAt(3);
                                        if (zIsKotlin) {
                                            arrayList2 = new ArrayList();
                                            while (i10 < methodArr2.length) {
                                                if (methodArr2[i10].getName().startsWith("get")) {
                                                    arrayList2.add(methodArr2[i10].getName());
                                                }
                                            }
                                        } else {
                                            arrayList2 = null;
                                        }
                                        if (Character.isUpperCase(cCharAt)) {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        } else {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        }
                                        if (field3 == null) {
                                            field3 = TypeUtils.getField(cls3, propertyNameByMethodName2, fieldArr6);
                                        }
                                        if (field3 == null) {
                                            i8 = 0;
                                            if (parameterTypes[0] == Boolean.TYPE) {
                                                StringBuilder sb6 = new StringBuilder();
                                                sb6.append("is");
                                                sb6.append(Character.toUpperCase(propertyNameByMethodName2.charAt(0)));
                                                z5 = true;
                                                sb6.append(propertyNameByMethodName2.substring(1));
                                                field3 = TypeUtils.getField(cls3, sb6.toString(), fieldArr6);
                                            } else {
                                                z5 = true;
                                            }
                                        } else {
                                            z5 = true;
                                            i8 = 0;
                                        }
                                        if (field3 != null) {
                                            jSONField4 = (JSONField) TypeUtils.getAnnotation(field3, JSONField.class);
                                            if (jSONField4 != null) {
                                                if (!jSONField4.deserialize()) {
                                                    iOrdinal = jSONField4.ordinal();
                                                    iOf = SerializerFeature.of(jSONField4.serialzeFeatures());
                                                    iOf2 = Feature.of(jSONField4.parseFeatures());
                                                    if (jSONField4.name().length() != 0) {
                                                        add(arrayList, new FieldInfo(jSONField4.name(), method2, field3, cls, type, iOrdinal, iOf, iOf2, superMethodAnnotation, jSONField4, null, mapBuildGenericInfo));
                                                    } else {
                                                        i9 = iOf2;
                                                    }
                                                }
                                                propertyNamingStrategy3 = propertyNamingStrategy3;
                                            } else {
                                                i9 = i5;
                                            }
                                            jSONField3 = jSONField4;
                                        } else {
                                            fieldArr6 = fieldArr6;
                                            i8 = i8;
                                            str4 = str4;
                                            methodArr2 = methodArr2;
                                            jSONField3 = null;
                                            i9 = i5;
                                        }
                                        propertyNamingStrategy6 = propertyNamingStrategy3;
                                        if (propertyNamingStrategy6 != null) {
                                            propertyNameByMethodName2 = propertyNamingStrategy6.translate(propertyNameByMethodName2);
                                        }
                                        cls2 = cls2;
                                        propertyNamingStrategy3 = propertyNamingStrategy6;
                                        add(arrayList, new FieldInfo(propertyNameByMethodName2, method2, field3, cls, type, iOrdinal, iOf, i9, superMethodAnnotation, jSONField3, null, mapBuildGenericInfo));
                                    } else {
                                        cCharAt = name2.charAt(3);
                                        if (zIsKotlin) {
                                            arrayList2 = new ArrayList();
                                            while (i10 < methodArr2.length) {
                                                if (methodArr2[i10].getName().startsWith("get")) {
                                                    arrayList2.add(methodArr2[i10].getName());
                                                }
                                            }
                                        } else {
                                            arrayList2 = null;
                                        }
                                        if (Character.isUpperCase(cCharAt)) {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        } else {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        }
                                        if (field3 == null) {
                                            field3 = TypeUtils.getField(cls3, propertyNameByMethodName2, fieldArr6);
                                        }
                                        if (field3 == null) {
                                            i8 = 0;
                                            if (parameterTypes[0] == Boolean.TYPE) {
                                                StringBuilder sb7 = new StringBuilder();
                                                sb7.append("is");
                                                sb7.append(Character.toUpperCase(propertyNameByMethodName2.charAt(0)));
                                                z5 = true;
                                                sb7.append(propertyNameByMethodName2.substring(1));
                                                field3 = TypeUtils.getField(cls3, sb7.toString(), fieldArr6);
                                            } else {
                                                z5 = true;
                                            }
                                        } else {
                                            z5 = true;
                                            i8 = 0;
                                        }
                                        if (field3 != null) {
                                            jSONField4 = (JSONField) TypeUtils.getAnnotation(field3, JSONField.class);
                                            if (jSONField4 != null) {
                                                if (!jSONField4.deserialize()) {
                                                    iOrdinal = jSONField4.ordinal();
                                                    iOf = SerializerFeature.of(jSONField4.serialzeFeatures());
                                                    iOf2 = Feature.of(jSONField4.parseFeatures());
                                                    if (jSONField4.name().length() != 0) {
                                                        add(arrayList, new FieldInfo(jSONField4.name(), method2, field3, cls, type, iOrdinal, iOf, iOf2, superMethodAnnotation, jSONField4, null, mapBuildGenericInfo));
                                                    } else {
                                                        i9 = iOf2;
                                                    }
                                                }
                                                propertyNamingStrategy3 = propertyNamingStrategy3;
                                            } else {
                                                i9 = i5;
                                            }
                                            jSONField3 = jSONField4;
                                        } else {
                                            fieldArr6 = fieldArr6;
                                            i8 = i8;
                                            str4 = str4;
                                            methodArr2 = methodArr2;
                                            jSONField3 = null;
                                            i9 = i5;
                                        }
                                        propertyNamingStrategy6 = propertyNamingStrategy3;
                                        if (propertyNamingStrategy6 != null) {
                                            propertyNameByMethodName2 = propertyNamingStrategy6.translate(propertyNameByMethodName2);
                                        }
                                        cls2 = cls2;
                                        propertyNamingStrategy3 = propertyNamingStrategy6;
                                        add(arrayList, new FieldInfo(propertyNameByMethodName2, method2, field3, cls, type, iOrdinal, iOf, i9, superMethodAnnotation, jSONField3, null, mapBuildGenericInfo));
                                    }
                                }
                            }
                            cls2 = cls2;
                            methodArr2 = methodArr2;
                            fieldArr6 = fieldArr;
                            propertyNamingStrategy3 = propertyNamingStrategy3;
                            str4 = str;
                            i8 = 0;
                        } else {
                            i6 = i2;
                            i7 = length;
                            if (parameterTypes.length == 1) {
                                if (superMethodAnnotation == null) {
                                    superMethodAnnotation = TypeUtils.getSuperMethodAnnotation(cls, method2);
                                }
                                if (superMethodAnnotation == null) {
                                    if (superMethodAnnotation != null) {
                                        if (superMethodAnnotation.deserialize()) {
                                            iOrdinal = superMethodAnnotation.ordinal();
                                            iOf = SerializerFeature.of(superMethodAnnotation.serialzeFeatures());
                                            iOf3 = Feature.of(superMethodAnnotation.parseFeatures());
                                            if (superMethodAnnotation.name().length() != 0) {
                                                add(arrayList, new FieldInfo(superMethodAnnotation.name(), method2, null, cls, type, iOrdinal, iOf, iOf3, superMethodAnnotation, null, null, mapBuildGenericInfo));
                                                cls2 = cls2;
                                                methodArr2 = methodArr2;
                                                fieldArr6 = fieldArr;
                                                str4 = str;
                                                i8 = 0;
                                            } else {
                                                i5 = iOf3;
                                            }
                                        }
                                    }
                                    str4 = str;
                                    if (superMethodAnnotation == null) {
                                        cCharAt = name2.charAt(3);
                                        if (zIsKotlin) {
                                            arrayList2 = new ArrayList();
                                            while (i10 < methodArr2.length) {
                                                if (methodArr2[i10].getName().startsWith("get")) {
                                                    arrayList2.add(methodArr2[i10].getName());
                                                }
                                            }
                                        } else {
                                            arrayList2 = null;
                                        }
                                        if (Character.isUpperCase(cCharAt)) {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        } else {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        }
                                        if (field3 == null) {
                                            field3 = TypeUtils.getField(cls3, propertyNameByMethodName2, fieldArr6);
                                        }
                                        if (field3 == null) {
                                            i8 = 0;
                                            if (parameterTypes[0] == Boolean.TYPE) {
                                                StringBuilder sb8 = new StringBuilder();
                                                sb8.append("is");
                                                sb8.append(Character.toUpperCase(propertyNameByMethodName2.charAt(0)));
                                                z5 = true;
                                                sb8.append(propertyNameByMethodName2.substring(1));
                                                field3 = TypeUtils.getField(cls3, sb8.toString(), fieldArr6);
                                            } else {
                                                z5 = true;
                                            }
                                        } else {
                                            z5 = true;
                                            i8 = 0;
                                        }
                                        if (field3 != null) {
                                            jSONField4 = (JSONField) TypeUtils.getAnnotation(field3, JSONField.class);
                                            if (jSONField4 != null) {
                                                if (!jSONField4.deserialize()) {
                                                    iOrdinal = jSONField4.ordinal();
                                                    iOf = SerializerFeature.of(jSONField4.serialzeFeatures());
                                                    iOf2 = Feature.of(jSONField4.parseFeatures());
                                                    if (jSONField4.name().length() != 0) {
                                                        add(arrayList, new FieldInfo(jSONField4.name(), method2, field3, cls, type, iOrdinal, iOf, iOf2, superMethodAnnotation, jSONField4, null, mapBuildGenericInfo));
                                                    } else {
                                                        i9 = iOf2;
                                                    }
                                                }
                                                propertyNamingStrategy3 = propertyNamingStrategy3;
                                            } else {
                                                i9 = i5;
                                            }
                                            jSONField3 = jSONField4;
                                        } else {
                                            fieldArr6 = fieldArr6;
                                            i8 = i8;
                                            str4 = str4;
                                            methodArr2 = methodArr2;
                                            jSONField3 = null;
                                            i9 = i5;
                                        }
                                        propertyNamingStrategy6 = propertyNamingStrategy3;
                                        if (propertyNamingStrategy6 != null) {
                                            propertyNameByMethodName2 = propertyNamingStrategy6.translate(propertyNameByMethodName2);
                                        }
                                        cls2 = cls2;
                                        propertyNamingStrategy3 = propertyNamingStrategy6;
                                        add(arrayList, new FieldInfo(propertyNameByMethodName2, method2, field3, cls, type, iOrdinal, iOf, i9, superMethodAnnotation, jSONField3, null, mapBuildGenericInfo));
                                    } else {
                                        cCharAt = name2.charAt(3);
                                        if (zIsKotlin) {
                                            arrayList2 = new ArrayList();
                                            while (i10 < methodArr2.length) {
                                                if (methodArr2[i10].getName().startsWith("get")) {
                                                    arrayList2.add(methodArr2[i10].getName());
                                                }
                                            }
                                        } else {
                                            arrayList2 = null;
                                        }
                                        if (Character.isUpperCase(cCharAt)) {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        } else {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        }
                                        if (field3 == null) {
                                            field3 = TypeUtils.getField(cls3, propertyNameByMethodName2, fieldArr6);
                                        }
                                        if (field3 == null) {
                                            i8 = 0;
                                            if (parameterTypes[0] == Boolean.TYPE) {
                                                StringBuilder sb9 = new StringBuilder();
                                                sb9.append("is");
                                                sb9.append(Character.toUpperCase(propertyNameByMethodName2.charAt(0)));
                                                z5 = true;
                                                sb9.append(propertyNameByMethodName2.substring(1));
                                                field3 = TypeUtils.getField(cls3, sb9.toString(), fieldArr6);
                                            } else {
                                                z5 = true;
                                            }
                                        } else {
                                            z5 = true;
                                            i8 = 0;
                                        }
                                        if (field3 != null) {
                                            jSONField4 = (JSONField) TypeUtils.getAnnotation(field3, JSONField.class);
                                            if (jSONField4 != null) {
                                                if (!jSONField4.deserialize()) {
                                                    iOrdinal = jSONField4.ordinal();
                                                    iOf = SerializerFeature.of(jSONField4.serialzeFeatures());
                                                    iOf2 = Feature.of(jSONField4.parseFeatures());
                                                    if (jSONField4.name().length() != 0) {
                                                        add(arrayList, new FieldInfo(jSONField4.name(), method2, field3, cls, type, iOrdinal, iOf, iOf2, superMethodAnnotation, jSONField4, null, mapBuildGenericInfo));
                                                    } else {
                                                        i9 = iOf2;
                                                    }
                                                }
                                                propertyNamingStrategy3 = propertyNamingStrategy3;
                                            } else {
                                                i9 = i5;
                                            }
                                            jSONField3 = jSONField4;
                                        } else {
                                            fieldArr6 = fieldArr6;
                                            i8 = i8;
                                            str4 = str4;
                                            methodArr2 = methodArr2;
                                            jSONField3 = null;
                                            i9 = i5;
                                        }
                                        propertyNamingStrategy6 = propertyNamingStrategy3;
                                        if (propertyNamingStrategy6 != null) {
                                            propertyNameByMethodName2 = propertyNamingStrategy6.translate(propertyNameByMethodName2);
                                        }
                                        cls2 = cls2;
                                        propertyNamingStrategy3 = propertyNamingStrategy6;
                                        add(arrayList, new FieldInfo(propertyNameByMethodName2, method2, field3, cls, type, iOrdinal, iOf, i9, superMethodAnnotation, jSONField3, null, mapBuildGenericInfo));
                                    }
                                } else {
                                    if (superMethodAnnotation != null) {
                                        if (superMethodAnnotation.deserialize()) {
                                            iOrdinal = superMethodAnnotation.ordinal();
                                            iOf = SerializerFeature.of(superMethodAnnotation.serialzeFeatures());
                                            iOf3 = Feature.of(superMethodAnnotation.parseFeatures());
                                            if (superMethodAnnotation.name().length() != 0) {
                                                add(arrayList, new FieldInfo(superMethodAnnotation.name(), method2, null, cls, type, iOrdinal, iOf, iOf3, superMethodAnnotation, null, null, mapBuildGenericInfo));
                                                cls2 = cls2;
                                                methodArr2 = methodArr2;
                                                fieldArr6 = fieldArr;
                                                str4 = str;
                                                i8 = 0;
                                            } else {
                                                i5 = iOf3;
                                            }
                                        }
                                    }
                                    str4 = str;
                                    if (superMethodAnnotation == null) {
                                        cCharAt = name2.charAt(3);
                                        if (zIsKotlin) {
                                            arrayList2 = new ArrayList();
                                            while (i10 < methodArr2.length) {
                                                if (methodArr2[i10].getName().startsWith("get")) {
                                                    arrayList2.add(methodArr2[i10].getName());
                                                }
                                            }
                                        } else {
                                            arrayList2 = null;
                                        }
                                        if (Character.isUpperCase(cCharAt)) {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        } else {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        }
                                        if (field3 == null) {
                                            field3 = TypeUtils.getField(cls3, propertyNameByMethodName2, fieldArr6);
                                        }
                                        if (field3 == null) {
                                            i8 = 0;
                                            if (parameterTypes[0] == Boolean.TYPE) {
                                                StringBuilder sb10 = new StringBuilder();
                                                sb10.append("is");
                                                sb10.append(Character.toUpperCase(propertyNameByMethodName2.charAt(0)));
                                                z5 = true;
                                                sb10.append(propertyNameByMethodName2.substring(1));
                                                field3 = TypeUtils.getField(cls3, sb10.toString(), fieldArr6);
                                            } else {
                                                z5 = true;
                                            }
                                        } else {
                                            z5 = true;
                                            i8 = 0;
                                        }
                                        if (field3 != null) {
                                            jSONField4 = (JSONField) TypeUtils.getAnnotation(field3, JSONField.class);
                                            if (jSONField4 != null) {
                                                if (!jSONField4.deserialize()) {
                                                    iOrdinal = jSONField4.ordinal();
                                                    iOf = SerializerFeature.of(jSONField4.serialzeFeatures());
                                                    iOf2 = Feature.of(jSONField4.parseFeatures());
                                                    if (jSONField4.name().length() != 0) {
                                                        add(arrayList, new FieldInfo(jSONField4.name(), method2, field3, cls, type, iOrdinal, iOf, iOf2, superMethodAnnotation, jSONField4, null, mapBuildGenericInfo));
                                                    } else {
                                                        i9 = iOf2;
                                                    }
                                                }
                                                propertyNamingStrategy3 = propertyNamingStrategy3;
                                            } else {
                                                i9 = i5;
                                            }
                                            jSONField3 = jSONField4;
                                        } else {
                                            fieldArr6 = fieldArr6;
                                            i8 = i8;
                                            str4 = str4;
                                            methodArr2 = methodArr2;
                                            jSONField3 = null;
                                            i9 = i5;
                                        }
                                        propertyNamingStrategy6 = propertyNamingStrategy3;
                                        if (propertyNamingStrategy6 != null) {
                                            propertyNameByMethodName2 = propertyNamingStrategy6.translate(propertyNameByMethodName2);
                                        }
                                        cls2 = cls2;
                                        propertyNamingStrategy3 = propertyNamingStrategy6;
                                        add(arrayList, new FieldInfo(propertyNameByMethodName2, method2, field3, cls, type, iOrdinal, iOf, i9, superMethodAnnotation, jSONField3, null, mapBuildGenericInfo));
                                    } else {
                                        cCharAt = name2.charAt(3);
                                        if (zIsKotlin) {
                                            arrayList2 = new ArrayList();
                                            while (i10 < methodArr2.length) {
                                                if (methodArr2[i10].getName().startsWith("get")) {
                                                    arrayList2.add(methodArr2[i10].getName());
                                                }
                                            }
                                        } else {
                                            arrayList2 = null;
                                        }
                                        if (Character.isUpperCase(cCharAt)) {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        } else {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        }
                                        if (field3 == null) {
                                            field3 = TypeUtils.getField(cls3, propertyNameByMethodName2, fieldArr6);
                                        }
                                        if (field3 == null) {
                                            i8 = 0;
                                            if (parameterTypes[0] == Boolean.TYPE) {
                                                StringBuilder sb11 = new StringBuilder();
                                                sb11.append("is");
                                                sb11.append(Character.toUpperCase(propertyNameByMethodName2.charAt(0)));
                                                z5 = true;
                                                sb11.append(propertyNameByMethodName2.substring(1));
                                                field3 = TypeUtils.getField(cls3, sb11.toString(), fieldArr6);
                                            } else {
                                                z5 = true;
                                            }
                                        } else {
                                            z5 = true;
                                            i8 = 0;
                                        }
                                        if (field3 != null) {
                                            jSONField4 = (JSONField) TypeUtils.getAnnotation(field3, JSONField.class);
                                            if (jSONField4 != null) {
                                                if (!jSONField4.deserialize()) {
                                                    iOrdinal = jSONField4.ordinal();
                                                    iOf = SerializerFeature.of(jSONField4.serialzeFeatures());
                                                    iOf2 = Feature.of(jSONField4.parseFeatures());
                                                    if (jSONField4.name().length() != 0) {
                                                        add(arrayList, new FieldInfo(jSONField4.name(), method2, field3, cls, type, iOrdinal, iOf, iOf2, superMethodAnnotation, jSONField4, null, mapBuildGenericInfo));
                                                    } else {
                                                        i9 = iOf2;
                                                    }
                                                }
                                                propertyNamingStrategy3 = propertyNamingStrategy3;
                                            } else {
                                                i9 = i5;
                                            }
                                            jSONField3 = jSONField4;
                                        } else {
                                            fieldArr6 = fieldArr6;
                                            i8 = i8;
                                            str4 = str4;
                                            methodArr2 = methodArr2;
                                            jSONField3 = null;
                                            i9 = i5;
                                        }
                                        propertyNamingStrategy6 = propertyNamingStrategy3;
                                        if (propertyNamingStrategy6 != null) {
                                            propertyNameByMethodName2 = propertyNamingStrategy6.translate(propertyNameByMethodName2);
                                        }
                                        cls2 = cls2;
                                        propertyNamingStrategy3 = propertyNamingStrategy6;
                                        add(arrayList, new FieldInfo(propertyNameByMethodName2, method2, field3, cls, type, iOrdinal, iOf, i9, superMethodAnnotation, jSONField3, null, mapBuildGenericInfo));
                                    }
                                }
                            }
                            cls2 = cls2;
                            methodArr2 = methodArr2;
                            fieldArr6 = fieldArr;
                            propertyNamingStrategy3 = propertyNamingStrategy3;
                            str4 = str;
                            i8 = 0;
                        }
                    }
                } else {
                    parameterTypes = method2.getParameterTypes();
                    if (parameterTypes.length == 0) {
                        i6 = i2;
                        i7 = length;
                        i8 = i;
                        cls2 = cls2;
                        methodArr2 = methodArr2;
                        fieldArr6 = fieldArr;
                        propertyNamingStrategy3 = propertyNamingStrategy3;
                        str4 = str;
                    } else if (parameterTypes.length > 2) {
                        i6 = i2;
                        i7 = length;
                        i8 = i;
                        cls2 = cls2;
                        methodArr2 = methodArr2;
                        fieldArr6 = fieldArr;
                        propertyNamingStrategy3 = propertyNamingStrategy3;
                        str4 = str;
                    } else {
                        superMethodAnnotation = (JSONField) TypeUtils.getAnnotation(method2, JSONField.class);
                        if (superMethodAnnotation == null) {
                            i6 = i2;
                            i7 = length;
                            if (parameterTypes.length == 1) {
                                if (superMethodAnnotation == null) {
                                    superMethodAnnotation = TypeUtils.getSuperMethodAnnotation(cls, method2);
                                }
                                if (superMethodAnnotation == null) {
                                    if (superMethodAnnotation != null) {
                                        if (superMethodAnnotation.deserialize()) {
                                            iOrdinal = superMethodAnnotation.ordinal();
                                            iOf = SerializerFeature.of(superMethodAnnotation.serialzeFeatures());
                                            iOf3 = Feature.of(superMethodAnnotation.parseFeatures());
                                            if (superMethodAnnotation.name().length() != 0) {
                                                add(arrayList, new FieldInfo(superMethodAnnotation.name(), method2, null, cls, type, iOrdinal, iOf, iOf3, superMethodAnnotation, null, null, mapBuildGenericInfo));
                                                cls2 = cls2;
                                                methodArr2 = methodArr2;
                                                fieldArr6 = fieldArr;
                                                str4 = str;
                                                i8 = 0;
                                            } else {
                                                i5 = iOf3;
                                            }
                                        }
                                    }
                                    str4 = str;
                                    if (superMethodAnnotation == null) {
                                        cCharAt = name2.charAt(3);
                                        if (zIsKotlin) {
                                            arrayList2 = new ArrayList();
                                            while (i10 < methodArr2.length) {
                                                if (methodArr2[i10].getName().startsWith("get")) {
                                                    arrayList2.add(methodArr2[i10].getName());
                                                }
                                            }
                                        } else {
                                            arrayList2 = null;
                                        }
                                        if (Character.isUpperCase(cCharAt)) {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        } else {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        }
                                        if (field3 == null) {
                                            field3 = TypeUtils.getField(cls3, propertyNameByMethodName2, fieldArr6);
                                        }
                                        if (field3 == null) {
                                            i8 = 0;
                                            if (parameterTypes[0] == Boolean.TYPE) {
                                                StringBuilder sb12 = new StringBuilder();
                                                sb12.append("is");
                                                sb12.append(Character.toUpperCase(propertyNameByMethodName2.charAt(0)));
                                                z5 = true;
                                                sb12.append(propertyNameByMethodName2.substring(1));
                                                field3 = TypeUtils.getField(cls3, sb12.toString(), fieldArr6);
                                            } else {
                                                z5 = true;
                                            }
                                        } else {
                                            z5 = true;
                                            i8 = 0;
                                        }
                                        if (field3 != null) {
                                            jSONField4 = (JSONField) TypeUtils.getAnnotation(field3, JSONField.class);
                                            if (jSONField4 != null) {
                                                if (!jSONField4.deserialize()) {
                                                    iOrdinal = jSONField4.ordinal();
                                                    iOf = SerializerFeature.of(jSONField4.serialzeFeatures());
                                                    iOf2 = Feature.of(jSONField4.parseFeatures());
                                                    if (jSONField4.name().length() != 0) {
                                                        add(arrayList, new FieldInfo(jSONField4.name(), method2, field3, cls, type, iOrdinal, iOf, iOf2, superMethodAnnotation, jSONField4, null, mapBuildGenericInfo));
                                                    } else {
                                                        i9 = iOf2;
                                                    }
                                                }
                                                propertyNamingStrategy3 = propertyNamingStrategy3;
                                            } else {
                                                i9 = i5;
                                            }
                                            jSONField3 = jSONField4;
                                        } else {
                                            fieldArr6 = fieldArr6;
                                            i8 = i8;
                                            str4 = str4;
                                            methodArr2 = methodArr2;
                                            jSONField3 = null;
                                            i9 = i5;
                                        }
                                        propertyNamingStrategy6 = propertyNamingStrategy3;
                                        if (propertyNamingStrategy6 != null) {
                                            propertyNameByMethodName2 = propertyNamingStrategy6.translate(propertyNameByMethodName2);
                                        }
                                        cls2 = cls2;
                                        propertyNamingStrategy3 = propertyNamingStrategy6;
                                        add(arrayList, new FieldInfo(propertyNameByMethodName2, method2, field3, cls, type, iOrdinal, iOf, i9, superMethodAnnotation, jSONField3, null, mapBuildGenericInfo));
                                    } else {
                                        cCharAt = name2.charAt(3);
                                        if (zIsKotlin) {
                                            arrayList2 = new ArrayList();
                                            while (i10 < methodArr2.length) {
                                                if (methodArr2[i10].getName().startsWith("get")) {
                                                    arrayList2.add(methodArr2[i10].getName());
                                                }
                                            }
                                        } else {
                                            arrayList2 = null;
                                        }
                                        if (Character.isUpperCase(cCharAt)) {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        } else {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        }
                                        if (field3 == null) {
                                            field3 = TypeUtils.getField(cls3, propertyNameByMethodName2, fieldArr6);
                                        }
                                        if (field3 == null) {
                                            i8 = 0;
                                            if (parameterTypes[0] == Boolean.TYPE) {
                                                StringBuilder sb13 = new StringBuilder();
                                                sb13.append("is");
                                                sb13.append(Character.toUpperCase(propertyNameByMethodName2.charAt(0)));
                                                z5 = true;
                                                sb13.append(propertyNameByMethodName2.substring(1));
                                                field3 = TypeUtils.getField(cls3, sb13.toString(), fieldArr6);
                                            } else {
                                                z5 = true;
                                            }
                                        } else {
                                            z5 = true;
                                            i8 = 0;
                                        }
                                        if (field3 != null) {
                                            jSONField4 = (JSONField) TypeUtils.getAnnotation(field3, JSONField.class);
                                            if (jSONField4 != null) {
                                                if (!jSONField4.deserialize()) {
                                                    iOrdinal = jSONField4.ordinal();
                                                    iOf = SerializerFeature.of(jSONField4.serialzeFeatures());
                                                    iOf2 = Feature.of(jSONField4.parseFeatures());
                                                    if (jSONField4.name().length() != 0) {
                                                        add(arrayList, new FieldInfo(jSONField4.name(), method2, field3, cls, type, iOrdinal, iOf, iOf2, superMethodAnnotation, jSONField4, null, mapBuildGenericInfo));
                                                    } else {
                                                        i9 = iOf2;
                                                    }
                                                }
                                                propertyNamingStrategy3 = propertyNamingStrategy3;
                                            } else {
                                                i9 = i5;
                                            }
                                            jSONField3 = jSONField4;
                                        } else {
                                            fieldArr6 = fieldArr6;
                                            i8 = i8;
                                            str4 = str4;
                                            methodArr2 = methodArr2;
                                            jSONField3 = null;
                                            i9 = i5;
                                        }
                                        propertyNamingStrategy6 = propertyNamingStrategy3;
                                        if (propertyNamingStrategy6 != null) {
                                            propertyNameByMethodName2 = propertyNamingStrategy6.translate(propertyNameByMethodName2);
                                        }
                                        cls2 = cls2;
                                        propertyNamingStrategy3 = propertyNamingStrategy6;
                                        add(arrayList, new FieldInfo(propertyNameByMethodName2, method2, field3, cls, type, iOrdinal, iOf, i9, superMethodAnnotation, jSONField3, null, mapBuildGenericInfo));
                                    }
                                } else {
                                    if (superMethodAnnotation != null) {
                                        if (superMethodAnnotation.deserialize()) {
                                            iOrdinal = superMethodAnnotation.ordinal();
                                            iOf = SerializerFeature.of(superMethodAnnotation.serialzeFeatures());
                                            iOf3 = Feature.of(superMethodAnnotation.parseFeatures());
                                            if (superMethodAnnotation.name().length() != 0) {
                                                add(arrayList, new FieldInfo(superMethodAnnotation.name(), method2, null, cls, type, iOrdinal, iOf, iOf3, superMethodAnnotation, null, null, mapBuildGenericInfo));
                                                cls2 = cls2;
                                                methodArr2 = methodArr2;
                                                fieldArr6 = fieldArr;
                                                str4 = str;
                                                i8 = 0;
                                            } else {
                                                i5 = iOf3;
                                            }
                                        }
                                    }
                                    str4 = str;
                                    if (superMethodAnnotation == null) {
                                        cCharAt = name2.charAt(3);
                                        if (zIsKotlin) {
                                            arrayList2 = new ArrayList();
                                            while (i10 < methodArr2.length) {
                                                if (methodArr2[i10].getName().startsWith("get")) {
                                                    arrayList2.add(methodArr2[i10].getName());
                                                }
                                            }
                                        } else {
                                            arrayList2 = null;
                                        }
                                        if (Character.isUpperCase(cCharAt)) {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        } else {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        }
                                        if (field3 == null) {
                                            field3 = TypeUtils.getField(cls3, propertyNameByMethodName2, fieldArr6);
                                        }
                                        if (field3 == null) {
                                            i8 = 0;
                                            if (parameterTypes[0] == Boolean.TYPE) {
                                                StringBuilder sb14 = new StringBuilder();
                                                sb14.append("is");
                                                sb14.append(Character.toUpperCase(propertyNameByMethodName2.charAt(0)));
                                                z5 = true;
                                                sb14.append(propertyNameByMethodName2.substring(1));
                                                field3 = TypeUtils.getField(cls3, sb14.toString(), fieldArr6);
                                            } else {
                                                z5 = true;
                                            }
                                        } else {
                                            z5 = true;
                                            i8 = 0;
                                        }
                                        if (field3 != null) {
                                            jSONField4 = (JSONField) TypeUtils.getAnnotation(field3, JSONField.class);
                                            if (jSONField4 != null) {
                                                if (!jSONField4.deserialize()) {
                                                    iOrdinal = jSONField4.ordinal();
                                                    iOf = SerializerFeature.of(jSONField4.serialzeFeatures());
                                                    iOf2 = Feature.of(jSONField4.parseFeatures());
                                                    if (jSONField4.name().length() != 0) {
                                                        add(arrayList, new FieldInfo(jSONField4.name(), method2, field3, cls, type, iOrdinal, iOf, iOf2, superMethodAnnotation, jSONField4, null, mapBuildGenericInfo));
                                                    } else {
                                                        i9 = iOf2;
                                                    }
                                                }
                                                propertyNamingStrategy3 = propertyNamingStrategy3;
                                            } else {
                                                i9 = i5;
                                            }
                                            jSONField3 = jSONField4;
                                        } else {
                                            fieldArr6 = fieldArr6;
                                            i8 = i8;
                                            str4 = str4;
                                            methodArr2 = methodArr2;
                                            jSONField3 = null;
                                            i9 = i5;
                                        }
                                        propertyNamingStrategy6 = propertyNamingStrategy3;
                                        if (propertyNamingStrategy6 != null) {
                                            propertyNameByMethodName2 = propertyNamingStrategy6.translate(propertyNameByMethodName2);
                                        }
                                        cls2 = cls2;
                                        propertyNamingStrategy3 = propertyNamingStrategy6;
                                        add(arrayList, new FieldInfo(propertyNameByMethodName2, method2, field3, cls, type, iOrdinal, iOf, i9, superMethodAnnotation, jSONField3, null, mapBuildGenericInfo));
                                    } else {
                                        cCharAt = name2.charAt(3);
                                        if (zIsKotlin) {
                                            arrayList2 = new ArrayList();
                                            while (i10 < methodArr2.length) {
                                                if (methodArr2[i10].getName().startsWith("get")) {
                                                    arrayList2.add(methodArr2[i10].getName());
                                                }
                                            }
                                        } else {
                                            arrayList2 = null;
                                        }
                                        if (Character.isUpperCase(cCharAt)) {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        } else {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        }
                                        if (field3 == null) {
                                            field3 = TypeUtils.getField(cls3, propertyNameByMethodName2, fieldArr6);
                                        }
                                        if (field3 == null) {
                                            i8 = 0;
                                            if (parameterTypes[0] == Boolean.TYPE) {
                                                StringBuilder sb15 = new StringBuilder();
                                                sb15.append("is");
                                                sb15.append(Character.toUpperCase(propertyNameByMethodName2.charAt(0)));
                                                z5 = true;
                                                sb15.append(propertyNameByMethodName2.substring(1));
                                                field3 = TypeUtils.getField(cls3, sb15.toString(), fieldArr6);
                                            } else {
                                                z5 = true;
                                            }
                                        } else {
                                            z5 = true;
                                            i8 = 0;
                                        }
                                        if (field3 != null) {
                                            jSONField4 = (JSONField) TypeUtils.getAnnotation(field3, JSONField.class);
                                            if (jSONField4 != null) {
                                                if (!jSONField4.deserialize()) {
                                                    iOrdinal = jSONField4.ordinal();
                                                    iOf = SerializerFeature.of(jSONField4.serialzeFeatures());
                                                    iOf2 = Feature.of(jSONField4.parseFeatures());
                                                    if (jSONField4.name().length() != 0) {
                                                        add(arrayList, new FieldInfo(jSONField4.name(), method2, field3, cls, type, iOrdinal, iOf, iOf2, superMethodAnnotation, jSONField4, null, mapBuildGenericInfo));
                                                    } else {
                                                        i9 = iOf2;
                                                    }
                                                }
                                                propertyNamingStrategy3 = propertyNamingStrategy3;
                                            } else {
                                                i9 = i5;
                                            }
                                            jSONField3 = jSONField4;
                                        } else {
                                            fieldArr6 = fieldArr6;
                                            i8 = i8;
                                            str4 = str4;
                                            methodArr2 = methodArr2;
                                            jSONField3 = null;
                                            i9 = i5;
                                        }
                                        propertyNamingStrategy6 = propertyNamingStrategy3;
                                        if (propertyNamingStrategy6 != null) {
                                            propertyNameByMethodName2 = propertyNamingStrategy6.translate(propertyNameByMethodName2);
                                        }
                                        cls2 = cls2;
                                        propertyNamingStrategy3 = propertyNamingStrategy6;
                                        add(arrayList, new FieldInfo(propertyNameByMethodName2, method2, field3, cls, type, iOrdinal, iOf, i9, superMethodAnnotation, jSONField3, null, mapBuildGenericInfo));
                                    }
                                }
                            }
                            cls2 = cls2;
                            methodArr2 = methodArr2;
                            fieldArr6 = fieldArr;
                            propertyNamingStrategy3 = propertyNamingStrategy3;
                            str4 = str;
                            i8 = 0;
                        } else {
                            i6 = i2;
                            i7 = length;
                            if (parameterTypes.length == 1) {
                                if (superMethodAnnotation == null) {
                                    superMethodAnnotation = TypeUtils.getSuperMethodAnnotation(cls, method2);
                                }
                                if (superMethodAnnotation == null) {
                                    if (superMethodAnnotation != null) {
                                        if (superMethodAnnotation.deserialize()) {
                                            iOrdinal = superMethodAnnotation.ordinal();
                                            iOf = SerializerFeature.of(superMethodAnnotation.serialzeFeatures());
                                            iOf3 = Feature.of(superMethodAnnotation.parseFeatures());
                                            if (superMethodAnnotation.name().length() != 0) {
                                                add(arrayList, new FieldInfo(superMethodAnnotation.name(), method2, null, cls, type, iOrdinal, iOf, iOf3, superMethodAnnotation, null, null, mapBuildGenericInfo));
                                                cls2 = cls2;
                                                methodArr2 = methodArr2;
                                                fieldArr6 = fieldArr;
                                                str4 = str;
                                                i8 = 0;
                                            } else {
                                                i5 = iOf3;
                                            }
                                        }
                                    }
                                    str4 = str;
                                    if (superMethodAnnotation == null) {
                                        cCharAt = name2.charAt(3);
                                        if (zIsKotlin) {
                                            arrayList2 = new ArrayList();
                                            while (i10 < methodArr2.length) {
                                                if (methodArr2[i10].getName().startsWith("get")) {
                                                    arrayList2.add(methodArr2[i10].getName());
                                                }
                                            }
                                        } else {
                                            arrayList2 = null;
                                        }
                                        if (Character.isUpperCase(cCharAt)) {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        } else {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        }
                                        if (field3 == null) {
                                            field3 = TypeUtils.getField(cls3, propertyNameByMethodName2, fieldArr6);
                                        }
                                        if (field3 == null) {
                                            i8 = 0;
                                            if (parameterTypes[0] == Boolean.TYPE) {
                                                StringBuilder sb16 = new StringBuilder();
                                                sb16.append("is");
                                                sb16.append(Character.toUpperCase(propertyNameByMethodName2.charAt(0)));
                                                z5 = true;
                                                sb16.append(propertyNameByMethodName2.substring(1));
                                                field3 = TypeUtils.getField(cls3, sb16.toString(), fieldArr6);
                                            } else {
                                                z5 = true;
                                            }
                                        } else {
                                            z5 = true;
                                            i8 = 0;
                                        }
                                        if (field3 != null) {
                                            jSONField4 = (JSONField) TypeUtils.getAnnotation(field3, JSONField.class);
                                            if (jSONField4 != null) {
                                                if (!jSONField4.deserialize()) {
                                                    iOrdinal = jSONField4.ordinal();
                                                    iOf = SerializerFeature.of(jSONField4.serialzeFeatures());
                                                    iOf2 = Feature.of(jSONField4.parseFeatures());
                                                    if (jSONField4.name().length() != 0) {
                                                        add(arrayList, new FieldInfo(jSONField4.name(), method2, field3, cls, type, iOrdinal, iOf, iOf2, superMethodAnnotation, jSONField4, null, mapBuildGenericInfo));
                                                    } else {
                                                        i9 = iOf2;
                                                    }
                                                }
                                                propertyNamingStrategy3 = propertyNamingStrategy3;
                                            } else {
                                                i9 = i5;
                                            }
                                            jSONField3 = jSONField4;
                                        } else {
                                            fieldArr6 = fieldArr6;
                                            i8 = i8;
                                            str4 = str4;
                                            methodArr2 = methodArr2;
                                            jSONField3 = null;
                                            i9 = i5;
                                        }
                                        propertyNamingStrategy6 = propertyNamingStrategy3;
                                        if (propertyNamingStrategy6 != null) {
                                            propertyNameByMethodName2 = propertyNamingStrategy6.translate(propertyNameByMethodName2);
                                        }
                                        cls2 = cls2;
                                        propertyNamingStrategy3 = propertyNamingStrategy6;
                                        add(arrayList, new FieldInfo(propertyNameByMethodName2, method2, field3, cls, type, iOrdinal, iOf, i9, superMethodAnnotation, jSONField3, null, mapBuildGenericInfo));
                                    } else {
                                        cCharAt = name2.charAt(3);
                                        if (zIsKotlin) {
                                            arrayList2 = new ArrayList();
                                            while (i10 < methodArr2.length) {
                                                if (methodArr2[i10].getName().startsWith("get")) {
                                                    arrayList2.add(methodArr2[i10].getName());
                                                }
                                            }
                                        } else {
                                            arrayList2 = null;
                                        }
                                        if (Character.isUpperCase(cCharAt)) {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        } else {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        }
                                        if (field3 == null) {
                                            field3 = TypeUtils.getField(cls3, propertyNameByMethodName2, fieldArr6);
                                        }
                                        if (field3 == null) {
                                            i8 = 0;
                                            if (parameterTypes[0] == Boolean.TYPE) {
                                                StringBuilder sb17 = new StringBuilder();
                                                sb17.append("is");
                                                sb17.append(Character.toUpperCase(propertyNameByMethodName2.charAt(0)));
                                                z5 = true;
                                                sb17.append(propertyNameByMethodName2.substring(1));
                                                field3 = TypeUtils.getField(cls3, sb17.toString(), fieldArr6);
                                            } else {
                                                z5 = true;
                                            }
                                        } else {
                                            z5 = true;
                                            i8 = 0;
                                        }
                                        if (field3 != null) {
                                            jSONField4 = (JSONField) TypeUtils.getAnnotation(field3, JSONField.class);
                                            if (jSONField4 != null) {
                                                if (!jSONField4.deserialize()) {
                                                    iOrdinal = jSONField4.ordinal();
                                                    iOf = SerializerFeature.of(jSONField4.serialzeFeatures());
                                                    iOf2 = Feature.of(jSONField4.parseFeatures());
                                                    if (jSONField4.name().length() != 0) {
                                                        add(arrayList, new FieldInfo(jSONField4.name(), method2, field3, cls, type, iOrdinal, iOf, iOf2, superMethodAnnotation, jSONField4, null, mapBuildGenericInfo));
                                                    } else {
                                                        i9 = iOf2;
                                                    }
                                                }
                                                propertyNamingStrategy3 = propertyNamingStrategy3;
                                            } else {
                                                i9 = i5;
                                            }
                                            jSONField3 = jSONField4;
                                        } else {
                                            fieldArr6 = fieldArr6;
                                            i8 = i8;
                                            str4 = str4;
                                            methodArr2 = methodArr2;
                                            jSONField3 = null;
                                            i9 = i5;
                                        }
                                        propertyNamingStrategy6 = propertyNamingStrategy3;
                                        if (propertyNamingStrategy6 != null) {
                                            propertyNameByMethodName2 = propertyNamingStrategy6.translate(propertyNameByMethodName2);
                                        }
                                        cls2 = cls2;
                                        propertyNamingStrategy3 = propertyNamingStrategy6;
                                        add(arrayList, new FieldInfo(propertyNameByMethodName2, method2, field3, cls, type, iOrdinal, iOf, i9, superMethodAnnotation, jSONField3, null, mapBuildGenericInfo));
                                    }
                                } else {
                                    if (superMethodAnnotation != null) {
                                        if (superMethodAnnotation.deserialize()) {
                                            iOrdinal = superMethodAnnotation.ordinal();
                                            iOf = SerializerFeature.of(superMethodAnnotation.serialzeFeatures());
                                            iOf3 = Feature.of(superMethodAnnotation.parseFeatures());
                                            if (superMethodAnnotation.name().length() != 0) {
                                                add(arrayList, new FieldInfo(superMethodAnnotation.name(), method2, null, cls, type, iOrdinal, iOf, iOf3, superMethodAnnotation, null, null, mapBuildGenericInfo));
                                                cls2 = cls2;
                                                methodArr2 = methodArr2;
                                                fieldArr6 = fieldArr;
                                                str4 = str;
                                                i8 = 0;
                                            } else {
                                                i5 = iOf3;
                                            }
                                        }
                                    }
                                    str4 = str;
                                    if (superMethodAnnotation == null) {
                                        cCharAt = name2.charAt(3);
                                        if (zIsKotlin) {
                                            arrayList2 = new ArrayList();
                                            while (i10 < methodArr2.length) {
                                                if (methodArr2[i10].getName().startsWith("get")) {
                                                    arrayList2.add(methodArr2[i10].getName());
                                                }
                                            }
                                        } else {
                                            arrayList2 = null;
                                        }
                                        if (Character.isUpperCase(cCharAt)) {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        } else {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        }
                                        if (field3 == null) {
                                            field3 = TypeUtils.getField(cls3, propertyNameByMethodName2, fieldArr6);
                                        }
                                        if (field3 == null) {
                                            i8 = 0;
                                            if (parameterTypes[0] == Boolean.TYPE) {
                                                StringBuilder sb18 = new StringBuilder();
                                                sb18.append("is");
                                                sb18.append(Character.toUpperCase(propertyNameByMethodName2.charAt(0)));
                                                z5 = true;
                                                sb18.append(propertyNameByMethodName2.substring(1));
                                                field3 = TypeUtils.getField(cls3, sb18.toString(), fieldArr6);
                                            } else {
                                                z5 = true;
                                            }
                                        } else {
                                            z5 = true;
                                            i8 = 0;
                                        }
                                        if (field3 != null) {
                                            jSONField4 = (JSONField) TypeUtils.getAnnotation(field3, JSONField.class);
                                            if (jSONField4 != null) {
                                                if (!jSONField4.deserialize()) {
                                                    iOrdinal = jSONField4.ordinal();
                                                    iOf = SerializerFeature.of(jSONField4.serialzeFeatures());
                                                    iOf2 = Feature.of(jSONField4.parseFeatures());
                                                    if (jSONField4.name().length() != 0) {
                                                        add(arrayList, new FieldInfo(jSONField4.name(), method2, field3, cls, type, iOrdinal, iOf, iOf2, superMethodAnnotation, jSONField4, null, mapBuildGenericInfo));
                                                    } else {
                                                        i9 = iOf2;
                                                    }
                                                }
                                                propertyNamingStrategy3 = propertyNamingStrategy3;
                                            } else {
                                                i9 = i5;
                                            }
                                            jSONField3 = jSONField4;
                                        } else {
                                            fieldArr6 = fieldArr6;
                                            i8 = i8;
                                            str4 = str4;
                                            methodArr2 = methodArr2;
                                            jSONField3 = null;
                                            i9 = i5;
                                        }
                                        propertyNamingStrategy6 = propertyNamingStrategy3;
                                        if (propertyNamingStrategy6 != null) {
                                            propertyNameByMethodName2 = propertyNamingStrategy6.translate(propertyNameByMethodName2);
                                        }
                                        cls2 = cls2;
                                        propertyNamingStrategy3 = propertyNamingStrategy6;
                                        add(arrayList, new FieldInfo(propertyNameByMethodName2, method2, field3, cls, type, iOrdinal, iOf, i9, superMethodAnnotation, jSONField3, null, mapBuildGenericInfo));
                                    } else {
                                        cCharAt = name2.charAt(3);
                                        if (zIsKotlin) {
                                            arrayList2 = new ArrayList();
                                            while (i10 < methodArr2.length) {
                                                if (methodArr2[i10].getName().startsWith("get")) {
                                                    arrayList2.add(methodArr2[i10].getName());
                                                }
                                            }
                                        } else {
                                            arrayList2 = null;
                                        }
                                        if (Character.isUpperCase(cCharAt)) {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        } else {
                                            cls3 = cls;
                                            fieldArr6 = fieldArr;
                                            if (zIsKotlin) {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName("g" + name2.substring(1));
                                            } else if (TypeUtils.compatibleWithJavaBean) {
                                                propertyNameByMethodName2 = TypeUtils.decapitalize(name2.substring(3));
                                            } else {
                                                propertyNameByMethodName2 = TypeUtils.getPropertyNameByMethodName(name2);
                                            }
                                            field3 = null;
                                        }
                                        if (field3 == null) {
                                            field3 = TypeUtils.getField(cls3, propertyNameByMethodName2, fieldArr6);
                                        }
                                        if (field3 == null) {
                                            i8 = 0;
                                            if (parameterTypes[0] == Boolean.TYPE) {
                                                StringBuilder sb19 = new StringBuilder();
                                                sb19.append("is");
                                                sb19.append(Character.toUpperCase(propertyNameByMethodName2.charAt(0)));
                                                z5 = true;
                                                sb19.append(propertyNameByMethodName2.substring(1));
                                                field3 = TypeUtils.getField(cls3, sb19.toString(), fieldArr6);
                                            } else {
                                                z5 = true;
                                            }
                                        } else {
                                            z5 = true;
                                            i8 = 0;
                                        }
                                        if (field3 != null) {
                                            jSONField4 = (JSONField) TypeUtils.getAnnotation(field3, JSONField.class);
                                            if (jSONField4 != null) {
                                                if (!jSONField4.deserialize()) {
                                                    iOrdinal = jSONField4.ordinal();
                                                    iOf = SerializerFeature.of(jSONField4.serialzeFeatures());
                                                    iOf2 = Feature.of(jSONField4.parseFeatures());
                                                    if (jSONField4.name().length() != 0) {
                                                        add(arrayList, new FieldInfo(jSONField4.name(), method2, field3, cls, type, iOrdinal, iOf, iOf2, superMethodAnnotation, jSONField4, null, mapBuildGenericInfo));
                                                    } else {
                                                        i9 = iOf2;
                                                    }
                                                }
                                                propertyNamingStrategy3 = propertyNamingStrategy3;
                                            } else {
                                                i9 = i5;
                                            }
                                            jSONField3 = jSONField4;
                                        } else {
                                            fieldArr6 = fieldArr6;
                                            i8 = i8;
                                            str4 = str4;
                                            methodArr2 = methodArr2;
                                            jSONField3 = null;
                                            i9 = i5;
                                        }
                                        propertyNamingStrategy6 = propertyNamingStrategy3;
                                        if (propertyNamingStrategy6 != null) {
                                            propertyNameByMethodName2 = propertyNamingStrategy6.translate(propertyNameByMethodName2);
                                        }
                                        cls2 = cls2;
                                        propertyNamingStrategy3 = propertyNamingStrategy6;
                                        add(arrayList, new FieldInfo(propertyNameByMethodName2, method2, field3, cls, type, iOrdinal, iOf, i9, superMethodAnnotation, jSONField3, null, mapBuildGenericInfo));
                                    }
                                }
                            }
                            cls2 = cls2;
                            methodArr2 = methodArr2;
                            fieldArr6 = fieldArr;
                            propertyNamingStrategy3 = propertyNamingStrategy3;
                            str4 = str;
                            i8 = 0;
                        }
                    }
                }
            }
            i2 = i6 + 1;
            propertyNamingStrategy3 = propertyNamingStrategy3;
            length = i7;
            i = i8;
            methodArr2 = methodArr2;
            str = str4;
            cls2 = cls2;
            fieldArr = fieldArr6;
        }
        int i28 = i;
        Class<?> cls8 = cls2;
        fieldArr2 = fieldArr;
        propertyNamingStrategy4 = propertyNamingStrategy3;
        type2 = type;
        computeFields(cls, type2, propertyNamingStrategy4, arrayList, cls.getFields());
        methods = cls.getMethods();
        length2 = methods.length;
        i4 = i28;
        while (i4 < length2) {
            method = methods[i4];
            name = method.getName();
            if (name.length() < i3) {
                fieldArr4 = fieldArr2;
            } else {
                if (jSONField == null) {
                    propertyNameByMethodName = TypeUtils.getPropertyNameByMethodName(name);
                    fieldArr5 = fieldArr2;
                    field = TypeUtils.getField(cls, propertyNameByMethodName, fieldArr5);
                    if (field == null) {
                        jSONField2 = (JSONField) TypeUtils.getAnnotation(field, JSONField.class);
                        if (jSONField2 != null) {
                        }
                        if (Collection.class.isAssignableFrom(method.getReturnType())) {
                        }
                        field2 = field;
                        if (propertyNamingStrategy4 != null) {
                            propertyNameByMethodName = propertyNamingStrategy4.translate(propertyNameByMethodName);
                        }
                        str3 = propertyNameByMethodName;
                        if (getField(arrayList, str3) != null) {
                            fieldArr4 = fieldArr5;
                            i4 = i4;
                            i3 = i3;
                            str2 = str2;
                            length2 = length2;
                            methods = methods;
                            propertyNamingStrategy4 = propertyNamingStrategy4;
                            add(arrayList, new FieldInfo(str3, method, field2, cls, type, 0, 0, 0, jSONField, null, null, mapBuildGenericInfo));
                        }
                    } else {
                        field2 = null;
                        if (propertyNamingStrategy4 != null) {
                            propertyNameByMethodName = propertyNamingStrategy4.translate(propertyNameByMethodName);
                        }
                        str3 = propertyNameByMethodName;
                        if (getField(arrayList, str3) != null) {
                            fieldArr4 = fieldArr5;
                            i4 = i4;
                            i3 = i3;
                            str2 = str2;
                            length2 = length2;
                            methods = methods;
                            propertyNamingStrategy4 = propertyNamingStrategy4;
                            add(arrayList, new FieldInfo(str3, method, field2, cls, type, 0, 0, 0, jSONField, null, null, mapBuildGenericInfo));
                        }
                    }
                } else {
                    propertyNameByMethodName = TypeUtils.getPropertyNameByMethodName(name);
                    fieldArr5 = fieldArr2;
                    field = TypeUtils.getField(cls, propertyNameByMethodName, fieldArr5);
                    if (field == null) {
                        jSONField2 = (JSONField) TypeUtils.getAnnotation(field, JSONField.class);
                        if (jSONField2 != null) {
                        }
                        if (Collection.class.isAssignableFrom(method.getReturnType())) {
                        }
                        field2 = field;
                        if (propertyNamingStrategy4 != null) {
                            propertyNameByMethodName = propertyNamingStrategy4.translate(propertyNameByMethodName);
                        }
                        str3 = propertyNameByMethodName;
                        if (getField(arrayList, str3) != null) {
                            fieldArr4 = fieldArr5;
                            i4 = i4;
                            i3 = i3;
                            str2 = str2;
                            length2 = length2;
                            methods = methods;
                            propertyNamingStrategy4 = propertyNamingStrategy4;
                            add(arrayList, new FieldInfo(str3, method, field2, cls, type, 0, 0, 0, jSONField, null, null, mapBuildGenericInfo));
                        }
                    } else {
                        field2 = null;
                        if (propertyNamingStrategy4 != null) {
                            propertyNameByMethodName = propertyNamingStrategy4.translate(propertyNameByMethodName);
                        }
                        str3 = propertyNameByMethodName;
                        if (getField(arrayList, str3) != null) {
                            fieldArr4 = fieldArr5;
                            i4 = i4;
                            i3 = i3;
                            str2 = str2;
                            length2 = length2;
                            methods = methods;
                            propertyNamingStrategy4 = propertyNamingStrategy4;
                            add(arrayList, new FieldInfo(str3, method, field2, cls, type, 0, 0, 0, jSONField, null, null, mapBuildGenericInfo));
                        }
                    }
                }
                fieldArr4 = fieldArr5;
            }
            i4++;
            type2 = type2;
            length2 = length2;
            i3 = i3;
            str2 = str2;
            methods = methods;
            propertyNamingStrategy4 = propertyNamingStrategy4;
            fieldArr2 = fieldArr4;
        }
        propertyNamingStrategy5 = propertyNamingStrategy4;
        fieldArr3 = fieldArr2;
        type3 = type2;
        if (arrayList.size() == 0) {
            if (TypeUtils.isXmlField(cls) ? true : z) {
                while (superclass != null) {
                    computeFields(cls, type3, propertyNamingStrategy5, arrayList, fieldArr3);
                }
            }
        }
        return new JavaBeanInfo(cls, cls8, constructor, constructor2, factoryMethod, method3, jSONType, arrayList);
    }

    /* JADX WARN: Code duplicated, block: B:24:0x005a  */
    /* JADX WARN: Code duplicated, block: B:27:0x0064  */
    /* JADX WARN: Code duplicated, block: B:33:0x007c  */
    /* JADX WARN: Code duplicated, block: B:35:0x008b  */
    /* JADX WARN: Code duplicated, block: B:38:0x0092  */
    /* JADX WARN: Code duplicated, block: B:40:0x00b0  */
    /* JADX WARN: Code duplicated, block: B:42:0x00b8  */
    /* JADX WARN: Code duplicated, block: B:44:0x00be  */
    /* JADX WARN: Code duplicated, block: B:51:0x0077 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:6:0x0019  */
    private static void computeFields(Class<?> cls, Type type, PropertyNamingStrategy propertyNamingStrategy, List<FieldInfo> list, Field[] fieldArr) {
        Iterator<FieldInfo> it;
        String name;
        JSONField jSONField;
        int i;
        int i2;
        int i3;
        Map<TypeVariable, Type> mapBuildGenericInfo = buildGenericInfo(cls);
        int i4 = 0;
        for (int length = fieldArr.length; i4 < length; length = length) {
            Field field = fieldArr[i4];
            int modifiers = field.getModifiers();
            if ((modifiers & 8) == 0) {
                boolean z = true;
                if ((modifiers & 16) != 0) {
                    Class<?> type2 = field.getType();
                    if (Map.class.isAssignableFrom(type2) || Collection.class.isAssignableFrom(type2) || AtomicLong.class.equals(type2) || AtomicInteger.class.equals(type2) || AtomicBoolean.class.equals(type2)) {
                        it = list.iterator();
                        do {
                            if (it.hasNext()) {
                                z = false;
                                break;
                            }
                        } while (!it.next().name.equals(field.getName()));
                        if (z) {
                            name = field.getName();
                            jSONField = (JSONField) TypeUtils.getAnnotation(field, JSONField.class);
                            if (jSONField != null) {
                                i = 0;
                                i2 = 0;
                                i3 = 0;
                            } else if (!jSONField.deserialize()) {
                                int iOrdinal = jSONField.ordinal();
                                int iOf = SerializerFeature.of(jSONField.serialzeFeatures());
                                int iOf2 = Feature.of(jSONField.parseFeatures());
                                if (jSONField.name().length() != 0) {
                                    name = jSONField.name();
                                }
                                i = iOrdinal;
                                i2 = iOf;
                                i3 = iOf2;
                            }
                            if (propertyNamingStrategy != null) {
                                name = propertyNamingStrategy.translate(name);
                            }
                            add(list, new FieldInfo(name, null, field, cls, type, i, i2, i3, null, jSONField, null, mapBuildGenericInfo));
                        }
                    }
                } else {
                    it = list.iterator();
                    do {
                        if (it.hasNext()) {
                            z = false;
                            break;
                        }
                    } while (!it.next().name.equals(field.getName()));
                    if (z) {
                        name = field.getName();
                        jSONField = (JSONField) TypeUtils.getAnnotation(field, JSONField.class);
                        if (jSONField != null) {
                            i = 0;
                            i2 = 0;
                            i3 = 0;
                        } else if (!jSONField.deserialize()) {
                            int iOrdinal2 = jSONField.ordinal();
                            int iOf3 = SerializerFeature.of(jSONField.serialzeFeatures());
                            int iOf4 = Feature.of(jSONField.parseFeatures());
                            if (jSONField.name().length() != 0) {
                                name = jSONField.name();
                            }
                            i = iOrdinal2;
                            i2 = iOf3;
                            i3 = iOf4;
                        }
                        if (propertyNamingStrategy != null) {
                            name = propertyNamingStrategy.translate(name);
                        }
                        add(list, new FieldInfo(name, null, field, cls, type, i, i2, i3, null, jSONField, null, mapBuildGenericInfo));
                    }
                }
            }
            i4++;
        }
    }

    static Constructor<?> getDefaultConstructor(Class<?> cls, Constructor<?>[] constructorArr) {
        Constructor<?> constructor = null;
        if (Modifier.isAbstract(cls.getModifiers())) {
            return null;
        }
        for (Constructor<?> constructor2 : constructorArr) {
            if (constructor2.getParameterTypes().length == 0) {
                constructor = constructor2;
                break;
            }
        }
        if (constructor != null || !cls.isMemberClass() || Modifier.isStatic(cls.getModifiers())) {
            return constructor;
        }
        for (Constructor<?> constructor3 : constructorArr) {
            Class<?>[] parameterTypes = constructor3.getParameterTypes();
            if (parameterTypes.length == 1 && parameterTypes[0].equals(cls.getDeclaringClass())) {
                return constructor3;
            }
        }
        return constructor;
    }

    public static Constructor<?> getCreatorConstructor(Constructor[] constructorArr) {
        boolean z;
        Constructor constructor = null;
        for (Constructor constructor2 : constructorArr) {
            if (((JSONCreator) constructor2.getAnnotation(JSONCreator.class)) != null) {
                if (constructor != null) {
                    throw new JSONException("multi-JSONCreator");
                }
                constructor = constructor2;
            }
        }
        if (constructor != null) {
            return constructor;
        }
        for (Constructor constructor3 : constructorArr) {
            Annotation[][] parameterAnnotations = TypeUtils.getParameterAnnotations(constructor3);
            if (parameterAnnotations.length != 0) {
                int length = parameterAnnotations.length;
                int i = 0;
                while (true) {
                    z = true;
                    if (i >= length) {
                        break;
                    }
                    Annotation[] annotationArr = parameterAnnotations[i];
                    int length2 = annotationArr.length;
                    int i2 = 0;
                    while (true) {
                        if (i2 >= length2) {
                            z = false;
                            break;
                        }
                        if (annotationArr[i2] instanceof JSONField) {
                            break;
                        }
                        i2++;
                    }
                    if (!z) {
                        z = false;
                        break;
                    }
                    i++;
                }
                if (!z) {
                    continue;
                } else {
                    if (constructor != null) {
                        throw new JSONException("multi-JSONCreator");
                    }
                    constructor = constructor3;
                }
            }
        }
        return constructor;
    }

    private static Method getFactoryMethod(Class<?> cls, Method[] methodArr, boolean z) {
        Method method = null;
        for (Method method2 : methodArr) {
            if (Modifier.isStatic(method2.getModifiers()) && cls.isAssignableFrom(method2.getReturnType()) && ((JSONCreator) TypeUtils.getAnnotation(method2, JSONCreator.class)) != null) {
                if (method != null) {
                    throw new JSONException("multi-JSONCreator");
                }
                method = method2;
            }
        }
        if (method != null || !z) {
            return method;
        }
        for (Method method3 : methodArr) {
            if (TypeUtils.isJacksonCreator(method3)) {
                return method3;
            }
        }
        return method;
    }

    public static Class<?> getBuilderClass(JSONType jSONType) {
        return getBuilderClass(null, jSONType);
    }

    public static Class<?> getBuilderClass(Class<?> cls, JSONType jSONType) {
        Class<?> clsBuilder;
        if (cls != null && cls.getName().equals("org.springframework.security.web.savedrequest.DefaultSavedRequest")) {
            return TypeUtils.loadClass("org.springframework.security.web.savedrequest.DefaultSavedRequest$Builder");
        }
        if (jSONType == null || (clsBuilder = jSONType.builder()) == Void.class) {
            return null;
        }
        return clsBuilder;
    }
}
