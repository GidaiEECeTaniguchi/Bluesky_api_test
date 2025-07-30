package com.aether.myaether.DataBaseManupilate;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


import com.aether.myaether.DataBaseManupilate.dao.AuthorDao;
import com.aether.myaether.DataBaseManupilate.dao.BasePostDao;
import com.aether.myaether.DataBaseManupilate.dao.GroupAnnotationDao;
import com.aether.myaether.DataBaseManupilate.dao.GroupEntityDao;
import com.aether.myaether.DataBaseManupilate.dao.GroupMemberDao;
import com.aether.myaether.DataBaseManupilate.dao.GroupRefDao;
import com.aether.myaether.DataBaseManupilate.dao.GroupRewriteDao;
import com.aether.myaether.DataBaseManupilate.dao.GroupTagAssignmentDao;
import com.aether.myaether.DataBaseManupilate.dao.TagAssignmentDao;
import com.aether.myaether.DataBaseManupilate.dao.TagDao;
import com.aether.myaether.DataBaseManupilate.dao.UserDao;
import com.aether.myaether.DataBaseManupilate.entity.Author;
import com.aether.myaether.DataBaseManupilate.entity.BasePost;
import com.aether.myaether.DataBaseManupilate.entity.GroupAnnotation;
import com.aether.myaether.DataBaseManupilate.entity.GroupEntity;
import com.aether.myaether.DataBaseManupilate.entity.GroupMember;
import com.aether.myaether.DataBaseManupilate.entity.GroupRef;
import com.aether.myaether.DataBaseManupilate.entity.GroupRewrite;
import com.aether.myaether.DataBaseManupilate.entity.GroupTagAssignment;
import com.aether.myaether.DataBaseManupilate.entity.Tag;
import com.aether.myaether.DataBaseManupilate.entity.TagAssignment;
import com.aether.myaether.DataBaseManupilate.entity.User;

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