package com.arpit.collegetrade

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatViewModel(application: Application) : ViewModel() {

    private val repository = application.chatRepository
    private val currentUser = application.currentUser

    private val _name = MutableLiveData("")
    val name: LiveData<String> = _name

    private val _image = MutableLiveData("")
    val image: LiveData<String> = _image

    private val _lastSeen = MutableLiveData("")
    val lastSeen: LiveData<String> = _lastSeen

    fun setDetails(name: String, image: String) {
        _name.value = name
        _image.value = image
    }

}