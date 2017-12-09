package com.example.user.apptime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.apptime.Entity.StatisticCategory;
import com.example.user.apptime.R;

import java.util.List;


public class StatisticsAdapter extends BaseAdapter {

    private Context context;
    private List<StatisticCategory> categoryList;
    private LayoutInflater inflater;

    public StatisticsAdapter(Context context, List<StatisticCategory> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int i) {
        return categoryList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;

        if (view == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_category, viewGroup, false);
        }

        final StatisticCategory statisticCategory = getCategory(i);
        ((TextView) view.findViewById(R.id.tvCategoryName)).setText(statisticCategory.getCategory().getTitle().toString()+" ("+statisticCategory.getStatisticRes()+")");
        ((ImageView) view.findViewById(R.id.ivCategoryIcon)).setImageResource(context.getResources().getIdentifier(statisticCategory.getCategory().getIcon(), "drawable", context.getPackageName()));
        ((ImageView) view.findViewById(R.id.ivCategoryIcon)).setTag(statisticCategory.getCategory().getIcon());
        return view;

    }

    public StatisticCategory getCategory(int position) {
        return ((StatisticCategory) getItem(position));
    }

    public List<StatisticCategory> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<StatisticCategory> categoryList) {
        this.categoryList = categoryList;
    }
}