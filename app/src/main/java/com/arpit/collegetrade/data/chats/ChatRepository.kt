package com.arpit.collegetrade.data.chats

interface ChatRepository {

    fun sendMessage(message: Message, chatId: String, chatRoom: ChatRoom)

}