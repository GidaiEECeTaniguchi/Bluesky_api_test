package com.testapp.bluesky_api_test.repository;

import android.content.Context;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabase;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabaseSingleton;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.GroupRewriteDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupRewrite;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroupRewriteRepository {

    private final GroupRewriteDao groupRewriteDao;
    private final ExecutorService executorService;

    public GroupRewriteRepository(Context context) {
        AppDatabase db = AppDatabaseSingleton.getInstance(context);
        this.groupRewriteDao = db.groupRewriteDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /* グループリライトをデータベースに保存する
     * @param rewrite 保存するグループリライト
     */
    public void saveGroupRewrite(GroupRewrite rewrite) {
        executorService.execute(() -> {
            groupRewriteDao.insert(rewrite);
        });
    }

    /* 値を直接入れても動くようにする
     * @param id ID
     * @param groupId グループID
     * @param matcherText マッチャーテキスト
     * @param rewrittenText リライトされたテキスト
     * @param description 説明
     * @param createdAt 作成日時
     */
    public void saveGroupRewrite(int id, int groupId, String matcherText, String rewrittenText, String description, String createdAt) {
        GroupRewrite rewrite = new GroupRewrite(groupId, matcherText, rewrittenText, description, createdAt);
        rewrite.setId(id);
        saveGroupRewrite(rewrite);
    }

    /* グループリライトをデータベースから取得する
     * @param id 取得するグループリライトのID
     */
    public GroupRewrite getGroupRewriteById(int id) {
        return groupRewriteDao.getById(id);
    }

    public List<GroupRewrite> getAllGroupRewritesFromDb() {
        return groupRewriteDao.getAll();
    }

    public List<GroupRewrite> getGroupRewritesByIdsFromDb(List<Integer> ids) {
        return groupRewriteDao.loadAllByIds(ids);
    }

    /* シャットダウン処理
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
