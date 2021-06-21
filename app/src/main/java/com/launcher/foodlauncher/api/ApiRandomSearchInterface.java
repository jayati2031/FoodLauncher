package com.launcher.foodlauncher.api;

import com.launcher.foodlauncher.ui.find_food.ComplexSearch;
import com.launcher.foodlauncher.ui.find_food.RandomSearch;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiRandomSearchInterface {

    @GET("random")
    Call<RandomSearch> getRandomSearchResult(@Query("apiKey") String apiKey, @Query("number") int number, @Query("tags") String tags);

}