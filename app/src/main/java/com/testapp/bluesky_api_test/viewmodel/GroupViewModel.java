package com.testapp.bluesky_api_test.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupEntity;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.User;
import com.testapp.bluesky_api_test.repository.AuthRepository;
import com.testapp.bluesky_api_test.repository.GroupEntityRepository;
import com.testapp.bluesky_api_test.repository.UserRepository;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class GroupViewModel extends androidx.lifecycle.AndroidViewModel {

    private GroupEntityRepository groupEntityRepository;
    private UserRepository userRepository;
    private AuthRepository authRepository;
    private LiveData<List<GroupEntity>> allGroups;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public GroupViewModel(@NonNull Application application) {
        super(application);
        Log.d("GroupViewModel", "ViewModel created.");
        groupEntityRepository = new GroupEntityRepository(application);
        userRepository = new UserRepository(application);
        authRepository = new AuthRepository(application);
        allGroups = groupEntityRepository.getAllGroups();
    }

    public LiveData<List<GroupEntity>> getAllGroups() {
        Log.d("GroupViewModel", "getAllGroups() called.");
        return allGroups;
    }

    public void createNewGroup(String groupName) {
        executorService.execute(() -> {
            String userDid = authRepository.getDid();
            if (userDid == null) {
                Log.e("GroupViewModel", "User not logged in. Cannot create group.");
                return;
            }

            User currentUser = userRepository.getExistingUserByDid(userDid);
            if (currentUser == null) {
                // ユーザーが存在しない場合、新しく作成
                currentUser = new User(authRepository.getHandle(), userDid);
                userRepository.insertUser(currentUser);
                // 挿入後にIDが生成されるため、再度取得
                currentUser = userRepository.getExistingUserByDid(userDid);
            }

            if (currentUser == null) {
                Log.e("GroupViewModel", "Failed to get or create user. Cannot create group.");
                return;
            }

            // 適当なIDと名前で新しいグループを作成
            int newId = (int) System.currentTimeMillis(); // ユニークなIDを生成
            String createdAt = String.valueOf(System.currentTimeMillis());
            
            GroupEntity newGroup = new GroupEntity(newId, currentUser.getId(), groupName, createdAt);
            groupEntityRepository.insert(newGroup);
            refreshGroups(); // データを再読み込みしてUIを更新
        });
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
        userRepository.shutdown();
        executorService.shutdown();
    }
}
