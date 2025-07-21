package com.testapp.bluesky_api_test.bluesky;

public class BlueskyPostInfo {
    private final String authorHandle;
    private final String postUri;
    private final String cid;
    private final String text; //投稿本文
    private final int charCount;
    private final String createdAt;
    private final String error; // エラー情報を保持

    //Constructor
    public BlueskyPostInfo(String authorHandle, String postUri, String cid, String text, int charCount, String createdAt) {
        this.authorHandle = authorHandle;
        this.postUri = postUri;
        this.cid = cid;
        this.text = text;
        this.charCount = charCount;
        this.createdAt = createdAt;
        this.error = null;
    }

    public BlueskyPostInfo(String error) {
        this.authorHandle = null;
        this.postUri = null;
        this.cid = null;
        this.text = null;
        this.charCount = 0;
        this.createdAt = null;
        this.error = error;
    }

    public boolean isSuccess() {
        return error == null;
    }

    public String getAuthorHandle() {
        return authorHandle;
    }

    public String getPostUri() {
        return postUri;
    }

    public String getCid() {
        return cid;
    }

    public String getText() {
        return text;
    }

    public int getCharCount() {
        return charCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        if (isSuccess()) {
            return "投稿者: @" + authorHandle + "\n"
                    + "テキスト: " + text + "\n"
                    + "文字数: " + charCount;
        } else {
            return "エラー: " + error;
        }
    }
}