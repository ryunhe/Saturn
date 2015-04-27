package io.knows.saturn.helper;

import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.convert.ReflectiveEntityConverter;

/**
 * Created by ryun on 15-4-25.
 */
public class ModelReflectiveEntityConverter extends ReflectiveEntityConverter {
    public ModelReflectiveEntityConverter(Cupboard cupboard, Class entityClass) {
        super(cupboard, entityClass);
    }
}
