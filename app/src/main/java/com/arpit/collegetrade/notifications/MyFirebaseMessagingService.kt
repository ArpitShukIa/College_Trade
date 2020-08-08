package com.arpit.collegetrade.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.media.RingtoneManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat.*
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.graphics.drawable.IconCompat
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import com.arpit.collegetrade.Application
import com.arpit.collegetrade.R
import com.arpit.collegetrade.SplashScreenActivity
import com.arpit.collegetrade.data.MyRoomDatabase
import com.arpit.collegetrade.data.chats.Message
import com.arpit.collegetrade.util.getNotificationText
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.tempo.Tempo
import timber.log.Timber

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "TAG FbMessagingService"

    private val GROUP_KEY = "group_key"
    private val CHANNEL_ID = "my_channel"
    private val REMOTE_INPUT_KEY = "KEY_TEXT_REPLY"
    private val notificationId = 102 // For Android M
    private val SUMMARY_NOTIFICATION_ID = 101

    private lateinit var senderBitmap: Bitmap

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val userId = Firebase.auth.currentUser?.uid ?: return
        Timber.tag(TAG).d("onNewToken: token = $token")
        Firebase.firestore.collection("Users").document(userId).update("deviceToken", token)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        try {
            val data = p0.data

            if ((application as Application).userId != data["receiver"]) return

            NotificationsRepository().markMessageAsDelivered(data["chatId"]!!, data["id"]!!)

            val bitmap = getCircleBitmap(data["myImage"]!!)
            val receiver = Person.Builder()
                .setName("You")
                .setKey(data["receiver"])
                .setIcon(IconCompat.createWithBitmap(bitmap))
                .build()

            val notification = Notification(
                data["id"]!!, data["chatId"]!!, data["adTitle"]!!,
                data["message"]!!, data["time"]!!.toLong(), data["myName"]!!,
                data["receiver"]!!, data["myImage"]!!, data["name"]!!,
                data["sender"]!!, data["image"]!!, data["deviceToken2"]!!, data["deviceToken"]!!
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
            Timber.tag(TAG).e(e)
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

            val n = notifList[0]

            senderBitmap = getCircleBitmap(n.senderImage)
            val sender = Person.Builder()
                .setKey(n.senderKey)
                .setName(n.senderName)
                .setIcon(IconCompat.createWithBitmap(senderBitmap))
                .build()

            val messagingStyle = MessagingStyle(receiver)
            messagingStyle.conversationTitle = n.adTitle
            messagingStyle.isGroupConversation = true

            val data = mapOf(
                "adTitle" to n.adTitle,
                "rKey" to n.receiverKey, "sKey" to n.senderKey,
                "rName" to n.receiverName, "rImage" to n.receiverImage,
                "sName" to n.senderName, "sImage" to n.senderImage,
                "token1" to n.senderDeviceToken, "token2" to n.receiverDeviceToken
            )
            val extras = bundleOf("data" to data)

            var timestamp = 0L
            title = n.adTitle

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
                buildNotification(chatId, messagingStyle, timestamp, this, extras)
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

        val n = notificationManager.activeNotifications
            .find { it.id == getNotificationId(chatId) }
            ?.notification!!

        val messagingStyle = n.let { MessagingStyle.extractMessagingStyleFromNotification(it) }!!

        if (reply.isBlank()) {
            buildNotification(chatId, messagingStyle, n.`when`, context, n.extras)
            return
        }
        createMessage(reply.toString(), chatId, n.extras)
        updateDatabase(context, chatId, false)

        val currentTime = Tempo.now() ?: System.currentTimeMillis()
        messagingStyle.addMessage(reply, currentTime, messagingStyle.user)
        buildNotification(chatId, messagingStyle, currentTime, context, n.extras)
    }

    private fun buildNotification(
        chatId: String,
        messagingStyle: MessagingStyle,
        timestamp: Long,
        context: Context,
        extras: Bundle
    ) {
        val broadcastIntent = Intent(context, DirectReplyReceiver::class.java)
        broadcastIntent.putExtra("markAsRead", chatId)
        val actionIntent =
            PendingIntent.getBroadcast(context, getNotificationId(chatId) + 1, broadcastIntent, 0)

        val builder = Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon)
            .setStyle(messagingStyle)
            .setColor(0x3867E3)
            .setWhen(timestamp)
            .setShowWhen(true)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(getContentIntent(context))
            .addAction(getRemoteInputAction(context, chatId))
            .setCategory(CATEGORY_MESSAGE)
            .addExtras(extras)
            .setGroup(GROUP_KEY)
            .setGroupAlertBehavior(
                if (this == context) GROUP_ALERT_SUMMARY else GROUP_ALERT_CHILDREN
            )

        if (this == context)
            builder.addAction(R.drawable.ic_read, "Mark As Read", actionIntent)

        createNotificationChannel(context)
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
        val builder = Builder(this, CHANNEL_ID)
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
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val summaryNotification = Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon)
            .setStyle(inboxStyle)
            .setColor(0x3867E3)
            .setGroup(GROUP_KEY)
            .setCategory(CATEGORY_MESSAGE)
            .setContentIntent(getContentIntent(context))
            .setGroupSummary(true)
            .setGroupAlertBehavior(
                if (this == context) GROUP_ALERT_SUMMARY else GROUP_ALERT_CHILDREN
            ).build()

        if (notificationManager.activeNotifications.isNotEmpty())
            notificationManager.notify(SUMMARY_NOTIFICATION_ID, summaryNotification)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(CHANNEL_ID, "Chats", IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
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
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                notificationManager.cancel(getNotificationId(chatId))

                val activeNotificationsCount = notificationManager.activeNotifications.size
                if (activeNotificationsCount == 1) {
                    NotificationManagerCompat.from(context).cancel(SUMMARY_NOTIFICATION_ID)
                    return@execute
                }
                val notifications = notificationDao.getAllNotifications()

                val msgCount = notifications.size
                val chatsCount = notifications.distinctBy { it.chatId }.size
                val summary = when {
                    msgCount < 2 -> null
                    chatsCount == 1 -> "$msgCount new messages"
                    else -> "$msgCount messages from $chatsCount chats"
                }
                showSummaryNotification(InboxStyle().setSummaryText(summary), context)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun createMessage(reply: String, chatId: String, extras: Bundle) {
        val data = extras["data"] as Map<String, String>
        val currentTime = (Tempo.now() ?: System.currentTimeMillis()).toString()
        val message = Message(
            "", reply,
            data.getValue("rKey"), data.getValue("sKey"),
            data.getValue("rName"), data.getValue("rImage"),
            data.getValue("sName"), data.getValue("sImage"),
            data.getValue("adTitle"), currentTime, 0,
            data.getValue("token1"), data.getValue("token2")
        )
        NotificationsRepository().sendMessage(message, chatId)
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