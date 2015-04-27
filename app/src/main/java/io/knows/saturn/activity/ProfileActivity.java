package io.knows.saturn.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.faradaj.blurbehind.BlurBehind;

import butterknife.ButterKnife;
import io.knows.saturn.R;
import io.knows.saturn.fragment.CongratsFragment;
import io.knows.saturn.fragment.ProfileFragment;

/**
 * Created by ryun on 15-4-25.
 */
public class ProfileActivity extends FragmentActivity {
    public static final String INTENT_KEY_USER = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_frame, new ProfileFragment())
                .commit();
    }
}
