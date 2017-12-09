package com.example.user.apptime;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.user.apptime.adapter.CategoryAdapter;
import com.example.user.apptime.database.DatabaseService;
import com.example.user.apptime.Entity.Category;

import java.util.List;

public class ListCategoryActivity extends AppCompatActivity {

    private final static String CATEGORY_LIST_TO_CATEGORY_ACT = "CATEGORY_LIST_TO_CATEGORY_ACT";

    private DatabaseBroadcastReceiver receiver;
    private final static String KEY_ACTION = "KEY_ACTION";
    private final static String FROM = "FROM";
    private final static String ACTION_ACCESS_TO_CATEGORY_TABLE = "ACCESS_TO_CATEGORY_TABLE";

    private ListView lvCategory;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_category2);

        receiver = new DatabaseBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CATEGORY_LIST_TO_CATEGORY_ACT);
        registerReceiver(receiver, intentFilter);

        lvCategory = (ListView) findViewById(R.id.lvCategory);
        registerForContextMenu(lvCategory);

        Intent intent = new Intent(this, DatabaseService.class);
        intent.putExtra(KEY_ACTION, "getAll");
        intent.putExtra(FROM,"ListCategoryActivity");
        intent.setAction(ACTION_ACCESS_TO_CATEGORY_TABLE);
        startService(intent);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation_record);
        bottomNavigationView.getMenu().findItem(R.id.menu_categories).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_records:
                        Intent intent = new Intent(ListCategoryActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.menu_statistics:
                        Intent intent1 = new Intent(ListCategoryActivity.this, StatisticActivity.class);
                        startActivity(intent1);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, DatabaseService.class);
        intent.putExtra(KEY_ACTION, "getAll");
        intent.putExtra(FROM,"ListCategoryActivity");
        intent.setAction(ACTION_ACCESS_TO_CATEGORY_TABLE);
        startService(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu_category, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final CategoryAdapter categoryAdapter = (CategoryAdapter) lvCategory.getAdapter();

        switch (item.getItemId()) {
            case R.id.item_edit: //изменение категории
                long categoryId = categoryAdapter.getCategoryList().get(info.position).getId();
                Intent intent = new Intent(this, InsertCategoryActivity.class);
                intent.putExtra("categoryId", categoryId);
                startActivity(intent);
                break;
            case R.id.item_delete:  //удаление категории
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.dialog_message_category).setTitle(R.string.dialog_title_category);
                builder.setPositiveButton(R.string.dialog_btn_delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        long categoryId = categoryAdapter.getCategoryList().get(info.position).getId();
                        Intent intent = new Intent(getApplicationContext(), DatabaseService.class);
                        intent.putExtra("categoryId",categoryId);
                        intent.putExtra(KEY_ACTION, "delete");
                        intent.setAction(ACTION_ACCESS_TO_CATEGORY_TABLE);
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

    public class DatabaseBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CATEGORY_LIST_TO_CATEGORY_ACT)) {
                List<Category> categoryList = (List<Category>) intent.getSerializableExtra("categoryList");
                CategoryAdapter categoryAdapter = new CategoryAdapter(getApplicationContext(), categoryList);
                lvCategory.setAdapter(categoryAdapter);
            }
            else {
                Toast.makeText(ListCategoryActivity.this,"не пришло",Toast.LENGTH_SHORT).show();}

        }
    }
    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    public void onClickInsertCategory(View view) {
        Intent intent = new Intent(this, InsertCategoryActivity.class);
        startActivity(intent);
    }
}
