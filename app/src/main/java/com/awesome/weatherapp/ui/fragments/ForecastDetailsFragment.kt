package com.awesome.weatherapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.awesome.weatherapp.R
import com.awesome.weatherapp.databinding.FragmentDetailForecastBinding
import com.awesome.weatherapp.models.WeatherItemModel
import com.awesome.weatherapp.utilities.WeatherDateUtils
import com.awesome.weatherapp.utilities.WeatherUtils


class ForecastDetailsFragment : Fragment() {

    private lateinit var weatherItemModel: WeatherItemModel

    private var _binding: FragmentDetailForecastBinding? = null
    internal var view: View? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            weatherItemModel =
                requireArguments().getParcelable<WeatherItemModel>("weatherItemModel") as WeatherItemModel
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_detail_forecast, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            weatherItemModel.also {
                /****************
                 * Weather Icon *
                 ****************/
                val weatherImageId: Int =
                    WeatherUtils.getLargeArtResourceIdForWeatherCondition(it.locationWeather.weatherConditionId)
                /* Set the resource ID on the icon to display the art */
                primaryInfo.weatherIcon.setImageResource(
                    weatherImageId
                )
                /****************
                 * Weather Date *
                 ****************/
                val dateText: String =
                    WeatherDateUtils.getFriendlyDateString(requireContext(), it.locationDate, true)

                primaryInfo.date.text = dateText

                /***********************
                 * Weather Description *
                 ***********************/
                /* Use the weatherId to obtain the proper description */
                val description: String =
                    WeatherUtils.getStringForWeatherCondition(
                        requireContext(),
                        it.locationWeather.weatherConditionId
                    )
                /* Set the text and content description (for accessibility purposes) */
                primaryInfo.weatherDescription.text = description

                /**************************
                 * High (max) temperature *
                 **************************/

                val highString: String = WeatherUtils.formatTemperature(
                    requireContext(),
                    it.locationWeather.weatherTempMax
                )

                /* Set the text and content description (for accessibility purposes) */
                primaryInfo.highTemperature.text = highString

                /*************************
                 * Low (min) temperature *
                 */
                val lowString: String = WeatherUtils.formatTemperature(
                    requireContext(),
                    it.locationWeather.weatherTempMin
                )
                /* Set the text and content description (for accessibility purposes) */
                primaryInfo.lowTemperature.text = lowString

                /************
                 * Humidity *
                 */
                /* Read humidity from the cursor */
                val humidity: Float = it.locationWeather.weatherHumidity.toFloat()
                val humidityString =
                    getString(R.string.format_humidity, humidity)
                /* Set the text and content description (for accessibility purposes) */
                extraDetails.humidity.text = humidityString

                /****************************
                 * Wind speed and direction *
                 ****************************/
                /* Read wind speed (in MPH) and direction (in compass degrees) from the cursor  */
                val windSpeed: Float = it.locationWeather.weatherWind.speed.toFloat()
                val windDirection: Float = it.locationWeather.weatherWind.deg.toFloat()
                val windString: String =
                    WeatherUtils.getFormattedWind(requireContext(), windSpeed, windDirection)
                /* Set the text and content description (for accessibility purposes) */
                extraDetails.windMeasurement.text = windString
                /************
                 * Pressure *
                 ************/
                val pressure: Float = it.locationWeather.weatherPressure.toFloat()

                val pressureString = getString(R.string.format_pressure, pressure)
                /* Set the text and content description (for accessibility purposes) */
                extraDetails.pressure.text = pressureString
            }
        }
    }

}