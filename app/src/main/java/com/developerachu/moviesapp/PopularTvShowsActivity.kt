package com.developerachu.moviesapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.developerachu.moviesapp.dialogs.AppDialogs
import com.developerachu.moviesapp.interfaces.OnHttpRequestListener
import com.developerachu.moviesapp.models.TvShow
import com.developerachu.moviesapp.utils.AppConstants
import com.developerachu.moviesapp.utils.AppUtils
import com.developerachu.moviesapp.webservices.GetApiResponse
import com.developerachu.moviesapp.webservices.HttpRequestObject
import kotlinx.coroutines.*
import org.json.JSONObject
import kotlin.coroutines.CoroutineContext

/**
 * Activity in which popular tv shows are loaded and displayed.
 * Extends both [AppCompatActivity] and [CoroutineScope]
 */
class PopularTvShowsActivity : AppCompatActivity(), CoroutineScope {
    // Initialize the context to the current activity
    val context = this

    // Create a coroutine context
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    // Create a job variable to handle the background tasks
    private lateinit var job: Job

    // Recyclerview can be initialized lazily at the time of accessing it
    private val popularTvShowsRecyclerView by lazy {
        findViewById<RecyclerView>(R.id.popular_tv_shows_recycler_view)
    }

    // Create an array list to hold the popular tv shows
    private val popularTvShowsList = ArrayList<TvShow>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popular_tv_shows)

        // On creating the activity, the job variable is initialized
        job = Job()
        clearAndReloadTvShows()
    }

    override fun onDestroy() {
        // The job gets cancelled once the activity is destroyed
        job.cancel()
        super.onDestroy()
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
                getPopularTvShowsList(data)
            }

            // Callback function to execute when the request resolves to failure
            override fun onHttpRequestFailure(errorCode: Int, errorMsg: String?) {
                // Show the error message as a popup
                AppDialogs.singleActionDialog(
                    context,
                    AppConstants.ERROR_TITLE,
                    errorMsg,
                    fun() {}
                )
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
                            currentTvShow[AppConstants.JSON_TAG_POSTER_PATH] as String,
                            currentTvShow[AppConstants.JSON_TAG_POPULARITY],
                            currentTvShow[AppConstants.JSON_TAG_VOTE_AVERAGE]
                        )
                    )
                }
                setMyStoriesListAdapter()
            } else {
//                myStoriesRecyclerView.setVisibility(View.GONE)
//                noItemsLabelTextView.setVisibility(View.VISIBLE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * TODO: function comment
     */
    private fun setMyStoriesListAdapter() {
        println(AppConstants.TAG_NAME + popularTvShowsList.size.toString())
    }
}
