package com.example.ckddn.capstoneproject2018_2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.ckddn.capstoneproject2018_2.Oblu.DeviceControlActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

public class LocFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived: 호출됨.");
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();
        String longitude = data.get("longitude");
        String latitude = data.get("latitude");

        sendToUserActivity(remoteMessage.getNotification(), longitude, latitude);
        Log.d(TAG, "longitude: "+ longitude + " latitude: " + latitude);
    }

    private void sendToUserActivity(RemoteMessage.Notification notification, String longitude, String latitude) {
        Intent intent = new Intent(this, DeviceControlActivity.class);
//        Intent intent = new Intent(this, UserActivitiy.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);

        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        getApplicationContext().startActivity(intent);

    }
}
