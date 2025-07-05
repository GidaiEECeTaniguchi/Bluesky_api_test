package com.testapp.bluesky_api_test.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.testapp.bluesky_api_test.ui.GroupAdapter.GroupEntity; // 仮の GroupEntity をインポート
import java.util.ArrayList;
import java.util.List;

public class GroupViewModel extends ViewModel {

    private MutableLiveData<List<GroupEntity>> groupList;

    public GroupViewModel() {
        groupList = new MutableLiveData<>();
        // ダミーデータをセット
        List<GroupEntity> dummyGroups = new ArrayList<>();
        dummyGroups.add(new GroupEntity("グループA"));
        dummyGroups.add(new GroupEntity("グループB"));
        dummyGroups.add(new GroupEntity("グループC"));
        groupList.setValue(dummyGroups);
    }

    public LiveData<List<GroupEntity>> getGroupList() {
        return groupList;
    }
}
