package com.arpit.collegetrade.util

import android.app.Activity
import androidx.fragment.app.Fragment
import com.arpit.collegetrade.Application
import com.arpit.collegetrade.ViewModelFactory

fun Fragment.getViewModelFactory(): ViewModelFactory {
    val application = (requireActivity().application as Application)
    return ViewModelFactory(application)
}

fun Activity.getViewModelFactory(): ViewModelFactory {
    val application = application as Application
    return ViewModelFactory(application)
}