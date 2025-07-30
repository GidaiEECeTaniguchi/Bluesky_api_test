package com.aether.myaether.DataBaseManupilate.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


import com.aether.myaether.DataBaseManupilate.entity.GroupAnnotation;

import java.util.List;

@Dao
public interface GroupAnnotationDao {

    @Query("SELECT * FROM GroupAnnotation")
    List<GroupAnnotation> getAll();

    @Query("SELECT * FROM GroupAnnotation WHERE group_id = :groupId")
    List<GroupAnnotation> getAnnotationsByGroupId(int groupId);

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

    @Query("DELETE FROM GroupAnnotation")
    void deleteAll();
}