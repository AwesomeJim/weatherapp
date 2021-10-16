package com.awesome.weatherapp.ui.fragments

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.awesome.weatherapp.R


class SettingsFragment : PreferenceFragmentCompat(),
SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }



    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val activity: Activity? = activity

    }
}