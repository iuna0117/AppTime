package com.example.user.apptime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.user.apptime.adapter.ChooseCategoryAdapter;
import com.example.user.apptime.database.DatabaseService;
import com.example.user.apptime.Entity.Category;

import java.io.Serializable;
import java.util.List;

public class ChooseCategoryActivity extends AppCompatActivity {

    private final static String CATEGORY_LIST_TO_CATEGORY_ACT = "CATEGORY_LIST_TO_CATEGORY_ACT";
    private final static String ACTION_ACCESS_TO_STATISTICS = "ACCESS_TO_STATISTICS";

    private DatabaseBroadcastReceiver receiver;
    private final static String KEY_ACTION = "KEY_ACTION";
    private final static String FROM = "FROM";
    private final static String ACTION_ACCESS_TO_CATEGORY_TABLE = "ACCESS_TO_CATEGORY_TABLE";

    private ListView lvCategory;
    private ChooseCategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);

        getSupportActionBar().setTitle(R.string.title_statistics);

        receiver = new DatabaseBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CATEGORY_LIST_TO_CATEGORY_ACT);
        registerReceiver(receiver, intentFilter);

        lvCategory = (ListView) findViewById(R.id.lvCategory);
        registerForContextMenu(lvCategory);

        Intent intent = new Intent(this, DatabaseService.class);
        intent.putExtra(KEY_ACTION, "getAll");
        intent.putExtra(FROM, "ListCategoryActivity");
        intent.setAction(ACTION_ACCESS_TO_CATEGORY_TABLE);
        startService(intent);
    }

    public class DatabaseBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CATEGORY_LIST_TO_CATEGORY_ACT)) {
                List<Category> categoryList = (List<Category>) intent.getSerializableExtra("categoryList");
                categoryAdapter = new ChooseCategoryAdapter(getApplicationContext(), categoryList);
                lvCategory.setAdapter(categoryAdapter);
            }

        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    public void onClickViewStatistics(View view) {
        Intent intentThird = new Intent(this, DatabaseService.class);
        intentThird.putExtra(KEY_ACTION, "thirdStatistic");
        intentThird.putExtra("startDate", getIntent().getLongExtra("startDate", -1));
        intentThird.putExtra("endDate", getIntent().getLongExtra("endDate", -1));
        intentThird.putExtra("chooseCategoryList",(Serializable)categoryAdapter.getChooseCategoryList());
        intentThird.setAction(ACTION_ACCESS_TO_STATISTICS);
        startService(intentThird);
    }
    public void back(View view) {
        Intent intent = new Intent(this, StatisticActivity.class);
        startActivity(intent);
        finish();
    }
}
