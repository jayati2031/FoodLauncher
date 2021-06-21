package com.launcher.foodlauncher.ui.favourites;

import java.util.List;

public class FavFood {

    String calories;
    String fat;
    String foodCredits;
    String foodId;
    String foodLikes;
    String foodScore;
    String foodServings;
    String foodTitle;
    String ingredients;
    String readyInMinutes;
    String veg;
    String foodDiets;
    String foodCuisines;
    List<Instructions> instructions;
    String summary;

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getFoodCredits() {
        return foodCredits;
    }

    public void setFoodCredits(String foodCredits) {
        this.foodCredits = foodCredits;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getFoodLikes() {
        return foodLikes;
    }

    public void setFoodLikes(String foodLikes) {
        this.foodLikes = foodLikes;
    }

    public String getFoodScore() {
        return foodScore;
    }

    public void setFoodScore(String foodScore) {
        this.foodScore = foodScore;
    }

    public String getFoodServings() {
        return foodServings;
    }

    public void setFoodServings(String foodServings) {
        this.foodServings = foodServings;
    }

    public String getFoodTitle() {
        return foodTitle;
    }

    public void setFoodTitle(String foodTitle) {
        this.foodTitle = foodTitle;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getReadyInMinutes() {
        return readyInMinutes;
    }

    public void setReadyInMinutes(String readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }

    public String getVeg() {
        return veg;
    }

    public void setVeg(String veg) {
        this.veg = veg;
    }

    public List<Instructions> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<Instructions> instructions) {
        this.instructions = instructions;
    }

    public String getFoodDiets() {
        return foodDiets;
    }

    public void setFoodDiets(String foodDiets) {
        this.foodDiets = foodDiets;
    }

    public String getFoodCuisines() {
        return foodCuisines;
    }

    public void setFoodCuisines(String foodCuisines) {
        this.foodCuisines = foodCuisines;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}

class Instructions {
    String number;
    String step;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }
}