package com.example.canihealthyou;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.Image;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.myViewHolder> {

    ArrayList<MedicationItem> medicationData;
    Activity mActivity;
    MedicationFragment medicationFragment;

    AudioManager audioManager;

    public MedicationAdapter(ArrayList<MedicationItem> medicationData, Activity activity, MedicationFragment medicationFragment) {
        this.medicationData = medicationData;
        this.mActivity = activity;
        this.medicationFragment = medicationFragment;
        this.audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.medication_card_item, null, false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, final int position) {
        String photoPath;
        int photoId;

        holder.medicationPhoto.setImageBitmap(null);
        if ((photoPath = medicationData.get(position).getPhotoPath()) != null) {
            File imgFile = new File(photoPath);

            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.medicationPhoto.setImageBitmap(myBitmap);
            }
        }
        else if ((photoId = medicationData.get(position).getPhotoId()) != -1) {
            holder.medicationPhoto.setImageResource(photoId);
        }

        holder.medicationName.setText(medicationData.get(position).getName());
        holder.medicationDescription.setText(medicationData.get(position).getDescription());
        String type = medicationData.get(position).getMedicationType().toString();
        if (type.equals("PILL")) {
            type = mActivity.getResources().getString(R.string.Medication_Pill);
        }
        else if (type.equals("EFFERVESCENT_TABLET")) {
            type = mActivity.getResources().getString(R.string.Medication_Effervescent);
        }
        else if (type.equals("POWDER_PACKET")) {
            type = mActivity.getResources().getString(R.string.Medication_PowderPacket);
        }
        holder.medicationQuantity.setText(String.valueOf(medicationData.get(position).getQuantity()) + " " + type.replace('_', ' ')
            + ((medicationData.get(position).getQuantity() > 1) ? "S":""));

        final ArrayList<Calendar> schedule = medicationData.get(position).getSchedule();
        StringBuilder scheduleString = new StringBuilder();
        for (int i = 0; i < schedule.size(); i++) {
            Calendar c = schedule.get(i);
            if (i < schedule.size() - 1) {
                scheduleString.append(String.format("%02d:%02d, ", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)));
            }
            else {
                scheduleString.append(String.format("%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)));
            }

        }

        holder.medicationSchedule.setText(scheduleString.toString());
        holder.medicationPhoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                SharedPreferences prefs = mActivity.getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.Theme_AppCompat_Light_Dialog);
                builder.setCancelable(true);
                builder.setTitle(mActivity.getResources().getString(R.string.RemoveMedication_Title));
                builder.setIcon(R.drawable.ic_delete_red_36dp);
                builder.setMessage(mActivity.getResources().getString(R.string.RemoveMedication_Message));
                builder.setPositiveButton(R.string.dialog_confirm, null);
                builder.setNegativeButton(R.string.dialog_cancel, null);

                final AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        TypedValue typedValue = new TypedValue();
                        Resources.Theme theme = mActivity.getTheme();
                        theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
                        @ColorInt int color = typedValue.data;
                        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(color);
                        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SharedPreferences prefs = mActivity.getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
                                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                                    view.setHapticFeedbackEnabled(true);
                                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                                }
                                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                                    audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                                }
                                MedicationItem medicationItem = medicationData.remove(position);
                                notifyDataSetChanged();
                                //SharedPreferences prefs = mActivity.getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                Gson gson = new Gson();
                                String json = gson.toJson(medicationData);
                                editor.putString(MedicationFragment.MEDICATIONS_KEY, json);
                                editor.commit();
                                medicationFragment.removeItem(medicationItem);
                                dialog.dismiss();
                            }
                        });
                        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(color);
                        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SharedPreferences prefs = mActivity.getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
                                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                                    view.setHapticFeedbackEnabled(true);
                                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                                }
                                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                                    audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
                                }
                                dialog.dismiss();
                            }
                        });
                    }
                });
                dialog.show();
                return false;
            }
        });
    }




    @Override
    public int getItemCount() {
        return this.medicationData.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        ImageView medicationPhoto;
        TextView medicationName, medicationDescription, medicationQuantity, medicationSchedule;

        public myViewHolder(View itemView) {
            super(itemView);
            medicationPhoto = itemView.findViewById(R.id.image_medication_card);
            medicationName = itemView.findViewById(R.id.medication_name);
            medicationDescription = itemView.findViewById(R.id.medication_description);
            medicationQuantity = itemView.findViewById(R.id.medication_quantity);
            medicationSchedule = itemView.findViewById(R.id.medication_schedule);
        }
    }
}
