package com.testapp.bluesky_api_test.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Fragmentの基本的な初期化処理を定義するインターフェース
 */
public interface BaseFragmentInterface {

    /**
     * Viewの初期化処理をここに書く
     * (findViewById, ViewBindingの初期化など)
     */
    void initViews(@NonNull View view, @Nullable Bundle savedInstanceState);

    /**
     * ViewModelのLiveDataの監視設定をここに書く
     */
    void initObservers();

    /**
     * ボタンなどのクリックリスナーの設定をここに書く
     */
    void initListeners();

    /**
     * 画面表示時に必要な最初のデータ読み込み処理をここに書く
     */
    void loadData();
}
