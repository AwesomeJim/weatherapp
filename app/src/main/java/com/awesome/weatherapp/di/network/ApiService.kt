package com.awesome.weatherapp.di.network

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("api/{endPoint}")
    suspend fun makeApiCall(@Header("Authorization") token:String, @Path("endPoint") endPoint: String, @Body body: RequestBody): Response<String>
}