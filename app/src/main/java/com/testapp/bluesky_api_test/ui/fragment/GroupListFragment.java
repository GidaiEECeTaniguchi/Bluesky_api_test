package com.testapp.bluesky_api_test.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.testapp.bluesky_api_test.R;

public class GroupListFragment extends Fragment {

    private RecyclerView groupListRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);

        groupListRecyclerView = view.findViewById(R.id.group_list_recycler_view);
        // Setup RecyclerView (LayoutManager, Adapter, etc.)

        return view;
    }
}
