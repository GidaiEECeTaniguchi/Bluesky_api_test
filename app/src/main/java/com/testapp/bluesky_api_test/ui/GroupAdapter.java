package com.testapp.bluesky_api_test.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.testapp.bluesky_api_test.R;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupEntity; // 本物の GroupEntity をインポート
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<GroupEntity> groupList;

    public GroupAdapter(List<GroupEntity> groupList) {
        this.groupList = groupList;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        GroupEntity group = groupList.get(position);
        holder.groupNameTextView.setText(group.getName()); // getName() を使う
        // ここでアイコンを設定する処理を追加する
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        ImageView groupIconImageView;
        TextView groupNameTextView;

        GroupViewHolder(View itemView) {
            super(itemView);
            groupIconImageView = itemView.findViewById(R.id.icon_group);
            groupNameTextView = itemView.findViewById(R.id.text_group_name);
        }
    }
}
