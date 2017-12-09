package com.example.user.apptime;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.user.apptime.adapter.PhotoAdapter;
import com.example.user.apptime.database.DatabaseService;
import com.example.user.apptime.Entity.Category;
import com.example.user.apptime.Entity.Photo;
import com.example.user.apptime.Entity.Record;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class InsertRecordActivity extends AppCompatActivity {

    private static final String PICKER_TAG = "PICKER_TAG";

    private final static String CATEGORY_LIST_TO_RECORD_ACT = "CATEGORY_LIST_TO_RECORD_ACT";
    private final static String KEY_ACTION = "KEY_ACTION";
    private final static String FROM = "FROM";
    private final static String ACTION_ACCESS_TO_CATEGORY_TABLE = "ACCESS_TO_CATEGORY_TABLE";
    private final static String ACTION_ACCESS_TO_RECORD_TABLE = "ACCESS_TO_RECORD_TABLE";
    private final static String RECORD_BY_ID = "RECORD_BY_ID";

    private DatabaseBroadcastReceiver receiver;
    private SimpleDateFormat sdfTime;
    private Calendar calendarStart;
    private Calendar calendarEnd;


    private TextView tvTimeStart;
    private TextView tvTimeEnd;
    private EditText etDurationHour;
    private EditText etDurationMinutes;
    private EditText etDescription;
    private ImageView ivCategoryIcon;
    private TextView tvCategory;

    private static List<Category> categoryList;
    private static ArrayList<HashMap<String, ?>> imagesWithNames;

    static final int GALLERY_REQUEST = 1;

    private ListView lvPhotos;
    private List<Photo> photoList;
    private PhotoAdapter photoAdapter;
    private ImageView ivAddPhoto;

    private Category category;

    private long recordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_record);

        getSupportActionBar().setTitle(R.string.insert_record_title);

        receiver = new DatabaseBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CATEGORY_LIST_TO_RECORD_ACT);
        intentFilter.addAction(RECORD_BY_ID);
        registerReceiver(receiver, intentFilter);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getIntent().getLongExtra("choosenDate",System.currentTimeMillis()));

        tvTimeStart = (TextView) findViewById(R.id.tvTimeStart);
        tvTimeEnd = (TextView) findViewById(R.id.tvTimeEnd);
        etDurationHour = (EditText) findViewById(R.id.etDurationHour);
        etDurationMinutes = (EditText) findViewById(R.id.etDurationMinutes);
        etDescription = (EditText) findViewById(R.id.etDescription);
        ivCategoryIcon = (ImageView) findViewById(R.id.ivCategoryIcon);
        tvCategory = (TextView) findViewById(R.id.tvCategory);

        calendarStart = Calendar.getInstance();
        calendarStart.setTimeInMillis(System.currentTimeMillis() - 1000);
        calendarStart.set(Calendar.YEAR,calendar.get(Calendar.YEAR));
        calendarStart.set(Calendar.MONTH,calendar.get(Calendar.MONTH));
        calendarStart.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH));
        calendarEnd = Calendar.getInstance();
        calendarEnd.setTimeInMillis(System.currentTimeMillis() - 1000);
        calendarEnd.set(Calendar.YEAR,calendar.get(Calendar.YEAR));
        calendarEnd.set(Calendar.MONTH,calendar.get(Calendar.MONTH));
        calendarEnd.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH));
        sdfTime = new SimpleDateFormat("HH:mm");
        String currentTime = sdfTime.format(calendarStart.getTime());
        tvTimeStart.setText(currentTime);

        Intent intent = new Intent(this, DatabaseService.class);
        intent.putExtra(KEY_ACTION, "getAll");
        intent.putExtra(FROM, "InsertRecordActivity");
        intent.setAction(ACTION_ACCESS_TO_CATEGORY_TABLE);
        startService(intent);

        photoList = new ArrayList<Photo>();
        lvPhotos = (ListView) findViewById(R.id.lvPhotos);
        photoAdapter = new PhotoAdapter(getApplicationContext(), photoList);
        // Привяжем массив через адаптер к ListView
        lvPhotos.setAdapter(photoAdapter);
        ivAddPhoto = (ImageView) findViewById(R.id.ivAddPhoto);

        ivAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAddPhoto();
            }
        });

        recordId = getIntent().getLongExtra("recordId", -1);
        if (recordId != -1) { //обновление
            getSupportActionBar().setTitle(R.string.update_meet_title);
            Intent intentIn = new Intent(InsertRecordActivity.this, DatabaseService.class);
            intentIn.putExtra("recordId", recordId);
            intentIn.putExtra(KEY_ACTION, "getById");
            intentIn.setAction(ACTION_ACCESS_TO_RECORD_TABLE);
            startService(intentIn);

        } else
            getSupportActionBar().setTitle(R.string.insert_meet_title);
    }

    public void onClickChooseCategoryForRecord(View view) {
        show();
    }

    public void onClickStartTime(View view) {
        new TimePickerDialog(this, timeDialogStart, calendarStart.get(Calendar.HOUR_OF_DAY), calendarStart.get(Calendar.MINUTE), true).show();

    }

    public void onClickEndTime(View view) {
        new TimePickerDialog(this, timeDialogEnd, calendarEnd.get(Calendar.HOUR_OF_DAY), calendarEnd.get(Calendar.MINUTE), true).show();
    }

    TimePickerDialog.OnTimeSetListener timeDialogStart = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
            calendarStart.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendarStart.set(Calendar.MINUTE, minuteOfHour);
            tvTimeStart.setText(sdfTime.format(calendarStart.getTime()));

            long durationLong = (calendarEnd.getTimeInMillis()) - (calendarStart.getTimeInMillis()) - 10800000;
            SimpleDateFormat sdf = new SimpleDateFormat("HH");
            Date duration = new Date(durationLong);
            String strDurationHH = sdf.format(duration);
            sdf = new SimpleDateFormat("mm");
            String strDurationmm = sdf.format(duration);

            etDurationHour.setText(strDurationHH);
            etDurationMinutes.setText(strDurationmm);
        }
    };
    TimePickerDialog.OnTimeSetListener timeDialogEnd = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
            calendarEnd.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendarEnd.set(Calendar.MINUTE, minuteOfHour);
            tvTimeEnd.setText(sdfTime.format(calendarEnd.getTime()));

            long durationLong = (calendarEnd.getTimeInMillis()) - (calendarStart.getTimeInMillis()) - 10800000;
            SimpleDateFormat sdf = new SimpleDateFormat("HH");
            Date duration = new Date(durationLong);
            String strDurationHH = sdf.format(duration);
            sdf = new SimpleDateFormat("mm");
            String strDurationmm = sdf.format(duration);

            etDurationHour.setText(strDurationHH);
            etDurationMinutes.setText(strDurationmm);
        }
    };

    public void onBtnClickFiveMin(View view) {
        etDurationHour.setText("00");
        etDurationMinutes.setText("05");
        calendarEnd.setTime(calendarStart.getTime());
        calendarEnd.add(Calendar.MINUTE, 5);
        tvTimeEnd.setText(sdfTime.format(calendarEnd.getTime()));
    }

    public void onBtnClickFifteenMin(View view) {
        etDurationHour.setText("00");
        etDurationMinutes.setText("15");
        calendarEnd.setTime(calendarStart.getTime());
        calendarEnd.add(Calendar.MINUTE, 15);
        tvTimeEnd.setText(sdfTime.format(calendarEnd.getTime()));
    }

    public void onBtnClickOneHour(View view) {
        etDurationHour.setText("01");
        etDurationMinutes.setText("00");
        calendarEnd.setTime(calendarStart.getTime());
        calendarEnd.add(Calendar.HOUR, 1);
        tvTimeEnd.setText(sdfTime.format(calendarEnd.getTime()));
    }

    public void onBtnClickTwoHour(View view) {
        etDurationHour.setText("02");
        etDurationMinutes.setText("00");

        calendarEnd.setTime(calendarStart.getTime());
        calendarEnd.add(Calendar.HOUR, 2);
        tvTimeEnd.setText(sdfTime.format(calendarEnd.getTime()));
    }

    private void show() {
        new WithNamesFragment().show(getSupportFragmentManager(), PICKER_TAG);
    }

    public static class WithNamesFragment extends DialogFragment {
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final SimpleAdapter adapter = new SimpleAdapter(getContext(), imagesWithNames, R.layout.simple_row_with_image,
                    new String[]{"name", "imageID"}, new int[]{R.id.text1, R.id.image1});
            return new AlertDialog.Builder(getContext()).setAdapter(adapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ((InsertRecordActivity) getContext()).OnSelected(WithNamesFragment.this.getClass(), (int) ((HashMap<String, Object>) adapter.getItem(i)).get("imageID"), (String) ((HashMap<String, Object>) adapter.getItem(i)).get("name"), (long) ((HashMap<String, Object>) adapter.getItem(i)).get("id"));
                        }
                    }).setCancelable(true).setTitle(R.string.choose_category).create();
        }
    }

    public void OnSelected(Class<?> clazz, int i, String name, long id) {
        ivCategoryIcon.setImageResource(i);
        tvCategory.setText(name);
        category = new Category(id, name, getResources().getResourceEntryName(i));
    }


    public class DatabaseBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CATEGORY_LIST_TO_RECORD_ACT)) {
                categoryList = (List<Category>) intent.getSerializableExtra("categoryList");
                imagesWithNames = new ArrayList<>();
                HashMap<String, Object> row;
                for (Category category : categoryList) {
                    row = new HashMap<>();
                    row.put("imageID", getResources().getIdentifier(category.getIcon(), "drawable", getPackageName()));
                    row.put("name", category.getTitle());
                    row.put("id", category.getId());
                    imagesWithNames.add(row);
                }
            }
            if (intent.getAction().equals(RECORD_BY_ID)) {
                Record record = (Record) intent.getSerializableExtra("recordById");
                photoList.clear();
                photoList.addAll((List<Photo>) intent.getSerializableExtra("photoList"));
                category = (Category) intent.getSerializableExtra("categoryById");

                calendarEnd.setTimeInMillis(record.getTimeEnd());
                calendarStart.setTimeInMillis(record.getTimeStart());

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                Date startDate = new Date(record.getTimeStart());
                String strStartDate = sdf.format(startDate);
                Date endDate = new Date(record.getTimeEnd());
                String strEndDate = sdf.format(endDate);

                sdf = new SimpleDateFormat("HH");
                Date duration = new Date(record.getDuration());
                String strDurationHH = sdf.format(duration);

                sdf = new SimpleDateFormat("mm");
                String strDurationmm = sdf.format(duration);

                tvTimeStart.setText(strStartDate);
                tvTimeEnd.setText(strEndDate);
                etDurationHour.setText(strDurationHH);
                etDurationMinutes.setText(strDurationmm);

                ivCategoryIcon.setImageResource(getResources().getIdentifier(category.getIcon(), "drawable", context.getPackageName()));
                ivCategoryIcon.setTag(category.getIcon());
                tvCategory.setText(category.getTitle());

                etDescription.setText(record.getDescription());

                photoAdapter.notifyDataSetChanged();

                registerForContextMenu(lvPhotos);
            }
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                        final Photo photo = new Photo(getRealPathFromURI(this, imageUri), getString(R.string.not_description));

                        LayoutInflater li = LayoutInflater.from(this);
                        View promptsView = li.inflate(R.layout.dialog_add_photo_description, null);
                        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
                        mDialogBuilder.setView(promptsView);
                        final EditText positionInput = (EditText) promptsView.findViewById(R.id.input_text);
                        mDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton(R.string.dialog_btn_ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                if (positionInput.getText().toString().trim().equals(""))
                                                    photo.setDescription(getString(R.string.not_description));
                                                else
                                                    photo.setDescription(positionInput.getText().toString());
                                            }
                                        })
                                .setNegativeButton(R.string.dialog_btn_cancel,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });


                        AlertDialog alertDialog = mDialogBuilder.create();
                        alertDialog.show();


                        photoList.add(photo);
                        photoAdapter.notifyDataSetChanged();


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

        }
    }

    public void onClickAddPhoto() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(InsertRecordActivity.this);

        String[] typesMelody = getResources().getStringArray(R.array.make_photo);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(InsertRecordActivity.this, android.R.layout.select_dialog_item, typesMelody);

        builderSingle.setNegativeButton(getString(R.string.dialog_btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (arrayAdapter.getItem(which)) {
                    case "Сделать снимок":
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, GALLERY_REQUEST);
                        break;

                    case "Загрузить из галереи":
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                        break;

                }
            }
        });
        builderSingle.show();
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void onClickSave(View view) {
        if (calendarStart.getTimeInMillis() > calendarEnd.getTimeInMillis()) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setMessage(R.string.start_more_end).setTitle(R.string.dialog_title_save);
            builder.setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    calendarEnd.add(Calendar.DAY_OF_MONTH, 1);
                    saveRecord();
                }
            });
            builder.setNegativeButton(R.string.dialog_btn_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            android.app.AlertDialog dialog = builder.create();
            dialog.show();
            Toast.makeText(getBaseContext(), getString(R.string.error_time), Toast.LENGTH_SHORT).show();
        } else saveRecord();

    }

    public void saveRecord() {
        if (etDurationMinutes.getText().toString().trim().equals(""))
            Toast.makeText(getBaseContext(), getString(R.string.error_empty_duration), Toast.LENGTH_SHORT).show();
        else if (category == null)
            Toast.makeText(getBaseContext(), getString(R.string.error_empty_category), Toast.LENGTH_SHORT).show();
        else {
            Intent intent = new Intent(InsertRecordActivity.this, DatabaseService.class);
            for(Photo photo: photoList ){
                File imgFile = new File(photo.getFileName());

                if(imgFile.exists()) {
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 5;

                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
                    String fileName = imgFile.getName();//photo.getFileName().substring(photo.getFileName().lastIndexOf('/')+1);
                    photo.setFileName(saveToInternalStorage(myBitmap,fileName));
                }
            }
            Record record = new Record();
            long durationLong = (calendarEnd.getTimeInMillis()) - (calendarStart.getTimeInMillis()) - 10800000;
            record.setTimeEnd(calendarEnd.getTimeInMillis());
            record.setTimeStart(calendarStart.getTimeInMillis());
            record.setDuration(durationLong);
            if (etDescription.getText().toString().trim().equals(""))
                record.setDescription(getString(R.string.not_description));
            else record.setDescription(etDescription.getText().toString());
            record.setIdCategory(category.getId());
            if (recordId != -1)//обновление записи
            {
                intent.putExtra(KEY_ACTION, "update");
                record.setId(recordId);
            } else
                intent.putExtra(KEY_ACTION, "insert");
            intent.putExtra("record", record);
            intent.setAction(ACTION_ACCESS_TO_RECORD_TABLE);
            intent.putExtra("photoList", (Serializable) photoList);
            startService(intent);
            super.onBackPressed();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu_photo, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final PhotoAdapter photoAdapter = (PhotoAdapter) lvPhotos.getAdapter();

        switch (item.getItemId()) {
            case R.id.item_delete:  //удаление фото
                photoList.remove(info.position);
                photoAdapter.notifyDataSetChanged();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private String saveToInternalStorage(Bitmap bitmapImage,String name){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
// Create imageDir
        File mypath=new File(directory,name);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
// Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }

    public void back(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
