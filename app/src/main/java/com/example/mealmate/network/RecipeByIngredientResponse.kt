package com.example.mealmate.network

import com.example.mealmate.models.Ingredient

data class RecipeByIngredientsResponse(
    val id: Int,                      // Recipe ID
    val title: String,                 // Recipe title
    val image: String,                 // URL to the recipe image
    val missedIngredientCount: Int,    // Number of ingredients missing from the user's input
    val usedIngredientCount: Int,      // Number of ingredients used from the user's input
    val missedIngredients: List<Ingredient>,  // List of missing ingredients
    val usedIngredients: List<Ingredient>     // List of used ingredients
)


