package com.example.trackerv2.models

import com.google.gson.annotations.SerializedName

data class ResultToken (
    @SerializedName("access_token"  ) var accessToken  : String? = null,
    @SerializedName("refresh_token" ) var refreshToken : String? = null
)