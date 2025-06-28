package com.testapp.bluesky_api_test.DataBaseManupilate;

import android.content.Context;
import android.util.Log;

import com.testapp.bluesky_api_test.DataBaseManupilate.dao.AuthorDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.Author;
import work.socialhub.kbsky.model.app.bsky.actor.ActorDefsProfileView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthorDatabaseOperations {

    private static final String TAG = "AuthorDBOperations";
    private final AuthorDao authorDao;
    private final ExecutorService executorService;

    public AuthorDatabaseOperations(Context context) {
        AppDatabase db = AppDatabaseSingleton.getInstance(context);
        authorDao = db.authorDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * 単一のAuthorをデータベースに挿入します。
     * 既に存在するhandleまたはdidを持つAuthorは挿入されません。
     * @param author 挿入するAuthorオブジェクト
     */
    public void insertAuthor(Author author) {
        executorService.execute(() -> {
            try {
                Author existingAuthorByHandle = authorDao.getAuthorByHandle(author.getHandle());
                Author existingAuthorByDid = authorDao.getAuthorByDid(author.getDid());

                if (existingAuthorByHandle == null && existingAuthorByDid == null) {
                    long id = authorDao.insert(author);
                    Log.d(TAG, "Author inserted with ID: " + id + ", Handle: " + author.getHandle());
                } else {
                    Log.d(TAG, "Author already exists: " + author.getHandle() + " or " + author.getDid());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error inserting author: " + author.getHandle(), e);
            }
        });
    }

    /**
     * ActorDefsProfileViewのリストをAuthorテーブルに保存します。
     * @param profileViews 保存するActorDefsProfileViewのリスト
     */
    public void saveFollowingAuthors(List<ActorDefsProfileView> profileViews) {
        executorService.execute(() -> {
            for (ActorDefsProfileView profileView : profileViews) {
                // handleとdidがnullでないことを確認
                if (profileView.getHandle() != null && profileView.getDid() != null) {
                    Author author = new Author(profileView.getHandle(), profileView.getDid());
                    insertAuthor(author);
                } else {
                    Log.w(TAG, "Skipping author due to null handle or DID: " + profileView);
                }
            }
            Log.d(TAG, "Finished saving following authors.");
        });
    }

    /**
     * ExecutorServiceをシャットダウンします。
     * アプリケーション終了時などに呼び出す必要があります。
     */
    public void shutdown() {
        executorService.shutdown();
    }
}