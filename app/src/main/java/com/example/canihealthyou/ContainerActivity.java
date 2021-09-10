package com.example.canihealthyou;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.Activity;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ContainerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;
    private SpeechRecognizer mySpeechRecognizer;

    public static final String PREF_NAME = "settings";
    public static final String VIBRATION_KEY = "vibration";
    public static final String SOUND_KEY = "sound";
    public static final String AUTOMATIC_SPEECH_RECOGNITION_KEY = "automatic_speech_recognition";
    public static final String HIGHCONTRAST_KEY = "highcontrast";
    public static final String LANGUAGE_KEY = "language";
    public static final String FONT_SIZE_KEY = "fontsize";
    public static final String COLOR_THEME_KEY = "colortheme";

    SharedPreferences prefs;

    NutritionFragment nutritionFragment;
    MedicationFragment medicationFragment;
    MemoryFragment memoryFragment;

    List<String> typeOfMeal;

    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSharedPreferences(ContainerActivity.PREF_NAME, 0).edit().clear().commit();

        prefs = getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        String language = prefs.getString(ContainerActivity.LANGUAGE_KEY, Locale.getDefault().getLanguage());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(language));
        res.updateConfiguration(conf, dm);
        //Locale.setDefault(new Locale(language));
        String fontSize = prefs.getString(ContainerActivity.FONT_SIZE_KEY, "medium");
        String colorTheme = prefs.getString(ContainerActivity.COLOR_THEME_KEY, "default");
        boolean highContrast = prefs.getBoolean(ContainerActivity.HIGHCONTRAST_KEY, false);
        switch (fontSize) {
            case "small":
                switch (colorTheme) {
                    case "default":
                        if (highContrast)
                            setTheme(R.style.DefaultAppThemeSmallHighContrast_NoActionBar);
                        else
                            setTheme(R.style.DefaultAppThemeSmallDefaultContrast_NoActionBar);
                        break;
                    case "red":
                        if (highContrast)
                            setTheme(R.style.RedAppThemeSmallHighContrast_NoActionBar);
                        else
                            setTheme(R.style.RedAppThemeSmallDefaultContrast_NoActionBar);
                        break;
                    case "purple":
                        if (highContrast)
                            setTheme(R.style.PurpleAppThemeSmallHighContrast_NoActionBar);
                        else
                            setTheme(R.style.PurpleAppThemeSmallDefaultContrast_NoActionBar);
                        break;
                    case "green":
                        if (highContrast)
                            setTheme(R.style.GreenAppThemeSmallHighContrast_NoActionBar);
                        else
                            setTheme(R.style.GreenAppThemeSmallDefaultContrast_NoActionBar);
                        break;
                    case "blue":
                        if (highContrast)
                            setTheme(R.style.BlueAppThemeSmallHighContrast_NoActionBar);
                        else
                            setTheme(R.style.BlueAppThemeSmallDefaultContrast_NoActionBar);
                        break;
                    case "yellow":
                        if (highContrast)
                            setTheme(R.style.YellowAppThemeSmallHighContrast_NoActionBar);
                        else
                            setTheme(R.style.YellowAppThemeSmallDefaultContrast_NoActionBar);
                        break;
                }
                break;
            case "medium":
                switch (colorTheme) {
                    case "default":
                        if (highContrast)
                            setTheme(R.style.DefaultAppThemeMediumHighContrast_NoActionBar);
                        else
                            setTheme(R.style.DefaultAppThemeMediumDefaultContrast_NoActionBar);
                        break;
                    case "red":
                        if (highContrast)
                            setTheme(R.style.RedAppThemeMediumHighContrast_NoActionBar);
                        else
                            setTheme(R.style.RedAppThemeMediumDefaultContrast_NoActionBar);
                        break;
                    case "purple":
                        if (highContrast)
                            setTheme(R.style.PurpleAppThemeMediumHighContrast_NoActionBar);
                        else
                            setTheme(R.style.PurpleAppThemeMediumDefaultContrast_NoActionBar);
                        break;
                    case "green":
                        if (highContrast)
                            setTheme(R.style.GreenAppThemeMediumHighContrast_NoActionBar);
                        else
                            setTheme(R.style.GreenAppThemeMediumDefaultContrast_NoActionBar);
                        break;
                    case "blue":
                        if (highContrast)
                            setTheme(R.style.BlueAppThemeMediumHighContrast_NoActionBar);
                        else
                            setTheme(R.style.BlueAppThemeMediumDefaultContrast_NoActionBar);
                        break;
                    case "yellow":
                        if (highContrast)
                            setTheme(R.style.YellowAppThemeMediumHighContrast_NoActionBar);
                        else
                            setTheme(R.style.YellowAppThemeMediumDefaultContrast_NoActionBar);
                        break;
                }
                break;
            case "large":
                switch (colorTheme) {
                    case "default":
                        if (highContrast)
                            setTheme(R.style.DefaultAppThemeLargeHighContrast_NoActionBar);
                        else
                            setTheme(R.style.DefaultAppThemeLargeDefaultContrast_NoActionBar);
                        break;
                    case "red":
                        if (highContrast)
                            setTheme(R.style.RedAppThemeLargeHighContrast_NoActionBar);
                        else
                            setTheme(R.style.RedAppThemeLargeDefaultContrast_NoActionBar);
                        break;
                    case "purple":
                        if (highContrast)
                            setTheme(R.style.PurpleAppThemeLargeHighContrast_NoActionBar);
                        else
                            setTheme(R.style.PurpleAppThemeLargeDefaultContrast_NoActionBar);
                        break;
                    case "green":
                        if (highContrast)
                            setTheme(R.style.GreenAppThemeLargeHighContrast_NoActionBar);
                        else
                            setTheme(R.style.GreenAppThemeLargeDefaultContrast_NoActionBar);
                        break;
                    case "blue":
                        if (highContrast)
                            setTheme(R.style.BlueAppThemeLargeHighContrast_NoActionBar);
                        else
                            setTheme(R.style.BlueAppThemeLargeDefaultContrast_NoActionBar);
                        break;
                    case "yellow":
                        if (highContrast)
                            setTheme(R.style.YellowAppThemeLargeHighContrast_NoActionBar);
                        else
                            setTheme(R.style.YellowAppThemeLargeDefaultContrast_NoActionBar);
                        break;
                }
                break;
        }


        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        setContentView(R.layout.activity_container);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setSoundEffectsEnabled(false);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setSoundEffectsEnabled(false);
        navigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setHapticFeedbackEnabled(prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false));
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        View headerLayout = navigationView.getHeaderView(0); // 0-index header
        ImageView navImage = headerLayout.findViewById(R.id.home_nav);
        navImage.setSoundEffectsEnabled(false);
        navImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                Intent intent = new Intent(ContainerActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
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

        typeOfMeal = Arrays.asList(getResources().getStringArray(R.array.meal_type));

        /*
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
        */

        // In the case that the orientation changes, this method is called again
        // However, the android:configChanges attribute in the manifest can be changed
        if (savedInstanceState == null) {
            switch (getIntent().getExtras().getString("frgToLoad")) {
                case "Nutrition":
                    nutritionFragment = new NutritionFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", getIntent().getExtras().getInt("position", 0));
                    nutritionFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, nutritionFragment).commit();
                    navigationView.setCheckedItem(R.id.nav_nutrition);
                    break;
                case "Menstrual Cycle":
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MenstrualCycleFragment()).commit();
                    navigationView.setCheckedItem(R.id.nav_menstrual_cycle);
                    break;
                case "Medication":
                    medicationFragment = new MedicationFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, medicationFragment).commit();
                    navigationView.setCheckedItem(R.id.nav_medication);
                    break;
                case "Memory":
                    memoryFragment = new MemoryFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, memoryFragment).commit();
                    navigationView.setCheckedItem(R.id.nav_memory);
                    break;
                case "Pedometer":
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PedometerFragment()).commit();
                    navigationView.setCheckedItem(R.id.nav_pedometer);
                    break;
                case "Heart Rate":
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HeartRateStopFragment()).commit();
                    navigationView.setCheckedItem(R.id.nav_heart_rate);
                    break;
                case "Settings":
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                    navigationView.setCheckedItem(R.id.nav_settings);
                    break;
                case "About":
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();
                    navigationView.setCheckedItem(R.id.nav_about);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            /*
            Intent intent = new Intent(ContainerActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            */
            finish();
            //super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        SharedPreferences prefs = getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
            drawer.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
        }
        if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
            audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
        }
        switch (menuItem.getItemId()) {
            case R.id.nav_nutrition:
                nutritionFragment = new NutritionFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("position", getIntent().getExtras().getInt("position", 0));
                nutritionFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, nutritionFragment).commit();
                break;
            case R.id.nav_menstrual_cycle:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MenstrualCycleFragment()).commit();
                break;
            case R.id.nav_medication:
                medicationFragment = new MedicationFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, medicationFragment).commit();
                break;
            case R.id.nav_memory:
                memoryFragment = new MemoryFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, memoryFragment).commit();
                break;
            case R.id.nav_pedometer:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PedometerFragment()).commit();
                break;
            case R.id.nav_heart_rate:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HeartRateStopFragment()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                break;
            case R.id.nav_about:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tts_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.microphone_tts_activity:
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, prefs.getString(ContainerActivity.LANGUAGE_KEY, Locale.getDefault().getLanguage()));
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                mySpeechRecognizer.startListening(intent);
                break;

        }
        return true;
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

    EditText searchRecipe;
    Button addMeal;
    TextInputEditText mealDescription;
    Spinner mealType;
    boolean satisfactoryRecognition = false;

    private void processResult(String command) {
        command = command.toLowerCase();

        satisfactoryRecognition = false;
        //Log.e("speech", command);
        Log.e("speech", command);
        if (getTitle().equals(getResources().getString(R.string.memory))) {
            if (memoryFragment != null) {
                satisfactoryRecognition = true;
                String[] movements = command.split(" ");
                for (int i = 0; i < movements.length; i++) {
                    String movement = movements[i].toUpperCase();
                    if (movement.equals(getResources().getString(R.string.Memory_Up))) {
                        memoryFragment.upCard.performClick();
                        memoryFragment.reentrantLock.lock();
                        try {
                            if (!memoryFragment.currentlyGuessing)
                                break;
                        } finally {
                            memoryFragment.reentrantLock.unlock();
                        }
                    }
                    else if (movement.equals(getResources().getString(R.string.Memory_Down))) {
                        memoryFragment.downCard.performClick();
                        memoryFragment.reentrantLock.lock();
                        try {
                            if (!memoryFragment.currentlyGuessing)
                                break;
                        } finally {
                            memoryFragment.reentrantLock.unlock();
                        }
                    }
                    else if (movement.equals(getResources().getString(R.string.Memory_Left))) {
                        memoryFragment.leftCard.performClick();
                        memoryFragment.reentrantLock.lock();
                        try {
                            if (!memoryFragment.currentlyGuessing)
                                break;
                        } finally {
                            memoryFragment.reentrantLock.unlock();
                        }
                    }
                    else if (movement.equals(getResources().getString(R.string.Memory_Right))) {
                        memoryFragment.rightCard.performClick();
                        memoryFragment.reentrantLock.lock();
                        try {
                            if (!memoryFragment.currentlyGuessing)
                                break;
                        } finally {
                            memoryFragment.reentrantLock.unlock();
                        }
                    }
                }
            }
        }
        else if (command.contains(getResources().getString(R.string.nutrition).toLowerCase()) || command.contains(getResources().getString(R.string.Nutrition_Diet).toLowerCase())) {
            nutritionFragment = new NutritionFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", 0);
            nutritionFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, nutritionFragment).commit();
        }
        else if (command.contains(getResources().getString(R.string.Nutrition_Recipes).toLowerCase())) {
            nutritionFragment = new NutritionFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", 1);
            nutritionFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, nutritionFragment).commit();
        }
        else if (command.contains(getResources().getString(R.string.menstrual_cycle).toLowerCase())) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MenstrualCycleFragment()).commit();
        }
        else if (command.contains(getResources().getString(R.string.medication).toLowerCase())) {
            medicationFragment = new MedicationFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, medicationFragment).commit();
        }
        else if (command.contains(getResources().getString(R.string.memory).toLowerCase())) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MemoryFragment()).commit();
        }
        else if (command.contains(getResources().getString(R.string.pedometer).toLowerCase())) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PedometerFragment()).commit();
        }
        else if (command.contains(getResources().getString(R.string.heart_rate).toLowerCase())) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HeartRateMonitor()).commit();
        }
        else if (command.contains(getResources().getString(R.string.settings).toLowerCase())) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
        }
        else if (getTitle().equals(getResources().getString(R.string.nutrition))) {
            if (nutritionFragment != null && nutritionFragment.tabLayout.getSelectedTabPosition() == 0) {
                if (command.contains(getResources().getString(R.string.Calendar_AddButton).toLowerCase())) {
                    addMeal = nutritionFragment.adapter.calendarFragment.getView().findViewById(R.id.calendar_addbutton);
                    addMeal.performClick();
                    satisfactoryRecognition = true;
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, prefs.getString(ContainerActivity.LANGUAGE_KEY, Locale.getDefault().getLanguage()));
                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                    mySpeechRecognizer.startListening(intent);
                }
                else if (addMeal != null) {
                    if (command.contains(getResources().getString(R.string.dialog_add).toLowerCase())) {
                        Button positiveButton = nutritionFragment.adapter.calendarFragment.newMealDialog.positiveButton;
                        positiveButton.performClick();
                        satisfactoryRecognition = true;
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, prefs.getString(ContainerActivity.LANGUAGE_KEY, Locale.getDefault().getLanguage()));
                        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                        mySpeechRecognizer.startListening(intent);
                    }
                    else if (command.contains(getResources().getString(R.string.dialog_cancel).toLowerCase())) {
                        Button negativeButton = nutritionFragment.adapter.calendarFragment.newMealDialog.negativeButton;
                        negativeButton.performClick();
                        satisfactoryRecognition = true;
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, prefs.getString(ContainerActivity.LANGUAGE_KEY, Locale.getDefault().getLanguage()));
                        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                        mySpeechRecognizer.startListening(intent);
                    }
                    else if (command.contains(getResources().getString(R.string.NewMeal_Description).toLowerCase())) {
                        mealDescription = nutritionFragment.adapter.calendarFragment.newMealDialog.getDialog().findViewById(R.id.meal_description_edit_text);
                        mealDescription.requestFocus();
                        mealDescription.performClick();
                        satisfactoryRecognition = true;
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, prefs.getString(ContainerActivity.LANGUAGE_KEY, Locale.getDefault().getLanguage()));
                        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                        mySpeechRecognizer.startListening(intent);
                    }
                    else {
                        boolean found = false;
                        for (String s : typeOfMeal) {
                            if (command.contains(s.toLowerCase())) {
                                mealType = nutritionFragment.adapter.calendarFragment.newMealDialog.getDialog().findViewById(R.id.meal_spinner);
                                mealType.setSelection(typeOfMeal.indexOf(s));
                                Log.e("spinner", s);
                                satisfactoryRecognition = true;
                                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, prefs.getString(ContainerActivity.LANGUAGE_KEY, Locale.getDefault().getLanguage()));
                                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                                mySpeechRecognizer.startListening(intent);
                                found = true;
                                break;
                            }
                        }
                        if (!found && mealDescription != null) {
                            mealDescription.setText((mealDescription.getText().toString().isEmpty() ? "" : (mealDescription.getText().toString() + "\n")) + command);
                            mealDescription.setSelection(mealDescription.getText().toString().length());
                            satisfactoryRecognition = true;
                            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, prefs.getString(ContainerActivity.LANGUAGE_KEY, Locale.getDefault().getLanguage()));
                            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                            mySpeechRecognizer.startListening(intent);
                        }
                        else {
                            satisfactoryRecognition = false;
                        }
                    }
                }
            }
            else if (nutritionFragment != null && nutritionFragment.tabLayout.getSelectedTabPosition() == 1) {

                if (command.contains(getResources().getString(R.string.Recipes_SearchHint).toLowerCase().substring(0, getResources().getString(R.string.Recipes_SearchHint).toLowerCase().length()-3))) {
                    searchRecipe = nutritionFragment.adapter.recipesFragment.getView().findViewById(R.id.search_recipes);
                    searchRecipe.requestFocus();
                    searchRecipe.performClick();
                    satisfactoryRecognition = true;
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, prefs.getString(ContainerActivity.LANGUAGE_KEY, Locale.getDefault().getLanguage()));
                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                    mySpeechRecognizer.startListening(intent);
                }
                else if (searchRecipe != null){
                    searchRecipe.setText(command);
                    searchRecipe.setSelection(searchRecipe.getText().toString().length());
                    satisfactoryRecognition = true;
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, prefs.getString(ContainerActivity.LANGUAGE_KEY, Locale.getDefault().getLanguage()));
                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                    mySpeechRecognizer.startListening(intent);
                }
            }
        }
        else if (getTitle().equals(getResources().getString(R.string.medication))) {
            if (medicationFragment != null) {
                if (command.contains(getResources().getString(R.string.Medication_AddMedication).toLowerCase())) {
                    FloatingActionButton addMedication = medicationFragment.getView().findViewById(R.id.add_medication);
                    addMedication.performClick();
                    satisfactoryRecognition = true;
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, prefs.getString(ContainerActivity.LANGUAGE_KEY, Locale.getDefault().getLanguage()));
                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                    mySpeechRecognizer.startListening(intent);
                }
            }
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
