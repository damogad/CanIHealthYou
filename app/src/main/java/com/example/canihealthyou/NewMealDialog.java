package com.example.canihealthyou;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.muddzdev.styleabletoast.StyleableToast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class NewMealDialog extends AppCompatDialogFragment implements SelectRecipeDialog.SelectRecipeDialogListener {
    public TextInputEditText editTextMealDescription;
    public TextInputLayout layoutMealDescription;
    public Spinner mealTypeSpinner;
    public Button selectMealButton;
    public Button positiveButton;
    public Button negativeButton;
    private String title, description;
    private int position;
    SharedPreferences prefs;
    AudioManager audioManager;

    public NewMealDialog(int position, String title, String description) {
        super();
        // title or description == null --> CREATE, else --> EDIT
        this.position = position;
        this.title = title;
        this.description = description;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.new_meal_layout, null);
        if (position == -1)
            builder.setView(view).setTitle(getActivity().getResources().getString(R.string.NewMeal_Title)).setNegativeButton(getActivity().getResources().getString(R.string.dialog_cancel), null).setPositiveButton(getActivity().getResources().getString(R.string.dialog_add), null);
        else
            builder.setView(view).setTitle(getActivity().getResources().getString(R.string.NewMeal_TitleEdit)).setNegativeButton(getActivity().getResources().getString(R.string.dialog_cancel), null).setPositiveButton(getActivity().getResources().getString(R.string.dialog_confirm), null);

        prefs = getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        mealTypeSpinner = view.findViewById(R.id.meal_spinner);
        if (position != -1) {
            mealTypeSpinner.setEnabled(false);
            mealTypeSpinner.setClickable(false);
        }
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.meal_type, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypeSpinner.setAdapter(adapter);
        if (title == null) {
            mealTypeSpinner.setSelection(0);
        }
        else {
            mealTypeSpinner.setSelection(((ArrayAdapter<CharSequence>)mealTypeSpinner.getAdapter()).getPosition(title));
        }

        mealTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //quantity = adapterView.getItemAtPosition(i).toString();
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        layoutMealDescription = view.findViewById(R.id.meal_description_layout);
        layoutMealDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
            }
        });
        editTextMealDescription = view.findViewById(R.id.meal_description_edit_text);
        if (description != null) {
            editTextMealDescription.setText(description);
        }
        editTextMealDescription.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (!editTextMealDescription.getText().toString().isEmpty()) {
                    layoutMealDescription.setError(null); //Clear the error
                }
                return false;
            }
        });
        editTextMealDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
            }
        });

        selectMealButton = view.findViewById(R.id.select_recipe_button);
        selectMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                SelectRecipeDialog selectRecipeDialog = new SelectRecipeDialog();
                selectRecipeDialog.setTargetFragment(NewMealDialog.this, 0);
                selectRecipeDialog.show(getFragmentManager(), "SELECT_RECIPE");
            }
        });


        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                            view.setHapticFeedbackEnabled(true);
                            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                        }

                        // TODO Do something
                        String description = editTextMealDescription.getText().toString();
                        if (description.isEmpty()) {
                            layoutMealDescription.setError(getActivity().getResources().getString(R.string.NewMeal_ErrorDescription));
                            if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                            }
                        }
                        else if ((position != -1) && description.equals(NewMealDialog.this.description)) {
                            layoutMealDescription.setError(getActivity().getResources().getString(R.string.NewMeal_ErrorDescriptionNoChange));
                            if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                            }
                        }
                        else if (mealTypeSpinner.getSelectedItem().toString().isEmpty()) {
                            if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                            }
                            StyleableToast.makeText(getActivity(), getActivity().getResources().getString(R.string.NewMeal_ErrorDescription), R.style.error).show();
                        }
                        else {
                            if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
                            }
                            if ((title == null) || (description == null)) {
                                ((CalendarFragment)getTargetFragment()).addNewMeal(mealTypeSpinner.getSelectedItem().toString(), description);
                            }
                            else {
                                ((CalendarFragment)getTargetFragment()).editMeal(position, description);
                            }

                            dialog.dismiss();
                        }
                        //Dismiss once everything is OK.
                    }
                });
                negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                            view.setHapticFeedbackEnabled(true);
                            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                        }
                        if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                            audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                        }
                        if (position != -1) {
                            ((CalendarFragment)getTargetFragment()).editMeal(position, NewMealDialog.this.description);
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

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setElevation(4);
        }*/
    }

    @Override
    public void selectedRecipe(String title) {
        if (editTextMealDescription.getText().toString().isEmpty()) {
            editTextMealDescription.setText(title);
        }
        else {
            editTextMealDescription.setText(editTextMealDescription.getText().toString() + "\n" + title);
        }
    }


    public interface NewMealDialogListener {
        void addNewMeal(String title, String description);
        void editMeal(int position, String description);
    }
}
