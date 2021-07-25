package com.developerachu.moviesapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.bumptech.glide.Glide
import com.developerachu.moviesapp.dialogs.AppDialogs
import com.developerachu.moviesapp.interfaces.OnHttpRequestListener
import com.developerachu.moviesapp.utils.AppConstants
import com.developerachu.moviesapp.utils.AppUtils
import com.developerachu.moviesapp.webservices.GetApiResponse
import com.developerachu.moviesapp.webservices.HttpRequestObject
import com.google.android.flexbox.FlexboxLayout
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import kotlin.coroutines.CoroutineContext


class TvShowDetailsActivity : AppCompatActivity(), CoroutineScope {
    // Create a job variable to handle the background tasks
    private lateinit var job: Job

    // Create a coroutine context
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    // Initialize the context to the current activity
    private val context = this

    // Create a variable to hold the progressbar widget
    private lateinit var progressBar: ProgressBar

    // Create a variable to hold the textview widget
    private lateinit var noDetailsTextView: TextView

    // Create a variable to hold the nested scrollview widget
    private lateinit var detailsScrollView: NestedScrollView

    // Create variables to hold all the widgets that need to be populated later
    private lateinit var posterImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var genreLayout: FlexboxLayout
    private lateinit var overviewTextView: TextView
    private lateinit var popularityTextView: TextView
    private lateinit var firstAirDateTextView: TextView
    private lateinit var seasonsTextView: TextView
    private lateinit var episodesTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var typeTextView: TextView
    private lateinit var voteAverageTextView: TextView
    private lateinit var voteCountTextView: TextView

    // Create variables to hold the tv show name and image to be passed on to the next screen
    private lateinit var name: String
    private lateinit var imageUrl: String


    // Create a variable to hold the id of the tv show
    private var id: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_show_details)
        init()
    }

    override fun onDestroy() {
        // Cancel the job when the activity gets destroyed
        job.cancel()
        super.onDestroy()
    }

    /**
     * Function to initialize the UI elements and start the flow of execution once the activity
     * lifecycle is started
     */
    private fun init() {
        // Get the id of the tv show from the intent extras
        id = intent.getIntExtra(AppConstants.POPULAR_SHOW_ID, -1)
        // The job variable is initialized here
        job = Job()
        initUiElements()
        initClickEvents()
        loadTvShowDetails()
    }

    /**
     * Function ot initialize the UI elements and control their visibility
     */
    private fun initUiElements() {
        progressBar = findViewById(R.id.progress_bar)
        noDetailsTextView = findViewById(R.id.no_details_text_view)
        detailsScrollView = findViewById(R.id.details_scroll_view)
        AppUtils.visibilityController(
            arrayOf(progressBar),
            arrayOf(noDetailsTextView, detailsScrollView)
        )
        posterImageView = findViewById(R.id.poster_image_view)
        nameTextView = findViewById(R.id.name_text_view)
        genreLayout = findViewById(R.id.genre_layout)
        overviewTextView = findViewById(R.id.overview_text_view)
        popularityTextView = findViewById(R.id.popularity_text_view)
        firstAirDateTextView = findViewById(R.id.first_air_date_text_view)
        seasonsTextView = findViewById(R.id.seasons_text_view)
        episodesTextView = findViewById(R.id.episodes_text_view)
        statusTextView = findViewById(R.id.status_text_view)
        typeTextView = findViewById(R.id.type_text_view)
        voteAverageTextView = findViewById(R.id.vote_average_text_view)
        voteCountTextView = findViewById(R.id.vote_count_text_view)
    }

    /**
     * Function to handle the click event on the poster image. This will open the new
     * activity with the name of the tv show and the image url passed as intent extras
     */
    private fun initClickEvents() {
        posterImageView.setOnClickListener {
            val intent = Intent(this, ImageViewActivity::class.java)
            intent.putExtra(AppConstants.NAME, name)
            intent.putExtra(AppConstants.IMAGE, imageUrl)
            startActivity(intent)
        }
    }

    /**
     * Function to initialize an HttpRequestObject and its callback implementations. Function
     * launches the function on the IO thread to fetch the tv show details
     */
    private fun loadTvShowDetails() {
        // Create a new HttpRequestObject and set its properties
        val httpRequestObject = HttpRequestObject()
        httpRequestObject.context = context
        httpRequestObject.url = String.format(AppConstants.URL_GET_TV_SHOW_DETAILS, id.toString())
        httpRequestObject.setOnHttpRequestListener(object : OnHttpRequestListener {
            // Reimplement the abstract implementation to execute when
            // the request resolves to success
            override fun onHttpRequestSuccess(responseCode: Int, data: String?) {
                onSuccess(data)
            }

            // Reimplement the abstract implementation to execute when
            // the request resolves to failure
            override fun onHttpRequestFailure(errorCode: Int, errorMsg: String?) {
                onFailure(errorMsg)
            }
        })

        if (AppUtils.isConnectivityAvailable(context)) {
            /*
                Launch a new coroutine to handle the asynchronous network calls
                Use withContext to call the functions serially one after the other.
             */
            launch {
                try {
                    val responseJson = withContext(Dispatchers.IO) {
                        GetApiResponse.invokeRemoteApi(httpRequestObject)
                    }
                    withContext(Dispatchers.IO) {
                        GetApiResponse.parseResponseAndInvokeCallback(
                            httpRequestObject,
                            responseJson
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            AppDialogs.singleActionDialog(
                context,
                AppConstants.ERROR_CONNECTION_PROBLEM,
                AppConstants.ERROR_NO_INTERNET_CONNECTION,
                fun() { finish() }
            )
        }
    }

    /**
     * Function to execute once the API call resolves to success. This function is dispatched on
     * to the main thread. Function takes in the [data] to be parsed to get the API response
     */
    private fun onSuccess(data: String?) {
        MainScope().launch {
            withContext(Dispatchers.Main) {
                AppUtils.visibilityController(
                    arrayOf(detailsScrollView),
                    arrayOf(progressBar, noDetailsTextView)
                )
                getTvShowDetailsList(data)
            }
        }
    }

    /**
     * Function to execute once the API call resolves to failure. This function is dispatched on
     * to the main thread. Function takes in the [errorMsg] to be displayed in the alert popup
     */
    private fun onFailure(errorMsg: String?) {
        MainScope().launch {
            withContext(Dispatchers.Main) {
                AppUtils.visibilityController(
                    arrayOf(noDetailsTextView),
                    arrayOf(progressBar, detailsScrollView)
                )
                // Show the error message as a popup
                AppDialogs.singleActionDialog(
                    context,
                    AppConstants.ERROR_TITLE,
                    errorMsg,
                    fun() {}
                )
            }
        }
    }

    /**
     * Function to parse the tv show details using the [data] received from the API response
     */
    private fun getTvShowDetailsList(data: String?) {
        try {
            val jsonObject = JSONObject(data!!)

            name = jsonObject[AppConstants.JSON_TAG_NAME] as String
            imageUrl = String.format(
                AppConstants.IMAGE_URL_PREFIX,
                jsonObject[AppConstants.JSON_TAG_POSTER_PATH] as String
            )
            val genres = getGenres(jsonObject.getJSONArray(AppConstants.JSON_TAG_GENRES))
            val popularity = jsonObject[AppConstants.JSON_TAG_POPULARITY].toString()
            val firstAirDate =
                AppUtils.formatDate(jsonObject[AppConstants.JSON_TAG_FIRST_AIR_DATE] as String)
            val overview = jsonObject[AppConstants.JSON_TAG_OVERVIEW] as String
            val numberOfEpisodes = jsonObject[AppConstants.JSON_TAG_NUMBER_OF_EPISODES].toString()
            val numberOfSeasons = jsonObject[AppConstants.JSON_TAG_NUMBER_OF_SEASONS].toString()
            val status = jsonObject[AppConstants.JSON_TAG_STATUS] as String
            val type = jsonObject[AppConstants.JSON_TAG_TYPE] as String
            val voteAverage = jsonObject[AppConstants.JSON_TAG_VOTE_AVERAGE].toString()
            val voteCount = jsonObject[AppConstants.JSON_TAG_VOTE_COUNT].toString()

            populateUI(
                name,
                imageUrl,
                genres,
                popularity,
                firstAirDate,
                overview,
                numberOfEpisodes,
                numberOfSeasons,
                status,
                type,
                voteAverage,
                voteCount
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Function to convert the genres json array to an array of strings
     */
    private fun getGenres(jsonArray: JSONArray): Array<String> {
        val genres = mutableListOf<String>()

        for (i in 0 until jsonArray.length()) {
            genres.add(jsonArray.getJSONObject(i)[AppConstants.JSON_TAG_NAME] as String)
        }

        return genres.toTypedArray()
    }

    /**
     * Function to populate the UI elements with the data
     */
    @SuppressLint("InflateParams")
    private fun populateUI(
        name: String,
        imageUrl: String,
        genres: Array<String>,
        popularity: String,
        firstAirDate: String,
        overview: String,
        numberOfEpisodes: String,
        numberOfSeasons: String,
        status: String,
        type: String,
        voteAverage: String,
        voteCount: String
    ) {
        Glide.with(context).load(imageUrl).into(posterImageView)
        nameTextView.text = name
        overviewTextView.text = overview
        popularityTextView.text = popularity
        firstAirDateTextView.text = firstAirDate.replace("\n", ", ")
        seasonsTextView.text = numberOfSeasons
        episodesTextView.text = numberOfEpisodes
        statusTextView.text = status
        typeTextView.text = type
        voteAverageTextView.text = voteAverage
        voteCountTextView.text = voteCount

        // For each genre item we have, we inflate a new view with a textview and add that to
        // the layout
        for (genre in genres) {
            val view: View = LayoutInflater.from(context).inflate(R.layout.genre_item, null, false)
            val textView = view.findViewById<TextView>(R.id.genre_text_view)
            textView.text = genre
            genreLayout.addView(view)
        }
    }
}
