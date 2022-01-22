package com.example.hanger.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.hanger.ui.settings.SharedPreferencesHelper;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HangerUser {

    public HangerUser() {
    }

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String name = "Anonymous";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private double latitude = 0;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    private double longitude = 0;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private int discoveryRadiusMeters = 50;

    public int getDiscoveryRadiusMeters() {
        return discoveryRadiusMeters;
    }

    public void setDiscoveryRadiusMeters(int discoveryRadiusMeters) {
        this.discoveryRadiusMeters = discoveryRadiusMeters;
    }

    private Map<String, Boolean> usersMatched = new HashMap<>();

    public Map<String, Boolean> getUsersMatched() {
        return usersMatched;
    }

    public void setUsersMatched(Map<String, Boolean> map) {
        usersMatched = map;
    }

    private ArrayList<String> usersLastInRange = new ArrayList<>();

    public ArrayList<String> getUsersLastInRange() {
        return usersLastInRange;
    }

    public void setUsersLastInRange(ArrayList<String> map) {
        usersLastInRange = map;
    }

    private List<String> usersNotified;

    public List<String> getUsersNotified() {
        return usersNotified;
    }

    public void setUsersNotified(List<String> usersNotified) {
        this.usersNotified = usersNotified;
    }
}

