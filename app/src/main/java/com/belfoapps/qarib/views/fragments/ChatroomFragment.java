package com.belfoapps.qarib.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.belfoapps.qarib.base.MainListener;
import com.belfoapps.qarib.databinding.ChatroomFragmentBinding;
import com.belfoapps.qarib.pojo.NearMessage;
import com.belfoapps.qarib.viewmodels.ChatroomViewModel;
import com.belfoapps.qarib.views.MainActivity;
import com.google.android.gms.nearby.messages.Message;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

public class ChatroomFragment extends Fragment {
    private static final String TAG = "ChatroomFragment";
    public static final String RV_STATE = "RecyclerView_Status";
    public static final String MESSAGE = "message";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private Observer<Message> observer = new Observer<Message>() {
        @Override
        public void onChanged(Message message) {
            String text = new String(message.getContent()).replaceAll("\\\\", "");
            if (text.charAt(0) == '"')
                text = text.substring(1, text.length() - 1);

            NearMessage nearMessage = mViewModel.getNearMessage(text);

            messagesAdapter.addToStart(nearMessage, true);
            mBinding.rippleBackground.stopRippleAnimation();
        }
    };
    private ChatroomViewModel mViewModel;
    private ChatroomFragmentBinding mBinding;
    private MainListener listener;
    private Parcelable listState;
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
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Set ViewModel
        mViewModel = new ViewModelProvider(requireActivity()).get(ChatroomViewModel.class);

        //Init UI
        initUI();

        if (savedInstanceState != null) {
            messagesAdapter = mViewModel.getMessagesListAdapter();
            listState = savedInstanceState.getParcelable(RV_STATE);
            mBinding.messageInput.getInputEditText().setText(savedInstanceState.getString(MESSAGE));
        } else {
            messagesAdapter = new MessagesListAdapter<>(String.valueOf(mViewModel.getUser().hashCode()),
                    null);

            mViewModel.setMessagesListAdapter(messagesAdapter);

            //Scan Messages
            mViewModel.listenForMessages().observe(getViewLifecycleOwner(), observer);
            mBinding.rippleBackground.startRippleAnimation();
        }

        mBinding.messages.setAdapter(messagesAdapter);

        //Update Messages List
        mViewModel.getMessageData().observe(getViewLifecycleOwner(),
                nearMessage -> {
                    messagesAdapter.addToStart(nearMessage, true);
                    mBinding.rippleBackground.stopRippleAnimation();
                });

        //Set State
        if (listState != null)
            mBinding.messages.getLayoutManager().onRestoreInstanceState(listState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        listState = mBinding.messages.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(RV_STATE, listState);
        outState.putString(MESSAGE, mBinding.messageInput.getInputEditText().getText().toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
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
        //Go Back
        mBinding.back.setOnClickListener(v -> listener.goBack());
        //Send Message
        mBinding.messageInput.setInputListener(input -> {
            NearMessage msg = new NearMessage(mViewModel.getUser(), input.toString(), System.currentTimeMillis());
            mViewModel.sendMessage(msg);
            messagesAdapter.addToStart(msg, true);
            mBinding.rippleBackground.stopRippleAnimation();
            return true;
        });
    }
}