
package com.testapp.bluesky_api_test.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import com.testapp.bluesky_api_test.R;
import com.testapp.bluesky_api_test.util.SessionManager;
// import com.testapp.bluesky_api_test.viewmodel.LoginViewModel;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import work.socialhub.kbsky.ATProtocolException;
import work.socialhub.kbsky.api.entity.share.Response;
import work.socialhub.kbsky.auth.Auth;
import work.socialhub.kbsky.auth.AuthFactory;
import work.socialhub.kbsky.auth.OAuthContext;
import work.socialhub.kbsky.auth.api.entity.oauth.BuildAuthorizationUrlRequest;
import work.socialhub.kbsky.auth.api.entity.oauth.OAuthAuthorizationCodeTokenRequest;
import work.socialhub.kbsky.auth.api.entity.oauth.OAuthPushedAuthorizationRequest;
import work.socialhub.kbsky.auth.api.entity.oauth.OAuthPushedAuthorizationResponse;
import work.socialhub.kbsky.auth.api.entity.oauth.OAuthTokenResponse;
import work.socialhub.kbsky.auth.domain.OAuthScopes;


public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private SessionManager sessionManager;

    // OAuth 関連の情報を保持
    private Auth auth;
    private OAuthContext context;

    // 仮の値を設定。後で正式なものに置き換える必要があります。
    private static final String CLIENT_ID = "https://my-aether-six.vercel.app/client-metadata.json";
    private static final String REDIRECT_URI = "https://my-aether-six.vercel.app/api/callback";
    private static final String PDS_URL = "https://bsky.social";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        // すでにログイン済みかチェック
        if (sessionManager.isLoggedIn()) {
            navigateToMain();
            return; // LoginActivityのUIは表示しない
        }

        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> initiateOAuthFlow());
    }

    private void initiateOAuthFlow() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                // 1. Auth インスタンスと OAuthContext の作成
                auth = AuthFactory.INSTANCE.instance(PDS_URL);
                context = new OAuthContext();
                context.setClientId(CLIENT_ID);
                context.setRedirectUri(REDIRECT_URI);

                // 3. Pushed Authorization Request の実行
                try {
                    OAuthPushedAuthorizationRequest parRequest = new OAuthPushedAuthorizationRequest();
                    parRequest.setScope(Arrays.asList(OAuthScopes.ATProto.getValue(), OAuthScopes.TransitionGeneric.getValue(), OAuthScopes.TransitionChatBsky.getValue()));
                    Response<OAuthPushedAuthorizationResponse> parResponse = auth.oauth().pushedAuthorizationRequest(context, parRequest);
                    String requestUri = parResponse.getData().getRequestUri();

                    // 4. 認可URLの構築
                    BuildAuthorizationUrlRequest buildAuthUrlRequest = new BuildAuthorizationUrlRequest();
                    buildAuthUrlRequest.setRequestUri(requestUri);
                    String authorizationUrl = auth.oauth().buildAuthorizationUrl(context, buildAuthUrlRequest);

                    // 5. ブラウザで認可URLを開く
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authorizationUrl));
                    startActivity(intent);

                } catch (ATProtocolException e) {
                    // エラー処理
                    String errorMessage;
                    if (e.getResponse() != null) {
                        errorMessage = "Pushed Authorization Requestに失敗しました: " + e.getResponse().messageForDisplay();
                    } else {
                        errorMessage = "Pushed Authorization Requestに失敗しました: " + e.getMessage();
                    }
                    Log.e("LoginActivity", errorMessage, e);
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show());
                }

            } catch (Exception e) {
                Log.e("LoginActivity", "OAuthフローの開始中にエラーが発生", e);
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "エラーが発生しました。", Toast.LENGTH_LONG).show());
            }
        });
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.d("LoginActivity", "onNewIntent called");

        Uri uri = intent.getData();
        Log.d("LoginActivity", "Received URI: " + (uri != null ? uri.toString() : "null"));

        if (uri != null && "myaether".equals(uri.getScheme()) && "callback".equals(uri.getHost())) {
            // サーバーからのリダイレクトを受け取る
            String accessToken = uri.getQueryParameter("access_token");
            String refreshToken = uri.getQueryParameter("refresh_token");
            String userDid = uri.getQueryParameter("user_did");
            String state = uri.getQueryParameter("state"); // 必要であればstateの検証も行う

            Log.d("LoginActivity", "Access Token: " + accessToken);
            Log.d("LoginActivity", "Refresh Token: " + refreshToken);
            Log.d("LoginActivity", "User DID: " + userDid);

            if (accessToken != null && refreshToken != null && userDid != null) {
                // TODO: state の検証

                // セッション情報を保存
                // contextはinitiateOAuthFlowで初期化されているはず
                if (context == null) {
                    // initiateOAuthFlowが呼ばれていない場合（例：アプリがバックグラウンドから復帰した場合）
                    // contextを再生成する必要があるかもしれない
                    auth = AuthFactory.INSTANCE.instance(PDS_URL);
                    context = new OAuthContext();
                    context.setClientId(CLIENT_ID);
                    context.setRedirectUri(REDIRECT_URI);
                }
                sessionManager.saveSession(accessToken, refreshToken, userDid, context);

                Toast.makeText(this, "ログイン成功！", Toast.LENGTH_SHORT).show();
                navigateToMain();

            } else {
                // エラー処理
                String error = uri.getQueryParameter("error");
                String errorDescription = uri.getQueryParameter("error_description");
                Log.e("LoginActivity", "OAuth Error: " + error + ", Description: " + errorDescription);
                Toast.makeText(this, "ログインに失敗しました: " + (errorDescription != null ? errorDescription : "不明なエラー"), Toast.LENGTH_LONG).show();
            }
        } else {
            Log.w("LoginActivity", "Received an unexpected URI: " + (uri != null ? uri.toString() : "null"));
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
