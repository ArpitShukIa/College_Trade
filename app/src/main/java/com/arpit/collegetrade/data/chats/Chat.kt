package com.arpit.collegetrade.data.chats

import com.arpit.collegetrade.data.Ad

data class Chat(
    val id: String,
    var ad: Ad = Ad(),
    var name: String = "",
    var lastMsg: Message = Message(),
    var personImage: String = "",
    var personId: String = "",
    var isLastMsgMine: Boolean = false,
    var unreadCount: Int = 0
)