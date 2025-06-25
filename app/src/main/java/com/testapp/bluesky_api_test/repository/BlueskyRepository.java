package com.testapp.bluesky_api_test.repository;


import android.content.Context;

import com.testapp.bluesky_api_test.bluesky.BlueskyOperations;
import com.testapp.bluesky_api_test.bluesky.BlueskyPostInfo;

import work.socialhub.kbsky.auth.BearerTokenAuthProvider;

/**
 * アプリケーションのデータ全般へのアクセスを管理する「窓口」クラス。
 * データの取得元（DBかAPIか）を隠蔽する役割を持つ。
 */
public class BlueskyRepository {


    private final BlueskyOperations blueskyOperations;
    private final AuthRepository authRepository;
    public BlueskyRepository(Context context) {
        // DB担当とAPI担当のクラスを初期化して保持する


        this.blueskyOperations = new BlueskyOperations();
        this.authRepository = new AuthRepository(context);
    }

    /**
     * DBからアクセス履歴を取得するメソッド。
     * 内部でDatabaseOperationsを呼び出す。
     * @return DBの全アクセス履歴
     */

    private BearerTokenAuthProvider getLoginAuthProvider() {
        BearerTokenAuthProvider authProvider = authRepository.getAuthProvider();
        if (authProvider == null) {
            throw new IllegalStateException("ログインしていません。");
        }
        return authProvider;
    }
    /**
     * Blueskyからランダムなフォローユーザーの情報を取得するメソッド。
     * 内部でBlueskyOperationsを呼び出す。
     * @return ランダムなユーザー情報を含む文字列
     */
    public String getRandomFollowingUserInfo() {
        BearerTokenAuthProvider authProvider = getLoginAuthProvider();
        String did = authRepository.getDid();
        return blueskyOperations.fetchRandomFollowingUserHandle(authProvider, did);
    }

    /**
     * Blueskyからタイムラインの最初の投稿情報を取得するメソッド。
     * 内部でBlueskyOperationsを呼び出す。
     * @return 投稿情報オブジェクト
     */
    public BlueskyPostInfo getFirstTimelinePostInfo() {
        BearerTokenAuthProvider authProvider = getLoginAuthProvider();
        return blueskyOperations.fetchFirstTimelinePostInfo(authProvider);
    }
}