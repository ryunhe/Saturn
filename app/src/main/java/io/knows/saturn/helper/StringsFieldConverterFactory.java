package io.knows.saturn.helper;

import com.google.gson.Gson;

import java.lang.reflect.Type;

import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.convert.FieldConverter;
import nl.qbusict.cupboard.convert.FieldConverterFactory;

/**
 * Created by ryun on 15-4-25.
 */
public class StringsFieldConverterFactory implements FieldConverterFactory {
    Gson mGson;
    public StringsFieldConverterFactory(Gson gson) {
        this.mGson = gson;
    }

    @Override
    public FieldConverter<?> create(Cupboard cupboard, Type type) {
        if (type == String[].class) {
            return new GsonFieldConverter<>(mGson, String[].class);
        }
        return null;
    }
}
