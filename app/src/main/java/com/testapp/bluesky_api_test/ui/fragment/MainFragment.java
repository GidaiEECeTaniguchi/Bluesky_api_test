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
import androidx.lifecycle.ViewModelProvider;

import com.testapp.bluesky_api_test.R;
import com.testapp.bluesky_api_test.repository.AuthRepository;
import com.testapp.bluesky_api_test.repository.AuthorRepository;
import com.testapp.bluesky_api_test.repository.PostRepository;
import com.testapp.bluesky_api_test.viewmodel.MainViewModel;

import java.util.List;

public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private MainViewModel mainViewModel;

    private TextView statusTextView;
    // private RecyclerView timelineRecyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize UI elements
        statusTextView = root.findViewById(R.id.status_text_view);
        // timelineRecyclerView = root.findViewById(R.id.timeline_recycler_view);
        // Setup RecyclerView (e.g., set LayoutManager and Adapter)
        // Example: timelineRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Example: timelineRecyclerView.setAdapter(new TimelineAdapter());

        // Initialize MainViewModel with a custom factory
        // The Application context is needed for BlueskyRepository
        mainViewModel = new ViewModelProvider(this, new MainViewModelFactory(requireContext().getApplicationContext()))
                .get(MainViewModel.class);

        // Observe LiveData from MainViewModel
        mainViewModel.getTimelinePosts().observe(getViewLifecycleOwner(), posts -> {
            if (posts != null && !posts.isEmpty()) {
                Log.d(TAG, "Timeline posts received: " + posts.size());
                statusTextView.setText("Posts loaded: " + posts.size());
                // Update RecyclerView adapter with new posts
                // ((TimelineAdapter) timelineRecyclerView.getAdapter()).submitList(posts);
            } else {
                Log.d(TAG, "No timeline posts received.");
                statusTextView.setText("No posts to display.");
            }
        });

        mainViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                Log.d(TAG, "Loading timeline...");
                statusTextView.setText("Loading timeline...");
                // Show loading indicator
            } else {
                Log.d(TAG, "Finished loading timeline.");
                // Hide loading indicator
            }
        });

        mainViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Log.e(TAG, "Error: " + errorMessage);
                statusTextView.setText("Error: " + errorMessage);
                // Show error message to user
            } else {
                statusTextView.setText(""); // Clear error message if no error
            }
        });

        // Fetch timeline data when the fragment is created
        mainViewModel.fetchTimeline();

        return root;
    }

    // Custom ViewModelFactory for MainViewModel
    private static class MainViewModelFactory implements ViewModelProvider.Factory {
        private final Context applicationContext;

        public MainViewModelFactory(Context applicationContext) {
            this.applicationContext = applicationContext;
        }

        @NonNull
        @Override
        public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(MainViewModel.class)) {
                AuthRepository authRepository = new AuthRepository(applicationContext);
                AuthorRepository authorRepository = new AuthorRepository(applicationContext);
                PostRepository postRepository = new PostRepository(applicationContext);
                return (T) new MainViewModel(authRepository, authorRepository, postRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}