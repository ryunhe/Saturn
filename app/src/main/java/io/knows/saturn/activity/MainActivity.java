package io.knows.saturn.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import io.knows.saturn.fragment.MediaCardStackFragment;
import io.knows.saturn.fragment.MediaListFragment;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

/**
 * Created by ryun on 15-4-21.
 */
public class MainActivity extends MaterialNavigationDrawer {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_list);
//
//
//        getSupportFragmentManager().beginTransaction()
//                .addToBackStack(this.toString())
//                .replace(R.id.frame_main, new MediaListFragment())
//                .commit();
//    }

    @Override
    public void init(Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Remove the status bar color. The DrawerLayout is responsible for drawing it from now on.
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        addSection(newSection("首页", new MediaCardStackFragment()));
    }
}
