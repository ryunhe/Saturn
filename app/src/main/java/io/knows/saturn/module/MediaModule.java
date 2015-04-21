package io.knows.saturn.module;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.knows.saturn.fragment.ListFragment;
import io.knows.saturn.model.Media;
import io.knows.saturn.response.MediaListResponse;
import io.knows.saturn.service.SamuiService;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ryun on 15-4-21.
 */
@Module(
    includes = ApiModule.class,
    injects = ListFragment.class
)

public class MediaModule {
    @Provides
    Observable<MediaListResponse> provideMediaPopular(SamuiService samuiService) {
        return samuiService.getPopularMedia()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
