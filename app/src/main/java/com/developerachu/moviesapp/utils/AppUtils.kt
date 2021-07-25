@file:Suppress("DEPRECATION")

package com.developerachu.moviesapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import java.text.SimpleDateFormat
import java.util.*

object AppUtils {
    /**
     * Checks whether the device has an active internet connection or not.
     * Returns the active connection state using the given [context]
     */
    fun isConnectivityAvailable(context: Context?): Boolean {
        if (context == null) return false
        // Access the internet connectivity services
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Some of the features in the connectivity manager is deprecated in from API version 29.
        // Therefore, check the version of the current device.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // When the version code is higher than or equal to 29 (Q), access the network
            // capabilities from the active network
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                // If the device use cellular or wifi capability, return true. Else, return false
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                }
                return false
            }
        } else {
            // If the version code is less than 29 (Q)
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) return true
        }
        return false
    }

    fun visibilityController(visibleWidgets: Array<View>, hiddenWidgets: Array<View>) {
        for (element in visibleWidgets) {
            element.visibility = View.VISIBLE
        }

        for (element in hiddenWidgets) {
            element.visibility = View.GONE
        }
    }

    /**
     * Function converts the date string received from the API response to MMM dd\nyyyy format
     * to be used in the app. Function takes in [date] in yyyy-MM-dd format
     */
    fun formatDate(date: String): String {
        val parsedDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date)
        return SimpleDateFormat("MMM dd\nyyyy", Locale.US).format(parsedDate!!)
    }
}