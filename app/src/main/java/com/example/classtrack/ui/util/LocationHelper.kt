package com.example.classtrack.ui.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationHelper(context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getCurrentLatitude(onResult: (Double?) -> Unit) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                val latitude = location?.latitude
                onResult(latitude)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLongitude(onResult: (Double?) -> Unit) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                val longitude = location?.longitude
                onResult(longitude)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
}
