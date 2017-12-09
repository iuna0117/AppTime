package com.example.user.apptime.Entity;

import android.arch.persistence.room.Entity;

import java.io.Serializable;


@Entity
public class Category implements Serializable{
    private long id;
    private String title;
    private String icon;

    public Category(String title, String icon) {
        this.title = title;
        this.icon = icon;
    }

    public Category() {
    }

    public Category(long id, String title, String icon) {
        this.id = id;
        this.title = title;
        this.icon = icon;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
