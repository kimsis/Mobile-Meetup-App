package com.example.hanger.ui.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hanger.MainActivity;
import com.example.hanger.Notifications;
import com.example.hanger.R;
import com.example.hanger.model.HangerUser;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsFragment extends Fragment implements LocationListener {

    private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://hanger-1648c-default-rtdb.europe-west1.firebasedatabase.app/");
    private final FirebaseAuth authentication = FirebaseAuth.getInstance();
    private HangerUser currentUser;
    private static ArrayList<Marker> allMarkers = new ArrayList<>();
    private static GoogleMap map;
    private static boolean hasSubscribed = false;
    private boolean initialZoom = true;
    private static Circle currentCircle;
    private static final ArrayList<String> usersSeenInRange = new ArrayList<>();

    private final LocationListener locationChangeListener = this;
    private final OnMapReadyCallback mapReadyCallback = this::onMapReady;
    private Context context;

    private void onMapReady(@NonNull GoogleMap googleMap) {
        Activity currentActivity = getActivity();
        if (currentActivity != null && (
                ActivityCompat.checkSelfPermission(currentActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(currentActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, locationChangeListener);
            map = googleMap;
        } else
            showErrorToast("Rendering activity was null!");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        context = getContext();
        setCurrentUser();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(mapReadyCallback);
    }

    private void setCurrentUser() {
        database.getReference("locations/" + authentication.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(HangerUser.class);
                if(currentUser == null) {
                    showErrorToast("Failed to get user, please clear app data!");
                    return;
                }
                if (!hasSubscribed) {
                    setOnAnyLocationChangeListener();
                    hasSubscribed = true;
                } else {
                    setInitialPins();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showErrorToast("Failed get current user:" + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng nextLocation = new LatLng(location.getLatitude(), location.getLongitude());
        if (currentUser == null)
            database.getReference("locations/" + authentication.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    HangerUser fetchedUserLocation = dataSnapshot.getValue(HangerUser.class);
                    if (fetchedUserLocation == null) {
                        showErrorToast("Could not find current user when trying to set location");
                        return;
                    }
                    setCurrentUserLocation(fetchedUserLocation, nextLocation);
                    currentUser = fetchedUserLocation;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    showErrorToast("Failed to set user location:" + databaseError.getMessage());
                }
            });
        else setCurrentUserLocation(currentUser, nextLocation);
    }

    private void setCurrentUserLocation(HangerUser user, LatLng nextLocation) {
        user.setLatitude(nextLocation.latitude);
        user.setLongitude(nextLocation.longitude);
        database.getReference("locations/" + user.getId() + "/latitude").setValue(nextLocation.latitude);
        database.getReference("locations/" + user.getId() + "/longitude").setValue(nextLocation.longitude);

        if (currentCircle != null)
            currentCircle.remove();

        currentCircle = map.addCircle(new CircleOptions().center(nextLocation).radius(user.getDiscoveryRadiusMeters()).strokeColor(Color.BLUE));

        if (initialZoom) {
            initialZoom = false;
            CameraPosition cameraPosition = new CameraPosition.Builder().target(nextLocation).zoom(16.0f).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            map.moveCamera(cameraUpdate);
        } else
            map.animateCamera(CameraUpdateFactory.newLatLng(nextLocation));
    }

    private void setOnAnyLocationChangeListener() {
        database.getReference("locations").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<HangerUser> inRadius = getVisibleUsersInRadius(dataSnapshot);
                        setLocationPins(inRadius);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showErrorToast(databaseError.getMessage());
                    }
                });
    }

    private void setInitialPins() {
        database.getReference("locations").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<HangerUser> inRadius = getVisibleUsersInRadius(dataSnapshot);
                        setLocationPins(inRadius);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showErrorToast(databaseError.getMessage());
                    }
                });
    }

    private List<HangerUser> getVisibleUsersInRadius(DataSnapshot dataSnapshot) {

        HashMap<String, HangerUser> userLocations = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, HangerUser>>() {
        });

        ArrayList<HangerUser> visibleUsers = new ArrayList<>();

        if (userLocations != null)
            filterVisibleUsers(userLocations, visibleUsers);
        else
            showErrorToast("Could not find any user locations.");

        return visibleUsers;
    }

    private void filterVisibleUsers(HashMap<String, HangerUser> userLocations, ArrayList<HangerUser> visibleUsers) {
        visibleUsers.add(currentUser);

        for (Map.Entry<String, HangerUser> userLocation : userLocations.entrySet())
            filterIfUserShouldBeVisible(visibleUsers, userLocation);
    }

    private void filterIfUserShouldBeVisible(ArrayList<HangerUser> visibleUsers, Map.Entry<String, HangerUser> userLocation) {
        HangerUser otherUser = userLocation.getValue();

        if (otherUser.getId().equals(currentUser.getId())) {
            currentUser = otherUser;
            return;
        }

        if (!getBothInRangeOfEachOther(otherUser))
            return;

        resolveMatchedUsersInRange(visibleUsers, otherUser);
    }

    private boolean getBothInRangeOfEachOther(HangerUser otherUser) {
        double distanceToCurrentUser = getDistance(currentUser.getLatitude(), otherUser.getLatitude(), currentUser.getLongitude(), otherUser.getLongitude());
        return distanceToCurrentUser < currentUser.getDiscoveryRadiusMeters() && distanceToCurrentUser < otherUser.getDiscoveryRadiusMeters();
    }

    private void resolveMatchedUsersInRange(ArrayList<HangerUser> visibleUsers, HangerUser otherUser) {

        String currentWithOtherMatch = currentUser.getUsersMatched().get(otherUser.getId());
        String otherWithCurrentMatch = otherUser.getUsersMatched().get(currentUser.getId());
        boolean currentMatched = currentWithOtherMatch != null && currentWithOtherMatch.equals("Matched");
        boolean otherMatched = otherWithCurrentMatch != null && otherWithCurrentMatch.equals("Matched");

        if (currentMatched && otherMatched)
            visibleUsers.add(otherUser);

        // * First time we see the other user
        if (currentWithOtherMatch == null) {
            DatabaseReference currentUserReference = database.getReference("locations/" + currentUser.getId());
            currentUser.getUsersMatched().put(otherUser.getId(), "Unknown");
            currentUserReference.setValue(currentUser);
            notifyIfNotNotifiedInThisSession(otherUser);
        } else if (currentWithOtherMatch.equals("Unknown"))
            notifyIfNotNotifiedInThisSession(otherUser);
    }

    private void notifyIfNotNotifiedInThisSession(HangerUser otherUser) {
        if (!usersSeenInRange.contains(otherUser.getId())) {
            usersSeenInRange.add(otherUser.getId());
            showMatchRequestNotification(otherUser.getId(), otherUser.getName(), otherUser.hashCode());
        }
    }

    private void showErrorToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    private void setLocationPins(List<HangerUser> visibleUserLocations) {
        removeAllMarkers();
        for (HangerUser userLocation : visibleUserLocations) {
            LatLng location = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            Marker marker = map.addMarker(new MarkerOptions().position(location));
            if (marker != null) {
                marker.setTitle(userLocation.getName());
                allMarkers.add(marker);
            }
        }
    }

    private void removeAllMarkers() {
        for (Marker marker : allMarkers)
            marker.remove();

        allMarkers = new ArrayList<>();
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

    private void showMatchRequestNotification(String id, String name, int channelId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Intent intentActionAccept = new Intent(context, Notifications.class);
        Intent intentActionDecline = new Intent(context, Notifications.class);

        intentActionAccept.putExtra("action", "accept");
        intentActionAccept.putExtra("userId", id);
        intentActionAccept.putExtra("channelId", channelId);

        intentActionDecline.putExtra("action", "decline");
        intentActionDecline.putExtra("userId", id);
        intentActionAccept.putExtra("channelId", channelId);

        PendingIntent acceptRequest = PendingIntent.getBroadcast(context, 1, intentActionAccept, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent declineRequest = PendingIntent.getBroadcast(context, 2, intentActionDecline, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "someId")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(name + " is in your area!")
                .setContentText("Wanna see them on the map?")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.common_google_signin_btn_icon_light_normal, "Accept",
                        acceptRequest)
                .addAction(R.drawable.common_google_signin_btn_icon_dark_normal, "Decline",
                        declineRequest);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(channelId, builder.build());
    }
}