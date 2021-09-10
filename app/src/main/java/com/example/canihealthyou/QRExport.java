package com.example.canihealthyou;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

public class QRExport extends AppCompatActivity {

    private Toolbar mTopToolbar;
    SharedPreferences prefs;
    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        final String language = prefs.getString(ContainerActivity.LANGUAGE_KEY, Locale.getDefault().getLanguage());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(language));
        res.updateConfiguration(conf, dm);

        setTitle(getResources().getString(R.string.qrexport));

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

        setContentView(R.layout.activity_qrexport);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar_qrexport);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(4.0f);

        String recipeItemName = getIntent().getExtras().getString("recipe");
        Gson gson = new Gson();
        String json = prefs.getString(RecipesFragment.recipesKey, null);
        Type type = new TypeToken<ArrayList<RecipeItem>>() {}.getType();
        ArrayList<RecipeItem> recipesData = gson.fromJson(json, type);

        RecipeItem recipeItem = null;

        if (!recipesData.isEmpty()) {
            for (RecipeItem r : recipesData) {
                if (r.getName().equals(recipeItemName)) {
                    recipeItem = r;
                    break;
                }
            }
        }

        String serializeString = gson.toJson(recipeItem);
        Bitmap bitmap = QRCodeHelper
                .newInstance(this)
                .setContent(serializeString)
                .setErrorCorrectionLevel(ErrorCorrectionLevel.L)
                .setMargin(2)
                .getQRCOde();

        ImageView qrCodeImageView = findViewById(R.id.qrCodeImageView);
        qrCodeImageView.setImageBitmap(bitmap);

        TextView recipeName = findViewById(R.id.recipe_name_qrexport);
        recipeName.setText(recipeItemName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    mTopToolbar.setHapticFeedbackEnabled(true);
                    mTopToolbar.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                Intent containerActivity = new Intent(QRExport.this, ContainerActivity.class);
                containerActivity.putExtra("frgToLoad", "Nutrition");
                containerActivity.putExtra("position", 1);
                containerActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(containerActivity);
                break;
        }
        return true;
    }
}
