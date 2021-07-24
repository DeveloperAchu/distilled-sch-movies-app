package com.developerachu.moviesapp.webservices

import com.developerachu.moviesapp.utils.AppConstants
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object GetApiResponse {
    /**
     * Invokes the remote URL that is given in the [httpRequestObject]
     * Returns the complete response as a string
     */
    fun invokeRemoteApi(httpRequestObject: HttpRequestObject): String {
        // Initialize the StringBuilder to build the response string
        val response: StringBuilder = StringBuilder("")

        try {
            // Create a URL object using the url variable in the HttpRequestObject
            val url = URL(httpRequestObject.url)
            // Open a new connection and set the timeouts and content type
            val conn = url.openConnection() as HttpURLConnection
            conn.readTimeout = 20000
            conn.connectTimeout = 25000
            conn.setRequestProperty("Content-Type", "application/json")

            val responseCode = conn.responseCode
            /*
                If the response code from the connection is 200 OK, read the connection's input
                stream and and build the response string
            */
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val input = BufferedReader(InputStreamReader(conn.inputStream))
                var inputLine: String?
                while (input.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }
                input.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return response.toString()
    }

    /**
     * Parse [responseJson] received from the remote API using [httpRequestObject]
     */
    fun parseResponseAndInvokeCallback(
        httpRequestObject: HttpRequestObject,
        responseJson: String
    ) {
        if (responseJson.isEmpty()) {
            // If the responseJson is empty, invoke failure callback and return
            return httpRequestObject.onFailure(
                AppConstants.RESPONSE_CODE_NO_CONTENT,
                AppConstants.ERROR_NO_RESPONSE
            )
        }
        try {
            /*
                Convert the responseJson string to a Json object and initialize the
                status code as 200. Invoke the success callback
            */
            val apiResponse = JSONObject(responseJson)
            val statusCode = AppConstants.RESPONSE_CODE_SUCCESS

            httpRequestObject.onSuccess(statusCode, apiResponse.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            httpRequestObject.onFailure(
                AppConstants.RESPONSE_CODE_NO_CONTENT,
                AppConstants.ERROR_SOMETHING_WENT_WRONG
            )
        }
    }
}