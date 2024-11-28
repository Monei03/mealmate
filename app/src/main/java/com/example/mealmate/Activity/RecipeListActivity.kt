package com.example.mealmate.Activity

import com.example.mealmate.auth.RecipeAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealmate.databinding.ActivityRecipeListBinding
import com.example.mealmate.network.ApiClient
import com.example.mealmate.network.Recipe
import com.example.mealmate.network.RecipeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeListBinding
    private val apiKey = "eeb8a093312b411a8366b24c2899da67" // Replace  Spoonacular API key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val searchQuery = intent.getStringExtra("SEARCH_QUERY") ?: "pasta"
        fetchRecipes(searchQuery)
    }

    private fun fetchRecipes(query: String) {
        binding.progressBar.visibility = View.VISIBLE
        ApiClient.apiService.searchRecipes(query, apiKey)
            .enqueue(object : Callback<RecipeResponse> {
                override fun onResponse(call: Call<RecipeResponse>, response: Response<RecipeResponse>) {
                    binding.progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val recipes = response.body()?.results ?: emptyList()
                        if (recipes.isNotEmpty()) {
                            setupRecyclerView(recipes)
                        } else {
                            showError("No recipes found for \"$query\"")
                        }
                    } else {
                        showError("Failed to load recipes")
                        Log.e("RecipeListActivity", "API Error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    showError("An error occurred. Please try again.")
                    Log.e("RecipeListActivity", "API Call Failed: ${t.message}")
                }
            })
    }

    private fun setupRecyclerView(recipes: List<Recipe>) {
        val adapter = RecipeAdapter(this, recipes) { recipe ->
            val intent = Intent(this, RecipeDetailActivity::class.java)
            intent.putExtra("RECIPE_ID", recipe.id)
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        binding.errorTextView.visibility = View.GONE
    }

    private fun showError(message: String) {
        binding.errorTextView.text = message
        binding.errorTextView.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }
}
