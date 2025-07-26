package com.testapp.bluesky_api_test.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.testapp.bluesky_api_test.R;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.BasePost;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.PostWithAuthorName;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<PostWithAuthorName> postList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PostWithAuthorName post);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public PostAdapter(List<PostWithAuthorName> postList) {
        this.postList = postList;
    }

    public void setPostList(List<PostWithAuthorName> newPostList) {
        Log.d("PostAdapter", "Setting new post list. Count: " + newPostList.size());
        this.postList.clear();
        this.postList.addAll(newPostList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostWithAuthorName postWithAuthorName = postList.get(position);
        holder.contentTextView.setText(postWithAuthorName.content);
        holder.authorTextView.setText("Author: " + postWithAuthorName.authorHandle);
        holder.timestampTextView.setText(postWithAuthorName.created_at);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(postWithAuthorName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView contentTextView;
        TextView authorTextView;
        TextView timestampTextView;

        PostViewHolder(View itemView) {
            super(itemView);
            contentTextView = itemView.findViewById(R.id.text_post_content);
            authorTextView = itemView.findViewById(R.id.text_post_author);
            timestampTextView = itemView.findViewById(R.id.text_post_timestamp);
        }
    }
}