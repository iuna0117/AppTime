package com.example.user.apptime;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.apptime.Entity.StatisticCategory;
import com.example.user.apptime.adapter.StatisticsAdapter;

import java.util.List;

public class StatisticResultActivity extends AppCompatActivity {

    private ListView lvCategory;
    private TextView tvExistRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_result);

        getSupportActionBar().setTitle(R.string.title_statistics);

        lvCategory = (ListView) findViewById(R.id.lvCategory);
        tvExistRecord = (TextView)findViewById(R.id.tvExistRecord);

        List<StatisticCategory> statisticCategoryList = (List<StatisticCategory>) getIntent().getSerializableExtra("statisticCategoryList");
        StatisticsAdapter statisticsAdapter = new StatisticsAdapter(getApplicationContext(), statisticCategoryList);
        lvCategory.setAdapter(statisticsAdapter);

        if(statisticCategoryList.size()==0) {
            tvExistRecord.setVisibility(View.VISIBLE);
            tvExistRecord.setText(R.string.not_exist_record);
        }else  tvExistRecord.setVisibility(View.GONE);
    }
}
