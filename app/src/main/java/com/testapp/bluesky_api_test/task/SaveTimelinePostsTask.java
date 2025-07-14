package com.testapp.bluesky_api_test.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.testapp.bluesky_api_test.DataBaseManupilate.entity.Author;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost;
import com.testapp.bluesky_api_test.repository.PostRepository;
import com.testapp.bluesky_api_test.repository.AuthorRepository;

import java.util.ArrayList;
import java.util.List;

import work.socialhub.kbsky.BlueskyTypes;
import work.socialhub.kbsky.model.app.bsky.feed.FeedDefsFeedViewPost;
import work.socialhub.kbsky.model.app.bsky.feed.FeedDefsPostView;
import work.socialhub.kbsky.model.app.bsky.feed.FeedPost;
import work.socialhub.kbsky.model.share.RecordUnion;

public class SaveTimelinePostsTask extends AsyncTask<List<FeedDefsFeedViewPost>, Void, Void> {

    private static final String TAG = "SaveTimelinePostsTask";
    private final PostRepository postRepository;
    private final AuthorRepository authorRepository;

    public SaveTimelinePostsTask(Context context) {
        this.postRepository = new PostRepository(context);
        this.authorRepository = new AuthorRepository(context);
    }

    @Override
    protected Void doInBackground(List<FeedDefsFeedViewPost>... lists) {
        if (lists.length == 0 || lists[0] == null) {
            Log.e(TAG, "No posts provided to save.");
            return null;
        }

        List<FeedDefsFeedViewPost> postsToSave = lists[0];
        List<BasePost> basePosts = new ArrayList<>();

        for (FeedDefsFeedViewPost feedViewPost : postsToSave) {
            FeedDefsPostView postView = feedViewPost.getPost();
            if (postView != null && postView.getRecord() != null) {
                RecordUnion record = postView.getRecord();
                if (BlueskyTypes.FeedPost.equals(record.getType())) {
                    FeedPost postContent = (FeedPost) record;

                    try {
                        // Authorの保存または取得
                        Author author = authorRepository.getAuthorByDidFromDb(postView.getAuthor().getDid());
                        if (author == null) {
                            author = new Author();
                            author.setDid(postView.getAuthor().getDid());
                            author.setHandle(postView.getAuthor().getHandle());
                            author.setDisplayName(postView.getAuthor().getDisplayName());
                            author.setAvatar(postView.getAuthor().getAvatar());
                            author.setIndexedAt(postView.getAuthor().getIndexedAt().getTime());
                            long authorId = authorRepository.insertAuthor(author);
                            author.setId((int) authorId);
                        }

                        String content = postContent.getText() != null ? postContent.getText() : "";
                        String createdAt = postContent.getCreatedAt();
                        String uri = postView.getUri();
                        String cid = postView.getCid();

                        BasePost basePost = new BasePost(uri, cid, author.getId(), author.getId(), content, createdAt);
                        basePosts.add(basePost);
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing post for saving: " + e.getMessage());
                    }
                }
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
