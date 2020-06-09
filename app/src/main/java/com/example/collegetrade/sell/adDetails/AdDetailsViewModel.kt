package com.example.collegetrade.sell.adDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.collegetrade.Event
import com.example.collegetrade.util.getErrorMessage

class AdDetailsViewModel : ViewModel() {

    val title = MutableLiveData<String>()
    val desc = MutableLiveData<String>()

    private val _isInfoValid = MutableLiveData<Event<Boolean>>()
    val isInfoValid: LiveData<Event<Boolean>> = _isInfoValid

    val titleError: LiveData<String> = Transformations.map(title) {
        getErrorMessage(it, 50)
    }

    val descError: LiveData<String> = Transformations.map(desc) {
        getErrorMessage(it, 2000)
    }

    init {
        title.value = ""
        desc.value = ""
    }

    fun navigate() {
        _isInfoValid.value = Event(titleError.value == null && descError.value == null)
    }

}