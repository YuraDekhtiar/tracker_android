package com.example.trackerv2.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ResponseRefreshToken(
    @SerializedName("error")
    var error: Boolean? = null,
    @SerializedName("message")
    var message: String?  = null,
    @SerializedName("result")
    var result: ResultToken?  = ResultToken()
)