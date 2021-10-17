package com.awesome.weatherapp.ui.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesome.weatherapp.di.network.NetworkHelper
import com.awesome.weatherapp.models.Coordinates
import com.awesome.weatherapp.models.WeatherItemModel
import com.awesome.weatherapp.network.ApiResponse
import com.awesome.weatherapp.network.Constants.OWM_MESSAGE_CODE
import com.awesome.weatherapp.repository.MainRepository
import com.awesome.weatherapp.utilities.OpenWeatherJsonUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber
import java.net.HttpURLConnection
import javax.inject.Inject


@HiltViewModel
class ForecastMainViewModel @Inject constructor(
    private val networkHelper: NetworkHelper,
    private val repository: MainRepository
) : ViewModel() {

    private var _apiWeatherDataResponse = MutableLiveData<ApiResponse<WeatherItemModel>>()
    val apiWeatherDataResponse: LiveData<ApiResponse<WeatherItemModel>>
        get() = _apiWeatherDataResponse

    fun loadWeatherData(coordinates: Coordinates) {
        _apiWeatherDataResponse.value = ApiResponse.loading(null)
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {// if connected lets load data from the Internet
                val data = repository.fetchWeatherWithLatitudeLongitude(coordinates)
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
                                            ApiResponse.success(weatherItemModel)
                                    }
                                    HttpURLConnection.HTTP_NOT_FOUND -> {/* Location invalid */
                                        _apiWeatherDataResponse.value = ApiResponse.error(
                                            "Location invalid",
                                            null
                                        )
                                    }
                                    else -> { /* Server probably down */
                                        _apiWeatherDataResponse.value = ApiResponse.error(
                                            "Server Error",
                                            null
                                        )
                                    }
                                }
                            }
                        }
                    }
                    else -> {/* Server probably down */
                        Timber.e(data.message())
                        _apiWeatherDataResponse.value = ApiResponse.error(
                            "Server Error",
                            null
                        )
                    }
                }
            } else {// Load Local Repository is it exist

            }
        }
    }
}