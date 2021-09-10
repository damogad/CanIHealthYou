package com.example.canihealthyou;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.myViewHolder> {

    private Activity mActivity;
    private ArrayList<IngredientItem> ingredientsData;
    int counterCheckedIngredients = 0;

    public IngredientsAdapter(ArrayList<IngredientItem> ingredientsData, Activity activity) {
        this.ingredientsData = ingredientsData;
        this.mActivity = activity;
    }

    @Override
    public IngredientsAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View _v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_card_item, parent, false );
        return new IngredientsAdapter.myViewHolder(_v);
    }

    @Override
    public void onBindViewHolder(IngredientsAdapter.myViewHolder holder, int position) {
        IngredientItem ingredientItem = ingredientsData.get(position);

        holder.ingredientName.setText(ingredientItem.getName());

        StringBuilder s = new StringBuilder();
        s.append(String.valueOf(ingredientItem.getQuantity()) + " ");
        String quantityType = null;
        switch (ingredientItem.getQuantityType().toString()) {
            case "GRAMS":
                quantityType = (ingredientItem.getQuantity() == 1) ? mActivity.getResources().getString(R.string.Ingredient_Gram) : mActivity.getResources().getString(R.string.Ingredient_Grams);
                break;
            case "UNITS":
                quantityType = (ingredientItem.getQuantity() == 1) ? mActivity.getResources().getString(R.string.Ingredient_Unit) : mActivity.getResources().getString(R.string.Ingredient_Units);
                break;
            case "MILLILITERS":
                quantityType = (ingredientItem.getQuantity() == 1) ? mActivity.getResources().getString(R.string.Ingredient_Milliliter) : mActivity.getResources().getString(R.string.Ingredient_Milliliters);
                break;
        }
        s.append(quantityType);
        holder.ingredientQuantity.setText(s.toString());

        holder.ingredientCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences prefs = mActivity.getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    compoundButton.setHapticFeedbackEnabled(true);
                    compoundButton.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    AudioManager audioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                if (b)
                    counterCheckedIngredients++;
                else
                    counterCheckedIngredients--;
            }
        });
    }


    @Override
    public int getItemCount() {
        return this.ingredientsData.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView ingredientName, ingredientQuantity;
        CheckBox ingredientCheckBox;

        public myViewHolder(View itemView) {
            super(itemView);
            ingredientName = itemView.findViewById(R.id.ingredient_name);
            ingredientQuantity = itemView.findViewById(R.id.ingredient_quantity);
            ingredientCheckBox = itemView.findViewById(R.id.ingredient_checkbox);
        }
    }
}
