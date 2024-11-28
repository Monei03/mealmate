package com.example.mealmate.Activity

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.mealmate.databinding.ActivityAddRecipeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso // Picasso for loading images from URLs

class AddRecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddRecipeBinding
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle back icon click
        binding.backIcon.setOnClickListener {
            finish()
        }

        // Register the Activity Result API for image picking
        val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = it
                binding.recipeImageView.setImageURI(uri) // Display selected image
            }
        }

        // Handle image upload button click using the Activity Result API
        binding.uploadImageButton.setOnClickListener {
            getImage.launch("image/*") // Open the image picker
        }

        // Save recipe button click
        binding.btnSaveRecipe.setOnClickListener {
            val title = binding.etRecipeTitle.text.toString()
            val ingredients = binding.etIngredients.text.toString().split("\n").filter { it.isNotBlank() }
            val instructions = binding.etInstructions.text.toString()

            if (title.isNotBlank() && ingredients.isNotEmpty() && instructions.isNotBlank() && imageUri != null) {
                saveRecipe(title, ingredients, instructions) // Call the saveRecipe method
            } else {
                Toast.makeText(this, "Please fill in all fields and upload an image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Save the recipe to Firestore and upload image to Firebase Storage
    private fun saveRecipe(title: String, ingredients: List<String>, instructions: String) {
        // Initialize Firestore and Firebase Storage
        val firestore = FirebaseFirestore.getInstance()
        val storage = FirebaseStorage.getInstance()

        // Create a reference to Firebase Storage for the recipe image
        val storageRef = storage.reference.child("recipe_images/${System.currentTimeMillis()}.jpg")

        // Upload the image to Firebase Storage
        val uploadTask = imageUri?.let { uri -> storageRef.putFile(uri) }

        uploadTask?.addOnSuccessListener { taskSnapshot ->
            // Get the download URL of the uploaded image
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                // Create a recipe object
                val recipe = hashMapOf(
                    "id" to System.currentTimeMillis().toString(),
                    "name" to title,
                    "ingredients" to ingredients,
                    "instructions" to instructions,
                    "image" to downloadUrl.toString() // Store the download URL of the image
                )

                // Save the recipe to Firestore
                firestore.collection("recipes")
                    .add(recipe)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Recipe saved successfully!", Toast.LENGTH_SHORT).show()
                        finish() // Close the activity
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to save recipe: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Failed to get image URL: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }?.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Method to retrieve the recipe and display the image
    private fun retrieveRecipe(recipeId: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("recipes")
            .document(recipeId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val imageUrl = document.getString("image")
                    imageUrl?.let {
                        // Use Picasso to load the image from URL into the ImageView
                        Picasso.get().load(it).into(binding.recipeImageView)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to retrieve recipe: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
