package com.belfoapps.qarib.viewmodels;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.belfoapps.qarib.models.SharedPreferencesHelper;
import com.belfoapps.qarib.pojo.NearMessage;
import com.belfoapps.qarib.utils.ConnectionsService;
import com.belfoapps.qarib.utils.JsonUtils;
import com.belfoapps.qarib.utils.Types;
import com.google.android.gms.nearby.messages.Message;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

public class ChatroomViewModel extends ViewModel {
    private static final String TAG = "ChatroomViewModel";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private MutableLiveData<NearMessage> messageData;
    private SharedPreferencesHelper mSharedPrefs;
    private MessagesListAdapter<NearMessage> messagesAdapter;
    private ConnectionsService connections;
    private Observer<Message> observer = message -> {
        String text = new String(message.getContent()).replaceAll("\\\\", "");
        if (text.charAt(0) == '"')
            text = text.substring(1, text.length() - 1);
        NearMessage msg = JsonUtils.json2Object(text, NearMessage.class);
        messageData.postValue(msg);
    };

    /***********************************************************************************************
     * *********************************** Constructor
     */
    @ViewModelInject
    public ChatroomViewModel(ConnectionsService connections, SharedPreferencesHelper mSharedPrefs) {
        this.mSharedPrefs = mSharedPrefs;
        this.connections = connections;
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    //Getters/Setters
    public String getUser() {
        return mSharedPrefs.getUser();
    }

    public MutableLiveData<NearMessage> getMessageData() {
        if (messageData == null)
            messageData = new MutableLiveData<>();

        return messageData;
    }

    public MessagesListAdapter<NearMessage> getMessagesListAdapter() {
        return messagesAdapter;
    }

    public void setMessagesListAdapter(MessagesListAdapter<NearMessage> messagesAdapter) {
        this.messagesAdapter = messagesAdapter;
    }

    //Methods
    public MutableLiveData<Message> listenForMessages() {
        connections.startScanningForMessages();
        return connections.getMessageData();
    }

    public NearMessage getNearMessage(String text) {
        return JsonUtils.json2Object(text, NearMessage.class);
    }

    public void sendMessage(NearMessage msg) {
        connections.sendMessage(JsonUtils.object2Json(msg), Types.Message.name());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        connections.stopScanningMessages();
        connections.getMessageData().removeObserver(observer);
    }
}