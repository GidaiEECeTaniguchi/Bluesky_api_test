package com.testapp.bluesky_api_test.bluesky;

import android.util.Log;

import work.socialhub.kbsky.Bluesky;
import work.socialhub.kbsky.BlueskyFactory;
import work.socialhub.kbsky.BlueskyTypes;
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

public class BlueskyOperations {

    private static final String TAG = "BlueskyOperations";

    //!!! Bluesky認証情報 - 必ず実際の値に置き換えてください!!!
    //!!! 注意: アプリ内に認証情報をハードコードするのはセキュリティ上非常に危険です。
    //!!! これはあくまでテスト用であり、実際の製品では絶対に行わないでください。
    private final String blueskyHandle = "YOUR_HANDLE_OR_EMAIL";
    private final String blueskyPassword = "YOUR_PASSWORD";

    public BlueskyPostInfo fetchFirstTimelinePostInfo() {
        if ("YOUR_HANDLE_OR_EMAIL".equals(blueskyHandle) || "YOUR_PASSWORD".equals(blueskyPassword)) {
            Log.e(TAG, "Bluesky認証情報が設定されていません。");
            return new BlueskyPostInfo("エラー: Blueskyの認証情報をコード内で設定してください。");
        }

        try {
            Bluesky bluesky = BlueskyFactory.INSTANCE.instance("https://bsky.social"); // [1]

            // 1. セッションを作成 (ログイン)
            ServerCreateSessionRequest sessionRequest = new ServerCreateSessionRequest(); // [1]
            sessionRequest.setIdentifier(blueskyHandle);
            sessionRequest.setPassword(blueskyPassword);

            Log.d(TAG, blueskyHandle + " としてログイン試行中...");
            Response<ServerCreateSessionResponse> sessionResponse = bluesky.server().createSession(sessionRequest); // [1]
            ServerCreateSessionResponse sessionData = sessionResponse.getData(); // [1]
            Log.d(TAG, "ログイン成功。DID: " + sessionData.getDid());

            // 2. BearerTokenAuthProvider を作成
            BearerTokenAuthProvider authProvider = new BearerTokenAuthProvider( // [1]
                    sessionData.getAccessJwt(),
                    sessionData.getRefreshJwt()
            );

            // 3. タイムライン取得リクエストを作成
            FeedGetTimelineRequest timelineRequest = new FeedGetTimelineRequest(authProvider); // [1]
            timelineRequest.setLimit(1); // タイムラインから最新の投稿を1件だけ取得

            Log.d(TAG, "タイムラインを取得中...");
            // 4. タイムラインを取得
            Response<FeedGetTimelineResponse> timelineResponse = bluesky.feed().getTimeline(timelineRequest); // [1]
            FeedGetTimelineResponse timelineData = timelineResponse.getData(); // [1]

            // 5. 取得した投稿データを処理
            if (timelineData.getFeed()!= null &&!timelineData.getFeed().isEmpty()) {
                FeedDefsFeedViewPost firstFeedViewPost = timelineData.getFeed().get(0); // [1]
                FeedDefsPostView postView = firstFeedViewPost.getPost(); // [1]

                if (postView!= null && postView.getRecord()!= null) {
                    RecordUnion record = postView.getRecord(); // [1]
                    if (BlueskyTypes.FeedPost.equals(record.getType())) { // [1]
                        FeedPost postContent = (FeedPost) record; // [1]
                        String postText = postContent.getText();

                        if (postText!= null) {
                            int charCount = postText.length();
                            Log.d(TAG, "投稿取得成功: " + postText.substring(0, Math.min(postText.length(), 20)) + "..., 文字数: " + charCount);
                            return new BlueskyPostInfo(postView.getAuthor().getHandle(), postView.getUri(), postText, charCount);
                        } else {
                            Log.d(TAG, "投稿にテキストなし URI: " + postView.getUri());
                            return new BlueskyPostInfo("取得した投稿にテキストが含まれていません。");
                        }
                    } else {
                        Log.d(TAG, "通常の投稿ではないアイテム URI: " + postView.getUri() + ", Type: " + record.getType());
                        return new BlueskyPostInfo("取得した最初のタイムラインアイテムは通常の投稿ではありません。タイプ: " + record.getType());
                    }
                } else {
                    Log.w(TAG, "PostViewまたはRecordがnullです。");
                    return new BlueskyPostInfo("投稿データ (PostViewまたはRecord) が見つかりません。");
                }
            } else {
                Log.d(TAG, "タイムラインに投稿なし。");
                return new BlueskyPostInfo("タイムラインに投稿がありません。");
            }

        } catch (ATProtocolException e) { // [1]
            Log.e(TAG, "Bluesky APIエラー: " + e.getMessage(), e);
            String errorMessage = "Bluesky APIエラー: " + e.getMessage();
            if (e.getResponse()!= null) { // [1]
                errorMessage += "\n  ステータス: " + e.getStatus() +
                        "\n  エラーコード: " + e.getResponse().getError() +
                        "\n  詳細: " + e.getResponse().messageForDisplay();
            }
            return new BlueskyPostInfo(errorMessage);
        } catch (Exception e) {
            Log.e(TAG, "予期せぬBluesky処理エラー: " + e.getMessage(), e);
            return new BlueskyPostInfo("予期せぬBluesky処理エラー: " + e.getMessage());
        }
    }
}