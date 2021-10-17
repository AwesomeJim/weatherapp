package com.awesome.weatherapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.awesome.weatherapp.adapters.ForecastListAdapter
import com.awesome.weatherapp.databinding.FragmentMainForecastBinding
import com.awesome.weatherapp.models.Coordinates
import com.awesome.weatherapp.models.WeatherItemModel
import com.awesome.weatherapp.network.Status
import com.awesome.weatherapp.utilities.WeatherDateUtils
import com.awesome.weatherapp.utilities.WeatherUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ForecastMainFragment : Fragment() {

    internal var view: View? = null
    private var _binding: FragmentMainForecastBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var adaptor: ForecastListAdapter

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
        binding.recyclerviewForecast.adapter = adaptor
        binding.recyclerviewForecast.layoutManager =
            LinearLayoutManager(binding.recyclerviewForecast.context)
        binding.recyclerviewForecast.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
        val coordinates = Coordinates(
            36.8167, -1.28337
        )
        viewModel.loadWeatherData(coordinates)
        viewModel.loadForecastWeatherData(coordinates)
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
        setUpObservers()

    }

    private fun setUpObservers() {
        viewModel.apiForecastWeatherDataResponse.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { weatherItems ->
                        adaptor.setForecastListItemst(requireContext(), weatherItems)
                        adaptor.notifyDataSetChanged()
                    }
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
                        true
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