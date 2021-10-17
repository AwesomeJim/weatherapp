package com.awesome.weatherapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class WeatherItemModel(
    val locationName: String,
    val locationId: Int,
    val locationDate: Long,
    val locationCoordinates: Coordinates,
    val locationWeather: WeatherStatus,
    val locationWeatherDay: Int
) : Parcelable


@Parcelize
data class Coordinates(
    val longitude: Double,
    val latitude: Double,
) : Parcelable

@Parcelize
data class WeatherStatus(
    val weatherConditionId: Int,
    val weatherCondition: String,
    val weatherConditionDescription: String,
    val weatherTemp: Double,
    val weatherTempMin: Double,
    val weatherTempMax: Double,
    val weatherPressure: Double,
    val weatherHumidity: Int,
    val weatherWind: Wind
) : Parcelable


@Parcelize
data class Wind(
    val speed: Double,
    val deg: Double,
) : Parcelable