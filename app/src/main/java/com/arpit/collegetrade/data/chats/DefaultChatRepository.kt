package com.arpit.collegetrade.data.chats

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import timber.log.Timber

object DefaultChatRepository : ChatRepository {

    private const val TAG = "TAG ChatRepository"

    private val firestore: FirebaseFirestore by lazy { Firebase.firestore }

    override fun sendMessage(message: Message, chatRoom: ChatRoom) {
        val chatRef = firestore.collection("Chats").document(chatRoom.id)
        val doc = chatRef.collection("Messages").document()
        message.id = doc.id
        chatRoom.lastMsg.id = doc.id

        firestore.runBatch { batch ->
            if (chatRoom.unreadCount == 1) {
                batch.set(chatRef, chatRoom)
            } else {
                batch.update(chatRef, "unreadCount", FieldValue.increment(1))
                batch.update(chatRef, "lastMsg", message)
            }
            batch.set(doc, message)
        }
    }

    override suspend fun markMessagesAsDelivered(chatId: String, count: Long) {
        try {
            val chatRef = firestore.collection("Chats").document(chatId)
            val docs = chatRef.collection("Messages").orderBy("timestamp")
                .limitToLast(count).get().await()
            for (doc in docs) {
                if (doc.getLong("status") == 1L) {
                    doc.reference.update("status", FieldValue.increment(1))
                }
            }
            chatRef.update("lastMsg.status", FieldValue.increment(1))
        } catch (e: Throwable) {
            Timber.tag(TAG).e(e)
        }
    }

    override fun markMessagesAsRead(msgIds: Array<String>, chatId: String) {
        val chatRef = firestore.collection("Chats").document(chatId)
        msgIds.forEach { id ->
            val doc = chatRef.collection("Messages").document(id)
            doc.update("status", 3)
        }
        chatRef.update("lastMsg.status", 3)
        chatRef.update("unreadCount", 0)
    }
}