package com.example.canihealthyou;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.media.AudioManager;
import android.opengl.Visibility;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.muddzdev.styleabletoast.StyleableToast;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipesFragment extends Fragment {

    public static final String recipesKey = "Recipes_list";
    public ArrayList<RecipeItem> recipesData = new ArrayList<>();

    private FloatingActionButton fabImportRecipe;
    private FloatingActionButton fabAddRecipe;
    private FloatingActionsMenu fabMenu;

    private RecyclerView recyclerView;
    private RecipesAdapter adapter;
    private RecyclerView.SmoothScroller smoothScroller;
    private SharedPreferences prefs;
    private EditText searchEditText;

    private String newRecipeJSON;
    AudioManager audioManager;

    public RecipesFragment() {
        // Required empty public constructor
    }


    /*
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.recipes_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search_recipes);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        prefs = getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);

        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recipes, container, false);

        recyclerView = v.findViewById(R.id.recipes_recyclerview_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadRecipesData();
        adapter = new RecipesAdapter(recipesData, getActivity());
        recyclerView.setAdapter(adapter);

        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter, getActivity()));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        smoothScroller = new LinearSmoothScroller(getActivity()) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        fabMenu = v.findViewById(R.id.fab_menu_recipes);

        fabImportRecipe = v.findViewById(R.id.recipes_fab);
        fabImportRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
                openQRScanner();
            }
        });

        fabAddRecipe = v.findViewById(R.id.add_recipe_fab);
        fabAddRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fabMenu.isShown())
                    fabMenu.setVisibility(View.GONE);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    fabMenu.setVisibility(View.VISIBLE);
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        searchEditText = v.findViewById(R.id.search_recipes);
        if (recipesData.isEmpty()) {
            searchEditText.setVisibility(View.GONE);
        }
        else {
            searchEditText.setVisibility(View.VISIBLE);
        }
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                if (searchEditText.getText().toString().isEmpty()) {
                    fabMenu.setVisibility(View.VISIBLE);
                    itemTouchHelper.attachToRecyclerView(recyclerView);
                }
                else {
                    fabMenu.setVisibility(View.GONE);
                    itemTouchHelper.attachToRecyclerView(null);
                }
                String text = searchEditText.getText().toString();
                adapter.getFilter().filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });
        searchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = RecipesFragment.this.getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    view.setHapticFeedbackEnabled(true);
                    view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }
                if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
            }
        });
        return v;
    }

    public void openQRScanner() {
        /*
        NewMedicationDialog newMedicationDialog = new NewMedicationDialog();
        newMedicationDialog.setTargetFragment(this, 0);
        newMedicationDialog.show(getFragmentManager(), "NEW_MEDICATION_DIALOG");
        */
        Intent qrScannerActivity = new Intent(getActivity(), QRScanner.class);
        getActivity().startActivity(qrScannerActivity);
    }

    private void loadRecipesData() {
        ArrayList<RecipeItem> defaultList = new ArrayList<>();

        ArrayList<IngredientItem> ingredientsPaella = new ArrayList<>();
        ingredientsPaella.add(new IngredientItem(getActivity().getResources().getString(R.string.paella_ingredient1), 250, QuantityType.GRAMS));
        ingredientsPaella.add(new IngredientItem(getActivity().getResources().getString(R.string.paella_ingredient2), 250, QuantityType.GRAMS));
        ingredientsPaella.add(new IngredientItem(getActivity().getResources().getString(R.string.paella_ingredient3), 250, QuantityType.GRAMS));
        ingredientsPaella.add(new IngredientItem(getActivity().getResources().getString(R.string.paella_ingredient4), 250, QuantityType.GRAMS));
        ingredientsPaella.add(new IngredientItem(getActivity().getResources().getString(R.string.paella_ingredient5), 250, QuantityType.GRAMS));
        ingredientsPaella.add(new IngredientItem(getActivity().getResources().getString(R.string.paella_ingredient6), 250, QuantityType.GRAMS));
        ingredientsPaella.add(new IngredientItem(getActivity().getResources().getString(R.string.paella_ingredient7), 250, QuantityType.GRAMS));
        ingredientsPaella.add(new IngredientItem(getActivity().getResources().getString(R.string.paella_ingredient8), 250, QuantityType.GRAMS));
        ingredientsPaella.add(new IngredientItem(getActivity().getResources().getString(R.string.paella_ingredient9), 250, QuantityType.GRAMS));
        ingredientsPaella.add(new IngredientItem(getActivity().getResources().getString(R.string.paella_ingredient10), 250, QuantityType.GRAMS));
        ingredientsPaella.add(new IngredientItem(getActivity().getResources().getString(R.string.paella_ingredient11), 250, QuantityType.GRAMS));

        ArrayList<StepItem> stepsPaella = new ArrayList<>();
        stepsPaella.add(new StepItem(getActivity().getResources().getString(R.string.paella_step1), 0, 0, 30));
        stepsPaella.add(new StepItem(getActivity().getResources().getString(R.string.paella_step2), 0, 0, 30));
        stepsPaella.add(new StepItem(getActivity().getResources().getString(R.string.paella_step3), 0, 0, 30));
        stepsPaella.add(new StepItem(getActivity().getResources().getString(R.string.paella_step4), 0, 0, 30));
        stepsPaella.add(new StepItem(getActivity().getResources().getString(R.string.paella_step5), 0, 0, 30));

        defaultList.add(new RecipeItem(R.raw.paella, getActivity().getResources().getString(R.string.Recipes_Paella_Name), getActivity().getResources().getString(R.string.Recipes_Paella_ApproxTime), ingredientsPaella, stepsPaella));

        ArrayList<IngredientItem> ingredientsLentils = new ArrayList<>();
        ingredientsLentils.add(new IngredientItem(getActivity().getResources().getString(R.string.lentils_ingredient1), 250, QuantityType.GRAMS));
        ingredientsLentils.add(new IngredientItem(getActivity().getResources().getString(R.string.lentils_ingredient2), 250, QuantityType.GRAMS));
        ingredientsLentils.add(new IngredientItem(getActivity().getResources().getString(R.string.lentils_ingredient3), 250, QuantityType.GRAMS));
        ingredientsLentils.add(new IngredientItem(getActivity().getResources().getString(R.string.lentils_ingredient4), 250, QuantityType.GRAMS));
        ingredientsLentils.add(new IngredientItem(getActivity().getResources().getString(R.string.lentils_ingredient5), 250, QuantityType.GRAMS));
        ingredientsLentils.add(new IngredientItem(getActivity().getResources().getString(R.string.lentils_ingredient6), 250, QuantityType.GRAMS));
        ingredientsLentils.add(new IngredientItem(getActivity().getResources().getString(R.string.lentils_ingredient7), 250, QuantityType.GRAMS));
        ingredientsLentils.add(new IngredientItem(getActivity().getResources().getString(R.string.lentils_ingredient8), 250, QuantityType.GRAMS));
        ingredientsLentils.add(new IngredientItem(getActivity().getResources().getString(R.string.lentils_ingredient9), 250, QuantityType.GRAMS));
        ingredientsLentils.add(new IngredientItem(getActivity().getResources().getString(R.string.lentils_ingredient10), 250, QuantityType.GRAMS));
        ingredientsLentils.add(new IngredientItem(getActivity().getResources().getString(R.string.lentils_ingredient11), 250, QuantityType.GRAMS));
        ingredientsLentils.add(new IngredientItem(getActivity().getResources().getString(R.string.lentils_ingredient12), 250, QuantityType.GRAMS));
        ingredientsLentils.add(new IngredientItem(getActivity().getResources().getString(R.string.lentils_ingredient13), 250, QuantityType.GRAMS));
        ingredientsLentils.add(new IngredientItem(getActivity().getResources().getString(R.string.lentils_ingredient14), 250, QuantityType.GRAMS));
        ingredientsLentils.add(new IngredientItem(getActivity().getResources().getString(R.string.lentils_ingredient15), 250, QuantityType.GRAMS));
        ingredientsLentils.add(new IngredientItem(getActivity().getResources().getString(R.string.lentils_ingredient16), 250, QuantityType.GRAMS));

        ArrayList<StepItem> stepsLentils = new ArrayList<>();
        stepsLentils.add(new StepItem(getActivity().getResources().getString(R.string.lentils_step1), 0, 0, 20));
        stepsLentils.add(new StepItem(getActivity().getResources().getString(R.string.lentils_step2), 0, 0, 20));
        stepsLentils.add(new StepItem(getActivity().getResources().getString(R.string.lentils_step3), 0, 0, 20));
        stepsLentils.add(new StepItem(getActivity().getResources().getString(R.string.lentils_step4), 0, 0, 20));

        defaultList.add(new RecipeItem(R.raw.lentils, getActivity().getResources().getString(R.string.Recipes_Lentils_Name), getActivity().getResources().getString(R.string.Recipes_Lentils_ApproxTime), ingredientsLentils, stepsLentils));

        ArrayList<IngredientItem> ingredientsSpongeCake = new ArrayList<>();
        ingredientsSpongeCake.add(new IngredientItem(getActivity().getResources().getString(R.string.spongeCake_ingredient1), 250, QuantityType.GRAMS));
        ingredientsSpongeCake.add(new IngredientItem(getActivity().getResources().getString(R.string.spongeCake_ingredient2), 250, QuantityType.GRAMS));
        ingredientsSpongeCake.add(new IngredientItem(getActivity().getResources().getString(R.string.spongeCake_ingredient3), 250, QuantityType.GRAMS));
        ingredientsSpongeCake.add(new IngredientItem(getActivity().getResources().getString(R.string.spongeCake_ingredient4), 250, QuantityType.GRAMS));
        ingredientsSpongeCake.add(new IngredientItem(getActivity().getResources().getString(R.string.spongeCake_ingredient5), 250, QuantityType.GRAMS));
        ingredientsSpongeCake.add(new IngredientItem(getActivity().getResources().getString(R.string.spongeCake_ingredient6), 250, QuantityType.GRAMS));
        ingredientsSpongeCake.add(new IngredientItem(getActivity().getResources().getString(R.string.spongeCake_ingredient7), 250, QuantityType.GRAMS));
        ingredientsSpongeCake.add(new IngredientItem(getActivity().getResources().getString(R.string.spongeCake_ingredient8), 250, QuantityType.GRAMS));

        ArrayList<StepItem> stepsSpongeCake = new ArrayList<>();
        stepsSpongeCake.add(new StepItem(getActivity().getResources().getString(R.string.spongeCake_step1), 0, 0, 20));
        stepsSpongeCake.add(new StepItem(getActivity().getResources().getString(R.string.spongeCake_step2), 0, 0, 30));
        stepsSpongeCake.add(new StepItem(getActivity().getResources().getString(R.string.spongeCake_step3), 0, 0, 30));

        defaultList.add(new RecipeItem(R.raw.sponge_cake, getActivity().getResources().getString(R.string.Recipes_SpongeCake_Name), getActivity().getResources().getString(R.string.Recipes_SpongeCake_ApproxTime), ingredientsSpongeCake, stepsSpongeCake));

        Gson gson = new Gson();
        String json = prefs.getString(RecipesFragment.recipesKey, gson.toJson(defaultList));
        Type type = new TypeToken<ArrayList<RecipeItem>>() {}.getType();
        recipesData = gson.fromJson(json, type);
        if (recipesData.isEmpty()) {
            recipesData = new ArrayList<>(defaultList);
            saveRecipesData();
        }

        newRecipeJSON = getActivity().getIntent().getExtras().getString("recipeJSON", null);
        getActivity().getIntent().removeExtra("recipeJSON");
        if (newRecipeJSON != null) {
            Type typeSingleRecipe = new TypeToken<RecipeItem>(){}.getType();
            try {
                RecipeItem newRecipe = gson.fromJson(newRecipeJSON, typeSingleRecipe);
                boolean alreadyExists = false;
                for (RecipeItem r : recipesData) {
                    if (r.getName().toLowerCase().equals(newRecipe.getName().toLowerCase())) {
                        alreadyExists = true;
                    }
                }
                if (!alreadyExists) {
                    newRecipe.setPictureId(-1);
                    newRecipe.setPicturePath(null);
                    recipesData.add(newRecipe);
                    saveRecipesData();
                    StyleableToast.makeText(getActivity(), getActivity().getResources().getString(R.string.Recipes_Added) + " (" + newRecipe.getName() + ") ", R.style.success_black).show();
                } else {
                    StyleableToast.makeText(getActivity(), getActivity().getResources().getString(R.string.Recipes_AlreadyExisting) + " (" + newRecipe.getName() + ") ", R.style.error).show();
                }
            } catch(JsonParseException e) {
                StyleableToast.makeText(getActivity(), getActivity().getResources().getString(R.string.Recipes_qrscanner_error), R.style.error).show();
            }
        }
    }

    private void saveRecipesData() {
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(recipesData);
        editor.putString(RecipesFragment.recipesKey, json);
        editor.commit();
    }




}
