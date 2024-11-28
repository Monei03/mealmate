package com.example.mealmate.Activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mealmate.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.button.MaterialButton

class MealPlanActivity : AppCompatActivity() {

    private lateinit var mealInput: TextInputEditText
    private lateinit var mealInputLayout: TextInputLayout
    private lateinit var datePicker: DatePicker
    private lateinit var saveButton: MaterialButton
    private lateinit var previewButton: MaterialButton

    private val sharedPreferences by lazy {
        getSharedPreferences("MealStorage", MODE_PRIVATE)
    }

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_plan)

        // Initialize views
        mealInput = findViewById(R.id.mealInput)
        mealInputLayout = findViewById(R.id.mealInputLayout)
        datePicker = findViewById(R.id.datePicker)
        saveButton = findViewById(R.id.saveButton)
        previewButton = findViewById(R.id.previewButton)

        // Save meal button listener
        saveButton.setOnClickListener {
            val meal = mealInput.text.toString().trim()
            val day = datePicker.dayOfMonth
            val month = datePicker.month + 1
            val year = datePicker.year
            val dateKey = "$day/$month/$year"

            if (meal.isBlank()) {
                mealInputLayout.error = "Meal description cannot be empty!"
            } else {
                mealInputLayout.error = null
                saveMeal(dateKey, meal)
                Toast.makeText(this, "Meal for $dateKey saved successfully! 🎉", Toast.LENGTH_SHORT).show()
                mealInput.text?.clear()
            }
        }

        // Preview meal button listener
        previewButton.setOnClickListener {
            val day = datePicker.dayOfMonth
            val month = datePicker.month + 1
            val year = datePicker.year
            val dateKey = "$day/$month/$year"

            val meal = loadMeal(dateKey)
            Toast.makeText(
                this,
                meal?.let { "Meal for $dateKey: $it" } ?: "No meal planned for $dateKey.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun saveMeal(dateKey: String, meal: String) {
        sharedPreferences.edit().putString(dateKey, meal).apply()
    }

    private fun loadMeal(dateKey: String): String? {
        return sharedPreferences.getString(dateKey, null)
    }
}
