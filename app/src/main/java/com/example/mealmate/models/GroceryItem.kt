package com.example.mealmate.models

data class GroceryItem(
    val name: String,  // Item name
    val quantity: Int = 1,  // Quantity of the item
    var isPurchased: Boolean = false  // Purchased state, default to false
)
