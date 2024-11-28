package com.example.mealmate.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.mealmate.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Add a delay of 3 seconds and then start the next activity
        Handler(Looper.getMainLooper()).postDelayed({
            // Transition to the SignInActivity
            val intent = Intent(this@SplashActivity, SignInActivity::class.java)
            startActivity(intent)
            finish()  // Close SplashActivity to prevent going back to it
        }, 3000)  // 3000 milliseconds = 3 seconds
    }
}
