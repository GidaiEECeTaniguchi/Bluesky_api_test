package com.testapp.bluesky_api_test.DataBaseManupilate.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost;
import java.util.List;
@Dao
public interface BasePostDao {

    @Query("SELECT * FROM BasePost")
    List<BasePost> getAll();

    @Query("SELECT * FROM BasePost WHERE id = :id")
    BasePost getById(int id);

    @Query("SELECT * FROM BasePost WHERE id IN (:ids)")
    List<BasePost> loadAllByIds(List<Integer> ids);

    @Insert
    void insertAll(BasePost... items);

    @Insert
    void insert(BasePost item);

    @Delete
    void delete(BasePost item);
}
