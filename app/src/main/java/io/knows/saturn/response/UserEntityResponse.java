package io.knows.saturn.response;

import io.knows.saturn.model.Media;
import io.knows.saturn.model.User;

/**
 * Created by ryun on 15-4-20.
 */
public class UserEntityResponse implements EntityResponse {
    private User data;

    @Override
    public User getEntity() {
        return data;
    }
}
