
package com.testapp.bluesky_api_test.task;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.testapp.bluesky_api_test.bluesky.BlueskyOperations;
import com.testapp.bluesky_api_test.repository.AuthRepository;
import com.testapp.bluesky_api_test.repository.PostRepository;
import com.testapp.bluesky_api_test.repository.AuthorRepository;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.Author;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost;

import java.util.ArrayList;
import java.util.List;

import work.socialhub.kbsky.BlueskyTypes;
import work.socialhub.kbsky.auth.BearerTokenAuthProvider;
import work.socialhub.kbsky.model.app.bsky.feed.FeedDefsFeedViewPost;
import work.socialhub.kbsky.model.app.bsky.feed.FeedDefsPostView;
import work.socialhub.kbsky.model.app.bsky.feed.FeedPost;
import work.socialhub.kbsky.model.share.RecordUnion;

public class TimelineCrawlerWorker extends Worker {

    private static final String TAG = "TimelineCrawlerWorker";
    private final Context context;

    public TimelineCrawlerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "TimelineCrawlerWorker started.");

        AuthRepository authRepository = new AuthRepository(context);
        BearerTokenAuthProvider authProvider = authRepository.getAuthProvider(); // getLoginSession -> getAuthProvider

        if (authProvider == null) {
            Log.e(TAG, "User not logged in. Worker stopping.");
            return Result.failure();
        }

        try {
            // 1. データの収集
            BlueskyOperations blueskyOperations = new BlueskyOperations();
            List<FeedDefsFeedViewPost> timelinePosts = blueskyOperations.fetchTimeline(authProvider);

            if (timelinePosts == null || timelinePosts.isEmpty()) {
                Log.d(TAG, "No new posts fetched.");
                return Result.success();
            }

            // 2. データの処理とDB格納
            PostRepository postRepository = new PostRepository(context);
            AuthorRepository authorRepository = new AuthorRepository(context);
            List<BasePost> basePosts = new ArrayList<>();

            for (FeedDefsFeedViewPost feedViewPost : timelinePosts) {
                FeedDefsPostView postView = feedViewPost.getPost();
                if (postView != null && postView.getRecord() != null) {
                    RecordUnion record = postView.getRecord();
                    if (BlueskyTypes.FeedPost.equals(record.getType())) {
                        FeedPost postContent = (FeedPost) record;

                        Author author = authorRepository.getAuthorByDidFromDb(postView.getAuthor().getDid());
                        if (author == null) {
                            // Authorの正しいコンストラクタを使ってインスタンスを作成
                            author = new Author(postView.getAuthor().getHandle(), postView.getAuthor().getDid());
                            // setDisplayName, setAvatar は存在しないので削除
                            author = authorRepository.insertAuthorToDb(author);
                            if (author == null) {
                                Log.e(TAG, "Failed to insert author: " + postView.getAuthor().getHandle());
                                continue; // Skip this post if author cannot be saved
                            }
                        }

                        String content = postContent.getText() != null ? postContent.getText() : "";
                        String createdAt = postContent.getCreatedAt();
                        String uri = postView.getUri();
                        String cid = postView.getCid();
                        
                        // BasePostのコンストラクタに合わせて user_id を取得する
                        // ここではログインユーザーのIDを取得する必要があるが、AuthRepositoryからDIDは取得できるが、DBのIDは直接取得できない。
                        // そのため、ひとまず固定値(e.g., 1) or DIDからUserを取得してIDを得る必要がある。
                        // 今回はAuthRepositoryにgetDid()があるので、それを使う。
                        // String userDid = authRepository.getDid(); // DIDを取得
                        // User currentUser = userRepository.getUserByDid(userDid); // DIDからUserを取得
                        // int userId = currentUser.getId(); // UserのIDを取得
                        // FIXME: 上記の実装はUserDaoをWorkerで使えるようにする必要があるため、一旦固定値で対応
                        int userId = 1; 

                        BasePost basePost = new BasePost(uri, cid, userId, author.getId(), content, createdAt);
                        basePosts.add(basePost);
                    }
                }
            }

            if (!basePosts.isEmpty()) {
                postRepository.insertAllPosts(basePosts);
                Log.d(TAG, "Successfully saved " + basePosts.size() + " posts to database.");
            }

            Log.d(TAG, "TimelineCrawlerWorker finished successfully.");
            return Result.success();

        } catch (Exception e) {
            Log.e(TAG, "Worker failed", e);
            return Result.failure();
        }
    }
}
