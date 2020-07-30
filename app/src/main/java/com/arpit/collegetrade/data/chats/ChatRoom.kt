package com.arpit.collegetrade.data.chats

data class ChatRoom(
    val id: String,
    var adId: String,
    var sellerName: String,
    var sellerImage: String,
    var buyerName: String,
    var buyerImage: String,
    var lastMsg: Message,
    var unreadCount: Int
)