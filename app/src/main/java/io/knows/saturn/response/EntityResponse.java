package io.knows.saturn.response;

import java.util.List;

import io.knows.saturn.model.Model;

/**
 * Created by ryun on 15-4-20.
 */
public interface EntityResponse {
    <T extends Model> T getEntity();
}