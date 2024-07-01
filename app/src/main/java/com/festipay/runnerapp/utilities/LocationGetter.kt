package com.festipay.runnerapp.utilities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.app.Activity
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng

object LocationGetter {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    private val steps = listOf(10, 15, 25, 40, 50)
    private const val triesPerStep = 50

    fun getLocation(activity: Activity, onSuccess: (LatLng) -> Unit, onError: (String) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            showError(
                activity,
                "Sikertelen engedélyadás!\nAz applikáció beállításaiban újra engedélyezheted őket.\nAz app bezárul",
                onComplete = { activity.finish() })
        } else {
            executeLocationGet(activity, onSuccess, onError)
        }
    }
    @SuppressLint("MissingPermission")
    private fun executeLocationGet(activity: Activity, onSuccess: (LatLng) -> Unit, onError: (String) -> Unit, step: Int = 0){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    Log.i("Location", "${location.accuracy}")
                    if(step/ triesPerStep == 5) {
                        val errorMessage = "Bad precision, cannot get position."
                        onError(errorMessage)
                        return@addOnSuccessListener
                    }

                    if(location.accuracy <= steps[step / triesPerStep])
                        onSuccess(LatLng(location.latitude, location.longitude))
                    else
                        executeLocationGet(activity, onSuccess, onError, step+1)
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
