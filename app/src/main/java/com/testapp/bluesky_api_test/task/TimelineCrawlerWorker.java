package com.testapp.bluesky_api_test.task;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.testapp.bluesky_api_test.repository.AuthRepository;
import com.testapp.bluesky_api_test.repository.PostRepository;
import com.testapp.bluesky_api_test.repository.AuthorRepository;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import work.socialhub.kbsky.auth.BearerTokenAuthProvider;

// 1. @HiltWorkerアノテーションを追加
@HiltWorker
public class TimelineCrawlerWorker extends Worker {

    private static final String TAG = "TimelineCrawlerWorker";

    // 2. Hiltに注入してもらうフィールドを定義
    private final AuthRepository authRepository;
    private final PostRepository postRepository;
    private final AuthorRepository authorRepository;

    // 3. @AssistedInjectを使ってコンストラクタを定義し、必要なRepositoryを引数に追加
    @AssistedInject
    public TimelineCrawlerWorker(
            @Assisted @NonNull Context context,
            @Assisted @NonNull WorkerParameters workerParams,
            AuthRepository authRepository, // HiltがAppModuleから探して渡してくれる
            PostRepository postRepository,   // HiltがAppModuleから探して渡してくれる
            AuthorRepository authorRepository  // HiltがAppModuleから探して渡してくれる
    ) {
        super(context, workerParams);
        // 4. 注入されたインスタンスをフィールドに設定
        this.authRepository = authRepository;
        this.postRepository = postRepository;
        this.authorRepository = authorRepository;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "TimelineCrawlerWorker started.");

        // 5. `new`を使わず、注入されたインスタンスをそのまま使用する
        BearerTokenAuthProvider authProvider = authRepository.getAuthProvider();

        if (authProvider == null) {
            Log.e(TAG, "User not logged in. Worker stopping.");
            return Result.failure();
        }

        try {
            // Repositoryのインスタンス生成は不要！すでにあるものを使うだけ
            Log.d(TAG, "Repositories are injected. Ready to use.");

            // ここでpostRepositoryやauthorRepositoryを使った処理を記述
            // 例: postRepository.fetchTimeline();

        } catch (Exception e) {
            Log.e(TAG, "Worker failed", e);
            return Result.failure();
        }

        Log.d(TAG, "TimelineCrawlerWorker finished successfully.");
        return Result.success(); // 処理が成功したらsuccess()を返す
    }
}