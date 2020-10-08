package com.belfoapps.qarib.utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.nearby.Nearby;
import com.huawei.hms.nearby.message.GetOption;
import com.huawei.hms.nearby.message.Message;
import com.huawei.hms.nearby.message.MessageEngine;
import com.huawei.hms.nearby.message.MessageHandler;
import com.huawei.hms.nearby.message.MessagePicker;
import com.huawei.hms.nearby.message.Policy;
import com.huawei.hms.nearby.message.PutOption;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ActivityContext;

public class ConnectionsService {
    private static final String TAG = "ConnectionsService";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private Context context;
    private HiAnalyticsInstance analytics;
    private MutableLiveData<Message> postData;
    private MutableLiveData<Message> messageData;
    private MutableLiveData<Integer> errorData;
    private MessageEngine messageEngine;
    private ArrayList<Message> my_messages;
    private MessageHandler messagesHandler = new MessageHandler() {
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
    private MessageHandler postsHandler = new MessageHandler() {
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
        if (analytics != null)
            analytics.onEvent("Error", bundle);
        if (errorData != null)
            errorData.postValue(Integer.parseInt(e.getMessage().split(":")[0]));
    };

    /***********************************************************************************************
     * *********************************** Constructor
     */
    @Inject
    public ConnectionsService(@ActivityContext Context context, HiAnalyticsInstance analytics) {
        this.context = context;
        this.my_messages = new ArrayList<>();
        messageEngine = Nearby.getMessageEngine(context);
        this.analytics = analytics;
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
        GetOption.Builder builder = new GetOption.Builder()
                .setPolicy(new Policy.Builder()
                        .setTtlSeconds(Policy.POLICY_TTL_SECONDS_MAX).build())
                .setPicker(new MessagePicker.Builder().includeNamespaceType(context.getPackageName(),
                        Types.Post.name()).build());
        messageEngine.get(postsHandler, builder.build())
                .addOnFailureListener(failure);
    }

    public void startScanningForMessages() {
        Log.d(TAG, "startScanningForMessages");
        GetOption.Builder builder = new GetOption.Builder()
                .setPolicy(new Policy.Builder()
                        .setTtlSeconds(Policy.POLICY_TTL_SECONDS_MAX).build())
                .setPicker(new MessagePicker.Builder().includeNamespaceType(context.getPackageName(),
                        Types.Message.name()).build());
        messageEngine.get(messagesHandler, builder.build())
                .addOnFailureListener(failure);
    }

    public void sendMessage(String messageContent, String type) {
        Log.d(TAG, "sendMessage: " + messageContent);
        Message message = new Message(JsonUtils.object2Json(messageContent).getBytes(), type,
                context.getPackageName());
        PutOption.Builder builder = new PutOption.Builder()
                .setPolicy(new Policy.Builder().setTtlSeconds(Policy.POLICY_TTL_SECONDS_MAX)
                        .build());

        //Add you message to unget
        my_messages.add(message);

        messageEngine.put(message, builder.build())
                .addOnFailureListener(failure);
    }

    public void stopScanningPosts() {
        Log.d(TAG, "stopScanningPosts");
        messageEngine.unget(postsHandler);
    }

    public void stopScanningMessages() {
        Log.d(TAG, "stopScanningMessages");
        messageEngine.unget(messagesHandler);
    }

    public void stopBroadcasting() {
        Log.d(TAG, "stopBroadcasting");
        for (Message message :
                my_messages) {
            messageEngine.unput(message);
        }

        my_messages.clear();
    }
}
