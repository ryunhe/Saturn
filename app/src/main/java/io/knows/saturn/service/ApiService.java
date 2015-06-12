package io.knows.saturn.service;

import io.knows.saturn.response.AuthResponse;
import io.knows.saturn.response.MatchedResponse;
import io.knows.saturn.response.MediaEntityResponse;
import io.knows.saturn.response.MediaListResponse;
import io.knows.saturn.response.SchoolListResponse;
import io.knows.saturn.response.StringResponse;
import io.knows.saturn.response.UserEntityResponse;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by ryun on 15-4-20.
 */
public interface ApiService {
    // Media
    @GET("/media/{media}")
    Observable<MediaEntityResponse> getMedia(@Path("media") String media);

    @GET("/media/feed")
    Observable<MediaListResponse> getFeedMedia();

    @GET("/media/recent")
    Observable<MediaListResponse> getRecentMedia();

    @GET("/media/nearby")
    Observable<MediaListResponse> getNearByMedia(@Query("lat") double lat,
                                                 @Query("lng") double lng,
                                                 @Query("distance") float distance);

    @GET("/media/popular")
    Observable<MediaListResponse> getPopularMedia();

    @FormUrlEncoded
    @POST("/media")
    Observable<MediaEntityResponse> createMedia(@Field("resource") String resource,
                                                @Field("content") String content,
                                                @Field("lat") double lat,
                                                @Field("lng") double lng);

    // User
    @GET("/users/{user}")
    Observable<UserEntityResponse> getUser(@Path("user") String user);

    @GET("/users/{user}/media/recent")
    Observable<MediaListResponse> getUserRecentMedia(@Path("user") String user,
                                                     @Query("limit") int limit);

    @FormUrlEncoded
    @POST("/users/self/profile")
    Observable<UserEntityResponse> updateProfile(@Field("nickname") String nickname,
                                                 @Field("gender") int gender,
                                                 @Field("birthday") int birthday,
                                                 @Field("hometown") String hometown,
                                                 @Field("school") String school,
                                                 @Field("department") String department,
                                                 @Field("bio") String bio);

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
    Observable<AuthResponse> authRenren(@Query("device_id") String deviceId,
                                        @Query("identity") String identity);

    // Service
    @GET("/services/qiniu/uptoken")
    Observable<StringResponse> getQiniuToken();

}
