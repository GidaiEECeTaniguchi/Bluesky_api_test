package com.testapp.bluesky_api_test.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabaseSingleton;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.UserDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.User;

import java.io.IOException;
import java.security.GeneralSecurityException;

import work.socialhub.kbsky.Bluesky;
import work.socialhub.kbsky.BlueskyFactory;
import work.socialhub.kbsky.api.entity.com.atproto.server.ServerCreateSessionRequest;
import work.socialhub.kbsky.api.entity.com.atproto.server.ServerCreateSessionResponse;

import work.socialhub.kbsky.api.entity.com.atproto.server.ServerRefreshSessionResponse;
import work.socialhub.kbsky.api.entity.share.AuthRequest;
import work.socialhub.kbsky.api.entity.share.Response;
import work.socialhub.kbsky.auth.BearerTokenAuthProvider;

public class AuthRepository {

    private static final String PREF_NAME = "EncryptedAuthPrefs";
    private static final String KEY_ACCESS_TOKEN = "accessToken";
    private static final String KEY_REFRESH_TOKEN = "refreshToken";
    private static final String KEY_DID = "did";
    private static final String KEY_HANDLE = "handle";
    private static final String TAG = "AuthRepository";

    private final SharedPreferences sharedPreferences;
    private final Bluesky bluesky;
    private final UserDao userDao;

    public AuthRepository(Context context) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            this.sharedPreferences = EncryptedSharedPreferences.create(
                    PREF_NAME,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Could not create EncryptedSharedPreferences", e);
        }
        this.bluesky = BlueskyFactory.INSTANCE.instance("https://bsky.social");
        this.userDao = AppDatabaseSingleton.getInstance(context).userDao();
    }

    // ログイン処理
    public void login(String handle, String password) throws Exception {
        ServerCreateSessionRequest request = new ServerCreateSessionRequest();
        request.setIdentifier(handle);
        request.setPassword(password);

        Response<ServerCreateSessionResponse> response = bluesky.server().createSession(request);
        ServerCreateSessionResponse data = response.getData();

        // ログイン成功時の情報をログに出力
        Log.d(TAG, "Login successful. AccessJwt: " + data.getAccessJwt());
        Log.d(TAG, "Login successful. RefreshJwt: " + data.getRefreshJwt());
        Log.d(TAG, "Login successful. DID: " + data.getDid());
        Log.d(TAG, "Login successful. Handle: " + data.getHandle());

        // ユーザーがDBに存在するか確認し、存在しなければ追加
        User user = userDao.getUserByDid(data.getDid());
        if (user == null) {
            user = new User(data.getHandle(), data.getDid());
            userDao.insert(user);
        }

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

    // トークンをリフレッシュする
    public boolean refreshToken() {
        // Get the current AuthProvider
        BearerTokenAuthProvider authProvider = getAuthProvider();
        if (authProvider == null) {
            Log.e(TAG, "No AuthProvider available for refresh.");
            return false;
        }

        try {
            // トークンをログに出力
            Log.d(TAG, "Attempting to refresh token with Access Token: " + authProvider.getAccessTokenJwt());
            Log.d(TAG, "Attempting to refresh token with Refresh Token: " + authProvider.getRefreshTokenJwt());

            // Use the AuthProvider to create the request
            AuthRequest request = new AuthRequest(authProvider);

            Response<ServerRefreshSessionResponse> response = bluesky.server().refreshSession(request);
            ServerRefreshSessionResponse data = response.getData();

            // 新しいトークンを保存
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_ACCESS_TOKEN, data.getAccessJwt());
            editor.putString(KEY_REFRESH_TOKEN, data.getRefreshJwt());
            editor.putString(KEY_DID, data.getDid());
            editor.putString(KEY_HANDLE, data.getHandle());
            editor.apply();
            Log.d(TAG, "Token refreshed and saved successfully.");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to refresh token", e);
            return false;
        }
    }

    public String getDid() {
        return sharedPreferences.getString(KEY_DID, null);
    }

    public String getHandle() {
        return sharedPreferences.getString(KEY_HANDLE, null);
    }
}
