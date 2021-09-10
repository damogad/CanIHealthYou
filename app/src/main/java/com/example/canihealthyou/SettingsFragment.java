package com.example.canihealthyou;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import androidx.annotation.Dimension;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    AudioManager audioManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle(getActivity().getResources().getString(R.string.settings));


        final SharedPreferences prefs = getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);

        String language = prefs.getString(ContainerActivity.LANGUAGE_KEY, Locale.getDefault().getLanguage());
        //Locale.setDefault(new Locale(language));

        String fontSize = prefs.getString(ContainerActivity.FONT_SIZE_KEY, "medium");
        String colorTheme = prefs.getString(ContainerActivity.COLOR_THEME_KEY, "default");
        boolean highContrast = prefs.getBoolean(ContainerActivity.HIGHCONTRAST_KEY, false);
        int progress = 1;

        switch (fontSize) {
            case "small":
                progress = 0;
                break;
            case "medium":
                progress = 1;
                break;
            case "large":
                progress = 2;
                break;
        }

        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Switch vibrationSwitch = view.findViewById(R.id.switch_vibration);
        vibrationSwitch.setChecked(prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false));
        vibrationSwitch.setSoundEffectsEnabled(false);
        vibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    compoundButton.setHapticFeedbackEnabled(true);
                    compoundButton.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(ContainerActivity.VIBRATION_KEY, b);
                editor.commit();
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    if (b)
                        audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
                    else
                        audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                }
            }
        });

        Switch soundSwitch = view.findViewById(R.id.switch_sound);
        soundSwitch.setChecked(prefs.getBoolean(ContainerActivity.SOUND_KEY, false));
        soundSwitch.setSoundEffectsEnabled(false);
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    compoundButton.setHapticFeedbackEnabled(true);
                    compoundButton.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(ContainerActivity.SOUND_KEY, b);
                editor.commit();
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    if (b)
                        audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
                    else
                        audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                }
            }
        });


        Switch highContrastSwitch = view.findViewById(R.id.switch_highcontrast);
        highContrastSwitch.setChecked(prefs.getBoolean(ContainerActivity.HIGHCONTRAST_KEY, false));
        highContrastSwitch.setSoundEffectsEnabled(false);
        highContrastSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    compoundButton.setHapticFeedbackEnabled(true);
                    compoundButton.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    if (b)
                        audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
                    else
                        audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(ContainerActivity.HIGHCONTRAST_KEY, b);
                editor.commit();
                getActivity().recreate();
            }
        });

        Switch automaticSpeechRecognitionSwitch = view.findViewById(R.id.switch_automatic_speechrecognition);
        automaticSpeechRecognitionSwitch.setChecked(prefs.getBoolean(ContainerActivity.AUTOMATIC_SPEECH_RECOGNITION_KEY, false));
        automaticSpeechRecognitionSwitch.setSoundEffectsEnabled(false);
        automaticSpeechRecognitionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    compoundButton.setHapticFeedbackEnabled(true);
                    compoundButton.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    if (b)
                        audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
                    else
                        audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(ContainerActivity.AUTOMATIC_SPEECH_RECOGNITION_KEY, b);
                editor.commit();
                //getActivity().recreate();
            }
        });

        CardView englishCard = view.findViewById(R.id.english_card);
        if (language.equals("en"))
            englishCard.setCardBackgroundColor(getResources().getColor(android.R.color.black));
        englishCard.setSoundEffectsEnabled(false);
        englishCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(ContainerActivity.LANGUAGE_KEY, "en");
                editor.commit();
                getActivity().recreate();
            }
        });

        CardView spanishCard = view.findViewById(R.id.spanish_card);
        if (language.contains("es"))
            spanishCard.setCardBackgroundColor(getResources().getColor(android.R.color.black));
        spanishCard.setSoundEffectsEnabled(false);
        spanishCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(ContainerActivity.LANGUAGE_KEY, "es");
                editor.commit();
                getActivity().recreate();
            }
        });

        SeekBar seekBarTextSize = view.findViewById(R.id.seekbar_textsize);
        seekBarTextSize.setMax(2);
        seekBarTextSize.setProgress(progress);
        seekBarTextSize.setSoundEffectsEnabled(false);
        seekBarTextSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    seekBar.setHapticFeedbackEnabled(true);
                    seekBar.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                SharedPreferences.Editor editor = prefs.edit();
                String fontSize = "medium";
                switch (i) {
                    case 0:
                        fontSize = "small";
                        break;
                    case 1:
                        fontSize = "medium";
                        break;
                    case 2:
                        fontSize = "large";
                        break;
                }

                editor.putString(ContainerActivity.FONT_SIZE_KEY, fontSize);
                //---saves the values---
                editor.commit();

                getActivity().recreate();

                //---display file saved message---
                //Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.font_size_message) + " " + fontSize + "!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        CardView defaultColorTheme = view.findViewById(R.id.default_color_theme_card);
        if (colorTheme.equals("default"))
            defaultColorTheme.setCardBackgroundColor(getResources().getColor(android.R.color.black));
        defaultColorTheme.setSoundEffectsEnabled(false);
        defaultColorTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(ContainerActivity.COLOR_THEME_KEY, "default");
                //---saves the values---
                editor.commit();

                getActivity().recreate();

                //---display file saved message---
                //Toast.makeText(getActivity(),"Color theme saved successfully to default!", Toast.LENGTH_LONG).show();
            }
        });

        CardView redColorTheme = view.findViewById(R.id.red_color_theme_card);
        if (colorTheme.equals("red"))
            redColorTheme.setCardBackgroundColor(getResources().getColor(android.R.color.black));
        redColorTheme.setSoundEffectsEnabled(false);
        redColorTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(ContainerActivity.COLOR_THEME_KEY, "red");
                //---saves the values---
                editor.commit();

                getActivity().recreate();

                //---display file saved message---
                //Toast.makeText(getActivity(),"Color theme saved successfully to red!", Toast.LENGTH_LONG).show();
            }
        });

        CardView purpleColorTheme = view.findViewById(R.id.purple_color_theme_card);
        if (colorTheme.equals("purple"))
            purpleColorTheme.setCardBackgroundColor(getResources().getColor(android.R.color.black));
        purpleColorTheme.setSoundEffectsEnabled(false);
        purpleColorTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(ContainerActivity.COLOR_THEME_KEY, "purple");
                //---saves the values---
                editor.commit();

                getActivity().recreate();

                //---display file saved message---
                //Toast.makeText(getActivity(),"Color theme saved successfully to purple!", Toast.LENGTH_LONG).show();
            }
        });

        CardView greenColorTheme = view.findViewById(R.id.green_color_theme_card);
        if (colorTheme.equals("green"))
            greenColorTheme.setCardBackgroundColor(getResources().getColor(android.R.color.black));
        greenColorTheme.setSoundEffectsEnabled(false);
        greenColorTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(ContainerActivity.COLOR_THEME_KEY, "green");
                //---saves the values---
                editor.commit();

                getActivity().recreate();

                //---display file saved message---
                //Toast.makeText(getActivity(),"Color theme saved successfully to green!", Toast.LENGTH_LONG).show();
            }
        });

        CardView blueColorTheme = view.findViewById(R.id.blue_color_theme_card);
        if (colorTheme.equals("blue"))
            blueColorTheme.setCardBackgroundColor(getResources().getColor(android.R.color.black));
        blueColorTheme.setSoundEffectsEnabled(false);
        blueColorTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(ContainerActivity.COLOR_THEME_KEY, "blue");
                //---saves the values---
                editor.commit();

                getActivity().recreate();

                //---display file saved message---
                //Toast.makeText(getActivity(),"Color theme saved successfully to blue!", Toast.LENGTH_LONG).show();
            }
        });

        CardView yellowColorTheme = view.findViewById(R.id.yellow_color_theme_card);
        if (colorTheme.equals("yellow"))
            yellowColorTheme.setCardBackgroundColor(getResources().getColor(android.R.color.black));
        yellowColorTheme.setSoundEffectsEnabled(false);
        yellowColorTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(ContainerActivity.COLOR_THEME_KEY, "yellow");
                //---saves the values---
                editor.commit();

                getActivity().recreate();

                //---display file saved message---
                //Toast.makeText(getActivity(),"Color theme saved successfully to yellow!", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
/*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
*/
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
