package com.example.canihealthyou;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;

public class SelectRecipesAdapter extends ArrayAdapter<RecipeItem> {

    public SelectRecipesAdapter(Activity context, ArrayList<RecipeItem> recipes) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, recipes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.select_recipe_item, parent, false);
        }

        RecipeItem currentRecipe = getItem(position);

        ImageView recipePicture = listItemView.findViewById(R.id.select_recipe_item_picture);
        TextView recipeName = listItemView.findViewById(R.id.select_recipe_item_name);
        String picturePath;
        int pictureId;

        recipePicture.setImageBitmap(null);
        if ((picturePath = currentRecipe.getPicturePath()) != null) {
            File imgFile = new File(picturePath);

            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                recipePicture.setImageBitmap(myBitmap);
            }
        }
        else if ((pictureId = currentRecipe.getPictureId()) != -1) {
            recipePicture.setImageResource(pictureId);
        }
        recipeName.setText(currentRecipe.getName());


        return listItemView;
    }
}
