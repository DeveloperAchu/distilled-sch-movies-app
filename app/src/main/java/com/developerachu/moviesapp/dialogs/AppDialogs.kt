package com.developerachu.moviesapp.dialogs

import android.app.AlertDialog
import android.content.Context

object AppDialogs {
    /**
     * Builds a single action dialog box using the given
     * [context],
     * [title],
     * [message],
     * [function]
     */
    fun singleActionDialog(
        context: Context,
        title: String?,
        message: String?,
        function: () -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setNegativeButton("OK") { dialog, _ ->
                dialog.dismiss()
                function()
            }
        builder.create().show()
    }
}