package com.belfoapps.qarib.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.belfoapps.qarib.base.MainListener;
import com.belfoapps.qarib.databinding.StartFragmentBinding;
import com.belfoapps.qarib.pojo.User;
import com.belfoapps.qarib.viewmodels.StartViewModel;
import com.belfoapps.qarib.views.MainActivity;

import java.util.Date;

public class StartFragment extends Fragment {
    private static final String TAG = "StartFragment";

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

        //Set ViewModel
        mViewModel = new ViewModelProvider(requireActivity()).get(StartViewModel.class);

        //Request Focus
        mBinding.username.getEditText().requestFocus();

        //Init Listener
        initListeners();

        return mBinding.getRoot();
    }


    private void initListeners() {
        //Start Listener
        mBinding.start.setOnClickListener(v -> {
            if (mBinding.username.getEditText().getText().toString().equals(""))
                mBinding.username.setError(" ");
            else {
                //Set User
                mViewModel.setUser(mBinding.username.getEditText().getText().toString());

                //Go to feed
                listener.feed();
            }
        });
    }

}