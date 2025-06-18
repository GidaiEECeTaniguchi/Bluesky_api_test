package com.testapp.bluesky_api_test;

import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabase;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabaseSingleton;
import com.testapp.bluesky_api_test.task.DataFetchTask; // 新しいタスククラスをインポート
import com.testapp.bluesky_api_test.repository.AuthRepository;
// Rクラスのimportはプロジェクト構成によって変わる可能性があります
// import com.testapp.bluesky_api_test.R;

import java.lang.ref.WeakReference;


// kbskyライブラリのimportはBlueskyOperationsへ移動

public class TestMainActivity extends AppCompatActivity {

    private static final String TAG = "TestMainActivity"; // 必要に応じて

    private TextView tvInfo;
    private AppDatabase appDb;


    public TestMainActivity(AuthRepository authRepository) {

    }

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
        private final AuthRepository authRepository;

        private ButtonClickListener(Activity activity, AppDatabase db, TextView tv) {
            this.activityReference = new WeakReference<>(activity);
            this.db = db;
            this.tv = tv;
            this.authRepository = new AuthRepository(activity.getApplicationContext());
        }

        @Override
        public void onClick(View view) {
            Activity activity = activityReference.get();
            if (activity!= null &&!activity.isFinishing()) {new DataFetchTask(activity,authRepository,tv).execute();
            }
        }
    }
}