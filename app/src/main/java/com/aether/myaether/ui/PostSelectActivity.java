package com.aether.myaether.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aether.myaether.R;
import com.aether.myaether.viewmodel.PostSelectViewModel;
import com.aether.myaether.DataBaseManupilate.entity.BasePost;

import java.util.ArrayList;

public class PostSelectActivity extends AppCompatActivity {

    public static final String EXTRA_SELECTED_POST_ID = "selected_post_id";

    private PostSelectViewModel postSelectViewModel;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("PostSelectActivity", "onCreate started.");
        setContentView(R.layout.activity_post_select);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postAdapter = new PostAdapter(new ArrayList<>());
        recyclerView.setAdapter(postAdapter);

        postAdapter.setOnItemClickListener(postWithAuthorName -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_SELECTED_POST_ID, postWithAuthorName.id);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        postSelectViewModel = new ViewModelProvider(this).get(PostSelectViewModel.class);
        Log.d("PostSelectActivity", "ViewModel created and observing posts.");
        postSelectViewModel.getAllPosts().observe(this, posts -> {
            if (posts != null) {
                Log.d("PostSelectActivity", "Posts received from ViewModel. Count: " + posts.size());
                postAdapter.setPostList(posts);
            } else {
                Log.d("PostSelectActivity", "Received null post list from ViewModel.");
            }
        });
    }
}