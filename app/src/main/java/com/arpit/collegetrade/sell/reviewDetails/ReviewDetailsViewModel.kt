package com.arpit.collegetrade.sell.reviewDetails

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arpit.collegetrade.Application
import com.arpit.collegetrade.Event
import com.arpit.collegetrade.data.Ad
import com.arpit.collegetrade.sell.reviewDetails.Actions.*
import com.arpit.collegetrade.util.getCurrentDate
import id.zelory.compressor.Compressor
import io.tempo.Tempo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

enum class Actions { EMPTY_NAME, UPLOAD_STARTED, UPLOAD_SUCCEEDED, UPLOAD_FAILED, IMAGE_LOADING_FAILED }

class ReviewDetailsViewModel(private val application: Application) : ViewModel() {

    private val TAG = "TAG ReviewDetailsModel"

    private val userId = application.currentUserId
    private val repository = application.repository

    val name = MutableLiveData(application.currentUserName)

    private val _sellerImage = MutableLiveData("")
    val sellerImage: LiveData<String> = _sellerImage

    private val _action = MutableLiveData<Event<Actions>>()
    val action: LiveData<Event<Actions>> = _action

    private lateinit var ad: Ad

    var buttonEnabled = " ".toUri()

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
                _action.value = Event(UPLOAD_FAILED)
            }
        }
    }

    private fun getAd(): Ad {
        ad.sellerName = name.value!!
        ad.sellerPhoto = sellerImage.value!!

        if (ad.timestamp == 0L) {
            val timestamp = Tempo.now() ?: System.currentTimeMillis()

            ad.sellerId = userId
            ad.timestamp = timestamp
            ad.datePosted = getCurrentDate()
        }
        return ad
    }

    fun getAdDetails(ad: Ad) {
        this.ad = ad
        if (ad.sellerName.isNotEmpty()) {
            name.value = ad.sellerName
            _sellerImage.value = ad.sellerPhoto
        }
    }

    fun compressImage(file: File) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                buttonEnabled = "".toUri()
                try {
                    val compressedImageFile =
                        Compressor.compress(application.applicationContext, file)
                    val uri = Uri.fromFile(compressedImageFile)
                    _sellerImage.postValue(uri.toString())
                } catch (e: Exception) {
                    _action.value = Event(IMAGE_LOADING_FAILED)
                } finally {
                    buttonEnabled = " ".toUri()
                }
            }
        }
    }
}