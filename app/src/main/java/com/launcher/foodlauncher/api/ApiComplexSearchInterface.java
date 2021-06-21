package com.launcher.foodlauncher.api;

import com.launcher.foodlauncher.ui.find_food.ComplexSearch;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiComplexSearchInterface {

    @GET("complexSearch")
    Call<ComplexSearch> getSearchResult(@Query("apiKey") String apiKey, @Query("query") String query, @Query("diet") String diet,
                                      @Query("intolerances") String intolerances, @Query("excludeIngredients") String excludeIngredients,
                                      @Query("instructionsRequired") boolean instructionsRequired, @Query("type") String type,
                                      @Query("addRecipeInformation") boolean addRecipeInformation, @Query("maxFat") String maxFat,
                                      @Query("number") int number, @Query("cuisine") String cuisine, @Query("maxCalories") String maxCalories);

}