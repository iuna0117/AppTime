package com.example.user.apptime.database;

import android.provider.BaseColumns;

public class DatabaseSchema {
    public static abstract class CategoryEntity implements BaseColumns {
        public static final String TABLE_NAME = "category";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_ICON = "icon";
    }

    public static abstract class RecordEntity implements BaseColumns {
        public static final String TABLE_NAME = "record";
        public static final String COLUMN_NAME_TIME_START = "time_start";
        public static final String COLUMN_NAME_TIME_END = "time_end";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_DURATION = "duration";
        public static final String COLUMN_NAME_ID_CATEGORY = "id_category";
    }

    public static abstract class PhotoEntity implements BaseColumns {
        public static final String TABLE_NAME = "photo";
        public static final String COLUMN_NAME_FILE_NAME = "file_name";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_ID_RECORD = "id_record";
    }
}
