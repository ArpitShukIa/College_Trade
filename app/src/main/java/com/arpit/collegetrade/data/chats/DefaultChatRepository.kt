package com.arpit.collegetrade.data.chats

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object DefaultChatRepository : ChatRepository {

    private const val TAG = "TAG ChatRepository"

    private val firestore: FirebaseFirestore by lazy { Firebase.firestore }

    override fun sendMessage(message: Message, chatId: String, chatRoom: ChatRoom) {
        Log.d(TAG, "sendMessage: sending")
        val doc = firestore.collection("Chats").document(chatId).collection("Messages").document()
        message.id = doc.id
        chatRoom.lastMsg.id = doc.id
        if (chatRoom.sellerImage != chatRoom.buyerImage)
            firestore.collection("Chats").document(chatId).set(chatRoom)
        doc.set(message)
    }
}