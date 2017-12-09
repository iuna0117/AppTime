package com.example.user.apptime;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import com.example.user.apptime.Entity.StatisticCategory;

import java.util.List;
import java.util.Random;

public class PieChartActivity extends AppCompatActivity {

    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);

        getSupportActionBar().setTitle(R.string.title_statistics);

        pieChart = (PieChart) findViewById(R.id.piechart);

        List<StatisticCategory> statisticCategoryList = (List<StatisticCategory>) getIntent().getSerializableExtra("statisticCategoryList");
        for(StatisticCategory stat: statisticCategoryList) {
            long duration = Long.parseLong(stat.getStatisticRes());
            long hour = duration/3600000;
            long minutes = (duration-hour*3600000)/60000;
            //statisticCategory.setStatisticRes(hour+" ч "+minutes+" мин");
            pieChart.addPieSlice(new PieModel(stat.getCategory().getTitle()+"\n"+hour+" ч "+minutes+" мин",duration , generateRandomColor(Color.argb(255, new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256)))));
        }
        // pieChart.setValueTextSize(0);
        pieChart.setDrawValueInPie(false);
        // pieChart.setInnerPaddingColor(android.R.color.transparent);
        pieChart.startAnimation();
    }

    public int generateRandomColor(int mix) {
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);

        red = (red + mix) / 2;
        green = (green + mix) / 2;
        blue = (blue + mix) / 2;
        return Color.argb(255, red, green, blue);
    }
}
