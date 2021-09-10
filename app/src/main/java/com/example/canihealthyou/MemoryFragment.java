package com.example.canihealthyou;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.muddzdev.styleabletoast.StyleableToast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import mehdi.sakout.fancybuttons.FancyButton;


public class MemoryFragment extends Fragment implements SensorEventListener {

    private SharedPreferences prefs;
    private String language;
    Vibrator vibrator;

    private String UPSTRING;
    private String DOWNSTRING;
    private String LEFTSTRING;
    private String RIGHTSTRING;

    CardView upCard;
    CardView downCard;
    CardView leftCard;
    CardView rightCard;

    ArrayList<String> movementsToGuess = new ArrayList<>();
    ArrayList<String> userMovements = new ArrayList<>();

    Random random = new Random();

    boolean currentlyGuessing = false;
    private boolean correct = true;

    final ReentrantLock reentrantLock = new ReentrantLock(true);
    private final Condition condition = reentrantLock.newCondition();

    private TextToSpeech mTTS;
    private float pitch;
    private float speed;

    Handler handler;
    Runnable runnable;
    Thread timeThread;
    Thread dictateThread;
    private boolean initiated = false;

    private boolean vibrate;
    FancyButton startButton;

    private TextView mTextViewCountDown;
    private Button mButtonStartPause;
    private Button mButtonReset;

    private CountDownTimer mCountDownTimer;

    private boolean mTimerRunning;
    private boolean startedTimer = false;
    private long mTimeLeftInMillis = 4000;

    private SensorManager sensorManager;
    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];

    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];
    private final float[] orientationAnglesOld = new float[3];

    private static final float THRESHOLD = 0.2f;

    Handler gyroscopeHandler;
    Runnable gyroscopeRunnable;
    private float[] linear_acceleration = new float[3];
    private float[] gravity = {0, 0, 0};
    private float[] old_linear_acceleration = new float[3];

    MediaPlayer mpError;
    MediaPlayer mpSuccess;

    public MemoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        prefs = getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        language = prefs.getString(ContainerActivity.LANGUAGE_KEY, Locale.getDefault().getLanguage());
        vibrate = prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        mpError = MediaPlayer.create(getActivity(), R.raw.smb_gameover);
        mpSuccess = MediaPlayer.create(getActivity(), R.raw.smw_coin);

        pitch = (float) prefs.getInt("pitch", 25) / 50;
        if (pitch < 0.1)
            pitch = 0.1f;
        speed = (float) prefs.getInt("speed", 25) / 50;
        if (speed < 0.1)
            speed = 0.1f;

        UPSTRING = getActivity().getResources().getString(R.string.Memory_Up);
        DOWNSTRING = getActivity().getResources().getString(R.string.Memory_Down);
        LEFTSTRING = getActivity().getResources().getString(R.string.Memory_Left);
        RIGHTSTRING = getActivity().getResources().getString(R.string.Memory_Right);

        // Inflate the layout for this fragment
        getActivity().setTitle(getActivity().getResources().getString(R.string.memory));
        View v = inflater.inflate(R.layout.fragment_memory, container, false);

        mTextViewCountDown = v.findViewById(R.id.text_view_countdown);

        upCard = v.findViewById(R.id.up_arrow);
        upCard.setMaxCardElevation(16.0f);
        upCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reentrantLock.lock();
                try {
                    if (vibrate) {
                        upCard.setHapticFeedbackEnabled(true);
                        upCard.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                    }
                    upCard.setCardBackgroundColor(getResources().getColor(android.R.color.black));
                    downCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                    leftCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                    rightCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                    if (currentlyGuessing) {
                        userMovements.add(UPSTRING);
                        Log.e("movementsToGuess", movementsToGuess.toString());
                        Log.e("userMovements", userMovements.toString());
                        if (!movementsToGuess.get(userMovements.size()-1).equals(UPSTRING)) {
                            StyleableToast.makeText(MemoryFragment.this.getActivity(), "ERROR!", R.style.error_short).show();
                            currentlyGuessing = false;
                            correct = false;
                            StyleableToast.makeText(MemoryFragment.this.getActivity(), "Final score: " + String.valueOf(MemoryFragment.this.movementsToGuess.size()-1), R.style.info).show();
                            userMovements.clear();
                            pauseTimer();
                            MemoryFragment.this.mTextViewCountDown.setText("00:00");
                            mpError.start();
                            //upCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                            downCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                            leftCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                            rightCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                            startButton.setVisibility(View.VISIBLE);
                            if (timeThread != null)
                                timeThread.interrupt();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                //deprecated in API 26
                                vibrator.vibrate(200);
                            }
                            condition.signal();
                        }
                        else {
                            if (movementsToGuess.size() == userMovements.size()) {
                                StyleableToast.makeText(MemoryFragment.this.getActivity(), "CORRECT!", R.style.success_short).show();
                                currentlyGuessing = false;
                                correct = true;
                                userMovements.clear();
                                pauseTimer();
                                MemoryFragment.this.mTextViewCountDown.setText("00:00");
                                mpSuccess.start();
                                //upCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                                downCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                                leftCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                                rightCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                                if (timeThread != null)
                                    timeThread.interrupt();
                                condition.signal();
                            }
                            else {
                                currentlyGuessing = true;
                                correct = false;
                            }
                        }
                    }
                    //upCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                } finally {
                    reentrantLock.unlock();
                }
            }
        });

        downCard = v.findViewById(R.id.down_arrow);
        downCard.setMaxCardElevation(16.0f);
        downCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reentrantLock.lock();
                try {
                    if (vibrate) {
                        downCard.setHapticFeedbackEnabled(true);
                        downCard.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                    }
                    downCard.setCardBackgroundColor(getResources().getColor(android.R.color.black));
                    upCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                    leftCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                    rightCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                    if (currentlyGuessing) {
                        userMovements.add(DOWNSTRING);
                        Log.e("movementsToGuess", movementsToGuess.toString());
                        Log.e("userMovements", userMovements.toString());
                        if (!movementsToGuess.get(userMovements.size()-1).equals(DOWNSTRING)) {
                            StyleableToast.makeText(MemoryFragment.this.getActivity(), "ERROR!", R.style.error_short).show();
                            currentlyGuessing = false;
                            correct = false;
                            StyleableToast.makeText(MemoryFragment.this.getActivity(), "Final score: " + String.valueOf(MemoryFragment.this.movementsToGuess.size()-1), R.style.info).show();
                            userMovements.clear();
                            pauseTimer();
                            MemoryFragment.this.mTextViewCountDown.setText("00:00");
                            mpError.start();
                            upCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                            //downCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                            leftCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                            rightCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                            startButton.setVisibility(View.VISIBLE);
                            if (timeThread != null)
                                timeThread.interrupt();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                //deprecated in API 26
                                vibrator.vibrate(200);
                            }
                            condition.signal();
                        }
                        else {
                            if (movementsToGuess.size() == userMovements.size()) {
                                StyleableToast.makeText(MemoryFragment.this.getActivity(), "CORRECT!", R.style.success_short).show();
                                currentlyGuessing = false;
                                correct = true;
                                userMovements.clear();
                                pauseTimer();
                                MemoryFragment.this.mTextViewCountDown.setText("00:00");
                                mpSuccess.start();
                                upCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                                //downCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                                leftCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                                rightCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                                if (timeThread != null)
                                    timeThread.interrupt();
                                condition.signal();
                            }
                            else {
                                currentlyGuessing = true;
                                correct = false;
                            }
                        }
                    }
                    //downCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                } finally {
                    reentrantLock.unlock();
                }
            }
        });

        leftCard = v.findViewById(R.id.left_arrow);
        leftCard.setMaxCardElevation(16.0f);
        leftCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reentrantLock.lock();
                try {
                    if (vibrate) {
                        leftCard.setHapticFeedbackEnabled(true);
                        leftCard.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                    }
                    leftCard.setCardBackgroundColor(getResources().getColor(android.R.color.black));
                    downCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                    upCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                    rightCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                    if (currentlyGuessing) {
                        userMovements.add(LEFTSTRING);
                        Log.e("movementsToGuess", movementsToGuess.toString());
                        Log.e("userMovements", userMovements.toString());
                        if (!movementsToGuess.get(userMovements.size()-1).equals(LEFTSTRING)) {
                            StyleableToast.makeText(MemoryFragment.this.getActivity(), "ERROR!", R.style.error_short).show();
                            currentlyGuessing = false;
                            correct = false;
                            StyleableToast.makeText(MemoryFragment.this.getActivity(), "Final score: " + String.valueOf(MemoryFragment.this.movementsToGuess.size()-1), R.style.info).show();
                            userMovements.clear();
                            pauseTimer();
                            MemoryFragment.this.mTextViewCountDown.setText("00:00");
                            mpError.start();
                            upCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                            downCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                            //leftCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                            rightCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                            startButton.setVisibility(View.VISIBLE);
                            if (timeThread != null)
                                timeThread.interrupt();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                //deprecated in API 26
                                vibrator.vibrate(200);
                            }
                            condition.signal();
                        }
                        else {
                            if (movementsToGuess.size() == userMovements.size()) {
                                StyleableToast.makeText(MemoryFragment.this.getActivity(), "CORRECT!", R.style.success_short).show();
                                currentlyGuessing = false;
                                correct = true;
                                userMovements.clear();
                                pauseTimer();
                                MemoryFragment.this.mTextViewCountDown.setText("00:00");
                                mpSuccess.start();
                                upCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                                downCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                                //leftCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                                rightCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                                if (timeThread != null)
                                    timeThread.interrupt();
                                condition.signal();
                            }
                            else {
                                currentlyGuessing = true;
                                correct = false;
                            }
                        }
                    }
                    //leftCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                } finally {
                    reentrantLock.unlock();
                }
            }
        });

        rightCard = v.findViewById(R.id.right_arrow);
        rightCard.setMaxCardElevation(16.0f);
        rightCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reentrantLock.lock();
                try {
                    if (vibrate) {
                        rightCard.setHapticFeedbackEnabled(true);
                        rightCard.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                    }
                    rightCard.setCardBackgroundColor(getResources().getColor(android.R.color.black));
                    downCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                    leftCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                    upCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                    if (currentlyGuessing) {
                        userMovements.add(RIGHTSTRING);
                        Log.e("movementsToGuess", movementsToGuess.toString());
                        Log.e("userMovements", userMovements.toString());
                        if (!movementsToGuess.get(userMovements.size()-1).equals(RIGHTSTRING)) {
                            StyleableToast.makeText(MemoryFragment.this.getActivity(), "ERROR!", R.style.error_short).show();
                            currentlyGuessing = false;
                            correct = false;
                            StyleableToast.makeText(MemoryFragment.this.getActivity(), "Final score: " + String.valueOf(MemoryFragment.this.movementsToGuess.size()-1), R.style.info).show();
                            userMovements.clear();
                            pauseTimer();
                            MemoryFragment.this.mTextViewCountDown.setText("00:00");
                            mpError.start();
                            upCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                            downCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                            leftCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                            //rightCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                            startButton.setVisibility(View.VISIBLE);
                            if (timeThread != null)
                                timeThread.interrupt();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                //deprecated in API 26
                                vibrator.vibrate(200);
                            }
                            condition.signal();
                        }
                        else {
                            if (movementsToGuess.size() == userMovements.size()) {
                                StyleableToast.makeText(MemoryFragment.this.getActivity(), "CORRECT!", R.style.success_short).show();
                                currentlyGuessing = false;
                                correct = true;
                                userMovements.clear();
                                pauseTimer();
                                MemoryFragment.this.mTextViewCountDown.setText("00:00");
                                mpSuccess.start();
                                upCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                                downCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                                leftCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                                //rightCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                                if (timeThread != null)
                                    timeThread.interrupt();
                                condition.signal();
                            }
                            else {
                                currentlyGuessing = true;
                                correct = false;
                            }
                        }
                    }
                    //rightCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                } finally {
                    reentrantLock.unlock();
                }
            }
        });

        mTTS = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(new Locale(language));

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        StyleableToast.makeText(getActivity(), "Language not supported", R.style.error_short).show();
                    }
                    mTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String s) {

                        }

                        @Override
                        public void onDone(String s) {
                            reentrantLock.lock();
                            try {
                                if (reentrantLock.hasWaiters(condition)) {
                                    condition.signal();
                                }

                            } finally {
                                reentrantLock.unlock();
                            }
                        }

                        @Override
                        public void onError(String s) {

                        }
                    });
                }
                else {
                    StyleableToast.makeText(getActivity(), "TTS initialization failed", R.style.error_short).show();
                }
            }
        });



        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);

        startButton = v.findViewById(R.id.memory_start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!initiated) {
                    startButton.setVisibility(View.GONE);
                    initiated = true;
                    upCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                    downCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                    leftCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                    rightCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                    (dictateThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            generateRandomSequence();
                        }
                    })).start();
                }
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

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

        gyroscopeHandler = new Handler();
        gyroscopeRunnable = new Runnable() {
            @Override
            public void run() {
                updateOrientationAngles();
                float diffPitch = orientationAngles[1] - orientationAnglesOld[1];
                float diffRoll = orientationAngles[2] - orientationAnglesOld[2];
                reentrantLock.lock();
                try {
                    if (currentlyGuessing) {
                        Log.e("pitch", String.valueOf(orientationAngles[1]));
                        Log.e("roll", String.valueOf(orientationAngles[2]));
                        if (orientationAngles[2] > 0.7f && diffRoll > THRESHOLD) {
                            rightCard.performClick();
                        }
                        else if (orientationAngles[2] < -0.7f && diffRoll < (-1) * THRESHOLD) {
                            leftCard.performClick();
                        }
                        else if (orientationAngles[1] > 0.1f && diffPitch > THRESHOLD) {
                            upCard.performClick();
                        }
                        else if (orientationAngles[1] < -0.8f && diffPitch < (-1) * THRESHOLD) {
                            downCard.performClick();
                        }
                    }
                    /*//RIGHT
                    if ((linear_acceleration[0] - old_linear_acceleration[0]) > THRESHOLD) {
                        rightCard.performClick();
                    }
                    //LEFT
                    else if ((linear_acceleration[0] - old_linear_acceleration[0]) > (-1) * THRESHOLD) {
                        leftCard.performClick();
                    }
                    //UP
                    else if ((linear_acceleration[1] - old_linear_acceleration[1]) > THRESHOLD) {
                        upCard.performClick();
                    }
                    //DOWN
                    else if ((linear_acceleration[1] - old_linear_acceleration[1]) > (-1) * THRESHOLD) {
                        downCard.performClick();
                    }*/

                } finally {
                    reentrantLock.unlock();
                }
                gyroscopeHandler.postDelayed(gyroscopeRunnable, 600);
            }
        };
        gyroscopeHandler.postDelayed(gyroscopeRunnable, 100);
    }

    private void updateOrientationAngles() {
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
    public void onPause() {
        if (gyroscopeHandler != null && gyroscopeRunnable != null)
            gyroscopeHandler.removeCallbacks(gyroscopeRunnable);
        gyroscopeHandler = null;
        gyroscopeRunnable = null;
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onStop() {
        correct = false;
        if (dictateThread != null)
            dictateThread.interrupt();
        dictateThread = null;
        if (timeThread != null)
            timeThread.interrupt();
        timeThread = null;
        mTTS.stop();
        mTTS.shutdown();
        super.onStop();
    }

    private void generateRandomSequence() {
        Log.e("llego a entrar", "si");
        String[] movements = {UPSTRING, DOWNSTRING, LEFTSTRING, RIGHTSTRING};
        while(correct) {
            reentrantLock.lock();
            ArrayList<String> repeatMovementsToGuess = new ArrayList<>(this.movementsToGuess);
            try {
                CardView cardToElevate;
                for (String s : repeatMovementsToGuess) {
                    cardToElevate = null;
                    if (s.equals(UPSTRING)) {
                        cardToElevate = upCard;
                    } else if (s.equals(DOWNSTRING)) {
                        cardToElevate = downCard;
                    } else if (s.equals(LEFTSTRING)) {
                        cardToElevate = leftCard;
                    } else if (s.equals(RIGHTSTRING)) {
                        cardToElevate = rightCard;
                    }
                    cardToElevate.setCardElevation(16.0f);
                    cardToElevate.setCardBackgroundColor(getResources().getColor(android.R.color.black));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mTTS.speak(s, TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
                    }
                    if (vibrate) {
                        cardToElevate.setHapticFeedbackEnabled(true);
                        cardToElevate.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                    }
                    condition.await();
                    cardToElevate.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                    cardToElevate.setCardElevation(2.0f);
                }
                String newMovement = movements[random.nextInt(4)];
                this.movementsToGuess.add(newMovement);
                cardToElevate = null;
                if (newMovement.equals(UPSTRING)) {
                    cardToElevate = upCard;
                } else if (newMovement.equals(DOWNSTRING)) {
                    cardToElevate = downCard;
                } else if (newMovement.equals(LEFTSTRING)) {
                    cardToElevate = leftCard;
                } else if (newMovement.equals(RIGHTSTRING)) {
                    cardToElevate = rightCard;
                }
                cardToElevate.setCardElevation(16.0f);
                cardToElevate.setCardBackgroundColor(getResources().getColor(android.R.color.black));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mTTS.speak(newMovement, TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
                }
                if (vibrate) {
                    cardToElevate.setHapticFeedbackEnabled(true);
                    cardToElevate.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                condition.await();
                cardToElevate.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                cardToElevate.setCardElevation(2.0f);
                //handler = new Handler();
                (timeThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            reentrantLock.lock();
                            try {
                                MemoryFragment.this.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        resetTimer();
                                        startTimer();
                                        MemoryFragment.this.mTextViewCountDown.setText("00:00");
                                    }
                                });
                            } finally {
                                reentrantLock.unlock();
                            }
                            Thread.sleep(movementsToGuess.size() * 4000);
                            reentrantLock.lock();
                            try {
                                if (reentrantLock.hasWaiters(condition)) {
                                    Log.e("finish", "score: " + String.valueOf(movementsToGuess.size() - 1));
                                    MemoryFragment.this.getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mpError.start();
                                            StyleableToast.makeText(MemoryFragment.this.getActivity(), "Final score: " + String.valueOf(MemoryFragment.this.movementsToGuess.size() - 1), R.style.info).show();
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                MemoryFragment.this.vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                                            } else {
                                                //deprecated in API 26
                                                MemoryFragment.this.vibrator.vibrate(200);
                                            }
                                            MemoryFragment.this.startButton.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    condition.signal();
                                }
                            } finally {
                                reentrantLock.unlock();
                            }
                        } catch (InterruptedException e) {
                            Log.e("Exception", "InterruptedException in MemoryFragment (line 245)");
                        }
                    }
                })).start();

                correct = false;
                currentlyGuessing = true;
                condition.await();
                currentlyGuessing = false;
                /*if (correct) {
                    StyleableToast.makeText(getActivity(), "CORRECT!", R.style.success_short).show();
                }
                else {
                    StyleableToast.makeText(getActivity(), "ERROR!", R.style.error_short).show();
                }*/
                userMovements.clear();
                Thread.sleep(500);
                cardToElevate.setCardBackgroundColor(getResources().getColor(android.R.color.white));

            } catch (InterruptedException e) {
                Log.e("Exception", "InterruptedException in MemoryFragment (line 217)");
            } finally {
                initiated = false;
                reentrantLock.unlock();
            }

        }
        correct = true;
        movementsToGuess.clear();
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                //mButtonStartPause.setText("Start");
                //mButtonStartPause.setVisibility(View.INVISIBLE);
                //mButtonReset.setVisibility(View.VISIBLE);
            }
        }.start();

        mTimerRunning = true;
        //mButtonStartPause.setText("pause");
        //mButtonReset.setVisibility(View.INVISIBLE);
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        //mButtonStartPause.setText("Start");
        //mButtonReset.setVisibility(View.VISIBLE);
    }

    private void resetTimer() {
        mTimeLeftInMillis = this.movementsToGuess.size() * 3000;
        updateCountDownText();
        //mButtonReset.setVisibility(View.INVISIBLE);
        //mButtonStartPause.setVisibility(View.VISIBLE);
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        mTextViewCountDown.setText(timeLeftFormatted);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading,
                    0, accelerometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);
        }
        /*

        final float alpha = 0.8f;

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        old_linear_acceleration[0] = linear_acceleration[0];
        old_linear_acceleration[1] = linear_acceleration[1];
        old_linear_acceleration[2] = linear_acceleration[2];

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];


        //Arrays.toString(old_linear_acceleration);

        Log.e("linear_acceleration", Arrays.toString(linear_acceleration));

        //RIGHT
        if ((linear_acceleration[0] - old_linear_acceleration[0]) > THRESHOLD) {
            reentrantLock.lock();
            try {
                leftCard.performClick();
            } finally {
                reentrantLock.unlock();
            }
        }
        //LEFT
        else if ((linear_acceleration[0] - old_linear_acceleration[0]) > (-1) * THRESHOLD) {
            reentrantLock.lock();
            try {

                rightCard.performClick();
            } finally {
                reentrantLock.unlock();
            }
        }
        //UP
        else if ((linear_acceleration[1] - old_linear_acceleration[1]) > THRESHOLD) {
            reentrantLock.lock();
            try {
                upCard.performClick();
            } finally {
                reentrantLock.unlock();
            }
        }
        //DOWN
        else if ((linear_acceleration[1] - old_linear_acceleration[1]) > (-1) * THRESHOLD) {
            reentrantLock.lock();
            try {
                downCard.performClick();
            } finally {
                reentrantLock.unlock();
            }
        }*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
