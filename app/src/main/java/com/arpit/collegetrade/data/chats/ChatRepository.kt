package com.arpit.collegetrade.data.chats

interface ChatRepository {

    fun sendMessage(message: Message, chatRoom: ChatRoom)

    suspend fun markMessagesAsDelivered(chatId: String, count: Long)

    fun markMessagesAsRead(msgIds: Array<String>, chatId: String)

}