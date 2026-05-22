package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

import models.Post;

public class PostAdapter extends ArrayAdapter<Post> {

    public PostAdapter(@NonNull Context context, @NonNull List<Post> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_item_post, parent, false);

            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tvName);
            holder.tvDate = convertView.findViewById(R.id.tvDate);
            holder.tvContent = convertView.findViewById(R.id.tvContent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Post post = getItem(position);
        if (post != null) {
            String authorName = (post.getAuthor() != null) ? post.getAuthor().getName() : "Người dùng";
            holder.tvName.setText(authorName);
            holder.tvDate.setText(post.getCreatedAt());
            holder.tvContent.setText(post.getContent());
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView tvName, tvDate, tvContent;
    }
}