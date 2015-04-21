package io.knows.saturn;

import android.app.Activity;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.knows.saturn.model.Media;
import io.knows.saturn.model.Model;
import io.knows.saturn.service.MediaService;
import io.knows.saturn.service.UserService;
import retrofit.RestAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ryun on 15-4-20.
 */
public class MainActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener {
    protected static String TAG = MainActivity.class.getName();

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeContainer;

    @InjectView(R.id.list_main)
    ListView mListView;
    RestAdapter mRestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        mSwipeContainer.setColorSchemeResources(
                R.color.green,
                R.color.orange,
                R.color.blue,
                R.color.purple

        );
        mSwipeContainer.setOnRefreshListener(this);

        mListView.setOnScrollListener(this);
        mListView.setSelector(new StateListDrawable());

        mRestAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("http://samui.knows.io/api/v1/")
                .build();

        onRefresh();
    }

    @Override
    public void onRefresh() {
        mSwipeContainer.setRefreshing(true);

        mRestAdapter.create(UserService.class).getRecent("100001")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mediaListResponse -> {
                    MediaListAdapter listAdapter = new MediaListAdapter(this, mediaListResponse.getList());
                    mListView.setAdapter(listAdapter);
                    mSwipeContainer.setRefreshing(false);
                });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int pos = (mListView == null || mListView.getChildCount() == 0) ? 0 : mListView.getChildAt(0).getTop();
        mSwipeContainer.setEnabled(pos >= 0);
    }

    class MediaListAdapter extends BaseAdapter {
        private Activity mContext;
        private List<Media> mList = new ArrayList<Media>();
        public MediaListAdapter(Activity context, List<Media> list) {
            mContext = context;
            mList = list;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Media getItem(int position) {
            return mList.get(position);
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

            Log.d(TAG, getItem(position).id);
            holder.text.setText(getItem(position).id);

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