package com.example.collegetrade.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar.*

fun getErrorMessage(s: String, n: Int): String? {
    val l = s.trim()
        .replace("\n", "")
        .replace("\\s+".toRegex(), " ")
        .length

    return when {
        l < 10 -> "Minimum 10 characters are required"
        l > n -> "Maximum $n characters are allowed"
        else -> null
    }
}

fun showToast(context: Context, msg: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, msg, length).show()
}

fun showSnackBar(view: View, msg: String, length: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(view, msg, length).setAnchorView(view).show()
}

fun hideKeyboard(activity: Activity, view: View) {
    val imm =
        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun getCurrentDate() : String {
    val calendar = getInstance()
    val date = calendar.get(DAY_OF_MONTH)
    val month = calendar.get(MONTH) + 1
    val year = calendar.get(YEAR)

    return "$date/$month/$year"
}