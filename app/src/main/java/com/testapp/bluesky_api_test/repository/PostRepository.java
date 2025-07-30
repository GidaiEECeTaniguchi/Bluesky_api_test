package com.testapp.bluesky_api_test.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.Author;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.PostWithAuthorName;
import com.testapp.bluesky_api_test.bluesky.BlueskyPostInfo;
import com.testapp.bluesky_api_test.data.source.local.BlueskyLocalDataSource;
import com.testapp.bluesky_api_test.data.source.remote.BlueskyRemoteDataSource;

import dagger.hilt.android.qualifiers.ApplicationContext;
import work.socialhub.kbsky.auth.BearerTokenAuthProvider;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PostRepository {

    private final BlueskyRemoteDataSource blueskyRemoteDataSource;
    private final BlueskyLocalDataSource blueskyLocalDataSource;


    public PostRepository(
             Context context,
            BlueskyRemoteDataSource blueskyRemoteDataSource,
            BlueskyLocalDataSource blueskyLocalDataSource) {
        this.blueskyRemoteDataSource = blueskyRemoteDataSource;
        this.blueskyLocalDataSource = blueskyLocalDataSource;
    }

    public void fetchAndSaveAuthorFeed(BearerTokenAuthProvider authProvider, String actorIdentifier, int userId) throws Exception {
        List<BlueskyPostInfo> feedViewPosts = blueskyRemoteDataSource.fetchAuthorFeed(authProvider, actorIdentifier);
        List<BasePost> postsToSave = new ArrayList<>();

        for (BlueskyPostInfo postInfo : feedViewPosts) {
            // Authorを保存または取得
            Author author = blueskyLocalDataSource.getAuthorByHandleFromDb(postInfo.getAuthorHandle());
            if (author == null) {
                author = blueskyLocalDataSource.insertAuthorToDb(new Author(postInfo.getAuthorHandle(), ""));
                if (author == null) {
                    // Log.e("PostRepository", "Failed to insert author: " + postInfo.getAuthorHandle());
                    continue; // Skip this post if author cannot be saved
                }
            }

            // BasePostを保存
            // ここではuser_idを仮に1としています。
            BasePost basePost = new BasePost(postInfo.getPostUri(), postInfo.getCid(), userId, author.getId(), postInfo.getText(), postInfo.getCreatedAt());
            postsToSave.add(basePost);
        }

        if (!postsToSave.isEmpty()) {
            blueskyLocalDataSource.insertAllPosts(postsToSave);
        }
    }

    public List<BlueskyPostInfo> fetchTimelineFromApi(BearerTokenAuthProvider authProvider) throws Exception {
        return blueskyRemoteDataSource.fetchTimeline(authProvider);
    }

    public List<BlueskyPostInfo> fetchAuthorFeedFromApi(BearerTokenAuthProvider authProvider, String actorIdentifier) throws Exception {
        return blueskyRemoteDataSource.fetchAuthorFeed(authProvider, actorIdentifier);
    }

    public LiveData<List<BasePost>> getSavedPostsFromDb() {
        return blueskyLocalDataSource.getSavedPostsFromDb();
    }

    public long insertPostToDb(BasePost post) {
        return blueskyLocalDataSource.insertPostToDb(post);
    }

    public BasePost getPostByIdFromDb(int id) {
        return blueskyLocalDataSource.getPostByIdFromDb(id);
    }
    public List<BasePost> getPostsByUserIdFromDb(int userId) {
        return blueskyLocalDataSource.getPostsByUserIdFromDb(userId);
    }

            public List<BasePost> getPostsByAuthorIdFromDb(int authorId) {
    return blueskyLocalDataSource.getPostsByAuthorIdFromDb(authorId);
 }

    public LiveData<List<PostWithAuthorName>> getAllPostsWithAuthorName() {
        return blueskyLocalDataSource.getAllPostsWithAuthorName();
    }

    public LiveData<List<BasePost>> getAllPosts() {
        return blueskyLocalDataSource.getAllPosts();
    }

    public void insertAllPosts(List<BasePost> posts) {
        blueskyLocalDataSource.insertAllPosts(posts);
    }

    public void shutdown() {
        // BlueskyOperations は ExecutorService を持っていないので、ここでは何もしない
        // 必要であれば、BlueskyOperations 内部で ExecutorService を管理し、ここでシャットダウンを呼び出す
    }
}
