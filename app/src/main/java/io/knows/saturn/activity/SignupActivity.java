package io.knows.saturn.activity;

import android.content.Intent;
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
public class SignupActivity extends SubmitActivity {
    public static final String INTENT_KEY_SCHOOL = "school";
    public static final String INTENT_KEY_REGION = "region";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPageTitle.setText(R.string.title_signup);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_frame, new SignupFragment())
                .commit();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

}
