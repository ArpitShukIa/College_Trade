package com.example.collegetrade.sell.reviewDetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegetrade.Application
import com.example.collegetrade.Event
import com.example.collegetrade.data.Ad
import com.example.collegetrade.sell.reviewDetails.Actions.*
import com.example.collegetrade.util.getCurrentDate
import io.tempo.Tempo
import kotlinx.coroutines.launch

enum class Actions { EMPTY_NAME, UPLOAD_STARTED, UPLOAD_SUCCEEDED, UPLOAD_FAILED }

class ReviewDetailsViewModel(private val application: Application) : ViewModel() {

    private val TAG = "TAG ReviewDetailsModel"

    private val userId = application.currentUserId
    private val repository = application.repository

    val name = MutableLiveData(application.currentUserName)

    private val _action = MutableLiveData<Event<Actions>>()
    val action: LiveData<Event<Actions>> = _action

    private lateinit var ad: Ad

    fun postAd() {
        if (name.value.isNullOrEmpty()) {
            _action.value = Event(EMPTY_NAME)
            return
        }
        _action.value = Event(UPLOAD_STARTED)

        val ad = getAd()
        viewModelScope.launch {
            try {
                repository.postAd(ad)
                _action.value = Event(UPLOAD_SUCCEEDED)
            } catch (e: Exception) {
                Log.e(TAG, "postAd: ${e.stackTrace}", e)
                _action.value = Event((UPLOAD_FAILED))
            }
        }
    }

    private fun getAd(): Ad {
        ad.sellerName = name.value!!

        if(ad.timestamp == 0L) {
            val timestamp = if (application.trueTimeAvailable)
                Tempo.now()!!
            else
                System.currentTimeMillis()

            ad.sellerId = userId
            ad.timestamp = timestamp
            ad.datePosted = getCurrentDate()
        }
        return ad
    }

    fun getAdDetails(ad: Ad) {
        this.ad = ad
        if(ad.sellerName.isNotEmpty())
            name.value = ad.sellerName
    }
}