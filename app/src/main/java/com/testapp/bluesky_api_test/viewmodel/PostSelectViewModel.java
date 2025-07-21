package com.testapp.bluesky_api_test.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;

import com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost;
import com.testapp.bluesky_api_test.repository.PostRepository;

public class PostSelectViewModel extends androidx.lifecycle.AndroidViewModel {

    private PostRepository postRepository;
    private androidx.lifecycle.LiveData<java.util.List<com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost>> allPosts;

    public PostSelectViewModel(@NonNull Application application) {
        super(application);
        postRepository = new PostRepository(application);
        allPosts = postRepository.getAllPosts();
    }

    public androidx.lifecycle.LiveData<java.util.List<com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost>> getAllPosts() {
        return allPosts;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        postRepository.shutdown();
    }
}