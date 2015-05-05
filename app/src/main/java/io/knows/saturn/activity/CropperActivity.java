package io.knows.saturn.activity;

import android.os.Bundle;

import io.knows.saturn.R;
import io.knows.saturn.fragment.CropperFragment;

/**
 * Created by ryun on 15-4-25.
 */
public class CropperActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_fragment, new CropperFragment())
                .commit();
    }
}
