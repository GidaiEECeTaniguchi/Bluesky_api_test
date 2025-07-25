package com.testapp.bluesky_api_test.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabaseSingleton;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.UserDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.User;
import com.testapp.bluesky_api_test.util.SessionManager;

import java.io.IOException;
import java.security.GeneralSecurityException;

import work.socialhub.kbsky.Bluesky;
import work.socialhub.kbsky.BlueskyFactory;
import work.socialhub.kbsky.api.entity.com.atproto.server.ServerCreateSessionRequest;
import work.socialhub.kbsky.api.entity.com.atproto.server.ServerCreateSessionResponse;

import work.socialhub.kbsky.api.entity.com.atproto.server.ServerRefreshSessionResponse;
import work.socialhub.kbsky.api.entity.share.AuthRequest;
import work.socialhub.kbsky.api.entity.share.Response;
import work.socialhub.kbsky.auth.AuthProvider;
import work.socialhub.kbsky.auth.OAuthContext;
import work.socialhub.kbsky.auth.OAuthProvider;
import work.socialhub.kbsky.auth.api.entity.oauth.OAuthRefreshTokenRequest;
import work.socialhub.kbsky.auth.api.entity.oauth.OAuthTokenResponse;

public class AuthRepository {

    private static final String TAG = "AuthRepository";

    private final SessionManager sessionManager;
    private final Bluesky bluesky;
    private final UserDao userDao;

    public AuthRepository(Context context) {
        this.sessionManager = new SessionManager(context);
        this.bluesky = BlueskyFactory.INSTANCE.instance("https://bsky.social");
        this.userDao = AppDatabaseSingleton.getInstance(context).userDao();
    }

    // ログアウト処理
    public void logout() {
        sessionManager.clearSession();
    }

    // ログイン済みかチェック
    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    // 保存された認証情報からAuthProviderを取得
    public AuthProvider getAuthProvider() {
        String accessToken = sessionManager.getAccessToken();
        String refreshToken = sessionManager.getRefreshToken();
        OAuthContext context = sessionManager.getOauthContext();

        if (accessToken != null && refreshToken != null && context != null) {
            return new OAuthProvider(accessToken, refreshToken, context);
        }
        return null;
    }

    // トークンをリフレッシュする
    public boolean refreshToken() {
        OAuthContext context = sessionManager.getOauthContext();
        if (context == null) {
            Log.e(TAG, "No OAuthContext available for refresh.");
            return false;
        }

        try {
            // kbskyのAuthインスタンスを取得（PDS_URLは適切に設定する必要がある）
            work.socialhub.kbsky.auth.Auth auth = work.socialhub.kbsky.auth.AuthFactory.INSTANCE.instance("https://bsky.social");

            // リフレッシュトークンリクエストを作成
            OAuthRefreshTokenRequest request = new OAuthRefreshTokenRequest(null); // AuthProviderは後で設定される

            // トークンリフレッシュを実行
            Response<OAuthTokenResponse> response = auth.oauth().refreshTokenRequest(context, request);

            // 新しいトークンを保存
            OAuthTokenResponse data = response.getData();
            sessionManager.saveSession(data.getAccessToken(), data.getRefreshToken(), data.getSub(), context);

            Log.d(TAG, "Token refreshed and saved successfully.");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Failed to refresh token", e);
            if (e instanceof work.socialhub.kbsky.ATProtocolException) {
                // エラーレスポンスを詳しく見て、リフレッシュトークン自体が失効しているか判断する
                // 例: e.getResponse().getError().equals("invalid_grant")
                Log.d(TAG, "Refresh token might be expired. Logging out.");
                logout();
            }
            return false;
        }
    }

    public String getDid() {
        return sessionManager.getUserDid();
    }

    public String getHandle() {
        // HandleはDIDから取得するか、別途保存する必要があります。
        // 現状はDIDを返していますが、必要に応じて修正してください。
        return sessionManager.getUserDid();
    }
}
