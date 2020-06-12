package com.example.collegetrade

import android.app.Application
import com.example.collegetrade.data.AdRepository
import com.example.collegetrade.data.DefaultAdRepository

class Application : Application() {
    var currentUserId = ""
    var currentUserName = "Anonymous"
    val repository: AdRepository
        get() = DefaultAdRepository
}