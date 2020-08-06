package com.arpit.collegetrade.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.media.RingtoneManager
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat.*
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.graphics.drawable.IconCompat
import androidx.core.text.HtmlCompat
import com.arpit.collegetrade.R
import com.arpit.collegetrade.SplashScreenActivity
import com.arpit.collegetrade.data.MyRoomDatabase
import com.arpit.collegetrade.util.getNotificationText
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.tempo.Tempo

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "TAG FbMessagingService"

    private val GENERAL_CHANNEL_ID = "com.arpit.collegetrade.general_channel"
    private val IMPORTANT_CHANNEL_ID = "com.arpit.collegetrade.important_channel"
    private val GROUP_KEY = "com.arpit.collegetrade.group_notification_key"
    private val REMOTE_INPUT_KEY = "KEY_TEXT_REPLY"
    private val SUMMARY_ID = 101
    private val notificationId = 102 // For Android M

    private lateinit var senderBitmap: Bitmap

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val userId = Firebase.auth.currentUser?.uid ?: return
        Log.d(TAG, "onNewToken token: $token")
        Firebase.firestore.collection("Users").document(userId)
            .update("deviceToken", token)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        try {
            Log.d(TAG, "onMessageReceived: started")
            val data = p0.data

            NotificationsRepository().markMessageAsDelivered(data["chatId"]!!, data["id"]!!)

            val bitmap = getCircleBitmap(data["myImage"]!!)
            val receiver = Person.Builder()
                .setName("You")
                .setKey(data["receiver"])
                .setIcon(IconCompat.createWithBitmap(bitmap))
                .build()

            val notification = Notification(
                data["id"]!!, data["chatId"]!!, data["adTitle"]!!, data["message"]!!,
                data["time"]!!.toLong(), data["name"]!!, data["sender"]!!, data["image"]!!
            )

            val notifications = getAllNotifications(notification)
            val hashMap = HashMap<String, List<Notification>>()

            for (N in notifications) {
                val list = hashMap[N.chatId]?.toMutableList() ?: ArrayList()
                list.add(N)
                hashMap[N.chatId] = list
            }
            filterNotifications(hashMap, data["chatId"]!!, receiver)

        } catch (e: Exception) {
            Log.e(TAG, "onMessageReceived: ${e.stackTrace}", e)
        }
    }

    private fun filterNotifications(
        hashMap: HashMap<String, List<Notification>>,
        msgChatId: String,
        receiver: Person
    ) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val isNotM = Build.VERSION.SDK_INT != Build.VERSION_CODES.M

        val inboxStyle = InboxStyle()
        var title = ""
        var text: CharSequence = ""
        val chatsCount = hashMap.keys.size
        var msgCount = 0

        hashMap.forEach { (chatId, notifList) ->

            msgCount += notifList.size

            val notInDrawer = notificationManager.activeNotifications
                .find { it.id == getNotificationId(chatId) }
                ?.notification == null

            senderBitmap = getCircleBitmap(notifList[0].senderIcon)
            val sender = Person.Builder()
                .setKey(notifList[0].senderKey)
                .setName(notifList[0].senderName)
                .setIcon(IconCompat.createWithBitmap(senderBitmap))
                .build()

            val messagingStyle = MessagingStyle(receiver)
            messagingStyle.conversationTitle = notifList[0].adTitle
//            messagingStyle.isGroupConversation = true

            var timestamp = 0L
            title = notifList[0].adTitle

            if (chatsCount > 1)
                inboxStyle.addLine(
                    HtmlCompat.fromHtml("<b>$title</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)
                )

            notifList.forEach {
                messagingStyle.addMessage(it.message, it.timestamp, sender)
                timestamp = it.timestamp
                text = getNotificationText(it.senderName, it.message, chatsCount == 1)
                inboxStyle.addLine(text)
            }

            if (isNotM && (notInDrawer || msgChatId == chatId))
                buildNotification(chatId, messagingStyle, timestamp, this)
        }

        val summary = when {
            msgCount == 1 -> "1 new message"
            chatsCount == 1 -> "$msgCount new messages"
            else -> "$msgCount messages from $chatsCount chats"
        }
        inboxStyle.setSummaryText(summary)

        if (isNotM)
            showSummaryNotification(inboxStyle, this)
        else {
            val contentText = if (msgCount == 1) text else summary
            if (chatsCount > 1) title = "College Trade"
            inboxStyle.setBigContentTitle(title)
            buildNotificationForM(chatsCount, title, inboxStyle, msgCount, contentText)
        }
    }

    fun getReply(reply: CharSequence, chatId: String, context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = notificationManager.activeNotifications
            .find { it.id == getNotificationId(chatId) }
            ?.notification

        val messagingStyle = notification
            ?.let { MessagingStyle.extractMessagingStyleFromNotification(it) }!!

        if (reply.isBlank()) {
            buildNotification(chatId, messagingStyle, notification.`when`, context)
            return
        }
        updateDatabase(context, chatId, false)

        val currentTime = Tempo.now() ?: System.currentTimeMillis()
        messagingStyle.addMessage(reply, currentTime, messagingStyle.user)
        buildNotification(chatId, messagingStyle, currentTime, context)
    }

    private fun buildNotification(
        chatId: String,
        messagingStyle: MessagingStyle,
        timestamp: Long,
        context: Context
    ) {
        val broadcastIntent = Intent(context, DirectReplyReceiver::class.java)
        broadcastIntent.putExtra("markAsRead", chatId)
        val actionIntent =
            PendingIntent.getBroadcast(context, getNotificationId(chatId) + 1, broadcastIntent, 0)

        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = Builder(context, GENERAL_CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon)
            .setStyle(messagingStyle)
            .setColor(0x3867E3)
            .setSound(sound)
            .setWhen(timestamp)
            .setShowWhen(true)
            .setAutoCancel(true)
            .setGroup(GROUP_KEY)
            .setCategory(CATEGORY_MESSAGE)
            .setGroupAlertBehavior(GROUP_ALERT_SUMMARY)
            .setContentIntent(getContentIntent(context))
            .addAction(getRemoteInputAction(context, chatId))

        if (this == context)
            builder.addAction(R.drawable.ic_read, "Mark As Read", actionIntent)

        createNotificationChannel(context, GENERAL_CHANNEL_ID)
        NotificationManagerCompat.from(context).notify(getNotificationId(chatId), builder.build())
    }

    private fun buildNotificationForM(
        chatsCount: Int,
        title: String,
        inboxStyle: InboxStyle,
        msgCount: Int,
        contentText: CharSequence
    ) {
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = Builder(this, IMPORTANT_CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon)
            .setPriority(PRIORITY_HIGH)
            .setColor(0x3867E3)
            .setSound(sound)
            .setContentTitle(title)
            .setContentText(contentText)
            .setContentIntent(getContentIntent(this))
            .setAutoCancel(true)

        if (msgCount > 1) builder.setStyle(inboxStyle)
        if (chatsCount == 1) builder.setLargeIcon(senderBitmap)

        NotificationManagerCompat.from(this).notify(notificationId, builder.build())
    }

    private fun getRemoteInputAction(context: Context, chatId: String): Action? {

        val remoteInput = RemoteInput.Builder(REMOTE_INPUT_KEY).setLabel("Reply").build()
        val intent = Intent(context, DirectReplyReceiver::class.java)
        intent.putExtra("CHAT_ID", chatId)

        val replyPendingIntent =
            PendingIntent.getBroadcast(context, getNotificationId(chatId), intent, 0)

        return Action.Builder(R.drawable.ic_reply, "Reply", replyPendingIntent)
            .addRemoteInput(remoteInput).build()
    }

    private fun showSummaryNotification(inboxStyle: InboxStyle, context: Context) {
        val channelId = if (this == context) IMPORTANT_CHANNEL_ID else GENERAL_CHANNEL_ID
        val summaryNotification = Builder(context, channelId)
            .setSmallIcon(R.drawable.app_icon)
            .setStyle(inboxStyle)
            .setColor(0x3867E3)
            .setGroup(GROUP_KEY)
            .setGroupSummary(true)
            .setGroupAlertBehavior(GROUP_ALERT_SUMMARY)
            .setCategory(CATEGORY_MESSAGE)
            .setContentIntent(getContentIntent(context))
            .build()

        createNotificationChannel(context, channelId)
        NotificationManagerCompat.from(context).notify(SUMMARY_ID, summaryNotification)
    }

    private fun createNotificationChannel(context: Context, channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                if (channelId == GENERAL_CHANNEL_ID)
                    NotificationChannel(
                        GENERAL_CHANNEL_ID,
                        "General",
                        NotificationManager.IMPORTANCE_LOW
                    )
                else
                    NotificationChannel(
                        IMPORTANT_CHANNEL_ID,
                        "Important",
                        NotificationManager.IMPORTANCE_HIGH
                    )
            NotificationManagerCompat.from(context).createNotificationChannel(channel)
        }
    }

    private fun getNotificationId(chatId: String) =
        (chatId.take(2) + chatId.takeLast(3)).hashCode()

    private fun getContentIntent(context: Context) =
        Intent(context, SplashScreenActivity::class.java).let {
            PendingIntent.getActivity(context, 0, it, 0)
        }

    private fun getAllNotifications(notification: Notification): List<Notification> {
        val notificationDao = MyRoomDatabase.getDatabase(this).notificationDao()
        notificationDao.insert(notification)
        return notificationDao.getAllNotifications()
    }

    fun updateDatabase(context: Context, chatId: String, clearNotification: Boolean) {

        val notificationDao = MyRoomDatabase.getDatabase(context).notificationDao()

        AsyncTask.execute {
            notificationDao.getNotificationsByChatId(chatId).also {
                NotificationsRepository().markMessagesAsRead(it)
            }
            notificationDao.deleteNotificationsByChatId(chatId)

            if (clearNotification) {
                NotificationManagerCompat.from(context).cancel(getNotificationId(chatId))
                val notifications = notificationDao.getAllNotifications()
                val msgCount = notifications.size
                if (msgCount == 0) {
                    NotificationManagerCompat.from(context).cancel(SUMMARY_ID)
                    return@execute
                }

                val chatsCount = notifications.distinctBy { it.chatId }.size
                val summary = when {
                    msgCount == 1 -> null
                    chatsCount == 1 -> "$msgCount new messages"
                    else -> "$msgCount messages from $chatsCount chats"
                }
                showSummaryNotification(InboxStyle().setSummaryText(summary), context)
            }
        }
    }

    private fun getCircleBitmap(imageUrl: String): Bitmap {
        val bitmap = Glide.with(this).asBitmap().load(imageUrl).submit().get()

        val output = Bitmap.createBitmap(
            bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawOval(rectF, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }
}