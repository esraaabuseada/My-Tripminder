package com.esraa.android.plannertracker.SqltieDatabase;

import android.provider.BaseColumns;

public  final class DbContract {

    public DbContract(){}

    public static class DbTripDetails implements BaseColumns {

        public static final String TABLE_NAME = "tripDetails";
        public static final String COLUMN_ID= "Id";
        public static final String COLUMN_TRIP_NAME= "tripName";
        public static final String COLUMN_START_POINT = "start";
        public static final String COLUMN_END_POINT = "end";
        public static final String COLUMN_DATE_TRIP = "date";
        public static final String COLUMN_TIME_TRIP = "time";
        public static final String COLUMN_NOTES = "notes";
        public static final String COLUMN_TYPE_TRIP= "type";



    }
}
