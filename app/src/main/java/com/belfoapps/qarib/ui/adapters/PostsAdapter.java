package com.belfoapps.qarib.ui.adapters;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StrikethroughSpan;
import android.util.Log;
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

import static android.content.Context.CLIPBOARD_SERVICE;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    private static final String TAG = "PostsAdapter";
    public static final int NORMAL = 1;
    public static final int CONTACTS = 2;
    public static final int SOCIAL_MEDIA = 3;
    public static final int WEBSITE = 4;
    public static final int OFFER = 5;

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
        Log.d(TAG, "PostsAdapter: " + posts.size());
    }

    @Override
    public int getItemViewType(int position) {
        Post post = posts.get(position);
        if (post.getWebsite() != null)
            return WEBSITE;
        else if (post.getEmail() != null)
            return CONTACTS;
        else if (post.getPrice() != 0)
            return OFFER;
        else if (post.getTitle() != null)
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
            case OFFER:
                return new PostsAdapter.ViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.offer_post_item, parent, false));
            default:
                return new PostsAdapter.ViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.post_recyclerview_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder holder, int position) {

        //Set Normal Account Icon
        holder.icon.setImageResource(R.drawable.person);

        //Set Author
        holder.author.setText(posts.get(position).getAuthor());

        //Set Time
        holder.time.setText(dateFormat.format(new Date(posts.get(position).getTimestamp())));

        //Set Content
        holder.content.setText(posts.get(position).getContent());

        if (posts.get(position).getAuthor().equals(mViewModel.getUser())) {
            holder.hide.setText(context.getResources().getString(R.string.delete));
            holder.hide.setTag(0);
        } else {
            holder.hide.setText(context.getResources().getString(R.string.hide));
            holder.hide.setTag(1);
        }

        //Share Option
        if (posts.get(position).getAuthor().equals(mViewModel.getUser()))
            holder.share.setVisibility(View.GONE);

        //Listeners
        holder.hide.setOnClickListener(v -> {
            mViewModel.hidePost(posts.get(position), v.getTag().equals(0));
        });

        holder.share.setOnClickListener(v -> {
            if (!posts.get(position).getAuthor().equals(mViewModel.getUser()))
                mViewModel.sendPost(posts.get(position));
        });

        //-------------------------- Extra Posts
        if (getItemViewType(position) != NORMAL) {
            //Set Business Account Icon
            holder.icon.setImageResource(R.drawable.business);
            holder.title.setText(posts.get(position).getTitle());
            switch (getItemViewType(position)) {
                case CONTACTS:
                    holder.email.setText(posts.get(position).getEmail());
                    holder.email.setOnClickListener(v -> {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("message/rfc822");
                        intent.putExtra(Intent.EXTRA_EMAIL,
                                new String[]{((MaterialButton) v).getText().toString()});
                        context.startActivity(Intent.createChooser(intent, "Send email"));
                    });
                    holder.email.setOnLongClickListener(v -> {
                        copyContent(((MaterialButton) v).getText().toString());
                        return true;
                    });

                    holder.phone.setText(posts.get(position).getPhone());
                    holder.phone.setOnClickListener(v -> {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + ((MaterialButton) v).getText().toString()));
                        context.startActivity(intent);
                    });
                    holder.phone.setOnLongClickListener(v -> {
                        copyContent(((MaterialButton) v).getText().toString());
                        return true;
                    });
                    break;
                case SOCIAL_MEDIA:
                    if (posts.get(position).getFacebook() == null)
                        holder.facebook.setVisibility(View.GONE);
                    if (posts.get(position).getTwitter() == null)
                        holder.twitter.setVisibility(View.GONE);
                    if (posts.get(position).getInstagram() == null)
                        holder.instagram.setVisibility(View.GONE);
                    if (posts.get(position).getLinkedin() == null)
                        holder.linkedin.setVisibility(View.GONE);

                    holder.facebook.setOnClickListener(v -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.facebook.com/" + posts.get(position).getFacebook()));
                        context.startActivity(browserIntent);
                    });
                    holder.twitter.setOnClickListener(v -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://twitter.com/" + posts.get(position).getTwitter()));
                        context.startActivity(browserIntent);

                    });
                    holder.instagram.setOnClickListener(v -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.instagram.com/" + posts.get(position).getInstagram()));
                        context.startActivity(browserIntent);
                    });
                    holder.linkedin.setOnClickListener(v -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.linkedin.com/in/" + posts.get(position).getLinkedin()));
                        context.startActivity(browserIntent);
                    });
                    break;
                case WEBSITE:
                    holder.website.setText(posts.get(position).getWebsite());
                    holder.website.setOnClickListener(v -> {
                        String url = ((MaterialButton) v).getText().toString();
                        if (!url.contains("https") && !url.contains("http"))
                            url = "https://" + url;
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(url));
                        context.startActivity(browserIntent);
                    });
                    holder.website.setOnLongClickListener(v -> {
                        copyContent(((MaterialButton) v).getText().toString());
                        return true;
                    });
                    break;
                case OFFER:
                    double new_price;
                    if (posts.get(position).getPercentage() > 0)
                        new_price = posts.get(position).getPrice() -
                                (posts.get(position).getPrice() * ((float) posts.get(position).getPercentage() / 100));
                    else new_price = posts.get(position).getPrice();

                    String final_price1 = "Price: " + posts.get(position).getPrice();
                    String final_price2 = " - " + new_price;
                    SpannableStringBuilder spannable = new SpannableStringBuilder(final_price1 + final_price2);
                    spannable.setSpan(
                            new StrikethroughSpan(),
                            7, // start
                            final_price1.length(), // end
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                    );
                    holder.product.setText(spannable);
                    break;
            }
        }
    }

    private void copyContent(String to_copy) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(context.getResources().getString(R.string.app_name), to_copy);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(context, "Text Copied", Toast.LENGTH_SHORT).show();
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
        if (!posts.contains(post))
            posts.add(0, post);
        notifyDataSetChanged();
    }

    public ArrayList<Post> getPosts() {
        ArrayList<Post> clone = new ArrayList<>();
        for (Post post:
             posts) {
            try {
                clone.add((Post) post.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return clone;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView container;
        private ImageView icon;
        private TextView author;
        private TextView time;
        private MaterialButton hide;
        private TextView content;
        private ImageButton share;

        private TextView title;

        private MaterialButton facebook;
        private MaterialButton twitter;
        private MaterialButton instagram;
        private MaterialButton linkedin;

        private MaterialButton website;

        private MaterialButton email;
        private MaterialButton phone;

        private TextView product;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.post_container);
            icon = itemView.findViewById(R.id.author_type);
            author = itemView.findViewById(R.id.post_author);
            time = itemView.findViewById(R.id.post_time);
            hide = itemView.findViewById(R.id.hide);
            content = itemView.findViewById(R.id.post_content);
            share = itemView.findViewById(R.id.share);

            title = itemView.findViewById(R.id.post_title);

            facebook = itemView.findViewById(R.id.facebook);
            twitter = itemView.findViewById(R.id.twitter);
            instagram = itemView.findViewById(R.id.instagram);
            linkedin = itemView.findViewById(R.id.linkedin);

            website = itemView.findViewById(R.id.website);

            email = itemView.findViewById(R.id.email);
            phone = itemView.findViewById(R.id.phone);

            product = itemView.findViewById(R.id.product_price);
        }
    }
}
