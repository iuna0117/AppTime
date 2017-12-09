package com.example.user.apptime.database;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.user.apptime.R;
import com.example.user.apptime.Entity.Category;
import com.example.user.apptime.Entity.Photo;
import com.example.user.apptime.Entity.Record;
import com.example.user.apptime.Entity.StatisticCategory;

import java.io.Serializable;
import java.util.List;


public class DatabaseService extends IntentService {

    private final static String CATEGORY_LIST_TO_CATEGORY_ACT = "CATEGORY_LIST_TO_CATEGORY_ACT";
    private final static String CATEGORY_LIST_TO_RECORD_ACT = "CATEGORY_LIST_TO_RECORD_ACT";
    private final static String CATEGORY_BY_ID = "CATEGORY_BY_ID";
    private final static String RECORD_LIST = "RECORD_LIST";
    private final static String RECORD_BY_ID = "RECORD_BY_ID";

    private final static String KEY_ACTION = "KEY_ACTION";
    private final static String FROM = "FROM";
    private final static String ACTION_ACCESS_TO_CATEGORY_TABLE = "ACCESS_TO_CATEGORY_TABLE";
    private final static String ACTION_ACCESS_TO_RECORD_TABLE = "ACCESS_TO_RECORD_TABLE";
    private final static String ACTION_ACCESS_TO_PHOTO_TABLE = "ACCESS_TO_PHOTO_TABLE";
    private final static String SAVE_OK = "SAVE_OK";

    private final static String CATEGORY_LIST_TO_STATISTIC_ACT = "CATEGORY_LIST_TO_STATISTIC_ACT";
    private final static String CATEGORY_LIST_TO_PIE_ACT = "CATEGORY_LIST_TO_PIE_ACT";
    private final static String ACTION_ACCESS_TO_STATISTICS = "ACCESS_TO_STATISTICS";

    private DatabaseHelper dbHelper;

    public DatabaseService() {
        super("DatabaseService");
    }

    public DatabaseService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DatabaseHelper(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(ACTION_ACCESS_TO_CATEGORY_TABLE)) {
                switch (intent.getStringExtra(KEY_ACTION)) {
                    case "insert":
                        Category category = (Category) intent.getSerializableExtra("category");
                        if (dbHelper.isUniqueCategory(category.getTitle())) {
                            dbHelper.insertCategory(category);
                            Intent intentSaveOk = new Intent(SAVE_OK);
                            sendBroadcast(intentSaveOk);
                        } else {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), getString(R.string.error_insert_same_category), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        break;
                    case "update":
                        Category categoryForUpdate = (Category) intent.getSerializableExtra("category");
                        if (dbHelper.isUniqueCategory(categoryForUpdate.getTitle())) {
                            dbHelper.updateCategory(categoryForUpdate);
                            Intent intentSaveOk = new Intent(SAVE_OK);
                            sendBroadcast(intentSaveOk);
                        } else {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getBaseContext(), getString(R.string.error_update_same_category), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        break;
                    case "delete":
                        try {
                            long categoryIdDelete = intent.getLongExtra("categoryId", -1);
                            dbHelper.deleteCategory(categoryIdDelete);

                            List<Category> categoryListOfDelete = dbHelper.getAllCategory();
                            Intent intentToMainDelete = new Intent(CATEGORY_LIST_TO_CATEGORY_ACT);
                            intentToMainDelete.putExtra("categoryList", (Serializable) categoryListOfDelete);
                            sendBroadcast(intentToMainDelete);
                        } catch (android.database.sqlite.SQLiteConstraintException e) {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getBaseContext(), getString(R.string.error_delete_category), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        break;
                    case "getAll":
                        List<Category> categoryList = dbHelper.getAllCategory();
                        Intent intentToMain;
                        if (intent.getStringExtra(FROM).equals("ListCategoryActivity"))
                            intentToMain = new Intent(CATEGORY_LIST_TO_CATEGORY_ACT);
                        else
                            intentToMain = new Intent(CATEGORY_LIST_TO_RECORD_ACT);
                        intentToMain.putExtra("categoryList", (Serializable) categoryList);
                        sendBroadcast(intentToMain);
                        break;
                    case "getById":
                        long categoryId = intent.getLongExtra("categoryId", -1);
                        Category categoryById = dbHelper.getCategory(categoryId);
                        Intent intentToInsertAct = new Intent(CATEGORY_BY_ID);
                        intentToInsertAct.putExtra("categoryById", (Serializable) categoryById);
                        sendBroadcast(intentToInsertAct);
                        break;
                }
            }
            if (intent.getAction().equals(ACTION_ACCESS_TO_RECORD_TABLE)) {
                switch (intent.getStringExtra(KEY_ACTION)) {
                    case "insert":
                        try {
                            Record record = (Record) intent.getSerializableExtra("record");
                            long id = dbHelper.insertRecord(record);
                            List<Photo> photoList = (List<Photo>) intent.getSerializableExtra("photoList");
                            for (Photo photo : photoList) {
                                photo.setIdRecord(id);
                                dbHelper.insertPhoto(photo);
                            }
                        } catch (android.database.sqlite.SQLiteConstraintException e) {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), getString(R.string.error_insert_same_category), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        break;
                    case "update":
                        try {
                            Record recordForUpdate = (Record) intent.getSerializableExtra("record");
                            List<Photo> photoListOld = dbHelper.getAllPhotoByRecordId(recordForUpdate.getId());
                            List<Photo> photoListNew = (List<Photo>) intent.getSerializableExtra("photoList");
                            for (Photo photo : photoListOld) {
                                if (!photoListNew.contains(photo))
                                    dbHelper.deletePhoto(photo.getId());
                            }
                            for (Photo photo : photoListNew) {
                                if (!photoListOld.contains(photo)) {
                                    photo.setIdRecord(recordForUpdate.getId());
                                    dbHelper.insertPhoto(photo);
                                }
                            }
                            dbHelper.updateRecord(recordForUpdate);
                        } catch (android.database.sqlite.SQLiteConstraintException e) {

                        }
                        break;
                    case "delete":
                        long recordIdToDeleted = intent.getLongExtra("recordId", -1);
                        dbHelper.deletePhotoByRecordId(recordIdToDeleted);
                        dbHelper.deleteRecord(recordIdToDeleted);

                        List<Record> recordListDeleted = dbHelper.getAllRecord(intent.getLongExtra("startDate", -1), intent.getLongExtra("endDate", -1));
                        Intent intentToMainDelete = new Intent(RECORD_LIST);
                        intentToMainDelete.putExtra("recordList", (Serializable) recordListDeleted);
                        sendBroadcast(intentToMainDelete);
                        break;
                    case "getAll":
                        List<Record> recordList = dbHelper.getAllRecord(intent.getLongExtra("startDate", -1), intent.getLongExtra("endDate", -1));
                        Intent intentToMain = new Intent(RECORD_LIST);
                        intentToMain.putExtra("recordList", (Serializable) recordList);
                        sendBroadcast(intentToMain);
                        break;
                    case "getById":
                        long recordId = intent.getLongExtra("recordId", -1);
                        Record recordById = dbHelper.getRecord(recordId);
                        Category categoryById = dbHelper.getCategory(recordById.getIdCategory());
                        List<Photo> photoList = dbHelper.getAllPhotoByRecordId(recordId);
                        Intent intentToInsertAct = new Intent(RECORD_BY_ID);
                        intentToInsertAct.putExtra("recordById", (Serializable) recordById);
                        intentToInsertAct.putExtra("categoryById", (Serializable) categoryById);
                        intentToInsertAct.putExtra("photoList", (Serializable) photoList);
                        sendBroadcast(intentToInsertAct);
                        break;
                }
            }
            if (intent.getAction().equals(ACTION_ACCESS_TO_PHOTO_TABLE)) {
                switch (intent.getStringExtra(KEY_ACTION)) {
                    case "insert":
                        break;
                    case "update":
                        break;
                    case "delete":
                        break;
                    case "getAll":
                        break;
                    case "getById":
                        break;
                }
            }
            if (intent.getAction().equals(ACTION_ACCESS_TO_STATISTICS)) {
                switch (intent.getStringExtra(KEY_ACTION)) {
                    case "firstStatistic":
                        List<StatisticCategory> statisticCategoryList = dbHelper.firstStatistic(intent.getLongExtra("startDate", -1), intent.getLongExtra("endDate", -1));
                        Intent intentFirstStat = new Intent(CATEGORY_LIST_TO_STATISTIC_ACT);
                        intentFirstStat.putExtra("statisticCategoryList", (Serializable) statisticCategoryList);
                        sendBroadcast(intentFirstStat);
                        break;

                    case "secondStatistic":
                        List<StatisticCategory> statisticCategoryList2 = dbHelper.secondStatistic(intent.getLongExtra("startDate", -1), intent.getLongExtra("endDate", -1));
                        Intent intentSecondStat = new Intent(CATEGORY_LIST_TO_STATISTIC_ACT);
                        intentSecondStat.putExtra("statisticCategoryList", (Serializable) statisticCategoryList2);
                        sendBroadcast(intentSecondStat);
                        break;

                    case "thirdStatistic":
                        List<StatisticCategory> statisticCategoryList3 = dbHelper.thirdStatistic(intent.getLongExtra("startDate", -1), intent.getLongExtra("endDate", -1), (List<Category>) intent.getSerializableExtra("chooseCategoryList"));
                        Intent intentThirdStat = new Intent(CATEGORY_LIST_TO_STATISTIC_ACT);
                        intentThirdStat.putExtra("statisticCategoryList", (Serializable) statisticCategoryList3);
                        sendBroadcast(intentThirdStat);
                        break;

                    case "fourthStatistic":
                        List<StatisticCategory> statisticCategoryList4 = dbHelper.fourthStatistic(intent.getLongExtra("startDate", -1), intent.getLongExtra("endDate", -1));
                        Intent intentFourthStat = new Intent(CATEGORY_LIST_TO_PIE_ACT);
                        intentFourthStat.putExtra("statisticCategoryList", (Serializable) statisticCategoryList4);
                        sendBroadcast(intentFourthStat);
                        break;
                }
            }
            dbHelper.close();
        }
    }
}
