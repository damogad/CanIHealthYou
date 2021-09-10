package com.example.canihealthyou;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;


import java.util.Locale;

import static android.app.PendingIntent.FLAG_ONE_SHOT;

/**
 * Created by azem on 10/29/17.
 */

public class AlarmNotificationReceiver extends BroadcastReceiver {
    static int counter = 0;
    private TextToSpeech mTTS;
    private SharedPreferences prefs;

    @Override
    public void onReceive(final Context context, Intent intent) {
        prefs = context.getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        final String language = prefs.getString(ContainerActivity.LANGUAGE_KEY, Locale.getDefault().getLanguage());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getResources().getString(R.string.medication);
            //String description = intent.getExtras().getString("medication");
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(context.getResources().getString(R.string.medication), name, importance);
            //channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,  context.getResources().getString(R.string.medication));

        Intent myIntent = new Intent(context, ContainerActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        myIntent.putExtra("frgToLoad", "Medication");
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                intent.getExtras().getInt("counter"),
                myIntent,
                FLAG_ONE_SHOT );

        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_stat_app_launcher)
                .setContentTitle(context.getResources().getString(R.string.medication))
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(intent.getExtras().getString("medication")))
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(intent.getExtras().getInt("counter"),builder.build());
        Log.e("notificacion", intent.getExtras().getString("medication") + ", " + intent.getExtras().getInt("counter"));


        Intent speechIntent = new Intent(context, TTS.class);
        speechIntent.putExtra("medication", intent.getExtras().getString("medication"));
        speechIntent.putExtra("language", language);
        float pitch = (float) prefs.getInt("pitch", 25) / 50;
        if (pitch < 0.1)
            pitch = 0.1f;
        speechIntent.putExtra("pitch", pitch);
        float speed = (float) prefs.getInt("speed", 25) / 50;
        if (speed < 0.1)
            speed = 0.1f;
        speechIntent.putExtra("speed", speed);
        context.startService(speechIntent);

    }


    public static class TTS extends Service implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {
        private TextToSpeech mTts;
        private String spokenText;
        private String language;
        private float pitch;
        private float speed;

        @Override
        public void onCreate() {
            mTts = new TextToSpeech(this, this);
            // This is a good place to set spokenText
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            spokenText = intent.getExtras().getString("medication");
            language = intent.getExtras().getString("language");
            pitch = intent.getExtras().getFloat("pitch");
            speed = intent.getExtras().getFloat("speed");
            Log.e("parametros", spokenText + " " + language + " " + String.valueOf(pitch) + " " + String.valueOf(speed));
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                int result = mTts.setLanguage(new Locale(language));
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    mTts.setPitch(pitch);
                    mTts.setSpeechRate(speed);
                    mTts.speak(spokenText, TextToSpeech.QUEUE_ADD, null);
                }
            }
        }

        @Override
        public void onUtteranceCompleted(String uttId) {
            stopSelf();
        }

        @Override
        public void onDestroy() {
            if (mTts != null) {
                mTts.stop();
                mTts.shutdown();
            }
            super.onDestroy();
        }

        @Override
        public IBinder onBind(Intent arg0) {
            return null;
        }
    }

}
