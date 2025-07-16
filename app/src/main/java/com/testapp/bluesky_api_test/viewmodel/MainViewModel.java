package com.testapp.bluesky_api_test.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.testapp.bluesky_api_test.bluesky.BlueskyPostInfo;
import com.testapp.bluesky_api_test.repository.AuthRepository;
import com.testapp.bluesky_api_test.repository.AuthorRepository;
import com.testapp.bluesky_api_test.repository.PostRepository;
import work.socialhub.kbsky.auth.BearerTokenAuthProvider;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.Author;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final AuthorRepository authorRepository;
    private final PostRepository postRepository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final MutableLiveData<List<BlueskyPostInfo>> _timelinePosts = new MutableLiveData<>();
    public LiveData<List<BlueskyPostInfo>> getTimelinePosts() {
        return _timelinePosts;
    }

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> getIsLoading() {
        return _isLoading;
    }

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> getErrorMessage() {
        return _errorMessage;
    }

    public MainViewModel(AuthRepository authRepository, AuthorRepository authorRepository, PostRepository postRepository) {
        this.authRepository = authRepository;
        this.authorRepository = authorRepository;
        this.postRepository = postRepository;
    }

    public void fetchTimeline() {
        _isLoading.setValue(true);
        _errorMessage.setValue(null);
        executorService.execute(() -> {
            try {
                // 認証情報を取得
                BearerTokenAuthProvider authProvider = authRepository.getAuthProvider();
                if (authProvider == null) {
                    _errorMessage.postValue("ログインしていません。");
                    return;
                }

                // タイムラインを取得
                List<BlueskyPostInfo> posts = postRepository.fetchTimelineFromApi(authProvider);
                _timelinePosts.postValue(posts);

                // 取得したタイムラインをデータベースに保存 (例: PostRepository内で処理することも可能)
                // ここではViewModelの責務として、取得したデータをDBに保存する処理を記述
                for (BlueskyPostInfo postInfo : posts) {
                    // Authorを保存または取得
                    // DIDは後で取得するか、必要に応じて更新
                    Author author = authorRepository.getAuthorByHandleFromDb(postInfo.getAuthorHandle());
                    if (author == null) {
                        author = new Author(postInfo.getAuthorHandle(), "");
                        authorRepository.insertAuthorToDb(author);
                        // DBから再度取得して、IDがセットされたAuthorを取得する
                        author = authorRepository.getAuthorByHandleFromDb(postInfo.getAuthorHandle());
                    }

                    // BasePostを保存
                    // ここではuser_idを仮に1としています。
                    BasePost basePost = new BasePost(postInfo.getPostUri(), postInfo.getCid(), 1, author.getId(), postInfo.getText(), postInfo.getCreatedAt());
                    postRepository.insertPostToDb(basePost);
                }

            } catch (Exception e) {
                _errorMessage.postValue("Failed to fetch timeline: " + e.getMessage());
            } finally {
                _isLoading.postValue(false);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
        authorRepository.shutdown(); // AuthorRepositoryのExecutorServiceもシャットダウン
    }
}
