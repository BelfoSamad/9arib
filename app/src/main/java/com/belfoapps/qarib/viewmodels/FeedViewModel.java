package com.belfoapps.qarib.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.belfoapps.qarib.base.AppDatabase;
import com.belfoapps.qarib.pojo.Post;
import com.belfoapps.qarib.ui.adapters.PostsAdapter;
import com.huawei.hms.nearby.Nearby;
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

import dagger.hilt.android.qualifiers.ActivityContext;

public class FeedViewModel extends ViewModel {
    private static final String TAG = "FeedViewModel";

    /***********************************************************************************************
     * *********************************** Declarations
     */

    private DataCallback mDataCb = new DataCallback() {
        @Override
        public void onReceived(String string, Data data) {
        }

        @Override
        public void onTransferUpdate(String string, TransferStateUpdate update) {
        }
    };
    private ConnectCallback mConnCb = new ConnectCallback() {
        @Override
        public void onEstablish(String s, ConnectInfo connectInfo) {
            mDiscoveryEngine.acceptConnect(s, mDataCb);
        }

        @Override
        public void onResult(String s, ConnectResult connectResult) {
        }

        @Override
        public void onDisconnected(String s) {
        }
    };

    private AppDatabase mDb;
    private MutableLiveData<Post> postData;
    private PostsAdapter mAdapter;
    private DiscoveryEngine mDiscoveryEngine;
    private TransferEngine mTransferEngine;

    /***********************************************************************************************
     * *********************************** Constructor
     */
    @ViewModelInject
    public FeedViewModel(@ActivityContext Context context, AppDatabase mDb) {
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

    //Methods
    public void startDiscovering(Context context, String useranme) {
        ScanOption.Builder discBuilder = new ScanOption.Builder();
        discBuilder.setPolicy(Policy.POLICY_MESH);
        mDiscoveryEngine.startScan(context.getPackageName(), new ScanEndpointCallback() {
            @Override
            public void onFound(String s, ScanEndpointInfo scanEndpointInfo) {
                mDiscoveryEngine.requestConnect(useranme, s, mConnCb);
            }

            @Override
            public void onLost(String s) {

            }
        }, discBuilder.build()).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.getMessage()));
    }

    public void startBroadcasting(Context context, String username) {
        BroadcastOption.Builder advBuilder = new BroadcastOption.Builder();
        advBuilder.setPolicy(Policy.POLICY_STAR);
        mDiscoveryEngine.startBroadcasting(username, context.getPackageName(), mConnCb,
                advBuilder.build());
    }

    public void sendPost(String post) {

    }

    public void hidePost(Long post_idm, boolean delete) {

    }

    public void rePost(Long post_id) {

    }

    public void sendMessage(String receiver, String msg) {
        Data data = Data.fromBytes(msg.getBytes(Charset.defaultCharset()));
        mTransferEngine.sendData(receiver, data);
    }
}