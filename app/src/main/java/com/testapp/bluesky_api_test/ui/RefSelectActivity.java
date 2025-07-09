package com.testapp.bluesky_api_test.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.testapp.bluesky_api_test.R;
import com.testapp.bluesky_api_test.viewmodel.RefSelectViewModel;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupRef;

import java.util.ArrayList;

public class RefSelectActivity extends AppCompatActivity {

    public static final String EXTRA_SELECTED_REF_ID = "selected_ref_id";

    private RefSelectViewModel refSelectViewModel;
    private GroupRefAdapter groupRefAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ref_select);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_refs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        groupRefAdapter = new GroupRefAdapter(new ArrayList<>());
        recyclerView.setAdapter(groupRefAdapter);

        // TODO: GroupRefAdapterにクリックリスナーを追加し、選択された参考資料のIDを返す
        // groupRefAdapter.setOnItemClickListener(ref -> {
        //     Intent resultIntent = new Intent();
        //     resultIntent.putExtra(EXTRA_SELECTED_REF_ID, ref.getId());
        //     setResult(RESULT_OK, resultIntent);
        //     finish();
        // });

        refSelectViewModel = new ViewModelProvider(this).get(RefSelectViewModel.class);
        refSelectViewModel.getAllRefs().observe(this, refs -> {
            groupRefAdapter.setRefList(refs);
        });
    }
}