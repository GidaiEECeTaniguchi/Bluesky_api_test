package com.testapp.bluesky_api_test.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupEntity; // 本物の GroupEntity をインポート
import java.util.ArrayList;
import java.util.List;

public class GroupViewModel extends ViewModel {

    private MutableLiveData<List<GroupEntity>> groupList;

    public GroupViewModel() {
        groupList = new MutableLiveData<>();
        // ダミーデータをセット
        List<GroupEntity> dummyGroups = new ArrayList<>();
        dummyGroups.add(new GroupEntity(1, "グループA", "2025-07-05"));
        dummyGroups.add(new GroupEntity(1, "グループB", "2025-07-05"));
        dummyGroups.add(new GroupEntity(1, "グループC", "2025-07-05"));
        groupList.setValue(dummyGroups);
    }

    public LiveData<List<GroupEntity>> getGroupList() {
        return groupList;
    }
}
