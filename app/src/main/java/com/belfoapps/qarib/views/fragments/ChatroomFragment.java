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

import com.belfoapps.qarib.base.MainListener;
import com.belfoapps.qarib.databinding.ChatroomFragmentBinding;
import com.belfoapps.qarib.pojo.NearMessage;
import com.belfoapps.qarib.viewmodels.ChatroomViewModel;
import com.belfoapps.qarib.views.MainActivity;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

public class ChatroomFragment extends Fragment {
    private static final String TAG = "ChatroomFragment";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private ChatroomViewModel mViewModel;
    private ChatroomFragmentBinding mBinding;
    private MainListener listener;
    private MessagesListAdapter<NearMessage> messagesAdapter;

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
        mBinding = ChatroomFragmentBinding.inflate(inflater, container, false);

        //Set ViewModel
        mViewModel = new ViewModelProvider(requireActivity()).get(ChatroomViewModel.class);

        //Go Back
        mBinding.back.setOnClickListener(v -> listener.goBack());


        if (savedInstanceState != null) {
            messagesAdapter = mViewModel.getMessagesListAdapter();
            //listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
        } else initMessagesList();

        mViewModel.getMessageData().observe(getViewLifecycleOwner(), nearMessage ->
                messagesAdapter.addToStart(nearMessage, true));
        mViewModel.listenForMessages(requireContext());

        //Send Message
        mBinding.messageInput.setInputListener(input -> {
            NearMessage msg = new NearMessage(mViewModel.getUser(), input.toString(), System.currentTimeMillis());
            mViewModel.sendMessage(requireContext(), msg);
            messagesAdapter.addToStart(msg, true);
            return true;
        });

        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.stopBroadcasting();
        mViewModel.stopScanning();
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    private void initMessagesList() {
        messagesAdapter = new MessagesListAdapter<>(String.valueOf(mViewModel.getUser().hashCode()),
                null);
        mBinding.messages.setAdapter(messagesAdapter);
        mViewModel.setMessagesListAdapter(messagesAdapter);
    }

}