package com.arpit.collegetrade.chats.sell

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.arpit.collegetrade.Application
import com.arpit.collegetrade.data.FirestoreQueryLiveData
import com.arpit.collegetrade.data.chats.Chat
import com.arpit.collegetrade.data.chats.ChatRoom
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SellingViewModel(application: Application) : ViewModel() {

    private val TAG = "TAG BuyingViewModel"

    private val repository = application.chatRepository
    private val userId = application.currentUser.id

    private val query = Firebase.firestore.collection("Chats").whereEqualTo("ad.sellerId", userId)
        .orderBy("lastMsg.timestamp", Query.Direction.DESCENDING)
    private val chatsLiveData: LiveData<QuerySnapshot> = FirestoreQueryLiveData(query)

    val chatsList: LiveData<List<Chat>> = chatsLiveData.map { querySnapshot ->
        val list: MutableList<Chat> = ArrayList()
        for (doc in querySnapshot) {
            val room = doc.toObject(ChatRoom::class.java)
            val chat = Chat(
                room.id, room.ad, room.buyerName, room.lastMsg, room.buyerImage,
                room.buyerId, room.lastMsg.from == userId, room.unreadCount
            )
            list.add(chat)
        }
        list
    }
}