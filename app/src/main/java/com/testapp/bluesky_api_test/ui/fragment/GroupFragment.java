package com.testapp.bluesky_api_test.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.testapp.bluesky_api_test.R;
import com.testapp.bluesky_api_test.ui.GroupAdapter;
import com.testapp.bluesky_api_test.viewmodel.GroupViewModel;

import java.util.ArrayList;

public class GroupFragment extends Fragment {

    private GroupViewModel groupViewModel;
    private GroupAdapter groupAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        groupViewModel = new ViewModelProvider(this).get(GroupViewModel.class);
        View root = inflater.inflate(R.layout.fragment_group, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view_groups);
        groupAdapter = new GroupAdapter(new ArrayList<>()); // 最初は空のリストで初期化
        recyclerView.setAdapter(groupAdapter);

        groupViewModel.getGroupList().observe(getViewLifecycleOwner(), groups -> {
            // データが更新されたらアダプターにセットして表示を更新
            groupAdapter = new GroupAdapter(groups);
            recyclerView.setAdapter(groupAdapter);
        });

        return root;
    }
}
