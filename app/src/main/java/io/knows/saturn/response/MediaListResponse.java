package io.knows.saturn.response;

import java.util.List;

import io.knows.saturn.model.Media;
import io.knows.saturn.model.Model;

/**
 * Created by ryun on 15-4-20.
 */
public class MediaListResponse implements ListResponse {
    private List<Media> data;

    @Override
    public List<Media> getList() {
        return data;
    }
}
