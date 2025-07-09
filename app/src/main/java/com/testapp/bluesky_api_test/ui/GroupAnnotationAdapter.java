package com.testapp.bluesky_api_test.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.testapp.bluesky_api_test.R;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupAnnotation;
import java.util.List;

public class GroupAnnotationAdapter extends RecyclerView.Adapter<GroupAnnotationAdapter.GroupAnnotationViewHolder> {

    private List<GroupAnnotation> annotationList;

    public GroupAnnotationAdapter(List<GroupAnnotation> annotationList) {
        this.annotationList = annotationList;
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
    }

    @Override
    public int getItemCount() {
        return annotationList.size();
    }

    static class GroupAnnotationViewHolder extends RecyclerView.ViewHolder {
        TextView conceptTextView;
        TextView descriptionTextView;

        GroupAnnotationViewHolder(View itemView) {
            super(itemView);
            conceptTextView = itemView.findViewById(R.id.annotation_concept_text_view);
            descriptionTextView = itemView.findViewById(R.id.annotation_description_text_view);
        }
    }
}
