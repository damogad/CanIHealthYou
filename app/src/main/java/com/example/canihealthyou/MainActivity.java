package com.example.canihealthyou;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageView;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private SpeechRecognizer mySpeechRecognizer;

    private CardView nutritionCard;
    private CardView menstrualCycleCard;
    private CardView medicationCard;
    private CardView memoryCard;
    private CardView pedometerCard;
    private CardView heartRateCard;
    private ImageView settingsImage;

    SharedPreferences prefs;
    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        String language = prefs.getString(ContainerActivity.LANGUAGE_KEY, Locale.getDefault().getLanguage());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(language));
        res.updateConfiguration(conf, dm);
        //Locale.setDefault(new Locale(language));

        setContentView(R.layout.activity_main);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        settingsImage = findViewById(R.id.settings);
        settingsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                    try {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e) {

                    }
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                Intent containerActivity = new Intent(MainActivity.this, ContainerActivity.class);
                containerActivity.putExtra("frgToLoad", "Settings");
                startActivity(containerActivity);
            }
        });
        nutritionCard = (CardView) findViewById(R.id.nutrition_card);
        nutritionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                    try {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e) {

                    }
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                Intent containerActivity = new Intent(MainActivity.this, ContainerActivity.class);
                containerActivity.putExtra("frgToLoad", "Nutrition");
                startActivity(containerActivity);
            }
        });

        menstrualCycleCard = (CardView) findViewById(R.id.menstrual_cycle_card);
        menstrualCycleCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                    try {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e) {

                    }
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                Intent containerActivity = new Intent(MainActivity.this, ContainerActivity.class);
                containerActivity.putExtra("frgToLoad", "Menstrual Cycle");
                startActivity(containerActivity);
            }
        });

        medicationCard = (CardView) findViewById(R.id.medication_card);
        medicationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                    try {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e) {

                    }
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                Intent containerActivity = new Intent(MainActivity.this, ContainerActivity.class);
                containerActivity.putExtra("frgToLoad", "Medication");
                startActivity(containerActivity);
            }
        });

        memoryCard = (CardView) findViewById(R.id.memory_card);
        memoryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                    try {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e) {

                    }
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                Intent containerActivity = new Intent(MainActivity.this, ContainerActivity.class);
                containerActivity.putExtra("frgToLoad", "Memory");
                startActivity(containerActivity);
            }
        });

        pedometerCard = (CardView) findViewById(R.id.pedometer_card);
        pedometerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                    try {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e) {

                    }
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                Intent containerActivity = new Intent(MainActivity.this, ContainerActivity.class);
                containerActivity.putExtra("frgToLoad", "Pedometer");
                startActivity(containerActivity);
            }
        });

        heartRateCard = (CardView) findViewById(R.id.heart_rate_card);
        heartRateCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                    try {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e) {

                    }
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                Intent containerActivity = new Intent(MainActivity.this, ContainerActivity.class);
                containerActivity.putExtra("frgToLoad", "Heart Rate");
                startActivity(containerActivity);
            }
        });

        initializeSpeechRecognizer();

        if (prefs.getBoolean(ContainerActivity.AUTOMATIC_SPEECH_RECOGNITION_KEY, false)) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, prefs.getString(ContainerActivity.LANGUAGE_KEY, Locale.getDefault().getLanguage()));
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            mySpeechRecognizer.startListening(intent);
        }

        ImageView microphone = findViewById(R.id.speech_recognition_main);
        microphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, prefs.getString(ContainerActivity.LANGUAGE_KEY, Locale.getDefault().getLanguage()));
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                mySpeechRecognizer.startListening(intent);
            }
        });
    }

    private void initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            mySpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            mySpeechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle bundle) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float v) {

                }

                @Override
                public void onBufferReceived(byte[] bytes) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int i) {

                }

                @Override
                public void onResults(Bundle bundle) {
                    List<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    processResult(results.get(0));
                }

                @Override
                public void onPartialResults(Bundle bundle) {

                }

                @Override
                public void onEvent(int i, Bundle bundle) {

                }
            });
        }
    }

    boolean satisfactoryRecognition = false;

    private void processResult(String command) {
        command = command.toLowerCase();
        //Log.e("speech", command);
        satisfactoryRecognition = false;
        Log.e("speech", command);
        if (command.contains(getResources().getString(R.string.nutrition).toLowerCase())) {
            satisfactoryRecognition = true;
            nutritionCard.performClick();
        }
        else if (command.contains(getResources().getString(R.string.menstrual_cycle).toLowerCase())) {
            satisfactoryRecognition = true;
            menstrualCycleCard.performClick();
        }
        else if (command.contains(getResources().getString(R.string.medication).toLowerCase())) {
            satisfactoryRecognition = true;
            medicationCard.performClick();
        }
        else if (command.contains(getResources().getString(R.string.memory).toLowerCase())) {
            satisfactoryRecognition = true;
            memoryCard.performClick();
        }
        else if (command.contains(getResources().getString(R.string.pedometer).toLowerCase())) {
            satisfactoryRecognition = true;
            pedometerCard.performClick();
        }
        else if (command.contains(getResources().getString(R.string.heart_rate).toLowerCase())) {
            satisfactoryRecognition = true;
            heartRateCard.performClick();
        }
        else if (command.contains(getResources().getString(R.string.settings).toLowerCase())) {
            satisfactoryRecognition = true;
            settingsImage.performClick();
        }

        if (!satisfactoryRecognition) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, prefs.getString(ContainerActivity.LANGUAGE_KEY, Locale.getDefault().getLanguage()));
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            mySpeechRecognizer.startListening(intent);
        }
    }

    @Override
    protected void onStop() {
        mySpeechRecognizer.stopListening();
        super.onStop();
    }

}
