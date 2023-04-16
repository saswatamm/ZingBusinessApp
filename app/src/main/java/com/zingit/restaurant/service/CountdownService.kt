package com.zingit.restaurant.service


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.zingit.restaurant.R
import kotlin.random.Random


class CountdownService : Service() {
    companion object {
        private const val CHANNEL_ID = "countdown_channel"
        private  val NOTIFICATION_ID = Random.nextInt(1000)
        private const val TIMER_DURATION = 60000L // 1 minute in milliseconds
    }

    private lateinit var timer: CountDownTimer

    override fun onCreate() {
        super.onCreate()

        // Create a notification channel
        createNotificationChannel()
        val soundUri =
            Uri.parse("android.resource://" + applicationContext.packageName + "/" + R.raw.sliver_sprint)

        // Create a NotificationCompat.Builder object
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Accept Order")
            .setContentText("Accept Order before countdown ends")
            .setAutoCancel(true)
            .setSound(soundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setUsesChronometer(true)

        // Start the service as a foreground service with the notification
        startForeground(NOTIFICATION_ID, builder.build())

        // Start the countdown timer
        startCountdownTimer()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Countdown Timer",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.setSound(
                Uri.parse("android.resource://" + applicationContext.packageName + "/" + R.raw.sliver_sprint),
                null)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startCountdownTimer() {
        timer = object : CountDownTimer(TIMER_DURATION, 1000) {
            @SuppressLint("MissingPermission")
            override fun onTick(millisUntilFinished: Long) {
                // Update the notification's content text with the remaining time in seconds
                val remainingTime = millisUntilFinished / 1000
                val builder = NotificationCompat.Builder(this@CountdownService, CHANNEL_ID)
                    .setContentText("Accept Order before $remainingTime seconds")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setChronometerCountDown(true)
                    .setWhen(remainingTime)
                val notificationManager = NotificationManagerCompat.from(this@CountdownService)
                notificationManager.notify(NOTIFICATION_ID, builder.build())
            }

            override fun onFinish() {
                // Remove the notification and stop the service
                val notificationManager = NotificationManagerCompat.from(this@CountdownService)
                notificationManager.cancel(NOTIFICATION_ID)
                stopForeground(true)
                stopSelf()
            }
        }

        timer.start()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Cancel the countdown timer if the service is destroyed
        if (::timer.isInitialized) {
            timer.cancel()
        }
    }
}

