package com.example.canihealthyou;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muddzdev.styleabletoast.StyleableToast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;

public class TTSActivity extends AppCompatActivity {

    Toolbar mTopToolbar;
    RecipeItem item;

    ImageView recipeImage;
    TextView recipeTitle;
    TextView recipeApproxTime;
    ImageView qrExport;
    ImageView nfcExport;

    RecyclerView ingredientsRecyclerView;
    IngredientsAdapter ingredientsAdapter;
    RecyclerView stepsRecyclerView;
    StepsAdapter stepsAdapter;
    RecyclerView.SmoothScroller smoothScroller;

    BottomAppBar bottomAppBar;
    FloatingActionButton playFAB;
    ImageView skipPrevious;
    ImageView skipNext;
    ImageView ttsSettings;
    TextView totalTimeSpent;

    SharedPreferences prefs;
    AudioManager audioManager;

    // TTS
    Handler handler;
    Runnable r;
    int currentStep = 0;
    boolean playing = false;
    TextToSpeech mTTS;
    /*
    SeekBar mSeekBarPitch;
    SeekBar mSeekBarSpeed;
    */
    boolean initCounter = false;
    int counter = 0;
    Handler timerHandler;
    Runnable t;
    boolean skipFlag = false;
    final ReentrantLock reentrantLock = new ReentrantLock(true);
    boolean repeat = false;


    String currentPhotoPath = null;
    ArrayList<RecipeItem> recipesFullList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        final String language = prefs.getString(ContainerActivity.LANGUAGE_KEY, Locale.getDefault().getLanguage());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(language));
        res.updateConfiguration(conf, dm);

        setTitle(getResources().getString(R.string.RecipeDetails_ActivityTitle));

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
        setContentView(R.layout.activity_tts);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar_tts);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(4.0f);


        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        final @ColorInt int color = typedValue.data;
        theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
        final @ColorInt int color2 = typedValue.data;

        recipeImage = findViewById(R.id.recipe_image);
        recipeTitle = findViewById(R.id.recipe_title);
        recipeApproxTime = findViewById(R.id.recipe_approx_time);
        totalTimeSpent = findViewById(R.id.total_time_spent);
        qrExport = findViewById(R.id.send_recipe_qr);
        nfcExport = findViewById(R.id.send_recipe_nfc);

        Gson gson = new Gson();
        String json = prefs.getString(RecipesFragment.recipesKey, null);
        Type type = new TypeToken<ArrayList<RecipeItem>>() {}.getType();
        recipesFullList = gson.fromJson(json, type);
        if (recipesFullList.isEmpty()) {
            recipesFullList = new ArrayList<>();
        }

        for (RecipeItem r : recipesFullList) {
            if (r.getName().equals(getIntent().getExtras().getString("RecipeToLoad"))) {
                item = r;
                break;
            }
        }

        String imagePath;
        int imageId;

        if ((imagePath = item.getPicturePath()) != null) {
            File imgFile = new File(imagePath);

            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                recipeImage.setImageBitmap(myBitmap);
            }
        }
        else if ((imageId = item.getPictureId()) != -1) {
            recipeImage.setImageResource(imageId);
        }
        else {
            recipeImage.setScaleType(ImageView.ScaleType.CENTER);
            recipeImage.setImageResource(R.drawable.ic_camera_alt_white_108dp);
            recipeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                        view.setHapticFeedbackEnabled(true);
                        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                    }
                    if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                        audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                    }
                    dispatchTakePictureIntent();
                }
            });
        }

        recipeTitle.setText(item.getName());
        recipeApproxTime.setText(getResources().getString(R.string.Recipes_ApproxTime) + " " + item.getApproxTime());

        playFAB = findViewById(R.id.recipe_fab_play);
        playFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                if (repeat && !skipFlag) {
                    currentStep = 0;
                }
                if (ingredientsAdapter.counterCheckedIngredients == item.getIngredients().size()) {
                    if (!initCounter) {
                        timerHandler = new Handler();
                        t = new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder s = new StringBuilder();
                                s.append(counter/3600000 + ":");
                                s.append(String.format(new Locale(language), "%02d", ((counter/60000) % 60)) + ":");
                                s.append(String.format(new Locale(language), "%02d", (counter/1000) % 60));
                                totalTimeSpent.setText(s.toString());
                                timerHandler.postDelayed(this, 1000);
                                counter += 1000;
                                //Log.e("time", String.valueOf(counter));
                            }
                        };
                        initCounter = true;
                        timerHandler.postDelayed(t, 1000);
                    }
                    reentrantLock.lock();
                    if (!playing) {
                        playFAB.setImageDrawable(TTSActivity.this.getResources().getDrawable(R.drawable.ic_pause_white_24dp));
                        playing = true;
                        reentrantLock.unlock();
                        handler = new Handler();

                        r = new Runnable() {
                            public void run() {
                                reentrantLock.lock();
                                repeat = false;
                                Log.e("step,playing", String.valueOf(currentStep) + "," + String.valueOf(playing));
                                if (currentStep < item.getSteps().size() && playing) {
                                    for (int i = 0; i < item.getSteps().size(); i++) {
                                        if (i != currentStep)
                                            stepsAdapter.listOfStepViews.get(i).stepDescription.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                                    }
                                    //if (currentStep > 0)
                                    //    stepsAdapter.listOfStepViews.get(currentStep-1).stepDescription.setTypeface(stepsAdapter.listOfStepViews.get(currentStep-1).stepDescription.getTypeface(), Typeface.NORMAL);
                                    //if (currentStep < (item.getSteps().size() - 1))
                                    //    stepsAdapter.listOfStepViews.get(currentStep+1).stepDescription.setTypeface(stepsAdapter.listOfStepViews.get(currentStep+1).stepDescription.getTypeface(), Typeface.NORMAL);
                                    stepsAdapter.listOfStepViews.get(currentStep).stepDescription.setTypeface(stepsAdapter.listOfStepViews.get(currentStep).stepDescription.getTypeface(), Typeface.BOLD);
                                    reentrantLock.unlock();
                                    speak();
                                    reentrantLock.lock();
                                    StepItem stepItem = item.getSteps().get(currentStep);
                                    reentrantLock.unlock();
                                    int pause = stepItem.getHours() * 3600 * 1000;
                                    pause += stepItem.getMinutes() * 60 * 1000;
                                    pause += stepItem.getSeconds() * 1000;
                                    handler.postDelayed(this, pause);
                                    reentrantLock.lock();
                                    Log.e("currentstep", String.valueOf(currentStep));
                                    if (!skipFlag /*|| (currentStep == (item.getSteps().size()-1))*/) {
                                        currentStep++;
                                    }
                                    skipFlag = false;
                                    reentrantLock.unlock();
                                    //stepsAdapter.listOfStepViews.get(currentStep-1).stepDescription.setTypeface(Typeface.DEFAULT);
                                }
                                else {
                                    playFAB.setImageDrawable(TTSActivity.this.getResources().getDrawable(R.drawable.ic_icons8_replay_90));
                                    playing = false;
                                    //currentStep = 0;
                                    counter = 0;
                                    repeat = true;
                                    handler.removeCallbacks(this);
                                    timerHandler.removeCallbacks(t);
                                    initCounter = false;
                                    reentrantLock.unlock();
                                }
                            }
                        };

                        handler.postDelayed(r, 10);

                    }
                    else {
                        playFAB.setImageDrawable(TTSActivity.this.getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
                        mTTS.stop();
                        handler.removeCallbacks(r);
                        timerHandler.removeCallbacks(t);
                        //currentStep = 0;
                        playing = false;
                        initCounter = false;
                        reentrantLock.unlock();
                    }
                }
                else {
                    StyleableToast.makeText(TTSActivity.this, TTSActivity.this.getResources().getString(R.string.Step_CannotPlayUntilCheckedIngredients), R.style.error_withicon).show();
                }

            }
        });


        skipPrevious = findViewById(R.id.recipe_previous_arrow);
        skipPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                reentrantLock.lock();
                if (currentStep > 0) {
                    //stepsAdapter.listOfStepViews.get(currentStep).stepDescription.setTypeface(stepsAdapter.listOfStepViews.get(currentStep).stepDescription.getTypeface(), Typeface.NORMAL);
                    currentStep--;
                    playing = false;
                    skipFlag = true;
                    handler.removeCallbacks(r);
                    for (int i = 0; i < item.getSteps().size(); i++) {
                        if (i != currentStep)
                            stepsAdapter.listOfStepViews.get(i).stepDescription.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                    }
                    reentrantLock.unlock();

                    //stepsAdapter.listOfStepViews.get(currentStep).stepDescription.setTypeface(stepsAdapter.listOfStepViews.get(currentStep).stepDescription.getTypeface(), Typeface.BOLD);


                    playFAB.performClick();
                }
                else {
                    StyleableToast.makeText(TTSActivity.this, TTSActivity.this.getResources().getString(R.string.Step_PreviousNotAllowed), R.style.error).show();
                    reentrantLock.unlock();
                }
            }
        });


        skipNext = findViewById(R.id.recipe_next_arrow);
        skipNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                reentrantLock.lock();
                if (currentStep < (item.getSteps().size()-1) && handler != null) {
                    //stepsAdapter.listOfStepViews.get(currentStep).stepDescription.setTypeface(stepsAdapter.listOfStepViews.get(currentStep).stepDescription.getTypeface(), Typeface.NORMAL);
                    currentStep++;
                    //stepsAdapter.listOfStepViews.get(currentStep).stepDescription.setTypeface(stepsAdapter.listOfStepViews.get(currentStep).stepDescription.getTypeface(), Typeface.BOLD);
                    playing = false;
                    skipFlag = true;
                    handler.removeCallbacks(r);
                    for (int i = 0; i < item.getSteps().size(); i++) {
                        if (i != currentStep)
                            stepsAdapter.listOfStepViews.get(i).stepDescription.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                    }
                    reentrantLock.unlock();
                    playFAB.performClick();
                }
                else {
                    StyleableToast.makeText(TTSActivity.this, TTSActivity.this.getResources().getString(R.string.Step_NextNotAllowed), R.style.error).show();
                    reentrantLock.unlock();
                }
            }
        });

        ttsSettings = findViewById(R.id.recipe_tts_settings);
        ttsSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                TTSSettingsDialog ttsSettingsDialog = new TTSSettingsDialog(language);
                ttsSettingsDialog.show(getSupportFragmentManager(), "TTS_SETTINGS");
            }
        });


        ingredientsRecyclerView = findViewById(R.id.recipe_ingredients_recyclerview);
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ingredientsAdapter = new IngredientsAdapter(item.getIngredients(), this);
        ingredientsRecyclerView.setAdapter(ingredientsAdapter);


        stepsRecyclerView = findViewById(R.id.recipe_steps_recyclerview);
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        stepsAdapter = new StepsAdapter(item.getSteps(), this);
        stepsRecyclerView.setAdapter(stepsAdapter);
        smoothScroller = new LinearSmoothScroller(this) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(new Locale(language));

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        StyleableToast.makeText(TTSActivity.this, "Language not supported", R.style.error).show();
                        playFAB.setEnabled(false);
                    }
                    else {
                        playFAB.setEnabled(true);
                    }
                }
                else {
                    StyleableToast.makeText(TTSActivity.this, "TTS initialization failed", R.style.error).show();
                }
            }
        });

        /*
        mSeekBarPitch = findViewById(R.id.seek_bar_pitch);
        mSeekBarSpeed = findViewById(R.id.seek_bar_speed);
        */

        qrExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
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
                //StyleableToast.makeText(mActivity, "EXPORT RECIPE WITH QR CODE", R.style.success).show();
                Intent qrExportActivity = new Intent(TTSActivity.this, QRExport.class);
                qrExportActivity.putExtra("recipe", item.getName());
                startActivity(qrExportActivity);
            }
        });

        nfcExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
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
                //StyleableToast.makeText(mActivity, "EXPORT RECIPE WITH QR CODE", R.style.success).show();
                Intent qrExportActivity = new Intent(TTSActivity.this, SenderActivity.class);
                qrExportActivity.putExtra("recipe_name", item.getName());
                startActivity(qrExportActivity);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    recipeTitle.setHapticFeedbackEnabled(true);
                    recipeTitle.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                Intent containerActivity = new Intent(TTSActivity.this, ContainerActivity.class);
                containerActivity.putExtra("frgToLoad", "Nutrition");
                containerActivity.putExtra("position", 1);
                containerActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(containerActivity);
                break;
            /*case R.id.microphone_tts_activity:
                Toast.makeText(TTSActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
                break;*/

        }
        return true;
    }

    private void speak() {
        float pitch = (float) prefs.getInt("pitch", 25) / 50;
        if (pitch < 0.1)
            pitch = 0.1f;
        float speed = (float) prefs.getInt("speed", 25) / 50;
        if (speed < 0.1)
            speed = 0.1f;

        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);

        StepItem stepItem = this.item.getSteps().get(currentStep);
        String text = stepItem.getDescription();
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        //mTTS.playSilence(pause, TextToSpeech.QUEUE_ADD, null);
    }

    /*
    private void pause() {
        mTTS.playSilence(0, TextToSpeech.QUEUE_ADD, null);
    }
    */

    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        if (handler != null)
            handler.removeCallbacks(r);
        if (timerHandler != null)
            timerHandler.removeCallbacks(t);
        super.onDestroy();
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tts_activity, menu);
        return true;
    }
    */

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("TTSActivity", "Error while creating image file");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            File imgFile = new File(currentPhotoPath);
            if (imgFile.exists()) {
                recipesFullList.get(recipesFullList.indexOf(item)).setPicturePath(currentPhotoPath);
                SharedPreferences.Editor editor = prefs.edit();
                Gson gson = new Gson();
                String json = gson.toJson(recipesFullList);
                editor.putString(RecipesFragment.recipesKey, json);
                editor.commit();
                recipeImage.setImageBitmap(null);
                recipeImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                recipeImage.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
            }
        }
        else {
            currentPhotoPath = null;
        }
    }

}
