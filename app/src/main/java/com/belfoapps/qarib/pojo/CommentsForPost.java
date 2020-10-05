package com.belfoapps.qarib.pojo;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class CommentsForPost {
    @Embedded
    public Post post;
    @Relation(
            parentColumn = "id",
            entityColumn = "postId"
    )
    public List<Comment> comments;
}
