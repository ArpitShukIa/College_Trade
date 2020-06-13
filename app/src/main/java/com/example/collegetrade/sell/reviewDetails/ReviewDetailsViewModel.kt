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
import kotlinx.coroutines.launch

enum class Actions { EMPTY_NAME, UPLOAD_STARTED, UPLOAD_SUCCEEDED, UPLOAD_FAILED }

class ReviewDetailsViewModel(application: Application) : ViewModel() {

    private val TAG = "TAG ReviewDetailsModel"

    private val userId = application.currentUserId
    private val repository = application.repository

    val name = MutableLiveData(application.currentUserName)

    private val _action = MutableLiveData<Event<Actions>>()
    val action: LiveData<Event<Actions>> = _action

    private lateinit var adDetails: Array<String>
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
        return Ad(
            sellerName = name.value!!,
            sellerId = userId,
            category = adDetails[0],
            subCategory = adDetails[1],
            title = adDetails[2],
            description = adDetails[3],
            image = adDetails[4],
            price = adDetails[5],
            dataPosted = getCurrentDate()
        )
    }

    fun getAdDetails(details: Array<String>) {
        adDetails = details
    }

}