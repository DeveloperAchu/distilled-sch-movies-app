package com.developerachu.moviesapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

/**
 * The main activity from which the app execution starts. It extends the AppCompatActivity class
 */
class MainActivity : AppCompatActivity() {
    // On create function sets the activity's content view file
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the button view by its id here and register an onclick listener
        val goButton: Button = findViewById(R.id.go_button)
        goButton.setOnClickListener {
            navigateToMoviesListScreen()
        }

    }

    /**
     * Function to handle the go button click event.
     * Navigates the user to the popular tv shows screen
     */
    private fun navigateToMoviesListScreen() {
        val intent = Intent(this, PopularTvShowsActivity::class.java)
        startActivity(intent)
    }
}