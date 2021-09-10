package com.example.canihealthyou;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SelectRecipeDialog extends AppCompatDialogFragment {

    ListView listView;
    SelectRecipesAdapter adapter;
    ArrayList<RecipeItem> recipesData;
    SharedPreferences prefs;
    AudioManager audioManager;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.select_recipe_layout, null);
        builder.setView(view).setTitle(getActivity().getResources().getString(R.string.SelectRecipeDialog_Title));

        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        listView = view.findViewById(R.id.select_recipes_listview);
        loadRecipesData();
        adapter = new SelectRecipesAdapter(getActivity(), recipesData);
        listView.setAdapter(adapter);

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        SharedPreferences prefs = getContext().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
                        if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                            view.setHapticFeedbackEnabled(true);
                            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                        }
                        if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                            audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                        }
                        ((NewMealDialog)getTargetFragment()).selectedRecipe(recipesData.get(i).getName());
                        dialog.dismiss();
                    }
                });
            }
        });

        return dialog;
    }

    private void loadRecipesData() {
        SharedPreferences prefs = getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        ArrayList<RecipeItem> defaultList = new ArrayList<>();
        defaultList.add(new RecipeItem(R.raw.paella, getActivity().getResources().getString(R.string.Recipes_Paella_Name), getActivity().getResources().getString(R.string.Recipes_Paella_ApproxTime)));
        defaultList.add(new RecipeItem(R.raw.lentils, getActivity().getResources().getString(R.string.Recipes_Lentils_Name), getActivity().getResources().getString(R.string.Recipes_Lentils_ApproxTime)));
        defaultList.add(new RecipeItem(R.raw.sponge_cake, getActivity().getResources().getString(R.string.Recipes_SpongeCake_Name), getActivity().getResources().getString(R.string.Recipes_SpongeCake_ApproxTime)));
        Gson gson = new Gson();
        String json = prefs.getString(RecipesFragment.recipesKey, gson.toJson(defaultList));
        Type type = new TypeToken<ArrayList<RecipeItem>>() {}.getType();
        recipesData = gson.fromJson(json, type);
        if (recipesData.isEmpty()) {
            recipesData = new ArrayList<>(defaultList);
        }
    }

    public interface SelectRecipeDialogListener {
        void selectedRecipe(String title);
    }
}
