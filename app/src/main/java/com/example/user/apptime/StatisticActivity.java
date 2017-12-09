package com.example.user.apptime;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.apptime.database.DatabaseService;
import com.example.user.apptime.Entity.StatisticCategory;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StatisticActivity extends AppCompatActivity {

    private final static String KEY_ACTION = "KEY_ACTION";
    private final static String ACTION_ACCESS_TO_STATISTICS = "ACCESS_TO_STATISTICS";
    private final static String CATEGORY_LIST_TO_STATISTIC_ACT = "CATEGORY_LIST_TO_STATISTIC_ACT";
    private final static String CATEGORY_LIST_TO_PIE_ACT = "CATEGORY_LIST_TO_PIE_ACT";
    private DatabaseBroadcastReceiver receiver;


    private TextView tvCurrentDate;

    private TextView tvStartDate;
    private TextView tvEndDate;
    private RadioGroup radioGroup;


    private SimpleDateFormat sdfDate;
    private Calendar calendarStart;
    private Calendar calendarEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        getSupportActionBar().setTitle(R.string.title_statistics);

        tvStartDate=(TextView) findViewById(R.id.tvStartDate);
        tvEndDate=(TextView) findViewById(R.id.tvEndDate);
        radioGroup=(RadioGroup) findViewById(R.id.radio_group_stat);

        sdfDate =  new SimpleDateFormat("dd.MM.yyyy");
        onBtnImgCalendarClick(null);

        receiver = new DatabaseBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CATEGORY_LIST_TO_STATISTIC_ACT);
        intentFilter.addAction(CATEGORY_LIST_TO_PIE_ACT);
        registerReceiver(receiver, intentFilter);

    }

    public void onClickStartDate(View view) {
        tvCurrentDate = (TextView) findViewById(R.id.tvStartDate);
        new DatePickerDialog(this, dateDialogStart, calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void onClickEndDate(View view) {
        tvCurrentDate = (TextView) findViewById(R.id.tvEndDate);
        new DatePickerDialog(this, dateDialogEnd, calendarEnd.get(Calendar.YEAR), calendarEnd.get(Calendar.MONTH), calendarEnd.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void onBtnImgCalendarClick(View view){
        calendarStart = Calendar.getInstance();
        calendarStart.setTimeInMillis(System.currentTimeMillis() - 1000);
        calendarStart.add(Calendar.MONTH,-1);
        calendarStart.set(Calendar.HOUR_OF_DAY,0);
        calendarStart.set(Calendar.MINUTE,0);
        calendarEnd = Calendar.getInstance();
        calendarEnd.setTimeInMillis(System.currentTimeMillis() - 1000);
        calendarEnd.set(Calendar.HOUR_OF_DAY,23);
        calendarEnd.set(Calendar.MINUTE,59);

        tvEndDate.setText(sdfDate.format(calendarEnd.getTime()));
        tvStartDate.setText(sdfDate.format(calendarStart.getTime()));
    }

    DatePickerDialog.OnDateSetListener dateDialogStart = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int newYear, int monthOfYear,
                              int dayOfMonth) {
            calendarStart.set(Calendar.YEAR, newYear);
            calendarStart.set(Calendar.MONTH, monthOfYear);
            calendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calendarStart.set(Calendar.HOUR_OF_DAY,0);
            calendarStart.set(Calendar.MINUTE,0);
            tvCurrentDate.setText(sdfDate.format(calendarStart.getTime()));
        }
    };
    DatePickerDialog.OnDateSetListener dateDialogEnd = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int newYear, int monthOfYear,
                              int dayOfMonth) {
            calendarEnd.set(Calendar.YEAR, newYear);
            calendarEnd.set(Calendar.MONTH, monthOfYear);
            calendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calendarEnd.set(Calendar.HOUR_OF_DAY,23);
            calendarEnd.set(Calendar.MINUTE,59);
            tvCurrentDate.setText(sdfDate.format(calendarEnd.getTime()));
        }
    };

    public void onClickViewStatistics(View view){
        try {
            Date dateStart = sdfDate.parse(tvStartDate.getText().toString());
            Date dateEnd = sdfDate.parse(tvEndDate.getText().toString());
            if(dateStart.getTime()>dateEnd.getTime())
                Toast.makeText(getBaseContext(), getString(R.string.error_date), Toast.LENGTH_SHORT).show();
            else {
                switch (getResources().getResourceEntryName(radioGroup.getCheckedRadioButtonId())){
                    case "rBtnFirstStat":
                        Intent intentFirst = new Intent(this, DatabaseService.class);
                        intentFirst.putExtra(KEY_ACTION, "firstStatistic");
                        intentFirst.putExtra("startDate", calendarStart.getTimeInMillis());
                        intentFirst.putExtra("endDate", calendarEnd.getTimeInMillis());
                        intentFirst.setAction(ACTION_ACCESS_TO_STATISTICS);
                        startService(intentFirst);

                        break;
                    case "rBtnSecondStat":
                        Intent intentSecond = new Intent(this, DatabaseService.class);
                        intentSecond.putExtra(KEY_ACTION, "secondStatistic");
                        intentSecond.putExtra("startDate", calendarStart.getTimeInMillis());
                        intentSecond.putExtra("endDate", calendarEnd.getTimeInMillis());
                        intentSecond.setAction(ACTION_ACCESS_TO_STATISTICS);
                        startService(intentSecond);

                        break;
                    case "rBtnThirdStat":
                        Intent intentThird= new Intent(this, ChooseCategoryActivity.class);
                        intentThird.putExtra("startDate", calendarStart.getTimeInMillis());
                        intentThird.putExtra("endDate", calendarEnd.getTimeInMillis());
                        startActivity(intentThird);
                        break;
                    case "rBtnFourthStat":
                        Intent intentFourth = new Intent(this, DatabaseService.class);
                        intentFourth.putExtra(KEY_ACTION, "fourthStatistic");
                        intentFourth.putExtra("startDate", calendarStart.getTimeInMillis());
                        intentFourth.putExtra("endDate", calendarEnd.getTimeInMillis());
                        intentFourth.setAction(ACTION_ACCESS_TO_STATISTICS);
                        startService(intentFourth);
                        break;

                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public class DatabaseBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CATEGORY_LIST_TO_STATISTIC_ACT)) {
                List<StatisticCategory> statisticCategoryList = (List<StatisticCategory>) intent.getSerializableExtra("statisticCategoryList");
                Intent intentResFirst=new Intent(StatisticActivity.this,StatisticResultActivity.class);
                intentResFirst.putExtra("statisticCategoryList", (Serializable) statisticCategoryList);
                startActivity(intentResFirst);
            }
            if (intent.getAction().equals(CATEGORY_LIST_TO_PIE_ACT)) {
                List<StatisticCategory> statisticCategoryList = (List<StatisticCategory>) intent.getSerializableExtra("statisticCategoryList");
                Intent intentResFirst=new Intent(StatisticActivity.this,PieChartActivity.class);
                intentResFirst.putExtra("statisticCategoryList", (Serializable) statisticCategoryList);
                startActivity(intentResFirst);
            }
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
    public void back(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
