package io.knows.saturn.activity;

import android.os.Bundle;

import io.knows.saturn.R;
import io.knows.saturn.fragment.PostFragment;

/**
 * Created by ryun on 15-5-6.
 */
public class PostActivity extends SubmitActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPageTitle.setText(R.string.title_post);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_fragment, new PostFragment())
                .commit();

    }
}
