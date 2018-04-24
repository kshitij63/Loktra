package com.loktra_assign.loktra

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.*

/**
 * Created by user on 4/24/2018.
 */
class LocationTrackService : Service() {

    var fusedLocationClient: FusedLocationProviderClient? = null
    var locationCallback: LocationCallback? = null;
    override fun onBind(p0: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show()
        var locationDao = LocationDatabase.getLocationDatabse(this).getLocationDao()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                Log.e("status", "onLoresult ${p0?.lastLocation}")
                p0 ?: return
                for (location in p0.locations) {
                    locationDao.insertLocation(LocationObject(System.currentTimeMillis()
                            , location.latitude, location.longitude))

                    /*        Toast.makeText(this@LocationTrackService
                                    , "${location.latitude},${location.longitude}", Toast.LENGTH_SHORT)
                                    .show()
                    */
                }
            }

            override fun onLocationAvailability(p0: LocationAvailability?) {
                Log.e("location available", "${p0?.isLocationAvailable}")
            }
        }

        startLocationUpdates(locationCallback!!)
        return START_STICKY
    }

    override fun onCreate() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onDestroy() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
        Toast.makeText(this, "service stopped", Toast.LENGTH_SHORT).show()

    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates(locationCallback: LocationCallback) {
        fusedLocationClient?.requestLocationUpdates(createLocationRequest(),
                locationCallback,
                null /* Looper */)
    }

    fun createLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest().apply {
            interval = 30000
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        return locationRequest
    }


}