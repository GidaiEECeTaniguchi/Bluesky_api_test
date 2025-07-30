package com.aether.myaether.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.aether.myaether.DataBaseManupilate.AppDatabase;
import com.aether.myaether.DataBaseManupilate.AppDatabaseSingleton;
import com.aether.myaether.DataBaseManupilate.dao.GroupRefDao;
import com.aether.myaether.DataBaseManupilate.entity.GroupRef;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroupRefRepository {

    private final GroupRefDao groupRefDao;
    private final ExecutorService executorService;

    public GroupRefRepository(Context context) {
        AppDatabase db = AppDatabaseSingleton.getInstance(context);
        this.groupRefDao = db.groupRefDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /* グループ参照をデータベースに保存するよぉ
     * @param ref 保存するグループ参照
     */
    public void saveGroupRef(GroupRef ref) {
        executorService.execute(() -> {
            groupRefDao.insert(ref);
        });
    }

    /* 値を直接入れても動くようにするよぉ
     * @param id ID
     * @param groupId グループID
     * @param title タイトル
     * @param type タイプ
     * @param refPath 参照パス
     */
    public void saveGroupRef(int id, int groupId, String title, String type, String refPath) {
        GroupRef ref = new GroupRef(groupId, title, type, refPath);
        ref.setId(id);
        saveGroupRef(ref);
    }

    /* グループ参照をデータベースから取得するよぉ
     * @param id 取得するグループ参照のID
     */
    public GroupRef getGroupRefById(int id) {
        return groupRefDao.getById(id);
    }

    public LiveData<List<GroupRef>> getAllGroupRefsFromDb() {
        return groupRefDao.getAll();
    }

    public List<GroupRef> getGroupRefsByIdsFromDb(List<Integer> ids) {
        return groupRefDao.loadAllByIds(ids);
    }

    public List<GroupRef> getRefsByGroupId(int groupId) {
        return groupRefDao.getRefsByGroupId(groupId);
    }

    public LiveData<List<GroupRef>> getAllRefs() {
        return groupRefDao.getAll();
    }

    /* シャットダウン処理だよぉ
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
