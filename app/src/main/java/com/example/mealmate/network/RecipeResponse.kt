package com.example.mealmate.network

data class RecipeResponse(
    val results: List<Recipe>
)

data class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    val extendedIngredients: List<Ingredient> // Added the extendedIngredients field
)

data class Ingredient(
    val name: String,
    val isPurchased: Boolean // Assuming this structure for ingredients
)
