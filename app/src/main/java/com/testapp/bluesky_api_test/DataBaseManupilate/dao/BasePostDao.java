package com.testapp.bluesky_api_test.DataBaseManupilate.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost;

import java.util.List;

import androidx.lifecycle.LiveData;

@Dao
public interface BasePostDao {
    @Insert
    long insert(BasePost basePost);

    @Insert
    long[] insertAll(BasePost... basePosts);

    @Update
    void update(BasePost basePost);

    @Delete
    void delete(BasePost basePost);

    @Query("DELETE FROM base_posts")
    void deleteAll();

    @Query("SELECT * FROM base_posts WHERE id = :id")
    BasePost getById(int id);

    @Query("SELECT * FROM base_posts WHERE user_id = :userId")
    List<BasePost> getPostsByUserId(int userId);

    @Query("SELECT * FROM base_posts WHERE author_id = :authorId")
    List<BasePost> getPostsByAuthorId(int authorId);

    @Query("SELECT * FROM base_posts ORDER BY created_at DESC")
    LiveData<List<BasePost>> getAll();

    @Query("SELECT * FROM base_posts ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    List<BasePost> getPagedPosts(int limit, int offset);

    @Query("SELECT COUNT(*) FROM base_posts")
    int getPostCount();
}