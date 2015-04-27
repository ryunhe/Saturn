package io.knows.saturn.helper;

import com.google.gson.Gson;

import java.lang.reflect.Type;

import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.convert.FieldConverter;
import nl.qbusict.cupboard.convert.FieldConverterFactory;

/**
 * Created by ryun on 15-4-25.
 */
public class GsonFieldConverterFactory implements FieldConverterFactory {
    private Type mType;
    private Gson mGson;

    public GsonFieldConverterFactory(Gson gson, Type type) {
        this.mGson = gson;
        this.mType = type;
    }

    @Override
    public FieldConverter<?> create(Cupboard cupboard, Type type) {
        if (type == mType) {
            return new GsonFieldConverter(mGson, type);
        }
        return null;
    }
}
