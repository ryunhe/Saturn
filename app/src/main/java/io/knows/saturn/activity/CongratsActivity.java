package io.knows.saturn.activity;

import android.graphics.Color;
import android.os.Bundle;

import com.faradaj.blurbehind.BlurBehind;

import io.knows.saturn.R;
import io.knows.saturn.fragment.CongratsFragment;

/**
 * Created by ryun on 15-4-25.
 */
public class CongratsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);

        BlurBehind.getInstance()
                .withAlpha(80)
                .withFilterColor(Color.BLACK)
                .setBackground(this);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_fragment, new CongratsFragment())
                .commit();
    }
}
