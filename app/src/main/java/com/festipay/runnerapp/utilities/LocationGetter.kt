package com.festipay.runnerapp.utilities

import android.Manifest
import android.content.pm.PackageManager
import android.app.Activity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

object LocationGetter {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private const val LOCATION_PERMISSION_REQUEST_CODE = 100

    fun getLocation(activity: Activity, onSuccess: (LatLng) -> Unit, onError: (String) -> Unit) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        onSuccess(LatLng(location.latitude, location.longitude))
                    } else {
                        val errorMessage = "Last known location is null"
                        showError(activity, errorMessage)
                        onError(errorMessage)
                    }
                }
                .addOnFailureListener { e ->
                    val errorMessage = "Failed to get location: ${e.message}"
                    onError(errorMessage)
                }
        }
    }
}
