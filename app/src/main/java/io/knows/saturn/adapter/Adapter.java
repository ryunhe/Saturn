package io.knows.saturn.adapter;

/**
 * Created by ryun on 15-4-21.
 */

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import io.knows.saturn.R;

/**
 * A child class shall subclass this Adapter and
 * implement method getDataRow(int position, View convertView, ViewGroup parent),
 * which supplies a View present data in a ListRow.
 * <p>
 * This parent Adapter takes care of displaying ProgressBar in a row or
 * indicating that it has reached the last row.
 */
public abstract class Adapter<T> extends BaseAdapter {

    // the main data list to save loaded data
    protected List<T> mDataList;

    protected Activity mActivity;

    // the ServerListSize is the total number of items on the server side,
    // which should be returned from the web request results
    protected int mServerListSize = -1;

    // Two view types which will be used to determine whether a row should be displaying
    // data or a Progressbar
    public static final int VIEW_TYPE_LOADING = 0;
    public static final int VIEW_TYPE_ACTIVITY = 1;

    public Adapter(Activity activity, List<T> list) {
        mActivity = activity;
        mDataList = list;
    }

    public void setServerListSize(int size) {
        this.mServerListSize = size;
    }

    /**
     * disable click events on indicating rows
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
     * the size of the List plus one, the one is the last row, which displays a Progressbar
     */
    @Override
    public int getCount() {
        return mDataList.size() + 1;
    }


    /**
     * return the type of the row,
     * the last row indicates the user that the ListView is loading more data
     */
    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        return (position >= mDataList.size()) ? VIEW_TYPE_LOADING
                : VIEW_TYPE_ACTIVITY;
    }

    @Override
    public T getItem(int position) {
        // TODO Auto-generated method stub
        return (getItemViewType(position) == VIEW_TYPE_ACTIVITY) ? mDataList
                .get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return (getItemViewType(position) == VIEW_TYPE_ACTIVITY) ? position
                : -1;
    }

    /**
     * returns the correct view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == VIEW_TYPE_LOADING) {
            // display the last row
            return getFooterView(position, convertView, parent);
        } else {
            return getDataRow(position, convertView, parent);
        }
    }

    ;

    /**
     * A subclass should override this method to supply the data row.
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public abstract View getDataRow(int position, View convertView, ViewGroup parent);


    /**
     * returns a View to be displayed in the last row.
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
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
