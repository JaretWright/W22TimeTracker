package com.constantlearningdad.w22timetracker

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.constantlearningdad.w22timetracker.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var lastLocation : Location

    //The Java equivalent is private static final LOCATION_PERMISSION_REQUEST_CODE = 1;
    companion object{
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val location = LatLng(44.4116, -79.6683)
        mMap.addMarker(MarkerOptions().position(location).title("Lakehead @ Georgian"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f)) //up to 22 zoom levels
        mMap.uiSettings.isZoomControlsEnabled = true

        setUpMap()
    }

    private fun setUpMap()
    {
        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE

        //check if the user gave us permission to access the location of the device running the app
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                                                            LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
            location?.let{ currentLocation ->
                lastLocation = currentLocation
                val currentLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                placeMarkerOnMap(currentLatLng)
            }
        }
    }

    /**
     * This method will place a marker on the Google Map at the lat and long provided
     */
    private fun placeMarkerOnMap(location : LatLng)
    {
        val marker = MarkerOptions().position(location)
        mMap.addMarker(marker)
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f)) //up to 22 zoom levels
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
    }
}