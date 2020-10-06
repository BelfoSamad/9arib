package com.belfoapps.qarib.views.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.belfoapps.qarib.R;
import com.belfoapps.qarib.base.MainListener;
import com.belfoapps.qarib.databinding.FeedFragmentBinding;
import com.belfoapps.qarib.pojo.Post;
import com.belfoapps.qarib.ui.adapters.PostsAdapter;
import com.belfoapps.qarib.ui.custom.CreatePostDialog;
import com.belfoapps.qarib.viewmodels.FeedViewModel;
import com.belfoapps.qarib.views.MainActivity;

import java.util.ArrayList;

public class FeedFragment extends Fragment implements CreatePostDialog.PostCreationListener {
    private static final String TAG = "FeedFragment";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private MainListener listener;
    private FeedViewModel mViewModel;
    private PostsAdapter mAdapter;
    private FeedFragmentBinding mBinding;

    /***********************************************************************************************
     * *********************************** LifeCycle
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (MainActivity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //Set ViewBinding
        mBinding = FeedFragmentBinding.inflate(inflater, container, false);

        //Set ViewModel
        mViewModel = new ViewModelProvider(requireActivity()).get(FeedViewModel.class);

        //Add Create Options
        addCreateOptions();

        //Init RecyclerView
        if (savedInstanceState != null)
            mAdapter = mViewModel.getAdapter();
        else initRecyclerView();

        //Init Profile Listener
        mBinding.chatroom.setOnClickListener(v -> listener.chatroom());

        //prepare input ui
        prepareInputUi();

        //Start Broadcasting
        mViewModel.startBroadcasting(requireContext());

        //Discover Posts
        mViewModel.getPostData().observe(getViewLifecycleOwner(), post -> {
            if (post.getContent() == null)
                mAdapter.removePost(post);
            else mAdapter.addPost(post);
        });
        mViewModel.startDiscovering(requireContext());

        return mBinding.getRoot();
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    private void addCreateOptions() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        TypedArray imgs = getResources().obtainTypedArray(R.array.options_images);
        TypedArray colors = getResources().obtainTypedArray(R.array.options_colors);
        for (int i = 0; i < getResources().getStringArray(R.array.options_titles).length; i++) {
            View option = inflater.inflate(R.layout.create_post_item, mBinding.createPostsRecyclerviewContainer,
                    false);
            //Bind Content
            ((ImageView) option.findViewById(R.id.create_post_illustration)).setImageResource(imgs.getResourceId(i, -1));
            ((TextView) option.findViewById(R.id.create_post_title)).setText(getResources().getStringArray(R.array.options_titles)[i]);
            ((CardView) option.findViewById(R.id.create_post_container))
                    .setCardBackgroundColor(getResources().getColor(colors.getResourceId(i, -1)));

            int finalI = i;
            option.findViewById(R.id.create_post_container).setOnClickListener(v -> {
                //dialog = new CreatePostDialog(getResources().getStringArray(R.array.options_titles)[finalI]);
                //dialog.show(getChildFragmentManager(), "CreatePostDialog");
            });

            mBinding.createPostsRecyclerviewContainer.addView(option);
        }

        imgs.recycle();
        colors.recycle();
    }

    private void initRecyclerView() {
        mAdapter = new PostsAdapter(getContext(), mViewModel, new ArrayList<>());
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mBinding.postsRecyclerview.setLayoutManager(manager);
        mBinding.postsRecyclerview.addItemDecoration(new RecyclerViewDecoration());
        mBinding.postsRecyclerview.setAdapter(mAdapter);

        mViewModel.setAdapter(mAdapter);
    }

    private void prepareInputUi() {
        mBinding.addFeed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0)
                    mBinding.post.setEnabled(true);
                else mBinding.post.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.post.setOnClickListener(v -> {
            mViewModel.addPost(mBinding.addFeed.getText().toString());
            mBinding.addFeed.setText("");
        });
    }

    @Override
    public void createPost(Post post) {
        mViewModel.addExtraPost(post);
    }

    private static class RecyclerViewDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.left = 16;
            outRect.right = 16;
            outRect.bottom = 24;
        }
    }
}