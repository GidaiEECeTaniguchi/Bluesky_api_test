package com.testapp.bluesky_api_test.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupEntity;
import com.testapp.bluesky_api_test.repository.GroupEntityRepository;

import java.util.List;

public class GroupViewModel extends androidx.lifecycle.AndroidViewModel {

    private GroupEntityRepository groupEntityRepository;
    private LiveData<List<GroupEntity>> allGroups;

    public GroupViewModel(@NonNull Application application) {
        super(application);
        Log.d("GroupViewModel", "ViewModel created.");
        groupEntityRepository = new GroupEntityRepository(application);
        allGroups = groupEntityRepository.getAllGroups();
    }

    public LiveData<List<GroupEntity>> getAllGroups() {
        Log.d("GroupViewModel", "getAllGroups() called.");
        return allGroups;
    }

    // データ再読み込みのためのメソッドを追加
    public void refreshGroups() {
        Log.d("GroupViewModel", "Refreshing groups data.");
        // LiveDataを再取得して更新する
        allGroups = groupEntityRepository.getAllGroups();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        groupEntityRepository.shutdown();
    }
}
