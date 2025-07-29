package com.testapp.bluesky_api_test.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabase;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabaseSingleton;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.BasePostDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost;
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
