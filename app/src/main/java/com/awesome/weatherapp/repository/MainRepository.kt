package com.awesome.weatherapp.repository

import com.awesome.weatherapp.BuildConfig
import com.awesome.weatherapp.di.network.ApiService
import com.awesome.weatherapp.di.network.NetworkUtils
import com.awesome.weatherapp.models.Coordinates
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun fetchWeatherWithLatitudeLongitude(
        coordinates: Coordinates
    ) = apiService.fetchWeatherWithLatitudeLongitude(
        BuildConfig.OPEN_WEATHER_APP_ID,
        NetworkUtils.units,
        coordinates.latitude.toString(),
        coordinates.longitude.toString()
    )


    suspend fun fetchWeatherWithLocationQuery(
        locationQuery: String,
    ) = apiService.fetchWeatherWithLocationQuery(
        BuildConfig.OPEN_WEATHER_APP_ID,
        NetworkUtils.units,
        locationQuery
    )


    suspend fun fetchWeatherWeatherForecast(
        coordinates: Coordinates
    ) = apiService.fetchWeatherForecast(
        BuildConfig.OPEN_WEATHER_APP_ID,
        NetworkUtils.units,
        coordinates.latitude.toString(),
        coordinates.longitude.toString()
    )
}