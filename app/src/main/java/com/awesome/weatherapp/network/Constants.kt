package com.awesome.weatherapp.network

import com.awesome.weatherapp.BuildConfig

object Constants {

    const val weatherEndpoint = "weather?appid=${BuildConfig.OPEN_WEATHER_APP_ID}"
    const val weatherForecaseEndpoint = "forecast?appid=${BuildConfig.OPEN_WEATHER_APP_ID}"

}