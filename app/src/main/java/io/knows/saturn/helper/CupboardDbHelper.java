package io.knows.saturn.helper;

/**
 * Created by ryun on 15-4-22.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.knows.saturn.model.Media;
import io.knows.saturn.model.User;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class CupboardDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SaturnCupboard.db";
    private static final int DATABASE_VERSION = 1;

    private static SQLiteDatabase database;

    static {
        // Register our models with Cupboard as usual
        cupboard().register(User.class);
        cupboard().register(Media.class);
    }

    /**
     * Returns a raw handle to the SQLite database connection. Do not close!
     * @param context A context, which is used to (when needed) set up a connection to the database
     * @return The single, unique connection to the database, as is (also) used by our Cupboard instance
     */
    public synchronized static SQLiteDatabase getConnection(Context context) {
        if (database == null) {
            // Construct the single helper and open the unique(!) db connection for the app
            database = new CupboardDbHelper(context.getApplicationContext()).getWritableDatabase();
        }
        return database;
    }

    public CupboardDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        cupboard().withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        cupboard().withDatabase(db).upgradeTables();
    }
}