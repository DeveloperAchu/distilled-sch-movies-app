package com.developerachu.moviesapp.webservices

import android.content.Context
import com.developerachu.moviesapp.interfaces.OnHttpRequestListener

/**
 * Class to represent an HTTP request object with properties
 * [url],
 * [context],
 * [onHttpRequestListener]
 */
class HttpRequestObject {
    var context: Context? = null
    var url: String? = null
    private var onHttpRequestListener: OnHttpRequestListener? = null

    /**
     * Function sets the OnHttpRequestListener object with its abstract function implementations
     */
    fun setOnHttpRequestListener(mOnHttpRequestListener: OnHttpRequestListener?) {
        onHttpRequestListener = mOnHttpRequestListener
    }

    /**
     * Handles the callback to onHttpRequestSuccess function implementation of
     * OnHttpRequestListener using [responseCode] and [response]
     */
    fun onSuccess(responseCode: Int, response: String?) {
        onHttpRequestListener?.onHttpRequestSuccess(responseCode, response)
    }

    /**
     * Handles the callback to onHttpRequestFailure function implementation of
     * OnHttpRequestListener using [errorCode] and [response]
     */
    fun onFailure(errorCode: Int, response: String?) {
        onHttpRequestListener?.onHttpRequestFailure(errorCode, response)
    }
}