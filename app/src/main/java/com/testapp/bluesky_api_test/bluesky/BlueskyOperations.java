package com.testapp.bluesky_api_test.bluesky;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

import work.socialhub.kbsky.Bluesky;
import work.socialhub.kbsky.BlueskyFactory;
import work.socialhub.kbsky.api.entity.app.bsky.feed.FeedGetAuthorFeedRequest;
import work.socialhub.kbsky.api.entity.app.bsky.feed.FeedGetAuthorFeedResponse;
import work.socialhub.kbsky.api.entity.app.bsky.graph.GraphGetFollowsRequest;
import work.socialhub.kbsky.api.entity.app.bsky.graph.GraphGetFollowsResponse;
import work.socialhub.kbsky.api.entity.app.bsky.feed.FeedGetTimelineRequest;
import work.socialhub.kbsky.api.entity.app.bsky.feed.FeedGetTimelineResponse;
import work.socialhub.kbsky.api.entity.share.Response;
import work.socialhub.kbsky.auth.AuthProvider;
import work.socialhub.kbsky.auth.BearerTokenAuthProvider;
import work.socialhub.kbsky.model.app.bsky.actor.ActorDefsProfileView;
import work.socialhub.kbsky.model.app.bsky.feed.FeedDefsFeedViewPost;


public class BlueskyOperations {
    private static final String TAG = "BlueskyOperations";
    private final Bluesky bluesky;
    public BlueskyOperations() {
        this.bluesky = BlueskyFactory.INSTANCE.instance("https://bsky.social");
    }

    /**
     * Blueskyからタイムラインの投稿情報を取得するメソッド。
     * @param authProvider 認証済みのAuthProvider（外部から渡される）
     * @return 投稿情報オブジェクトのリスト
     * @throws Exception API呼び出し中にエラーが発生した場合
     */
    public List<FeedDefsFeedViewPost> fetchTimeline(AuthProvider authProvider) throws Exception {
        if (authProvider == null) {
            throw new IllegalStateException("ログインしていません。");
        }

        List<FeedDefsFeedViewPost> timelinePosts = new ArrayList<>();
        String cursor = null;

        do {
            FeedGetTimelineRequest timelineRequest = new FeedGetTimelineRequest(authProvider);
            timelineRequest.setLimit(100); // 1度に取得する投稿数
            timelineRequest.setCursor(cursor);

            Response<FeedGetTimelineResponse> timelineResponse = bluesky.feed().getTimeline(timelineRequest);
            FeedGetTimelineResponse timelineData = timelineResponse.getData();

            if (timelineData.getFeed() != null) {
                timelinePosts.addAll(timelineData.getFeed());
            }
            cursor = timelineData.getCursor();
        } while (cursor != null && !cursor.isEmpty());

        return timelinePosts;
    }

    /**
     * Blueskyから特定のユーザーの投稿情報を取得するメソッド。
     * @param authProvider 認証済みのAuthProvider（外部から渡される）
     * @param actorIdentifier ユーザーの識別子（DIDまたはハンドル）
     * @return 投稿情報オブジェクトのリスト
     * @throws Exception API呼び出し中にエラーが発生した場合
     */
    public List<FeedDefsFeedViewPost> fetchAuthorFeed(AuthProvider authProvider,String actorIdentifier) throws Exception {
        if(authProvider == null){
            throw new IllegalStateException("ログインしてないよ");
        }

        List<FeedDefsFeedViewPost> authorPosts = new ArrayList<>();
        String cursor=null;

        do{
            FeedGetAuthorFeedRequest request = new FeedGetAuthorFeedRequest(authProvider);
                                     request.setActor(actorIdentifier);
                                     request.setLimit(100);
                                     request.setCursor(cursor);
            Response<FeedGetAuthorFeedResponse> response = bluesky.feed().getAuthorFeed(request);
            FeedGetAuthorFeedResponse authorFeedData = response.getData();

            if(authorFeedData.getFeed() != null) {
                authorPosts.addAll(authorFeedData.getFeed());
            }
            cursor = authorFeedData.getCursor();
        }while (cursor != null && !cursor.isEmpty());
        return authorPosts;
    }

    /**
     * フォローしている全ユーザーのリストを取得するメソッド。
     * @param authProvider 認証済みのAuthProvider（外部から渡される）
     * @param userDid 情報を取得したいユーザーのDID
     * @return フォローしているユーザーのActorDefsProfileViewリスト
     */
    public List<ActorDefsProfileView> fetchAllFollowingUsers(AuthProvider authProvider, String userDid) {
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
}
