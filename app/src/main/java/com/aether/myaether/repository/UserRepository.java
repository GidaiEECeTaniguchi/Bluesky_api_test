package com.aether.myaether.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aether.myaether.DataBaseManupilate.AppDatabase;
import com.aether.myaether.DataBaseManupilate.AppDatabaseSingleton;
import com.aether.myaether.DataBaseManupilate.dao.UserDao;
import com.aether.myaether.DataBaseManupilate.entity.User;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class UserRepository {
    private UserDao userDao;
    private ExecutorService executorService;


    public UserRepository( Application application) {
        AppDatabase db = AppDatabaseSingleton.getInstance(application);
        userDao = db.userDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<User>> getAllUsers() {
        MutableLiveData<List<User>> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            liveData.postValue(userDao.getAll());
        });
        return liveData;
    }

    public LiveData<User> getUserByDid(String did) {
        MutableLiveData<User> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            liveData.postValue(userDao.getUserByDid(did));
        });
        return liveData;
    }

    public void insertUser(User user) {
        executorService.execute(() -> userDao.insert(user));
    }

    public User getExistingUserByDid(String did) {
        // 同期的にユーザーを取得するためのヘルパーメソッド
        // このメソッドは、呼び出し元がexecutorService内で実行されていることを前提とします。
        return userDao.getUserByDid(did);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
