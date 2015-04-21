package io.knows.saturn.service;

import io.knows.saturn.model.Media;
import io.knows.saturn.model.User;
import io.knows.saturn.response.MediaEntityResponse;
import io.knows.saturn.response.MediaListResponse;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by ryun on 15-4-20.
 */
public interface MediaService {
    @GET("/media/{media}")
    Observable<MediaEntityResponse> get(@Path("media") String media);

    @GET("/media/popular")
    Observable<MediaListResponse> getPopular();

}
