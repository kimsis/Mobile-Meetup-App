package com.example.hanger.ui.settings;

import android.content.Context;
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

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hanger.R;
import com.example.hanger.databinding.FragmentSettingsBinding;
import com.example.hanger.ui.helpers.ImageHelper;
import com.google.android.material.slider.Slider;

import java.io.IOException;

public class SettingsFragment extends Fragment implements SensorEventListener {

    private SettingsViewModel settingsViewModel;
    private FragmentSettingsBinding binding;
    // constant to compare
    // the activity result code
    int SELECT_IMAGE = 200;
    private static final String TAG = "Settings Fragment";
    ImageView userProfileImage;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        imageHelper = new ImageHelper(context);
        settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesHelper = new SharedPreferencesHelper(preferences);
        sharedPreferenceEntry = sharedPreferencesHelper.getPersonalInfo();

        String type = sharedPreferenceEntry.getDistanceType();
        distanceType = type == null || type.isEmpty() || type.equals("") ? "Km." : sharedPreferenceEntry.getDistanceType();
        distance = sharedPreferenceEntry.getDistanceAmount();
        sensorSensitivity = sharedPreferenceEntry.getThemeThreshold();
        binding.distanceTypeTv.setText(distanceType);
        binding.tvDistance.setText(distance + distanceType);
        binding.tvSensorSensitivity.setText(sensorSensitivity + "");
        switch (distanceType) {
            case "M.":
                binding.activityMainTogglebutton.check(R.id.btn_m);
                break;
            case "Km.":
                binding.activityMainTogglebutton.check(R.id.btn_km);
                break;
            case "Ft.":
                binding.activityMainTogglebutton.check(R.id.btn_ft);
                break;
            case "Mi.":
                binding.activityMainTogglebutton.check(R.id.btn_mi);
                break;
        }

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.sliderSensorSensitivity.setValue(sharedPreferenceEntry.getThemeThreshold());
        binding.sliderSensorSensitivity.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                sharedPreferencesHelper.saveThemeThreshold(value);
            }
        });
        userProfileImage = binding.userProfileImage;
        imageHelper.getImage(userProfileImage);
        binding.sliderDistance.setValue(sharedPreferenceEntry.getDistanceAmount());
        binding.sliderDistance.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                sharedPreferencesHelper.saveDistanceAmount(value);
            }
        });

        binding.btnM.setOnClickListener(x ->
                sharedPreferencesHelper.saveDistanceType("M."));
        binding.btnKm.setOnClickListener(x ->
                sharedPreferencesHelper.saveDistanceType("Km."));
        binding.btnFt.setOnClickListener(x ->
                sharedPreferencesHelper.saveDistanceType("Ft."));
        binding.btnMi.setOnClickListener(x ->
                sharedPreferencesHelper.saveDistanceType("Mi."));

        // Saving it from garbage collector
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(
                    SharedPreferences prefs, String key) {

                switch (key) {
                    case SharedPreferencesHelper.KEY_DISTANCE_TYPE:
                        distanceType = prefs.getString(SharedPreferencesHelper.KEY_DISTANCE_TYPE, "");
                        binding.distanceTypeTv.setText(distanceType);
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

        ActivityResultLauncher<String> mGetContent = this.registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                            userProfileImage.setImageBitmap(bitmap);
                            imageHelper.uploadImage(uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d(TAG, "onSensorChanged: Sensor value:" + sensorEvent.values[0] + " Sensor sensitivity: " + sensorSensitivity);
        if(sensorEvent.values[0] < sensorSensitivity) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}