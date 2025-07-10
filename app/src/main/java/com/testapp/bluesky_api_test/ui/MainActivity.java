package com.testapp.bluesky_api_test.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.testapp.bluesky_api_test.R;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupEntity;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupMember;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupAnnotation;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupRef;
import com.testapp.bluesky_api_test.repository.GroupEntityRepository;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.BasePostDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.GroupMemberDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.GroupAnnotationDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.GroupRefDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabaseSingleton;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_FIRST_RUN = "first_run";

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

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
                R.id.navigation_home, R.id.navigation_notifications, R.id.navigation_profile)
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

        // グループ画面への遷移処理
        Button buttonToGroup = findViewById(R.id.button_to_group);
        buttonToGroup.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GroupActivity.class);
            startActivity(intent);
        });

        // 初回起動時にダミーデータを挿入

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        //if (prefs.getBoolean(PREF_FIRST_RUN, true)) {
        if (true) { // 強制的にtrueにして毎回データ挿入を実行
            insertInitialData();
            prefs.edit().putBoolean(PREF_FIRST_RUN, false).apply();
        }

    }

    private void insertInitialData() {
        executorService.execute(() -> {
            try {
                Log.d("MainActivity", "Starting initial data insertion.");
                GroupEntityRepository groupEntityRepository = new GroupEntityRepository(getApplication());
                BasePostDao basePostDao = AppDatabaseSingleton.getInstance(getApplication()).basePostDao();
                GroupMemberDao groupMemberDao = AppDatabaseSingleton.getInstance(getApplication()).groupMemberDao();
                GroupAnnotationDao groupAnnotationDao = AppDatabaseSingleton.getInstance(getApplication()).groupAnnotationDao();
                GroupRefDao groupRefDao = AppDatabaseSingleton.getInstance(getApplication()).groupRefDao();

                // 既存データをクリア（テスト用）
                Log.d("MainActivity", "Deleting existing data.");
                groupEntityRepository.deleteAllGroups();
                basePostDao.deleteAll();
                groupMemberDao.deleteAll();
                groupAnnotationDao.deleteAll();
                groupRefDao.deleteAll();
                Log.d("MainActivity", "Existing data deleted.");

                // GroupEntity の挿入
                GroupEntity group1 = new GroupEntity(1, "Group1", "2025-07-09");
                groupEntityRepository.insert(group1);
                Log.d("MainActivity", "Inserted Group1.");

                // BasePost の挿入
                BasePost post11 = new BasePost("uri_post11", "cid_post11", 1, 1, "Post11", "2025-07-09T10:00:00Z");
                BasePost post8 = new BasePost("uri_post8", "cid_post8", 1, 1, "Post8 (rewritten)", "2025-07-09T10:05:00Z");
                BasePost post37 = new BasePost("uri_post37", "cid_post37", 1, 1, "Post37", "2025-07-09T10:10:00Z");
                BasePost post20 = new BasePost("uri_post20", "cid_post20", 1, 1, "Post20", "2025-07-09T10:15:00Z");

                basePostDao.insertAll(Arrays.asList(post11, post8, post37, post20));
                Log.d("MainActivity", "Inserted BasePosts.");

                // GroupMember の挿入 (Group1 に Post を関連付け)
                groupMemberDao.insert(new GroupMember(group1.getId(), post11.getId(), 0));
                groupMemberDao.insert(new GroupMember(group1.getId(), post8.getId(), 1));
                groupMemberDao.insert(new GroupMember(group1.getId(), post37.getId(), 2));
                groupMemberDao.insert(new GroupMember(group1.getId(), post20.getId(), 4)); // appendix of post37 のために順序を空ける
                Log.d("MainActivity", "Inserted GroupMembers.");

                // GroupAnnotation の挿入 (appendix of post37)
                GroupAnnotation appendix37 = new GroupAnnotation(group1.getId(), "appendix of post37", "This is an appendix for Post37.", post37.getId());
                groupAnnotationDao.insert(appendix37);
                Log.d("MainActivity", "Inserted GroupAnnotation.");

                // GroupRef の挿入 (Reference materials 1)
                GroupRef ref1 = new GroupRef(group1.getId(), "Reference materials 1", "document", "/path/to/ref1.pdf", 5);
                groupRefDao.insert(ref1);
                Log.d("MainActivity", "Inserted GroupRef.");

                Log.d("MainActivity", "Finished initial data insertion successfully.");
            } catch (Exception e) {
                Log.e("MainActivity", "Error inserting initial data", e);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
