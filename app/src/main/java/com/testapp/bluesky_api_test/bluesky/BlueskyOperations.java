package com.testapp.bluesky_api_test.bluesky;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import work.socialhub.kbsky.Bluesky;
import work.socialhub.kbsky.BlueskyFactory;
import work.socialhub.kbsky.BlueskyTypes;
import work.socialhub.kbsky.api.entity.app.bsky.feed.FeedGetAuthorFeedRequest;
import work.socialhub.kbsky.api.entity.app.bsky.feed.FeedGetAuthorFeedResponse;
import work.socialhub.kbsky.api.entity.app.bsky.graph.GraphGetFollowsRequest;
import work.socialhub.kbsky.api.entity.app.bsky.graph.GraphGetFollowsResponse;
import work.socialhub.kbsky.ATProtocolException;
import work.socialhub.kbsky.api.entity.com.atproto.server.ServerCreateSessionRequest;
import work.socialhub.kbsky.api.entity.com.atproto.server.ServerCreateSessionResponse;
import work.socialhub.kbsky.api.entity.app.bsky.feed.FeedGetTimelineRequest;
import work.socialhub.kbsky.api.entity.app.bsky.feed.FeedGetTimelineResponse;
import work.socialhub.kbsky.api.entity.share.Response;
import work.socialhub.kbsky.auth.BearerTokenAuthProvider;
import work.socialhub.kbsky.model.app.bsky.feed.FeedDefsFeedViewPost;
import work.socialhub.kbsky.model.app.bsky.feed.FeedDefsPostView;
import work.socialhub.kbsky.model.app.bsky.feed.FeedPost;
import work.socialhub.kbsky.model.share.RecordUnion;
import work.socialhub.kbsky.model.app.bsky.actor.ActorDefsProfileView;


public class BlueskyOperations {
    private static final String TAG = "BlueskyOperations";
    private final Bluesky bluesky;
    public BlueskyOperations() {
        this.bluesky = BlueskyFactory.INSTANCE.instance("https://bsky.social");
    }
    

    /**
     * フォローリストからランダムに1人を選ぶメソッド。
     * @param authProvider 認証済みのAuthProvider（外部から渡される）
     * @param userDid 情報を取得したいユーザーのDID
     * @return 整形された結果文字列
     */
    public String fetchRandomFollowingUserHandle(BearerTokenAuthProvider authProvider, String userDid) {
        if (authProvider == null || userDid == null) {
            return "エラー: ログインしていません。";
        }
        try {
            List<ActorDefsProfileView> allFollows = new ArrayList<>();
            String cursor = null;

            Log.d(TAG, "フォローリストを取得中...");
            do {
                GraphGetFollowsRequest request = new GraphGetFollowsRequest(authProvider);
                request.setActor(userDid); // 引数で渡されたDIDを使用
                request.setLimit(100);
                request.setCursor(cursor);

                Response<GraphGetFollowsResponse> response = bluesky.graph().getFollows(request);
                if (response.getData().getFollows() != null) {
                    allFollows.addAll(response.getData().getFollows());
                }
                cursor = response.getData().getCursor();
            } while (cursor != null && !cursor.isEmpty());

            Log.d(TAG, "合計 " + allFollows.size() + " 人のフォローを取得しました。");

            if (allFollows.isEmpty()) {
                return "フォローしているユーザーがいません。";
            }

            Random random = new Random();
            ActorDefsProfileView randomUser = allFollows.get(random.nextInt(allFollows.size()));
            return "→ @" + randomUser.getHandle();

        } catch (Exception e) {
            Log.e(TAG, "フォローリストの取得に失敗", e);
            return "エラー: " + e.getMessage();
        }
    }

    /**
     * タイムラインの最初の投稿情報を取得するメソッド。
     * @param authProvider 認証済みのAuthProvider（外部から渡される）
     * @return 投稿情報オブジェクト
     */
    // BlueskyOperations.java

    public BlueskyPostInfo fetchFirstTimelinePostInfo(BearerTokenAuthProvider authProvider) {
        if (authProvider == null) {
            return new BlueskyPostInfo("エラー: ログインしていません。");
        }
        try {
            FeedGetTimelineRequest timelineRequest = new FeedGetTimelineRequest(authProvider);
            timelineRequest.setLimit(1);

            Response<FeedGetTimelineResponse> timelineResponse = bluesky.feed().getTimeline(timelineRequest);
            FeedGetTimelineResponse timelineData = timelineResponse.getData();

            if (timelineData.getFeed() != null && !timelineData.getFeed().isEmpty()) {
                FeedDefsFeedViewPost firstFeedViewPost = timelineData.getFeed().get(0);
                FeedDefsPostView postView = firstFeedViewPost.getPost();

                if (postView != null && postView.getRecord() != null) {
                    RecordUnion record = postView.getRecord();
                    if (BlueskyTypes.FeedPost.equals(record.getType())) {
                        FeedPost postContent = (FeedPost) record;
                        String postText = postContent.getText() != null ? postContent.getText() : "";
                        int charCount = postText.length();
                        String authorHandle = postView.getAuthor().getHandle();

                        // ★★★ ここでURIを取得します ★★★
                        String postUri = postView.getUri();

                        // ★★★ 新しいコンストラクタを呼び出します ★★★
                        return new BlueskyPostInfo(authorHandle, postUri, postText, charCount);
                    } else {
                        return new BlueskyPostInfo("取得したアイテムは通常の投稿ではありません。");
                    }
                }
            }
            return new BlueskyPostInfo("タイムラインに投稿がありません。");
        } catch (Exception e) {
            Log.e(TAG, "タイムライン取得エラー", e);
            return new BlueskyPostInfo("エラー: " + e.getMessage());
        }
    }

    /**
     * Blueskyからタイムラインの投稿情報を取得するメソッド。
     * @param authProvider 認証済みのAuthProvider（外部から渡される）
     * @return 投稿情報オブジェクトのリスト
     * @throws Exception API呼び出し中にエラーが発生した場合
     */
    public List<BlueskyPostInfo> fetchTimeline(BearerTokenAuthProvider authProvider) throws Exception {
        if (authProvider == null) {
            throw new IllegalStateException("ログインしていません。");
        }

        List<BlueskyPostInfo> timelinePosts = new ArrayList<>();
        String cursor = null;

        do {
            FeedGetTimelineRequest timelineRequest = new FeedGetTimelineRequest(authProvider);
            timelineRequest.setLimit(100); // 1度に取得する投稿数
            timelineRequest.setCursor(cursor);

            Response<FeedGetTimelineResponse> timelineResponse = bluesky.feed().getTimeline(timelineRequest);
            FeedGetTimelineResponse timelineData = timelineResponse.getData();

            if (timelineData.getFeed() != null) {
                for (FeedDefsFeedViewPost feedViewPost : timelineData.getFeed()) {
                    FeedDefsPostView postView = feedViewPost.getPost();
                    if (postView != null && postView.getRecord() != null) {
                        RecordUnion record = postView.getRecord();
                        if (BlueskyTypes.FeedPost.equals(record.getType())) {
                            FeedPost postContent = (FeedPost) record;
                            String postText = postContent.getText() != null ? postContent.getText() : "";
                            int charCount = postText.length();
                            String authorHandle = postView.getAuthor().getHandle();
                            String postUri = postView.getUri();
                            timelinePosts.add(new BlueskyPostInfo(authorHandle, postUri, postText, charCount));
                        }
                    }
                }
            }
            cursor = timelineData.getCursor();
        } while (cursor != null && !cursor.isEmpty());

        return timelinePosts;
    }
    public List<BlueskyPostInfo> fetchAuthorFeed(BearerTokenAuthProvider authProvider,String actorIdentifier) throws Exception {
        if(authProvider == null){
            throw new IllegalStateException("ログインしてないよ");
        }

        List<BlueskyPostInfo> authorPosts = new ArrayList<>();
        String cursor=null;

        do{
            FeedGetAuthorFeedRequest request = new FeedGetAuthorFeedRequest(authProvider);
                                     request.setActor(actorIdentifier);
                                     request.setLimit(100);
                                     request.setCursor(cursor);
            Response<FeedGetAuthorFeedResponse> response = bluesky.feed().getAuthorFeed(request);
            FeedGetAuthorFeedResponse authorFeedData = response.getData();

            if(authorFeedData.getFeed() != null) {
                for(FeedDefsFeedViewPost feedViewPost :authorFeedData.getFeed()){
                    FeedDefsPostView postView = feedViewPost.getPost();
                    if(postView != null && postView.getRecord() !=null){
                        RecordUnion record = postView.getRecord();
                        if(BlueskyTypes.FeedPost.equals(record.getType()))){
                            FeedPost postContent = (FeedPost) record;
                            String postText = postContent.getText() != null ? postContent.getText():"";
                            int charCount = postContent.getText().length();
                            String authorHandle = postView.getAuthor().getHandle();
                            String postUri = postView.getUri();
                            authorPosts.add(new BlueskyPostInfo(authorHandle,postUri,postText,charCount));

                        }
                    }
                }
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

            Log.d(TAG, "合計 " + allFollows.size() + " 人のフォローを取得しました。");
            return allFollows;

        } catch (Exception e) {
            Log.e(TAG, "フォローリストの取得に失敗", e);
            return new ArrayList<>();
        }
    }
}
