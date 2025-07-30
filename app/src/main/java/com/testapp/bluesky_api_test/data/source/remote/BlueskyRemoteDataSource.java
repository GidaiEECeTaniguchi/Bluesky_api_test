package com.testapp.bluesky_api_test.data.source.remote;

import com.testapp.bluesky_api_test.bluesky.BlueskyPostInfo;
import work.socialhub.kbsky.auth.BearerTokenAuthProvider;

import java.util.List;

public interface BlueskyRemoteDataSource {
    List<BlueskyPostInfo> fetchTimeline(BearerTokenAuthProvider authProvider) throws Exception;
    List<BlueskyPostInfo> fetchAuthorFeed(BearerTokenAuthProvider authProvider, String actorIdentifier) throws Exception;
    List<work.socialhub.kbsky.model.app.bsky.actor.ActorDefsProfileView> fetchAllFollowingUsers(BearerTokenAuthProvider authProvider, String userDid);
}
