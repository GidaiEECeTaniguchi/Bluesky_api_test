package com.testapp.bluesky_api_test.data.source.remote;

import android.util.Log;
import com.testapp.bluesky_api_test.bluesky.BlueskyPostInfo;
import work.socialhub.kbsky.Bluesky;
import work.socialhub.kbsky.BlueskyFactory;
import work.socialhub.kbsky.api.entity.app.bsky.feed.FeedGetAuthorFeedRequest;
import work.socialhub.kbsky.api.entity.app.bsky.feed.FeedGetAuthorFeedResponse;
import work.socialhub.kbsky.api.entity.app.bsky.feed.FeedGetTimelineRequest;
import work.socialhub.kbsky.api.entity.app.bsky.feed.FeedGetTimelineResponse;
import work.socialhub.kbsky.api.entity.app.bsky.graph.GraphGetFollowsRequest;
import work.socialhub.kbsky.api.entity.app.bsky.graph.GraphGetFollowsResponse;
import work.socialhub.kbsky.api.entity.share.Response;
import work.socialhub.kbsky.auth.BearerTokenAuthProvider;
import work.socialhub.kbsky.model.app.bsky.actor.ActorDefsProfileView;
import work.socialhub.kbsky.model.app.bsky.feed.FeedDefsFeedViewPost;
import work.socialhub.kbsky.model.app.bsky.feed.FeedDefsPostView;
import work.socialhub.kbsky.model.app.bsky.feed.FeedPost;
import work.socialhub.kbsky.model.share.RecordUnion;
import work.socialhub.kbsky.BlueskyTypes;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class BlueskyRemoteDataSourceImpl implements BlueskyRemoteDataSource {

    private static final String TAG = "BlueskyRemoteDataSourceImpl";
    private final Bluesky bluesky;


    public BlueskyRemoteDataSourceImpl() {
        this.bluesky = BlueskyFactory.INSTANCE.instance("https://bsky.social");
    }

    @Override
    public List<BlueskyPostInfo> fetchTimeline(BearerTokenAuthProvider authProvider) throws Exception {
        if (authProvider == null) {
            throw new IllegalStateException("ログインしていません。");
        }

        List<FeedDefsFeedViewPost> feedViewPosts = new ArrayList<>();
        String cursor = null;

        do {
            FeedGetTimelineRequest timelineRequest = new FeedGetTimelineRequest(authProvider);
            timelineRequest.setLimit(100);
            timelineRequest.setCursor(cursor);

            Response<FeedGetTimelineResponse> timelineResponse = bluesky.feed().getTimeline(timelineRequest);
            FeedGetTimelineResponse timelineData = timelineResponse.getData();

            if (timelineData.getFeed() != null) {
                feedViewPosts.addAll(timelineData.getFeed());
            }
            cursor = timelineData.getCursor();
        } while (cursor != null && !cursor.isEmpty());

        return convertFeedViewPostsToBlueskyPostInfo(feedViewPosts);
    }

    @Override
    public List<BlueskyPostInfo> fetchAuthorFeed(BearerTokenAuthProvider authProvider, String actorIdentifier) throws Exception {
        if (authProvider == null) {
            throw new IllegalStateException("ログインしてないよ");
        }

        List<FeedDefsFeedViewPost> feedViewPosts = new ArrayList<>();
        String cursor = null;

        do {
            FeedGetAuthorFeedRequest request = new FeedGetAuthorFeedRequest(authProvider);
            request.setActor(actorIdentifier);
            request.setLimit(100);
            request.setCursor(cursor);
            Response<FeedGetAuthorFeedResponse> response = bluesky.feed().getAuthorFeed(request);
            FeedGetAuthorFeedResponse authorFeedData = response.getData();

            if (authorFeedData.getFeed() != null) {
                feedViewPosts.addAll(authorFeedData.getFeed());
            }
            cursor = authorFeedData.getCursor();
        } while (cursor != null && !cursor.isEmpty());

        return convertFeedViewPostsToBlueskyPostInfo(feedViewPosts);
    }

    @Override
    public List<ActorDefsProfileView> fetchAllFollowingUsers(BearerTokenAuthProvider authProvider, String userDid) {
        if (authProvider == null || userDid == null) {
            Log.e(TAG, "エラー: ログインしていません。またはユーザーDIDが指定されていません。");
            return new ArrayList<>();
        }
        try {
            List<ActorDefsProfileView> allFollows = new ArrayList<>();
            String cursor = null;

            Log.d(TAG, "フォローリストを取得中...");
            do {
                GraphGetFollowsRequest request = new GraphGetFollowsRequest(authProvider);
                request.setActor(userDid);
                request.setLimit(100);
                request.setCursor(cursor);

                Response<GraphGetFollowsResponse> response = bluesky.graph().getFollows(request);
                if (response.getData().getFollows() != null) {
                    allFollows.addAll(response.getData().getFollows());
                }
                cursor = response.getData().getCursor();
            } while (cursor != null && !cursor.isEmpty());

            Log.d(TAG, "合計 " + allFollows.size() + " 人のフォローを取得しました。アクターのリストを返します。");
            return allFollows;

        } catch (Exception e) {
            Log.e(TAG, "フォローリストの取得に失敗", e);
            return new ArrayList<>();
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
}
