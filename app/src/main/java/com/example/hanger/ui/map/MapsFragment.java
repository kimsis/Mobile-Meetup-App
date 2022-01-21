package com.example.hanger.ui.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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

import com.example.hanger.R;
import com.example.hanger.model.HangerUser;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
    private ArrayList<Marker> allMarkers = new ArrayList<>();
    private static GoogleMap map;
    private Circle currentCircle;

    private final LocationListener locationChangeListener = this;
    private final OnMapReadyCallback mapReadyCallback = this::onMapReady;

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
                setOnAnyLocationChangeListener();
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
        database.getReference("locations/" + user.getId()).setValue(user);
        if (currentCircle != null)
            currentCircle.remove();
        currentCircle = map.addCircle(new CircleOptions().center(nextLocation).radius(user.getDiscoveryRadiusMeters()).strokeColor(Color.BLUE));
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

    private List<HangerUser> getVisibleUsersInRadius(DataSnapshot dataSnapshot) {

        HashMap<String, HangerUser> userLocations = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, HangerUser>>() {
        });

        if (userLocations == null) {
            showErrorToast("Could not find any user locations.");
            return new ArrayList<>();
        }

        HangerUser currentUserLocation = userLocations.get(authentication.getUid());
        if (currentUserLocation == null) {
            showErrorToast("Could not find current user in the set of locations.");
            return new ArrayList<>();
        }

        ArrayList<HangerUser> filtered = new ArrayList<>();
        filtered.add(currentUser);

        for (Map.Entry<String, HangerUser> userLocation : userLocations.entrySet()) {

            double distanceToCurrentUser = getDistance(currentUserLocation.getLatitude(), userLocation.getValue().getLatitude(), currentUserLocation.getLongitude(), userLocation.getValue().getLongitude());
            Boolean matchedWithCurrentUser = userLocation.getValue().getUsersMatched().get(currentUserLocation.getId());
            boolean isInRadius = distanceToCurrentUser < currentUser.getDiscoveryRadiusMeters() && distanceToCurrentUser < userLocation.getValue().getDiscoveryRadiusMeters();
            boolean shouldShowOnMap = isInRadius && matchedWithCurrentUser != null && matchedWithCurrentUser;

            if (shouldShowOnMap)
                filtered.add(userLocation.getValue());

            if (matchedWithCurrentUser != null && !matchedWithCurrentUser) {
                showErrorToast("Notifications are not yet implemented!");
            }
        }

        return filtered;
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

    private void removeAllMarkers(){
        for (Marker marker : allMarkers) {
            marker.remove();
        }
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
}