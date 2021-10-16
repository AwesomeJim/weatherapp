package com.awesome.weatherapp.di.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("weather?units=metric")
    suspend fun fetchWeather(
        @Query("appid") appid: String,
        @Query("lat") lat: String,
        @Query("lon") lon: String
    ): Response<String>


    @GET("forecast?units=metric")
    suspend fun fetchWeatherForecast(
        @Query("appid") appid: String,
        @Query("lat") lat: String,
        @Query("lon") lon: String
    ): Response<String>

}