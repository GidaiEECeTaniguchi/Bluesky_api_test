## 開発日記

### 2025-07-02
- DBOPEdevブランチをmainにマージしたよ。
- コンフリクトはなかったから、スムーズに統合できたね。
- これからはここに開発メモを残していくよ。

### 2025-07-07
- `BaseFragmentInterface`を作成し、`MainFragment`, `NotificationsFragment`, `ProfileFragment`, `GroupFragment`に適用したよ。
- 不要になった`GroupListFragment`を削除したよ。（`mobile_navigation.xml`と`MainActivity.java`の関連記述も削除）
- `PostAdapter`と`item_post.xml`を作成したよ。
- ブランチを`UIdev`から`DBOPEdev`に切り替えたよ。

### 2025-07-08
- `PostRepository`に`getPostsByUserIdFromDb`と`getPostsByAuthorIdFromDb`を追加したよ。
- `AuthorRepository`に`getAuthorByIdFromDb`と`getAllAuthorsFromDb`を追加したよ。
- `GroupRepository`に`getAllGroupMembersFromDb`と`getAllGroupMembersByIdsFromDb`を追加したよ。
- `GroupAnnotationRepository`に`getAllGroupAnnotationsFromDb`と`getGroupAnnotationsByIdsFromDb`を追加したよ。
- `GroupRefRepository`に`getAllGroupRefsFromDb`と`getGroupRefsByIdsFromDb`を追加したよ。
- `GroupRewriteRepository`に`getAllGroupRewritesFromDb`と`getGroupRewritesByIdsFromDb`を追加したよ。
- `GroupTagAssignmentRepository`に`getAllGroupTagAssignmentsFromDb`と`getAllGroupTagAssignmentsByIdsFromDb`を追加したよ。
- `TagAssignmentRepository`に`getAllTagAssignmentsFromDb`と`getTagAssignmentsByTagIdFromDb`を追加したよ。
- `TagRepository`に`getAllTagsFromDb`と`getTagsByIdsFromDb`を追加したよ。
- `PostRepository`に`getAllPostsFromDb`と`convertBasePostsToBlueskyPostInfo`を追加したが、その後元に戻したよ。
- `GroupFragment`から`GroupEditActivity`への画面遷移の基本部分を実装したよ。
- `GroupEditActivity`と`activity_group_edit.xml`を新規作成し、`AndroidManifest.xml`に登録したよ。
- `GroupAdapter`にアイテムクリックリスナーを追加したよ。
