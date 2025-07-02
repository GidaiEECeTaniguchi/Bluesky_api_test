package com.testapp.bluesky_api_test.DataBaseManupilate.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.testapp.bluesky_api_test.DataBaseManupilate.entity.TagAssignment;

import java.util.List;

@Dao
public interface TagAssignmentDao {

    @Query("SELECT * FROM TagAssignment")
    List<TagAssignment> getAll();

    @Query("SELECT * FROM TagAssignment WHERE tag_id = :tagId")
    List<TagAssignment> getTagAssignmentByTagId(int tagId);

    @Query("SELECT * FROM TagAssignment")
    TagAssignment getByComposite();



    @Insert
    void insertAll(TagAssignment... items);

    @Insert
    void insert(TagAssignment item);

    @Delete
    void delete(TagAssignment item);
}
