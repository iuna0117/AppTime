package com.example.user.apptime.Entity;

import android.arch.persistence.room.Entity;
import java.io.Serializable;


@Entity
public class Photo implements Serializable{
    private long id;
    private String fileName;
    private String description;
    private long idRecord;

    public Photo() {
    }

    public Photo(long id, String fileName, String description, long idRecord) {
        this.id = id;
        this.fileName = fileName;
        this.description = description;
        this.idRecord = idRecord;
    }

    public Photo(String fileName, String description) {
        this.fileName = fileName;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getIdRecord() {
        return idRecord;
    }

    public void setIdRecord(long idRecord) {
        this.idRecord = idRecord;
    }
}
