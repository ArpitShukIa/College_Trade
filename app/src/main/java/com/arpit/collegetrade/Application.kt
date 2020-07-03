package com.arpit.collegetrade

import android.app.Application
import android.os.AsyncTask
import android.util.Log
import com.arpit.collegetrade.data.AdRepository
import com.arpit.collegetrade.data.DefaultAdRepository
import io.tempo.Tempo

class Application : Application() {

    private val TAG = "TAG Application"

    var currentUserId = ""
    var currentUserName = "Anonymous"
    val repository: AdRepository by lazy { DefaultAdRepository }

    override fun onCreate() {
        super.onCreate()
        AsyncTask.execute {
            try {
                Tempo.initialize(this)
            } catch (e: Exception) {
                Log.e(TAG, "onCreate: ${e.stackTrace}", e)
            }
        }
    }
}