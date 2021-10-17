package com.awesome.weatherapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.awesome.weatherapp.databinding.FragmentMainForecastBinding
import com.awesome.weatherapp.models.Coordinates
import com.awesome.weatherapp.models.WeatherItemModel
import com.awesome.weatherapp.network.Status
import com.awesome.weatherapp.utilities.WeatherDateUtils
import com.awesome.weatherapp.utilities.WeatherUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForecastMainFragment : Fragment() {

    internal var view: View? = null
    private var _binding: FragmentMainForecastBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ForecastMainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainForecastBinding.inflate(inflater, container, false)
        view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //  val url = "${BuildConfig.OPEN_WEATHER_APP_ID}&units=metric&lat=-1.28337&lon=36.8167"
        val coordinates = Coordinates(
            36.8167, -1.28337
        )
        viewModel.loadWeatherData(coordinates)
        viewModel.apiWeatherDataResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { weatherItemModel -> bindWeatherData(weatherItemModel) }
                }
                Status.LOADING -> {

                }
                Status.ERROR -> {

                }
            }
        }

    }

    private fun bindWeatherData(weatherItemModel: WeatherItemModel) {
        with(binding) {
            weatherItemModel.also {
                val weatherTempMin: String = WeatherUtils.formatTemperature(
                    requireContext(),
                    it.locationWeather.weatherTempMin
                )
                val weatherTemp: String = WeatherUtils.formatTemperature(
                    requireContext(),
                    it.locationWeather.weatherTemp
                )
                val weatherTempMax: String = WeatherUtils.formatTemperature(
                    requireContext(),
                    it.locationWeather.weatherTempMax
                )
                val dateString: String =
                    WeatherDateUtils.getFriendlyDateString(
                        requireContext(), it.locationDate,
                        false
                    )
                date.text = dateString
                tvMainTemperature.text = weatherTemp
                tvWeatherDescription.text = it.locationWeather.weatherCondition
                tvTempMini.text = weatherTempMin
                tvTempCurrent.text = weatherTemp
                tvTempHigh.text = weatherTempMax
            }

        }
    }

}