package com.awesome.weatherapp.utilities

import android.location.Location

interface LocationChangeCallback {
    /**
     * Location has changed.
     *
     * @param location the location
     */
    fun locationHasChanged(location: Location?)
}