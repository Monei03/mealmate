package com.example.mealmate.network

import com.example.mealmate.models.Amount

data class IngredientResponse(
    val ingredients: List<IngredientItem>
)

data class IngredientItem(
    val name: String,
    val amount: Amount,
    val image: String // URL to the ingredient image, based on API structure
)


