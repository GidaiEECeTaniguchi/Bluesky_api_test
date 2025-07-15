package com.testapp.bluesky_api_test.ui.fragment;



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
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost;
import com.testapp.bluesky_api_test.viewmodel.MainViewModel;

import java.util.List;

public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private MainViewModel mainViewModel;

    private TextView statusTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        statusTextView = root.findViewById(R.id.status_text_view);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mainViewModel.getTimelinePosts().observe(getViewLifecycleOwner(), (List<BasePost> posts) -> {
            if (posts != null && !posts.isEmpty()) {
                Log.d(TAG, "Timeline posts updated: " + posts.size());
                StringBuilder sb = new StringBuilder();
                sb.append("Posts loaded from DB: ").append(posts.size()).append("\n");
                for(int i = 0; i < Math.min(posts.size(), 5); i++) {
                    sb.append(posts.get(i).getContent()).append("\n");
                }
                statusTextView.setText(sb.toString());
            } else {
                Log.d(TAG, "No timeline posts in DB.");
                statusTextView.setText("No posts to display. Waiting for background sync...");
            }
        });

        return root;
    }
}
