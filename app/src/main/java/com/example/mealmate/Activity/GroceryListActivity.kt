package com.example.mealmate.Activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealmate.auth.GroceryAdapter
import com.example.mealmate.databinding.ActivityGroceryListBinding
import com.example.mealmate.models.Ingredient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GroceryListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroceryListBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val SMS_PERMISSION_CODE = 100
    private var ingredientsList = mutableListOf<Ingredient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroceryListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check and request SMS permission
        checkAndRequestSmsPermission()

        // Setup RecyclerView
        binding.rvGroceryList.layoutManager = LinearLayoutManager(this)

        // Fetch grocery list from Firebase
        fetchGroceryList()

        // Add ingredient functionality
        binding.addIngredientButton.setOnClickListener {
            val ingredientName = binding.ingredientNameEditText.text.toString().trim()
            if (ingredientName.isNotEmpty()) {
                addIngredient(Ingredient(ingredientName, "", ""))
                binding.ingredientNameEditText.text.clear()
            } else {
                Toast.makeText(this, "Please enter an ingredient name", Toast.LENGTH_SHORT).show()
            }
        }

        // Send SMS functionality
        binding.sendSmsButton.setOnClickListener {
            val message = prepareGroceryListMessage(ingredientsList)
            val phoneNumber = binding.phoneNumberEditText.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                sendSmsViaIntent(phoneNumber, message)
            } else {
                Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show()
            }
        }

        // Locate nearby stores functionality
        binding.btnLocateStore.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        // Add recipe from Firebase functionality
        binding.promptAddRecipeButton.setOnClickListener {
            fetchRecipesFromFirebase()
        }
    }

    private fun checkAndRequestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), SMS_PERMISSION_CODE)
        }
    }

    private fun fetchGroceryList() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        firestore.collection("Users").document(currentUserId)
            .collection("GroceryList")
            .get()
            .addOnSuccessListener { result ->
                ingredientsList.clear()
                for (document in result.documents) {
                    val ingredientList = document.get("ingredients") as? List<Map<String, Any>>
                    ingredientList?.forEach { ingredientData ->
                        val ingredient = Ingredient(
                            name = ingredientData["name"] as? String ?: "Unknown",
                            amount = ingredientData["amount"] as? String ?: "",
                            image = ingredientData["image"] as? String ?: ""
                        )
                        ingredientsList.add(ingredient)
                    }
                }
                displayGroceryList(ingredientsList)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load grocery list", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayGroceryList(ingredients: List<Ingredient>) {
        val adapter = GroceryAdapter(
            ingredients,
            onEditClick = { ingredient -> editIngredient(ingredient) },
            onDeleteClick = { ingredient -> deleteIngredient(ingredient) }
        )
        binding.rvGroceryList.adapter = adapter
    }

    private fun GroceryAdapter(groceryItems: List<Ingredient>, onEditClick: (Ingredient) -> Unit, onDeleteClick: (Ingredient) -> Unit): GroceryAdapter {

        return TODO("Provide the return value")
    }

    private fun addIngredient(ingredient: Ingredient) {
        ingredientsList.add(ingredient)
        displayGroceryList(ingredientsList)
    }

    private fun editIngredient(ingredient: Ingredient) {
        Toast.makeText(this, "Edit functionality to be implemented", Toast.LENGTH_SHORT).show()
    }

    private fun deleteIngredient(ingredient: Ingredient) {
        ingredientsList.remove(ingredient)
        displayGroceryList(ingredientsList)
    }

    private fun prepareGroceryListMessage(ingredients: List<Ingredient>): String {
        return ingredients.joinToString(separator = "\n") { "- ${it.name}: ${it.amount}" }
    }

    private fun sendSmsViaIntent(phoneNumber: String, message: String) {
        val smsIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("sms:$phoneNumber")
            putExtra("sms_body", message)
        }
        startActivity(smsIntent)
    }

    private fun fetchRecipesFromFirebase() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        firestore.collection("Users").document(currentUserId)
            .collection("Recipes")
            .get()
            .addOnSuccessListener { result ->
                result.documents.forEach { document ->
                    val ingredients = document.get("ingredients") as? List<Map<String, Any>>
                    ingredients?.forEach { ingredientData ->
                        val ingredient = Ingredient(
                            name = ingredientData["name"] as? String ?: "Unknown",
                            amount = ingredientData["amount"] as? String ?: "",
                            image = ingredientData["image"] as? String ?: ""
                        )
                        if (ingredient !in ingredientsList) {
                            ingredientsList.add(ingredient)
                        }
                    }
                }
                displayGroceryList(ingredientsList)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch recipes", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}