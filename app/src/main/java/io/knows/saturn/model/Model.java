package io.knows.saturn.model;

import io.knows.saturn.helper.StorageWrapper;
import nl.qbusict.cupboard.annotation.Index;

/**
 * Created by ryun on 15-4-20.
 */
public class Model {
    public Long _id; // for cupboard
    @Index(unique = true)
    public String id;

    public void save(StorageWrapper wrapper) {
        wrapper.save(this);
    }
}
