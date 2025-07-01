package com.testapp.bluesky_api_test.task;

import android.content.Context;
import android.util.Log;

import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabase;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabaseSingleton;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.AuthorDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.BasePostDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.UserDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.Author;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.User;
import com.testapp.bluesky_api_test.repository.AuthorRepository;
import com.testapp.bluesky_api_test.repository.PostRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import work.socialhub.kbsky.auth.BearerTokenAuthProvider;
import work.socialhub.kbsky.model.app.bsky.actor.ActorDefsProfileView;
import work.socialhub.kbsky.model.app.bsky.feed.FeedDefsFeedViewPost;
import work.socialhub.kbsky.model.app.bsky.feed.FeedDefsPostView;
import work.socialhub.kbsky.model.app.bsky.feed.FeedPost;
import work.socialhub.kbsky.model.share.RecordUnion;
import work.socialhub.kbsky.BlueskyTypes;


public class FetchFollowsPostTask {

    private static final String TAG = "FetchFollowsPostTask";

    private final AuthorRepository authorRepository;
    private final PostRepository postRepository;
    private final UserDao userDao;
    private final ExecutorService executorService;

    public FetchFollowsPostTask(Context context) {
        this.authorRepository = new AuthorRepository(context);
        this.postRepository = new PostRepository(context);
        AppDatabase db = AppDatabaseSingleton.getInstance(context);
        this.userDao = db.userDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * フォローしている全ユーザーの投稿を取得し、データベースに保存します。
     * @param authProvider 認証プロバイダー
     * @param userDid ログインしているユーザーのDID
     */
    public void fetchAndSaveAllFollowsPosts(BearerTokenAuthProvider authProvider, String userDid) {
        executorService.execute(() -> {
            try {
                User currentUser = userDao.getUserByDid(userDid);
                if (currentUser == null) {
                    Log.e(TAG, "Current user not found in database: " + userDid);
                    return;
                }
                int currentUserId = currentUser.getId();

                // 1. フォローしているユーザーのリストをAPIから取得
                List<ActorDefsProfileView> following = authorRepository.fetchAndSaveFollowingFromApi(authProvider, userDid);
                Log.d(TAG, "Found " + following.size() + " following users.");

                // 2. 各ユーザーの投稿を取得して保存
                for (ActorDefsProfileView followedUser : following) {
                    Log.d(TAG, "Fetching posts for: " + followedUser.getHandle());
                    postRepository.fetchAndSaveAuthorFeed(authProvider, followedUser.getDid(), currentUserId);
                }

                Log.d(TAG, "Finished fetching and saving all posts from followed users.");

            } catch (Exception e) {
                Log.e(TAG, "Error fetching or saving follows' posts", e);
            }
        });
    }

    public void shutdown() {
        executorService.shutdown();
    }
}

