package com.example.canihealthyou;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muddzdev.styleabletoast.StyleableToast;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MedicationFragment extends Fragment implements NewMedicationDialog.NewMedicationDialogListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String MEDICATIONS_KEY = "medications_list";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

/***************************************************************************************/
    private ArrayList<MedicationItem> medicationData = new ArrayList<>();
    private FloatingActionButton addMedication, enableNotifications, disableNotifications;
    private RecyclerView recyclerView;
    private MedicationAdapter adapter;
    private RecyclerView.SmoothScroller smoothScroller;
    private int counter;
    private TextView textViewNotifications;
    AudioManager audioManager;
    private boolean openedFABs = false;
/***************************************************************************************/
    public MedicationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences prefs = getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        // Inflate the layout for this fragment
        getActivity().setTitle(getActivity().getResources().getString(R.string.medication));
        View v = inflater.inflate(R.layout.fragment_medication, container, false);

        recyclerView = v.findViewById(R.id.recyclerview_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fillMedicationData();
        adapter = new MedicationAdapter(medicationData, getActivity(), this);
        recyclerView.setAdapter(adapter);
        //recyclerView.setNestedScrollingEnabled(false);

        //SharedPreferences.Editor editor = prefs.edit();
        int max = 0;
        for(MedicationItem m : medicationData) {
            if ((prefs.getInt(m.getName() + "_notification", 0) > max)) {
                max = prefs.getInt(m.getName() + "_notification", 0);
            }
        }
        counter = max;

        smoothScroller = new LinearSmoothScroller(getActivity()) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        /*
        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab.isShown())
                    fab.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    fab.show();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        */

        /*
        FloatingActionsMenu fabMenu = v.findViewById(R.id.fab_menu);
        fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = MedicationFragment.this.getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                openedFABs = !openedFABs;
                if (openedFABs) {
                    addMedication.setVisibility(View.VISIBLE);
                    enableNotifications.setVisibility(View.VISIBLE);
                    disableNotifications.setVisibility(View.VISIBLE);
                }
                else {
                    addMedication.setVisibility(View.GONE);
                    enableNotifications.setVisibility(View.GONE);
                    disableNotifications.setVisibility(View.GONE);
                }
            }
        });
        */
        addMedication = v.findViewById(R.id.add_medication);
        addMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = MedicationFragment.this.getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                openDialog();
            }
        });

        textViewNotifications = v.findViewById(R.id.textview_notifications_enabled);
        if (prefs.getBoolean("notifications_enabled", false)) {
            textViewNotifications.setVisibility(View.VISIBLE);
        }
        else {
            textViewNotifications.setVisibility(View.GONE);
        }

        enableNotifications = v.findViewById(R.id.enable_notifications);
        enableNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = MedicationFragment.this.getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
                }
                //SharedPreferences prefs = MedicationFragment.this.getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
                if (!prefs.getBoolean("notifications_enabled", false)) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("notifications_enabled", true);
                    editor.commit();

                    for (MedicationItem medicationItem : medicationData) {
                        for (Calendar calendar : medicationItem.getSchedule()) {
                            startAlarm(medicationItem, calendar);
                        }
                    }
                    textViewNotifications.setVisibility(View.VISIBLE);
                    StyleableToast.makeText(getActivity(), getActivity().getResources().getString(R.string.Medication_NotificationCreated), R.style.success).show();
                }
                else {
                    StyleableToast.makeText(getActivity(), getActivity().getResources().getString(R.string.Medication_NotificationsAlreadyEnabled), R.style.error).show();
                }
            }
        });

        disableNotifications = v.findViewById(R.id.disable_notifications);
        disableNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = MedicationFragment.this.getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                }
                //SharedPreferences prefs = MedicationFragment.this.getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
                if (prefs.getBoolean("notifications_enabled", false)) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("notifications_enabled", false);
                    editor.commit();

                    for (MedicationItem medicationItem : medicationData) {
                        for (Calendar calendar : medicationItem.getSchedule()) {
                            stopAlarm(medicationItem, calendar);
                        }
                    }
                    textViewNotifications.setVisibility(View.GONE);
                    StyleableToast.makeText(getActivity(), getActivity().getResources().getString(R.string.Medication_NotificationRemoved), R.style.error).show();
                }
                else {
                    StyleableToast.makeText(getActivity(), getActivity().getResources().getString(R.string.Medication_NotificationsAlreadyDisabled), R.style.error).show();
                }
            }
        });

        return v;
    }

    public void openDialog() {
        NewMedicationDialog newMedicationDialog = new NewMedicationDialog();
        newMedicationDialog.setTargetFragment(this, 0);
        newMedicationDialog.show(getFragmentManager(), "NEW_MEDICATION_DIALOG");
    }

    private void fillMedicationData() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 20);
        c.set(Calendar.MINUTE, 00);
        ArrayList<Calendar> a = new ArrayList<>();
        a.add(c);
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.HOUR_OF_DAY, 10);
        c1.set(Calendar.MINUTE, 20);
        ArrayList<Calendar> b = new ArrayList<>();
        b.add(c1);
        ArrayList<MedicationItem> defaultList = new ArrayList<>();
        defaultList.add(new MedicationItem(getActivity().getResources().getString(R.string.ibuprofeno_name), getActivity().getResources().getString(R.string.ibuprofeno_description), MedicationType.PILL, 1, a, R.raw.ibuprofeno));
        defaultList.add(new MedicationItem(getActivity().getResources().getString(R.string.paracetamol_name), getActivity().getResources().getString(R.string.paracetamol_description), MedicationType.PILL, 2, b, R.raw.paracetamol));
        defaultList.add(new MedicationItem(getActivity().getResources().getString(R.string.aspirin_name), getActivity().getResources().getString(R.string.aspirin_description), MedicationType.POWDER_PACKET, 1, b, R.raw.aspirina));
        /*Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.HOUR_OF_DAY, 17);
        c1.set(Calendar.MINUTE, 00);
        ArrayList<Calendar> a1 = new ArrayList<>();
        a1.add(c1);
        defaultList.add(new MedicationItem(getActivity().getResources().getString(R.string.ibuprofeno_name), getActivity().getResources().getString(R.string.ibuprofeno_description), MedicationType.PILL, 1, a1, R.raw.ibuprofeno));
        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.HOUR_OF_DAY, 23);
        c2.set(Calendar.MINUTE, 00);
        ArrayList<Calendar> a2 = new ArrayList<>();
        a2.add(c2);
        defaultList.add(new MedicationItem(getActivity().getResources().getString(R.string.ibuprofeno_name), getActivity().getResources().getString(R.string.ibuprofeno_description), MedicationType.PILL, 1, a2, R.raw.ibuprofeno));*/

        SharedPreferences prefs = getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(MedicationFragment.MEDICATIONS_KEY, gson.toJson(defaultList));
        Type type = new TypeToken<ArrayList<MedicationItem>>() {}.getType();
        medicationData = gson.fromJson(json, type);
        if (medicationData.isEmpty()) {
            medicationData = new ArrayList<>(defaultList);
        }

    }

    @Override
    public void addNewMedication(String name, String description, String photoPath, String quantity, MedicationType type, ArrayList<Calendar> schedule) {
        boolean alreadyExists = false;
        for (MedicationItem m : medicationData) {
            if (m.getName().equals(name)) {
                alreadyExists = true;
            }
        }
        if (!alreadyExists) {
            MedicationItem newMedItem = null;
            if (photoPath == null) {
                newMedItem = new MedicationItem(name, description, type, Integer.parseInt(quantity), schedule);
            }
            else {
                newMedItem = new MedicationItem(name, description, type, Integer.parseInt(quantity), schedule, photoPath);
            }

            medicationData.add(0, newMedItem);
            adapter.notifyItemInserted(0);

            SharedPreferences prefs = getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(medicationData);
            editor.putString(MedicationFragment.MEDICATIONS_KEY, json);
            editor.putInt(newMedItem.getName() + "_notification", ++counter);
            editor.commit();

            if(prefs.getBoolean("notifications_enabled", false)) {
                for (Calendar calendar : newMedItem.getSchedule()) {
                    startAlarm(newMedItem, calendar);
                }
            }

            smoothScroller.setTargetPosition(0);
            recyclerView.getLayoutManager().startSmoothScroll(smoothScroller);
        }
        else {
            StyleableToast.makeText(getActivity(), getActivity().getResources().getString(R.string.Medication_ErrorMessage), R.style.error).show();
        }

        /*StringBuilder scheduleString = new StringBuilder();
        for (int i = 0; i < schedule.size(); i++) {
            Calendar c = schedule.get(i);
            if (i < schedule.size() - 1) {
                scheduleString.append(String.format("%02d:%02d, ", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)));
            }
            else {
                scheduleString.append(String.format("%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)));
            }
        }
        Toast.makeText(getActivity(), name + "\n" + description + "\n" + photoPath + "\n" + quantity + "\n" + type.toString() + "\n"
                + scheduleString.toString(), Toast.LENGTH_LONG).show();*/
    }


    private void stopAlarm(MedicationItem medicationItem, Calendar calendar) {
        AlarmManager manager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;
        PendingIntent pendingIntent;

        /*
        // SET TIME HERE
        Calendar calendar= Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,15);
        calendar.set(Calendar.MINUTE,20);
        */

        myIntent = new Intent(getActivity(), AlarmNotificationReceiver.class);
        StringBuilder s = new StringBuilder();
        s.append(getActivity().getResources().getString(R.string.Medication_notification_take) + " ");
        s.append(medicationItem.getQuantity() + " ");
        switch (medicationItem.getMedicationType().toString()) {
            case "PILL":
                s.append(getActivity().getResources().getString(R.string.Medication_Pill).toLowerCase());
                break;
            case "EFFERVESCENT_TABLET":
                s.append(getActivity().getResources().getString(R.string.Medication_Effervescent).toLowerCase());
                break;
            case "POWDER_PACKET":
                s.append(getActivity().getResources().getString(R.string.Medication_PowderPacket).toLowerCase());
                break;
        }
        s.append((medicationItem.getQuantity() > 1 ? "s":"") + " " + getActivity().getResources().getString(R.string.of) + " " + medicationItem.getName());
        //SimpleDateFormat fmtOut = new SimpleDateFormat("HH:mm");
        //String s1 = fmtOut.format(new Date(calendar.getTimeInMillis()));
        String s1 = String.format(new Locale(getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE).getString(ContainerActivity.LANGUAGE_KEY, Locale.getDefault().getLanguage())), "%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE));
        s.append(" " + getActivity().getResources().getString(R.string.at) + " " + s1);

        SharedPreferences prefs = getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        myIntent.putExtra("medication", s.toString());
        myIntent.putExtra("counter", prefs.getInt(medicationItem.getName() + "_notification", ++counter));
        pendingIntent = PendingIntent.getBroadcast(getActivity(), prefs.getInt(medicationItem.getName() + "_notification", ++counter),myIntent,0);

        manager.cancel(pendingIntent);
        Log.e("hora y minuto", calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));

    }


    private void startAlarm(MedicationItem medicationItem, Calendar calendar) {
        AlarmManager manager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;
        PendingIntent pendingIntent;

        /*
        // SET TIME HERE
        Calendar calendar= Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,15);
        calendar.set(Calendar.MINUTE,20);
        */

        myIntent = new Intent(getActivity(), AlarmNotificationReceiver.class);
        StringBuilder s = new StringBuilder();
        s.append(getActivity().getResources().getString(R.string.Medication_notification_take) + " ");
        s.append(medicationItem.getQuantity() + " ");
        switch (medicationItem.getMedicationType().toString()) {
            case "PILL":
                s.append(getActivity().getResources().getString(R.string.Medication_Pill).toLowerCase());
                break;
            case "EFFERVESCENT_TABLET":
                s.append(getActivity().getResources().getString(R.string.Medication_Effervescent).toLowerCase());
                break;
            case "POWDER_PACKET":
                s.append(getActivity().getResources().getString(R.string.Medication_PowderPacket).toLowerCase());
                break;
        }
        s.append((medicationItem.getQuantity() > 1 ? "s":"") + " " + getActivity().getResources().getString(R.string.of) + " " + medicationItem.getName());
        //SimpleDateFormat fmtOut = new SimpleDateFormat("HH:mm");
        //String s1 = fmtOut.format(new Date(calendar.getTimeInMillis()));
        String s1 = String.format(new Locale(getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE).getString(ContainerActivity.LANGUAGE_KEY, Locale.getDefault().getLanguage())), "%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE));
        s.append(" " + getActivity().getResources().getString(R.string.at) + " " + s1);

        myIntent.putExtra("medication", s.toString());

        SharedPreferences prefs = getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        myIntent.putExtra("counter", prefs.getInt(medicationItem.getName() + "_notification", ++counter));
        pendingIntent = PendingIntent.getBroadcast(getActivity(),prefs.getInt(medicationItem.getName() + "_notification", ++counter),myIntent,0);

        //if(!isRepeat)
        //    manager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime()+3000, pendingIntent);
        //else
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        Log.e("hora y minuto", calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));

    }

    public void removeItem(MedicationItem medicationItem) {
        for (Calendar c : medicationItem.getSchedule()) {
            stopAlarm(medicationItem, c);
        }
    }
}
