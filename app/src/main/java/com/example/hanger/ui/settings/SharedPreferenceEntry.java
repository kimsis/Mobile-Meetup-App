package com.example.hanger.ui.settings;

import java.util.Calendar;

/**
 * Model class containing personal information that will be saved to SharedPreferences.
 */
public class SharedPreferenceEntry {

    // Distance type selected
    private String mDistanceType;

    // Distance amount
    private float mDistanceAmount;

    // Theme threshold
    private float mThemeThreshold;

    public SharedPreferenceEntry(String distanceType, float distanceAmount, float themeThreshold) {
        mDistanceType = distanceType;
        mDistanceAmount = distanceAmount;
        mThemeThreshold = themeThreshold;
    }

    public String getDistanceType() {
        return mDistanceType;
    }

    public float getDistanceAmount() {
        return mDistanceAmount;
    }

    public float getThemeThreshold() {
        return mThemeThreshold;
    }

    public void setDistanceType(String mDistanceType) {
        this.mDistanceType = mDistanceType;
    }

    public void setDistanceAmount(float mDistanceAmount) {
        this.mDistanceAmount = mDistanceAmount;
    }

    public void setThemeThreshold(float mThemeThreshold) {
        this.mThemeThreshold = mThemeThreshold;
    }
}