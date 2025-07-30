
package com.testapp.bluesky_api_test;

import android.app.Application;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.testapp.bluesky_api_test.task.TimelineCrawlerWorker;
import java.util.concurrent.TimeUnit;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        setupPeriodicWork();
    }

    private void setupPeriodicWork() {
        // WorkRequestの作成
        PeriodicWorkRequest crawlWorkRequest = 
            new PeriodicWorkRequest.Builder(TimelineCrawlerWorker.class, 6, TimeUnit.HOURS)
                // ここに制約を追加することもできるよ (例: .setConstraints(constraints))
                .build();

        // WorkManagerにタスクを登録
        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork(
            "TimelineCrawler",
            ExistingPeriodicWorkPolicy.KEEP, // 既に同じ名前のタスクがあれば、何もしない
            crawlWorkRequest
        );
    }
}
