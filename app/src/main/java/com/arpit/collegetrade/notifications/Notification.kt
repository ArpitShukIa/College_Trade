package com.arpit.collegetrade.notifications

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_table")
data class Notification(
    @PrimaryKey
    @ColumnInfo(name = "message_id")
    val messageId: String,

    @ColumnInfo(name = "chat_id")
    val chatId: String,

    @ColumnInfo(name = "ad_title")
    val adTitle: String,

    @ColumnInfo(name = "message")
    val message: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "receiver_name")
    val receiverName: String,

    @ColumnInfo(name = "receiver_key")
    val receiverKey: String,

    @ColumnInfo(name = "receiver_icon")
    val receiverImage: String,

    @ColumnInfo(name = "sender_name")
    val senderName: String,

    @ColumnInfo(name = "sender_key")
    val senderKey: String,

    @ColumnInfo(name = "sender_icon")
    val senderImage: String,

    @ColumnInfo(name = "sender_device_token")
    val senderDeviceToken: String,

    @ColumnInfo(name = "receiver_device_token")
    val receiverDeviceToken: String
)