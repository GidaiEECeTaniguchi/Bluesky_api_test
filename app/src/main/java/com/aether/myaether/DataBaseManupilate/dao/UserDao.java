package com.aether.myaether.DataBaseManupilate.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.aether.myaether.DataBaseManupilate.entity.User;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM User")
    List<User> getAll();

    @Query("SELECT * FROM User WHERE id = :id")
    User getById(int id);

    @Query("SELECT * FROM User WHERE did = :did")
    User getUserByDid(String did);

    @Query("SELECT * FROM User WHERE id IN (:ids)")
    List<User> loadAllByIds(List<Integer> ids);

    @Insert
    void insertAll(User... items);

    @Insert
    long insert(User item);

    @Delete
    void delete(User item);

    @Query("DELETE FROM User")
    void deleteAll();
}
