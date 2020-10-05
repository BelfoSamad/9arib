package com.belfoapps.qarib.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.belfoapps.qarib.R;
import com.belfoapps.qarib.pojo.Post;
import com.belfoapps.qarib.viewmodels.FeedViewModel;
import com.google.android.material.button.MaterialButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    private static final String TAG = "PostsAdapter";
    @SuppressLint("SimpleDateFormat")
    private DateFormat dateFormat = new SimpleDateFormat("HH:mm");

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private Context context;
    private FeedViewModel mViewModel;
    private ArrayList<Post> posts;

    /***********************************************************************************************
     * *********************************** Constructor
     */
    public PostsAdapter(Context context, FeedViewModel mViewModel, ArrayList<Post> posts) {
        this.context = context;
        this.mViewModel = mViewModel;
        this.posts = posts;
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    @NonNull
    @Override
    public PostsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostsAdapter.ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.post_recyclerview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder holder, int position) {

        //TODO: Author Type

        //TODO: Set Author

        //Set Time
        holder.time.setText(dateFormat.format(new Date(posts.get(position).getTimestamp())));

        //Set Content
        holder.content.setText(posts.get(position).getContent());

        //Set Views
        holder.views.setText(posts.get(position).getViews());

        //TODO: Set Hide/Delete

        //Listeners
        holder.hide.setOnClickListener(v -> {
            mViewModel.hidePost(posts.get(position).getId(), v.getTag().equals(0));
        });

        holder.share.setOnClickListener(v -> {
            mViewModel.rePost(posts.get(position).getId());
        });
    }

    @Override
    public int getItemCount() {
        if (posts == null) return 0;
        else return posts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView container;
        private ImageView icon;
        private TextView author;
        private TextView time;
        private MaterialButton hide;
        private TextView content;
        private TextView views;
        private ImageButton share;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.post_container);
            icon = itemView.findViewById(R.id.author_type);
            author = itemView.findViewById(R.id.post_author);
            time = itemView.findViewById(R.id.post_time);
            hide = itemView.findViewById(R.id.hide);
            content = itemView.findViewById(R.id.post_content);
            views = itemView.findViewById(R.id.views);
            share = itemView.findViewById(R.id.share);
        }
    }
}
