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
 * Extends both [AppCompatActivity] and [CoroutineScope]
 */
class PopularTvShowsActivity : AppCompatActivity(), CoroutineScope, OnTvShowClickListener {
    // Create a coroutine context
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    // Initialize the context to the current activity
    private val context = this

    // Create an array list to hold the popular tv shows
    private val popularTvShowsList = ArrayList<TvShow>()

    // Create a job variable to handle the background tasks
    private lateinit var job: Job

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

    private fun init() {
        // On creating the activity, the job variable is initialized
        job = Job()
        initUiElementsAndVisibility()
        clearAndReloadTvShows()
    }

    /**
     * Function to initialize the UI elements and to initialize their visibility
     */
    private fun initUiElementsAndVisibility() {
        popularTvShowsRecyclerView = findViewById(R.id.popular_tv_shows_recycler_view)
        progressBar = findViewById(R.id.progress_bar)
        noContentTextView = findViewById(R.id.no_content_text_view)
        showProgressBar(true)
    }

    /**
     * Function to control the visibility of progressbar and recyclerview
     */
    private fun showProgressBar(visibility: Boolean) {
        if (visibility) {
            popularTvShowsRecyclerView.visibility = View.GONE
            noContentTextView.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
            popularTvShowsRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun showNoContentText() {
        popularTvShowsRecyclerView.visibility = View.GONE
        progressBar.visibility = View.GONE
        noContentTextView.visibility = View.VISIBLE
    }

    /**
     * Function to clear the array list holding the popular tv shows data
     * and initializing an HttpRequestObject
     */
    private fun clearAndReloadTvShows() {
        popularTvShowsList.clear()

        // Create a new HttpRequestObject and set its properties
        val httpRequestObject = HttpRequestObject()
        httpRequestObject.context = context
        httpRequestObject.url = AppConstants.URL_GET_POPULAR_TV_SHOWS
        httpRequestObject.setOnHttpRequestListener(object : OnHttpRequestListener {
            // Callback function to execute when the request resolves to success
            override fun onHttpRequestSuccess(responseCode: Int, data: String?) {
                onSuccess(data)
            }

            // Callback function to execute when the request resolves to failure
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
                withContext(Dispatchers.IO) {
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

    private fun onSuccess(data: String?) {
        MainScope().launch {
            withContext(Dispatchers.Main) {
                showProgressBar(false)
                getPopularTvShowsList(data)
            }
        }
    }

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
     * Callback function to parse the tv shows using the [data] received from the API response
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
                            String.format(
                                AppConstants.IMAGE_URL_PREFIX,
                                currentTvShow[AppConstants.JSON_TAG_POSTER_PATH] as String
                            ),
                            String.format(
                                AppConstants.POPULARITY,
                                currentTvShow[AppConstants.JSON_TAG_POPULARITY].toString()
                            ),
                            currentTvShow[AppConstants.JSON_TAG_VOTE_AVERAGE].toString(),
                            formatDate(currentTvShow[AppConstants.JSON_TAG_FIRST_AIR_DATE] as String)

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

    private fun formatDate(date: String): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val convertedPattern = SimpleDateFormat("MMM dd\nyyyy", Locale.US)
        val parsedDate = simpleDateFormat.parse(date)
        return convertedPattern.format(parsedDate!!)
    }

    /**
     * TODO: function comment
     */
    private fun setMyStoriesListAdapter() {
        val popularTvShowsListAdapter = PopularTvShowsListAdapter(context, popularTvShowsList)
        popularTvShowsRecyclerView.layoutManager = LinearLayoutManager(context)
        popularTvShowsListAdapter.setTvShowClickListener(this)
        popularTvShowsRecyclerView.adapter = popularTvShowsListAdapter
    }

    override fun tvShowItemClicked(v: View, position: Int) {
        val intent = Intent(this, TvShowDetailsActivity::class.java)
        startActivity(intent)
    }
}
