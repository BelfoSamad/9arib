package com.belfoapps.qarib.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.belfoapps.qarib.models.SharedPreferencesHelper;
import com.belfoapps.qarib.pojo.NearMessage;
import com.belfoapps.qarib.utils.JsonUtils;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.nearby.Nearby;
import com.huawei.hms.nearby.message.GetOption;
import com.huawei.hms.nearby.message.Message;
import com.huawei.hms.nearby.message.MessageEngine;
import com.huawei.hms.nearby.message.MessageHandler;
import com.huawei.hms.nearby.message.MessagePicker;
import com.huawei.hms.nearby.message.Policy;
import com.huawei.hms.nearby.message.PutOption;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;

import dagger.hilt.android.qualifiers.ActivityContext;

public class ChatroomViewModel extends ViewModel {
    private static final String TAG = "ChatroomViewModel";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private MessageHandler mHandler = new MessageHandler() {
        @Override
        public void onFound(Message message) {
            super.onFound(message);
            NearMessage msg = JsonUtils.json2Object(new String(message.getContent()), NearMessage.class);
            messageData.postValue(msg);
        }

        @Override
        public void onLost(Message message) {
            super.onLost(message);
        }
    };
    private SharedPreferencesHelper mSharedPrefs;
    private MutableLiveData<NearMessage> messageData;
    private MessageEngine messageEngine;
    private ArrayList<Message> my_messages;
    private MessagesListAdapter<NearMessage> messagesAdapter;

    /***********************************************************************************************
     * *********************************** Constructor
     */
    @ViewModelInject
    public ChatroomViewModel(@ActivityContext Context context, SharedPreferencesHelper mSharedPrefs) {
        this.mSharedPrefs = mSharedPrefs;
        messageEngine = Nearby.getMessageEngine(context);

        my_messages = new ArrayList<>();
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    public String getUser() {
        return mSharedPrefs.getUser();
    }

    public MutableLiveData<NearMessage> getMessageData() {
        if (messageData == null)
            messageData = new MutableLiveData<>();

        return messageData;
    }

    public void listenForMessages(Context context) {
        GetOption.Builder builder = new GetOption.Builder()
                .setPolicy(new Policy.Builder()
                        .setTtlSeconds(Policy.POLICY_TTL_SECONDS_MAX).build())
                .setPicker(new MessagePicker.Builder().includeNamespaceType(context.getPackageName(),
                        "Messages").build());
        messageEngine.get(mHandler, builder.build());
    }

    public void sendMessage(Context context, NearMessage msg) {
        //Publish Post
        Message message = new Message(JsonUtils.object2Json(msg).getBytes(), "Messages",
                context.getPackageName());
        PutOption.Builder builder = new PutOption.Builder()
                .setPolicy(new Policy.Builder().setTtlSeconds(Policy.POLICY_TTL_SECONDS_MAX)
                        .build());

        //Add you message to unget
        my_messages.add(message);

        messageEngine.put(message, builder.build()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess");
            }
        });
    }

    public void stopScanning() {
        messageEngine.unget(mHandler);
    }

    public void stopBroadcasting() {
        for (Message message :
                my_messages) {
            messageEngine.unput(message);
        }

        my_messages.clear();
    }

    public MessagesListAdapter<NearMessage> getMessagesListAdapter() {
        return messagesAdapter;
    }

    public void setMessagesListAdapter(MessagesListAdapter<NearMessage> messagesAdapter) {
        this.messagesAdapter = messagesAdapter;
    }
}