package com.testapp.bluesky_api_test.DataBaseManupilate.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.testapp.bluesky_api_test.DataBaseManupilate.entity.Tag;

import java.util.List;

@Dao
public interface TagDao {

    @Query("SELECT * FROM Tag")
    List<Tag> getAll();

    @Query("SELECT * FROM Tag WHERE id = :id")
    Tag getById(int id);

    @Query("SELECT * FROM Tag WHERE id IN (:ids)")
    List<Tag> loadAllByIds(List<Integer> ids);

    @Insert
    void insertAll(Tag... items);

    @Query("SELECT * FROM Tag WHERE name = :name")
    Tag getTagByName(String name);

    @Insert
    void insert(Tag item);

    @Delete
    void delete(Tag item);
}
