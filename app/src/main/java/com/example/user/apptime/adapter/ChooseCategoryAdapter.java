package com.example.user.apptime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.apptime.Entity.Category;
import com.example.user.apptime.R;

import java.util.ArrayList;
import java.util.List;

public class ChooseCategoryAdapter extends BaseAdapter {

    private Context context;
    private List<Category> categoryList;
    private List<Category> categoryChooseList;
    private LayoutInflater inflater;

    public ChooseCategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        categoryChooseList= new ArrayList<Category>();
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
            view = inflater.inflate(R.layout.item_choose_category, viewGroup, false);
        }

        final Category category = getCategory(i);
        ((TextView) view.findViewById(R.id.tvCategoryName)).setText(category.getTitle().toString());
        ((ImageView) view.findViewById(R.id.ivCategoryIcon)).setImageResource(context.getResources().getIdentifier(category.getIcon(), "drawable", context.getPackageName()));
        ((ImageView) view.findViewById(R.id.ivCategoryIcon)).setTag(category.getIcon());
        CheckBox chbPartis = (CheckBox) view.findViewById(R.id.isChoose);
        chbPartis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( isChecked )
                    categoryChooseList.add(category);
                else categoryChooseList.remove(category);
            }
        });
        return view;

    }

    public Category getCategory(int position) {
        return ((Category) getItem(position));
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public List<Category> getChooseCategoryList() {
        return categoryChooseList;
    }
}