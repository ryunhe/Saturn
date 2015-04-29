package io.knows.saturn.fragment;

import android.view.View;

import butterknife.ButterKnife;
import io.knows.saturn.SaturnApp;

/**
 * Created by ryun on 15-4-27.
 */
public class Fragment extends android.support.v4.app.Fragment {
    protected void inject(View layout) {
        ButterKnife.inject(this, layout);
        ((SaturnApp) getActivity().getApplication()).getObjectGraph().inject(this);
    }
}
