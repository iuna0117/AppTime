package com.example.user.apptime;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.user.apptime.database.DatabaseService;
import com.example.user.apptime.Entity.Category;

public class InsertCategoryActivity extends AppCompatActivity {

    private static final String PICKER_TAG = "PICKER_TAG";
    private final static String KEY_ACTION = "KEY_ACTION";
    private final static String ACTION_ACCESS_TO_CATEGORY_TABLE = "ACCESS_TO_CATEGORY_TABLE";

    private final static String CATEGORY_BY_ID = "CATEGORY_BY_ID";
    private final static String SAVE_OK = "SAVE_OK";

    private EditText etTitle;
    private ImageView ivIcon;
    private long categoryId;
    private DatabaseBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_category);

        receiver = new DatabaseBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CATEGORY_BY_ID);
        intentFilter.addAction(SAVE_OK);
        registerReceiver(receiver, intentFilter);

        etTitle = (EditText) findViewById(R.id.etCategoryTitle);
        ivIcon = (ImageView) findViewById(R.id.ivCategoryIcon);

        categoryId = getIntent().getLongExtra("categoryId", -1);
        if (categoryId != -1) { //обновление категории
            getSupportActionBar().setTitle(R.string.update_meet_title);
            Intent intent = new Intent(InsertCategoryActivity.this, DatabaseService.class);
            intent.putExtra("categoryId", categoryId);
            intent.putExtra(KEY_ACTION, "getById");
            intent.setAction(ACTION_ACCESS_TO_CATEGORY_TABLE);
            startService(intent);

        } else
            getSupportActionBar().setTitle(R.string.insert_meet_title);


    }


    public void onClickSave(View view) {
        if(etTitle.getText().toString().trim().equals("") || ivIcon.getTag()==null) {
            Toast.makeText(this,R.string.error_empty_field,Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(InsertCategoryActivity.this, DatabaseService.class);
            Category category = new Category(etTitle.getText().toString(), ivIcon.getTag().toString());

            if (categoryId != -1)//обновление категории
            {
                intent.putExtra(KEY_ACTION, "update");
                category.setId(categoryId);
            } else
                intent.putExtra(KEY_ACTION, "insert");
            intent.putExtra("category", category);
            intent.setAction(ACTION_ACCESS_TO_CATEGORY_TABLE);
            startService(intent);
            Toast.makeText(this,R.string.good_cat,Toast.LENGTH_SHORT).show();

        }
    }


    public class DatabaseBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CATEGORY_BY_ID)) {
                Category category = (Category) intent.getSerializableExtra("categoryById");
                etTitle.setText(category.getTitle());
                ivIcon.setImageResource(context.getResources().getIdentifier(category.getIcon(), "drawable", context.getPackageName()));
                ivIcon.setTag(category.getIcon());
            }
            if(intent.getAction().equals(SAVE_OK))
                InsertCategoryActivity.this.onBackPressed();
        }
    }

    public static class SimpleFragment extends DialogFragment {

        final static int[] imagesOnly = new int[]{
                R.drawable.coffee, R.drawable.cook,
                R.drawable.film, R.drawable.fly,
                R.drawable.sleep, R.drawable.sport,
                R.drawable.swimming, R.drawable.wish,
                R.drawable.work, R.drawable.book};

        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final BaseAdapter adapter = new BaseAdapter() {
                @Override
                public int getCount() {
                    return imagesOnly.length;
                }

                @Override
                public Object getItem(int i) {
                    return imagesOnly[i];
                }

                @Override
                public long getItemId(int i) {
                    return imagesOnly[i];
                }

                @Override
                public View getView(int i, View view, ViewGroup viewGroup) {
                    if (view == null)
                        view = new ImageView(getContext());
                    ((ImageView) view).setImageResource((int) getItem(i));
                    return view;
                }
            };

            return new AlertDialog.Builder(getContext()).setAdapter(adapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ((InsertCategoryActivity) getContext()).OnSelected(SimpleFragment.this.getClass(), (int) adapter.getItem(i));
                        }
                    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            }).setCancelable(true).setTitle(R.string.choose_image).create();
        }
    }

    void showSimple() {
        new SimpleFragment().show(getSupportFragmentManager(), PICKER_TAG);
    }

    public void OnSelected(Class<?> clazz, int i) {
        ivIcon.setImageResource(i);
        ivIcon.setTag(getResources().getResourceEntryName(i));

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    public void onClickChoose(View view) {
        showSimple();
    }

    public void back(View view) {
        Intent intent = new Intent(this, ListCategoryActivity.class);
        startActivity(intent);
        finish();
    }
}
