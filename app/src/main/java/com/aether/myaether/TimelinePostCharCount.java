package com.aether.myaether;

import work.socialhub.kbsky.Bluesky;
import work.socialhub.kbsky.BlueskyFactory;
import work.socialhub.kbsky.BlueskyTypes; // FeedPostの型比較用
import work.socialhub.kbsky.ATProtocolException; // エラーハンドリング用
import work.socialhub.kbsky.api.entity.com.atproto.server.ServerCreateSessionRequest;
import work.socialhub.kbsky.api.entity.com.atproto.server.ServerCreateSessionResponse;
import work.socialhub.kbsky.api.entity.app.bsky.feed.FeedGetTimelineRequest;
import work.socialhub.kbsky.api.entity.app.bsky.feed.FeedGetTimelineResponse;
import work.socialhub.kbsky.api.entity.share.Response; // APIレスポンスのラッパー
import work.socialhub.kbsky.auth.BearerTokenAuthProvider;
import work.socialhub.kbsky.model.app.bsky.feed.FeedDefsFeedViewPost; // タイムラインの投稿エントリ
import work.socialhub.kbsky.model.app.bsky.feed.FeedDefsPostView; // 投稿の詳細ビュー
import work.socialhub.kbsky.model.app.bsky.feed.FeedPost; // 実際の投稿内容
import work.socialhub.kbsky.model.share.RecordUnion; // 投稿レコードのコンテナ

public class TimelinePostCharCount {

    public static void main(String args) {
        // 1. Blueskyクライアントの初期化
        // 接続先のPDSを指定します。通常は "https://bsky.social" です。
        Bluesky bluesky = BlueskyFactory.INSTANCE.instance("https://bsky.social");

        //!!注意!!: 以下の認証情報を実際の値に置き換えてください。
        String userIdentifier = "YOUR_HANDLE_OR_EMAIL"; // あなたのBlueskyハンドルまたはメールアドレス
        String password = "YOUR_PASSWORD";           // あなたのBlueskyパスワード

        // ユーザー名とパスワードがデフォルト値のままの場合は警告して終了
        if ("YOUR_HANDLE_OR_EMAIL".equals(userIdentifier) || "YOUR_PASSWORD".equals(password)) {
            System.err.println("エラー: サンプルコード内の userIdentifier と password を実際の認証情報に置き換えてください。");
            return;
        }

        try {
            // 2. セッションを作成 (ログイン)
            ServerCreateSessionRequest sessionRequest = new ServerCreateSessionRequest();
            sessionRequest.setIdentifier(userIdentifier);
            sessionRequest.setPassword(password);

            System.out.println(userIdentifier + " としてログイン試行中...");
            Response<ServerCreateSessionResponse> sessionResponse = bluesky.server().createSession(sessionRequest);
            ServerCreateSessionResponse sessionData = sessionResponse.getData();
            System.out.println("ログイン成功。DID: " + sessionData.getDid());

            // 3. BearerTokenAuthProvider を作成
            // アクセストークンとリフレッシュトークンを使用して認証プロバイダを初期化します。
            // これにより、認証が必要なAPIリクエストに自動的に認証情報が付加されます。
            BearerTokenAuthProvider authProvider = new BearerTokenAuthProvider(
                    sessionData.getAccessJwt(),
                    sessionData.getRefreshJwt()
            );
            // BearerTokenAuthProviderのdidとpdsDidはJWTから自動的に読み込まれるため、
            // 通常は手動で設定する必要はありません。

            // 4. タイムライン取得リクエストを作成
            FeedGetTimelineRequest timelineRequest = new FeedGetTimelineRequest(authProvider);
            timelineRequest.setLimit(1); // タイムラインから最新の投稿を1件だけ取得

            System.out.println("タイムラインを取得中...");
            // 5. タイムラインを取得
            Response<FeedGetTimelineResponse> timelineResponse = bluesky.feed().getTimeline(timelineRequest);
            FeedGetTimelineResponse timelineData = timelineResponse.getData();

            // 6. 取得した投稿データを処理
            if (timelineData.getFeed()!= null &&!timelineData.getFeed().isEmpty()) {
                // 最初の投稿 (FeedDefsFeedViewPost) を取得
                FeedDefsFeedViewPost firstFeedViewPost = timelineData.getFeed().get(0);

                // FeedDefsFeedViewPost から投稿の詳細 (FeedDefsPostView) を取得
                FeedDefsPostView postView = firstFeedViewPost.getPost();

                if (postView!= null && postView.getRecord()!= null) {
                    // FeedDefsPostView から実際のレコード (RecordUnion) を取得
                    RecordUnion record = postView.getRecord();

                    // レコードの型が通常の投稿 (app.bsky.feed.post) であるかを確認
                    if (BlueskyTypes.FeedPost.equals(record.getType())) {
                        // RecordUnion を FeedPost 型にキャスト
                        FeedPost postContent = (FeedPost) record;
                        String postText = postContent.getText();

                        if (postText!= null) {
                            int charCount = postText.length();
                            System.out.println("\n取得した投稿:");
                            System.out.println("投稿者: @" + postView.getAuthor().getHandle());
                            System.out.println("投稿URI: " + postView.getUri());
                            System.out.println("テキスト: " + postText);
                            System.out.println("文字数: " + charCount);
                        } else {
                            System.out.println("投稿にテキストが含まれていません。");
                        }
                    } else {
                        System.out.println("取得した最初のタイムラインアイテムは通常の投稿ではありません。タイプ: " + record.getType());
                    }
                } else {
                    System.out.println("投稿データ (PostViewまたはRecord) が見つかりません。");
                }
            } else {
                System.out.println("タイムラインに投稿がありません。");
            }

        } catch (ATProtocolException e) {
            System.err.println("APIエラーが発生しました: " + e.getMessage());
            if (e.getResponse()!= null) {
                System.err.println("  エラーステータス: " + e.getStatus());
                System.err.println("  エラーコード: " + e.getResponse().getError());
                System.err.println("  エラー詳細: " + e.getResponse().messageForDisplay());
            }
            // デバッグ用にスタックトレースを出力したい場合はコメントを解除してください
            // e.printStackTrace();
        } catch (Exception e) {
            System.err.println("予期せぬエラーが発生しました: " + e.getMessage());
            e.printStackTrace();
        }
    }
}