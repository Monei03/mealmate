package com.example.mealmate.auth

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mealmate.Activity.RecipeDetailActivity
import com.example.mealmate.network.RecipeByIngredientsResponse
import com.squareup.picasso.Picasso
import com.example.mealmate.R

class RecipeByIngredientsAdapter(private val recipes: List<RecipeByIngredientsResponse>) :
    RecyclerView.Adapter<RecipeByIngredientsAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)
        val recipeTitle: TextView = itemView.findViewById(R.id.recipeTitle)
        val usedIngredients: TextView = itemView.findViewById(R.id.usedIngredients)
        val missedIngredients: TextView = itemView.findViewById(R.id.missedIngredients)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_item, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]

        // Load recipe image
        Picasso.get().load(recipe.image).into(holder.recipeImage)

        // Set recipe title and ingredient counts
        holder.recipeTitle.text = recipe.title
        holder.usedIngredients.text = "Used: ${recipe.usedIngredientCount}"
        holder.missedIngredients.text = "Missed: ${recipe.missedIngredientCount}"

        // Set click listener for the image
        holder.recipeImage.setOnClickListener {
            // Start RecipeDetailActivity and pass the recipe ID
            val context = holder.recipeImage.context
            val intent = Intent(context, RecipeDetailActivity::class.java)
            intent.putExtra("RECIPE_ID", recipe.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = recipes.size
}
