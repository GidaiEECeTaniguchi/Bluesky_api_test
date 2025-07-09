package com.testapp.bluesky_api_test.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.testapp.bluesky_api_test.R;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupRef;
import java.util.List;

public class GroupRefAdapter extends RecyclerView.Adapter<GroupRefAdapter.GroupRefViewHolder> {

    private List<GroupRef> refList;

    public GroupRefAdapter(List<GroupRef> refList) {
        this.refList = refList;
    }

    @NonNull
    @Override
    public GroupRefViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_ref, parent, false);
        return new GroupRefViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupRefViewHolder holder, int position) {
        GroupRef ref = refList.get(position);
        holder.titleTextView.setText(ref.getTitle());
        holder.typeTextView.setText(ref.getType());
        holder.pathTextView.setText(ref.getRef_path());
    }

    @Override
    public int getItemCount() {
        return refList.size();
    }

    static class GroupRefViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView typeTextView;
        TextView pathTextView;

        GroupRefViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.ref_title_text_view);
            typeTextView = itemView.findViewById(R.id.ref_type_text_view);
            pathTextView = itemView.findViewById(R.id.ref_path_text_view);
        }
    }
}
