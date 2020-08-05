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
import com.arpit.collegetrade.R
import com.arpit.collegetrade.SplashScreenActivity
import com.arpit.collegetrade.data.MyRoomDatabase
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

        val inboxStyle = InboxStyle()
        val chatsCount = hashMap.keys.size
        var msgCount = 0

        hashMap.forEach { (chatId, notifList) ->

//            Log.d(TAG, "filterNotifications: \n\n$notifList")
            val notInDrawer = notificationManager.activeNotifications
                .find { it.id == getNotificationId(chatId) }
                ?.notification == null

//            if (notifList[0].senderKey.isEmpty()) {
//                if (notInDrawer) {
//                    updateDatabase(this, notifList[0])
//                } else {
////                    inboxStyle.addLine("Me ${notifList[0].message}")
//                    Log.d(TAG, "filterNotifications: add line = Me ${notifList[0].message}")
//                }
//                chatsCount--
//                return@forEach
//            }

            val sender = Person.Builder()
                .setKey(notifList[0].senderKey)
                .setName(notifList[0].senderName)
                .setIcon(IconCompat.createWithBitmap(getCircleBitmap(notifList[0].senderIcon)))
                .build()

            val messagingStyle = MessagingStyle(receiver)
            messagingStyle.conversationTitle = notifList[0].adTitle
            messagingStyle.isGroupConversation = true

//            if (notifList.size > 1 && notifList[1].senderKey.isEmpty()) {
//                val notification = notifList[0]
//                updateDatabase(this, notification)
//
//                val message = MessagingStyle.Message(
//                    notification.message,
//                    notification.timestamp,
//                    sender
//                )
//
//                messagingStyle.addMessage(message)
////                inboxStyle.addLine("${notification.senderName} ${notification.message}")
//                buildNotification(
//                    chatId,
//                    messagingStyle,
//                    notification.timestamp,
//                    this
//                )
//                return@forEach
//            }
//            var lastMsg = ""
            var timestamp = 0L

            notifList.reversed().forEach {
                messagingStyle.addMessage(it.message, it.timestamp, sender)
//                lastMsg = "${sender.name}  ${message.text}"
                timestamp = it.timestamp
                msgCount++
            }

//            inboxStyle.addLine(lastMsg)
//            Log.d(TAG, "filterNotifications: add line $lastMsg")

            if (notInDrawer || msgChatId == chatId)
                buildNotification(chatId, messagingStyle, timestamp, this)
        }
        val startText = if (msgCount > 1) "$msgCount messages" else "1 message"
        val endText = if (chatsCount > 1) "from $chatsCount chats" else ""
        inboxStyle.setSummaryText("$startText $endText")
        showSummaryNotification(inboxStyle)
//        val notification = Builder(this, chatsChannelId)
//            .setSmallIcon(R.drawable.ic_reply)
//            .setStyle(inboxStyle)
//            .build()
//        getNotificationManager(this).notify(1234, notification)
//        Log.d(TAG, "filterNotifications: sending summary")
    }

    fun getReply(reply: CharSequence, chatId: String, context: Context) {
//        Log.d(TAG, "getReply: started")
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val messagingStyle = notificationManager.activeNotifications
            .find { it.id == getNotificationId(chatId) }
            ?.notification
            ?.let { MessagingStyle.extractMessagingStyleFromNotification(it) }!!

        val currentTime = Tempo.now() ?: System.currentTimeMillis()

        if (reply.isBlank()) {
            buildNotification(chatId, messagingStyle, currentTime, context)
            return
        }
        clearDatabase(context, chatId)
//
//        val notification = Notification(
//            UUID.randomUUID().toString(), chatId, "", reply.toString(), currentTime, "Me", "", ""
//        )
//        insertInDatabase(notification, context)

        messagingStyle.addMessage(reply, currentTime, messagingStyle.user)
        buildNotification(chatId, messagingStyle, currentTime, context)
    }

    private fun buildNotification(
        chatId: String,
        messagingStyle: MessagingStyle,
        timestamp: Long,
        context: Context
    ) {
        val activityIntent = Intent(context, SplashScreenActivity::class.java)
        val contentIntent = PendingIntent.getActivity(context, 0, activityIntent, 0)

        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = Builder(context, GENERAL_CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon)
            .setStyle(messagingStyle)
            .setColor(0x3867E3)
            .setPriority(PRIORITY_LOW)
            .setSound(sound)
            .setContentIntent(contentIntent)
            .setWhen(timestamp)
            .setShowWhen(true)
            .setAutoCancel(true)
            .setGroup(GROUP_KEY)
            .setGroupAlertBehavior(GROUP_ALERT_SUMMARY)
            .addAction(getRemoteInputAction(context, chatId))
            .setCategory(CATEGORY_MESSAGE)

        createNotificationChannel(context, GENERAL_CHANNEL_ID)
        NotificationManagerCompat.from(context)
            .notify(getNotificationId(chatId), notification.build())
//        Log.d(TAG, "buildNotification: sending")
    }

    private fun getRemoteInputAction(
        context: Context, chatId: String
    ): Action? {
        val remoteInput = RemoteInput.Builder(REMOTE_INPUT_KEY).setLabel("Reply").build()
        val intent = Intent(context, DirectReplyReceiver::class.java)
        intent.putExtra("CHAT_ID", chatId)

        val replyPendingIntent =
            PendingIntent.getBroadcast(context, getNotificationId(chatId), intent, 0)

        return Action.Builder(R.drawable.ic_reply, "Reply", replyPendingIntent)
            .addRemoteInput(remoteInput).build()
    }

    private fun showSummaryNotification(inboxStyle: InboxStyle) {
        val summaryNotification = Builder(this, IMPORTANT_CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon)
            .setStyle(inboxStyle)
            .setColor(0x3867E3)
            .setPriority(PRIORITY_HIGH)
            .setGroup(GROUP_KEY)
            .setGroupAlertBehavior(GROUP_ALERT_SUMMARY)
            .setGroupSummary(true)
            .build()
//        SystemClock.sleep(2000)
        createNotificationChannel(this, IMPORTANT_CHANNEL_ID)
        NotificationManagerCompat.from(this).notify(SUMMARY_ID, summaryNotification)
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
        (chatId.take(3) + chatId.takeLast(3)).hashCode()

    private fun getAllNotifications(notification: Notification): List<Notification> {
        val notificationDao = MyRoomDatabase.getDatabase(this).notificationDao()
        notificationDao.insert(notification)
        return notificationDao.getAllNotifications()
    }

    private fun clearDatabase(context: Context, chatId: String) {
        val notificationDao = MyRoomDatabase.getDatabase(context).notificationDao()
        AsyncTask.execute {
            notificationDao.deleteNotificationsByChatId(chatId)
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