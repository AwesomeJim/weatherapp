package com.awesome.weatherapp.network

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName


data class GeneralResponse(
    @SerializedName("message") val message: String,
    @SerializedName("status") val status: Int,
    @SerializedName("data") val data: JsonObject
)