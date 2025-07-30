package com.testapp.bluesky_api_test.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.testapp.bluesky_api_test.bluesky.BlueskyPostInfo;
import com.testapp.bluesky_api_test.data.source.remote.BlueskyRemoteDataSource;
import com.testapp.bluesky_api_test.data.source.remote.BlueskyRemoteDataSourceImpl;
import work.socialhub.kbsky.auth.BearerTokenAuthProvider;

import java.util.List;

public class CrawlPostsTask extends AsyncTask<BearerTokenAuthProvider, Void, List<BlueskyPostInfo>> {

    private static final String TAG = "CrawlPostsTask";
    private final BlueskyRemoteDataSource blueskyRemoteDataSource;

    private final Context context;

    public CrawlPostsTask(Context context) {
        this.context = context;
        this.blueskyRemoteDataSource = new BlueskyRemoteDataSourceImpl();
    }

    @Override
    protected List<BlueskyPostInfo> doInBackground(BearerTokenAuthProvider... authProviders) {
        if (authProviders.length == 0 || authProviders[0] == null) {
            Log.e(TAG, "AuthProvider is null or not provided.");
            return null;
        }
        BearerTokenAuthProvider authProvider = authProviders[0];
        try {
            Log.d(TAG, "Fetching timeline posts...");
            return blueskyRemoteDataSource.fetchTimeline(authProvider);
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch timeline posts", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<BlueskyPostInfo> posts) {
        if (posts != null) {
            Log.d(TAG, "Successfully fetched " + posts.size() + " posts.");
            new SaveTimelinePostsTask(context).execute(posts);
        } else {
            Log.d(TAG, "No posts fetched or an error occurred.");
        }
    }
}
