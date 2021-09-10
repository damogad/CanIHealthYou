package com.example.canihealthyou;

import java.io.Serializable;
import java.util.ArrayList;

public class RecipeItem implements Serializable {
    private int pictureId = -1;
    private String picturePath;
    private String name;
    private String approxTime;
    private ArrayList<IngredientItem> ingredients;
    private ArrayList<StepItem> steps;
    private String encondedPicture;

    public RecipeItem(String name, String approxTime) {
        this.name = name;
        this.approxTime = approxTime;
    }

    public RecipeItem(int pictureId, String name, String approxTime) {
        this(name, approxTime);
        this.pictureId = pictureId;
    }

    public RecipeItem(String picturePath, String name, String approxTime) {
        this(name, approxTime);
        this.picturePath = picturePath;
    }

    public RecipeItem(String picturePath, String name, String approxTime, ArrayList<IngredientItem> ingredients, ArrayList<StepItem> steps) {
        this(picturePath, name, approxTime);
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public RecipeItem(int pictureId, String name, String approxTime, ArrayList<IngredientItem> ingredients, ArrayList<StepItem> steps) {
        this(pictureId, name, approxTime);
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public int getPictureId() {
        return pictureId;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public String getName() {
        return name;
    }

    public String getApproxTime() {
        return approxTime;
    }

    public ArrayList<IngredientItem> getIngredients() {
        return ingredients;
    }

    public ArrayList<StepItem> getSteps() {
        return steps;
    }

    public void setPictureId(int pictureId) {
        this.pictureId = pictureId;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getEncondedPicture() {
        return encondedPicture;
    }

    public void setEncondedPicture(String encondedPicture) {
        this.encondedPicture = encondedPicture;
    }
}
