package com.testapp.bluesky_api_test.DataBaseManupilate.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupMember;

import java.util.List;

@Dao
public interface GroupMemberDao {

    @Query("SELECT * FROM GroupMember")
    List<GroupMember> getAll();

    @Query("SELECT * FROM GroupMember")
    GroupMember getByComposite();

    @Query("SELECT * FROM GroupMember ")
    List<GroupMember> loadAllByIds();

    @Insert
    void insertAll(GroupMember... items);

    @Insert
    void insert(GroupMember item);

    @Delete
    void delete(GroupMember item);
}
