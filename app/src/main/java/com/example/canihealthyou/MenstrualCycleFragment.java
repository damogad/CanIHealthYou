package com.example.canihealthyou;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;

import static android.content.Context.SENSOR_SERVICE;


public class MenstrualCycleFragment extends Fragment implements SensorEventListener {

    /*
    Sensor accelerometer;
    Sensor magnetometer;
    Sensor vectorSensor;
    DeviceOrientation deviceOrientation;
    SensorManager mSensorManager;
    */

    private SensorManager sensorManager;
    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];

    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];
    private final float[] orientationAnglesOld = new float[3];


    TextView data;
    Handler h;
    Runnable r;

    public MenstrualCycleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        deviceOrientation = new DeviceOrientation();
        */
        // Inflate the layout for this fragment
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        getActivity().setTitle(getActivity().getResources().getString(R.string.menstrual_cycle));
        View v = inflater.inflate(R.layout.fragment_menstrual_cycle, container, false);
        data = v.findViewById(R.id.orientation_textview);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
        mSensorManager.registerListener(deviceOrientation.getEventListener(), accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(deviceOrientation.getEventListener(), magnetometer, SensorManager.SENSOR_DELAY_UI);
        */
        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.
        //
        // In this example, the sensor reporting delay is small enough such that
        // the application receives an update before the system checks the sensor
        // readings again.
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            sensorManager.registerListener(this, magneticField,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }

        h = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                updateOrientationAngles();
                /*data.setText("Accelerometer: " + Arrays.toString(accelerometerReading) + "\n"
                            + "Magnetometer: " + Arrays.toString(magnetometerReading) + "\n"
                            + "Rotation matrix: " + Arrays.toString(rotationMatrix) + "\n"
                            + "Orientation angles: " + Arrays.toString(orientationAngles));*/
                data.setText("Orientation angles: " + "\n"
                            + orientationAngles[0] + "\n"
                            + orientationAngles[1] + "\n"
                            + orientationAngles[2] + "\n");
                /*data.setText("Rotation matrix: " + "\n");
                for (int i = 0; i < rotationMatrix.length; i++) {
                    data.setText(data.getText().toString() + rotationMatrix[i] + "\n");
                }
                data.setText(data.getText().toString() + "\n" + Math.atan((-1)*rotationMatrix[6]/Math.sqrt(Math.pow(rotationMatrix[7], 2)+Math.pow(rotationMatrix[8],2))) + "\n");
                data.setText(data.getText().toString() + Math.atan(rotationMatrix[7]/rotationMatrix[8]));*/
                float maxDiff = Float.NEGATIVE_INFINITY;
                for (int i = 0; i < 3; i++) {
                    float diff = orientationAngles[i] - orientationAnglesOld[i];
                    if (maxDiff < diff)
                        maxDiff = diff;
                }
                data.setText(data.getText().toString() + "\nMax. difference: " + maxDiff);
                h.postDelayed(r, 500);
            }
        };
        h.postDelayed(r, 500);
    }

    @Override
    public void onPause() {
        if (h != null && r != null)
            h.removeCallbacks(r);
        h = null;
        r = null;
        sensorManager.unregisterListener(this);
        //mSensorManager.unregisterListener(deviceOrientation.getEventListener());
        super.onPause();
    }

    // Get readings from accelerometer and magnetometer. To simplify calculations,
    // consider storing these readings as unit vectors.
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading,
                    0, accelerometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);
        }
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    public void updateOrientationAngles() {
        orientationAnglesOld[0] = orientationAngles[0];
        orientationAnglesOld[1] = orientationAngles[1];
        orientationAnglesOld[2] = orientationAngles[2];
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);

        // "mRotationMatrix" now has up-to-date information.

        SensorManager.getOrientation(rotationMatrix, orientationAngles);

        // "mOrientationAngles" now has up-to-date information.
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }
}
