package com.arpit.collegetrade.data.chats

data class Message(
    var id: String = "",
    var message: String = "",
    var from: String = "",
    var to: String = "",
    var senderName: String = "",
    var senderImage: String = "",
    var receiverName: String = "",
    var receiverImage: String = "",
    var adTitle: String = "",
    var timestamp: String = "",
    var status: Int = -1,
    var deviceToken: String = "",
    var receiverDeviceToken: String = ""
)