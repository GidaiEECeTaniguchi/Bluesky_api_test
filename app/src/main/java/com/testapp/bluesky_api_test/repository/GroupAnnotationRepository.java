package com.testapp.bluesky_api_test.repository;

import android.content.Context;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabase;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabaseSingleton;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.GroupAnnotationDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupAnnotation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroupAnnotationRepository {

    private final GroupAnnotationDao groupAnnotationDao;
    private final ExecutorService executorService;

    public GroupAnnotationRepository(Context context) {
        AppDatabase db = AppDatabaseSingleton.getInstance(context);
        this.groupAnnotationDao = db.groupAnnotationDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /* グループアノテーションをデータベースに保存するよぉ
     * @param annotation 保存するグループアノテーション
     */
    public void saveGroupAnnotation(GroupAnnotation annotation) {
        executorService.execute(() -> {
            groupAnnotationDao.insert(annotation);
        });
    }

    /* 値を直接入れても動くようにするよぉ
     * @param id ID
     * @param groupId グループID
     * @param concept コンセプト
     * @param description 説明
     * @param createdAt 作成日時
     */
    public void saveGroupAnnotation(int id, int groupId, String concept, String description, String createdAt) {
        GroupAnnotation annotation = new GroupAnnotation(groupId, concept, description, createdAt);
        annotation.setId(id);
        saveGroupAnnotation(annotation);
    }

    /* グループアノテーションをデータベースから取得するよぉ
     * @param id 取得するグループアノテーションのID
     */
    public GroupAnnotation getGroupAnnotationById(int id) {
        return groupAnnotationDao.getById(id);
    }

    public List<GroupAnnotation> getAllGroupAnnotationsFromDb() {
        return groupAnnotationDao.getAll();
    }

    public List<GroupAnnotation> getGroupAnnotationsByIdsFromDb(List<Integer> ids) {
        return groupAnnotationDao.loadAllByIds(ids);
    }

    public List<GroupAnnotation> getAnnotationsByGroupId(int groupId) {
        return groupAnnotationDao.getAnnotationsByGroupId(groupId);
    }

    /* シャットダウン処理だよぉ
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
