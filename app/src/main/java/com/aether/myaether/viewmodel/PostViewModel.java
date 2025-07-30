package com.aether.myaether.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.aether.myaether.DataBaseManupilate.AppDatabase;
import com.aether.myaether.DataBaseManupilate.AppDatabaseSingleton;
import com.aether.myaether.DataBaseManupilate.dao.BasePostDao;
import com.aether.myaether.DataBaseManupilate.entity.BasePost;
import java.util.List;

public class PostViewModel extends AndroidViewModel {

    private LiveData<List<BasePost>> allPosts;
    private BasePostDao basePostDao;

    public PostViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabaseSingleton.getInstance(application);
        basePostDao = db.basePostDao();
        allPosts = basePostDao.getAll();
    }

    public LiveData<List<BasePost>> getAllPosts() {
        return allPosts;
    }
}
