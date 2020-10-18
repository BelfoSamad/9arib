package com.belfoapps.qarib.base;

public interface MainListener {

    void requestPermissions();

    void startService(int code);

    void start();

    void feed();

    void profile();

    void chatroom();

    void privateChatroom(String chatroom);

    void goBack();
}
