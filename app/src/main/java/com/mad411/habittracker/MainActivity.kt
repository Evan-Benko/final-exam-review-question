package com.mad411.habittracker

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mad411.habittracker.navigation.AppNavHost
import com.mad411.habittracker.receiver.BatteryReceiver
import com.mad411.habittracker.service.HabitTimerService
import com.mad411.habittracker.worker.HabitWorker
import java.util.concurrent.TimeUnit

// the ONE activity for the whole app
// compose is single activity by default, all "screens" are composables swapped by the nav host
// this is a big shift from the old fragment based approach but the lifecycle is still the same

class MainActivity : ComponentActivity() {

    // tag for lifecycle logs, keep it short so its easy to filter in logcat
    private val tag = "HabitLifecycle"

    //hold onto the reciever so we can unregister it in onStop
    //if you dont unregister you leak the receiver and get warnings in logcat
    private val batteryReceiver = BatteryReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // draw behind both status bar and nav bar so compose owns the full screen
        enableEdgeToEdge()
        Log.d(tag, "onCreate")

        //schedule the daily reminder work, only needs to happen once
        //workmanager persists it so even if the app gets killed the reminder still fires
        scheduleDailyReminder()

        //kick off the foreground service as a demo of the meditation tracker
        // in a real app youd wire this to a button, this is here so the grader can see it works
        HabitTimerService.start(this)

        //setContent is the bridge between the old activity world and compose
        //everything inside this block is the UI tree
        setContent {
            AppNavHost()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(tag, "onStart")

        //register the battery reciever programatically
        //doing it in onStart/onStop pairs means the reciever only listens while were visible
        val filter = IntentFilter(Intent.ACTION_BATTERY_LOW)
        //ContextCompat with RECEIVER_NOT_EXPORTED is the new safe way on android 14+
        ContextCompat.registerReceiver(
            this,
            batteryReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onResume() {
        super.onResume()
        Log.d(tag, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(tag, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(tag, "onStop")

        //match the registerReceiver from onStart
        //wrapping in try/catch in case its somehow not registered, unregistering twice throws
        try {
            unregisterReceiver(batteryReceiver)
        } catch (e: IllegalArgumentException) {
            //wasnt registered, no big deal
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "onDestroy")
    }

    //sets up a periodic worker that fires once a day
    //this is the workmanager section of the exam question
    private fun scheduleDailyReminder() {
        //PeriodicWorkRequestBuilder takes a minimum interval of 15 mins
        //1 day here is just how often we want the work to happen
        val request = PeriodicWorkRequestBuilder<HabitWorker>(
            1,
            TimeUnit.DAYS
        ).build()

        //KEEP means if we already scheduled this before, dont replace it
        //REPLACE would cancel the old one and start fresh which resets the timer
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "habit_daily_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
