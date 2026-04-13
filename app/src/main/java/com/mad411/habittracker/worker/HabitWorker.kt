package com.mad411.habittracker.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

// worker that runs once a day and reminds the user to check their habits
//extending CoroutineWorker instead of Worker so doWork can be suspend and we can do async stuff if needed

class HabitWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    //this is where the actual background work happens
    //workmanager calls this on a background thread for us
    override suspend fun doWork(): Result {
        return try {
            showReminderNotification()
            //tell workmanager we succeeded so it doesnt retry
            Result.success()
        } catch (e: Exception) {
            //retry means workmanager will try again later with backoff
            Result.retry()
        }
    }

    private fun showReminderNotification() {
        val manager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //notification channel is required on oreo and up
        //if you skip this the notification silently doesnt show which is SUPER annoying to debug
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Habit Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily reminder to check your habits"
            }
            manager.createNotificationChannel(channel)
        }

        //build the actual notification
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            //using a built in icon, in a real app youd use your own drawable
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("Dont forget your habits")
            .setContentText("Take a sec to check in on todays habits")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }

    //constants in companion so we can reference them from outside if needed
    companion object {
        const val CHANNEL_ID = "habit_reminder_channel"
        const val NOTIFICATION_ID = 1001
    }
}
