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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.knows.saturn.R;
import io.knows.saturn.SaturnApp;
import io.knows.saturn.model.Media;
import io.knows.saturn.module.MediaModule;
import io.knows.saturn.response.MediaListResponse;
import io.knows.saturn.service.SamuiService;
import retrofit.RestAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ryun on 15-4-21.
 */

public class ListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener  {

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeContainer;

    @InjectView(R.id.list_main)
    ListView mListView;

    @Inject
    Observable<MediaListResponse> mediaPopular;

    MediaListAdapter mListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new MediaListAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ((SaturnApp) getActivity().getApplication()).getObjectGraph()
                .plus(new MediaModule()).inject(this);

        View layout = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.inject(this, layout);

        mSwipeContainer.setColorSchemeResources(
                R.color.green,
                R.color.orange,
                R.color.blue,
                R.color.purple
        );
        mSwipeContainer.setOnRefreshListener(this);

        mListView.setOnScrollListener(this);
        mListView.setSelector(new StateListDrawable());
        mListView.setAdapter(mListAdapter);

        mListAdapter.doFetch();

        return layout;
    }


    @Override
    public void onRefresh() {
        mSwipeContainer.setRefreshing(true);
        mListAdapter.doFetch();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int pos = (mListView == null || mListView.getChildCount() == 0) ? 0 : mListView.getChildAt(0).getTop();
        mSwipeContainer.setEnabled(pos >= 0);
    }

    class MediaListAdapter extends ArrayAdapter<Media> {
        private Activity mContext;
        private List<Media> mData = new ArrayList<Media>();
        public MediaListAdapter(Activity context) {
            super(context, R.layout.row_main);
            mContext = context;
        }

        public void doFetch() {
            mData.clear();

            mediaPopular.subscribe(mediaListResponse -> {
                mData.addAll(mediaListResponse.getList());
                mListAdapter.notifyDataSetChanged();
                mSwipeContainer.setRefreshing(false);
            });
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Media getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder holder;
            if (view != null) {
                holder = (ViewHolder) view.getTag();
            } else {
                view = LayoutInflater.from(mContext).inflate(R.layout.row_main, parent, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            }

            holder.text.setText(getItem(position).content);

            return view;
        }

        class ViewHolder {
            @InjectView(R.id.text)
            public TextView text;

            public ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }
    }
}

