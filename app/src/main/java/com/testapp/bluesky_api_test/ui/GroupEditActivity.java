package com.testapp.bluesky_api_test.ui;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.testapp.bluesky_api_test.R;
import com.testapp.bluesky_api_test.viewmodel.GroupEditViewModel;

import java.util.ArrayList;

/**
 * グループの編集画面を管理するActivity。
 * グループ名と、そのグループに属する投稿（メンバー）のリストを表示します。
 */
public class GroupEditActivity extends AppCompatActivity {

    private GroupEditViewModel groupEditViewModel;
    private TextView groupNameTextView;
    private RecyclerView groupMembersRecyclerView;
    private GroupMemberAdapter groupMemberAdapter;

    private RecyclerView groupAnnotationsRecyclerView;
    private GroupAnnotationAdapter groupAnnotationAdapter;

    private RecyclerView groupRefsRecyclerView;
    private GroupRefAdapter groupRefAdapter;

    private RecyclerView groupTagAssignmentsRecyclerView;
    private GroupTagAssignmentAdapter groupTagAssignmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);

        // UI要素の初期化
        groupNameTextView = findViewById(R.id.group_name_text_view);
        groupMembersRecyclerView = findViewById(R.id.group_members_recycler_view);
        groupAnnotationsRecyclerView = findViewById(R.id.group_annotations_recycler_view);
        groupRefsRecyclerView = findViewById(R.id.group_refs_recycler_view);
        groupTagAssignmentsRecyclerView = findViewById(R.id.group_tag_assignments_recycler_view);

        // RecyclerViewのセットアップ
        groupMembersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupMemberAdapter = new GroupMemberAdapter(new ArrayList<>());
        groupMembersRecyclerView.setAdapter(groupMemberAdapter);

        groupAnnotationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupAnnotationAdapter = new GroupAnnotationAdapter(new ArrayList<>());
        groupAnnotationsRecyclerView.setAdapter(groupAnnotationAdapter);

        groupRefsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupRefAdapter = new GroupRefAdapter(new ArrayList<>());
        groupRefsRecyclerView.setAdapter(groupRefAdapter);

        groupTagAssignmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupTagAssignmentAdapter = new GroupTagAssignmentAdapter(new ArrayList<>());
        groupTagAssignmentsRecyclerView.setAdapter(groupTagAssignmentAdapter);

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

        // ViewModelからのグループアノテーションデータの変更を監視し、UIを更新
        groupEditViewModel.getGroupAnnotations().observe(this, groupAnnotations -> {
            groupAnnotationAdapter = new GroupAnnotationAdapter(groupAnnotations);
            groupAnnotationsRecyclerView.setAdapter(groupAnnotationAdapter);
        });

        // ViewModelからのグループ参照データの変更を監視し、UIを更新
        groupEditViewModel.getGroupRefs().observe(this, groupRefs -> {
            groupRefAdapter = new GroupRefAdapter(groupRefs);
            groupRefsRecyclerView.setAdapter(groupRefAdapter);
        });

        // ViewModelからのグループタグ割り当てデータの変更を監視し、UIを更新
        groupEditViewModel.getGroupTagAssignments().observe(this, groupTagAssignments -> {
            groupTagAssignmentAdapter = new GroupTagAssignmentAdapter(groupTagAssignments);
            groupTagAssignmentsRecyclerView.setAdapter(groupTagAssignmentAdapter);
        });
    }
}