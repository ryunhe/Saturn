package io.knows.saturn.module;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.github.pwittchen.networkevents.library.NetworkEvents;
import com.github.pwittchen.prefser.library.Prefser;
import com.google.gson.Gson;
import com.qiniu.android.storage.UploadManager;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.knows.saturn.activity.CongratsActivity;
import io.knows.saturn.activity.MainActivity;
import io.knows.saturn.activity.PostActivity;
import io.knows.saturn.activity.ProfileActivity;
import io.knows.saturn.activity.RegionPickerActivity;
import io.knows.saturn.activity.SchoolPickerActivity;
import io.knows.saturn.activity.SignupActivity;
import io.knows.saturn.fragment.AuthFragment;
import io.knows.saturn.fragment.CongratsFragment;
import io.knows.saturn.fragment.CardStackFragment;
import io.knows.saturn.fragment.CropperFragment;
import io.knows.saturn.fragment.MapDiscoverFragment;
import io.knows.saturn.fragment.MediaListFragment;
import io.knows.saturn.fragment.PostFragment;
import io.knows.saturn.fragment.ProfileFragment;
import io.knows.saturn.fragment.RegionPickerFragment;
import io.knows.saturn.fragment.SchoolPickerFragment;
import io.knows.saturn.fragment.SignupFragment;
import io.knows.saturn.helper.CupboardHelper;
import io.knows.saturn.helper.DoublesFieldConverterFactory;
import io.knows.saturn.helper.GsonFieldConverterFactory;
import io.knows.saturn.helper.LocationManager;
import io.knows.saturn.helper.ResourceFieldConverterFactory;
import io.knows.saturn.helper.StorageWrapper;
import io.knows.saturn.helper.StringsFieldConverterFactory;
import io.knows.saturn.model.Authenticator;
import io.knows.saturn.model.Media;
import io.knows.saturn.model.Resource;
import io.knows.saturn.model.User;
import nl.nl2312.rxcupboard.RxCupboard;
import nl.nl2312.rxcupboard.RxDatabase;
import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.CupboardBuilder;
import nl.qbusict.cupboard.CupboardFactory;
import nl.qbusict.cupboard.DatabaseCompartment;
import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;
import static com.jakewharton.byteunits.DecimalByteUnit.MEGABYTES;
import static java.util.concurrent.TimeUnit.SECONDS;


/**
 * Created by ryun on 15-4-21.
 */
@Module(
        library = true,
        complete = false,
        includes = ApiModule.class,
        injects = {
                MediaListFragment.class,
                CardStackFragment.class,
                MapDiscoverFragment.class,
                CongratsFragment.class,
                ProfileFragment.class,
                SchoolPickerFragment.class,
                RegionPickerFragment.class,
                CropperFragment.class,
                PostFragment.class,
                AuthFragment.class,
                SignupFragment.class,

                MainActivity.class,
                CongratsActivity.class,
                ProfileActivity.class,
                SignupActivity.class,
                PostActivity.class,
                SchoolPickerActivity.class,
                RegionPickerActivity.class,
        }
)
public class DataModule {
    static final int DISK_CACHE_SIZE = (int) MEGABYTES.toBytes(100);
    Application application;

    public DataModule(Application application) {
        this.application = application;
    }

    @Provides
    Application provideApplication() {
        return application;
    }

    @Provides @Singleton
    Cupboard provideCupboard(Gson gson) {
        return createCupboard(gson);
    }

    @Provides @Singleton
    Authenticator provideAuthenticator(StorageWrapper wrapper, Prefser prefser) {
        return Authenticator.getInstance(wrapper, prefser);
    }

    @Provides @Singleton
    SQLiteDatabase provideSQLiteDatabase(Application application) {
        return CupboardHelper.getConnection(application);
    }

    @Provides @Singleton
    Gson provideGson() {
        return new Gson();
    }

    @Provides @Singleton
    Bus provideBus() {
        return new Bus();
    }

    @Provides @Singleton
    NetworkEvents provideNetworkEvents(Application application, Bus bus) {
        return new NetworkEvents(application, bus, "http://www.baidu.com");
    }

    @Provides
    LocationManager provideLocationManager(Application application, Prefser prefser) {
        return new LocationManager(application, prefser);
    }

    @Provides @Singleton
    RxDatabase provideRxDatabase(Cupboard cupboard, SQLiteDatabase db) {
        return RxCupboard.with(cupboard, db);
    }

    @Provides @Singleton
    StorageWrapper provideStorageWrapper(RxDatabase database) {
        return new StorageWrapper(database);
    }

    @Provides @Singleton
    DatabaseCompartment provideDatabaseCompartment(Cupboard cupboard, SQLiteDatabase db) {
        return cupboard.withDatabase(db);
    }

    @Provides @Singleton
    SharedPreferences provideSharedPreferences(Application application) {
        return application.getSharedPreferences("io.knows.saturn", MODE_PRIVATE);
    }

    @Provides @Singleton
    Prefser providePrefser(SharedPreferences sharedPreferences) {
        return new Prefser(sharedPreferences);
    }

    @Provides @Singleton
    OkHttpClient provideOkHttpClient(Application application) {
        return createOkHttpClient(application);
    }

    @Provides @Singleton
    UploadManager provideUploadManager() {
        return new UploadManager();
    }

    @Provides @Singleton
    Picasso providePicasso(Application application, OkHttpClient client) {
        return new Picasso.Builder(application)
                .downloader(new OkHttpDownloader(client))
                .listener((picasso, uri, e) -> Timber.e(e, "Failed to load image: %s", uri))
//                .loggingEnabled(BuildConfig.DEBUG)
                .build();
    }

    static OkHttpClient createOkHttpClient(Application application) {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(10, SECONDS);
        client.setReadTimeout(10, SECONDS);
        client.setWriteTimeout(10, SECONDS);

        // Install an HTTP cache in the application cache directory.
        File cacheDir = new File(application.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
        client.setCache(cache);

        return client;
    }

    static Cupboard createCupboard(Gson gson) {
        CupboardFactory.setCupboard(new CupboardBuilder()
                .registerFieldConverterFactory(new GsonFieldConverterFactory(gson, User.Counts.class))
                .registerFieldConverterFactory(new GsonFieldConverterFactory(gson, User.Like[].class))
                .registerFieldConverterFactory(new StringsFieldConverterFactory(gson))
                .registerFieldConverterFactory(new DoublesFieldConverterFactory(gson))
                .registerFieldConverterFactory(new ResourceFieldConverterFactory())
                .useAnnotations()
                .build());
        return CupboardFactory.cupboard();
    }
}
