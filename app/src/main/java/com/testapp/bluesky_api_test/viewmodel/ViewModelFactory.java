
package com.testapp.bluesky_api_test.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.testapp.bluesky_api_test.repository.AuthRepository;
import com.testapp.bluesky_api_test.repository.AuthorRepository;
import com.testapp.bluesky_api_test.repository.PostRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final AuthRepository authRepository;
    private final AuthorRepository authorRepository;
    private final PostRepository postRepository;

    public ViewModelFactory(AuthRepository authRepository, AuthorRepository authorRepository, PostRepository postRepository) {
        this.authRepository = authRepository;
        this.authorRepository = authorRepository;
        this.postRepository = postRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(authRepository, authorRepository, postRepository);
        }
        // 他のViewModelが必要な場合はここに追加
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
