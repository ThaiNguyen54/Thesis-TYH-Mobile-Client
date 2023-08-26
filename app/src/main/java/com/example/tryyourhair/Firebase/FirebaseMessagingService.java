package com.example.tryyourhair.Firebase;

import android.content.Intent;
import android.service.quicksettings.Tile;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.tryyourhair.GeneratedHair;
import com.example.tryyourhair.Singleton.Singleton;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMessagingService";

    public FirebaseMessagingService() {}

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Singleton singleton;
        singleton = Singleton.getInstance();

        // Check if message contains data payload
        if (message.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + message.getData().get("GeneratedImageURL"));
            singleton.setReceivedGeneratedHair(true);
            singleton.setGeneratedURL(message.getData().get("GeneratedImageURL"));
            Intent GeneratedHairIntent = new Intent(this, GeneratedHair.class);
            GeneratedHairIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(GeneratedHairIntent);
        }


        // Check if message contains a notification payload
        if (message.getNotification() != null) {
            String title = message.getNotification().getTitle();
            String msg = message.getNotification().getBody();

            Log.d(TAG, "Message Notification Title: " + title);
            Log.d(TAG, "Message Notification Body: " + msg);
        }
    }
}
