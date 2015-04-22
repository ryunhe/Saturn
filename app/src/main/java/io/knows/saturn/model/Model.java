package io.knows.saturn.model;

import nl.qbusict.cupboard.annotation.Column;
import nl.qbusict.cupboard.annotation.Index;

/**
 * Created by ryun on 15-4-20.
 */
public class Model {
    public Long _id; // for cupboard
    @Index(unique = true)
    public String id;
}
