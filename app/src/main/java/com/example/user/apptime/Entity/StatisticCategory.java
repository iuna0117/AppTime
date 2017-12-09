package com.example.user.apptime.Entity;

import java.io.Serializable;

public class StatisticCategory implements Serializable {
    private Category category;
    private String statisticRes;

    public StatisticCategory(Category category, String statisticRes) {

        this.category = category;
        this.statisticRes = statisticRes;
    }

    public StatisticCategory() {

    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setStatisticRes(String statisticRes) {
        this.statisticRes = statisticRes;
    }

    public Category getCategory() {

        return category;
    }

    public String getStatisticRes() {
        return statisticRes;
    }


}
