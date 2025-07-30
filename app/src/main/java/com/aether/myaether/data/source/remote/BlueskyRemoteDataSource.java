package com.aether.myaether.data.source.remote;

import com.aether.myaether.bluesky.BlueskyPostInfo;
import work.socialhub.kbsky.auth.BearerTokenAuthProvider;

import java.util.List;

public interface BlueskyRemoteDataSource {
    List<BlueskyPostInfo> fetchTimeline(BearerTokenAuthProvider authProvider) throws Exception;
    List<BlueskyPostInfo> fetchAuthorFeed(BearerTokenAuthProvider authProvider, String actorIdentifier) throws Exception;
    List<work.socialhub.kbsky.model.app.bsky.actor.ActorDefsProfileView> fetchAllFollowingUsers(BearerTokenAuthProvider authProvider, String userDid);
}
