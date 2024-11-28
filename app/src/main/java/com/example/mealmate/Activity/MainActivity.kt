package com.example.mealmate.Activity

import com.example.mealmate.auth.RecipeAdapter
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealmate.R
import com.example.mealmate.databinding.ActivityMainBinding
import com.example.mealmate.network.ApiClient
import com.example.mealmate.network.Recipe
import com.example.mealmate.network.RecipeResponse
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.view.View // Ensure this is imported correctly for visibility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser: FirebaseUser? = firebaseAuth.currentUser
        if (currentUser != null) {
            binding.tvGreeting.text = "Hey, ${currentUser.displayName}!"
        } else {
            binding.tvGreeting.text = "Hey, User!"  // Fallback text if user is not logged in
        }

        // Set up the drawer toggle
        drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set up navigation item selection listener
        binding.navigationView.setNavigationItemSelectedListener(this)

        // Set up SearchView listener for searching recipes
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    val intent = Intent(this@MainActivity, RecipeListActivity::class.java)
                    intent.putExtra("SEARCH_QUERY", it)
                    startActivity(intent)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        // Back button click listener
        binding.backIcon.setOnClickListener {
            finish() // Go back to the previous activity
        }

        // Profile picture click listener
        binding.ivProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java)) // Navigate to profile
        }

        // Navigate to Recipe List
        binding.btnViewRecipes.setOnClickListener {
            startActivity(Intent(this, RecipeListActivity::class.java))
        }

        // Navigate to Grocery List
        binding.btnGroceryList.setOnClickListener {
            startActivity(Intent(this, GroceryListActivity::class.java))
        }

        // Navigate to Add Custom Recipe
        binding.btnAddRecipe.setOnClickListener {
            startActivity(Intent(this, AddRecipeActivity::class.java))
        }

        // Navigate to Weekly Plan
        binding.btnWeeklyPlan.setOnClickListener {
            startActivity(Intent(this, MealPlanActivity::class.java)) // Navigate to MealPlanActivity
        }

        // Set up RecyclerView for displaying recipes
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch default recipes
        fetchRecipes("Cuisine")
    }

    private fun fetchRecipes(query: String) {
        // Show loading animation
        View.VISIBLE.also { binding.loadingAnimation.visibility = it } // Corrected usage of visibility

        // Call the API (You will need to define ApiClient and related methods)
        ApiClient.apiService.searchRecipes(query, "8701f1e07da94234b2b7bdfc6fc2c3f5")
            .enqueue(object : Callback<RecipeResponse> {
                override fun onResponse(
                    call: Call<RecipeResponse>,
                    response: Response<RecipeResponse>
                ) {
                    // Hide loading animation
                    binding.loadingAnimation.visibility = View.GONE // Corrected usage of visibility

                    if (response.isSuccessful) {
                        val recipes = response.body()?.results ?: emptyList()
                        displayRecipes(recipes)
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Failed to fetch recipes",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                    // Hide loading animation and display error
                    binding.loadingAnimation.visibility = View.GONE // Corrected usage of visibility
                    Toast.makeText(
                        this@MainActivity,
                        "An error occurred: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun displayRecipes(recipes: List<Recipe>) {
        val adapter = RecipeAdapter(this, recipes) { recipe ->
            val intent = Intent(this, RecipeDetailActivity::class.java)
            intent.putExtra("RECIPE_ID", recipe.id)
            startActivity(intent)
        }
        binding.recyclerView.adapter = adapter
    }

    // Handle navigation item selection
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_groceries -> {
                startActivity(Intent(this, GroceryListActivity::class.java))
            }
            R.id.nav_my_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
            R.id.nav_my_recipes -> {
                startActivity(Intent(this, RecipeListActivity::class.java))
            }
            R.id.nav_logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // Handle opening and closing of the drawer with the toggle
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (drawerToggle.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(item)
    }

    // Synchronize the drawer toggle state on configuration change
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }
}
