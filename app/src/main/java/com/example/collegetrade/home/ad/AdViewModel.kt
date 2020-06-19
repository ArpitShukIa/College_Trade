package com.example.collegetrade.home.ad

import androidx.lifecycle.ViewModel
import com.example.collegetrade.Application
import com.example.collegetrade.data.Ad

class AdViewModel(application: Application) : ViewModel() {

    private val repository = application.repository

    suspend fun getAd(id: String): Ad? {
        return repository.getAdFromId(id)
    }

}