package com.belfoapps.qarib.pojo;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;

public class NearMessage implements IMessage {

    private String username;
    private String message;
    private Long timestamp;

    public NearMessage(String username, String message, Long timestamp) {
        this.username = username;
        this.message = message;
        this.timestamp = timestamp;
    }

    @Override
    public String getId() {
        return String.valueOf(username.hashCode() + message.hashCode());
    }

    @Override
    public String getText() {
        return message;
    }

    @Override
    public IUser getUser() {
        return new IUser() {
            @Override
            public String getId() {
                return String.valueOf(username.hashCode());
            }

            @Override
            public String getName() {
                return username;
            }

            @Override
            public String getAvatar() {
                return null;
            }
        };
    }

    @Override
    public Date getCreatedAt() {
        return new Date(timestamp);
    }

}
