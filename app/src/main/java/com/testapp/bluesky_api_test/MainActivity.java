package com.testapp.bluesky_api_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.testapp.bluesky_api_test.DataBaseManupilate.AccessTime;
import com.testapp.bluesky_api_test.DataBaseManupilate.AccessTimeDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabase;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabaseSingleton;
import com.testapp.bluesky_api_test.R;

import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = findViewById(R.id.index);
        Button bt = findViewById(R.id.button);
        AppDatabase db = AppDatabaseSingleton.getInstance(getApplicationContext());

        bt.setOnClickListener(new ButtonClickListener(this, db, tv));

    }

    private class ButtonClickListener implements View.OnClickListener {
        private Activity activity;
        private AppDatabase db;
        private TextView tv;

        private ButtonClickListener(Activity activity, AppDatabase db, TextView tv) {
            this.activity = activity;
            this.db = db;
            this.tv = tv;
        }

        @Override
        public void onClick(View view) {
            new DataStoreAsyncTask(db, activity, tv).execute();

        }
    }

    private static class DataStoreAsyncTask extends AsyncTask<Void, Void, Integer> {
        private WeakReference<Activity> weakActivity;
        private AppDatabase db;
        private TextView textView;
        private StringBuilder sb;

        public DataStoreAsyncTask(AppDatabase db, Activity activity, TextView textView) {
            this.db = db;
            weakActivity = new WeakReference<>(activity);
            this.textView = textView;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            AccessTimeDao accessTimeDao = db.accessTimeDao();
            ((com.testapp.bluesky_api_test.DataBaseManupilate.AccessTimeDao) accessTimeDao).insert(new AccessTime(new Timestamp(System.currentTimeMillis()).toString()));

            sb = new StringBuilder();
            List<AccessTime> atList = ((com.testapp.bluesky_api_test.DataBaseManupilate.AccessTimeDao) accessTimeDao).getAll();
            for (AccessTime at: atList) {
                sb.append(at.getAccessTime()).append("\n");
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer code) {
            Activity activity = weakActivity.get();
            if(activity == null) {
                return;
            }

            textView.setText(sb.toString());

        }
    }

}