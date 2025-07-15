package com.testapp.bluesky_api_test.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.testapp.bluesky_api_test.repository.PostRepository;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final PostRepository postRepository;
    private final LiveData<List<BasePost>> timelinePosts;

    public MainViewModel(Application application) {
        super(application);
        postRepository = new PostRepository(application);
        timelinePosts = postRepository.getSavedPostsFromDb();
    }

    public LiveData<List<BasePost>> getTimelinePosts() {
        return timelinePosts;
    }
}
