package com.testapp.bluesky_api_test.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupRef;
import com.testapp.bluesky_api_test.repository.GroupRefRepository;
import java.util.List;

public class RefSelectViewModel extends AndroidViewModel {

    private GroupRefRepository groupRefRepository;
    private LiveData<List<GroupRef>> allRefs;

    public RefSelectViewModel(@NonNull Application application) {
        super(application);
        groupRefRepository = new GroupRefRepository(application);
        allRefs = groupRefRepository.getAllRefs();
    }

    public LiveData<List<GroupRef>> getAllRefs() {
        return allRefs;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        groupRefRepository.shutdown();
    }
}