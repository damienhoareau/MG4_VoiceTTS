package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.util.TypeUtils;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public final class ListSerializer implements ObjectSerializer {
    public static final ListSerializer instance = new ListSerializer();

    @Override // com.alibaba.fastjson.serializer.ObjectSerializer
    public final void write(JSONSerializer jSONSerializer, Object obj, Object obj2, Type type, int i) throws IOException {
        char c;
        boolean z = jSONSerializer.out.isEnabled(SerializerFeature.WriteClassName) || SerializerFeature.isEnabled(i, SerializerFeature.WriteClassName);
        SerializeWriter serializeWriter = jSONSerializer.out;
        Type collectionItemType = z ? TypeUtils.getCollectionItemType(type) : null;
        if (obj == null) {
            serializeWriter.writeNull(SerializerFeature.WriteNullListAsEmpty);
            return;
        }
        List list = (List) obj;
        if (list.size() == 0) {
            serializeWriter.append((CharSequence) "[]");
            return;
        }
        SerialContext serialContext = jSONSerializer.context;
        jSONSerializer.setContext(serialContext, obj, obj2, 0);
        try {
            char c2 = ',';
            char c3 = ']';
            if (serializeWriter.isEnabled(SerializerFeature.PrettyFormat)) {
                serializeWriter.append('[');
                jSONSerializer.incrementIndent();
                int i2 = 0;
                for (Object obj3 : list) {
                    if (i2 != 0) {
                        serializeWriter.append(c2);
                    }
                    jSONSerializer.println();
                    if (obj3 != null) {
                        if (jSONSerializer.containsReference(obj3)) {
                            jSONSerializer.writeReference(obj3);
                        } else {
                            ObjectSerializer objectWriter = jSONSerializer.getObjectWriter(obj3.getClass());
                            jSONSerializer.context = new SerialContext(serialContext, obj, obj2, 0, 0);
                            objectWriter.write(jSONSerializer, obj3, Integer.valueOf(i2), collectionItemType, i);
                        }
                    } else {
                        jSONSerializer.out.writeNull();
                    }
                    i2++;
                    c3 = c3;
                    c2 = ',';
                }
                jSONSerializer.decrementIdent();
                jSONSerializer.println();
                serializeWriter.append(c3);
                return;
            }
            char c4 = ']';
            serializeWriter.append('[');
            int size = list.size();
            int i3 = 0;
            while (i3 < size) {
                Object obj4 = list.get(i3);
                if (i3 != 0) {
                    c = ',';
                    serializeWriter.append(',');
                } else {
                    c = ',';
                }
                if (obj4 == null) {
                    serializeWriter.append((CharSequence) "null");
                } else {
                    Class<?> cls = obj4.getClass();
                    if (cls == Integer.class) {
                        serializeWriter.writeInt(((Integer) obj4).intValue());
                    } else {
                        if (cls == Long.class) {
                            long jLongValue = ((Long) obj4).longValue();
                            if (z) {
                                serializeWriter.writeLong(jLongValue);
                                serializeWriter.write(76);
                            } else {
                                serializeWriter.writeLong(jLongValue);
                            }
                        } else if ((SerializerFeature.DisableCircularReferenceDetect.mask & i) != 0) {
                            jSONSerializer.getObjectWriter(obj4.getClass()).write(jSONSerializer, obj4, Integer.valueOf(i3), collectionItemType, i);
                            z = z;
                        } else {
                            i3 = i3;
                            if (!serializeWriter.disableCircularReferenceDetect) {
                                jSONSerializer.context = new SerialContext(serialContext, obj, obj2, 0, 0);
                            }
                            if (jSONSerializer.containsReference(obj4)) {
                                jSONSerializer.writeReference(obj4);
                            } else {
                                ObjectSerializer objectWriter2 = jSONSerializer.getObjectWriter(obj4.getClass());
                                if ((SerializerFeature.WriteClassName.mask & i) != 0 && (objectWriter2 instanceof JavaBeanSerializer)) {
                                    ((JavaBeanSerializer) objectWriter2).writeNoneASM(jSONSerializer, obj4, Integer.valueOf(i3), collectionItemType, i);
                                } else {
                                    objectWriter2.write(jSONSerializer, obj4, Integer.valueOf(i3), collectionItemType, i);
                                }
                            }
                        }
                        i3++;
                        z = z;
                        c4 = ']';
                    }
                }
                z = z;
                i3++;
                z = z;
                c4 = ']';
            }
            serializeWriter.append(c4);
        } finally {
            jSONSerializer.context = serialContext;
        }
    }
}
