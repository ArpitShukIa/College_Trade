package com.arpit.collegetrade.data.chats

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object DefaultChatRepository : ChatRepository {

    private const val TAG = "TAG ChatRepository"

    private val firestore: FirebaseFirestore by lazy { Firebase.firestore }

    override fun sendMessage(message: Message, chatRoom: ChatRoom) {
        val chatRef = firestore.collection("Chats").document(chatRoom.id)
        val doc = chatRef.collection("Messages").document()
        message.id = doc.id
        chatRoom.lastMsg.id = doc.id

        firestore.runBatch { batch ->
            if (chatRoom.unreadCount == 0) {
                batch.set(chatRef, chatRoom)
            } else {
                batch.update(chatRef, "unreadCount", FieldValue.increment(1))
                batch.update(chatRef, "lastMsg", message)
            }
            batch.set(doc, message)
        }
    }
}