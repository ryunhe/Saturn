package io.knows.saturn.service;

import io.knows.saturn.model.Media;
import io.knows.saturn.model.User;
import io.knows.saturn.response.MediaEntityResponse;
import io.knows.saturn.response.MediaListResponse;
import io.knows.saturn.response.UserEntityResponse;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by ryun on 15-4-20.
 */
public interface SamuiService {
    // Media
    @GET("/media/{media}")
    Observable<MediaEntityResponse> getMedia(@Path("media") String media);

    @GET("/media/feed")
    Observable<MediaListResponse> getFeedMedia(@Query("offset") int offset);

    @GET("/media/recent")
    Observable<MediaListResponse> getRecentMedia(@Query("offset") int offset);

    @GET("/media/popular")
    Observable<MediaListResponse> getPopularMedia(@Query("offset") int offset);

    // User
    @GET("/users/{user}")
    Observable<UserEntityResponse> getUser(@Path("user") String user);

    @GET("/users/{user}/media/recent")
    Observable<MediaListResponse> getUserRecentMedia(@Path("user") String user, @Query("offset") int offset);

}
