package com.example.hanger.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hanger.LoginRegisterActivity;
import com.example.hanger.MainActivity;
import com.example.hanger.R;
import com.example.hanger.databinding.FragmentSettingsBinding;
import com.example.hanger.model.HangerUser;
import com.example.hanger.ui.helpers.ImageHelper;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class SettingsFragment extends Fragment implements SensorEventListener {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://hanger-1648c-default-rtdb.europe-west1.firebasedatabase.app/");
    private final FirebaseAuth authentication = FirebaseAuth.getInstance();
    private HangerUser currentUser;
    private FragmentSettingsBinding binding;
    // constant to compare
    // the activity result code
    int SELECT_IMAGE = 200;
    private static final String TAG = "Settings Fragment";
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private float sensorSensitivity;
    private SharedPreferences preferences;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private SharedPreferenceEntry sharedPreferenceEntry;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private float distance;
    private String distanceType;
    private Context context;
    private ImageHelper imageHelper;
    private boolean firstTrigger = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        getCurrentUser();
        context = getActivity();
        imageHelper = new ImageHelper(context);
        userImage();
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent logoutIntent = new Intent(context, LoginRegisterActivity.class);
                startActivity(logoutIntent);
            }
        });

        binding.btnSaveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = database.getReference().child("locations/" + authentication.getUid() + "/name");
                ref.setValue(binding.etUserName.getText().toString());
            }
        });

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesHelper = new SharedPreferencesHelper(preferences);
        sharedPreferenceEntry = sharedPreferencesHelper.getPersonalInfo();
        setupPreferences();

        return root;
    }

    /**
     * Get user image and add onClick lister for image update
     */
    private void userImage(){
        imageHelper.getImage(binding.ivUserProfile, authentication.getUid());
        ActivityResultLauncher<String> mGetContent = this.registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                            binding.ivUserProfile.setImageBitmap(bitmap);
                            imageHelper.uploadImage(uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        binding.ivUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });
    }

    /**
     * Get currently logged in user and display his name
     */
    private void getCurrentUser() {
        database.getReference("locations/" + authentication.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HangerUser fetchedUserLocation = dataSnapshot.getValue(HangerUser.class);
                if (fetchedUserLocation == null) {
                    showErrorToast("Could not find current user when trying to set location");
                    return;
                }
                currentUser = fetchedUserLocation;
                binding.etUserName.setText(currentUser.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showErrorToast("Failed to set user location:" + databaseError.getMessage());
            }
        });
    }

    /**
     * Get current sharedPreferences and show them in appropriate fields
     */
    private void setupPreferences() {
        String type = sharedPreferenceEntry.getDistanceType();
        distanceType = type == null || type.isEmpty() || type.equals("") ? "M." : sharedPreferenceEntry.getDistanceType();
        distance = sharedPreferenceEntry.getDistanceAmount();
        sensorSensitivity = sharedPreferenceEntry.getThemeThreshold();

        binding.tvDistanceType.setText(distanceType);
        binding.tvDistance.setText(distance + " " + distanceType);
        binding.tvNumberSensorSensitivity.setText(sensorSensitivity + "");
        switch (distanceType) {
            case "M.":
                binding.btnGroupDistanceType.check(R.id.btn_m);
                break;
            case "Km.":
                binding.btnGroupDistanceType.check(R.id.btn_km);
                break;
            case "Ft.":
                binding.btnGroupDistanceType.check(R.id.btn_ft);
                break;
            case "Mi.":
                binding.btnGroupDistanceType.check(R.id.btn_mi);
                break;
        }
        binding.sliderSensorSensitivity.setValue(sensorSensitivity);
        binding.sliderSensorSensitivity.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                sharedPreferencesHelper.saveThemeThreshold(value);
            }
        });
        binding.sliderDistance.setValue(sharedPreferenceEntry.getDistanceAmount());
        binding.sliderDistance.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                saveDistance(value);
                sharedPreferencesHelper.saveDistanceAmount(value);
            }
        });

        binding.btnM.setOnClickListener(x ->
                saveDistanceType("M."));
        binding.btnKm.setOnClickListener(x ->
                saveDistanceType("Km."));
        binding.btnFt.setOnClickListener(x ->
                saveDistanceType("Ft."));
        binding.btnMi.setOnClickListener(x ->
                saveDistanceType("Mi."));

        // Saving it from garbage collector
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(
                    SharedPreferences prefs, String key) {

                switch (key) {
                    case SharedPreferencesHelper.KEY_DISTANCE_TYPE:
                        distanceType = prefs.getString(SharedPreferencesHelper.KEY_DISTANCE_TYPE, "");
                        binding.tvDistanceType.setText(distanceType);
                        binding.tvDistance.setText(distance + distanceType);
                        break;
                    case SharedPreferencesHelper.KEY_DISTANCE_AMOUNT:
                        distance = prefs.getFloat(SharedPreferencesHelper.KEY_DISTANCE_AMOUNT, 0.0f);
                        binding.tvDistance.setText(distance + distanceType);
                        break;
                    case SharedPreferencesHelper.KEY_THEME_THRESHOLD:
                        sensorSensitivity = prefs.getFloat(SharedPreferencesHelper.KEY_THEME_THRESHOLD, 0.0f);
                        binding.tvNumberSensorSensitivity.setText(sensorSensitivity + "");
                        break;
                    default:
                        break;
                }
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Save the given distanceType into shared preferences and update distance
     * @param type
     */
    private void saveDistanceType(String type) {
        sharedPreferencesHelper.saveDistanceType(type);

        saveDistance(binding.sliderDistance.getValue());
    }

    /**
     * Save the given distance into shared preferences
     * @param value
     */
    private void saveDistance(float value) {
        DatabaseReference ref = database.getReference().child("locations/" + authentication.getUid() + "/discoveryRadiusMeters");
        switch (distanceType) {
            case "Km.":
                ref.setValue(value * 1000);
                break;
            case "Mi.":
                ref.setValue(value * 1609.34);
                break;
            case "Ft.":
                ref.setValue(value * 0.3048);
            default:
                ref.setValue(value);
                break;
        }
    }

    /**
     * Get sensor value and update related field and change theme if outside range
     * @param sensorEvent
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        binding.tvSensorSensitivity.setText(sensorEvent.values[0] + "");
    }

    public void changeTheme() {

    }

    /**
     * Necessary empty implementation
     * @param sensor
     * @param i
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /**
     * Attach light sensor onChange listener
     */
    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, lightSensor);
    }

    /**
     * Show toast with error message
     * @param message
     */
    private void showErrorToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
}