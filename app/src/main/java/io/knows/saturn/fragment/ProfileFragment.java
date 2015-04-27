package io.knows.saturn.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.knows.saturn.R;
import io.knows.saturn.SaturnApp;
import io.knows.saturn.activity.ProfileActivity;
import io.knows.saturn.model.Media;
import io.knows.saturn.model.Model;
import io.knows.saturn.model.User;
import nl.nl2312.rxcupboard.RxDatabase;
import rx.Observer;

/**
 * Created by ryun on 15-4-22.
 */
public class ProfileFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @Inject
    Picasso mPicasso;
    @Inject
    RxDatabase mRxDatabase;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeContainer;
    @InjectView(R.id.image_resource)
    ImageView mResource;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ((SaturnApp) getActivity().getApplication()).inject(this);

        View layout = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.inject(this, layout);

        if (getActivity().getIntent().hasExtra(ProfileActivity.INTENT_KEY_USER)) {
            String userId = getActivity().getIntent().getStringExtra(ProfileActivity.INTENT_KEY_USER);
            Model.load(User.class, mRxDatabase, userId).subscribe(model -> {
                mPicasso.load(((User) model).cover).into(mResource);
            });
        }

        mSwipeContainer.setColorSchemeResources(
                R.color.green,
                R.color.orange,
                R.color.blue,
                R.color.purple
        );
        mSwipeContainer.setOnRefreshListener(this);

        return layout;
    }

    @Override
    public void onRefresh() {

    }
}
