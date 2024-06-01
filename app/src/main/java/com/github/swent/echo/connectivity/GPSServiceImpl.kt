package com.github.swent.echo.connectivity

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.github.swent.echo.compose.navigation.LOCATION_PERMISSIONS
import com.google.android.gms.location.LocationServices
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A simple implementation of a [GPSService]
 *
 * @param context The context of the application that's using this service.
 */
class GPSServiceImpl(val context: Context) : GPSService {
    private val locationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    // This manager allows to check whether the location is disabled in settings.
    private val locationManager: LocationManager? =
        getSystemService(context, LocationManager::class.java)

    // User's last known location
    private var _location = MutableStateFlow<LatLng?>(null)

    /** Updates the last known location. */
    private fun getLocation() {
        if (
            LOCATION_PERMISSIONS.any {
                ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        ) {
            locationProviderClient.lastLocation.addOnSuccessListener {
                it?.apply {
                    val old = _location.value
                    _location.compareAndSet(
                        old,
                        this.let { location -> LatLng(location.latitude, location.longitude) }
                    )
                }
                // Request again if null, unless location is turned off in settings.
                // Even if this process has permissions to access the location, as
                // long as it's disabled in settings, the listener will return null.
                locationManager?.apply {
                    // TODO maybe also check for NETWORK_PROVIDER
                    if (isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        it ?: getLocation()
                    }
                }
            }
        }
    }

    init {
        getLocation()
    }

    override val userLocation: StateFlow<LatLng?> = _location.asStateFlow()

    override fun currentUserLocation(): LatLng? = _location.value

    override fun refreshUserLocation() = getLocation()
}
