package com.testapp.bluesky_api_test.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.testapp.bluesky_api_test.R;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupTagAssignment;
import java.util.List;

public class GroupTagAssignmentAdapter extends RecyclerView.Adapter<GroupTagAssignmentAdapter.GroupTagAssignmentViewHolder> {

    private List<GroupTagAssignment> tagAssignmentList;
    private boolean isEditMode = false;

    public GroupTagAssignmentAdapter(List<GroupTagAssignment> tagAssignmentList) {
        this.tagAssignmentList = tagAssignmentList;
    }

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
        notifyDataSetChanged(); // データセットの変更を通知してUIを更新
    }

    @NonNull
    @Override
    public GroupTagAssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_tag_assignment, parent, false);
        return new GroupTagAssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupTagAssignmentViewHolder holder, int position) {
        GroupTagAssignment tagAssignment = tagAssignmentList.get(position);
        holder.groupIdTextView.setText("Group ID: " + tagAssignment.getGroup_id());
        holder.tagIdTextView.setText("Tag ID: " + tagAssignment.getTag_id());

        // 編集モードに応じて編集ボタンの表示を切り替える
        holder.editButton.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return tagAssignmentList.size();
    }

    static class GroupTagAssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView groupIdTextView;
        TextView tagIdTextView;
        ImageView editButton;

        GroupTagAssignmentViewHolder(View itemView) {
            super(itemView);
            groupIdTextView = itemView.findViewById(R.id.tag_assignment_group_id_text_view);
            tagIdTextView = itemView.findViewById(R.id.tag_assignment_tag_id_text_view);
            editButton = itemView.findViewById(R.id.edit_button);
        }
    }
}
