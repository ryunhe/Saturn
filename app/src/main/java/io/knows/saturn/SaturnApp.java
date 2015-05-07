package io.knows.saturn;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.adobe.creativesdk.foundation.AdobeCSDKFoundation;
import com.adobe.creativesdk.foundation.auth.IAdobeAuthClientCredentials;
import com.aviary.android.feather.sdk.IAviaryClientCredentials;
import com.github.pwittchen.networkevents.library.NetworkEventsConfig;
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
public class SaturnApp extends MultiDexApplication implements IAviaryClientCredentials {
    ObjectGraph mObjectGraph;
    static final String CREATIVE_SDK_CLIENT_ID = "6d56ef3ccebf4afe9dca49c82915e6df";
    static final String CREATIVE_SDK_CLIENT_SECRET = "d7927d46-6e6a-4ad5-8ba1-cb558d259c4c";

    @Override
    public void onCreate() {
        super.onCreate();

        // Dagger
        mObjectGraph = ObjectGraph.create(new DataModule(this), new RennModule());

        // Iconics
        Iconics.registerFont(new FontAwesome());
        Iconics.registerFont(new CommunityMaterial());

        // Adobe Creative SDK
        AdobeCSDKFoundation.initializeCSDKFoundation(getApplicationContext());

        // Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            // Crash reporting
        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }

    @Override
    public String getClientID() {
        return CREATIVE_SDK_CLIENT_ID;
    }

    @Override
    public String getClientSecret() {
        return CREATIVE_SDK_CLIENT_SECRET;
    }

    @Override
    public String getBillingKey() {
        return "";
    }

}
