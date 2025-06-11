package com.testapp.bluesky_api_test;

import androidx.appcompat.app.AppCompatActivity;
// Room関連のimportは既存のまま
// import androidx.room.Room;
// import androidx.room.RoomDatabase;

import android.app.Activity;
import android.os.Bundle;
// import android.util.Log; // 必要に応じて
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
// import android.widget.Toast; // DataFetchTask内で処理

import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabase;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabaseSingleton;
import com.testapp.bluesky_api_test.task.DataFetchTask; // 新しいタスククラスをインポート

// Rクラスのimportはプロジェクト構成によって変わる可能性があります
// import com.testapp.bluesky_api_test.R;

import java.lang.ref.WeakReference;
// import java.sql.Timestamp; // DatabaseOperationsへ移動
// import java.util.List; // DatabaseOperationsへ移動

// kbskyライブラリのimportはBlueskyOperationsへ移動

public class MainActivity extends AppCompatActivity {

    // private static final String TAG = "MainActivity"; // 必要に応じて

    private TextView tvInfo;
    private AppDatabase appDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvInfo = findViewById(R.id.index);
        Button btFetchData = findViewById(R.id.button);
        appDb = AppDatabaseSingleton.getInstance(getApplicationContext());

        btFetchData.setOnClickListener(new ButtonClickListener(this, appDb, tvInfo));
    }

    private static class ButtonClickListener implements View.OnClickListener {
        private final WeakReference<Activity> activityReference;
        private final AppDatabase db;
        private final TextView tv;

        private ButtonClickListener(Activity activity, AppDatabase db, TextView tv) {
            this.activityReference = new WeakReference<>(activity);
            this.db = db;
            this.tv = tv;
        }

        @Override
        public void onClick(View view) {
            Activity activity = activityReference.get();
            if (activity!= null &&!activity.isFinishing()) {
                new DataFetchTask(activity, db, tv).execute();
            }
        }
    }
}