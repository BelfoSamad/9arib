package com.belfoapps.qarib.views.fragments;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.belfoapps.qarib.R;
import com.belfoapps.qarib.viewmodels.PrivateChatroomViewModel;

public class PrivateChatroomFragment extends Fragment {

    private PrivateChatroomViewModel mViewModel;

    public static PrivateChatroomFragment newInstance() {
        return new PrivateChatroomFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.private_chatroom_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PrivateChatroomViewModel.class);
        // TODO: Use the ViewModel
    }

}