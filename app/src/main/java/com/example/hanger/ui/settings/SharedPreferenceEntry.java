package com.example.hanger.ui.settings;

import java.util.Calendar;

/**
 * Model class containing personal information that will be saved to SharedPreferences.
 */
public class SharedPreferenceEntry {

    // Name of the user.
    private final String mName;

    // Date of Birth of the user.
    private final Calendar mDateOfBirth;

    // Email address of the user.
    private final String mEmail;

    // Distance type selected
    private final String mDistanceType;

    // Distance amount
    private final String mDistanceAmount;

    public SharedPreferenceEntry(String name, Calendar dateOfBirth, String email, String distanceType, String distanceAmount) {
        mName = name;
        mDateOfBirth = dateOfBirth;
        mEmail = email;
        mDistanceType = distanceType;
        mDistanceAmount = distanceAmount;
    }

    public String getName() {
        return mName;
    }

    public Calendar getDateOfBirth() {
        return mDateOfBirth;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getDistanceType() {
        return mDistanceType;
    }

    public String getDistanceAmount() {
        return mDistanceAmount;
    }
}