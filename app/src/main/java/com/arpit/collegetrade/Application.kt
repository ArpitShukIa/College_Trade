package com.arpit.collegetrade

import android.app.Application
import android.os.AsyncTask
import android.util.Log
import com.arpit.collegetrade.data.AdRepository
import com.arpit.collegetrade.data.DefaultAdRepository
import com.arpit.collegetrade.data.User
import com.arpit.collegetrade.data.chats.ChatRepository
import com.arpit.collegetrade.data.chats.DefaultChatRepository
import io.tempo.Tempo

class Application : Application() {

    private val TAG = "TAG Application"

    var currentUser = User()
    val adRepository: AdRepository by lazy { DefaultAdRepository }
    val chatRepository: ChatRepository by lazy { DefaultChatRepository }

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