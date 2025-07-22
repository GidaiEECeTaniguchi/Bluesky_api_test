
package com.testapp.bluesky_api_test.task;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.testapp.bluesky_api_test.DataBaseManupilate.entity.User;
import com.testapp.bluesky_api_test.bluesky.BlueskyOperations;
import com.testapp.bluesky_api_test.repository.AuthRepository;
import com.testapp.bluesky_api_test.repository.PostRepository;
import com.testapp.bluesky_api_test.repository.AuthorRepository;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.Author;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost;
import com.testapp.bluesky_api_test.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import work.socialhub.kbsky.BlueskyTypes;
import work.socialhub.kbsky.auth.BearerTokenAuthProvider;
import work.socialhub.kbsky.model.app.bsky.embed.EmbedRecordViewRecord;
import work.socialhub.kbsky.model.app.bsky.embed.EmbedVideoView;
import work.socialhub.kbsky.model.app.bsky.feed.FeedDefsFeedViewPost;
import work.socialhub.kbsky.model.app.bsky.feed.FeedDefsPostView;
import work.socialhub.kbsky.model.app.bsky.feed.FeedPost;
import work.socialhub.kbsky.model.share.RecordUnion;
import work.socialhub.kbsky.model.app.bsky.embed.EmbedViewUnion;
import work.socialhub.kbsky.model.app.bsky.embed.EmbedRecordView;
import work.socialhub.kbsky.model.app.bsky.embed.EmbedImagesView;
import work.socialhub.kbsky.model.app.bsky.embed.EmbedExternalView;
import work.socialhub.kbsky.model.app.bsky.embed.EmbedRecordWithMediaView;
import work.socialhub.kbsky.model.app.bsky.embed.EmbedImagesViewImage;

public class TimelineCrawlerWorker extends Worker {

    private static final String TAG = "TimelineCrawlerWorker";
    private final Context context;

    public TimelineCrawlerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "TimelineCrawlerWorker started.");

        AuthRepository authRepository = new AuthRepository(context);
        BearerTokenAuthProvider authProvider = authRepository.getAuthProvider(); // getLoginSession -> getAuthProvider

        if (authProvider == null) {
            Log.e(TAG, "User not logged in. Worker stopping.");
            return Result.failure();
        }

        try {
            // 1. データの収集
            BlueskyOperations blueskyOperations = new BlueskyOperations();
            List<FeedDefsFeedViewPost> timelinePosts = blueskyOperations.fetchTimeline(authProvider);

            if (timelinePosts == null || timelinePosts.isEmpty()) {
                Log.d(TAG, "No new posts fetched.");
                return Result.success();
            }

            // 2. データの処理とDB格納
            PostRepository postRepository = new PostRepository(context);
            AuthorRepository authorRepository = new AuthorRepository(context);
            List<BasePost> basePosts = new ArrayList<>();

            for (FeedDefsFeedViewPost feedViewPost : timelinePosts) {
                FeedDefsPostView postView = feedViewPost.getPost();
                if (feedViewPost.getReason() != null && "app.bsky.feed.defs#reasonRepost".equals(feedViewPost.getReason().getType())) {
                    Log.d(TAG, "Skipping repost: " + feedViewPost.getPost().getUri());
                    continue;
                }

                if (postView != null && postView.getRecord() != null) {
                    RecordUnion record = postView.getRecord();
                    if (BlueskyTypes.FeedPost.equals(record.getType())) {
                        FeedPost postContent = (FeedPost) record;

                        String content = postContent.getText();
                        if (content == null || content.isEmpty()) {
                            EmbedViewUnion embed = postView.getEmbed();
                            if (embed != null) {
                                if (embed instanceof EmbedRecordView) {
                                    EmbedRecordView recordEmbed = (EmbedRecordView) embed;
                                    if (recordEmbed.getRecord() instanceof EmbedRecordViewRecord) {
                                        EmbedRecordViewRecord actualRecord = (EmbedRecordViewRecord) recordEmbed.getRecord();
                                        if (actualRecord.getAuthor() != null) {
                                            content = "引用: " + actualRecord.getAuthor().getHandle() + " の投稿";
                                        }
                                    }
                                } else if (embed instanceof EmbedImagesView) {
                                    EmbedImagesView imagesEmbed = (EmbedImagesView) embed;
                                    if (imagesEmbed.getImages() != null && !imagesEmbed.getImages().isEmpty()) {
                                        StringBuilder imageAlts = new StringBuilder("画像: ");
                                        for (EmbedImagesViewImage image : imagesEmbed.getImages()) {
                                            if (image.getAlt() != null && !image.getAlt().isEmpty()) {
                                                imageAlts.append(image.getAlt()).append("; ");
                                            }
                                        }
                                        content = imageAlts.toString();
                                    }
                                } else if (embed instanceof EmbedVideoView) {
                                    // EmbedVideoView の処理を追加
                                    EmbedVideoView videoEmbed = (EmbedVideoView) embed;
                                    if (videoEmbed.getAsVideo() != null && videoEmbed.getAsVideo().getAlt() != null && !videoEmbed.getAsVideo().getAlt().isEmpty()) {
                                        content = "動画: " + videoEmbed.getAsVideo().getAlt();
                                    }
                                } else if (embed instanceof EmbedExternalView) {
                                    EmbedExternalView externalEmbed = (EmbedExternalView) embed;
                                    if (externalEmbed.getExternal() != null) {
                                        content = "外部リンク: " + externalEmbed.getExternal().getTitle();
                                    }
                                } else if (embed instanceof EmbedRecordWithMediaView) {
                                    EmbedRecordWithMediaView mediaEmbed = (EmbedRecordWithMediaView) embed;
                                    // Handle record part
                                    if (mediaEmbed.getRecord() != null) {
                                        content = "引用 (メディア付き)";
                                    }
                                    // Handle media part (e.g., images or external)
                                    if (mediaEmbed.getMedia() != null) {
                                        if (mediaEmbed.getMedia() instanceof EmbedImagesView) {
                                            EmbedImagesView imagesEmbed = (EmbedImagesView) mediaEmbed.getMedia();
                                            if (imagesEmbed.getImages() != null && !imagesEmbed.getImages().isEmpty()) {
                                                StringBuilder imageAlts = new StringBuilder(content.isEmpty() ? "画像: " : content + " 画像: ");
                                                for (EmbedImagesViewImage image : imagesEmbed.getImages()) {
                                                    if (image.getAlt() != null && !image.getAlt().isEmpty()) {
                                                        imageAlts.append(image.getAlt()).append("; ");
                                                    }
                                                }
                                                content = imageAlts.toString();
                                            }
                                        } else if (mediaEmbed.getMedia() instanceof EmbedExternalView) {
                                            EmbedExternalView externalEmbed = (EmbedExternalView) mediaEmbed.getMedia();
                                            if (externalEmbed.getExternal() != null) {
                                                content = (content.isEmpty() ? "外部リンク: " : content + " 外部リンク: ") + externalEmbed.getExternal().getTitle();
                                            }
                                        }
                                    }
                                }
                            }
                            if (content == null || content.isEmpty()) {
                                Log.d(TAG, "Empty post content after embed check. Post type: " + record.getType() + ", URI: " + postView.getUri());
                                content = "[内容なし]"; // Fallback for truly empty content
                            }
                        }

                        Author author = authorRepository.getAuthorByDidFromDb(postView.getAuthor().getDid());
                        if (author == null) {
                            // Authorの正しいコンストラクタを使ってインスタンスを作成
                            author = new Author(postView.getAuthor().getHandle(), postView.getAuthor().getDid());
                            // setDisplayName, setAvatar は存在しないので削除
                            author = authorRepository.insertAuthorToDb(author);
                            if (author == null) {
                                Log.e(TAG, "Failed to insert author: " + postView.getAuthor().getHandle());
                                continue; // Skip this post if author cannot be saved
                            }
                        }

                        
                        String createdAt = postContent.getCreatedAt();
                        String uri = postView.getUri();
                        String cid = postView.getCid();
                        
                        // BasePostのコンストラクタに合わせて user_id を取得する
                        // AuthRepositoryからDIDを取得し、それを使ってUserRepositoryからUserを取得し、IDを得る。
                        UserRepository userRepository = new UserRepository((Application) getApplicationContext());
                        String userDid = authRepository.getDid();
                        if (userDid == null) {
                            Log.e(TAG, "Could not get user DID. Worker stopping.");
                            return Result.failure();
                        }
                        User currentUser = userRepository.getExistingUserByDid(userDid);
                        if (currentUser == null) {
                            Log.e(TAG, "Could not find user with DID: " + userDid);
                            // 必要であれば、ここでユーザーをDBに再度追加する処理も検討できる
                            return Result.failure();
                        }
                        int userId = currentUser.getId(); 

                        BasePost basePost = new BasePost(uri, cid, userId, author.getId(), content, createdAt);
                        basePosts.add(basePost);
                    }
                }
            }

            if (!basePosts.isEmpty()) {
                postRepository.insertAllPosts(basePosts);
                Log.d(TAG, "Successfully saved " + basePosts.size() + " posts to database.");
            }

            Log.d(TAG, "TimelineCrawlerWorker finished successfully.");
            return Result.success();

        } catch (Exception e) {
            Log.e(TAG, "Worker failed", e);
            return Result.failure();
        }
    }
}
