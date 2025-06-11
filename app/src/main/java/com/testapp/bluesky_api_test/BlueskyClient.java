package com.testapp.bluesky_api_test;

import work.socialhub.kbsky.Bluesky;
import work.socialhub.kbsky.BlueskyConfig;
import work.socialhub.kbsky.BlueskyFactory;

/**
 * BlueskyClient は kbsky の Factory メソッドをラップして、
 * AT Protocol の Bluesky インスタンスをアプリ全体で保持します。
 */
public class BlueskyClient {

    private static BlueskyClient instance;
    private final Bluesky bluesky;

    /**
     * コンストラクタはシングルトンにし、他所から直接インスタンス化できないようにします。
     * @param pdsUri 例: "https://bsky.social" など
     */
    private BlueskyClient(String pdsUri) {
        BlueskyConfig config = new BlueskyConfig();
        config.setPdsUri(pdsUri);
        this.bluesky = BlueskyFactory.INSTANCE.instance(config);
    }

    /**
     * シングルトン取得用メソッド。最初に PDS URI を渡して初期化してください。
     */
    public static synchronized BlueskyClient init(String pdsUri) {
        if (instance == null) {
            instance = new BlueskyClient(pdsUri);
        }
        return instance;
    }

    /**
     * すでに初期化済みのシングルトンインスタンスを返します。
     * init(...) を先に呼び出していない場合、例外をスローします。
     */
    public static BlueskyClient getInstance() {
        if (instance == null) {
            throw new IllegalStateException("BlueskyClient is not initialized. Call init(pdsUri) first.");
        }
        return instance;
    }

    /**
     * 実際の Bluesky オブジェクトを返します。
     * API 呼び出しの際に、AuthProvider とともに必要です。
     */
    public Bluesky getBluesky() {
        return bluesky;
    }
}
