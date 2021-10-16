package com.awesome.weatherapp.repository

import com.awesome.weatherapp.di.network.ApiService
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun fetchOnlineWeather(
        appid: String,
        lat: String,
        lon: String
    ) = apiService.fetchWeather(appid, lat, lon)
}