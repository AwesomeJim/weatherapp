package com.awesome.weatherapp.utilities

import com.awesome.weatherapp.models.Coordinates
import com.awesome.weatherapp.models.WeatherItemModel
import com.awesome.weatherapp.models.WeatherStatus
import com.awesome.weatherapp.models.Wind
import com.awesome.weatherapp.network.Constants.OWM_CITY
import com.awesome.weatherapp.network.Constants.OWM_CITY_ID
import com.awesome.weatherapp.network.Constants.OWM_CITY_NAME
import com.awesome.weatherapp.network.Constants.OWM_COORD
import com.awesome.weatherapp.network.Constants.OWM_HUMIDITY
import com.awesome.weatherapp.network.Constants.OWM_LATITUDE
import com.awesome.weatherapp.network.Constants.OWM_LIST
import com.awesome.weatherapp.network.Constants.OWM_LONGITUDE
import com.awesome.weatherapp.network.Constants.OWM_MAIN
import com.awesome.weatherapp.network.Constants.OWM_MAX
import com.awesome.weatherapp.network.Constants.OWM_MIN
import com.awesome.weatherapp.network.Constants.OWM_PRESSURE
import com.awesome.weatherapp.network.Constants.OWM_TEMPERATURE
import com.awesome.weatherapp.network.Constants.OWM_WEATHER
import com.awesome.weatherapp.network.Constants.OWM_WEATHER_ID
import com.awesome.weatherapp.network.Constants.OWM_WIND
import com.awesome.weatherapp.network.Constants.OWM_WINDSPEED
import com.awesome.weatherapp.network.Constants.OWM_WIND_DIRECTION
import com.awesome.weatherapp.utilities.WeatherDateUtils.normalizedUtcDateForToday
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*


/**
 * Utility functions to handle OpenWeatherMap JSON data.
 */
object ForecastListJsonUtils {

    /*
    {
    "cod": "200",
    "message": 0,
    "cnt": 40,
    "list": [
        {
            "dt": 1634493600,
            "main": {
                "temp": 18.81,
                "feels_like": 18.76,
                "temp_min": 18.81,
                "temp_max": 21.45,
                "pressure": 1019,
                "sea_level": 1019,
                "grnd_level": 837,
                "humidity": 77,
                "temp_kf": -2.64
            },
            "weather": [
                {
                    "id": 803,
                    "main": "Clouds",
                    "description": "broken clouds",
                    "icon": "04n"
                }
            ],
            "clouds": {
                "all": 75
            },
            "wind": {
                "speed": 5.54,
                "deg": 79,
                "gust": 7.92
            },
            "visibility": 10000,
            "pop": 0.19,
            "sys": {
                "pod": "n"
            },
            "dt_txt": "2021-10-17 18:00:00"
        },
        "city": {
        "id": 184745,
        "name": "Nairobi",
        "coord": {
            "lat": -1.2833,
            "lon": 36.8167
        },
        "country": "KE",
        "population": 2750547,
        "timezone": 10800,
        "sunrise": 1634440423,
        "sunset": 1634484127
    }
     */

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
    ): List<WeatherItemModel> {

        val normalizedUtcStartDay = normalizedUtcDateForToday
        /*
         * We ignore all the datetime values embedded in the JSON and assume that
         * the values are returned in-order by day (which is not guaranteed to be correct).
         */
        var dateTimeMillis: Long
        /*City details*/
        val city = forecastJson.getJSONObject(OWM_CITY)
        var locationName = city.getString(OWM_CITY_NAME)
        val locationId = city.getInt(OWM_CITY_ID)
        val country = city.getString("country")
        locationName = "$locationName -$country"

        /*City Coord*/
        val cityCoord = city.getJSONObject(OWM_COORD)
        val cityLatitude = cityCoord.getDouble(OWM_LATITUDE)
        val cityLongitude = cityCoord.getDouble(OWM_LONGITUDE)
        val coordinates = Coordinates(
            cityLongitude,
            cityLatitude
        )
        val jsonWeatherArray = forecastJson.getJSONArray(OWM_LIST)
        val weatherListItems: MutableList<WeatherItemModel> = ArrayList()
        for (i in 0 until jsonWeatherArray.length()) {
            val dayForecast = jsonWeatherArray.getJSONObject(i)
          //  dateTimeMillis = normalizedUtcStartDay + WeatherDateUtils.DAY_IN_MILLIS * i
            /**
             * open weather forecast returns response for 5 days with data every 3 hours (5*8)
             * I had to have way to get just a single forecast of the  day by using the the calender instance then filtering the list
             * with a distinct day of the , thus even though there are 8 entries for a single day we will have only one
             */
            val timeInMilliSeconds = dayForecast.getLong("dt")
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMilliSeconds * 1000
            dateTimeMillis = timeInMilliSeconds * 1000
            val locationWeatherDay = calendar[Calendar.DAY_OF_MONTH]
            Timber.e("<<<<<<<<<locationWeatherDay>>>>>>>>>>: %s", locationWeatherDay)
            //Preferences.setLocationDetails(context, cityLatitude, cityLongitude);
            /*
         * Description is in a child array called "weather", which is 1 element long.
         * That element also contains a weather code.
         */
            val weatherForecast = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0)
            val weatherConditionId = weatherForecast.getInt(OWM_WEATHER_ID)
            val weatherCondition = weatherForecast.getString(OWM_MAIN)
            val weatherConditionDescription = weatherForecast.getString("description")

            /* Get the JSON object representing the day */
            val mainForecast = dayForecast.getJSONObject(OWM_MAIN)
            val weatherTemp = mainForecast.getDouble(OWM_TEMPERATURE)
            val weatherTempMin = mainForecast.getDouble(OWM_MIN)
            val weatherTempMax = mainForecast.getDouble(OWM_MAX)
            val weatherPressure = mainForecast.getDouble(OWM_PRESSURE)
            val weatherHumidity = mainForecast.getInt(OWM_HUMIDITY)
            //Wind Details
            val windForecast = dayForecast.getJSONObject(OWM_WIND)
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
            val itemModel = WeatherItemModel(
                locationName,
                locationId,
                dateTimeMillis,
                coordinates,
                weatherStatus,
                locationWeatherDay
            )
            weatherListItems.add(itemModel)
        }
        return weatherListItems.distinctBy { it.locationWeatherDay }
    }
}