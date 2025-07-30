package com.aether.myaether.repository;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;
import com.aether.myaether.DataBaseManupilate.AppDatabase;
import com.aether.myaether.DataBaseManupilate.AppDatabaseSingleton;
import com.aether.myaether.DataBaseManupilate.dao.GroupEntityDao;
import com.aether.myaether.DataBaseManupilate.entity.GroupEntity;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroupEntityRepository {
    private GroupEntityDao groupEntityDao;
    private ExecutorService executorService;

    public GroupEntityRepository(Application application) {
        AppDatabase db = AppDatabaseSingleton.getInstance(application);
        groupEntityDao = db.groupEntityDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<GroupEntity>> getAllGroups() {
        Log.d("GroupEntityRepository", "Fetching all groups from database.");
        return groupEntityDao.getAll();
    }

    public void insert(GroupEntity group) {
        executorService.execute(() -> groupEntityDao.insert(group));
    }

    public void insertAll(List<GroupEntity> groups) {
        executorService.execute(() -> groupEntityDao.insertAll(groups.toArray(new GroupEntity[0])));
    }

    public void deleteAllGroups() {
        executorService.execute(() -> groupEntityDao.deleteAll());
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
