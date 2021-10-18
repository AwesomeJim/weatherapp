package com.awesome.weatherapp.utilities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import timber.log.Timber

class LocationUtil private constructor(
    /**
     * The Context.
     */
    var context: Context
) : LocationListener {
    /**
     * The Is gps enabled.
     */
    // flag for GPS status
    var isGPSEnabled = false
    private var listener: LocationChangeCallback? = null

    /**
     * The Is network enabled.
     */
    // flag for network status
    var isNetworkEnabled = false

    /**
     * The Can get location.
     */
    var canGetLocation = false

    /**
     * The Location.
     */
    var myLocation :Location? = null

    /**
     * The Latitude.
     */
    var currentlLatitude :Double= 0.0

    /**
     * The Longitude.
     */
    var currentLongitude = 0.0

    /**
     * The Location manager.
     */
    // Declaring a Location Manager
    protected var locationManager: LocationManager?

    /**
     * Function to get latitude
     *
     * @return the latitude
     */
    fun getLatitude(): Double {
        if (myLocation != null) {
            currentlLatitude = myLocation!!.latitude
        }
        return currentlLatitude
    }

    /**
     * Function to get longitude
     *
     * @return the longitude
     */
    fun getLongitude(): Double {
        if (myLocation != null) {
            currentLongitude = myLocation!!.longitude
        }

        // return longitude
        return currentLongitude
    }

    /**
     * Gets location.
     *
     * @return the location
     */
    fun getLocation(): Location? {

        // getting GPS status
        isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

        // getting network status
        isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (!isGPSEnabled && !isNetworkEnabled) {
            Toast.makeText(context, "GPS or Internet not Enabled...", Toast.LENGTH_SHORT).show()
        } else {
            canGetLocation = true

            // First get location from Network Provider
            if (isNetworkEnabled) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Toast.makeText(context, "Permissions not granted...", Toast.LENGTH_SHORT).show()
                    return null
                }
                locationManager!!.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                )
                Timber.d("Network")
                if (locationManager != null) {
                    myLocation = locationManager!!
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (myLocation != null) {
                        currentlLatitude = myLocation!!.latitude
                        currentLongitude = myLocation!!.longitude
                    }
                }
            }

            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                if (myLocation == null) {
                    locationManager!!.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                    )
                    Timber.d("GPS Enabled")
                    if (locationManager != null) {
                        myLocation = locationManager!!
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        if (myLocation != null) {
                            currentlLatitude = myLocation!!.latitude
                            currentLongitude = myLocation!!.longitude
                        }
                    }
                }
            }
        }
        return myLocation
    }

    override fun onLocationChanged(location: Location) {
        this.myLocation = location
        if (location.latitude != 0.0 && location.longitude != 0.0 && listener != null) {
            listener!!.locationHasChanged(location)
            listener = null
        }
    }

    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {

    }

    override fun onProviderEnabled(s: String) {}
    override fun onProviderDisabled(s: String) {}

    /**
     * Sets listener.
     *
     * @param listener the listener
     */
    fun setListener(listener: LocationChangeCallback?) {
        this.listener = listener
    }

    companion object {
        // The minimum distance to change Updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 1 // 10 meters

        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES: Long = 100 // 1 minute
        private var mInstance: LocationUtil? = null

        /**
         * Gets instance.
         *
         * @param context  the context
         * @param listener the listener
         * @return the instance
         */
        fun getInstance(context: Context, listener: LocationChangeCallback?): LocationUtil? {
            getInstance(context) // initialize instance
            mInstance!!.listener = listener // assign location callback
            return mInstance
        }

        /**
         * Gets instance.
         *
         * @param context the context
         * @return the instance
         */
        fun getInstance(context: Context): LocationUtil? {
            if (mInstance == null) {
                mInstance = LocationUtil(context)
                mInstance!!.getLocation()
                return mInstance
            }
            mInstance!!.context = context
            mInstance!!.getLocation()
            return mInstance
        }
    }

    init {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
}