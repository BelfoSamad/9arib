package com.belfoapps.qarib.base;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.belfoapps.qarib.models.PostsDao;
import com.belfoapps.qarib.pojo.Post;

@Database(entities = {Post.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PostsDao postsDao();
}
