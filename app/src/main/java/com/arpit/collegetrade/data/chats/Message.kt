package com.arpit.collegetrade.data.chats

data class Message(
    var id: String,
    val message: String,
    val from: String,
    val to: String,
    val senderName: String,
    val senderImage: String,
    val receiverName: String,
    val receiverImage: String,
    val adTitle: String,
    val timestamp: String,
    val status: Int,
    val deviceToken: String,
    val receiverDeviceToken: String
)