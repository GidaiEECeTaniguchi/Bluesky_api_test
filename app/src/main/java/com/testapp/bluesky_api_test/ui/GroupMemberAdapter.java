package com.testapp.bluesky_api_test.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.testapp.bluesky_api_test.R;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost;
import java.util.List;

/**
 * グループメンバー（投稿）のリストを表示するためのRecyclerView.Adapter。
 * 各アイテムはBasePostオブジェクトを表します。
 */
public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.GroupMemberViewHolder> {

    private List<BasePost> postList;
    private boolean isEditMode = false;

    /**
     * コンストラクタ。
     * @param postList 表示するBasePostのリスト
     */
    public GroupMemberAdapter(List<BasePost> postList) {
        this.postList = postList;
    }

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
        notifyDataSetChanged(); // データセットの変更を通知してUIを更新
    }

    @NonNull
    @Override
    public GroupMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_member, parent, false);
        return new GroupMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMemberViewHolder holder, int position) {
        BasePost post = postList.get(position);
        holder.postContentTextView.setText(post.getContent());
        // 他の情報を表示する場合はここに追加

        // 編集モードに応じて編集ボタンの表示を切り替える
        holder.editButton.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    /**
     * 各リストアイテムのビューを保持するViewHolderクラス。
     */
    static class GroupMemberViewHolder extends RecyclerView.ViewHolder {
        TextView postContentTextView;
        ImageView editButton;

        GroupMemberViewHolder(View itemView) {
            super(itemView);
            postContentTextView = itemView.findViewById(R.id.text_post_content);
            editButton = itemView.findViewById(R.id.edit_button);
        }
    }
}
