package io.knows.saturn.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnEditorAction;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;
import io.knows.saturn.R;
import io.knows.saturn.activity.SignupActivity;
import io.knows.saturn.adapter.Adapter;
import io.knows.saturn.service.SamuiService;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ryun on 15-4-22.
 */
public class SchoolPickerFragment extends Fragment {
    @Inject
    SamuiService mSamuiService;

    @InjectView(R.id.list_main)
    ListView mListView;

    @InjectView(R.id.input_school)
    EditText mSchoolInput;

    SchoolListAdapter mListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new SchoolListAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_school_picker, container, false);
        inject(layout);

        mListView.setAdapter(mListAdapter);
        mListAdapter.doFetch("");

        return layout;
    }


    class SchoolListAdapter extends Adapter<String> {

        public SchoolListAdapter(Activity activity) {
            super(activity);
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

            holder.text.setText(getItem(position));

            return convertView;
        }

        class ViewHolder {
            @InjectView(R.id.text_content)
            public TextView text;

            public ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }

        public void doFetch(String keyword) {
            mSamuiService.getSearchSchool(keyword)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(schoolListResponse -> {
                        if (schoolListResponse.getResult().size() > 0) {
                            mDataList.clear();
                            mDataList.addAll(schoolListResponse.getResult());
                            mListAdapter.notifyDataSetChanged();
                            mListView.setSelection(0);
                        }
                    });
        }
    }

    @OnItemClick(R.id.list_main)
    void select(int position) {
        submit(mListAdapter.getItem(position));
    }

    @OnEditorAction(R.id.input_school)
    boolean custom(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            submit(mSchoolInput.getText().toString());
            return true;
        }
        return false;
    }

    @OnTextChanged(R.id.input_school)
    void search(CharSequence s, int start, int before, int count) {
        mListAdapter.doFetch(s.toString());
    }

    void submit(String school) {
        Intent i = new Intent();
        i.putExtra(SignupActivity.INTENT_KEY_SCHOOL, school);
        getActivity().setResult(Activity.RESULT_OK, i);
        getActivity().finish();
    }

}
