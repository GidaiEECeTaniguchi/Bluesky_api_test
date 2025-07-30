package com.testapp.bluesky_api_test.task;

import android.content.Context;
import android.util.Log;

import com.testapp.bluesky_api_test.DataBaseManupilate.entity.User;
import com.testapp.bluesky_api_test.repository.AuthorRepository;
import com.testapp.bluesky_api_test.repository.PostRepository;
import com.testapp.bluesky_api_test.data.source.local.BlueskyLocalDataSource;
import com.testapp.bluesky_api_test.data.source.local.BlueskyLocalDataSourceImpl;
import com.testapp.bluesky_api_test.data.source.remote.BlueskyRemoteDataSource;
import com.testapp.bluesky_api_test.data.source.remote.BlueskyRemoteDataSourceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import work.socialhub.kbsky.auth.BearerTokenAuthProvider;
import work.socialhub.kbsky.model.app.bsky.actor.ActorDefsProfileView;


public class FetchFollowsPostTask {

    private static final String TAG = "FetchFollowsPostTask";

    private final AuthorRepository authorRepository;
    private final PostRepository postRepository;
    private final BlueskyLocalDataSource blueskyLocalDataSource;
    private final ExecutorService executorService;

    public FetchFollowsPostTask(Context context) {
        BlueskyRemoteDataSource blueskyRemoteDataSource = new BlueskyRemoteDataSourceImpl();
        this.blueskyLocalDataSource = new BlueskyLocalDataSourceImpl(context);
        this.authorRepository = new AuthorRepository(blueskyRemoteDataSource, blueskyLocalDataSource);
        this.postRepository = new PostRepository(context, blueskyRemoteDataSource, blueskyLocalDataSource);
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
                User currentUser = blueskyLocalDataSource.getUserByDidFromDb(userDid);
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