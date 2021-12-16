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

    public HangerUser() {

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


    private double latitude;
    private double longitude;
    private int discoveryRadiusMeters = -1;
    private String name;
    private String id;
}

