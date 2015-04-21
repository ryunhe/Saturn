package io.knows.saturn.response;

import java.util.List;

import io.knows.saturn.model.Media;

/**
 * Created by ryun on 15-4-20.
 */
public class MediaEntityResponse implements EntityResponse {
    private Media data;

    @Override
    public Media getEntity() {
        return data;
    }
}
