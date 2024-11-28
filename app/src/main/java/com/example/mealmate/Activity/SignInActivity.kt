package com.example.mealmate.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mealmate.R
import com.example.mealmate.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Set header text (Optional, if dynamic updates are needed)
        binding.headerTextView.text = getString(R.string.welcome_to_the_login_page)

        // Navigate to Sign Up screen
        binding.nonExist.setOnClickListener { // Fixed ID to match the XML file
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Sign in user
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString().trim() // Added trim() for better validation
            val pass = binding.passET.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Sign In Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Close the activity to prevent back navigation to Sign In
                    } else {
                        Toast.makeText(
                            this,
                            task.exception?.message ?: "Sign In Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are Not Allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Auto-login if user is already signed in
        if (firebaseAuth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Close this activity to prevent back navigation
        }
    }
}