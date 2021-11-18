package com.example.hanger.ui.secondarySensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hanger.R;
import com.example.hanger.databinding.FragmentSecondarySensorBinding;

public class SecondarySensorFragment extends Fragment implements SensorEventListener {

    private FragmentSecondarySensorBinding binding;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private TextView sensorOutput;
    private int sensorSensitivity = 1;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        SecondarySensorViewModel notificationsViewModel = new ViewModelProvider(this).get(SecondarySensorViewModel.class);
        binding = FragmentSecondarySensorBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sensorOutput = (TextView)view.findViewById(R.id.text_theme_state);

        EditText sensitivityInput = (EditText)view.findViewById(R.id.number_sensor_sensitivity);

        view.findViewById(R.id.button_set_threshehold).setOnClickListener(x ->
                sensorSensitivity = Integer.parseInt(sensitivityInput.getText().toString()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        sensorOutput.setText(String.valueOf(sensorEvent.values[0]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}