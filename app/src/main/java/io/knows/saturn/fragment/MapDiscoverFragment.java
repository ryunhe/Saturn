package io.knows.saturn.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.github.pwittchen.prefser.library.Prefser;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.knows.saturn.R;
import io.knows.saturn.activity.MainActivity;
import io.knows.saturn.activity.ProfileActivity;
import io.knows.saturn.helper.DeviceHelper;
import io.knows.saturn.helper.DisplayMetricsConverter;
import io.knows.saturn.helper.LocationManager;
import io.knows.saturn.helper.StorageWrapper;
import io.knows.saturn.model.Media;
import io.knows.saturn.model.Resource;
import io.knows.saturn.service.ApiService;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by ryun on 15-5-8.
 */
public class MapDiscoverFragment extends Fragment implements AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter, AMap.OnMapLoadedListener, AMap.OnCameraChangeListener {
    @Inject
    ApiService mApiService;
    @Inject
    Picasso mPicasso;
    @Inject
    Prefser mPrefser;
    @Inject
    StorageWrapper mStorageWrapper;

    @InjectView(R.id.map)
    MapView mMapView;

    AMap mMap;

    static final int PAGE_PROFILE = 1;

    static final float DEFAULT_ZOOM_LEVEL = 16;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_map, container, false);
        inject(layout);

        mMapView.onCreate(savedInstanceState);
        initMap();

        return layout;
    }

    void initMap() {
        if (null == mMap) {
            mMap = mMapView.getMap();

            mMap.setOnMapLoadedListener(this);
            mMap.setOnMarkerClickListener(this);
            mMap.setOnInfoWindowClickListener(this);// 设置点击InfoWindow事件监听器
            mMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
            mMap.setOnCameraChangeListener(this);

            fetchData(LocationManager.getLatLng(mPrefser), 2.5f);
        }
    }

    void setUpMyLocation() {
        // setup location source
        mMap.setLocationSource((MainActivity) getActivity()); // 设置定位监听

        // setup style
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
        myLocationStyle.radiusFillColor(getResources().getColor(R.color.green_transparent));
        myLocationStyle.strokeColor(getResources().getColor(R.color.green));

        myLocationStyle.strokeWidth(0.5f);// 设置圆形的边框粗细
        mMap.setMyLocationStyle(myLocationStyle);

        mMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
    }

    HashMap<String, Media> mMediaMap = new HashMap<>();
    HashSet<String> mUserSet = new HashSet<>();
    void fetchData(LatLng current, float distance) {
        mApiService.getNearByMedia(current.latitude, current.longitude, distance)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mediaListResponse -> {
                    if (mediaListResponse.getResult().size() > 0) {
                        for (Media media : mediaListResponse.getResult()) {
                            if (!mUserSet.contains(media.user.id)) {
                                media.user.save(mStorageWrapper); // store user
                                new GetDataTask(media).execute();
                            }
                        }
                    }
                });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.isInfoWindowShown()) {
            onInfoWindowClick(marker);
            return true;
        }
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Media media = mMediaMap.get(marker.getId());
        Intent i = new Intent(getActivity(), ProfileActivity.class);
        i.putExtra(ProfileActivity.INTENT_KEY_USER, media.user.id);
        startActivityForResult(i, PAGE_PROFILE);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_marker_info_window, null);
        ViewHolder holder = new ViewHolder(view);
        Media media = mMediaMap.get(marker.getId());

        holder.contentText.setText(media.user.nickname);

        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onMapLoaded() {
        Toast.makeText(getActivity(), "MapLoaded", Toast.LENGTH_SHORT).show();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LocationManager.getLatLng(mPrefser), DEFAULT_ZOOM_LEVEL));
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        DisplayMetrics metrics = DeviceHelper.getScreenSize(getActivity());
        float meters = metrics.heightPixels * mMap.getScalePerPixel();

        Timber.d(Float.toString(meters));
        Timber.d(cameraPosition.toString());

        fetchData(cameraPosition.target, meters / 1000);

        LatLngBounds latLngBounds = mMap.getProjection().getVisibleRegion().latLngBounds;// 获取可视区域的Bounds
        for (Marker marker : mMap.getMapScreenMarkers()) {
            if (!latLngBounds.contains(marker.getPosition())) {
                Media media = mMediaMap.get(marker.getId());
                mUserSet.remove(media.user.id);
                mMediaMap.remove(marker.getId());
                marker.destroy();
            }
        }
    }

    class ViewHolder {
        @InjectView(R.id.text_content)
        public TextView contentText;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    class MarkerBuilder {
        MarkerOptions markerOption;
        AMap map;
        public MarkerBuilder(AMap aMap) {
            markerOption = new MarkerOptions();
            map = aMap;
        }

        public MarkerBuilder setPosition(LatLng latLng) {
            markerOption.position(latLng);
            return this;
        }

        public MarkerBuilder setIcon(BitmapDescriptor icon) {
            markerOption.icon(icon);
            return this;
        }

        public MarkerBuilder setIcons(ArrayList<BitmapDescriptor> icons) {
            markerOption.icons(icons);
            return this;
        }

        public MarkerBuilder setTitle(String title) {
            markerOption.title(title);
            return this;
        }

        public MarkerBuilder setSnippet(String snippet) {
            markerOption.snippet(snippet);
            return this;
        }

        public Marker show() {
            Marker marker = map.addMarker(markerOption);
            map.invalidate();
            return marker;
        }
    }

    class GetDataTask extends AsyncTask<Void, Void, Void> {
        Bitmap bitmap;
        Media media;

        public GetDataTask(Media media) {
            this.media = media;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                bitmap = mPicasso.load(media.resource.getUrl(Resource.ResourceSize.THUMBNAIL)).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (null != bitmap) {
                int thumbSize = (int) DisplayMetricsConverter.getPixel(getActivity(), 40);
                int marginSize = (int) DisplayMetricsConverter.getPixel(getActivity(), 4);

                RoundedImageView imageView = new RoundedImageView(getActivity());
                imageView.setImageBitmap(bitmap);
                imageView.setCornerRadius((float) thumbSize);

                RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(thumbSize, thumbSize);
                imageParams.setMargins(marginSize, marginSize, marginSize, marginSize);
                imageView.setLayoutParams(imageParams);

                RelativeLayout layout = new RelativeLayout(getActivity());
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        (int) DisplayMetricsConverter.getPixel(getActivity(), 48),
                        (int) DisplayMetricsConverter.getPixel(getActivity(), 51));
                layout.setLayoutParams(layoutParams);
                layout.setBackgroundResource(R.drawable.background_place_marker);
                layout.addView(imageView);

                Marker marker = new MarkerBuilder(mMap)
                        .setPosition(new LatLng(media.location[1], media.location[0]))
                        .setSnippet(media.user.nickname)
                        .setIcon(BitmapDescriptorFactory.fromView(layout))
                        .show();

                mMediaMap.put(marker.getId(), media);// add to markers
                mUserSet.add(media.user.id);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
}
