package com.testapp.bluesky_api_test.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabase;
import com.testapp.bluesky_api_test.bluesky.BlueskyOperations;
import com.testapp.bluesky_api_test.bluesky.BlueskyPostInfo;
import com.testapp.bluesky_api_test.DataBaseManupilate.DatabaseOperations;
import com.testapp.bluesky_api_test.repository.BlueskyRepository;
import java.lang.ref.WeakReference;
import com.testapp.bluesky_api_test.repository.AuthRepository; // AuthRepositoryをインポート
import work.socialhub.kbsky.auth.BearerTokenAuthProvider; // AuthProviderをインポート
public class DataFetchTask extends AsyncTask<Void, String, String> {

    private final WeakReference<Activity> weakActivity;
    private final BlueskyRepository repository;
    private final AuthRepository authRepository;
    private final TextView textViewToUpdate;

    public DataFetchTask(Activity activity, AuthRepository authRepository, TextView textView) {
        this.weakActivity = new WeakReference<>(activity);
        this.repository = new BlueskyRepository(activity.getApplicationContext());
        this.authRepository = authRepository;
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
        String dbHistory = repository.getAccessHistory();
        resultBuilder.append(dbHistory);
        resultBuilder.append("-----------------------------------\n");
        // publishProgressで現在の結果をUIスレッドに送信
        publishProgress(resultBuilder.toString());
        BearerTokenAuthProvider authProvider = authRepository.getAuthProvider();
        String did = authRepository.getDid();
        // ログインしていない場合はエラーメッセージを表示して終了
        if (authProvider == null || did == null) {
            resultBuilder.append("ログインしていません。\nログイン画面からログインしてください。");
            return resultBuilder.toString();
        }
        // 2. RepositoryにBlueskyの情報を要求
        publishProgress(resultBuilder.toString() + "Blueskyの情報を取得中...");
        String randomUserInfo = repository.getRandomFollowingUserInfo();
        resultBuilder.append("ランダムに選ばれたフォロー中のユーザー:\n");
        resultBuilder.append(randomUserInfo);
        /* 
        resultBuilder.append("Blueskyタイムライン情報:\n");
        BlueskyPostInfo postInfo = blueskyOperations.fetchFirstTimelinePostInfo();
        resultBuilder.append(postInfo.toString()).append("\n");
         */
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