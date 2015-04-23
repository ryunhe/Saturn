package io.knows.saturn.fragment;

import android.app.Activity;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.knows.saturn.R;
import io.knows.saturn.SaturnApp;
import io.knows.saturn.adapter.IndicatorAdapter;
import io.knows.saturn.listener.EndlessScrollListener;
import io.knows.saturn.model.Media;
import io.knows.saturn.service.SamuiService;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ryun on 15-4-21.
 */

public class MediaListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.list_main)
    ListView mListView;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeContainer;

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

        View layout = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.inject(this, layout);

        mSwipeContainer.setColorSchemeResources(
                R.color.green,
                R.color.orange,
                R.color.blue,
                R.color.purple
        );
        mSwipeContainer.setOnRefreshListener(this);

        mListView.setOnScrollListener(new OnScrollListener());
        mListView.setSelector(new StateListDrawable());
        mListView.setAdapter(mListAdapter);

//        mDatabase.query(Media.class)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(media -> Timber.i(media.id));

        return layout;
    }


    @Override
    public void onRefresh() {
        mSwipeContainer.setRefreshing(true);
        mListAdapter.doFetch(0);
    }

    class OnScrollListener extends EndlessScrollListener {
        @Override
        public void onLoadMore(int page, int totalItemsCount) {
            mListAdapter.doFetch(totalItemsCount - 1);
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int pos = (mListView == null || mListView.getChildCount() == 0) ? 0 : mListView.getChildAt(0).getTop();
            mSwipeContainer.setEnabled(pos >= 0);

            super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    class MediaListAdapter extends IndicatorAdapter<Media> {
        private boolean fetching = false;

        public MediaListAdapter(Activity activity, List<Media> list) {
            super(activity, list);
        }

        @Override
        public View getDataRow(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_row, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }

            holder.text.setText(getItem(position).content);

            return convertView;
        }

        class ViewHolder {
            @InjectView(R.id.text_content)
            public TextView text;

            public ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
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
                            mDataList.addAll(mediaListResponse.getResult());
                            mListAdapter.notifyDataSetChanged();
                            mSwipeContainer.setRefreshing(false);
                            fetching = false;
                        });
            }
        }
    }

}

