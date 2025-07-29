package com.testapp.bluesky_api_test.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabase;
import com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabaseSingleton;
import com.testapp.bluesky_api_test.DataBaseManupilate.dao.*;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.*;
import com.testapp.bluesky_api_test.repository.GroupAnnotationRepository;
import com.testapp.bluesky_api_test.repository.GroupRefRepository;
import com.testapp.bluesky_api_test.repository.GroupTagAssignmentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroupEditViewModel extends AndroidViewModel {

    private MutableLiveData<GroupEntity> group;
    private MutableLiveData<List<BasePost>> groupMembers;
    private MutableLiveData<List<GroupAnnotation>> groupAnnotations;
    private MutableLiveData<List<GroupRef>> groupRefs;
    private MutableLiveData<List<GroupTagAssignment>> groupTagAssignments;
    private MutableLiveData<List<Tag>> allTags;

    private GroupEntityDao groupEntityDao;
    private GroupMemberDao groupMemberDao;
    private BasePostDao basePostDao;
    private TagDao tagDao;
    private TagAssignmentDao tagAssignmentDao;
    private GroupAnnotationRepository groupAnnotationRepository;
    private GroupRefRepository groupRefRepository;
    private GroupTagAssignmentRepository groupTagAssignmentRepository;

    private ExecutorService executorService;

    public GroupEditViewModel(Application application) {
        super(application);
        group = new MutableLiveData<>();
        groupMembers = new MutableLiveData<>();
        groupAnnotations = new MutableLiveData<>();
        groupRefs = new MutableLiveData<>();
        groupTagAssignments = new MutableLiveData<>();
        allTags = new MutableLiveData<>();

        AppDatabase db = AppDatabaseSingleton.getInstance(application);
        groupEntityDao = db.groupEntityDao();
        groupMemberDao = db.groupMemberDao();
        basePostDao = db.basePostDao();
        tagDao = db.tagDao();
        tagAssignmentDao = db.tagAssignmentDao();
        groupAnnotationRepository = new GroupAnnotationRepository(application);
        groupRefRepository = new GroupRefRepository(application);
        groupTagAssignmentRepository = new GroupTagAssignmentRepository(application);

        executorService = Executors.newSingleThreadExecutor();
        loadAllTags();
    }

    public LiveData<GroupEntity> getGroup() { return group; }
    public LiveData<List<BasePost>> getGroupMembers() { return groupMembers; }
    public LiveData<List<GroupAnnotation>> getGroupAnnotations() { return groupAnnotations; }
    public LiveData<List<GroupRef>> getGroupRefs() { return groupRefs; }
    public LiveData<List<GroupTagAssignment>> getGroupTagAssignments() { return groupTagAssignments; }
    public LiveData<List<Tag>> getAllTags() { return allTags; }

    public void loadAllTags() {
        executorService.execute(() -> {
            List<Tag> tags = tagDao.getAll();
            allTags.postValue(tags);
        });
    }

    public void createTag(String tagName) {
        executorService.execute(() -> {
            Tag newTag = new Tag(tagName, ""); // scopeは今は空
            tagDao.insert(newTag);
            loadAllTags(); // タグリストを再読み込みしてUIを更新
        });
    }

    public void assignTagsToPost(int postId, List<Integer> tagIds) {
        executorService.execute(() -> {
            for (Integer tagId : tagIds) {
                TagAssignment newAssignment = new TagAssignment(postId, tagId);
                tagAssignmentDao.insert(newAssignment);
            }
        });
    }

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

    public void addGroupMember(int groupId, int postId) {
        executorService.execute(() -> {
            GroupMember newMember = new GroupMember(groupId, postId, 0);
            groupMemberDao.insert(newMember);
            loadGroupAndMembers(groupId);
        });
    }

    public void removeGroupMember(int groupId, int postId) {
        executorService.execute(() -> {
            GroupMember memberToRemove = new GroupMember(groupId, postId, 0);
            groupMemberDao.delete(memberToRemove);
            loadGroupAndMembers(groupId);
        });
    }

    public void addNewRefToGroup(int groupId, String title, String refPath) {
        executorService.execute(() -> {
            GroupRef newRef = new GroupRef(groupId, title, "", refPath, 0);
            groupRefRepository.saveGroupRef(newRef);
            loadGroupAndMembers(groupId);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}