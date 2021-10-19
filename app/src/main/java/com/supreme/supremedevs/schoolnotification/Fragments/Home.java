package com.supreme.supremedevs.schoolnotification.Fragments;


import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.supreme.supremedevs.schoolnotification.MainActivity;
import com.supreme.supremedevs.schoolnotification.R;


import java.util.ArrayList;


public class Home extends Fragment {


    ArrayList<String> documentIds;
    FirebaseFirestore firebaseFirestore;
    public static final String NOTIFICATION = "PushNotification";
    TextView currentTxv;
    Boolean isShowingDialg = false;
    String token;
    DatabaseReference firebaseDatabase;
    DatabaseReference rootDatabase;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        documentIds = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();

        currentTxv = view.findViewById(R.id.gasLevels);

        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("GasLeakage");
        rootDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseInstallations.getInstance().getToken(true)
                .addOnCompleteListener(new OnCompleteListener<InstallationTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstallationTokenResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Get Instance Failed", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        token = task.getResult().getToken();
                        FirebaseMessaging.getInstance().subscribeToTopic("All");
                        rootDatabase.child("token").setValue(token);

                    }
                });

        rootDatabase.child("CurrentLevel").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                  //  showDialog();

                    String current = dataSnapshot.getValue().toString();

                    String message = "Current Gas Levels : " + current;

                    currentTxv.setText(message);
                    buildLocalNotification("Gas Leakage Detected", "Check out to our latest updates");



            }}
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        return view;


    }
        private void showDialog(){

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity().getApplicationContext());
            alertDialogBuilder.setTitle("Warning");
            alertDialogBuilder.setMessage("Gas leakage detected");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    isShowingDialg = false;
                }
            });

            if (!isShowingDialg) {
                alertDialogBuilder.show();
                isShowingDialg = true;
            }

        }




        private void buildLocalNotification(String title, String message) {

            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            String channelId = getString(R.string.app_name);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(getActivity(), channelId)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setSound(defaultSoundUri)
                            .setContentTitle(title)
                            .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);


            /**
             * Since Android Oreo
             */

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                createNotificationChannel(channelId, notificationManager);

            }

            assert notificationManager != null;
            notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
        }

        @SuppressLint("NewApi")
        public void createNotificationChannel(String channelId, NotificationManager notificationManager) {


            @SuppressLint("WrongConstant")
            NotificationChannel channel = new NotificationChannel(channelId, getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_MAX);
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            assert notificationManager != null;

            channel.setLightColor(Color.parseColor("#F1E605"));

            channel.canShowBadge();
            channel.enableVibration(true);
            notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);

        }
    }










