package com.example.trackerv2.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ResponseError(
    @SerializedName("error") var error: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("status") var status: Int? = null,
    @SerializedName("route") var route: String? = null
)
