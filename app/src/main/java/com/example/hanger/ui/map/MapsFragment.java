package com.example.hanger.ui.map;

import static com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hanger.MainActivity;
import com.example.hanger.Notifications;
import com.example.hanger.R;
import com.example.hanger.model.HangerUser;
import com.example.hanger.ui.settings.SharedPreferenceEntry;
import com.example.hanger.ui.settings.SharedPreferencesHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsFragment extends Fragment implements LocationListener {

    private GoogleMap map;
    private final LocationListener listener = this;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private ArrayList<Marker> allMarkers;
    private  Circle currentCircle;
    private String CHANNEL_ID = "someId";
    private double distance;
    private HashMap<String, HangerUser> userMappings;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, listener);
            }
            map = googleMap;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(preferences);
        SharedPreferenceEntry sharedPreferenceEntry = sharedPreferencesHelper.getPersonalInfo();
        String type = sharedPreferenceEntry.getDistanceType();
        float amount = sharedPreferenceEntry.getDistanceAmount();
        switch (type) {
            case "Km.":
                distance = amount * 1000;
                break;
            case "Mi.":
                distance = amount * 1609.34;
                break;
            case "Ft.":
                distance = amount * 0.3048;
            default:
                distance = amount;
                break;
        }

        allMarkers = new ArrayList<>();

        this.database = FirebaseDatabase.getInstance("https://hanger-1648c-default-rtdb.europe-west1.firebasedatabase.app/");
        this.auth = FirebaseAuth.getInstance();

        DatabaseReference allLocations = database.getReference("locations");

        DatabaseReference myLocation = database.getReference("locations/" + auth.getUid());

        allLocations.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<HangerUser> filteredUsers = FilterRelevantUsers(dataSnapshot);
                        setLocationPins(filteredUsers);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        return view;
    }

    private List<HangerUser> FilterRelevantUsers(DataSnapshot dataSnapshot) {

        GenericTypeIndicator<HashMap<String, HangerUser>> t = new GenericTypeIndicator<HashMap<String, HangerUser>>() {};

        userMappings = dataSnapshot.getValue(t);

        HangerUser currentUser = userMappings.get(auth.getUid());

        ArrayList<HangerUser> filtered =  new ArrayList<>();

        for (Map.Entry<String, HangerUser> entry: userMappings.entrySet())
        {
            if(currentUser == null)
                continue;

            double distanceToCurrentUser = getDistance(currentUser.getLatitude(), entry.getValue().getLatitude(), currentUser.getLongitude(), entry.getValue().getLongitude());

            if(distanceToCurrentUser < distance)
                filtered.add(entry.getValue());
            if(!entry.getValue().getId().equals(auth.getCurrentUser().getUid()))
            {
                DatabaseReference ref = FirebaseDatabase.getInstance("https://hanger-1648c-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("locations/" + FirebaseAuth.getInstance().getUid() + "/usersNotified");

                if(currentUser.getUsersMatched().get(entry.getValue().getId()).equals("false") && !currentUser.getUsersNotified().contains(entry.getValue().getId()))
                {
                    showMatchRequestNotification(entry.getValue().getId(), entry.getValue().getName(), entry.hashCode());
                    currentUser.addToUserNotified(entry.getValue().getId());

                    ref.setValue(currentUser.getUsersNotified());
                }
            }
        }
        return filtered;
    }


    private void showMatchRequestNotification(String id, String name, int channelId) {
        Intent intent = new Intent(this.getContext(),MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this.getContext(), 0, intent, 0);

        Intent intentActionAccept = new Intent(this.getContext(), Notifications.class);
        Intent intentActionDecline = new Intent(this.getContext(), Notifications.class);

        intentActionAccept.putExtra("action", "accept");
        intentActionAccept.putExtra("userId",id);

        intentActionDecline.putExtra("action", "decline");
        intentActionDecline.putExtra("userId",id);

        PendingIntent acceptRequest = PendingIntent.getBroadcast(this.getContext(), 1, intentActionAccept, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent declineRequest = PendingIntent.getBroadcast(this.getContext(), 2, intentActionDecline, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(name + " is in your area :O")
                .setContentText("Wanna see me on the map?")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.common_google_signin_btn_icon_light_normal, "Accept",
                acceptRequest)
                .addAction(R.drawable.common_google_signin_btn_icon_dark_normal, "Decline",
                        declineRequest);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.getContext());
        notificationManager.notify(channelId, builder.build());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());

        FirebaseUser currentUser = auth.getCurrentUser();

        if(currentUser == null)
            return;

        DatabaseReference personLocationReference = database.getReference("locations/" + currentUser.getUid());

        personLocationReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HangerUser existingUser = dataSnapshot.getValue(HangerUser.class);

                if(existingUser == null)
                    existingUser = new HangerUser(currentUser.getUid());

                existingUser.setLatitude(location.getLatitude());
                existingUser.setLongitude(location.getLongitude());

                database.getReference("locations/" + currentUser.getUid()).setValue(existingUser);

                if(currentCircle != null)
                    currentCircle.remove();

                currentCircle = map.addCircle(new CircleOptions().center(current).radius(distance).strokeColor(Color.BLUE));
                Log.d(TAG, "onDataChange: " + distance);

                map.animateCamera(CameraUpdateFactory.newLatLng(current));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    private void setLocationPins(List<HangerUser> users){

        for(Marker marker : allMarkers){
            marker.remove();
        }
        //Displays personal marker
        for (HangerUser user:users) {
            if(user.getId().equals(auth.getCurrentUser().getUid()))
            {
                double latitude = user.getLatitude();
                double longitude = user.getLongitude();
                LatLng location = new LatLng(latitude, longitude);
                Marker marker = map.addMarker(new MarkerOptions().position(location));
                marker.setTitle(user.getName());
                allMarkers.add(marker);
            }
        }

        for(HangerUser entry : users) {
            double latitude = entry.getLatitude();
            double longitude = entry.getLongitude();


            LatLng location = new LatLng(latitude, longitude);

            if(entry.getUsersMatched().get(auth.getCurrentUser().getUid()) == "true")
            {
                for (HangerUser user:users) {
                    if(user.getId().equals(auth.getCurrentUser().getUid()))
                    {
                        if(user.getUsersMatched().get(entry.getId()) == "true")
                        {
                            Marker marker = map.addMarker(new MarkerOptions().position(location));

                            marker.setTitle(entry.getName());

                            allMarkers.add(marker);
                        }
                    }
                }
            }

        }
    }

    public static double getDistance(double lat1, double lat2, double lon1, double lon2) {

        final int R = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000;

        double height = 0;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}