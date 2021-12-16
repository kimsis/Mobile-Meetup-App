package com.example.hanger.ui.settings;

import java.util.Calendar;

/**
 * Model class containing personal information that will be saved to SharedPreferences.
 */
public class SharedPreferenceEntry {

    // Distance type selected
    private final String mDistanceType;

    // Distance amount
    private final String mDistanceAmount;

    // Theme threshold
    private final float mThemeThreshold;

    public SharedPreferenceEntry(String distanceType, String distanceAmount, float themeThreshold) {
        mDistanceType = distanceType;
        mDistanceAmount = distanceAmount;
        mThemeThreshold = themeThreshold;
    }

    public String getDistanceType() {
        return mDistanceType;
    }

    public String getDistanceAmount() {
        return mDistanceAmount;
    }

    public float getThemeThreshold() {
        return mThemeThreshold;
    }
}