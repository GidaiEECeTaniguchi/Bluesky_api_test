package com.aether.myaether.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aether.myaether.R;
import com.aether.myaether.ui.GroupAdapter;
import com.aether.myaether.viewmodel.GroupViewModel;

import android.content.Intent;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import com.aether.myaether.DataBaseManupilate.entity.GroupEntity;

import java.util.ArrayList;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GroupFragment extends Fragment implements  BaseFragmentInterface {

    private GroupViewModel groupViewModel;
    private GroupAdapter groupAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddGroup;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groupViewModel = new ViewModelProvider(this).get(GroupViewModel.class);

        initViews(view, savedInstanceState);
        initListeners();
        // onViewCreatedで一度だけ監視を設定する
        initObservers();
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 画面が表示されるたびにデータを再読み込みする
        groupViewModel.refreshGroups();
    }

    @Override
    public void initViews(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recycler_view_groups);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        groupAdapter = new GroupAdapter(new ArrayList<>()); // 最初は空のリストで初期化
        recyclerView.setAdapter(groupAdapter);

        fabAddGroup = view.findViewById(R.id.fab_add_group);

        groupAdapter.setOnItemClickListener(group -> {
            Intent intent = new Intent(getActivity(), com.aether.myaether.ui.GroupEditActivity.class);
            intent.putExtra("group_id", group.getId());
            intent.putExtra("group_name", group.getName());
            startActivity(intent);
        });
    }

    @Override
    public void initListeners() {
        fabAddGroup.setOnClickListener(v -> showCreateGroupDialog());
    }

    private void showCreateGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("New Group");

        final EditText input = new EditText(requireContext());
        input.setHint("Group Name");
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String groupName = input.getText().toString().trim();
            if (!groupName.isEmpty()) {
                groupViewModel.createNewGroup(groupName);
            } else {
                // 名前が空の場合の処理（例: トースト表示など）
                Log.d("GroupFragment", "Group name cannot be empty.");
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    @Override
    public void initObservers() {
        groupViewModel.getAllGroups().observe(getViewLifecycleOwner(), groups -> {
            // ログを追加して、取得したグループの数を確認
            Log.d("GroupFragment", "Groups updated. Size: " + (groups != null ? groups.size() : "null"));
            if (groups != null) {
                for (GroupEntity group : groups) {
                    Log.d("GroupFragment", "Group Name: " + group.getName());
                }
            }
            // データが更新されたらアダプターにセットして表示を更新
            groupAdapter.setGroupList(groups);
        });
    }

    @Override
    public void loadData() {
        // No data to load initially, as it's observed from ViewModel
    }
}