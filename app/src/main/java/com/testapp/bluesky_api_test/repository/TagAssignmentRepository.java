package com.testapp.bluesky_api_test.repository;

import android.content.Context;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabase;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabaseSingleton;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.TagAssignmentDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.TagAssignment;

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
    /*シャットダウン処理
   
     */
    public void shutdown() {
        executorService.shutdown();
    }
}