package com.awesome.weatherapp.ui.fragments

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesome.weatherapp.R
import com.awesome.weatherapp.di.network.NetworkHelper
import com.awesome.weatherapp.models.Coordinates
import com.awesome.weatherapp.models.WeatherItemModel
import com.awesome.weatherapp.network.ApiResponse
import com.awesome.weatherapp.network.Constants.OWM_MESSAGE_CODE
import com.awesome.weatherapp.repository.MainRepository
import com.awesome.weatherapp.utilities.Event
import com.awesome.weatherapp.utilities.ForecastListJsonUtils
import com.awesome.weatherapp.utilities.OpenWeatherJsonUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber
import java.net.HttpURLConnection
import javax.inject.Inject


@HiltViewModel
class ForecastMainViewModel @Inject constructor(
    private val networkHelper: NetworkHelper,
    private val repository: MainRepository,
    private val application: Application
) : ViewModel() {

    private var _apiWeatherDataResponse = MutableLiveData<Event<ApiResponse<WeatherItemModel>>>()
    val apiWeatherDataResponse: LiveData<Event<ApiResponse<WeatherItemModel>>>
        get() = _apiWeatherDataResponse

    fun loadWeatherData(coordinates: Coordinates) {
        _apiWeatherDataResponse.value = Event(ApiResponse.loading(null))
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {// if connected lets load data from the Internet
                val response = async {
                    repository.fetchWeatherWithLatitudeLongitude(coordinates)
                }
                val data = response.await()
                when (data.isSuccessful) {
                    true -> {
                        with(data.body().orEmpty()) {
                            Timber.e(this)
                            val forecastJson = JSONObject(this)
                            if (forecastJson.has(OWM_MESSAGE_CODE)) {
                                when (forecastJson.getInt(OWM_MESSAGE_CODE)) {
                                    HttpURLConnection.HTTP_OK -> {//location exist
                                        val weatherItemModel =
                                            OpenWeatherJsonUtils.getWeatherContentValuesFromJson(
                                                forecastJson
                                            )
                                        _apiWeatherDataResponse.value =
                                            Event(ApiResponse.success(weatherItemModel))
                                    }
                                    HttpURLConnection.HTTP_NOT_FOUND -> {/* Location invalid */
                                        _apiWeatherDataResponse.value = Event(
                                            ApiResponse.error(
                                                "Location invalid",
                                                null
                                            )
                                        )
                                    }
                                    else -> { /* Server probably down */
                                        _apiWeatherDataResponse.value = Event(
                                            ApiResponse.error(
                                                "Server Error",
                                                null
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                    else -> {/* Server probably down */
                        Timber.e(data.message())
                        _apiWeatherDataResponse.value = Event(
                            ApiResponse.error(
                                "Server Error",
                                null
                            )
                        )
                    }
                }
            } else {// Load Local Repository is it exist
                _apiWeatherDataResponse.value = Event(
                    ApiResponse.error(
                        application.applicationContext.resources.getString(R.string.internetErr),
                        null
                    )
                )
            }
        }
    }

    private var _apiForecastWeatherDataResponse =
        MutableLiveData<Event<ApiResponse<List<WeatherItemModel>>>>()
    val apiForecastWeatherDataResponse: LiveData<Event<ApiResponse<List<WeatherItemModel>>>>
        get() = _apiForecastWeatherDataResponse

    fun loadForecastWeatherData(coordinates: Coordinates) {
        _apiForecastWeatherDataResponse.value = Event(ApiResponse.loading(null))
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = async { repository.fetchWeatherWeatherForecast(coordinates) }
                val data = response.await()
                when (data.isSuccessful) {
                    true -> {
                        with(data.body().orEmpty()) {
                            Timber.e(this)
                            val forecastJson = JSONObject(this)
                            if (forecastJson.has(OWM_MESSAGE_CODE)) {
                                when (forecastJson.getInt(OWM_MESSAGE_CODE)) {
                                    HttpURLConnection.HTTP_OK -> {//location exist
                                        val weatherItemModel =
                                            ForecastListJsonUtils.getWeatherContentValuesFromJson(
                                                forecastJson
                                            )
                                        _apiForecastWeatherDataResponse.value =
                                            Event(ApiResponse.success(weatherItemModel))
                                    }
                                    HttpURLConnection.HTTP_NOT_FOUND -> {/* Location invalid */
                                        _apiForecastWeatherDataResponse.value = Event(
                                            ApiResponse.error(
                                                "Location invalid",
                                                null
                                            )
                                        )
                                    }
                                    else -> { /* Server probably down */
                                        _apiForecastWeatherDataResponse.value = Event(
                                            ApiResponse.error(
                                                "Server Error",
                                                null
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                    else -> {/* Server probably down */
                        Timber.e(data.message())
                        _apiForecastWeatherDataResponse.value = Event(
                            ApiResponse.error(
                                "Server Error",
                                null
                            )
                        )
                    }
                }
            }
        }
    }
}