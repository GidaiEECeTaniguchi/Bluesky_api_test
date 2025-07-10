package com.testapp.bluesky_api_test.DataBaseManupilate.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupEntity;

import java.util.List;

@Dao
public interface GroupEntityDao {

    @Query("SELECT * FROM GroupEntity")
    LiveData<List<GroupEntity>> getAll();

    @Query("SELECT * FROM GroupEntity WHERE id = :id")
    GroupEntity getById(int id);

    @Query("SELECT * FROM GroupEntity WHERE id IN (:ids)")
    List<GroupEntity> loadAllByIds(List<Integer> ids);

    @Insert
    void insertAll(GroupEntity... items);

    @Insert
    long insert(GroupEntity item);

    @Delete
    void delete(GroupEntity item);

    @Query("DELETE FROM GroupEntity")
    void deleteAll();
}
