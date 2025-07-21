package com.testapp.bluesky_api_test.DataBaseManupilate;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


import com.testapp.bluesky_api_test.DataBaseManupilate.dao.AuthorDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.BasePostDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.GroupAnnotationDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.GroupEntityDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.GroupMemberDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.GroupRefDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.GroupRewriteDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.GroupTagAssignmentDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.TagAssignmentDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.TagDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.UserDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.Author;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupAnnotation;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupEntity;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupMember;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupRef;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupRewrite;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupTagAssignment;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.Tag;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.TagAssignment;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.User;

@Database(entities = {User.class, Author.class, BasePost.class, GroupEntity.class, GroupMember.class, GroupRef.class, GroupAnnotation.class, GroupRewrite.class, Tag.class, TagAssignment.class, GroupTagAssignment.class},
        version = 1, exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract AuthorDao authorDao();
    public abstract BasePostDao basePostDao();
    public abstract GroupEntityDao groupEntityDao();
    public abstract GroupMemberDao groupMemberDao();
    public abstract GroupRefDao groupRefDao();
    public abstract GroupAnnotationDao groupAnnotationDao();
    public abstract GroupRewriteDao groupRewriteDao();
    public abstract TagDao tagDao();
    public abstract TagAssignmentDao tagAssignmentDao();
    public abstract GroupTagAssignmentDao groupTagAssignmentDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "bluesky_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}