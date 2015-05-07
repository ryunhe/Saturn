package io.knows.saturn.activity;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.squareup.otto.Bus;

import butterknife.ButterKnife;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import io.knows.saturn.SaturnApp;
import timber.log.Timber;

/**
 * Created by ryun on 15-4-28.
 */
public class Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag("LifeCycles");
        Timber.d("Activity Created");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Crouton.cancelAllCroutons();
    }

    protected void inject() {
        ButterKnife.inject(this);
        ((SaturnApp) getApplication()).getObjectGraph().inject(this);
    }

    protected Activity getActivity() {
        return this;
    }

}
