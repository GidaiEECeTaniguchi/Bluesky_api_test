package com.testapp.bluesky_api_test.DataBaseManupilate;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface AccessTimeDao {
    @Query("SELECT * FROM accesstime")
    List<AccessTime> getAll();

    @Query("SELECT * FROM accesstime WHERE id IN (:ids)")
    List<AccessTime> loadAllByIds(int[] ids);

    @Insert
    void insertAll(AccessTime... accessTimes);

    @Insert
    void insert(AccessTime accessTime);

    @Delete
    void delete(AccessTime accessTime);
}