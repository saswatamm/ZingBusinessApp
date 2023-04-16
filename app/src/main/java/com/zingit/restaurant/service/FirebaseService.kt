package com.zingit.restaurant.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.zingit.restaurant.R
import com.zingit.restaurant.ZingBusiness.Companion.firebaseToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL

class FirebaseService : FirebaseMessagingService() {


    override fun onCreate() {
        super.onCreate()
        fcmToken()

    }

    private fun fcmToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token: String ->
            Log.e(TAG, "fcmToken: ${token}")
            firebaseToken = token
        }
    }


    companion object {
        private const val TAG = "mFirebaseIIDService"
        private const val CHANNEL_ID = "zing_buisness"
        private const val NOTIFICATION_ID = 1

        fun NotificationManager.sendNotification(
            messageBody: String,
            notificationTitle: String,
            imageUrl: String?,
            intent: Intent,
            applicationContext: Context
        ) {


            //for Android 12

            val contentPendingIntent: PendingIntent
            contentPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getActivity(
                    applicationContext,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getActivity(
                    applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

            val builder = NotificationCompat.Builder(
                applicationContext, CHANNEL_ID
            ).setSmallIcon(R.mipmap.ic_launcher_round).setContentTitle(notificationTitle)
                .setContentText(messageBody).setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true)
                .setSound(
                    Uri.parse("android.resource://" + applicationContext.packageName + "/" + R.raw.sliver_sprint),
                )

            if (imageUrl != null) {
                runBlocking {
                    val url = URL(imageUrl)

                    withContext(Dispatchers.IO) {
                        try {
                            val input = url.openStream()
                            BitmapFactory.decodeStream(input)
                        } catch (e: IOException) {
                            null
                        }
                    }?.let { bitmap ->
                        builder.setLargeIcon(bitmap)
                            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                    }
                }
            }
            notify(NOTIFICATION_ID, builder.build())
        }

    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.e(TAG, "onNewToken: DEVICE_TOKEN_${p0}")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.e("onMeessageReceived", "onMessageReceived: ${message.data}")
        message.data.let {
            val intent = Intent()
            intent.putExtra("redirect", it["redirect"])
            sendNotification(
                it["body"] ?: "ZingBusiness", it["title"] ?: "ZingBusiness", it["image"], intent
            )

        }


    }

    private fun sendNotification(
        messageBody: String, title: String, imageUrl: String?, intent: Intent
    ) {
        createNotificationChannel()

        val notificationManager = ContextCompat.getSystemService(
            applicationContext, NotificationManager::class.java
        ) as NotificationManager
        notificationManager.sendNotification(
            messageBody, title, imageUrl, intent, applicationContext
        )
    }

    private fun redirectToSplash(): Intent {
        val intent = Intent("SPLASH_ACTIVITY").putExtra("notification", 1)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return intent
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Zing Business"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val audioAttributes =
                AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
            channel.enableLights(true)
            channel.setSound(
                Uri.parse("android.resource://" + applicationContext.packageName + "/" + R.raw.sliver_sprint),
                null
            )
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager.createNotificationChannel(channel)

        }
    }


}