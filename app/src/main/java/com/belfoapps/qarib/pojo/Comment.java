package com.belfoapps.qarib.pojo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Comment {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    public long postId;
    private String comment;
    private Long timestamp;

    public Comment(String comment, Long timestamp) {
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
