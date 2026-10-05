package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalHelpers;
import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.utils.TypeReference;
import android.util.Rational;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes.dex */
public final class MarshalQueryablePrimitive<T> implements MarshalQueryable<T> {

    private class MarshalerPrimitive extends Marshaler<T> {
        private final Class<T> mClass;

        protected MarshalerPrimitive(TypeReference<T> typeReference, int i) {
            super(MarshalQueryablePrimitive.this, typeReference, i);
            this.mClass = MarshalHelpers.wrapClassIfPrimitive(typeReference.getRawType());
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public T unmarshal(ByteBuffer byteBuffer) {
            return this.mClass.cast(unmarshalObject(byteBuffer));
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public int calculateMarshalSize(T t) {
            return MarshalHelpers.getPrimitiveTypeSize(this.mNativeType);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.hardware.camera2.marshal.Marshaler
        public void marshal(T t, ByteBuffer byteBuffer) {
            if (t instanceof Integer) {
                MarshalHelpers.checkNativeTypeEquals(1, this.mNativeType);
                marshalPrimitive(((Integer) t).intValue(), byteBuffer);
                return;
            }
            if (t instanceof Float) {
                MarshalHelpers.checkNativeTypeEquals(2, this.mNativeType);
                marshalPrimitive(((Float) t).floatValue(), byteBuffer);
                return;
            }
            if (t instanceof Long) {
                MarshalHelpers.checkNativeTypeEquals(3, this.mNativeType);
                marshalPrimitive(((Long) t).longValue(), byteBuffer);
                return;
            }
            if (t instanceof Rational) {
                MarshalHelpers.checkNativeTypeEquals(5, this.mNativeType);
                marshalPrimitive((Rational) t, byteBuffer);
                return;
            }
            if (t instanceof Double) {
                MarshalHelpers.checkNativeTypeEquals(4, this.mNativeType);
                marshalPrimitive(((Double) t).doubleValue(), byteBuffer);
            } else if (t instanceof Byte) {
                MarshalHelpers.checkNativeTypeEquals(0, this.mNativeType);
                marshalPrimitive(((Byte) t).byteValue(), byteBuffer);
            } else {
                throw new UnsupportedOperationException("Can't marshal managed type " + this.mTypeReference);
            }
        }

        private void marshalPrimitive(int i, ByteBuffer byteBuffer) {
            byteBuffer.putInt(i);
        }

        private void marshalPrimitive(float f, ByteBuffer byteBuffer) {
            byteBuffer.putFloat(f);
        }

        private void marshalPrimitive(double d, ByteBuffer byteBuffer) {
            byteBuffer.putDouble(d);
        }

        private void marshalPrimitive(long j, ByteBuffer byteBuffer) {
            byteBuffer.putLong(j);
        }

        private void marshalPrimitive(Rational rational, ByteBuffer byteBuffer) {
            byteBuffer.putInt(rational.getNumerator());
            byteBuffer.putInt(rational.getDenominator());
        }

        private void marshalPrimitive(byte b, ByteBuffer byteBuffer) {
            byteBuffer.put(b);
        }

        private Object unmarshalObject(ByteBuffer byteBuffer) {
            int i = this.mNativeType;
            if (i == 0) {
                return Byte.valueOf(byteBuffer.get());
            }
            if (i == 1) {
                return Integer.valueOf(byteBuffer.getInt());
            }
            if (i == 2) {
                return Float.valueOf(byteBuffer.getFloat());
            }
            if (i == 3) {
                return Long.valueOf(byteBuffer.getLong());
            }
            if (i == 4) {
                return Double.valueOf(byteBuffer.getDouble());
            }
            if (i == 5) {
                return new Rational(byteBuffer.getInt(), byteBuffer.getInt());
            }
            throw new UnsupportedOperationException("Can't unmarshal native type " + this.mNativeType);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public int getNativeSize() {
            return MarshalHelpers.getPrimitiveTypeSize(this.mNativeType);
        }
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public Marshaler<T> createMarshaler(TypeReference<T> typeReference, int i) {
        return new MarshalerPrimitive(typeReference, i);
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public boolean isTypeMappingSupported(TypeReference<T> typeReference, int i) {
        if (!(typeReference.getType() instanceof Class)) {
            return false;
        }
        Class cls = (Class) typeReference.getType();
        if (cls == Byte.TYPE || cls == Byte.class) {
            return i == 0;
        }
        if (cls == Integer.TYPE || cls == Integer.class) {
            return i == 1;
        }
        if (cls == Float.TYPE || cls == Float.class) {
            return i == 2;
        }
        if (cls == Long.TYPE || cls == Long.class) {
            return i == 3;
        }
        if (cls == Double.TYPE || cls == Double.class) {
            return i == 4;
        }
        return cls == Rational.class && i == 5;
    }
}
