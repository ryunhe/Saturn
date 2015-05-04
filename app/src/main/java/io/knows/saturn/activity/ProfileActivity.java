package io.knows.saturn.activity;

import android.os.Bundle;

import io.knows.saturn.R;
import io.knows.saturn.fragment.ProfileFragment;

/**
 * Created by ryun on 15-4-25.
 */
public class ProfileActivity extends MenuActivity {
    public static final String INTENT_KEY_USER = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_frame, new ProfileFragment())
                .commit();
    }
}
