package com.testapp.bluesky_api_test.DataBaseManupilate.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.testapp.bluesky_api_test.DataBaseManupilate.entity.Author;

import java.util.List;

@Dao
public interface AuthorDao {
    @Insert
    long insert(Author author);

    @Update
    void update(Author author);

    @Delete
    void delete(Author author);

    @Query("SELECT * FROM authors WHERE id = :id")
    Author getAuthorById(int id);

    @Query("SELECT * FROM authors WHERE handle = :handle")
    Author getAuthorByHandle(String handle);

    @Query("SELECT * FROM authors WHERE did = :did")
    Author getAuthorByDid(String did);

    @Query("SELECT * FROM authors")
    List<Author> getAllAuthors();
}