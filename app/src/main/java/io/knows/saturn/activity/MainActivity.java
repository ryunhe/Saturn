package io.knows.saturn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.KeyboardUtil;
import com.renn.rennsdk.RennClient;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.knows.saturn.R;
import io.knows.saturn.fragment.CardStackFragment;
import io.knows.saturn.fragment.Fragment;

/**
 * Created by ryun on 15-4-21.
 */
public class MainActivity extends Activity implements Drawer.OnDrawerItemClickListener, Drawer.OnDrawerListener {
    @Inject
    RennClient mRennClient;

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
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(CommunityMaterial.Icon.cmd_clipboard_account),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_exit).withIcon(CommunityMaterial.Icon.cmd_arrow_right_bold_hexagon_outline)
                        )
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
        if (drawerItem != null && drawerItem instanceof Nameable) {
            mToolbar.setTitle(((Nameable) drawerItem).getNameRes());

            switch (position) {
                case 0:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_frame, new CardStackFragment())
                            .commit();
                    break;
                case 1:
                    mRennClient.logout();
                    Intent i = new Intent(getActivity(), StartActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    break;

            }



        }
    }

    @Override
    public void onDrawerOpened(View view) {

    }

    @Override
    public void onDrawerClosed(View view) {

    }
}
