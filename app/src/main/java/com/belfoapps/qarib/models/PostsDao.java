package com.belfoapps.qarib.models;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.belfoapps.qarib.pojo.Post;

import java.util.List;

@Dao
public interface PostsDao {

    //------------------------------------------ Insert
    @Insert
    public Long insertPost(Post post);

    //------------------------------------------ Queries
    @Query("SELECT * FROM Post")
    public List<Post> getPosts();

    @Transaction
    @Query("SELECT * FROM Post WHERE id = :id")
    public Post getPostById(Long id);

    //------------------------------------------ Updates
    @Update
    public void updatePost(Post post);

    //------------------------------------------ Delete
    @Delete
    public void deletePost(Post post);

}
