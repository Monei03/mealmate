package com.example.mealmate.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.example.mealmate.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Navigate to the Sign In Activity
        binding.existingTag.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        // Sign Up Button Click Listener
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val fullName = binding.fullNameEt.text.toString()  // Get full name from the input
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            if (email.isNotEmpty() && fullName.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = firebaseAuth.currentUser
                                user?.let {
                                    // Set the display name on Firebase User
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                        .setDisplayName(fullName)  // Set the user's display name
                                        .build()

                                    // Update Firebase Auth profile
                                    user.updateProfile(profileUpdates).addOnCompleteListener { profileTask ->
                                        if (profileTask.isSuccessful) {
                                            // Save additional user data in Firestore if needed
                                            val userData = hashMapOf("fullName" to fullName, "email" to email)
                                            firestore.collection("users").document(user.uid)
                                                .set(userData)
                                                .addOnCompleteListener { firestoreTask ->
                                                    if (firestoreTask.isSuccessful) {
                                                        Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                                                        val intent = Intent(this, SignInActivity::class.java)
                                                        startActivity(intent)
                                                    } else {
                                                        Toast.makeText(
                                                            this,
                                                            firestoreTask.exception?.message ?: "Firestore Update Failed",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                        } else {
                                            Toast.makeText(this, "Profile Update Failed", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    this,
                                    task.exception?.message ?: "Sign Up Failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Passwords Do Not Match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are Not Allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
