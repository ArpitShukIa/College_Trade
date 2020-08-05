package com.arpit.collegetrade.notifications

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotificationDao {

    @Insert
    fun insert(notification: Notification)

    @Query("SELECT * from notification_table ORDER BY timestamp ASC")
    fun getAllNotifications(): List<Notification>

    @Query("DELETE FROM notification_table WHERE chat_id = :chatId")
    fun deleteNotificationsByChatId(chatId: String)

    @Query("DELETE FROM notification_table")
    fun deleteAllNotifications()
}