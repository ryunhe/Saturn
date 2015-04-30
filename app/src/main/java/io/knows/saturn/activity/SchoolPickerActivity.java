package io.knows.saturn.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import butterknife.InjectView;
import butterknife.OnClick;
import io.knows.saturn.R;
import io.knows.saturn.fragment.SchoolPickerFragment;

/**
 * Created by ryun on 15-4-25.
 */
public class SchoolPickerActivity extends SubmitActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPageTitle.setText(R.string.title_school_picker);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_frame, new SchoolPickerFragment())
                .commit();

    }
}
