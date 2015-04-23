package io.knows.saturn.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ryun on 15-4-20.
 */
public class Media extends Model {
    public String content;
    public Resource resource;

    public class Resource {
        public String standard;
        public String medium;
        public String thumbnail;
    }
}
