package io.knows.saturn.activity;

import android.os.Bundle;

import io.knows.saturn.R;
import io.knows.saturn.fragment.RegionPickerFragment;

/**
 * Created by ryun on 15-4-25.
 */
public class RegionPickerActivity extends SubmitActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPageTitle.setText(R.string.title_region_picker);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_fragment, new RegionPickerFragment())
                .commit();

    }

}
