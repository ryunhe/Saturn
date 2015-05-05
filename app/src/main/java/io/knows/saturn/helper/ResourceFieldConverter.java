package io.knows.saturn.helper;

import android.content.ContentValues;
import android.database.Cursor;

import io.knows.saturn.model.Resource;
import nl.qbusict.cupboard.convert.EntityConverter;
import nl.qbusict.cupboard.convert.FieldConverter;

/**
 * Created by ryun on 15-4-25.
 */
public class ResourceFieldConverter implements FieldConverter<Resource> {
    @Override
    public Resource fromCursorValue(Cursor cursor, int columnIndex) {
        return new Resource(cursor.getString(columnIndex));
    }

    @Override
    public EntityConverter.ColumnType getColumnType() {
        return EntityConverter.ColumnType.TEXT;
    }

    @Override
    public void toContentValue(Resource value, String key, ContentValues values) {
        values.put(key, value.identity);
    }
}