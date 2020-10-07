package com.belfoapps.qarib.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.belfoapps.qarib.R;
import com.belfoapps.qarib.base.MainListener;
import com.belfoapps.qarib.databinding.FeedFragmentBinding;
import com.belfoapps.qarib.pojo.Post;
import com.belfoapps.qarib.ui.adapters.PostsAdapter;
import com.belfoapps.qarib.ui.custom.CreatePostDialog;
import com.belfoapps.qarib.utils.ResourcesUtils;
import com.belfoapps.qarib.viewmodels.FeedViewModel;
import com.belfoapps.qarib.views.MainActivity;
import com.google.android.material.snackbar.Snackbar;
import com.huawei.hms.nearby.StatusCode;
import com.huawei.hms.nearby.message.Message;

import java.util.ArrayList;

public class FeedFragment extends Fragment implements CreatePostDialog.PostCreationListener {
    private static final String TAG = "FeedFragment";
    public static final String RV_STATE = "RecyclerView_Status";
    public static final String POST = "post";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private PostsAdapter mAdapter;
    private FeedFragmentBinding mBinding;
    private MainListener listener;
    private Observer<Message> observer = new Observer<Message>() {
        @Override
        public void onChanged(Message message) {
            String text = new String(message.getContent()).replaceAll("\\\\", "");
            if (text.charAt(0) == '"')
                text = text.substring(1, text.length() - 1);

            Post post = mViewModel.getPost(text);

            if (post.getContent() == null) {
                mAdapter.removePost(post);
                if (mAdapter.getItemCount() == 0)
                    mBinding.rippleBackground.startRippleAnimation();
            } else {
                mAdapter.addPost(post);
                mBinding.rippleBackground.stopRippleAnimation();
            }
        }
    };
    private Observer<Post> postObserver = post -> {
        if (post.getContent() == null) {
            mAdapter.removePost(post);
            if (mAdapter.getItemCount() == 0)
                mBinding.rippleBackground.startRippleAnimation();
        } else {
            mAdapter.addPost(post);
            mBinding.rippleBackground.stopRippleAnimation();
        }
    };
    private Observer<Integer> errorObserver = error -> {
        switch (error) {
            case StatusCode.STATUS_MISSING_SETTING_LOCATION_ON:
                Snackbar.make(mBinding.getRoot(), "You should turn on Location",
                        Snackbar.LENGTH_LONG)
                        .setAction("Turn On", v -> ResourcesUtils.enableLocation(requireContext())).show();
                break;
            case StatusCode.STATUS_AIRPLANE_MODE_MUST_BE_OFF:
                Snackbar.make(mBinding.getRoot(), "This app won't work in plan mode",
                        Snackbar.LENGTH_LONG)
                        .show();
                break;
            case StatusCode.STATUS_MESSAGE_BLUETOOTH_OFF:
                Snackbar.make(mBinding.getRoot(), "You should turn bluetooth on",
                        Snackbar.LENGTH_LONG)
                        .setAction("Turn On", v -> ResourcesUtils.enableBluetooth(requireContext())).show();
                break;
            case StatusCode.STATUS_MISSING_PERMISSION_ACCESS_COARSE_LOCATION:
                Snackbar.make(mBinding.getRoot(), "The app requires this permission to work",
                        Snackbar.LENGTH_LONG)
                        .setAction("Request", v -> listener.requestPermissions()).show();
                break;
            case StatusCode.STATUS_NO_NETWORK:
                Snackbar.make(mBinding.getRoot(), "For authentication the app need network connection, turn on wifi or mobile data", Snackbar.LENGTH_LONG)
                        .show();
                break;
        }
    };
    private FeedViewModel mViewModel;
    private CreatePostDialog dialog;

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
        Log.d(TAG, "onCreateView: creating viewbinding");
        //Set ViewBinding
        mBinding = FeedFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Set ViewModel
        mViewModel = new ViewModelProvider(requireActivity()).get(FeedViewModel.class);

        //Init UI
        initUI();

        //Init Data
        if (savedInstanceState != null) {
            mBinding.feedInput.setText(savedInstanceState.getString(POST));

            //Init RecyclerView
            initRecyclerView(mViewModel.getPosts());

            //Scroll
            mBinding.scrollLayout.scrollTo(savedInstanceState.getInt("ScrollX"),
                    savedInstanceState.getInt("ScrollY"));
        } else {
            //Discover Posts
            mViewModel.startScanning(true).observe(getViewLifecycleOwner(), observer);

            //Start Ripple Animation
            Toast.makeText(getContext(), "Listening For Posts", Toast.LENGTH_SHORT).show();
            mBinding.rippleBackground.startRippleAnimation();

            //Set RecyclerView
            initRecyclerView(new ArrayList<Post>());
        }

        //Update RecyclerView
        mViewModel.getPostData().observe(getViewLifecycleOwner(), postObserver);

        //Listen For errors
        mViewModel.getErrorData().observe(getViewLifecycleOwner(), errorObserver);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(POST, mBinding.feedInput.getText().toString());
        outState.putInt("ScrollX", mBinding.scrollLayout.getScrollX());
        outState.putInt("ScrollY", mBinding.scrollLayout.getScrollY());
        mViewModel.setPosts(mAdapter.getPosts());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        mViewModel.startScanning(false).removeObservers(getViewLifecycleOwner());
        mViewModel.getPostData().removeObservers(getViewLifecycleOwner());
        mViewModel.getErrorData().removeObservers(getViewLifecycleOwner());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    private void initUI() {
        //Init Profile Listener
        mBinding.goChatroom.setOnClickListener(v -> {
            mViewModel.stopScanning();
            listener.chatroom();
        });
        //Add Create Options
        addCreateOptions();
        //prepare input ui
        prepareInputUi();
    }

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
                dialog = new CreatePostDialog(getResources().getStringArray(R.array.options_titles)[finalI]);
                dialog.show(getChildFragmentManager(), "CreatePostDialog");
            });

            mBinding.createPostsRecyclerviewContainer.addView(option);
        }

        imgs.recycle();
        colors.recycle();
    }

    private void prepareInputUi() {
        mBinding.feedInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0)
                    mBinding.sendPost.setEnabled(true);
                else mBinding.sendPost.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.sendPost.setOnClickListener(v -> {
            Log.d(TAG, "prepareInputUi: Send");
            mViewModel.addPost(mBinding.feedInput.getText().toString());
            mBinding.feedInput.setText("");
            hideKeyboard();
        });

        //Hide Keyboard when losing focus
        mBinding.feedInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard();
        });
    }

    private void initRecyclerView(ArrayList<Post> posts) {
        mAdapter = new PostsAdapter(getContext(), mViewModel, posts);
        mBinding.postsRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false));
        mBinding.postsRecyclerview.addItemDecoration(new RecyclerViewDecoration());
        mBinding.postsRecyclerview.setAdapter(mAdapter);
    }

    @Override
    public void createPost(Post post) {
        mViewModel.addExtraPost(post);
        dialog.dismiss();
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getActivity().getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(getActivity());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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