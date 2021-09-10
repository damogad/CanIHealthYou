package com.example.canihealthyou;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.muddzdev.styleabletoast.StyleableToast;

import java.util.Locale;

public class TTSSettingsDialog extends AppCompatDialogFragment {

    TextToSpeech mTTS;
    SeekBar pitchSeekbar;
    SeekBar speedSeekbar;
    Button tryTTSButton;

    String language;

    SharedPreferences prefs;
    AudioManager audioManager;

    public TTSSettingsDialog(String language) {
        this.language = language;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.tts_settings_layout, null);
        builder.setView(view).setTitle(getActivity().getResources().getString(R.string.TTS_Settings_Title)).setNegativeButton(getActivity().getResources().getString(R.string.dialog_cancel), null).setPositiveButton(getActivity().getResources().getString(R.string.dialog_confirm), null);

        prefs = getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        pitchSeekbar = view.findViewById(R.id.seek_bar_pitch);
        pitchSeekbar.setProgress(prefs.getInt("pitch", 25));
        pitchSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    seekBar.setHapticFeedbackEnabled(true);
                    seekBar.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        speedSeekbar = view.findViewById(R.id.seek_bar_speed);
        speedSeekbar.setProgress(prefs.getInt("speed", 25));
        speedSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    seekBar.setHapticFeedbackEnabled(true);
                    seekBar.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mTTS = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(new Locale(language));

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        StyleableToast.makeText(getActivity(), "Language not supported", R.style.error).show();
                    }
                }
                else {
                    StyleableToast.makeText(getActivity(), "TTS initialization failed", R.style.error).show();
                }
            }
        });

        tryTTSButton = view.findViewById(R.id.try_tts_button);
        tryTTSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                speak();
            }
        });


        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        SharedPreferences prefs = getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
                        if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                            view.setHapticFeedbackEnabled(true);
                            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                        }
                        if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                            audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
                        }
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("pitch", pitchSeekbar.getProgress());
                        editor.putInt("speed", speedSeekbar.getProgress());
                        editor.commit();
                        dialog.dismiss();
                        //Dismiss once everything is OK.
                    }
                });
                Button buttonCancel = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                buttonCancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        SharedPreferences prefs = getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
                        if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                            view.setHapticFeedbackEnabled(true);
                            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                        }
                        if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                            audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        @ColorInt int color = typedValue.data;
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(color);
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(color);
    }

    private void speak() {
        float pitch = (float) pitchSeekbar.getProgress() / 50;
        if (pitch < 0.1)
            pitch = 0.1f;
        float speed = (float) speedSeekbar.getProgress() / 50;
        if (speed < 0.1)
            speed = 0.1f;

        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);

        String text = getActivity().getResources().getString(R.string.TTS_Settings_PlaceholderText);
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDismiss(dialog);
    }

    public interface TTSSettingsDialogListener {
        void adjust(float pitch, float speed);
    }
}
