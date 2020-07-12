package com.arpit.collegetrade.data.chats

data class Chat(
    val id: String,
    var name: String = "",
    var lastMsg: String = "",
    var personImage: String = "",
    var adImage: String = "",
    var lastMsgTime: String = "",
    var timestamp: Long = 0,
    var status: String = "",
    var unreadCount: Int = 0
)