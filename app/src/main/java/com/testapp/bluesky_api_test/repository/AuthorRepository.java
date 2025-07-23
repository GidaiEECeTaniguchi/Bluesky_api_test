package com.testapp.bluesky_api_test.repository;

import android.content.Context;
import android.util.Log;

import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabase;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabaseSingleton;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.AuthorDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.Author;
import com.testapp.bluesky_api_test.bluesky.BlueskyOperations;


import work.socialhub.kbsky.auth.AuthProvider;

import work.socialhub.kbsky.model.app.bsky.actor.ActorDefsProfileView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthorRepository {

    private static final String TAG = "AuthorRepository";
    private final BlueskyOperations blueskyOperations;
    private final AuthorDao authorDao;
    private final ExecutorService executorService;

    public AuthorRepository(Context context) {
        this.blueskyOperations = new BlueskyOperations();
        AppDatabase db = AppDatabaseSingleton.getInstance(context);
        this.authorDao = db.authorDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Bluesky APIからフォローしている全ユーザーのリストを取得し、データベースに保存します。
     * @param authProvider 認証プロバイダー
     * @param userDid ユーザーのDID
     * @return フォローしているユーザーのリスト
     */
    public List<ActorDefsProfileView> fetchAndSaveFollowingFromApi(AuthProvider authProvider, String userDid) {
        List<ActorDefsProfileView> followingProfiles = blueskyOperations.fetchAllFollowingUsers(authProvider, userDid);
        saveFollowingAuthorsToDb(followingProfiles);
        return followingProfiles;
    }

    /**
     * 単一のAuthorをデータベースに挿入します。
     * 既に存在するhandleまたはdidを持つAuthorは挿入されません。
     * @param author 挿入するAuthorオブジェクト
     * @return 挿入または取得されたAuthorオブジェクト（IDがセットされたもの）
     */
    public Author insertAuthorToDb(Author author) {
        try {
            Author existingAuthorByHandle = authorDao.getAuthorByHandle(author.getHandle());
            Author existingAuthorByDid = authorDao.getAuthorByDid(author.getDid());

            if (existingAuthorByHandle == null && existingAuthorByDid == null) {
                long id = authorDao.insert(author);
                Log.d(TAG, "Author inserted with ID: " + id + ", Handle: " + author.getHandle());
                // 挿入されたAuthorをID付きで再取得して返す
                return authorDao.getAuthorByHandle(author.getHandle());
            } else {
                Log.d(TAG, "Author already exists: " + author.getHandle() + " or " + author.getDid());
                // 既存のAuthorを返す
                if (existingAuthorByHandle != null) {
                    return existingAuthorByHandle;
                } else {
                    return existingAuthorByDid;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error inserting author: " + author.getHandle(), e);
            return null; // エラー時はnullを返す
        }
    }

    /**
     * ActorDefsProfileViewのリストをAuthorテーブルに保存します。
     * @param profileViews 保存するActorDefsProfileViewのリスト
     */
    public void saveFollowingAuthorsToDb(List<ActorDefsProfileView> profileViews) {
        executorService.execute(() -> {
            for (ActorDefsProfileView profileView : profileViews) {
                if (profileView.getHandle() != null && profileView.getDid() != null) {
                    Author author = new Author(profileView.getHandle(), profileView.getDid());
                    insertAuthorToDb(author);
                } else {
                    Log.w(TAG, "Skipping author due to null handle or DID: " + profileView);
                }
            }
            Log.d(TAG, "Finished saving following authors to DB.");
        });
    }

    /**
     * データベースからAuthorを取得します。
     * @param handle Authorのハンドル
     * @return 該当するAuthorオブジェクト、またはnull
     */
    public Author getAuthorByHandleFromDb(String handle) {
        return authorDao.getAuthorByHandle(handle);
    }

    public Author getAuthorByDidFromDb(String did) {
        return authorDao.getAuthorByDid(did);
    }

    public Author getAuthorByIdFromDb(int id) {
        return authorDao.getAuthorById(id);
    }

    public List<Author> getAllAuthorsFromDb() {
        return authorDao.getAllAuthors();
    }

    /**
     * ExecutorServiceをシャットダウンします。
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
