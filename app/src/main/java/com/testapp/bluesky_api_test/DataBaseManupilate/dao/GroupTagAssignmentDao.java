package com.testapp.bluesky_api_test.DataBaseManupilate.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupTagAssignment;

import java.util.List;

@Dao
public interface GroupTagAssignmentDao {

    @Query("SELECT * FROM GroupTagAssignment")
    List<GroupTagAssignment> getAll();

    @Query("SELECT * FROM GroupTagAssignment ")
    GroupTagAssignment getByComposite();

    @Query("SELECT * FROM GroupTagAssignment ")
    List<GroupTagAssignment> loadAllByIds();

    @Insert
    void insertAll(GroupTagAssignment... items);

    @Insert
    void insert(GroupTagAssignment item);

    @Delete
    void delete(GroupTagAssignment item);
}
