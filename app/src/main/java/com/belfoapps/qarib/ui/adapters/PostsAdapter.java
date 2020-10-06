package com.belfoapps.qarib.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    public static final int NORMAL = 1;
    public static final int CONTACTS = 2;
    public static final int SOCIAL_MEDIA = 3;
    public static final int WEBSITE = 4;
    public static final int LOCATION = 5;

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

    @Override
    public int getItemViewType(int position) {
        Post post = posts.get(position);
        if (post.getWebsite() != null)
            return WEBSITE;
        else if (post.getEmail() != null)
            return CONTACTS;
        else if (post.getDescription() != null)
            return SOCIAL_MEDIA;
        else return NORMAL;
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    @NonNull
    @Override
    public PostsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case CONTACTS:
                return new PostsAdapter.ViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.contacts_post_item, parent, false));
            case SOCIAL_MEDIA:
                return new PostsAdapter.ViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.social_media_post_item, parent, false));
            case WEBSITE:
                return new PostsAdapter.ViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.website_post_item, parent, false));
            default:
                return new PostsAdapter.ViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.post_recyclerview_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder holder, int position) {

        //TODO: Author Type

        //Set Author
        holder.author.setText(posts.get(position).getAuthor());

        //Set Time
        holder.time.setText(dateFormat.format(new Date(posts.get(position).getTimestamp())));

        //Set Content
        holder.content.setText(posts.get(position).getContent());

        if (posts.get(position).getAuthor().equals(mViewModel.getUser()))
            holder.hide.setTag(0);
        else holder.hide.setTag(1);

        //Listeners
        holder.hide.setOnClickListener(v -> {
            mViewModel.hidePost(posts.get(position), v.getTag().equals(0));
        });

        holder.share.setOnClickListener(v -> {
            mViewModel.rePost(posts.get(position).getId());
        });

        //-------------------------- Extra Posts
        switch (getItemViewType(position)) {
            case CONTACTS:
                holder.email.setText(posts.get(position).getEmail());
                holder.email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "Email", Toast.LENGTH_SHORT).show();
                    }
                });


                holder.phone.setText(posts.get(position).getEmail());
                holder.phone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "Phone", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case SOCIAL_MEDIA:
                holder.twitter.setOnClickListener(v -> {
                    Toast.makeText(context, "Twitter", Toast.LENGTH_SHORT).show();
                });
                holder.facebook.setOnClickListener(v -> {
                    Toast.makeText(context, "Facebook", Toast.LENGTH_SHORT).show();
                });
                holder.instagram.setOnClickListener(v -> {
                    Toast.makeText(context, "Instagram", Toast.LENGTH_SHORT).show();
                });
                holder.copy.setOnClickListener(v -> {
                    Toast.makeText(context, "Copy", Toast.LENGTH_SHORT).show();
                });
                break;
            case WEBSITE:
                holder.website.setText(posts.get(position).getWebsite());
                holder.website.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "Website", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (posts == null) return 0;
        else return posts.size();
    }

    public void removePost(Post post) {
        posts.remove(post);
        notifyDataSetChanged();
    }

    public void addPost(Post post) {
        posts.add(0, post);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView container;
        private ImageView icon;
        private TextView author;
        private TextView time;
        private MaterialButton hide;
        private TextView content;
        private ImageButton share;

        private TextView description;
        private MaterialButton twitter;
        private MaterialButton facebook;
        private MaterialButton instagram;
        private MaterialButton copy;

        private MaterialButton website;

        private MaterialButton email;
        private MaterialButton phone;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.post_container);
            icon = itemView.findViewById(R.id.author_type);
            author = itemView.findViewById(R.id.post_author);
            time = itemView.findViewById(R.id.post_time);
            hide = itemView.findViewById(R.id.hide);
            content = itemView.findViewById(R.id.post_content);
            share = itemView.findViewById(R.id.share);

            twitter = itemView.findViewById(R.id.twitter);
            facebook = itemView.findViewById(R.id.facebook);
            instagram = itemView.findViewById(R.id.instagram);
            copy = itemView.findViewById(R.id.copy);

            website = itemView.findViewById(R.id.website);

            email = itemView.findViewById(R.id.email);
            phone = itemView.findViewById(R.id.phone);
        }
    }
}
