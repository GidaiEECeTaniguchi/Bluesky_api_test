package com.aether.myaether.repository;

import android.content.Context;
import com.aether.myaether.DataBaseManupilate.AppDatabase;
import com.aether.myaether.DataBaseManupilate.AppDatabaseSingleton;
import com.aether.myaether.DataBaseManupilate.dao.TagAssignmentDao;
import com.aether.myaether.DataBaseManupilate.entity.TagAssignment;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TagAssignmentRepository {
    private final TagAssignmentDao tagAssignmentDao;
    private final ExecutorService executorService;

    public TagAssignmentRepository(Context context) {
        AppDatabase db = AppDatabaseSingleton.getInstance(context);
        this.tagAssignmentDao = db.tagAssignmentDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }
    /*投稿とタグを紐づけます
     * @param postId 投稿のID
     * @param tagId タグのID
     */
    public void assignTagToPost(int postId, int tagId) {
        executorService.execute(() -> {
            TagAssignment tagAssignment = new TagAssignment(postId, tagId);
            tagAssignmentDao.insert(tagAssignment);
        });
    }

    public List<TagAssignment> getAllTagAssignmentsFromDb() {
        return tagAssignmentDao.getAll();
    }

    public List<TagAssignment> getTagAssignmentsByTagIdFromDb(int tagId) {
        return tagAssignmentDao.getTagAssignmentByTagId(tagId);
    }
    /*シャットダウン処理
   
     */
    public void shutdown() {
        executorService.shutdown();
    }
}