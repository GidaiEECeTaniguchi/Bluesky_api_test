package com.testapp.bluesky_api_test.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabase;
import com.testapp.bluesky_api_test.bluesky.BlueskyOperations;
import com.testapp.bluesky_api_test.bluesky.BlueskyPostInfo;
import com.testapp.bluesky_api_test.DataBaseManupilate.DatabaseOperations;

import java.lang.ref.WeakReference;

public class DataFetchTask extends AsyncTask<Void, String, String> {

    private final WeakReference<Activity> weakActivity;
    private final DatabaseOperations databaseOperations;
    private final BlueskyOperations blueskyOperations;
    private final TextView textViewToUpdate;

    public DataFetchTask(Activity activity, AppDatabase db, TextView textView) {
        this.weakActivity = new WeakReference<>(activity);
        this.databaseOperations = new DatabaseOperations(db);
        this.blueskyOperations = new BlueskyOperations();
        this.textViewToUpdate = textView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Activity activity = weakActivity.get();
        if (activity!= null &&!activity.isFinishing()) {
            Toast.makeText(activity, "データを取得中...", Toast.LENGTH_SHORT).show();
            textViewToUpdate.setText("処理中...\n");
        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        StringBuilder resultBuilder = new StringBuilder();

        // 1. DB操作
        String dbHistory = databaseOperations.recordAccessAndGetHistory();
        resultBuilder.append(dbHistory);
        resultBuilder.append("-----------------------------------\n");
        // publishProgressで現在の結果をUIスレッドに送信
        publishProgress(resultBuilder.toString());

        // 2. Bluesky API操作
        resultBuilder.append("Blueskyタイムライン情報:\n");
        BlueskyPostInfo postInfo = blueskyOperations.fetchFirstTimelinePostInfo();
        resultBuilder.append(postInfo.toString()).append("\n");

        // publishProgressで最終結果をUIスレッドに送信することも可能ですが、
        // doInBackgroundの戻り値として返し、onPostExecuteで処理する方が一般的です。
        // ここでは最終結果を戻り値として返します。
        return resultBuilder.toString();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Activity activity = weakActivity.get();
        // valuesがnullでなく、要素が存在することを確認
        if (activity!= null &&!activity.isFinishing() && values!= null && values.length > 0) {
            // 配列の最初の要素をTextViewに設定
            textViewToUpdate.setText(values[0]);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Activity activity = weakActivity.get();
        if (activity!= null &&!activity.isFinishing()) {
            Toast.makeText(activity, "データ取得完了", Toast.LENGTH_SHORT).show();
            if (result!= null) {
                textViewToUpdate.setText(result);
            } else {
                // doInBackgroundでエラーが発生した場合など、resultがnullになる可能性も考慮
                textViewToUpdate.append("\n最終結果の取得に失敗しました。");
            }
        }
    }
}