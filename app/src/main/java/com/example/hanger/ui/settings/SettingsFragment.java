package com.example.hanger.ui.settings;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hanger.R;
import com.example.hanger.databinding.FragmentSettingsBinding;
import com.example.hanger.ui.secondarySensor.SecondarySensorViewModel;
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
    private int sensorSensitivity = 1;
    private SharedPreferences preferences;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private SharedPreferenceEntry sharedPreferenceEntry;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private double distance;
    private String distanceType;
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        context = getActivity();

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesHelper = new SharedPreferencesHelper(preferences);
        sharedPreferenceEntry = sharedPreferencesHelper.getPersonalInfo();

        String type = sharedPreferenceEntry.getDistanceType();
        distanceType = type == null || type.isEmpty() || type.equals("") ? "Km." : sharedPreferenceEntry.getDistanceType();
        binding.distanceTypeTv.setText(distanceType);
        distance = sharedPreferenceEntry.getDistanceAmount();
        binding.distanceTV.setText(distance + distanceType);
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
        binding.buttonSetThreshehold.setOnClickListener(x ->
                sharedPreferencesHelper.saveThemeThreshold(Float.parseFloat(binding.numberSensorSensitivity.getText().toString())));

        binding.distanceSlider.setValue(sharedPreferenceEntry.getDistanceAmount());
        binding.distanceSlider.addOnChangeListener(new Slider.OnChangeListener() {
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
                        binding.distanceTV.setText(distance + distanceType);
                        break;
                    case SharedPreferencesHelper.KEY_DISTANCE_AMOUNT:
                        distance = prefs.getFloat(SharedPreferencesHelper.KEY_DISTANCE_AMOUNT, 0.0f);
                        binding.distanceTV.setText(distance + distanceType);
                        break;
                    case SharedPreferencesHelper.KEY_THEME_THRESHOLD:
                        sensorSensitivity = Integer.parseInt(binding.numberSensorSensitivity.getText().toString());
                        break;
                    default:
                        break;
                }
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(listener);

        userProfileImage = binding.userProfileImage;
        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_IMAGE);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
                        userProfileImage.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == RESULT_CANCELED)  {
                Toast.makeText(context, "Canceled", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onActivityResult: Toast shown");
            }
        }
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        binding.textThemeState.setText(String.valueOf(sensorEvent.values[0]));
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