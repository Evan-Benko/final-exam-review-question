package com.mad411.habittracker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

// broadcast reciever that fires when the system says battery is low
//we register this at runtime from MainActivity because since oreo implicit broadcasts
//cant be declared in the manifest for most actions
//action were listening for is Intent.ACTION_BATTERY_LOW

class BatteryReceiver : BroadcastReceiver() {

    //onReceive runs on the main thread so keep it FAST, no file io or network here
    //if you need to do heavy work, start a worker or service from in here
    override fun onReceive(context: Context?, intent: Intent?) {
        //null checks for safety even though these are basically always non null
        if (context == null || intent == null) return

        //only react to the one action we care about
        if (intent.action == Intent.ACTION_BATTERY_LOW) {
            Log.d("BatteryReceiver", "battery low broadcast received")
            //toast to tell the user were pausing sync
            //could also emit a snackbar from here by updating some shared state
            Toast.makeText(
                context,
                "Sync paused due to low battery",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
