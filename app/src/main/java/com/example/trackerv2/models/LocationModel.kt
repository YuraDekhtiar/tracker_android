package com.example.trackerv2.models

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import kotlin.math.roundToLong

@RequiresApi(Build.VERSION_CODES.N)
open class LocationModel(location: Location) {
    var latitude: Double
    var longitude: Double
    var time: String

    init {
        time = location.time.toString()
        latitude = toRound(location.latitude)
        longitude = toRound(location.longitude)
    }

    private fun toRound(value: Double): Double {
        val numbersAfterPoint = 10000.0

        return (value * numbersAfterPoint).roundToLong() / numbersAfterPoint
    }

    fun equalsLocation(location: LocationModel): Boolean {
        if(latitude == location.latitude && longitude == location.longitude)
            return true
        return false
    }

    override fun toString(): String {
        return "latitude: $latitude longitude: $longitude" +
                " datetime: $time"
    }

}