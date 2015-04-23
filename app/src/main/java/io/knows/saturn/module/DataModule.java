package io.knows.saturn.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.knows.saturn.BuildConfig;
import io.knows.saturn.fragment.MediaCardStackFragment;
import io.knows.saturn.fragment.MediaListFragment;
import io.knows.saturn.helper.CupboardDbHelper;
import nl.nl2312.rxcupboard.RxCupboard;
import nl.nl2312.rxcupboard.RxDatabase;
import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.CupboardBuilder;
import nl.qbusict.cupboard.CupboardFactory;
import timber.log.Timber;

import static com.jakewharton.byteunits.DecimalByteUnit.MEGABYTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static android.content.Context.MODE_PRIVATE;


/**
 * Created by ryun on 15-4-21.
 */
@Module(
    library = true,
    complete = false,
    includes = ApiModule.class,
    injects = {
        MediaListFragment.class,
        MediaCardStackFragment.class
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
    Cupboard provideCupboard() {
        CupboardFactory.setCupboard(new CupboardBuilder().useAnnotations().build());
        return CupboardFactory.cupboard();
    }

    @Provides @Singleton
    RxDatabase provideRxDatabase(Application application, Cupboard cupboard) {
        SQLiteDatabase db = CupboardDbHelper.getConnection(application);
        return RxCupboard.with(cupboard, db);
    }

    @Provides @Singleton
    SharedPreferences provideSharedPreferences(Application application) {
        return application.getSharedPreferences("io.knows.saturn", MODE_PRIVATE);
    }

    @Provides @Singleton
    OkHttpClient provideOkHttpClient(Application application) {
        return createOkHttpClient(application);
    }

    @Provides @Singleton
    Picasso providePicasso(Application application, OkHttpClient client) {
        return new Picasso.Builder(application)
                .downloader(new OkHttpDownloader(client))
                .listener((picasso, uri, e) -> Timber.e(e, "Failed to load image: %s", uri))
                .loggingEnabled(BuildConfig.DEBUG)
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
}
