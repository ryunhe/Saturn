package io.knows.saturn.response;

import java.util.HashMap;

import io.knows.saturn.model.User;

/**
 * Created by ryun on 15-4-20.
 */
public class MatchedResponse {
    private HashMap<String, Boolean> data;

    public boolean matched() {
        return data.get("matched");
    }
}
