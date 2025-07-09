package com.testapp.bluesky_api_test.DataBaseManupilate.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost;
import java.util.List;

@Dao
public interface BasePostDao {

    @Query("SELECT * FROM base_posts")
    LiveData<List<BasePost>> getAll();

    @Query("SELECT * FROM base_posts WHERE id = :id")
    BasePost getById(int id);

    @Query("SELECT * FROM base_posts WHERE user_id = :userId")
    List<BasePost> getPostsByUserId(int userId);

    @Query("SELECT * FROM base_posts WHERE author_id = :authorId")
    List<BasePost> getPostsByAuthorId(int authorId);

    @Query("SELECT * FROM base_posts WHERE uri = :uri")
    BasePost getByUri(String uri);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(BasePost item);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] insertAll(BasePost... items);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<BasePost> items);

    @Update
    void update(BasePost item);

    @Delete
    void delete(BasePost item);

    @Query("DELETE FROM base_posts")
    void deleteAll();
}
