package com.aether.myaether.DataBaseManupilate.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.aether.myaether.DataBaseManupilate.entity.GroupRewrite;

import java.util.List;

@Dao
public interface GroupRewriteDao {

    @Query("SELECT * FROM GroupRewrite")
    List<GroupRewrite> getAll();

    @Query("SELECT * FROM GroupRewrite WHERE id = :id")
    GroupRewrite getById(int id);

    @Query("SELECT * FROM GroupRewrite WHERE id IN (:ids)")
    List<GroupRewrite> loadAllByIds(List<Integer> ids);

    @Insert
    void insertAll(GroupRewrite... items);

    @Insert
    void insert(GroupRewrite item);

    @Delete
    void delete(GroupRewrite item);
}
