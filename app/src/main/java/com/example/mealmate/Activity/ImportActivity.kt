package com.example.mealmate.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mealmate.R

class ImportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import)

        val intent = intent
        if (Intent.ACTION_SEND == intent.action && intent.type != null) {
            when (intent.type) {
                "text/plain" -> handleSharedText(intent.getStringExtra(Intent.EXTRA_TEXT))
                "application/json" -> handleSharedJson(intent.getStringExtra(Intent.EXTRA_TEXT))
            }
        }
    }

    private fun handleSharedText(sharedText: String?) {
        sharedText?.let {
            // Pass the text data to GroceryListActivity
            sendToGroceryList(it)
        }
    }

    private fun handleSharedJson(sharedJson: String?) {
        sharedJson?.let {
            // Pass the JSON data to GroceryListActivity
            sendToGroceryList(it)
        }
    }

    private fun sendToGroceryList(data: String) {
        val intent = Intent(this, GroceryListActivity::class.java).apply {
            putExtra("imported_data", data)
        }
        startActivity(intent)
        Toast.makeText(this, "Data sent to Grocery List", Toast.LENGTH_SHORT).show()
        finish()
    }
}
