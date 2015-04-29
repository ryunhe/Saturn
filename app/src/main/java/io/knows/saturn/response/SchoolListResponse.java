package io.knows.saturn.response;

import java.util.List;

import io.knows.saturn.model.Media;

/**
 * Created by ryun on 15-4-20.
 */
public class SchoolListResponse implements ListResponse {
    private List<String> data;

    @Override
    public List<String> getResult() {
        return data;
    }
}
