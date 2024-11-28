package com.example.mealmate.auth

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mealmate.network.Recipe
import com.squareup.picasso.Picasso
import com.example.mealmate.R

class RecipeAdapter(
    private val context: Context,
    private val recipes: List<Recipe>,
    private val onItemClick: (Recipe) -> Unit // Explicitly specifying the type of the lambda
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)
        val recipeTitle: TextView = itemView.findViewById(R.id.recipeTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_item, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]

        // Load recipe image and set title
        Picasso.get().load(recipe.image).into(holder.recipeImage)
        holder.recipeTitle.text = recipe.title

        // Handle click event for each item
        holder.itemView.setOnClickListener { onItemClick(recipe) }
    }

    override fun getItemCount(): Int = recipes.size
}