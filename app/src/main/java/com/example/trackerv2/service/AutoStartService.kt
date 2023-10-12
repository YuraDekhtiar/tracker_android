package com.example.trackerv2.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import com.example.trackerv2.activities.MainActivity


class AutoStartService : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AutoStartService", "AutoStartService")
        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            context!!.startActivity(
                Intent(context, MainActivity::class.java).setFlags(
                    FLAG_ACTIVITY_NEW_TASK
                )
            )

        }
    }
}

