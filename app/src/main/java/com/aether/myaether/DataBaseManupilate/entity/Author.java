package com.aether.myaether.DataBaseManupilate.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "authors")
public class Author {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "handle")
    private String handle;

    @ColumnInfo(name = "did")
    private String did;

    @Ignore
    public Author() {}

    public Author(String handle, String did) {
        this.handle = handle;
        this.did = did;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }
}