package com.example.canihealthyou;


import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.muddzdev.styleabletoast.StyleableToast;

import java.text.SimpleDateFormat;
import java.util.Date;

import mehdi.sakout.fancybuttons.FancyButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class PedometerFragment extends Fragment implements SensorEventListener {

    TextView numberSteps;
    TextView lastReboot;
    CircularProgressBar circularProgressBar;
    SensorManager sensorManager;
    boolean running = false;

    SharedPreferences prefs;
    AudioManager audioManager;
    private int height;
    private int steps;

    public PedometerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle(getActivity().getResources().getString(R.string.pedometer));
        View view = inflater.inflate(R.layout.fragment_pedometer, container, false);

        prefs = getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        height = prefs.getInt("height", 175);

        numberSteps = (TextView) view.findViewById(R.id.number_steps);
        circularProgressBar = view.findViewById(R.id.circularProgressBar);
        lastReboot = view.findViewById(R.id.textview_lastreboot);

        TextInputLayout inputLayout = view.findViewById(R.id.edittextlayout_height);
        inputLayout.setVisibility(View.GONE);
        TextInputEditText inputEditText = view.findViewById(R.id.edittext_height);
        inputEditText.setText(String.valueOf(height));
        inputEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (!inputEditText.getText().toString().isEmpty() && !inputEditText.getText().toString().contains(",") && !inputEditText.getText().toString().contains(".")) {
                    inputLayout.setError(null); //Clear the error
                }
                return false;
            }
        });
        FancyButton saveButton = view.findViewById(R.id.btn_save);
        saveButton.setVisibility(View.GONE);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                if (inputEditText.getText().toString().contains(",") || inputEditText.getText().toString().contains(".")) {
                    inputLayout.setError(PedometerFragment.this.getActivity().getResources().getString(R.string.Pedometer_IntegerError));
                }
                else if (inputEditText.getText().toString().isEmpty()){
                    inputLayout.setError(PedometerFragment.this.getActivity().getResources().getString(R.string.Pedometer_EmptyError));
                }
                else {
                    String heightString = inputEditText.getText().toString().trim();
                    int height = Integer.parseInt(heightString);
                    prefs.edit().putInt("height", height).commit();
                    PedometerFragment.this.height = height;
                    saveButton.setVisibility(View.GONE);
                    inputLayout.setVisibility(View.GONE);
                    updateText();
                    StyleableToast.makeText(getActivity(), getActivity().getResources().getString(R.string.Pedometer_HeightChanged) + " " + String.valueOf(height), R.style.success_black).show();
                }
            }
        });

        FancyButton heightButton = view.findViewById(R.id.btn_height);
        heightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                    }
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                inputLayout.setVisibility(View.VISIBLE);
                saveButton.setVisibility(View.VISIBLE);
            }
        });

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        running = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
            Log.e("sensor_registrado", "true");
        }
        else {
            //Toast.makeText(getActivity(), "Step sensor not found", Toast.LENGTH_LONG).show();
            ErrorDialog errorDialog = new ErrorDialog(getActivity().getResources().getString(R.string.Pedometer_ErrorDialog));
            errorDialog.show(getFragmentManager(), "Error dialog");
            //getActivity().finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //running = false;
        //sensorManager.unregisterListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (running) {
            Log.e("elementos sensor", String.valueOf(sensorEvent.values.length));
            steps = (int)sensorEvent.values[0];
            numberSteps.setText(String.valueOf(steps));
            circularProgressBar.setProgress(steps%1000);
            updateText();
        }

    }

    private void updateText() {
        Date now = new Date();
        long elapsed = SystemClock.uptimeMillis();
        Date reboot = new Date(now.getTime() - elapsed);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        lastReboot.setText(getActivity().getResources().getString(R.string.Pedometer_StepsLastReboot) + " (" + format.format(reboot) + "). ");
        lastReboot.setText(lastReboot.getText().toString() + getActivity().getString(R.string.Pedometer_StepsLastReboot2) + " (" + height + " cm) " + getActivity().getResources().getString(R.string.Pedometer_StepsLastReboot3) + " ");
        lastReboot.setText(lastReboot.getText().toString() + String.format("%.3f", height*0.43*steps/100000f) + " km. ");
        lastReboot.setText(lastReboot.getText().toString() + getActivity().getResources().getString(R.string.Pedometer_StepsLastReboot4) + " " + String.format("%.3f", (height*0.43*steps/100000f)/elapsed/86400000f) + " km " + getActivity().getResources().getString(R.string.Pedometer_StepsLastReboot5));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
