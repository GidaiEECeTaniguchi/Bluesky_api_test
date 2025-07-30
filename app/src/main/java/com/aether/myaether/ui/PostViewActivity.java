package com.aether.myaether.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aether.myaether.R;
import com.aether.myaether.viewmodel.PostViewModel;
import java.util.ArrayList;

public class PostViewActivity extends AppCompatActivity {

    private PostViewModel postViewModel;
    private RecyclerView postRecyclerView;
    private GroupMemberAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("投稿一覧");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postRecyclerView = findViewById(R.id.post_recycler_view);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new GroupMemberAdapter(new ArrayList<>());
        postRecyclerView.setAdapter(postAdapter);

        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        postViewModel.getAllPosts().observe(this, posts -> {
            postAdapter = new GroupMemberAdapter(posts);
            postRecyclerView.setAdapter(postAdapter);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
