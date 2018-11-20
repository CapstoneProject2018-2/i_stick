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
        Intent intent = new Intent(this, UserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);

        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        getApplicationContext().startActivity(intent);

//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_CANCEL_CURRENT);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_dialog_info))
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("FCM message")
//                .setContentText("새로운 목적지가 설정됨")
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }
}
