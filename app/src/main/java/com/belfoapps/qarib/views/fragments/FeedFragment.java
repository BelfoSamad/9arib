package com.belfoapps.qarib.views.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.belfoapps.qarib.base.MainListener;
import com.belfoapps.qarib.databinding.FeedFragmentBinding;
import com.belfoapps.qarib.pojo.Post;
import com.belfoapps.qarib.ui.adapters.PostsAdapter;
import com.belfoapps.qarib.viewmodels.FeedViewModel;
import com.belfoapps.qarib.views.MainActivity;

import java.util.ArrayList;

public class FeedFragment extends Fragment {
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

        //Init RecyclerView
        if (savedInstanceState != null)
            mAdapter = mViewModel.getAdapter();
        else initRecyclerView();

        //Init Profile Listener
        mBinding.profile.setOnClickListener(v -> listener.profile());

        //prepare input ui
        prepareInputUi();

        //Discover Posts
        mViewModel.getPostData().observe(getViewLifecycleOwner(), new Observer<Post>() {
            @Override
            public void onChanged(Post post) {
                //TODO: Add/Remove Post
            }
        });

        return mBinding.getRoot();
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
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
                    mBinding.post.setEnabled(false);
                else mBinding.post.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.post.setOnClickListener(v ->
                mViewModel.sendPost(mBinding.addFeed.getText().toString()));
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