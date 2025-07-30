package com.testapp.bluesky_api_test.data.source.local;

import com.testapp.bluesky_api_test.data.source.local.BlueskyLocalDataSource;
import android.content.Context;
import androidx.lifecycle.LiveData;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabase;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabaseSingleton;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.AuthorDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.BasePostDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.UserDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.Author;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.PostWithAuthorName;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.User;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class BlueskyLocalDataSourceImpl implements BlueskyLocalDataSource {

    private final BasePostDao basePostDao;
    private final AuthorDao authorDao;
    private final UserDao userDao;


    public BlueskyLocalDataSourceImpl(Context context) {
        AppDatabase db = AppDatabaseSingleton.getInstance(context);
        this.basePostDao = db.basePostDao();
        this.authorDao = db.authorDao();
        this.userDao = db.userDao();
    }

    @Override
    public LiveData<List<BasePost>> getSavedPostsFromDb() {
        return basePostDao.getAll();
    }

    @Override
    public long insertPostToDb(BasePost post) {
        if (basePostDao.getPostByUri(post.getUri()) == null) {
            return basePostDao.insert(post);
        }
        return -1;
    }

    @Override
    public BasePost getPostByIdFromDb(int id) {
        return basePostDao.getById(id);
    }

    @Override
    public List<BasePost> getPostsByUserIdFromDb(int userId) {
        return basePostDao.getPostsByUserId(userId);
    }

    @Override
    public List<BasePost> getPostsByAuthorIdFromDb(int authorId) {
        return basePostDao.getPostsByAuthorId(authorId);
    }

    @Override
    public LiveData<List<PostWithAuthorName>> getAllPostsWithAuthorName() {
        return basePostDao.getAllPostsWithAuthorName();
    }

    @Override
    public LiveData<List<BasePost>> getAllPosts() {
        return basePostDao.getAll();
    }

    @Override
    public void insertAllPosts(List<BasePost> posts) {
        List<BasePost> postsToInsert = new ArrayList<>();
        for (BasePost post : posts) {
            if (basePostDao.getPostByUri(post.getUri()) == null) {
                postsToInsert.add(post);
            }
        }
        if (!postsToInsert.isEmpty()) {
            basePostDao.insertAll(postsToInsert.toArray(new BasePost[0]));
        }
    }

    @Override
    public Author getAuthorByHandleFromDb(String handle) {
        return authorDao.getAuthorByHandle(handle);
    }

    @Override
    public Author getAuthorByDidFromDb(String did) {
        return null;
    }

    @Override
    public Author getAuthorByIdFromDb(int id) {
        return null;
    }

    @Override
    public List<Author> getAllAuthorsFromDb() {
        return authorDao.getAllAuthors();
    }

    @Override
    public Author insertAuthorToDb(Author author) {
        long id = authorDao.insert(author);
        if (id != -1) {
            return authorDao.getAuthorById((int) id);
        }
        return null;
    }

    @Override
    public User getUserByDidFromDb(String did) {
        return userDao.getUserByDid(did);
    }
}
