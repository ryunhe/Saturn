package io.knows.saturn.activity;

import android.content.Intent;
import android.os.Bundle;

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
                .replace(R.id.frame_fragment, new SignupFragment())
                .commit();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

}
