package com.arpit.collegetrade

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.arpit.collegetrade.data.AdRepository
import com.arpit.collegetrade.data.DefaultAdRepository
import com.arpit.collegetrade.data.User
import com.arpit.collegetrade.data.chats.ChatRepository
import com.arpit.collegetrade.data.chats.DefaultChatRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.tempo.Tempo
import timber.log.Timber
import timber.log.Timber.DebugTree

class Application : Application() {

    var currentUser = User()
    var deviceToken = ""
    var userId = ""
    var isActivityRunning = false
    var activeChatId = ""
    val adRepository: AdRepository by lazy { DefaultAdRepository }
    val chatRepository: ChatRepository by lazy { DefaultChatRepository }

    val buyUnreadCount = MutableLiveData(0)
    val sellUnreadCount = MutableLiveData(0)

    override fun onCreate() {
        super.onCreate()
        userId = Firebase.auth.currentUser?.uid ?: ""
        if (BuildConfig.DEBUG) Timber.plant(DebugTree())
        Tempo.initialize(this)
    }
}