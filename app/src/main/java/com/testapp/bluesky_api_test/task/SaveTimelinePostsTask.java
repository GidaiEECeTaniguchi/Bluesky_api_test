package com.testapp.bluesky_api_test.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.testapp.bluesky_api_test.DataBaseManupilate.entity.Author;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost;
import com.testapp.bluesky_api_test.bluesky.BlueskyPostInfo;
import com.testapp.bluesky_api_test.repository.PostRepository;
import com.testapp.bluesky_api_test.repository.AuthorRepository;
import com.testapp.bluesky_api_test.data.source.local.BlueskyLocalDataSourceImpl;
import com.testapp.bluesky_api_test.data.source.remote.BlueskyRemoteDataSourceImpl;

import java.util.ArrayList;
import java.util.List;

public class SaveTimelinePostsTask extends AsyncTask<List<BlueskyPostInfo>, Void, Void> {

    private static final String TAG = "SaveTimelinePostsTask";
    private final PostRepository postRepository;
    private final AuthorRepository authorRepository;

    public SaveTimelinePostsTask(Context context) {
        BlueskyRemoteDataSourceImpl blueskyRemoteDataSource = new BlueskyRemoteDataSourceImpl();
        BlueskyLocalDataSourceImpl blueskyLocalDataSource = new BlueskyLocalDataSourceImpl(context);
        this.postRepository = new PostRepository(context, blueskyRemoteDataSource, blueskyLocalDataSource);
        this.authorRepository = new AuthorRepository(blueskyRemoteDataSource, blueskyLocalDataSource);
    }

    @Override
    protected Void doInBackground(List<BlueskyPostInfo>... lists) {
        if (lists.length == 0 || lists[0] == null) {
            Log.e(TAG, "No posts provided to save.");
            return null;
        }

        List<BlueskyPostInfo> postsToSave = lists[0];
        List<BasePost> basePosts = new ArrayList<>();

        for (BlueskyPostInfo postInfo : postsToSave) {
            try {
                // Authorの保存または取得
                Author author = authorRepository.getAuthorByHandleFromDb(postInfo.getAuthorHandle());
                if (author == null) {
                    author = authorRepository.insertAuthorToDb(new Author(postInfo.getAuthorHandle(), ""));
                }

                String content = postInfo.getText();
                String createdAt = postInfo.getCreatedAt();
                String uri = postInfo.getPostUri();
                String cid = postInfo.getCid();

                // TODO: 実際のuser_idを取得するように修正する
                BasePost basePost = new BasePost(uri, cid, 1, author.getId(), content, createdAt);
                basePosts.add(basePost);
            } catch (Exception e) {
                Log.e(TAG, "Error processing post for saving: " + e.getMessage());
            }
        }

        if (!basePosts.isEmpty()) {
            try {
                postRepository.insertAllPosts(basePosts);
                Log.d(TAG, "Successfully saved " + basePosts.size() + " posts to database.");
            } catch (Exception e) {
                Log.e(TAG, "Failed to save posts to database using PostRepository", e);
            }
        }
        return null;
    }
}