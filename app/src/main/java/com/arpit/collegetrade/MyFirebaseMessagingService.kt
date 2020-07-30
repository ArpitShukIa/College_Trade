package com.arpit.collegetrade

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Person
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "TAG FbMessagingService"

    private val firestore: FirebaseFirestore by lazy { Firebase.firestore }
    private val channelId = "CollegeTradeChatChannel"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("TAG", "onNewToken: $token")
        Firebase.firestore.collection("Users").document(Firebase.auth.currentUser?.uid!!)
            .update("deviceToken", token)
    }

    @RequiresApi(Build.VERSION_CODES.P)
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
                .setIcon(Icon.createWithBitmap(bitmap1))
                .build()

            val receiver = Person.Builder()
                .setName("Me")
                .setKey(data["to"])
                .setIcon(Icon.createWithBitmap(bitmap2))
                .build()

            val message = Notification.MessagingStyle.Message(
                data["message"]!!,
                data["time"]!!.toLong(),
                sender
            )
            val messagingStyle = Notification.MessagingStyle(receiver)
                .addMessage(message)

            val notificationBuilder = Notification.Builder(this, channelId)
                .setStyle(messagingStyle)
                .setSmallIcon(R.drawable.app_icon)

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel = NotificationChannel(
                channelId,
                "Chats",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
            notificationManager.notify(0, notificationBuilder.build())

        } catch (e: Exception) {
            Log.e(TAG, "onMessageReceived: ${e.stackTrace}", e)
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
        if (!bitmap.isRecycled) {
            bitmap.recycle()
        }
        return output
    }
}