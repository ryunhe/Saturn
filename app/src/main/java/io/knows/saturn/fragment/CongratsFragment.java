package io.knows.saturn.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.knows.saturn.R;
import io.knows.saturn.SaturnApp;

/**
 * Created by ryun on 15-4-22.
 */
public class CongratsFragment extends Fragment {
    @Inject
    Picasso mPicasso;

    @InjectView(R.id.text_title)
    TextView titleText;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_congrats, container, false);
        inject(layout);

        titleText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Estrya_Handwriting.ttf"));

        return layout;
    }

    @OnClick(R.id.button_chat)
    void chat() {

    }

}
