package io.knows.saturn.adapter;

/**
 * Created by ryun on 15-4-21.
 */

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.knows.saturn.R;

public abstract class IndicatorAdapter<T> extends Adapter {
    protected int mServerListSize = -1;

    // Two view types which will be used to determine whether a row should be displaying
    // data or a Progressbar
    public static final int VIEW_TYPE_LOADING = 0;
    public static final int VIEW_TYPE_ACTIVITY = 1;

    public IndicatorAdapter(Activity activity) {
        super(activity);
    }

    public void setServerListSize(int size) {
        this.mServerListSize = size;
    }

    /**
     * Disable click events on indicating rows
     */
    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) == VIEW_TYPE_ACTIVITY;
    }

    /**
     * One type is normal data row, the other type is Progressbar
     */
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /**
     * The size of the List plus one, the one is the last row, which displays a Progressbar
     */
    @Override
    public int getCount() {
        return mDataList.size() + 1;
    }

    /**
     * Return the type of the row,
     * the last row indicates the user that the ListView is loading more data
     */
    @Override
    public int getItemViewType(int position) {
        return (position >= mDataList.size()) ? VIEW_TYPE_LOADING : VIEW_TYPE_ACTIVITY;
    }

    @Override
    public T getItem(int position) {
        return (getItemViewType(position) == VIEW_TYPE_ACTIVITY) ? (T) mDataList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return (getItemViewType(position) == VIEW_TYPE_ACTIVITY) ? position : -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == VIEW_TYPE_LOADING) {
            // Display the last row
            return getFooterView(position, convertView, parent);
        } else {
            return getRowView(position, convertView, parent);
        }
    }

    /**
     * A subclass should override this method to supply the data row.
     */
    public abstract View getRowView(int position, View convertView, ViewGroup parent);

    /**
     * Returns a View to be displayed in the last row.
     */
    public View getFooterView(int position, View convertView, ViewGroup parent) {
        if (position >= mServerListSize && mServerListSize > 0) {
            // the ListView has reached the last row
            TextView tvLastRow = new TextView(mActivity);
            tvLastRow.setHint("Reached the last row.");
            tvLastRow.setGravity(Gravity.CENTER);
            return tvLastRow;
        }

        View row = convertView;
        if (row == null) {
            row = mActivity.getLayoutInflater().inflate(R.layout.progress, parent, false);
        }

        return row;
    }
}
