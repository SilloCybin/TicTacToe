package com.example.cedricdevries.tictactoerecoveryapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String CHANNEL_ID = "1";

    @Override
    public void onNewToken(String s){
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remotemessage){
        super.onMessageReceived(remotemessage);

        String title = remotemessage.getNotification().getTitle();
        String body = remotemessage.getNotification().getBody();
        String click_action = remotemessage.getNotification().getClickAction();

        String dataTitle = remotemessage.getData().get("title");
        String dataBody = remotemessage.getData().get("body");
        String dataNotifSenderID = remotemessage.getData().get("notifSenderID");
        String dataNotifSenderName = remotemessage.getData().get("notifSenderName");
        String dataNotifRecipientName = remotemessage.getData().get("notifRecipientName");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                                                    .setSmallIcon(R.drawable.tictactoegrid)
                                                    .setContentTitle(title)
                                                    .setContentText(body)
                                                    .setAutoCancel(true);

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Game proposal", NotificationManager.IMPORTANCE_HIGH);

        Intent mToNotificationResponse = new Intent(click_action);
        mToNotificationResponse.putExtra("title", dataTitle);
        mToNotificationResponse.putExtra("body", dataBody);
        mToNotificationResponse.putExtra("notifSenderID", dataNotifSenderID);
        mToNotificationResponse.putExtra("notifSenderName", dataNotifSenderName);
        mToNotificationResponse.putExtra("notifRecipientName", dataNotifRecipientName);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, mToNotificationResponse, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotifyMngr = getSystemService(NotificationManager.class);
        int mNotificationId = (int) System.currentTimeMillis();
        mNotifyMngr.createNotificationChannel(channel);
        mNotifyMngr.notify(mNotificationId, mBuilder.build());

    }

}
