package io.knows.saturn.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.ButterKnife;
import io.knows.saturn.SaturnApp;
import timber.log.Timber;

/**
 * Created by ryun on 15-4-27.
 */
public class Fragment extends android.support.v4.app.Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag("LifeCycles");
        Timber.d("Fragment Created");
    }

    protected void inject(View layout) {
        ButterKnife.inject(this, layout);
        ((SaturnApp) getActivity().getApplication()).getObjectGraph().inject(this);
    }

}
