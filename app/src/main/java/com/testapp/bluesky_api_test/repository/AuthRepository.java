package com.testapp.bluesky_api_test.repository;

import android.content.Context;
import android.content.SharedPreferences;

import work.socialhub.kbsky.Bluesky;
import work.socialhub.kbsky.BlueskyFactory;
import work.socialhub.kbsky.api.entity.com.atproto.server.ServerCreateSessionRequest;
import work.socialhub.kbsky.api.entity.com.atproto.server.ServerCreateSessionResponse;
import work.socialhub.kbsky.api.entity.share.Response;
import work.socialhub.kbsky.auth.BearerTokenAuthProvider;

public class AuthRepository {

    private static final String PREF_NAME = "AuthPrefs";
    private static final String KEY_ACCESS_TOKEN = "accessToken";
    private static final String KEY_REFRESH_TOKEN = "refreshToken";
    private static final String KEY_DID = "did";
    private static final String KEY_HANDLE = "handle";

    private final SharedPreferences sharedPreferences;
    private final Bluesky bluesky;

    public AuthRepository(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.bluesky = BlueskyFactory.INSTANCE.instance("https://bsky.social");
    }

    // ログイン処理
    public void login(String handle, String password) throws Exception {
        ServerCreateSessionRequest request = new ServerCreateSessionRequest();
        request.setIdentifier(handle);
        request.setPassword(password);

        Response<ServerCreateSessionResponse> response = bluesky.server().createSession(request);
        ServerCreateSessionResponse data = response.getData();

        // 取得したトークンと情報をSharedPreferencesに保存
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, data.getAccessJwt());
        editor.putString(KEY_REFRESH_TOKEN, data.getRefreshJwt());
        editor.putString(KEY_DID, data.getDid());
        editor.putString(KEY_HANDLE, data.getHandle());

        editor.apply();
    }

    // ログアウト処理
    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    // ログイン済みかチェック
    public boolean isLoggedIn() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null) != null;
    }

    // 保存された認証情報からAuthProviderを取得
    public BearerTokenAuthProvider getAuthProvider() {
        String accessToken = sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
        String refreshToken = sharedPreferences.getString(KEY_REFRESH_TOKEN, null);

        if (accessToken != null && refreshToken != null) {
            return new BearerTokenAuthProvider(accessToken, refreshToken);
        }
        return null;
    }
    public String getDid() {
        return sharedPreferences.getString(KEY_DID, null);
    }

    public String getHandle() {
        return sharedPreferences.getString(KEY_HANDLE, null);
    }
}