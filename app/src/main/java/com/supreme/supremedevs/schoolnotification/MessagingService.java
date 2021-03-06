package com.supreme.supremedevs.schoolnotification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;



import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import org.json.JSONObject;

import java.util.Random;


public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "MessagingService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

//            handleNow(remoteMessage.getData().toString());

            dialogmessage("Recieved data", remoteMessage.getData().toString());
            sendNotification("Recieved bbbb ", remoteMessage.getData().toString());
            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                dialogmessage("Recieved notificatin body", remoteMessage.getNotification().getBody());
            }

//            sendNotification(title, message);

        } else {
            dialogmessage("error", "size is 0");
        }
    }

    private void handleNow(String result) {
        try {
            JSONObject json = new JSONObject(result);
            JSONObject data = json.getJSONObject("data");
            String title = data.getString("title");
            String message = data.getString("message");


            sendNotification(title, message);
            Intent pushNotification = new Intent(MainActivity.NOTIFICATION_SERVICE);
            pushNotification.putExtra("message", message);
            pushNotification.putExtra("title", title);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);


            dialogmessage(title, message);

        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void dialogmessage(String title, String message) {

    }


    private void sendNotification(String title, String message) {

        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra("notification", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.launcher)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Random random = new Random();
        int id = random.nextInt(20054);
        int aid = random.nextInt(9395);
        int finalid = id + aid;

        notificationManager.notify(finalid, notificationBuilder.build());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("GasLeakage").child("token");
        firebaseDatabase.setValue(s);
    }
}
