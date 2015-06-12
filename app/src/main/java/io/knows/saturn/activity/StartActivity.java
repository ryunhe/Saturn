package io.knows.saturn.activity;

import android.content.Intent;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.renn.rennsdk.RennClient;

import javax.inject.Inject;

import io.knows.saturn.R;
import io.knows.saturn.fragment.AuthFragment;
import io.knows.saturn.helper.LocationManager;
import io.knows.saturn.model.Authenticator;
import timber.log.Timber;

/**
 * Created by ryun on 15-4-25.
 */
public class StartActivity extends Activity implements LocationManager.LocationListener {
    @Inject
    LocationManager mLocationManager;
    @Inject
    RennClient mRennClient;
    @Inject
    Authenticator mAuthenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);

        inject();

        if (mAuthenticator.isLoggedIn() && mRennClient.isLogin()) {
            start();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_fragment, new AuthFragment())
                    .commit();
        }
    }

    public void start() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationManager.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.destroy();
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        Timber.d(location.toString());
    }
}
