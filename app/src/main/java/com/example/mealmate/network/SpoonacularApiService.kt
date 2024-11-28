package com.example.mealmate.network

import com.example.mealmate.models.RecipeDetails
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpoonacularApiService {
    // Get detailed recipe information by ID, excluding nutrition


    @GET("recipes/complexSearch")
    fun searchRecipes(
        @Query("query") query: String, // The search term, e.g., "pasta"
        @Query("apiKey") apiKey: String,
        @Query("number") number: Int = 10 // The number of recipes to return
    ): Call<RecipeResponse>


    @GET("recipes/{id}/information")
    fun getRecipeInformation(
        @Path("id") id: Int,
        @Query("includeNutrition") includeNutrition: Boolean = false,
        @Query("apiKey") apiKey: String
    ): Call<RecipeDetails>
}
