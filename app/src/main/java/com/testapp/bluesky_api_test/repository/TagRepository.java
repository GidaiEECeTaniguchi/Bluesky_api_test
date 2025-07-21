package com.testapp.bluesky_api_test.repository;

import android.content.Context;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabase;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabaseSingleton;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.TagDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.Tag;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TagRepository {

    private final TagDao tagDao;
    private final ExecutorService executorService;

    public TagRepository(Context context) {
        AppDatabase db = AppDatabaseSingleton.getInstance(context);
        this.tagDao = db.tagDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }
    /** タグをデータベースに保存する
     * @param tag 保存するタグ
     */
    public void saveTag(Tag tag) {
        executorService.execute(() -> {
            tagDao.insert(tag);
        });
    }
    /*値を直接入れても動くようにする
     * PKはid,int型
     * nameはString型
     * scopeはString型
     * 
     */
    public void saveTag(int id, String name, String scope) {
        Tag tag = new Tag(name, scope);
        tag.setId(id);
        saveTag(tag);
    }

    /* タグをデータベースから取得する
     * @param id 取得するタグのid
     */
    public Tag getTagById(int id) {
        return tagDao.getById(id);
    }

    /* タグをデータベースから取得する
     * @param name 取得するタグのname
     */
    public Tag getTagByName(String name) {
        return tagDao.getTagByName(name);
    }

    public List<Tag> getAllTagsFromDb() {
        return tagDao.getAll();
    }

    public List<Tag> getTagsByIdsFromDb(List<Integer> ids) {
        return tagDao.loadAllByIds(ids);
    }
    /*シャットダウン処理
     * 
     */
    public void shutdown() {
        executorService.shutdown();
    }
}