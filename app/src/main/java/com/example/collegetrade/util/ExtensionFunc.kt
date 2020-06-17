package com.example.collegetrade.util

import android.app.Activity
import androidx.fragment.app.Fragment
import com.example.collegetrade.Application
import com.example.collegetrade.ViewModelFactory

fun Fragment.getViewModelFactory(): ViewModelFactory {
    val application = (requireContext().applicationContext as Application)
    return ViewModelFactory(application)
}

fun Activity.getViewModelFactory(): ViewModelFactory {
    val application = application as Application
    return ViewModelFactory(application)
}