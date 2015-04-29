package io.knows.saturn.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.knows.saturn.R;
import io.knows.saturn.fragment.SignupFragment;

/**
 * Created by ryun on 15-4-25.
 */
public class SignupActivity extends Activity {
    public static final String INTENT_KEY_SCHOOL = "school";

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.text_title)
    TextView mPageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        inject();

        mPageTitle.setText(R.string.title_signup);

        setSupportActionBar(mToolbar);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_frame, new SignupFragment())
                .commit();

    }

    @OnClick(R.id.button_back)
    void back() {
        finish();
    }

    @OnClick(R.id.button_submit)
    void submit(View v) {

    }

}
