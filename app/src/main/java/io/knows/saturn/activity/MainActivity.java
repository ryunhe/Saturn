package io.knows.saturn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps2d.LocationSource;
import com.github.pwittchen.networkevents.library.NetworkEvents;
import com.github.pwittchen.networkevents.library.event.ConnectivityChanged;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.renn.rennsdk.RennClient;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import io.knows.saturn.R;
import io.knows.saturn.fragment.CardStackFragment;
import io.knows.saturn.fragment.MapDiscoverFragment;
import io.knows.saturn.helper.LocationManager;
import io.knows.saturn.model.Authenticator;
import io.knows.saturn.model.Resource;
import io.knows.saturn.model.User;
import io.knows.saturn.service.ApiService;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by ryun on 15-4-21.
 */
public class MainActivity extends Activity implements Drawer.OnDrawerItemClickListener, Drawer.OnDrawerListener, LocationManager.LocationListener, LocationSource {
    @Inject
    LocationManager mLocationManager;
    @Inject
    ApiService mApiService;
    @Inject
    RennClient mRennClient;
    @Inject
    Authenticator mAuthenticator;
    @Inject
    Picasso mPicasso;
    @Inject
    Bus mBus;
    @Inject
    NetworkEvents mNetworkEvents;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.alert_offline)
    View mOfflineAlert;

    AMapLocation mLocation;
    OnLocationChangedListener mLocationChangedListener;
    Drawer.Result mDrawerResult;
    User me;

    static final int PAGE_POST = 1;

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

        mAuthenticator.toObservable()
                .subscribe(auth -> {
                    me = auth.getUser();
                    mPicasso.load(me.cover.getUrl(Resource.ResourceSize.MEDIUM)).into(avatarImage);
                    nicknameText.setText(me.nickname);
                });

        // load drawer
        mDrawerResult = new Drawer()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withHeader(drawerHeader)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_card).withIcon(CommunityMaterial.Icon.cmd_home),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_map).withIcon(CommunityMaterial.Icon.cmd_map),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_exit).withIcon(CommunityMaterial.Icon.cmd_power)
                )
                .withOnDrawerItemClickListener(this)
                .withOnDrawerListener(this)
                .withFireOnInitialOnClick(true)
                .build();

        mDrawerResult.keyboardSupportEnabled(this, true);

        // update profile
        mApiService.getUser(mAuthenticator.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userEntityResponse -> mAuthenticator.saveUser(userEntityResponse.getEntity()));
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
                            .replace(R.id.frame_fragment, new CardStackFragment())
                            .commit();
                    break;
                case 1:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame_fragment, new MapDiscoverFragment())
                            .commit();
                    break;
                case 2:
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

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (null != location) {
            mLocation = location;
            if (null != mLocationChangedListener) {
                mLocationChangedListener.onLocationChanged(location);
            }

            Timber.d(location.toString());
        }
    }

    @Subscribe
    public void onConnectivityChanged(ConnectivityChanged event) {
        Timber.d(event.getConnectivityStatus().toString());
        switch (event.getConnectivityStatus()) {
            case WIFI_CONNECTED_HAS_NO_INTERNET:
            case OFFLINE:
                mOfflineAlert.setVisibility(View.VISIBLE);
                break;
            default:
                mOfflineAlert.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBus.register(this);
        mNetworkEvents.register();
        mLocationManager.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBus.unregister(this);
        mNetworkEvents.unregister();
        mLocationManager.destroy();
    }

    @OnClick(R.id.button_post)
    void post() {
        Intent i = new Intent(this, PostActivity.class);
        startActivityForResult(i, PAGE_POST);
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mLocationChangedListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mLocationChangedListener = null;
    }
}
