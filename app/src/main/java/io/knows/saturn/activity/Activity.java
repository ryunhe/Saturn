package io.knows.saturn.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import io.knows.saturn.R;
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

    protected void inject() {
        ButterKnife.inject(this);
        ((SaturnApp) getApplication()).getObjectGraph().inject(this);
    }

    protected Activity getActivity() {
        return this;
    }

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }
}
