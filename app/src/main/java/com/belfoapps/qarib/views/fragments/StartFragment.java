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

import com.belfoapps.qarib.R;
import com.belfoapps.qarib.base.MainListener;
import com.belfoapps.qarib.databinding.StartFragmentBinding;
import com.belfoapps.qarib.viewmodels.StartViewModel;
import com.belfoapps.qarib.views.MainActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StartFragment extends Fragment {
    private static final String TAG = "StartFragment";
    public static final String USERNAME = "username";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private MainListener listener;
    private StartViewModel mViewModel;
    private StartFragmentBinding mBinding;

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
        mBinding = StartFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Set ViewModel
        mViewModel = new ViewModelProvider(requireActivity()).get(StartViewModel.class);

        if (savedInstanceState != null)
            mBinding.username.getEditText().setText(savedInstanceState.getString(USERNAME));

        //Request Focus
        mBinding.username.getEditText().requestFocus();

        //Init Listener
        initListeners();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(USERNAME, mBinding.username.getEditText().getText().toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    private void initListeners() {
        //Start Listener
        mBinding.start.setOnClickListener(v -> {
            if (mBinding.username.getEditText().getText().toString().equals(""))
                mBinding.username.setError(getResources().getString(R.string.username_error));
            else {
                //Set User
                mViewModel.setUser(mBinding.username.getEditText().getText().toString());

                //Go to feed
                listener.feed();
            }
        });
    }
}