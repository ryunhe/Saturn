package io.knows.saturn.service;

import io.knows.saturn.model.User;
import io.knows.saturn.response.MediaListResponse;
import io.knows.saturn.response.UserEntityResponse;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by ryun on 15-4-20.
 */
public interface UserService {
    @GET("/users/{user}")
    Observable<UserEntityResponse> get(@Path("user") String user);

    @GET("/users/{user}/media/recent")
    Observable<MediaListResponse> getRecent(@Path("user") String user);
}
