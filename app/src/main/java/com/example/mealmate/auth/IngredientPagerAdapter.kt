package com.example.mealmate.auth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mealmate.R
import com.example.mealmate.databinding.ItemIngredientBinding
import com.example.mealmate.models.Ingredient
import com.squareup.picasso.Picasso

class IngredientPagerAdapter(private val ingredients: List<Ingredient>) :
    RecyclerView.Adapter<IngredientPagerAdapter.IngredientViewHolder>() {

    inner class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ingredientImage: ImageView = itemView.findViewById(R.id.ingredientImage)
        val ingredientName: TextView = itemView.findViewById(R.id.ingredientName)
        val ingredientAmount: TextView = itemView.findViewById(R.id.ingredientAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ingredient_view_item, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[position]
        // Load the ingredient image
        val imageUrl = "https://spoonacular.com/cdn/ingredients_100x100/${ingredient.image}"
        Picasso.get().load(imageUrl).into(holder.ingredientImage)

        // Set ingredient name and amount
        holder.ingredientName.text = ingredient.name
        holder.ingredientAmount.text = ingredient.amount
    }

    override fun getItemCount(): Int = ingredients.size

    // Public method to retrieve the list of ingredients
    fun getIngredients(): List<Ingredient> = ingredients
}