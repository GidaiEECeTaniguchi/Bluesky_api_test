package com.testapp.bluesky_api_test.repository;

import android.content.Context;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabase;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabaseSingleton;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.GroupRefDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupRef;

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

    /* シャットダウン処理だよぉ
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
