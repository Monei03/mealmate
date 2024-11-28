package com.example.mealmate.models

// Recipe Details model with a list of ingredients
data class RecipeDetails(
    val id: Int? = null,
    val title: String,
    val image: String,
    val instructions: String?,
    val extendedIngredients: List<Ingredient>
)

// Ingredient model with amount and image URL
data class Ingredient(
    val name: String,
    val amount: String,  // Make sure amount is a String
    val image: String,
    var isPurchased: Boolean = false
)


// Amount model to represent the quantity of an ingredient
data class Amount(
    val metric: MetricAmount
)

// MetricAmount model to represent value and unit of measurement
data class MetricAmount(
    val value: Double, // The numerical value of the amount
    val unit: String   // The unit of measurement (e.g., grams, cups)
)
