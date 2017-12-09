package com.example.user.apptime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.apptime.R;
import com.example.user.apptime.database.DatabaseHelper;
import com.example.user.apptime.Entity.Category;
import com.example.user.apptime.Entity.Record;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecordAdapter extends BaseAdapter {

    private Context context;
    private List<Record> recordList;
    private LayoutInflater inflater;

    private Calendar c;

    public RecordAdapter(Context context, List<Record> recordList) {
        this.context = context;
        this.recordList = recordList;
        inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        c = Calendar.getInstance();
    }

    @Override
    public int getCount() {
        return recordList.size();
    }

    @Override
    public Object getItem(int i) {
        return recordList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.item_record, viewGroup, false);
        final Record record = getRecord(i);
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        Category category = dbHelper.getCategory(record.getIdCategory());

        ((TextView) convertView.findViewById(R.id.tvDescription)).setText(record.getDescription());
        ((ImageView) convertView.findViewById(R.id.ivCategory)).setImageResource(context.getResources().getIdentifier(category.getIcon(), "drawable", context.getPackageName()));
        ((ImageView) convertView.findViewById(R.id.ivCategory)).setTag(category.getIcon());


        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date startDate = new Date(record.getTimeStart());
        String strStartDate = sdf.format(startDate);

        Date duration = new Date(record.getDuration());
        String strDuration = sdf.format(duration);


        ((TextView) convertView.findViewById(R.id.tvTime)).setText("Время начала:"+strStartDate+" Длительность: ("+strDuration+")");

        return convertView;

    }
    public Record getRecord(int position){
        return ((Record) getItem(position));
    }

    public List<Record> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
    }

}