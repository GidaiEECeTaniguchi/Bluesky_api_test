package com.testapp.bluesky_api_test.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.testapp.bluesky_api_test.repository.AuthRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // ログイン処理の結果を保持するLiveData
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    // ログイン済みかどうかを保持するLiveData
    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        // AuthRepositoryを初期化
        authRepository = new AuthRepository(application);
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public LiveData<Boolean> getIsLoggedIn() {
        return isLoggedIn;
    }

    // すでにログイン済みかチェックする
    public void checkIfAlreadyLoggedIn() {
        if (authRepository.isLoggedIn()) {
            isLoggedIn.setValue(true);
        }
    }

    // ログイン処理を実行するメソッド
    public void login(String handle, String password) {
        // 処理開始をUIに通知
        loginResult.setValue(new LoginResult(true, null, false));

        executorService.execute(() -> {
            try {
                // Repositoryにログイン処理を依頼
                authRepository.login(handle, password);
                // 成功をUIに通知
                loginResult.postValue(new LoginResult(false, null, true));
            } catch (Exception e) {
                // 失敗をUIに通知
                loginResult.postValue(new LoginResult(false, e.getMessage(), false));
            }
        });
    }

    // ログイン結果を表現するための内部クラス
    public static class LoginResult {
        private final boolean isLoading;
        private final String error;
        private final boolean isSuccess;

        LoginResult(boolean isLoading, String error, boolean isSuccess) {
            this.isLoading = isLoading;
            this.error = error;
            this.isSuccess = isSuccess;
        }

        public boolean isLoading() { return isLoading; }
        public String getError() { return error; }
        public boolean isSuccess() { return isSuccess; }
    }
}