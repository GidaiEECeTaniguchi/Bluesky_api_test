package com.testapp.bluesky_api_test.DataBaseManupilate.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupAnnotation;

import java.util.List;

@Dao
public interface GroupAnnotationDao {

    @Query("SELECT * FROM GroupAnnotation")
    List<GroupAnnotation> getAll();

    @Query("SELECT * FROM GroupAnnotation WHERE id = :id")
    GroupAnnotation getById(int id);

    @Query("SELECT * FROM GroupAnnotation WHERE id IN (:ids)")
    List<GroupAnnotation> loadAllByIds(List<Integer> ids);

    @Insert
    void insertAll(GroupAnnotation... items);

    @Insert
    void insert(GroupAnnotation item);

    @Delete
    void delete(GroupAnnotation item);
}
