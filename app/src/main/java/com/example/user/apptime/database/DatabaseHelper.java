package com.example.user.apptime.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.user.apptime.Entity.Category;
import com.example.user.apptime.Entity.Photo;
import com.example.user.apptime.Entity.Record;
import com.example.user.apptime.Entity.StatisticCategory;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "myActionsDB";
    private static final int DB_VERSION = 1;
    private static final String CREATE_TABLE_CATEGORY = "create table category ("
            + "_id integer primary key autoincrement,"
            + "title text unique,"
            + "icon text" + ");";
    private static final String CREATE_TABLE_RECORD = "create table record ("
            + "_id integer primary key autoincrement,"
            + "time_start numeric,"
            + "time_end numeric,"
            + "description text,"
            + "duration numeric,"
            + "id_category integer,"
            + " FOREIGN KEY(id_category) REFERENCES category(_id));";
    private static final String CREATE_TABLE_PHOTO = "create table photo ("
            + "_id integer primary key autoincrement,"
            + "file_name text,"
            + "description text,"
            + "id_record integer,"
            + " FOREIGN KEY(id_record) REFERENCES record(_id));";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_CATEGORY);
        sqLiteDatabase.execSQL(CREATE_TABLE_RECORD);
        sqLiteDatabase.execSQL(CREATE_TABLE_PHOTO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    //TABLE CATEGORY

    public Category getCategory(long idCategory) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + DatabaseSchema.CategoryEntity.TABLE_NAME + " WHERE "
                + DatabaseSchema.CategoryEntity._ID + " = " + idCategory;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        Category category = new Category();
        category.setId(cursor.getLong(cursor.getColumnIndex(DatabaseSchema.CategoryEntity._ID)));
        category.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseSchema.CategoryEntity.COLUMN_NAME_TITLE)));
        category.setIcon(cursor.getString(cursor.getColumnIndex(DatabaseSchema.CategoryEntity.COLUMN_NAME_ICON)));
        return category;
    }

    public List<Category> getAllCategory() {
        List<Category> categoryList = new ArrayList<Category>();
        String selectQuery = "SELECT  * FROM " + DatabaseSchema.CategoryEntity.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getLong(cursor.getColumnIndex(DatabaseSchema.CategoryEntity._ID)));
                category.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseSchema.CategoryEntity.COLUMN_NAME_TITLE)));
                category.setIcon(cursor.getString(cursor.getColumnIndex(DatabaseSchema.CategoryEntity.COLUMN_NAME_ICON)));
                categoryList.add(category);
            } while (cursor.moveToNext());
        }
        return categoryList;
    }

    public boolean isUniqueCategory(String titleCategory) {
        String selectQuery = "SELECT * FROM " + DatabaseSchema.CategoryEntity.TABLE_NAME
                + " WHERE lower(" + DatabaseSchema.CategoryEntity.COLUMN_NAME_TITLE + ") = lower(?)";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{titleCategory.toLowerCase()});
        if (cursor.getCount() > 0)
            return false;
        else return true;
    }

    public long insertCategory(Category category) {
        long idCategory;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSchema.CategoryEntity.COLUMN_NAME_TITLE, category.getTitle());
        values.put(DatabaseSchema.CategoryEntity.COLUMN_NAME_ICON, category.getIcon());
        idCategory = db.insertOrThrow(DatabaseSchema.CategoryEntity.TABLE_NAME, null, values);

        return idCategory;
    }

    public int updateCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSchema.CategoryEntity.COLUMN_NAME_TITLE, category.getTitle());
        values.put(DatabaseSchema.CategoryEntity.COLUMN_NAME_ICON, category.getIcon());

        return db.update(DatabaseSchema.CategoryEntity.TABLE_NAME, values, DatabaseSchema.CategoryEntity._ID + " = ?",
                new String[]{String.valueOf(category.getId())});
    }

    public void deleteCategory(long idCategory) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DatabaseSchema.CategoryEntity.TABLE_NAME, DatabaseSchema.CategoryEntity._ID + " = ?",
                new String[]{String.valueOf(idCategory)});
    }

    //TABLE RECORD

    public Record getRecord(long idRecord) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + DatabaseSchema.RecordEntity.TABLE_NAME + " WHERE "
                + DatabaseSchema.RecordEntity._ID + " = " + idRecord;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        Record record = new Record();
        record.setId(cursor.getLong(cursor.getColumnIndex(DatabaseSchema.RecordEntity._ID)));
        record.setTimeStart(cursor.getLong(cursor.getColumnIndex(DatabaseSchema.RecordEntity.COLUMN_NAME_TIME_START)));
        record.setTimeEnd(cursor.getLong(cursor.getColumnIndex(DatabaseSchema.RecordEntity.COLUMN_NAME_TIME_END)));
        record.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseSchema.RecordEntity.COLUMN_NAME_DESCRIPTION)));
        record.setDuration(cursor.getLong(cursor.getColumnIndex(DatabaseSchema.RecordEntity.COLUMN_NAME_DURATION)));
        record.setIdCategory(cursor.getLong(cursor.getColumnIndex(DatabaseSchema.RecordEntity.COLUMN_NAME_ID_CATEGORY)));

        return record;
    }

    public List<Record> getAllRecord(long dateStart, long dateEnd) {
        List<Record> recordList = new ArrayList<Record>();
        String selectQuery = "SELECT  * FROM " + DatabaseSchema.RecordEntity.TABLE_NAME
                + " where time_start >= ? AND time_start<= ? ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,new String[]{String.valueOf(dateStart), String.valueOf(dateEnd)});

        if (cursor.moveToFirst()) {
            do {
                Record record = new Record();
                record.setId(cursor.getLong(cursor.getColumnIndex(DatabaseSchema.RecordEntity._ID)));
                record.setTimeStart(cursor.getLong(cursor.getColumnIndex(DatabaseSchema.RecordEntity.COLUMN_NAME_TIME_START)));
                record.setTimeEnd(cursor.getLong(cursor.getColumnIndex(DatabaseSchema.RecordEntity.COLUMN_NAME_TIME_END)));
                record.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseSchema.RecordEntity.COLUMN_NAME_DESCRIPTION)));
                record.setDuration(cursor.getLong(cursor.getColumnIndex(DatabaseSchema.RecordEntity.COLUMN_NAME_DURATION)));
                record.setIdCategory(cursor.getLong(cursor.getColumnIndex(DatabaseSchema.RecordEntity.COLUMN_NAME_ID_CATEGORY)));
                recordList.add(record);
            } while (cursor.moveToNext());
        }
        return recordList;
    }

    public long insertRecord(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSchema.RecordEntity.COLUMN_NAME_TIME_START, record.getTimeStart());
        values.put(DatabaseSchema.RecordEntity.COLUMN_NAME_TIME_END, record.getTimeEnd());
        values.put(DatabaseSchema.RecordEntity.COLUMN_NAME_DESCRIPTION, record.getDescription());
        values.put(DatabaseSchema.RecordEntity.COLUMN_NAME_DURATION, record.getDuration());
        values.put(DatabaseSchema.RecordEntity.COLUMN_NAME_ID_CATEGORY, record.getIdCategory());
        long idRecord = db.insertOrThrow(DatabaseSchema.RecordEntity.TABLE_NAME, null, values);

        return idRecord;
    }

    public int updateRecord(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSchema.RecordEntity.COLUMN_NAME_TIME_START, record.getTimeStart());
        values.put(DatabaseSchema.RecordEntity.COLUMN_NAME_TIME_END, record.getTimeEnd());
        values.put(DatabaseSchema.RecordEntity.COLUMN_NAME_DESCRIPTION, record.getDescription());
        values.put(DatabaseSchema.RecordEntity.COLUMN_NAME_DURATION, record.getDuration());
        values.put(DatabaseSchema.RecordEntity.COLUMN_NAME_ID_CATEGORY, record.getIdCategory());

        return db.update(DatabaseSchema.RecordEntity.TABLE_NAME, values, DatabaseSchema.RecordEntity._ID + " = ?",
                new String[]{String.valueOf(record.getId())});
    }

    public void deleteRecord(long idRecord) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DatabaseSchema.RecordEntity.TABLE_NAME, DatabaseSchema.RecordEntity._ID + " = ?",
                new String[]{String.valueOf(idRecord)});
    }


    //TABLE PHOTO

    public Photo getPhoto(long idPhoto) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + DatabaseSchema.PhotoEntity.TABLE_NAME + " WHERE "
                + DatabaseSchema.PhotoEntity._ID + " = " + idPhoto;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        Photo photo = new Photo();
        photo.setId(cursor.getLong(cursor.getColumnIndex(DatabaseSchema.PhotoEntity._ID)));
        photo.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseSchema.PhotoEntity.COLUMN_NAME_DESCRIPTION)));
        photo.setIdRecord(cursor.getLong(cursor.getColumnIndex(DatabaseSchema.PhotoEntity.COLUMN_NAME_ID_RECORD)));
        photo.setFileName(cursor.getString(cursor.getColumnIndex(DatabaseSchema.PhotoEntity.COLUMN_NAME_FILE_NAME)));

        return photo;
    }

    public List<Photo> getAllPhotoByRecordId(long idRecord) {
        List<Photo> photoList = new ArrayList<Photo>();
        String selectQuery = "SELECT  * FROM " + DatabaseSchema.PhotoEntity.TABLE_NAME
                + " WHERE " + DatabaseSchema.PhotoEntity.COLUMN_NAME_ID_RECORD + "=" + idRecord;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Photo photo = new Photo();
                photo.setId(cursor.getLong(cursor.getColumnIndex(DatabaseSchema.PhotoEntity._ID)));
                photo.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseSchema.PhotoEntity.COLUMN_NAME_DESCRIPTION)));
                photo.setIdRecord(cursor.getLong(cursor.getColumnIndex(DatabaseSchema.PhotoEntity.COLUMN_NAME_ID_RECORD)));
                photo.setFileName(cursor.getString(cursor.getColumnIndex(DatabaseSchema.PhotoEntity.COLUMN_NAME_FILE_NAME)));
                photoList.add(photo);
            } while (cursor.moveToNext());
        }
        return photoList;
    }

    public long insertPhoto(Photo photo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSchema.PhotoEntity.COLUMN_NAME_DESCRIPTION, photo.getDescription());
        values.put(DatabaseSchema.PhotoEntity.COLUMN_NAME_FILE_NAME, photo.getFileName());
        values.put(DatabaseSchema.PhotoEntity.COLUMN_NAME_ID_RECORD, photo.getIdRecord());
        long idPhoto = db.insertOrThrow(DatabaseSchema.PhotoEntity.TABLE_NAME, null, values);

        return idPhoto;
    }

    public int updatePhoto(Photo photo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseSchema.PhotoEntity.COLUMN_NAME_DESCRIPTION, photo.getDescription());
        values.put(DatabaseSchema.PhotoEntity.COLUMN_NAME_FILE_NAME, photo.getFileName());
        values.put(DatabaseSchema.PhotoEntity.COLUMN_NAME_ID_RECORD, photo.getIdRecord());

        return db.update(DatabaseSchema.PhotoEntity.TABLE_NAME, values, DatabaseSchema.PhotoEntity._ID + " = ?",
                new String[]{String.valueOf(photo.getId())});
    }

    public void deletePhoto(long idPhoto) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DatabaseSchema.PhotoEntity.TABLE_NAME, DatabaseSchema.PhotoEntity._ID + " = ?",
                new String[]{String.valueOf(idPhoto)});
    }

    public void deletePhotoByRecordId(long idRecord) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DatabaseSchema.PhotoEntity.TABLE_NAME, DatabaseSchema.PhotoEntity.COLUMN_NAME_ID_RECORD + " = ?",
                new String[]{String.valueOf(idRecord)});
    }

    //Statistics

    public List<StatisticCategory> firstStatistic(long dateStart, long dateEnd){

        List<StatisticCategory> statisticCategoryList = new ArrayList<StatisticCategory>();
        String selectQuery = "Select count(*) as res, record.id_category as id_cat, title, icon "
                + "from record "
                + "inner join category "
                + "on category._id = record.id_category "
                + "where time_start >= ? AND time_start<= ? "
                + "group by id_cat, title, icon "
                + "order by res desc";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(dateStart), String.valueOf(dateEnd)});

        if (cursor.moveToFirst()) {
            do {
                StatisticCategory statisticCategory = new StatisticCategory();
                Category category = new Category();
                statisticCategory.setStatisticRes(cursor.getString(cursor.getColumnIndex("res")));
                category.setId(cursor.getLong(cursor.getColumnIndex("id_cat")));
                category.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseSchema.CategoryEntity.COLUMN_NAME_TITLE)));
                category.setIcon(cursor.getString(cursor.getColumnIndex(DatabaseSchema.CategoryEntity.COLUMN_NAME_ICON)));
                statisticCategory.setCategory(category);
                statisticCategoryList.add(statisticCategory);
            } while (cursor.moveToNext());
        }
        return statisticCategoryList;
    }

    public List<StatisticCategory> secondStatistic(long dateStart, long dateEnd){

        List<StatisticCategory> statisticCategoryList = new ArrayList<StatisticCategory>();
        String selectQuery = "Select max(duration) as res , record.id_category as id_cat, title, icon "
                + "from record "
                + "inner join category "
                + "on category._id = record.id_category "
                + "where time_start >= ? AND time_start<= ? "
                + "group by id_cat, title, icon "
                + "order by res desc";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(dateStart), String.valueOf(dateEnd)});

        if (cursor.moveToFirst()) {
            do {
                StatisticCategory statisticCategory = new StatisticCategory();
                Category category = new Category();
                long duration = cursor.getLong(cursor.getColumnIndex("res"))+10800000;
                long hour = duration/3600000;
                long minutes = (duration-hour*3600000)/60000;
                statisticCategory.setStatisticRes(hour+" ч "+minutes+" мин");
                category.setId(cursor.getLong(cursor.getColumnIndex("id_cat")));
                category.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseSchema.CategoryEntity.COLUMN_NAME_TITLE)));
                category.setIcon(cursor.getString(cursor.getColumnIndex(DatabaseSchema.CategoryEntity.COLUMN_NAME_ICON)));
                statisticCategory.setCategory(category);
                statisticCategoryList.add(statisticCategory);
            } while (cursor.moveToNext());
        }
        return statisticCategoryList;
    }

    public List<StatisticCategory> thirdStatistic(long dateStart, long dateEnd, List<Category> categoryList){

        List<StatisticCategory> statisticCategoryList = new ArrayList<StatisticCategory>();
        String inCategory="(";
        for(Category category: categoryList){
            inCategory=inCategory+category.getId()+",";
        }
        inCategory=inCategory.substring(0,inCategory.length()-1);
        inCategory=inCategory+")";
        String selectQuery = "Select sum(duration) as res ,count(*) as count, record.id_category as id_cat, title, icon "
                + "from record "
                + "inner join category "
                + "on category._id = record.id_category "
                + "where time_start >= ? AND time_start<= ? "
                + " AND record.id_category IN "+inCategory
                + " group by id_cat, title, icon "
                + "order by res desc";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(dateStart), String.valueOf(dateEnd)});

        if (cursor.moveToFirst()) {
            do {
                StatisticCategory statisticCategory = new StatisticCategory();
                Category category = new Category();
                int count = cursor.getInt(cursor.getColumnIndex("count"));
                long duration = cursor.getLong(cursor.getColumnIndex("res"))+count*10800000;
                long hour = duration/3600000;
                long minutes = (duration-hour*3600000)/60000;
                statisticCategory.setStatisticRes(hour+" ч "+minutes+" мин");
                category.setId(cursor.getLong(cursor.getColumnIndex("id_cat")));
                category.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseSchema.CategoryEntity.COLUMN_NAME_TITLE)));
                category.setIcon(cursor.getString(cursor.getColumnIndex(DatabaseSchema.CategoryEntity.COLUMN_NAME_ICON)));
                statisticCategory.setCategory(category);
                statisticCategoryList.add(statisticCategory);
            } while (cursor.moveToNext());
        }
        return statisticCategoryList;
    }

    public List<StatisticCategory> fourthStatistic(long dateStart, long dateEnd){

        List<StatisticCategory> statisticCategoryList = new ArrayList<StatisticCategory>();
        String selectQuery = "Select sum(duration) as res ,count(*) as count, record.id_category as id_cat, title, icon "
                + "from record "
                + "inner join category "
                + "on category._id = record.id_category "
                + "where time_start >= ? AND time_start<= ? "
                + "group by id_cat, title, icon "
                + "order by res desc";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(dateStart), String.valueOf(dateEnd)});

        if (cursor.moveToFirst()) {
            do {
                StatisticCategory statisticCategory = new StatisticCategory();
                Category category = new Category();
                int count = cursor.getInt(cursor.getColumnIndex("count"));
                long duration = cursor.getLong(cursor.getColumnIndex("res"))+count*10800000;
                statisticCategory.setStatisticRes(String.valueOf(duration));
                category.setId(cursor.getLong(cursor.getColumnIndex("id_cat")));
                category.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseSchema.CategoryEntity.COLUMN_NAME_TITLE)));
                category.setIcon(cursor.getString(cursor.getColumnIndex(DatabaseSchema.CategoryEntity.COLUMN_NAME_ICON)));
                statisticCategory.setCategory(category);
                statisticCategoryList.add(statisticCategory);
            } while (cursor.moveToNext());
        }
        return statisticCategoryList;
    }

}
