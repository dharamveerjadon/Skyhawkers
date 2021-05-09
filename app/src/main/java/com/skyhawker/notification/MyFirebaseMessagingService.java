package com.skyhawker.notification;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.skyhawker.R;
import com.skyhawker.activities.SplashActivity;
import com.skyhawker.utils.AppPreferences;
import com.skyhawker.utils.Keys;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final Random mRnd = new Random();

    private static final String CHANNEL_ID = "skyHawker_01";// The id of the channel.
    private static final CharSequence CHANNEL_NAME = "skyHawker";// The user-visible name of the channel.

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        Map<String, String> map = remoteMessage.getData();
        sendNotification(map);


    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        AppPreferences.setFcmToken(token);
    }

    /**
     * Send notification window
     *
     * @param map map
     */
    private void sendNotification(Map<String, String> map) {

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        @SuppressLint("IconColors")
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(map.get(Keys.TITLE))
                .setContentText(map.get(Keys.MESSAGE))
                .setSmallIcon(R.drawable.skyhawk_splash_logo)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(map.get(Keys.MESSAGE)))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(createPendingIntentForNotifications(map));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager
                        .IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(mChannel);
            }

            notificationManager.notify(mRnd.nextInt(), notificationBuilder.build());
        }
    }

    private PendingIntent createPendingIntentForNotifications(Map<String, String> map) {
        Intent notificationIntent = new Intent(this, SplashActivity.class);
        notificationIntent.putExtra(Keys.NOTIFICATION, new HashMap<>(map));
        return PendingIntent.getActivity(this, mRnd.nextInt(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
