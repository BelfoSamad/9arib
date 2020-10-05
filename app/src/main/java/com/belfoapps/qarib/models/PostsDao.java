package com.belfoapps.qarib.models;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.belfoapps.qarib.pojo.Comment;
import com.belfoapps.qarib.pojo.CommentsForPost;
import com.belfoapps.qarib.pojo.Post;

import java.util.List;

@Dao
public interface PostsDao {

    //------------------------------------------ Insert
    @Insert
    public void insertPost(Post post);

    @Insert
    public void insertComment(Comment comment);

    //------------------------------------------ Queries
    @Query("SELECT * FROM Post")
    public List<Post> getPosts();

    @Transaction
    @Query("SELECT * FROM Post WHERE id = :id")
    public List<CommentsForPost> getPostById(Long id);

    //------------------------------------------ Updates
    @Update
    public void updatePost(Post post);

    //------------------------------------------ Delete
    @Delete
    public void deletePost(Post post);

}
