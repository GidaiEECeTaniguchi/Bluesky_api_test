package com.testapp.bluesky_api_test.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.testapp.bluesky_api_test.bluesky.BlueskyOperations;

import java.util.List;

import work.socialhub.kbsky.auth.BearerTokenAuthProvider;
import work.socialhub.kbsky.model.app.bsky.feed.FeedDefsFeedViewPost;

public class CrawlPostsTask extends AsyncTask<BearerTokenAuthProvider, Void, List<FeedDefsFeedViewPost>> {

    private static final String TAG = "CrawlPostsTask";
    private final BlueskyOperations blueskyOperations;

    private final Context context;

    public CrawlPostsTask(Context context, BlueskyOperations blueskyOperations) {
        this.context = context;
        this.blueskyOperations = blueskyOperations;
    }

    @Override
    protected List<FeedDefsFeedViewPost> doInBackground(BearerTokenAuthProvider... authProviders) {
        if (authProviders.length == 0 || authProviders[0] == null) {
            Log.e(TAG, "AuthProvider is null or not provided.");
            return null;
        }
        BearerTokenAuthProvider authProvider = authProviders[0];
        try {
            Log.d(TAG, "Fetching timeline posts...");
            return blueskyOperations.fetchTimeline(authProvider);
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch timeline posts", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<FeedDefsFeedViewPost> posts) {
        if (posts != null) {
            Log.d(TAG, "Successfully fetched " + posts.size() + " posts.");
            new SaveTimelinePostsTask(context).execute(posts);
        } else {
            Log.d(TAG, "No posts fetched or an error occurred.");
        }
    }
}
