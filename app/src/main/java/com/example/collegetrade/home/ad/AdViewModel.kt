package com.example.collegetrade.home.ad

import android.util.Log
import androidx.lifecycle.*
import com.example.collegetrade.Application
import com.example.collegetrade.Event
import com.example.collegetrade.data.Ad
import kotlinx.coroutines.launch

class AdViewModel(application: Application) : ViewModel() {

    private val repository = application.repository
    private val userId = application.currentUserId

    val ad = MutableLiveData(Ad())
    val isSeller = ad.map { it.sellerId == userId }

    private val _goBackEvent = MutableLiveData<Event<Boolean>>()
    val goBackEvent: LiveData<Event<Boolean>> = _goBackEvent

    fun updateAd(ad: Ad) {
        this.ad.value = ad
        if(ad.sellerId != userId)
            repository.addToViewersList(ad.id, userId)
    }

    fun getAd(id: String) {
        viewModelScope.launch {
            repository.getAdFromId(id).also {
                it?.apply {
                    isLiked = repository.isLiked(id, userId)
                    likesCount = repository.getLikesCount(id)
                    viewsCount = repository.getViewsCount(id)
                    updateAd(this)
                }
                _goBackEvent.value = Event(it == null)
            }
        }
    }

    fun updateFavList(addToFav: Boolean) {
        try {
            repository.updateFavList(ad.value!!, userId, addToFav)
        } catch (e: Exception) {
            Log.e("TAG", "updateFavList: ${e.stackTrace}", e)
        }
    }
}