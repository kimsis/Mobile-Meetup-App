package com.example.hanger.model;

import android.graphics.Bitmap;

public class Connection {
    String id;
    String username;
    String isMatched;
    Bitmap avatar;

    public Connection(String id, String username, String isMatched) {
        this.id = id;
        this.username = username;
        this.isMatched = isMatched;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIsMatched() {
        return isMatched;
    }

    public void setIsMatched(String isMatched) {
        this.isMatched = isMatched;
    }
}
