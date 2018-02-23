package com.example.batere3a.joggingpartner.notify;

import android.app.NotificationManager;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.batere3a.joggingpartner.R;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

/**
 * Created by adity on 2/23/2018.
 */

public class MyFirebaseService extends FirebaseMessagingService {
    private LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("MessageReceived", remoteMessage.toString());
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //setupChannels();
        }

        Intent intent = new Intent("MyData");
        broadcaster.sendBroadcast(intent);

        int notificationId = new Random().nextInt(60000);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MyFirebaseService.this, "")
                .setContentText(remoteMessage.getData().get("message"))
                .setContentTitle(remoteMessage.getData().get("title"))
                .setSmallIcon(R.drawable.jogging_partner_logo)
                .setAutoCancel(false)
                .setSound(defaultSoundUri);

        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
