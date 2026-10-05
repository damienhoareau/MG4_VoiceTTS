package android.hardware.usb;

import android.util.Slog;
import com.android.internal.util.dump.DualDumpOutputStream;
import java.io.IOException;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* JADX INFO: loaded from: classes.dex */
public class DeviceFilter {
    private static final String TAG = DeviceFilter.class.getSimpleName();
    public final int mClass;
    public final String mManufacturerName;
    public final int mProductId;
    public final String mProductName;
    public final int mProtocol;
    public final String mSerialNumber;
    public final int mSubclass;
    public final int mVendorId;

    public DeviceFilter(int i, int i2, int i3, int i4, int i5, String str, String str2, String str3) {
        this.mVendorId = i;
        this.mProductId = i2;
        this.mClass = i3;
        this.mSubclass = i4;
        this.mProtocol = i5;
        this.mManufacturerName = str;
        this.mProductName = str2;
        this.mSerialNumber = str3;
    }

    public DeviceFilter(UsbDevice usbDevice) {
        this.mVendorId = usbDevice.getVendorId();
        this.mProductId = usbDevice.getProductId();
        this.mClass = usbDevice.getDeviceClass();
        this.mSubclass = usbDevice.getDeviceSubclass();
        this.mProtocol = usbDevice.getDeviceProtocol();
        this.mManufacturerName = usbDevice.getManufacturerName();
        this.mProductName = usbDevice.getProductName();
        this.mSerialNumber = usbDevice.getSerialNumber();
    }

    public static DeviceFilter read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        int i;
        int attributeCount = xmlPullParser.getAttributeCount();
        int i2 = 0;
        String str = null;
        String str2 = null;
        String str3 = null;
        int i3 = -1;
        int i4 = -1;
        int i5 = -1;
        int i6 = -1;
        int i7 = -1;
        int i8 = 0;
        while (i8 < attributeCount) {
            String attributeName = xmlPullParser.getAttributeName(i8);
            String attributeValue = xmlPullParser.getAttributeValue(i8);
            if ("manufacturer-name".equals(attributeName)) {
                str = attributeValue;
            } else if ("product-name".equals(attributeName)) {
                str2 = attributeValue;
            } else if ("serial-number".equals(attributeName)) {
                str3 = attributeValue;
            } else {
                if (attributeValue == null || attributeValue.length() <= 2 || attributeValue.charAt(i2) != '0' || !(attributeValue.charAt(1) == 'x' || attributeValue.charAt(1) == 'X')) {
                    i = 10;
                } else {
                    i = 16;
                    attributeValue = attributeValue.substring(2);
                }
                try {
                    int i9 = Integer.parseInt(attributeValue, i);
                    if ("vendor-id".equals(attributeName)) {
                        i3 = i9;
                    } else if ("product-id".equals(attributeName)) {
                        i4 = i9;
                    } else if ("class".equals(attributeName)) {
                        i5 = i9;
                    } else if ("subclass".equals(attributeName)) {
                        i6 = i9;
                    } else if ("protocol".equals(attributeName)) {
                        i7 = i9;
                    }
                } catch (NumberFormatException e) {
                    Slog.e(TAG, "invalid number for field " + attributeName, e);
                }
            }
            i8++;
            i2 = 0;
        }
        return new DeviceFilter(i3, i4, i5, i6, i7, str, str2, str3);
    }

    public void write(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag(null, "usb-device");
        int i = this.mVendorId;
        if (i != -1) {
            xmlSerializer.attribute(null, "vendor-id", Integer.toString(i));
        }
        int i2 = this.mProductId;
        if (i2 != -1) {
            xmlSerializer.attribute(null, "product-id", Integer.toString(i2));
        }
        int i3 = this.mClass;
        if (i3 != -1) {
            xmlSerializer.attribute(null, "class", Integer.toString(i3));
        }
        int i4 = this.mSubclass;
        if (i4 != -1) {
            xmlSerializer.attribute(null, "subclass", Integer.toString(i4));
        }
        int i5 = this.mProtocol;
        if (i5 != -1) {
            xmlSerializer.attribute(null, "protocol", Integer.toString(i5));
        }
        String str = this.mManufacturerName;
        if (str != null) {
            xmlSerializer.attribute(null, "manufacturer-name", str);
        }
        String str2 = this.mProductName;
        if (str2 != null) {
            xmlSerializer.attribute(null, "product-name", str2);
        }
        String str3 = this.mSerialNumber;
        if (str3 != null) {
            xmlSerializer.attribute(null, "serial-number", str3);
        }
        xmlSerializer.endTag(null, "usb-device");
    }

    private boolean matches(int i, int i2, int i3) {
        int i4;
        int i5;
        int i6 = this.mClass;
        return (i6 == -1 || i == i6) && ((i4 = this.mSubclass) == -1 || i2 == i4) && ((i5 = this.mProtocol) == -1 || i3 == i5);
    }

    public boolean matches(UsbDevice usbDevice) {
        if (this.mVendorId != -1 && usbDevice.getVendorId() != this.mVendorId) {
            return false;
        }
        if (this.mProductId != -1 && usbDevice.getProductId() != this.mProductId) {
            return false;
        }
        if (this.mManufacturerName != null && usbDevice.getManufacturerName() == null) {
            return false;
        }
        if (this.mProductName != null && usbDevice.getProductName() == null) {
            return false;
        }
        if (this.mSerialNumber != null && usbDevice.getSerialNumber() == null) {
            return false;
        }
        if (this.mManufacturerName != null && usbDevice.getManufacturerName() != null && !this.mManufacturerName.equals(usbDevice.getManufacturerName())) {
            return false;
        }
        if (this.mProductName != null && usbDevice.getProductName() != null && !this.mProductName.equals(usbDevice.getProductName())) {
            return false;
        }
        if (this.mSerialNumber != null && usbDevice.getSerialNumber() != null && !this.mSerialNumber.equals(usbDevice.getSerialNumber())) {
            return false;
        }
        if (matches(usbDevice.getDeviceClass(), usbDevice.getDeviceSubclass(), usbDevice.getDeviceProtocol())) {
            return true;
        }
        int interfaceCount = usbDevice.getInterfaceCount();
        for (int i = 0; i < interfaceCount; i++) {
            UsbInterface usbInterface = usbDevice.getInterface(i);
            if (matches(usbInterface.getInterfaceClass(), usbInterface.getInterfaceSubclass(), usbInterface.getInterfaceProtocol())) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(DeviceFilter deviceFilter) {
        int i = this.mVendorId;
        if (i != -1 && deviceFilter.mVendorId != i) {
            return false;
        }
        int i2 = this.mProductId;
        if (i2 != -1 && deviceFilter.mProductId != i2) {
            return false;
        }
        String str = this.mManufacturerName;
        if (str != null && !Objects.equals(str, deviceFilter.mManufacturerName)) {
            return false;
        }
        String str2 = this.mProductName;
        if (str2 != null && !Objects.equals(str2, deviceFilter.mProductName)) {
            return false;
        }
        String str3 = this.mSerialNumber;
        if (str3 == null || Objects.equals(str3, deviceFilter.mSerialNumber)) {
            return matches(deviceFilter.mClass, deviceFilter.mSubclass, deviceFilter.mProtocol);
        }
        return false;
    }

    public boolean equals(Object obj) {
        int i;
        int i2;
        int i3;
        int i4;
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        int i5 = this.mVendorId;
        if (i5 != -1 && (i = this.mProductId) != -1 && (i2 = this.mClass) != -1 && (i3 = this.mSubclass) != -1 && (i4 = this.mProtocol) != -1) {
            if (obj instanceof DeviceFilter) {
                DeviceFilter deviceFilter = (DeviceFilter) obj;
                if (deviceFilter.mVendorId != i5 || deviceFilter.mProductId != i || deviceFilter.mClass != i2 || deviceFilter.mSubclass != i3 || deviceFilter.mProtocol != i4) {
                    return false;
                }
                if ((deviceFilter.mManufacturerName != null && this.mManufacturerName == null) || ((deviceFilter.mManufacturerName == null && this.mManufacturerName != null) || ((deviceFilter.mProductName != null && this.mProductName == null) || ((deviceFilter.mProductName == null && this.mProductName != null) || ((deviceFilter.mSerialNumber != null && this.mSerialNumber == null) || (deviceFilter.mSerialNumber == null && this.mSerialNumber != null)))))) {
                    return false;
                }
                String str6 = deviceFilter.mManufacturerName;
                return (str6 == null || (str5 = this.mManufacturerName) == null || str5.equals(str6)) && ((str = deviceFilter.mProductName) == null || (str4 = this.mProductName) == null || str4.equals(str)) && ((str2 = deviceFilter.mSerialNumber) == null || (str3 = this.mSerialNumber) == null || str3.equals(str2));
            }
            if (obj instanceof UsbDevice) {
                UsbDevice usbDevice = (UsbDevice) obj;
                if (usbDevice.getVendorId() == this.mVendorId && usbDevice.getProductId() == this.mProductId && usbDevice.getDeviceClass() == this.mClass && usbDevice.getDeviceSubclass() == this.mSubclass && usbDevice.getDeviceProtocol() == this.mProtocol) {
                    if ((this.mManufacturerName == null || usbDevice.getManufacturerName() != null) && ((this.mManufacturerName != null || usbDevice.getManufacturerName() == null) && ((this.mProductName == null || usbDevice.getProductName() != null) && ((this.mProductName != null || usbDevice.getProductName() == null) && ((this.mSerialNumber == null || usbDevice.getSerialNumber() != null) && (this.mSerialNumber != null || usbDevice.getSerialNumber() == null)))))) {
                        return (usbDevice.getManufacturerName() == null || this.mManufacturerName.equals(usbDevice.getManufacturerName())) && (usbDevice.getProductName() == null || this.mProductName.equals(usbDevice.getProductName())) && (usbDevice.getSerialNumber() == null || this.mSerialNumber.equals(usbDevice.getSerialNumber()));
                    }
                    return false;
                }
            }
        }
        return false;
    }

    public int hashCode() {
        return ((this.mVendorId << 16) | this.mProductId) ^ (((this.mClass << 16) | (this.mSubclass << 8)) | this.mProtocol);
    }

    public String toString() {
        return "DeviceFilter[mVendorId=" + this.mVendorId + ",mProductId=" + this.mProductId + ",mClass=" + this.mClass + ",mSubclass=" + this.mSubclass + ",mProtocol=" + this.mProtocol + ",mManufacturerName=" + this.mManufacturerName + ",mProductName=" + this.mProductName + ",mSerialNumber=" + this.mSerialNumber + "]";
    }

    public void dump(DualDumpOutputStream dualDumpOutputStream, String str, long j) {
        long jStart = dualDumpOutputStream.start(str, j);
        dualDumpOutputStream.write("vendor_id", 1120986464257L, this.mVendorId);
        dualDumpOutputStream.write("product_id", 1120986464258L, this.mProductId);
        dualDumpOutputStream.write("class", 1120986464259L, this.mClass);
        dualDumpOutputStream.write("subclass", 1120986464260L, this.mSubclass);
        dualDumpOutputStream.write("protocol", 1120986464261L, this.mProtocol);
        dualDumpOutputStream.write("manufacturer_name", 1138166333446L, this.mManufacturerName);
        dualDumpOutputStream.write("product_name", 1138166333447L, this.mProductName);
        dualDumpOutputStream.write("serial_number", 1138166333448L, this.mSerialNumber);
        dualDumpOutputStream.end(jStart);
    }
}
