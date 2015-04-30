package io.knows.saturn.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Strings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import io.knows.saturn.R;
import io.knows.saturn.activity.SignupActivity;
import io.knows.saturn.activity.SubmitActivity;
import io.knows.saturn.adapter.Adapter;
import io.knows.saturn.model.Region;
import io.knows.saturn.service.SamuiService;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ryun on 15-4-22.
 */
public class RegionPickerFragment extends Fragment {
    @Inject
    SamuiService mSamuiService;

    @InjectView(R.id.list_main)
    ListView mListView;

    RegionListAdapter mListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mListAdapter = new RegionListAdapter(getActivity());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_list, container, false);
        inject(layout);

        mListView.setAdapter(mListAdapter);

        ((SubmitActivity) getActivity()).setOnPageSubmitListener(new SubmitActivity.OnPageSubmitListener() {
            @Override
            public void onSubmit() {
                submit();
            }
        });

        return layout;
    }


    class RegionListAdapter extends Adapter<Region> {

        public RegionListAdapter(Activity activity) throws IOException {
            super(activity, Region.build(activity));
        }

        public void update(List<Region> list) {
            mDataList.clear();
            mDataList.addAll(list);
            mListAdapter.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_row, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }

            holder.text.setText(getItem(position).fullName);

            return convertView;
        }

        class ViewHolder {
            @InjectView(R.id.text_content)
            public TextView text;

            public ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }
    }

    @OnItemClick(R.id.list_main)
    void select(int position) {
        Region region = mListAdapter.getItem(position);
        regions.add(region.name);

        if (null != region.children) {
            if (1 == region.children.size() && null != region.children.get(0).children) {
                mListAdapter.update(region.children.get(0).children);
            } else {
                mListAdapter.update(region.children);
            }
        } else {
            submit();
        }
    }

    ArrayList<String> regions = new ArrayList<>();

    void submit() {

        StringBuilder builder = new StringBuilder();
        builder.append(regions.remove(0));

        for (String name : regions) {
            builder.append(" ");
            builder.append(name);
        }

        Intent i = new Intent();
        i.putExtra(SignupActivity.INTENT_KEY_REGION, builder.toString());
        getActivity().setResult(Activity.RESULT_OK, i);
        getActivity().finish();
    }
}
