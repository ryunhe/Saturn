package io.knows.saturn.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;

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
import io.knows.saturn.service.SamuiService;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by ryun on 15-4-22.
 */
public class MediaCardStackFragment extends Fragment {
    @InjectView(R.id.frame)
    SwipeFlingAdapterView mFlingContainer;
    @Inject
    SamuiService mSamuiService;

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

        return layout;
    }

    @OnClick(R.id.button_right)
    public void right() {
        if (mListAdapter.getCount() > 0) {
            mFlingContainer.getTopCardListener().selectRight();
        }
    }

    @OnClick(R.id.button_left)
    public void left() {
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
            // Ask for more data here
            mListAdapter.doFetch(0);
            Timber.d("Notified");
        }

        @Override
        public void onScroll(float scrollProgressPercent) {
            View view = mFlingContainer.getSelectedView();
            view.findViewById(R.id.indicator_item_swipe_right).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
            view.findViewById(R.id.indicator_item_swipe_left).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
        }
    }

    class MediaListAdapter extends Adapter<Media> {
        private boolean fetching = false;

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

            holder.text.setText(getItem(position).content);

            return convertView;
        }

        class ViewHolder {
            @InjectView(R.id.text)
            public TextView text;

            public ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }

        public void remove(int position) {
            mDataList.remove(position);
        }

        public void doFetch(int offset) {
            if (!fetching) {
                fetching = true;

                if (0 == offset) {
                    mDataList.clear();
                }

                mSamuiService.getRecentMedia(offset)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(mediaListResponse -> {
                            mDataList.addAll(mediaListResponse.getList());
                            mListAdapter.notifyDataSetChanged();
                            fetching = false;
                        });
            }
        }
    }
}
