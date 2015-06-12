package io.knows.saturn.helper;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.model.LatLng;
import com.github.pwittchen.prefser.library.Prefser;

import io.knows.saturn.model.Preferences;

/**
 * Created by ryun on 15-5-6.
 */
public class LocationManager implements AMapLocationListener, Runnable {
    LocationManagerProxy mAMapLocationManager;
    LocationListener mListener;
    Context mContext;
    Prefser mPrefser;
    Handler mHandler = new Handler();

    public LocationManager(Context context, Prefser prefser) {
        mContext = context;
        mPrefser = prefser;

    }

    public void register(LocationListener listener) {
        mListener = listener;
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(mContext);
            mAMapLocationManager.requestLocationData(
                    LocationProviderProxy.AMapNetwork, 1000 * 60, 10, this);
            mHandler.postDelayed(this, 12000);
        }
    }

    public void destroy() {
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destroy();
        }
        mAMapLocationManager = null;
        mListener = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        mPrefser.put(Preferences.LAST_KNOWN_LATITUDE.toString(), aMapLocation.getLatitude());
        mPrefser.put(Preferences.LAST_KNOWN_LONGITUDE.toString(), aMapLocation.getLongitude());
        mPrefser.put(Preferences.LAST_KNOWN_POI.toString(), aMapLocation.getPoiName());

        mListener.onLocationChanged(aMapLocation);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void run() {
        if (mAMapLocationManager == null) {
            destroy();
        }
    }

    public interface LocationListener {
        void onLocationChanged(AMapLocation location);
    }

    public static String getDistanceReadable(Double[] location, Prefser prefser) {
        float distance = getDistance(location, prefser);
        if (distance > 1000 * 100) {
            return "Far away";
        } else if (distance < 1000) {
            return String.format("%1$fm", distance);
        } else {
            return String.format("%1$,.1fkm", distance / 1000);
        }
    }

    public static float getDistance(Double[] location, Prefser prefser) {
        float[] results = new float[1];
        results[0] = 0;

        if (null != location) {
            AMapLocation.distanceBetween(location[1], location[0], getLatitude(prefser), getLongitude(prefser), results);
        }

        return results[0];

    }

    @Nullable
    public static String getPoi(Prefser prefser) {
        return prefser.get(Preferences.LAST_KNOWN_POI.toString(), String.class, null);
    }

    @Nullable
    public static Double getLatitude(Prefser prefser) {
        return prefser.get(Preferences.LAST_KNOWN_LATITUDE.toString(), Double.class, null);
    }

    @Nullable
    public static Double getLongitude(Prefser prefser) {
        return prefser.get(Preferences.LAST_KNOWN_LONGITUDE.toString(), Double.class, null);
    }

    public static LatLng getLatLng(Prefser mPrefser) {
        Double lat = getLatitude(mPrefser), lng = getLongitude(mPrefser);
        if (null != lat && null != lng) {
            return new LatLng(lat, lng);
        } else {
            return SHANGHAI;
        }
    }

    public static final LatLng SHANGHAI = new LatLng(31.238068, 121.501654);// 上海市经纬度
    public static final LatLng BEIJING = new LatLng(39.90403, 116.407525);// 北京市经纬度
    public static final LatLng CHENGDU = new LatLng(30.679879, 104.064855);// 成都市经纬度
    public static final LatLng XIAN = new LatLng(34.341568, 108.940174);// 西安市经纬度

    public static final LatLng FUDAN = new LatLng(31.298537, 121.501437);// 复旦大学经纬度
}
