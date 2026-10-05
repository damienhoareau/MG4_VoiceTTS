package android.bluetooth;

import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class BluetoothGattCharacteristic implements Parcelable {
    public static final Parcelable.Creator<BluetoothGattCharacteristic> CREATOR = new Parcelable.Creator<BluetoothGattCharacteristic>() { // from class: android.bluetooth.BluetoothGattCharacteristic.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BluetoothGattCharacteristic createFromParcel(Parcel parcel) {
            return new BluetoothGattCharacteristic(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BluetoothGattCharacteristic[] newArray(int i) {
            return new BluetoothGattCharacteristic[i];
        }
    };
    public static final int FORMAT_FLOAT = 52;
    public static final int FORMAT_SFLOAT = 50;
    public static final int FORMAT_SINT16 = 34;
    public static final int FORMAT_SINT32 = 36;
    public static final int FORMAT_SINT8 = 33;
    public static final int FORMAT_UINT16 = 18;
    public static final int FORMAT_UINT32 = 20;
    public static final int FORMAT_UINT8 = 17;
    public static final int PERMISSION_READ = 1;
    public static final int PERMISSION_READ_ENCRYPTED = 2;
    public static final int PERMISSION_READ_ENCRYPTED_MITM = 4;
    public static final int PERMISSION_WRITE = 16;
    public static final int PERMISSION_WRITE_ENCRYPTED = 32;
    public static final int PERMISSION_WRITE_ENCRYPTED_MITM = 64;
    public static final int PERMISSION_WRITE_SIGNED = 128;
    public static final int PERMISSION_WRITE_SIGNED_MITM = 256;
    public static final int PROPERTY_BROADCAST = 1;
    public static final int PROPERTY_EXTENDED_PROPS = 128;
    public static final int PROPERTY_INDICATE = 32;
    public static final int PROPERTY_NOTIFY = 16;
    public static final int PROPERTY_READ = 2;
    public static final int PROPERTY_SIGNED_WRITE = 64;
    public static final int PROPERTY_WRITE = 8;
    public static final int PROPERTY_WRITE_NO_RESPONSE = 4;
    public static final int WRITE_TYPE_DEFAULT = 2;
    public static final int WRITE_TYPE_NO_RESPONSE = 1;
    public static final int WRITE_TYPE_SIGNED = 4;
    protected List<BluetoothGattDescriptor> mDescriptors;
    protected int mInstance;
    protected int mKeySize;
    protected int mPermissions;
    protected int mProperties;
    protected BluetoothGattService mService;
    protected UUID mUuid;
    protected byte[] mValue;
    protected int mWriteType;

    private int getTypeLen(int i) {
        return i & 15;
    }

    private int intToSignedBits(int i, int i2) {
        if (i >= 0) {
            return i;
        }
        int i3 = 1 << (i2 - 1);
        return (i & (i3 - 1)) + i3;
    }

    private int unsignedByteToInt(byte b) {
        return b & 255;
    }

    private int unsignedToSigned(int i, int i2) {
        int i3 = 1 << (i2 - 1);
        return (i & i3) != 0 ? (i3 - (i & (i3 - 1))) * (-1) : i;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public BluetoothGattCharacteristic(UUID uuid, int i, int i2) {
        this.mKeySize = 16;
        initCharacteristic(null, uuid, 0, i, i2);
    }

    BluetoothGattCharacteristic(BluetoothGattService bluetoothGattService, UUID uuid, int i, int i2, int i3) {
        this.mKeySize = 16;
        initCharacteristic(bluetoothGattService, uuid, i, i2, i3);
    }

    public BluetoothGattCharacteristic(UUID uuid, int i, int i2, int i3) {
        this.mKeySize = 16;
        initCharacteristic(null, uuid, i, i2, i3);
    }

    private void initCharacteristic(BluetoothGattService bluetoothGattService, UUID uuid, int i, int i2, int i3) {
        this.mUuid = uuid;
        this.mInstance = i;
        this.mProperties = i2;
        this.mPermissions = i3;
        this.mService = bluetoothGattService;
        this.mValue = null;
        this.mDescriptors = new ArrayList();
        if ((this.mProperties & 4) != 0) {
            this.mWriteType = 1;
        } else {
            this.mWriteType = 2;
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(new ParcelUuid(this.mUuid), 0);
        parcel.writeInt(this.mInstance);
        parcel.writeInt(this.mProperties);
        parcel.writeInt(this.mPermissions);
        parcel.writeInt(this.mKeySize);
        parcel.writeInt(this.mWriteType);
        parcel.writeTypedList(this.mDescriptors);
    }

    private BluetoothGattCharacteristic(Parcel parcel) {
        this.mKeySize = 16;
        this.mUuid = ((ParcelUuid) parcel.readParcelable(null)).getUuid();
        this.mInstance = parcel.readInt();
        this.mProperties = parcel.readInt();
        this.mPermissions = parcel.readInt();
        this.mKeySize = parcel.readInt();
        this.mWriteType = parcel.readInt();
        this.mDescriptors = new ArrayList();
        ArrayList<BluetoothGattDescriptor> arrayListCreateTypedArrayList = parcel.createTypedArrayList(BluetoothGattDescriptor.CREATOR);
        if (arrayListCreateTypedArrayList != null) {
            for (BluetoothGattDescriptor bluetoothGattDescriptor : arrayListCreateTypedArrayList) {
                bluetoothGattDescriptor.setCharacteristic(this);
                this.mDescriptors.add(bluetoothGattDescriptor);
            }
        }
    }

    public int getKeySize() {
        return this.mKeySize;
    }

    public boolean addDescriptor(BluetoothGattDescriptor bluetoothGattDescriptor) {
        this.mDescriptors.add(bluetoothGattDescriptor);
        bluetoothGattDescriptor.setCharacteristic(this);
        return true;
    }

    BluetoothGattDescriptor getDescriptor(UUID uuid, int i) {
        for (BluetoothGattDescriptor bluetoothGattDescriptor : this.mDescriptors) {
            if (bluetoothGattDescriptor.getUuid().equals(uuid) && bluetoothGattDescriptor.getInstanceId() == i) {
                return bluetoothGattDescriptor;
            }
        }
        return null;
    }

    public BluetoothGattService getService() {
        return this.mService;
    }

    void setService(BluetoothGattService bluetoothGattService) {
        this.mService = bluetoothGattService;
    }

    public UUID getUuid() {
        return this.mUuid;
    }

    public int getInstanceId() {
        return this.mInstance;
    }

    public void setInstanceId(int i) {
        this.mInstance = i;
    }

    public int getProperties() {
        return this.mProperties;
    }

    public int getPermissions() {
        return this.mPermissions;
    }

    public int getWriteType() {
        return this.mWriteType;
    }

    public void setWriteType(int i) {
        this.mWriteType = i;
    }

    public void setKeySize(int i) {
        this.mKeySize = i;
    }

    public List<BluetoothGattDescriptor> getDescriptors() {
        return this.mDescriptors;
    }

    public BluetoothGattDescriptor getDescriptor(UUID uuid) {
        for (BluetoothGattDescriptor bluetoothGattDescriptor : this.mDescriptors) {
            if (bluetoothGattDescriptor.getUuid().equals(uuid)) {
                return bluetoothGattDescriptor;
            }
        }
        return null;
    }

    public byte[] getValue() {
        return this.mValue;
    }

    public Integer getIntValue(int i, int i2) {
        int typeLen = getTypeLen(i) + i2;
        byte[] bArr = this.mValue;
        if (typeLen > bArr.length) {
            return null;
        }
        if (i == 17) {
            return Integer.valueOf(unsignedByteToInt(bArr[i2]));
        }
        if (i == 18) {
            return Integer.valueOf(unsignedBytesToInt(bArr[i2], bArr[i2 + 1]));
        }
        if (i == 20) {
            return Integer.valueOf(unsignedBytesToInt(bArr[i2], bArr[i2 + 1], bArr[i2 + 2], bArr[i2 + 3]));
        }
        if (i == 36) {
            return Integer.valueOf(unsignedToSigned(unsignedBytesToInt(bArr[i2], bArr[i2 + 1], bArr[i2 + 2], bArr[i2 + 3]), 32));
        }
        if (i == 33) {
            return Integer.valueOf(unsignedToSigned(unsignedByteToInt(bArr[i2]), 8));
        }
        if (i != 34) {
            return null;
        }
        return Integer.valueOf(unsignedToSigned(unsignedBytesToInt(bArr[i2], bArr[i2 + 1]), 16));
    }

    public Float getFloatValue(int i, int i2) {
        int typeLen = getTypeLen(i) + i2;
        byte[] bArr = this.mValue;
        if (typeLen > bArr.length) {
            return null;
        }
        if (i == 50) {
            return Float.valueOf(bytesToFloat(bArr[i2], bArr[i2 + 1]));
        }
        if (i != 52) {
            return null;
        }
        return Float.valueOf(bytesToFloat(bArr[i2], bArr[i2 + 1], bArr[i2 + 2], bArr[i2 + 3]));
    }

    public String getStringValue(int i) {
        byte[] bArr = this.mValue;
        if (bArr == null || i > bArr.length) {
            return null;
        }
        byte[] bArr2 = new byte[bArr.length - i];
        int i2 = 0;
        while (true) {
            byte[] bArr3 = this.mValue;
            if (i2 == bArr3.length - i) {
                return new String(bArr2);
            }
            bArr2[i2] = bArr3[i + i2];
            i2++;
        }
    }

    public boolean setValue(byte[] bArr) {
        this.mValue = bArr;
        return true;
    }

    public boolean setValue(int i, int i2, int i3) {
        int typeLen = getTypeLen(i2) + i3;
        if (this.mValue == null) {
            this.mValue = new byte[typeLen];
        }
        if (typeLen > this.mValue.length) {
            return false;
        }
        if (i2 != 17) {
            if (i2 != 18) {
                if (i2 != 20) {
                    if (i2 == 36) {
                        i = intToSignedBits(i, 32);
                    } else if (i2 == 33) {
                        i = intToSignedBits(i, 8);
                    } else {
                        if (i2 != 34) {
                            return false;
                        }
                        i = intToSignedBits(i, 16);
                    }
                }
                byte[] bArr = this.mValue;
                int i4 = i3 + 1;
                bArr[i3] = (byte) (i & 255);
                int i5 = i4 + 1;
                bArr[i4] = (byte) ((i >> 8) & 255);
                bArr[i5] = (byte) ((i >> 16) & 255);
                bArr[i5 + 1] = (byte) ((i >> 24) & 255);
                return true;
            }
            byte[] bArr2 = this.mValue;
            bArr2[i3] = (byte) (i & 255);
            bArr2[i3 + 1] = (byte) ((i >> 8) & 255);
            return true;
        }
        this.mValue[i3] = (byte) (i & 255);
        return true;
    }

    public boolean setValue(int i, int i2, int i3, int i4) {
        int typeLen = getTypeLen(i3) + i4;
        if (this.mValue == null) {
            this.mValue = new byte[typeLen];
        }
        if (typeLen > this.mValue.length) {
            return false;
        }
        if (i3 == 50) {
            int iIntToSignedBits = intToSignedBits(i, 12);
            int iIntToSignedBits2 = intToSignedBits(i2, 4);
            byte[] bArr = this.mValue;
            int i5 = i4 + 1;
            bArr[i4] = (byte) (iIntToSignedBits & 255);
            bArr[i5] = (byte) ((iIntToSignedBits >> 8) & 15);
            bArr[i5] = (byte) (bArr[i5] + ((byte) ((iIntToSignedBits2 & 15) << 4)));
            return true;
        }
        if (i3 != 52) {
            return false;
        }
        int iIntToSignedBits3 = intToSignedBits(i, 24);
        int iIntToSignedBits4 = intToSignedBits(i2, 8);
        byte[] bArr2 = this.mValue;
        int i6 = i4 + 1;
        bArr2[i4] = (byte) (iIntToSignedBits3 & 255);
        int i7 = i6 + 1;
        bArr2[i6] = (byte) ((iIntToSignedBits3 >> 8) & 255);
        int i8 = i7 + 1;
        bArr2[i7] = (byte) ((iIntToSignedBits3 >> 16) & 255);
        bArr2[i8] = (byte) (bArr2[i8] + ((byte) (iIntToSignedBits4 & 255)));
        return true;
    }

    public boolean setValue(String str) {
        this.mValue = str.getBytes();
        return true;
    }

    private int unsignedBytesToInt(byte b, byte b2) {
        return unsignedByteToInt(b) + (unsignedByteToInt(b2) << 8);
    }

    private int unsignedBytesToInt(byte b, byte b2, byte b3, byte b4) {
        return unsignedByteToInt(b) + (unsignedByteToInt(b2) << 8) + (unsignedByteToInt(b3) << 16) + (unsignedByteToInt(b4) << 24);
    }

    private float bytesToFloat(byte b, byte b2) {
        return (float) (((double) unsignedToSigned(unsignedByteToInt(b) + ((unsignedByteToInt(b2) & 15) << 8), 12)) * Math.pow(10.0d, unsignedToSigned(unsignedByteToInt(b2) >> 4, 4)));
    }

    private float bytesToFloat(byte b, byte b2, byte b3, byte b4) {
        return (float) (((double) unsignedToSigned(unsignedByteToInt(b) + (unsignedByteToInt(b2) << 8) + (unsignedByteToInt(b3) << 16), 24)) * Math.pow(10.0d, b4));
    }
}
