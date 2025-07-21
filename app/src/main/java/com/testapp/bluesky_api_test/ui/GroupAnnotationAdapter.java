package com.testapp.bluesky_api_test.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.testapp.bluesky_api_test.R;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupAnnotation;
import java.util.List;

public class GroupAnnotationAdapter extends RecyclerView.Adapter<GroupAnnotationAdapter.GroupAnnotationViewHolder> {

    private List<GroupAnnotation> annotationList;
    private boolean isEditMode = false;

    public GroupAnnotationAdapter(List<GroupAnnotation> annotationList) {
        this.annotationList = annotationList;
    }

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
        notifyDataSetChanged(); // データセットの変更を通知してUIを更新
    }

    @NonNull
    @Override
    public GroupAnnotationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_annotation, parent, false);
        return new GroupAnnotationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupAnnotationViewHolder holder, int position) {
        GroupAnnotation annotation = annotationList.get(position);
        holder.conceptTextView.setText(annotation.getConcept());
        holder.descriptionTextView.setText(annotation.getDescription());

        // 編集モードに応じて編集ボタンの表示を切り替える
        holder.editButton.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return annotationList.size();
    }

    static class GroupAnnotationViewHolder extends RecyclerView.ViewHolder {
        TextView conceptTextView;
        TextView descriptionTextView;
        ImageView editButton;

        GroupAnnotationViewHolder(View itemView) {
            super(itemView);
            conceptTextView = itemView.findViewById(R.id.annotation_concept_text_view);
            descriptionTextView = itemView.findViewById(R.id.annotation_description_text_view);
            editButton = itemView.findViewById(R.id.edit_button);
        }
    }
}
