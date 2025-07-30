package com.testapp.bluesky_api_test.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider; // 念のためimportを確認
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import dagger.hilt.android.AndroidEntryPoint;
import javax.inject.Inject;

import com.testapp.bluesky_api_test.R;
import com.testapp.bluesky_api_test.task.TimelineCrawlerWorker;
import com.testapp.bluesky_api_test.viewmodel.MainViewModel;

import java.util.List;

@AndroidEntryPoint
public class MainFragment extends Fragment implements BaseFragmentInterface{

    private static final String TAG = "MainFragment";

    // ViewModelの宣言
    private MainViewModel mainViewModel;

    private TextView statusTextView;
    // private RecyclerView timelineRecyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ✅ ViewModelProviderを使ってViewModelを正しく初期化します
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        initViews(view, savedInstanceState);
        initListeners();
        initObservers();
        loadData();
    }

    @Override
    public void initViews(@NonNull View view, @Nullable Bundle savedInstanceState) {
        statusTextView = view.findViewById(R.id.status_text_view);
        // timelineRecyclerView = view.findViewById(R.id.timeline_recycler_view);
        // Setup RecyclerView (e.g., set LayoutManager and Adapter)
    }

    @Override
    public void initListeners() {
        // Set any listeners here
    }

    @Override
    public void initObservers() {
        mainViewModel.getTimelinePosts().observe(getViewLifecycleOwner(), posts -> {
            if (posts != null && !posts.isEmpty()) {
                Log.d(TAG, "Timeline posts received: " + posts.size());
                statusTextView.setText("Posts loaded: " + posts.size());
                // Update RecyclerView adapter with new posts
            } else {
                Log.d(TAG, "No timeline posts received.");
                statusTextView.setText("No posts to display.");
            }
        });

        mainViewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                Log.d(TAG, "Loading timeline...");
                statusTextView.setText("Loading timeline...");
            } else {
                Log.d(TAG, "Finished loading timeline.");
            }
        });

        mainViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Log.e(TAG, "Error: " + errorMessage);
                statusTextView.setText("Error: " + errorMessage);
            } else {
                statusTextView.setText("");
            }
        });
    }

    @Override
    public void loadData() {
        mainViewModel.fetchTimeline();

        // For debugging: Enqueue the worker to run immediately
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(TimelineCrawlerWorker.class).build();
        WorkManager.getInstance(requireContext()).enqueue(workRequest);
        Log.d(TAG, "TimelineCrawlerWorker enqueued for immediate execution.");
    }
}