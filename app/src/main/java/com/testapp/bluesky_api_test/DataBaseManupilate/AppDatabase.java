package com.testapp.bluesky_api_test.DataBaseManupilate;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.testapp.bluesky_api_test.DataBaseManupilate.entity.*; // すべてのEntity
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.*;
@Database(
        entities = {
                User.class,
                GroupEntity.class,  // ← 旧 Group
                BasePost.class,
                Tag.class,
                GroupMember.class,
                TagAssignment.class,
                GroupTagAssignment.class,
                GroupAnnotation.class,
                GroupRewrite.class,
                GroupRef.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase{
    public abstract UserDao userDao();
    public abstract GroupEntityDao groupEntityDao();
    public abstract BasePostDao basePostDao();
    public abstract TagDao tagDao();
    public abstract GroupMemberDao groupMemberDao();
    public abstract TagAssignmentDao tagAssignmentDao();
    public abstract GroupTagAssignmentDao groupTagAssignmentDao();
    public abstract GroupAnnotationDao groupAnnotationDao();
    public abstract GroupRewriteDao groupRewriteDao();
    public abstract GroupRefDao groupRefDao();
}
