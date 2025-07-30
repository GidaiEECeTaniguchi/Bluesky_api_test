package com.aether.myaether.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.aether.myaether.DataBaseManupilate.entity.GroupRef;
import com.aether.myaether.repository.GroupRefRepository;
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