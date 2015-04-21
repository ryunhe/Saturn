package io.knows.saturn;

import android.app.Application;

import javax.inject.Inject;

import dagger.ObjectGraph;
import dagger.internal.Modules;
import timber.log.Timber;

/**
 * Created by ryun on 15-4-21.
 */
public class SaturnApp extends Application {
    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        mObjectGraph = ObjectGraph.create();
    }

    public void inject(Object o) {
        mObjectGraph.inject(o);
    }

    public ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }
}
