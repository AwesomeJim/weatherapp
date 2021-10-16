package com.awesome.weatherapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.awesome.weatherapp.R

class ForecastMainFragment : Fragment() {

    companion object {
        fun newInstance() = ForecastMainFragment()
    }

    private lateinit var viewModel: ForecastMainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_forecast, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ForecastMainViewModel::class.java)
        // TODO: Use the ViewModel
    }

}