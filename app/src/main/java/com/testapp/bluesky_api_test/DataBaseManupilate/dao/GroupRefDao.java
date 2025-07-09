package com.testapp.bluesky_api_test.DataBaseManupilate.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupRef;

import java.util.List;

@Dao
public interface GroupRefDao {

    @Query("SELECT * FROM GroupRef")
    LiveData<List<GroupRef>> getAll();

    @Query("SELECT * FROM GroupRef WHERE group_id = :groupId")
    List<GroupRef> getRefsByGroupId(int groupId);

    @Query("SELECT * FROM GroupRef WHERE id = :id")
    GroupRef getById(int id);

    @Query("SELECT * FROM GroupRef WHERE id IN (:ids)")
    List<GroupRef> loadAllByIds(List<Integer> ids);

    @Insert
    void insertAll(GroupRef... items);

    @Insert
    void insert(GroupRef item);

    @Delete
    void delete(GroupRef item);

    @Query("DELETE FROM GroupRef")
    void deleteAll();
}
