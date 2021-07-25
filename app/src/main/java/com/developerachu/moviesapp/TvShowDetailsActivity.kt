package com.developerachu.moviesapp

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.developerachu.moviesapp.dialogs.AppDialogs
import com.developerachu.moviesapp.interfaces.OnHttpRequestListener
import com.developerachu.moviesapp.utils.AppConstants
import com.developerachu.moviesapp.utils.AppUtils
import com.developerachu.moviesapp.webservices.GetApiResponse
import com.developerachu.moviesapp.webservices.HttpRequestObject
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
        loadTvShowDetails()
    }

    /**
     * Function ot initialize the UI elements and control their visibility
     */
    private fun initUiElements() {
        progressBar = findViewById(R.id.progress_bar)
        noDetailsTextView = findViewById(R.id.no_details_text_view)
        AppUtils.visibilityController(
            arrayOf(progressBar),
            arrayOf(noDetailsTextView)
        )
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
                    arrayOf(),
                    arrayOf(progressBar)
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
                    arrayOf(),
                    arrayOf(progressBar)
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

            val name = jsonObject[AppConstants.JSON_TAG_NAME] as String
            val imageUrl = String.format(
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
     * TODO: function comment
     */
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

    }
}
