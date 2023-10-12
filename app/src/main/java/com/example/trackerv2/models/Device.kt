package com.example.trackerv2.models

import com.google.gson.annotations.SerializedName

data class Device(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("login") var login: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("access_token") var accessToken: String? = null,
    @SerializedName("refresh_token") var refreshToken: String? = null
)
