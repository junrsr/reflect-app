package com.example.reflect;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    // variable used to denote the version of the database. Manually increase on database changes.
    private static final int DB_VERSION = 3;

    /**
     * creating a new instance of the database in a particular context
     * @param context the context we want to use the database in
     */
    public DatabaseHelper(@Nullable Context context) {
        // create the database
        super(context, Utils.DATABASE_NAME, null, DB_VERSION);
    }

    /**
     * creates the relevant database tables throughout our project
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create the morning reflection table
        String morningTableStatement = "CREATE TABLE " + Utils.MORNING_REFLECTION_TABLE +  " (id INTEGER PRIMARY KEY AUTOINCREMENT, date INTEGER, sleepScore INTEGER, motivationScore INTEGER)"; // TODO: add additional field for goals
        db.execSQL(morningTableStatement);
    }

    /**
     *
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
