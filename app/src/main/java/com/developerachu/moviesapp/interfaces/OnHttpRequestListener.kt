package com.developerachu.moviesapp.interfaces

/**
 * Create an interface with abstract functions to be implemented later when the network call request
 * resolves to success or failure
 */
interface OnHttpRequestListener {
    /**
     * Function to be invoked with [responseCode] and [data] when the network request resolves
     * to a success
     */
    fun onHttpRequestSuccess(responseCode: Int, data: String?)

    /**
     * Function to be invoked with [errorCode] and [errorMsg] when the network request
     * resolves to a failure
     */
    fun onHttpRequestFailure(errorCode: Int, errorMsg: String?)
}