package com.aether.myaether.data.source.local;

import androidx.lifecycle.LiveData;
import com.aether.myaether.DataBaseManupilate.entity.Author;
import com.aether.myaether.DataBaseManupilate.entity.BasePost;
import com.aether.myaether.DataBaseManupilate.entity.PostWithAuthorName;
import com.aether.myaether.DataBaseManupilate.entity.User;

import java.util.List;

public interface BlueskyLocalDataSource {
    LiveData<List<BasePost>> getSavedPostsFromDb();
    long insertPostToDb(BasePost post);
    BasePost getPostByIdFromDb(int id);
    List<BasePost> getPostsByUserIdFromDb(int userId);
    List<BasePost> getPostsByAuthorIdFromDb(int authorId);
    LiveData<List<PostWithAuthorName>> getAllPostsWithAuthorName();
    LiveData<List<BasePost>> getAllPosts();
    void insertAllPosts(List<BasePost> posts);
    Author getAuthorByHandleFromDb(String handle);
    Author getAuthorByDidFromDb(String did);
    Author getAuthorByIdFromDb(int id);
    List<Author> getAllAuthorsFromDb();
    User getUserByDidFromDb(String did);
    Author insertAuthorToDb(Author author);
}
