package io.knows.saturn.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.knows.saturn.R;
import io.knows.saturn.SaturnApp;
import io.knows.saturn.adapter.Adapter;
import io.knows.saturn.model.Media;
import io.knows.saturn.response.MediaListResponse;
import io.knows.saturn.service.SamuiService;
import io.knows.saturn.widget.RoundedTopTransformation;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tr.xip.errorview.ErrorView;

/**
 * Created by ryun on 15-4-22.
 */
public class MediaCardStackFragment extends Fragment {
    @InjectView(R.id.error_view)
    ErrorView mErrorView;
    @InjectView(R.id.container)
    SwipeFlingAdapterView mFlingContainer;
    @Inject
    SamuiService mSamuiService;
    @Inject
    Picasso mPicasso;

    MediaListAdapter mListAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new MediaListAdapter(getActivity(), new ArrayList<Media>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ((SaturnApp) getActivity().getApplication()).inject(this);

        View layout = inflater.inflate(R.layout.fragment_card, container, false);
        ButterKnife.inject(this, layout);

        mFlingContainer.setAdapter(mListAdapter);
        mFlingContainer.setFlingListener(new CardFlingListener());
        mFlingContainer.setOnItemClickListener((itemPosition, dataObject) -> {
            // Optionally add an OnItemClickListener
        });

        mErrorView.setOnRetryListener(mListAdapter::onRetry);

        return layout;
    }

    @OnClick(R.id.button_like)
    public void like() {
        if (mListAdapter.getCount() > 0) {
            mFlingContainer.getTopCardListener().selectRight();
        }
    }

    @OnClick(R.id.button_pass)
    public void pass() {
        if (mListAdapter.getCount() > 0) {
            mFlingContainer.getTopCardListener().selectLeft();
        }
    }

    class CardFlingListener implements SwipeFlingAdapterView.onFlingListener {
        @Override
        public void removeFirstObjectInAdapter() {
            if (mListAdapter.getCount() > 0) {
                mListAdapter.remove(0);
                mListAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onLeftCardExit(Object dataObject) {

        }

        @Override
        public void onRightCardExit(Object dataObject) {

        }

        @Override
        public void onAdapterAboutToEmpty(int itemsInAdapter) {
            mListAdapter.doFetch();
        }

        @Override
        public void onScroll(float scrollProgressPercent) {
            View view = mFlingContainer.getSelectedView();
            if (view != null) {
                view.findViewById(R.id.indicator_item_swipe_right).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.indicator_item_swipe_left).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        }
    }

    class MediaListAdapter extends Adapter<Media> {
        private boolean fetching = false;
        private boolean retrying = false;
        private final RoundedTopTransformation transformation = new RoundedTopTransformation(2, 0);

        public MediaListAdapter(Activity activity, List<Media> list) {
            super(activity, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_card, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }

            Media media = getItem(position);

            mPicasso.load(media.resource.medium)
//                    .transform(transformation)
                    .into(holder.resourceImage);

            return convertView;
        }

        class ViewHolder {
//            @InjectView(R.id.text_content)
//            public TextView contentText;

            @InjectView(R.id.image_resource)
            public ImageView resourceImage;

            public ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }

        public void remove(int position) {
            mDataList.remove(position);
        }

        public void doFetch() {
            if (!fetching) {
                fetching = true;

                mSamuiService.getRecentMedia(0)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<MediaListResponse>() {
                            @Override
                            public void onCompleted() {
                                mErrorView.setVisibility(View.INVISIBLE);
                                retrying = false;

                                fetching = false;
                            }

                            @Override
                            public void onError(Throwable e) {
                                mErrorView.setVisibility(View.VISIBLE);
                                retrying = false;
                            }

                            @Override
                            public void onNext(MediaListResponse mediaListResponse) {
                                for (Media media : mediaListResponse.getResult()) {
                                    mPicasso.load(media.resource.medium)
//                                            .transform(transformation)
                                            .fetch(new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    mDataList.add(media);
                                                    mListAdapter.notifyDataSetChanged();
                                                }

                                                @Override
                                                public void onError() {

                                                }
                                            });
                                }

                            }
                        });
            }
        }

        public void onRetry() {
            if (!retrying) {
                retrying = true;
                fetching = false;
                doFetch();
            }
        }
    }
}
