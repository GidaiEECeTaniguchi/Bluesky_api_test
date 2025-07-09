package com.testapp.bluesky_api_test.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabase;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabaseSingleton;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.GroupEntityDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.GroupMemberDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.BasePostDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.GroupAnnotationDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.GroupRefDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.GroupTagAssignmentDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupEntity;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupMember;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupAnnotation;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupRef;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupTagAssignment;
import com.testapp.bluesky_api_test.repository.GroupAnnotationRepository;
import com.testapp.bluesky_api_test.repository.GroupRefRepository;
import com.testapp.bluesky_api_test.repository.GroupTagAssignmentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * グループ編集画面のデータを管理するViewModel。
 * 特定のグループの情報をロードし、そのグループに属する投稿（メンバー）のリストを管理します。
 */
public class GroupEditViewModel extends AndroidViewModel {

    private MutableLiveData<GroupEntity> group;
    private MutableLiveData<List<BasePost>> groupMembers;
    private MutableLiveData<List<GroupAnnotation>> groupAnnotations;
    private MutableLiveData<List<GroupRef>> groupRefs;
    private MutableLiveData<List<GroupTagAssignment>> groupTagAssignments;

    private GroupEntityDao groupEntityDao;
    private GroupMemberDao groupMemberDao;
    private BasePostDao basePostDao;
    private GroupAnnotationRepository groupAnnotationRepository;
    private GroupRefRepository groupRefRepository;
    private GroupTagAssignmentRepository groupTagAssignmentRepository;

    private ExecutorService executorService;

    /**
     * コンストラクタ。
     * データベースインスタンスとDAOを初期化します。
     * @param application アプリケーションコンテキスト
     */
    public GroupEditViewModel(Application application) {
        super(application);
        group = new MutableLiveData<>();
        groupMembers = new MutableLiveData<>();
        groupAnnotations = new MutableLiveData<>();
        groupRefs = new MutableLiveData<>();
        groupTagAssignments = new MutableLiveData<>();

        AppDatabase db = AppDatabaseSingleton.getInstance(application);
        groupEntityDao = db.groupEntityDao();
        groupMemberDao = db.groupMemberDao();
        basePostDao = db.basePostDao();
        groupAnnotationRepository = new GroupAnnotationRepository(application);
        groupRefRepository = new GroupRefRepository(application);
        groupTagAssignmentRepository = new GroupTagAssignmentRepository(application);

        executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * 監視可能なグループデータを返します。
     * @return LiveData<GroupEntity> グループデータ
     */
    public LiveData<GroupEntity> getGroup() {
        return group;
    }

    /**
     * 監視可能なグループメンバー（投稿）リストを返します。
     * @return LiveData<List<BasePost>> グループメンバーリスト
     */
    public LiveData<List<BasePost>> getGroupMembers() {
        return groupMembers;
    }

    /**
     * 監視可能なグループアノテーションリストを返します。
     * @return LiveData<List<GroupAnnotation>> グループアノテーションリスト
     */
    public LiveData<List<GroupAnnotation>> getGroupAnnotations() {
        return groupAnnotations;
    }

    /**
     * 監視可能なグループ参照リストを返します。
     * @return LiveData<List<GroupRef>> グループ参照リスト
     */
    public LiveData<List<GroupRef>> getGroupRefs() {
        return groupRefs;
    }

    /**
     * 監視可能なグループタグ割り当てリストを返します。
     * @return LiveData<List<GroupTagAssignment>> グループタグ割り当てリスト
     */
    public LiveData<List<GroupTagAssignment>> getGroupTagAssignments() {
        return groupTagAssignments;
    }

    /**
     * 指定されたグループIDに基づいて、グループ情報とそのメンバーをデータベースからロードします。
     * @param groupId ロードするグループのID
     */
    public void loadGroupAndMembers(int groupId) {
        executorService.execute(() -> {
            GroupEntity loadedGroup = groupEntityDao.getById(groupId);
            group.postValue(loadedGroup);

            List<GroupMember> members = groupMemberDao.getMembersByGroupId(groupId);
            List<BasePost> posts = new ArrayList<>();
            for (GroupMember member : members) {
                BasePost post = basePostDao.getById(member.getPost_id());
                if (post != null) {
                    posts.add(post);
                }
            }
            groupMembers.postValue(posts);

            List<GroupAnnotation> annotations = groupAnnotationRepository.getAnnotationsByGroupId(groupId);
            groupAnnotations.postValue(annotations);

            List<GroupRef> refs = groupRefRepository.getRefsByGroupId(groupId);
            groupRefs.postValue(refs);

            List<GroupTagAssignment> tagAssignments = groupTagAssignmentRepository.getTagAssignmentsByGroupId(groupId);
            groupTagAssignments.postValue(tagAssignments);
        });
    }

    /**
     * グループに新しいメンバー（投稿）を追加します。
     * @param groupId メンバーを追加するグループのID
     * @param postId 追加する投稿のID
     */
    public void addGroupMember(int groupId, int postId) {
        executorService.execute(() -> {
            // TODO: order の設定を考慮する
            GroupMember newMember = new GroupMember(groupId, postId, 0);
            groupMemberDao.insert(newMember);
            loadGroupAndMembers(groupId); // メンバーリストを再読み込み
        });
    }

    /**
     * グループからメンバー（投稿）を削除します。
     * @param groupId メンバーを削除するグループのID
     * @param postId 削除する投稿のID
     */
    public void removeGroupMember(int groupId, int postId) {
        executorService.execute(() -> {
            GroupMember memberToRemove = new GroupMember(groupId, postId, 0); // order は仮
            groupMemberDao.delete(memberToRemove);
            loadGroupAndMembers(groupId); // メンバーリストを再読み込み
        });
    }

    /**
     * グループに新しい参照を追加します。
     * @param groupId 参照を追加するグループのID
     * @param refId 追加する参照のID
     */
    public void addRefToGroup(int groupId, int refId) {
        executorService.execute(() -> {
            GroupRef originalRef = groupRefRepository.getGroupRefById(refId);
            if (originalRef != null) {
                // 新しいGroupRefオブジェクトを作成し、group_idを現在のグループに設定
                // idは自動生成されるので設定しない
                GroupRef newRef = new GroupRef(
                        groupId,
                        originalRef.getTitle(),
                        originalRef.getType(),
                        originalRef.getRef_path(),
                        originalRef.getOrder_in_group() // 順序もコピー
                );
                groupRefRepository.saveGroupRef(newRef);
                loadGroupAndMembers(groupId); // 参照リストを再読み込み
            }
        });
    }

    // TODO: メンバーの並べ替え機能を追加する


    /**
     * ViewModelが破棄される際にExecutorServiceをシャットダウンします。
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
