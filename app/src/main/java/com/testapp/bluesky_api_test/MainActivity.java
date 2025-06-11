package com.testapp.bluesky_api_test;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import work.socialhub.kbsky.BlueskyFactory;

import work.socialhub.kbsky.api.entity.com.atproto.server.ServerCreateSessionRequest;
import work.socialhub.kbsky.api.entity.com.atproto.server.ServerCreateSessionResponse;

import work.socialhub.kbsky.api.entity.share.Response;

public class MainActivity extends AppCompatActivity {

    public void login() {

        // Session Request

        // 1) セッション作成リクエストを組み立て
        ServerCreateSessionRequest req = new ServerCreateSessionRequest();
        req.setIdentifier("your-handle.bsky.social");
        req.setPassword("your-app-password");
    }

}