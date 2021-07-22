package com.developerachu.moviesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

import com.developerachu.moviesapp.utils.AppConstants

//This will be the main activity from which the app execution starts. It extends AppCompatActivity class
class MainActivity : AppCompatActivity() {
    //    On create function sets the activity's content view file
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        Get the button view by its id here and register an onclick listener
        val goButton: Button = findViewById(R.id.go_button)
        goButton.setOnClickListener {
            navigateToMoviesListScreen()
        }

    }

    //    This function is called the go button is clicked. This function navigates the user to the
    //    screen where popular tv shows are shown
    private fun navigateToMoviesListScreen() {
        Log.d(AppConstants.TAG_NAME, "Navigating to Movies list screen")
        val intent = Intent(this, PopularTvShowsActivity::class.java)
        startActivity(intent)
    }
}