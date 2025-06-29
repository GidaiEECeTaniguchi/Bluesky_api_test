package com.testapp.bluesky_api_test.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.testapp.bluesky_api_test.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ステップ1で修正したレイアウトファイルをセット
        setContentView(R.layout.activity_main);
        // ✅ ToolbarをActionBarとして設定
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // 各メニューIDをトップレベルの遷移先として設定します。
        // これにより、戻るボタンの挙動が適切に管理されます。
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_notifications, R.id.navigation_profile, R.id.navigation_group_list)
                .build();

        // Fragmentの表示領域(nav_host_fragment)を管理するNavControllerを取得

        // Navigation.findNavController(this, R.id.nav_host_fragment_activity_home) を使わずに：
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_home);
        NavController navController = navHostFragment.getNavController();

        // ActionBar（画面上部のバー）とNavControllerを連携
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // BottomNavigationViewとNavControllerを連携
        NavigationUI.setupWithNavController(navView, navController);
    }
}