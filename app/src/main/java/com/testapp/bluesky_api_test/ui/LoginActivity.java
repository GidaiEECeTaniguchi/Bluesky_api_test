package com.testapp.bluesky_api_test.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar; // ProgressBarを追加するとより良い
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.testapp.bluesky_api_test.TestMainActivity;
import com.testapp.bluesky_api_test.R;
import com.testapp.bluesky_api_test.viewmodel.LoginViewModel;
import com.testapp.bluesky_api_test.viewmodel.LoginViewModel.LoginResult;

public class LoginActivity extends AppCompatActivity {

    private EditText etHandle;
    private EditText etPassword;
    private Button btnLogin;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // XMLファイル名を activity_login.xml と想定

        // ViewModelを初期化
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // UIコンポーネントを初期化
        etHandle = findViewById(R.id.etHandle);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // ログインボタンのクリックリスナーを設定
        btnLogin.setOnClickListener(v -> {
            String handle = etHandle.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (handle.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "ユーザー名とパスワードを入力してください", Toast.LENGTH_SHORT).show();
                return;
            }
            // ViewModelにログイン処理を依頼
            loginViewModel.login(handle, password);
        });

        // ViewModelのログイン結果を監視
        observeViewModel();

        // アプリ起動時にすでにログイン済みかチェック
        loginViewModel.checkIfAlreadyLoggedIn();
    }

    private void observeViewModel() {
        // ログイン処理の結果を監視
        loginViewModel.getLoginResult().observe(this, result -> {
            if (result == null) return;

            // ログイン中のUI制御（例: ボタンを無効化）
            setLoading(result.isLoading());

            // エラーがあれば表示
            if (result.getError() != null) {
                Toast.makeText(this, "エラー: " + result.getError(), Toast.LENGTH_LONG).show();
            }

            // ログインに成功したらMainActivityに遷移
            if (result.isSuccess()) {
                Toast.makeText(this, "ログイン成功！", Toast.LENGTH_SHORT).show();
                navigateToMain();
            }
        });

        // ログイン済みチェックの結果を監視
        loginViewModel.getIsLoggedIn().observe(this, isLoggedIn -> {
            if (isLoggedIn) {
                // すでにログイン済みなら直接MainActivityへ
                navigateToMain();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        btnLogin.setEnabled(!isLoading);
        // XMLにProgressBarを追加した場合、ここで表示/非表示を切り替えると良い
        // progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, TestMainActivity.class);
        startActivity(intent);
        finish(); // ログイン画面に戻らないように終了する
    }
}