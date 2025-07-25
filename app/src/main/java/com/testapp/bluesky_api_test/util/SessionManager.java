
package com.testapp.bluesky_api_test.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import work.socialhub.kbsky.auth.OAuthContext;

public class SessionManager {

    private static final String PREF_NAME = "BlueSkyApiTestPref";
    private static final String KEY_ACCESS_TOKEN = "accessToken";
    private static final String KEY_REFRESH_TOKEN = "refreshToken";
    private static final String KEY_USER_DID = "userDid";
    private static final String KEY_OAUTH_CONTEXT = "oauthContext";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Gson gson;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        gson = new Gson();
    }

    public void saveSession(String accessToken, String refreshToken, String userDid, OAuthContext oauthContext) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putString(KEY_USER_DID, userDid);
        String oauthContextJson = gson.toJson(oauthContext);
        editor.putString(KEY_OAUTH_CONTEXT, oauthContextJson);
        editor.apply();
    }

    public String getAccessToken() {
        return pref.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return pref.getString(KEY_REFRESH_TOKEN, null);
    }

    public String getUserDid() {
        return pref.getString(KEY_USER_DID, null);
    }

    public OAuthContext getOauthContext() {
        String oauthContextJson = pref.getString(KEY_OAUTH_CONTEXT, null);
        if (oauthContextJson != null) {
            return gson.fromJson(oauthContextJson, OAuthContext.class);
        }
        return null;
    }

    public boolean isLoggedIn() {
        return getAccessToken() != null;
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
