package com.awesome.weatherapp.network

object Constants {

    /* Weather information. Each day's forecast info is an element of the "list" array */
     const val OWM_LIST = "list"

    /* Location information */
     const val OWM_CITY_NAME = "name"
     const val OWM_CITY_ID = "id"
     const val OWM_CITY = "city"

    /* Location coordinate */
     const val OWM_COORD = "coord"
     const val OWM_LATITUDE = "lat"
     const val OWM_LONGITUDE = "lon"

    /* Location wind */
     const val OWM_WIND = "wind"
     const val OWM_MAIN = "main"
     const val OWM_PRESSURE = "pressure"
     const val OWM_HUMIDITY = "humidity"
     const val OWM_WINDSPEED = "speed"
     const val OWM_WIND_DIRECTION = "deg"

    /* All temperatures are children of the "temp" object */
     const val OWM_TEMPERATURE = "temp"

    /* Max temperature for the day */
     const val OWM_MAX = "temp_max"
     const val OWM_MIN = "temp_min"
     const val OWM_WEATHER = "weather"
     const val OWM_WEATHER_ID = "id"
     const val OWM_MESSAGE_CODE = "cod"

    const val INDEX_WEATHER_DATE = 0
    const val INDEX_WEATHER_MAX_TEMP = 1
    const val INDEX_WEATHER_MIN_TEMP = 2
    const val INDEX_WEATHER_CONDITION_ID = 3

}