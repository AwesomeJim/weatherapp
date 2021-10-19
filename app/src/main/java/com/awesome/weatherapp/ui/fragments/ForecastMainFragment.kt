package com.awesome.weatherapp.ui.fragments

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.awesome.weatherapp.R
import com.awesome.weatherapp.adapters.ForecastListAdapter
import com.awesome.weatherapp.databinding.FragmentMainForecastBinding
import com.awesome.weatherapp.models.Coordinates
import com.awesome.weatherapp.models.WeatherItemModel
import com.awesome.weatherapp.network.Status
import com.awesome.weatherapp.utilities.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
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
        requestPermissionsIfNecessary()
        setUpObservers()
        setUpView()


    }

    private fun setUpView() {
        binding.recyclerviewForecast.adapter = adaptor
        binding.recyclerviewForecast.layoutManager =
            LinearLayoutManager(binding.recyclerviewForecast.context)
        binding.recyclerviewForecast.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
        adaptor.setOnItemClickListener { _, item, _ ->
            val args = Bundle().apply {
                putParcelable("weatherItemModel", item as WeatherItemModel)
            }
            this.findNavController()
                .navigate(R.id.action_forecastMainFragment_to_forecastDetailsFragment, args)
        }
        binding.btnFailedOk.setOnClickListener {
            getCurrentLocationAndFetchWeather()
        }
    }

    private fun setUpObservers() {
        viewModel.apiWeatherDataResponse.observe(viewLifecycleOwner, EventObserver {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.progressLayout.visibility = View.GONE
                    binding.failedLayout.visibility = View.GONE
                    binding.currentWeatherStatusView.visibility = View.VISIBLE
                    it.data?.let { weatherItemModel -> bindWeatherData(weatherItemModel) }
                }
                Status.LOADING -> {
                    binding.progressLayout.visibility = View.VISIBLE
                    binding.recyclerviewForecast.visibility = View.GONE
                    binding.failedLayout.visibility = View.GONE
                    binding.currentWeatherStatusView.visibility = View.GONE
                }
                Status.ERROR -> {
                    binding.progressLayout.visibility = View.GONE
                    binding.recyclerviewForecast.visibility = View.GONE
                    binding.failedLayout.visibility = View.VISIBLE
                    binding.currentWeatherStatusView.visibility = View.GONE
                    binding.tvFailedMsg.text = it.message
                    binding.lottieFailedAnimationView.playAnimation()
                }
            }
        })
        viewModel.apiForecastWeatherDataResponse.observe(viewLifecycleOwner, EventObserver {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { weatherItems ->
                        adaptor.setForecastListItemst(requireContext(), weatherItems)
                        adaptor.notifyDataSetChanged()
                    }
                    binding.recyclerviewForecast.visibility = View.VISIBLE
                }
                Status.LOADING -> {

                }
                Status.ERROR -> {

                }
            }
        })
    }

    private fun getCurrentLocationAndFetchWeather() {
        var longitude: Double? = LocationUtil.getInstance(requireContext())?.currentLongitude
        var latitude: Double? = LocationUtil.getInstance(requireContext())?.currentlLatitude
        if (!isGPSEnabled) {
            buildAlertMessageNoGps()
            return
        }
        val listener = object : LocationChangeCallback {
            override fun locationHasChanged(location: Location?) {
                longitude = LocationUtil.getInstance(requireContext())?.currentLongitude
                latitude = LocationUtil.getInstance(requireContext())?.currentlLatitude
                val coordinates = Coordinates(
                    longitude!!, latitude!!
                )
                viewModel.loadWeatherData(coordinates)
                viewModel.loadForecastWeatherData(coordinates)
            }
        }
        LocationUtil.getInstance(requireContext())?.setListener(listener)
        if (latitude == 0.0 && longitude == 0.0) {
            LocationUtil.getInstance(requireContext(), listener)?.myLocation
            Toast.makeText(
                requireContext(), "Finding device location... \nTry moving device.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            val coordinates = Coordinates(
                longitude!!, latitude!!
            )
            viewModel.loadWeatherData(coordinates)
            viewModel.loadForecastWeatherData(coordinates)
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
                tvWeatherDescription.text = it.locationWeather.weatherCondition.capitalize()
                tvTempMini.text = weatherTempMin
                tvTempCurrent.text = weatherTemp
                tvTempHigh.text = weatherTempMax
                tvCity.text = it.locationName
                when (it.locationWeather.weatherConditionId) {
                    in 801..804 -> {
                        binding.recyclerviewForecast.setBackgroundColor(resources.getColor(R.color.grey_cloudy))
                        binding.moreInfoLayout.setBackgroundColor(resources.getColor(R.color.grey_cloudy))
                        binding.primaryInfoLayout.setBackgroundResource(R.drawable.forest_cloudy)
                        (activity as AppCompatActivity?)!!.supportActionBar?.setBackgroundDrawable(ColorDrawable(
                            resources.getColor(R.color.grey_cloudy)))
                    }
                    in 799..800 -> {
                        binding.recyclerviewForecast.setBackgroundColor(resources.getColor(R.color.greenish_sunny))
                        binding.moreInfoLayout.setBackgroundColor(resources.getColor(R.color.greenish_sunny))
                        binding.primaryInfoLayout.setBackgroundResource(R.drawable.forest_sunny)
                        (activity as AppCompatActivity?)!!.supportActionBar?.setBackgroundDrawable(ColorDrawable(
                            resources.getColor(R.color.greenish_sunny)))
                    }
                    in 500..531 -> {
                        binding.recyclerviewForecast.setBackgroundColor(resources.getColor(R.color.grey_rainy))
                        binding.moreInfoLayout.setBackgroundColor(resources.getColor(R.color.grey_rainy))
                        binding.primaryInfoLayout.setBackgroundResource(R.drawable.forest_rainy)
                        (activity as AppCompatActivity?)!!.supportActionBar?.setBackgroundDrawable(ColorDrawable(
                            resources.getColor(R.color.grey_rainy)))
                    }
                }
            }
        }
        binding.currentWeatherStatusView.setOnClickListener {
            val args = Bundle().apply {
                putParcelable("weatherItemModel", weatherItemModel)
            }
            this.findNavController()
                .navigate(R.id.action_forecastMainFragment_to_forecastDetailsFragment, args)
        }
    }

    private val permissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_NETWORK_STATE

    )

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultMap ->
            resultMap.forEach {
                Timber.e("Permission: " + it.key + ", granted: " + it.value)
                getCurrentLocationAndFetchWeather()
            }
        }

    private fun requestPermissionsIfNecessary() {
        if (!checkAllPermissions()) {
            requestPermissionsLauncher.launch(permissions.toTypedArray())
        } else {
            getCurrentLocationAndFetchWeather()
        }
    }

    /** Permission Checking  */
    private fun checkAllPermissions(): Boolean {
        var hasPermissions = true
        for (permission in permissions) {
            hasPermissions = hasPermissions and isPermissionGranted(permission)
        }
        return hasPermissions
    }

    private fun isPermissionGranted(permission: String) =
        ActivityCompat.checkSelfPermission(requireContext(), permission) ==
                PackageManager.PERMISSION_GRANTED

    /**
     * Is gps enabled boolean.
     *
     * @return the boolean
     */
    val isGPSEnabled: Boolean
        get() {
            val manager =
                requireActivity().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
            return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Device Location is disabled. Are you ready to enable Location  to continue?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
                startActivity(
                    Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS
                    )
                )
            }
            .setNegativeButton("No") { dialog: DialogInterface, id: Int -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }
}