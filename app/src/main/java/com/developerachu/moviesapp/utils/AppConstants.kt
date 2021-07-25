package com.developerachu.moviesapp.utils

/**
 * This object holds the common string constants and URLs that are being used in other files
 */
object AppConstants {
    const val NAME = "name"
    const val IMAGE = "imageUrl"

    // Constants
    const val POPULAR_SHOW_ID = "id"
    const val POPULARITY = "Popularity: %s"
    const val IMAGE_URL_PREFIX = "https://image.tmdb.org/t/p/w500%s"

    // Urls
    const val URL_GET_POPULAR_TV_SHOWS =
        "https://api.themoviedb.org/3/tv/top_rated?api_key=25a8f80ba018b52efb64f05140f6b43c&language=en-US&page=1"
    const val URL_GET_TV_SHOW_DETAILS =
        "https://api.themoviedb.org/3/tv/%s?api_key=25a8f80ba018b52efb64f05140f6b43c&language=en-US"

    // Json tags
    const val JSON_TAG_RESULTS = "results"
    const val JSON_TAG_ID = "id"
    const val JSON_TAG_NAME = "name"
    const val JSON_TAG_POSTER_PATH = "poster_path"
    const val JSON_TAG_POPULARITY = "popularity"
    const val JSON_TAG_VOTE_AVERAGE = "vote_average"
    const val JSON_TAG_FIRST_AIR_DATE = "first_air_date"
    const val JSON_TAG_GENRES = "genres"
    const val JSON_TAG_OVERVIEW = "overview"
    const val JSON_TAG_NUMBER_OF_EPISODES = "number_of_episodes"
    const val JSON_TAG_NUMBER_OF_SEASONS = "number_of_seasons"
    const val JSON_TAG_STATUS = "status"
    const val JSON_TAG_TYPE = "type"
    const val JSON_TAG_VOTE_COUNT = "vote_count"

    // Response codes
    const val RESPONSE_CODE_SUCCESS = 200
    const val RESPONSE_CODE_NO_CONTENT = 204

    // Error messages
    const val ERROR_TITLE = "Error"
    const val ERROR_CONNECTION_PROBLEM = "Connection Problem"
    const val ERROR_NO_INTERNET_CONNECTION =
        "This application requires internet connection to work properly."
    const val ERROR_NO_RESPONSE = "No response from server"
    const val ERROR_SOMETHING_WENT_WRONG = "Something went wrong"
}
