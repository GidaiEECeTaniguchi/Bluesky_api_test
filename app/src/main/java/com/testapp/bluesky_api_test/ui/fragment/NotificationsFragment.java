package com.testapp.bluesky_api_test.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.testapp.bluesky_api_test.R;

public class NotificationsFragment extends Fragment implements  BaseFragmentInterface{
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view, savedInstanceState);
        initListeners();
        initObservers();
        loadData();
    }

    @Override
    public void initViews(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // No specific views to initialize yet
    }

    @Override
    public void initListeners() {
        // No specific listeners yet
    }

    @Override
    public void initObservers() {
        // No specific observers yet
    }

    @Override
    public void loadData() {
        // No data to load yet
    }
}