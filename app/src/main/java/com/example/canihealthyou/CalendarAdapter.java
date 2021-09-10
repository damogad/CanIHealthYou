package com.example.canihealthyou;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.Image;
import android.util.Log;
import android.view.DragEvent;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.myViewHolder> {

    ArrayList<CalendarItem> calendarData;
    CalendarItem mRecentlyDeletedItem;
    int mRecentlyDeletedItemPosition;
    Activity mActivity;
    Date currentDate;
    CalendarFragment calendarFragment;


    public CalendarAdapter(ArrayList<CalendarItem> calendarData, Activity activity, Date currentDate, CalendarFragment calendarFragment) {
        this.calendarData = calendarData;
        this.mActivity = activity;
        this.currentDate = currentDate;
        this.calendarFragment = calendarFragment;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //View v = inflater.inflate(R.layout.calendar_card_item, null, false);
        View _v = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_card_item, parent, false );
        return new myViewHolder(_v);
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        holder.mealTitle.setText(calendarData.get(position).getTitle());
        holder.mealDescription.setText(calendarData.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return this.calendarData.size();
    }


    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView mealTitle, mealDescription;

        public myViewHolder(View itemView) {
            super(itemView);
            mealTitle = itemView.findViewById(R.id.meal_title);
            mealDescription = itemView.findViewById(R.id.meal_description);
        }
    }

    public void editItem(int position) {
        calendarFragment.editElement(position);
    }

    public void deleteItem(int position) {

        try {
            mRecentlyDeletedItem = calendarData.get(position);
            mRecentlyDeletedItemPosition = position;
            calendarData.remove(position);
            notifyItemRemoved(position);
            /*
            SharedPreferences prefs = mActivity.getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
            Set<String> s = prefs.getStringSet(formatter.format(currentDate), null);
            ArrayList<String> values = new ArrayList<>(s);
            values.remove(mRecentlyDeletedItemPosition);
            editor.putStringSet(formatter.format(currentDate), new HashSet<String>(values));
            editor.commit();
            */
            SharedPreferences prefs = mActivity.getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(calendarData);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
            editor.putString(formatter.format(currentDate), json);
            editor.commit();

            showUndoSnackbar();
        } catch (IndexOutOfBoundsException e) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
            Log.e("DIA", formatter.format(currentDate));
        }

    }

    private void showUndoSnackbar() {
        View view = mActivity.findViewById(R.id.fragment_container);
        Snackbar snackbar = Snackbar.make(view, R.string.Calendar_SnackbarText,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.Calendar_SnackbarUndo, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = mActivity.getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    AudioManager audioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                undoDelete();
            }
        });
        snackbar.show();
    }

    private void undoDelete() {
        calendarData.add(mRecentlyDeletedItemPosition,
                mRecentlyDeletedItem);
        notifyItemInserted(mRecentlyDeletedItemPosition);
        /*
        SharedPreferences prefs = mActivity.getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
        Set<String> s = prefs.getStringSet(formatter.format(currentDate), null);
        ArrayList<String> values;
        if (s != null) {
            values = new ArrayList<>(s);
        }
        else {
            values = new ArrayList<>();
        }
        values.add(mRecentlyDeletedItem.getTitle() + "_" + mRecentlyDeletedItem.getDescription());
        editor.putStringSet(formatter.format(currentDate), new HashSet<String>(values));
        */
        SharedPreferences prefs = mActivity.getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(calendarData);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
        editor.putString(formatter.format(currentDate), json);
        editor.commit();
    }

    public void moveItem(int positionDragged, int positionTarget) {
        Collections.swap(calendarData, positionDragged, positionTarget);
        notifyItemMoved(positionDragged, positionTarget);
        SharedPreferences prefs = mActivity.getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(calendarData);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
        editor.putString(formatter.format(currentDate), json);
        editor.commit();
    }
}