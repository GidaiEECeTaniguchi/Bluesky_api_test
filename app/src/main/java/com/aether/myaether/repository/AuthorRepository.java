package com.aether.myaether.repository;

import android.util.Log;

import com.aether.myaether.DataBaseManupilate.entity.Author;
import com.aether.myaether.data.source.local.BlueskyLocalDataSource;
import com.aether.myaether.data.source.remote.BlueskyRemoteDataSource;

import work.socialhub.kbsky.auth.BearerTokenAuthProvider;
import work.socialhub.kbsky.model.app.bsky.actor.ActorDefsProfileView;

import java.util.List;

import javax.inject.Inject;

public class AuthorRepository {

    private static final String TAG = "AuthorRepository";
    private final BlueskyRemoteDataSource blueskyRemoteDataSource;
    private final BlueskyLocalDataSource blueskyLocalDataSource;


    public AuthorRepository(
            BlueskyRemoteDataSource blueskyRemoteDataSource,
            BlueskyLocalDataSource blueskyLocalDataSource) {
        this.blueskyRemoteDataSource = blueskyRemoteDataSource;
        this.blueskyLocalDataSource = blueskyLocalDataSource;
    }

    /**
     * Bluesky APIからフォローしている全ユーザーのリストを取得し、データベースに保存します。
     * @param authProvider 認証プロバイダー
     * @param userDid ユーザーのDID
     * @return フォローしているユーザーのリスト
     */
    public List<ActorDefsProfileView> fetchAndSaveFollowingFromApi(BearerTokenAuthProvider authProvider, String userDid) {
        List<ActorDefsProfileView> followingProfiles = blueskyRemoteDataSource.fetchAllFollowingUsers(authProvider, userDid);
        saveFollowingAuthorsToDb(followingProfiles);
        return followingProfiles;
    }

    public Author insertAuthorToDb(Author author) {
        return blueskyLocalDataSource.insertAuthorToDb(author);
    }

    public void saveFollowingAuthorsToDb(List<ActorDefsProfileView> profileViews) {
        for (ActorDefsProfileView profileView : profileViews) {
            if (profileView.getHandle() != null && profileView.getDid() != null) {
                Author author = new Author(profileView.getHandle(), profileView.getDid());
                blueskyLocalDataSource.insertAuthorToDb(author);
            } else {
                Log.w(TAG, "Skipping author due to null handle or DID: " + profileView);
            }
        }
        Log.d(TAG, "Finished saving following authors to DB.");
    }

    public Author getAuthorByHandleFromDb(String handle) {
        return blueskyLocalDataSource.getAuthorByHandleFromDb(handle);
    }

    public Author getAuthorByDidFromDb(String did) {
        return blueskyLocalDataSource.getAuthorByDidFromDb(did);
    }

    public Author getAuthorByIdFromDb(int id) {
        return blueskyLocalDataSource.getAuthorByIdFromDb(id);
    }

    public List<Author> getAllAuthors() {
        return blueskyLocalDataSource.getAllAuthorsFromDb();
    }

    public void shutdown() {
        // BlueskyOperations は ExecutorService を持っていないので、ここでは何もしない
        // 必要であれば、BlueskyOperations 内部で ExecutorService を管理し、ここでシャットダウンを呼び出す
    }
}
