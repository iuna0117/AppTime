package com.example.user.apptime.Entity;

import android.arch.persistence.room.Entity;

import java.io.Serializable;

@Entity
public class Record implements Serializable{
    private long id;
    private long timeStart;
    private long timeEnd;
    private String description;
    private long duration;
    private long idCategory;

    public Record() {
    }
    public Record(long timeStart, long timeEnd, String description, long duration, int idCategory) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.description = description;
        this.duration = duration;
        this.idCategory = idCategory;
    }

    public Record(long id, long timeStart, long timeEnd, String description, long duration, int idCategory) {
        this.id = id;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.description = description;
        this.duration = duration;
        this.idCategory = idCategory;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(long idCategory) {
        this.idCategory = idCategory;
    }
}
