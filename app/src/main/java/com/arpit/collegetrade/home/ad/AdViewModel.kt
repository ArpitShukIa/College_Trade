package com.arpit.collegetrade.home.ad

import androidx.lifecycle.*
import com.arpit.collegetrade.Application
import com.arpit.collegetrade.Event
import com.arpit.collegetrade.data.Ad
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
        if (ad.sellerId != userId)
            viewModelScope.launch {
                repository.addToViewersList(ad.id, userId)
            }
    }

    fun getAd(id: String) {
        viewModelScope.launch {
            repository.getAdFromId(id).also {
                it?.apply {
                    likeTime = this.likers[userId] ?: 0
                    likesCount = this.likers.size
                    updateAd(this)
                }
                _goBackEvent.value = Event(it == null)
            }
        }
    }
}