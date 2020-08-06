package com.arpit.collegetrade

import android.app.Application
import com.arpit.collegetrade.data.AdRepository
import com.arpit.collegetrade.data.DefaultAdRepository
import com.arpit.collegetrade.data.User
import com.arpit.collegetrade.data.chats.ChatRepository
import com.arpit.collegetrade.data.chats.DefaultChatRepository
import io.tempo.Tempo
import timber.log.Timber
import timber.log.Timber.DebugTree

class Application : Application() {

    var currentUser = User()
    var chatPersonId = ""
    val adRepository: AdRepository by lazy { DefaultAdRepository }
    val chatRepository: ChatRepository by lazy { DefaultChatRepository }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG)
            Timber.plant(DebugTree())
        Tempo.initialize(this)
    }
}