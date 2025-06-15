package com.testapp.bluesky_api_test.bluesky;

public class BlueskyPostInfo {
    private final String authorHandle;
    private final String postUri;
    private final String text;
    private final int charCount;
    private final String error; // エラー情報を保持

    public BlueskyPostInfo(String authorHandle, String postUri, String text, int charCount) {
        this.authorHandle = authorHandle;
        this.postUri = postUri;
        this.text = text;
        this.charCount = charCount;
        this.error = null;
    }

    public BlueskyPostInfo(String error) {
        this.authorHandle = null;
        this.postUri = null;
        this.text = null;
        this.charCount = 0;
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

    public String getText() {
        return text;
    }

    public int getCharCount() {
        return charCount;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        if (isSuccess()) {
            return "投稿者: @" + authorHandle + "\n"
                    + "テキスト: " + text + "\n" // ここで全文を表示
                    + "文字数: " + charCount;
        } else {
            return "エラー: " + error;
        }
    }
}