package com.developerachu.moviesapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.developerachu.moviesapp.adapters.PopularTvShowsListAdapter
import com.developerachu.moviesapp.dialogs.AppDialogs
import com.developerachu.moviesapp.interfaces.OnHttpRequestListener
import com.developerachu.moviesapp.interfaces.OnTvShowClickListener
import com.developerachu.moviesapp.models.TvShow
import com.developerachu.moviesapp.utils.AppConstants
import com.developerachu.moviesapp.utils.AppUtils
import com.developerachu.moviesapp.webservices.GetApiResponse
import com.developerachu.moviesapp.webservices.HttpRequestObject
import kotlinx.coroutines.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

/**
 * Activity in which popular tv shows are loaded and displayed.
 * Extends [AppCompatActivity], [CoroutineScope] and [OnTvShowClickListener]
 */
class PopularTvShowsActivity : AppCompatActivity(), CoroutineScope, OnTvShowClickListener {
    // Create a job variable to handle the background tasks
    private lateinit var job: Job

    // Create a coroutine context
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    // Initialize the context to the current activity
    private val context = this

    // Create an array list to hold the popular tv shows
    private val popularTvShowsList = ArrayList<TvShow>()

    // Create a variable to hold the recyclerview widget
    private lateinit var popularTvShowsRecyclerView: RecyclerView

    // Create a variable to hold the progressbar widget
    private lateinit var progressBar: ProgressBar

    // Create a variable to hold the textview widget
    private lateinit var noContentTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popular_tv_shows)
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
        // The job variable is initialized here
        job = Job()
        initUiElements()
        clearAndReloadTvShows()
    }

    /**
     * Function ot initialize the UI elements
     */
    private fun initUiElements() {
        popularTvShowsRecyclerView = findViewById(R.id.popular_tv_shows_recycler_view)
        progressBar = findViewById(R.id.progress_bar)
        noContentTextView = findViewById(R.id.no_content_text_view)
        showProgressBar(true)
    }

    /**
     * Function controls the visibility of progressbar using [visibility]
     * If [visibility] is true, progressbar is made visible and the recyclerview and textview
     * are hidden. Else, progressbar is hidden and the recyclerview only is made visible
     */
    private fun showProgressBar(visibility: Boolean) {
        if (visibility) {
            AppUtils.visibilityController(
                arrayOf(progressBar),
                arrayOf(popularTvShowsRecyclerView, noContentTextView)
            )
        } else {
            AppUtils.visibilityController(
                arrayOf(popularTvShowsRecyclerView),
                arrayOf(progressBar)
            )
        }
    }

    /**
     * Function to control the visibility of textview. This textview show a message when no data
     * is fetched from the API
     */
    private fun showNoContentText() {
        AppUtils.visibilityController(
            arrayOf(noContentTextView),
            arrayOf(popularTvShowsRecyclerView, progressBar)
        )
    }

    /**
     * Function clears the array list holding the popular tv shows data
     * and initializes an HttpRequestObject. Function launches the function on the IO
     * thread to fetch the popular tv shows list using the API
     */
    private fun clearAndReloadTvShows() {
        popularTvShowsList.clear()

        // Create a new HttpRequestObject and set its properties
        val httpRequestObject = HttpRequestObject()
        httpRequestObject.context = context
        httpRequestObject.url = AppConstants.URL_GET_POPULAR_TV_SHOWS
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
                showProgressBar(false)
                getPopularTvShowsList(data)
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
                showProgressBar(false)
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
     * Function to parse the tv shows using the [data] received from the API response
     */
    private fun getPopularTvShowsList(data: String?) {
        try {
            // Create a json object using the data and access the results json array
            val jsonObject = JSONObject(data!!)
            val resultsJsonArray = jsonObject.getJSONArray(AppConstants.JSON_TAG_RESULTS)

            var currentTvShow: JSONObject
            if (resultsJsonArray.length() != 0) {
                // Iterate through the results json array to get the current tv show json object
                for (i in 0 until resultsJsonArray.length()) {
                    currentTvShow = resultsJsonArray.getJSONObject(i)
                    // Add the current tv show object to the popularTvShowsList
                    popularTvShowsList.add(
                        TvShow(
                            currentTvShow[AppConstants.JSON_TAG_ID] as Int,
                            currentTvShow[AppConstants.JSON_TAG_NAME] as String,
                            // Image prefix URL is added here
                            String.format(
                                AppConstants.IMAGE_URL_PREFIX,
                                currentTvShow[AppConstants.JSON_TAG_POSTER_PATH] as String
                            ),
                            // Popularity string is formatted here
                            String.format(
                                AppConstants.POPULARITY,
                                currentTvShow[AppConstants.JSON_TAG_POPULARITY].toString()
                            ),
                            currentTvShow[AppConstants.JSON_TAG_VOTE_AVERAGE].toString(),
                            AppUtils.formatDate(currentTvShow[AppConstants.JSON_TAG_FIRST_AIR_DATE] as String)

                        )
                    )
                }
                setMyStoriesListAdapter()
            } else {
                showNoContentText()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Function to setup the adapter for the recyclerview.
     *
     * Creates a new instance of the custom recyclerview adapter implementation using the
     * context and popularTvShowsList.
     *
     * Creates a new liner layout manager instance for the recyclerview
     *
     * Initializes a custom click listener in the current activity context
     *
     * Finally, set the adapter of the recyclerview to the newly created custom adapter instance
     */
    private fun setMyStoriesListAdapter() {
        val popularTvShowsListAdapter = PopularTvShowsListAdapter(context, popularTvShowsList)
        popularTvShowsRecyclerView.layoutManager = LinearLayoutManager(context)
        popularTvShowsListAdapter.setTvShowClickListener(this)
        popularTvShowsRecyclerView.adapter = popularTvShowsListAdapter
    }

    /**
     * Reimplement the abstract implementation to handle the click events on the
     * tv show item in the recyclerview list. Receives [view] and [position] of the view in
     * the recyclerview.
     * Function finds the id of the clicked tv show and pass that id to the details activity
     * that is being started from here.
     */
    override fun tvShowItemClicked(view: View, position: Int) {
        val id = popularTvShowsList[position].id
        val intent = Intent(this, TvShowDetailsActivity::class.java)
        intent.putExtra(AppConstants.POPULAR_SHOW_ID, id)
        startActivity(intent)
    }
}
