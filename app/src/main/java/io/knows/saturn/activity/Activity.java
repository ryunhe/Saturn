package io.knows.saturn.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
