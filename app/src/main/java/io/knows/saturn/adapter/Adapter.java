package io.knows.saturn.adapter;

/**
 * Created by ryun on 15-4-21.
 */

import android.app.Activity;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public abstract class Adapter<T> extends BaseAdapter {

    protected List<T> mDataList;
    protected Activity mActivity;

    public Adapter(Activity activity) {
        mActivity = activity;
        mDataList = new ArrayList<>();
    }

    public Adapter(Activity activity, List<T> list) {
        mActivity = activity;
        mDataList = list;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public T getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
