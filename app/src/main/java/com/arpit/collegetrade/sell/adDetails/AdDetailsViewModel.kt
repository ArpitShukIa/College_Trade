package com.arpit.collegetrade.sell.adDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.arpit.collegetrade.Event
import com.arpit.collegetrade.data.Ad
import com.arpit.collegetrade.util.getErrorMessage

class AdDetailsViewModel : ViewModel() {

    val title = MutableLiveData("")
    val desc = MutableLiveData("")

    private val _isInfoValid = MutableLiveData<Event<Boolean>>()
    val isInfoValid: LiveData<Event<Boolean>> = _isInfoValid

    val titleError: LiveData<String> = Transformations.map(title) {
        getErrorMessage(it, 50)
    }

    val descError: LiveData<String> = Transformations.map(desc) {
        getErrorMessage(it, 2000)
    }

    fun navigate() {
        _isInfoValid.value = Event(titleError.value == null && descError.value == null)
    }

    fun setData(ad: Ad) {
        title.value = ad.title
        desc.value = ad.description
    }

}