package com.arpit.collegetrade.data.chats

import com.arpit.collegetrade.data.Ad

data class ChatRoom(
    var id: String = "",
    val ad: Ad = Ad(),
    var buyerId: String = "",
    var buyerName: String = "",
    var buyerImage: String = "",
    var lastMsg: Message = Message(),
    var unreadCount: Int = -1
) {
    override fun toString(): String {
        return "ChatRoom(adTitle=${ad.title})\n"
    }
}