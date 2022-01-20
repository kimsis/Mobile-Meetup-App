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

    public static ArrayList<Integer> getRadiusMetersOptions()
    {
        ArrayList<Integer> map = new ArrayList<>();

        map.add(100);
        map.add(500);
        map.add(1000);
        map.add(2000);
        map.add(5000);
        map.add(30000);
        map.add(1000000);

        return map;
    }

    public HangerUser() {
        usersNotified = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HangerUser(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getDiscoveryRadiusMeters() {

        if(discoveryRadiusMeters == -1)
            return getRadiusMetersOptions().get(4);
        else
            return discoveryRadiusMeters;

    }

    public int getDiscoveryRadius() {
        return 1;
    }

    public void setDiscoveryRadiusMeters(int discoveryRadiusMeters) {
        this.discoveryRadiusMeters = discoveryRadiusMeters;
    }

    public String getName() {

        if(name == null || name.equals(""))
            return "Anonymous";

        return name;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setUsersMatched(Object map)
    {
        usersMatched = map;
    }

    public Map<String, String> getUsersMatched()
    {
        Map<String, String> hm = new HashMap<String,String>();
        if(usersMatched == null)
            return hm;

        String[] keys = usersMatched.toString()
                .replace("{","")
                .replace("}","")
                .replace("=","")
                .replace("false","")
                .replace("true","").trim().split(",");
        String[] values = usersMatched.toString()
                .split(",");

        for (int i = 0; i < keys.length; i++)
        {
            if(values[i].contains("false"))
            {
                values[i] = "false";
            }
            else {
                values[i] = "true";
            }
            hm.put(keys[i].trim(),values[i]);
        }
        return hm;
    }


    private double latitude;
    private double longitude;
    private int discoveryRadiusMeters = -1;
    private String name;
    private String id;
    private Object usersMatched;
    private List<String> usersNotified;

    public List<String> getUsersNotified() {
        return usersNotified;
    }

    public void setUsersNotified(List<String> usersNotified) {
        this.usersNotified = usersNotified;
    }

    public void addToUserNotified(String user){
        usersNotified.add(user);
    }
}

