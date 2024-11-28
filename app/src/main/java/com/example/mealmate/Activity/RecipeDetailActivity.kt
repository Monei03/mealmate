package com.example.mealmate.Activity

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.example.mealmate.auth.IngredientPagerAdapter
import com.example.mealmate.databinding.ActivityRecipeDetailBinding
import com.example.mealmate.models.RecipeDetails
import com.example.mealmate.network.ApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailBinding
    private lateinit var ingredientAdapter: IngredientPagerAdapter
    private val apiKey = "e7d798b70fbc47619d2b96d1ecda0801" // Replace with your actual Spoonacular API key
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the recipe ID from the intent
        val recipeId = intent.getIntExtra("RECIPE_ID", -1)

        if (recipeId != -1) {
            fetchRecipeDetails(recipeId)
        } else {
            Toast.makeText(this, "Invalid recipe ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Add to Cart button listener
        binding.btnAddToCart.setOnClickListener {
            addToCart(recipeId)
        }
    }

    private fun fetchRecipeDetails(recipeId: Int) {
        ApiClient.apiService.getRecipeInformation(recipeId, includeNutrition = false, apiKey = apiKey)
            .enqueue(object : Callback<RecipeDetails> {
                override fun onResponse(call: Call<RecipeDetails>, response: Response<RecipeDetails>) {
                    if (response.isSuccessful) {
                        val recipeDetails = response.body()
                        if (recipeDetails != null) {
                            displayRecipeDetails(recipeDetails) // Show the recipe details in your UI
                        } else {
                            Log.e("RecipeDetailActivity", "Recipe details are null.")
                            Toast.makeText(this@RecipeDetailActivity, "Recipe details are null", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("RecipeDetailActivity", "Failed to load recipe details. Code: ${response.code()}, Message: ${response.message()}")
                        Toast.makeText(this@RecipeDetailActivity, "Failed to load recipe details. Code: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RecipeDetails>, t: Throwable) {
                    // Log error if the API call fails
                    Log.e("RecipeDetailActivity", "API call failed: ${t.message}")
                    Toast.makeText(this@RecipeDetailActivity, "An error occurred: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun displayRecipeDetails(recipeDetails: RecipeDetails) {
        // Set recipe title
        binding.tvRecipeTitle.text = recipeDetails.title

        // Set recipe instructions, handle HTML tags
        val instructionsText = recipeDetails.instructions ?: "Instructions not available."
        binding.tvRecipeInstructions.text = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            HtmlCompat.fromHtml(instructionsText, HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            android.text.Html.fromHtml(instructionsText)
        }

        // Load the image with optimized size based on screen density
        val imageUrl = recipeDetails.image // Assuming this URL points to the image
        val (width, height) = getOptimizedImageSize()

        Picasso.get()
            .load(imageUrl)
            .resize(width, height) // Resize the image to the desired width and height
            .centerCrop() // Center crop to fill the ImageView
            .into(binding.ivRecipeImage)

        // Display ingredients in ViewPager2
        ingredientAdapter = IngredientPagerAdapter(recipeDetails.extendedIngredients)
        binding.viewPagerIngredients.adapter = ingredientAdapter
    }

    private fun getOptimizedImageSize(): Pair<Int, Int> {
        val metrics = resources.displayMetrics
        val screenDensity = metrics.densityDpi

        val width: Int
        val height: Int
        when (screenDensity) {
            DisplayMetrics.DENSITY_HIGH -> {
                width = 800
                height = (width * 9) / 16 // 16:9 aspect ratio
            }
            DisplayMetrics.DENSITY_MEDIUM -> {
                width = 600
                height = (width * 9) / 16
            }
            else -> {
                width = 400
                height = (width * 9) / 16
            }
        }
        return Pair(width, height)
    }

    private fun addToCart(recipeId: Int) {
        val recipeTitle = binding.tvRecipeTitle.text.toString()
        val ingredients = ingredientAdapter.getIngredients()  // Use getIngredients() method

        // Get current user ID
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Structure the data for Firestore
        val cartItem = hashMapOf(
            "recipeId" to recipeId,
            "recipeTitle" to recipeTitle,
            "ingredients" to ingredients.map { ingredient ->
                mapOf(
                    "name" to ingredient.name,
                    "amount" to ingredient.amount,
                    "image" to ingredient.image
                )
            }
        )

        firestore.collection("Users").document(currentUserId)
            .collection("GroceryList").document(recipeId.toString())
            .set(cartItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("RecipeDetailActivity", "Failed to add to cart: ${e.message}")
                Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show()
            }
    }
}
