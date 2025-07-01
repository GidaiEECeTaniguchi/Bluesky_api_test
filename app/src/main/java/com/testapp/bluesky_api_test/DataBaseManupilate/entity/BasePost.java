package com.testapp.bluesky_api_test.DataBaseManupilate.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Index;

@Entity(
    tableName = "base_posts",
    foreignKeys = {
        @ForeignKey(
            entity = User.class,
            parentColumns = "id",
            childColumns = "user_id",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = Author.class,
            parentColumns = "id",
            childColumns = "author_id",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {@Index(value = {"uri"}, unique = true)}
)
public class BasePost {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "uri")
    private String uri;

    @ColumnInfo(name = "cid")
    private String cid;

    @ColumnInfo(name = "user_id")
    private int user_id;

    @ColumnInfo(name = "author_id")
    private int author_id;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "created_at")
    private String created_at;

    public BasePost() {}

    public BasePost(String uri, String cid, int user_id, int author_id, String content, String created_at) {
        this.uri = uri;
        this.cid = cid;
        this.user_id = user_id;
        this.author_id = author_id;
        this.content = content;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getCid() {
        return cid;
    }
    public void setCid(String cid) {
        this.cid = cid;
    }

    public int getUser_id() {
        return user_id;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getAuthor_id() {
        return author_id;
    }
    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated_at() {
        return created_at;
    }
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

}