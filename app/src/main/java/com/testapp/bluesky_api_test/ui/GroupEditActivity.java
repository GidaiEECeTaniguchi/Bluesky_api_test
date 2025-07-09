package com.testapp.bluesky_api_test.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.Nullable;

import com.testapp.bluesky_api_test.R;
import com.testapp.bluesky_api_test.viewmodel.GroupEditViewModel;
import com.testapp.bluesky_api_test.ui.GroupMemberAdapter;
import com.testapp.bluesky_api_test.ui.GroupAnnotationAdapter;
import com.testapp.bluesky_api_test.ui.GroupRefAdapter;
import com.testapp.bluesky_api_test.ui.GroupTagAssignmentAdapter;
import com.testapp.bluesky_api_test.ui.PostSelectActivity;
import com.testapp.bluesky_api_test.ui.RefSelectActivity;

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

    private LinearLayout editButtonsContainer;
    private Button addPostsButton;
    private Button addRefsButton;

    private boolean isEditMode = false;
    private int currentGroupId = -1; // 現在のグループIDを保持

    private ActivityResultLauncher<Intent> selectPostLauncher;
    private ActivityResultLauncher<Intent> selectRefLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // UI要素の初期化
        groupNameTextView = findViewById(R.id.group_name_text_view);
        groupMembersRecyclerView = findViewById(R.id.group_members_recycler_view);
        groupAnnotationsRecyclerView = findViewById(R.id.group_annotations_recycler_view);
        groupRefsRecyclerView = findViewById(R.id.group_refs_recycler_view);
        groupTagAssignmentsRecyclerView = findViewById(R.id.group_tag_assignments_recycler_view);

        editButtonsContainer = findViewById(R.id.edit_buttons_container);
        addPostsButton = findViewById(R.id.add_posts_button);
        addRefsButton = findViewById(R.id.add_refs_button);

        // ActivityResultLauncherの初期化
        selectPostLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        int selectedPostId = result.getData().getIntExtra(PostSelectActivity.EXTRA_SELECTED_POST_ID, -1);
                        if (selectedPostId != -1 && currentGroupId != -1) {
                            groupEditViewModel.addGroupMember(currentGroupId, selectedPostId);
                            Toast.makeText(this, "Post added: " + selectedPostId, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to add post.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        selectRefLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        int selectedRefId = result.getData().getIntExtra(RefSelectActivity.EXTRA_SELECTED_REF_ID, -1);
                        if (selectedRefId != -1 && currentGroupId != -1) {
                            groupEditViewModel.addRefToGroup(currentGroupId, selectedRefId);
                            Toast.makeText(this, "Reference added: " + selectedRefId, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to add reference.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        addPostsButton.setOnClickListener(v -> {
            Intent intent = new Intent(GroupEditActivity.this, PostSelectActivity.class);
            selectPostLauncher.launch(intent);
        });

        addRefsButton.setOnClickListener(v -> {
            Intent intent = new Intent(GroupEditActivity.this, RefSelectActivity.class);
            selectRefLauncher.launch(intent);
        });

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

        // 現在のグループIDを保存
        currentGroupId = groupId;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            toggleEditMode();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;
        if (isEditMode) {
            editButtonsContainer.setVisibility(View.VISIBLE);
        } else {
            editButtonsContainer.setVisibility(View.GONE);
        }
        // TODO: 各Adapterに編集モードの変更を通知する
        groupMemberAdapter.setEditMode(isEditMode);
        groupAnnotationAdapter.setEditMode(isEditMode);
        groupRefAdapter.setEditMode(isEditMode);
        groupTagAssignmentAdapter.setEditMode(isEditMode);
    }
}