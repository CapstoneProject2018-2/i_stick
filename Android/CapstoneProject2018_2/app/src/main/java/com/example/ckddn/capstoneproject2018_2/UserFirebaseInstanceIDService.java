package com.example.ckddn.capstoneproject2018_2;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class UserFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "UserFirebaseIIDService";
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "onTokenRefresh: " + refreshedToken);
    }
}
