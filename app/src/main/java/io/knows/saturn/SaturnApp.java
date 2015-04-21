package io.knows.saturn;

import android.app.Application;

import dagger.ObjectGraph;
import io.knows.saturn.module.ApiModule;

/**
 * Created by ryun on 15-4-21.
 */
public class SaturnApp extends Application {
    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        mObjectGraph = ObjectGraph.create(new ApiModule());
    }

    public void inject(Object o) {
        mObjectGraph.inject(o);
    }

    public ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }
}
