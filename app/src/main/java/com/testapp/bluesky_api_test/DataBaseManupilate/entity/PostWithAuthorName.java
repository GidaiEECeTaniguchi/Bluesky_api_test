package com.testapp.bluesky_api_test.DataBaseManupilate.entity;

public class PostWithAuthorName {
    // BasePost のフィールド
    public int id;
    public String uri;
    public String cid;
    public int user_id;
    public int author_id;
    public String content;
    public String created_at;

    // Author のハンドル
    public String authorHandle;

    // Getter and Setter (Roomが自動でマッピングするために必要)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUri() { return uri; }
    public void setUri(String uri) { this.uri = uri; }
    public String getCid() { return cid; }
    public void setCid(String cid) { this.cid = cid; }
    public int getUser_id() { return user_id; }
    public void setUser_id(int user_id) { this.user_id = user_id; }
    public int getAuthor_id() { return author_id; }
    public void setAuthor_id(int author_id) { this.author_id = author_id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
    public String getAuthorHandle() { return authorHandle; }
    public void setAuthorHandle(String authorHandle) { this.authorHandle = authorHandle; }
}