package com.esraa.android.plannertracker.SqltieDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Trip.db";
    public static final int DATABASE_VERSION = 1;


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + DbContract.DbTripDetails.TABLE_NAME+ " (" +
                        DbContract.DbTripDetails.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        DbContract.DbTripDetails.COLUMN_TRIP_NAME+ " TEXT NOT NULL," +
                        DbContract.DbTripDetails.COLUMN_START_POINT + " TEXT NOT NULL," +
                        DbContract.DbTripDetails.COLUMN_END_POINT + "TEXT NOT NULL," +
                        DbContract.DbTripDetails.COLUMN_DATE_TRIP+ " TEXT NOT NULL," +
                        DbContract.DbTripDetails.COLUMN_TIME_TRIP+ " TEXT NOT NULL," +
                        DbContract.DbTripDetails.COLUMN_NOTES+ " TEXT NOT NULL," +
                        DbContract.DbTripDetails.COLUMN_TYPE_TRIP+ " TEXT NOT NULL ) ";
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + DbContract.DbTripDetails.TABLE_NAME;
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
