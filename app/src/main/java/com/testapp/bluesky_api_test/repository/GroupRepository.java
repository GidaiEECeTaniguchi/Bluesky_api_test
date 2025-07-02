package com.testapp.bluesky_api_test.repository;

import android.content.Context;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabase;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabaseSingleton;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.GroupMemberDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupMember;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroupRepository {

    private final GroupMemberDao groupMemberDao;
    private final ExecutorService executorService;

    public GroupRepository(Context context) {
        AppDatabase db = AppDatabaseSingleton.getInstance(context);
        this.groupMemberDao = db.groupMemberDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * 投稿をグループに登録します。
     * @param groupId 登録するグループのID
     * @param postId 登録する投稿のID
     * @param order グループ内での表示順序
     */
    public void addPostToGroup(int groupId, int postId, int order) {
        executorService.execute(() -> {
            GroupMember groupMember = new GroupMember(groupId, postId, order);
            groupMemberDao.insert(groupMember);
        });
    }

    /**
     * ExecutorServiceをシャットダウンします。
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
