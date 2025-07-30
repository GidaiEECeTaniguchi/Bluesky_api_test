package com.aether.myaether.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.aether.myaether.repository.AuthorRepository;
import com.aether.myaether.repository.PostRepository;
import com.aether.myaether.bluesky.BlueskyPostInfo;
import com.aether.myaether.data.source.local.BlueskyLocalDataSourceImpl;
import com.aether.myaether.data.source.remote.BlueskyRemoteDataSourceImpl;

import java.lang.ref.WeakReference;
import com.aether.myaether.repository.AuthRepository;
import work.socialhub.kbsky.auth.BearerTokenAuthProvider;
import work.socialhub.kbsky.model.app.bsky.actor.ActorDefsProfileView;

import java.util.List;
import java.util.Random;
import android.util.Log;

public class DataFetchTask extends AsyncTask<Void, String, String> {

    private final WeakReference<Activity> weakActivity;
    private final AuthRepository authRepository;
    private final AuthorRepository authorRepository;
    private final PostRepository postRepository;
    private final TextView textViewToUpdate;

    public DataFetchTask(Activity activity, AuthRepository authRepository, TextView textView) {
        this.weakActivity = new WeakReference<>(activity);
        this.authRepository = authRepository;
        BlueskyRemoteDataSourceImpl blueskyRemoteDataSource = new BlueskyRemoteDataSourceImpl();
        BlueskyLocalDataSourceImpl blueskyLocalDataSource = new BlueskyLocalDataSourceImpl(activity.getApplicationContext());
        this.authorRepository = new AuthorRepository(blueskyRemoteDataSource, blueskyLocalDataSource);
        this.postRepository = new PostRepository(activity.getApplicationContext(), blueskyRemoteDataSource, blueskyLocalDataSource);
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

        resultBuilder.append("-----------------------------------\n");
        publishProgress(resultBuilder.toString());

        BearerTokenAuthProvider authProvider = authRepository.getAuthProvider();
        String did = authRepository.getDid();

        if (authProvider == null || did == null) {
            resultBuilder.append("ログインしていません。\nログイン画面からログインしてください。");
            return resultBuilder.toString();
        }

        publishProgress(resultBuilder.toString() + "Blueskyの情報を取得中...");

        // フォローしているユーザーをランダムに取得
        List<ActorDefsProfileView> allFollows = authorRepository.fetchAndSaveFollowingFromApi(authProvider, did);
        if (!allFollows.isEmpty()) {
            Random random = new Random();
            ActorDefsProfileView randomUser = allFollows.get(random.nextInt(allFollows.size()));
            resultBuilder.append("ランダムに選ばれたフォロー中のユーザー:\n");
            resultBuilder.append("→ @" + randomUser.getHandle()).append("\n");
        } else {
            resultBuilder.append("フォローしているユーザーがいません。\n");
        }

        // タイムラインの最初の投稿情報を取得
        try {
            List<BlueskyPostInfo> timeline = postRepository.fetchTimelineFromApi(authProvider);
            if (!timeline.isEmpty()) {
                BlueskyPostInfo firstPost = timeline.get(0);
                resultBuilder.append("Blueskyタイムライン情報:\n");
                resultBuilder.append(firstPost.toString()).append("\n");
            } else {
                resultBuilder.append("タイムラインに投稿がありません。\n");
            }
        } catch (Exception e) {
            resultBuilder.append("タイムライン取得エラー: " + e.getMessage()).append("\n");
            Log.e("DataFetchTask", "タイムライン取得エラー", e);
        }

        return resultBuilder.toString();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Activity activity = weakActivity.get();
        if (activity!= null &&!activity.isFinishing() && values!= null && values.length > 0) {
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
                textViewToUpdate.append("\n最終結果の取得に失敗しました。");
            }
        }
    }
}