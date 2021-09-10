package com.example.canihealthyou;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView qrCodeScanner;
    private Toolbar mTopToolbar;
    private SharedPreferences prefs;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_qr);

        prefs = getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        final String language = prefs.getString(ContainerActivity.LANGUAGE_KEY, Locale.getDefault().getLanguage());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(language));
        res.updateConfiguration(conf, dm);

        setTitle(getResources().getString(R.string.qrscanner));

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
        setContentView(R.layout.activity_qr);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar_qrscanner);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(4.0f);

        qrCodeScanner = findViewById(R.id.qrCodeScanner);
        setScannerProperties();

    }

    private void setScannerProperties() {
        qrCodeScanner.setFormats(Arrays.asList(BarcodeFormat.QR_CODE));
        qrCodeScanner.setAutoFocus(true);
        /*
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        @ColorInt int color = typedValue.data;
        */
        qrCodeScanner.setLaserColor(R.attr.colorPrimary);
        qrCodeScanner.setMaskColor(R.attr.colorPrimary);
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeScanner.startCamera();
        qrCodeScanner.setResultHandler(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeScanner.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        if (result != null) {
            //StyleableToast.makeText(this, result.getText(), R.style.success).show();
            Intent containerActivity = new Intent(QRScanner.this, ContainerActivity.class);
            containerActivity.putExtra("frgToLoad", "Nutrition");
            containerActivity.putExtra("position", 1);
            containerActivity.putExtra("recipeJSON", result.getText());
            containerActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(containerActivity);
        }
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
                Intent containerActivity = new Intent(QRScanner.this, ContainerActivity.class);
                containerActivity.putExtra("frgToLoad", "Nutrition");
                containerActivity.putExtra("position", 1);
                containerActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(containerActivity);
                break;
        }
        return true;
    }
}
