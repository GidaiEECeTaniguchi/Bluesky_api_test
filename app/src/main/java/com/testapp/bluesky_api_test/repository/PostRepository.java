package com.testapp.bluesky_api_test.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabase;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabaseSingleton;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.AuthorDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.BasePostDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.Author;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.PostWithAuthorName;
import com.testapp.bluesky_api_test.bluesky.BlueskyOperations;
import com.testapp.bluesky_api_test.bluesky.BlueskyPostInfo;
import java.time.Instant;
import java.util.Date;
import work.socialhub.kbsky.auth.BearerTokenAuthProvider;
import work.socialhub.kbsky.model.app.bsky.feed.FeedDefsFeedViewPost;
import work.socialhub.kbsky.model.app.bsky.feed.FeedDefsPostView;
import work.socialhub.kbsky.model.app.bsky.feed.FeedPost;
import work.socialhub.kbsky.model.share.RecordUnion;
import work.socialhub.kbsky.BlueskyTypes;

import java.util.ArrayList;
import java.util.List;

public class PostRepository {

    private final BlueskyOperations blueskyOperations;
    private final BasePostDao basePostDao;
    private final AuthorDao authorDao;

    public PostRepository(Context context) {
        this.blueskyOperations = new BlueskyOperations();
        AppDatabase db = AppDatabaseSingleton.getInstance(context);
        this.basePostDao = db.basePostDao();
        this.authorDao = db.authorDao();
    }

    public void fetchAndSaveAuthorFeed(BearerTokenAuthProvider authProvider, String actorIdentifier, int userId) throws Exception {
        List<FeedDefsFeedViewPost> feedViewPosts = blueskyOperations.fetchAuthorFeed(authProvider, actorIdentifier);
        List<BasePost> postsToSave = new ArrayList<>();

        for (FeedDefsFeedViewPost feedViewPost : feedViewPosts) {
            FeedDefsPostView postView = feedViewPost.getPost();
            if (postView != null && postView.getRecord() != null) {
                RecordUnion record = postView.getRecord();
                if (BlueskyTypes.FeedPost.equals(record.getType())) {
                    FeedPost postContent = (FeedPost) record;

                    Author author = authorDao.getAuthorByDid(postView.getAuthor().getDid());
                    if (author == null) {
                        continue;
                    }
                    int authorId = author.getId();

                    String content = postContent.getText() != null ? postContent.getText() : "";
                    String createdAt = postContent.getCreatedAt();
                    String uri = postView.getUri();
                    String cid = postView.getCid();

                    BasePost basePost = new BasePost(uri, cid, userId, authorId, content, createdAt);

                    postsToSave.add(basePost);
                }
            }
        }

        if (!postsToSave.isEmpty()) {
            basePostDao.insertAll(postsToSave.toArray(new BasePost[0]));
        }
    }

    public List<BlueskyPostInfo> fetchTimelineFromApi(BearerTokenAuthProvider authProvider) throws Exception {
        List<FeedDefsFeedViewPost> feedViewPosts = blueskyOperations.fetchTimeline(authProvider);
        return convertFeedViewPostsToBlueskyPostInfo(feedViewPosts);
    }

    public List<BlueskyPostInfo> fetchAuthorFeedFromApi(BearerTokenAuthProvider authProvider, String actorIdentifier) throws Exception {
        List<FeedDefsFeedViewPost> feedViewPosts = blueskyOperations.fetchAuthorFeed(authProvider, actorIdentifier);
        return convertFeedViewPostsToBlueskyPostInfo(feedViewPosts);
    }

    public LiveData<List<BasePost>> getSavedPostsFromDb() {
        return basePostDao.getAll();
    }

    public long insertPostToDb(BasePost post) {
        if (basePostDao.getPostByUri(post.getUri()) == null) {
            return basePostDao.insert(post);
        }
        return -1; // Indicate that the post was not inserted (e.g., already exists)
    }

    public BasePost getPostByIdFromDb(int id) {
        return basePostDao.getById(id);
    }
    public List<BasePost> getPostsByUserIdFromDb(int userId) {
        return basePostDao.getPostsByUserId(userId);
    }

            public List<BasePost> getPostsByAuthorIdFromDb(int authorId) {
    return basePostDao.getPostsByAuthorId(authorId);
 }

    public LiveData<List<PostWithAuthorName>> getAllPostsWithAuthorName() {
        return basePostDao.getAllPostsWithAuthorName();
    }

    public LiveData<List<BasePost>> getAllPosts() {
        return basePostDao.getAll();
    }

    public void insertAllPosts(List<BasePost> posts) {
        List<BasePost> postsToInsert = new ArrayList<>();
        for (BasePost post : posts) {
            if (basePostDao.getPostByUri(post.getUri()) == null) {
                postsToInsert.add(post);
            }
        }
        if (!postsToInsert.isEmpty()) {
            basePostDao.insertAll(postsToInsert.toArray(new BasePost[0]));
        }
    }

    private List<BlueskyPostInfo> convertFeedViewPostsToBlueskyPostInfo(List<FeedDefsFeedViewPost> feedViewPosts) {
        List<BlueskyPostInfo> blueskyPostInfos = new ArrayList<>();
        for (FeedDefsFeedViewPost feedViewPost : feedViewPosts) {
            FeedDefsPostView postView = feedViewPost.getPost();
            if (postView != null && postView.getRecord() != null) {
                RecordUnion record = postView.getRecord();
                if (BlueskyTypes.FeedPost.equals(record.getType())) {
                    FeedPost postContent = (FeedPost) record;
                    String postText = postContent.getText() != null ? postContent.getText() : "";
                    int charCount = postText.length();
                    String authorHandle = postView.getAuthor().getHandle();
                    String postUri = postView.getUri();
                    String cid = postView.getCid();
                    String createdAt = postContent.getCreatedAt();
                    blueskyPostInfos.add(new BlueskyPostInfo(authorHandle, postUri, cid, postText, charCount, createdAt));
                }
            }
        }
        return blueskyPostInfos;
    }

    public void shutdown() {
        // BlueskyOperations は ExecutorService を持っていないので、ここでは何もしない
        // 必要であれば、BlueskyOperations 内部で ExecutorService を管理し、ここでシャットダウンを呼び出す
    }
}
