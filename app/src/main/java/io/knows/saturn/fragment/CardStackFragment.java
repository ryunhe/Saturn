package io.knows.saturn.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.faradaj.blurbehind.BlurBehind;
import com.github.pwittchen.prefser.library.Prefser;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.knows.saturn.R;
import io.knows.saturn.activity.CongratsActivity;
import io.knows.saturn.activity.ProfileActivity;
import io.knows.saturn.adapter.Adapter;
import io.knows.saturn.helper.LocationManager;
import io.knows.saturn.helper.StorageWrapper;
import io.knows.saturn.model.Media;
import io.knows.saturn.model.Resource;
import io.knows.saturn.response.MediaListResponse;
import io.knows.saturn.service.ApiService;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by ryun on 15-4-22.
 */
public class CardStackFragment extends Fragment {
    @InjectView(R.id.container)
    SwipeFlingAdapterView mFlingContainer;
    @Inject
    ApiService mApiService;
    @Inject
    Picasso mPicasso;
    @Inject
    Prefser mPrefser;
    @Inject
    StorageWrapper mStorageWrapper;

    MediaListAdapter mListAdapter;
    static final int PAGE_CONGRATS = 1;
    static final int PAGE_PROFILE = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListAdapter = new MediaListAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_card, container, false);
        inject(layout);

        mFlingContainer.setAdapter(mListAdapter);
        mFlingContainer.setFlingListener(new CardFlingListener());
        mFlingContainer.setOnItemClickListener((itemPosition, dataObject) -> {
            Intent i = new Intent(getActivity(), ProfileActivity.class);
            i.putExtra(ProfileActivity.INTENT_KEY_USER, mListAdapter.getItem(0).user.id);
            startActivityForResult(i, PAGE_PROFILE);
        });

        return layout;
    }

    @OnClick(R.id.button_like)
    void like() {
        if (mListAdapter.getCount() > 0) {
            mFlingContainer.getTopCardListener().selectRight();
        }
    }

    @OnClick(R.id.button_pass)
    void pass() {
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
            Media media = (Media) dataObject;
            mApiService.like(media.id).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(matchedResponse -> {
                        if (matchedResponse.matched()) {
                            BlurBehind.getInstance().execute(getActivity(), () -> {
                                Intent i = new Intent(getActivity(), CongratsActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivityForResult(i, PAGE_CONGRATS);
                            });
                        }
                    });
        }

        @Override
        public void onAdapterAboutToEmpty(int itemsInAdapter) {
            mListAdapter.doFetch();
        }

        @Override
        public void onScroll(float scrollProgressPercent) {
            View view = mFlingContainer.getSelectedView();
            if (view != null) {
                view.findViewById(R.id.indicator_item_swipe_pass).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.indicator_item_swipe_like).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        }
    }

    class MediaListAdapter extends Adapter<Media> {
        private boolean fetching = false;

        public MediaListAdapter(Activity activity) {
            super(activity);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Timber.d("show card");

            ViewHolder holder;
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_card, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }

            Media media = getItem(position);

            StringBuilder content = new StringBuilder();
            content.append(media.content);
            content.append(" ~ ");
            content.append(LocationManager.getDistanceReadable(media.location, mPrefser));
            holder.contentText.setText(content);
            holder.primaryText.setText(String.format("%s, %d", media.user.nickname, media.user.age));
            holder.secondaryText.setText(String.format("%s, %s", media.user.school, media.user.hometown[media.user.hometown.length - 1]));
            holder.countsText.setText(String.format("%d", media.user.counts.media));

            mPicasso.load(media.resource.getUrl(Resource.ResourceSize.STANDARD))
                    .placeholder(R.drawable.content_default_pic)
                    .into(holder.resourceImage);

            return convertView;
        }

        class ViewHolder {
            @InjectView(R.id.text_content)
            public TextView contentText;

            @InjectView(R.id.text_primary)
            public TextView primaryText;

            @InjectView(R.id.text_secondary)
            public TextView secondaryText;

            @InjectView(R.id.text_counts)
            public TextView countsText;

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

                mApiService.getRecentMedia()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(mediaListResponse -> {
                            for (Media media : mediaListResponse.getResult()) {

                                // Pre-load resource
                                mPicasso.load(media.resource.getUrl(Resource.ResourceSize.STANDARD))
                                        .fetch(new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                mDataList.add(media);
                                                mListAdapter.notifyDataSetChanged();

                                                // Store object
                                                media.user.save(mStorageWrapper);
                                            }

                                            @Override
                                            public void onError() {

                                            }
                                        });
                            }

                            fetching = false;
                        });
            }
        }
    }

}
