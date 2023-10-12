package com.example.trackerv2.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trackerv2.api.RetrofitClient
import com.example.trackerv2.models.LocationModel
import com.example.trackerv2.utils.BatteryUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.*

class LocationUpdatesService : Service() {

    private val TAG = "LocationService"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var viewModel: MyViewModel = MyViewModel()
    private val job = Job()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            createNotificationChanel()
        else
            startForeground(
                1,
                Notification()
            )
        requestLocationUpdates()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChanel() {
        val NOTIFICATION_CHANNEL_ID = "com.example.tracker"
        val channelName = "Background Service"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager =
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationBuilder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setContentTitle("App is running")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(2, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        Log.d("MyLog", "requestLocationUpdates")

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        CoroutineScope(Dispatchers.Default + job).launch {
            while (true) {
                runBlocking {
                    fusedLocationClient.getCurrentLocation(
                        LocationRequest.PRIORITY_HIGH_ACCURACY,
                        object : CancellationToken() {
                            override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                                CancellationTokenSource().token

                            override fun isCancellationRequested() = false
                        })
                        .addOnSuccessListener { location: Location? ->
                            if (location != null) {
                                viewModel.sendData(location, this@LocationUpdatesService)
//                                currentLocation = LocationModel(location)
//                                RetrofitClient.instance.sendLocation(
//                                    currentLocation.time,
//                                    currentLocation.latitude,
//                                    currentLocation.longitude,
//                                    (if (location.speed < 1) 0 else location.speed).toString(),
//                                    BatteryUtils.getBatteryPercentage(this@LocationUpdatesService).toString(),
//                                    BatteryUtils.getBatteryTemp(this@LocationUpdatesService).toString(),
//                                    BatteryUtils.isCharging(this@LocationUpdatesService).toString()
//                                ).enqueue(object:
//                                    Callback<ResponseSendLocation> {
//                                    override fun onResponse(
//                                        call: Call<ResponseSendLocation>,
//                                        response: Response<ResponseSendLocation>
//                                    ) {
//                                        Log.d("onResponse", "ResponseSendLocation")
//                                    }
//                                    override fun onFailure(call: Call<ResponseSendLocation>, t: Throwable) {
//                                        Log.d("onFailure", "ResponseSendLocation")
//                                    }
//                                })
                            }
                        }
                }
                delay(5000)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun requestLocationUpdatesNew() {
        try {
            fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this)

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
//            while (true) {
//                runBlocking {
//                    fusedLocationClient.getCurrentLocation(
//                        LocationRequest.PRIORITY_HIGH_ACCURACY,
//                        object : CancellationToken() {
//                            override fun onCanceledRequested(p0: OnTokenCanceledListener) =
//                                CancellationTokenSource().token
//
//                            override fun isCancellationRequested() = false
//                        })
//                        .addOnSuccessListener { location: Location? ->
//                            viewModel.sendData(location, this@LocationUpdatesService)
//                        }
//                    delay(5000)
//                }
//            }
        } catch (e: Exception) {
            Log.d("MyLog", "Exception: $e")
        }

    }
}

class MyViewModel : ViewModel() {
    private lateinit var currentLocation: LocationModel

    @RequiresApi(Build.VERSION_CODES.N)
    fun sendData(location: Location?, context: Context) {
        Log.d("MyLog", "sendData")
        viewModelScope.launch {
            try {
                if (location != null) {
                    currentLocation = LocationModel(location)
                    RetrofitClient.instance.sendLocationNew(
                        currentLocation.time,
                        currentLocation.latitude,
                        currentLocation.longitude,
                        (if (location.speed < 1) 0 else location.speed).toString(),
                        BatteryUtils.getBatteryPercentage(context).toString(),
                        BatteryUtils.getBatteryTemp(context).toString(),
                        BatteryUtils.isCharging(context).toString()
                    )
                }
            } catch (e: Exception) {
                Log.d("MyLog", "Exception: $e")
            }
        }
    }
}
