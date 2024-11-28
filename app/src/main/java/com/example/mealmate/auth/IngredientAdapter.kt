package com.example.mealmate.auth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mealmate.models.Ingredient
import com.squareup.picasso.Picasso
import com.example.mealmate.R

class IngredientAdapter(private val ingredients: MutableList<Ingredient>) :
    RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

    // ViewHolder class to hold references to the views for each item
    inner class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ingredientImage: ImageView = itemView.findViewById(R.id.ingredientImage)
        val ingredientAmount: TextView = itemView.findViewById(R.id.ingredientAmount)
        val ingredientName: TextView = itemView.findViewById(R.id.ingredientName)
    }

    // Inflate the layout for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ingredient_grid_item, parent, false)
        return IngredientViewHolder(view)
    }

    // Bind data to the views for each item
    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[position]

        // Load the ingredient image
        if (ingredient.image.isNotEmpty()) {
            val imageUrl = "https://spoonacular.com/cdn/ingredients_100x100/${ingredient.image}"
            Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image) // Add placeholder image
                .error(R.drawable.error_image) // Add error fallback image
                .into(holder.ingredientImage)
        } else {
            holder.ingredientImage.setImageResource(R.drawable.placeholder_image) // Fallback for missing image
        }

        // Set the ingredient name and amount
        holder.ingredientAmount.text = ingredient.amount
        holder.ingredientName.text = ingredient.name
    }

    // Get the number of items in the list
    override fun getItemCount(): Int = ingredients.size

    // Public method to update the ingredient list
    fun updateIngredients(newIngredients: List<Ingredient>) {
        ingredients.clear()
        ingredients.addAll(newIngredients)
        notifyDataSetChanged()
    }
}
