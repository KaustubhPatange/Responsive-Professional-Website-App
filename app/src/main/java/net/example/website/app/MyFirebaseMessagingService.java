package net.example.website.app;

/**
 * Created by kp on 1/7/17.
 */


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String message, title, link, post;
    Map<String, String> Data;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("Notif", remoteMessage.toString());
        Data = remoteMessage.getData();
        message = Data.get("body");
        title = Data.get("title");
        link= Data.get("image");
        post= Data.get("post");
        sendNotification(message, title, link, post);

        //Toast.makeText(getApplicationContext(), "Status: " + String.valueOf(status), Toast.LENGTH_SHORT).show();

    }

    private void sendNotification(String messageBody, String Title, String data, String post) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("link",post);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Bitmap image = getBitmapFromURL(data);
        long[] vibrate = {100,250,100,250};
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setTicker(Title)
                .setContentTitle(Title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(10)
                .setVibrate(vibrate)
                .setContentIntent(pendingIntent);

        if (image != null) {
            notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(image).setSummaryText(messageBody));
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}