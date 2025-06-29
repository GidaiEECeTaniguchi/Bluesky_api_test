package com.testapp.bluesky_api_test.DataBaseManupilate.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost;
import java.util.List;

@Dao
public interface BasePostDao {

    @Query("SELECT * FROM base_posts")
    List<BasePost> getAll();

    @Query("SELECT * FROM base_posts WHERE id = :id")
    BasePost getById(int id);

    @Query("SELECT * FROM base_posts WHERE user_id = :userId")
    List<BasePost> getPostsByUserId(int userId);

    @Query("SELECT * FROM base_posts WHERE author_id = :authorId")
    List<BasePost> getPostsByAuthorId(int authorId);

    @Insert
    long insert(BasePost item);

    @Insert
    long[] insertAll(BasePost... items);

    @Update
    void update(BasePost item);

    @Delete
    void delete(BasePost item);
}
