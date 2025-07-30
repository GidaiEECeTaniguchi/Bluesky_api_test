package com.aether.myaether.repository;

import android.content.Context;
import com.aether.myaether.DataBaseManupilate.AppDatabase;
import com.aether.myaether.DataBaseManupilate.AppDatabaseSingleton;
import com.aether.myaether.DataBaseManupilate.dao.GroupMemberDao;
import com.aether.myaether.DataBaseManupilate.entity.GroupMember;

import java.util.List;
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

    public List<GroupMember> getAllGroupMembersFromDb() {
        return groupMemberDao.getAll();
    }

    public List<GroupMember> getAllGroupMembersByIdsFromDb() {
        return groupMemberDao.loadAllByIds();
    }

    /**
     * ExecutorServiceをシャットダウンします。
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
