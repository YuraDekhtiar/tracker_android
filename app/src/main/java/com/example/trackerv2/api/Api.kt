package com.example.trackerv2.api

import com.example.trackerv2.models.ResponseLoginDevice
import com.example.trackerv2.models.ResponseRefreshToken
import com.example.trackerv2.models.ResponseSendLocation
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @FormUrlEncoded
    @POST("device-auth/login")
    fun loginDevice(
        @Field("login") login: String,
        @Field("password") password: String
    ): Call<ResponseLoginDevice>

    @FormUrlEncoded
    @POST("device-auth/logout")
    fun logout(
        @Field("refreshToken") refreshToken: String,
    ): Call<ResponseLoginDevice>

    @FormUrlEncoded
    @POST("device-auth/refresh-token")
    fun refreshToken(
        @Field("refreshToken") refreshToken: String
    ): Call<ResponseRefreshToken>

    @GET("device/status1")
    fun status(): Call<ResponseLoginDevice>

    @FormUrlEncoded
    @POST("device/locations")
    fun sendLocation(
        @Field("time") time: String,
        @Field("latitude") latitude: Double,
        @Field("longitude") longitude: Double,
        @Field("speed") speed: String,
        @Field("batteryLevel") batteryLevel: String,
        @Field("batteryTemp") batteryTemp: String,
        @Field("isCharging") isCharging: String,
        ): Call<ResponseSendLocation>



    // New Coroutines

    @FormUrlEncoded
    @POST("location")
    suspend fun sendLocationNew(
        @Field("time") time: String,
        @Field("latitude") latitude: Double,
        @Field("longitude") longitude: Double,
        @Field("speed") speed: String,
        @Field("batteryLevel") batteryLevel: String,
        @Field("batteryTemp") batteryTemp: String,
        @Field("isCharging") isCharging: String,
    ): ResponseSendLocation
}