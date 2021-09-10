package com.example.canihealthyou;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.canihealthyou.nfc.OutcomingNfcManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muddzdev.styleabletoast.StyleableToast;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;


public class SenderActivity extends AppCompatActivity implements OutcomingNfcManager.NfcActivity {

    private TextView tvOutcomingMessage;
    private String recipeName;
    private Button btnSetOutcomingMessage;
    private ImageView recipeImage;

    private String outMessage = "";

    private NfcAdapter nfcAdapter;
    private OutcomingNfcManager outcomingNfccallback;

    private Toolbar mTopToolbar;
    private SharedPreferences prefs;
    private AudioManager audioManager;

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

        setTitle(getResources().getString(R.string.NFC_Sender_Title));

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
        setContentView(R.layout.activity_sender);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar_nfc_sender);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(4.0f);


        if (!isNfcSupported()) {
            StyleableToast.makeText(this, getResources().getString(R.string.NFC_NotSupported), R.style.error).show();
            Log.e("NFC", "Not supported");
            finish();
            return;
        }
        if (!nfcAdapter.isEnabled()) {
            StyleableToast.makeText(this, getResources().getString(R.string.NFC_NotEnabled), R.style.error).show();
        }

        initViews();

        // encapsulate sending logic in a separate class
        this.outcomingNfccallback = new OutcomingNfcManager(this);
        this.nfcAdapter.setOnNdefPushCompleteCallback(outcomingNfccallback, this);
        this.nfcAdapter.setNdefPushMessageCallback(outcomingNfccallback, this);

        recipeName = getIntent().getExtras().getString("recipe_name", null);
        outMessage = null;
        if (recipeName != null) {
            SharedPreferences prefs = getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String recipeJSON = prefs.getString(RecipesFragment.recipesKey, null);
            Type type = new TypeToken<ArrayList<RecipeItem>>() {}.getType();
            ArrayList<RecipeItem> recipesData = gson.fromJson(recipeJSON, type);
            if (recipesData != null && !recipesData.isEmpty()) {
                RecipeItem recipeToSend = null;
                for (RecipeItem r : recipesData) {
                    if (r.getName().equals(recipeName)) {
                        recipeToSend = r;
                        break;
                    }
                }
                if (recipeToSend != null) {
                    outMessage = gson.toJson(recipeToSend);
                    tvOutcomingMessage.setText(recipeToSend.getName());
                }
            }
        }
    }

    private void initViews() {
        this.tvOutcomingMessage = findViewById(R.id.tv_out_message);
        //this.btnSetOutcomingMessage = findViewById(R.id.btn_set_out_message);
        this.recipeImage = findViewById(R.id.recipe_image_nfc);
        //this.btnSetOutcomingMessage.setOnClickListener((v) -> setOutGoingMessage());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    private boolean isNfcSupported() {
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        return this.nfcAdapter != null;
    }

    private void setOutGoingMessage() {
        //String outMessage = this.etOutcomingMessage.getText().toString();
        this.tvOutcomingMessage.setText(outMessage);
    }

    @Override
    public String getOutcomingMessage() {
        //return this.tvOutcomingMessage.getText().toString();
        return this.outMessage;
    }

    @Override
    public void signalResult() {
        // this will be triggered when NFC message is sent to a device.
        // should be triggered on UI thread. We specify it explicitly
        // cause onNdefPushComplete is called from the Binder thread
        runOnUiThread(() ->
                Toast.makeText(SenderActivity.this, R.string.message_beaming_complete, Toast.LENGTH_SHORT).show());
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
                Intent containerActivity = new Intent(SenderActivity.this, ContainerActivity.class);
                containerActivity.putExtra("frgToLoad", "Nutrition");
                containerActivity.putExtra("position", 1);
                containerActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(containerActivity);
                break;
        }
        return true;
    }
}
