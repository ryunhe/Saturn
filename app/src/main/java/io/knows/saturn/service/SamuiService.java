package io.knows.saturn.service;

import io.knows.saturn.response.AuthResponse;
import io.knows.saturn.response.MatchedResponse;
import io.knows.saturn.response.MediaEntityResponse;
import io.knows.saturn.response.MediaListResponse;
import io.knows.saturn.response.SchoolListResponse;
import io.knows.saturn.response.UserEntityResponse;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
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
    Observable<MediaListResponse> getFeedMedia();

    @GET("/media/recent")
    Observable<MediaListResponse> getRecentMedia();

    @GET("/media/popular")
    Observable<MediaListResponse> getPopularMedia();

    // User
    @GET("/users/{user}")
    Observable<UserEntityResponse> getUser(@Path("user") String user);

    @GET("/users/{user}/media/recent")
    Observable<MediaListResponse> getUserRecentMedia(@Path("user") String user, @Query("limit") int limit);

    // Like
    @POST("/media/{media}/likes")
    Observable<MatchedResponse> like(@Path("media") String media);

    @DELETE("/media/{media}/likes")
    void dislike(@Path("media") String media);

    // School
    @GET("/schools/search")
    Observable<SchoolListResponse> getSearchSchool(@Query("q") String query);

    // Auth
    @GET("/auth/renren")
    Observable<AuthResponse> authRenren(@Query("device_id") String deviceId, @Query("identity") String identity);

}
