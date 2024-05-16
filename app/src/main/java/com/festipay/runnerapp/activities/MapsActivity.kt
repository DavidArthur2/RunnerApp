package com.festipay.runnerapp.activities

import android.content.pm.PackageManager
import android.os.Bundle
import android.Manifest
import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.festipay.runnerapp.R
import com.festipay.runnerapp.fragments.InstallFragment
import com.festipay.runnerapp.utilities.showError
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    private lateinit var latLng: LatLng

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val lat = intent.getStringExtra("lat")?.toDouble()
        val long = intent.getStringExtra("long")?.toDouble()
        if(lat == null || long == null) {
            showError(this, "Long and latitude values are null when starting MapsActivity intent")
            finish()
            return
        }
        latLng = LatLng(lat, long)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)


    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, SecondActivity::class.java)
        startActivity(intent)
    }


    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
            return
        }

        var lastLocation: Location? = null
        var lastAccuracy: Double = 0.0
        googleMap.isMyLocationEnabled = true

        googleMap.setOnMyLocationChangeListener { location ->
            lastLocation = location
            lastAccuracy = location.accuracy.toDouble()

        }

        googleMap.setOnMyLocationButtonClickListener {
            if (lastLocation != null) {
                val latLng = LatLng(lastLocation!!.latitude, lastLocation!!.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))
                true
            }
            false
        }

        googleMap.addMarker(MarkerOptions().position(latLng).title("Telephely"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f))
    }
}
