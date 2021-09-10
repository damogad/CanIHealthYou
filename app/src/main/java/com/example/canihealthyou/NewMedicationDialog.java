package com.example.canihealthyou;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
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

public class NewMedicationDialog extends AppCompatDialogFragment implements TimePickerFragment.OnScheduleDialogListener {

    private TextInputEditText editTextMedicationName;
    private TextInputEditText editTextMedicationDescription;
    private TextInputLayout layoutMedicationName;
    private Spinner medicationQuantitySpinner;
    private RadioGroup rGroup;
    private CardView camera;
    private CardView schedule;
    private TextView scheduleText;
    private ArrayList<Calendar> scheduleHoursCalendar;
    private ImageView takenPhoto;
    String currentPhotoPath = null;
    private CardView removeSchedule;

    SharedPreferences prefs;
    AudioManager audioManager;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.new_medication_layout, null);
        builder.setView(view).setTitle(getActivity().getResources().getString(R.string.NewMedication_Title)).setNegativeButton(getActivity().getResources().getString(R.string.dialog_cancel), null).setPositiveButton(getActivity().getResources().getString(R.string.dialog_add), null);

        prefs = getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        editTextMedicationName = view.findViewById(R.id.medication_name_edit_text);
        editTextMedicationName.setOnClickListener(new View.OnClickListener() {
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
        editTextMedicationName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (!editTextMedicationName.getText().toString().isEmpty()) {
                   layoutMedicationName.setError(null); //Clear the error
                }
                return false;
            }
        });
        editTextMedicationDescription = view.findViewById(R.id.medication_description_edit_text);
        editTextMedicationDescription.setOnClickListener(new View.OnClickListener() {
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
        layoutMedicationName = view.findViewById(R.id.medication_name_textinputlayout);

        camera = view.findViewById(R.id.take_medication_photo);
        camera.setOnClickListener(new View.OnClickListener() {
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

        medicationQuantitySpinner = view.findViewById(R.id.medication_quantity_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.numbers, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medicationQuantitySpinner.setAdapter(adapter);
        medicationQuantitySpinner.setSelection(0);
        medicationQuantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        // This will get the radiogroup
        rGroup = (RadioGroup)view.findViewById(R.id.medication_quantity_radio_group);
        rGroup.check(R.id.medication_quantity_pill);
        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    radioGroup.setHapticFeedbackEnabled(true);
                    radioGroup.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                switch (i) {
                    case R.id.medication_quantity_pill:
                        break;
                    case R.id.medication_quantity_effervescent:
                        break;
                    case R.id.medication_quantity_powder:
                        break;
                }
            }
        });

        scheduleHoursCalendar = new ArrayList<>();
        scheduleText = view.findViewById(R.id.medication_schedule_text);
        schedule = view.findViewById(R.id.take_medication_schedule);
        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.setTargetFragment(NewMedicationDialog.this, 0);
                timePicker.show(getFragmentManager(), "TIME_PICKER");
            }
        });

        takenPhoto = view.findViewById(R.id.added_medication_photo);

        removeSchedule = view.findViewById(R.id.remove_medication_schedule);
        removeSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!scheduleHoursCalendar.isEmpty()) {
                    if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                        view.setHapticFeedbackEnabled(true);
                        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                    }
                    if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                        audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                    }
                    StringBuilder s = new StringBuilder();
                    scheduleHoursCalendar.remove(scheduleHoursCalendar.size()-1);
                    for (int i = 0; i < scheduleHoursCalendar.size(); i++) {
                        Calendar c = scheduleHoursCalendar.get(i);
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);
                        s.append(String.format("%02d:%02d", hour, minute) + ((i == (scheduleHoursCalendar.size()-1)) ? "":", "));
                    }
                    scheduleText.setText(s.toString());
                }

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

                        if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                            view.setHapticFeedbackEnabled(true);
                            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                        }
                        String name = editTextMedicationName.getText().toString();
                        String description = editTextMedicationDescription.getText().toString();
                        String quantity = medicationQuantitySpinner.getSelectedItem().toString();
                        MedicationType type = null;
                        int radioButtonId = rGroup.getCheckedRadioButtonId();
                        switch (radioButtonId) {
                            case R.id.medication_quantity_pill:
                                type = MedicationType.PILL;
                                break;
                            case R.id.medication_quantity_effervescent:
                                type = MedicationType.EFFERVESCENT_TABLET;
                                break;
                            case R.id.medication_quantity_powder:
                                type = MedicationType.POWDER_PACKET;
                                break;
                        }

                        if (name.isEmpty()) {
                            layoutMedicationName.setError(getActivity().getResources().getString(R.string.NewMedication_NameMandatory));
                            if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                            }
                        }
                        else if (scheduleHoursCalendar.isEmpty()) {
                            StyleableToast.makeText(getActivity(), getActivity().getResources().getString(R.string.NewMedication_ErrorSchedule), R.style.error).show();
                            if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                            }
                        }
                        else {
                            if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
                            }
                            ((MedicationFragment)getTargetFragment()).addNewMedication(name, description, currentPhotoPath, quantity, type, scheduleHoursCalendar);
                            dialog.dismiss();
                        }
                        //Dismiss once everything is OK.
                    }
                });
                Button buttonCancel = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setElevation(4);
        }*/
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        boolean alreadyExists = false;
        for (Calendar calendar : scheduleHoursCalendar) {
            if ((calendar.get(Calendar.HOUR_OF_DAY) == hourOfDay) && (calendar.get(Calendar.MINUTE) == minute)) {
                alreadyExists = true;
            }
        }

        if (!alreadyExists) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
            c.set(Calendar.MINUTE, minute);
            if (scheduleHoursCalendar.isEmpty()) {
                scheduleText.setText(String.format("%02d:%02d", hourOfDay, minute));
            }
            else {
                scheduleText.setText(scheduleText.getText().toString() + String.format(", %02d:%02d", hourOfDay, minute));
            }
            scheduleHoursCalendar.add(c);
        }
        else {
            StyleableToast.makeText(getActivity(), getActivity().getResources().getString(R.string.NewMedication_RepeatedHour), R.style.error).show();
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("NewMedicationDialog", "Error while creating image file");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            File imgFile = new File(currentPhotoPath);
            if (imgFile.exists()) {
                takenPhoto.setVisibility(View.VISIBLE);
            }
        }
        else {
             currentPhotoPath = null;
        }
    }

    public interface NewMedicationDialogListener {
        void addNewMedication(String name, String description, String photoPath, String quantity, MedicationType type, ArrayList<Calendar> schedule);
    }

}
