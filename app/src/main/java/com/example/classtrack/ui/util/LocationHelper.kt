package com.example.classtrack.ui.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class LocationHelper(context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 10000  // 每10秒请求一次位置
        fastestInterval = 5000  // 最快5秒请求一次位置
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY  // 高精度模式
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLatitude(onResult: (Double?) -> Unit) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    onResult(location.latitude)
                } else {
                    // 如果 lastLocation 为 null，开始请求位置更新
                    requestLocationUpdates { latitude, _ ->
                        onResult(latitude)
                    }
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLongitude(onResult: (Double?) -> Unit) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    onResult(location.longitude)
                } else {
                    // 如果 lastLocation 为 null，开始请求位置更新
                    requestLocationUpdates { _, longitude ->
                        onResult(longitude)
                    }
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates(onResult: (Double?, Double?) -> Unit) {
        fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                val location = locationResult.lastLocation
                onResult(location?.latitude, location?.longitude)

                // 停止位置更新，防止持续请求
                fusedLocationClient.removeLocationUpdates(this)
            }
        }, Looper.getMainLooper())
    }
}
