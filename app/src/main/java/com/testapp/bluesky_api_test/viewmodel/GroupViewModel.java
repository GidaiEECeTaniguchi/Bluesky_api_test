package com.testapp.bluesky_api_test.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;

import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupEntity;
import com.testapp.bluesky_api_test.repository.GroupEntityRepository;

public class GroupViewModel extends androidx.lifecycle.AndroidViewModel {

    private GroupEntityRepository groupEntityRepository;
    private androidx.lifecycle.LiveData<java.util.List<com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupEntity>> allGroups;

    public GroupViewModel(@NonNull Application application) {
        super(application);
        groupEntityRepository = new GroupEntityRepository(application);
        allGroups = (androidx.lifecycle.LiveData<java.util.List<com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupEntity>>) groupEntityRepository.getAllGroups();
    }

    public androidx.lifecycle.LiveData<java.util.List<com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupEntity>> getAllGroups() {
        return allGroups;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        groupEntityRepository.shutdown();
    }
}
