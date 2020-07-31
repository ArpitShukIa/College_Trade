package com.arpit.collegetrade

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "TAG FbMessagingService"

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    private val channelId = "CollegeTradeChatChannel"
    private val notificationId = 567

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "onNewToken: $token")
        Firebase.firestore.collection("Users").document(Firebase.auth.currentUser?.uid!!)
            .update("deviceToken", token)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        try {
            val data = p0.data
            val bitmap1 = getCircleBitmap(
                Glide.with(this).asBitmap().load(data["image"]).submit().get()
            )
            val bitmap2 = getCircleBitmap(
                Glide.with(this).asBitmap().load(data["myImage"]).submit().get()
            )

            val sender = Person.Builder()
                .setKey(data["sender"])
                .setName(data["name"])
                .setIcon(IconCompat.createWithBitmap(bitmap1))
                .build()

            val receiver = Person.Builder()
                .setName("Me")
                .setKey(data["to"])
                .setIcon(IconCompat.createWithBitmap(bitmap2))
                .build()

            val message = NotificationCompat.MessagingStyle.Message(
                data["message"]!!,
                data["time"]!!.toLong(),
                sender
            )

            displayNotification(receiver, message)

        } catch (e: Exception) {
            Log.e(TAG, "onMessageReceived: ${e.stackTrace}", e)
        }
    }

    private fun displayNotification(
        receiver: Person,
        message: NotificationCompat.MessagingStyle.Message
    ) {
        val messagingStyle = notificationManager.activeNotifications
            .find { it.id == notificationId }
            ?.notification
            ?.let { NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(it) }
            ?: NotificationCompat.MessagingStyle(receiver)

        messagingStyle.addMessage(message)

        val activityIntent = Intent(this, SplashScreenActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0)

        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.app_icon)
            .setStyle(messagingStyle)
            .setColor(0x3867E3)
            .setAutoCancel(true)
            .setSound(sound)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .build()

        createNotificationChannel()
        notificationManager.notify(notificationId, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Chats",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getCircleBitmap(bitmap: Bitmap): Bitmap? {
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