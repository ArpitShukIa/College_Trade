package com.arpit.collegetrade.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

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

fun adjustResize(activity: Activity, required: Boolean) {
    if (required)
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    else
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
}