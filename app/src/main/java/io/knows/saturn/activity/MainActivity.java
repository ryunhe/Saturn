package io.knows.saturn.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.KeyboardUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.knows.saturn.R;
import io.knows.saturn.fragment.CardStackFragment;

/**
 * Created by ryun on 15-4-21.
 */
public class MainActivity extends Activity implements Drawer.OnDrawerItemClickListener, Drawer.OnDrawerListener {
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    View mDrawerHeader;
    ImageView mAvatarImage;

    Drawer.Result mDrawerResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        inject();

        setSupportActionBar(mToolbar);

        mDrawerHeader = getLayoutInflater().inflate(R.layout.item_profile, null);
        mAvatarImage = (ImageView) mDrawerHeader.findViewById(R.id.image_avatar);

        mDrawerResult = new Drawer()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withHeader(mDrawerHeader)
                .addDrawerItems(new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home))
                .withOnDrawerItemClickListener(this)
                .withOnDrawerListener(this)
                .withFireOnInitialOnClick(true)
                .build();

        mDrawerResult.keyboardSupportEnabled(this, true);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerResult != null && mDrawerResult.isDrawerOpen()) {
            mDrawerResult.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem drawerItem) {
        if (drawerItem != null && drawerItem instanceof Nameable) {
            mToolbar.setTitle(((Nameable) drawerItem).getNameRes());
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_frame, new CardStackFragment())
                    .commit();

        }
    }

    @Override
    public void onDrawerOpened(View view) {

    }

    @Override
    public void onDrawerClosed(View view) {

    }
}
