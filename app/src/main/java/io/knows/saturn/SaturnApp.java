package io.knows.saturn;

import android.app.Application;

import dagger.ObjectGraph;
import io.knows.saturn.module.ApiModule;
import io.knows.saturn.module.DataModule;
import timber.log.Timber;

/**
 * Created by ryun on 15-4-21.
 */
public class SaturnApp extends Application {
    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        mObjectGraph = ObjectGraph.create(new DataModule(this));
    }

    public void inject(Object o) {
        mObjectGraph.inject(o);
    }

    public ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }
}
