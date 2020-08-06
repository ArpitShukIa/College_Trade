package com.arpit.collegetrade.notifications

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class NotificationsRepository {

    private val TAG = "TAG NotificationsRepo"

    private val firestore by lazy { Firebase.firestore }

    fun markMessageAsDelivered(chatId: String, msgId: String) {
        try {
            val docRef = firestore.collection("Chats").document(chatId)
                .collection("Messages").document(msgId)
            firestore.runTransaction { transaction ->
                val status = transaction.get(docRef).get("status").toString()
                if (status == "1")
                    transaction.update(docRef, "status", 2)
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    fun markMessagesAsRead(messages: List<Notification>) {
        try {
            for (M in messages)
                firestore.collection("Chats").document(M.chatId)
                    .collection("Messages").document(M.messageId).update("status", 3)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }
}