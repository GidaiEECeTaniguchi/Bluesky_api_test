package com.aether.myaether.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.aether.myaether.DataBaseManupilate.AppDatabase;
import com.aether.myaether.DataBaseManupilate.AppDatabaseSingleton;
import com.aether.myaether.DataBaseManupilate.dao.TagDao;
import com.aether.myaether.DataBaseManupilate.entity.Tag;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TagViewModel extends AndroidViewModel {

    private MutableLiveData<List<Tag>> allTags;
    private TagDao tagDao;
    private ExecutorService executorService;

    public TagViewModel(Application application) {
        super(application);
        allTags = new MutableLiveData<>();
        AppDatabase db = AppDatabaseSingleton.getInstance(application);
        tagDao = db.tagDao();
        executorService = Executors.newSingleThreadExecutor();
        loadAllTags();
    }

    public LiveData<List<Tag>> getAllTags() {
        return allTags;
    }

    private void loadAllTags() {
        executorService.execute(() -> {
            List<Tag> tags = tagDao.getAll();
            allTags.postValue(tags);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
