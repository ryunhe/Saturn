package io.knows.saturn.activity;

import android.os.Bundle;

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
                .replace(R.id.frame_fragment, new SchoolPickerFragment())
                .commit();

    }
}
