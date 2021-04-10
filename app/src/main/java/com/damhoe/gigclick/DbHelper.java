package com.damhoe.gigclick;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "db_gigclick.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_SET = "t_set";
    public static final String TABLE_TRACK = "t_track";
    public static final String COLUMN_ID = "c_id";
    public static final String COLUMN_TITLE = "c_title";
    public static final String COLUMN_DATE = "c_date";
    public static final String COLUMN_BPM = "c_bpm";
    public static final String COLUMN_COMMENT = "c_comment";
    public static final String COLUMN_SET_ID = "c_set_id";
    public static final String COLUMN_FAVE = "c_fave";
    public static final String COLUMN_POSITION = "c_pos";
    public static final String UNIQUE_TRACK_POSITION = "u_t_pos";

    public static final String CREATE_TABLE_SET = "CREATE TABLE " + TABLE_SET + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TITLE + " TEXT, " +
            COLUMN_DATE + " INTEGER NOT NULL, " +
            COLUMN_FAVE + " INTEGER NOT NULL " + ");";

    public static final String CREATE_TABLE_TRACK = "CREATE TABLE " + TABLE_TRACK + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TITLE + " TEXT NOT NULL, " +
            COLUMN_BPM + " INTEGER NOT NULL, " +
            COLUMN_COMMENT + " TEXT, " +
            COLUMN_SET_ID + " INTEGER NOT NULL, " +
            COLUMN_POSITION + " INTEGER NOT NULL, " +
            " CONSTRAINT " + UNIQUE_TRACK_POSITION + " UNIQUE (" + COLUMN_SET_ID + "," + COLUMN_POSITION + "), " +
            " FOREIGN KEY (" + COLUMN_SET_ID + ") REFERENCES " + TABLE_SET + "(" + COLUMN_ID + "));";


    public DbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_SET);
        sqLiteDatabase.execSQL(CREATE_TABLE_TRACK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
