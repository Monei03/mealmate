package com.example.mealmate.models

data class CartItem(
    val name: String = "",
    val quantity: Double = 0.0,
    val unit: String = "",
    val image: String? = null // Optional field for the ingredient image URL
)
