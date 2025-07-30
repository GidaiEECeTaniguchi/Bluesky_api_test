package com.aether.myaether.repository;

import android.content.Context;
import com.aether.myaether.DataBaseManupilate.AppDatabase; // AppDatabaseSingleton.getInstance() が AppDatabase を返すため
import com.aether.myaether.DataBaseManupilate.AppDatabaseSingleton;
import com.aether.myaether.DataBaseManupilate.dao.GroupTagAssignmentDao;
import com.aether.myaether.DataBaseManupilate.entity.GroupTagAssignment;
import com.aether.myaether.DataBaseManupilate.entity.Tag;
import com.aether.myaether.DataBaseManupilate.entity.TagAssignment;
import com.aether.myaether.DataBaseManupilate.entity.BasePost;
import com.aether.myaether.DataBaseManupilate.dao.TagDao;
import com.aether.myaether.DataBaseManupilate.dao.TagAssignmentDao;
import com.aether.myaether.DataBaseManupilate.dao.BasePostDao;

import java.util.List; // List を使うので追加
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroupTagAssignmentRepository {
    private final GroupTagAssignmentDao groupTagAssignmentDao;
    private final TagDao tagDao;
    private final TagAssignmentDao tagAssignmentDao;
    private final BasePostDao basePostDao;
    private final ExecutorService executorService;

    public GroupTagAssignmentRepository(Context context) {
        AppDatabase db = AppDatabaseSingleton.getInstance(context);
        this.groupTagAssignmentDao = db.groupTagAssignmentDao();
        this.tagDao = db.tagDao(); // 初期化を追加
        this.tagAssignmentDao = db.tagAssignmentDao(); // 初期化を追加
        this.basePostDao = db.basePostDao(); // 初期化を追加
        this.executorService = Executors.newSingleThreadExecutor();
    }
    /*グループとタグを紐づけます
     * @param groupId グループのID
     * @param tagId タグのID
     */
    public void assignTagToGroup(int groupId, int tagId) {
        executorService.execute(() -> {
            GroupTagAssignment groupTagAssignment = new GroupTagAssignment(groupId, tagId);
            groupTagAssignmentDao.insert(groupTagAssignment);
        });
    }
    /*この紐づけから投稿を取得します */
    public BasePost getPostsByIds(int groupId,int tagId) {
        //まずTAGテーブルを叩きます
        Tag tag = tagDao.getById(tagId); // getTagById を getById に修正
        if (tag == null) {
            return null; // タグが見つからない場合はnullを返す
        }
       //次にTAG_ASSINGMENTテーブルを叩きます
       List<TagAssignment> tagAssignments = tagAssignmentDao.getTagAssignmentByTagId(tagId); // getTagAssingmentById を getTagAssignmentByTagId に修正
       if (tagAssignments == null || tagAssignments.isEmpty()) {
           return null; // タグアサインメントが見つからない場合はnullを返す
       }
       // 複数のTagAssignmentが見つかる可能性があるので、最初のものを使うか、適切なロジックを追加する必要があるよ
       // ここではとりあえず最初のものを使う例
       TagAssignment tagAssignment = tagAssignments.get(0);

       //これをもとにBASEPOSTテーブルに投稿を聞きます
       BasePost basePost = basePostDao.getById(tagAssignment.getPost_id()); // getBasePostById を getById に修正
       //お疲れ様でした。
       return basePost;
    }

    public List<GroupTagAssignment> getAllGroupTagAssignmentsFromDb() {
        return groupTagAssignmentDao.getAll();
    }

    public List<GroupTagAssignment> getAllGroupTagAssignmentsByIdsFromDb() {
        return groupTagAssignmentDao.loadAllByIds();
    }

    public List<GroupTagAssignment> getTagAssignmentsByGroupId(int groupId) {
        return groupTagAssignmentDao.getTagAssignmentsByGroupId(groupId);
    }

    /*シャットダウン処理*/
    public void shutdown() {
        executorService.shutdown();
    }
}