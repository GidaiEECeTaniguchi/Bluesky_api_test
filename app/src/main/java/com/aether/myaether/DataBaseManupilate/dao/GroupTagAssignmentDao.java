package com.aether.myaether.DataBaseManupilate.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.aether.myaether.DataBaseManupilate.entity.GroupTagAssignment;

import java.util.List;

@Dao
public interface GroupTagAssignmentDao {

    @Query("SELECT * FROM GroupTagAssignment")
    List<GroupTagAssignment> getAll();

    @Query("SELECT * FROM GroupTagAssignment WHERE group_id = :groupId")
    List<GroupTagAssignment> getTagAssignmentsByGroupId(int groupId);

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
