package com.developerachu.moviesapp.utils

/**
 * This object holds the common string constants and URLs that are being used in other files
 */
object AppConstants {
    // Constants
    const val TAG_NAME = "Movies app log: "

    // Urls
    const val URL_GET_POPULAR_TV_SHOWS =
        "https://api.themoviedb.org/3/tv/top_rated?api_key=25a8f80ba018b52efb64f05140f6b43c&language=en-US&page=1"

    // Json tags
    const val JSON_TAG_RESULTS = "results"
    const val JSON_TAG_ID = "id"
    const val JSON_TAG_NAME = "name"
    const val JSON_TAG_POSTER_PATH = "poster_path"
    const val JSON_TAG_POPULARITY = "popularity"
    const val JSON_TAG_VOTE_AVERAGE = "vote_average"

    // Error messages
    const val ERROR_TITLE = "Error"
    const val ERROR_CONNECTION_PROBLEM = "Connection Problem"
    const val ERROR_NO_INTERNET_CONNECTION =
        "This application requires internet connection to work properly."
    const val ERROR_NO_RESPONSE = "No response from server"
    const val ERROR_SOMETHING_WENT_WRONG = "Something went wrong"

    // Response codes
    const val RESPONSE_CODE_SUCCESS = 200
    const val RESPONSE_CODE_NO_CONTENT = 204
}
