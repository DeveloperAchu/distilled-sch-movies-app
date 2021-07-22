package com.developerachu.moviesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

import com.developerachu.moviesapp.utils.AppConstants

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val goButton: Button = findViewById(R.id.go_button)
        goButton.setOnClickListener {
            navigateToNextScreen()
        }

    }

    private fun navigateToNextScreen() {
        println(AppConstants.TAG_NAME + "Navigating to top rated movies screen")
    }
}