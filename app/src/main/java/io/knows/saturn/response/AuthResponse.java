package io.knows.saturn.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.knows.saturn.model.Media;
import io.knows.saturn.model.Model;
import io.knows.saturn.model.User;

/**
 * Created by ryun on 15-4-20.
 */
public class AuthResponse {
    private Auth data;

    public class Auth {
        @SerializedName("session_code")
        public String sessionCode;
        @SerializedName("session_secret")
        public String sessionSecret;
        public User user;
    }

    public Auth getAuth() {
        return data;
    }
}
