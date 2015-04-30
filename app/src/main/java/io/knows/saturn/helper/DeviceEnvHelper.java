package io.knows.saturn.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import java.util.UUID;

public class DeviceEnvHelper {

    /**
     * 检测网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }

        return false;
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    public static String getDeviceId(final Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;

        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());

        String uniqueId = deviceUuid.toString();

        return uniqueId;
    }

    public static String getModel() {
        String model = Build.MODEL;
        return TextUtils.isEmpty(model) ? "N/A" : model;
    }

    public static String getBrand() {
        String brand = Build.BRAND;
        return TextUtils.isEmpty(brand) ? "N/A" : brand;
    }

    public static String getVersion() {
        String version = Build.VERSION.RELEASE;
        return TextUtils.isEmpty(version) ? "N/A" : version;
    }

    public static String getArch() {
        String arch = System.getProperty("os.arch");
        return TextUtils.isEmpty(arch) ? "N/A" : arch;
    }

    public static void setScreenBrightness(Window window) {
        WindowManager.LayoutParams layout = window.getAttributes();
        layout.screenBrightness = 1F;
        window.setAttributes(layout);
    }
}
