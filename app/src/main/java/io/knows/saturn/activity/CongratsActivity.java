package io.knows.saturn.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.faradaj.blurbehind.BlurBehind;

import butterknife.ButterKnife;
import io.knows.saturn.R;
import io.knows.saturn.fragment.CongratsFragment;

/**
 * Created by ryun on 15-4-25.
 */
public class CongratsActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);
        ButterKnife.inject(this);

        BlurBehind.getInstance()
                .withAlpha(80)
                .withFilterColor(Color.BLACK)
                .setBackground(this);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_frame, new CongratsFragment())
                .commit();
    }
}
