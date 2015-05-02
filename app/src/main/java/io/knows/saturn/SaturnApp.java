package io.knows.saturn;

import android.app.Application;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.typeface.FontAwesome;

import dagger.ObjectGraph;
import io.knows.saturn.module.DataModule;
import io.knows.saturn.module.RennModule;
import timber.log.Timber;

/**
 * Created by ryun on 15-4-21.
 */
public class SaturnApp extends Application {
    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        mObjectGraph = ObjectGraph.create(new DataModule(this), new RennModule());

        Iconics.registerFont(new FontAwesome());
        Iconics.registerFont(new CommunityMaterial());

//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                        .setDefaultFontPath("fonts/LantingHei.ttf")
//                        .setFontAttrId(R.attr.fontPath)
//                        .build()
//        );

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            // Crash reporting
        }
    }

    public ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }
}
