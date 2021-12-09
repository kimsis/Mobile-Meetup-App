package com.example.hanger.model;

import java.util.ArrayList;

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

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HangerUser() {}

    public HangerUser(String id, double latitude, double longitude) {
        this.discoveryRadiusMeters = HangerUser.getRadiusMetersOptions().get(4);
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getDiscoveryRadiusMeters() {
        return discoveryRadiusMeters;
    }

    public void setDiscoveryRadiusMeters(int discoveryRadiusMeters) {
        this.discoveryRadiusMeters = discoveryRadiusMeters;
    }

    public String getName() {

        if(name == null || name.equals(""))
            return "Anonymous";

        return name;
    }

    public String getId() {
        return id;
    }

    private double latitude;
    private double longitude;
    private int discoveryRadiusMeters;
    private String name;
    private String id;
}

