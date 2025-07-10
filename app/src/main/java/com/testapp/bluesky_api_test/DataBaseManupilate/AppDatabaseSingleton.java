package com.testapp.bluesky_api_test.DataBaseManupilate;
import android.content.Context;
import androidx.room.Room;
public class AppDatabaseSingleton {
    private static volatile AppDatabase instance;
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabaseSingleton.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "app-database"
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return instance;
    }
}
