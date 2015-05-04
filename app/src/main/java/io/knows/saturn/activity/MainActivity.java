package io.knows.saturn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.renn.rennsdk.RennClient;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.InjectView;
import io.knows.saturn.R;
import io.knows.saturn.fragment.CardStackFragment;
import io.knows.saturn.model.Authenticator;
import io.knows.saturn.model.User;
import io.knows.saturn.service.SamuiService;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by ryun on 15-4-21.
 */
public class MainActivity extends Activity implements Drawer.OnDrawerItemClickListener, Drawer.OnDrawerListener {
    @Inject
    SamuiService mSamuiService;
    @Inject
    RennClient mRennClient;
    @Inject
    Authenticator mAuthenticator;
    @Inject
    Picasso mPicasso;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    Drawer.Result mDrawerResult;
    User me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        inject();

        setSupportActionBar(mToolbar);

        // load profile
        View drawerHeader = getLayoutInflater().inflate(R.layout.item_profile, null);
        drawerHeader.setOnClickListener(v -> startActivity(new Intent(getActivity(), ProfileActivity.class)));

        ImageView avatarImage = (ImageView) drawerHeader.findViewById(R.id.image_avatar);
        TextView nicknameText = (TextView) drawerHeader.findViewById(R.id.text_nickname);

        mAuthenticator.getObservable()
                .subscribe(auth -> {
                    me = auth.getUser();
                    mPicasso.load(me.cover).into(avatarImage);
                    nicknameText.setText(me.nickname);
                });

        // load drawer
        mDrawerResult = new Drawer()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withHeader(drawerHeader)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(CommunityMaterial.Icon.cmd_home),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_exit).withIcon(CommunityMaterial.Icon.cmd_power)
                )
                .withOnDrawerItemClickListener(this)
                .withOnDrawerListener(this)
                .withFireOnInitialOnClick(true)
                .build();

        mDrawerResult.keyboardSupportEnabled(this, true);

        // update profile
        mSamuiService.getUser(mAuthenticator.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userEntityResponse -> {
                    mAuthenticator.saveUser(userEntityResponse.getEntity());
                });
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
