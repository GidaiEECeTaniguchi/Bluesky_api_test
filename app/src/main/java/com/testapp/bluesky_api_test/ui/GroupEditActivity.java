package com.testapp.bluesky_api_test.ui;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.testapp.bluesky_api_test.R;
import com.testapp.bluesky_api_test.viewmodel.GroupEditViewModel;

/**
 * グループの編集画面を管理するActivity。
 * グループ名と、そのグループに属する投稿（メンバー）のリストを表示します。
 */
public class GroupEditActivity extends AppCompatActivity {

    private GroupEditViewModel groupEditViewModel;
    private TextView groupNameTextView;
    private RecyclerView groupMembersRecyclerView;
    private GroupMemberAdapter groupMemberAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);

        // UI要素の初期化
        groupNameTextView = findViewById(R.id.group_name_text_view);
        groupMembersRecyclerView = findViewById(R.id.group_members_recycler_view);

        // RecyclerViewのセットアップ
        groupMembersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupMemberAdapter = new GroupMemberAdapter(new java.util.ArrayList<>());
        groupMembersRecyclerView.setAdapter(groupMemberAdapter);

        // ViewModelの初期化
        groupEditViewModel = new ViewModelProvider(this).get(GroupEditViewModel.class);

        // IntentからグループIDとグループ名を取得
        int groupId = getIntent().getIntExtra("group_id", -1);
        String groupName = getIntent().getStringExtra("group_name");

        // グループIDが有効な場合、データをロードして表示
        if (groupId != -1) {
            groupNameTextView.setText(groupName);
            groupEditViewModel.loadGroupAndMembers(groupId);
        }

        // ViewModelからのグループデータの変更を監視し、UIを更新
        groupEditViewModel.getGroup().observe(this, groupEntity -> {
            if (groupEntity != null) {
                groupNameTextView.setText(groupEntity.getName());
            }
        });

        // ViewModelからのグループメンバー（投稿）データの変更を監視し、UIを更新
        groupEditViewModel.getGroupMembers().observe(this, basePosts -> {
            groupMemberAdapter = new GroupMemberAdapter(basePosts);
            groupMembersRecyclerView.setAdapter(groupMemberAdapter);
        });
    }
}