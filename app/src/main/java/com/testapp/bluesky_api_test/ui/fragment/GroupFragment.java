package com.testapp.bluesky_api_test.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.testapp.bluesky_api_test.R;
import com.testapp.bluesky_api_test.ui.GroupAdapter;
import com.testapp.bluesky_api_test.viewmodel.GroupViewModel;

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
        initObservers();
        loadData();
    }

    @Override
    public void initViews(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recycler_view_groups);
        groupAdapter = new GroupAdapter(new ArrayList<>()); // 最初は空のリストで初期化
        recyclerView.setAdapter(groupAdapter);
    }

    @Override
    public void initListeners() {
        // No specific listeners yet
    }

    @Override
    public void initObservers() {
        groupViewModel.getGroupList().observe(getViewLifecycleOwner(), groups -> {
            // データが更新されたらアダプターにセットして表示を更新
            groupAdapter = new GroupAdapter(groups);
            recyclerView.setAdapter(groupAdapter);
        });
    }

    @Override
    public void loadData() {
        // No data to load initially, as it's observed from ViewModel
    }
}