package android.app;

import android.content.Context;
import android.os.ICarConfigService;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
public class CarConfigManager {
    public static final int AS22MCE_ECE_CR = 93;
    public static final int AS22MCE_GCC_CR = 92;
    public static final int AS22MCE_MEXIGO_CR = 116;
    public static final int AS23MCE_TAIWAN_CR = 87;
    public static final int AS23PMCE_TAIWAN_CR = 88;
    public static final int AS28M2_ECE = 118;
    public static final int AS28M2_GCC = 117;
    public static final int AS32_GCC_CR = 115;

    @Deprecated
    public static final int AS32_MEXICO_CR = 112;
    public static final int AS32_SA_CR = 113;
    public static final int AS33P_EUUK_AUS_CR_CRL = 139;
    public static final int AS33P_EUUK_SAIC = 143;
    public static final int AS33P_GCC_CR_CRL = 140;
    public static final int AS33P_MEXICO_CR_CRL = 141;
    public static final int AS33_ECE_CHILE_CR_CRL = 137;
    public static final int AS33_EUUK_AUS_CR_CRL = 136;
    public static final int AS33_EUUK_SAIC = 142;
    public static final int AS33_GCC_CR_CRL = 138;
    public static final int CAMERA_VARIANT_CVBS_1 = 0;
    public static final int CAMERA_VARIANT_CVBS_4 = 1;
    public static final int CAMERA_VARIANT_HD_CVBS_1 = 3;
    public static final int CAMERA_VARIANT_HD_CVBS_4 = 4;
    public static final int CAMERA_VARIANT_HD_VIDEO = 2;
    public static final int DEFAULT_VALUE = 255;
    public static final int DRIVE_HDL_TYPE_LEFT = 0;
    public static final int DRIVE_HDL_TYPE_RIGHT = 1;
    public static final int EC32_AUS_FICM_SAIC = 161;
    public static final int EC32_AUS_RICM_SAIC = 162;
    public static final int EC32_EUUK_FICM_SIAC = 134;
    public static final int EC32_EUUK_RICM_SAIC = 135;
    public static final int EC32_TT_FICM_CR = 163;
    public static final int EC32_TT_RICM_CR = 164;
    public static final int EH32_AUS_CR = 95;
    public static final int EH32_AUS_SAIC = 165;
    public static final int EH32_EUUK = 68;
    public static final int EH32_EUUK_CR = 69;
    public static final int EH32_GCC_CR = 130;
    public static final int EH32_HK_SAIC = 169;
    public static final int EH32_IDN_CR = 173;
    public static final int EH32_ISRAEL_CR = 129;
    public static final int EH32_ISRAEL_SAIC = 168;
    public static final int EH32_MALAYSIA_CR = 166;
    public static final int EH32_MALAYSIA_SAIC = 167;
    public static final int EH32_MCE24_CR = 132;
    public static final int EH32_MCE24_SAIC = 133;
    public static final int EH32_SA_CR = 131;
    public static final int EH32_SINGAPORE_SAIC = 170;
    public static final int EH32_TT = 86;
    public static final int EH32_TT_CR = 85;
    public static final int EP21MCE_EUUK = 51;
    public static final int EP22MCE_EUUK_CR = 83;
    public static final int EP22MCE_TT_CR = 99;
    public static final int ERROR = -1;
    public static final int ES34_EUUK_SAIC = 128;
    public static final int HAVE_NAVIGATION = 0;
    public static final int HAVE_REAR_CAMERA = 0;
    public static final String HEX_VALUE_DEFAULT = "0x";
    public static final int INTELLIGENT_ENERGY_PREDICTION_TYPE0 = 0;
    public static final int INTELLIGENT_ENERGY_PREDICTION_TYPE1 = 1;
    public static final int INTELLIGENT_ENERGY_PREDICTION_TYPE2 = 2;
    public static final int INTELLIGENT_ENERGY_PREDICTION_TYPE3 = 3;
    public static final int INTELLIGENT_ENERGY_PREDICTION_TYPE4 = 4;
    public static final int INTELLIGENT_ENERGY_PREDICTION_TYPE5 = 5;
    public static final int INTELLIGENT_ENERGY_PREDICTION_TYPE6 = 6;
    public static final int INTELLIGENT_ENERGY_PREDICTION_TYPE7 = 7;
    public static final int IP42_GCC_CR = 171;
    public static final int IP42_SA_MX = 172;
    public static final String KEY_CAL_360_CAMERA_CONFIG = "CAL_360_camera_config";
    public static final String KEY_CAL_ADAPTIVE_C_CONTROL = "CAL_ADAPTIVE_C_CONTROL";
    public static final String KEY_CAL_AIR_CONDITIONER = "CAL_AIR_CONDITIONER";
    public static final String KEY_CAL_AIR_FILTER = "CAL_AIR_FILTER";
    public static final String KEY_CAL_AMPLIFIER = "CAL_AMPLIFIER";
    public static final String KEY_CAL_APP_SW_NUMBER = "CAL_APP_SW_NUMBER";
    public static final String KEY_CAL_AQS = "CAL_AQS";
    public static final String KEY_CAL_AUDIO = "CAL_AUDIO";
    public static final String KEY_CAL_AUTOMATED_PARKING_SYSTEM = "CAL_AUTOMATED_PARKING_SYSTEM";
    public static final String KEY_CAL_AUTOMATED_VALET_PARKING = "CAL_AUTOMATED_VALET_PARKING";
    public static final String KEY_CAL_AUTOMATIC_EMERGENCY_CALL = "CAL_AUTOMATIC_EMERGENCY_CALL";
    public static final String KEY_CAL_AUTONOMOUS_EMERGENCY_BRAKING = "CAL_AUTONOMOUS_EMERGENCY_BRAKING";
    public static final String KEY_CAL_AUTO_HOLD = "CAL_Auto_hold";
    public static final String KEY_CAL_AUX_PORT = "CAL_AUX_PORT";
    public static final String KEY_CAL_BLIS = "CAL_BLIS";
    public static final String KEY_CAL_BODY_TYPE = "CAL_BODY_TYPE";
    public static final String KEY_CAL_BRAND_NAME = "CAL_Brand_Name";
    public static final String KEY_CAL_CAR_MODEL = "CAL_CAR_MODEL";
    public static final String KEY_CAL_CAR_SERIES = "CAL_CAR_SERIES";
    public static final String KEY_CAL_CBS = "CAL_CBS";
    public static final String KEY_CAL_CHARGER_FORM = "CAL_CHARGER_FORM";
    public static final String KEY_CAL_CHARGER_FUNCTION = "CAL_CHARGER_FUNCTION";
    public static final String KEY_CAL_CHARGING_ENERGY_STORAGE = "CAL_CHARGING_ENERGY_STORAGE";
    public static final String KEY_CAL_DAB = "CAL_DAB";
    public static final String KEY_CAL_DMS = "CAL_DMS";
    public static final String KEY_CAL_DRIVER_SEAT = "CAL_DRIVER_SEAT";
    public static final String KEY_CAL_DRIVER_WINDOW_OPERATING = "CAL_DRIVER_WINDOW_OPERATING";
    public static final String KEY_CAL_DRIVE_HDL_TYPE = "CAL_DRIVE_HDL_TYPE";
    public static final String KEY_CAL_DRIVING_PACKAGE = "CAL_DRIVING_PACKAGE";
    public static final String KEY_CAL_DUST_SENSOR = "CAL_DUST_SENSOR";
    public static final String KEY_CAL_DVR = "CAL_DVR";
    public static final String KEY_CAL_ECONOMY_RIVING = "CAL_ECONOMY_RIVING";
    public static final String KEY_CAL_ECU_H_N = "CAL_ECU_H_N";
    public static final String KEY_CAL_ECU_PART_NUMBER = "CAL_ECU_PART_NUMBER";
    public static final String KEY_CAL_ECU_S_N = "CAL_ECU_S_N";
    public static final String KEY_CAL_EDU = "CAL_EDU";
    public static final String KEY_CAL_ENGINE = "CAL_ENGINE";
    public static final String KEY_CAL_ENTRY_LAMP = "CAL_ENTRY_LAMP";
    public static final String KEY_CAL_EXTERIOR_COLOR = "CAL_EXTERIOR_COLOR";
    public static final String KEY_CAL_FACE_ID = "CAL_FACE_ID";
    public static final String KEY_CAL_FICM_CONFIG = "CAL_FICM_Config";
    public static final String KEY_CAL_FICM_GPS_CHIP = "CAL_FICM_GPS_CHIP";
    public static final String KEY_CAL_FICM_HW_VARIANT = "CAL_FICM_HW_VARIANT";
    public static final String KEY_CAL_FRONTPANEL_HARDWARE_KEY_TP_IC = "CAL_FRONTPANEL_HARDWARE_KEY_TP_IC";
    public static final String KEY_CAL_FRONTPANEL_HARDWARE_TFT = "CAL_FRONTPANEL_HARDWARE_TFT";
    public static final String KEY_CAL_FRONTPANEL_HARDWARE_TP = "CAL_FRONTPANEL_HARDWARE_TP";
    public static final String KEY_CAL_FRT_SEAT_AIRBAG = "CAL_FRT_SEAT_AIRBAG";
    public static final String KEY_CAL_FRT_WIPER = "CAL_FRT_WIPER";
    public static final String KEY_CAL_GATE_LID_OPERATING = "CAL_GATE_LID_OPERATING";
    public static final String KEY_CAL_HANDS_FREE = "CAL_HANDS_FREE";
    public static final String KEY_CAL_HARDWARE_SAMPLE = "CAL_Hardware_Sample";
    public static final String KEY_CAL_HEATER_POWER_BATTERY = "CAL_HEATER_POWER_BATTERY";
    public static final String KEY_CAL_HEATING_DEVICE = "CAL_HEATING_DEVICE";
    public static final String KEY_CAL_HIGHWAY_INTELLIGENT_DRIVE = "CAL_Highway_Intelligent_Drive";
    public static final String KEY_CAL_HVAC_CONTROL = "CAL_HVAC_Control";
    public static final String KEY_CAL_HW_VARIANT = "CAL_HW_VARIANT";
    public static final String KEY_CAL_HYBRID_LEVEL = "CAL_HYBRID_LEVEL";
    public static final String KEY_CAL_INFOTAINMENT_DISPLAY_LEVEL = "CAL_INFOTAINMENT_DISPLAY_LEVEL";
    public static final String KEY_CAL_INTELLIGENT_ENERGY_MANAGEMENT = "CAL_intelligent_energy_management";
    public static final String KEY_CAL_INTELLIGENT_ENERGY_PREDICTION = "CAL_INTELLIGENT_ENERGY_PREDICTION";
    public static final String KEY_CAL_ION_GENERATOR = "CAL_ION_GENERATOR";
    public static final String KEY_CAL_IPK_SIZE = "CAL_IPK_SIZE";
    public static final String KEY_CAL_IPOD_EQUIPMENT = "CAL_IPOD_EQUIPMENT";
    public static final String KEY_CAL_LAMP_PACKAGE = "CAL_LAMP_PACKAGE";
    public static final String KEY_CAL_LANE_DEPARTURE_WARNING_SYSTEM = "CAL_LANE_DEPARTURE_WARNING_SYSTEM";
    public static final String KEY_CAL_LIGHT_SENSOR = "CAL_Light_Sensor";
    public static final String KEY_CAL_LOCK_CONTROL = "CAL_LOCK_CONTROL";
    public static final String KEY_CAL_MECHANICAL_COUP_COMP = "CAL_MECHANICAL_COUP_COMP";
    public static final String KEY_CAL_MIC = "CAL_MIC";
    public static final String KEY_CAL_MODEL_CONVERSION = "CAL_MODEL_CONVERSION";
    public static final String KEY_CAL_MODEL_YEAR = "CAL_MODEL_YEAR";
    public static final String KEY_CAL_NATIONAL_CODE = "CAL_NATIONAL_CODE";
    public static final String KEY_CAL_NAVIGATION = "CAL_NAVIGATION";
    public static final String KEY_CAL_ONE_PEDAL = "CAL_ONE_PEDAL";
    public static final String KEY_CAL_PARKING_AID_SYSTEM = "CAL_PARKING_AID_SYSTEM";
    public static final String KEY_CAL_PEDESTRIAN_ALERT_SYSTEM = "CAL_PEDESTRIAN_ALERT_SYSTEM";
    public static final String KEY_CAL_PERIPHERAL_KEY_MODULE = "CAL_Peripheral_key_module";
    public static final String KEY_CAL_RADIO_FREQUENCY = "CAL_RADIO_FREQUENCY";
    public static final String KEY_CAL_RADIO_RANGE = "CAL_RADIO_RANGE";
    public static final String KEY_CAL_REMOTE_KEYLESS_ENTRY_SYSTEM = "CAL_REMOTE_KEYLESS_ENTRY_SYSTEM";
    public static final String KEY_CAL_RESERVED = "CAL_RESERVED";
    public static final String KEY_CAL_RR_HEATING_DEVICE = "CAL_RR_HEATING_DEVICE";
    public static final String KEY_CAL_RR_UNDER_VIEW_CAMERA = "CAL_RR_UNDER_VIEW_CAMERA";
    public static final String KEY_CAL_RR_VIEW_MIRROR = "CAL_RR_VIEW_MIRROR";
    public static final String KEY_CAL_SCREEN_SIZE = "CAL_Screen_Size";
    public static final String KEY_CAL_SEAT_HEATING = "CAL_SEAT_HEATING";
    public static final String KEY_CAL_SEAT_VENT = "CAL_Seat_Vent";
    public static final String KEY_CAL_SERVICE_BRAKE = "CAL_Service_Brake";
    public static final String KEY_CAL_SOC = "CAL_SOC";
    public static final String KEY_CAL_SPEAKER = "CAL_SPEAKER";
    public static final String KEY_CAL_SPEED_ALERT_SYSTEM = "CAL_SPEED_ALERT_SYSTEM";
    public static final String KEY_CAL_STEERING = "CAL_STEERING";
    public static final String KEY_CAL_STEERING_WHEEL_REMOCON = "CAL_STEERING_WHEEL_REMOCON";
    public static final String KEY_CAL_SUN_ROOF = "CAL_SUN_ROOF";
    public static final String KEY_CAL_TAILER_ELECTIC_INTERFACE = "CAL_TAILER_ELECTIC_INTERFACE";
    public static final String KEY_CAL_TELEMATICS = "CAL_TELEMATICS";
    public static final String KEY_CAL_TIRE_PRESSURE_MONITORING_SYSTEM = "CAL_TIRE_PRESSURE_MONITORING_SYSTEM";
    public static final String KEY_CAL_TOUCH_SCREEN_CONTROL_SWITCH = "CAL_Touch_Screen_Control_Switch";
    public static final String KEY_CAL_TRANSFERCASE = "CAL_TRANSFERCASE";
    public static final String KEY_CAL_TRANSFERCASE_CR = "CAL_TRANSFERCASE_CR";
    public static final String KEY_CAL_TRANSMISSION = "CAL_TRANSMISSION";
    public static final String KEY_CAL_USB_PORT = "CAL_USB_PORT";
    public static final String KEY_CAL_UV_LIGHT_ANTIBIOSIS = "CAL_UV_Light_ANTIBIOSIS";
    public static final String KEY_CAL_V2X = "CAL_V2X";
    public static final String KEY_CAL_VEHICLE_AIR_PURIFIER_SYSTEM = "CAL_VEHICLE_AIR_PURIFIER_SYSTEM";
    public static final String KEY_CAL_VEHICLE_BT_LISENCE = "CAL_Vehicle_BT_Lisence";
    public static final String KEY_CAL_VEHICLE_BT_MAC = "CAL_Vehicle_BT_MAC";
    public static final String KEY_CAL_VEHICLE_HWVER = "CAL_Vehicle_HWVer";
    public static final String KEY_CAL_VEHICLE_START_TYPE = "CAL_VEHICLE_START_TYPE";
    public static final String KEY_CAL_VEHICLE_VIN = "CAL_Vehicle_VIN";
    public static final String KEY_CAL_VEHICLE_WIFI_MAC = "CAL_Vehicle_WiFi_MAC";
    public static final String KEY_CAL_VIDEO_IN_FUNCTION = "CAL_VIDEO_IN_FUNCTION";
    public static final String KEY_CAL_VISION = "CAL_Vision";
    public static final String KEY_CAL_VOICE_RECOGNITION_CONFIG = "CAL_voice_recognition_config";
    public static final String KEY_ECU_PRAT_NUM = "ecuPratNum";
    public static final int NATIONAL_ADR = 24;
    public static final int NATIONAL_CHILE = 2;
    public static final int NATIONAL_CHINA = 0;
    public static final int NATIONAL_CHINA_HONGKONG = 18;
    public static final int NATIONAL_CHINA_MACAO = 19;
    public static final int NATIONAL_CHINA_TAIWAN = 9;
    public static final int NATIONAL_CTR_STH_AMERICA = 23;
    public static final int NATIONAL_ECE = 22;
    public static final int NATIONAL_EU = 5;
    public static final int NATIONAL_GCC = 3;
    public static final int NATIONAL_INDIA = 12;
    public static final int NATIONAL_IRAN = 4;
    public static final int NATIONAL_ISRAEL = 7;
    public static final int NATIONAL_JORDAN = 13;
    public static final int NATIONAL_LEBANON = 17;
    public static final int NATIONAL_MALAYSIA = 16;
    public static final int NATIONAL_MEXICO = 20;
    public static final int NATIONAL_SAUDI_ARABIA = 10;
    public static final int NATIONAL_SINGAPORE = 15;
    public static final int NATIONAL_SRI_LANKA = 14;
    public static final int NATIONAL_THAILAND = 6;
    public static final int NATIONAL_TURKEY = 11;
    public static final int NATIONAL_UK = 8;
    public static final int NATIONAL_UKRAINE = 1;
    public static final int NATIONAL_VIETNAM = 21;
    public static final int NON_NAVIGATION = 1;
    public static final int NON_REAR_CAMERA = 1;
    public static final int NON_TELEMATICS = 4;
    private static final String TAG = "CarConfigManager";
    public static final String TUNER_TYPE_DAB_BOX = "DAB_BOX";
    public static final int TUNER_TYPE_DAB_BOX_VALUE = 0;
    public static final String TUNER_TYPE_DSP = "DSP";
    public static final int TUNER_TYPE_DSP_VALUE = 1;
    public static final int VISION_360 = 0;
    public static final int VISION_360_ALL = 3;
    public static final int VISION_360_ALL_HD = 4;
    public static final int VISION_360_HD = 1;
    public static final int VISION_RVC = 2;
    public static final int ZS11EMCE_AUS_CR = 91;
    public static final int ZS11EMCE_EUUK = 58;
    public static final int ZS11EMCE_EUUK_CR = 66;
    public static final int ZS11EMCE_GCC_CR = 106;
    public static final int ZS11EMCE_MEXICO_CR = 114;
    public static final int ZS11EMCE_TUR_CR = 89;
    private Context mContext;
    private ICarConfigService mService;

    @Retention(RetentionPolicy.SOURCE)
    public @interface NationalCode {
    }

    public CarConfigManager(Context context, ICarConfigService iCarConfigService) {
        this.mContext = context;
        this.mService = iCarConfigService;
    }

    public String getCarConfig(String str) {
        if (this.mService == null || TextUtils.isEmpty(str)) {
            return "";
        }
        try {
            return this.mService.getCarConfig(str);
        } catch (RemoteException e) {
            e.printStackTrace();
            return "";
        }
    }

    public int getCarModel() {
        int conifg = formatConifg(getCarConfig(KEY_CAL_CAR_MODEL));
        if (conifg == 255) {
            return -1;
        }
        return conifg;
    }

    public String getProductCarModel() {
        return SystemProperties.get("ro.product.carmode");
    }

    public String getTunerType() {
        return getTunerTypeValue() == 1 ? TUNER_TYPE_DSP : TUNER_TYPE_DAB_BOX;
    }

    public boolean isInternalTuner() {
        return getTunerTypeValue() == 1;
    }

    public int getTunerTypeValue() {
        int conifg = formatConifg(getCarConfig(KEY_CAL_DAB));
        Log.d(TAG, "getTunerTypeValue: key = CAL_DAB, value = " + conifg);
        if (conifg == 255) {
            return 0;
        }
        return conifg;
    }

    public int getNavigationFuncationValue() {
        int conifg = formatConifg(getCarConfig(KEY_CAL_NAVIGATION));
        Log.d(TAG, "getNavigationFuncationValue: key = CAL_NAVIGATION, value = " + conifg);
        if (conifg == 255) {
            return 1;
        }
        return conifg;
    }

    public boolean isHaveNavigationFuncation() {
        return getNavigationFuncationValue() == 0;
    }

    public int getVisionConfig() {
        int conifg = formatConifg(getCarConfig(KEY_CAL_VISION));
        Log.d(TAG, "getVisionConfig: key = CAL_Vision, value = " + conifg);
        if (conifg == 255) {
            return 2;
        }
        return conifg;
    }

    public boolean isVisionConfig360() {
        return getVisionConfig() != 2;
    }

    public boolean isVisionConfigRvc() {
        return getVisionConfig() == 2;
    }

    public int getRearCameraConfig() {
        int conifg = formatConifg(getCarConfig(KEY_CAL_RR_UNDER_VIEW_CAMERA));
        Log.d(TAG, "getRearCameraConfig: key = CAL_RR_UNDER_VIEW_CAMERA, value = " + conifg);
        if (conifg == 255) {
            return 0;
        }
        return conifg;
    }

    public int getCameraVariantConfig() {
        int conifg = formatConifg(getCarConfig(KEY_CAL_HW_VARIANT));
        Log.d(TAG, "getCameraVariantConfig: key = CAL_HW_VARIANT, value = " + conifg);
        if (conifg == 255) {
            return 0;
        }
        return conifg;
    }

    public int getIntelligentEnergyPredictionType() {
        int conifg = formatConifg(getCarConfig(KEY_CAL_INTELLIGENT_ENERGY_PREDICTION));
        Log.d(TAG, "getIntelligentEnergyPredictionType: key = CAL_INTELLIGENT_ENERGY_PREDICTION, value = " + conifg);
        if (conifg == 255) {
            return 0;
        }
        return conifg;
    }

    public int formatConifg(String str) {
        if (!TextUtils.isEmpty(str) && str.contains(HEX_VALUE_DEFAULT)) {
            Log.d(TAG, "formatConifg: value = " + str);
            if (str.length() == 4) {
                try {
                    int i = Integer.parseInt(str.split(HEX_VALUE_DEFAULT)[1], 16);
                    Log.d(TAG, "formatConifg: config = " + i);
                    return i;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }

    public String getEcuPartNum() {
        int carModel = getCarModel();
        if (carModel == 89 || carModel == 91 || carModel == 106 || carModel == 115 || carModel == 112 || carModel == 113 || carModel == 117 || carModel == 118) {
            return getCarConfig(KEY_ECU_PRAT_NUM);
        }
        return SystemProperties.get("ro.abup.ecu.part.num");
    }

    public int getDriveHdlType() {
        if (!SystemProperties.getBoolean("ro.feature.auto_handtype", false)) {
            return -1;
        }
        int conifg = formatConifg(getCarConfig(KEY_CAL_DRIVE_HDL_TYPE));
        if (conifg == 255) {
            return 0;
        }
        return conifg;
    }

    public boolean isLeftDriveHdlType() {
        return getDriveHdlType() == 0;
    }

    public int getTelematics() {
        int conifg = formatConifg(getCarConfig(KEY_CAL_TELEMATICS));
        if (conifg == 255) {
            return 4;
        }
        return conifg;
    }

    public boolean isSupportTbox() {
        String str = SystemProperties.get("ro.product.name");
        boolean z = false;
        if (SystemProperties.getBoolean("persist.multiple.gps_channels", false)) {
            if (getTelematics() != 4) {
                z = true;
            }
        } else {
            z = this.mContext.getResources().getBoolean(R.bool.config_enable_force_to_fusedprovider);
        }
        Log.d(TAG, "isSupportTbox: productName = " + str + ", isSupportTbox = " + z);
        return z;
    }

    public int getNationalCode() {
        int conifg = formatConifg(getCarConfig(KEY_CAL_NATIONAL_CODE));
        if (conifg == 255) {
            return 5;
        }
        return conifg;
    }
}
