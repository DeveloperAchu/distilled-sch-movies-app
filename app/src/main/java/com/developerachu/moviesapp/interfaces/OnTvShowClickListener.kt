package com.developerachu.moviesapp.interfaces

import android.view.View

/**
 * Create an interface with abstract function to be implemented later when an item is clicked in
 * from the tv shows list
 */
interface OnTvShowClickListener {
    /**
     * Function to be invoked with [view] and [position] of the clicked tv show item
     */
    fun tvShowItemClicked(view: View, position: Int)
}