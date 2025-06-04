package com.testapp.bluesky_api_test;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import work.socialhub.kbsky.api.entity.com.atproto.server.ServerCreateSessionRequest;
import work.socialhub.kbsky.api.entity.com.atproto.server.ServerCreateSessionResponse;

import work.socialhub.kbsky.api.entity.share.Response;

public class MainActivity extends AppCompatActivity {

    ServerCreateSessionRequest req = new ServerCreateSessionRequest();

    // セッション作成（同期呼び出し）
    Response<ServerCreateSessionResponse> res = BlueskyFactory
            .instance(BSKY_SOCIAL_URI)
            .server()
            .createSession(req);
    String accessJwt = res.get().getAccessJwt();
}