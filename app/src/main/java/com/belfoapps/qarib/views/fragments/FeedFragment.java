package com.belfoapps.qarib.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.belfoapps.qarib.base.MainListener;
import com.belfoapps.qarib.databinding.FeedFragmentBinding;
import com.belfoapps.qarib.viewmodels.FeedViewModel;
import com.belfoapps.qarib.views.MainActivity;

public class FeedFragment extends Fragment {
    private static final String TAG = "FeedFragment";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private MainListener listener;
    private FeedViewModel mViewModel;
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

        //Init Profile Listener
        mBinding.profile.setOnClickListener(v -> listener.profile());

        //Init RecyclerView
        initRecyclerView();

        return mBinding.getRoot();
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    private void initRecyclerView() {

    }
}