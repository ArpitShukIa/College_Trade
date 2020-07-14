package com.arpit.collegetrade.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.arpit.collegetrade.Application
import com.arpit.collegetrade.data.FirestoreDocRefLiveData
import com.arpit.collegetrade.util.getLastSeen
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChatViewModel(application: Application) : ViewModel() {

    private val repository = application.chatRepository
    private val currentUser = application.currentUser

    private val userDoc = Firebase.firestore.collection("Users").document(application.chatPersonId)
    private val userDocLiveData: LiveData<DocumentSnapshot?> = FirestoreDocRefLiveData(userDoc)

    private val _name = MutableLiveData("")
    val name: LiveData<String> = _name

    private val _image = MutableLiveData("")
    val image: LiveData<String> = _image

    val lastSeen = userDocLiveData.map {
        val time = it?.getString("lastSeen")
        getLastSeen(time)
    }

    fun setDetails(name: String, image: String) {
        _name.value = name
        _image.value = image
    }
}