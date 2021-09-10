package com.example.canihealthyou;


import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muddzdev.styleabletoast.StyleableToast;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment implements NewMealDialog.NewMealDialogListener {

    private ArrayList<CalendarItem> calendarData = new ArrayList<>();
    ArrayList<String> values = new ArrayList<>();

    private Button addButton;
    private RecyclerView recyclerView;
    private CalendarAdapter adapter;
    private CalendarView calendarView;
    private RecyclerView.SmoothScroller smoothScroller;
    private SharedPreferences prefs;
    private SwipeToDeleteCallback swipeToDeleteCallback;
    private int lasti, lasti1, lasti2;

    public NewMealDialog newMealDialog;

    public CalendarFragment() {
        // Required empty public constructor
    }

    AudioManager audioManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        prefs = getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        recyclerView = v.findViewById(R.id.calendar_recyclerview_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fillCalendarData();
        calendarView = v.findViewById(R.id.calendarview);
        adapter = new CalendarAdapter(calendarData, getActivity(), new Date(calendarView.getDate()), this);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        lasti = Integer.parseInt(formatter.format(new Date(calendarView.getDate())));
        formatter = new SimpleDateFormat("MM");
        lasti1 = Integer.parseInt(formatter.format(new Date(calendarView.getDate()))) - 1;
        formatter = new SimpleDateFormat("dd");
        lasti2 = Integer.parseInt(formatter.format(new Date(calendarView.getDate())));

        recyclerView.setAdapter(adapter);
        /* SWIPING */
        swipeToDeleteCallback = new SwipeToDeleteCallback(adapter, getActivity());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        smoothScroller = new LinearSmoothScroller(getActivity()) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

        addButton = v.findViewById(R.id.calendar_addbutton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }

                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                openDialog();
            }
        });


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            // i: year, i1: month, i2: day
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                if ((i != lasti) || (i1 != lasti1) || (i2 != lasti2)) {
                    if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                        calendarView.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                    }
                    if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                        audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                    }
                    Gson gson = new Gson();
                    String json = prefs.getString(String.valueOf(i) + "_" + ((i1+1) >= 10 ? String.valueOf(i1+1):"0"+String.valueOf(i1+1)) + "_" + (i2 >= 10 ? String.valueOf(i2):"0"+String.valueOf(i2)), null);
                    //Log.e("json", json);
                    Type type = new TypeToken<ArrayList<CalendarItem>>() {}.getType();
                    calendarData = gson.fromJson(json, type);
                    if (calendarData == null) {
                        calendarData = new ArrayList<>();
                    }
                    adapter = new CalendarAdapter(calendarData, getActivity(), new Date(calendarView.getDate()), CalendarFragment.this);
                    recyclerView.swapAdapter(adapter, false);
                    swipeToDeleteCallback.mAdapter = adapter;
                    lasti = i;
                    lasti1 = i1;
                    lasti2 = i2;
                }

            }
        });



        return v;
    }

    public void openDialog() {
        newMealDialog = new NewMealDialog(-1,null, null);
        newMealDialog.setTargetFragment(this, 0);
        newMealDialog.show(getFragmentManager(), "NEW_MEAL_DIALOG");
    }


    private void fillCalendarData() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
        Date date = new Date();
        /*
        Set<String> fetch = prefs.getStringSet(formatter.format(date), null);
        if (fetch != null) {
            ArrayList<String> values = new ArrayList<>(fetch);
            for (int j = 0; j < values.size(); j++) {
                String s = values.get(j);
                int indexSeparation = s.indexOf('_');
                if (indexSeparation != -1) {
                    calendarData.add(new CalendarItem(s.substring(0, indexSeparation), s.substring(indexSeparation + 1)));
                }
            }
        }
        */
        Gson gson = new Gson();
        String json = prefs.getString(formatter.format(date), null);
        Type type = new TypeToken<ArrayList<CalendarItem>>() {}.getType();
        calendarData = gson.fromJson(json, type);
        if (calendarData == null) {
            calendarData = new ArrayList<>();
        }

    }


    @Override
    public void addNewMeal(String title, String description) {
        SharedPreferences.Editor editor = prefs.edit();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
        Date date = new Date(calendarView.getDate());
        /*
        Set<String> s = prefs.getStringSet(formatter.format(date), null);
        if (s != null) {
            values = new ArrayList<>(s);
        }
        else {
            values = new ArrayList<>();
        }
        */
        boolean alreadyExists = false;
        for (CalendarItem item : this.calendarData) {
            if (item.getTitle().equals(title)) {
                alreadyExists = true;
            }
        }

        if (!alreadyExists) {
            calendarData.add(new CalendarItem(title, description));
            adapter.notifyItemInserted(calendarData.size()-1);
            Gson gson = new Gson();
            editor.putString(formatter.format(date), gson.toJson(calendarData));
            editor.commit();
            smoothScroller.setTargetPosition(calendarData.size()-1);
            recyclerView.getLayoutManager().startSmoothScroll(smoothScroller);
        }
        else {
            StyleableToast.makeText(getActivity(), getActivity().getResources().getString(R.string.Calendar_Error), R.style.error).show();
        }

    }

    @Override
    public void editMeal(int position, String description) {
        calendarData.get(position).setDescription(description);
        adapter.notifyItemChanged(position);
        SharedPreferences.Editor editor = prefs.edit();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
        Date date = new Date(calendarView.getDate());
        Gson gson = new Gson();
        editor.putString(formatter.format(date), gson.toJson(calendarData));
        editor.commit();
    }

    public void editElement(int position) {
        NewMealDialog newMealDialog = new NewMealDialog(position, calendarData.get(position).getTitle(), calendarData.get(position).getDescription());
        newMealDialog.setTargetFragment(this, 0);
        newMealDialog.show(getFragmentManager(), "NEW_MEAL_DIALOG");
    }
}
