package io.knows.saturn.helper;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

import java.util.List;

import io.knows.saturn.activity.MainActivity;

/**
 * Created by ryun on 15-5-6.
 */
public class LocationManager implements AMapLocationListener {
    static AMapLocation mLocation;
    LocationManagerProxy mAMapLocationManager;
    LocationListener mListener;
    Context mContext;

    public LocationManager(Context context) {
        mContext = context;

    }

    public void register(LocationListener listener) {
        mListener = listener;
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(mContext);
            mAMapLocationManager.requestLocationData(
                    LocationProviderProxy.AMapNetwork, 1000 * 10, 10, this);
        }
    }

    public void unregister(LocationListener listener) {
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destroy();
        }
        mAMapLocationManager = null;
        mListener = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        mLocation = aMapLocation;
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

    public interface LocationListener {
        void onLocationChanged(AMapLocation location);
    }

    public static String getDistanceReadable(Double[] location) {
        float distance = getDistance(location);
        if (distance > 1000 * 100) {
            return "Far away";
        } else if (distance < 1000) {
            return String.format("%1$fm", distance);
        } else {
            return String.format("%1$,.1fkm", distance / 1000);
        }
    }

    public static float getDistance(Double[] location) {
        float[] results = new float[1];
        results[0] = 0;

        if (null != location) {
            AMapLocation.distanceBetween(location[1], location[0], getLatitude(), getLongitude(), results);
        }

        return results[0];


    }

    public static Double getLatitude() {
        return mLocation.getLatitude();
    }

    public static Double getLongitude() {
        return mLocation.getLongitude();
    }

    public enum DistanceUnit {
        KM, METER;
    }
}
