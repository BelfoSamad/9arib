package com.belfoapps.qarib.viewmodels;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.belfoapps.qarib.base.AppDatabase;
import com.belfoapps.qarib.models.SharedPreferencesHelper;
import com.belfoapps.qarib.pojo.Post;
import com.belfoapps.qarib.utils.ConnectionsService;
import com.belfoapps.qarib.utils.JsonUtils;
import com.belfoapps.qarib.utils.Types;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.nearby.message.Message;

import java.util.ArrayList;

public class FeedViewModel extends ViewModel {
    private static final String TAG = "FeedViewModel";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private SharedPreferencesHelper mSharedPrefs;
    private AppDatabase mDb;
    private HiAnalyticsInstance analytics;
    private ConnectionsService connections;
    private MutableLiveData<Post> postData;
    private MutableLiveData<ArrayList<Post>> postsData;
    private ArrayList<Post> posts;

    /***********************************************************************************************
     * *********************************** Constructor
     */
    @ViewModelInject
    public FeedViewModel(SharedPreferencesHelper mSharedPrefs, AppDatabase mDb,
                         ConnectionsService connections) {
        this.mSharedPrefs = mSharedPrefs;
        this.mDb = mDb;
        this.connections = connections;
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    //Getters/Setters
    public MutableLiveData<Post> getPostData() {
        if (postData == null)
            postData = new MutableLiveData<>();

        return postData;
    }

    public String getUser() {
        return mSharedPrefs.getUser();
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    public MutableLiveData<ArrayList<Post>> getPostsData() {
        if (postsData == null)
            postsData = new MutableLiveData<>();
        return postsData;
    }

    public void setPostsData(MutableLiveData<ArrayList<Post>> postsData) {
        this.postsData = postsData;
    }

    //Methods
    public MutableLiveData<Message> startScanning(boolean listening) {
        if (listening)
            connections.startScanningForPosts();
        return connections.getPostData();
    }

    public void getSavedPosts() {
        new GetPosts().execute();
    }

    public void addPost(String content) {
        Log.d(TAG, "addPost");
        //Make post object
        Post post = new Post(getUser(), content, System.currentTimeMillis());
        new InsertPost().execute(post);
    }

    public void addExtraPost(Post post) {
        post.setAuthor(getUser());
        new InsertPost().execute(post);
    }

    public void hidePost(Post post, boolean delete) {
        if (delete) {
            new DeletePost().execute(post);
        } else {
            post.setContent(null);
            postData.postValue(post);
        }
    }

    public void sendPost(Post post) {
        Log.d(TAG, "sendPost");
        connections.sendMessage(JsonUtils.object2Json(post), Types.Post.name());
    }

    public void stopScanning() {
        connections.stopScanningPosts();
    }

    public Post getPost(String text) {
        return JsonUtils.json2Object(text, Post.class);
    }

    public LiveData<Integer> getErrorData() {
        return connections.getErrorData();
    }

    //Tasks
    @SuppressLint("StaticFieldLeak")
    private class GetPosts extends AsyncTask<Void, Void, ArrayList<Post>> {

        @Override
        protected ArrayList<Post> doInBackground(Void... voids) {
            return new ArrayList<>(mDb.postsDao().getPosts());
        }

        @Override
        protected void onPostExecute(ArrayList<Post> post) {
            super.onPostExecute(post);
            postsData.postValue(post);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class InsertPost extends AsyncTask<Post, Void, Post> {

        @Override
        protected Post doInBackground(Post... posts) {
            Long id = mDb.postsDao().insertPost(posts[0]);
            posts[0].setId(id);
            return posts[0];
        }

        @Override
        protected void onPostExecute(Post post) {
            super.onPostExecute(post);

            Log.d(TAG, "onPostExecute: Saved " + post.getAuthor());
            sendPost(post);
            //Add Post
            postData.postValue(post);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DeletePost extends AsyncTask<Post, Void, Post> {

        @Override
        protected Post doInBackground(Post... posts) {
            mDb.postsDao().deletePost(posts[0]);
            return posts[0];
        }

        @Override
        protected void onPostExecute(Post post) {
            super.onPostExecute(post);
            post.setContent(null);
            sendPost(post);
            postData.postValue(post);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopScanning();
    }
}