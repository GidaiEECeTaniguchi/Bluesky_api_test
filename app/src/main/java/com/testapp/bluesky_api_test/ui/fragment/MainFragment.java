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

public class MainFragment extends Fragment implements BaseFragmentInterface{

    private static final String TAG = "MainFragment";
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

        mainViewModel = new ViewModelProvider(this, new MainViewModelFactory(requireContext().getApplicationContext()))
                .get(MainViewModel.class);

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

        mainViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
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