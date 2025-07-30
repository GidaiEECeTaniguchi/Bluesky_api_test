package com.aether.myaether.di;

import android.app.Application;
import android.content.Context;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import com.aether.myaether.repository.AuthRepository;
import com.aether.myaether.repository.PostRepository;
import com.aether.myaether.repository.AuthorRepository;
import com.aether.myaether.repository.UserRepository;
import com.aether.myaether.data.source.remote.BlueskyRemoteDataSource;
import com.aether.myaether.data.source.remote.BlueskyRemoteDataSourceImpl;
import com.aether.myaether.data.source.local.BlueskyLocalDataSource;
import com.aether.myaether.data.source.local.BlueskyLocalDataSourceImpl;
import com.aether.myaether.DataBaseManupilate.AppDatabase;
import com.aether.myaether.DataBaseManupilate.AppDatabaseSingleton;
import com.aether.myaether.DataBaseManupilate.dao.UserDao;

import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    public AuthRepository provideAuthRepository(@ApplicationContext Context context) {
        return new AuthRepository(context);
    }

    @Provides
    @Singleton
    public BlueskyRemoteDataSource provideBlueskyRemoteDataSource() {
        return new BlueskyRemoteDataSourceImpl();
    }

    @Provides
    @Singleton
    public BlueskyLocalDataSource provideBlueskyLocalDataSource(@ApplicationContext Context context) {
        return new BlueskyLocalDataSourceImpl(context);
    }

    @Provides
    @Singleton
    public PostRepository providePostRepository(
            @ApplicationContext Context context,
            BlueskyRemoteDataSource blueskyRemoteDataSource,
            BlueskyLocalDataSource blueskyLocalDataSource) {
        return new PostRepository(context, blueskyRemoteDataSource, blueskyLocalDataSource);
    }

    @Provides
    @Singleton
    public AuthorRepository provideAuthorRepository(
            BlueskyRemoteDataSource blueskyRemoteDataSource,
            BlueskyLocalDataSource blueskyLocalDataSource) {
        return new AuthorRepository(blueskyRemoteDataSource, blueskyLocalDataSource);
    }

    @Provides
    @Singleton
    public UserRepository provideUserRepository(@ApplicationContext Application application) {
        return new UserRepository(application);
    }

    @Provides
    @Singleton
    public UserDao provideUserDao(@ApplicationContext Context context) {
        return AppDatabaseSingleton.getInstance(context).userDao();
    }
}