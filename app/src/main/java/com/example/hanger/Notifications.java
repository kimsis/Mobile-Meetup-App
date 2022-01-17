package com.example.hanger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationManagerCompat;

import com.example.hanger.model.HangerUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ktx.AuthKt;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.ktx.Firebase;

import java.util.HashMap;


public class Notifications extends BroadcastReceiver {

    private FirebaseDatabase db;
    private FirebaseAuth auth;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra("action");
        String userId = intent.getStringExtra("userId");
        db = FirebaseDatabase.getInstance("https://hanger-1648c-default-rtdb.europe-west1.firebasedatabase.app/");
        this.auth = FirebaseAuth.getInstance();
        if(action.equals("accept")){

            DatabaseReference allLocations = db.getReference("locations");
            allLocations.child(auth.getCurrentUser().getUid())
                    .child("usersMatched")
                    .child(userId)
                    .setValue(true);
        }
        if (action.equals("decline")) {
            DatabaseReference allLocations = db.getReference("locations");
            allLocations.child(auth.getCurrentUser().getUid())
                    .child("usersMatched")
                    .child(userId)
                    .setValue(false);
        }
        if (context != null) {
            NotificationManagerCompat.from(context).cancel(1);
        }
    }
}
