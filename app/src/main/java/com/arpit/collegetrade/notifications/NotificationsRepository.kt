package com.arpit.collegetrade.notifications

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
            Log.e(TAG, "markMessageAsDelivered: ${e.stackTrace}", e)
        }
    }

    fun markMessagesAsRead(messages: List<Notification>) {
        for (M in messages)
            firestore.collection("Chats").document(M.chatId)
                .collection("Messages").document(M.messageId).update("status", 3)
    }
}