package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.params.StreamConfigurationDuration;
import android.hardware.camera2.utils.TypeReference;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes.dex */
public class MarshalQueryableStreamConfigurationDuration implements MarshalQueryable<StreamConfigurationDuration> {
    private static final long MASK_UNSIGNED_INT = 4294967295L;
    private static final int SIZE = 32;

    private class MarshalerStreamConfigurationDuration extends Marshaler<StreamConfigurationDuration> {
        @Override // android.hardware.camera2.marshal.Marshaler
        public int getNativeSize() {
            return 32;
        }

        protected MarshalerStreamConfigurationDuration(TypeReference<StreamConfigurationDuration> typeReference, int i) {
            super(MarshalQueryableStreamConfigurationDuration.this, typeReference, i);
        }

        @Override // android.hardware.camera2.marshal.Marshaler
        public void marshal(StreamConfigurationDuration streamConfigurationDuration, ByteBuffer byteBuffer) {
            byteBuffer.putLong(((long) streamConfigurationDuration.getFormat()) & 4294967295L);
            byteBuffer.putLong(streamConfigurationDuration.getWidth());
            byteBuffer.putLong(streamConfigurationDuration.getHeight());
            byteBuffer.putLong(streamConfigurationDuration.getDuration());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.hardware.camera2.marshal.Marshaler
        public StreamConfigurationDuration unmarshal(ByteBuffer byteBuffer) {
            return new StreamConfigurationDuration((int) byteBuffer.getLong(), (int) byteBuffer.getLong(), (int) byteBuffer.getLong(), byteBuffer.getLong());
        }
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public Marshaler<StreamConfigurationDuration> createMarshaler(TypeReference<StreamConfigurationDuration> typeReference, int i) {
        return new MarshalerStreamConfigurationDuration(typeReference, i);
    }

    @Override // android.hardware.camera2.marshal.MarshalQueryable
    public boolean isTypeMappingSupported(TypeReference<StreamConfigurationDuration> typeReference, int i) {
        return i == 3 && StreamConfigurationDuration.class.equals(typeReference.getType());
    }
}
