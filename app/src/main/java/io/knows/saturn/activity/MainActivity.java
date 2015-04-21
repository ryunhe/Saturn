package io.knows.saturn.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import io.knows.saturn.R;
import io.knows.saturn.fragment.ListFragment;

/**
 * Created by ryun on 15-4-21.
 */
public class MainActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Remove the status bar color. The DrawerLayout is responsible for drawing it from now on.
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        getSupportFragmentManager().beginTransaction()
                .addToBackStack(this.toString())
                .replace(R.id.frame_main, new ListFragment())
                .commit();
    }
}
