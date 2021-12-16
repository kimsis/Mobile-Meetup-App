package com.example.hanger.ui.settings;

import android.content.SharedPreferences;

import java.util.Calendar;

/**
 *  Helper class to manage access to {@link SharedPreferences}.
 */
public class SharedPreferencesHelper {

    // Keys for saving values in SharedPreferences.
    public static final String KEY_DISTANCE_TYPE = "key_distance_type";
    public static final String KEY_DISTANCE_AMOUNT = "key_distance_amount";
    public static final String KEY_THEME_THRESHOLD = "key_theme_threshold";

    // The injected SharedPreferences implementation to use for persistence.
    private final SharedPreferences mSharedPreferences;

    public SharedPreferencesHelper(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    /**
     * Saves the given {@link SharedPreferenceEntry} that contains the user's settings to
     * {@link SharedPreferences}.
     *
     * @param sharedPreferenceEntry contains data to save to {@link SharedPreferences}.
     * @return {@code true} if writing to {@link SharedPreferences} succeeded. {@code false}
     *         otherwise.
     */
    public boolean savePersonalInfo(SharedPreferenceEntry sharedPreferenceEntry){
        // Start a SharedPreferences transaction.
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_DISTANCE_TYPE, sharedPreferenceEntry.getDistanceType());
        editor.putString(KEY_DISTANCE_AMOUNT, sharedPreferenceEntry.getDistanceAmount());
        editor.putString(KEY_THEME_THRESHOLD, sharedPreferenceEntry.getDistanceAmount());

        // Commit changes to SharedPreferences.
        return editor.commit();
    }

    /**
     * Retrieves the {@link SharedPreferenceEntry} containing the user's personal information from
     * {@link SharedPreferences}.
     *
     * @return the Retrieved {@link SharedPreferenceEntry}.
     */
    public SharedPreferenceEntry getPersonalInfo() {
        // Get data from the SharedPreferences.
        String distanceType = mSharedPreferences.getString(KEY_DISTANCE_TYPE, "");
        String distanceAmount = mSharedPreferences.getString(KEY_DISTANCE_AMOUNT, "");
        float themeThreshold = mSharedPreferences.getFloat(KEY_THEME_THRESHOLD, 0.0f);

        // Create and fill a SharedPreferenceEntry model object.
        return new SharedPreferenceEntry(distanceType, distanceAmount, themeThreshold);
    }
}