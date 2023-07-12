package com.example.trackerv2.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager


object BatteryUtils {
    private val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)

    fun getBatteryPercentage(context: Context): Int {
        val intent: Intent? = context.registerReceiver(null, iFilter)
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = level / scale.toFloat()
        return (batteryPct * 100).toInt()
    }

    fun getBatteryTemp(context: Context): Double {
        val intent: Intent? = context.registerReceiver(null, iFilter)
        return (intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1) / 10.0
    }

    fun isCharging(context: Context): Boolean {
        val intent: Intent? = context.registerReceiver(null, iFilter)
        val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
    }
}