package com.example.collegetrade

import android.app.Application
import android.os.AsyncTask
import android.util.Log
import com.example.collegetrade.data.AdRepository
import com.example.collegetrade.data.DefaultAdRepository
import com.instacart.library.truetime.TrueTime

class Application : Application() {

    private val TAG = "TAG Application"

    var currentUserId = ""
    var currentUserName = "Anonymous"
    val repository: AdRepository by lazy { DefaultAdRepository }
    var trueTimeAvailable = false

    override fun onCreate() {
        super.onCreate()
        AsyncTask.execute {
            trueTimeAvailable = try {
                TrueTime.build().initialize()
                true
            } catch (e: Exception) {
                Log.d(TAG, "onCreate: ${e.stackTrace}")
                false
            }
        }
    }
}