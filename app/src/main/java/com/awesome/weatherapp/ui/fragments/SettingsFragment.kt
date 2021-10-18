package com.awesome.weatherapp.ui.fragments

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.awesome.weatherapp.R


class SettingsFragment : PreferenceFragmentCompat(),
SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }



    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val activity: Activity? = activity
        if (key == getString(R.string.pref_location_key)) {
            // we've changed the location
            // Wipe out any potential PlacePicker latlng values so that we can use this text entry.

        } else if (key == getString(R.string.pref_units_key)) {
            // units have changed. update lists of weather entries accordingly

        }
        val preference: Preference? = findPreference(key)
        if (null != preference) {
            if (preference !is CheckBoxPreference) {
                sharedPreferences.getString(key, "")?.let { setPreferenceSummary(preference, it) }
            }
        }

    }

    override fun onStop() {
        super.onStop()
        // unregister the preference change listener
        preferenceScreen.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onStart() {
        super.onStart()
        // register the preference change listener
        preferenceScreen.sharedPreferences
            .registerOnSharedPreferenceChangeListener(this)
    }

    private fun setPreferenceSummary(preference: Preference, value: Any) {
        val stringValue = value.toString()
        if (preference is ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            val listPreference: ListPreference = preference
            val prefIndex: Int = listPreference.findIndexOfValue(stringValue)
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.entries[prefIndex])
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.summary = stringValue
        }
    }
}