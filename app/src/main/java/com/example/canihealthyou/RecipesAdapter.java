package com.example.canihealthyou;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.util.Base64;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.muddzdev.styleabletoast.StyleableToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.myViewHolder> implements Filterable {

    ArrayList<RecipeItem> recipesData;
    ArrayList<RecipeItem> recipesDataFull;

    RecipeItem mRecentlyDeletedItem;
    int mRecentlyDeletedItemPosition;
    Activity mActivity;

    SharedPreferences prefs;
    AudioManager audioManager;

    public RecipesAdapter(ArrayList<RecipeItem> recipesData, Activity activity) {
        this.recipesData = recipesData;
        this.mActivity = activity;
        this.recipesDataFull = new ArrayList<>(recipesData);
        this.audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        this.prefs = activity.getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //View v = inflater.inflate(R.layout.calendar_card_item, null, false);
        View _v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_card_item, parent, false );
        return new myViewHolder(_v);
    }

    @Override
    public void onBindViewHolder(final RecipesAdapter.myViewHolder holder, final int position) {
        String picturePath;
        int pictureId;

        holder.recipePicture.setImageBitmap(null);
        /*if (recipesData.get(position).getEncondedPicture() != null) {
            holder.recipePicture.setImageBitmap(getBitmapFromString(recipesData.get(position).getEncondedPicture()));
        }
        else*/ if ((picturePath = recipesData.get(position).getPicturePath()) != null) {
            File imgFile = new File(picturePath);

            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.recipePicture.setImageBitmap(myBitmap);
            }
        }
        else if ((pictureId = recipesData.get(position).getPictureId()) != -1) {
            holder.recipePicture.setImageResource(pictureId);
        }
        holder.recipePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
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
                Intent ttsActivity = new Intent(mActivity, TTSActivity.class);
                ttsActivity.putExtra("RecipeToLoad", holder.recipeName.getText().toString());
                mActivity.startActivity(ttsActivity);
            }
        });
        holder.recipeName.setText(recipesData.get(position).getName());
        holder.recipeApproxTime.setText(mActivity.getResources().getString(R.string.Recipes_ApproxTime) + " " + recipesData.get(position).getApproxTime());

        holder.recipeListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
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
                Intent ttsActivity = new Intent(mActivity, TTSActivity.class);
                ttsActivity.putExtra("RecipeToLoad", holder.recipeName.getText().toString());
                mActivity.startActivity(ttsActivity);
            }
        });

        /*
        holder.qrExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
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
                //StyleableToast.makeText(mActivity, "EXPORT RECIPE WITH QR CODE", R.style.success).show();
                Intent qrExportActivity = new Intent(mActivity, QRExport.class);
                qrExportActivity.putExtra("recipe", holder.recipeName.getText().toString());
                mActivity.startActivity(qrExportActivity);
            }
        });
        */

    }

    /*
     * This functions converts Bitmap picture to a string which can be
     * JSONified.
     * */
    private String getStringFromBitmap(Bitmap bitmapPicture) {
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    /*
     * This Function converts the String back to Bitmap
     * */
    private Bitmap getBitmapFromString(String stringPicture) {
        byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    @Override
    public int getItemCount() {
        return recipesData.size();
    }

    @Override
    public Filter getFilter() {
        return recipesFilter;
    }

    private Filter recipesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<RecipeItem> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(recipesDataFull);
            }
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (RecipeItem item : recipesDataFull) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            recipesData.clear();
            recipesData.addAll((List)filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class myViewHolder extends RecyclerView.ViewHolder {
        ImageView recipePicture;
        TextView recipeName, recipeApproxTime;
        LinearLayout recipeListItem;
        //ImageView qrExport;

        public myViewHolder(View itemView) {
            super(itemView);
            recipePicture = itemView.findViewById(R.id.recipe_item_picture);
            recipeName = itemView.findViewById(R.id.recipe_item_name);
            recipeApproxTime = itemView.findViewById(R.id.recipe_item_approximate_time);
            recipeListItem = itemView.findViewById(R.id.recipe_list_item);
            //qrExport = itemView.findViewById(R.id.export_recipe_qr);
        }
    }

    public void deleteItem(int position) {
        mRecentlyDeletedItem = recipesData.get(position);
        mRecentlyDeletedItemPosition = position;
        recipesData.remove(position);
        notifyItemRemoved(position);

        SharedPreferences prefs = mActivity.getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(recipesData);
        editor.putString(RecipesFragment.recipesKey, json);
        editor.commit();

        showUndoSnackbar();
    }

    private void showUndoSnackbar() {
        View view = mActivity.findViewById(R.id.fragment_container);
        Snackbar snackbar = Snackbar.make(view, R.string.Recipes_SnackbarText,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.Recipes_SnackbarUndo, new View.OnClickListener() {
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
        recipesData.add(mRecentlyDeletedItemPosition,
                mRecentlyDeletedItem);
        notifyItemInserted(mRecentlyDeletedItemPosition);
        SharedPreferences prefs = mActivity.getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(recipesData);
        editor.putString(RecipesFragment.recipesKey, json);
        editor.commit();
    }

    public void moveItem(int positionDragged, int positionTarget) {
        Collections.swap(recipesData, positionDragged, positionTarget);
        notifyItemMoved(positionDragged, positionTarget);
        SharedPreferences prefs = mActivity.getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(recipesData);
        editor.putString(RecipesFragment.recipesKey, json);
        editor.commit();
    }
}
