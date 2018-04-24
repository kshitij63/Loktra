package com.loktra_assign.loktra

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    var googleMap: GoogleMap? = null
    var fusedLocationClient: FusedLocationProviderClient? = null
    var locationCallback: LocationCallback? = null


    override fun onMapReady(p0: GoogleMap?) {
        this.googleMap = p0
        duration_text.visibility = View.GONE
        checkForPermission()

        Log.e("here", "mapready")

    }

    var pref: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var frag = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        frag.getMapAsync(this)

    }

    fun createLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest().apply {
            interval = 60000 * 60
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        return locationRequest
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 200) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "here!", Toast.LENGTH_SHORT).show()

                initialSetUp()
                getLocationOnMap()
            } else {
                Toast.makeText(this, "Please turn on location!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    fun checkForPermission() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    100)
            Log.e("here", "check if")


        } else {
            Log.e("here", "check else")

            locationTurnedOn()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            100 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    locationTurnedOn()

                } else {

                    Toast.makeText(this, "Please grant the location permisssion!", Toast.LENGTH_SHORT)
                            .show()
                    finish()

                }
                return

            }

        }
    }

    fun initialSetUp() {
        duration_text.visibility=View.GONE
        pref = this.getSharedPreferences("default", Context.MODE_PRIVATE)
        editor = pref?.edit()

        if (pref?.getString("state", "default").equals("default")) {
            editor?.putString("state", "false")
            editor?.commit()
        } else if (pref?.getString("state", "default").equals("true")) {
            on_but.text = "CLICK TO STOP SERVICE"
        } else {
            on_but.text = "CLICK TO START SERVICE"

        }
        on_but.setOnClickListener {
            if (pref?.getString("state", "default").equals("false")) {
                duration_text.visibility=View.GONE
                var intent = Intent(this, LocationTrackService::class.java)
                startService(intent)
                on_but.text = "CLICK TO STOP SERVICE"
                editor?.putString("state", "true")
                editor?.commit()
            } else {
                var locationDao = LocationDatabase.getLocationDatabse(this).getLocationDao()
                locationDao.getAlldata().observe(this, Observer {
                    if (it?.size!! > 0) {
                        duration_text.text = "Total Shift Duration: ${(it?.get(0)?.timeStamp!! -
                                it?.get(it.size - 1)?.timeStamp!!) / 60000.toInt()} Minutes"
                        duration_text.visibility = View.VISIBLE
                        for (i in 0..it!!.size - 1) {
                            var line = googleMap?.addPolyline(PolylineOptions()
                                    .add(LatLng(it.get(i).latitude, it.get(i).longitude))
                                    .color(Color.RED)
                                    .width(5.0f))

                        }
                        locationDao.deleteAllData()
                    } else {
                        Toast.makeText(this, "No location tracked!!", Toast.LENGTH_SHORT)
                                .show()
                    }
                })
                var intent = Intent(this, LocationTrackService::class.java)
                stopService(intent)
                fusedLocationClient?.removeLocationUpdates(locationCallback)
                on_but.text = "CLICK TO START SERVICE"

                editor?.putString("state", "false")
                editor?.commit()

            }
        }
    }

    fun locationTurnedOn() {
        Log.e("here", "loctaion turned on")

        var builder = LocationSettingsRequest.Builder().addLocationRequest(createLocationRequest())
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            initialSetUp()
            getLocationOnMap()
        }
        task.addOnFailureListener {
            if (it is ResolvableApiException) {
                try {
                    it.startResolutionForResult(this@MainActivity,
                            200)
                } catch (sendEx: IntentSender.SendIntentException) {
                }
            }
        }

    }

    fun getLocationOnMap() {
        progress.visibility = View.VISIBLE
//        Toast.makeText(this, "here!", Toast.LENGTH_SHORT).show()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                progress.visibility = View.GONE
                Log.e("status", "onLoresult ${p0?.lastLocation}")
                p0 ?: return
                for (location in p0.locations) {
                    googleMap?.addMarker(MarkerOptions()
                            .position(LatLng(location.latitude, location.longitude)))
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            LatLng(location.latitude, location.longitude), 12.0f))
                }
            }

            override fun onLocationAvailability(p0: LocationAvailability?) {
                Log.e("location available", "${p0?.isLocationAvailable}")
            }

        }
        startLocationUpdates(locationCallback!!)

    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(locationCallback: LocationCallback) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient?.requestLocationUpdates(createLocationRequest(),
                locationCallback,
                null)
    }


    override fun onPause() {
        super.onPause()
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

}
