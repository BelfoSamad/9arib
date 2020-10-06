package com.belfoapps.qarib.viewmodels;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.belfoapps.qarib.base.AppDatabase;
import com.belfoapps.qarib.models.SharedPreferencesHelper;
import com.belfoapps.qarib.pojo.Post;
import com.belfoapps.qarib.ui.adapters.PostsAdapter;
import com.belfoapps.qarib.utils.ConnectionUtils;
import com.belfoapps.qarib.utils.JsonUtils;
import com.belfoapps.qarib.views.MainActivity;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.nearby.Nearby;
import com.huawei.hms.nearby.StatusCode;
import com.huawei.hms.nearby.discovery.BroadcastOption;
import com.huawei.hms.nearby.discovery.ConnectCallback;
import com.huawei.hms.nearby.discovery.ConnectInfo;
import com.huawei.hms.nearby.discovery.ConnectResult;
import com.huawei.hms.nearby.discovery.DiscoveryEngine;
import com.huawei.hms.nearby.discovery.Policy;
import com.huawei.hms.nearby.discovery.ScanEndpointCallback;
import com.huawei.hms.nearby.discovery.ScanEndpointInfo;
import com.huawei.hms.nearby.discovery.ScanOption;
import com.huawei.hms.nearby.transfer.Data;
import com.huawei.hms.nearby.transfer.DataCallback;
import com.huawei.hms.nearby.transfer.TransferEngine;
import com.huawei.hms.nearby.transfer.TransferStateUpdate;

import java.nio.charset.Charset;
import java.util.ArrayList;

import dagger.hilt.android.qualifiers.ActivityContext;

public class FeedViewModel extends ViewModel {
    private static final String TAG = "FeedViewModel";

    /***********************************************************************************************
     * *********************************** Declarations
     */

    private DataCallback mDataCb = new DataCallback() {
        @Override
        public void onReceived(String string, Data data) {
            receivePost(JsonUtils.json2Object(new String(data.asBytes()), Post.class));
        }

        @Override
        public void onTransferUpdate(String string, TransferStateUpdate update) {
        }
    };

    private ConnectCallback mConnCb = new ConnectCallback() {
        @Override
        public void onEstablish(String s, ConnectInfo connectInfo) {
            mDiscoveryEngine.acceptConnect(s, mDataCb);

            //Send all post to the new receiver
            new GetAllPosts(s).execute();

            //Send Post to all these receivers
            if (receivers == null)
                receivers = new ArrayList<>();

            receivers.add(s);
        }

        @Override
        public void onResult(String s, ConnectResult connectResult) {
            Log.d(TAG, "onResult: " + connectResult.getStatus().getStatusMessage());
        }

        @Override
        public void onDisconnected(String s) {
        }
    };

    private SharedPreferencesHelper mSharedPrefs;
    private AppDatabase mDb;
    private MutableLiveData<Post> postData;
    private PostsAdapter mAdapter;
    private DiscoveryEngine mDiscoveryEngine;
    private TransferEngine mTransferEngine;
    private ArrayList<String> receivers;

    /***********************************************************************************************
     * *********************************** Constructor
     */
    @ViewModelInject
    public FeedViewModel(@ActivityContext Context context, SharedPreferencesHelper mSharedPrefs,
                         AppDatabase mDb) {
        this.mSharedPrefs = mSharedPrefs;
        this.mDb = mDb;
        mDiscoveryEngine = Nearby.getDiscoveryEngine(context);
        mTransferEngine = Nearby.getTransferEngine(context);
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

    public PostsAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(PostsAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    public String getUser() {
        return mSharedPrefs.getUser();
    }

    //Methods
    public void startDiscovering(Context context) {
        ScanOption.Builder discBuilder = new ScanOption.Builder();
        discBuilder.setPolicy(Policy.POLICY_MESH);
        mDiscoveryEngine.startScan(context.getPackageName(), new ScanEndpointCallback() {
                    @Override
                    public void onFound(String s, ScanEndpointInfo scanEndpointInfo) {
                        mDiscoveryEngine.requestConnect(getUser(), s, mConnCb);
                    }

                    @Override
                    public void onLost(String s) {

                    }
                },
                discBuilder.build())
                .addOnFailureListener(e -> {
                    Log.d(TAG, "startScanning: " + e.getMessage());
                    switch (Integer.parseInt(e.getMessage().split(":")[0])) {
                        case StatusCode.STATUS_MISSING_SETTING_LOCATION_ON:
                            ConnectionUtils.enableLocation((MainActivity) context);
                            break;
                        case StatusCode.STATUS_AIRPLANE_MODE_MUST_BE_OFF:
                            break;
                        case StatusCode.STATUS_MESSAGE_BLUETOOTH_OFF:
                            break;
                        case StatusCode.STATUS_MISSING_PERMISSION_ACCESS_COARSE_LOCATION:
                            break;
                    }
                })
                .addOnSuccessListener(aVoid -> Log.d(TAG, "onSuccess"));
    }

    public void startBroadcasting(Context context) {
        BroadcastOption.Builder advBuilder = new BroadcastOption.Builder();
        advBuilder.setPolicy(Policy.POLICY_MESH);
        mDiscoveryEngine.startBroadcasting(getUser(), context.getPackageName(), mConnCb,
                advBuilder.build())
                .addOnFailureListener(e -> {
                    Log.d(TAG, "startBroadcasting: " + e.getMessage());
                    switch (Integer.parseInt(e.getMessage().split(":")[0])) {
                        case StatusCode.STATUS_MISSING_SETTING_LOCATION_ON:
                            ConnectionUtils.enableLocation((MainActivity) context);
                            break;
                    }
                })
                .addOnSuccessListener(aVoid -> Log.d(TAG, "onSuccess"));
    }

    public void addPost(String content) {
        //Make post object
        Post post = new Post(getUser(), content, System.currentTimeMillis());
        new InsertPost(true).execute(post);
    }

    public void addExtraPost(Post post) {
        post.setAuthor(getUser());
        new InsertPost(true).execute(post);
    }

    public void hidePost(Post post, boolean delete) {
        new DeletePost(delete).execute(post);
    }

    public void rePost(Long post_id) {
        new GetPost().execute(post_id);
    }

    public void sendPost(Post post) {
        if (receivers != null) {
            Data data = Data.fromBytes(JsonUtils.object2Json(post).getBytes(Charset.defaultCharset()));
            mTransferEngine.sendData(receivers, data);
        } else Log.d(TAG, "sendMessage: No Receivers");
    }

    public void sendPost(Post post, String receiver) {
        Data data = Data.fromBytes(JsonUtils.object2Json(post).getBytes(Charset.defaultCharset()));
        mTransferEngine.sendData(receiver, data);
    }

    public void receivePost(Post post) {
        post.setId(null);
        //Insert Post
        if (post.getContent() == null)
            postData.postValue(post);
        else
            new InsertPost(false).execute(post);
    }

    public void stopDiscovering() {
        mDiscoveryEngine.stopScan();
    }

    public void stopBroadcasting() {
        mDiscoveryEngine.stopBroadcasting();
    }

    //Tasks
    @SuppressLint("StaticFieldLeak")
    private class GetAllPosts extends AsyncTask<Void, Void, ArrayList<Post>> {

        private String receiver;

        public GetAllPosts(String receiver) {
            this.receiver = receiver;
        }

        @Override
        protected ArrayList<Post> doInBackground(Void... voids) {
            return new ArrayList<>(mDb.postsDao().getPosts());
        }

        @Override
        protected void onPostExecute(ArrayList<Post> posts) {
            super.onPostExecute(posts);
            for (Post post :
                    posts) {
                if (post.getAuthor().equals(getUser()))
                    sendPost(post, receiver);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class InsertPost extends AsyncTask<Post, Void, Post> {

        private boolean send;

        public InsertPost(boolean send) {
            this.send = send;
        }

        @Override
        protected Post doInBackground(Post... posts) {
            Long id = mDb.postsDao().insertPost(posts[0]);
            posts[0].setId(id);
            return posts[0];
        }

        @Override
        protected void onPostExecute(Post post) {
            super.onPostExecute(post);
            if (send)
                sendPost(post);

            //Add Post
            postData.postValue(post);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetPost extends AsyncTask<Long, Void, Post> {

        @Override
        protected Post doInBackground(Long... longs) {
            return mDb.postsDao().getPostById(longs[0]);
        }

        @Override
        protected void onPostExecute(Post post) {
            super.onPostExecute(post);
            post.setId(null);
            sendPost(post);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DeletePost extends AsyncTask<Post, Void, Post> {

        private boolean delete;

        public DeletePost(boolean delete) {
            this.delete = delete;
        }

        @Override
        protected Post doInBackground(Post... posts) {
            mDb.postsDao().deletePost(posts[0]);
            return posts[0];
        }

        @Override
        protected void onPostExecute(Post post) {
            super.onPostExecute(post);
            if (delete) {
                post.setContent(null);
                postData.postValue(post);
                sendPost(post);
            }
        }
    }
}