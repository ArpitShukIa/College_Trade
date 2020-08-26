package com.arpit.collegetrade.notifications

import com.arpit.collegetrade.data.chats.Message
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class NotificationsRepository {

    private val TAG = "TAG NotificationsRepo"

    private val firestore by lazy { Firebase.firestore }

    fun markMessageAsDelivered(chatId: String, msgId: String) {
        try {
            val chatRef = firestore.collection("Chats").document(chatId)
            val docRef = chatRef.collection("Messages").document(msgId)

            firestore.runTransaction { transaction ->
                val status = transaction.get(docRef).get("status").toString()
                if (status == "1") {
                    transaction.update(docRef, "status", 2)
                    transaction.update(chatRef, "lastMsg.status", 2)
                }
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    fun markMessagesAsRead(messages: List<Notification>) {
        val chatRef = firestore.collection("Chats").document(messages[0].chatId)
        for (M in messages) {
            val docRef = chatRef.collection("Messages").document(M.messageId)
            docRef.update("status", 3)
            chatRef.update("lastMsg.status", 3)
        }
        chatRef.update("unreadCount", 0)
    }

    fun sendMessage(message: Message, chatId: String, isFirstReply: Boolean) {
        val chatRef = firestore.collection("Chats").document(chatId)
        val doc = chatRef.collection("Messages").document()
        val value: Any = if (isFirstReply) 1 else FieldValue.increment(1)
        message.id = doc.id
        firestore.runBatch { batch ->
            batch.set(doc, message)
            batch.update(chatRef, "lastMsg", message)
            batch.update(chatRef, "unreadCount", value)
        }
    }
}