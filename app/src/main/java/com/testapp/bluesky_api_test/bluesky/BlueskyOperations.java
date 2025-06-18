package com.testapp.bluesky_api_test.bluesky;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import work.socialhub.kbsky.Bluesky;
import work.socialhub.kbsky.BlueskyFactory;
import work.socialhub.kbsky.BlueskyTypes;
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
    
    //!!! Bluesky認証情報 - 必ず実際の値に置き換えてください!!!
    //!!! 注意: アプリ内に認証情報をハードコードするのはセキュリティ上非常に危険です。
    //!!! これはあくまでテスト用であり、実際の製品では絶対に行わないでください。
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
}