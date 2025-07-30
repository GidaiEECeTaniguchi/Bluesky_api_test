package com.aether.myaether.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import com.aether.myaether.DataBaseManupilate.entity.BasePost;
import com.aether.myaether.DataBaseManupilate.entity.PostWithAuthorName;
import com.aether.myaether.repository.PostRepository;
import com.aether.myaether.data.source.local.BlueskyLocalDataSourceImpl;
import com.aether.myaether.data.source.remote.BlueskyRemoteDataSourceImpl;

public class PostSelectViewModel extends androidx.lifecycle.AndroidViewModel {

    private PostRepository postRepository;
    private androidx.lifecycle.LiveData<java.util.List<com.aether.myaether.DataBaseManupilate.entity.PostWithAuthorName>> allPosts;

    public PostSelectViewModel(@NonNull Application application) {
        super(application);
        BlueskyRemoteDataSourceImpl blueskyRemoteDataSource = new BlueskyRemoteDataSourceImpl();
        BlueskyLocalDataSourceImpl blueskyLocalDataSource = new BlueskyLocalDataSourceImpl(application);
        postRepository = new PostRepository(application, blueskyRemoteDataSource, blueskyLocalDataSource);
        allPosts = postRepository.getAllPostsWithAuthorName();
    }

    public androidx.lifecycle.LiveData<java.util.List<com.aether.myaether.DataBaseManupilate.entity.PostWithAuthorName>> getAllPosts() {
        return allPosts;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        postRepository.shutdown();
    }
}