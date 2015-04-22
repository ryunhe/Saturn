package io.knows.saturn.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.knows.saturn.fragment.MediaListFragment;
import io.knows.saturn.service.SamuiService;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RestAdapter;

/**
 * Created by ryun on 15-4-21.
 */
@Module (
    library = true
)
public class ApiModule {
    public static final String PRODUCTION_API_URL = "http://samui.knows.io/api/v1/";

    @Provides @Singleton
    Endpoint provideEndpoint() {
        return Endpoints.newFixedEndpoint(PRODUCTION_API_URL);
    }

    @Provides @Singleton
    RestAdapter provideRestAdapter(Endpoint endpoint) {
        return new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build();
    }

    @Provides @Singleton
    SamuiService provideSamuiService(RestAdapter restAdapter) {
        return restAdapter.create(SamuiService.class);
    }
}
