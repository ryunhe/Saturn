package io.knows.saturn.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.KeyboardUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.knows.saturn.R;
import io.knows.saturn.fragment.MediaCardStackFragment;
import tr.xip.errorview.ErrorView;
import tr.xip.errorview.HttpStatusCodes;

/**
 * Created by ryun on 15-4-21.
 */
public class MainActivity extends AppCompatActivity {
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    View mDrawerHeader;
    ImageView mAvatarImage;

    Drawer.Result mDrawerResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);

        mDrawerHeader = getLayoutInflater().inflate(R.layout.item_profile, null);
        mAvatarImage = (ImageView) mDrawerHeader.findViewById(R.id.image_avatar);

        mDrawerResult = new Drawer()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withHeader(mDrawerHeader)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home)
                )
                .withOnDrawerItemClickListener((parent, view, position, id, drawerItem) -> {
                    if (drawerItem != null && drawerItem instanceof Nameable) {
                        mToolbar.setTitle(((Nameable) drawerItem).getNameRes());
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, new MediaCardStackFragment())
                                .commit();
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        KeyboardUtil.hideKeyboard(MainActivity.this);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {

                    }
                })
                .withFireOnInitialOnClick(true)
                .build();

        mDrawerResult.keyboardSupportEnabled(this, true);
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (mDrawerResult != null && mDrawerResult.isDrawerOpen()) {
            mDrawerResult.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
