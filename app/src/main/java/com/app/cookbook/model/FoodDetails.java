package com.app.cookbook.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class FoodDetails implements Serializable {
    private List<String>  ingredients;
    private String instructions;
    private int preparationTime;
    private int cookingTime;
    private int servings;

    public FoodDetails() {
    }

    public FoodDetails(List<String> ingredients, String instructions, int preparationTime, int cookingTime, int servings) {
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.preparationTime = preparationTime;
        this.cookingTime = cookingTime;
        this.servings = servings;
    }


    public List< String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(int preparationTime) {
        this.preparationTime = preparationTime;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }
}