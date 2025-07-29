package com.testapp.bluesky_api_test.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
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
import androidx.fragment.app.DialogFragment;
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

import java.util.ArrayList;
import java.util.List;

public class GroupEditActivity extends AppCompatActivity implements GroupMemberAdapter.OnEditButtonClickListener, TagSelectionDialogFragment.TagSelectionListener, TagSelectionDialogFragment.OnAddNewTagRequestListener, AddNewTagDialogFragment.AddNewTagDialogListener {

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
    private int currentGroupId = -1;

    private ActivityResultLauncher<Intent> selectPostLauncher;
    private ActivityResultLauncher<String[]> openFileLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("グループ編集");
        groupNameTextView = findViewById(R.id.group_name_text_view);
        groupMembersRecyclerView = findViewById(R.id.group_members_recycler_view);
        groupAnnotationsRecyclerView = findViewById(R.id.group_annotations_recycler_view);
        groupRefsRecyclerView = findViewById(R.id.group_refs_recycler_view);
        groupTagAssignmentsRecyclerView = findViewById(R.id.group_tag_assignments_recycler_view);

        editButtonsContainer = findViewById(R.id.edit_buttons_container);
        addPostsButton = findViewById(R.id.add_posts_button);
        addRefsButton = findViewById(R.id.add_refs_button);

        setupLaunchers();
        setupButtons();
        setupRecyclerViews();

        groupEditViewModel = new ViewModelProvider(this).get(GroupEditViewModel.class);

        int groupId = getIntent().getIntExtra("group_id", -1);
        String groupName = getIntent().getStringExtra("group_name");

        currentGroupId = groupId;

        if (groupId != -1) {
            groupNameTextView.setText(groupName);
            groupEditViewModel.loadGroupAndMembers(groupId);
        }

        observeViewModel();
    }

    private void setupLaunchers() {
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

        openFileLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        String fileName = "Unknown";
                        try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                            if (cursor != null && cursor.moveToFirst()) {
                                int nameIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME);
                                if (nameIndex != -1) {
                                    fileName = cursor.getString(nameIndex);
                                }
                            }
                        }

                        groupEditViewModel.addNewRefToGroup(currentGroupId, fileName, uri.toString());
                        Toast.makeText(this, "Reference added: " + fileName, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to get file.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void setupButtons() {
        addPostsButton.setOnClickListener(v -> {
            Intent intent = new Intent(GroupEditActivity.this, PostSelectActivity.class);
            selectPostLauncher.launch(intent);
        });

        addRefsButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            openFileLauncher.launch(new String[]{"*/*"});
        });
    }

    private void setupRecyclerViews() {
        groupMembersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupMemberAdapter = new GroupMemberAdapter(new ArrayList<>());
        groupMemberAdapter.setOnEditButtonClickListener(this);
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
    }

    private void observeViewModel() {
        groupEditViewModel.getGroup().observe(this, groupEntity -> {
            if (groupEntity != null) {
                groupNameTextView.setText(groupEntity.getName());
            }
        });

        groupEditViewModel.getGroupMembers().observe(this, basePosts -> {
            groupMemberAdapter = new GroupMemberAdapter(basePosts);
            groupMemberAdapter.setOnEditButtonClickListener(this);
            groupMembersRecyclerView.setAdapter(groupMemberAdapter);
        });

        groupEditViewModel.getGroupAnnotations().observe(this, groupAnnotations -> {
            groupAnnotationAdapter = new GroupAnnotationAdapter(groupAnnotations);
            groupAnnotationsRecyclerView.setAdapter(groupAnnotationAdapter);
        });

        groupEditViewModel.getGroupRefs().observe(this, groupRefs -> {
            groupRefAdapter = new GroupRefAdapter(groupRefs);
            groupRefsRecyclerView.setAdapter(groupRefAdapter);
        });

        groupEditViewModel.getGroupTagAssignments().observe(this, groupTagAssignments -> {
            groupTagAssignmentAdapter = new GroupTagAssignmentAdapter(groupTagAssignments);
            groupTagAssignmentsRecyclerView.setAdapter(groupTagAssignmentAdapter);
        });
    }

    @Override
    public void onEditClick(int postId) {
        DialogFragment dialogFragment = TagSelectionDialogFragment.newInstance(postId);
        dialogFragment.show(getSupportFragmentManager(), "TagSelectionDialog");
    }

    @Override
    public void onTagsSelected(int postId, List<Integer> selectedTagIds) {
        groupEditViewModel.assignTagsToPost(postId, selectedTagIds);
        Toast.makeText(this, selectedTagIds.size() + " tags assigned.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddNewTagRequest() {
        DialogFragment newTagDialog = new AddNewTagDialogFragment();
        newTagDialog.show(getSupportFragmentManager(), "AddNewTagDialog");
    }

    @Override
    public void onDialogPositiveClick(String tagName) {
        groupEditViewModel.createTag(tagName);
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
        groupMemberAdapter.setEditMode(isEditMode);
        groupAnnotationAdapter.setEditMode(isEditMode);
        groupRefAdapter.setEditMode(isEditMode);
        groupTagAssignmentAdapter.setEditMode(isEditMode);
    }
}
