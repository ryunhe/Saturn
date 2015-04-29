package io.knows.saturn.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.InjectView;
import butterknife.OnClick;
import io.knows.saturn.R;
import io.knows.saturn.fragment.SchoolPickerFragment;

/**
 * Created by ryun on 15-4-25.
 */
public class SchoolPickerActivity extends Activity {
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.text_title)
    TextView mPageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        inject();

        mPageTitle.setText(R.string.title_school_picker);

        setSupportActionBar(mToolbar);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_frame, new SchoolPickerFragment())
                .commit();

    }

    @OnClick(R.id.button_back)
    void back() {
        finish();
    }
}
