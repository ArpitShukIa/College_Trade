package com.arpit.collegetrade.chats

import androidx.lifecycle.*
import com.arpit.collegetrade.Application
import com.arpit.collegetrade.data.Ad
import com.arpit.collegetrade.data.FirestoreDocRefLiveData
import com.arpit.collegetrade.data.chats.ChatRoom
import com.arpit.collegetrade.data.chats.Message
import com.arpit.collegetrade.util.getLastSeen
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.tempo.Tempo

class ChatViewModel(val application: Application) : ViewModel() {

    private val TAG = "TAG ChatViewModel"

    private val repository = application.chatRepository
    private val currentUser = application.currentUser

    private val otherUserId = MutableLiveData("random_value")

    private val userDocLiveData: LiveData<DocumentSnapshot> = otherUserId.switchMap {
        val userDoc = Firebase.firestore.collection("Users").document(it)
        FirestoreDocRefLiveData(userDoc)
    }

    private val _messages = MutableLiveData<MutableList<Message>>(ArrayList())
    val messages: LiveData<MutableList<Message>> = _messages

    val messageText = MutableLiveData("")
    private var chatId = ""
    private var deviceToken = ""
    private var ad = Ad()

    private val _name = MutableLiveData("")
    val name: LiveData<String> = _name

    private val _image = MutableLiveData("")
    val image: LiveData<String> = _image

    val lastSeen = userDocLiveData.map {
        try {
            // TODO Ignore the data from cache for the first time
            val time = it.getString("lastSeen")
            deviceToken = it.getString("deviceToken") ?: ""
            getLastSeen(time)
        } catch (e: Exception) {
            ""
        }

    }

    fun setDetails(ad: Ad, chatId: String, otherUserId: String, name: String, image: String) {
        this.ad = ad
        this.chatId = chatId
        this.otherUserId.value = otherUserId
        _name.value = name
        _image.value = image
    }

    fun sendMessage() {
        if (messageText.value!!.isEmpty()) return
        val currentTime = Tempo.now() ?: System.currentTimeMillis()
        val senderName = if (ad.id == currentUser.id) ad.sellerName else currentUser.name
        val senderImage = if (ad.id == currentUser.id) ad.sellerPhoto else currentUser.photo
        val msg = Message(
            "", messageText.value!!, currentUser.id, otherUserId.value!!, senderName,
            senderImage, _image.value!!, ad.title, currentTime.toString(), 0, deviceToken
        )

        _messages.value!!.add(msg)

        val chatRoom = ChatRoom(
            chatId, ad.id, ad.sellerName, ad.sellerPhoto,
            currentUser.name, currentUser.photo, msg, 0
        )

        repository.sendMessage(msg, chatId, chatRoom)

        messageText.value = ""
    }
}