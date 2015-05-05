package io.knows.saturn.helper;

import com.google.gson.Gson;

import java.lang.reflect.Type;

import io.knows.saturn.model.Resource;
import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.convert.FieldConverter;
import nl.qbusict.cupboard.convert.FieldConverterFactory;

/**
 * Created by ryun on 15-4-25.
 */
public class ResourceFieldConverterFactory implements FieldConverterFactory {
    @Override
    public FieldConverter<?> create(Cupboard cupboard, Type type) {
        if (type == Resource.class) {
            return new ResourceFieldConverter();
        }
        return null;
    }
}
