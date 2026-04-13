package com.mad411.habittracker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat

//foreground service that simulates tracking a timed habit like meditation
// foreground means the system treats this as important and wont kill it easily
//the tradeoff is we MUST show a persistent notification while its running

class HabitTimerService : Service() {

    //we dont support binding so just return null
    //bound services are a whole diff thing, this is a started service
    override fun onBind(intent: Intent?): IBinder? = null

    //runs every time someone calls startService or startForegroundService
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //channel has to be set up first, same deal as with workmanager
        createChannel()

        //build the persistent notification, this is REQUIRED for a foreground service
        //if we dont call startForeground within ~5 seconds the system kills us with an ANR
        val notification = buildNotification()

        // On Android 14+ (API 34), startForeground() MUST receive the type that matches
        // the foregroundServiceType declared in the manifest, or the system throws
        // InvalidForegroundServiceTypeException. ServiceCompat handles the API version split.
        @Suppress("InlinedApi")
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notification,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            else
                0
        )

        //START_STICKY means if the system kills us, try to restart us later
        return START_STICKY
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Habit Timer",
                //LOW so it doesnt buzz every time, its just status info
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_recent_history)
            .setContentTitle("Habit Timer")
            .setContentText("Tracking meditation...")
            //ongoing means user cant swipe it away, correct behavior for foreground service
            .setOngoing(true)
            .build()
    }

    companion object {
        const val CHANNEL_ID = "habit_timer_channel"
        const val NOTIFICATION_ID = 2002

        //helper to start this service from anywhere
        //wraps the startForegroundService call so callers dont need to know the details
        fun start(context: Context) {
            val intent = Intent(context, HabitTimerService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}
