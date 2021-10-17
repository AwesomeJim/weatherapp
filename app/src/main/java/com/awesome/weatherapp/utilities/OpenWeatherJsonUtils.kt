package com.awesome.weatherapp.utilities

import com.awesome.weatherapp.models.Coordinates
import com.awesome.weatherapp.models.WeatherItemModel
import com.awesome.weatherapp.models.WeatherStatus
import com.awesome.weatherapp.models.Wind
import com.awesome.weatherapp.utilities.WeatherDateUtils.normalizedUtcDateForToday
import org.json.JSONException
import org.json.JSONObject

/**
 * Utility functions to handle OpenWeatherMap JSON data.
 */
object OpenWeatherJsonUtils {
    /**
     * Fields in API response
     *
     *
     * coord
     * coord.lon City geo location, longitude
     * coord.lat City geo location, latitude
     * weather (more info Weather condition codes)
     * weather.id Weather condition id
     * weather.main Group of weather parameters (Rain, Snow, Extreme etc.)
     * weather.description Weather condition within the group. You can get the output in your language. Learn more
     * weather.icon Weather icon id
     * base Internal parameter
     * main
     * main.temp Temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
     * main.feels_like Temperature. This temperature parameter accounts for the human perception of weather. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
     * main.pressure Atmospheric pressure (on the sea level, if there is no sea_level or grnd_level data), hPa
     * main.humidity Humidity, %
     * main.temp_min Minimum temperature at the moment. This is minimal currently observed temperature (within large megalopolises and urban areas). Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
     * main.temp_max Maximum temperature at the moment. This is maximal currently observed temperature (within large megalopolises and urban areas). Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
     * main.sea_level Atmospheric pressure on the sea level, hPa
     * main.grnd_level Atmospheric pressure on the ground level, hPa
     * wind
     * wind.speed Wind speed. Unit Default: meter/sec, Metric: meter/sec, Imperial: miles/hour.
     * wind.deg Wind direction, degrees (meteorological)
     * wind.gust Wind gust. Unit Default: meter/sec, Metric: meter/sec, Imperial: miles/hour
     * clouds
     * clouds.all Cloudiness, %
     * rain
     * rain.1h Rain volume for the last 1 hour, mm
     * rain.3h Rain volume for the last 3 hours, mm
     * snow
     * snow.1h Snow volume for the last 1 hour, mm
     * snow.3h Snow volume for the last 3 hours, mm
     * dt Time of data calculation, unix, UTC
     * sys
     * sys.type Internal parameter
     * sys.id Internal parameter
     * sys.message Internal parameter
     * sys.country Country code (GB, JP etc.)
     * sys.sunrise Sunrise time, unix, UTC
     * sys.sunset Sunset time, unix, UTC
     * timezone Shift in seconds from UTC
     * id City ID
     * name City name
     * cod Internal parameter
     */
    /* Location information */
    private const val OWM_CITY_NAME = "name"
    private const val OWM_CITY_ID = "id"
    private const val OWM_CITY = "city"

    /* Location coordinate */
    private const val OWM_COORD = "coord"
    private const val OWM_LATITUDE = "lat"
    private const val OWM_LONGITUDE = "lon"

    /* Location wind */
    private const val OWM_WIND = "wind"
    private const val OWM_MAIN = "main"
    private const val OWM_PRESSURE = "pressure"
    private const val OWM_HUMIDITY = "humidity"
    private const val OWM_WINDSPEED = "speed"
    private const val OWM_WIND_DIRECTION = "deg"

    /* All temperatures are children of the "temp" object */
    private const val OWM_TEMPERATURE = "temp"

    /* Max temperature for the day */
    private const val OWM_MAX = "temp_max"
    private const val OWM_MIN = "temp_min"
    private const val OWM_WEATHER = "weather"
    private const val OWM_WEATHER_ID = "id"
    private const val OWM_MESSAGE_CODE = "cod"

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the weather over various days from the forecast.
     *
     *
     * Later on, we'll be parsing the JSON into structured data within the
     * getFullWeatherDataFromJson function, leveraging the data we have stored in the JSON. For
     * now, we just convert the JSON into human-readable strings.
     *
     * @param forecastJson JSON response from server
     * @return Array of Strings describing weather data
     * @throws JSONException If JSON data cannot be properly parsed
     */
    @Throws(JSONException::class)
    fun getWeatherContentValuesFromJson(
        forecastJson: JSONObject
    ): WeatherItemModel {
        val normalizedUtcStartDay = normalizedUtcDateForToday
        /*
         * We ignore all the datetime values embedded in the JSON and assume that
         * the values are returned in-order by day (which is not guaranteed to be correct).
         */
        val dateTimeMillis = normalizedUtcStartDay + WeatherDateUtils.DAY_IN_MILLIS*0
        val locationName = forecastJson.getString(OWM_CITY_NAME)
        val locationId = forecastJson.getInt(OWM_CITY_ID)
        val cityCoord = forecastJson.getJSONObject(OWM_COORD)
        val cityLatitude = cityCoord.getDouble(OWM_LATITUDE)
        val cityLongitude = cityCoord.getDouble(OWM_LONGITUDE)
        val coordinates = Coordinates(
            cityLongitude,
            cityLatitude
        )
        //Preferences.setLocationDetails(context, cityLatitude, cityLongitude);
        /*
         * Description is in a child array called "weather", which is 1 element long.
         * That element also contains a weather code.
         */
        val weatherForecast = forecastJson.getJSONArray(OWM_WEATHER).getJSONObject(0)
        val weatherConditionId = weatherForecast.getInt(OWM_WEATHER_ID)
        val weatherCondition = weatherForecast.getString(OWM_MAIN)
        val weatherConditionDescription = weatherForecast.getString("description")

        /* Get the JSON object representing the day */
        val mainForecast = forecastJson.getJSONObject(OWM_MAIN)
        val weatherTemp = mainForecast.getDouble(OWM_TEMPERATURE)
        val weatherTempMin = mainForecast.getDouble(OWM_MIN)
        val weatherTempMax =  mainForecast.getDouble(OWM_MAX)
        val weatherPressure = mainForecast.getDouble(OWM_PRESSURE)
        val weatherHumidity = mainForecast.getInt(OWM_HUMIDITY)
        //Wind Details
        val windForecast = forecastJson.getJSONObject(OWM_WIND)
        val windSpeed = windForecast.getDouble(OWM_WINDSPEED)
        val windDirection = windForecast.getDouble(OWM_WIND_DIRECTION)
        val wind = Wind(windSpeed, windDirection)
        val weatherStatus = WeatherStatus(
            weatherConditionId,
            weatherCondition,
            weatherConditionDescription,
            weatherTemp,
            weatherTempMin,
            weatherTempMax,
            weatherPressure,
            weatherHumidity,
            wind
        )
        return WeatherItemModel(
            locationName,
            locationId,
            dateTimeMillis,
            coordinates,
            weatherStatus
        )
    }
}