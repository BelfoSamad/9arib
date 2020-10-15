package com.belfoapps.qarib.utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageFilter;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.MessagesClient;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.google.android.gms.tasks.OnFailureListener;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ActivityContext;

public class ConnectionsService {
    private static final String TAG = "ConnectionsService";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private Context context;
    private MutableLiveData<Message> postData;
    private MutableLiveData<Message> messageData;
    private MutableLiveData<Integer> errorData;
    private MessagesClient messageEngine;
    private ArrayList<Message> my_messages;
    private MessageListener messagesHandler = new MessageListener() {
        @Override
        public void onFound(Message message) {
            super.onFound(message);
            Log.d(TAG, "onFound: " + new String(message.getContent()));
            messageData.postValue(message);
        }

        @Override
        public void onLost(Message message) {
            super.onLost(message);
        }
    };
    private MessageListener postsHandler = new MessageListener() {
        @Override
        public void onFound(Message message) {
            super.onFound(message);
            Log.d(TAG, "onFound: " + new String(message.getContent()));
            postData.postValue(message);
        }

        @Override
        public void onLost(Message message) {
            super.onLost(message);
        }
    };
    private OnFailureListener failure = e -> {
        Bundle bundle = new Bundle();
        bundle.putString("error", e.getMessage());
        if (errorData != null)
            errorData.postValue(Integer.parseInt(e.getMessage().split(":")[0]));
    };

    /***********************************************************************************************
     * *********************************** Constructor
     */
    @Inject
    public ConnectionsService(@ActivityContext Context context) {
        this.context = context;
        this.my_messages = new ArrayList<>();
        messageEngine = Nearby.getMessagesClient(context);
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    public MutableLiveData<Message> getPostData() {
        if (postData == null)
            postData = new MutableLiveData<>();

        return postData;
    }

    public MutableLiveData<Message> getMessageData() {
        if (messageData == null)
            messageData = new MutableLiveData<>();

        return messageData;
    }

    public MutableLiveData<Integer> getErrorData() {
        if (errorData == null)
            errorData = new MutableLiveData<>();

        return errorData;
    }

    public void startScanningForPosts() {
        Log.d(TAG, "startScanningForPosts");
        SubscribeOptions options = new SubscribeOptions.Builder()
                .setStrategy(Strategy.BLE_ONLY)
                .setFilter(new MessageFilter.Builder().includeNamespacedType(context.getPackageName(),
                        Types.Post.name()).build()).build();
        messageEngine.subscribe(postsHandler, options)
                .addOnFailureListener(failure);
    }

    public void startScanningForMessages() {
        Log.d(TAG, "startScanningForMessages");
        SubscribeOptions options = new SubscribeOptions.Builder()
                .setStrategy(Strategy.BLE_ONLY)
                .setFilter(new MessageFilter.Builder().includeNamespacedType(context.getPackageName(),
                        Types.Message.name()).build()).build();
        messageEngine.subscribe(postsHandler, options)
                .addOnFailureListener(failure);
    }

    public void sendMessage(String messageContent, String type) {
        Log.d(TAG, "sendMessage: " + messageContent);
        Message message = new Message(JsonUtils.object2Json(messageContent).getBytes(), type,
                context.getPackageName());
        PublishOptions options = new PublishOptions.Builder()
                .setStrategy(Strategy.BLE_ONLY).build();

        //Add you message to unget
        my_messages.add(message);

        messageEngine.publish(message, options)
                .addOnFailureListener(failure);
    }

    public void stopScanningPosts() {
        Log.d(TAG, "stopScanningPosts");
        messageEngine.unsubscribe(postsHandler);
    }

    public void stopScanningMessages() {
        Log.d(TAG, "stopScanningMessages");
        messageEngine.unsubscribe(messagesHandler);
    }

    public void stopBroadcasting() {
        Log.d(TAG, "stopBroadcasting");
        for (Message message :
                my_messages) {
            messageEngine.unpublish(message);
        }

        my_messages.clear();
    }
}
