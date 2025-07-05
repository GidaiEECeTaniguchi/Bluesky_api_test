package com.testapp.bluesky_api_test.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.testapp.bluesky_api_test.R;

public class GroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // あとで activity_group.xml を作るからね
        setContentView(R.layout.activity_group); 
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
