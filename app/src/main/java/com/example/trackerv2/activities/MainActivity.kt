package com.example.trackerv2.activities

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.trackerv2.service.LocationUpdatesService
import com.example.trackerv2.R
import com.example.trackerv2.utils.Util
import com.example.trackerv2.api.RetrofitClient
import com.example.trackerv2.models.ResponseLoginDevice
import com.example.trackerv2.storage.UserPreference
import com.example.trackerv2.utils.ContextHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var mLocationService: LocationUpdatesService
    private lateinit var mServiceIntent: Intent

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ContextHolder.context = this.applicationContext
        val userPreference = UserPreference(this)
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        RetrofitClient.refreshToken = runBlocking {
            userPreference.authToken.first().toString()
        }
        setContentView(R.layout.activity_main)
        requestPermissions()

        mLocationService = LocationUpdatesService()
        mServiceIntent = Intent(this, mLocationService.javaClass)

        logoutButton.setOnClickListener {
            RetrofitClient.instance.logout(runBlocking {
                userPreference.authToken.first().toString()
            }).enqueue(object:
                Callback<ResponseLoginDevice> {
                override fun onResponse(
                    call: Call<ResponseLoginDevice>,
                    response: Response<ResponseLoginDevice>
                ) {
                    if (!Util.isServiceRunning(mLocationService.javaClass, this@MainActivity)) {
                        startService(mServiceIntent)
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.service_start_successfully),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        stopService(mServiceIntent)
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.service_stopped_successfully),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    runBlocking {
                        userPreference.saveAuthToken("null")
                    }
                    startActivity(loginIntent)
                }
                override fun onFailure(call: Call<ResponseLoginDevice>, t: Throwable) {
                    Toast.makeText(this@MainActivity, R.string.failure_connecting, Toast.LENGTH_LONG).show()
                }
            })

        }

        sendInfo.setOnClickListener {
            RetrofitClient.instance.status().enqueue(object :
                Callback<ResponseLoginDevice> {
                override fun onResponse(
                    call: Call<ResponseLoginDevice>,
                    response: Response<ResponseLoginDevice>
                ) {
                    Log.d("onFailure", "send")
                }

                override fun onFailure(call: Call<ResponseLoginDevice>, t: Throwable) {
                    Log.d("onFailure", t.toString())
                }

            })
        }

        locationUpdateButton.setOnClickListener {
            startStopLocationUpdate()
        }

        if(RetrofitClient.refreshToken == "null") {
            startActivity(loginIntent)
        } else {
            startStopLocationUpdate()
        }


    }
    private fun startStopLocationUpdate() {

        if(!Util.isPermissions(this)) return

        if (!Util.isServiceRunning(mLocationService.javaClass, this@MainActivity)) {
            startService(mServiceIntent)
            Toast.makeText(
                this@MainActivity,
                getString(R.string.service_start_successfully),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            stopService(mServiceIntent)
            Toast.makeText(
                this@MainActivity,
                getString(R.string.service_stopped_successfully),
                Toast.LENGTH_SHORT
            ).show()
        }
        updateStartStopButton()
    }
    private fun updateStartStopButton() {
        if (Util.isServiceRunning(mLocationService.javaClass, this@MainActivity)) {
            locationUpdateButton.text = getString(R.string.stop_location_updates)
        } else {
            locationUpdateButton.text = getString(R.string.start_location_updates)
        }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun requestPermissions() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                    Toast.makeText(this, "approximate location access granted.", Toast.LENGTH_LONG).show()

                } else -> {
                // No location access granted.
                Toast.makeText(this, "No location access granted.", Toast.LENGTH_LONG).show()
            }
            }
        }

        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }
}