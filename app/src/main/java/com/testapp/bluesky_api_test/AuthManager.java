package com.testapp.bluesky_api_test;

import android.content.Context;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import android.util.Base64;

import work.socialhub.kbsky.auth.AuthFactory;
import work.socialhub.kbsky.auth.OAuthContext;
import work.socialhub.kbsky.auth.OAuthProvider;
import work.socialhub.kbsky.auth.api.OAuthResource;
import work.socialhub.kbsky.auth.api.entity.oauth.BuildAuthorizationUrlRequest;
import work.socialhub.kbsky.auth.api.entity.oauth.OAuthAuthorizationCodeTokenRequest;
import work.socialhub.kbsky.auth.api.entity.oauth.OAuthRefreshTokenRequest;
import work.socialhub.kbsky.auth.api.entity.oauth.OAuthTokenResponse;
import work.socialhub.kbsky.api.entity.share.Response;

/**
 * AuthManager：AT Protocol (Bluesky) の OAuth 認証を管理するユーティリティクラス
 */
public class AuthManager {

    private static final String PREFS_NAME = "myaether_auth";
    private static final String KEY_ACCESS  = "access_token";
    private static final String KEY_REFRESH = "refresh_token";

    private final Context    appContext;
    private final String     pdsUri;
    private OAuthContext     oauthContext;
    private OAuthProvider    oauthProvider;

    public AuthManager(Context context, String pdsUri) {
        this.appContext = context.getApplicationContext();
        this.pdsUri     = pdsUri;
        initContext();
    }

    /** OAuthContext の初期化（PKCE codeVerifier を生成） */
    private void initContext() {
        this.oauthContext = new OAuthContext();
        // codeVerifier はランダム 32 バイト → Base64 URL エンコード
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        String codeVerifier = Base64.encodeToString(bytes, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
        oauthContext.setCodeVerifier(codeVerifier);
    }

    /**
     * 認可 URL を組み立てて返却
     */
    public String getAuthorizationUrl() {
        // 1) PKCE codeVerifier はすでに oauthContext に入っている前提
        BuildAuthorizationUrlRequest req = new BuildAuthorizationUrlRequest();
        req.setClientId(oauthContext.getClientId());
        req.setRequestUri(oauthContext.getRedirectUri());

        return AuthFactory.INSTANCE
                .instance(pdsUri)
                .oauth()
                .buildAuthorizationUrl(oauthContext, req);
    }
    /**
     * コールバックで取得した authorizationCode を使い、
     * accessToken / refreshToken を取得・永続化し、OAuthProvider を生成
     */

    public void exchangeCodeForToken(String authorizationCode)
            throws GeneralSecurityException, IOException {

        OAuthAuthorizationCodeTokenRequest req = new OAuthAuthorizationCodeTokenRequest();
        req.setClientId(oauthContext.getClientId());
        req.setRedirectUri(oauthContext.getRedirectUri());
        req.setCode(authorizationCode);
        req.setCodeVerifier(oauthContext.getCodeVerifier());

        Response<OAuthTokenResponse> resp =
                AuthFactory.INSTANCE
                        .instance(pdsUri)
                        .oauth()
                        .authorizationCodeTokenRequest(oauthContext, req);

        OAuthTokenResponse data = resp.getData();
        saveTokens(data.getAccessToken(), data.getRefreshToken());
        this.oauthProvider = new OAuthProvider(
                data.getAccessToken(),
                data.getRefreshToken(),
                oauthContext
        );
    }
    /**
     * 保存済みトークンを読み込んで OAuthProvider を復元できれば true
     */
    public boolean restoreFromStorage() {
        try {
            EncryptedSharedPreferences prefs = createPrefs();
            String access  = prefs.getString(KEY_ACCESS, null);
            String refresh = prefs.getString(KEY_REFRESH, null);
            if (access != null && refresh != null) {
                this.oauthProvider = new OAuthProvider(access, refresh, oauthContext);
                return true;
            }
        } catch (Exception ignored) { }
        return false;
    }

    /**
     * リフレッシュトークンを使ってアクセストークンを再取得
     */
    public void refreshToken() throws GeneralSecurityException, IOException {
        OAuthRefreshTokenRequest req = new OAuthRefreshTokenRequest(oauthProvider);

        req.setClientId( oauthContext.getClientId());
        req.setRedirectUri(oauthContext.getRedirectUri());
        // buildTokenRequest() で内部的に grant_type=refresh_token をセット

        Response<OAuthTokenResponse> resp =
                AuthFactory.INSTANCE.instance(pdsUri)
                        .oauth()
                        .refreshTokenRequest(oauthContext, req);

        OAuthTokenResponse data = resp.getData();
        saveTokens(data.getAccessToken(), data.getRefreshToken());
        this.oauthProvider = new OAuthProvider(
                data.getAccessToken(),
                data.getRefreshToken(),
                oauthContext
        );
    }

    /** 永続化ヘルパー */
    private void saveTokens(String accessToken, String refreshToken)
            throws GeneralSecurityException, IOException {
        EncryptedSharedPreferences prefs = createPrefs();
        prefs.edit()
                .putString(KEY_ACCESS, accessToken)
                .putString(KEY_REFRESH, refreshToken)
                .apply();
    }

    private EncryptedSharedPreferences createPrefs()
            throws GeneralSecurityException, IOException {
        String masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        return (EncryptedSharedPreferences) EncryptedSharedPreferences.create(
                PREFS_NAME,
                masterKey,
                appContext,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    /** 現在の OAuthProvider を取得 */
    public OAuthProvider getOAuthProvider() {
        return oauthProvider;
    }
}
