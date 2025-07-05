package com.testapp.bluesky_api_test.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.testapp.bluesky_api_test.R;
import com.testapp.bluesky_api_test.viewmodel.GroupViewModel;

public class GroupFragment extends Fragment {

    private GroupViewModel groupViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        groupViewModel = new ViewModelProvider(this).get(GroupViewModel.class);
        View root = inflater.inflate(R.layout.fragment_group, container, false);

        // ここで groupViewModel を使って何かするよ

        return root;
    }
}
