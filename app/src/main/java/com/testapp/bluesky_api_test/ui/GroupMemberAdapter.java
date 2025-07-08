package com.testapp.bluesky_api_test.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    /**
     * コンストラクタ。
     * @param postList 表示するBasePostのリスト
     */
    public GroupMemberAdapter(List<BasePost> postList) {
        this.postList = postList;
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

        GroupMemberViewHolder(View itemView) {
            super(itemView);
            postContentTextView = itemView.findViewById(R.id.text_post_content);
        }
    }
}
