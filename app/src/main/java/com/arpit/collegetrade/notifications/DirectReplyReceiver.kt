package com.arpit.collegetrade.notifications

import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class DirectReplyReceiver : BroadcastReceiver() {

    private val TAG = "TAG DirectReplyReceiver"

    private val REMOTE_INPUT_KEY = "KEY_TEXT_REPLY"

    override fun onReceive(context: Context?, intent: Intent) {
        try {
            Log.d(TAG, "onReceive uid: ${Firebase.auth.currentUser?.uid}")
            RemoteInput.getResultsFromIntent(intent)?.let {
                val chatId = intent.getStringExtra("CHAT_ID")
                val reply = it.getCharSequence(REMOTE_INPUT_KEY)
                MyFirebaseMessagingService().getReply(reply!!, chatId!!, context!!)
            }
        } catch (e: Exception) {
            Log.e(TAG, "onReceive: ${e.stackTrace}", e)
        }
    }
}