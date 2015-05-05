package io.knows.saturn.model;

import io.knows.saturn.helper.StorageWrapper;

/**
 * Created by ryun on 15-4-20.
 */
public class Media extends Model {
    public Resource resource;
    public User user;
    public String userId;
    public String content;
    public Double[] location;

    public void save(StorageWrapper wrapper) {
        userId = user.id;
        super.save(wrapper);
    }
}
