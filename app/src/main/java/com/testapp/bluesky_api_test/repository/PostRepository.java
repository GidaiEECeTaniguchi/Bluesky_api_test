package com.testapp.bluesky_api_test.repository;

import android.content.Context;

import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabase;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabaseSingleton;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.BasePostDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost;
import com.testapp.bluesky_api_test.bluesky.BlueskyOperations;
import com.testapp.bluesky_api_test.bluesky.BlueskyPostInfo;

import work.socialhub.kbsky.auth.BearerTokenAuthProvider;

import java.util.List;

public class PostRepository {

    private final BlueskyOperations blueskyOperations;
    private final BasePostDao basePostDao;

    public PostRepository(Context context) {
        this.blueskyOperations = new BlueskyOperations();
        AppDatabase db = AppDatabaseSingleton.getInstance(context);
        this.basePostDao = db.basePostDao();
    }

    /**
     * Bluesky APIからタイムラインの投稿情報を取得します。
     * @param authProvider 認証プロバイダー
     * @return 投稿情報オブジェクトのリスト
     * @throws Exception API呼び出し中にエラーが発生した場合
     */
    public List<BlueskyPostInfo> fetchTimelineFromApi(BearerTokenAuthProvider authProvider) throws Exception {
        return blueskyOperations.fetchTimeline(authProvider);
    }

    /**
     * Bluesky APIから特定のユーザーの投稿情報を取得します。
     * @param authProvider 認証プロバイダー
     * @param actorIdentifier ユーザーの識別子（DIDまたはハンドル）
     * @return 投稿情報オブジェクトのリスト
     * @throws Exception API呼び出し中にエラーが発生した場合
     */
    public List<BlueskyPostInfo> fetchAuthorFeedFromApi(BearerTokenAuthProvider authProvider, String actorIdentifier) throws Exception {
        return blueskyOperations.fetchAuthorFeed(authProvider, actorIdentifier);
    }

    /**
     * データベースから保存された投稿を取得するメソッド。
     * @return 保存された投稿のリスト
     */
    public List<BasePost> getSavedPostsFromDb() {
        return basePostDao.getAll();
    }

    /**
     * 単一のBasePostをデータベースに挿入します。
     * @param post 挿入するBasePostオブジェクト
     * @return 挿入された行のID
     */
    public long insertPostToDb(BasePost post) {
        return basePostDao.insert(post);
    }

    /**
     * データベースから特定のIDの投稿を取得します。
     * @param id 投稿ID
     * @return 該当するBasePostオブジェクト、またはnull
     */
    public BasePost getPostByIdFromDb(int id) {
        return basePostDao.getById(id);
    }
}