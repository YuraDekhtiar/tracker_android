package com.example.trackerv2.models

import com.google.gson.annotations.SerializedName


data class ResponseLoginDevice(
    @SerializedName("error"        ) var error        : Boolean? = null,
    @SerializedName("message"      ) var message      : String?  = null,
    @SerializedName("result"       ) var device       : Device?  = Device()
)