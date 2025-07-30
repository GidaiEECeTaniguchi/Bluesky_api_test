package com.aether.myaether.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.aether.myaether.R;

public class GroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group); 
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("グループ管理");

        // 戻るボタンを有効にする
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // 戻るボタンが押されたときの処理
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
