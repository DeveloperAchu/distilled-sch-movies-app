package com.developerachu.moviesapp

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.developerachu.moviesapp.utils.AppConstants

class ImageViewActivity : AppCompatActivity() {

    // Create variable to hold the imageview widget
    private lateinit var posterImageView: ImageView

    // Create variables to hold the name and image url passed from the previous activity
    private lateinit var name: String
    private lateinit var imageUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)
        init()
    }

    /**
     * Function to initialize the UI elements and start the flow of execution once the activity
     * lifecycle is started
     */
    private fun init() {
        loadIntentExtras()
        initUiElements()
        setDetails()
    }

    /**
     * Function to get the details passed as intent extras from the previous activity
     */
    private fun loadIntentExtras() {
        name = intent.getStringExtra(AppConstants.NAME)!!
        imageUrl = intent.getStringExtra(AppConstants.IMAGE)!!
    }

    /**
     * Function ot initialize the UI elements
     */
    private fun initUiElements() {
        posterImageView = findViewById(R.id.poster_image)
    }

    /**
     * Function to set the title of the activity as the name of the tv show and show
     * the image in the imageview widget
     */
    private fun setDetails() {
        title = name
        Glide.with(this).load(imageUrl).into(posterImageView)
    }
}
