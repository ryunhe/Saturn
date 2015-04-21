package io.knows.saturn.service;

import io.knows.saturn.model.Media;
import io.knows.saturn.model.User;
import io.knows.saturn.response.MediaEntityResponse;
import io.knows.saturn.response.MediaListResponse;
import io.knows.saturn.response.UserEntityResponse;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by ryun on 15-4-20.
 */
public interface SamuiService {
    // Media
    @GET("/media/{media}")
    Observable<MediaEntityResponse> getMedia(@Path("media") String media);

    @GET("/media/popular")
    Observable<MediaListResponse> getPopularMedia();

    // User
    @GET("/users/{user}")
    Observable<UserEntityResponse> getUser(@Path("user") String user);

    @GET("/users/{user}/media/recent")
    Observable<MediaListResponse> getUserRecentMedia(@Path("user") String user);

}
