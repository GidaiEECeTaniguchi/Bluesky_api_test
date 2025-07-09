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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<BasePost> postList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(BasePost post);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public PostAdapter(List<BasePost> postList) {
        this.postList = postList;
    }

    public void setPostList(List<BasePost> newPostList) {
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
        BasePost post = postList.get(position);
        holder.contentTextView.setText(post.getContent());
        holder.authorTextView.setText("Author ID: " + post.getAuthor_id()); // 仮の表示
        holder.timestampTextView.setText(post.getCreated_at()); // 仮の表示

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(post);
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