package com.arpit.collegetrade.notifications

import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class DirectReplyReceiver : BroadcastReceiver() {

    private val TAG = "TAG DirectReplyReceiver"

    private val REMOTE_INPUT_KEY = "KEY_TEXT_REPLY"

    override fun onReceive(context: Context?, intent: Intent) {
        try {
            intent.getStringExtra("markAsRead")?.let {
                Log.d(TAG, "onReceive: chatId=$it")
                MyFirebaseMessagingService().updateDatabase(context!!, it, true)
            }

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