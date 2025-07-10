package com.testapp.bluesky_api_test.ui.fragment;

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
import com.testapp.bluesky_api_test.R;
import com.testapp.bluesky_api_test.ui.GroupAdapter;
import com.testapp.bluesky_api_test.viewmodel.GroupViewModel;

import android.content.Intent;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupEntity;

import java.util.ArrayList;

public class GroupFragment extends Fragment implements  BaseFragmentInterface {

    private GroupViewModel groupViewModel;
    private GroupAdapter groupAdapter;
    private RecyclerView recyclerView;

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

        groupAdapter.setOnItemClickListener(group -> {
            Intent intent = new Intent(getActivity(), com.testapp.bluesky_api_test.ui.GroupEditActivity.class);
            intent.putExtra("group_id", group.getId());
            intent.putExtra("group_name", group.getName());
            startActivity(intent);
        });
    }

    @Override
    public void initListeners() {
        // No specific listeners yet
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