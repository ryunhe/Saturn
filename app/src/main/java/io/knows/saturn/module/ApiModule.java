package io.knows.saturn.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.knows.saturn.service.ApiService;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

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
                .setErrorHandler(new ApiErrorHandler())
//                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build();
    }

    @Provides @Singleton
    ApiService provideApiService(RestAdapter restAdapter) {
        return restAdapter.create(ApiService.class);
    }

    class ApiErrorHandler implements ErrorHandler {
        @Override
        public Throwable handleError(RetrofitError cause) {
            Response responseError = cause.getResponse();
            Timber.d(responseError.getReason());
            return null;
        }
    }
}
