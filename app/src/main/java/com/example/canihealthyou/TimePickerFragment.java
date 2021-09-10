package com.example.canihealthyou;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.HapticFeedbackConstants;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public interface OnScheduleDialogListener {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute);
    }

    OnScheduleDialogListener mListener;
/*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnScheduleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnScheduleDialogListener");
        }
    }*/

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), this,
                hour, minute, DateFormat.is24HourFormat(getContext()));
        timePickerDialog.setTitle(null);
        return timePickerDialog;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        SharedPreferences prefs = getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
            timePicker.setHapticFeedbackEnabled(true);
            timePicker.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
        }
        if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
            audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
        }
        ((OnScheduleDialogListener)getTargetFragment()).onTimeSet(timePicker, hourOfDay, minute);
    }
}
