package com.arpit.collegetrade.chats

import androidx.lifecycle.*
import com.arpit.collegetrade.Application
import com.arpit.collegetrade.data.Ad
import com.arpit.collegetrade.data.FirestoreDocRefLiveData
import com.arpit.collegetrade.data.FirestoreQueryLiveData
import com.arpit.collegetrade.data.chats.ChatRoom
import com.arpit.collegetrade.data.chats.Message
import com.arpit.collegetrade.util.getLastSeen
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
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

    private var msgsCount = 0

    val messageText = MutableLiveData("")
    private var chatId = MutableLiveData("")
    private var deviceToken = ""
    private var ad = Ad()

    private val messagesLiveData: LiveData<QuerySnapshot> = chatId.switchMap {
        val query = Firebase.firestore.collection("Chats").document(it)
            .collection("Messages").orderBy("timestamp")
        FirestoreQueryLiveData(query)
    }

    val messages = messagesLiveData.map { getMessages(it) }

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
        this.chatId.value = chatId
        this.otherUserId.value = otherUserId
        _name.value = name
        _image.value = image
    }

    private fun getMessages(snapshot: QuerySnapshot): List<Message> {
        val msgList = mutableListOf<Message>()
        var unreadMsgIds = arrayOf<String>()
        for (doc in snapshot) {
            val message = doc.toObject(Message::class.java)
            msgList.add(message)
            if (message.status != 3)
                unreadMsgIds += message.id
        }
        msgsCount = msgList.size
        return msgList.also {
            if (it.isNotEmpty() && it.last().to == currentUser.id)
                repository.markMessagesAsRead(unreadMsgIds, chatId.value!!)
        }
    }

    fun sendMessage() {
        if (messageText.value!!.isEmpty()) return
        val currentTime = Tempo.now() ?: System.currentTimeMillis()
        val senderName = if (ad.id == currentUser.id) ad.sellerName else currentUser.name
        val senderImage = if (ad.id == currentUser.id) ad.sellerPhoto else currentUser.photo

        val msg = Message(
            "", messageText.value!!.trim(), currentUser.id, otherUserId.value!!,
            senderName, senderImage, _name.value!!, _image.value!!, ad.title,
            currentTime.toString(), 0, deviceToken, application.deviceToken
        )

        val chatRoom = if (msgsCount == 0)
            ChatRoom(
                chatId.value!!, ad, currentUser.id, currentUser.name, currentUser.photo, msg, 1
            ) else ChatRoom(chatId.value!!)

        repository.sendMessage(msg, chatRoom)

        messageText.value = ""
    }
}