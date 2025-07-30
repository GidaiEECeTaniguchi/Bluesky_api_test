package com.aether.myaether.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aether.myaether.R;
import com.aether.myaether.viewmodel.TagViewModel;
import java.util.ArrayList;

public class TagViewActivity extends AppCompatActivity {

    private TagViewModel tagViewModel;
    private RecyclerView tagRecyclerView;
    private TagAdapter tagAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("タグ一覧");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tagRecyclerView = findViewById(R.id.tag_recycler_view);
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tagAdapter = new TagAdapter(new ArrayList<>());
        tagRecyclerView.setAdapter(tagAdapter);

        tagViewModel = new ViewModelProvider(this).get(TagViewModel.class);
        tagViewModel.getAllTags().observe(this, tags -> {
            tagAdapter = new TagAdapter(tags);
            tagRecyclerView.setAdapter(tagAdapter);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
