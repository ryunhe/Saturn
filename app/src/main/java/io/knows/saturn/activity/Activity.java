package io.knows.saturn.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import butterknife.ButterKnife;
import io.knows.saturn.R;
import io.knows.saturn.SaturnApp;

/**
 * Created by ryun on 15-4-28.
 */
public class Activity extends AppCompatActivity {
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
