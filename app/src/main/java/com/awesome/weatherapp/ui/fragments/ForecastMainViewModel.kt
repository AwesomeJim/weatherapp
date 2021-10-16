package com.awesome.weatherapp.ui.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesome.weatherapp.BuildConfig
import com.awesome.weatherapp.di.network.NetworkHelper
import com.awesome.weatherapp.network.ApiResponse
import com.awesome.weatherapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ForecastMainViewModel @Inject constructor(
    private val networkHelper: NetworkHelper,
    private val repository: MainRepository
) : ViewModel() {

    private val weatherLiveData = MutableLiveData<String?>()

    private var _apiWeatherDataResponse = MutableLiveData<ApiResponse<String>>()
    val apiWeatherDataResponse: LiveData<ApiResponse<String>>
        get() = _apiWeatherDataResponse


     fun loadWeatherData(url: String) {
        _apiWeatherDataResponse.value = ApiResponse.loading(null)
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val url ="${ BuildConfig.OPEN_WEATHER_APP_ID}&units=metric&lat=-1.28337&lon=36.8167"
                //https://api.openweathermap.org/data/2.5/weather?appid=c4c097fd5ac619b813670df592fc1391&units=metric&lat=-1.28337&lon=36.8167// if connected lets load data from the Internet
                val data = repository.fetchOnlineWeather(BuildConfig.OPEN_WEATHER_APP_ID,"-1.28337","36.8167")
                when (data.isSuccessful) {
                    true -> {
                        with(data.body().orEmpty()) {
                            Timber.e(this)
                        }
                    }
                    else -> {
                        Timber.e(data.message())
                    }
                }
            } else {// Load Local Repository is it exist

            }
        }
    }
}