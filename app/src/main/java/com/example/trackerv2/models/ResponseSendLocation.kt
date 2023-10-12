package com.example.trackerv2.models

import com.google.gson.annotations.SerializedName

data class ResponseSendLocation(
    @SerializedName("error") var error: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("result") var result: String? = null
)