package com.example.user.apptime;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.apptime.Entity.Record;
import com.example.user.apptime.adapter.RecordAdapter;
import com.example.user.apptime.database.DatabaseService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private ListView mRecord;
    private BottomNavigationView bottomNavigationView;
    private Calendar calendar;
    private SimpleDateFormat sdfDate;
    private TextView tvStartDate;

    private final static String KEY_ACTION = "KEY_ACTION";
    private final static String ACTION_ACCESS_TO_RECORD_TABLE = "ACCESS_TO_RECORD_TABLE";
    private final static String RECORD_LIST = "RECORD_LIST";
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle(R.string.title_records);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        tvStartDate = (TextView) findViewById(R.id.tvStartDate);
        sdfDate = new SimpleDateFormat("dd.MM.yyyy");
        onBtnImgCalendarClick(null);

        receiver = new DatabaseBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECORD_LIST);
        registerReceiver(receiver, intentFilter);


        mRecord = (ListView) findViewById(R.id.mRecord);
        registerForContextMenu(mRecord);

        intentToService();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.getMenu().findItem(R.id.menu_records).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_categories:
                        Intent intent = new Intent(MainActivity.this, ListCategoryActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.menu_statistics:
                        Intent intent1 = new Intent(MainActivity.this, StatisticActivity.class);
                        startActivity(intent1);
                        break;
                }
                return true;
            }

        });
    }


    public void onClickInsertCategory(View view) {
        Intent intent = new Intent(this, InsertRecordActivity.class);
        startActivity(intent);
    }

    public void onBtnImgCalendarClick(View view) {
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() - 1000);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        tvStartDate.setText(sdfDate.format(calendar.getTime()));
    }

    public void onClickStartDate(View view) {
        new DatePickerDialog(this, dateDialogStart, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener dateDialogStart = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int newYear, int monthOfYear,
                              int dayOfMonth) {
            calendar.set(Calendar.YEAR, newYear);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            tvStartDate.setText(sdfDate.format(calendar.getTime()));

            intentToService();
        }
    };

    public void intentToService() {
        Intent intent = new Intent(MainActivity.this, DatabaseService.class);
        intent.putExtra(KEY_ACTION, "getAll");
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        intent.putExtra("startDate", calendar.getTimeInMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        intent.putExtra("endDate", calendar.getTimeInMillis());
        intent.setAction(ACTION_ACCESS_TO_RECORD_TABLE);
        startService(intent);
    }


    public class DatabaseBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RECORD_LIST)) {
                List<Record> recordList = (List<Record>) intent.getSerializableExtra("recordList");
                RecordAdapter recordAdapter = new RecordAdapter(getApplicationContext(), recordList);
                mRecord.setAdapter(recordAdapter);
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu_category, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final RecordAdapter recordAdapter = (RecordAdapter) mRecord.getAdapter();

        switch (item.getItemId()) {
            case R.id.item_edit: //изменение записи
                long recordId = recordAdapter.getRecordList().get(info.position).getId();
                Intent intent = new Intent(this, InsertRecordActivity.class);
                intent.putExtra("recordId", recordId);
                startActivity(intent);
                break;
            case R.id.item_delete:  //удаление категории
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.dialog_message_category).setTitle(R.string.dialog_title_category);
                builder.setPositiveButton(R.string.dialog_btn_delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        long recordId = recordAdapter.getRecordList().get(info.position).getId();
                        Intent intent = new Intent(getApplicationContext(), DatabaseService.class);
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        intent.putExtra("startDate", calendar.getTimeInMillis());
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);
                        intent.putExtra("endDate", calendar.getTimeInMillis());
                        intent.putExtra("recordId", recordId);
                        intent.putExtra(KEY_ACTION, "delete");
                        intent.setAction(ACTION_ACCESS_TO_RECORD_TABLE);
                        startService(intent);

                    }
                });
                builder.setNegativeButton(R.string.dialog_btn_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
        return super.onContextItemSelected(item);
    }

}
